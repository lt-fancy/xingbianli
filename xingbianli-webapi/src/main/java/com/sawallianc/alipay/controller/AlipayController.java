package com.sawallianc.alipay.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/alipay")
public class AlipayController {

    @GetMapping("/toPay")
    public String toPay(){
        // TODO
        return "";
    }
}
