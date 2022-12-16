package aitu.booking.userService.dto;

import aitu.booking.userService.util.ValidationUtils;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RegistrationFormDTO {
    @NotNull(message = "{phone.required}")
    @Size(min = 12, message = "{phone.invalid}")
    @Pattern(regexp = ValidationUtils.PATTERN_PHONE, message = "{phone.invalid}")
    private String phone;

    @NotBlank
    private String name;

    @NotBlank(message = "{password.required}")
    @Size(min = 8)
    private String password;
}
