package com.totalchange.jiffy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FlashbackServlet extends HttpServlet {
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_FILENAME = "filename";
    private static final String PARAM_CONTENT = "content";

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        // Chuck it all back
        String type = request.getParameter(PARAM_TYPE);
        String filename = request.getParameter(PARAM_FILENAME);

        if (type != null) {
            response.setContentType(type);
        } else {
            response.setContentType("application/octet-stream");
        }

        if (filename != null) {
            response.setHeader("Content-Disposition", "attachment; filename="
                    + filename);
        }

        response.getWriter().print(request.getParameter(PARAM_CONTENT));
    }
}
