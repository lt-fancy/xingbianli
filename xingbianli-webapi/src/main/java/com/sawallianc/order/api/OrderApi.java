package com.sawallianc.order.api;

import com.sawallianc.springboot.advice.WebApiAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/order")
public class OrderApi extends WebApiAdvice{
}
