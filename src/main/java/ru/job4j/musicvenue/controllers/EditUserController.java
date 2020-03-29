package ru.job4j.musicvenue.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.PersistException;
import ru.job4j.musicvenue.persistence.dao.DaoFactory;
import ru.job4j.musicvenue.persistence.dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditUserController extends HttpServlet {
    private static final Logger LOG = LoggerFactory.getLogger(EditUserController.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String currentUserLogin = (String) session.getAttribute("login");
        String currentUserRole = (String) session.getAttribute("role");
        int userId = Integer.valueOf(req.getParameter("id"));
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        try {
            User editUser = DaoFactory.getFactory().getUserDao().getById(userId);
            if (!editUser.getLogin().equals(currentUserLogin)) {
                editUser.setPassword(null);
            }
            jsonObject.addProperty("editUser", gson.toJson(editUser));
            jsonObject.addProperty("currentUserRole", currentUserRole);
        } catch (PersistException e) {
            LOG.error(e.getMessage(), e);
        }
        resp.setContentType("application/json");

        new PrintWriter(resp.getOutputStream()).append(jsonObject.toString()).flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DaoFactory factory = DaoFactory.getFactory();
        JsonObject jsonObject = new JsonObject();
        Map<String, String[]> reqParams = req.getParameterMap();
        int id = Integer.valueOf(req.getParameter("id"));
        int role = Integer.valueOf(req.getParameter("role"));

        User user;
        try {
            UserDao dao = DaoFactory.getFactory().getUserDao();
            user = dao.getById(id);
            user.getRole().setId(role);

            if (reqParams.size() == 7) {
                Address address = user.getAddress();
                address.setCountry(req.getParameter("country"));
                address.setCity(req.getParameter("city"));
                factory.getAddressDao().update(address);
                List<MusicType> types = new ArrayList<>();
                String[] typesArr = reqParams.get("types");
                for (String aTypesArr : typesArr) {
                    types.add(new MusicType(Integer.valueOf(aTypesArr)));
                }
                user.setTypes(types);
            }
            dao.update(user);
            jsonObject.addProperty("updated", true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            jsonObject.addProperty("updated", false);
            jsonObject.addProperty("msg", "Server is busy. Try it later.");
        }
        new PrintWriter(resp.getOutputStream()).append(jsonObject.toString()).flush();
    }
}
