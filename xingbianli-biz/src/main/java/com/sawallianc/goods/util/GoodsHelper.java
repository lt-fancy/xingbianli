package com.sawallianc.goods.util;

import com.google.common.collect.Lists;
import com.sawallianc.common.Constant;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.module.GoodsDO;
import com.sawallianc.state.service.StateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoodsHelper {

    @Autowired
    private StateService stateService;

    public GoodsBO boFromDo(GoodsDO goodsDO){
        if(null == goodsDO){
            return null;
        }
        GoodsBO bo = new GoodsBO();
        bo.setGoodsCategoryName(stateService.findStateByEnameAndStateId(Constant.GOODS_CATEGORY_ENAME,goodsDO.getGoodsCategory()).getStateName());
        bo.setGoodsCategory(String.valueOf(goodsDO.getGoodsCategory()));
        bo.setGoodsDiscount(String.valueOf(goodsDO.getGoodsDiscount()));
        bo.setGoodsNowPrice(String.valueOf(goodsDO.getGoodsNowPrice()));
        bo.setGoodsOldPrice(String.valueOf(goodsDO.getGoodsOldPrice()));
        bo.setGoodsName(goodsDO.getGoodsName());
        bo.setGoodsUri(goodsDO.getGoodsUri());
        String goodsState = goodsDO.getGoodsState();
        if(!StringUtils.isBlank(goodsState)){
            String[] states = goodsState.split(",");
            StringBuffer sb = new StringBuffer();
            for(String state : states){
                sb.append(stateService.findStateByEnameAndStateId(Constant.GOODS_STATE_ENAME,Integer.parseInt(state)).getStateName()+",");
            }
            String result = sb.toString();
            bo.setGoodsStateName(result.substring(0,result.length()-1));
            bo.setGoodsState(goodsDO.getGoodsState());
        }
        return bo;
    }

    public List<GoodsBO> bosFromDos(List<GoodsDO> list){
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        List<GoodsBO> result = Lists.newArrayListWithCapacity(list.size());
        for(GoodsDO goodsDO : list){
            result.add(this.boFromDo(goodsDO));
        }
        return result;
    }
}
