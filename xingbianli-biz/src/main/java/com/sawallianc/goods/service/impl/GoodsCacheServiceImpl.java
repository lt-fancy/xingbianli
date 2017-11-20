package com.sawallianc.goods.service.impl;

import com.sawallianc.annotation.Cacheable;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.dao.GoodsDAO;
import com.sawallianc.goods.service.GoodsCacheService;
import com.sawallianc.goods.util.GoodsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsCacheServiceImpl implements GoodsCacheService{

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsHelper goodsHelper;

    @Override
    @Cacheable
    public List<GoodsBO> getOftenBuy(String uuid) {
        return null;
    }

    @Override
    @Cacheable
    public List<GoodsBO> getTodaySpecialPrice(String uuid) {
        return goodsHelper.bosFromDos(goodsDAO.getTodaySpecialPrice(uuid));
    }

    @Override
    @Cacheable
    public List<GoodsBO> getAllGoods(String uuid) {
        return goodsHelper.bosFromDos(goodsDAO.findGoodsByRackUUId(uuid));
    }
}
