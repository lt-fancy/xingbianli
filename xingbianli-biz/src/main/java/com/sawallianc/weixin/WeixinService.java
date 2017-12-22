package com.sawallianc.weixin;

import com.sawallianc.weixin.bo.WeixinUnionOrderBO;
import com.sawallianc.weixin.vo.WeixinPayVO;

public interface WeixinService {
    String getAccessToken();

    String getTicket();

    String getSignature(String url,String timestamp);

    WeixinPayVO getWeixinPayConfig(WeixinUnionOrderBO bo);

    String notifyAfterPay(String xml);

}
