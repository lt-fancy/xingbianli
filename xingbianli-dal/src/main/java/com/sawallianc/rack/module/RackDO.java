package com.sawallianc.rack.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class RackDO extends BaseDO{
    private String uuid;
    private Long bizmanId;
    private Long replenishmanId;
    private String rackName;
    private String rackAddress;
}
