package aitu.booking.userService.util;

import aitu.booking.userService.dto.UserDTO;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

public class KeycloakUtils {
    public static UserDTO convertToUserDTO(UserRepresentation userRepresentation) {
        UserDTO userDto = new UserDTO();
        userDto.setId(userRepresentation.getId());
        userDto.setPhone(userRepresentation.getUsername());
        userDto.setName(getFullName(userRepresentation));
        userDto.setFirstName(userRepresentation.getFirstName());
        userDto.setLastName(userRepresentation.getLastName());
        userDto.setPatronymic(userRepresentation.firstAttribute("patronymic"));
        userDto.setEmail(userRepresentation.getEmail());

        Instant instant = Instant.ofEpochMilli(userRepresentation.getCreatedTimestamp());
        userDto.setCreatedAt(ZonedDateTime.ofInstant(instant, ZoneOffset.UTC));

        return userDto;
    }

    public static UserRepresentation convertToUserRepresentation(UserDTO userDTO) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setId(userDTO.getId());
        userRepresentation.setUsername(userDTO.getPhone());
        userRepresentation.setFirstName(userDTO.getFirstName());
        userRepresentation.setLastName(userDTO.getLastName());
        userRepresentation.setEmail(userDTO.getEmail());
        userRepresentation.singleAttribute("patronymic", StringUtils.hasLength(userDTO.getPatronymic()) ? userDTO.getPatronymic() : null);
        userRepresentation.singleAttribute("phone", userDTO.getPhone());

        return userRepresentation;
    }

    public static CredentialRepresentation getCredentialRepresentation(String password, boolean isTempPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(isTempPassword);
        return credential;
    }

    public static void setUserRepresentationPassword(UserRepresentation userRepresentation, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        userRepresentation.setCredentials(Arrays.asList(credential));
    }

    private static String getFullName(UserRepresentation userRepresentation) {
        String firstName = userRepresentation.getFirstName();
        String lastName = userRepresentation.getLastName();
        String patronymic = userRepresentation.firstAttribute("patronymic");

        String fullname = (StringUtils.hasLength(lastName) ? lastName : "") + " " +
                (StringUtils.hasLength(firstName) ? firstName : "") + " " +
                (StringUtils.hasLength(patronymic) ? patronymic : "");

        return fullname.trim();
    }

}
