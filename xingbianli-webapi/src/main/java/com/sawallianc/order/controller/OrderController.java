package com.sawallianc.order.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/order")
public class OrderController extends WebApiAdvice{

    @Autowired
    private OrderService orderService;

    @PostMapping(value = "/makeOrder")
    public Result makeOrder(@RequestBody OrderVO orderVO){
        if(null == orderVO){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter orderVO is null");
        }
        orderService.makeOrder(orderVO);
        return Result.getSuccessResult(0);
    }
}
