package com.javabedrock.bridge.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe auxiliar para gerenciamento de informações do mod/aplicação
 */
public class Java2BedrockBridge {
    public static final String MODID = "java2bedrock";
    public static final String NAME = "Java2Bedrock Bridge";
    public static final String VERSION = "1.0.0-alpha";
    
    private static final Logger LOGGER = LogManager.getLogger(NAME);
    private static Java2BedrockBridge instance;
    
    private BridgeCore bridgeCore;
    
    public Java2BedrockBridge() {
        instance = this;
    }
    
    public static Java2BedrockBridge getInstance() {
        return instance;
    }
    
    public BridgeCore getBridgeCore() {
        return bridgeCore;
    }
    
    public void setBridgeCore(BridgeCore core) {
        this.bridgeCore = core;
    }
    
    public static String getVersion() {
        return VERSION;
    }
    
    public static String getName() {
        return NAME;
    }
}

