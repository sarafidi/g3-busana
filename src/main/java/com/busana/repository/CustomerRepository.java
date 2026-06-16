package com.busana.repository;

import com.busana.model.Customer;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;   
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Optional<Customer> findByEmail(String email);
}
