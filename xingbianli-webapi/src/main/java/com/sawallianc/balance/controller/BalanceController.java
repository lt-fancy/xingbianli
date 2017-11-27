package com.sawallianc.balance.controller;

import com.sawallianc.common.Constant;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/balance")
public class BalanceController {

    @Autowired
    private UserService userService;

    @Autowired
    private StateService stateService;

    @PostMapping(value = "/charge/{phone}/{amount}/{chargeMethod}")
    public Result charge(@PathVariable String phone,@PathVariable String amount,@PathVariable String chargeMethod){
        if(StringUtils.isBlank(phone) || StringUtils.isBlank(amount) || StringUtils.isBlank(chargeMethod)){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"phone or amount or chargeMethod is blank while charging");
        }
        BalanceVO vo = new BalanceVO();
        int chargeAmount = Integer.parseInt(amount);
        vo.setChargeAmount(chargeAmount);
        vo.setPhone(phone);
        List<StateBO> stateBOS = stateService.findChildrenStateByEname(Constant.CHARGE_ENAME);
        if(CollectionUtils.isEmpty(stateBOS)){
            throw new BizRuntimeException(ResultCode.ERROR,"charge bonus is not configured");
        }
        for(StateBO bo : stateBOS){
            Integer configuredChargeBonus = Integer.parseInt(bo.getStateName());
            if(chargeAmount == bo.getStateId().intValue()){
                vo.setBonusAmount(configuredChargeBonus);
                break;
            }
        }
        vo.setChargeMethod(Integer.parseInt(chargeMethod));
        vo.setChargeMethodName(Constant.ChargeMethod.getNameByCode(vo.getChargeMethod()));
        return Result.getSuccessResult(userService.charge(vo));
    }
}
