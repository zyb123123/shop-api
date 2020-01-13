package com.fh.service;

import com.fh.common.ServerResponse;

public interface CartService {
    ServerResponse addCartItem(Integer productId, Integer id);

    ServerResponse getCartTotalCount(Integer id);

    ServerResponse queryCart(Integer id);

    ServerResponse changeCartItemCount(Integer productId, Integer count, Integer id);

    ServerResponse changeAllCartItemCheckedStatus(Boolean checked, Integer id);

    ServerResponse changeCartItemCheckedStatus(Integer productId, Integer id);

    ServerResponse deleteCartItem(Integer productId, Integer id);

    ServerResponse bacthDeleteCartItem(Integer id);

    ServerResponse getCheckedStatus(Integer id);

    ServerResponse queryCheckedCart(Integer id);
}
