package aitu.booking.userService.controller;

import aitu.booking.userService.exception.ApiException;
import aitu.booking.userService.exception.ErrorMessage;
import aitu.booking.userService.exception.FormValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@RestControllerAdvice
public class ControllerExceptionHandler {
    private MessageSource messageSource;

    @ExceptionHandler(FormValidationException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage handleValidationExceptions(FormValidationException exc) {
        BindingResult bindingResult = exc.getBindingResult();
        String msg = "Ошибка валидации";
        String code = "validation-failed";
        ErrorMessage errorMessage = new ErrorMessage(msg, code);
        errorMessage.setFieldErrors(bindingResult.getFieldErrors());
        return errorMessage;
    }

    @ExceptionHandler(ApiException.class)
    public ErrorMessage handleApiException(ApiException exc, HttpServletResponse httpResp, Locale locale) {
        httpResp.setStatus(exc.getStatusCode());
        String msg = messageSource.getMessage(exc.getMsgCode(), exc.getArgs(), locale);
        ErrorMessage errorMessage = new ErrorMessage(msg, exc.getMsgCode());
        if (exc.getData() != null) {
            errorMessage.setData(exc.getData());
        }
        return errorMessage;
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
