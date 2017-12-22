package com.sawallianc.rack.controller;

import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.rack.service.RackService;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
