package com.fh.dao;

import com.fh.bean.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductMapper {

    List<Product> queryHotProductList();

    //根据id查询商品的信息
    Product queryProductById(Integer productId);

    Product getProductById(Integer productId);

    Long updateProductStock(@Param("id") Integer productId, @Param("count") Long count);
}
