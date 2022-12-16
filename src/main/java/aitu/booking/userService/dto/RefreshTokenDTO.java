package aitu.booking.userService.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class RefreshTokenDTO {
    @NotNull(message = "{token.required}")
    @NotBlank(message = "{token.required}")
    private String refreshToken;
}
