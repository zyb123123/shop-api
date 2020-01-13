package com.fh.service.impl;

import com.fh.bean.Category;
import com.fh.dao.CategoryMapper;
import com.fh.service.CategoryService;
import com.fh.utils.RedisKeyConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    public List<Category> queryListCategory() {
        //先判断redis中是否有分类数据
        if (redisTemplate.hasKey(RedisKeyConstant.KEY_CATEGORY)) {
            return (List<Category>) redisTemplate.opsForValue().get(RedisKeyConstant.KEY_CATEGORY);
        }
        List<Category> categoryList = categoryMapper.queryListCategory();
        redisTemplate.opsForValue().set(RedisKeyConstant.KEY_CATEGORY, categoryList);
        return categoryList;
    }

}