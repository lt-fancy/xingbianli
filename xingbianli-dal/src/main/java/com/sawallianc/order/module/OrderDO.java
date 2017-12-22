package com.sawallianc.order.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class OrderDO extends BaseDO{
    private String orderId;
    private String phone;
    private String rackUUID;
    private Double goodsTotalPrice;
    private Double goodsSettlePrice;
    private Double benefitPrice;
    private Double randomBenefitPrice;
    private Integer orderState;

}
