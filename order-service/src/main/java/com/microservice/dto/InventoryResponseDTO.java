package com.microservice.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryResponseDTO {
    //creado en microservicio inventario - NO podemos acceder a ella dentro de order por eso DUPLICO
    private String skuCode;
    private boolean isInStock;
}
