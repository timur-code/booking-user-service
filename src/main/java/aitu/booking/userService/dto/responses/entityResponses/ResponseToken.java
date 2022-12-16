package aitu.booking.userService.dto.responses.entityResponses;

import aitu.booking.userService.dto.responses.ResponseSuccessWithData;
import org.keycloak.representations.AccessTokenResponse;

public class ResponseToken extends ResponseSuccessWithData<AccessTokenResponse> {
    public ResponseToken(AccessTokenResponse token) {
        super(token);
    }
}
