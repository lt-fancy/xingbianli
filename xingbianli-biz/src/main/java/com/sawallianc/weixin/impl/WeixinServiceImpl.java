package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.sawallianc.thirdparty.WeixinFeignClient;
import com.sawallianc.weixin.WeixinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeixinServiceImpl implements WeixinService {
    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @Override
    public String getAccessToken() {
        String grant_type = "client_credential";
        String appid = "wx31a33d085b32ff73";
        String secret = "1c44c3a294c77a433eeb6a3a440e2268";
        JSONObject json = (JSONObject) weixinFeignClient.getAccessToken(grant_type,appid,secret);
        return json.getString("access_token");
    }

    @Override
    public String getTicket() {
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        return json.getString("ticket");
    }

    @Override
    public String getSignature() {
        String noncestr = "sawallianc";
        String jsapi_ticket = this.getTicket();
        String timestamp = String.valueOf(System.currentTimeMillis());
        return null;
    }
}
