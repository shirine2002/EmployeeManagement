package org.gestionwebservice.Services;

import java.util.HashSet;
import java.util.Set;

import org.gestionwebservice.Domain.User;
import org.gestionwebservice.Repository.UserRepository;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AuthenticationService {

    private final UserRepository userRepository; 
    private final TokenService tokenService;

    public AuthenticationService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService ;
    }

    public String authenticateUser(String email, String password) {
    User user = userRepository.findByEmail(email);
    if (user != null && user.getPassword().equals(password)) {
        Set<String> roles = determineRoles(user); // Determine roles for the user
        try {
            return tokenService.generateToken(email, roles, "src/main/java/org/gestionwebservice/privateKey.pem");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    return null; // if the authnetication fails 
}

     private Set<String> determineRoles(User user) {
        Set<String> roles = new HashSet<>();

        if (user.getEmail().endsWith("@admin.com")) {
            roles.add("admin");  
        } else {
            roles.add("staff");  
        }

        return roles;
    }
}
