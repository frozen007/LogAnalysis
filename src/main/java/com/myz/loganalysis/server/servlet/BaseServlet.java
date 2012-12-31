package com.myz.loganalysis.server.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class BaseServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(BaseServlet.class);

    protected void dispatch(String resource, HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher(resource).forward(request, response);
        } catch (Exception e) {
            logger.error("erro when dispatch:" + resource, e);
        }
    }

    protected void dispathError(String errorMsg, HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("error_msg", errorMsg);
        this.dispatch("/WEB-INF/error.jsp", request, response);
    }
}
