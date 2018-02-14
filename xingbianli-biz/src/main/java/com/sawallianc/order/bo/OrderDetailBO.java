package com.sawallianc.order.bo;

import lombok.Data;

@Data
public class OrderDetailBO {
    private String orderId;
    private String goodsId;
    private String number;
    private String price;
    private String phone;
    private String openid;
    private String alipayid;
    private String rackUuid;
    private String goodsName;
}
