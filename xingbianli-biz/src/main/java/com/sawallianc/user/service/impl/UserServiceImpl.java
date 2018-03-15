package com.sawallianc.user.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.qcloudsms.SmsMultiSender;
import com.github.qcloudsms.SmsMultiSenderResult;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.annotation.ChargeLogAnnotation;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderDetailBO;
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
import com.sawallianc.user.dao.WithholdRecordInfoDAO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.module.ChargeSucceedRecord;
import com.sawallianc.user.module.WithholdRecordInfo;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.util.UserHelper;
import com.sawallianc.user.vo.BalanceVO;
import com.sawallianc.weixin.cons.WexinConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService{

    private static final String CHECK_CODE = "checkcode:{0}";

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ChargeRecordInfoDAO chargeRecordInfoDAO;

    @Autowired
    private WithholdRecordInfoDAO withholdRecordInfoDAO;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StateService stateService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Resource
    private RedisTemplate redisTemplate;

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
    public OrderBO purchase(BalanceVO balanceVO) {
        if(null == balanceVO){
            throw new BizRuntimeException(ResultCode.ERROR,"balanceVO is null");
        }
        Double price = balanceVO.getSettlePrice();
        String phone = balanceVO.getPhone();
        UserBO user = this.queryUserInfoByPhone(phone);
        String beforeBalance = user.getBalance();
        if(null == user){
            throw new BizRuntimeException(ResultCode.NOT_REGISTERED,"用户不存在");
        }
        if(price > Double.parseDouble(beforeBalance)){
            throw new BizRuntimeException(ResultCode.USER_BALANCE_NOT_ENOUGH,"用户余额不足");
        }
        if(null == price || price < 0){
            throw new BizRuntimeException(ResultCode.ERROR,"price is negative while purchasing");
        }
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"phone is blank while purchasing");
        }
        List<StateBO> list = stateService.findChildrenStateByEname(Constant.RANDOM_DISCOUNT);
        List<OrderDetailBO> details = JSONArray.parseArray(balanceVO.getJson(), OrderDetailBO.class);
        List<Long> goodIds = Lists.newArrayListWithCapacity(details.size());
        Map<Long,BigDecimal> map = Maps.newHashMapWithExpectedSize(details.size());
        for(OrderDetailBO detailBO : details){
            Long goodsId = Long.parseLong(detailBO.getGoodsId());
            goodIds.add(goodsId);
            map.put(goodsId,new BigDecimal(detailBO.getPrice()).multiply(new BigDecimal(detailBO.getNumber())));
        }
        List<GoodsBO> goodsList = goodsService.queryGoodsByGoodsId(goodIds);
        if(CollectionUtils.isEmpty(goodsList)){
            throw new BizRuntimeException(ResultCode.GOODS_DOWN,"goods are down,please refresh");
        }
        BigDecimal noNeedRandomPrice = new BigDecimal("0");
        BigDecimal needRandomPrice = new BigDecimal("0");
        for(GoodsBO goodsBO : goodsList){
            Long id = goodsBO.getId();
            BigDecimal inner = map.get(id);
            if(0 == goodsBO.getIsRandomDiscount()){
                //需要参加随机折扣
                needRandomPrice = needRandomPrice.add(inner);
            } else {
                noNeedRandomPrice = noNeedRandomPrice.add(inner);
            }
        }
        DiscountVO discountVO = UserHelper.randomDiscount(list,needRandomPrice.doubleValue());
        double realSettlePrice = new BigDecimal(discountVO.getSettlePrice()).add(noNeedRandomPrice).doubleValue();
        boolean flag = this.withhold(phone,realSettlePrice);
		if(!flag){
			throw new BizRuntimeException(ResultCode.ERROR,"balance withhold failed");
		}
        redisTemplate.opsForValue().set("beforeBalance",beforeBalance);
        redisTemplate.opsForValue().set("afterBalance",new BigDecimal(beforeBalance).subtract(new BigDecimal(realSettlePrice)).doubleValue()+"");
        OrderVO orderVO = new OrderVO();
        orderVO.setGoodsSettlePrice(realSettlePrice);
        orderVO.setGoodsTotalPrice(balanceVO.getTotalPrice());
        orderVO.setRackUUID(balanceVO.getRackUuid());
        orderVO.setBenefitPrice(balanceVO.getBenefitPrice());
        orderVO.setPhone(phone);
        orderVO.setJson(balanceVO.getJson());
        orderVO.setRandomBenefitPrice(discountVO.getDiscount());
        return orderService.makeOrder(orderVO,1);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean withhold(String phone, Double price){
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
        UserBO user = UserHelper.boFromDo(userDAO.queryUserByType(3,openid));
        return user;
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
        String openid = userBO.getOpenid();
        String alipayId = userBO.getAlipayId();
        if(StringUtils.isBlank(openid) && StringUtils.isBlank(alipayId)){
            throw new BizRuntimeException(ResultCode.ERROR,"openid and alipayId both are null while register");
        }
        if(StringUtils.isNotBlank(openid) && StringUtils.isNotBlank(alipayId)){
            throw new BizRuntimeException(ResultCode.ERROR,"openid and alipayId both are not null while register");
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
        if(null != exists){
            if(StringUtils.isNotBlank(openid)){
                if(StringUtils.isBlank(exists.getOpenid())){
                    userDAO.updateWeixin(phone,openid);
                    userBO.setOpenid(openid);
                    return userBO;
                }
                if(openid.equalsIgnoreCase(exists.getOpenid())){
                    return exists;
                } else{
                    //说明该手机号已注册
                    throw new BizRuntimeException(ResultCode.PHONE_ALREADY_REGISTERED,"phone already registered");
                }
            }
            if(StringUtils.isNotBlank(alipayId)){
                if(StringUtils.isBlank(exists.getAlipayId())){
                    userDAO.updateAlipay(phone,alipayId);
                    userBO.setAlipayId(alipayId);
                    return userBO;
                }
                if(alipayId.equalsIgnoreCase(exists.getAlipayId())){
                    return exists;
                } else{
                    //说明该手机号已注册
                    throw new BizRuntimeException(ResultCode.PHONE_ALREADY_REGISTERED,"phone already registered");
                }
            }
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
    public boolean recordChargeSucceed(String recordId,int type){
        ChargeSucceedRecord record = new ChargeSucceedRecord();
        if(0==type){
            record.setAlipayOrderId(recordId);
            return chargeRecordInfoDAO.insertChargeSucceedRecord4Alipay(record) > 0;
        } else {
            record.setWeixinOrderId(recordId);
            return chargeRecordInfoDAO.insertChargeSucceedRecord4Weixin(record) > 0;
        }
    }

    @Override
    public Integer queryIfRecordWeixinOrderId(String weixin) {
        return chargeRecordInfoDAO.queryIfRecordWeixinOrderId(weixin);
    }

    @Override
    public Integer queryIfRecordAlipayOrderId(String alipay) {
        return chargeRecordInfoDAO.queryIfRecordAlipayOrderId(alipay);
    }

    @Override
    public void insertWithholdRecord(WithholdRecordInfo withholdRecordInfo) {
        withholdRecordInfoDAO.insertWithholdRecordInfo(withholdRecordInfo);
    }

    @Override
    public String sendCheckCode(String phone,String openid,String alipayId) {
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.BLANK_MOBILE,"phone is blank while register");
        }
        String checkFrequentlySendKey = phone+openid+alipayId;
        if(redisValueOperations.exists(checkFrequentlySendKey)){
            throw new BizRuntimeException(ResultCode.SEND_CODE_TOO_FREQUENTLY,"phone is request checkcode too frequently");
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
            //防止前端不拦住60s获取验证码
            redisValueOperations.set(checkFrequentlySendKey,phone+"sa",60);
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
