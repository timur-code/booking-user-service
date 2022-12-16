package aitu.booking.userService.dto;

import aitu.booking.userService.util.ValidationUtils;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class LoginByPhoneFormDTO {

    @NotNull(message = "{phone.required}")
    @Size(min = 12, message = "{phone.invalid}")
    @Pattern(regexp = ValidationUtils.PATTERN_PHONE, message = "{phone.invalid}")
    private String phone;

    @NotBlank(message = "{password.required}")
    private String password;

}
