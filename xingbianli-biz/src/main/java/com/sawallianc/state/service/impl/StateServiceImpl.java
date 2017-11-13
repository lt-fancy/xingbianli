package com.sawallianc.state.service.impl;

import com.sawallianc.annotation.Cacheable;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.dao.StateDAO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.state.util.StateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateServiceImpl implements StateService{

    @Autowired
    private StateDAO stateDAO;
    @Override
    @Cacheable
    public StateBO findStateByEnameAndStateId(String ename, Integer stateId) {
        return StateHelper.boFromDo(stateDAO.findStateByEnameAndStateId(ename,stateId));
    }

    @Override
    @Cacheable
    public List<StateBO> findChildrenStateByEname(String ename) {
        return StateHelper.bosFromDos(stateDAO.findChildrenStateByEname(ename));
    }
}
