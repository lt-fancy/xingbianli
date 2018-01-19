package com.sawallianc.rack.dao;

import com.sawallianc.rack.module.RackApplyDO;
import com.sawallianc.rack.module.RackDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RackDAO {
    List<RackDO> findAllRack();

    List<RackDO> findAllAvaliableRack();

    List<RackDO> findAllDisableRack();

    RackDO getRackByUUID(@Param("uuid")String uuid);

    int apply(RackApplyDO bo);

    List<String> getRackApplyCity();
}
