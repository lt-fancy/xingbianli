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
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderBO makeOrder(OrderVO orderVO,Integer orderState) {
        if(null == orderVO){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"orderVO is null while insert order");
        }
        if(null == orderVO.getBenefitPrice() || null == orderVO.getGoodsSettlePrice() || null == orderVO.getGoodsTotalPrice()){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"orderVO price is null while insert order");
        }
        if(StringUtils.isBlank(orderVO.getPhone()) || StringUtils.isBlank(orderVO.getRackUUID())){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"phone or rackUUid is blank while insert order");
        }
        String json = orderVO.getJson();
        if(StringUtils.isBlank(json)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"order detail is blank while insert order");
        }
        OrderDO orderDO = OrderHelper.doFromVo(orderVO);
        orderDO.setOrderState(orderState);
        orderDAO.makeOrder(orderDO);
        orderDO.setGmtCreated(new Date());
        List<OrderDetailBO> details = JSONArray.parseArray(json, OrderDetailBO.class);
        if(CollectionUtils.isEmpty(details)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"order detail list is empty after parse json");
        }
        orderDAO.makeOrderDetail(OrderHelper.detailDOSFromBOS(details,orderDO));
        String key = CacheUtil.generateCacheKey(Constant.ORDER_LIST_INFO,orderVO.getPhone());
        redisValueOperations.delete(key);
        OrderBO result = OrderHelper.boFromDo(orderDO);
        result.setDetails(details);
        return result;
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
        orderList = OrderHelper.bosFromDos(orderDAO.queryOrderInfo(phone));
        if(CollectionUtils.isEmpty(orderList)){
            return Lists.newArrayList();
        }
        List<OrderDetailDO> details = orderDAO.queryOrderDetailInfoByPhone(phone);
        for(OrderBO bo : orderList){
            List<OrderDetailDO> inner = Lists.newArrayList();
            for(OrderDetailDO detail : details){
                if(bo.getId().equals(detail.getOrderId())){
                    inner.add(detail);
                    bo.setFirstGoodsPic(goodsService.getGoodsById(detail.getGoodsId()).getGoodsUri());
                }
            }
            bo.setDetails(OrderHelper.detailBOSFromDOS(inner));
        }

        redisValueOperations.set(key,orderList,3600L);
        return orderList;
    }

    @Override
    public List<OrderBO> queryOrderInfoByOpenid(String openid) {
        if(StringUtils.isBlank(openid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter openid is blank while querying order info");
        }
        UserBO user = userService.queryUserInfoByOpenid(openid);
        if(null == user){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter openid can't find correct user info");
        }
        String phone = user.getPhone();
        if(StringUtils.isBlank(phone)){
            throw new BizRuntimeException(ResultCode.ERROR,"request parameter openid find user info does not have phone");
        }
        return this.queryOrderInfoByPhone(phone);
    }

    @Override
    public int updateOrderState2Succeed(String orderId,String weixinOrderId) {
        return orderDAO.updateOrderState2Succeed(orderId,weixinOrderId);
    }

    @Override
    public Integer queryIfDealtWeixin(String weixin) {
        return orderDAO.queryIfDealtWeixin(weixin);
    }

    @Override
    public List<OrderDetailDO> queryOrderDetailByOrderId(String orderId) {
        return orderDAO.queryOrderDetailInfoByOrderId(orderId);
    }
}
