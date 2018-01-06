package com.sawallianc.goods.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.thirdparty.weixin.WeixinFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private WeixinFeignClient weixinFeignClient;

    @GetMapping(value = "/{uuid}")
    public Result getGoodsByRackUUID(@PathVariable String uuid,String code){
//        System.out.println(weixinFeignClient.getOpenid(Constant.APPID,Constant.SECRET,code,Constant.GRANT_TYPE));
        List<GoodsVO> result = goodsService.findGoodsByRackUUId(uuid);
        if(CollectionUtils.isEmpty(result)){
            throw new BizRuntimeException(ResultCode.RACK_HAS_BEEN_DOWN,"rack has been down");
        }
        return Result.getSuccessResult(result);
    }
}
