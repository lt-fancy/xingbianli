package com.sawallianc.common;

public enum GoodsCategoryEnums {
    ALL_GOODS("all-goods","全部商品"),
    TODAY_SPECIAL_PRICE("today-special-price","今日特价"),
    MY_OFTEN_BUY("me-often-buy","我常购买");
    private String name;
    private String type;

    GoodsCategoryEnums(String type, String name) {
        this.name = name;
        this.type = type;
    }

    public static String getName(String type){
        for(GoodsCategoryEnums t : GoodsCategoryEnums.values()){
            if(type.equals(t.type)){
                return t.name;
            }
        }
        return "";
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
