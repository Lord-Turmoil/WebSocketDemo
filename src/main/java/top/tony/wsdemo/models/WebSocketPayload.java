package top.tony.wsdemo.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.Data;
import org.springframework.web.socket.TextMessage;

@Data
public class WebSocketPayload<TData> {
    private static final ObjectWriter WRITER = new ObjectMapper().writer();
    private String type;
    private TData data;

    public WebSocketPayload(String type, TData data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        try {
            return WRITER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return "Invalid WebSocketPayload";
        }
    }

    public TextMessage toTextMessage() {
        return new TextMessage(toString());
    }

    public static WebSocketPayload<String> message(String message) {
        return new WebSocketPayload<>("message", message);
    }

    public static <TData> WebSocketPayload<TData> of(String type, TData data) {
        return new WebSocketPayload<>(type, data);
    }
}
