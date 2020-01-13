package com.fh.controller;

import com.fh.common.ServerResponse;
import com.fh.utils.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.rmi.ServerError;
import java.util.List;

@RestController
@RequestMapping("snowflake")
public class SnowFlakeIdController {

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("getId")
    public ServerResponse getId() {
        try {
            String token = IdUtil.createIdWithoutTimestamp() + "";
            redisTemplate.opsForHash().put(token, token, token);
            return ServerResponse.success(token);
        } catch (Exception e) {
            e.printStackTrace();
            return ServerResponse.error();
        }
    }

}