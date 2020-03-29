package ru.job4j.musicvenue.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.PersistException;
import ru.job4j.musicvenue.persistence.dao.DaoFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DeleteController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(DeleteController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            DaoFactory factory = DaoFactory.getFactory();
            User user = factory.getUserDao().getById(Integer.valueOf(req.getParameter("id")));
            factory.getAddressDao().delete(user.getAddress().getId());
            getServletContext().getRequestDispatcher("/signout");
        } catch (PersistException e) {
            LOG.error(e.getMessage(), e);
        }
    }
}