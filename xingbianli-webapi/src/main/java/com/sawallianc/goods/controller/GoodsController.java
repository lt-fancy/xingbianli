package com.sawallianc.goods.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.service.GoodsService;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController extends WebApiAdvice {

    @Autowired
    private GoodsService goodsService;

    @GetMapping(value = "/{uuid}")
    public Result getGoodsByRackUUID(@PathVariable String uuid){
        List<GoodsVO> result = goodsService.findGoodsByRackUUId(uuid);
        if(CollectionUtils.isEmpty(result)){
            return Result.buildResult(ResultCode.RACK_HAS_BEEN_DOWN);
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

    @PostMapping(value = "/queryGoodsByGoodsId")
    public Result queryGoodsByGoodsId(@RequestBody Map<String,Object> map){
        if(null == map || map.isEmpty()){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"map must not be blank while query goods info");
        }
        return Result.getSuccessResult(goodsService.queryGoodsByGoodsId(map));
    }

    @GetMapping(value = "/queryGoodsByEanCode")
    public Result queryGoodsByEanCode(String goodsEanCode,String rackUUid){
        return Result.getSuccessResult(goodsService.queryGoodsByEanCode(goodsEanCode,rackUUid));
    }
}
