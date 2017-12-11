package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.thirdparty.WeixinFeignClient;
import com.sawallianc.weixin.WeixinService;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.vo.WeixinPayVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.MessageDigest;
import java.util.*;

@Service
public class WeixinServiceImpl implements WeixinService {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String JS_TICKET = "js_ticket";

    @Resource
    private RedisValueOperations redisValueOperations;

    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @Override
    public String getAccessToken() {
        String token = redisValueOperations.get(ACCESS_TOKEN);
        if(!StringUtils.isBlank(token)){
            return token;
        }
        String grant_type = "client_credential";
        String appid = "wx31a33d085b32ff73";
        String secret = "34408901cccb09f653157fa649f3e634";
        JSONObject json = (JSONObject) weixinFeignClient.getAccessToken(grant_type,appid,secret);
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get access_token failed");
        }
        String value = json.getString("access_token");
        redisValueOperations.set(ACCESS_TOKEN,value,7190L);
        return value;
    }

    @Override
    public String getTicket() {
        String ticket = redisValueOperations.get(JS_TICKET);
        if(!StringUtils.isBlank(ticket)){
            return ticket;
        }
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get js_ticket failed");
        }
        String value = json.getString("ticket");
        redisValueOperations.set(JS_TICKET,value,7190L);
        return value;
    }

    @Override
    public String getSignature(String url,String timestamp) {
        String ticket = this.getTicket();
        return SHA1(url,ticket,timestamp);
    }

    @Override
    public WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo) {
        return null;
    }

    private static String SHA1(String url,String ticket,String timestamp) {
        MessageDigest messageDigest = null;
        String signature = null;
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
            messageDigest = MessageDigest.getInstance("SHA-1");
            messageDigest.reset();
            messageDigest.update(ready.getBytes("UTF-8"));
            signature = byteToHex(messageDigest.digest());
            messageDigest.update(ready.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signature;
    }
    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
