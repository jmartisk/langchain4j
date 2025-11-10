/// usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS dev.langchain4j:langchain4j-mcp:1.9.0-beta16-SNAPSHOT
//DEPS org.tinylog:slf4j-tinylog:2.7.0
//DEPS org.tinylog:tinylog-impl:2.7.0

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.websocket.WebSocketMcpTransport;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProviderResult;


class client {

    public static void main(String[] args) throws InterruptedException {
        McpTransport transport = new WebSocketMcpTransport.Builder()
                .url("ws://localhost:8080/mcp/ws")
                .logRequests(true)
                .logResponses(true)
                .build();
        DefaultMcpClient mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofSeconds(4))
                .build();
        // execute the 'longRunningOperation' tool

        McpToolProvider toolProvider = McpToolProvider.builder().mcpClients(mcpClient).build();
        ToolProviderResult toolProviderResult = toolProvider.provideTools(null);

        ToolExecutor longOperationExecutor = toolProviderResult.toolExecutorByName("longOperation");
        // this operation takes 10 seconds, but we set a timeout of 4 seconds, so it should be cancelled
        longOperationExecutor.execute(ToolExecutionRequest.builder().arguments("{}").build(), null);

        // "wasCancellationReceived" tool should now return true, but instead, this tool itself times out because the server waits for longOperation to normally finish first
        ToolExecutor wasCancellationReceivedExecutor = toolProviderResult.toolExecutorByName("wasCancellationReceived");
        String wasCancelled = wasCancellationReceivedExecutor.execute(ToolExecutionRequest.builder().arguments("{}").build(), null);
        System.out.println(wasCancelled);

        System.out.println("sleeping...");
        TimeUnit.SECONDS.sleep(10);

    }

}

