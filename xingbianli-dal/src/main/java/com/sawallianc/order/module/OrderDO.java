package com.sawallianc.order.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class OrderDO extends BaseDO{
    private String orderId;
    private Long userId;
    private String goodsId;
    private String rackUUID;
    private Double goodsTotalPrice;
    private Double goodsSettlePrice;
    private Double benefitPrice;
    private Integer orderState;

}
