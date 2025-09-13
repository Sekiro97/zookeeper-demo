package com.tuling.mall.user.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tuling.mall.user.entity.Order;
import com.tuling.mall.user.entity.User;

@DS("s1")
public interface OrderMapper extends BaseMapper<Order> {
}
