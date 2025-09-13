package com.tuling.mall.order.controller;



import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import cn.hutool.jwt.JWTValidator;
import com.alibaba.fastjson2.JSON;
import com.tuling.mall.order.entity.Order;
import com.tuling.mall.order.mapper.OrderMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.tuling.common.utils.R;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


/**
 * 
 *
 * @author fox
 * @email 2763800211@qq.com
 * @date 2021-01-28 15:46:19
 */
@RestController
@RequestMapping("/order")
@Slf4j
@RefreshScope
public class OrderController {

    @Value("${server.id}")
    private String serverId;

    @Value("${redis_ip}")
    private String redisIp;

    @Value("${common}")
    private String common;

    @Value("${common2:'oh'}")
    private String common2;

    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/config")
    public R getConfig() {

        return R.ok().put("redisIp", redisIp).put("common",common).put("common2",common2);
    }

    @GetMapping("/test/{wait}/{exp}")
    public R test(@PathVariable("wait") String wait,@PathVariable("exp") String exp) throws ExecutionException, InterruptedException {
        byte[] key = "1234567890".getBytes();

        String token = JWT.create()
                .setKey(key)
                .setPayload("username","zhangweijia")
                .setPayload("auth", Arrays.asList("sys:dict:data","sys:dict:add"))
                .setPayload("exp_date",System.currentTimeMillis() + (Long.parseLong(exp) * 1000))
                .sign();

        //等待10s
        Thread.sleep(Long.parseLong(wait) * 1000);

        JWT jwt = JWT.of(token);
        Object auth = jwt.getPayload("auth");
        log.info("auth: {}", JSON.toJSONString(auth));
        Long tokenExpTime = Long.parseLong(String.valueOf(jwt.getPayload("exp_date")));


        return R.ok().put("token", token).put("isExp",System.currentTimeMillis() > tokenExpTime);
    }

    /**
     * 根据用户id查询订单信息
     * @param userId
     * @return
     */
    @RequestMapping("/findOrderByUserId/{userId}")
    //@Transactional
    public R findOrderByUserId(@PathVariable("userId") Integer userId) {
        Order order = new Order();
        order.setOrderName("订单1");
        orderMapper.insert(order);
        return R.ok().put("orders", "success").put("serverId",serverId);
    }
}
