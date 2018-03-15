package com.sawallianc.user.dao;

import com.sawallianc.user.module.WithholdRecordInfo;

public interface WithholdRecordInfoDAO {
    /**
     * 记录用户余额消费流水
     * @param withholdRecordInfo
     * @return
     */
    int insertWithholdRecordInfo(WithholdRecordInfo withholdRecordInfo);

}
