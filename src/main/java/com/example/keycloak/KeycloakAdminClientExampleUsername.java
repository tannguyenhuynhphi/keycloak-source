package com.example.keycloak;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;

public class KeycloakAdminClientExampleUsername {

    private Keycloak keycloak;
    private String serverUrl = "http://172.16.3.15:9999/auth";
    private String realm = "ilogic-pt";
    // idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
    private String clientId = "admin-cli";
    private String clientSecret = "rQ44EJMOhCayllXcvFNqhY0hrxVwv7Np";

    public KeycloakAdminClientExampleUsername() {
        this.keycloak = keycloak();
    }

    private Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();
    }

    public RealmResource realm() {
        return keycloak.realm(realm);
    }

    private UserResource getUserResource(String username) {
        UserRepresentation userRepresentation = findUser(username);
        return realm().users().get(userRepresentation.getId());
    }

    private UserRepresentation findUser(String username) {
        List<UserRepresentation> users = realm().users().search(username);
        return users.get(0);
    }

    private void addUser(RealmResource realm, KeyCloakUserDto userDto) {
        UserRepresentation user = new UserRepresentation();
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(userDto.getPassword());
        credential.setTemporary(false);
        user.setEnabled(userDto.getEnabled());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setCredentials(Arrays.asList(credential));
        realm.users().create(user);
    }

    private void updatePassword(String username, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        UserResource userResource = getUserResource(username);
        userResource.resetPassword(passwordCred);
    }

    private void updateUser(String username, Boolean enabled ) {
        UserRepresentation user = findUser(username);
        user.setEnabled(enabled);
        UserResource userResource = getUserResource(username);
        userResource.update(user);
    }

    private void addRole(String username, List<String> roles) {
        List<RoleRepresentation> listRealmRole = new ArrayList<>();
        for(String role: roles ){
            listRealmRole.add(realm().roles().get(role).toRepresentation());
        }
        getUserResource(username).roles().realmLevel().remove(getUserResource(username).roles().realmLevel().listAll());
        getUserResource(username).roles().realmLevel().add(listRealmRole);
    }
    private void removeRole(String username, List<String> roles) {
        List<RoleRepresentation> listRealmRole = new ArrayList<>();
        for(String role: roles ){
            listRealmRole.add(realm().roles().get(role).toRepresentation());
        }
        getUserResource(username).roles().realmLevel().remove(listRealmRole);
    }

    public static void main(String[] args) {
        KeycloakAdminClientExampleUsername keycloakAdminClientExampleUsername = new KeycloakAdminClientExampleUsername();
        KeyCloakUserDto userNew = new KeyCloakUserDto();
        RealmResource realm = keycloakAdminClientExampleUsername.realm();
        userNew.setUsername("tclient1");
        userNew.setFirstName("First client1");
        userNew.setLastName("Last client1");
        userNew.setEmail("tom+tester1432client1@tdlabs.local");
        userNew.setPassword("1234567");
        userNew.setEnabled(true);
        List<String> roles = new ArrayList<>();
        roles.add("USER_ROLE");
        roles.add("tester");
//        keycloakAdminClientExampleUsername.addUser(realm,userNew);
//        keycloakAdminClientExampleUsername.addRole("tclient1",roles);
//        keycloakAdminClientExampleUsername.removeRole("tclient1",roles);
//        keycloakAdminClientExampleUsername.updatePassword("tclient1","nguyentan");
    }

}
