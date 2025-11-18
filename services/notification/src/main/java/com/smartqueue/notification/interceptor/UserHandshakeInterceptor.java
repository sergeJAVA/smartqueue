package com.smartqueue.notification.interceptor;

import com.smartqueue.notification.dto.StompPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@Slf4j
public class UserHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String userId = request.getHeaders().getFirst("X-User-Id");

        if (userId != null && !userId.isEmpty()) {
            attributes.put("user", new StompPrincipal(userId));
            log.info("Handshake successful. Principal set for user ID: {}", userId);
            return true;
        }

        log.error("Handshake failed: 'X-User-Id' header is missing or empty.");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception exception) {

    }

}
