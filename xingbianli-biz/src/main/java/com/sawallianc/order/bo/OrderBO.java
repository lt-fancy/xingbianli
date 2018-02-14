package com.sawallianc.order.bo;

import com.sawallianc.BaseBO;
import com.sawallianc.order.module.OrderDetailDO;
import lombok.Data;

import java.util.List;

@Data
public class OrderBO extends BaseBO{
    private String orderId;
    private String phone;
    private String openid;
    private String alipayid;
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
    private Integer orderNum;
    private List<OrderDetailBO> details;
}
