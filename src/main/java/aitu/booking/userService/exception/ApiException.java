package aitu.booking.userService.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApiException extends RuntimeException {
    private String msgCode;
    private Object[] args;
    private int statusCode = 500;
    private Map<String, Object> data;

    public ApiException(String msgCode) {
        this.msgCode = msgCode;
    }

    public ApiException(String msgCode, Object... args) {
        this.msgCode = msgCode;
        this.args = args;
    }

    public ApiException(int statusCode, String msgCode, Object... args) {
        this.statusCode = statusCode;
        this.msgCode = msgCode;
        this.args = args;
    }
}
