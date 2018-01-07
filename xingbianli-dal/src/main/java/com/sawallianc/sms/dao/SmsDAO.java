package com.sawallianc.sms.dao;

import com.sawallianc.sms.module.SmsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SmsDAO {
    SmsDO getSmsById(@Param("id") Integer id);

    List<SmsDO> getAll();
}
