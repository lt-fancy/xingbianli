package com.sawallianc.user.vo;

import lombok.Data;

@Data
public class BalanceVO {
    private Double totalPrice;
    private Double settlePrice;
    private Double benefitPrice;
    private Integer chargeAmount;
    private Integer bonusAmount;
    private Integer chargeMethod;
    private String chargeMethodName;
    private String phone;
    private String rackUuid;
    private String json;
}
