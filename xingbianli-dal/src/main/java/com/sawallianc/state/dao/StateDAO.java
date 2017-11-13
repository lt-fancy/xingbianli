package com.sawallianc.state.dao;

import com.sawallianc.state.module.StateDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StateDAO {

    StateDO findStateByEnameAndStateId(@Param("ename")String ename,@Param("stateId")Integer stateId);

    List<StateDO> findChildrenStateByEname(@Param("ename")String ename);
}
