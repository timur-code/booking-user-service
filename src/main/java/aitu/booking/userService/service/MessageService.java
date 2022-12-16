package aitu.booking.userService.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    protected MessageSource messageSource;

    public String getMessage(String msgCode) {
        return messageSource.getMessage(msgCode, null, LocaleContextHolder.getLocale());
    }

    public String getMessage(String msgCode, Object... args) {
        return messageSource.getMessage(msgCode, args, LocaleContextHolder.getLocale());
    }

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}
