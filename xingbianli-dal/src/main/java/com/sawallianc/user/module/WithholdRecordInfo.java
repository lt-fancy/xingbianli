package com.sawallianc.user.module;

import lombok.Data;

import java.util.Date;

@Data
public class WithholdRecordInfo {
    private Long id;
    private String phone;
    private Double beforeBalance;
    private Double afterBalance;
    private String orderId;
    private Date payTime;
}
