package com.sawallianc.rack.service;

import com.sawallianc.rack.bo.RackBO;

import java.util.List;

public interface RackService {

    List<RackBO> findAllRack();

    List<RackBO> findAllAvaliableRack();

    List<RackBO> findAllDisableRack();

    RackBO getRackByUUID(String uuid);
}
