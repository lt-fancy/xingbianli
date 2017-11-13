package com.sawallianc.state.module;

import com.sawallianc.BaseDO;
import lombok.Data;

@Data
public class StateDO extends BaseDO{
    private String ename;
    private Integer stateId;
    private String stateName;
}
