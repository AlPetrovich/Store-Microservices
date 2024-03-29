package com.microservice.service;
import com.microservice.dto.InventoryResponseDTO;
import com.microservice.dto.OrderLineItemsDto;
import com.microservice.dto.OrderRequest;
import com.microservice.event.OrderPlacedEvent;
import com.microservice.model.Order;
import com.microservice.model.OrderLineItems;
import com.microservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    //kafka
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto).collect(Collectors.toList());
        //agrego lista de items a la orden
        order.setOrderLineItemsList(orderLineItems);
        //obtengo skuCode de cada item para trabajar con metodo de inventario
        List<String> skuCodes = order.getOrderLineItemsList()
                .stream().map(OrderLineItems::getSkuCode).toList();

        //Cloud Sleuth -- RASTREAR PIEZA DE CODIGO
        log.info("Calling inventory service to check inventory");
        Span inventoryServiceLookup = tracer.nextSpan().name("inventoryServiceLookup");
        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())){

            //LLamar al servicio inventario y haga el pedido si el producto esta en stock
            //obtener una instancia de WebClient
            InventoryResponseDTO[] inventoryResponseArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponseDTO[].class) //almacenar esta respuesta (matriz de objetos) de inventario dentro de order-service
                    .block();
            //verifica si la variable esta en stock es verdadera o falsa
            boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponseDTO::isInStock);

            if (allProductsInStock){
                orderRepository.save(order);
                //kafka - enviar objeto de evento de pedido como un mensaje al tema notification topic
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
                return "Order placed successfully";
            }else{
                throw new IllegalArgumentException("Product is not in stock, please try again later");
            }

        }finally {
            inventoryServiceLookup.end();

        }

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }
}

