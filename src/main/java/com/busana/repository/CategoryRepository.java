package com.busana.repository;

import com.busana.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
    ===============================================================
    how to create a repository interface (follow this pattern for all repositories)

    1. @Repository      -> marks this as a Spring-managed repo bean
    2. JpaRepository<T, ID>
        - T     = the Model class this repo is for
        - ID    = the data type of that model's primary key (@Id field)
                    e.g. Category's PK is String -> JpaRepository<Category, String>
                         CartItem's PK is String -> JpaRepository<CartItem, String>

    3. These method will be generated FOR FREE without writing any code:
        - findAll()
        - findById(id)      → get one row by PK
        - save(entity)      → insert or update
        - deleteById(id)    → delete by PK
        - existsById(id)    → check if row exists
        - count()           → count all rows
    
    4. Custom queries use method naming conventions - Spring auto-generates the SQL:
        - findByFieldName(value)         → WHERE fieldName = value
        - findByFieldNameAndOther(a, b)  → WHERE fieldName = a AND other = b
        - findByFieldNameContaining(str) → WHERE fieldName LIKE '%str%'

    5. Place the file in: src/main/java/com/busana/repository/
    ===============================================================
*/

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    // example custom query - finds a category by its name
    // Spring auto-generates: SELECT * FROM Category WHERE categoryName = ?
    Category findByCategoryName(String categoryName);
}
