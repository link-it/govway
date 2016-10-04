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
package org.openspcoop2.generic_project.web.utils;

import javax.servlet.http.HttpServletRequest;

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
	 * @param userAgent il contenuto dell'header 'User-Agent' da analizzare
	 * @return la coppia di valori <BrowserName,Versione>
	 */
	public static  String[] getBrowserInfo(String userAgent) {
		String info[] = null;
		
		if(userAgent == null)
			return null;

		if(userAgent.contains("MSIE")){
			String subsString = userAgent.substring( userAgent.indexOf("MSIE"));
			info = (subsString.split(";")[0]).split(" ");
		}
		else if(userAgent.contains("msie")){
			String subsString = userAgent.substring( userAgent.indexOf("msie"));
			info = (subsString.split(";")[0]).split(" ");
		}
		else if(userAgent.contains("Trident")){
			String subsString = userAgent.substring( userAgent.indexOf("Trident"));
			info = new String[2];
			
			info[0] = (subsString.split(";")[0]).split("/")[0];
			
			int idx = (subsString.split(";")[1]).indexOf(")");
			if(idx > -1)
				info[1] = ((String)(subsString.split(";")[1]).subSequence(0, idx)).split(":")[1];
			else
			info[1] = "";
			
		}
		else if(userAgent.contains("Firefox")){
			String subsString = userAgent.substring( userAgent.indexOf("Firefox"));
			info = (subsString.split(" ")[0]).split("/");
		}
		else if(userAgent.contains("Chrome")){
			String subsString = userAgent.substring( userAgent.indexOf("Chrome"));
			info = (subsString.split(" ")[0]).split("/");
		}
		else if(userAgent.contains("Opera")){
			String subsString = userAgent.substring( userAgent.indexOf("Opera"));
			info = (subsString.split(" ")[0]).split("/");
		}
		else if(userAgent.contains("Safari")){
			String subsString = userAgent.substring( userAgent.indexOf("Safari"));
			info = (subsString.split(" ")[0]).split("/");
		}
		return info;
	}
	
	
	/***
	 * 
	 * Decodifica il browser e la versione a partire dalla stringa dello UserAgent
	 * 
	 * @param request HttpServletRequest da analizzare.
	 * @return la coppia di valori <BrowserName,Versione>
	 */
	public static  String[] getBrowserInfo(HttpServletRequest request) {
		
		String userAgent =  request.getHeader(USER_AGENT_HEADER_NAME);
		
		return getBrowserInfo(userAgent);
	}
}
