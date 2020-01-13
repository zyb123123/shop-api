package com.fh.service.impl;

import com.fh.bean.CartItem;
import com.fh.bean.Order;
import com.fh.bean.OrderItem;
import com.fh.bean.Product;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.dao.OrderMapper;
import com.fh.service.OrderService;
import com.fh.service.ProductService;
import com.fh.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductService productService;


    @Override
    public ServerResponse addOrder(Integer id) {
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }

        //将用户购物车中的所有商品取出来
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);

        //用于存放用户购物车中库存不足的商品集合
        List<CartItem> shortOfStockCartItemList = new ArrayList<>();

        //用于存放订单明细的集合
        List<OrderItem> orderItemList = new ArrayList<>();

        //用于统计减库存成功的商品的总数量
        Long totalCount = 0L;

        //用于统计减库存成功的商品的总金额
        BigDecimal totalPrice = new BigDecimal("0");

        //使用时间戳+雪花算法生成一个订单编号，保证订单号的可读性和全局唯一性
        String orderId = IdUtil.createId();

        //用于统计购物车中被选中的商品个数的变量
        int checkedCartItemCount = 0;

        //遍历用户购物车中的所有商品
        for (CartItem cartItem : cartItemList) {
            //如果当前遍历的商品被选中
            if (cartItem.getChecked()) {
                checkedCartItemCount++;
                //通过商品id查询商品的基本信息
                Product product = productService.getProductById(cartItem.getProductId());
                //判断商品库存是否充足
                //10件iphone11,张三买5件,李四买6件
                if (product.getStock() >= cartItem.getCount()) {
                    //进行减库存的操作
                    Long rowsCount = productService.updateProductStock(cartItem.getProductId(), cartItem.getCount());
                    //如果数据表受SQL语句的影响行数大于0，则说明减库存成功，库存充足
                    if (rowsCount > 0) {
                        //创建商品对应的订单明细
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrderId(orderId);
                        orderItem.setMemberId(id);
                        orderItem.setProductId(cartItem.getProductId());
                        orderItem.setProductName(cartItem.getProductName());
                        orderItem.setPrice(cartItem.getPrice());
                        orderItem.setImage(cartItem.getImage());
                        orderItem.setSubtotalPrice(cartItem.getSubtotalPrice());
                        orderItem.setTotal(cartItem.getCount());
                        //将订单明细放入到订单明细集合中
                        orderItemList.add(orderItem);
                        totalCount += cartItem.getCount();
                        totalPrice = totalPrice.add(cartItem.getSubtotalPrice());
                    } else {
                        //减库存失败，商品库存不足，将库存不足的商品信息放入库存不足的商品集合
                        shortOfStockCartItemList.add(cartItem);
                    }
                } else {
                    //将库存不足的商品信息放入库存不足的商品集合
                    shortOfStockCartItemList.add(cartItem);
                }
            }
        }

        //判断用户购物车中所有选中的商品是否都库存不足
        if (shortOfStockCartItemList.size() == checkedCartItemCount) {
            return ServerResponse.error(ResponseEnum.CART_ALL_CHECKED_PRODUCT_UNDER_STOCK);
        }

        //创建订单
        Order order = new Order();
        order.setId(orderId);
        order.setMemberId(id);
        order.setCreateTime(new Date());
        order.setTotalCount(totalCount);
        order.setTotalPrice(totalPrice);
        order.setPayType(1); //1代表在线支付 2代表货到付款
        order.setStatus(1); //1代表未付款 2代表已支付
        orderMapper.addOrder(order);

        //创建订单明细
        orderMapper.addOrderItemList(orderItemList);

        //将下单成功的商品从用户的购物车中删除掉
        for (OrderItem orderItem : orderItemList) {
            redisTemplate.opsForHash().delete(cartKey, orderItem.getProductId() + "");
        }

        return ServerResponse.success(shortOfStockCartItemList);
    }

}