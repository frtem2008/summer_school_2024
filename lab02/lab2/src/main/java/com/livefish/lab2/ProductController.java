package com.livefish.lab2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(value = "/product")
    public ResponseEntity<?> create(@RequestBody Product product) {
        productService.create(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<Product>> read() {
        final List<Product> products = productService.readAll();

        return products != null && !products.isEmpty()
                ? new ResponseEntity<>(products, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/product/{product_id}")
    public ResponseEntity<Product> read(@PathVariable(name = "product_id") int id) {
        final Product product = productService.read(id);

        return product != null ? new ResponseEntity<>(product, HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/product/{product_id}")
    public ResponseEntity<?> update(@PathVariable(name = "product_id") int id, @RequestBody Product product) {
        final boolean updated = productService.update(product, id);

        return updated ? new ResponseEntity<>(productService.read(id), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping(value = "/product/{product_id}")
    public ResponseEntity<?> delete(@PathVariable(name = "product_id") int id) {
        Product del = productService.read(id);
        final boolean deleted = productService.delete(id);

        return deleted ? new ResponseEntity<>(del, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }
}
