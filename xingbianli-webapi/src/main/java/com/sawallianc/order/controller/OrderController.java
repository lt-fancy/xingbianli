package com.sawallianc.order.controller;

import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
        return Result.getSuccessResult(orderService.makeOrder(orderVO,0));
    }

    @GetMapping(value = "/queryOrderByPhone/{phone}")
    public Result queryOrderByPhone(@PathVariable String phone){
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter phone is blank while querying order info");
        }
        return Result.getSuccessResult(orderService.queryOrderInfoByPhone(phone));
    }

    @GetMapping(value = "/queryOrderByAlipayId/{alipayId}")
    public Result queryOrderByAlipayId(@PathVariable String alipayId){
        if(StringUtils.isBlank(alipayId)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter alipayId is blank while querying order info");
        }
        return Result.getSuccessResult(orderService.queryOrderInfoByAlipayId(alipayId));
    }

    @GetMapping(value = "/queryOrderByOpenid/{openid}")
    public Result queryOrderByOpenid(@PathVariable String openid){
        if(StringUtils.isBlank(openid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter openid is blank while querying order info");
        }
        return Result.getSuccessResult(orderService.queryOrderInfoByOpenid(openid));
    }

    @GetMapping(value = "/queryOrderDetailsByOrderId")
    public Result queryOrderDetailsByOrderId(String orderId){
        if(StringUtils.isBlank(orderId)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter orderId is blank while querying order details");
        }
        return Result.getSuccessResult(orderService.queryOrderDetailByOrderId(orderId));
    }
}
