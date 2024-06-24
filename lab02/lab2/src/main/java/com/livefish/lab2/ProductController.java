package com.livefish.lab2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static String UPLOAD_DIRECTORY = "uploads";

    @PostMapping(value = "/product/{product_id}/image")
    public ResponseEntity<?> addImage(@PathVariable(name = "product_id") int id, @RequestParam("icon")MultipartFile file) throws IOException {
        Product toUpdate = productService.read(id);
        if (toUpdate == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (!Files.exists(Path.of(UPLOAD_DIRECTORY)))
            Files.createDirectory(Path.of(UPLOAD_DIRECTORY));
        Path fileNameAndPath = Paths.get(UPLOAD_DIRECTORY, file.getOriginalFilename());
        System.out.println(fileNameAndPath);
        System.out.println(fileNameAndPath.toAbsolutePath());
        if (!Files.exists(fileNameAndPath))
            Files.createFile(fileNameAndPath);
        Files.write(fileNameAndPath, file.getBytes());
        productService.addImage(id, fileNameAndPath);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/product/{product_id}/image")
    public ResponseEntity<Resource> readImage(@PathVariable(name = "product_id") int id) throws MalformedURLException {
        final Product product = productService.read(id);
        if (product  == null || product.getIcon() == null || product.getIcon().isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Path filePath = Path.of(product.getIcon());
        Resource resource = new UrlResource(filePath.toUri());
        if (!resource.exists())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
