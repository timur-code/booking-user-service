package aitu.booking.userService.service;

import aitu.booking.userService.dto.CreateRestaurantAdminDTO;
import aitu.booking.userService.dto.UserInfoDTO;
import aitu.booking.userService.dto.UserDTO;
import aitu.booking.userService.util.KeycloakUtils;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserService {
    private KeycloakService keycloakService;


    public UserDTO findUserByPhone(String phone) {
        UserRepresentation user = keycloakService.getUserByUsername(phone);
        return user == null ? null : KeycloakUtils.convertToUserDTO(user);
    }

    public UserInfoDTO getMe(Authentication authentication) {
        String id = KeycloakUtils.getUserUuidFromAuth(authentication).toString();
        UserDTO dto = findById(id);
        UserInfoDTO response = new UserInfoDTO();
        BeanUtils.copyProperties(dto, response);
        return response;
    }

//    @Cacheable(value = CacheConfig.CACHE_USER)
    public UserDTO findById(String id) {
        return KeycloakUtils.convertToUserDTO(keycloakService.getUserById(id));
    }

    public boolean existsByPhone(String phone) {
        return findUserByPhone(phone) != null;
    }

    public boolean existsByIin(String phone) {
        throw new RuntimeException("Not implemented");
    }

    public List<UserDTO> search(String searchStr, Integer pageNum, Integer pageSize) {
        Integer from = (pageNum - 1) * pageSize;
        Integer to = pageNum * pageSize;
        return keycloakService.search(searchStr, from, to)
                .stream()
                .map(KeycloakUtils::convertToUserDTO)
                .collect(Collectors.toList());
    }

    public Integer count(String searchStr) {
        return keycloakService.count(searchStr);
    }

    public AccessTokenResponse login(String username, String password) {
        return keycloakService.login(username, password);
    }

    public AccessTokenResponse impersonate(String username) {
        return keycloakService.impersonate(username);
    }

    public void logout(Authentication authentication) {
        String userId = getUserIdByAuth(authentication);
        keycloakService.logout(userId);
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        return keycloakService.refreshToken(refreshToken);
    }

    public UserDTO create(UserDTO user, String password) throws InstanceAlreadyExistsException {
        UserRepresentation userRepresentation = KeycloakUtils.convertToUserRepresentation(user);
        userRepresentation.setEnabled(true);
        KeycloakUtils.setUserRepresentationPassword(userRepresentation, password);
        String id = keycloakService.createUser(userRepresentation);
        user.setId(id);

        return user;
    }

    public CreateRestaurantAdminDTO createRestaurantAdmin(CreateRestaurantAdminDTO adminDTO, String password) throws InstanceAlreadyExistsException {
        UserRepresentation userRepresentation = KeycloakUtils.convertToUserRepresentation(adminDTO);
        userRepresentation.setEnabled(true);
        KeycloakUtils.setUserRepresentationPassword(userRepresentation, password);
        String id = keycloakService.createRestaurantAdmin(userRepresentation);
        adminDTO.setId(id);

        return adminDTO;
    }

//    @CacheEvict(value = CacheConfig.CACHE_USER, key = "#user.id")
    public void update(UserDTO user) {
        keycloakService.updateUser(KeycloakUtils.convertToUserRepresentation(user));
    }

    public boolean resetPassword(String phone, String password) {
        try {
            keycloakService.resetPassword(phone, password);
            return true;
        } catch (Exception e) {
            log.error("error when reset password", e);
            return false;
        }
    }

    public int changePassword(String userId, String oldPassword, String newPassword) {
        UserDTO user = findById(userId);
        try {
            AccessTokenResponse accessTokenResponse = login(user.getPhone(), oldPassword);
            if (accessTokenResponse == null) {
                return -1;
            }
            keycloakService.resetPassword(user.getPhone(), newPassword);
            return 1;
        } catch (Exception e) {
            log.error("error while changing password", e);
            return -1;
        }
    }

    private String getUserIdByAuth(Authentication authentication) {
        return  ((JwtAuthenticationToken) authentication).getToken().getSubject();
    }

    @Autowired
    public void setKeycloakService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }
}
