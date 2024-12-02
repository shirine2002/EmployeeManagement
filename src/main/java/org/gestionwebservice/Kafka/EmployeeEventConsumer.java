package org.gestionwebservice.Kafka;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.microprofile.reactive.messaging.Incoming;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmployeeEventConsumer {

    private static final Logger LOGGER = Logger.getLogger(EmployeeEventConsumer.class.getName());

    @Inject
    NotificationWebSocket webSocket;

    @Incoming("employee-notifications-in")
    public void consume(String message) {
        try {
            LOGGER.info("Received Kafka event: " + message);
            webSocket.broadcast(message);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to process Kafka event", e);
        }
    }
}
