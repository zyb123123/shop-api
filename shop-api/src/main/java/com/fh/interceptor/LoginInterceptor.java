package com.fh.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.fh.bean.Member;
import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.utils.JsonUtil;
import com.fh.utils.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取请求头信息中的token
        String token = request.getHeader("abc");
        if (StringUtils.isBlank(token)) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_IS_NULL));
            return false;
        }

        //将token字符串以.进行分割
        String[] tokenArr = token.split("\\.");

//      判断token信息是否完整
        if (tokenArr.length != 3) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_IS_SHORT_OF));
            return false;
        }
//
//        //取出token中的会员信息部分和签名部分
//        String base64LoginMemberJson = tokenArr[0];
//        String base64Sign = tokenArr[1];
//
//        //将token中的会员信息部分再次进行签名，然后和token中签名部分作比较
//
//        //对进行Base64编码后(会员对象转为JSON格式的字符串)进行加密,也就是进行签名，签名的作用是为了防止客户端篡改TOKEN信息。
//        //在服务器端定义一个密钥，这个密钥只有服务器端知道
//        String secretKey = "dKskJsd23#3$%!~CS32*";
//        String sign = MD5Util.md5Hex(base64LoginMemberJson + secretKey);
//        //将签名字符串进行Base64编码
//        String newBase64Sign = Base64.getEncoder().encodeToString(sign.getBytes());
//
//        //如果不一致，则说明传过来的token信息被篡改
//        if(!base64Sign.equals(newBase64Sign)){
//            return false;
//        }
//       new String(Base64.getDecoder().decode(base64LoginMemberJson));
        String loginMemberJson = null;
        try {
            loginMemberJson = JwtUtil.verifyToken(token);
        } catch (Exception e) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_VERIFY_ERROR));
            return false;
        }
        Member loginMember = JSONObject.parseObject(loginMemberJson, Member.class);

        //从redis中查看是否存在当前登录会员对应的key
        if (!redisTemplate.hasKey("member:" + loginMember.getUuid())) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_IS_EXPIRED));
            return false;
        }

        //给redis中当前登录会员对应的key进行续命操作
        redisTemplate.expire("member:" + loginMember.getUuid(), 1, TimeUnit.HOURS);

        request.setAttribute("loginMember", loginMember);
        return true;
    }
}
