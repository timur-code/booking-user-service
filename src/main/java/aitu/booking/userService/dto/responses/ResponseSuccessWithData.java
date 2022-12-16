package aitu.booking.userService.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString(callSuper = true)
@AllArgsConstructor
public class ResponseSuccessWithData<T> extends ResponseSuccess {
    private final T data;
}
