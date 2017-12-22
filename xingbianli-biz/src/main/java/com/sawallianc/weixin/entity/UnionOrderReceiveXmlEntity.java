package com.sawallianc.weixin.entity;

import lombok.Data;

/**
 * 统一下单返回数据
 * @author fingertap
 * Created by fingertap on 2017/6/2.
 */
@Data
public class UnionOrderReceiveXmlEntity {
    private String return_code="";
    private String return_msg="";
    private String appid="";
    private String mch_id="";
    private String nonce_str="";
    private String openid="";
    private String sign="";
    private String result_code="";
    private String prepay_id="";
    private String trade_type="";
}
