package ru.job4j.musicvenue.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.musicvenue.domains.Address;
import ru.job4j.musicvenue.domains.MusicType;
import ru.job4j.musicvenue.domains.Role;
import ru.job4j.musicvenue.domains.User;
import ru.job4j.musicvenue.persistence.UserRepository;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SignUpController extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(SignUpController.class);
    private static final int DEFAULT_ROLE_ID = 3;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String country = req.getParameter("country");
        String city = req.getParameter("city");
        String[] typesArr = req.getParameterMap().get("types[]");

        JsonObject jsonObject = new JsonObject();

        if (!login.isEmpty() && !password.isEmpty() && !country.isEmpty() && !city.isEmpty()) {
            User user = this.createUserByParameters(login, password, country, city, typesArr);
            try {
                new UserRepository().save(user);
                jsonObject.addProperty("created", true);
                jsonObject.addProperty("msg",
                        "User was creating. For enter input you login and password on authorisation form.");
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                jsonObject.addProperty("created", false);
                jsonObject.addProperty("msg", "User already exist. Please, change it.");
            }
        } else {
            jsonObject.addProperty("created", false);
            jsonObject.addProperty("msg", "All field must be filled.");
        }
        resp.setContentType("application/json");

        PrintWriter writer = new PrintWriter(resp.getOutputStream());
        writer.append(jsonObject.toString());
        writer.flush();
    }

    private User createUserByParameters(String login, String password, String country, String city, String[] typesId) {
        User result = new User();

        Address address = new Address();
        Role role = new Role(DEFAULT_ROLE_ID);
        address.setCountry(country);
        address.setCity(city);
        List<MusicType> types = new ArrayList<>();
        for (String id : typesId) {
            types.add(new MusicType(Integer.parseInt(id)));
        }
        result.setLogin(login);
        result.setPassword(password);
        result.setAddress(address);
        result.setRole(role);
        result.setTypes(types);

        return result;
    }
}
