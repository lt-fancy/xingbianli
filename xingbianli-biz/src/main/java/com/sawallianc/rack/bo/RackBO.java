package com.sawallianc.rack.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class RackBO extends BaseBO{
    private String uuid;
    private String bizManId;
    private String replenishmanId;
    private String rackName;
    private String rackAddress;
}
