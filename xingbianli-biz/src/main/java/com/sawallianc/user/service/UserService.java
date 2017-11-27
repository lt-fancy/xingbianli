package com.sawallianc.user.service;

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
     * 记录充值信息
     * @param chargeRecordInfo
     */
    void recordChargeInfo(ChargeRecordInfo chargeRecordInfo);
}
