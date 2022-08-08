package com.example.keycloak;


import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

class KeyCloakUserDto {
    private String id;
    private String username;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

public class KeycloakAdminClientExample {

    private Keycloak keycloak;
    private String serverUrl = "http://172.16.3.15:9999/auth";
    private String realm = "ilogic-pt";
    // idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
    private String clientId = "admin-cli";
    private String clientSecret = "rQ44EJMOhCayllXcvFNqhY0hrxVwv7Np";

    public KeycloakAdminClientExample() {
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

    private UserResource findUser(String userId) {
//        List<UserRepresentation> userResources = realm().users().search(username);
        UserResource userResources = realm().users().get(userId);
//        UserRepresentation userResource  = new UserRepresentation();
//        for( UserRepresentation user : userResources){
//            userResource = user;
//            break;
//        }
        return userResources;
    }

    private Response addUser(RealmResource realm, KeyCloakUserDto userDto) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(userDto.getEnabled());
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());
//        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
        // Create user (requires manage-users role)
        Response response = realm.users().create(user);
        System.out.println(response);
        return response;
    }

    private void updatePassword(String userId, String password) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        UserResource userResource = findUser(userId);
        userResource.resetPassword(passwordCred);
    }

    private void addRole(String userId, String role) {
        RoleRepresentation testerRealmRole = realm().roles().get(role).toRepresentation();
        findUser(userId).roles().realmLevel().add(Arrays.asList(testerRealmRole));
    }
 public static void main(String[] args) {
     KeycloakAdminClientExample keycloakAdminClientExample = new KeycloakAdminClientExample();
     KeyCloakUserDto userNew = new KeyCloakUserDto();
     RealmResource realm = keycloakAdminClientExample.realm();
     userNew.setUsername("tester4");
     userNew.setFirstName("First");
     userNew.setLastName("Last");
     userNew.setEmail("tom+tester12@tdlabs.local");
     userNew.setEnabled(true);
     Response response = keycloakAdminClientExample.addUser(realm,userNew);
     String userId = CreatedResponseUtil.getCreatedId(response);
     keycloakAdminClientExample.updatePassword(userId,"123456");
     keycloakAdminClientExample.addRole(userId,"tester");
 }
//    public static void main(String[] args) {
//
//        String serverUrl = "http://172.16.3.15:9999/auth";
//        String realm = "ilogic-pt";
//        // idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
//        String clientId = "admin-cli";
//        String clientSecret = "rQ44EJMOhCayllXcvFNqhY0hrxVwv7Np";
//
////		// Client "idm-client" needs service-account with at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
////		Keycloak keycloak = KeycloakBuilder.builder() //
////				.serverUrl(serverUrl) //
////				.realm(realm) //
////				.grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
////				.clientId(clientId) //
////				.clientSecret(clientSecret).build();
//
//        // User "idm-admin" needs at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
//        Keycloak keycloak = KeycloakBuilder.builder() //
//                .serverUrl(serverUrl) //
//                .realm(realm) //
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
//                .clientId(clientId) //
//                .clientSecret(clientSecret) //
//                .build();
////                .username("phitan") //
////                .password("123456")
//
//
//        // Define user
//
//
//        // Get realm
//
//        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
//        System.out.println(response.getLocation());
//        String userId = CreatedResponseUtil.getCreatedId(response);
//
//        System.out.printf("User created with userId: %s%n", userId);
//
//        // Define password credential
//
//
////        // Get realm role "tester" (requires view-realm role)
//
////
////        // Get client
//        ClientRepresentation app1Client = realmResource.clients() //
//                .findByClientId("ilogic-pt-password-credentials").get(0);
////
////        // Get client level role (requires view-clients role)
//        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()) //
//                .roles().get("ADMIN").toRepresentation();
////
////        // Assign client level role to user
//        userResource.roles() //
//                .clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
//
//        // Send password reset E-Mail
//        // VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD, TERMS_AND_CONDITIONS
////        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
//
//        // Delete User
////        userResource.remove();
//    }

//    public static void main(String[] args) {
//
//        String serverUrl = "http://172.16.3.15:9999/auth";
//        String realm = "ilogic-pt";
//        // idm-client needs to allow "Direct Access Grants: Resource Owner Password Credentials Grant"
//        String clientId = "admin-cli";
//        String clientSecret = "rQ44EJMOhCayllXcvFNqhY0hrxVwv7Np";
//
////		// Client "idm-client" needs service-account with at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
////		Keycloak keycloak = KeycloakBuilder.builder() //
////				.serverUrl(serverUrl) //
////				.realm(realm) //
////				.grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
////				.clientId(clientId) //
////				.clientSecret(clientSecret).build();
//
//        // User "idm-admin" needs at least "manage-users, view-clients, view-realm, view-users" roles for "realm-management"
//        Keycloak keycloak = KeycloakBuilder.builder() //
//                .serverUrl(serverUrl) //
//                .realm(realm) //
//                .grantType(OAuth2Constants.CLIENT_CREDENTIALS) //
//                .clientId(clientId) //
//                .clientSecret(clientSecret) //
//                .build();
////                .username("phitan") //
////                .password("123456")
//
//
//        // Define user
//        UserRepresentation user = new UserRepresentation();
//        user.setEnabled(true);
//        user.setUsername("tester4");
//        user.setFirstName("First");
//        user.setLastName("Last");
//        user.setEmail("tom+tester12@tdlabs.local");
//        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
//
//        // Get realm
//        RealmResource realmResource = keycloak.realm(realm);
//        UsersResource usersRessource = realmResource.users();
//
//        // Create user (requires manage-users role)
//        Response response = usersRessource.create(user);
//        System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
//        System.out.println(response.getLocation());
//        String userId = CreatedResponseUtil.getCreatedId(response);
//
//        System.out.printf("User created with userId: %s%n", userId);
//
//        // Define password credential
//        CredentialRepresentation passwordCred = new CredentialRepresentation();
//        passwordCred.setTemporary(false);
//        passwordCred.setType(CredentialRepresentation.PASSWORD);
//        passwordCred.setValue("123456");
//
//        UserResource userResource = usersRessource.get(userId);
//
//        // Set password credential
//        userResource.resetPassword(passwordCred);
//
////        // Get realm role "tester" (requires view-realm role)
//        RoleRepresentation testerRealmRole = realmResource.roles()//
//                .get("tester").toRepresentation();
////
////        // Assign realm role tester to user
//        userResource.roles().realmLevel() //
//                .add(Arrays.asList(testerRealmRole));
////
////        // Get client
//        ClientRepresentation app1Client = realmResource.clients() //
//                .findByClientId("ilogic-pt-password-credentials").get(0);
////
////        // Get client level role (requires view-clients role)
//        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()) //
//                .roles().get("ADMIN").toRepresentation();
////
////        // Assign client level role to user
//        userResource.roles() //
//                .clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
//
//        // Send password reset E-Mail
//        // VERIFY_EMAIL, UPDATE_PROFILE, CONFIGURE_TOTP, UPDATE_PASSWORD, TERMS_AND_CONDITIONS
////        usersRessource.get(userId).executeActionsEmail(Arrays.asList("UPDATE_PASSWORD"));
//
//        // Delete User
////        userResource.remove();
//    }
}
