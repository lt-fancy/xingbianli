package com.sawallianc.user.service.impl;

import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChargeRecordInfoDAO chargeRecordInfoDAO;

    @Autowired
    private OrderService orderService;

    @Override
    @ChargeLogAnnotation
    @Transactional(rollbackFor = Exception.class)
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
    @Transactional(rollbackFor = Exception.class)
    public boolean purchase(BalanceVO balanceVO) {
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null");
        }
        Double price = balanceVO.getSettlePrice();
        String phone = balanceVO.getPhone();
        if(null == price || price < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"price is negative while purchasing");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while purchasing");
        }

        boolean flag = userDAO.purchase(price,phone) > 0;
        OrderVO orderVO = new OrderVO();
        orderVO.setBenefitPrice(balanceVO.getBenefitPrice());
        orderVO.setGoodsTotalPrice(balanceVO.getTotalPrice());
        orderVO.setGoodsSettlePrice(price);
        orderVO.setRackUUID(balanceVO.getRackUuid());
        orderVO.setPhone(phone);
        orderVO.setJson(balanceVO.getJson());
        orderService.makeOrder(orderVO);
        return flag;
    }

    @Override
    public void recordChargeInfo(ChargeRecordInfo chargeRecordInfo) {
        chargeRecordInfoDAO.insertChargeRecordInfo(chargeRecordInfo);
    }
}
