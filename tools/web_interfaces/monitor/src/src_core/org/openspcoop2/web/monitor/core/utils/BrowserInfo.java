/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * BrowserInfo
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BrowserInfo implements Serializable{

	public static final String USER_AGENT_HEADER_NAME = "User-Agent";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 
	public enum BrowserFamily{ IE, CHROME, FIREFOX, SAFARI, OPERA}

	private Double version;
	private String browserName;
	private BrowserFamily browserFamily;
	private String userAgentString;

	public Double getVersion() {
		return this.version;
	}
	public void setVersion(Double version) {
		this.version = version;
	}
	public String getBrowserName() {
		return this.browserName;
	}
	public void setBrowserName(String browserName) {
		this.browserName = browserName;
	}
	public BrowserFamily getBrowserFamily() {
		return this.browserFamily;
	}
	public void setBrowserFamily(BrowserFamily browserFamily) {
		this.browserFamily = browserFamily;
	}
	public String getUserAgentString() {
		return this.userAgentString;
	}
	public void setUserAgentString(String userAgentString) {
		this.userAgentString = userAgentString;
	}

	/***
	 * 
	 * Funzione che calcola lo user agent
	 * 
	 * @param Information
	 * @return Informazioni sul browser utilizzato
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

		if(info!=null) {
			browserInfo.setBrowserName(info[0]);
			try{
				if(StringUtils.isNotEmpty(info[1])) {
					String tmp = info[1];
					while(tmp.indexOf(".") != tmp.lastIndexOf(".")) {
						String pt1 = tmp.substring(0,tmp.lastIndexOf("."));
						String pt2 = "";
						// controllo che la stringa non finisca con un punto 
						if(tmp.substring(tmp.lastIndexOf(".")).length() > 1) {
							pt2 = tmp.substring(tmp.lastIndexOf(".") + 1);
						}
						tmp = pt1+pt2;
					}
				
					browserInfo.setVersion(StringUtils.isNotEmpty(tmp) ? Double.parseDouble(tmp) : null);
				} else {
					browserInfo.setVersion(null);
				}
			}catch(NumberFormatException e){
				// versione non riconosciuta
				browserInfo.setVersion(null);
			}
		}
		browserInfo.setBrowserFamily(bf); 

		return browserInfo;
	}


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
