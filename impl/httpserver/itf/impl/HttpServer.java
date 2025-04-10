package httpserver.itf.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import httpserver.itf.HttpRequest;
import httpserver.itf.HttpResponse;
import httpserver.itf.HttpRicmlet;

/**
 * Basic HTTP Server Implementation
 * 
 * Only manages static requests The url for a static ressource is of the form:
 * "http//host:port/<path>/<ressource name>" For example, try accessing the
 * following urls from your brower: http://localhost:<port>/
 * http://localhost:<port>/voile.jpg ...
 */
public class HttpServer {

	private int m_port;
	private File m_folder; // default folder for accessing static resources (files)
	private ServerSocket m_ssoc;
	private HashMap<String, HttpRicmlet> ricmlets = new HashMap<String, HttpRicmlet>();
	private Map<String, String> cookies = new HashMap<String, String>();
	public static int max_session = 5000; // time limit of a session
	private static Map<String, Session> sessions = new HashMap<>();
	private static int nb_sessions = 0;

	protected HttpServer(int port, String folderName) {
		m_port = port;
		if (!folderName.endsWith(File.separator))
			folderName = folderName + File.separator;
		m_folder = new File(folderName);
		try {
			m_ssoc = new ServerSocket(m_port);
			System.out.println("HttpServer started on port " + m_port);
		} catch (IOException e) {
			System.out.println("HttpServer Exception:" + e);
			System.exit(1);
		}
	}

	public File getFolder() {
		return m_folder;
	}

	public HttpRicmlet getInstance(String clsname) {
		HttpRicmlet ricmlet = ricmlets.get(clsname);
		if (ricmlet == null) {
			try {
				Class<?> c = Class.forName(clsname);
				try {
					ricmlet = (HttpRicmlet) c.getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			ricmlets.put(clsname, ricmlet);

		}

		return ricmlet;

	}

	/*
	 * Reads a request on the given input stream and returns the corresponding
	 * HttpRequest object
	 */
	public HttpRequest getRequest(BufferedReader br) throws IOException {
		HttpRequest request = null;

		String startline = br.readLine();
		StringTokenizer parseline = new StringTokenizer(startline);
		String method = parseline.nextToken().toUpperCase();
		String ressname = parseline.nextToken();

		if (method.equals("GET")) {
			if (ressname.startsWith("/ricmlets")) {
				request = new HttpRicmletRequestImpl(this, method, ressname, br);
			} else {
				request = new HttpStaticRequest(this, method, ressname);
			}
		} else
			request = new UnknownRequest(this, method, ressname);
		return request;
	}

	/*
	 * Returns an HttpResponse object associated to the given HttpRequest object
	 */
	public HttpResponse getResponse(HttpRequest req, PrintStream ps) {
		return new HttpResponseImpl(this, req, ps);
	}

	public Session getSessionById(String id) {
		return sessions.get(id);
	}

	public static int getNbSession() {
		return nb_sessions;
	}

	public static void increment_nbSession() {
		nb_sessions++;
	}

	public void putSession(String id, Session session) {
		sessions.put(id, session);
	}

	public Map<String, Session> getSessions() {
		return sessions;
	}

	public synchronized void cleanExpiredSessions() {
		long now = System.currentTimeMillis();
		Iterator<Map.Entry<String, Session>> it = sessions.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, Session> entry = it.next();
			Session session = entry.getValue();
			if (now - session.get_lastAccessed() > max_session) {
				System.out.println("Session has been cleaned");
				it.remove();
			}
		}
	}

	/*
	 * Server main loop
	 */
	protected void loop() {
		try {
			while (true) {
				cleanExpiredSessions(); // cleaning expired sessions
				Socket soc = m_ssoc.accept();
				(new HttpWorker(this, soc)).start();
			}
		} catch (IOException e) {
			System.out.println("HttpServer Exception, skipping request");
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		int port = 0;
		if (args.length != 2) {
			System.out.println("Usage: java Server <port-number> <file folder>");
		} else {
			port = Integer.parseInt(args[0]);
			String foldername = args[1];
			HttpServer hs = new HttpServer(port, foldername);
			hs.loop();
		}
	}

}
