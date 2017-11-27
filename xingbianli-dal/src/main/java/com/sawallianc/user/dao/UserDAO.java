package com.sawallianc.user.dao;

import org.apache.ibatis.annotations.Param;

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

}
