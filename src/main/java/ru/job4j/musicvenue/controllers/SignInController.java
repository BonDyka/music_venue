package ru.job4j.musicvenue.controllers;

import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.GetUserByLoginSpec;
import ru.job4j.musicvenue.persistence.PersistException;
import ru.job4j.musicvenue.persistence.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class SignInController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(SignInController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nextPage = "/view";

        String login = req.getParameter("login");
        String password = req.getParameter("password");

        JsonObject jsonObject = new JsonObject();

        if (!login.isEmpty() && !password.isEmpty()) {
            try {
                User user = new UserRepository().getByCriteria(new GetUserByLoginSpec(login)).get(0);
                if (login.equals(user.getLogin()) && password.equals(user.getPassword())) {
                    jsonObject.addProperty("logged", true);
                    jsonObject.addProperty("nextPage", nextPage);
                    HttpSession session = req.getSession(); //change session attribute
                    session.setAttribute("login", user.getLogin());
                    session.setAttribute("role", user.getRole().getTitle());
                } else {
                    jsonObject.addProperty("logged", false);
                    jsonObject.addProperty("msg", "Incorrect login or password. Please enter correct data.");
                }
            } catch (PersistException e) {
                LOG.error(e.getMessage(), e);
                jsonObject.addProperty("logged", false);
                jsonObject.addProperty("msg", "Incorrect login or password. Please enter correct data.");
            }
        }

        resp.setContentType("application/json");

        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        writer.append(jsonObject.toString());
        writer.flush();
    }
}
