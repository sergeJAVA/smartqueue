package com.smartqueue.gateway.config;

import com.smartqueue.gateway.filter.JwtAuthFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.setAllowedHeaders(List.of("*"));
        corsConfiguration.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsWebFilter(source);
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, JwtAuthFilter jwtFilter) {
        return builder.routes()
                .route("queue", r -> r.path("/api/v1/queue/**")
                        .filters(f -> f.filter(jwtFilter)).uri("lb://QUEUE-SERVICE"))
                .route("notification", r -> r.path("/ws")
                        .filters(f -> f.filter(jwtFilter)).uri("lb:ws://NOTIFICATION-SERVICE"))
                .build();
    }

}
