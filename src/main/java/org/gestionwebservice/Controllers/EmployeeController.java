package org.gestionwebservice.Controllers;

import java.util.List;

import org.gestionwebservice.Domain.Employee;
import org.gestionwebservice.Kafka.EmployeeEventProducer;
import org.gestionwebservice.Repository.EmployeeRepository;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/employees")
public class EmployeeController {

    @Inject
    EmployeeRepository employeeRepository;

    @Inject
    EmployeeEventProducer eventProducer;

    @GET
    public List<Employee> getAllEmployees() {
        return employeeRepository.listAll();
    }

    @GET
    @PermitAll  //there are no roles defined here anyone can access it 
    @Path("/{id}")
    public Response getEmployeeById(@PathParam("id") Long id) {
        return employeeRepository.findByIdOptional(id)
                .map(employee -> Response.ok(employee).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @RolesAllowed("admin")
    @Transactional
    public Response createEmployee(Employee employee) {
        employeeRepository.persist(employee);
        eventProducer.sendEmployeeEvent("CREATE", employee); //its going to send the producer this event after succesfully dealing with the database 
        return Response.status(Response.Status.CREATED).entity(employee).build();
    }

    @PUT
    @RolesAllowed("admin")
    @Path("/{id}")
    @Transactional
    public Response updateEmployee(@PathParam("id") Long id, Employee updatedEmployee) {
        Employee employee = employeeRepository.findById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        employee.setName(updatedEmployee.getName());
        employee.setDepartment(updatedEmployee.getDepartment());
        employee.setTitle(updatedEmployee.getTitle());
        eventProducer.sendEmployeeEvent("UPDATE", employee);
        return Response.ok(employee).build();
    }

    @DELETE
    @RolesAllowed("admin")
    @Path("/{id}")
    @Transactional
    public Response deleteEmployee(@PathParam("id") Long id) {
        Employee employee = employeeRepository.findById(id);
        if (employee == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        employeeRepository.delete(employee);
        eventProducer.sendEmployeeEvent("DELETE", employee);
        return Response.noContent().build();
    }
}
