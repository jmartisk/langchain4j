package dev.langchain4j.mcp.client.transport.docker;

import dev.langchain4j.mcp.client.DefaultMcpClient;
import java.time.Duration;

public class TESTING {

    public static void main(String[] args) {
        DockerMcpTransport transport = new DockerMcpTransport.Builder()
                .host("unix:///run/user/1000/podman/podman.sock")
                .image("docker.io/mcp/fetch")
                .logEvents(true)
                .build();
        DefaultMcpClient client = new DefaultMcpClient.Builder()
                .transport(transport)
                .reconnectInterval(Duration.ZERO)
                .autoHealthCheck(false)
                .build();

        System.out.println(client.listTools());
    }
}
