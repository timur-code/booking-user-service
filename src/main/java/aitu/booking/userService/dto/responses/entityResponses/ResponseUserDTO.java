package aitu.booking.userService.dto.responses.entityResponses;

import aitu.booking.userService.dto.UserDTO;
import aitu.booking.userService.dto.responses.ResponseSuccessWithData;

public class ResponseUserDTO extends ResponseSuccessWithData<UserDTO> {
    public ResponseUserDTO(UserDTO user) {
        super(user);
    }
}
