package com.sawallianc.order.module;

import lombok.Data;

@Data
public class OrderDetailDO {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private String goodsName;
    private Integer number;
    private Double price;
    private String phone;
    private String openid;
    private String alipayid;
    private String rackUuid;
    private String goodsTotalPrice;
    private String benefitPrice;
    private String randomBenefitPrice;
    private String goodsSettlePrice;
}
