package dev.langchain4j.mcp.client.integration;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class McpStdioTransportIT extends McpTransportTestBase {

    @BeforeAll
    public static void setup() {
        String path = ClassLoader.getSystemResource("tools_mcp_server.java").getFile();
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of("jbang", "--quiet", "--fresh", "run", path))
                .logEvents(true)
                .build();
        mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofSeconds(4))
                .build();
    }

    @AfterAll
    public static void teardown() throws Exception {
        if (mcpClient != null) {
            mcpClient.close();
        }
    }
}
