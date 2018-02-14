package com.sawallianc.weixin.entity;


import com.sawallianc.entity.ResultCode;
import com.sawallianc.entity.exception.BizRuntimeException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;

/**
 * Created by fingertap on 2017/6/2.
 */
public class ReceiveXmlProcess {
    /**
     * 解析微信xml消息
     * @param strXml
     * @return
     */
    public static Object getMsgEntity(String strXml, Class<?> clazz){
        Object msg = null;
        if (strXml.length() <= 0 || strXml == null)
            return null;

        // 将字符串转化为XML文档对象
        Document document = null;
        try {
            document = DocumentHelper.parseText(strXml);
        } catch (DocumentException e) {
            throw new BizRuntimeException(ResultCode.ERROR,"parse xml error,xml:"+strXml);
        }
        // 获得文档的根节点
        Element root = document.getRootElement();
        // 遍历根节点下所有子节点
        Iterator<?> iter = root.elementIterator();

        // 遍历所有结点
        try {
            msg = clazz.newInstance();
        } catch (Exception e) {
            throw new BizRuntimeException(ResultCode.ERROR,"new instance error:"+e);
        }
//            //利用反射机制，调用set方法
//            //获取该实体的元类型
//            Class<?> c = Class.forName("com.sawallianc.weixin.entity.UnionOrderReceiveXmlEntity");
//            msg = c.newInstance();//创建这个实体的对象

        while(iter.hasNext()){
            Element ele = (Element)iter.next();
            //获取set方法中的参数字段（实体类的属性）
            Field field = null;
            try {
                field = clazz.getDeclaredField(ele.getName());
            } catch (NoSuchFieldException e) {
                continue;
            }
            //获取set方法，field.getType())获取它的参数数据类型
            try {
                Method method = clazz.getDeclaredMethod("set"+ele.getName().substring(0,1).toUpperCase()+ele.getName().substring(1), field.getType());
                //调用set方法
                method.invoke(msg, ele.getText());
            } catch (Exception e) {
                throw new BizRuntimeException(ResultCode.ERROR,"reflect invoke method error: "+e);
            }
        }
        return msg;
    }

}
