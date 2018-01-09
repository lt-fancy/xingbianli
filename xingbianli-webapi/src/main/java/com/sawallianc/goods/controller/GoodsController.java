package com.sawallianc.goods.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.service.GoodsService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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

    @GetMapping(value = "/{uuid}")
    public Result getGoodsByRackUUID(@PathVariable String uuid){
        List<GoodsVO> result = goodsService.findGoodsByRackUUId(uuid);
        if(CollectionUtils.isEmpty(result)){
            throw new BizRuntimeException(ResultCode.RACK_HAS_BEEN_DOWN,"rack has been down");
        }
        return Result.getSuccessResult(result);
    }

    @GetMapping(value = "/queryGoodsByGoodsName")
    public Result queryGoodsByGoodsName(String uuid,String goodsName){
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"rack uuid must not be blank");
        }
        return Result.getSuccessResult(goodsService.queryGoodsByGoodsName(uuid,goodsName));
    }
}
