package com.sawallianc.order.util;

import com.google.common.collect.Lists;
import com.sawallianc.common.DateUtil;
import com.sawallianc.common.OrderIdUtil;
import com.sawallianc.order.bo.OrderBO;
import com.sawallianc.order.bo.OrderDetailBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.module.OrderDetailDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

public final class OrderHelper {
    private OrderHelper(){

    }
    public static OrderDO doFromVo(OrderVO orderVO){
        if(null == orderVO){
            return null;
        }
        OrderDO orderDO = new OrderDO();
        if(StringUtils.isBlank(orderVO.getOrderId())){
            orderDO.setOrderId(OrderIdUtil.getOrderId());
        } else {
            orderDO.setOrderId(orderVO.getOrderId());
        }
        orderDO.setBenefitPrice(orderVO.getBenefitPrice());
        orderDO.setGoodsSettlePrice(orderVO.getGoodsSettlePrice());
        orderDO.setGoodsTotalPrice(orderVO.getGoodsTotalPrice());
        orderDO.setRackUUID(orderVO.getRackUUID());
        orderDO.setPhone(orderVO.getPhone());
        orderDO.setOpenid(orderVO.getOpenid());
        orderDO.setAlipayid(orderVO.getAlipayid());
        if(null != orderVO.getRandomBenefitPrice()){
            orderDO.setRandomBenefitPrice(orderVO.getRandomBenefitPrice());
        } else {
            orderDO.setRandomBenefitPrice(0.00);
        }
        return orderDO;
    }

    public static OrderDetailDO detailDOFromBO(OrderDetailBO bo,OrderDO orderDO){
        if(null == bo){
            return null;
        }
        OrderDetailDO detail = new OrderDetailDO();
        detail.setOrderId(orderDO.getId());
        detail.setGoodsId(Long.parseLong(bo.getGoodsId()));
        detail.setNumber(Integer.parseInt(bo.getNumber()));
        detail.setPrice(Double.parseDouble(bo.getPrice()));
        detail.setPhone(orderDO.getPhone());
        detail.setOpenid(orderDO.getOpenid());
        detail.setAlipayid(orderDO.getAlipayid());
        detail.setRackUuid(orderDO.getRackUUID());
        return detail;
    }

    public static OrderDetailBO detailBOFromDO(OrderDetailDO orderDetailDO){
        OrderDetailBO bo = new OrderDetailBO();
        if(null == orderDetailDO){
            return bo;
        }
        bo.setGoodsId(orderDetailDO.getGoodsId()+"");
        bo.setNumber(orderDetailDO.getNumber()+"");
        bo.setOrderId(orderDetailDO.getOrderId()+"");
        bo.setPhone(orderDetailDO.getPhone());
        bo.setOpenid(orderDetailDO.getOpenid());
        bo.setAlipayid(orderDetailDO.getAlipayid());
        bo.setPrice(orderDetailDO.getPrice()+"");
        bo.setRackUuid(orderDetailDO.getRackUuid());
        bo.setGoodsName(orderDetailDO.getGoodsName());
        return bo;
    }

    public static List<OrderDetailBO> detailBOSFromDOS(List<OrderDetailDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderDetailBO> result = Lists.newArrayListWithExpectedSize(list.size());
        for(OrderDetailDO orderDetailDO : list){
            result.add(detailBOFromDO(orderDetailDO));
        }
        return result;
    }

    public static List<OrderDetailDO> detailDOSFromBOS(List<OrderDetailBO> list,OrderDO orderDO){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderDetailDO> result = Lists.newArrayListWithCapacity(list.size());
        for(OrderDetailBO bo : list){
            result.add(detailDOFromBO(bo,orderDO));
        }
        return result;
    }

    public static OrderBO boFromDo(OrderDO orderDO){
        if(null == orderDO){
            return null;
        }
        OrderBO bo = new OrderBO();
        bo.setBenefitPrice(String.valueOf(orderDO.getBenefitPrice()));
        bo.setGoodsSettlePrice(String.valueOf(orderDO.getGoodsSettlePrice()));
        bo.setGoodsTotalPrice(String.valueOf(orderDO.getGoodsTotalPrice()));
        Double random = orderDO.getRandomBenefitPrice();
        random = random == null ? 0.00 : random;
        bo.setRandomBenefitPrice(String.valueOf(random));
        bo.setId(orderDO.getId());
        bo.setWeixinOrderId(orderDO.getWeixinOrderId());
        bo.setAlipayOrderId(orderDO.getAlipayOrderId());
        boolean weixin = StringUtils.isBlank(bo.getWeixinOrderId()) || "null".equalsIgnoreCase(bo.getWeixinOrderId().trim());
        boolean alipay = StringUtils.isBlank(bo.getAlipayOrderId()) || "null".equalsIgnoreCase(bo.getAlipayOrderId().trim());
        if(weixin && alipay){
            bo.setIsBalancePurchased("1");
        } else {
            bo.setIsBalancePurchased("0");
        }
        bo.setPhone(orderDO.getPhone());
        bo.setOpenid(orderDO.getOpenid());
        bo.setAlipayid(orderDO.getAlipayid());
        bo.setRackUUID(orderDO.getRackUUID());
        bo.setOrderId(orderDO.getOrderId());
        bo.setGmtCreated(DateUtil.date2Str(orderDO.getGmtCreated()));
        return bo;
    }

    public static List<OrderBO> bosFromDos(List<OrderDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<OrderBO> result = Lists.newArrayListWithCapacity(list.size());
        for(OrderDO orderDO : list){
            result.add(boFromDo(orderDO));
        }
        return result;
    }

}
