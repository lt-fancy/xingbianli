package com.sawallianc.goods.service.impl;

import com.google.common.collect.Lists;
import com.sawallianc.annotation.Cacheable;
import com.sawallianc.common.Constant;
import com.sawallianc.common.GoodsCategoryEnums;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.dao.GoodsDAO;
import com.sawallianc.goods.service.GoodsCacheService;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.goods.util.GoodsHelper;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService{

    @Autowired
    private GoodsDAO goodsDAO;

    @Autowired
    private GoodsHelper goodsHelper;

    @Autowired
    private StateService stateService;

    @Autowired
    private GoodsCacheService goodsCacheService;

    @Override
    @Cacheable
    public List<GoodsVO> findGoodsByRackUUId(String uuid) {
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter uuid is blank");
        }
        List<GoodsBO> list = goodsCacheService.getAllGoods(uuid);
        List<StateBO> states = stateService.findChildrenStateByEname(Constant.GOODS_CATEGORY_ENAME);
        if(CollectionUtils.isEmpty(states)){
            throw new BizRuntimeException(ResultCode.ERROR,"goods category is not configured");
        }
        List<GoodsVO> result = Lists.newArrayListWithCapacity(states.size());
        for(StateBO stateBO : states) {
            List<GoodsBO> innerList = Lists.newArrayListWithExpectedSize(20);
            for (GoodsBO goodsBO : list) {
                if (stateBO.getStateId().equals(Integer.parseInt(goodsBO.getGoodsCategory()))) {
                    innerList.add(goodsBO);
                }
            }
            GoodsVO vo = new GoodsVO();
            vo.setName(stateBO.getStateName());
            vo.setGoods(innerList);
            result.add(vo);
        }
        return result;
    }

    @Override
    @Cacheable
    public GoodsVO getGoodsByCategory(String uuid, String type) {
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter uuid is blank");
        }
        if(StringUtils.isBlank(type)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter type is blank");
        }
        GoodsVO vo = new GoodsVO();
        String allGoods = GoodsCategoryEnums.ALL_GOODS.getType();
        String special = GoodsCategoryEnums.TODAY_SPECIAL_PRICE.getType();
        String often = GoodsCategoryEnums.MY_OFTEN_BUY.getType();
        if(allGoods.equals(type)){
            vo.setName(GoodsCategoryEnums.getName(allGoods));
            vo.setGoods(goodsCacheService.getAllGoods(uuid));
        } else if (special.equals(type)){
            vo.setName(GoodsCategoryEnums.getName(special));
            vo.setGoods(goodsCacheService.getTodaySpecialPrice(uuid));
        } else if (often.equals(type)){
            vo.setName(GoodsCategoryEnums.getName(often));
            vo.setGoods(goodsCacheService.getOftenBuy(uuid));
        } else {
            if(!StringUtils.isNumeric(type)){
                throw new BizRuntimeException(ResultCode.ERROR,"goods category type is not numeric");
            }
            vo.setName(stateService.findStateByEnameAndStateId(Constant.GOODS_CATEGORY_ENAME,Integer.parseInt(type)).getStateName());
            vo.setGoods(goodsHelper.bosFromDos(goodsDAO.getGoodsByCategory(uuid,type)));
        }
        return vo;
    }

}
