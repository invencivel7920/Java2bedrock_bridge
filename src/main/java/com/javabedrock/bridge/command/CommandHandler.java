package com.javabedrock.bridge.command;

import com.javabedrock.bridge.core.Java2BedrockBridge;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Objects;

/**
 * Sistema de comandos standalone para Java2Bedrock
 */
public class CommandHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int MAX_COMMAND_LENGTH = 1024;
    
    private final BridgeCore bridge;
    
    public CommandHandler(BridgeCore bridge) {
        this.bridge = Objects.requireNonNull(bridge, "bridge não pode ser nulo");
    }
    
    public void executeCommand(String command) {
        if (command == null || command.isBlank()) {
            LOGGER.debug("Comando vazio recebido");
            return;
        }
        
        // Limitar tamanho do comando por segurança
        if (command.length() > MAX_COMMAND_LENGTH) {
            LOGGER.warn("Comando excedeu tamanho máximo: {}", command.length());
            return;
        }
        
        String[] parts = command.trim().split("\\s+");
        if (parts.length == 0) return;
        
        String cmd = parts[0].toLowerCase();
        
        try {
            switch (cmd) {
                case "status" -> showStatus();
                case "debug" -> toggleDebug();
                case "reload" -> reloadConfig();
                case "connect" -> connect();
                case "disconnect" -> disconnect();
                case "help" -> showHelp();
                default -> LOGGER.warn("Comando desconhecido: {}", cmd);
            }
        } catch (Exception e) {
            LOGGER.error("Erro ao executar comando: {}", cmd, e);
        }
    }
    
    private void showStatus() {
        StringBuilder status = new StringBuilder();
        status.append("=== Java2Bedrock Bridge Status ===\n");
        status.append(String.format("Versão: %s\n", Java2BedrockBridge.getVersion()));
        status.append(String.format("Conexão: %s\n", 
            bridge.getNetworkManager().isConnected() ? "✓ Conectado" : "✗ Desconectado"));
        status.append(String.format("Blocos traduzidos: %,d\n", 
            bridge.getTotalTranslatedBlocks().get()));
        status.append(String.format("Pacotes enviados: %,d\n", 
            bridge.getTotalNetworkPackets().get()));
        status.append(String.format("Modo Debug: %s\n", 
            bridge.isDebugMode() ? "Ativado" : "Desativado"));
        status.append(String.format("Mods carregados: %d\n", 
            bridge.getModIntegration().getLoadedMods().size()));
        
        LOGGER.info(status.toString());
    }
    
    private void toggleDebug() {
        boolean newState = !bridge.isDebugMode();
        bridge.setDebugMode(newState);
        LOGGER.info("Modo Debug: {}", newState ? "ATIVADO" : "DESATIVADO");
    }
    
    private void reloadConfig() {
        try {
            bridge.getTranslationEngine().cleanupCaches();
            LOGGER.info("Configuração recarregada e cache limpo");
        } catch (Exception e) {
            LOGGER.error("Erro ao recarregar configuração", e);
        }
    }
    
    private void connect() {
        if (!bridge.getNetworkManager().isConnected()) {
            bridge.getNetworkManager().reconnect();
            LOGGER.info("Conectando ao servidor Bedrock...");
        } else {
            LOGGER.info("Já conectado ao servidor Bedrock");
        }
    }
    
    private void disconnect() {
        if (bridge.getNetworkManager().isConnected()) {
            LOGGER.info("Desconectando do servidor Bedrock...");
        } else {
            LOGGER.info("Não há conexão ativa");
        }
    }
    
    private void showHelp() {
        StringBuilder help = new StringBuilder();
        help.append("\n=== Comandos Disponíveis ===\n");
        help.append("  status      - Exibe status atual do bridge\n");
        help.append("  debug       - Ativa/desativa modo debug\n");
        help.append("  reload      - Recarrega configuração\n");
        help.append("  connect     - Conecta ao servidor Bedrock\n");
        help.append("  disconnect  - Desconecta do servidor Bedrock\n");
        help.append("  help        - Exibe esta mensagem\n");
        help.append("============================\n");
        
        LOGGER.info(help.toString());
    }
}
