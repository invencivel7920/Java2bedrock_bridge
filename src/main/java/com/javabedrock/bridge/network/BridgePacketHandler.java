package com.javabedrock.bridge.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Handler de pacotes Bedrock otimizado
 */
public class BridgePacketHandler extends SimpleChannelInboundHandler<ByteBuf> {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final BridgeCore bridge;
    private String remoteAddress;
    
    public BridgePacketHandler(BridgeCore bridge) {
        this.bridge = bridge;
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.remoteAddress = ctx.channel().remoteAddress().toString();
        LOGGER.info("Canal ativo: {}", remoteAddress);
        ctx.fireChannelActive();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        try {
            int readableBytes = msg.readableBytes();
            
            if (bridge.isDebugMode()) {
                LOGGER.debug("◄ Pacote recebido: {} bytes de {}", readableBytes, remoteAddress);
            }
            
            // Processa o pacote
            processPacket(msg, readableBytes);
            
        } catch (Exception e) {
            LOGGER.error("Erro ao processar pacote", e);
        } finally {
            msg.release();
        }
    }
    
    private void processPacket(ByteBuf buf, int size) {
        // TODO: Implementar processamento de pacotes Bedrock
        if (bridge.isDebugMode()) {
            LOGGER.debug("Processando pacote de {} bytes", size);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("Erro no canal com {}: {}", remoteAddress, cause.getMessage());
        ctx.close();
    }
    
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.warn("Canal desconectado: {}", remoteAddress);
        bridge.getNetworkManager().reconnect();
    }
}
