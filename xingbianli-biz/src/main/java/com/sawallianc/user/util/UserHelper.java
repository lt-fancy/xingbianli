package com.sawallianc.user.util;

import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.module.UserDO;
import org.apache.commons.lang3.StringUtils;

public class UserHelper {
    private UserHelper(){

    }
    public static UserDO doFromBo(UserBO bo){
        if(null == bo){
            return null;
        }
        UserDO userDO = new UserDO();
        userDO.setAlipayId(bo.getAlipayId());
        if(StringUtils.isNotBlank(bo.getBalance())){
            userDO.setBalance(Double.parseDouble(bo.getBalance()));
        }
        userDO.setOpenid(bo.getOpenid());
        userDO.setPhone(bo.getPhone());
        userDO.setUnionid(bo.getUnionid());
        return userDO;
    }

    public static UserBO boFromDo(UserDO userDO){
        if(null == userDO){
            return null;
        }
        UserBO bo = new UserBO();
        bo.setAlipayId(userDO.getAlipayId());
        bo.setBalance(String.valueOf(userDO.getBalance()));
        bo.setOpenid(userDO.getOpenid());
        bo.setPhone(userDO.getPhone());
        bo.setUnionid(userDO.getUnionid());
        return bo;
    }
}
