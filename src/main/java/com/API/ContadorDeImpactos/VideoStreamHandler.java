package com.API.ContadorDeImpactos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VideoStreamHandler extends TextWebSocketHandler {

    @Value("${app.edge.api-key}")
    private String edgeApiKey;

    private final Map<String, Set<WebSocketSession>> viewersPorPainel = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Map<String, String> params = extrairQueryParams(session.getUri());
        String painelId = params.get("painelId");
        String role = params.getOrDefault("role", "viewer");

        if (painelId == null || painelId.isBlank()) {
            session.close(CloseStatus.BAD_DATA.withReason("painelId obrigatorio"));
            return;
        }

        if ("publisher".equalsIgnoreCase(role)) {
            String token = params.get("token");
            if (!edgeApiKey.equals(token)) {
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Token invalido"));
                return;
            }
            session.getAttributes().put("role", "publisher");
            session.getAttributes().put("painelId", painelId);
            return;
        }

        session.getAttributes().put("role", "viewer");
        session.getAttributes().put("painelId", painelId);
        viewersPorPainel.computeIfAbsent(painelId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String role = (String) session.getAttributes().get("role");
        if (!"publisher".equals(role)) {
            return; 
        }

        String painelId = (String) session.getAttributes().get("painelId");
        Set<WebSocketSession> viewers = viewersPorPainel.get(painelId);

        if (viewers == null || viewers.isEmpty()) {
            return;
        }

        for (WebSocketSession viewer : viewers) {
            if (viewer.isOpen()) {
                try {
                    synchronized (viewer) {
                        viewer.sendMessage(message);
                    }
                } catch (IOException ignored) {
                    viewers.remove(viewer);
                }
            } else {
                viewers.remove(viewer);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String painelId = (String) session.getAttributes().get("painelId");
        if (painelId != null) {
            Set<WebSocketSession> viewers = viewersPorPainel.get(painelId);
            if (viewers != null) {
                viewers.remove(session);
            }
        }
    }

    private Map<String, String> extrairQueryParams(URI uri) {
        if (uri == null) return Map.of();
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams().toSingleValueMap();
    }
}