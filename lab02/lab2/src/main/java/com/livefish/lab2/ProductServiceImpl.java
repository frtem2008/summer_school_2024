package com.livefish.lab2;

import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ProductServiceImpl implements ProductService {

    // Хранилище клиентов
    private static final Map<Integer, Product> PRODUCT_MAP = new HashMap<>();

    // Переменная для генерации ID клиента
    private static final AtomicInteger PRODUCT_ID = new AtomicInteger();

    @Override
    public void create(Product client) {
        final int productId = PRODUCT_ID.incrementAndGet();
        client.setId(productId);
        PRODUCT_MAP.put(productId, client);
    }

    @Override
    public List<Product> readAll() {
        return new ArrayList<>(PRODUCT_MAP.values());
    }

    @Override
    public Product read(int id) {
        return PRODUCT_MAP.get(id);
    }

    @Override
    public boolean update(Product product, int id) {
        if (PRODUCT_MAP.containsKey(id)) {
            product.setId(id);
            Product modified = PRODUCT_MAP.get(id);
            if (product.description != null) {
                modified.description = product.description;
            }
            if (product.name != null) {
                modified.name = product.name;
            }
            PRODUCT_MAP.put(id, modified);
            return true;
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        return PRODUCT_MAP.remove(id) != null;
    }

    @Override
    public boolean addImage(int id, Path path) {
        if (PRODUCT_MAP.containsKey(id)) {
            Product toAddImage = PRODUCT_MAP.get(id);
            toAddImage.icon = path.toString();
            PRODUCT_MAP.put(id, toAddImage);
            return true;
        }
        return false;
    }
}