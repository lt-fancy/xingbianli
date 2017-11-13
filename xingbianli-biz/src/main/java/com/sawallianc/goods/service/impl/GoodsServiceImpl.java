package com.sawallianc.goods.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.annotation.Cacheable;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.dao.GoodsDAO;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.goods.util.GoodsHelper;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsHelper goodsHelper;

    @Autowired
    private StateService stateService;

    @Override
    @Cacheable
    public Map<String,Object> findGoodsByRackUUId(String uuid) {
        List<GoodsBO> list = goodsHelper.bosFromDos(goodsDAO.findGoodsByRackUUId(uuid));
        List<StateBO> states = stateService.findChildrenStateByEname(Constant.GOODS_CATEGORY_ENAME);
        if(CollectionUtils.isEmpty(states)){
            throw new BizRuntimeException(ResultCode.ERROR,"goods category is not configured");
        }

        Map<String,Object> result = Maps.newHashMapWithExpectedSize(states.size());
        for(StateBO stateBO : states) {
            List<GoodsBO> innerList = Lists.newArrayListWithExpectedSize(20);
            for (GoodsBO goodsBO : list) {
                if (stateBO.getStateId().equals(Integer.parseInt(goodsBO.getGoodsCategory()))) {
                    innerList.add(goodsBO);
                }
            }
            result.put(stateBO.getStateName(),innerList);
        }
        return result;
    }

}
