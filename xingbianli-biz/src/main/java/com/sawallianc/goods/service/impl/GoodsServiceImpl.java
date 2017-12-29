package com.sawallianc.goods.service.impl;

import com.google.common.collect.Lists;
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

import java.text.MessageFormat;
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
        String key = MessageFormat.format(Constant.ALL_GOODS_RACK_UUID,uuid);
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
        redisValueOperations.set(key,result);
        return result;
    }

}
