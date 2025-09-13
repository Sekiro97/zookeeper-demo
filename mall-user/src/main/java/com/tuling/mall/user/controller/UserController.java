package com.tuling.mall.user.controller;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.tuling.common.utils.R;
import com.tuling.mall.user.entity.Order;
import com.tuling.mall.user.entity.User;
import com.tuling.mall.user.feign.OrderFeignService;
import com.tuling.mall.user.mapper.OrderMapper;
import com.tuling.mall.user.mapper.UserMapper;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 *
 * @author fox
 * @email 2763800211@qq.com
 * @date 2021-01-28 15:53:24
 */
@RestController
@RequestMapping("/user")
@Slf4j
@RefreshScope
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${cfg1}")
    private String cfg1;

    @Value("${redis_ip}")
    private String redisIp;

    @Value("${common}")
    private String common;

    @Value("${var1.var2:null}")
    private String var;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;



    @RequestMapping(value = "/config/{id}")
    @GlobalTransactional
    //@DSTransactional
    public R getConfig(@PathVariable("id") Long id) throws InterruptedException {
        User user = new User();
        user.setUserName("张炜佳");
        userMapper.insert(user);

//        Order order = new Order();
//        order.setOrderName("订单2");
//        orderMapper.insert(order);
        R result = orderFeignService.findOrderByUserId(1);

        if(id == 1) {
            int rs = 1/0;
        }
        return R.ok().put("cfg1",cfg1).put("redisIp",redisIp).put("common",common).put("var",var);
    }


    @RequestMapping(value = "/findOrderByUserId/{id}")
    public R findOrderByUserId(@PathVariable("id") Integer id) {
        log.info("根据userId:"+id+"查询订单信息");
        // RestTemplate调用
//        String url = "http://localhost:8020/order/findOrderByUserId/"+id;
//        R result = restTemplate.getForObject(url,R.class);

        //模拟ribbon实现
        //String url = getUri("mall-order")+"/order/findOrderByUserId/"+id;
        // 添加@LoadBalanced
//        String url = "http://mall-order/order/findOrderByUserId/"+id;
//        R result = restTemplate.getForObject(url,R.class);

        //feign调用   封装 ribbon调用   rpc调用 http
        R result = orderFeignService.findOrderByUserId(id);

        return result;
    }


    @Autowired
    private DiscoveryClient discoveryClient;

    public String getUri(String serviceName) {
        // 获取服务的实例
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceName);
        if (serviceInstances == null || serviceInstances.isEmpty()) {
            return null;
        }
        int serviceSize = serviceInstances.size();
        //轮询
        int indexServer = incrementAndGetModulo(serviceSize);
        return serviceInstances.get(indexServer).getUri().toString();
    }
    private AtomicInteger nextIndex = new AtomicInteger(0);
    private int incrementAndGetModulo(int modulo) {
        for (;;) {
            int current = nextIndex.get();
            int next = (current + 1) % modulo;
            if (nextIndex.compareAndSet(current, next) && current < modulo){
                return current;
            }
        }
    }

}
