package com.sawallianc.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderDetailBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.dao.OrderDAO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.order.util.OrderHelper;
import com.sawallianc.order.vo.OrderDetailVO;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.module.WithholdRecordInfo;
import com.sawallianc.user.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDAO orderDAO;

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Resource
    private RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBO makeOrder(OrderVO orderVO,Integer orderState) {
        if(null == orderVO){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"orderVO is null while insert order");
        }
        if(null == orderVO.getBenefitPrice() || null == orderVO.getGoodsSettlePrice() || null == orderVO.getGoodsTotalPrice()){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"orderVO price is null while insert order");
        }
        String json = orderVO.getJson();
        if(StringUtils.isBlank(json)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"order detail is blank while insert order");
        }
        OrderDO orderDO = OrderHelper.doFromVo(orderVO);
        orderDO.setOrderState(orderState);
        Date now = new Date();
        orderDO.setGmtCreated(now);
        if(1 == orderState){
            orderDO.setGmtModified(now);
            WithholdRecordInfo withholdRecordInfo = new WithholdRecordInfo();
            withholdRecordInfo.setPhone(orderDO.getPhone());
            withholdRecordInfo.setBeforeBalance((String) redisTemplate.opsForValue().get("beforeBalance"));
            withholdRecordInfo.setAfterBalance((String) redisTemplate.opsForValue().get("afterBalance"));
            withholdRecordInfo.setOrderId(orderDO.getOrderId());
            withholdRecordInfo.setPayTime(now);
            userService.insertWithholdRecord(withholdRecordInfo);
        }
        orderDAO.makeOrder(orderDO);
        List<OrderDetailBO> details = JSONArray.parseArray(json, OrderDetailBO.class);
        if(CollectionUtils.isEmpty(details)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"order detail list is empty after parse json");
        }
        orderDAO.makeOrderDetail(OrderHelper.detailDOSFromBOS(details,orderDO));
        OrderBO result = OrderHelper.boFromDo(orderDO);
        result.setDetails(details);
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,
                returnNotBlank(orderVO.getOpenid(),orderVO.getAlipayid(),orderVO.getPhone()));
        redisValueOperations.delete(key);
        return result;
    }

    private String returnNotBlank(String openid,String alipayid,String phone){
        if(StringUtils.isNotBlank(phone)){
            return phone;
        }
        if(StringUtils.isNotBlank(openid)){
            return openid;
        }
        if(StringUtils.isNotBlank(alipayid)){
            return alipayid;
        }
        throw new BizRuntimeException(ResultCode.ERROR,"openid phone alipayid can't both be blank");
    }

    @Override
    public List<OrderBO> queryOrderInfoByPhone(String phone) {
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter phone is blank while query order info");
        }
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,phone);
        List<OrderBO> orderList = redisValueOperations.getArray(key,OrderBO.class);
        if(CollectionUtils.isNotEmpty(orderList)){
            return orderList;
        }
        orderList = OrderHelper.bosFromDos(orderDAO.queryOrderInfoByPhone(phone));
        if(CollectionUtils.isEmpty(orderList)){
            return Lists.newArrayList();
        }
        List<OrderDetailDO> details = orderDAO.queryOrderDetailInfoByPhone(phone);
        generateOrderDetail(orderList,details);
        redisValueOperations.set(key,orderList,3600L);
        return orderList;
    }

    @Override
    public List<OrderBO> queryOrderInfoByOpenid(String openid) {
        if(StringUtils.isBlank(openid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter openid is blank while query order info");
        }
        UserBO user = userService.queryUserInfoByOpenid(openid);
        if(null != user && StringUtils.isNotBlank(user.getPhone())){
            return this.queryOrderInfoByPhone(user.getPhone());
        }
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,openid);
        List<OrderBO> orderList = redisValueOperations.getArray(key,OrderBO.class);
        if(CollectionUtils.isNotEmpty(orderList)){
            return orderList;
        }
        orderList = OrderHelper.bosFromDos(orderDAO.queryOrderInfoByOpenid(openid));
        if(CollectionUtils.isEmpty(orderList)){
            return Lists.newArrayList();
        }
        List<OrderDetailDO> details = orderDAO.queryOrderDetailInfoByOpenid(openid);
        generateOrderDetail(orderList,details);
        redisValueOperations.set(key,orderList,3600L);
        return orderList;
    }

    @Override
    public List<OrderBO> queryOrderInfoByAlipayId(String alipayId) {
        if(StringUtils.isBlank(alipayId)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter alipayId is blank while query order info");
        }
        UserBO user = userService.queryUserInfoByAlipayId(alipayId);
        if(null != user && StringUtils.isNotBlank(user.getPhone())){
            return this.queryOrderInfoByPhone(user.getPhone());
        }
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,alipayId);
        List<OrderBO> orderList = redisValueOperations.getArray(key,OrderBO.class);
        if(CollectionUtils.isNotEmpty(orderList)){
            return orderList;
        }
        orderList = OrderHelper.bosFromDos(orderDAO.queryOrderInfoByAlipayId(alipayId));
        if(CollectionUtils.isEmpty(orderList)){
            return Lists.newArrayList();
        }
        List<OrderDetailDO> details = orderDAO.queryOrderDetailInfoByAlipayId(alipayId);
        generateOrderDetail(orderList,details);
        redisValueOperations.set(key,orderList,3600L);
        return orderList;
    }

    private void generateOrderDetail(List<OrderBO> orderList,List<OrderDetailDO> details){
        for(OrderBO bo : orderList){
            List<OrderDetailDO> inner = Lists.newArrayList();
            int number = 0;
            for(OrderDetailDO detail : details){
                if(bo.getId().equals(detail.getOrderId())){
                    inner.add(detail);
                    number += detail.getNumber();
                    bo.setFirstGoodsPic(goodsService.getGoodsById(detail.getGoodsId()).getGoodsUri());
                }
            }
            bo.setOrderNum(number);
            bo.setDetails(OrderHelper.detailBOSFromDOS(inner));
        }
    }

    @Override
    public OrderBO getOrderByOrderNo(String orderNo) {
        return OrderHelper.boFromDo(orderDAO.getOrderByOrderNo(orderNo));
    }

    @Override
    public int updateOrderState2SucceedWeixin(String orderId,String weixinOrderId,String value) {
        int result = orderDAO.updateOrderState2SucceedWeixin(orderId,weixinOrderId);
        if(StringUtils.isBlank(value)){
            this.batchDeleteCache();
            return result;
        }
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,value);
        redisValueOperations.delete(key);
        return result;
    }

    private void batchDeleteCache(){
        List<String> keys = redisValueOperations.keys(Constant.ORDER_LIST_INFO+"*");
        for(String value : keys){
            redisValueOperations.delete(value);
        }
    }

    @Override
    public int updateOrderState2SucceedAlipay(String orderId,String alipayOrderId,String value) {
        int result =  orderDAO.updateOrderState2SucceedAlipay(orderId,alipayOrderId);
        if(StringUtils.isBlank(value)){
            this.batchDeleteCache();
            return result;
        }
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,value);
        redisValueOperations.delete(key);
        return result;
    }

    @Override
    public Integer queryIfDealtWeixin(String weixin) {
        return orderDAO.queryIfDealtWeixin(weixin);
    }

    @Override
    public Integer queryIfDealtAlipay(String alipay) {
        return orderDAO.queryIfDealtAlipay(alipay);
    }

    @Override
    public Integer queryIfNotifyAlipay(String notifyId) {
        return orderDAO.queryIfNotifyAlipay(notifyId);
    }

    @Override
    public int insertAlipayNotify(String notifyId) {
        return orderDAO.insertAlipayNotify(notifyId);
    }

    @Override
    public List<OrderDetailDO> queryOrderDetailByOrderId(String orderId) {
        return orderDAO.queryOrderDetailInfoByOrderId(orderId);
    }

    @Override
    public OrderDetailVO queryOrderDetailWithOrderId(String orderId) {
        List<OrderDetailDO> list = orderDAO.queryOrderDetailWithOrderId(orderId);
        OrderDetailVO vo = new OrderDetailVO();
        vo.setDetails(list);
        BeanUtils.copyProperties(list.get(0),vo);
        return vo;
    }
}
