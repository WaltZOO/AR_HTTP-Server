package httpserver.itf.impl;

import java.util.HashMap;
import java.util.Map;

import httpserver.itf.HttpSession;

public class Session implements HttpSession {

	private String id;
	private Map<String, Object> attributes;
	private long lastAccessed;

	public Session(String id) {
		this.id = id;
		this.attributes = new HashMap<>();
		this.lastAccessed = System.currentTimeMillis();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getValue(String key) {
		return attributes.get(key);
	}

	@Override
	public void setValue(String key, Object value) {
		attributes.put(key, value);

	}
	
	
	public void updateAccessTime() {
        this.lastAccessed = System.currentTimeMillis();
    }

	public long get_lastAccessed() {
		return lastAccessed;
	}
	
    public boolean isExpired(long limit) {
        return (System.currentTimeMillis() - lastAccessed) > limit;
    }
}
