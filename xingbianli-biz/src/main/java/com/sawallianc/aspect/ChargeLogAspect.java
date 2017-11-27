package com.sawallianc.aspect;

import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
public class ChargeLogAspect {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChargeLogAspect.class);
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private UserService userService;

    public ChargeLogAspect() {
    }
    @Pointcut("@annotation(chargeLogAnnotation)")
    public void cache(ChargeLogAnnotation chargeLogAnnotation){

    }

    @After("cache(chargeLogAnnotation)")
    public Object proceed(JoinPoint joinPoint, ChargeLogAnnotation chargeLogAnnotation) throws Throwable{
        BalanceVO balanceVO = (BalanceVO) joinPoint.getArgs()[0];
        ChargeRecordInfo chargeRecordInfo = new ChargeRecordInfo();
        chargeRecordInfo.setChargeAmount(balanceVO.getChargeAmount());
        chargeRecordInfo.setBonusAmount(balanceVO.getBonusAmount());
        chargeRecordInfo.setChargeDate(new Date());
        chargeRecordInfo.setPhone(balanceVO.getPhone());
        chargeRecordInfo.setChargeMethod(balanceVO.getChargeMethod());
        chargeRecordInfo.setChargeMethodName(balanceVO.getChargeMethodName());
        userService.recordChargeInfo(chargeRecordInfo);
        return balanceVO;
    }
}
