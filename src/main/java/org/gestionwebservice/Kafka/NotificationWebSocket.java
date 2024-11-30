package org.gestionwebservice.Kafka;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import jakarta.enterprise.context.ApplicationScoped;

@ServerEndpoint("/employee-events")
@ApplicationScoped
public class NotificationWebSocket {

    private final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        sessions.remove(session);
        Logger.getLogger(NotificationWebSocket.class.getName())
                .log(Level.SEVERE, "WebSocket error", throwable);
    }

    public void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    Logger.getLogger(NotificationWebSocket.class.getName())
                          .info("Broadcasting message to session: " + session.getId() + " - Message: " + message);
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    Logger.getLogger(NotificationWebSocket.class.getName())
                          .log(Level.SEVERE, "Failed to send message to WebSocket", e);
                }
            }
        }
    }
    
}
