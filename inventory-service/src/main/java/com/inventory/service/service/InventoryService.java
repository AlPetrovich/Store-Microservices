package com.inventory.service.service;

import com.inventory.service.dto.InventoryResponseDTO;
import com.inventory.service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    //devuelve una lista
    @Transactional(readOnly = true) //solo de lectura
    public List<InventoryResponseDTO> isInStock(List<String> skuCode) {
        //objetos de inventario que coincidan con el codigo
        return inventoryRepository.findBySkuCodeIn(skuCode)
                .stream()
                .map(inventory ->
                    InventoryResponseDTO.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
