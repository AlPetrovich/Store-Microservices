package com.microservice.service;
import com.microservice.dto.InventoryResponseDTO;
import com.microservice.dto.OrderLineItemsDto;
import com.microservice.dto.OrderRequest;
import com.microservice.model.Order;
import com.microservice.model.OrderLineItems;
import com.microservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    public void placeOrder(OrderRequest orderRequest) {
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

        //LLamar al servicio inventario y haga el pedido si el producto esta en stock
        //http://localhost:8080/api/inventory?skuCode=iphone&skuCode=samsung&skuCode=nokia
        InventoryResponseDTO[] inventoryResponseArray = webClient.get()
                .uri("http://localhost:8082/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                        .retrieve()
                        .bodyToMono(InventoryResponseDTO[].class) //almacenar esta respuesta (matriz de objetos) de inventario dentro de order-service
                        .block();
        //verifica si la variable esta en stock es verdadera o falsa
        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponseDTO::isInStock);

        if (allProductsInStock){
            orderRepository.save(order);
        }else{
            throw new IllegalArgumentException("Product is not in stock, please try again later");
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
