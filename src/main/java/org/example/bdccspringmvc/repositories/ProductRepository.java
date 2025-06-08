package org.example.bdccspringmvc.repositories;



import org.example.bdccspringmvc.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Utilisez la convention de nommage de Spring Data JPA
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    // Pour le stock critique
    List<Product> findByQuantityLessThan(int quantity);
}

