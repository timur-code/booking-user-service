package aitu.booking.userService.controller;

import aitu.booking.userService.exception.FormValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BindingResult;

public class BaseController {

    protected MessageSource messageSource;

    protected String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, LocaleContextHolder.getLocale());
    }

    protected String getMessage(String msgCode, Object... args) {
        return messageSource.getMessage(msgCode, args, LocaleContextHolder.getLocale());
    }

    protected void checkErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new FormValidationException(bindingResult);
        }
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
