package com.busana.service;

import com.busana.model.Admin;
import com.busana.model.Customer;
import com.busana.service.singleton.DBConnection;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;


import java.sql.*;

@Service
public class AuthService {
    private PasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    private static final String ID_PREFIX = "CUST";
    
    public String registerCustomer(Customer customer) {
        Connection conn = DBConnection.getInstance().getConnection();
        String getMaxId = "SELECT MAX(CAST(SUBSTRING(customerID, 5) AS UNSIGNED)) FROM customer WHERE customerID LIKE 'CUST%'";
        try {
            System.out.println(conn);
            String checkExistingEmail = "SELECT * FROM busana.customer WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkExistingEmail);
            checkStmt.setString(1, customer.getEmail());
            ResultSet rs = checkStmt.executeQuery();
            System.out.println(rs);

            if (rs.next()) {
                return "Email already exists";
            }
        
        System.out.println(customer.getPassword());
        String hashedPassword = passwordEncoder.encode(customer.getPassword());
        System.out.println(hashedPassword);


        String insertCustomer = "INSERT INTO customer (customerID, name, email, password, deliveryAddress) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement insertStmt = conn.prepareStatement(insertCustomer);

            int nextId;
            try (PreparedStatement maxStmt = conn.prepareStatement(getMaxId);
                ResultSet HighestCustID = maxStmt.executeQuery()) {
                HighestCustID.next();
                int currentMax = HighestCustID.getInt(1);
                nextId = currentMax + 1;
            }
            customer.setCustomerID(formatId(ID_PREFIX, nextId));

        insertStmt.setString(1, customer.getCustomerID());
        insertStmt.setString(2, customer.getName());
        insertStmt.setString(3, customer.getEmail());
        insertStmt.setString(4, hashedPassword);
        insertStmt.setString(5, customer.getDeliveryAddress());
        insertStmt.executeUpdate(); 
        
        return "Registration successful";
        }
        catch (SQLException e) {
            e.printStackTrace();
            return "Registration failed";
        }
    }

    public Customer loginCustomer(String email, String password) {
       Connection conn = DBConnection.getInstance().getConnection();
        try {
            String query = "SELECT * FROM customer WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                if (passwordEncoder.matches(password, storedHashedPassword)) {
                    Customer customer = new Customer();
                    customer.setCustomerID(rs.getString("customerID"));
                    customer.setName(rs.getString("name"));
                    customer.setEmail(rs.getString("email"));
                    return customer; // successful login
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return null; // login failed
    }

    public Admin loginAdmin(String email, String password) {
        Connection conn = DBConnection.getInstance().getConnection();
        try {
            String query = "SELECT * FROM admin WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                if (passwordEncoder.matches(password, storedHashedPassword)) {
                    Admin admin = new Admin();
                    admin.setAdminID(rs.getString("adminID"));
                    admin.setName(rs.getString("name"));
                    admin.setEmail(rs.getString("email"));
                    return admin; // successful login
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return null; // login failed
    }

    private String formatId(String prefix, int value) {
        return prefix + String.format("%03d", value);
    }

}