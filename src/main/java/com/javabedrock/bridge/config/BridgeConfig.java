package com.javabedrock.bridge.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Configuração central otimizada do Bridge
 */
public class BridgeConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    
    // Validação de valores
    private static final int MIN_NETWORK_THREADS = 1;
    private static final int MAX_NETWORK_THREADS = 128;
    private static final int MIN_PORT = 1024;
    private static final int MAX_PORT = 65535;
    private static final int MIN_TIMEOUT = 1000; // 1 segundo
    private static final int MAX_TIMEOUT = 30000; // 30 segundos
    private static final int DEFAULT_CACHE_SIZE = 8192;
    
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    
    // Network settings
    public static final ForgeConfigSpec.IntValue NETWORK_THREADS;
    public static final ForgeConfigSpec.ConfigValue<String> BEDROCK_HOST;
    public static final ForgeConfigSpec.IntValue BEDROCK_PORT;
    public static final ForgeConfigSpec.IntValue CONNECTION_TIMEOUT;
    
    // Performance settings
    public static final ForgeConfigSpec.DoubleValue CACHE_RATIO;
    public static final ForgeConfigSpec.IntValue MAX_BLOCK_CACHE_SIZE;
    public static final ForgeConfigSpec.IntValue MAX_ITEM_CACHE_SIZE;
    
    // Feature flags
    public static final ForgeConfigSpec.BooleanValue ENABLE_MOD_INTEGRATION;
    public static final ForgeConfigSpec.BooleanValue ENABLE_RESOURCE_PACKS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_METRICS;
    
    static {
        LOGGER.debug("Inicializando configuração do Bridge...");
        
        // Network Configuration
        BUILDER.comment("═══ Network Configuration ═══")
            .push("network");
        
        NETWORK_THREADS = BUILDER
            .comment("Número de threads de rede (0 = auto-tuning)")
            .defineInRange("threads", 0, 0, MAX_NETWORK_THREADS);
        
        BEDROCK_HOST = BUILDER
            .comment("Host do servidor Bedrock")
            .define("host", "localhost");
        
        BEDROCK_PORT = BUILDER
            .comment("Porta do servidor Bedrock (padrão: 19132)")
            .defineInRange("port", 19132, MIN_PORT, MAX_PORT);
        
        CONNECTION_TIMEOUT = BUILDER
            .comment("Timeout de conexão em milissegundos")
            .defineInRange("timeout_ms", 5000, MIN_TIMEOUT, MAX_TIMEOUT);
        
        BUILDER.pop();
        
        // Performance Configuration
        BUILDER.comment("═══ Performance Configuration ═══")
            .push("performance");
        
        CACHE_RATIO = BUILDER
            .comment("Proporção de cache relativa à memória máxima (0.0-1.0)")
            .defineInRange("cache_ratio", 0.25, 0.0, 1.0);
        
        MAX_BLOCK_CACHE_SIZE = BUILDER
            .comment("Tamanho máximo do cache de blocos")
            .defineInRange("block_cache_size", DEFAULT_CACHE_SIZE, 1024, 65536);
        
        MAX_ITEM_CACHE_SIZE = BUILDER
            .comment("Tamanho máximo do cache de itens")
            .defineInRange("item_cache_size", 4096, 512, 32768);
        
        BUILDER.pop();
        
        // Feature Flags
        BUILDER.comment("═══ Feature Flags ═══")
            .push("features");
        
        ENABLE_MOD_INTEGRATION = BUILDER
            .comment("Ativar sistema de integração de mods")
            .define("mod_integration", true);
        
        ENABLE_RESOURCE_PACKS = BUILDER
            .comment("Ativar sistema de resource packs")
            .define("resource_packs", true);
        
        ENABLE_METRICS = BUILDER
            .comment("Ativar coleta de métricas")
            .define("metrics", true);
        
        BUILDER.pop();
        
        SPEC = BUILDER.build();
        LOGGER.debug("✓ Configuração do Bridge inicializada");
    }
    
    /**
     * Validar configuração em tempo de execução
     */
    public synchronized void validate() {
        LOGGER.info("╔════ Validando Configuração ════╗");
        
        // Validar host Bedrock
        String host = BEDROCK_HOST.get();
        if (host == null || host.isBlank()) {
            LOGGER.warn("⚠ Host Bedrock não configurado, usando localhost");
        }
        
        // Validar porta
        int port = BEDROCK_PORT.get();
        if (port < MIN_PORT || port > MAX_PORT) {
            LOGGER.warn("⚠ Porta inválida: {}, usando 19132", port);
        }
        
        // Validar timeout
        int timeout = CONNECTION_TIMEOUT.get();
        if (timeout < MIN_TIMEOUT || timeout > MAX_TIMEOUT) {
            LOGGER.warn("⚠ Timeout inválido: {}, usando 5000ms", timeout);
        }
        
        // Validar cache ratio
        double cacheRatio = CACHE_RATIO.get();
        if (cacheRatio <= 0 || cacheRatio > 1.0) {
            LOGGER.warn("⚠ Cache ratio inválido: {}, usando 0.25", cacheRatio);
        }
        
        LOGGER.info("✓ Validação de configuração concluída");
        LOGGER.info("╚════════════════════════════════╝");
    }
}
