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
package org.openspcoop2.generic_project.web.utils;

import java.io.Serializable;

/***
 * 
 * BrowserInfo Utilities per il supporto multibrowser.
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BrowserInfo implements Serializable{

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

}
