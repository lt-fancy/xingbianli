package com.sawallianc.weixin.entity;

import lombok.Data;

/**
 * @author fingertap
 * Created by fingertap on 2017/6/2.
 */
@Data
public class ReceiveXmlEntity {
    private String returnCode="";
    private String returnMsg="";
    private String appid="";
    private String mchId="";
    private String nonceStr="";
    private String openid="";
    private String sign="";
    private String resultCode="";
    private String prepayId="";
    private String tradeType="";
}
