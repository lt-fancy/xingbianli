package com.sawallianc.order.vo;

import com.sawallianc.order.module.OrderDetailDO;
import lombok.Data;

import java.util.List;

@Data
public class OrderDetailVO {
    private String goodsTotalPrice;
    private String benefitPrice;
    private String randomBenefitPrice;
    private String goodsSettlePrice;
    private List<OrderDetailDO> details;
}
