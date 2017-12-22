package com.sawallianc.goods.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.goods.bo.GoodsVO;
import com.sawallianc.goods.service.GoodsService;
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
        return Result.getSuccessResult(result);
    }
}
