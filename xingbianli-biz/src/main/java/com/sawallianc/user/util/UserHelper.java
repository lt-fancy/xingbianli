package com.sawallianc.user.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.module.UserDO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserHelper {
    private UserHelper(){

    }
    public static UserDO doFromBo(UserBO bo){
        if(null == bo){
            return null;
        }
        UserDO userDO = new UserDO();
        userDO.setAlipayId(bo.getAlipayId());
        if(StringUtils.isNotBlank(bo.getBalance())){
            userDO.setBalance(Double.parseDouble(bo.getBalance()));
        }
        userDO.setOpenid(bo.getOpenid());
        userDO.setPhone(bo.getPhone());
        userDO.setUnionid(bo.getUnionid());
        return userDO;
    }

    public static UserBO boFromDo(UserDO userDO){
        if(null == userDO){
            return null;
        }
        UserBO bo = new UserBO();
        bo.setAlipayId(userDO.getAlipayId());
        bo.setBalance(String.valueOf(userDO.getBalance()));
        bo.setOpenid(userDO.getOpenid());
        bo.setPhone(userDO.getPhone());
        bo.setUnionid(userDO.getUnionid());
        return bo;
    }

    public static double randomDiscount(List<StateBO> list,double price){
        if(CollectionUtils.isEmpty(list)){
            return price;
        }
        double random = Math.random();
        Map<Double,Double> map = Maps.newHashMapWithExpectedSize(list.size());
        List<Double> chances = Lists.newArrayListWithCapacity(list.size());
        for(StateBO bo : list){
            String value = bo.getStateName();
            if(StringUtils.isBlank(value)){
                continue;
            }
            double discount = Double.parseDouble(value.split(",")[0]);
            double chance = Double.parseDouble(value.split(",")[1]);
            if(0D != discount){
                map.put(chance,discount);
                chances.add(chance);
            }
        }
        Collections.sort(chances);
        System.out.println("概率："+chances);
        for(int i =0;i<chances.size()-1;i++){
            double left = chances.get(i);
            double right = chances.get(i + 1);
            if(random < left){
                return new BigDecimal(price * map.get(left)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            if(random > left && random < right){
                return new BigDecimal(price * map.get(right)).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            }
        }
        return price;
    }
}