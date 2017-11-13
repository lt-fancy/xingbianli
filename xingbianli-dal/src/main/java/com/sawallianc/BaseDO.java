package com.sawallianc;

import lombok.Data;

import java.util.Date;

@Data
public class BaseDO {
    private Long id;
    private Date gmtCreated;
    private Date gmtModified;
    private Integer isDeleted;
}
