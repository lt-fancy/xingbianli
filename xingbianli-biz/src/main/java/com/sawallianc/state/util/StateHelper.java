package com.sawallianc.state.util;

import com.google.common.collect.Lists;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.module.StateDO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class StateHelper {

    public static StateBO boFromDo(StateDO stateDO){
        if(null == stateDO){
            return null;
        }
        StateBO stateBO = new StateBO();
        stateBO.setEname(stateDO.getEname());
        stateBO.setStateId(stateDO.getStateId());
        stateBO.setStateName(stateDO.getStateName());
        return stateBO;
    }

    public static List<StateBO> bosFromDos(List<StateDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<StateBO> result = Lists.newArrayListWithCapacity(list.size());
        for(StateDO stateDO : list){
            result.add(boFromDo(stateDO));
        }
        return result;
    }
}
