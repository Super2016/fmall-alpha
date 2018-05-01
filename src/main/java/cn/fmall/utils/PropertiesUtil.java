package cn.fmall.utils;

import cn.fmall.common.Overload;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取配置文件
 */
public class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    //需在tomcat启动时作用
    static {

        String fileName = "fmall.properties";
        properties = new Properties();

        try{
            properties.load(new InputStreamReader(PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName)));
        } catch (Exception e) {
            logger.error("配置文件读取异常",e);
        }
    }

    //读取配置文件
    @Overload
    public static String getProperty(String key){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    //读取配置文件,设置默认值项
    @Overload
    public static String getProperty(String key,String defaultValue){
        String value = properties.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }
}
