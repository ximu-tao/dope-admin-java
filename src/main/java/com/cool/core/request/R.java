package com.cool.core.request;

import com.cool.core.util.I18nUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 返回信息
 */
@Schema(title = "响应数据结构")
@Data
public class R<T>  implements Serializable {
    private static final long serialVersionUID = 1L;


    @Schema(title = "编码：1000表示成功，其他值表示失败")
    private int code = 1000;

    @Schema(title = "消息内容")
    private String message = "success";

    @Schema(title = "响应数据")
    private T data;

    public R() {
        
    }    
    
    public R( int code, String message, T data ) {
        this.setCode(code);
        this.setMessage(message);
        this.setData(data);
    }

    public static R error() {
        return error(1001, "请求方式不正确或服务出现异常");
    }

    public static R error(String msg) {
        return error(1001, msg);
    }

    public static R error(int code, String msg) {
        R r = new R();
        r.setCode(code);
        r.setMessage(msg);
        return r;
    }

    public static R okMsg(String msg) {
        R r = new R();
        r.setMessage(msg);
        return r;
    }

    public void setMessage(String msg) {
        this.message = I18nUtil.getI18nMsg(msg);
    }

    public static R ok() {
        return new R();
    }

    public static <B> R<B> ok(B data) {
        return new R<B>(1000 , "success", data);
    }

    public static <B> R<B> ok(B data, String msg) {
        return new R<B>(1000 , msg , data);
    }


    public R<T> put(String key, Object value) {
        switch (key) {
            case "code" -> this.setCode( Integer.parseInt(value.toString()) );
            case "message" -> this.setMessage( value.toString() );
            case "data" -> this.setData( (T) value );
        }
        return this;
    }
}