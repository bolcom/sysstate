package nl.unionsoft.sysstate.server;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class SysStateServerServiceImpl {

    private static final String WAR = "src/main/webapp";
    public static final String CONNECTOR_HOST = "localhost";
    public static final int CONNECTOR_PORT = 8680;

    public static final String CONTEXT_PATH = "/";

    public static void main(final String[] args) throws Exception {

        Server server = new Server();


        final WebAppContext webappcontext = new WebAppContext();
        webappcontext.setContextPath(CONTEXT_PATH);
        webappcontext.setWar(WAR);

        final HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] { webappcontext, new DefaultHandler() });
        server.setHandler(handlers);

        final ServerConnector connector = new ServerConnector(server);
        connector.setPort(CONNECTOR_PORT);
        server.setConnectors(new Connector[] {connector});
        
        server.start();
        server.join();
        
    }
}
