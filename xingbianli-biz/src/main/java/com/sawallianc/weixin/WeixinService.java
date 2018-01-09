package com.sawallianc.weixin;

import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.vo.WeixinPayVO;

public interface WeixinService {
    String getAccessToken();

    String getTicket();

    String getOpenid(String code);

    String getSignature(String url,String timestamp,String nonceStr);

    WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo);

    String notifyAfterPay(String xml);

    String notifyAfterCharge(String xml);

}
