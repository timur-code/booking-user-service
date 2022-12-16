package aitu.booking.userService.dto.responses;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
public class ResponseSuccessWithData<T> extends ResponseSuccess {
    private T data;

    public ResponseSuccessWithData(T data) {
        super();
        this.data = data;
    }
}
