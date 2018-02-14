package com.sawallianc.alipay.biz.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.google.common.collect.Maps;
import com.sawallianc.alipay.biz.AlipayService;
import com.sawallianc.alipay.bo.AlipayBO;
import com.sawallianc.alipay.bo.AlipayNotifyBO;
import com.sawallianc.alipay.config.AlipayConfig;
import com.sawallianc.alipay.util.AlipayUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

@Service
public class AlipayServiceImpl implements AlipayService{

    private static final String FAIL = "fail";

    private static final String SUCCESS = "success";

    @Autowired
    private DefaultAlipayClient defaultAlipayClient;

    @Autowired
    private AlipayTradeCreateRequest alipayTradeCreateRequest;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @Override
    public String getUserInfo(String code) {
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AlipayConfig.APPID,
                AlipayConfig.RSA_PRIVATE_KEY, "json", AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, "RSA2");
        AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
        request.setCode(code);
        request.setGrantType("authorization_code");
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
            return oauthTokenResponse.getUserId();
        } catch (AlipayApiException e) {
            //处理异常
            throw new BizRuntimeException(ResultCode.ERROR,"with code:" +code+" get alipay user info failed :"+e);
        }
    }

    @Override
    public AlipayBO preOrder(AlipayBO alipayBO) {
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(5);
        String orderId = OrderIdUtil.getOrderId();
        map.put("out_trade_no", orderId);
        map.put("buyer_id", alipayBO.getBuyerId());
        String notifyUrl = "https://h5.ljlhz.com/api/alipay/alipayNotifyAfterPay";
        String subject = "零距狸-商品支付";
        boolean needOrder = true;
        if(null != alipayBO.getType() && alipayBO.getType() == 1){
            subject = "零距狸-余额充值";
            notifyUrl = "https://h5.ljlhz.com/api/alipay/alipayNotifyAfterCharge";
            needOrder = false;
        }
        map.put("total_amount",alipayBO.getTotal_amount());
        map.put("subject",subject);
        map.put("body",subject);
        try {
            if(StringUtils.isNotBlank(alipayBO.getPhone())){
                map.put("passback_params", URLEncoder.encode(alipayBO.getPhone(),AlipayConfig.CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            throw new BizRuntimeException(ResultCode.ALIPAY_PRE_ORDER_FAILURE,"alipay preOrder failed："+e);
        }
        alipayTradeCreateRequest.setNotifyUrl(notifyUrl);
        alipayTradeCreateRequest.setBizContent(JSONObject.toJSONString(map));
        try {
            AlipayTradeCreateResponse response = defaultAlipayClient.execute(alipayTradeCreateRequest);
            if(response.isSuccess()){
                AlipayBO result = new AlipayBO();
                result.setOutTradeNo(response.getOutTradeNo());
                result.setTradeNo(response.getTradeNo());
                if(needOrder){
                    OrderBO bo = orderService.makeOrder(generateOrderVO(alipayBO,orderId),0);
                    BeanUtils.copyProperties(bo,result);
                }
                return result;
            } else {
                throw new BizRuntimeException(ResultCode.ALIPAY_PRE_ORDER_FAILURE,"alipay preOrder failed："+response);
            }
        } catch (AlipayApiException e) {
            throw new BizRuntimeException(ResultCode.ALIPAY_PRE_ORDER_FAILURE,"alipay preOrder failed："+e);
        }
    }

    private OrderVO generateOrderVO(AlipayBO alipayBO,String orderId){
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(alipayBO,vo);
        vo.setBenefitPrice(Double.parseDouble(alipayBO.getBenefitPrice()));
        vo.setGoodsSettlePrice(Double.parseDouble(alipayBO.getGoodsSettlePrice()));
        vo.setGoodsTotalPrice(Double.parseDouble(alipayBO.getGoodsTotalPrice()));
        vo.setOrderId(orderId);
        vo.setAlipayid(alipayBO.getBuyerId());
        return vo;
    }

    @Override
    public String alipayNotifyAfterPay(AlipayNotifyBO alipayNotifyBO) {
        String value = this.check(alipayNotifyBO,true);
        if(!SUCCESS.equalsIgnoreCase(value)){
            return FAIL;
        }
        String phone = "";
        try {
            if(StringUtils.isNotBlank(alipayNotifyBO.getPassback_params())){
                phone = URLDecoder.decode(alipayNotifyBO.getPassback_params(),AlipayConfig.CHARSET);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return FAIL;
        }
        String orderNo = alipayNotifyBO.getOut_trade_no();
        String alipayTradeNo = alipayNotifyBO.getTrade_no();
        String notifyId = alipayNotifyBO.getNotify_id();
        Integer notify = orderService.queryIfNotifyAlipay(notifyId);
        Integer alipay = orderService.queryIfDealtAlipay(alipayTradeNo);
        if(null != notify && notify.intValue() == 1){
            if(null == alipay || alipay.intValue() != 1){
                orderService.updateOrderState2SucceedAlipay(orderNo,alipayTradeNo,phone);
            }
            return SUCCESS;
        }
        orderService.insertAlipayNotify(notifyId);
        orderService.updateOrderState2SucceedAlipay(orderNo,alipayTradeNo,phone);
        return SUCCESS;
    }
    @Override
    public String alipayNotifyAfterCharge(AlipayNotifyBO alipayNotifyBO) {
        String value = this.check(alipayNotifyBO,false);
        if(!SUCCESS.equalsIgnoreCase(value)){
            return FAIL;
        }
        String notifyId = alipayNotifyBO.getNotify_id();
        Integer notify = orderService.queryIfNotifyAlipay(notifyId);
        String alipayTradeNo = alipayNotifyBO.getTrade_no();
        Integer result = userService.queryIfRecordAlipayOrderId(alipayTradeNo);
        if((null != notify && notify.intValue() == 1) && (null != result && result.intValue() == 1)){
            return SUCCESS;
        }
        orderService.insertAlipayNotify(notifyId);
        BalanceVO vo = new BalanceVO();
        int chargeAmount = alipayNotifyBO.getTotal_amount().intValue();
        vo.setChargeAmount(chargeAmount);
        UserBO user = userService.queryUserInfoByAlipayId(alipayNotifyBO.getBuyer_id());
        if(null == user){
            throw new BizRuntimeException(ResultCode.NOT_REGISTERED,"alipayid:"+alipayNotifyBO.getBuyer_id()+" is not registered");
        }
        vo.setPhone(user.getPhone());
        List<StateBO> stateBOS = stateService.findChildrenStateByEname(Constant.CHARGE_ENAME);
        if(CollectionUtils.isEmpty(stateBOS)){
            throw new BizRuntimeException(ResultCode.ERROR,"charge bonus is not configured");
        }
        for(StateBO bo : stateBOS){
            String[] array = bo.getStateName().split(",");
            Integer configuredChargeBonus = Integer.parseInt(array[1]);
            if(chargeAmount == Integer.parseInt(array[0])){
                vo.setBonusAmount(configuredChargeBonus);
                break;
            }
        }
        if(null == vo.getBonusAmount()){
            vo.setBonusAmount(0);
        }
        vo.setChargeMethod(0);
        vo.setChargeMethodName(Constant.ChargeMethod.getNameByCode(vo.getChargeMethod()));
        userService.recordChargeSucceed(alipayTradeNo,0);
        if(!userService.charge(vo)){
            throw new BizRuntimeException(ResultCode.ERROR,"充值失败");
        }
        return SUCCESS;
    }

    private String check(AlipayNotifyBO alipayNotifyBO,boolean needCheckOrder){
        System.out.println("=================================alipayNotifyBO:"+alipayNotifyBO);
        Map<String,String> map = AlipayUtil.obj2Map(alipayNotifyBO);
        try {
            System.out.println("=========================================map:"+map);
            boolean flag = AlipaySignature.rsaCheckV1(map,AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.CHARSET,AlipayConfig.SIGNTYPE);
            if(flag){
                return FAIL;
            }
            String orderNo = alipayNotifyBO.getOut_trade_no();
            if(StringUtils.isBlank(orderNo)){
                return FAIL;
            }
            if(needCheckOrder){
                OrderBO orderBO = orderService.getOrderByOrderNo(orderNo);
                if(!checkIfOrderEffective(alipayNotifyBO,orderBO)){
                    return FAIL;
                }
            }
            return SUCCESS;
        } catch (AlipayApiException e) {
            throw new BizRuntimeException(ResultCode.ERROR,"alipay notify check sign error："+e);
        }
    }

    private boolean checkIfOrderEffective(AlipayNotifyBO alipayNotifyBO,OrderBO orderBO){
        if(null == orderBO){
            return false;
        }
        if(!alipayNotifyBO.getOut_trade_no().equalsIgnoreCase(orderBO.getOrderId())){
            return false;
        }
        if(!String.valueOf(alipayNotifyBO.getTotal_amount()).equalsIgnoreCase(orderBO.getGoodsSettlePrice())){
            return false;
        }
        System.out.println("=======================seller_id"+alipayNotifyBO.getSeller_id());
        if(!alipayNotifyBO.getApp_id().equalsIgnoreCase(AlipayConfig.APPID)){
            return false;
        }
        return true;
    }

}
