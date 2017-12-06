package com.sawallianc.order.module;

import lombok.Data;

@Data
public class OrderDetailDO {
    private Long id;
    private Long orderId;
    private Long goodsId;
    private Integer number;
    private Double price;
    private String phone;
    private String rackUuid;
}
