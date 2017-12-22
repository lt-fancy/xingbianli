package com.sawallianc.common;

public class Constant {

    public static final Long DAY_SECONDS = 86400L;

    /**
     * state相关
     */
    public static final String GOODS_CATEGORY_ENAME = "goods_category";
    public static final String GOODS_STATE_ENAME = "goods_state";
    public static final String RACK_ENAME = "rack";
    public static final String CHARGE_ENAME = "charge_amount";
    public static final String GOODS_TAG = "goods_tag";
    public static final String RANDOM_DISCOUNT = "random_discount";

    /**
     * 缓存key
     */
    public static final String ALL_GOODS_RACK_UUID = "xingbianli-all-goods-rack:{0}";
    public static final String ALL_RACK = "xingbianli-all-rack";
    public static final String ALL_AVAILABLE_RACK = "xingbianli-all-available-rack";
    public static final String ALL_DISABLE_RACK = "xingbianli-all-disable-rack";
    public static final String SINGLE_RACK = "xingbianli-single_rack:{0}";
    public static final String STATE_SINGLE_INFO = "state-single-info:{0}:{1}";
    public static final String STATE_LIST_INFO = "state-list-info:{0}";
    public static final String ORDER_LIST_INFO = "order-list-info:{0}";
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
