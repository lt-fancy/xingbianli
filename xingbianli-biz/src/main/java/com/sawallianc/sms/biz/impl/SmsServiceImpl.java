package com.sawallianc.sms.biz.impl;

import com.sawallianc.common.CacheUtil;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.sms.biz.SmsService;
import com.sawallianc.sms.dao.SmsDAO;
import com.sawallianc.sms.module.SmsDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class SmsServiceImpl implements SmsService{

    private static final String SMS_KEY = "sms:{0}";

    @Autowired
    private SmsDAO smsDAO;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Override
    public SmsDO getSmsById(Integer id) {
        String key = CacheUtil.generateCacheKey(SMS_KEY,id);
        SmsDO smsDO = redisValueOperations.get(key,SmsDO.class);
        if(null != smsDO){
            return smsDO;
        }
        smsDO = smsDAO.getSmsById(id);
        redisValueOperations.set(key,smsDO,60*60*24*30);
        return smsDO;
    }

    @Override
    public List<SmsDO> getAllSms() {
        return smsDAO.getAll();
    }
}
