package com.javabedrock.bridge.network;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;

/**
 * Gerenciador otimizado de conexão de rede com o Bedrock
 */
public class NetworkManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int HEALTH_CHECK_INTERVAL_SECONDS = 30;
    private static final int RECONNECT_DELAY_SECONDS = 5;
    private static final long INACTIVITY_THRESHOLD_MS = 30000;
    
    private final BridgeCore bridge;
    private EventLoopGroup workerGroup;
    private Channel bedrockChannel;
    private final AtomicLong lastActivity = new AtomicLong();
    
    private volatile boolean initialized = false;
    private volatile boolean connecting = false;
    
    public NetworkManager(BridgeCore bridge) {
        this.bridge = Objects.requireNonNull(bridge, "bridge não pode ser nulo");
        this.lastActivity.set(System.currentTimeMillis());
    }
    
    public void initialize() {
        if (initialized) {
            LOGGER.warn("NetworkManager já foi inicializado");
            return;
        }
        
        try {
            long startTime = System.currentTimeMillis();
            createEventLoopGroup();
            connect();
            startHealthMonitor();
            initialized = true;
            
            long elapsed = System.currentTimeMillis() - startTime;
            LOGGER.info("Network Manager inicializado em {}ms", elapsed);
        } catch (Exception e) {
            LOGGER.error("Falha ao inicializar Network Manager", e);
            throw new RuntimeException("Network initialization failed", e);
        }
    }
    
    private void createEventLoopGroup() {
        int threads = calculateThreadCount();
        
        try {
            final AtomicInteger threadCounter = new AtomicInteger(0);
            
            if (Epoll.isAvailable()) {
                workerGroup = new EpollEventLoopGroup(threads, r -> {
                    Thread t = new Thread(r, "JBB-Epoll-" + threadCounter.incrementAndGet());
                    t.setDaemon(true);
                    t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                });
                LOGGER.info("✓ Netty EPoll ativado para I/O de rede ({} threads)", threads);
            } else {
                workerGroup = new NioEventLoopGroup(threads, r -> {
                    Thread t = new Thread(r, "JBB-NIO-" + threadCounter.incrementAndGet());
                    t.setDaemon(true);
                    t.setPriority(Thread.NORM_PRIORITY);
                    return t;
                });
                LOGGER.info("► Netty NIO fallback para I/O de rede ({} threads)", threads);
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao criar EventLoopGroup", e);
            throw new RuntimeException(e);
        }
    }
    
    private int calculateThreadCount() {
        Objects.requireNonNull(bridge, "bridge não pode ser nulo");
        try {
            var config = bridge.getConfig();
            int configured = config.NETWORK_THREADS.get();
            int cpuCores = Runtime.getRuntime().availableProcessors();
            
            if (configured > 0) {
                int result = Math.min(configured, cpuCores * 2);
                LOGGER.debug("Threads de rede configuradas: {} (limitado a {} cores*2={})", configured, cpuCores, cpuCores * 2);
                return result;
            }
            
            // Auto-tuning: 1 thread por core, min 2, max 16
            int autoThreads = Math.max(2, Math.min(16, cpuCores));
            LOGGER.debug("Auto-tuning de threads: {} (cores={})", autoThreads, cpuCores);
            return autoThreads;
        } catch (Exception e) {
            LOGGER.error("Erro ao calcular contagem de threads", e);
            return 4;
        }
    }
    
    private void connect() {
        if (connecting) {
            LOGGER.debug("Conexão já em progresso");
            return;
        }
        
        connecting = true;
        
        try {
            String host = bridge.getConfig().BEDROCK_HOST.get();
            int port = bridge.getConfig().BEDROCK_PORT.get();
            int timeout = bridge.getConfig().CONNECTION_TIMEOUT.get();
            
            LOGGER.debug("Iniciando conexão com {}:{}...", host, port);
            
            new Bootstrap()
                .group(workerGroup)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, timeout)
                .option(ChannelOption.SO_LINGER, 0)
                .handler(new BridgeChannelInitializer(bridge))
                .connect(host, port)
                .addListener((ChannelFuture future) -> {
                    connecting = false;
                    
                    if (future.isSuccess()) {
                        onConnectionSuccess(future);
                    } else {
                        onConnectionFailure(future);
                    }
                });
        } catch (Exception e) {
            connecting = false;
            LOGGER.error("Erro ao iniciar conexão", e);
            scheduleReconnect();
        }
    }
    
    private void onConnectionSuccess(ChannelFuture future) {
        bedrockChannel = future.channel();
        lastActivity.set(System.currentTimeMillis());
        LOGGER.info("✓ Conectado ao servidor Bedrock em {}:{}", 
            bedrockChannel.remoteAddress(), bedrockChannel.localAddress());
    }
    
    private void onConnectionFailure(ChannelFuture future) {
        LOGGER.warn("✗ Falha na conexão com Bedrock: {}", 
            future.cause() != null ? future.cause().getMessage() : "Desconhecido");
        scheduleReconnect();
    }
    
    public void send(ByteBuf packet) {
        if (packet == null) {
            LOGGER.warn("Tentativa de enviar pacote nulo");
            return;
        }
        
        if (!isConnected()) {
            packet.release();
            LOGGER.debug("Pacote descartado - Sem conexão (tamanho: {} bytes)", packet.readableBytes());
            return;
        }
        
        try {
            lastActivity.set(System.currentTimeMillis());
            bridge.getTotalNetworkPackets().incrementAndGet();
            
            if (bridge.isDebugMode()) {
                LOGGER.debug("► Enviando pacote #{} ({} bytes)",
                    bridge.getTotalNetworkPackets().get(),
                    packet.readableBytes());
            }
            
            bedrockChannel.writeAndFlush(packet.retain())
                .addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        LOGGER.warn("✗ Pacote #{} falhou: {}",
                            bridge.getTotalNetworkPackets().get(),
                            future.cause().getMessage());
                    } else if (bridge.isDebugMode()) {
                        LOGGER.debug("✓ Pacote #{} enviado", bridge.getTotalNetworkPackets().get());
                    }
                    packet.release();
                });
        } catch (Exception e) {
            packet.release();
            LOGGER.error("Erro ao enviar pacote", e);
        }
    }
    
    public boolean isConnected() {
        return bedrockChannel != null && bedrockChannel.isActive();
    }
    
    public void reconnect() {
        if (workerGroup != null && !workerGroup.isShuttingDown()) {
            workerGroup.submit(() -> {
                try {
                    if (bedrockChannel != null && bedrockChannel.isActive()) {
                        bedrockChannel.close().sync();
                    }
                    connect();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Reconexão foi interrompida", e);
                }
            });
        }
    }
    
    private void startHealthMonitor() {
        bridge.getBackgroundExecutor().scheduleAtFixedRate(() -> {
            try {
                long inactiveMs = System.currentTimeMillis() - lastActivity.get();
                
                if (isConnected()) {
                    if (inactiveMs > INACTIVITY_THRESHOLD_MS) {
                        LOGGER.warn("Conexão inativa por {}ms - Reconectando...", inactiveMs);
                        reconnect();
                    }
                } else {
                    LOGGER.debug("Conexão perdida - Tentando reconectar...");
                    reconnect();
                }
            } catch (Exception e) {
                LOGGER.error("Erro no health monitor", e);
            }
        }, HEALTH_CHECK_INTERVAL_SECONDS, HEALTH_CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
    
    private void scheduleReconnect() {
        bridge.getBackgroundExecutor().schedule(
            this::connect, 
            RECONNECT_DELAY_SECONDS, 
            TimeUnit.SECONDS
        );
    }
    
    public void shutdown() {
        try {
            LOGGER.info("Encerrando Network Manager...");
            
            if (bedrockChannel != null && bedrockChannel.isActive()) {
                bedrockChannel.close().syncUninterruptibly();
            }
            
            if (workerGroup != null && !workerGroup.isShuttingDown()) {
                workerGroup.shutdownGracefully(0, 5, TimeUnit.SECONDS).sync();
            }
            
            LOGGER.info("Network Manager encerrado com sucesso");
        } catch (InterruptedException e) {
            LOGGER.error("Erro ao encerrar Network Manager", e);
            Thread.currentThread().interrupt();
        }
    }
    
    public Channel getChannel() {
        return bedrockChannel;
    }
    
    public long getLastActivityTime() {
        return lastActivity.get();
    }
}
