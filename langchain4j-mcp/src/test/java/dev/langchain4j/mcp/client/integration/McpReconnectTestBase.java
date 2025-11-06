package dev.langchain4j.mcp.client.integration;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static dev.langchain4j.mcp.client.integration.McpServerHelper.startServerHttp;
import static org.assertj.core.api.Assertions.assertThat;

public class McpReconnectTestBase {

    static Process process;

    static DefaultMcpClient mcpClient;

    @Test
    void reconnect() throws IOException, TimeoutException, InterruptedException {
        executeAToolAndAssertSuccess();

        // kill the server and restart it
        process.destroy();
        process = startServerHttp("tools_mcp_server.java");

        // give the MCP client some time to reconnect
        Thread.sleep(5_000);

        executeAToolAndAssertSuccess();
    }

    private void executeAToolAndAssertSuccess() {
        ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                .name("echoString")
                .arguments("{\"input\": \"abc\"}")
                .build();
        String result = mcpClient.executeTool(toolExecutionRequest).resultText();
        assertThat(result).isEqualTo("abc");
    }

}
