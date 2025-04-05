package httpserver.itf.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;

/*
 * This class allows to build an object representing an HTTP static request
 */
public class HttpStaticRequest extends HttpRequest {
	static final String DEFAULT_FILE = "index.html";

	public HttpStaticRequest(HttpServer hs, String method, String ressname) throws IOException {
		super(hs, method, ressname);
	}

	public void process(HttpResponse resp) throws Exception {

		if (this.getMethod().equals("GET")) {
			File f = new File(this.m_hs.getFolder()+m_ressname);
			if (!f.exists()) {
				resp.setReplyError(404, "Page Not Found!");
			} else {
				System.out.println(m_ressname);
				resp.setReplyOk();
				resp.setContentLength((int) f.length());
				resp.setContentType(getContentType(m_ressname));
				PrintStream ps = resp.beginBody();
				byte[] buffer = new byte[(int) f.length()]; 
				FileInputStream fis = new FileInputStream(f);
				
				fis.read(buffer);
				fis.close();
				ps.write(buffer);
				
				
			}
		}

	}

}
