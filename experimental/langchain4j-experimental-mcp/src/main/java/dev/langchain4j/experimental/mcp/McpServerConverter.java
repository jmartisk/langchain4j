package dev.langchain4j.experimental.mcp;

import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.registryclient.model.McpServer;

/**
 * This class can be used to attempt to automatically convert MCP server definitions
 * retrieved from an MCP registry into instances of McpTransport.
 *
 * <b>IMPORTANT WARNING:</b> Never use an MCP server without first checking that it can be trusted (especially
 * ones that you run by yourself). Otherwise,
 * it is very easy to fall victim to supply chain attacks.
 */
public class McpServerConverter {

    public McpTransport toTransport(McpServer server,)
}
