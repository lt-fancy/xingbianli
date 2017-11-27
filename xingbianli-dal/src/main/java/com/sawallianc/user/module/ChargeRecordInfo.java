package com.sawallianc.user.module;

import lombok.Data;

import java.util.Date;

@Data
public class ChargeRecordInfo {
    private Long id;
    private String phone;
    private Integer chargeAmount;
    private Integer chargeMethod;
    private String chargeMethodName;
    private Integer bonusAmount;
    private Date chargeDate;
}
