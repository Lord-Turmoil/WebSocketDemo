package top.tony.wsdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import top.tony.wsdemo.models.WebSocketPayload;
import top.tony.wsdemo.services.WebSocketSessionService;

import java.io.IOException;
import java.net.URI;

@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {
    private final WebSocketSessionService sessionService;

    public WebSocketHandler(WebSocketSessionService sessionService) {
        this.sessionService = sessionService;
    }

    /**
     * Invoked when a new WebSocket message arrives.
     *
     * @param session WebSocket session.
     * @param message WebSocket message.
     * @throws IOException IOException.
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        session.sendMessage(WebSocketPayload.message("Received: " + message.getPayload()).toTextMessage());
    }

    /**
     * Handle new connection. The websocket uri should be in the format of /ws/{username}.
     *
     * @param session WebSocket session.
     * @throws Exception Exception.
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        URI uri = session.getUri();
        if (uri == null) {
            session.close();
            log.error("Request uri is null!");
            return;
        }
        int pos = uri.toString().lastIndexOf('/');
        if (pos == -1) {
            session.close();
            log.error("Invalid uri: {}", uri);
            return;
        }
        String username = uri.toString().substring(pos + 1);
        session.sendMessage(WebSocketPayload.message("Welcome back, commander " + username + ".").toTextMessage());

        sessionService.addSession(session, username);
    }

    /**
     * Invoked after a WebSocket connection is closed.
     *
     * @param session WebSocket session.
     * @param status  Close status.
     * @throws Exception Exception.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Connection closed: {} status = {}", session, status);
        sessionService.removeSession(session);
    }

    /**
     * Handle transport error.
     *
     * @param webSocketSession WebSocket session.
     * @param throwable        Throwable.
     * @throws Exception Exception.
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }
        log.error("Transport error: {}", throwable.getMessage());
        sessionService.removeSession(webSocketSession);
    }
}
