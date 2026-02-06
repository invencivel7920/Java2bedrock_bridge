package com.javabedrock.bridge.translation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.javabedrock.bridge.core.BridgeCore;
import com.javabedrock.bridge.util.CacheFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;
import java.util.Objects;

/**
 * Engine de tradução de blocos entre Java e Bedrock
 */
public class TranslationEngine {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final BridgeCore bridge;
    private final BlockTranslator blockTranslator;
    private final ItemTranslator itemTranslator;
    private final EntityTranslator entityTranslator;
    
    private final Cache<String, Object> blockCache;
    private final Cache<String, Object> itemCache;
    private final Cache<String, Object> entityCache;
    
    public TranslationEngine(BridgeCore bridge) {
        Objects.requireNonNull(bridge, "bridge não pode ser nula");
        this.bridge = bridge;
        
        // Usar CacheFactory para caches otimizados
        this.blockCache = CacheFactory.createBlockCache(
            bridge.getConfig().MAX_BLOCK_CACHE_SIZE.get());
        
        this.itemCache = CacheFactory.createItemCache(
            bridge.getConfig().MAX_ITEM_CACHE_SIZE.get());
        
        this.entityCache = CacheFactory.createEntityCache(1024);
        
        this.blockTranslator = new BlockTranslator(bridge, blockCache);
        this.itemTranslator = new ItemTranslator(bridge, itemCache);
        this.entityTranslator = new EntityTranslator(bridge, entityCache);
        
        LOGGER.debug("TranslationEngine criado com caches otimizados");
    }
    
    public void initialize() {
        long startTime = System.currentTimeMillis();
        
        try {
            LOGGER.info("▪ Carregando mapeamentos de blocos...");
            blockTranslator.loadMappings();
            
            LOGGER.info("▪ Carregando mapeamentos de itens...");
            itemTranslator.loadMappings();
            
            LOGGER.info("▪ Carregando mapeamentos de entidades...");
            entityTranslator.loadMappings();
            
            long elapsed = System.currentTimeMillis() - startTime;
            LOGGER.info("✓ Translation Engine inicializado em {}ms", elapsed);
            
            // Log de estatísticas de cache
            logCacheStats();
            
        } catch (Exception e) {
            LOGGER.error("✗ Erro ao inicializar Translation Engine", e);
            throw new RuntimeException("Translation Engine initialization failed", e);
        }
    }
    
    public void cleanupCaches() {
        LOGGER.debug("Limpando caches...");
        
        try {
            blockCache.cleanUp();
            itemCache.cleanUp();
            entityCache.cleanUp();
            
            LOGGER.debug("✓ Caches limpos com sucesso");
            logCacheStats();
            
        } catch (Exception e) {
            LOGGER.warn("Erro ao limpar caches", e);
        }
    }
    
    /**
     * Log de estatísticas de caches
     */
    private void logCacheStats() {
        try {
            var blockStats = blockCache.stats();
            var itemStats = itemCache.stats();
            
            LOGGER.debug("Cache Stats - Blocks: hit={}, miss={}, rate={:.2f}%",
                blockStats.hitCount(), blockStats.missCount(),
                blockStats.hitRate() * 100);
            
            LOGGER.debug("Cache Stats - Items: hit={}, miss={}, rate={:.2f}%",
                itemStats.hitCount(), itemStats.missCount(),
                itemStats.hitRate() * 100);
        } catch (Exception e) {
            LOGGER.debug("Sem estatísticas disponíveis", e);
        }
    }
    
    // Getters
    public BlockTranslator getBlockTranslator() { return blockTranslator; }
    public ItemTranslator getItemTranslator() { return itemTranslator; }
    public EntityTranslator getEntityTranslator() { return entityTranslator; }
    
    public Cache<String, Object> getBlockCache() { return blockCache; }
    public Cache<String, Object> getItemCache() { return itemCache; }
    public Cache<String, Object> getEntityCache() { return entityCache; }
}
