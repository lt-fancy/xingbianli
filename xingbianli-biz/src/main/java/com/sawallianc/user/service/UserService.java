package com.sawallianc.user.service;

import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.module.ChargeRecordInfo;
import com.sawallianc.user.module.WithholdRecordInfo;
import com.sawallianc.user.vo.BalanceVO;

import java.util.List;

public interface UserService {
    /**
     * 用户余额充值
     * @param balanceVO
     * @return
     */
    boolean charge(BalanceVO balanceVO);

    /**
     * 直接用余额消费
     * @param balanceVO
     * @return
     */
    OrderBO purchase(BalanceVO balanceVO);

    /**
     * 根据手机号查找用户信息
     * @param phone
     * @return
     */
    UserBO queryUserInfoByPhone(String phone);

    /**
     * 只做扣款处理，目的是为和下单分开
     * @param phone
     * @param price
     * @return
     */
    boolean withhold(String phone, Double price);

    /**
     * 根据微信openid查找用户信息
     * @param openid
     * @return
     */
    UserBO queryUserInfoByOpenid(String openid);

    /**
     * 根据支付宝id查找用户信息
     * @param alipayId
     * @return
     */
    UserBO queryUserInfoByAlipayId(String alipayId);
    /**
     * 注册新增用户
     * @param userBO
     * @return
     */
    UserBO addUser(UserBO userBO);

    /**
     * 记录充值信息
     * @param chargeRecordInfo
     */
    void recordChargeInfo(ChargeRecordInfo chargeRecordInfo);

    /**
     * 发送验证码
     * @param phone
     * @param alipayId
     * @param openid
     * @return
     */
    String sendCheckCode(String phone,String openid,String alipayId);

    /**
     * 群发短信
     * @param id
     * @return
     */
    String batchSend(Integer id);

    boolean recordChargeSucceed(String recordId,int type);

    Integer queryIfRecordWeixinOrderId(String weixin);

    Integer queryIfRecordAlipayOrderId(String alipay);

    void insertWithholdRecord(WithholdRecordInfo withholdRecordInfo);
}
