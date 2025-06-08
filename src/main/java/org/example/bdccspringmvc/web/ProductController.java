package org.example.bdccspringmvc.web;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.example.bdccspringmvc.entities.Product;
import org.example.bdccspringmvc.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
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

    @GetMapping("/")
    public String home() {
        return "redirect:/user/index";
    }

    @PostMapping("/admin/delete")
    public String delete(@RequestParam(name = "id") Long id) {
        productRepository.deleteById(id);
        return "redirect:/user/index";
    }
    @GetMapping("/admin/newProduct")
    public String newProduct(Model model) {
        model.addAttribute("product", new Product());
        return "new-product";
    }
    @PostMapping("/admin/saveProduct")
    public String saveProduct(@Valid Product product, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "new-product";
        productRepository.save(product);
        return "redirect:/user/index";
    }

    @GetMapping("/notAuthorized")
    public String notAuthorized() {
        return "notAuthorized";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "login";
    }



    @GetMapping("/admin/editProduct")
    public String editProduct(@RequestParam(name = "id") Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null) throw new RuntimeException("Product not found");
        model.addAttribute("product", product);
        return "edit-product";
    }

    @PostMapping("/admin/updateProduct")
    public String updateProduct(@Valid Product product, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "edit-product";
        productRepository.save(product);
        return "redirect:/user/index";
    }

    /***@GetMapping("/user/search")
    public String search(@RequestParam(name = "keyword", defaultValue = "") String keyword, Model model) {
        List<Product> products = productRepository.findByNameContainingIgnoreCase(keyword);
        model.addAttribute("productList", products);
        model.addAttribute("keyword", keyword);
        return "products";
    }***/

    @GetMapping("/admin/criticalStock")
    public String criticalStock(Model model) {
        List<Product> products = productRepository.findByQuantityLessThan(5);
        model.addAttribute("productList", products);
        model.addAttribute("critical", true);
        return "critical-stock";
    }
}
