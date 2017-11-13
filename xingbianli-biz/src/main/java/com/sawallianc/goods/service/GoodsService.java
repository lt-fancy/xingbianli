package com.sawallianc.goods.service;

import java.util.Map;

public interface GoodsService {
    /**
     * 返回货架商品的json格式数据
     * @param uuid
     * @return
     */
    Map<String,Object> findGoodsByRackUUId(String uuid);
}
