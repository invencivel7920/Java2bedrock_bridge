package com.javabedrock.bridge.integration;

import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Engine de integração com mods
 */
public class ModIntegrationEngine {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final BridgeCore bridge;
    private final ConcurrentMap<String, ModHandler> handlers = new ConcurrentHashMap<>();
    
    public ModIntegrationEngine(BridgeCore bridge) {
        this.bridge = bridge;
    }
    
    public void initialize() {
        if (!bridge.getConfig().ENABLE_MOD_INTEGRATION.get()) {
            LOGGER.info("Integração de mods desabilitada");
            return;
        }
        
        long start = System.currentTimeMillis();
        
        // TODO: Carregar handlers de mods
        
        LOGGER.info("Mod Integration Engine inicializado em {}ms",
            System.currentTimeMillis() - start);
    }
    
    public void registerHandler(String modId, ModHandler handler) {
        handlers.put(modId, handler);
        LOGGER.info("Handler registrado para mod: {}", modId);
    }
    
    public List<String> getLoadedMods() {
        return new ArrayList<>(handlers.keySet());
    }
    
    public boolean isModLoaded(String modId) {
        return handlers.containsKey(modId);
    }
}
