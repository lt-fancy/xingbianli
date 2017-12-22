package com.sawallianc.user.service.impl;

import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.order.vo.DiscountVO;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.dao.ChargeRecordInfoDAO;
import com.sawallianc.user.dao.UserDAO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.util.UserHelper;
import com.sawallianc.user.vo.BalanceVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChargeRecordInfoDAO chargeRecordInfoDAO;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StateService stateService;

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
    public DiscountVO purchase(BalanceVO balanceVO) {
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null");
        }
        Double price = balanceVO.getSettlePrice();
        String phone = balanceVO.getPhone();
        UserBO user = this.queryUserInfoByPhone(phone);
        if(null == user){
            throw new BizRuntimeException(ResultCode.NOT_REGISTERED,"用户不存在");
        }
        if(price > Double.parseDouble(user.getBalance())){
            throw new BizRuntimeException(ResultCode.USER_BALANCE_NOT_ENOUGH,"用户余额不足");
        }
        if(null == price || price < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"price is negative while purchasing");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while purchasing");
        }
        List<StateBO> list = stateService.findChildrenStateByEname(Constant.RANDOM_DISCOUNT);
        DiscountVO discountVO = UserHelper.randomDiscount(list,price);
        boolean flag = userDAO.purchase(price,phone) > 0;
        OrderVO orderVO = new OrderVO();
        orderVO.setGoodsSettlePrice(discountVO.getSettlePrice());
        orderVO.setBenefitPrice(UserHelper.keep2Decimal(balanceVO.getTotalPrice()-orderVO.getGoodsSettlePrice()));
        orderVO.setGoodsTotalPrice(balanceVO.getTotalPrice());
        orderVO.setRackUUID(balanceVO.getRackUuid());
        orderVO.setPhone(phone);
        orderVO.setJson(balanceVO.getJson());
        orderVO.setRandomBenefitPrice(discountVO.getDiscount());
        orderService.makeOrder(orderVO, UUID.randomUUID().toString());
        return discountVO;
    }

    @Override
    public UserBO queryUserInfoByPhone(String phone) {
        return UserHelper.boFromDo(userDAO.queryUserByType(1,phone));
    }

    @Override
    public UserBO queryUserInfoByOpenid(String openid) {
        return UserHelper.boFromDo(userDAO.queryUserByType(3,openid));
    }

    @Override
    public UserBO queryUserInfoByAlipayId(String alipayId) {
        return UserHelper.boFromDo(userDAO.queryUserByType(2,alipayId));
    }

    @Override
    public boolean addUser(UserBO userBO) {
        UserBO exists = this.queryUserInfoByPhone(userBO.getPhone());
        //todo 暂时不知道微信openid和Alipayid会不会重复
        if(null != exists){
            //说明该手机号已注册
            throw new BizRuntimeException(ResultCode.PHONE_ALREADY_REGISTERED,"phone already registered");
        }
        return userDAO.addUser(UserHelper.doFromBo(userBO)) > 0;
    }

    @Override
    public void recordChargeInfo(ChargeRecordInfo chargeRecordInfo) {
        chargeRecordInfoDAO.insertChargeRecordInfo(chargeRecordInfo);
    }
}
