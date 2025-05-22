package com.javabedrock.ultimatebridge;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import mekanism.common.registries.MekanismBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.cloudburstmc.protocol.bedrock.packet.UpdateBlockPacket;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Mod("javabedrockbridge")
public class UltimateBridge {
    private static final Logger LOGGER = LogManager.getLogger();
    private static UltimateBridge instance;
    
    // Configuration
    private final BridgeConfig config;
    
    // Core Systems
    private final TranslationEngine translationEngine;
    private final NetworkManager networkManager;
    private final ModIntegrationEngine modIntegration;
    private final PerformanceManager performanceManager;
    private final CommandSystem commandSystem;
    private final ResourcePackSystem resourcePackSystem;
    private final MetricsSystem metricsSystem;
    private final DynamicMappingSystem mappingSystem;
    
    // State
    private final Map<UUID, PlayerSession> activeSessions = new ConcurrentHashMap<>();
    private final ScheduledExecutorService backgroundExecutor;
    private boolean debugMode = false;
    private final AtomicLong totalTranslatedBlocks = new AtomicLong();
    private final AtomicLong totalNetworkPackets = new AtomicLong();

    public UltimateBridge() {
        instance = this;
        
        // Configuration
        this.config = new BridgeConfig();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, BridgeConfig.SPEC);
        
        // Initialize systems
        this.performanceManager = new PerformanceManager(this);
        this.translationEngine = new TranslationEngine(this);
        this.modIntegration = new ModIntegrationEngine(this);
        this.networkManager = new NetworkManager(this);
        this.commandSystem = new CommandSystem(this);
        this.resourcePackSystem = new ResourcePackSystem(this);
        this.metricsSystem = new MetricsSystem(this);
        this.mappingSystem = new DynamicMappingSystem(this);
        
        // Optimized thread pool
        int coreThreads = Math.max(4, Runtime.getRuntime().availableProcessors());
        this.backgroundExecutor = Executors.newScheduledThreadPool(coreThreads,
            new NamedThreadFactory("JBB-Worker", Thread.NORM_PRIORITY - 1));
        
