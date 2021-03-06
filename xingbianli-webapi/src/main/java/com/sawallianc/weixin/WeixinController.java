package com.sawallianc.weixin;

import com.sawallianc.entity.Result;
import com.sawallianc.springboot.advice.WebApiAdvice;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.util.WeixinUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/weixin")
public class WeixinController extends WebApiAdvice {
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
    public Result getSignature(String url,String timestamp,String nonceStr){
        return Result.getSuccessResult(weixinService.getSignature(url,timestamp,nonceStr));
    }

    @GetMapping("/getOpenid")
    public Result getOpenid(String code){
        return Result.getSuccessResult(weixinService.getOpenid(code));
    }

    @PostMapping("/getPrepayId")
    public @ResponseBody
    Map<String,Object> getPrepayId(@RequestBody WeixinUnionOrderBO bo){
        return WeixinUtil.obj2Map(weixinService.getWeixinPayConfig(bo),0);
    }

    @PostMapping("/afterPaySucceed")
    public @ResponseBody String afterPaySucceed(@RequestBody String xml){
        return weixinService.notifyAfterPay(xml);
    }

    @PostMapping("/afterChargeSucceed")
    public @ResponseBody String afterChargeSucceed(@RequestBody String xml){
        return weixinService.notifyAfterCharge(xml);
    }


}
