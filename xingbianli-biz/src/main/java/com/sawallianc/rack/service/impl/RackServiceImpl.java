package com.sawallianc.rack.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.rack.bo.RackBO;
import com.sawallianc.rack.dao.RackDAO;
import com.sawallianc.rack.service.RackService;
import com.sawallianc.rack.util.RackHelper;
import com.sawallianc.redis.operations.RedisValueOperations;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class RackServiceImpl implements RackService {

    @Autowired
    private RackDAO rackDAO;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Override
    public List<RackBO> findAllRack() {
        String key = Constant.ALL_RACK;
        List<RackBO> list = redisValueOperations.getArray(key,RackBO.class);
        if(CollectionUtils.isNotEmpty(list)){
            return list;
        }
        list = RackHelper.bosFromDos(rackDAO.findAllRack());
        redisValueOperations.set(key,list,Constant.DAY_SECONDS * 7);
        return list;

    }

    @Override
    public List<RackBO> findAllAvaliableRack() {
        String key = Constant.ALL_AVAILABLE_RACK;
        List<RackBO> list = redisValueOperations.getArray(key,RackBO.class);
        if(CollectionUtils.isNotEmpty(list)){
            return list;
        }

        list = RackHelper.bosFromDos(rackDAO.findAllAvaliableRack());
        redisValueOperations.set(key,list,Constant.DAY_SECONDS * 7);
        return list;
    }

    @Override
    public List<RackBO> findAllDisableRack() {
        String key = Constant.ALL_DISABLE_RACK;
        List<RackBO> list = redisValueOperations.getArray(key,RackBO.class);
        if(CollectionUtils.isNotEmpty(list)){
            return list;
        }

        list = RackHelper.bosFromDos(rackDAO.findAllDisableRack());
        redisValueOperations.set(key,list,Constant.DAY_SECONDS * 7);
        return list;
    }

    @Override
    public RackBO getRackByUUID(String uuid) {
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"uuid is null while querying rack by uuid");
        }
        String key = MessageFormat.format(Constant.SINGLE_RACK,uuid);
        RackBO bo = JSONObject.parseObject(redisValueOperations.get(key),RackBO.class);
        if(null != bo){
            return bo;
        }
        bo = RackHelper.boFromDo(rackDAO.getRackByUUID(uuid));
        redisValueOperations.set(key,bo,Constant.DAY_SECONDS * 7);
        return bo;
    }
}
