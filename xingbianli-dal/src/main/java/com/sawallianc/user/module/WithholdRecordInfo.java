package com.sawallianc.user.module;

import lombok.Data;

import java.util.Date;

@Data
public class WithholdRecordInfo {
    private Long id;
    private String phone;
    private String beforeBalance;
    private String afterBalance;
    private String orderId;
    private Date payTime;
}
