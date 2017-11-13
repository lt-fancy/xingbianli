package com.sawallianc.state.bo;

import com.sawallianc.BaseBO;
import lombok.Data;

@Data
public class StateBO extends BaseBO{
    private String ename;
    private Integer stateId;
    private String stateName;
}
