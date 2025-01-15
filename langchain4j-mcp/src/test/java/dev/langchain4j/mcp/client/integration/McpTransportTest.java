package dev.langchain4j.mcp.client.integration;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.model.chat.request.json.JsonBooleanSchema;
import dev.langchain4j.model.chat.request.json.JsonIntegerSchema;
import dev.langchain4j.model.chat.request.json.JsonStringSchema;
import dev.langchain4j.service.tool.ToolExecutor;
import dev.langchain4j.service.tool.ToolProvider;
import dev.langchain4j.service.tool.ToolProviderResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

public abstract class McpTransportTest {

    static McpClient mcpClient;

    @Test
    public void listTools() {
        // obtain a list of tools from the MCP server
        ToolProvider toolProvider =
                McpToolProvider.builder().mcpClients(List.of(mcpClient)).build();
        ToolProviderResult toolProviderResult = toolProvider.provideTools(null);

        Map<ToolSpecification, ToolExecutor> tools = toolProviderResult.tools();
        assertThat(tools.size()).isEqualTo(4);

        ToolSpecification echoString = findToolByName(toolProviderResult, "echoString");
        assertThat(echoString.description()).isEqualTo("Echoes a string");
        JsonStringSchema echoStringParam =
                (JsonStringSchema) echoString.parameters().properties().get("input");
        assertThat(echoStringParam.description()).isEqualTo("The string to be echoed");

        ToolSpecification echoInteger = findToolByName(toolProviderResult, "echoInteger");
        assertThat(echoInteger.description()).isEqualTo("Echoes an integer");
        JsonIntegerSchema echoIntegerParam =
                (JsonIntegerSchema) echoInteger.parameters().properties().get("input");
        assertThat(echoIntegerParam.description()).isEqualTo("The integer to be echoed");

        ToolSpecification echoBoolean = findToolByName(toolProviderResult, "echoBoolean");
        assertThat(echoBoolean.description()).isEqualTo("Echoes a boolean");
        JsonBooleanSchema echoBooleanParam =
                (JsonBooleanSchema) echoBoolean.parameters().properties().get("input");
        assertThat(echoBooleanParam.description()).isEqualTo("The boolean to be echoed");

        ToolSpecification longOperation = findToolByName(toolProviderResult, "longOperation");
        assertThat(longOperation.description()).isEqualTo("Takes 10 seconds to complete");
        assertThat(longOperation.parameters().properties()).isEmpty();
    }

    @Test
    public void executingTools() {
        ToolProvider toolProvider =
                McpToolProvider.builder().mcpClients(List.of(mcpClient)).build();
        ToolProviderResult toolProviderResult = toolProvider.provideTools(null);

        // find the 'echoString' tool and execute it on the MCP server
        ToolExecutor executor = toolProviderResult.tools().entrySet().stream()
                .filter(entry -> entry.getKey().name().equals("echoString"))
                .findFirst()
                .get()
                .getValue();
        ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                .name("echoString")
                .arguments("{\"input\": \"abc\"}")
                .build();
        String toolExecutionResultString = executor.execute(toolExecutionRequest, null);

        // validate the tool execution result
        assertThat(toolExecutionResultString).isEqualTo("abc");
    }

    @Test
    public void timeout() {
        ToolProvider toolProvider =
                McpToolProvider.builder().mcpClients(List.of(mcpClient)).build();
        ToolProviderResult toolProviderResult = toolProvider.provideTools(null);

        // find the 'longOperation' tool and execute it on the MCP server
        ToolExecutor executor = toolProviderResult.tools().entrySet().stream()
                .filter(entry -> entry.getKey().name().equals("longOperation"))
                .findFirst()
                .get()
                .getValue();
        ToolExecutionRequest toolExecutionRequest = ToolExecutionRequest.builder()
                .name("longOperation")
                .arguments("{}")
                .build();
        String toolExecutionResultString = executor.execute(toolExecutionRequest, null);

        // validate the tool execution result
        assertThat(toolExecutionResultString).isEqualTo("There was a timeout executing the tool");
    }

    private ToolSpecification findToolByName(ToolProviderResult toolProviderResult, String name) {
        return toolProviderResult.tools().keySet().stream()
                .filter(toolSpecification -> toolSpecification.name().equals(name))
                .findFirst()
                .get();
    }
}
