package com.sawallianc.weixin.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.thirdparty.WeixinFeignClient;
import com.sawallianc.thirdparty.WeixinPayFeignClient;
import com.sawallianc.weixin.WeixinService;
import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.cons.WexinConstant;
import com.sawallianc.weixin.entity.ReceiveXmlEntity;
import com.sawallianc.weixin.entity.ReceiveXmlProcess;
import com.sawallianc.weixin.util.WeixinUtil;
import com.sawallianc.weixin.vo.WeixinPayVO;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

@Service
public class WeixinServiceImpl implements WeixinService {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String JS_TICKET = "js_ticket";

    private static final String FAIL = "FAIL";

    private static final String SUCCESS = "SUCCESS";

    @Resource
    private RedisValueOperations redisValueOperations;

    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @Autowired
    private WeixinPayFeignClient weixinPayFeignClient;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StateService stateService;

    @Override
    public String getAccessToken() {
        String token = redisValueOperations.get(ACCESS_TOKEN);
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
        redisValueOperations.set(ACCESS_TOKEN,value,7190L);
        return value;
    }

    @Override
    public String getTicket() {
        String ticket = redisValueOperations.get(JS_TICKET);
        if(!StringUtils.isBlank(ticket)){
            return ticket;
        }
        JSONObject json = (JSONObject) weixinFeignClient.getTicket(this.getAccessToken(),"jsapi");
        if(json.isEmpty()){
            throw new BizRuntimeException(ResultCode.ERROR,"get js_ticket failed");
        }
        String value = json.getString("ticket");
        redisValueOperations.set(JS_TICKET,value,7190L);
        return value;
    }

    @Override
    public String getSignature(String url,String timestamp) {
        String ticket = this.getTicket();
        return WeixinUtil.SHA1(url,ticket,timestamp);
    }

    @Override
    public WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo) {
        String orderId = UUID.randomUUID().toString();
        bo.setOut_trade_no(orderId);
        bo.setAppid(WexinConstant.APP_ID);
        StateBO stateBO = stateService.findStateByEnameAndStateId("weixin_body",1);
        bo.setBody(stateBO.getStateName());
        bo.setMch_id(WexinConstant.MCH_ID);
        bo.setNonce_str(WexinConstant.nonce_str);
        bo.setNotify_url(stateService.findStateByEnameAndStateId("weixin_notify_url",1).getStateName());
        bo.setTrade_type(WexinConstant.trade_type);
        bo.setSign(WeixinUtil.sign(WeixinUtil.obj2Map(bo)));
        String xmlResult = (String) weixinPayFeignClient.unionOrder(WeixinUtil.makeXml4UnionPrepay(bo));
        ReceiveXmlEntity entity = ReceiveXmlProcess.getMsgEntity(xmlResult);
        if(null == entity || FAIL.equalsIgnoreCase(entity.getReturnCode()) || FAIL.equalsIgnoreCase(entity.getResultCode())){
            throw new BizRuntimeException(ResultCode.ERROR,"call weixin union order failed");
        }
        WeixinPayVO vo = new WeixinPayVO();
        vo.setPrepayId("prepay_id="+entity.getPrepayId());
        Map<String,Object> map = Maps.newHashMap();
        map.put("appId","wx31a33d085b32ff73");
        map.put("timeStamp","1512697027");
        map.put("nonceStr","sawallianc");
        map.put("package",vo.getPrepayId());
        map.put("signType","MD5");
        vo.setSign(WeixinUtil.sign(map));
        orderService.makeOrder(bo,orderId);
        return vo;
    }


}
