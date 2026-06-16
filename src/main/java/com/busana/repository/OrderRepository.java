package com.busana.repository;

import com.busana.model.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
   // Returns orders of a customer
    List <Order> findByCustomer_CustomerID(String customerID);
}
