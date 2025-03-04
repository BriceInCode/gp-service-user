package com.example.serviceutilisateur.responses;

public class ResponseDTO<T> {

    public static final int SUCCESS = 200;
    public static final int CREATED = 201;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int ERROR = 500;
    public static final int WARNING = 300;

    private int code;
    private String message;
    private T data;

    public ResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResponseDTO(int code, String message) {
        this(code, message, null);
    }

    public ResponseDTO(int code, T data) {
        this(code, null, data);
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(SUCCESS, message, data);
    }

    public static <T> ResponseDTO<T> created(String message, T data) {
        return new ResponseDTO<>(CREATED, message, data);
    }

    public static <T> ResponseDTO<T> error(String message, T data) {
        return new ResponseDTO<>(ERROR, message, data);
    }

    public static <T> ResponseDTO<T> error(String message) {
        return new ResponseDTO<>(ERROR, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
