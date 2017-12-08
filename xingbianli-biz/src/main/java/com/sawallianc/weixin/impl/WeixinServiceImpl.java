package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.thirdparty.WeixinFeignClient;
import com.sawallianc.weixin.WeixinService;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WeixinServiceImpl implements WeixinService {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String JS_TICKET = "js_ticket";

    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @Override
    public String getAccessToken() {
        String token = redisTemplate.opsForValue().get(ACCESS_TOKEN);
        if(!StringUtils.isBlank(token)){
            return token;
        }
        String grant_type = "client_credential";
        String appid = "wx31a33d085b32ff73";
        String secret = "1c44c3a294c77a433eeb6a3a440e2268";
        JSONObject json = (JSONObject) weixinFeignClient.getAccessToken(grant_type,appid,secret);
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get access_token failed");
        }
        String value = json.getString("access_token");
        redisTemplate.opsForValue().set(ACCESS_TOKEN,value);
        redisTemplate.expire(ACCESS_TOKEN,7190,TimeUnit.SECONDS);
        return value;
    }

    @Override
    public String getTicket() {
        String ticket = redisTemplate.opsForValue().get(JS_TICKET);
        if(!StringUtils.isBlank(ticket)){
            return ticket;
        }
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get js_ticket failed");
        }
        String value = json.getString("ticket");
        redisTemplate.opsForValue().set(JS_TICKET,value);
        redisTemplate.expire(JS_TICKET,7190,TimeUnit.SECONDS);
        return value;
    }

    @Override
    public String getSignature(String url,String timestamp) {
        String ticket = this.getTicket();
//        String timestamp = String.valueOf(System.currentTimeMillis());
        return SHA1(url,ticket,timestamp);
    }
    private static String SHA1(String url,String ticket,String timestamp) {
        MessageDigest messageDigest = null;
        try {
            String nonceStr = "fingertap";
            Map<String,String> map = new HashMap<String, String>();
            map.put("jsapi_ticket",ticket);
            map.put("noncestr",nonceStr);
            map.put("timestamp",timestamp);
            map.put("url",url);
            ArrayList<String> list=new ArrayList<String>();
            list.add("jsapi_ticket");
            list.add("noncestr");
            list.add("timestamp");
            list.add("url");
            Collections.sort(list);
            StringBuffer sb = new StringBuffer();
            for(String s : list){
                sb.append(s).append("=").append(map.get(s)).append("&");
            }
            String ready = sb.toString();
            ready = ready.substring(0,ready.length()-1);
            System.out.println("================加密前："+ready);
            messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(ready.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return getFormattedText(messageDigest.digest());
    }
    private static String getFormattedText(byte[] bytes) {
        int len = bytes.length;
        StringBuilder buf = new StringBuilder(len * 2);
        // 把密文转换成十六进制的字符串形式
        for (int j = 0; j < len; j++) {
            buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
            buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
        }
        return buf.toString();
    }
}
