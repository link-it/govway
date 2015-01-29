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
package org.openspcoop2.generic_project.web.presentation.field;

import org.openspcoop2.generic_project.web.core.Utils;

/**
*
* OutputIcon (parent {@link OutputField}) Visualizza un tipo di output con icona .
* icon = path all'immagine da utilizzare;
* iconTitle descrizione da visualizzare quando si passa il cursore sull'icona;
* 
* @author Pintori Giuliano (pintori@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class OutputIcon extends OutputField<String> {

	public OutputIcon(){
		super();
		this.setType("icon"); 
		this.setInsideGroup(false);
	}
	
	private String icon = null;
	private String iconTitle = null;
	private String alt = null;
	
	public String getIcon() {
		return this.icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconTitle() {
		try{
			String tmp = Utils.getMessageFromResourceBundle(this.iconTitle);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.iconTitle;
	}

	public void setIconTitle(String iconTitle) {
		this.iconTitle = iconTitle;
	}
	public String getAlt() {
		try{
			String tmp = Utils.getMessageFromResourceBundle(this.alt);

			if(tmp != null && !tmp.startsWith("?? key ") && !tmp.endsWith(" not found ??"))
				return tmp;
		}catch(Exception e){}
		
		return this.alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	
	

}
