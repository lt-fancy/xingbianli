package com.sawallianc.weixin.entity;

import lombok.Data;

@Data
public class PayNotifyReceiveXmlEntity {
    private String return_code;
    private String return_msg;
    private String appid;
    private String mch_id;
    private String device_info;
    private String nonce_str;
    private String sign;
    private String sign_type;
    private String result_code;
    private String err_code;
    private String err_code_des;
    private String openid;
    private String is_subscribe;
    private String trade_type;
    private String bank_type;
    private String total_fee;
    private String settlement_total_fee;
    private String fee_type;
    private String cash_fee;
    private String cash_fee_type;
    private String coupon_fee;
    private String coupon_count;
    private String transaction_id;
    private String out_trade_no;
    private String time_end;
    private String attach;

    private String coupon_type_0;
    private String coupon_id_0;
    private String coupon_fee_0;

    private String coupon_type_1;
    private String coupon_id_1;
    private String coupon_fee_1;

    private String coupon_type_2;
    private String coupon_id_2;
    private String coupon_fee_2;

    private String coupon_type_3;
    private String coupon_id_3;
    private String coupon_fee_3;

    private String coupon_type_4;
    private String coupon_id_4;
    private String coupon_fee_4;
}
