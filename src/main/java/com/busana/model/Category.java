package com.busana.model;

import jakarta.persistence.*;

/* 
    ===============================================================
    guide on creating model class (follow this for all models)

    1. @Entity      -> tells Spring this class maps to a DB table
    2. @Table       -> specify the exact table name in MySQL
    3. @Id          -> makes the rpimary key field
    4. @Column      -> maps field toa column (match name, length, nullable)
    5. @ManyToOne   -> use this when table has a FK to another table
    6. @ JoinColumn -> pairs with @ManyToOne, specifies the FK column name

    for FK relationships, refer to Product.java as an example (@ManyToOne + JoinColumn)

    Field types from MySQL to Java
    1. VARCHAR          -> String
    2. INT              -> int
    3. DECIMAL(10, 2)   -> BigDecimal
    4. DATE             -> LocalDate
    ===============================================================
*/

@Entity
@Table(name = "Category")       // must match SQL table name
public class Category {
    
    @Id
    @Column(name = "categoryID", length = 20, nullable = false)
    private String categoryID;
    
    @Column(name = "categoryName", length = 100, nullable = false)
    private String categoryName;
    
    @Column(name = "description", length = 255)     // nullable = true by default
    private String description;

    // --- Constructor --------------------------
    public Category() {}        // always include an empty constructor for JPA

    // --- Getter & Setters --------------------------
    public String getCategoryID() { return categoryID; }
    public void setCategoryID(String categoryID) { this.categoryID = categoryID; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
