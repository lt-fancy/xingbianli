package com.sawallianc.goods.dao;

import com.sawallianc.goods.module.GoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDAO {
    List<GoodsDO> findGoodsByRackUUId(@Param("uuid")String uuid);

    List<GoodsDO> getGoodsByCategory(@Param("uuid")String uuid,@Param("category")String category);

    List<GoodsDO> getTodaySpecialPrice(@Param("uuid")String uuid);

    /**
     * 根据商品id查询商品详情
     * @param list
     * @return
     */
    List<GoodsDO> queryGoodsByGoodsId(@Param("list") List<Long> list);

    /**
     * 扫码得到EAN_13 CODE获取商品
     * @param eanCode
     * @param rackUUid
     * @return
     */
    GoodsDO queryGoodsByEanCode(@Param("eanCode")String eanCode,@Param("rackUUid")String rackUUid);

    /**
     * 通过id获取商品详情
     * @param id
     * @return
     */
    GoodsDO getGoodsById(@Param("id")Long id);
}
