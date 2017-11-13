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

    @GetMapping(value = "/{uuid}")
    public Result getGoodsByRackUUID(@PathVariable String uuid){
        if(StringUtils.isBlank(uuid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"request parameter uuid is blank");
        }
        return Result.getSuccessResult(rackService.findGoodsByRackUUId(uuid));
    }
}
