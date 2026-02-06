package com.javabedrock.bridge.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafxmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Eventos do Bridge que podem ser usados por outros mods
 */
public class BridgeEvents {
    
    /**
     * Disparado quando o Bridge é inicializado
     */
    public static class BridgeInitializedEvent extends Event {
        private final String version;
        
        public BridgeInitializedEvent(String version) {
            this.version = version;
        }
        
        public String getVersion() {
            return version;
        }
    }
    
    /**
     * Disparado quando a conexão com Bedrock é estabelecida
     */
    public static class ConnectionEstablishedEvent extends Event {
        private final String host;
        private final int port;
        
        public ConnectionEstablishedEvent(String host, int port) {
            this.host = host;
            this.port = port;
        }
        
        public String getHost() { return host; }
        public int getPort() { return port; }
    }
    
    /**
     * Disparado quando a conexão com Bedrock é perdida
     */
    public static class ConnectionLostEvent extends Event {
        private final String reason;
        
        public ConnectionLostEvent(String reason) {
            this.reason = reason;
        }
        
        public String getReason() { return reason; }
    }
}
