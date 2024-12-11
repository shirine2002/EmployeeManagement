package org.gestionwebservice.Domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;



@Entity
@Table(name= "user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", updatable = false, nullable = false)
    private Long authId ;
  
    @Column(name= "email" , nullable = false, unique = true) 
    private String email ; 

    @Column(name= "password" , nullable = false) 
    private String password ;


    public Long getAuthid() {
        return authId;
    }

    public void setAuthid(Long authId) {
        this.authId = authId;
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
