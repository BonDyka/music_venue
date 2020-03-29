package ru.job4j.musicvenue.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.GetAllUsersSpec;
import ru.job4j.musicvenue.persistence.GetUserByLoginSpec;
import ru.job4j.musicvenue.persistence.Repository;
import ru.job4j.musicvenue.persistence.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static java.util.Objects.nonNull;

public class UsersViewController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(UsersViewController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getServletContext().getRequestDispatcher(String.format("%s/user_page.html", req.getContextPath())).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentUserLogin = (String) req.getSession().getAttribute("login");
        JsonObject jsonObject = new JsonObject();
        if (nonNull(currentUserLogin)) {
            try {
                Repository<User> repository = new UserRepository();
                User currentUser = repository.getByCriteria(new GetUserByLoginSpec(currentUserLogin)).get(0);
                List<User> users = repository.getByCriteria(new GetAllUsersSpec());
                users.remove(currentUser);
                this.prepareListForResponse(users);
                Gson gson = new Gson();
                jsonObject.addProperty("currentUser", gson.toJson(currentUser));
                jsonObject.addProperty("users", gson.toJson(users));
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                jsonObject.addProperty("msg", "Server is busy. Try it later");
                jsonObject.addProperty("nextPage", "/signin");
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            jsonObject.addProperty("msg", "You must be logged for view the page.");
            jsonObject.addProperty("nextPage", "/signin");
        }
        resp.setContentType("application/json");

        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        writer.append(jsonObject.toString());
        writer.flush();
    }

    private void prepareListForResponse(List<User> list) {
        for (User item : list) {
            item.setPassword(null);
        }
    }
}
