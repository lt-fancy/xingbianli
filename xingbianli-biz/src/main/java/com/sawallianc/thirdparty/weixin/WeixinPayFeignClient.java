package com.sawallianc.thirdparty.weixin;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name="xingbianli-weixin")
public interface WeixinPayFeignClient {
    @RequestMapping(value = "/weixin/feign/weixinPay",method = RequestMethod.POST)
    @ResponseBody String unionOrder(@RequestBody String xml);
}
