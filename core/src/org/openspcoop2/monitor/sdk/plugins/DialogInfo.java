/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
package org.openspcoop2.monitor.sdk.plugins;

import java.util.List;

/**
 * DialogInfo
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DialogInfo {
	
	public DialogInfo() {	
	}
	public DialogInfo(String header) {
		this.header = header;
	}
	private String header;
	
	private String headerBody;
	
	private List<String> listBody;
	
	private String body;
	
	public String getHeader() {
		return this.header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getHeaderBody() {
		return this.headerBody;
	}
	public void setHeaderBody(String headerBody) {
		this.headerBody = headerBody;
	}
	public List<String> getListBody() {
		return this.listBody;
	}
	public void setListBody(List<String> listBody) {
		this.listBody = listBody;
	}
	public String getBody() {
		return this.body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
}
