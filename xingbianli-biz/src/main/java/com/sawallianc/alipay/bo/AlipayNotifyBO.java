package com.sawallianc.alipay.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AlipayNotifyBO implements Serializable{
    private String notify_time;
    private String notify_type;
    private String notify_id;
    private String app_id;
    private String charset;
    private String version;
    private String sign_type;
    private String sign;
    private String trade_no;
    private String out_trade_no;
    private String out_biz_no;
    private String buyer_id;
    private String buyer_logon_id;
    private String seller_id;
    private String seller_email;
    private String trade_status;
    private Double total_amount;
    private Double receipt_amount;
    private Double invoice_amount;
    private Double buyer_pay_amount;
    private Double point_amount;
    private Double refund_fee;
    private String subject;
    private String body;
    private String gmt_create;
    private String gmt_payment;
    private String gmt_refund;
    private String gmt_close;
    private String fund_bill_list;
    private String passback_params;
    private String voucher_detail_list;
}
