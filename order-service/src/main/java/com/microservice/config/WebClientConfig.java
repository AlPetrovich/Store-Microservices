package com.microservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    //bean de tipo WebClientConfig - dependencia spring webflux
    @Bean
    public WebClient webClient(){
        return WebClient.builder().build();
    }
}
