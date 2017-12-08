package com.sawallianc.weixin;

public interface WeixinService {
    String getAccessToken();

    String getTicket();

    String getSignature(String url,String timestamp);
}
