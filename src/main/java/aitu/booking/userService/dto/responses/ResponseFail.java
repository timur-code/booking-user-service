package aitu.booking.userService.dto.responses;

import lombok.Getter;

@Getter
public class ResponseFail implements StatusResponse {
    private final Boolean status = false;
    private final String message;

    public ResponseFail(String message) {
        this.message = message;
    }
}
