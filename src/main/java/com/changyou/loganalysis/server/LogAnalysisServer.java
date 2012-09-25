package com.changyou.loganalysis.server;

import java.net.URL;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class LogAnalysisServer {
    private static Logger logger = Logger.getLogger(LogAnalysisServer.class);

    public static void main(String[] args) throws Exception {
        new LogAnalysisServer().startServer();
    }

    public void startServer() throws Exception {
        Server server = new Server(8080);
        String contextPath = "com/changyou/loganalysis/server/webapp";
        URL warUrl = LogAnalysisServer.class.getClassLoader().getResource(contextPath);
        if (warUrl == null) {
            logger.error("can not get WebAppContext for:" + contextPath);
        }
        server.setHandler(new WebAppContext(warUrl.toExternalForm(), "/loganalysis"));
        server.start();
    }
}
