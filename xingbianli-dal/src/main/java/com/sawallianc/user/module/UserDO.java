package com.sawallianc.user.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class UserDO extends BaseDO{
    private String phone;
    private String openid;
    private String unionid;
    private String alipayId;
    private Double balance;
}
