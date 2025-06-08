
# bdcc‑spring‑MVC

Gestion de produits Web avec **Spring Boot**, **Spring Data JPA**, **Hibernate**, **Thymeleaf** et **Spring Security**.


---

##  Table des matières

1. [Présentation](#présentation)  
2. [Technologies & dépendances](#technologies--dépendances)  
3. [Architecture du projet](#architecture-du-projet)  
4. [Configuration](#configuration)  
5. [Fonctionnalités principales](#fonctionnalités-principales)  
   - CRUD produit  
   - Validation des formulaires  
   - Recherche  
   - Sécurité Spring Security
6. [Fonctionnalités supplémentaires](#fonctionnalités-supplémentaires)  
7. [Extraits de code & explications](#extraits-de-code--explications)  
8. [Captures d’écran attendues](#captures-décran-attendues)  
---

## Présentation

Une application Web JEE permettant de gérer un catalogue de produits, avec :

- Création, lecture, édition, suppression (CRUD)  
- Validation de formulaire  
- Authentification / autorisation via Spring Security  

---

## Technologies & dépendances

- **Spring Boot** (+ Web, Security, Validation)  
- **Spring Data JPA** / Hibernate  
- **Base de données H2 (dev)**  
- **Thymeleaf** (+ dialecte layout, extras security)  
- **Bootstrap 5** (via WebJars)  
- **Lombok** pour réduire le boilerplate  

---

## Architecture du projet

```

src/
├── main/
│   ├── java/org/example/bdccspringmvc
│   │   ├── entities/           ← Entités JPA (Product...)
│   │   ├── repositories/       ← ProductRepository   
│   │   ├── sec/                ← Sécurité
│   │   ├── web/                ← Contrôleurs Spring MVC
│   └── resources/
│       ├── templates/        ← Thymeleaf (layout + vues)
│       ├── static/        
│       └── application.properties
└── test/                   

````

---


Point de départ : `http://localhost:8084/login`

---

## Configuration

```properties
spring.application.name=bdcc-spring-MVC

spring.datasource.url=jdbc:h2:mem:products-db
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create
spring.h2.console.enabled=true
server.port=8084
```

---

## Fonctionnalités principales

1. **CRUD produit** : création, lecture, modification, suppression
2. **Recherche produit** : filtre par nom ou description
3. **Validation du formulaire** : annotations `@NoEmpty`, `@Size`…
4. **Sécurité**

   * Formulaire de login personnalisé (`/login`)
   * Rôles `USER`, `ADMIN`
   * Protection des routes

---

## Fonctionalités supplémentaires :

1. **Stock critique** : Affichage des produits dont le stock est inférieur à un seuil défini, pour anticiper les ruptures.
2. **Pagination** : Découpage de la liste des produits en pages pour une meilleure lisibilité et performance.


## Extraits de code & explications

###  Entité `Product`

- Représente la structure de la table produit en base de données avec ses attributs (id, name, price, quantity). Les annotations définissent les contraintes de validation (ex : nom non vide, taille, prix positif).

```java
@Entity
@Data @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 3, max = 50)
    private String name;

    @Min(value = 0, message = "Le prix doit être positif")
    private double price;
    @Min(1)
    private int quantity;
}
```

###  `ProductRepository`

- Interface pour accéder aux données Product. Étend JpaRepository pour bénéficier des méthodes CRUD et pagination. Fournit aussi des méthodes personnalisées pour chercher par nom (findByNameContainingIgnoreCase) et pour récupérer les produits en stock critique (findByQuantityLessThan).

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Utilisez la convention de nommage de Spring Data JPA
    Page<Product> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
    // Pour le stock critique
    List<Product> findByQuantityLessThan(int quantity);
}
```

###  Sécurité Spring Security

- Configure l’authentification en mémoire avec trois utilisateurs (deux simples et un admin), avec leurs rôles et mots de passe encodés. Cette configuration protège les accès selon les rôles.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager() {
        PasswordEncoder encoder = passwordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder().encode("1234")).roles("USER").build(),
                User.withUsername("user2").password(passwordEncoder().encode("1234")).roles("USER").build(),
                User.withUsername("admin").password(passwordEncoder().encode("1234")).roles("USER","ADMIN").build()
        );
    }
//...
}
```

###  Contrôleur produit

- Gère les requêtes HTTP liées aux produits. La méthode index récupère la liste paginée des produits, avec possibilité de filtrer par mot-clé. Elle prépare les données à afficher dans la vue (liste, pagination, page courante, filtre).

```java
@Controller
@RequestMapping("/products")
public class ProductController {
    
    @Autowired
    ProductRepository productRepository;

    @GetMapping("/user/index")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "0") int page,
                        @RequestParam(name = "size", defaultValue = "5") int size,
                        @RequestParam(name = "keyword", defaultValue = "") String keyword) {

        Page<Product> pageProducts;

        if(keyword.isEmpty()) {
            pageProducts = productRepository.findAll(PageRequest.of(page, size));
        } else {
            pageProducts = productRepository.findByNameContainingIgnoreCase(keyword, PageRequest.of(page, size));
        }

        model.addAttribute("productList", pageProducts.getContent());
        model.addAttribute("pages", new int[pageProducts.getTotalPages()]);
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("size", size);

        return "products";
    }
//...
}
```

---

## Captures d’écran attendues

1. Page de **connexion** (login)
   
   ![image](https://github.com/user-attachments/assets/789b11b4-2bfe-4ecf-98e6-f67669407597)

3. **Liste des produits** avec barre de recherche
   
   ![image](https://github.com/user-attachments/assets/f5e28007-1353-4709-9ada-fbb1f68d855e)

5. **Ajouter/éditer un produit** (formulaire + validation)
   
   ![image](https://github.com/user-attachments/assets/2cbe3618-734e-40d9-9804-a18cf7808c3f)

   ![image](https://github.com/user-attachments/assets/7a865154-fa27-4267-8b0c-1ab27340a697)

7. **Boutons supprimer** (pour ADMIN)
   
   ![image](https://github.com/user-attachments/assets/7e0c0a78-74d7-455f-ba05-0422c2494302)

9. **Stock critique**
   
- Affichage visuel des produits dont le stock est inférieur à un seuil (ex. < 5). Les produits concernés sont mis en évidence (badge rouge ou icône d’alerte).

- Liste des produits avec un badge rouge Stock faible
  
![image](https://github.com/user-attachments/assets/c6bc209f-f59f-4c11-a5bd-0b04b2c7d9ff)
![image](https://github.com/user-attachments/assets/2db2a05b-a611-46d4-86df-4181cfdc320f)



9. **Interfaces différenciées (Admin vs Utilisateur)**

- Administrateur : accès complet (ajout, modification, suppression, stock critique, etc.)
  
![image](https://github.com/user-attachments/assets/55e62e03-3c08-45c6-a27c-8437e220970c)

- Utilisateur : accès limité à la consultation et recherche des produits uniquement.
  
![image](https://github.com/user-attachments/assets/ecbb51f5-4004-4c7b-84f6-d42a8ac8a91c)


10. **Pagination et affichage du nom**
    
- Pagination : La liste des éléments est paginée pour améliorer la navigation et éviter un affichage trop long. L’utilisateur peut naviguer entre les pages via des boutons/précédent-suivant.
  
![image](https://github.com/user-attachments/assets/e1727732-12a5-43d2-a039-183e693615ff)

- Affichage du nom : Le nom de l’utilisateur (ou de la personne connectée) est affiché de façon visible dans un coin de l’interface (en haut à droite), pour un repérage rapide et une meilleure personnalisation.
  
![image](https://github.com/user-attachments/assets/0fb9cf00-f811-4f09-ba19-e01a4dd9d230)


11. **Gestion des accès et page Not Authorized**
    
- Lorsqu’un utilisateur non autorisé tente d’accéder à une fonctionnalité réservée à l’administrateur, il est redirigé vers une page "Not Authorized".
- Cette page informe clairement que l’accès est refusé et explique que les droits nécessaires ne sont pas présents.
  
![image](https://github.com/user-attachments/assets/0b8b88d5-a53c-411b-9afb-84268b895742)


---
