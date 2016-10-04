/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.generic_project.web.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;


/***
 * 
 * Utils Fornisce una serie di utilities.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class Utils {

	protected abstract ClassLoader getCurrentClassLoader(Object defaultObject);

	protected abstract Locale getLocale();
	
//	public abstract WebGenericProjectFactory getWebGenericProjectFactory () throws FactoryException;
//	public abstract WebGenericProjectFactory getWebGenericProjectFactory (Logger log) throws FactoryException;

	public   String getMessageFromResourceBundle(String key) {
		Locale locale = getLocale();
		return getMessageFromResourceBundle("messages", key, null, locale);
	}

	public   String getMessageWithParamsFromResourceBundle(String key, Object ... params) {
		Locale locale = getLocale();
		return getMessageFromResourceBundle("messages", key, params, locale);
	}


	public   String getMessageFromResourceBundle(String key,Locale locale){
		return getMessageFromResourceBundle("messages", key, null, locale);
	}

	public   String getMessageFromResourceBundle(String key, String bundleName){
		Locale locale = getLocale();

		if(bundleName == null)
			bundleName = "messages";

		return getMessageFromResourceBundle(bundleName, key, null, locale);
	}

	public   String getMessageFromResourceBundle(String key, String bundleName, Locale locale){
		if(locale == null){
			locale = getLocale();
		}

		if(bundleName == null)
			bundleName = "messages";

		return getMessageFromResourceBundle(bundleName, key, null, locale);
	}

	public   String getMessageFromCommonsResourceBundle(String key) {
		Locale locale = getLocale();
		return getMessageFromResourceBundle("commonsMessages", key, null, locale);
	}


	public   String getMessageWithParamsFromCommonsResourceBundle(String key, Object ... params) {
		Locale locale = getLocale();
		return getMessageFromResourceBundle("commonsMessages", key, params, locale);
	}

	public   String getMessageWithParamsFromCommonsResourceBundle(String key, Locale locale, Object [] params) {
		return getMessageFromResourceBundle("commonsMessages", key, params, locale);
	}

	public   String getMessageFromCommonsResourceBundle(String key,Locale locale){
		return getMessageFromResourceBundle("commonsMessages", key, null, locale);
	}

	public   String getMessageFromResourceBundle(
			String bundleName, 
			String key, 
			Object params[], 
			Locale locale){

		String text = null;
		try{
			ResourceBundle bundle = 
					ResourceBundle.getBundle(bundleName, locale, 
							getCurrentClassLoader(params));


			text = bundle.getString(key);
		} catch(MissingResourceException e){
			text = "?? key " + key + " not found ??";
		}
		if(params != null){
			MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}
		return text;
	}

	// copy method from From E.R. Harold's book "Java I/O"
	public static void copy(InputStream in, OutputStream out) 
			throws IOException {

		// do not allow other threads to read from the
		// input or write to the output while copying is
		// taking place

		synchronized (in) {
			synchronized (out) {

				byte[] buffer = new byte[256];
				while (true) {
					int bytesRead = in.read(buffer);
					if (bytesRead == -1) break;
					out.write(buffer, 0, bytesRead);
				}
			}
		}
	}
	
	public static String getProperty(Properties reader, String name,boolean required) throws Exception{
		String tmp = null;

		tmp = reader.getProperty(name);

		if(tmp==null){
			if(required){
				throw new Exception("Property ["+name+"] not found");
			}
		}
		if(tmp!=null){
			return tmp.trim();
		}else{
			return null;
		}
	}

	public static Boolean getBooleanProperty(Properties reader,String name,boolean required) throws Exception{
		String propAsString = getProperty(reader,name, required);

		if(propAsString != null){
			Boolean b = new Boolean(propAsString.equalsIgnoreCase("true"));
			return b;
		}
		return null;
	}
}
