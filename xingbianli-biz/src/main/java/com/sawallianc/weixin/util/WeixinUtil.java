package com.sawallianc.weixin.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.*;

/**
 * Created by sawallianc on 2017/12/11 0011.
 */
public class WeixinUtil {
    public static String SHA1(String url,String ticket,String timestamp,String nonceStr) {
        MessageDigest messageDigest = null;
        String signature = null;
        try {
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
    public static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String makeXml4UnionPrepay(WeixinUnionOrderBO bo){
        /**
         * <xml>
         <appid>wx31a33d085b32ff73</appid>
         <body>零食汇</body>
         <mch_id>1494046632</mch_id>
         <nonce_str>sawallianc</nonce_str>
         <notify_url>http://wxpay.wxutil.com/pub_v2/pay/notify.v2.php</notify_url>
         <openid>os8oH0xmVwZn2jMAqGTUOq2TKwj0</openid>
         <out_trade_no>2017121100000001</out_trade_no>
         <spbill_create_ip>211.149.244.44</spbill_create_ip>
         <total_fee>10</total_fee>
         <trade_type>JSAPI</trade_type>
         <sign>3F83AFFE89EBA9746098DE4A3614E16E</sign>
         </xml>
         */
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<appid><![CDATA[");
        sb.append(bo.getAppid());
        sb.append("]]></appid>");
        sb.append("<body><![CDATA[");
        sb.append(bo.getBody());
        sb.append("]]></body>");
        sb.append("<mch_id><![CDATA[");
        sb.append(bo.getMch_id());
        sb.append("]]></mch_id>");
        sb.append("<nonce_str><![CDATA[");
        sb.append(bo.getNonce_str());
        sb.append("]]></nonce_str>");
        sb.append("<notify_url><![CDATA[");
        sb.append(bo.getNotify_url());
        sb.append("]]></notify_url>");
        sb.append("<openid><![CDATA[");
        sb.append(bo.getOpenid());
        sb.append("]]></openid>");
        sb.append("<out_trade_no><![CDATA[");
        sb.append(bo.getOut_trade_no());
        sb.append("]]></out_trade_no>");
        sb.append("<spbill_create_ip><![CDATA[");
        sb.append(bo.getSpbill_create_ip());
        sb.append("]]></spbill_create_ip>");
        sb.append("<total_fee><![CDATA[");
        sb.append(bo.getTotal_fee());
        sb.append("]]></total_fee>");
        sb.append("<trade_type><![CDATA[");
        sb.append(bo.getTrade_type());
        sb.append("]]></trade_type>");
        sb.append("<sign><![CDATA[");
        sb.append(bo.getSign());
        sb.append("]]></sign>");
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 统一返回给微信的返回结果
     * @param msg
     * @param code
     * @return
     */
    public static String return2Weixin(String code,String msg){
        StringBuffer sb = new StringBuffer();
        sb.append("<xml><return_code><![CDATA[");
        sb.append(code.toUpperCase());
        sb.append("]]></return_code>");
        sb.append("<return_msg><![CDATA[");
        sb.append(msg);
        sb.append("]]></return_msg>");
        sb.append("</xml>");
        return sb.toString();
    }

    public static Map<String,Object> obj2Map(Object object,int type) {
        if(null == object){
            return null;
        }
        Map<String, Object> map = Maps.newHashMap();

        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object obj = field.get(object);
                String name = field.getName();
                if(null != obj){
                    if("timeStamp".equalsIgnoreCase(name) || "payType".equalsIgnoreCase(name)){
                        continue;
                    }
                    if(1==type){
                        if("sign".equalsIgnoreCase(name)){
                            continue;
                        }
                    }
                    map.put(field.getName(), obj);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static String sign(Map<String,Object> map){
        List<String> list = Lists.newArrayListWithCapacity(6);
        for(Map.Entry<String,Object> entry : map.entrySet()){
            if(null == entry.getValue()){
                continue;
            }
            list.add(entry.getKey());
        }
        Collections.sort(list);
        StringBuffer sb = new StringBuffer();
        for(String value : list){
            sb.append(value).append("=").append(map.get(value)).append("&");
        }
        String ready = sb.append("key=fingertap123456sawallianc1234567").toString();
        System.out.println("=======加密前"+ready);
        String result = getMD5(ready).toUpperCase();
        System.out.println("=======加密后"+result);
        return result;
    }

    public static String getMD5(String message) {
        String md5 = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");  // 创建一个md5算法对象
            byte[] messageByte = message.getBytes("UTF-8");
            byte[] md5Byte = md.digest(messageByte);              // 获得MD5字节数组,16*8=128位
            md5 = bytesToHex(md5Byte);                            // 转换为16进制字符串
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    // 二进制转十六进制
    public static String bytesToHex(byte[] bytes) {
        StringBuffer hexStr = new StringBuffer();
        int num;
        for (int i = 0; i < bytes.length; i++) {
            num = bytes[i];
            if(num < 0) {
                num += 256;
            }
            if(num < 16){
                hexStr.append("0");
            }
            hexStr.append(Integer.toHexString(num));
        }
        return hexStr.toString().toUpperCase();
    }
}
