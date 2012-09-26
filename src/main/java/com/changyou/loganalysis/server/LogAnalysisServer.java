package com.changyou.loganalysis.server;

import java.net.URL;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class LogAnalysisServer {
    private static Logger logger = Logger.getLogger(LogAnalysisServer.class);

    private int port = 8080;

    public static void main(String[] args) throws Exception {
        new LogAnalysisServer().startServer();
    }

    public LogAnalysisServer() {
        
    }

    public LogAnalysisServer(int port) {
        this.port = port;
    }

    public void startServer() throws Exception {
        Server server = new Server(this.port);
        String contextPath = "com/changyou/loganalysis/server/webapp/";
        URL warUrl = LogAnalysisServer.class.getClassLoader().getResource(contextPath);
        if (warUrl == null) {
            logger.error("can not get WebAppContext for:" + contextPath);
        } else {
            String webappPath = warUrl.toExternalForm();
            logger.info("loading webapp:"+webappPath);
            server.addHandler(new WebAppContext(webappPath, "/"));
            server.start();
        }
    }
}
