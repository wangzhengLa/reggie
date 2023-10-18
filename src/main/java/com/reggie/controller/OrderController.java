package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.Orders;
import com.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> userPage(int page,int pageSize){
        Page<Orders> pageInfo=new Page<>();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Orders::getOrderTime);
        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 后台管理订单信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String number,String beginTime,String endTime){
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(number),Orders::getNumber,number);
        queryWrapper.like(beginTime!=null,Orders::getOrderTime,beginTime);
        queryWrapper.like(endTime!=null,Orders::getOrderTime,endTime);
        queryWrapper.orderByAsc(Orders::getCheckoutTime);

        Long userId = BaseContext.getCurrentId();

        List<Orders> records = pageInfo.getRecords();
        records = records.stream().map((item) -> {

            item.setUserName("小王");

            return item;
        }).collect(Collectors.toList());


        orderService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 订单派送
     * @param orders
     * @return
     */

    @PutMapping
    public R<String> updateOrder(@RequestBody Orders orders){
        LambdaUpdateWrapper<Orders> Wrapper = new LambdaUpdateWrapper<>();
        Wrapper.eq(Orders::getId,orders.getId());
        Wrapper.set(Orders::getStatus,orders.getStatus());
        orderService.update(Wrapper);
        return R.success("订单派送成功");
    }
}
