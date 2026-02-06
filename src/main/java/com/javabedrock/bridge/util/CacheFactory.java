package com.javabedrock.bridge.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalNotification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Factory para criação otimizada de caches com logging detalhado
 */
public class CacheFactory {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<String, Cache<?, ?>> CACHE_REGISTRY = Collections.synchronizedMap(new WeakHashMap<>());
    private static int cacheCounter = 0;
    
    private CacheFactory() {
        throw new AssertionError("Não deve ser instanciado");
    }
    
    /**
     * Cria um cache com configurações padrão otimizadas
     */
    public static <K, V> Cache<K, V> createCache(
            long maxSize,
            long expireAfterAccessMinutes,
            boolean recordStats) {
        
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize deve ser > 0");
        }
        if (expireAfterAccessMinutes <= 0) {
            throw new IllegalArgumentException("expireAfterAccessMinutes deve ser > 0");
        }
        
        CacheBuilder builder = CacheBuilder.newBuilder()
            .maximumSize(Math.min(maxSize, Long.MAX_VALUE / 2))
            .expireAfterAccess(expireAfterAccessMinutes, TimeUnit.MINUTES)
            .concurrencyLevel(Math.max(1, Runtime.getRuntime().availableProcessors()))
            .removalListener(CacheFactory::logRemoval);
        
        if (recordStats) {
            builder.recordStats();
        }
        
        Cache<K, V> cache = builder.build();
        
        String cacheId = "Cache-" + (++cacheCounter);
        CACHE_REGISTRY.put(cacheId, cache);
        
        LOGGER.debug("✓ {} criado: {} máx, {} min expiry, stats={}", 
            cacheId, maxSize, expireAfterAccessMinutes, recordStats);
        
        return cache;
    }
    
    /**
     * Cria um cache de blocos otimizado (2 horas de expiração)
     */
    public static <K, V> Cache<K, V> createBlockCache(long maxSize) {
        return createCache(maxSize, 120, true);
    }
    
    /**
     * Cria um cache de itens otimizado (1 hora de expiração)
     */
    public static <K, V> Cache<K, V> createItemCache(long maxSize) {
        return createCache(maxSize, 60, true);
    }
    
    /**
     * Cria um cache de entidades otimizado (1 hora, sem stats)
     */
    public static <K, V> Cache<K, V> createEntityCache(long maxSize) {
        return createCache(maxSize, 60, false);
    }
    
    /**
     * Registra remoções de cache para debug
     */
    private static <K, V> void logRemoval(RemovalNotification<K, V> notification) {
        if ("EVICTED".equals(notification.getCause().toString())) {
            LOGGER.debug("⚠ Cache entry evicted: key={}", notification.getKey());
        }
    }
    
    /**
     * Obter estatísticas de cache (se disponível)
     */
    public static Map<String, String> getCacheStats(Cache<?, ?> cache) {
        Map<String, String> stats = new LinkedHashMap<>();
        try {
            var cacheStats = cache.stats();
            stats.put("hitCount", String.valueOf(cacheStats.hitCount()));
            stats.put("missCount", String.valueOf(cacheStats.missCount()));
            stats.put("hitRate", String.format("%.2f%%", cacheStats.hitRate() * 100));
            stats.put("evictionCount", String.valueOf(cacheStats.evictionCount()));
        } catch (Exception e) {
            stats.put("stats", "Não disponível");
        }
        return stats;
    }
    
    /**
     * Log de estatísticas de todos os caches registrados
     */
    public static void logAllCacheStats() {
        LOGGER.info("╔════════ Cache Statistics ════════╗");
        int count = 0;
        for (Cache<?, ?> cache : CACHE_REGISTRY.values()) {
            if (cache != null) {
                var stats = getCacheStats(cache);
                LOGGER.info("║ Cache {}: Hit Rate = {}", count++, stats.get("hitRate"));
            }
        }
        LOGGER.info("╚════════════════════════════════╝");
    }
}
