package com.sawallianc.goods.bo;

import lombok.Data;

@Data
public class ToPayGoodsBO {
    private Long id;
    private String goodsName;
    private String goodsUri;
    private String goodsOldPrice;
    private String goodsNowPrice;
    private Integer number;
    private Double price;
}
