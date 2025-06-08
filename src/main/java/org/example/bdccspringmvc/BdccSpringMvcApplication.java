package org.example.bdccspringmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.example.bdccspringmvc.entities.Product;
import org.example.bdccspringmvc.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

//@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) //pour activer la protection par défaut de spring security
@SpringBootApplication
public class BdccSpringMvcApplication {

    public static void main(String[] args) {
        SpringApplication.run(BdccSpringMvcApplication.class, args);
    }

    @Bean
    CommandLineRunner start(ProductRepository productRepository) {
        return args -> {
            productRepository.save(Product.builder()
                    .name("Computer")
                    .price(6500.0)
                    .quantity(12)
                    .build());

            productRepository.save(Product.builder()
                    .name("Printer")
                    .price(1200.0)
                    .quantity(5)
                    .build());

            productRepository.save(Product.builder()
                    .name("Smart phone")
                    .price(1700.0)
                    .quantity(10)
                    .build());

            productRepository.findAll().forEach(p -> {
                System.out.println(p.toString());
            });
        };
    }
}
