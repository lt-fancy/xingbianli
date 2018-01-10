package com.sawallianc.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.google.common.collect.Lists;
import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.order.vo.DiscountVO;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.sms.biz.SmsService;
import com.sawallianc.sms.module.SmsDO;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.dao.ChargeRecordInfoDAO;
import com.sawallianc.user.dao.UserDAO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.module.ChargeSucceedRecord;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.util.UserHelper;
import com.sawallianc.user.vo.BalanceVO;
import com.sawallianc.weixin.cons.WexinConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{

    private static final String CHECK_CODE = "checkcode:{0}";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChargeRecordInfoDAO chargeRecordInfoDAO;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RedisValueOperations redisValueOperations;

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
        boolean flag = this.withhold(balanceVO);
        OrderVO orderVO = new OrderVO();
        orderVO.setGoodsSettlePrice(discountVO.getSettlePrice());
        orderVO.setBenefitPrice(UserHelper.keep2Decimal(balanceVO.getTotalPrice()-orderVO.getGoodsSettlePrice()));
        orderVO.setGoodsTotalPrice(balanceVO.getTotalPrice());
        orderVO.setRackUUID(balanceVO.getRackUuid());
        orderVO.setPhone(phone);
        orderVO.setJson(balanceVO.getJson());
        orderVO.setRandomBenefitPrice(discountVO.getDiscount());
        orderService.makeOrder(orderVO, OrderIdUtil.getOrderId(),1);
        return discountVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withhold(BalanceVO balanceVO){
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null while withholding");
        }
        Double price = balanceVO.getSettlePrice();
        String phone = balanceVO.getPhone();
        if(null == price){
            throw new BizRuntimeException(ResultCode.ERROR,"price can't be null while withholding");
        }
        if(price < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"price must bigger than 0 while withholding");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while withholding");
        }
        return userDAO.purchase(price,phone) > 0;
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
    public UserBO addUser(UserBO userBO) {
        if(null == userBO){
            throw new BizRuntimeException(ResultCode.ERROR,"userBo is null while add user");
        }
        String code = userBO.getCheckCode();
        if(StringUtils.isBlank(code)){
            throw new BizRuntimeException(ResultCode.WRONG_CHECK_CODE,"checkcode is blank while register");
        }
        String phone = userBO.getPhone();
        String key = CacheUtil.generateCacheKey(CHECK_CODE,phone);
        JSONObject cacheCode = redisValueOperations.get(key,JSONObject.class);
        if(null == cacheCode || cacheCode.isEmpty()){
            throw new BizRuntimeException(ResultCode.CHECK_CODE_EXPIRED,"check code expired");
        }
        String checkcode = cacheCode.getString("checkcode");
        if(StringUtils.isBlank(checkcode)){
            throw new BizRuntimeException(ResultCode.CHECK_CODE_EXPIRED,"check code expired");
        }
        if(!code.equals(checkcode)){
            throw new BizRuntimeException(ResultCode.WRONG_CHECK_CODE,"check code is not correct,param code is "+code+" while cacheCode is "+cacheCode);
        }
        UserBO exists = this.queryUserInfoByPhone(phone);
        //todo 暂时不知道微信openid和Alipayid会不会重复
        if(null != exists){
            //说明该手机号已注册
            throw new BizRuntimeException(ResultCode.PHONE_ALREADY_REGISTERED,"phone already registered");
        }
        userDAO.addUser(UserHelper.doFromBo(userBO));
        userBO.setBalance("0.00");
        userBO.setCheckCode(null);
        return userBO;
    }

    @Override
    public void recordChargeInfo(ChargeRecordInfo chargeRecordInfo) {
        chargeRecordInfoDAO.insertChargeRecordInfo(chargeRecordInfo);
    }

    @Override
    public boolean recordChargeSucceed(String recordId){
        ChargeSucceedRecord record = new ChargeSucceedRecord();
        record.setWeixinOrderId(recordId);
        return chargeRecordInfoDAO.insertChargeSucceedRecord4Weixin(record) > 0;
    }

    @Override
    public Integer queryIfRecordWeixinOrderId(String weixin) {
        return chargeRecordInfoDAO.queryIfRecordWeixinOrderId(weixin);
    }

    @Override
    public String sendCheckCode(String phone) {
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.BLANK_MOBILE,"phone is blank while register");
        }
        Random random = new Random();
        String code = String.valueOf(random.nextInt(8999)+1000);
        try {
            SmsSingleSender sender = new   SmsSingleSender(WexinConstant.SMS_APP_ID, WexinConstant.SMS_APP_KEY);
            ArrayList<String> params = Lists.newArrayListWithExpectedSize(3);
            params.add(code);
            params.add("10");
            SmsSingleSenderResult result = sender.sendWithParam("86", phone, 74010, params, "", "", "");
            System.out.println(result);
            String key = CacheUtil.generateCacheKey(CHECK_CODE,phone);
            JSONObject jsonCache = new JSONObject();
            jsonCache.put("checkcode",code);
            redisValueOperations.set(key,jsonCache,10*60);
            return code;
        } catch (Exception e) {
            throw new BizRuntimeException(ResultCode.SEND_CODE_ERROR_HAPPEN,"error occured while send checkcode"+e);
        }
    }

    @Override
    public String batchSend(Integer id) {
        if(null == id){
            throw new BizRuntimeException(ResultCode.ERROR,"id is null while batch send sms");
        }
        SmsDO smsDO = smsService.getSmsById(id);
        if(null == smsDO) {
            throw new BizRuntimeException(ResultCode.ERROR, "sms is null while id =" + id);
        }
        batchSend(smsDO.getSmsText());
        return "发送成功";
    }

    private void batchSend(String text){
        try {
            SmsMultiSender multiSender = new SmsMultiSender(WexinConstant.SMS_APP_ID, WexinConstant.SMS_APP_KEY);
            // 下面是 3 个假设的号码
            ArrayList<String> phoneNumbers = userDAO.getAllPhone();
            ArrayList<String> param = Lists.newArrayListWithCapacity(1);
            param.add(text);
            SmsMultiSenderResult multiSenderResult = multiSender.sendWithParam("86", phoneNumbers, 74244, param, "", "", "");
            System.out.println(multiSenderResult);
        } catch (Exception e){

        }
    }
}
