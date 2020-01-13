package com.fh.service.impl;

import com.fh.bean.Product;
import com.fh.dao.ProductMapper;
import com.fh.service.ProductService;
import com.fh.utils.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public List<Product> queryHotProductList() {
        if (redisTemplate.hasKey(RedisKeyConstant.KEY_PRODUCT)) {
            return (List<Product>) redisTemplate.opsForValue().get(RedisKeyConstant.KEY_PRODUCT);
        }
        List<Product> productList = productMapper.queryHotProductList();
        redisTemplate.opsForValue().set(RedisKeyConstant.KEY_PRODUCT, productList);
        return productList;

    }

    @Override
    public Product queryProductById(Integer productId) {
        return productMapper.queryProductById(productId);
    }

    @Override
    public Product getProductById(Integer productId) {
        return productMapper.getProductById(productId);
    }

    @Override
    public Long updateProductStock(Integer productId, Long count) {
        return productMapper.updateProductStock(productId, count);
    }
}