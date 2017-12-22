package com.sawallianc.order.bo;

import lombok.Data;

@Data
public class OrderVO {
    private String phone;
    private String rackUUID;
    private Double goodsTotalPrice;
    private Double goodsSettlePrice;
    private Double benefitPrice;
    private Double randomBenefitPrice;
    private String json;
}
