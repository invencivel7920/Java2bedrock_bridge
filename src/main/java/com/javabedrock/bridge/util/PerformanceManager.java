package com.javabedrock.bridge.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Gerenciador de performance e otimizações JVM/Netty
 * Singleton com sincronização para thread-safety
 */
public class PerformanceManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private boolean optimized = false;
    private boolean debugMode = false;
    
    public PerformanceManager() {
    }
    
    /**
     * Aplicar otimizações de performance para Netty e JVM
     */
    public synchronized void optimize() {
        if (optimized) {
            LOGGER.debug("Otimizações já foram aplicadas");
            return;
        }
        
        LOGGER.info("╔════ Aplicando Otimizações ════╗");
        
        // Netty optimizations
        setNettyProperty("io.netty.allocator.type", "pooled", 
            "Ativando pool de buffers Netty para melhor performance");
        
        setNettyProperty("io.netty.tryReflectionSetAccessible", "true",
            "Habilitando acesso reflexivo Netty");
        
        setNettyProperty("io.netty.noUnsafe", "false",
            "Habilitando Unsafe operations para máxima performance");
        
        // JVM optimizations
        setNettyProperty("java.util.Arrays.useLegacyMergeSort", "false",
            "Usando algoritmo moderno de sort");
        
        setNettyProperty("java.io.tmpdir", System.getProperty("java.io.tmpdir"),
            "Diretório temporário configurado");
        
        // GC hints
        System.setProperty("com.sun.management.jmxremote", "false");
        
        LOGGER.info("╚════════════════════════════════╝");
        
        // Log de info do sistema
        logSystemInfo();
        
        optimized = true;
    }
    
    /**
     * Definir propriedade com logging
     */
    private void setNettyProperty(String key, String value, String description) {
        try {
            System.setProperty(key, value);
            LOGGER.debug("✓ {}: {}", description, value);
        } catch (Exception e) {
            LOGGER.warn("✗ Falha ao definir {}: {}", key, e.getMessage());
        }
    }
    
    /**
     * Ativar/desativar modo debug
     */
    public synchronized void setDebug(boolean debug) {
        this.debugMode = debug;
        
        String leakLevel = debug ? "PARANOID" : "DISABLED";
        String mode = debug ? "ativado" : "desativado";
        
        try {
            System.setProperty("io.netty.leakDetection.level", leakLevel);
            LOGGER.info("✓ Modo debug {}: detecção de leak = {}", mode, leakLevel);
        } catch (Exception e) {
            LOGGER.warn("✗ Erro ao alternar modo debug", e);
        }
    }
    
    /**
     * Log de informações do sistema
     */
    private void logSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        
        LOGGER.info("╔════ Info do Sistema ════════════╗");
        LOGGER.info("║ Java Version: {}", System.getProperty("java.version"));
        LOGGER.info("║ CPU Cores: {}", runtime.availableProcessors());
        LOGGER.info("║ Max Memory: {} MB", maxMemory / (1024 * 1024));
        LOGGER.info("║ Total Memory: {} MB", totalMemory / (1024 * 1024));
        LOGGER.info("║ OS: {} {}", 
            System.getProperty("os.name"), 
            System.getProperty("os.version"));
        LOGGER.info("╚═══════════════════════════════╝");
    }
    
    public boolean isOptimized() { return optimized; }
    public boolean isDebugMode() { return debugMode; }
}
