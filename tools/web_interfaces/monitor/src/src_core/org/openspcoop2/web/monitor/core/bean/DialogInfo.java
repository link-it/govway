/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.monitor.core.bean;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.web.monitor.core.constants.Costanti;

/**
 * DialogInfo
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DialogInfo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean resizeable;
	private boolean moveable;
	private String width;
	private String height;
	private String headerFinestraModale;
	private String headerBody;
	private List<String> listBody;
	private String body;
	private String buttonIcon;
	
	public DialogInfo(String headerFinestraModale) {
		this.headerFinestraModale = headerFinestraModale;
		this.resizeable = false;
		this.moveable = false;
		this.width = "500";
		this.height = "300";
		this.buttonIcon = Costanti.ICONA_INFO_WHITE;
	}
	
	public String getHeaderFinestraModale() {
		return this.headerFinestraModale;
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
	
	public String getListBodyHtml() {
		StringBuilder sb = new StringBuilder();
		
		if(this.getListBody() != null && !this.getListBody().isEmpty()) {
			sb.append("<ul>");
			
			for (String info : this.getListBody()) {
				sb.append("<li>").append(info).append("</li>");
			}
					
			sb.append("</ul>");		
		}
		return sb.toString();
	}

	public void setListBody(List<String> listBody) {
		this.listBody = listBody;
	}

	public String getBody() {
		StringBuilder sb = new StringBuilder();
		
		
		if(StringUtils.isNotEmpty(this.body)) {
			sb.append(this.body);
		} else {
			sb.append("<p>");
			
			boolean addBreak = false;
			if(StringUtils.isNotEmpty(this.getHeaderBody())) {
				sb.append("<span>").append(this.getHeaderBody()).append("</span>");
				addBreak = true;
			}
			
			if(StringUtils.isNotEmpty(this.getListBodyHtml())) {
				if(addBreak)
					sb.append("<br/>");
				
				sb.append(this.getListBodyHtml());
			}
			
			sb.append("</p>");
		}
		
		return sb.toString();
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getButtonIcon() {
		return this.buttonIcon;
	}

	public void setButtonIcon(String buttonIcon) {
		this.buttonIcon = buttonIcon;
	}
	public boolean isResizeable() {
		return this.resizeable;
	}
	public void setResizeable(boolean resizeable) {
		this.resizeable = resizeable;
	}
	public boolean isMoveable() {
		return this.moveable;
	}
	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	public String getWidth() {
		return this.width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return this.height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	
	
}
