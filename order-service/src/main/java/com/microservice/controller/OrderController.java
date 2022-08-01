package com.microservice.controller;

import com.microservice.dto.OrderRequest;
import com.microservice.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    //@CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    //@TimeLimiter(name = "inventory")
    //@Retry(name = "inventory")
    //public CompletableFuture<String> placeOrder(@RequestBody OrderRequest orderRequest) {
        //implementar tiempo de espera
      //  return CompletableFuture.supplyAsync(() -> orderService.placeOrder(orderRequest));
    //}
    public String placeOrder(@RequestBody OrderRequest orderRequest) {
        //implementar tiempo de espera
        return  orderService.placeOrder(orderRequest);
    }

    public CompletableFuture<String>  fallbackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture.supplyAsync(()-> "Oops! Something went wrong. Please try again later");
    }
}
