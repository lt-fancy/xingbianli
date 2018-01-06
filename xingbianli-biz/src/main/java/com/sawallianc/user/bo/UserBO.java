package com.sawallianc.user.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class UserBO extends BaseBO{
    private String phone;
    private String openid;
    private String unionid;
    private String alipayId;
    private String balance;
    private String weixinPic;
    private String alipayPic;
}
