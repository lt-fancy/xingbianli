package com.sawallianc.user.controller;

import com.sawallianc.common.Constant;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.springboot.advice.WebApiAdvice;
import com.sawallianc.user.bo.UserBO;
import com.sawallianc.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class UserController extends WebApiAdvice {

    @Autowired
    private UserService userService;

    @GetMapping(value = "/queryUserInfoByType/{type}/{value}")
    public Result queryUserInfoByType(@PathVariable Integer type, @PathVariable String value) {
        if (null == type) {
            throw new BizRuntimeException(ResultCode.PARAM_ERROR, "type is null while query userInfo");
        }
        if (StringUtils.isBlank(value)) {
            throw new BizRuntimeException(ResultCode.PARAM_ERROR, "value is blank while query userInfo");
        }
        UserBO bo;
        if (type.equals(Constant.QueryUserType.PHONE.getCode())) {
            bo = userService.queryUserInfoByPhone(value);
        } else if (type.equals(Constant.QueryUserType.ALIPAY.getCode())) {
            bo = userService.queryUserInfoByAlipayId(value);
        } else if (type.equals(Constant.QueryUserType.WEIXIN.getCode())) {
            bo = userService.queryUserInfoByOpenid(value);
        } else {
            throw new BizRuntimeException(ResultCode.PARAM_ERROR, "type " + type + " is not supported while query user info");
        }
        bo = bo == null ? new UserBO() : bo;
        return Result.getSuccessResult(bo);
    }

    @GetMapping(value = "/queryIfRegisteredByOpenid/{openid}")
    public Result queryIfRegisteredByOpenid(@PathVariable String openid){
        if(StringUtils.isBlank(openid)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"openid is blank while queryIfRegisteredByOpenid");
        }
        UserBO bo = userService.queryUserInfoByOpenid(openid);
        if(null == bo || StringUtils.isBlank(bo.getPhone())){
            return Result.getSuccessResult(0);
        } else {
            return Result.getSuccessResult(1);
        }
    }

    @PostMapping(value = "/addUser")
    public Result addUser(@RequestBody UserBO userBO) {
        if (null == userBO) {
            throw new BizRuntimeException(ResultCode.PARAM_ERROR, "userBO is null while add user");
        }
        return Result.getSuccessResult(userService.addUser(userBO));
    }
}
