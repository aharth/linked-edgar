package com.ontologycentral.edgarwrap.webapp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        String path = req.getRequestURI();
        if (!path.endsWith(".rdf")) {
        	path = path+".rdf";
        	res.sendRedirect(path);
        }
    }
}