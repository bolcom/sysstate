package nl.unionsoft.sysstate.server;

import java.util.Scanner;

import nl.unionsoft.common.server.ServerServiceImpl;

import org.apache.commons.lang.StringUtils;

public class SysStateServerServiceImpl {

    public static final String CONNECTOR_HOST = "localhost";
    public static final int CONNECTOR_PORT = 8680;

    public static void main(final String[] args) {

        ServerServiceImpl serverService = new ServerServiceImpl("/");
        serverService.setPort(CONNECTOR_PORT);
        // serverService.setOverrideDescriptor("./src/main/resources/override-web.xml");
        serverService.setWar("./src/main/webapp");
        serverService.setHost(CONNECTOR_HOST);
        

        if (serverService.startServer()) {
            final Scanner scanner = new Scanner(System.in);

            try {
                String line;
                boolean keepAsking = true;
                while (keepAsking) {
                    System.out.print("(start/stop/exit/restart):");
                    line = StringUtils.lowerCase(scanner.nextLine());
                    if ("exit".equals(line)) {
                        serverService.stopServer();
                        keepAsking = false;
                    } else if ("start".equals(line)) {
                        serverService.startServer();
                    } else if ("stop".equals(line)) {
                        serverService.stopServer();
                    } else if ("restart".equals(line)) {
                        serverService.stopServer();
                        serverService.startServer();
                    }
                }
            } finally {
                scanner.close();
            }
        }

    }

}
