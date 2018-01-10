package com.sawallianc.user.module;

import lombok.Data;

import java.util.Date;

@Data
public class ChargeSucceedRecord {
    private Long id;
    private String weixinOrderId;
    private String alipayOrderId;
    private Date happenTime;
}
