package com.sawallianc.common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    private static final String YYYY_MM_DD = "yyyy-MM-dd";
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final SimpleDateFormat SDF_YMD = new SimpleDateFormat(YYYY_MM_DD);
    private static final SimpleDateFormat SDF_YMDHMS = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
    public static String date2Str(Date date){
        return SDF_YMDHMS.format(date);
    }

}
