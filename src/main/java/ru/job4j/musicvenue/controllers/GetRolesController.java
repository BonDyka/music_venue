package ru.job4j.musicvenue.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.persistence.dao.DaoFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class GetRolesController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(GetRolesController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject answer = new JsonObject();
        try {
            List<Role> roles = DaoFactory.getFactory().getRoleDao().getAll();
            answer.addProperty("roles", new Gson().toJson(roles));
            new PrintWriter(resp.getOutputStream()).append(answer.toString()).flush();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
