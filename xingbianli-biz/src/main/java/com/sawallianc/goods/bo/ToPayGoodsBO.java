package com.sawallianc.goods.bo;

import lombok.Data;

@Data
public class ToPayGoodsBO {
    private Long id;
    private String goodsName;
    private Integer number;
    private Double price;
}
