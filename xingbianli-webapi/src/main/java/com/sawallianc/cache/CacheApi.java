package com.sawallianc.cache;

import com.sawallianc.entity.Result;
import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.springboot.advice.WebApiAdvice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/cache")
public class CacheApi extends WebApiAdvice{

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @GetMapping("/delCacheByKey")
    public Result delCacheByKey(String key){
        if(StringUtils.isBlank(key)){
            redisValueOperations.flushAll();
            return Result.getSuccessResult(1);
        }
        List<String> keys = redisValueOperations.keys(key+"*");
        for(String value : keys){
            redisValueOperations.delete(value);
        }
        return Result.getSuccessResult(1);
    }
}
