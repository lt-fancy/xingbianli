package com.sawallianc.alipay.bo;

import com.sawallianc.order.bo.OrderBO;
import lombok.Data;

import java.io.Serializable;

@Data
public class AlipayBO extends OrderBO implements Serializable{
    private String body;
    private String subject;
    private String out_trade_no;
    private Double total_amount;
    private String product_code = "QUICK_WAP_WAY";
    private String passback_params;
    private String outTradeNo;
    private String tradeNo;
    private String buyerId;
    private Integer type;
    private String json;
}
