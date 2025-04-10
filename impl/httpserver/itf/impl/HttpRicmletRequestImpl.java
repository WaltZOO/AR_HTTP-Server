package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;
import httpserver.itf.HttpRicmletRequest;
import httpserver.itf.HttpRicmletResponse;
import httpserver.itf.HttpSession;

public class HttpRicmletRequestImpl extends HttpRicmletRequest {

	public String path;
	public String[] request;
//	private String[] args;
	private Map<String, String> args;
	private Map<String, HttpRicmlet> singletonHashMap = new HashMap<String, HttpRicmlet>();
	private Map<String, String> cookies;
	private HttpRicmletResponseImpl ricmletResp;

	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		
		// parsing of the path
		request = m_ressname.split("\\?");
		path = request[0].replace("/", ".");
		path = path.substring(10); // remove the ricmlets part

		
		//parsing args
		args = new HashMap<>();
		if (request.length >= 2) {
			System.out.println(path);
			String[] paramPairs = request[1].split("&");
			for (String pair : paramPairs) {
				String[] keyValue = pair.split("=", 2);
				if (keyValue.length == 2) {
					args.put(keyValue[0], keyValue[1]);
				} else if (keyValue.length == 1) {
					args.put(keyValue[0], "");
				}
			}
		}

		// parse cookies
		cookies = new HashMap<>();
		String line;
		while ((line = br.readLine()) != null && !line.isEmpty()) {
			if (line.startsWith("Cookie: ")) {
				String[] cookiePairs = line.substring(8).split(";");
				for (String pair : cookiePairs) {
					String[] keyValue = pair.trim().split("=", 2);
					if (keyValue.length >= 1) {
						String value = keyValue.length > 1 ? keyValue[1] : "";
						cookies.put(keyValue[0].trim(), value.trim());
					}
				}
			}
		}
	}

	@Override
	public HttpSession getSession() {
		String sessionId = getCookie("session-id");
		Session session = null;

		if (sessionId != null) {
			session = m_hs.getSessionById(sessionId);
			if (session != null) {
				if (System.currentTimeMillis() - session.get_lastAccessed() > HttpServer.max_session) {
					m_hs.getSessions().remove(sessionId);
					session = null;
				} else {
					session.updateAccessTime();
				}
			}
		}

		if (session == null) {
			HttpServer.increment_nbSession();
			sessionId = String.valueOf(HttpServer.getNbSession()); // put the number of sessions as ID

			session = new Session(sessionId);
			m_hs.putSession(sessionId, session); // add it to the session map
			((HttpRicmletResponseImpl) ricmletResp).setCookie("session-id", sessionId);
		}

		return session;
	}

	@Override
	public String getArg(String name) {
		return args.get(name);
	}

	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		try {

			Class<?> c = Class.forName(path);
			HttpRicmlet ricmlet = m_hs.getInstance(path);
            ricmletResp = new HttpRicmletResponseImpl(((HttpResponseImpl)resp).m_ps, m_hs, this);
            ricmlet.doGet(this, ricmletResp);

		} catch (Exception e) {
			resp.setReplyError(404, "Not Found Error");
			e.printStackTrace();
		}
	}

}
