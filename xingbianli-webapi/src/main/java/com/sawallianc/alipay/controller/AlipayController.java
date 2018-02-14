package com.sawallianc.alipay.controller;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.google.common.collect.Maps;
import com.sawallianc.alipay.biz.AlipayService;
import com.sawallianc.alipay.bo.AlipayBO;
import com.sawallianc.alipay.bo.AlipayNotifyBO;
import com.sawallianc.alipay.config.AlipayConfig;
import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@RestController
@RequestMapping(value = "/alipay")
public class AlipayController extends WebApiAdvice{

    @Autowired
    private AlipayService alipayService;

    @GetMapping("/getUserInfo")
    public Result getUserInfo(String code){
        return Result.getSuccessResult(alipayService.getUserInfo(code));
    }

    @GetMapping("/toPay")
    public String toPay(AlipayBO alipayBO, HttpServletResponse httpResponse){
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",
                AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, "json", AlipayConfig.CHARSET,
                AlipayConfig.ALIPAY_PUBLIC_KEY, "RSA2"); //获得初始化的AlipayClient
        AlipayTradeWapPayRequest alipayRequest = new AlipayTradeWapPayRequest();//创建API对应的request
        alipayRequest.setReturnUrl("http://domain.com/CallBack/return_url.jsp");
        alipayRequest.setNotifyUrl("https://h5.ljlhz.com/api/alipay/alipayNotifyAfterPay");//在公共参数中设置回跳和通知地址
        Map<String,Object> map = Maps.newHashMapWithExpectedSize(10);
        map.put("out_trade_no", OrderIdUtil.getOrderId());
        map.put("total_amount",alipayBO.getTotal_amount());
        map.put("subject",alipayBO.getSubject());
        map.put("product_code",alipayBO.getProduct_code());
        alipayRequest.setBizContent(JSONObject.toJSONString(map));//填充业务参数
        String form="";
        try {
            form = alipayClient.pageExecute(alipayRequest).getBody(); //调用SDK生成表单
            return form;
        } catch (AlipayApiException e) {
            throw new BizRuntimeException(ResultCode.ALIPAY_PAY_FAILURE,"调用支付宝支付失败："+e);
        }
//        httpResponse.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
//        PrintWriter pw = null;
//        try {
//            pw = httpResponse.getWriter();
//            httpResponse.getWriter().write(form);//直接将完整的表单html输出到页面
//        } catch (IOException e) {
//            throw new BizRuntimeException(ResultCode.ALIPAY_PAY_FAILURE,"调用支付宝支付失败："+e);
//        } finally {
//            if(null != pw){
//                pw.flush();
//                pw.close();
//            }
//        }
    }

    @PostMapping("/preOrder")
    public Result preOrder(@RequestBody AlipayBO alipayBO){
        return Result.getSuccessResult(alipayService.preOrder(alipayBO));
    }

    @PostMapping("/alipayNotifyAfterPay")
    public String alipayNotifyAfterPay(AlipayNotifyBO alipayNotifyBO){
        return alipayService.alipayNotifyAfterPay(alipayNotifyBO);
    }
    @PostMapping("/alipayNotifyAfterCharge")
    public String alipayNotifyAfterCharge(AlipayNotifyBO alipayNotifyBO){
        return alipayService.alipayNotifyAfterCharge(alipayNotifyBO);
    }
}
