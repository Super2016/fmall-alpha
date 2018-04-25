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

    private ServerResponse(int status){
        this.status = status;
    }

    private ServerResponse(int status,String msg){
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status,String msg,T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    @JsonIgnore
    //不出现在json序列化内容中
    public boolean statusIsSuccess(){
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

    public static <T> ServerResponse<T> createSuccessResponse(){
        //仅返回status
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createSuccessResponseMsg(String msg){
        //返回status和msg
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> createSuccessResponse(T data){
        //返回status和data
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),data);
    }

    public static <T> ServerResponse<T> createSuccessResponse(String msg,T data){
        //返回status,msg,data
        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg,data);
    }

    public static <T> ServerResponse<T> createErrorResponse(){
        //仅返回status及其默认描述
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getDescription());
    }
    public static <T> ServerResponse<T> createErrorResponseMsg(String errorMsg){
        //返回status及自定义消息
        return new ServerResponse<T>(ResponseCode.ERROR.getCode(),errorMsg);
    }
    public static <T> ServerResponse<T> createErrorResponse(int errorCode,String errorMsg){
        //返回自定义status及自定义消息
        return new ServerResponse<T>(errorCode,errorMsg);
    }
}
