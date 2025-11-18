package com.smartqueue.gateway.filter;

import com.smartqueue.gateway.service.JwtService;
import com.smartqueue.gateway.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter implements GatewayFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        String token = TokenUtils.extractToken(request);

        if (token == null) {
            return unauthorized(exchange, "Missing JWT token");
        }

        if (!jwtService.validateToken(token)) {
            return unauthorized(exchange, "Invalid or expired JWT token");
        }

        String userId = String.valueOf(jwtService.extractUserId(token));
        ServerHttpRequest.Builder requestBuilder = request.mutate();

        if (path.startsWith("/ws")) {
            URI originalUri = request.getURI();

            URI newUri = UriComponentsBuilder.fromUri(originalUri)
                    .queryParam("userId", userId)
                    .build(true)
                    .toUri();

            requestBuilder.uri(newUri);
        } else {
            requestBuilder.header("X-User-Id", userId);
        }

        return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = """
        {
            "error": "Unauthorized",
            "message": "%s"
        }
        """.formatted(message);

        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(json.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

}
