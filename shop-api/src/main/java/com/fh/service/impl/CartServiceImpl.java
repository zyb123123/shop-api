package com.fh.service.impl;

import com.fh.bean.Cart;
import com.fh.bean.CartItem;
import com.fh.bean.Product;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.service.CartService;
import com.fh.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductService productService;

    @Override
    public ServerResponse addCartItem(Integer productId, Integer id) {
        //判断当前登录会员的购物车中是否存在此商品
        // 如果存在则将购物车中该商品的数量加1,否则的话就向购物车中添加一个新的商品。
        // 判断商品id是否为空
        if (productId == null) {
            return ServerResponse.error(ResponseEnum.PRODUCT_ID_IS_NULL);
        }
        //根据商品id查询商品基本信息
        Product product = productService.queryProductById(productId);
        //判断商品是否下架
        if (product.getStatus() == 2) {
            return ServerResponse.error(ResponseEnum.PRODUCT_IS_SOLD_OUT);
        }
        //定义全局的redisKey
        String cartKey = "cart:" + id;
        String productKey = productId + "";
        //如果用户的购物车中不存在该商品，则向购物车中添加一个新的商品
        if (!redisTemplate.opsForHash().hasKey(cartKey, productKey)) {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(product.getId());
            cartItem.setProductName(product.getName());
            cartItem.setPrice(product.getPrice());
            cartItem.setCount(1L);
            cartItem.setImage(product.getMainImage());
            cartItem.setSubtotalPrice(product.getPrice());
            cartItem.setChecked(true);
            redisTemplate.opsForHash().put(cartKey, productKey, cartItem);
        } else {
            //如果存在则将购物车中该商品的数量加1
            CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(cartKey, productKey);
            cartItem.setCount(cartItem.getCount() + 1);
            cartItem.setSubtotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())));
            redisTemplate.opsForHash().put(cartKey, productKey, cartItem);
        }
        //统计一下当前登录的会员您的购物车中的商品总数量
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);
        Long cartTotalNumber = 0L;
        for (CartItem cartItem : cartItemList) {
            cartTotalNumber += cartItem.getCount();
        }
        return ServerResponse.success(cartTotalNumber);
    }

    @Override
    public ServerResponse getCartTotalCount(Integer id) {
        String cartKey = "cart:" + id;
        //判断用户的购物车是否存在
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        //统计一下当前登录的会员您的购物车中的商品总数量
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);
        Long cartTotalNumber = 0L;
        for (CartItem cartItem : cartItemList) {
            cartTotalNumber += cartItem.getCount();
        }
        return ServerResponse.success(cartTotalNumber);
    }

    @Override
    public ServerResponse queryCart(Integer id) {
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error();
        }
        //取出当前登录会员购物车中的商品
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);
        //遍历购物车中的商品集合，计算总价格和总个数
        Long totalCount = 0L;
        BigDecimal totalPrice = new BigDecimal("0");
        for (CartItem cartItem : cartItemList) {
            //如果当前遍历的商品被选中
            if (cartItem.getChecked()) {
                totalCount += cartItem.getCount();
                totalPrice = totalPrice.add(cartItem.getSubtotalPrice());
            }
        }
        Cart cart = new Cart();
        cart.setCartItemList(cartItemList);
        cart.setTotalCount(totalCount);
        cart.setTotalPrice(totalPrice);
        return ServerResponse.success(cart);
    }

    @Override
    public ServerResponse changeCartItemCount(Integer productId, Integer count, Integer id) {
        //先判断当前登录会员有没有购物车
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        String productKey = productId + "";
        //判断当前登录会员的购物车中是否存在该商品
        if (!redisTemplate.opsForHash().hasKey(cartKey, productKey)) {
            return ServerResponse.error(ResponseEnum.CART_PRODUCT_IS_NOT_EXISTED);
        }
        CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(cartKey, productKey);
        cartItem.setCount(count + 0L);
        cartItem.setSubtotalPrice(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())));
        redisTemplate.opsForHash().put(cartKey, productKey, cartItem);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse changeAllCartItemCheckedStatus(Boolean checked, Integer id) {
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        //取出当前登录会员购物车中的商品
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);
        //遍历
        for (CartItem cartItem : cartItemList) {
            cartItem.setChecked(checked);
            redisTemplate.opsForHash().put(cartKey, cartItem.getProductId() + "", cartItem);
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse changeCartItemCheckedStatus(Integer productId, Integer id) {
        String cartKey = "cart:" + id;
        //判断有没有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        String productKey = productId + "";
        //判断当前登录会员的购物车中是否存在该商品
        if (!redisTemplate.opsForHash().hasKey(cartKey, productKey)) {
            return ServerResponse.error(ResponseEnum.CART_PRODUCT_IS_NOT_EXISTED);
        }
        CartItem cartItem = (CartItem) redisTemplate.opsForHash().get(cartKey, productKey);
        cartItem.setChecked(!cartItem.getChecked());
        redisTemplate.opsForHash().put(cartKey, productKey, cartItem);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse deleteCartItem(Integer productId, Integer id) {
        //先判断当前登录会员有没有购物车
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        String productKey = productId + "";
        //判断当前登录会员的购物车中是否存在该商品
        if (!redisTemplate.opsForHash().hasKey(cartKey, productKey)) {
            return ServerResponse.error(ResponseEnum.CART_PRODUCT_IS_NOT_EXISTED);
        }
        redisTemplate.opsForHash().delete(cartKey, productKey);
        return ServerResponse.success();
    }

    @Override
    public ServerResponse bacthDeleteCartItem(Integer id) {
        //先判断当前登录会员有没有购物车
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }
        //取出当前登录会员购物车中的商品
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);
        //遍历
        for (CartItem cartItem : cartItemList) {
            if (cartItem.getChecked()) {
                redisTemplate.opsForHash().delete(cartKey, cartItem.getProductId() + "");
            }
        }
        return ServerResponse.success();
    }

    @Override
    public ServerResponse queryCheckedCart(Integer id) {
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }

        //取出当前登录会员购物车中的商品
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);

        //用于存放购物车中被选中商品的List集合
        List<CartItem> checkedCartItemList = new ArrayList<>();

        //遍历购物车中的商品集合，计算总价格和总个数
        Long totalCount = 0L;
        BigDecimal totalPrice = new BigDecimal("0");
        for (CartItem cartItem : cartItemList) {
            //如果当前遍历的商品被选中
            if (cartItem.getChecked()) {
                checkedCartItemList.add(cartItem);
                totalCount += cartItem.getCount();
                totalPrice = totalPrice.add(cartItem.getSubtotalPrice());
            }
        }
        Cart cart = new Cart();
        cart.setTotalCount(totalCount);
        cart.setTotalPrice(totalPrice);
        cart.setCartItemList(checkedCartItemList);
        return ServerResponse.success(cart);
    }

    @Override
    public ServerResponse getCheckedStatus(Integer id) {
        String cartKey = "cart:" + id;
        //判断当前登录会员是否拥有购物车
        if (!redisTemplate.hasKey(cartKey)) {
            return ServerResponse.error(ResponseEnum.CART_IS_NOT_EXISTED);
        }

        //取出当前登录会员购物车中的商品
        List<CartItem> cartItemList = redisTemplate.opsForHash().values(cartKey);

        boolean checked = false;
        for (CartItem cartItem : cartItemList) {
            //如果当前遍历的商品被选中
            if (cartItem.getChecked()) {
                checked = true;
                break;
            }
        }
        return ServerResponse.success(checked);
    }


}