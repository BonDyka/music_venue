package ru.job4j.musicvenue.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class SignInControllerTest {
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);
    private ServletOutputStream stream = mock(ServletOutputStream.class);
    private HttpSession session = mock(HttpSession.class);

    @Before
    public void setUp() throws IOException {
        when(request.getParameter("login")).thenReturn("admin");
        when(request.getParameter("password")).thenReturn("admin");
        when(response.getOutputStream()).thenReturn(stream);
        when(request.getSession()).thenReturn(session);
        doNothing().when(session).setAttribute(anyString(), any());
    }

    @After
    public void shoutDown() {
        this.request = null;
        this.response = null;
    }

    @Test
    public void whenLoginWithTrueParametersThemMustBeLogged() throws ServletException, IOException {
        SignInController controller = new SignInController();

        controller.doGet(request, response);

        verify(request, atLeastOnce()).getParameter("login");
        verify(request, atLeastOnce()).getParameter("password");
        verify(session, atLeastOnce()).setAttribute(anyString(), any());
    }
}