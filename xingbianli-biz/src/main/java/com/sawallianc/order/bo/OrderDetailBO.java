package com.sawallianc.order.bo;

import lombok.Data;

@Data
public class OrderDetailBO {
    private String orderId;
    private String goodsId;
    private String number;
    private String price;
}
