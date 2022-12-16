package aitu.booking.userService.controller;

import aitu.booking.userService.dto.PathAwareDTO;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ControllerResponseHandler implements ResponseBodyAdvice<PathAwareDTO> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return PathAwareDTO.class.isAssignableFrom(methodParameter.getParameterType());
    }

    @Override
    public PathAwareDTO beforeBodyWrite(PathAwareDTO dto,
                                        MethodParameter methodParameter,
                                        MediaType mediaType,
                                        Class<? extends HttpMessageConverter<?>> aClass,
                                        ServerHttpRequest req,
                                        ServerHttpResponse resp) {

        if (dto != null) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) req).getServletRequest();
            String path = servletRequest.getRequestURL().toString();
            String origin = servletRequest.getHeader("Origin");
            String host = servletRequest.getHeader("Host");
            int apiIdx = path.indexOf("/api/");

            // Путь 1) до балансера и 2) от балансера до бэка может отличатся.
            if (StringUtils.hasLength(host) && StringUtils.hasLength(origin) && apiIdx > -1) {
                String schema = origin.replaceAll("://(.+)", "");
                path = schema + "://" + host + path.substring(apiIdx);
            }

            dto.setPath(path);
        }

        return dto;
    }
}
