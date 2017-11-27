package com.sawallianc.user.service.impl;

import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.dao.ChargeRecordInfoDAO;
import com.sawallianc.user.dao.UserDAO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChargeRecordInfoDAO chargeRecordInfoDAO;

    @Override
    @ChargeLogAnnotation
    public boolean charge(BalanceVO balanceVO) {
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null");
        }
        Integer chargeAmount = balanceVO.getChargeAmount();
        String phone = balanceVO.getPhone();
        if(null == chargeAmount || chargeAmount < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"chargeAmount is negative while charging");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while charging");
        }
        return userDAO.charge(chargeAmount,balanceVO.getBonusAmount(),phone) > 0;
    }

    @Override
    public boolean purchase(BalanceVO balanceVO) {
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null");
        }
        Double price = balanceVO.getPrice();
        String phone = balanceVO.getPhone();
        if(null == price || price < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"price is negative while purchasing");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while purchasing");
        }
        return userDAO.purchase(price,phone) > 0;
    }

    @Override
    public void recordChargeInfo(ChargeRecordInfo chargeRecordInfo) {
        chargeRecordInfoDAO.insertChargeRecordInfo(chargeRecordInfo);
    }
}
