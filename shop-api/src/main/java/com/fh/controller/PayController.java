package com.fh.controller;

import com.fh.bean.Member;
import com.fh.common.ServerResponse;
import com.fh.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("pay")
public class PayController {

    @Autowired
    private PayService payService;

    //创建微信支付预支付订单
    @RequestMapping("createNative")
    public ServerResponse createNative(HttpServletRequest request) {
        try {
            Member loginMember = (Member) request.getAttribute("loginMember");
            ServerResponse serverResponse = payService.createNative(loginMember.getId());
            return serverResponse;
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

}
