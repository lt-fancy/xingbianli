package com.sawallianc.thirdparty;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "weixinPay",url = "https://api.mch.weixin.qq.com")
public interface WeixinPayFeignClient {

    @RequestMapping(value = "/pay/unifiedorder",method = RequestMethod.POST)
    Object unionOrder(@RequestParam String xml);
}
