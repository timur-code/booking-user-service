package aitu.booking.userService.dto;

import aitu.booking.userService.util.ValidationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ToString
public class UserInfoDTO {

    private String id;
    @NotNull(message = "{firstName.required}")
    @NotBlank(message = "{firstName.required}")
    private String firstName;
    @NotNull(message = "{lastName.required}")
    @NotBlank(message = "{lastName.required}")
    private String lastName;
    private String patronymic;
    @NotNull(message = "{phone.required}")
    @NotBlank(message = "{phone.required}")
    @Pattern(regexp = ValidationUtils.PATTERN_PHONE, message = "{phone.required}")
    private String phone;
    private String email;
}
