/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.core;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.pdd.logger.info.FormatUtils;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;

/**
 * Utils
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Utils {


	private static Logger log = LoggerWrapperFactory.getLogger(Utils.class);

	protected static ClassLoader getCurrentClassLoader(Object defaultObject){

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}

	public static String getMessageFromResourceBundle(
			String bundleName, 
			String key, 
			Object params[], 
			Locale locale){

		String text = null;

		ResourceBundle bundle = 
				ResourceBundle.getBundle(bundleName, locale, 
						Utils.getCurrentClassLoader(params));

		try{
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

	public static String getMessageFromJSFBundle(String bundleName,String key) {
		ResourceBundle rb = ResourceBundle.getBundle(bundleName);
		return rb.getString(key);
		//return (String)resolveExpression("#{"+bundleName+"['" + key + "']}");
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

	public static String prettifyXml(String xml) {
		return FormatUtils.prettifyXml(log, xml);
	}
	public static String prettifyXml(String xml, String charset) {
		return FormatUtils.prettifyXml(log, xml, charset);
	}

	public static String prettifyXml(byte[] xml) {
		return FormatUtils.prettifyXml(log, xml);
	} 
	public static String prettifyXml(byte[] xml, String charset) {
		return FormatUtils.prettifyXml(log, xml, charset);
	} 
	
	 public static String getTestoVisualizzabile(byte [] b,StringBuilder stringBuffer, boolean logError) {
		 return FormatUtils.getTestoVisualizzabile(log, b, stringBuffer, logError);
	 }
	 public static String getTestoVisualizzabile(byte [] b,StringBuilder stringBuffer, boolean logError, int max) {
		 return FormatUtils.getTestoVisualizzabile(log, b, stringBuffer, logError, max);
	 }

	/**
	 * Legge le proprieta' dal fileProperties passato come parametro
	 * 
	 * @param fileProperties Il path del file properties
	 * @return Properties lette dal file
	 * @throws Exception
	 */
	public static Properties readProperties(String fileProperties) throws Exception {
		Properties prop = new Properties();
		InputStream inProp = Utils.class.getResourceAsStream(fileProperties);

		try {
			prop.load(inProp);

			return new Properties(prop);

		} catch (Exception e) {

			throw new Exception("Impossibile leggere il file di proprieta [" + fileProperties + "]", e);

		} finally {
			try {
				inProp.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Controlla che la pagina richiesta sia tra quelle che non necessitano di filtro sui contenuti,
	 * sono "libere" le pagine di login e timeout, e i path delle risorse richiesta dinamicamente dal framework 
	 *	
	 */
	public static boolean isContentAuthorizationRequiredForThisResource(HttpServletRequest httpServletRequest, List<String> excludedPaths) {
		String requestPath = httpServletRequest.getRequestURI();

		String contextPath = httpServletRequest.getContextPath(); // '/govwayMonitor'

		// caso limite, govwayMonitor/
		if(StringUtils.equals(requestPath, (contextPath + "/")))
			return false;

		boolean controlRequired = true;
		if(excludedPaths.size() > 0){
			for (String page : excludedPaths) {
				if(StringUtils.contains(requestPath, (contextPath + page))){
					controlRequired = false;
					break;
				}
			}
		}


		return controlRequired;
	}

}
