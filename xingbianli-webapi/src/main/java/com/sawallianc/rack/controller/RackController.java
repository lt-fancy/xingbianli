package com.sawallianc.rack.controller;

import com.alibaba.fastjson.JSONObject;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.rack.module.RackApplyDO;
import com.sawallianc.rack.service.RackService;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rack")
public class RackController extends WebApiAdvice{

    @Autowired
    private RackService rackService;

    @GetMapping(value = "/all")
    public Result findAllRack(){
        return Result.getSuccessResult(rackService.findAllRack());
    }

    @GetMapping(value = "/allAvailable")
    public Result findAllAvaliableRack(){
        return Result.getSuccessResult(rackService.findAllAvaliableRack());
    }

    @GetMapping(value = "/allDisable")
    public Result findAllDisableRack(){
        return Result.getSuccessResult(rackService.findAllDisableRack());
    }

    @GetMapping(value = "/{uuid}")
    public Result getRackByUUID(@PathVariable String uuid){
        return Result.getSuccessResult(rackService.getRackByUUID(uuid));
    }

    @GetMapping(value = "/applyRack")
    public Result applyRack(String json){
        if(StringUtils.isBlank(json)){
            throw new BizRuntimeException(ResultCode.ERROR,"json is blank while apply rack");
        }
        RackApplyDO bo = JSONObject.parseObject(json,RackApplyDO.class);
        return Result.getSuccessResult(rackService.apply(bo));
    }

    @GetMapping("/getRackApplyCity")
    public Result getRackApplyCity(){
        return Result.getSuccessResult(rackService.getRackApplyCity());
    }

    @GetMapping("/generateRackQRCode")
    public Result generateRackQRCode(String uuids){
        rackService.generateRackQRCode(uuids);
        return Result.getSuccessResult(1);
    }

}
