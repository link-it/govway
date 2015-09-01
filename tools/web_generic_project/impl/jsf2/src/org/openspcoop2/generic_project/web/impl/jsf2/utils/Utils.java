/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf2.utils;

import java.util.Locale;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/***
 * 
 * Utils Fornisce una serie di utilities.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Utils extends org.openspcoop2.generic_project.web.core.Utils {

	private static Utils instance = null;

	public static Utils getInstance(){

		if(Utils.instance == null)
			init();


		return Utils.instance;
	}

	private synchronized static void init(){
		if(Utils.instance == null)
			Utils.instance = new Utils();
	}

	@Override
	protected ClassLoader getCurrentClassLoader(Object defaultObject){

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		if(loader == null){
			loader = defaultObject.getClass().getClassLoader();
		}

		return loader;
	}

	@Override
	public Locale getLocale() {
		Locale locale = null;

		try{
			FacesContext currentInstance = FacesContext.getCurrentInstance();
			if(currentInstance != null){
				UIViewRoot viewRoot = currentInstance.getViewRoot();
				if(viewRoot != null)
					locale = viewRoot.getLocale();
			}
		}catch(Exception e){}

		if(locale == null){
			locale = Locale.ITALY;
		}

		return locale;
	}

	@SuppressWarnings("unchecked")
	public static <T> T findBean(String beanName) {
		FacesContext context = FacesContext.getCurrentInstance();
		return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}

	public static String getRequestParameter(String parameterName) {
		String parameterValue = null;

		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		Map<String, String[]> parameterMap = req.getParameterMap();


		for (String key : parameterMap.keySet()) {

			if(key.equals(parameterName)){
				String[] strings = parameterMap.get(key);

				if(strings.length == 1)
					parameterValue = strings[0];

				break;
			}
		}
		return parameterValue;
	}

	public static String getRequestParameterEndsWith(String parameterName) {
		String parameterValue = null;

		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		Map<String, String[]> parameterMap = req.getParameterMap();


		for (String key : parameterMap.keySet()) {

			if(key.endsWith(parameterName)){
				String[] strings = parameterMap.get(key);

				if(strings.length == 1)
					parameterValue = strings[0];

				break;
			}
		}
		return parameterValue;
	}


	public static void printRequestParameter(String parameterName,Logger log) {
		HttpServletRequest req = (HttpServletRequest)FacesContext.getCurrentInstance().getExternalContext().getRequest();

		Map<String, String[]> parameterMap = req.getParameterMap();

		String val = "";
		if(parameterName != null){
			String[] strings = parameterMap.get(parameterName);

			if(strings != null)
				for (String string : strings) {
					if(val.length() > 0)
						val += ", " ;

					val += string;
				}
			else 
				val = "Non presente";
			
			log.debug("PAR ["+parameterName+"] VAL["+val+"]" );

		}else {
			for (String key : parameterMap.keySet()) {

				if(key.equals(parameterName)){
					String[] strings = parameterMap.get(key);

					for (String string : strings) {
						if(val.length() > 0)
							val += ", " ;

						val += string;
					}
				}

				log.debug("PAR ["+key+"] VAL["+val+"]" );
			}
		}
	}
}
