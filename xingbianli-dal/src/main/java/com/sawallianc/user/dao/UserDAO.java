package com.sawallianc.user.dao;

import com.sawallianc.user.module.UserDO;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface UserDAO {
    /**
     * 充值
     * @param chargeAmount
     * @param phone
     * @return
     */
    int charge(@Param("chargeAmount") Integer chargeAmount,@Param("bonusAmount") Integer bonusAmount,@Param("phone")String phone);

    /**
     * 直接用余额购买商品
     * @param price
     * @param phone
     * @return
     */
    int purchase(@Param("price") Double price,@Param("phone")String phone);

    /**
     * 根据特定条件查找用户
     * 1、手机号，2、支付宝id，3、微信openid
     * @param value
     * @param type
     * @return
     */
    UserDO queryUserByType(@Param("type")Integer type,@Param("value")String value);

    /**
     * 注册用户
     * @param userDO
     * @return
     */
    int addUser(UserDO userDO);

    /**
     * 获取所有正常的用户
     * @return
     */
    ArrayList<String> getAllPhone();
}
