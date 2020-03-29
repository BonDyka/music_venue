package ru.job4j.musicvenue.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.persistence.PersistException;
import ru.job4j.musicvenue.persistence.dao.DaoFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class MusicTypeController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(MusicTypeController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonObject answer = new JsonObject();

        try {
            List<MusicType> types = DaoFactory.getFactory().getMusicTypeDao().getAll();
            answer.addProperty("types", new Gson().toJson(types));
            new PrintWriter(resp.getOutputStream()).append(answer.toString()).flush();
        } catch (PersistException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
