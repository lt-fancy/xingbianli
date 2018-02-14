package com.sawallianc.goods.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class GoodsDO extends BaseDO{
    private String goodsName;
    private String goodsUri;
    private String goodsTag;
    private String goodsEanCode;
    private Double goodsOldPrice;
    private Double goodsNowPrice;
    private Double goodsDiscount;
    private Integer goodsCategory;
    private Integer isSpecialPrice;
    private Integer isLimited;
    private Integer goodsNumber;
    private Integer isRandomDiscount;
}
