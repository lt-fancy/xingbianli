package com.sawallianc.user.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.order.vo.DiscountVO;
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
        userDO.setWeixinPic(bo.getWeixinPic());
        userDO.setAlipayPic(bo.getAlipayPic());
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
        bo.setWeixinPic(userDO.getWeixinPic());
        bo.setAlipayPic(userDO.getAlipayPic());
        return bo;
    }

    public static DiscountVO randomDiscount(List<StateBO> list, double price){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        double random = Math.random();
        System.out.println("随机小数："+random);
        Map<Double,Double> map = Maps.newHashMapWithExpectedSize(list.size());
        List<Double> chances = Lists.newArrayListWithCapacity(list.size());
        double bonusChance = 0.01D;
        for(StateBO bo : list){
            String value = bo.getStateName();
            if(StringUtils.isBlank(value)){
                continue;
            }
            double discount = Double.parseDouble(value.split(",")[0]);
            double chance = Double.parseDouble(value.split(",")[1]);
            if(0.01D != discount){
                map.put(chance,discount);
                chances.add(chance);
            } else {
                bonusChance = chance;
            }
        }
        if(random <= bonusChance){
            DiscountVO vo = new DiscountVO();
            vo.setDiscount(keep2Decimal(price-0.01));
            vo.setSettlePrice(0.01);
            return vo;
        }
        Collections.sort(chances);
        DiscountVO vo = new DiscountVO();
        for(int i =0;i<chances.size()-1;i++){
            double left = chances.get(i);
            double right = chances.get(i + 1);
            if(random <= left){
                vo.setChance(map.get(left));
                vo.setSettlePrice(keep2Decimal(price * map.get(left)));
                vo.setDiscount(keep2Decimal(price-vo.getSettlePrice()));
                return vo;
            }
            if(random > left && random <= right){
                vo.setChance(map.get(right));
                vo.setSettlePrice(keep2Decimal(price * map.get(right)));
                vo.setDiscount(keep2Decimal(price-vo.getSettlePrice()));
                return vo;
            }
        }
        vo.setDiscount(0);
        vo.setSettlePrice(price);
        return vo;
    }

    public static double keep2Decimal(double source){
        return new BigDecimal(source).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }


}