package org.gestionwebservice.Kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.gestionwebservice.Domain.Employee;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmployeeEventProducer {

    @Inject
    @Channel("employee-notifications")
    Emitter<String> emitter;

    @Inject
    ObjectMapper objectMapper;

    private static final Logger LOGGER = Logger.getLogger(EmployeeEventProducer.class.getName());

    public void sendEmployeeEvent(String action, Employee employee) {
        try {
            String event = objectMapper.writeValueAsString(new EmployeeEvent(action, employee));
            emitter.send(event)
                    .whenComplete((success, failure) -> {
                        if (failure != null) {
                            LOGGER.log(Level.SEVERE, "Failed to send Kafka event", failure);
                        } else {
                            LOGGER.info("Event sent successfully: " + event);
                        }
                    });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error serializing employee event", e);
        }
    }

    public static class EmployeeEvent {
        private final String action;
        private final Employee employee;

        public EmployeeEvent(String action, Employee employee) {
            this.action = action;
            this.employee = employee;
        }

        public String getAction() {
            return action;
        }

        public Employee getEmployee() {
            return employee;
        }
    }
}
