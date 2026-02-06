package com.javabedrock.bridge.command;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.javabedrock.bridge.core.Java2BedrockBridge;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registrador de comandos do mod
 */
@Mod.EventBusSubscriber(modid = "java2bedrock", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandEvents {
    private static final Logger LOGGER = LogManager.getLogger();
    
    @SubscribeEvent
    public static void onCommandsRegister(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("j2b")
                .requires(src -> src.hasPermission(3))
                .then(Commands.literal("status")
                    .executes(ctx -> showStatus(ctx.getSource())))
                .then(Commands.literal("debug")
                    .executes(ctx -> toggleDebug(ctx.getSource())))
                .then(Commands.literal("reload")
                    .executes(ctx -> reloadConfig(ctx.getSource())))
        );
    }
    
    private static int showStatus(CommandSourceStack src) {
        var bridge = Java2BedrockBridge.getInstance().getBridgeCore();
        
        String status = String.format(
            "Java2Bedrock Status:\n" +
            "- Conexão: %s\n" +
            "- Blocos traduzidos: %,d\n" +
            "- Pacotes enviados: %,d\n" +
            "- Modo debug: %s",
            bridge.getNetworkManager().isConnected() ? "Conectado" : "Desconectado",
            bridge.getTotalTranslatedBlocks().get(),
            bridge.getTotalNetworkPackets().get(),
            bridge.isDebugMode() ? "Ativado" : "Desativado"
        );
        
        src.sendSuccess(() -> Component.literal(status), false);
        return 1;
    }
    
    private static int toggleDebug(CommandSourceStack src) {
        var bridge = Java2BedrockBridge.getInstance().getBridgeCore();
        bridge.setDebugMode(!bridge.isDebugMode());
        
        src.sendSuccess(() -> Component.literal(
            "Modo debug: " + (bridge.isDebugMode() ? "ATIVADO" : "DESATIVADO")
        ), false);
        return 1;
    }
    
    private static int reloadConfig(CommandSourceStack src) {
        try {
            src.sendSuccess(() -> Component.literal("Configuração recarregada!"), false);
            return 1;
        } catch (Exception e) {
            src.sendFailure(Component.literal("Erro ao recarregar: " + e.getMessage()));
            LOGGER.error("Erro ao recarregar configuração", e);
            return 0;
        }
    }
}
