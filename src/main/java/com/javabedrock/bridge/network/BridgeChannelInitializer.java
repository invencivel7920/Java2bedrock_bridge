package com.javabedrock.bridge.network;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import com.javabedrock.bridge.core.BridgeCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Inicializador de canal do Netty para comunicação com Bedrock
 */
public class BridgeChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger LOGGER = LogManager.getLogger();
    
    private final BridgeCore bridge;
    
    public BridgeChannelInitializer(BridgeCore bridge) {
        this.bridge = bridge;
    }
    
    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        // Frame decoder - determina limites de mensagens
        pipeline.addLast("frameDecoder",
            new LengthFieldBasedFrameDecoder(1024 * 1024, 0, 4, -4, 4));
        
        // Frame encoder - adiciona tamanho da mensagem
        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        
        // Handler customizado para pacotes Bedrock
        pipeline.addLast("bedrockHandler", new BridgePacketHandler(bridge));
        
        LOGGER.debug("Canal inicializado: {}", ch.remoteAddress());
    }
}
