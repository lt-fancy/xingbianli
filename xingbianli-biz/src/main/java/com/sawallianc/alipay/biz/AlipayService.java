package com.sawallianc.alipay.biz;


import com.sawallianc.alipay.bo.AlipayBO;
import com.sawallianc.alipay.bo.AlipayNotifyBO;

public interface AlipayService {
    String getUserInfo(String code);

    AlipayBO preOrder(AlipayBO alipayBO);

    String alipayNotifyAfterPay(AlipayNotifyBO alipayNotifyBO);

    String alipayNotifyAfterCharge(AlipayNotifyBO alipayNotifyBO);
}
