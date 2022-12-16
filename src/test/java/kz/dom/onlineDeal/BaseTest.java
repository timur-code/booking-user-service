package kz.dom.onlineDeal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.lang.reflect.Method;

public class BaseTest {
    private ObjectMapper mapper;

    @SneakyThrows
    public Method getPrivateMethod(Object object, String methodName, Class<?>... parameterClasses) {
        Method method = object.getClass().getDeclaredMethod(methodName, parameterClasses);
        method.setAccessible(true);
        return method;
    }

    public String toJson(Object object) throws JsonProcessingException {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }
}
