package com.inventory.service.repository;
import com.inventory.service.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    //proporcionamos una lista de codigos y devuelve objetos de inventario que coincidan con el codigo
    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}

