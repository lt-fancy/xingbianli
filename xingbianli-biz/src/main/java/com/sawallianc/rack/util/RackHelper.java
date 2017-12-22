package com.sawallianc.rack.util;

import com.google.common.collect.Lists;
import com.sawallianc.rack.bo.RackBO;
import com.sawallianc.rack.module.RackDO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.List;

public final class RackHelper {
    private RackHelper(){

    }
    public static RackBO boFromDo(RackDO rackDO){
        if(null == rackDO){
            return null;
        }
        RackBO bo = new RackBO();
        BeanUtils.copyProperties(rackDO,bo);
        bo.setBizManId(String.valueOf(rackDO.getBizmanId()));
        bo.setReplenishmanId(String.valueOf(rackDO.getReplenishmanId()));
        bo.setGmtCreated(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rackDO.getGmtCreated()));
        bo.setGmtModified(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(rackDO.getGmtModified()));
        return bo;
    }

    public static List<RackBO> bosFromDos(List<RackDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<RackBO> result = Lists.newArrayListWithCapacity(list.size());
        for(RackDO rackDO : list){
            result.add(boFromDo(rackDO));
        }
        return result;
    }
}
