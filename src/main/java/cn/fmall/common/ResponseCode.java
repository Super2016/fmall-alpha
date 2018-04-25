package cn.fmall.common;

public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    //枚举属性
    private final int code;
    private final String description;

    ResponseCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    //取响应code值
    public int getCode(){
        return code;
    }

    //取响应code描述
    public String  getDescription(){
        return description;
    }
}
