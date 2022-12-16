package aitu.booking.userService.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@ToString
public class UserDTO {

    private String id;
    private String name; // fullname
    private String firstName;
    private String lastName;
    private String patronymic;
    private String phone;
    private String email;
    private String password;
    private ZonedDateTime createdAt;

}
