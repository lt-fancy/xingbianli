package com.sawallianc.goods.service;

import com.sawallianc.goods.bo.GoodsBO;

import java.util.List;

public interface GoodsCacheService {

    /**
     * 我常购买的商品，从订单表获取
     * @return
     */
    List<GoodsBO> getOftenBuy(String uuid);

    /**
     * 获取今日特价商品
     * @return
     */
    List<GoodsBO> getTodaySpecialPrice(String uuid);

    /**
     * 获取全部商品
     * @return
     */
    List<GoodsBO> getAllGoods(String uuid);
}
