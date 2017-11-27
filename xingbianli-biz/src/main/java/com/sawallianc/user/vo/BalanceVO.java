package com.sawallianc.user.vo;

import lombok.Data;

@Data
public class BalanceVO {
    private Double price;
    private Integer chargeAmount;
    private Integer bonusAmount;
    private Integer chargeMethod;
    private String chargeMethodName;
    private String phone;
}
