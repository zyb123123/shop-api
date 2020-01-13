package com.fh.controller;

import com.fh.bean.Category;
import com.fh.common.ServerResponse;
import com.fh.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("categorys")
    public ServerResponse queryListCategory() {
        List<Category> categoriesList = categoryService.queryListCategory();
        return ServerResponse.success(categoriesList);
    }

}
