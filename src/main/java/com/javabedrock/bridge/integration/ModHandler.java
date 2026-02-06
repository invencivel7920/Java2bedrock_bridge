package com.javabedrock.bridge.integration;

/**
 * Interface para handlers de integração de mods
 */
public interface ModHandler {
    /**
     * Inicializa o handler
     */
    void initialize();
    
    /**
     * Retorna true se o handler está habilitado
     */
    boolean isEnabled();
    
    /**
     * Define se o handler está habilitado
     */
    void setEnabled(boolean enabled);
    
    /**
     * Retorna o ID do mod que este handler suporta
     */
    String getModId();
}
