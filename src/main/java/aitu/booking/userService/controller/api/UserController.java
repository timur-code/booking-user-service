package aitu.booking.userService.controller.api;

import aitu.booking.userService.controller.BaseController;
import aitu.booking.userService.dto.*;
import aitu.booking.userService.dto.responses.ResponseFail;
import aitu.booking.userService.dto.responses.ResponseSuccess;
import aitu.booking.userService.dto.responses.ResponseSuccessWithData;
import aitu.booking.userService.dto.responses.entityResponses.ResponseToken;
import aitu.booking.userService.dto.responses.entityResponses.ResponseUserDTO;
import aitu.booking.userService.exception.ApiException;
import aitu.booking.userService.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@ApiResponse(responseCode = "400", description = "Error",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseFail.class)))
public class UserController extends BaseController {
    private UserService userService;

//    @PostMapping("/create/restaurant-admin")
//    public ResponseEntity<?> createRestaurantAdmin(CreateRestaurantAdminDTO adminDTO) {
//
//    }

    @PostMapping("/register")
    @Operation(summary = "Register new user",
            description = "Register new user by his phone number, first name, last name and with password. " +
                    "Other fields are not required, but can be saved (email, patronymic)")
    @ApiResponse(responseCode = "200", description = "If registration went successfully, get body of registered user")
    public ResponseUserDTO register(@RequestBody @Valid UserDTO userDTO) {
        try {
            UserDTO user = userService.create(userDTO, userDTO.getPassword());
            return new ResponseUserDTO(user);
        } catch (InstanceAlreadyExistsException ex) {
            throw new ApiException(409, "user.exists");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login user",
            description = "Log user in using their phone and password.")
    @ApiResponse(responseCode = "200", description = "Get access and refresh tokens for the user.")
    public ResponseToken loginUser(@RequestBody @Valid LoginByPhoneFormDTO loginDTO) {
        AccessTokenResponse token = userService.login(loginDTO.getPhone(), loginDTO.getPassword());
        return new ResponseToken(token);
    }

    @Secured("ROLE_admin")
    @PostMapping("/is-admin")
    public ResponseEntity<ResponseSuccess> isAdmin() {
        return ResponseEntity.ok(new ResponseSuccess());
    }

    @GetMapping("/me")
    @Operation(summary = "Get user info by token",
            description = "Get user info by token.")
    @ApiResponse(responseCode = "200", description = "Get user info by token.")
    public ResponseEntity<UserInfoDTO> loginUser(Authentication authentication) {
        UserInfoDTO user = userService.getMe(authentication);
        return ResponseEntity.ok(user);
    }

    @Secured({"ROLE_user", "ROLE_admin"})
    @PostMapping("/logout")
    @Operation(summary = "Logout user",
            description = "Log user out.")
    @ApiResponse(responseCode = "200", description = "Logged user out.")
    public ResponseSuccess logoutUser(Authentication authentication) {
        userService.logout(authentication);
        return new ResponseSuccess();
    }

    @Secured("ROLE_user")
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token",
            description = "Get new access token by refresh token.")
    @ApiResponse(responseCode = "200", description = "Got new token.")
    public ResponseToken refreshToken(@RequestBody @Valid RefreshTokenDTO refreshToken) {
        AccessTokenResponse token = userService.refreshToken(refreshToken.getRefreshToken());
        return new ResponseToken(token);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
