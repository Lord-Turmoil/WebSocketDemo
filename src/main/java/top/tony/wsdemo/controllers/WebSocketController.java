package top.tony.wsdemo.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tony.wsdemo.models.WebSocketPayload;
import top.tony.wsdemo.services.WebSocketSessionService;

@RestController
@RequestMapping("/api")
public class WebSocketController {
    private final WebSocketSessionService sessionService;

    public WebSocketController(WebSocketSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/send/{username}")
    public String send(@PathVariable String username) {
        sessionService.sendMessage(username, WebSocketPayload.message("Hello, " + username + "!"));
        return "WebSocket message sent.";
    }
}
