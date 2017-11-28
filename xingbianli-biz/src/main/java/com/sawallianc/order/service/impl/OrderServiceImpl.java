package com.sawallianc.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.order.bo.OrderDetailBO;
import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.dao.OrderDAO;
import com.sawallianc.order.module.OrderDO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.order.util.OrderHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDAO orderDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void makeOrder(OrderVO orderVO) {
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
        orderDAO.makeOrder(orderDO);
        List<OrderDetailBO> details = JSON.parseArray(json, OrderDetailBO.class);
        if(CollectionUtils.isEmpty(details)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"order detail list is empty after parse json");
        }
        orderDAO.makeOrderDetail(OrderHelper.detailDOSFromBOS(details,orderDO.getId()));
    }
}
