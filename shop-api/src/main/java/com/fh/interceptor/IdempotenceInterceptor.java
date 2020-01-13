package com.fh.interceptor;

import com.fh.common.ResponseEnum;
import com.fh.common.ServerResponse;
import com.fh.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IdempotenceInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uuid = request.getParameter("uuid");

        //判断请求传过来的参数是否包含token
        if (StringUtils.isBlank(uuid)) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_IS_NULL));
            return false;
        }

        //判断token是否在redis中存在
        if (!redisTemplate.hasKey(uuid)) {
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.TOKEN_IS_CHANGED));
            return false;
        }

        //从redis中将token删除掉
        if (redisTemplate.opsForHash().delete(uuid, uuid) < 1) {
            //如果从redis中删除时返回的值小于0，说明之前这个token肯定被删过了！
            JsonUtil.outJson(response, ServerResponse.error(ResponseEnum.REPETITIVE_OPERATION));
            return false;
        }
        return true;
    }

}
