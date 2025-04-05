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

public class HttpRicmletRequestImpl extends HttpRicmletRequest{
	
	public String path;
	public String[] request;
	public String args[];
	private Map<String, String> cookies;
	
	public HttpRicmletRequestImpl(HttpServer hs, String method, String ressname, BufferedReader br) throws IOException {
		super(hs, method, ressname, br);
		request = m_ressname.split("\\?");
		path = request[0].replace("/", ".");
		path = path.substring(10); // remove the ricmlets part
		System.out.println(request.length);
		
		// parse arguments
		if(request.length >= 2) {
			System.out.println(path);
			args =  request[1].split("&");
		}	
		else {
	        args = new String[0]; // avoid NullPointerException
	    }
		
		
		// parse cookies
		cookies = new HashMap<>();
		String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            if (line.startsWith("Cookie: ")) {
                String[] cookiePairs = line.substring(8).split(";");
                for (String pair : cookiePairs) {
                    String[] keyValue = pair.trim().split("=", 2); //
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getArg(String name) {
	if (args != null) {
        for (String arg : args) {
            String[] keyValue = arg.split("=");
            if (keyValue.length == 2 && keyValue[0].equals(name)) {
                return keyValue[1];
            }
        }
    }
    return null;
	}

	@Override
	public String getCookie(String name) {
		return cookies.get(name);
	}

	@Override
	public void process(HttpResponse resp) throws Exception {
		try {
            Class<?> c = Class.forName(path);
            HttpRicmlet ricmlet = (HttpRicmlet)c.getDeclaredConstructor().newInstance();
            HttpRicmletResponse ricmletResp = new HttpRicmletResponseImpl(((HttpResponseImpl)resp).m_ps);
            ricmlet.doGet(this, ricmletResp);
        } catch (ClassNotFoundException e) {
            resp.setReplyError(404, "Ricmlet not found");
        } catch (Exception e) {
            resp.setReplyError(500, "Internal Server Error");
            e.printStackTrace();
        }
    }
		
		
	

}
