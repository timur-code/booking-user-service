package aitu.booking.userService.exception;

import lombok.Data;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;

@Data
public class ErrorMessage {
    private boolean status;
    private String error;
    private String errorCode;
    private Map<String, Object> data;
    private List<FieldError> fieldErrors;

    public ErrorMessage(String error) {
        this.error = error;
    }

    public ErrorMessage(String error, String errorCode) {
        this.error = error;
        this.errorCode = errorCode;
    }
}
