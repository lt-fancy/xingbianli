package com.sawallianc.goods.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class GoodsBO extends BaseBO{
    private String goodsName;
    private String goodsUri;
    private String goodsOldPrice;
    private String goodsNowPrice;
    private String goodsDiscount;
    private String goodsCategory;
    private String goodsCategoryName;
    private String isLimited;
    private String isSpecialPrice;
    private String goodsStateName;
    private String goodsNumber;
    private String goodsTag;
}
