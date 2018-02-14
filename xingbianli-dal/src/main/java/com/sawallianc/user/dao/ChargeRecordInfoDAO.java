package com.sawallianc.user.dao;

import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.module.ChargeSucceedRecord;
import org.apache.ibatis.annotations.Param;

public interface ChargeRecordInfoDAO {
    /**
     * 记录充值流水
     * @param chargeRecordInfo
     * @return
     */
    int insertChargeRecordInfo(ChargeRecordInfo chargeRecordInfo);

    /**
     * 用户充值余额记录
     * @param chargeSucceedRecord
     * @return
     */
    int insertChargeSucceedRecord4Weixin(ChargeSucceedRecord chargeSucceedRecord);

    int insertChargeSucceedRecord4Alipay(ChargeSucceedRecord chargeSucceedRecord);

    Integer queryIfRecordWeixinOrderId(@Param("weixin") String weixin);

    Integer queryIfRecordAlipayOrderId(@Param("alipay") String alipay);
}
