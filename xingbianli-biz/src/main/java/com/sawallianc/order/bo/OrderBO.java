package com.sawallianc.order.bo;

import com.sawallianc.BaseBO;
import com.sawallianc.order.module.OrderDetailDO;
import lombok.Data;

import java.util.List;

@Data
public class OrderBO extends BaseBO{
    private String orderId;
    private String phone;
    private String rackUUID;
    private String goodsTotalPrice;
    private String goodsSettlePrice;
    private String benefitPrice;
    private String orderState;
    public List<OrderDetailDO> details;
}
