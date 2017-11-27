package com.sawallianc.goods.service;

import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.bo.GoodsVO;

import java.util.List;

public interface GoodsService {
    /**
     * 返回货架商品的json格式数据
     * @param uuid
     * @return
     */
    List<GoodsVO> findGoodsByRackUUId(String uuid);
    /**
     * 点选左侧分类的时候返回商品
     * @param uuid
     * @param type
     * @return
     */
    GoodsVO getGoodsByCategory(String uuid, String type);
}
