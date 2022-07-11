package com.inventory.service.controller;
import com.inventory.service.dto.InventoryResponseDTO;
import com.inventory.service.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    //http://localhost:8080/api/inventory?sku-code=iphone&sku-code=samsung&sku-code=nokia
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponseDTO> isInStock(@RequestParam List<String> skuCode) {
        return inventoryService.isInStock(skuCode);
    }
}