        // Event registration
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommandRegister);
        
        LOGGER.info("Initializing JavaBedrock Ultimate Bridge v{} on {}...", 
            getModVersion(), getPlatformName());
    }

    private void onSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                // Optimized initialization sequence
                performanceManager.optimize();
                mappingSystem.initialize();
                modIntegration.initialize();
                translationEngine.initialize();
                resourcePackSystem.initialize();
                networkManager.initialize();
                metricsSystem.start();
                
                LOGGER.info("Bridge initialized successfully! Loaded {} mod handlers", 
                    modIntegration.getLoadedMods().size());
                
                // Post-init optimization
                backgroundExecutor.schedule(this::postInit, 2, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.error("Initialization failed", e);
                throw new RuntimeException("Bridge initialization failed", e);
            }
        });
    }
    
    private void postInit() {
        mappingSystem.checkForUpdates();
        metricsSystem.recordStartupMetrics();
        networkManager.verifyConnection();
        
        // Warm up caches
        backgroundExecutor.execute(translationEngine::warmUpCaches);
    }
    
    @SubscribeEvent
    public void onCommandRegister(RegisterCommandsEvent event) {
        commandSystem.registerCommands(event.getDispatcher());
    }
    
    @SubscribeEvent
    public void onServerStart(ServerStartingEvent event) {
        backgroundExecutor.schedule(() -> {
            if (!networkManager.isConnected()) {
                LOGGER.warn("No active Bedrock connection - Reconnecting...");
                networkManager.reconnect();
            }
        }, 5, TimeUnit.SECONDS);
    }
    
    // ==================== TRANSLATION ENGINE ====================
    public class TranslationEngine {
        private final UltimateBridge bridge;
        private final BlockTranslator blockTranslator;
        private final ItemTranslator itemTranslator;
        private final EntityTranslator entityTranslator;
        
        // Enhanced caching
        private final Cache<BlockState, UpdateBlockPacket> blockCache;
        private final Cache<ResourceLocation, ByteBuf> itemCache;
        private final Cache<ResourceLocation, ByteBuf> entityCache;
        
        public TranslationEngine(UltimateBridge bridge) {
            this.bridge = bridge;
            
            // Dynamic cache sizing
            long maxMemory = Runtime.getRuntime().maxMemory();
            long cacheSize = (long)(maxMemory * config.cacheRatio.get());
            
            this.blockCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize / 1024)
                .expireAfterAccess(2, TimeUnit.HOURS)
                .recordStats()
                .build();
                
            this.itemCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize / 2048)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
                
            this.entityCache = CacheBuilder.newBuilder()
                .maximumSize(cacheSize / 2048)
                .expireAfterAccess(1, TimeUnit.HOURS)
                .build();
                
            this.blockTranslator = new BlockTranslator(bridge, blockCache);
            this.itemTranslator = new ItemTranslator(bridge, itemCache);
            this.entityTranslator = new EntityTranslator(bridge, entityCache);
        }
        
        public void initialize() {
            long start = System.currentTimeMillis();
            
            blockTranslator.loadMappings();
            itemTranslator.loadMappings();
            entityTranslator.loadMappings();
            
            modIntegration.registerHandlers(this);
            
            LOGGER.info("Translation engine ready in {}ms ({} blocks, {} items, {} entities)",
                System.currentTimeMillis() - start,
                blockCache.size(),
                itemCache.size(),
                entityCache.size());
        }
        
        public UpdateBlockPacket translateBlock(BlockState state, BlockPos pos, PlayerSession session) {
            totalTranslatedBlocks.incrementAndGet();
            
            // 1. Check for mod override
            Optional<UpdateBlockPacket> modPacket = modIntegration.translateBlock(state, pos, session);
            if (modPacket.isPresent()) return modPacket.get();
            
            // 2. Use cached or generate new
            try {
                return blockCache.get(state, () -> blockTranslator.translate(state, pos, session));
            } catch (ExecutionException e) {
                LOGGER.warn("Translation failed, using fallback", e);
                return blockTranslator.createFallback(state, pos);
            }
        }
        
        public void warmUpCaches() {
            // Pre-load common translations
            blockTranslator.warmUpCache();
        }
        
        public void cleanupCaches() {
            blockCache.cleanUp();
            itemCache.cleanUp();
            entityCache.cleanUp();
        }
    }

    // ==================== MOD INTEGRATION ====================
    public class ModIntegrationEngine {
        private final UltimateBridge bridge;
        private final Map<String, ModHandler> handlers = new ConcurrentHashMap<>();
        private final Map<Block, Supplier<UpdateBlockPacket>> blockOverrides = new ConcurrentHashMap<>();
        
        private static final Map<String, Supplier<ModHandler>> BUILTIN_HANDLERS = Map.of(
            "mekanism", MekanismHandler::new,
            "create", CreateHandler::new,
            "tconstruct", TConstructHandler::new,
            "thermal", ThermalSeriesHandler::new,
            "immersiveengineering", ImmersiveEngineeringHandler::new,
            "botania", BotaniaHandler::new,
            "astralsorcery", AstralSorceryHandler::new
        );
        
        public ModIntegrationEngine(UltimateBridge bridge) {
            this.bridge = bridge;
        }
        
        public void initialize() {
            long start = System.currentTimeMillis();
            int loaded = 0;
            
            // Register built-in handlers
            BUILTIN_HANDLERS.forEach((modId, supplier) -> {
                if (isModLoaded(modId)) {
                    registerHandler(modId, supplier.get());
                    loaded++;
                }
            });
            
            // Auto-discover generic mods
            ModList.get().getMods().stream()
                .map(IModInfo::getModId)
                .filter(id -> !handlers.containsKey(id))
                .forEach(id -> {
                    GenericModHandler handler = new GenericModHandler(id);
                    if (handler.initialize(bridge)) {
                        handlers.put(id, handler);
                        loaded++;
                    }
                });
            
            LOGGER.info("Mod integration initialized in {}ms ({} handlers)", 
                System.currentTimeMillis() - start, loaded);
        }
        
        public void registerHandlers(TranslationEngine engine) {
            handlers.values().forEach(h -> h.register(engine));
        }
        
        public Optional<UpdateBlockPacket> translateBlock(BlockState state, BlockPos pos, PlayerSession session) {
            Block block = state.getBlock();
            if (blockOverrides.containsKey(block)) {
                return Optional.of(blockOverrides.get(block).get());
            }
            return Optional.empty();
        }
        
        public List<String> getLoadedMods() {
            return new ArrayList<>(handlers.keySet());
        }
        
        public boolean toggleModSupport(String modId) {
            if (handlers.containsKey(modId)) {
                ModHandler handler = handlers.get(modId);
                handler.setEnabled(!handler.isEnabled());
                return true;
            }
            return false;
        }
        
        private boolean isModLoaded(String modId) {
            return ModList.get().isLoaded(modId);
        }
        
        private void registerHandler(String modId, ModHandler handler) {
            try {
                handler.initialize(bridge);
                handlers.put(modId, handler);
            } catch (Exception e) {
                LOGGER.error("Failed to initialize handler for {}", modId, e);
            }
        }
    }

    // ==================== NETWORK MANAGER ====================
    public class NetworkManager {
        private final UltimateBridge bridge;
        private EventLoopGroup workerGroup;
        private Channel bedrockChannel;
        private final AtomicLong lastActivity = new AtomicLong();
        
        public NetworkManager(UltimateBridge bridge) {
            this.bridge = bridge;
        }
        
        public void initialize() {
            int threads = calculateThreadCount();
            createEventLoopGroup(threads);
            connect();
            startHealthMonitor();
        }
        
        public void send(ByteBuf packet) {
            if (isConnected()) {
                lastActivity.set(System.currentTimeMillis());
                totalNetworkPackets.incrementAndGet();
                
                if (bridge.debugMode) {
                    LOGGER.debug("Sending packet #{} ({} bytes)", 
                        totalNetworkPackets.get(), packet.readableBytes());
                }
                
                bedrockChannel.writeAndFlush(packet.retain())
                    .addListener(f -> {
                        if (!f.isSuccess()) {
                            LOGGER.warn("Packet #{} failed", totalNetworkPackets.get(), f.cause());
                        }
                        packet.release();
                    });
            } else {
                packet.release();
                LOGGER.debug("Dropped packet - No connection");
            }
        }
        
        public boolean isConnected() {
            return bedrockChannel != null && bedrockChannel.isActive();
        }
        
        public void reconnect() {
            if (workerGroup != null && !workerGroup.isShuttingDown()) {
                workerGroup.submit(this::connect);
            }
        }
        
        public void verifyConnection() {
            if (!isConnected()) {
                LOGGER.warn("Connection lost - Reconnecting...");
                reconnect();
            }
        }
        
        private int calculateThreadCount() {
            return bridge.config.networkThreads.get() > 0 ? 
                bridge.config.networkThreads.get() : 
                Math.max(4, Runtime.getRuntime().availableProcessors());
        }
        
        private void createEventLoopGroup(int threads) {
            ThreadFactory factory = new NamedThreadFactory("JBB-NetIO", Thread.NORM_PRIORITY + 1);
            
            if (Epoll.isAvailable()) {
                workerGroup = new EpollEventLoopGroup(threads, factory);
                LOGGER.info("Using {} EPoll threads", threads);
            } else {
                workerGroup = new NioEventLoopGroup(threads, factory);
                LOGGER.info("Using {} NIO threads", threads);
            }
        }
        
        private void connect() {
            new Bootstrap()
                .group(workerGroup)
                .channel(Epoll.isAvailable() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new BridgeChannelInitializer(bridge))
                .connect("localhost", 19132)
                .addListener((ChannelFuture future) -> {
                    if (future.isSuccess()) {
                        bedrockChannel = future.channel();
                        lastActivity.set(System.currentTimeMillis());
                        LOGGER.info("Connected to Bedrock server");
                    } else {
                        LOGGER.warn("Connection failed", future.cause());
                        scheduleReconnect();
                    }
                });
        }
        
        private void startHealthMonitor() {
            bridge.backgroundExecutor.scheduleAtFixedRate(() -> {
                long inactiveMs = System.currentTimeMillis() - lastActivity.get();
                if (inactiveMs > 30000 && isConnected()) {
                    LOGGER.warn("Connection inactive for {}ms - Reconnecting...", inactiveMs);
                    reconnect();
                }
            }, 1, 1, TimeUnit.MINUTES);
        }
        
        private void scheduleReconnect() {
            bridge.backgroundExecutor.schedule(this::connect, 5, TimeUnit.SECONDS);
        }
    }

    // ==================== COMMAND SYSTEM ====================
    public class CommandSystem {
        private final UltimateBridge bridge;
        
        public CommandSystem(UltimateBridge bridge) {
            this.bridge = bridge;
        }
        
        public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(Commands.literal("jbbridge")
                .requires(src -> src.hasPermission(3))
                .then(Commands.literal("reload")
                    .executes(ctx -> reloadConfig(ctx.getSource())))
                .then(Commands.literal("mod")
                    .then(Commands.argument("modid", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            bridge.modIntegration.getLoadedMods().forEach(builder::suggest);
                            return builder.buildFuture();
                        })
                        .executes(ctx -> toggleMod(ctx.getSource(), 
                            StringArgumentType.getString(ctx, "modid"))))
                .then(Commands.literal("stats")
                    .executes(ctx -> showStats(ctx.getSource())))
                .then(Commands.literal("debug")
                    .executes(ctx -> toggleDebug(ctx.getSource()))));
        }
        
        private int reloadConfig(CommandSourceStack src) {
            try {
                bridge.reloadConfig();
                src.sendSuccess(() -> Component.literal("Config reloaded!"), false);
                return 1;
            } catch (Exception e) {
                src.sendFailure(Component.literal("Error: " + e.getMessage()));
                return 0;
            }
        }
        
        private int toggleMod(CommandSourceStack src, String modId) {
            if (bridge.modIntegration.toggleModSupport(modId)) {
                src.sendSuccess(() -> Component.literal("Toggled " + modId), false);
                return 1;
            }
            src.sendFailure(Component.literal("Unknown mod: " + modId));
            return 0;
        }
        
        private int showStats(CommandSourceStack src) {
            src.sendSuccess(() -> Component.literal(String.format(
                "JBBridge Stats:\n- Blocks: %,d\n- Packets: %,d\n- Memory: %dMB\n- Threads: %d",
                bridge.totalTranslatedBlocks.get(),
                bridge.totalNetworkPackets.get(),
                Runtime.getRuntime().totalMemory() / (1024 * 1024),
                Thread.activeCount()
            )), false);
            return 1;
        }
        
        private int toggleDebug(CommandSourceStack src) {
            bridge.debugMode = !bridge.debugMode;
            bridge.performanceManager.setDebug(bridge.debugMode);
            src.sendSuccess(() -> Component.literal("Debug " + (bridge.debugMode ? "ON" : "OFF")), false);
            return 1;
        }
    }

    // ==================== PERFORMANCE MANAGER ====================
    public class PerformanceManager {
        private final UltimateBridge bridge;
        private final ObjectPool<UpdateBlockPacket> blockPacketPool;
        private final ObjectPool<ByteBuf> bufferPool;
        
        public PerformanceManager(UltimateBridge bridge) {
            this.bridge = bridge;
            this.blockPacketPool = new ObjectPool<>(8192, UpdateBlockPacket::new);
            this.bufferPool = new ObjectPool<>(4096, () -> 
                PooledByteBufAllocator.DEFAULT.buffer(1024));
        }
        
        public void optimize() {
            // JVM-level optimizations
            System.setProperty("io.netty.allocator.type", "pooled");
            System.setProperty("io.netty.tryReflectionSetAccessible", "true");
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "false");
            
            // Native transport optimizations
            if (Epoll.isAvailable()) {
                System.setProperty("io.netty.transport.noNative", "false");
                System.setProperty("io.netty.epoll.levelTriggered", "false");
            }
            
            LOGGER.info("Performance optimizations applied");
        }
        
        public void setDebug(boolean debug) {
            if (debug) {
                System.setProperty("io.netty.leakDetection.level", "PARANOID");
                LOGGER.info("Debug mode enabled");
            } else {
                System.setProperty("io.netty.leakDetection.level", "DISABLED");
            }
        }
        
        public UpdateBlockPacket acquireBlockPacket() {
            return blockPacketPool.acquire();
        }
        
        public void releaseBlockPacket(UpdateBlockPacket packet) {
            blockPacketPool.release(packet);
        }
        
        public ByteBuf acquireBuffer(int capacity) {
            ByteBuf buf = bufferPool.acquire();
            buf.clear().ensureWritable(capacity);
            return buf;
        }
        
        public void releaseBuffer(ByteBuf buf) {
            if (buf.refCnt() > 0) {
                bufferPool.release(buf);
            }
        }
    }

    // ==================== UTILITY CLASSES ====================
    private static class NamedThreadFactory implements ThreadFactory {
        private final String name;
        private final int priority;
        private final AtomicInteger counter = new AtomicInteger();
        
        public NamedThreadFactory(String name, int priority) {
            this.name = name;
            this.priority = priority;
        }
        
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, name + "-" + counter.getAndIncrement());
            t.setPriority(priority);
            t.setDaemon(true);
            return t;
        }
    }
    
    public static String getPlatformName() {
        return ModList.get().getModContainerById("neoforge").isPresent() ? "NeoForge" : "Forge";
    }
    
    public static String getModVersion() {
        return ModList.get().getModContainerById("javabedrockbridge")
            .map(ModContainer::getModInfo)
            .map(IModInfo::getVersion)
            .map(Object::toString)
            .orElse("UNKNOWN");
    }
    
    public void reloadConfig() throws Exception {
        BridgeConfig.loadConfig(Paths.get("config/javabedrockbridge.toml"));
        translationEngine.cleanupCaches();
        networkManager.reconnect();
    }
    
    public static UltimateBridge getInstance() {
        return instance;
    }
    
    // ==================== MAIN ENTRY ====================
    public static void main(String[] args) {
        new UltimateBridge();
    }
}
