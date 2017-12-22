package com.sawallianc.goods.service;

import com.sawallianc.goods.bo.GoodsVO;

import java.util.List;

public interface GoodsService {
    /**
     * 返回货架商品的json格式数据
     * @param uuid
     * @return
     */
    List<GoodsVO> findGoodsByRackUUId(String uuid);

}
