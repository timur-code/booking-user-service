package aitu.booking.userService.controller.api;

import aitu.booking.userService.controller.BaseController;
import aitu.booking.userService.dto.LoginByPhoneFormDTO;
import aitu.booking.userService.dto.UserDTO;
import aitu.booking.userService.dto.responses.ResponseSuccess;
import aitu.booking.userService.dto.responses.ResponseSuccessWithData;
import aitu.booking.userService.service.UserService;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class UserController extends BaseController {
    private UserService userService;

    @PostMapping("/register")
    public ResponseSuccessWithData<UserDTO> register(@RequestBody UserDTO userDTO) {
        UserDTO user = userService.create(userDTO, userDTO.getPassword());
        return new ResponseSuccessWithData<>(user);
    }

    @PostMapping("/login")
    public ResponseSuccessWithData<AccessTokenResponse> loginUser(@RequestBody LoginByPhoneFormDTO loginDTO) {
        AccessTokenResponse token = userService.login(loginDTO.getPhone(), loginDTO.getPassword());
        return new ResponseSuccessWithData<>(token);
    }

    @PostMapping("/logout")
    @Secured({"ROLE_user", "ROLE_admin"})
    public ResponseSuccess logoutUser(Authentication authentication) {
        userService.logout(authentication);
        return new ResponseSuccess();
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
