package com.busana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.busana.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByStatus(String status);

    List<Product> findByCategory_CategoryID(String categoryID);

    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);

    List<Product> findByNameIgnoreCaseAndCategory_CategoryID(String name, String categoryID);
}
