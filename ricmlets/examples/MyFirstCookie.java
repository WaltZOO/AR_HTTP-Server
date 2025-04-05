package examples;

import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;

public class MyFirstCookie implements httpserver.itf.HttpRicmlet {
	@Override
    public void doGet(HttpRicmletRequest req, HttpRicmletResponse resp) throws IOException {
        String cookieValue = req.getCookie("MyFirstCookie");
        int newValue;
        if (cookieValue == null) {
            newValue = 1; //initialize the value of the cookie for the 1st time
        } else {
            int value = Integer.parseInt(cookieValue);
            newValue = value + 1; // increment the existing value
        }
        resp.setCookie("MyFirstCookie", String.valueOf(newValue));

        resp.setReplyOk();
        resp.setContentType("text/html");
        PrintStream ps = resp.beginBody();
        ps.println("<HTML><HEAD><TITLE>Cookie Demo</TITLE></HEAD>");
        ps.println("<BODY>");
        ps.println("<H1>MyFirstCookie Value: " + newValue + "</H1>");
        ps.println("</BODY></HTML>");
    }
}
