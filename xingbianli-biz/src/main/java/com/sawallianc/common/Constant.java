package com.sawallianc.common;

import lombok.Data;

public class Constant {
    public static final String GOODS_CATEGORY_ENAME = "goods_category";
    public static final String GOODS_STATE_ENAME = "goods_state";
    public static final String RACK_ENAME = "rack";
    public static final String CHARGE_ENAME = "charge_amount";

    /**
     * 全部商品
     */
    public static final String ALL_GOODS = "all-goods";
    /**
     * 今日特价
     */
    public static final String TODAY_SPECIAL_PRICE = "today-special-price";
    /**
     * 我常购买
     */
    public static final String MY_OFTEN_BUY = "me-often-buy";

    public enum ChargeMethod {
        ALIPAY(0,"支付宝"),
        WEIXIN(1,"微信");
        private Integer code;
        private String name;
        ChargeMethod(Integer code,String name) {
            this.code = code;
            this.name = name;
        }

        public static String getNameByCode(Integer code){
            if(null == code){
                return null;
            }
            for(ChargeMethod method : ChargeMethod.values()){
                if(method.code == code){
                    return method.name;
                }
            }
            return null;
        }
    }
    public enum QueryUserType {
        PHONE(1,"手机号"),
        ALIPAY(2,"支付宝Id"),
        WEIXIN(3,"微信openid");
        private Integer code;
        private String name;
        QueryUserType(Integer code,String name) {
            this.code = code;
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public static String getNameByCode(Integer code){
            if(null == code){
                return null;
            }
            for(QueryUserType type : QueryUserType.values()){
                if(type.code == code){
                    return type.name;
                }
            }
            return null;
        }
    }
}
