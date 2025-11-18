package com.smartqueue.gateway.config;

import com.smartqueue.gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, JwtAuthFilter jwtFilter) {
        return builder.routes()
                .route("queue", r -> r.path("/api/v1/queue/**")
                        .filters(f -> f.filter(jwtFilter)).uri("lb://QUEUE-SERVICE"))
                .route("notification", r -> r.path("/ws")
                        .filters(f -> f.filter(jwtFilter)).uri("lb://NOTIFICATION-SERVICE"))
                .build();
    }

}
