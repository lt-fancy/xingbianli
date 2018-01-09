package com.sawallianc.goods.service.impl;

import com.google.common.collect.Lists;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.dao.GoodsDAO;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.goods.util.GoodsHelper;
import com.sawallianc.redis.operations.RedisValueOperations;
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
    private RedisValueOperations redisValueOperations;

    @Override
    public List<GoodsVO> findGoodsByRackUUId(String uuid) {
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter uuid is blank");
        }
        String key = CacheUtil.generateCacheKey(Constant.ALL_GOODS_RACK_UUID, uuid);
        List<GoodsVO> result = redisValueOperations.getArray(key,GoodsVO.class);
        if(CollectionUtils.isNotEmpty(result)){
            return result;
        }
        result = Lists.newArrayList();
        List<GoodsBO> list = goodsHelper.bosFromDos(goodsDAO.findGoodsByRackUUId(uuid));
        if(CollectionUtils.isEmpty(list)){
            throw new BizRuntimeException(ResultCode.RACK_HAS_BEEN_DOWN,"rack has been down");
        }
        List<StateBO> states = stateService.findChildrenStateByEname(Constant.GOODS_CATEGORY_ENAME);
        if(CollectionUtils.isEmpty(states)){
            throw new BizRuntimeException(ResultCode.ERROR,"goods category is not configured");
        }
        List<GoodsBO> allGoods = Lists.newArrayListWithExpectedSize(100);
        int i = 0;
        for(StateBO stateBO : states) {
            List<GoodsBO> innerList = Lists.newArrayListWithExpectedSize(20);
            for (GoodsBO goodsBO : list) {
                if (stateBO.getStateId().equals(Integer.parseInt(goodsBO.getGoodsCategory()))) {
                    innerList.add(goodsBO);
                }
            }
            GoodsVO vo = new GoodsVO();
            vo.setName(stateBO.getStateName());
            vo.setType(String.valueOf(++i));
            vo.setGoods(innerList);
            result.add(vo);
        }
        for(GoodsVO vo : result){
            allGoods.addAll(vo.getGoods());
        }
        GoodsVO vo = new GoodsVO();
        vo.setName("全部商品");
        vo.setGoods(allGoods);
        result.add(vo);
        redisValueOperations.set(key,result);
        return result;
    }

    @Override
    public List<GoodsVO> queryGoodsByGoodsName(String uuid, String goodsName) {
        List<GoodsVO> list = this.findGoodsByRackUUId(uuid);
        if(StringUtils.isBlank(goodsName)){
            return list;
        }
        List<GoodsVO> result = Lists.newArrayListWithExpectedSize(list.size());
        for(GoodsVO vo : list){
            List<GoodsBO> goodsBOS = vo.getGoods();
            List<GoodsBO> innerList = Lists.newArrayListWithExpectedSize(goodsBOS.size());
            for(GoodsBO bo : goodsBOS){
                if(bo.getGoodsName().contains(goodsName)){
                    innerList.add(bo);
                }
            }
            vo.setGoods(innerList);
            result.add(vo);
        }
        return result;
    }

}
