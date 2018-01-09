package com.sawallianc.weixin.bo;

import com.sawallianc.order.bo.OrderVO;
import lombok.Data;

@Data
public class WeixinUnionOrderBO extends OrderVO {
    private String appid;
    private String mch_id;
    private String nonce_str;
    private String sign;
    private String body;
    private String out_trade_no;
    private String spbill_create_ip;
    private String notify_url;
    private String trade_type;
    private String openid;
    private Integer total_fee;
    private String timeStamp;
    private String payType;
}
