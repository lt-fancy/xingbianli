package com.sawallianc;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseBO implements Serializable{
    private Long id;
    private String gmtCreated;
    private String gmtModified;
    private String isDeleted;
}
