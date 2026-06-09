package com.busana.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.busana.model.ProductVariant;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, String> {
    List<ProductVariant> findByProduct_ProductID(String productID);
}
