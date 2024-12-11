package org.gestionwebservice.Repository;

import org.gestionwebservice.Domain.User;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository  implements PanacheRepository<User>{

    public User findByEmail(String email) {
    return find("email", email).firstResult(); 
    }
}
