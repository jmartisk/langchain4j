package dev.langchain4j.mcp.client.transport.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.mcp.client.transport.McpOperationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletionStage;

import static dev.langchain4j.mcp.client.transport.websocket.WebSocketMcpTransport.OBJECT_MAPPER;

public class WebSocketMcpListener implements WebSocket.Listener {

    private final McpOperationHandler operationHandler;
    private final Logger trafficLogger;
    private final boolean logResponses;
    private final Runnable onCloseCallback;
    private final Logger logger = LoggerFactory.getLogger(WebSocketMcpListener.class);


    public WebSocketMcpListener(McpOperationHandler operationHandler,
                                Logger trafficLogger,
                                boolean logResponses,
                                Runnable onCloseCallback) {
        this.operationHandler = operationHandler;
        this.trafficLogger = trafficLogger;
        this.logResponses = logResponses;
        this.onCloseCallback = onCloseCallback;
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        webSocket.request(1);
        logger.debug("Websocket connection opened");
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        webSocket.request(1);
        if (logResponses) {
            trafficLogger.info("< " + data);
        }
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(data.toString());
            operationHandler.handle(jsonNode);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse JSON message: {}", data, e);
        }
        return null;
    }

    @Override
    public CompletionStage<?> onPing(WebSocket webSocket, ByteBuffer message) {
        return WebSocket.Listener.super.onPing(webSocket, message);
    }

    @Override
    public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
        logger.debug("Websocket connection closed with status {} and reason: {}", statusCode, reason);
        // Consider all running operations failed - the websocket transport does not have recovery capabilities ATM
        operationHandler.cancelAllPendingOperations();
        onCloseCallback.run();
        return null;
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        logger.warn("WebSocket error", error);
    }

    @Override
    public CompletionStage<?> onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
        webSocket.request(1);
        logger.warn("Received binary data, this is not supported");
        return null;
    }


}
