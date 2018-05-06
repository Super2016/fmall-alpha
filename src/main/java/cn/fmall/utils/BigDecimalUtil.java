package cn.fmall.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    private BigDecimalUtil(){
    }
    //加
    public static BigDecimal add(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.add(b2);
    }

    //加
    public static BigDecimal sub(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.subtract(b2);
    }

    //加
    public static BigDecimal multip(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.multiply(b2);
    }

    //加
    public static BigDecimal div(double value1,double value2){
        BigDecimal b1 = new BigDecimal(Double.toString(value1));
        BigDecimal b2 = new BigDecimal(Double.toString(value2));
        return b1.divide(b2,2,BigDecimal.ROUND_HALF_UP);//保留两位小数,四舍五入
    }
}
