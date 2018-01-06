package com.sawallianc.common;

import com.sawallianc.redis.operations.RedisValueOperations;
import com.sawallianc.snow.SnowflakeIdWorker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

public class OrderIdUtil {
    private static final String ORDERID_KEY = "order_id_queue";

    public static String getOrderId(){
        SnowflakeIdWorker snow = new SnowflakeIdWorker(0L,0L);
        String orderId = String.valueOf(snow.nextId());
        return orderId;
    }
}
