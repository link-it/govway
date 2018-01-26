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
package org.openspcoop2.generic_project.web.utils;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.generic_project.web.utils.BrowserInfo.BrowserFamily;

/***
 * 
 * BrowserUtils Utilities per il supporto multibrowser.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BrowserUtils {

	public static final String USER_AGENT_HEADER_NAME = "User-Agent";


	/***
	 * 
	 * Decodifica il browser e la versione a partire dalla stringa dello UserAgent
	 * 
	 */
	public static BrowserInfo getBrowserInfo(String Information) {
		BrowserInfo browserInfo = new BrowserInfo();


		browserInfo.setUserAgentString(Information);
		String browser = browserInfo.getUserAgentString();
		String info[] = null;
		BrowserFamily bf = null;

		if(browser.contains("MSIE")){
			String subsString = browser.substring( browser.indexOf("MSIE"));
			info = (subsString.split(";")[0]).split(" ");
			bf =BrowserFamily.IE;
		}
		else if(browser.contains("msie")){
			String subsString = browser.substring( browser.indexOf("msie"));
			info = (subsString.split(";")[0]).split(" ");
			bf =BrowserFamily.IE;
		}
		//		else if(browser.contains("rv:")){
		//			String subsString = browser.substring( browser.indexOf("rv"));
		//			int idx = subsString.indexOf(")");
		//			if(idx > -1)
		//				info = ((String)subsString.subSequence(0, idx)).split(":");
		//		}
		else if(browser.contains("Trident")){
			String subsString = browser.substring( browser.indexOf("Trident"));
			info = new String[2];

			info[0] = (subsString.split(";")[0]).split("/")[0];

			int idx = (subsString.split(";")[1]).indexOf(")");
			if(idx > -1)
				info[1] = ((String)(subsString.split(";")[1]).subSequence(0, idx)).split(":")[1];
			else
				info[1] = "";
			bf =BrowserFamily.IE;
		}
		else if(browser.contains("Firefox")){
			String subsString = browser.substring( browser.indexOf("Firefox"));
			info = (subsString.split(" ")[0]).split("/");
			bf =BrowserFamily.FIREFOX;
		}
		else if(browser.contains("Chrome")){
			String subsString = browser.substring( browser.indexOf("Chrome"));
			info = (subsString.split(" ")[0]).split("/");
			bf =BrowserFamily.CHROME;
		}
		else if(browser.contains("Opera")){
			String subsString = browser.substring( browser.indexOf("Opera"));
			info = (subsString.split(" ")[0]).split("/");
			bf =BrowserFamily.OPERA;
		}
		else if(browser.contains("Safari")){
			String subsString = browser.substring( browser.indexOf("Safari"));
			info = (subsString.split(" ")[0]).split("/");
			bf =BrowserFamily.SAFARI;
		}

		browserInfo.setBrowserName(info[0]);
		try{
			browserInfo.setVersion(StringUtils.isNotEmpty(info[1]) ? Double.parseDouble(info[1]) : null);
		}catch(NumberFormatException e){
			// versione non riconosciuta
			browserInfo.setVersion(null);
		}
		browserInfo.setBrowserFamily(bf); 

		return browserInfo;
	}


	/***
	 * 
	 * Decodifica il browser e la versione a partire dalla stringa dello UserAgent
	 * 
	 */
	public static BrowserInfo getBrowserInfo(FacesContext context) throws Exception{
		if(context != null){
			ExternalContext externalContext = context.getExternalContext();
			if(externalContext != null){
				HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
				String userAgent = request.getHeader(USER_AGENT_HEADER_NAME);
				return getBrowserInfo(userAgent);
			}
		}

		return null;
	}
	
	/***
	 * 
	 * Decodifica il browser e la versione a partire dalla stringa dello UserAgent
	 * 
	 */
	public static BrowserInfo getBrowserInfo(HttpServletRequest request) {
		
		String userAgent =  request.getHeader(USER_AGENT_HEADER_NAME);
		
		return getBrowserInfo(userAgent);
	}
	
	public static HttpServletResponse getResponse(FacesContext context) throws Exception{
		if(context != null){
			ExternalContext externalContext = context.getExternalContext();
			if(externalContext != null){
				HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();
				return response;
			}
		}

		return null;
	}
}
