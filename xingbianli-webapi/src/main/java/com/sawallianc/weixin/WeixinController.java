package com.sawallianc.weixin;

import com.sawallianc.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/weixin")
public class WeixinController {
    @Autowired
    private WeixinService service;

    @GetMapping("/getAccessToken")
    public Result getAccessToken(){
        return Result.getSuccessResult(service.getAccessToken());
    }
    @GetMapping("/getTicket")
    public Result getTicket(){
        return Result.getSuccessResult(service.getTicket());
    }

//    @GetMapping("/getSignature")
//    public Result getSignature(){
//
//    }
}
