package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sawallianc.common.Constant;
import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.thirdparty.weixin.WeixinFeignClient;
import com.sawallianc.thirdparty.weixin.WeixinPayFeignClient;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import com.sawallianc.weixin.WeixinService;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.cons.WexinConstant;
import com.sawallianc.weixin.entity.PayNotifyReceiveXmlEntity;
import com.sawallianc.weixin.entity.ReceiveXmlProcess;
import com.sawallianc.weixin.entity.UnionOrderReceiveXmlEntity;
import com.sawallianc.weixin.util.WeixinUtil;
import com.sawallianc.weixin.vo.WeixinPayVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class WeixinServiceImpl implements WeixinService {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String JS_TICKET = "js_ticket";

    private static final String FAIL = "FAIL";

    private static final String SUCCESS = "SUCCESS";

    private static final Logger LOGGER = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @Autowired
    private WeixinPayFeignClient weixinPayFeignClient;

    @Autowired
    private OrderService orderService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Autowired
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Override
    public String getAccessToken() {
        JSONObject cache = redisValueOperations.get(ACCESS_TOKEN, JSONObject.class);
        if(null != cache && !cache.isEmpty()){
            String token = cache.getString("access_token");
            if(StringUtils.isNotBlank(token) && !"null".equals(token.trim())){
                return token;
            }
        }
        JSONObject json = (JSONObject) weixinFeignClient.getAccessToken("client_credential",WexinConstant.APP_ID,WexinConstant.SECRET);
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get access_token failed");
        }
        String value = json.getString("access_token");
        redisValueOperations.set(ACCESS_TOKEN,json,7190);
        return value;
    }

    @Override
    public String getTicket() {
        JSONObject cache = redisValueOperations.get(JS_TICKET, JSONObject.class);
        if(null != cache && !cache.isEmpty()){
            String ticket = cache.getString("ticket");
            if(StringUtils.isNotBlank(ticket) && !"null".equals(ticket.trim())){
                return ticket;
            }
        }
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get js_ticket failed");
        }
        String value = json.getString("ticket");
        redisValueOperations.set(JS_TICKET,json,7190);
        return value;

    }

    @Override
    public String getOpenid(String code) {
        if(StringUtils.isBlank(code)){
            throw new BizRuntimeException(ResultCode.ERROR,"code is blank while get openid");
        }
        String json = weixinFeignClient.getOpenid(WexinConstant.APP_ID,WexinConstant.SECRET,code,"authorization_code");

        if(StringUtils.isBlank(json)){
            throw new BizRuntimeException(ResultCode.ERROR,"get openid error");
        }
        JSONObject object = JSONObject.parseObject(json);
        if(object.isEmpty()||!object.containsKey("openid")){
            throw new BizRuntimeException(ResultCode.ERROR,"get openid error,code:"+object.getString("errcode")+",msg:"+object.getString("errmsg"));
        }
        return object.getString("openid");
    }

    @Override
    public String getSignature(String url,String timestamp,String nonceStr) {
        String ticket = this.getTicket();
        return WeixinUtil.SHA1(url,ticket,timestamp,nonceStr);
    }

    @Override
    public WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo) {
        String orderId = OrderIdUtil.getOrderId();
        bo.setOut_trade_no(orderId);
        bo.setOrderId(orderId);
        bo.setSpbill_create_ip(bo.getSpbill_create_ip());
        bo.setAppid(WexinConstant.APP_ID);
        boolean needMakeOrder = true;
        if("pay".equalsIgnoreCase(bo.getPayType())){
            bo.setBody("零距狸-商品支付");
            bo.setNotify_url(stateService.findStateByEnameAndStateId("weixin_notify_url",1).getStateName());
        } else {
            bo.setBody("零距狸-余额充值");
            bo.setNotify_url(stateService.findStateByEnameAndStateId("weixin_notify_url",2).getStateName());
            needMakeOrder = false;
        }
        bo.setMch_id(WexinConstant.MCH_ID);
        bo.setTrade_type(WexinConstant.trade_type);
        LOGGER.info("=================bo: {}",bo);
        bo.setSign(WeixinUtil.sign(WeixinUtil.obj2Map(bo,1)));
        String xml = WeixinUtil.makeXml4UnionPrepay(bo);
        LOGGER.info("=================xml: {}",xml);
        String xmlResult = weixinPayFeignClient.unionOrder(xml);
        LOGGER.info("=================xmlResult: {}",xmlResult);
        UnionOrderReceiveXmlEntity entity =(UnionOrderReceiveXmlEntity) ReceiveXmlProcess.getMsgEntity(xmlResult,UnionOrderReceiveXmlEntity.class);
        if(null == entity || FAIL.equalsIgnoreCase(entity.getReturn_code()) || FAIL.equalsIgnoreCase(entity.getResult_code())){
            throw new BizRuntimeException(ResultCode.ERROR,"call weixin union order failed");
        }
        WeixinPayVO vo = new WeixinPayVO();
        vo.setPrepayId("prepay_id="+entity.getPrepay_id());
        Map<String,Object> map = Maps.newHashMap();
        map.put("appId",WexinConstant.APP_ID);
        map.put("timeStamp",bo.getTimeStamp());
        map.put("nonceStr",bo.getNonce_str());
        map.put("package",vo.getPrepayId());
        map.put("signType","MD5");
        vo.setSign(WeixinUtil.sign(map));
        vo.setOrderId(orderId);
        if(!needMakeOrder){
            return vo;
        }
        OrderBO orderBO = orderService.makeOrder(bo,0);
        BeanUtils.copyProperties(orderBO,vo);
        LOGGER.info("============vo:"+vo);
        return vo;
    }

    @Override
    public String notifyAfterPay(String xml) {
        if(StringUtils.isBlank(xml)){
            return WeixinUtil.return2Weixin(FAIL,"xml is empty");
        }
        LOGGER.info("=============after pay success,weixin return xml"+xml);
        PayNotifyReceiveXmlEntity entity = (PayNotifyReceiveXmlEntity) ReceiveXmlProcess.getMsgEntity(xml,PayNotifyReceiveXmlEntity.class);
        if(null == entity){
            return WeixinUtil.return2Weixin(FAIL,"wrong xml format");
        }
        if(FAIL.equalsIgnoreCase(entity.getReturn_code())){
            return WeixinUtil.return2Weixin(FAIL,entity.getReturn_msg());
        }
        if(FAIL.equalsIgnoreCase(entity.getResult_code())){
            LOGGER.error(entity.getErr_code_des());
            return WeixinUtil.return2Weixin(FAIL,entity.getErr_code());
        }
        Map<String,Object> map = WeixinUtil.obj2Map(entity,1);
        String sign = WeixinUtil.sign(map);
        if(!sign.equalsIgnoreCase(entity.getSign())){
            return WeixinUtil.return2Weixin(FAIL,"invalid sign");
        }

        Integer result = orderService.queryIfDealtWeixin(entity.getTransaction_id());
        if(null != result && result.intValue() == 1){
            return WeixinUtil.return2Weixin(SUCCESS,"OK");
        }
        orderService.updateOrderState2SucceedWeixin(entity.getOut_trade_no(),entity.getTransaction_id(),entity.getAttach());
        return WeixinUtil.return2Weixin(SUCCESS,"OK");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String notifyAfterCharge(String xml) {
        if(StringUtils.isBlank(xml)){
            return WeixinUtil.return2Weixin(FAIL,"xml is empty");
        }
        LOGGER.info("=============after charge success,weixin return xml"+xml);
        PayNotifyReceiveXmlEntity entity = (PayNotifyReceiveXmlEntity) ReceiveXmlProcess.getMsgEntity(xml,PayNotifyReceiveXmlEntity.class);
        if(null == entity){
            return WeixinUtil.return2Weixin(FAIL,"wrong xml format");
        }
        if(FAIL.equalsIgnoreCase(entity.getReturn_code())){
            return WeixinUtil.return2Weixin(FAIL,entity.getReturn_msg());
        }
        if(FAIL.equalsIgnoreCase(entity.getResult_code())){
            LOGGER.error(entity.getErr_code_des());
            return WeixinUtil.return2Weixin(FAIL,entity.getErr_code());
        }
        Map<String,Object> map = WeixinUtil.obj2Map(entity,1);
        String sign = WeixinUtil.sign(map);
        if(!sign.equalsIgnoreCase(entity.getSign())){
            return WeixinUtil.return2Weixin(FAIL,"invalid sign");
        }
        String weixinOrderId = entity.getTransaction_id();
        Integer result = userService.queryIfRecordWeixinOrderId(weixinOrderId);
        if(null != result && result.intValue() == 1){
            //如果处理过，直接返回成功
            return WeixinUtil.return2Weixin(SUCCESS,"OK");
        }
        BalanceVO vo = new BalanceVO();
        int chargeAmount = Integer.parseInt(entity.getTotal_fee()) / 100;
        vo.setChargeAmount(chargeAmount);
        UserBO user = userService.queryUserInfoByOpenid(entity.getOpenid());
        if(null == user){
            throw new BizRuntimeException(ResultCode.NOT_REGISTERED,"openid:"+entity.getOpenid()+" is not registered");
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
        vo.setChargeMethod(1);
        vo.setChargeMethodName(Constant.ChargeMethod.getNameByCode(vo.getChargeMethod()));
        userService.recordChargeSucceed(weixinOrderId,1);
        if(!userService.charge(vo)){
            throw new BizRuntimeException(ResultCode.ERROR,"充值失败");
        }
        return WeixinUtil.return2Weixin(SUCCESS,"OK");
    }

}
