package com.sawallianc.user.dao;

import com.sawallianc.user.module.ChargeRecordInfo;

public interface ChargeRecordInfoDAO {
    /**
     * 记录充值流水
     * @param chargeRecordInfo
     * @return
     */
    int insertChargeRecordInfo(ChargeRecordInfo chargeRecordInfo);
}
