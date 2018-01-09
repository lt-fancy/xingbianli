package com.sawallianc.state.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.dao.StateDAO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.state.util.StateHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class StateServiceImpl implements StateService{

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Autowired
    private StateDAO stateDAO;
    @Override
    public StateBO findStateByEnameAndStateId(String ename, Integer stateId) {
        String key = CacheUtil.generateCacheKey(Constant.STATE_SINGLE_INFO,ename,stateId);
        StateBO bo = JSONObject.parseObject(redisValueOperations.get(key),StateBO.class);
        if(null != bo){
            return bo;
        }

        bo = StateHelper.boFromDo(stateDAO.findStateByEnameAndStateId(ename,stateId));
        redisValueOperations.set(key,bo);
        return bo;
    }

    @Override
    public List<StateBO> findChildrenStateByEname(String ename) {
        String key = CacheUtil.generateCacheKey(Constant.STATE_LIST_INFO,ename);
        List<StateBO> result = JSONArray.parseArray(redisValueOperations.get(key),StateBO.class);
        if(CollectionUtils.isNotEmpty(result)){
            return result;
        }
        result = StateHelper.bosFromDos(stateDAO.findChildrenStateByEname(ename));
        redisValueOperations.set(key,result);
        return result;
    }
}
