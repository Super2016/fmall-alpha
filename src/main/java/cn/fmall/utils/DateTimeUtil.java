package cn.fmall.utils;

import cn.fmall.common.Overload;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

public class DateTimeUtil {
    //使用joda-time库

    public static final String STANDARD_FORMAT = "yyyy-mm-dd HH:mm:ss";

    //时间字符串转Date格式
    @Overload
    public static Date stringToDateFormat(String time_string,String format_string){
        //格式转换器
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(format_string);
        //将字符转为日期格式
        DateTime dateTime = dateTimeFormatter.parseDateTime(time_string);
        return dateTime.toDate();
    }

    //Date格式转时间字符串
    @Overload
    public static String dateToStringFormat(Date date,String format_string){
        //接收为空内容,则返回空字符串
        if (date == null) {
            return StringUtils.EMPTY;
        }
        //将Date包装为joda
        DateTime dateTime = new DateTime(date);
        //转换为字符串,设置格式
        return dateTime.toString(format_string);
    }

    //时间字符串转Date格式(标准格式)
    @Overload
    public static Date stringToDateFormat(String time_string){
        //格式转换器
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STANDARD_FORMAT);
        //将字符转为日期格式
        DateTime dateTime = dateTimeFormatter.parseDateTime(time_string);
        return dateTime.toDate();
    }

    //Date格式转时间字符串(标准格式)
    @Overload
    public static String dateToStringFormat(Date date){
        //接收为空内容,则返回空字符串
        if (date == null) {
            return StringUtils.EMPTY;
        }
        //将Date包装为joda
        DateTime dateTime = new DateTime(date);
        //转换为字符串,设置格式
        return dateTime.toString(STANDARD_FORMAT);
    }
}
