package com.sawallianc.sms.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class SmsBO extends BaseBO{
    private String smsName;
    private String smsText;
}
