package com.sawallianc.order.controller;

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
        orderService.makeOrder(orderVO, UUID.randomUUID().toString());
        return Result.getSuccessResult(0);
    }

    @GetMapping(value = "/queryOrderByPhone/{rack}/{phone}")
    public Result queryOrderByPhone(@PathVariable String rack,@PathVariable String phone){
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter phone is blank while querying order info");
        }
        if(StringUtils.isBlank(rack)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter rack is blank while querying order info");
        }
        return Result.getSuccessResult(orderService.queryOrderInfo(phone,rack));
    }
}
