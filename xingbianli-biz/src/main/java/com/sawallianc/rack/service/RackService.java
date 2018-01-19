package com.sawallianc.rack.service;

import com.sawallianc.rack.bo.RackBO;
import com.sawallianc.rack.bo.RackCityBO;
import com.sawallianc.rack.module.RackApplyDO;

import java.util.List;

public interface RackService {

    List<RackBO> findAllRack();

    List<RackBO> findAllAvaliableRack();

    List<RackBO> findAllDisableRack();

    RackBO getRackByUUID(String uuid);

    int apply(RackApplyDO bo);

    List<RackCityBO> getRackApplyCity();

    void generateRackQRCode(String uuids);
}
