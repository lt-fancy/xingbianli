package com.sawallianc.rack.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.sawallianc.common.Constant;
import com.sawallianc.common.QRCodeUtil;
import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import com.sawallianc.rack.bo.RackBO;
import com.sawallianc.rack.bo.RackCityBO;
import com.sawallianc.rack.dao.RackDAO;
import com.sawallianc.rack.module.RackApplyDO;
import com.sawallianc.rack.service.RackService;
import com.sawallianc.rack.util.RackHelper;
import com.sawallianc.redis.operations.RedisValueOperations;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class RackServiceImpl implements RackService {
    private static final int THREAD_COUNT = 20;
    private static final CountDownLatch LATCH = new CountDownLatch(THREAD_COUNT);

    @Autowired
    private RackDAO rackDAO;

    @Autowired
    private RedisValueOperations redisValueOperations;

    @Override
    public List<RackBO> findAllRack() {
        String key = Constant.ALL_RACK;
        List<RackBO> list = redisValueOperations.getArray(key, RackBO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        list = RackHelper.bosFromDos(rackDAO.findAllRack());
        redisValueOperations.set(key, list, Constant.DAY_SECONDS * 7);
        return list;

    }

    @Override
    public List<RackBO> findAllAvaliableRack() {
        String key = Constant.ALL_AVAILABLE_RACK;
        List<RackBO> list = redisValueOperations.getArray(key, RackBO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }

        list = RackHelper.bosFromDos(rackDAO.findAllAvaliableRack());
        redisValueOperations.set(key, list, Constant.DAY_SECONDS * 7);
        return list;
    }

    @Override
    public List<RackBO> findAllDisableRack() {
        String key = Constant.ALL_DISABLE_RACK;
        List<RackBO> list = redisValueOperations.getArray(key, RackBO.class);
        if (CollectionUtils.isNotEmpty(list)) {
            return list;
        }

        list = RackHelper.bosFromDos(rackDAO.findAllDisableRack());
        redisValueOperations.set(key, list, Constant.DAY_SECONDS * 7);
        return list;
    }

    @Override
    public RackBO getRackByUUID(String uuid) {
        if (StringUtils.isBlank(uuid)) {
            throw new BizRuntimeException(ResultCode.PARAM_ERROR, "uuid is null while querying rack by uuid");
        }
        String key = MessageFormat.format(Constant.SINGLE_RACK, uuid);
        RackBO bo = JSONObject.parseObject(redisValueOperations.get(key), RackBO.class);
        if (null != bo) {
            return bo;
        }
        bo = RackHelper.boFromDo(rackDAO.getRackByUUID(uuid));
        redisValueOperations.set(key, bo, Constant.DAY_SECONDS * 7);
        return bo;
    }

    @Override
    public int apply(RackApplyDO bo) {
        return rackDAO.apply(bo) > 0 ? 1 : 0;
    }

    @Override
    public List<RackCityBO> getRackApplyCity() {
        String key = "rack-city-info";
        List<RackCityBO> result = redisValueOperations.getArray(key,RackCityBO.class);
        if(CollectionUtils.isNotEmpty(result)){
            return result;
        }
        List<String> list = rackDAO.getRackApplyCity();
        if(CollectionUtils.isEmpty(list)){
            return Lists.newArrayList();
        }
        result = Lists.newArrayListWithCapacity(list.size());
        for(String string : list){
            RackCityBO bo = new RackCityBO();
            bo.setLabel(string);
            bo.setValue(string);
            result.add(bo);
        }
        redisValueOperations.set(key,result);
        return result;
    }

    @Override
    public void generateRackQRCode(String uuids) {
        final String imagePath = "/app/images/rack/";
        List<RackBO> all = Lists.newArrayListWithExpectedSize(100);
        if(StringUtils.isBlank(uuids)){
            //为所有的货架生成
            all = this.findAllAvaliableRack();
        } else {
            if(uuids.contains(",")){
                String[] array = uuids.split(",");
                for(String string : array){
                    all.add(this.getRackByUUID(string));
                }
            } else {
                all.add(this.getRackByUUID(uuids));
            }
        }
        if(CollectionUtils.isEmpty(all)){
            return;
        }
        final LinkedBlockingQueue<String> queue = Queues.newLinkedBlockingQueue(all.size());
        for(RackBO bo : all){
            if(StringUtils.isNotBlank(bo.getUuid())){
                queue.add(bo.getUuid());
            }
        }
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        for(int i=0;i<THREAD_COUNT;i++){
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    String uuid = queue.peek();
                    while(StringUtils.isNotBlank(uuid)){
                        uuid = queue.poll();
                        String text = "https://h5.ljlhz.com/rack/"+uuid;
                        try {
                            QRCodeUtil.encode(text,"/app/images/logo.jpg",imagePath+uuid+".jpg",true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if(Thread.currentThread().isInterrupted()){
                            break;
                        }
                        uuid = queue.peek();
                    }
                    LATCH.countDown();
                }
            });
        }
        try {
            LATCH.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
