package com.sawallianc.config;

import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.sawallianc.alipay.config.AlipayConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AlipayConfiguration {

    @Bean
    public DefaultAlipayClient defaultAlipayClient(){
        DefaultAlipayClient client = new DefaultAlipayClient(AlipayConfig.URL,AlipayConfig.APPID,AlipayConfig.RSA_PRIVATE_KEY,
                AlipayConfig.FORMAT,AlipayConfig.CHARSET,AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        return client;
    }

    @Bean
    public AlipayTradeCreateRequest alipayTradeCreateRequest(){
        AlipayTradeCreateRequest request = new AlipayTradeCreateRequest();
        return request;
    }

}
