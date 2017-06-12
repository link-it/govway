/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it).
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
 * @author $Author: apoli $
 * @version $Rev: 12564 $, $Date: 2017-01-11 14:31:31 +0100(mer, 11 gen 2017) $
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
