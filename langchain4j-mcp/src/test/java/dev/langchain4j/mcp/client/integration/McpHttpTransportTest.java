package dev.langchain4j.mcp.client.integration;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class McpHttpTransportTest extends McpTransportTest {

    private static final Logger log = LoggerFactory.getLogger(McpHttpTransportTest.class);
    private static Process process;

    @BeforeAll
    public static void setup() throws IOException, InterruptedException, TimeoutException {
        String path = ClassLoader.getSystemResource("tools_mcp_server.java").getFile();
        String[] command = new String[] {"jbang", "--quiet", "--fresh", "run", path};
        log.info("Starting the MCP server using command: " + Arrays.toString(command));
        process = new ProcessBuilder()
                .command("jbang", "--quiet", "--fresh", "run", path)
                .inheritIO()
                .start();
        log.info("MCP server has started");
        waitForPort(8080, 120);
        McpTransport transport = new HttpMcpTransport.Builder()
                .sseUrl("http://localhost:8080/mcp/sse")
                .logRequests(true)
                .logResponses(true)
                .build();
        mcpClient = new DefaultMcpClient.Builder()
                .transport(transport)
                .toolExecutionTimeout(Duration.ofSeconds(4))
                .build();
    }

    private static void waitForPort(int port, int timeoutSeconds) throws InterruptedException, TimeoutException {
        Request request = new Request.Builder().url("http://localhost:" + port).build();
        long start = System.currentTimeMillis();
        OkHttpClient client = new OkHttpClient();
        while (System.currentTimeMillis() - start < timeoutSeconds * 1000) {
            try {
                client.newCall(request).execute();
                return;
            } catch (IOException e) {
                TimeUnit.SECONDS.sleep(1);
            }
        }
        throw new TimeoutException("Port " + port + " did not open within " + timeoutSeconds + " seconds");
    }

    @AfterAll
    public static void teardown() throws Exception {
        if (mcpClient != null) {
            mcpClient.close();
        }
        if (process.isAlive()) {
            process.destroyForcibly();
        }
    }
}