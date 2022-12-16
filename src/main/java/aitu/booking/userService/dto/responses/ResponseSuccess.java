package aitu.booking.userService.dto.responses;

import lombok.Getter;

@Getter
public class ResponseSuccess implements StatusResponse {
    private final Boolean status = true;

}
