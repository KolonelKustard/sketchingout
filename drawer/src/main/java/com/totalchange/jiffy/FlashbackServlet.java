package com.totalchange.jiffy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FlashbackServlet extends HttpServlet {
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_CONTENT = "content";
    
    private static Log log = LogFactory.getLog(FlashbackServlet.class);

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        log.trace("New request");
        
        // Chuck it all back
        String type = request.getParameter(PARAM_TYPE);
        String filename = request.getParameter(PARAM_FILENAME);

        if (type != null) {
            if (log.isTraceEnabled()) {
                log.trace("Setting content type to " + type);
            }
            response.setContentType(type);
        } else {
            log.trace("Setting content type to application/octet-stream");
            response.setContentType("application/octet-stream");
        }

        if (filename != null) {
            if (log.isTraceEnabled()) {
                log.trace("Setting filename of response to " + filename);
            }
            response.setHeader("Content-Disposition", "attachment; filename="
                    + filename);
        }

        log.trace("Providing content");
        response.getWriter().print(request.getParameter(PARAM_CONTENT));
        
        log.trace("Done");
    }
}
