package cn.fmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * 高复用服务响应对象
 * 根据传入的数据序列化成json对象返回响应
 * @param <T>
 */
//json序列化时不包含空值的key
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{

    //状态码
    private int status;
    //消息
    private String msg;
    //展示数据
    private T data;

    //构造方法
    //仅返回状态码
    private ServerResponse(int status){
        this.status = status;
    }
    //返回状态码与消息
    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }
    //返回状态码与数据
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    //返回状态码、消息、数据
    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //不出现在json序列化内容中
    //判断状态码是SUCCESS
    public boolean currentStatusIsSuccess(){
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    //获取响应码
    public int getStatus(){
        return this.status;
    }
    //获取消息
    public String getMsg() {
        return msg;
    }
    //获取数据
    public T getData() {
        return data;
    }





/* <<<<<<<<<<<<<<<<<<<<<<<<<<<  SUCCESS  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> */





    //SUCCESS
    //无参
    //仅返回status
    public static <T> ServerResponse<T> createSuccessResponse(){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    //SUCCESS
    //msg
    //返回status和msg
    public static <T> ServerResponse<T> createSuccessResponseMsg(String msg){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    //SUCCESS
    //data
    //返回status和data
    public static <T> ServerResponse<T> createSuccessResponse(T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    //SUCCESS
    //msg,data
    //返回status,msg,data
    public static <T> ServerResponse<T> createSuccessResponse(String msg,T data){
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }





    /* <<<<<<<<<<<<<<<<<<<<<<<<<<<  SUCCESS  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> */




    //ERROR
    //无参
    //仅返回status及其默认描述
    public static <T> ServerResponse<T> createErrorResponse(){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDescription());
    }

    //ERROR
    //errorMsg
    //返回status及自定义errorMsg
    public static <T> ServerResponse<T> createErrorResponseMsg(String errorMsg){
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }

    //ERROR
    //errorCode,errorMsg
    //返回自定义errorCode及自定义errorMsg
    public static <T> ServerResponse<T> createErrorResponse(int errorCode,String errorMsg){
        return new ServerResponse<T>(errorCode,errorMsg);
    }
}
