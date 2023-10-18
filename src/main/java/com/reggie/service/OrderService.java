package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.entity.Orders;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.core.annotation.Order;

public interface OrderService extends IService<Orders> {

    public void submit(Orders orders);
}
