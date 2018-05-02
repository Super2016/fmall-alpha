package cn.fmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Constant{
    public static final String CURRENT_USER = "currentUser";
    public static final String USERNAME =  "username";
    public static final String EMAIL = "email";
    public static final String TOKEN_PREFIX = "token_";

    //使用接口创建角色分组
    public interface Role{
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    //使用接口创建排序方式分组
    public interface ProductListOrderBy{
        //这里选择使用Set集合因为其时间复杂度为O(1),相比List的时间复杂度O(n)效率高
        Set<String> PRICE_ASC_DASC = Sets.newHashSet("price_asc","price_dasc");

    }

    public enum ProductStatusEnum{
        ON_SALE(1,"在线");

        private int code;
        private String value;

        ProductStatusEnum(int code,String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
