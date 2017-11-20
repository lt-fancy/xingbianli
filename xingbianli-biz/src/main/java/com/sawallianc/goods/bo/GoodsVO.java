package com.sawallianc.goods.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GoodsVO implements Serializable{
    private String name;
    private String type;
    private List<GoodsBO> goods;
}
