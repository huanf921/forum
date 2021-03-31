package top.vs.forum.dto;

import java.io.Serializable;

/**
 * @author visional
 * @type ResultDTO
 * @desc
 * @createDate 2021/2/22
 * @version: 1.0
 */
public class ResultDTO<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    protected String code;
    protected String message;
    protected T data;

    public ResultDTO() {
        this.code = "200";
        this.message = "OK";
    }

    public ResultDTO(T data) {
        this.code = "200";
        this.message = "OK";
        this.data = data;
    }

    public ResultDTO(String code, String message) {
        this(null, code, message);
    }

    public ResultDTO(T data, String code, String message) {
        this.code = "200";
        this.message = "OK";
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ResultDTO<T> ok() {
        return new ResultDTO();
    }

    public static <T> ResultDTO<T> ok(T data) {
        return new ResultDTO(data);
    }

    public static <T> ResultDTO<T> error(String code, String msg) {
        return new ResultDTO(code, msg);
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return this.data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
