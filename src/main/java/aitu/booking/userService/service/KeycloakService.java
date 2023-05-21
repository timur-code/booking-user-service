package aitu.booking.userService.service;

import aitu.booking.userService.exception.ApiException;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.authorization.client.Configuration;
import org.keycloak.authorization.client.util.Http;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.management.InstanceAlreadyExistsException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class KeycloakService {
    private volatile Keycloak instance;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;
    @Value("${keycloak-master.realm}")
    private String masterRealmName;
    @Value("${keycloak-master.username}")
    private String masterUsername;
    @Value("${keycloak-master.password}")
    private String masterPassword;
    @Value("${keycloak-master.client-id}")
    private String masterClientId;

    @Value("${keycloak.realm}")
    private String realmName;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    private String GROUP_USERS = "users";
    private String GROUP_RES_ADMIN = "restaurant_admins";

    public Keycloak getMasterKeycloakInstance() {
        Keycloak result = instance;
        if (result == null) {
            synchronized (this) {
                result = instance;
                if (result == null) {
                    instance = result = Keycloak.getInstance(serverUrl, masterRealmName, masterUsername, masterPassword, masterClientId);
                }
            }
        }
        return result;
    }

    public UserRepresentation getUserById(String id) {
        return getMasterKeycloakInstance()
                .realm(realmName)
                .users()
                .get(id)
                .toRepresentation();
    }

    public UserRepresentation getUserByUsername(String username) {
        List<UserRepresentation> list = getMasterKeycloakInstance()
                .realm(realmName)
                .users()
                .search(username, true);

        return list.size() > 0 ? list.get(0) : null;
    }

    public List<UserRepresentation> search(String searchStr, Integer from, Integer to) {
        return getMasterKeycloakInstance().realm(realmName).users().search(searchStr, from, to - from);
    }

    public Integer count(String searchStr) {
        return getMasterKeycloakInstance().realm(realmName).users().count(searchStr);
    }

    public String createUser(UserRepresentation userRepresentation) throws InstanceAlreadyExistsException {
        if (CollectionUtils.isEmpty(userRepresentation.getGroups())) {
            userRepresentation.setGroups(Arrays.asList(GROUP_USERS));
        }

        RealmResource realm = getMasterKeycloakInstance().realm(realmName);
        Response response = realm.users().create(userRepresentation);

        if (response.getStatus() == 409) {
            log.info("User {} already exists.", userRepresentation.getUsername());
            throw new InstanceAlreadyExistsException();
        }

        if (response.getStatus() < 200 || response.getStatus() > 299) {
            String error = "User create error: " + response.readEntity(String.class);
            log.error(error);
            throw new RuntimeException(error);
        }

        // Extract the uuid of the user we just created.
        String location = response.getMetadata().get("Location").get(0).toString();
        String uuid = location.substring(location.lastIndexOf("/") + 1);
        log.info("User created: " + uuid);

        return uuid;
    }

    public String createRestaurantAdmin(UserRepresentation userRepresentation) throws InstanceAlreadyExistsException {
        if (CollectionUtils.isEmpty(userRepresentation.getGroups())) {
            userRepresentation.setGroups(Arrays.asList(GROUP_RES_ADMIN));
        }

        RealmResource realm = getMasterKeycloakInstance().realm(realmName);
        Response response = realm.users().create(userRepresentation);

        if (response.getStatus() == 409) {
            log.info("User {} already exists.", userRepresentation.getUsername());
            throw new InstanceAlreadyExistsException();
        }

        if (response.getStatus() < 200 || response.getStatus() > 299) {
            String error = "User create error: " + response.readEntity(String.class);
            log.error(error);
            throw new RuntimeException(error);
        }

        // Extract the uuid of the user we just created.
        String location = response.getMetadata().get("Location").get(0).toString();
        String uuid = location.substring(location.lastIndexOf("/") + 1);
        log.info("Restaurant Admin created: " + uuid);

        return uuid;
    }

    public void updateUser(UserRepresentation userRepresentation) {
        UserResource userResource = getMasterKeycloakInstance()
                .realm(realmName)
                .users()
                .get(userRepresentation.getId());

        UserRepresentation curr = userResource.toRepresentation();
        curr.setEmail(userRepresentation.getEmail());
        curr.setFirstName(userRepresentation.getFirstName());
        curr.setLastName(userRepresentation.getLastName());
        Map<String, List<String>> attributes = curr.getAttributes();
        attributes = attributes == null ? new HashMap<>() : attributes;
        attributes.putAll(userRepresentation.getAttributes());
        curr.setAttributes(attributes);

        userResource.update(curr);
        log.info("User updater: " + curr.getId());
    }

    public int usersCount() {
        return getMasterKeycloakInstance().realm(realmName).users().count();
    }

    public AccessTokenResponse login(String username, String password) {
        try {
            Keycloak kc = Keycloak.getInstance(serverUrl, realmName, username, password, clientId, clientSecret);
            return kc.tokenManager().getAccessToken();
        } catch (NotAuthorizedException e) {
            log.error("login error", e);
            throw new ApiException(401, "login.error");
        }
    }

    public void logout(String userId) {
        getMasterKeycloakInstance().realm(realmName).users().get(userId).logout();
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        String url = serverUrl + "/realms/" + realmName + "/protocol/openid-connect/token";
        Configuration kcConfig = new Configuration(serverUrl, realmName, clientId, null, null);
        Http http = new Http(kcConfig, (params, headers) -> {
        });
        try {
            return http.<AccessTokenResponse>post(url)
                    .authentication()
                    .client()
                    .form()
                    .param("grant_type", "refresh_token")
                    .param("refresh_token", refreshToken)
                    .param("client_id", clientId)
                    .param("client_secret", clientSecret)
                    .response()
                    .json(AccessTokenResponse.class)
                    .execute();
        } catch (HttpResponseException ex) {
            if (ex.toString().contains("Session not active")) {
                throw new ApiException(401, "user.not-logged-in");
            }
            throw ex;
        }
    }

    public void resetPassword(String username, String password) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(password);

        RealmResource realm = getMasterKeycloakInstance().realm(realmName);

        String id = realm.users().search(username, true).get(0).getId();
        realm.users().get(id).resetPassword(credentialRepresentation);

        UserResource userResource = realm.users().get(id);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.getAttributes().put("passwordResetRequired", Arrays.asList("false"));
        userResource.update(userRepresentation);
    }

    public AccessTokenResponse impersonate(String username) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", clientId);
        map.add("client_secret", clientSecret);
        map.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
        map.add("requested_subject", username);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AccessTokenResponse> response = restTemplate.postForEntity(serverUrl + "/realms/" + realmName + "/protocol/openid-connect/token", request, AccessTokenResponse.class);

        return response.getBody();
    }
}
