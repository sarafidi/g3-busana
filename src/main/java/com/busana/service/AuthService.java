package com.busana.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.busana.model.Customer;
import com.busana.model.Admin;
import com.busana.repository.CustomerRepository;
import com.busana.repository.AdminRepository;
import java.util.*;

@Service    
public class AuthService {
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //customer registration
    public String registerCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            return "Email already exists";
        }
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
        return "Customer registered successfully";
    }

    //customer login
    public Customer loginCustomer(String email, String password) {
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);
        if (customerOpt.isPresent() && passwordEncoder.matches(password, customerOpt.get().getPassword())) {
            return customerOpt.get();
        }
        return null; // Invalid email or password
    }
    
    //admin login
    public Admin loginAdmin(String email, String password) {
        Optional<Admin> adminOpt = adminRepository.findByEmail(email);
        if (adminOpt.isPresent() && passwordEncoder.matches(password, adminOpt.get().getPassword())) {
            return adminOpt.get();
        }
        return null; // Invalid email or password
    }
}
