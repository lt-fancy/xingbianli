package com.sawallianc.goods.service;

import com.sawallianc.goods.bo.GoodsBO;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.bo.ToPayGoodsBO;

import java.util.List;

public interface GoodsService {
    /**
     * 返回货架商品的json格式数据
     * @param uuid
     * @return
     */
    List<GoodsVO> findGoodsByRackUUId(String uuid);

    /**
     * 根据商品名称筛选
     * @param uuid
     * @param goodsName
     * @return
     */
    List<GoodsVO> queryGoodsByGoodsName(String uuid,String goodsName);

    /**
     * 去待支付页面需要重新请求接口，不能从首页直接带值过来
     * 前端会传递客户购物车里面的goodsId集合，用英文逗号分隔
     * @param goodsIds
     * @return
     */
    List<ToPayGoodsBO> queryGoodsByGoodsId(String goodsIds);

    /**
     * 去待支付页面需要重新请求接口，不能从首页直接带值过来
     * 前端会传递客户购物车里面的goodsId集合，用英文逗号分隔
     * @param goodsIds
     * @return
     */
    List<GoodsBO> queryGoodsByGoodsId(List<Long> goodsIds);

    /**
     * 通过扫码获取商品详情
     * @param goodsEanCode
     * @param rackUUid
     * @return
     */
    GoodsBO queryGoodsByEanCode(String goodsEanCode,String rackUUid);

    /**
     * 通过id获取商品详情
     * @param id
     * @return
     */
    GoodsBO getGoodsById(Long id);
}
