package com.sawallianc.order.service.impl;

import com.sawallianc.order.bo.OrderVO;
import com.sawallianc.order.dao.OrderDAO;
import com.sawallianc.order.service.OrderService;
import com.sawallianc.order.util.OrderHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderDAO orderDAO;
    @Override
    public void makeOrder(OrderVO orderVO) {
        orderDAO.makeOrder(OrderHelper.doFromVo(orderVO));
    }
}
