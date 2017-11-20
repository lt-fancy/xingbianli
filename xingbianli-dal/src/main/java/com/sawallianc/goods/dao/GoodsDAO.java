package com.sawallianc.goods.dao;

import com.sawallianc.goods.module.GoodsDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface GoodsDAO {
    List<GoodsDO> findGoodsByRackUUId(@Param("uuid")String uuid);

    List<GoodsDO> getGoodsByCategory(@Param("uuid")String uuid,@Param("category")String category);

    List<GoodsDO> getTodaySpecialPrice(@Param("uuid")String uuid);
}
