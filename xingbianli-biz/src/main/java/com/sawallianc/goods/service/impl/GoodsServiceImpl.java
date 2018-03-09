package com.sawallianc.goods.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sawallianc.common.CacheUtil;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.bo.ToPayGoodsBO;
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
import java.util.Map;

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
        List<StateBO> states = stateService.findChildrenStateByEnameWithAscOrder(Constant.GOODS_CATEGORY_ENAME);
        if(CollectionUtils.isEmpty(states)){
            throw new BizRuntimeException(ResultCode.ERROR,"goods category is not ");
        }
        int i = 0;
        for(StateBO stateBO : states) {
            List<GoodsBO> innerList = Lists.newArrayListWithExpectedSize(20);
            for (GoodsBO goodsBO : list) {
                if (stateBO.getStateId().equals(Integer.parseInt(goodsBO.getGoodsCategory()))) {
                    innerList.add(goodsBO);
                }
            }
            GoodsVO vo = new GoodsVO();
            if(CollectionUtils.isEmpty(innerList)){
                continue;
            }
            vo.setName(stateBO.getStateName());
            vo.setType(String.valueOf(++i));
            vo.setGoods(innerList);
            result.add(vo);
        }
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

    @Override
    public List<ToPayGoodsBO> queryGoodsByGoodsId(String goodsIds) {
        if(StringUtils.isBlank(goodsIds)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"goodsIds must not be blank while query goods info");
        }
        JSONArray array = JSONArray.parseArray(goodsIds);
        Map<Long,Integer> map = Maps.newHashMapWithExpectedSize(array.size());
        for(int i=0;i<array.size();i++){
            JSONObject object = array.getJSONObject(i);
            map.put(object.getLong("goodsId"),object.getInteger("count"));
        }
        List<Long> idList = Lists.newArrayListWithCapacity(map.size());
        for(Long id : map.keySet()){
            idList.add(id);
        }
        List<GoodsBO> list = goodsHelper.bosFromDos(goodsDAO.queryGoodsByGoodsId(idList));
        List<ToPayGoodsBO> result = Lists.newArrayListWithCapacity(list.size());
        for(GoodsBO goodsBO : list){
            ToPayGoodsBO bo = new ToPayGoodsBO();
            bo.setGoodsName(goodsBO.getGoodsName());
            bo.setId(goodsBO.getId());
            bo.setNumber(map.get(bo.getId()));
            bo.setPrice(Double.parseDouble(goodsBO.getGoodsNowPrice()));
            result.add(bo);
        }
        return result;
    }

    @Override
    public List<GoodsBO> queryGoodsByGoodsId(List<Long> goodsIds) {
        return goodsHelper.bosFromDos(goodsDAO.queryGoodsByGoodsId(goodsIds));
    }

    @Override
    public GoodsBO queryGoodsByEanCode(String goodsEanCode,String rackUUid) {
        if(StringUtils.isBlank(goodsEanCode)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"goodsEanCode must not be blank while query goods info by goods ean code");
        }
        if(StringUtils.isBlank(rackUUid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"rackUUid must not be blank while query goods info by goods ean code");
        }
		GoodsBO bo = goodsHelper.boFromDo(goodsDAO.queryGoodsByEanCode(goodsEanCode,rackUUid));
        return bo == null?new GoodsBO():bo;
    }

    @Override
    public GoodsBO getGoodsById(Long id) {
        String key = CacheUtil.generateCacheKey(Constant.GOODS_SINGLE_INFO,id);
        GoodsBO result = redisValueOperations.get(key,GoodsBO.class);
        if(null != result){
            return result;
        }
        result = goodsHelper.boFromDo(goodsDAO.getGoodsById(id));
        redisValueOperations.set(key,result,86400L);
        return result;
    }

}
