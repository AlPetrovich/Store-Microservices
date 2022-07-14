package com.microservice.config;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //bean de tipo WebClientConfig - dependencia spring webflux
    @Bean
    @LoadBalanced //si nuestro servicio order encuentra muchas instancias de inventory, el cliente debe elegir una de ellas
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }
}
