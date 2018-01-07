package com.sawallianc.sms.biz;

import com.sawallianc.sms.module.SmsDO;

import java.util.List;

public interface SmsService {
    /**
     * 根据短信id获取营销短信内容
     * @param id
     * @return
     */
    SmsDO getSmsById(Integer id);

    /**
     * 获取所有在用的营销短信内容
     * @return
     */
    List<SmsDO> getAllSms();
}
