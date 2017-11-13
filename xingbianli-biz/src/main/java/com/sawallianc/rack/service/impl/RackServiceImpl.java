package com.sawallianc.rack.service.impl;

import com.sawallianc.annotation.Cacheable;
import com.sawallianc.rack.bo.RackBO;
import com.sawallianc.rack.dao.RackDAO;
import com.sawallianc.rack.service.RackService;
import com.sawallianc.rack.util.RackHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RackServiceImpl implements RackService {

    @Autowired
    private RackDAO rackDAO;

    @Override
    @Cacheable
    public List<RackBO> findGoodsByRackUUId(String uuid) {
        return RackHelper.bosFromDos(rackDAO.findGoodsByRackUUId(uuid));
    }
}
