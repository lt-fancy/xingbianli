package com.sawallianc.weixin.vo;

import lombok.Data;

@Data
public class WeixinPayVO {
    private String prepayId;
    private String sign;
    private String orderId;
}
