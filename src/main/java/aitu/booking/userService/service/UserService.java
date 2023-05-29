package aitu.booking.userService.service;

import aitu.booking.userService.dto.*;
import aitu.booking.userService.exception.ApiException;
import aitu.booking.userService.util.KeycloakUtils;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class UserService {
    private KeycloakService keycloakService;
    @Value("${service.user.token}")
    private String serviceToken;


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

    public UserInfoDTO getInfoById(UUID userId) {
        UserDTO dto = findById(userId.toString());
        UserInfoDTO response = new UserInfoDTO();
        BeanUtils.copyProperties(dto, response);
        return response;
    }

    public List<UserInfoDTO> getUsersInfoById(RequestUsersDTO dto) {
        List<UserInfoDTO> list = new ArrayList<>(dto.getIdList().size());
        dto.getIdList().forEach(id -> list.add(getInfoById(id)));
        return list;
    }

    //    @Cacheable(value = CacheConfig.CACHE_USER)
    public UserDTO findById(String id) {
        return KeycloakUtils.convertToUserDTO(keycloakService.getUserById(id));
    }

    public boolean existsByPhone(String phone) {
        return findUserByPhone(phone) != null;
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

    public CreateRestaurantAdminDTO createRestaurantAdmin(CreateRestaurantAdminDTO adminDTO, String password, String token) throws InstanceAlreadyExistsException {
        UserRepresentation userRepresentation = KeycloakUtils.convertToUserRepresentation(adminDTO);
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName("Администратор");
        userRepresentation.setLastName(".");
        KeycloakUtils.setUserRepresentationPassword(userRepresentation, password);
        String id = keycloakService.createRestaurantAdmin(userRepresentation);
        adminDTO.setId(id);
        adminDTO.setPassword(null);
        return adminDTO;
    }

    //    @CacheEvict(value = CacheConfig.CACHE_USER, key = "#user.id")
    public void update(UpdateUserDTO user, Authentication authentication) throws IllegalAccessException {
        String id = KeycloakUtils.getUserUuidFromAuth(authentication).toString();
        user.setId(id);
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
        return ((JwtAuthenticationToken) authentication).getToken().getSubject();
    }

    @Autowired
    public void setKeycloakService(KeycloakService keycloakService) {
        this.keycloakService = keycloakService;
    }
}
