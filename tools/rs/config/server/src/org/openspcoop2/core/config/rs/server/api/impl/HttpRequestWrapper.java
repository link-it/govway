package org.openspcoop2.core.config.rs.server.api.impl;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.transport.http.WrappedHttpServletRequest;

public class HttpRequestWrapper extends WrappedHttpServletRequest  {

	private HashMap<String,String> overrides = new HashMap<>();
	
	@Override
	public String getParameter(String key) {
		if (this.overrides.get(key) != null) {
			return this.overrides.get(key);
		}
		
		return super.getParameter(key);
	}
	
	public void overrideParameter(String key, Object value) {
		if (value == null)
			this.overrides.put(key, null);
		else
			this.overrides.put(key, value.toString());
	}

	public HttpRequestWrapper(HttpServletRequest httpServletRequest) {
		super(httpServletRequest);
	}

}
