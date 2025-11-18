package com.smartqueue.gateway.util;

import lombok.experimental.UtilityClass;
import org.springframework.http.server.reactive.ServerHttpRequest;

@UtilityClass
public class TokenUtils {

    public static String extractToken(ServerHttpRequest request) {
        String header = request.getHeaders().getFirst("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

}
