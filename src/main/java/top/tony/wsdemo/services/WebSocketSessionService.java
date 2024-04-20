package top.tony.wsdemo.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import top.tony.wsdemo.models.WebSocketPayload;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class WebSocketSessionService {

    private final Map<WebSocketSession, String> sessionToUsername = new ConcurrentHashMap<>();

    public void addSession(WebSocketSession session, String username) {
        sessionToUsername.put(session, username);
    }

    public void removeSession(WebSocketSession session) {
        sessionToUsername.remove(session);
    }

    /**
     * Send message to user with username.
     *
     * @param username User username.
     * @param payload  Message payload.
     */
    public void sendMessage(String username, WebSocketPayload<?> payload) {
        try {
            for (Map.Entry<WebSocketSession, String> entry : sessionToUsername.entrySet()) {
                if (entry.getValue().equals(username)) {
                    entry.getKey().sendMessage(payload.toTextMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to send message to user: {}", username, e);
        }
    }
}
