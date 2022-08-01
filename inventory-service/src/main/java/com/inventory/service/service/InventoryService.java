package com.inventory.service.service;

import com.inventory.service.dto.InventoryResponseDTO;
import com.inventory.service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    //devuelve una lista
    @Transactional(readOnly = true) //solo de lectura
    @SneakyThrows
    public List<InventoryResponseDTO> isInStock(List<String> skuCode) {
        //objetos de inventario que coincidan con el codigo
        log.info("Wait Started");
        //simulacion ejecucion tarda 10 segundos
        Thread.sleep(10000);
        log.info("Wait Ended");
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
