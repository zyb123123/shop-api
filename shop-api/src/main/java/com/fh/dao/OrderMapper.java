package com.fh.dao;

import com.fh.bean.Order;
import com.fh.bean.OrderItem;

import java.util.List;

public interface OrderMapper {
    void addOrder(Order order);

    void addOrderItemList(List<OrderItem> orderItemList);
}
