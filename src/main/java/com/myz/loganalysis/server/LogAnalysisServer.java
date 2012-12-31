package com.myz.loganalysis.server;

import java.net.URL;
import java.util.TimeZone;

import org.apache.log4j.Logger;
import org.mortbay.jetty.NCSARequestLog;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.RequestLogHandler;
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
        String contextPath = "com/myz/loganalysis/server/webapp/";
        URL warUrl = LogAnalysisServer.class.getClassLoader().getResource(contextPath);
        if (warUrl == null) {
            logger.error("can not get WebAppContext for:" + contextPath);
        } else {
            String webappPath = warUrl.toExternalForm();
            logger.info("loading webapp:"+webappPath);

            RequestLogHandler logHandler = new RequestLogHandler();
            NCSARequestLog requestLog = new NCSARequestLog();
            requestLog.setFilename(System.getProperty("loganalysis.home")+"/log/request.yyyy_mm_dd.log");
            requestLog.setFilenameDateFormat("yyyy_MM_dd");
            requestLog.setLogLatency(true);
            requestLog.setAppend(true);
            requestLog.setLogTimeZone(TimeZone.getDefault().getID());
            logHandler.setRequestLog(requestLog);

            server.addHandler(logHandler);
            server.addHandler(new WebAppContext(webappPath, "/"));
            server.start();
        }
    }
}
