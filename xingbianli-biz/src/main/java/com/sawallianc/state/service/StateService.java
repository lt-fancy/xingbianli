package com.sawallianc.state.service;

import com.sawallianc.state.bo.StateBO;

import java.util.List;

public interface StateService {
    StateBO findStateByEnameAndStateId(String ename,Integer stateId);

    List<StateBO> findChildrenStateByEname(String ename);

    List<StateBO> findChildrenStateByEnameWithAscOrder(String ename);

    List<StateBO> findChildrenStateByEnameWithDescOrder(String ename);
}
