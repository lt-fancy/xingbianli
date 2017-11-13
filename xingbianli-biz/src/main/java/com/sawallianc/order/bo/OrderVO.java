package com.sawallianc.order.bo;

import lombok.Data;

@Data
public class OrderVO {
    private String userId;
    private String weixinId;
    private Long rackId;
    private String rackUUID;
    private String goodsIdCombine;
    private String goodsTotalPrice;
    private String goodsSettlePrice;
    private String benefitPrice;
}
