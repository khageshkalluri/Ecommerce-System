package com.ecommerce.apigateway.JwtAuthGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class JwtAuthGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {


    private final WebClient webClient;

    public JwtAuthGatewayFilterFactory(WebClient.Builder webClient, @Value("${auth.service.url}") String serviceUrl) {
       this.webClient=webClient.baseUrl(serviceUrl).build();
    }

    @Override
    public GatewayFilter  apply(Object config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if(authHeader == null || !authHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            return this.webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange));
        };
    }
}
