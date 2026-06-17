USE busana;

-- Admin seed (password: admin123)
INSERT INTO admin (adminid, name, email, password) VALUES
    ('ADMIN-001', 'Super Admin', 'admin@busana.my', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lbeat');

-- Customer seed (password: customer123)
INSERT INTO Customer (customerID, name, email, password, deliveryAddress) VALUES
    ('CUST-001', 'Lili Test', 'lili@busana.my', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'No. 1, Jalan Teknokrat, Cyberjaya, Selangor');