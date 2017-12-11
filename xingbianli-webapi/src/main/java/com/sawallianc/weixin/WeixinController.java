package com.sawallianc.weixin;

import com.sawallianc.entity.Result;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/weixin")
public class WeixinController {
    @Autowired
    private WeixinService weixinService;

    @GetMapping("/getAccessToken")
    public Result getAccessToken(){
        return Result.getSuccessResult(weixinService.getAccessToken());
    }
    @GetMapping("/getTicket")
    public Result getTicket(){
        return Result.getSuccessResult(weixinService.getTicket());
    }

    @GetMapping("/getSignature")
    public Result getSignature(String url,String timestamp){
        return Result.getSuccessResult(weixinService.getSignature(url,timestamp));
    }

    @PostMapping("/getPrepayId")
    public Result getPrepayId(@RequestBody WeixinUnionOrderBO bo){
        return Result.getSuccessResult(weixinService.getWeixinPayConfig(bo));
    }
}
