package com.fh.dao;

import com.fh.bean.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<Category> queryListCategory();

}
