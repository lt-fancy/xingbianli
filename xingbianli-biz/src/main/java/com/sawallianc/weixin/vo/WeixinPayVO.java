package com.sawallianc.weixin.vo;

import com.sawallianc.order.bo.OrderDetailBO;
import lombok.Data;

import java.util.List;

@Data
public class WeixinPayVO {
    private String prepayId;
    private String sign;
    private String orderId;
    private String phone;
    private String rackUUID;
    private String goodsTotalPrice;
    private String goodsSettlePrice;
    private String benefitPrice;
    private String randomBenefitPrice;
    private String orderState;
    private String firstGoodsPic;
    private String weixinOrderId;
    private String alipayOrderId;
    private String isBalancePurchased;
    private String gmtCreated;
    private Integer orderNum;
    private List<OrderDetailBO> details;
}
