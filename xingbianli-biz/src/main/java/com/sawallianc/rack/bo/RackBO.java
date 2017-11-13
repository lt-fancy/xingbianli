package com.sawallianc.rack.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class RackBO extends BaseBO{
    private String uuid;
    private String goodsId;
    private String goodsName;
    private String goodsPrice;
    private String goodsStock;
    private String rackName;
    private String rackAddress;
}
