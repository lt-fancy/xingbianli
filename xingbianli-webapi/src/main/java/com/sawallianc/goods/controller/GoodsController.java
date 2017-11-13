package com.sawallianc.goods.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.goods.service.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @GetMapping(value = "/{uuid}")
    public Result getGoodsByRackUUID(@PathVariable String uuid){
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter uuid is blank");
        }
        Map<String,Object> result = goodsService.findGoodsByRackUUId(uuid);
        return Result.getSuccessResult(result);
    }
}
