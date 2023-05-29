package aitu.booking.userService.dto;

import aitu.booking.userService.util.ValidationUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@ToString
public class UpdateUserDTO {

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
