package com.fh.controller;

import com.fh.bean.Member;
import com.fh.common.ServerResponse;
import com.fh.service.CartService;
import org.omg.PortableServer.REQUEST_PROCESSING_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @RequestMapping("addCartItem")
    public ServerResponse addCartItem(Integer productId, HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.addCartItem(productId, loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ServerResponse.error();
    }

    //查询当前登录会员购物车中商品总数量
    @RequestMapping("getCartTotalCount")
    public ServerResponse getCartTotalCount(HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.getCartTotalCount(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    //查询当前登录会员购物车中的商品信息
    @RequestMapping("queryCart")
    public ServerResponse queryCart(HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.queryCart(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    //修改购物车中商品数量的API接口
    @RequestMapping("changeCartItemCount")
    public ServerResponse changeCartItemCount(Integer productId, Integer count, HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.changeCartItemCount(productId, count, loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    //购物车全选反选
    @RequestMapping("changeAllCartItemCheckedStatus")
    public ServerResponse changeAllCartItemCheckedStatus(Boolean checked, HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.changeAllCartItemCheckedStatus(checked, loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    @RequestMapping("changeCartItemCheckedStatus")
    public ServerResponse changeCartItemCheckedStatus(HttpServletRequest request, Integer productId) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.changeCartItemCheckedStatus(productId, loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    @RequestMapping("deleteCartItem")
    public ServerResponse deleteCartItem(HttpServletRequest request, Integer productId) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.deleteCartItem(productId, loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    @RequestMapping("bacthDeleteCartItem")
    public ServerResponse bacthDeleteCartItem(HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.bacthDeleteCartItem(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    //查询当前登录会员购物车中有没有被选中的商品
    @RequestMapping("getCheckedStatus")
    public ServerResponse getCheckedStatus(HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.getCheckedStatus(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

    //查询当前登录会员购物车中选中的商品信息
    @RequestMapping("queryCheckedCart")
    public ServerResponse queryCheckedCart(HttpServletRequest request) {

        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = cartService.queryCheckedCart(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }

    }

}
