/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.utils;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.web.monitor.core.bean.ApplicationBean;

/**
 * MessageManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class MessageManager {
	
	public MessageManager() {
		// inizializzazione, lasciare public il costruttore poichè usato in tools/web_interfaces/monitor/src/src_core/META-INF/faces-config.xml
		// altrimenti si ottiene errore: Caused by: com.sun.faces.mgbean.ManagedBeanCreationException: Unable to create managed bean applicationBean.  The following problems were found:
		// - Managed bean class org.openspcoop2.web.monitor.core.utils.MessageManager for managed bean applicationBean doesnt declare a public no-argument constructor.
	}
	
	private static MessageManager instance = null;

	public static MessageManager getInstance(){

		if(MessageManager.instance == null) {
			// spotbugs warning 'SING_SINGLETON_GETTER_NOT_SYNCHRONIZED': l'istanza viene creata allo startup
			synchronized (MessageManager.class) {
				init();
			}
		}

		return MessageManager.instance;
	}

	private static synchronized void init(){
		if(MessageManager.instance == null)
			MessageManager.instance = new MessageManager();
	}

	public String getMessage(String key) {

		// use standard JSF Resource Bundle mechanism
		/**return getMessageFromJSFBundle(key);*/

		// use the default Java ResourceBund;e mechanism
		return getMessageFromResourceBundle(key);
	}

	@SuppressWarnings("unused")
	private String getMessageFromJSFBundle(String key) {
		return (String)MessageManager.resolveExpression("#{msg['" + key + "']}");
	}

	public static ClassLoader getCurrentLoader(Object fallbackClass) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null)
			loader = fallbackClass.getClass().getClassLoader();
		return loader;
	}

	// from JSFUtils in Oracle ADF 11g Storefront Demo
	public static Object resolveExpression(String expression) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		Application app = facesContext.getApplication();
		ExpressionFactory elFactory = app.getExpressionFactory();
		ELContext elContext = facesContext.getELContext();
		ValueExpression valueExp =
				elFactory.createValueExpression(elContext, expression,
						Object.class);
		return valueExp.getValue(elContext);
	}

	public String getMessageFromResourceBundle(String bundleName, String key, Object params[], Locale locale){

		String text = null;
		try{
			ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentClassLoader(params));
			text = bundle.getString(key);
		} catch(MissingResourceException e){
			text = "?? key " + key + " not found ??";
			LoggerWrapperFactory.getLogger(MessageManager.class).error(text,e);
		}
		if(params != null){
			MessageFormat mf = new MessageFormat(text, locale);
			text = mf.format(params, new StringBuffer(), null).toString();
		}
		return text;
	}

	public  ClassLoader getCurrentClassLoader(Object defaultObject){

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}
	
	public Locale getLocale() {
		Locale locale = null;

		try{
			FacesContext currentInstance = FacesContext.getCurrentInstance();
			if(currentInstance != null){
				UIViewRoot viewRoot = currentInstance.getViewRoot();
				if(viewRoot != null)
					locale = viewRoot.getLocale();
			}
		}catch(Throwable e){}

		if(locale == null){
			locale = ApplicationBean.getInstance().getLocale();
		}

		return locale;
	}
	
	public String getMessageFromResourceBundle(String key) {
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
}
