package com.sawallianc.rack.dao;

import com.sawallianc.rack.module.RackDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RackDAO {
    List<RackDO> findGoodsByRackUUId(@Param("uuid")String uuid);
}
