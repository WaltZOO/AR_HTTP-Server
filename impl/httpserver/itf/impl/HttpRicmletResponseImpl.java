package httpserver.itf.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpRicmletResponse;

public class HttpRicmletResponseImpl implements HttpRicmletResponse{

	private PrintStream ps;
	private HttpServer m_hs;
	private HttpRequest m_req;
	private Map<String, String> cookies = new HashMap<>();
	
	public HttpRicmletResponseImpl(PrintStream ps, HttpServer hs, HttpRequest req) {
		this.ps = ps;
		this.m_hs = hs;
		this.m_req = req;
    }
	
	@Override
	public void setReplyOk() throws IOException {
		ps.println("HTTP/1.1 200 OK");
        ps.println("Date: " + new Date());
        ps.println("Server: Ricm4HttpServer");		
	}

	@Override
	public void setReplyError(int codeRet, String msg) throws IOException {
		ps.println("HTTP/1.1 " + codeRet + " " + msg);
        ps.println("Date: " + new Date());
        ps.println("Server: Ricm4HttpServer");
        ps.println("Content-type: text/html");
        ps.println();
	}

	@Override
	public void setContentLength(int length) throws IOException {
		ps.println("Content-Length: " + length);
		
	}

	@Override
	public void setContentType(String type) throws IOException {
		ps.println("Content-Type: " + type);
		
	}

	@Override
	public PrintStream beginBody() throws IOException {
		for (Map.Entry<String, String> current : cookies.entrySet()) {
            ps.println("Set-Cookie: " + current.getKey() + "=" + current.getValue());
        }
		ps.println();
        return ps;
	}

	@Override
	public void setCookie(String name, String value) {
		cookies.put(name, value);
		
	}
	

  
}
