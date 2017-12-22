package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.snow.SnowflakeIdWorker;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.thirdparty.WeixinFeignClient;
import com.sawallianc.thirdparty.WeixinPayFeignClient;
import com.sawallianc.user.service.UserService;
import com.sawallianc.weixin.WeixinService;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.cons.WexinConstant;
import com.sawallianc.weixin.entity.PayNotifyReceiveXmlEntity;
import com.sawallianc.weixin.entity.ReceiveXmlProcess;
import com.sawallianc.weixin.entity.UnionOrderReceiveXmlEntity;
import com.sawallianc.weixin.util.WeixinUtil;
import com.sawallianc.weixin.vo.WeixinPayVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private StateService stateService;

    @Autowired
    private UserService userService;

    @Override
    public String getAccessToken() {
        String token = redisTemplate.opsForValue().get(ACCESS_TOKEN);
        if(!StringUtils.isBlank(token)){
            return token;
        }
        String grant_type = "client_credential";
        String appid = "wx31a33d085b32ff73";
        String secret = "34408901cccb09f653157fa649f3e634";
        JSONObject json = (JSONObject) weixinFeignClient.getAccessToken(grant_type,appid,secret);
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get access_token failed");
        }
        String value = json.getString("access_token");
        redisTemplate.opsForValue().set(ACCESS_TOKEN,value,7190L, TimeUnit.SECONDS);
        return value;
    }

    @Override
    public String getTicket() {
        String ticket = redisTemplate.opsForValue().get(JS_TICKET);
        if(!StringUtils.isBlank(ticket)){
            return ticket;
        }
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get js_ticket failed");
        }
        String value = json.getString("ticket");
        redisTemplate.opsForValue().set(JS_TICKET,value,7190L, TimeUnit.SECONDS);
        return value;
    }

    @Override
    public String getSignature(String url,String timestamp) {
        String ticket = this.getTicket();
        return WeixinUtil.SHA1(url,ticket,timestamp);
    }

    @Override
    public WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo) {
        SnowflakeIdWorker snow = new SnowflakeIdWorker(0L,0L);
        String orderId = String.valueOf(snow.nextId());
        bo.setOut_trade_no(orderId);
        bo.setTotal_fee(1);
        bo.setOpenid("os8oH0xmVwZn2jMAqGTUOq2TKwj0");
        bo.setSpbill_create_ip("192.168.1.100");
        bo.setAppid(WexinConstant.APP_ID);
        StateBO stateBO = stateService.findStateByEnameAndStateId("weixin_body",1);
        bo.setBody(stateBO.getStateName());
        bo.setMch_id(WexinConstant.MCH_ID);
        bo.setNonce_str(WexinConstant.nonce_str);
        bo.setNotify_url(stateService.findStateByEnameAndStateId("weixin_notify_url",1).getStateName());
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
        map.put("appId","wx31a33d085b32ff73");
        map.put("timeStamp","1512697027");
        map.put("nonceStr","sawallianc");
        map.put("package",vo.getPrepayId());
        map.put("signType","MD5");
        vo.setSign(WeixinUtil.sign(map));
        bo.setBenefitPrice(0D);
        bo.setGoodsTotalPrice(0D);
        bo.setGoodsSettlePrice(0.01);
        bo.setRackUUID("qweqweqwe");
        bo.setPhone("17682305850");
        bo.setJson("[{\"goodsId\":\"1\",\"number\":\"2\",\"price\":\"1.88\"},{\"goodsId\":\"2\",\"number\":\"5\",\"price\":\"4.9\"},{\"goodsId\":\"4\",\"number\":\"3\",\"price\":\"2.7\"}]");
        //todo 后期在redis维护一定数量的id，每次用一个就取一个。当数量减少到阈值的时候就尾端进行补
        orderService.makeOrder(bo,orderId);
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

        List<String> weixinOrderIds = orderService.queryPayedWeixinOrderIds();
        if(weixinOrderIds.contains(entity.getTransaction_id())){
            return WeixinUtil.return2Weixin(FAIL,"this order has been dealt");
        }
        orderService.updateOrderState2Succeed(entity.getOut_trade_no(),entity.getTransaction_id());
        return WeixinUtil.return2Weixin(SUCCESS,"OK");
    }

}
