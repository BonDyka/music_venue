package ru.job4j.musicvenue.controllers;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static java.util.Objects.nonNull;

public class AuthFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getRequestURI().contains("/")) {
            chain.doFilter(req, resp);
        } else {
            HttpSession session = request.getSession(false);
            if (nonNull(session) && nonNull(session.getAttribute("login")) && nonNull(session.getAttribute("role"))) {
                ((HttpServletResponse) resp).sendRedirect(String.format("%s/", request.getContextPath()));
                return;
            }
            chain.doFilter(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
