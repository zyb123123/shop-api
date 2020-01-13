package com.fh.controller;

import com.fh.bean.Product;
import com.fh.common.ServerResponse;
import com.fh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("product")
    public ServerResponse queryHotProductList() {
        List<Product> productList = productService.queryHotProductList();
        return ServerResponse.success(productList);
    }

}
