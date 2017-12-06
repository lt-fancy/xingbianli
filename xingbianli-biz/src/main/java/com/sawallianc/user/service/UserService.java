package com.sawallianc.user.service;

import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.vo.BalanceVO;

public interface UserService {
    /**
     * 用户余额充值
     * @param balanceVO
     * @return
     */
    boolean charge(BalanceVO balanceVO);

    /**
     * 直接用余额消费
     * @param balanceVO
     * @return
     */
    boolean purchase(BalanceVO balanceVO);

    /**
     * 根据手机号查找用户信息
     * @param phone
     * @return
     */
    UserBO queryUserInfoByPhone(String phone);

    /**
     * 根据微信openid查找用户信息
     * @param openid
     * @return
     */
    UserBO queryUserInfoByOpenid(String openid);

    /**
     * 根据支付宝id查找用户信息
     * @param alipayId
     * @return
     */
    UserBO queryUserInfoByAlipayId(String alipayId);
    /**
     * 注册新增用户
     * @param userBO
     * @return
     */
    boolean addUser(UserBO userBO);

    /**
     * 记录充值信息
     * @param chargeRecordInfo
     */
    void recordChargeInfo(ChargeRecordInfo chargeRecordInfo);
}
