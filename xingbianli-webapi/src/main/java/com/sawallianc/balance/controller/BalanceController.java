package com.sawallianc.balance.controller;

import com.google.common.collect.Lists;
import com.sawallianc.common.Constant;
import com.sawallianc.entity.Result;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.springboot.advice.WebApiAdvice;
import com.sawallianc.state.bo.StateBO;
import com.sawallianc.state.service.StateService;
import com.sawallianc.user.service.UserService;
import com.sawallianc.user.vo.BalanceVO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/balance")
public class BalanceController extends WebApiAdvice {

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
            String[] array = bo.getStateName().split(",");
            if(null == array){
                continue;
            }
            Integer configuredChargeBonus = Integer.parseInt(array[1]);
            if(chargeAmount == Integer.parseInt(array[0])){
                vo.setBonusAmount(configuredChargeBonus);
                break;
            }
        }
        if(null == vo.getBonusAmount()){
            vo.setBonusAmount(0);
        }
        vo.setChargeMethod(Integer.parseInt(chargeMethod));
        vo.setChargeMethodName(Constant.ChargeMethod.getNameByCode(vo.getChargeMethod()));
        return Result.getSuccessResult(userService.charge(vo));
    }
    @PostMapping(value = "/purchase")
    public Result purchase(@RequestBody BalanceVO vo){
        if(null == vo){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"balanceVO is null while purchasing");
        }
        if(StringUtils.isBlank(vo.getPhone()) || StringUtils.isBlank(vo.getRackUuid())){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"phone or rackUuid is blank while purchasing");
        }
        if(null == vo.getBenefitPrice() || null == vo.getSettlePrice() || null == vo.getTotalPrice()){
            throw new BizRuntimeException(ResultCode.PARAM_ERROR,"settlePrice or totalPrice or benefitPrice is null while purchasing");
        }
        return Result.getSuccessResult(userService.purchase(vo));
    }

    @GetMapping(value = "/getChargeAmountConfig")
    public Result getChargeAmountConfig(){
        List<StateBO> list = stateService.findChildrenStateByEname("charge_amount");
        if(CollectionUtils.isEmpty(list)){
            throw new BizRuntimeException(ResultCode.ERROR,"ename charge_amount queried state list is empty");
        }
        List<BalanceVO> result = Lists.newArrayListWithCapacity(list.size());
        for(StateBO bo : list){
            BalanceVO vo = new BalanceVO();
            String[] array = bo.getStateName().split(",");
            if(null == array){
                continue;
            }
            vo.setChargeAmount(Integer.parseInt(array[0]));
            vo.setBonusAmount(Integer.parseInt(array[1]));
            result.add(vo);
        }
        return Result.getSuccessResult(result);
    }
}
