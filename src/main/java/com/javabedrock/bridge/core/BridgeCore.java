package com.javabedrock.bridge.core;

import com.javabedrock.bridge.config.BridgeConfig;
import com.javabedrock.bridge.network.NetworkManager;
import com.javabedrock.bridge.translation.TranslationEngine;
import com.javabedrock.bridge.integration.ModIntegrationEngine;
import com.javabedrock.bridge.util.PerformanceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Núcleo do sistema de bridge
 * Coordena todos os subsistemas
 */
public class BridgeCore {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final BridgeConfig config;
    private final TranslationEngine translationEngine;
    private final NetworkManager networkManager;
    private final ModIntegrationEngine modIntegration;
    private final PerformanceManager performanceManager;
    private final ScheduledExecutorService backgroundExecutor;
    
    private final AtomicLong totalTranslatedBlocks = new AtomicLong();
    private final AtomicLong totalNetworkPackets = new AtomicLong();
    
    private boolean debugMode = false;
    
    public BridgeCore() {
        this.config = new BridgeConfig();
        this.performanceManager = new PerformanceManager();
        this.translationEngine = new TranslationEngine(this);
        this.networkManager = new NetworkManager(this);
        this.modIntegration = new ModIntegrationEngine(this);
        
        int coreThreads = Math.max(4, Runtime.getRuntime().availableProcessors());
        this.backgroundExecutor = Executors.newScheduledThreadPool(coreThreads,
            r -> {
                Thread t = new Thread(r, "Java2Bedrock-Worker-" + Thread.currentThread().getId());
                t.setDaemon(true);
                t.setPriority(Thread.NORM_PRIORITY - 1);
                return t;
            });
    }
    
    public void initialize() {
        try {
            long startTime = System.currentTimeMillis();
            
            LOGGER.info("Aplicando otimizações de performance...");
            performanceManager.optimize();
            
            LOGGER.info("Inicializando sistema de tradução...");
            translationEngine.initialize();
            
            LOGGER.info("Inicializando integração de mods...");
            modIntegration.initialize();
            
            LOGGER.info("Inicializando gerenciador de rede...");
            networkManager.initialize();
            
            LOGGER.info("Bridge Core inicializado em {}ms", System.currentTimeMillis() - startTime);
            
            // Agendar verificações periódicas
            schedulePeriodicChecks();
            
        } catch (Exception e) {
            LOGGER.error("Falha ao inicializar Bridge Core", e);
            throw new RuntimeException("Bridge initialization failed", e);
        }
    }
    
    private void schedulePeriodicChecks() {
        backgroundExecutor.scheduleAtFixedRate(() -> {
            if (!networkManager.isConnected()) {
                LOGGER.warn("Conexão perdida - Reconectando...");
                networkManager.reconnect();
            }
        }, 1, 30, TimeUnit.SECONDS);
    }
    
    public void shutdown() {
        LOGGER.info("Desligando Bridge Core...");
        try {
            // Encerrar gerenciador de rede primeiro
            if (networkManager != null) {
                networkManager.shutdown();
            }
            
            // Encerrar executor de background
            if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
                backgroundExecutor.shutdown();
                if (!backgroundExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    LOGGER.warn("Timeout ao encerrar executor de background");
                    backgroundExecutor.shutdownNow();
                    if (!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                        LOGGER.error("Executor de background não terminou após forceStop");
                    }
                }
            }
            
            // Limpar caches
            if (translationEngine != null) {
                translationEngine.cleanupCaches();
            }
            
            LOGGER.info("Bridge Core desligado com sucesso");
        } catch (InterruptedException e) {
            LOGGER.error("Erro ao desligar Bridge Core", e);
            if (backgroundExecutor != null) {
                backgroundExecutor.shutdownNow();
            }
            Thread.currentThread().interrupt();
        }
    }
    
    // Getters
    public BridgeConfig getConfig() { return config; }
    public TranslationEngine getTranslationEngine() { return translationEngine; }
    public NetworkManager getNetworkManager() { return networkManager; }
    public ModIntegrationEngine getModIntegration() { return modIntegration; }
    public PerformanceManager getPerformanceManager() { return performanceManager; }
    public ScheduledExecutorService getBackgroundExecutor() { return backgroundExecutor; }
    
    public boolean isDebugMode() { return debugMode; }
    public void setDebugMode(boolean debug) { 
        this.debugMode = debug;
        performanceManager.setDebug(debug);
    }
    
    public AtomicLong getTotalTranslatedBlocks() { return totalTranslatedBlocks; }
    public AtomicLong getTotalNetworkPackets() { return totalNetworkPackets; }
}
