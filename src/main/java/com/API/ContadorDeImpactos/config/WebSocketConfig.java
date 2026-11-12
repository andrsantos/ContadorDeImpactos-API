package com.API.ContadorDeImpactos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import com.API.ContadorDeImpactos.components.VideoStreamHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final VideoStreamHandler videoStreamHandler;

    public WebSocketConfig(VideoStreamHandler videoStreamHandler) {
        this.videoStreamHandler = videoStreamHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(videoStreamHandler, "/video-stream")
                .setAllowedOrigins("*"); 
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(512 * 1024);  
        container.setMaxBinaryMessageBufferSize(512 * 1024);
        container.setMaxSessionIdleTimeout(60_000L);         
        return container;
    }
}