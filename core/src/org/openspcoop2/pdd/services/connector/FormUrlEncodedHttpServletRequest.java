/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3, as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openspcoop2.pdd.services.connector;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.ContentType;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.WrappedHttpServletRequest;


/**
 * FormUrlEncodedHttpServletRequest
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FormUrlEncodedHttpServletRequest extends WrappedHttpServletRequest {

	private final static String CONTENT_TYPE_FORM_URL_ENCODED = "application/x-www-form-urlencoded";
	
	public static boolean isFormUrlEncodedRequest(HttpServletRequest httpServletRequest) {
		String ct = httpServletRequest.getContentType();
		try {
			String baseType = readBaseType(ct);
			if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)) {
				return true;
			}
		}catch(Exception e) {
		}
		return false;
	}
	
	public static HttpServletRequest convert(HttpServletRequest httpServletRequest) {
		String ct = httpServletRequest.getContentType();
		try {
			String baseType = readBaseType(ct);
			if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)) {
				return new FormUrlEncodedHttpServletRequest(httpServletRequest);
			}
		}catch(Exception e) {
		}
		return httpServletRequest;
	}
	
	private static String readBaseType(String ct) throws UtilsException {
		if(ct==null || "".equals(ct)) {
			throw new UtilsException("Content-Type not defined");
		}
		String baseType = null;
		try {
			ContentType ctObject = new ContentType(ct);
			baseType = ctObject.getBaseType();
		}catch(Exception e) {
			throw new UtilsException("Content-Type ["+ct+"] (parsing error): "+e.getMessage(),e);
		}
		return baseType;
	}
	
	private byte[] content;
	private Properties properties;
	public FormUrlEncodedHttpServletRequest(HttpServletRequest httpServletRequest) throws UtilsException{
		super(httpServletRequest);
		
		String ct = httpServletRequest.getContentType();
		String baseType = null;
		try {
			baseType = readBaseType(ct);
		}catch(Exception e) {
		}
		if(CONTENT_TYPE_FORM_URL_ENCODED.equalsIgnoreCase(baseType)==false) {
			throw new UtilsException("Content-Type ["+ct+"] non supportato");
		}
		
		try {
			InputStream is = httpServletRequest.getInputStream();
			if(is!=null) {
				this.content = Utilities.getAsByteArray(is);
			}
		}catch(Exception e) {
			throw new UtilsException("Content-Type ["+ct+"] read stream error: "+e.getMessage(),e);
		}
		
		java.util.Enumeration<?> en = httpServletRequest.getParameterNames();
		this.properties = new Properties();
		while(en.hasMoreElements()){
			String nomeProperty = (String)en.nextElement();
			String valueProperty = httpServletRequest.getParameter(nomeProperty);
			this.properties.put(nomeProperty, valueProperty);
		}
	}
	
	// Metodi modificati nei risultati
	
	@Override
	public int getContentLength() {
		if(this.content!=null) {
			return this.content.length;
		}
		return 0;
	}
	
	@Override
	public String getContentType() {
		return this.httpServletRequest.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		if(this.content!=null) {
			try {				
				return new FormUrlEncodedServletInputStream(new ByteArrayInputStream(this.content));
			}catch(Exception e) {
				throw new IOException(e.getMessage(),e);
			}
		}
		return null;
	}
	
	@Override
	public BufferedReader getReader() throws IOException {
		if(this.content!=null) {
			return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.content), this.httpServletRequest.getCharacterEncoding()));
		}
		return null;
	}
	
	@Override
	public String getParameter(String key) {
		return null;
	}

	@Override
	public Map<java.lang.String,java.lang.String[]> getParameterMap() {
		return new HashMap<java.lang.String,java.lang.String[]>();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		Hashtable<String, String> empty = new Hashtable<String, String>();
		return empty.keys();
	}

	@Override
	public String[] getParameterValues(String arg0) {
		return null;
	}
	
	public String getFormUrlEncodedParameter(String key) {
		return this.properties.getProperty(key);
	}

	public Enumeration<Object> getFormUrlEncodedParameterNames() {
		return this.properties.keys();
	}
	
	
}


class FormUrlEncodedServletInputStream extends ServletInputStream {

	private InputStream is;
	public FormUrlEncodedServletInputStream(InputStream is) {
		this.is = is;
	}
	
	@Override
	public int readLine(byte[] b, int off, int len) throws IOException {
		return this.is.read(b, off, len);
	}

	@Override
	public int read() throws IOException {
		return this.is.read();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return this.is.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return this.is.read(b, off, len);
	}

	@Override
	public long skip(long n) throws IOException {
		return this.is.skip(n);
	}

	@Override
	public int available() throws IOException {
		return this.is.available();
	}

	@Override
	public void close() throws IOException {
		this.is.close();
	}

	@Override
	public synchronized void mark(int readlimit) {
		this.is.mark(readlimit);
	}

	@Override
	public synchronized void reset() throws IOException {
		this.is.reset();
	}

	@Override
	public boolean markSupported() {
		return this.is.markSupported();
	}


}
