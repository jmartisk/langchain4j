package dev.langchain4j.mcp.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.McpToolExecutor;
import dev.langchain4j.mcp.client.protocol.McpCallToolRequest;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import org.junit.jupiter.api.Test;
import java.util.List;

public class GG {

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    public void test() throws JsonProcessingException {
        StdioMcpTransport.Builder transport = new StdioMcpTransport.Builder()
                .logEvents(true)
                .command(List.of("uvx", "mcp-email-server", "stdio"));
        DefaultMcpClient client = new DefaultMcpClient.Builder()
                .transport(transport.build())
                .build();
        try {
//            ToolSpecification sendEmailTool = client.listTools().stream().filter(tool -> tool.name().equals("send_email")).findFirst().get();
//            System.out.println(sendEmailTool.parameters());
            McpToolExecutor toolExecutor = new McpToolExecutor(client);
            //JSON
//            String args = """
//                    {
//                      "to": "user@example.com",
//                      "subject": "Test",
//                      "body": "Content",
//                      "cc": null,
//                      "bcc": null
//                    }
//                    """;
            String args = """
                    {
                      "recipients": ["user@example.com"],
                      "account_name": "account1",
                      "subject": "Test",
                      "body": "Content",
                      "cc": null,
                      "bcc": null
                    }
                    """;
            String result = toolExecutor.execute(ToolExecutionRequest.builder()
                    .arguments(args)
                    .id("1")
                    .name("send_email")
                    .build(), null);
            System.out.println(result);
        } finally {
            client.close();
        }

    }
}
