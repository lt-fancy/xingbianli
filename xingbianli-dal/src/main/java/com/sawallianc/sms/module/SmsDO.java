package com.sawallianc.sms.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class SmsDO extends BaseDO{
    private String smsName;
    private String smsText;
}
