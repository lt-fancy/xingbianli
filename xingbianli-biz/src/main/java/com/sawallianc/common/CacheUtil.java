package com.sawallianc.common;

import java.text.MessageFormat;

public class CacheUtil {
    public static String generateCacheKey(String suffix,Object ... args){
        return MessageFormat.format(suffix,args);
    }
}
