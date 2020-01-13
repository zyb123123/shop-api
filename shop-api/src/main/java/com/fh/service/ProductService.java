package com.fh.service;

import com.fh.bean.Product;

import java.util.List;

public interface ProductService {

    List<Product> queryHotProductList();

    Product queryProductById(Integer productId);

    Product getProductById(Integer productId);

    Long updateProductStock(Integer productId, Long count);
}
