package org.gestionwebservice.Repository;
import org.gestionwebservice.Domain.Employee;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;



@ApplicationScoped
public class EmployeeRepository implements PanacheRepository<Employee> {
    
}