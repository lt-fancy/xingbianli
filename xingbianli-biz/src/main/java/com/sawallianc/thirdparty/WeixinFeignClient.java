package com.sawallianc.thirdparty;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "weixin",url = "https://api.weixin.qq.com")
public interface WeixinFeignClient {
    @RequestMapping(value = "/cgi-bin/token",method = RequestMethod.GET)
    Object getAccessToken(@RequestParam("grant_type")String grant_type,@RequestParam("appid")String appid
            ,@RequestParam("secret")String secret);

    @RequestMapping(value = "/cgi-bin/ticket/getticket",method = RequestMethod.GET)
    Object getTicket(@RequestParam("access_token")String access_token
            ,@RequestParam("type")String type);
}
