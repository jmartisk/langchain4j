///usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS io.quarkus:quarkus-bom:${quarkus.version:3.17.5}@pom
//DEPS io.quarkiverse.mcp:quarkus-mcp-server-stdio:999-SNAPSHOT
//DEPS io.quarkiverse.mcp:quarkus-mcp-server-sse:999-SNAPSHOT

package jb;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;

public class tools_mcp_server {

    @Tool(description = "Echoes a string")
    public String echoString(@ToolArg(description = "The string to be echoed") String input) {
        return input;
    }

    @Tool(description = "Echoes an integer")
    public String echoInteger(@ToolArg(description = "The integer to be echoed") Integer input) {
        return String.valueOf(input);
    }

    @Tool(description = "Echoes a boolean")
    public String echoBoolean(@ToolArg(description = "The boolean to be echoed") Boolean input) {
        return Boolean.valueOf(input).toString();
    }

    @Tool(description = "Takes 10 seconds to complete")
    public String longOperation() throws Exception {
        TimeUnit.SECONDS.sleep(10);
        return "ok";
    }

}