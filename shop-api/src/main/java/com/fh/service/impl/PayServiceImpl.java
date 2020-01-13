package com.fh.service.impl;

import com.fh.bean.PayLog;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.service.PayService;
import com.fh.utils.DateUtil;
import com.github.wxpay.sdk.MyWxConfig;
import com.github.wxpay.sdk.WXPay;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public ServerResponse createNative(Integer id) {
        //判断登陆的会员有没有支付日志
        if (!redisTemplate.hasKey("payLog:" + id)) {
            return ServerResponse.error(ResponseEnum.PAY_LOG_IS_NULL);
        }

        //从redis中取出支付日志
        PayLog payLog = (PayLog) redisTemplate.opsForValue().get("payLog:" + id);

        try {
            //调用微信支付统一下单API接口生成预支付交易单
            WXPay wxPay = new WXPay(new MyWxConfig());
            //准备调用统一下单API接口需要的参数
            Map<String, String> map = new HashMap<>();
            map.put("body", "飞狐商城支付订单");
            map.put("out_trade_no", payLog.getOutTradeNo());
            map.put("total_fee", payLog.getPayMoney().multiply(new BigDecimal("100")).intValue() + "");
            map.put("spbill_create_ip", "127.0.0.1");
            //将预支付订单是失效时间设为在系统当前时间上加5分钟
            map.put("time_expire", DateUtil.format(DateUtils.addMinutes(new Date(), 5)));
            map.put("notify_url", "http://www.baidu.com");
            map.put("trade_type", "NAT  IVE");
            //调用微信支付统一下单API接口
            Map<String, String> resultMap = wxPay.unifiedOrder(map);

            //判断接口返回状态码是否为SUCCESS
            if (!resultMap.get("return_code").equalsIgnoreCase("SUCCESS")) {
                return ServerResponse.error(123, "微信支付失败，错误信息为:" + resultMap.get("return_msg"));
            }

            //判断接口返回业务结果是否为SUCCESS
            if (!resultMap.get("result_code").equalsIgnoreCase("SUCCESS")) {
                return ServerResponse.error(123, "微信支付失败，错误信息为:" + resultMap.get("err_code_des"));
            }

            //获取接口返回的二维码链接
            String codeUrl = resultMap.get("code_url");

            //将订单的总金额，商家支付订单号，二维码链接响应给客户端
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("totalPrice", payLog.getPayMoney());
            responseMap.put("outTradeNo", payLog.getOutTradeNo());
            responseMap.put("codeUrl", codeUrl);

            return ServerResponse.success(responseMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }
}
