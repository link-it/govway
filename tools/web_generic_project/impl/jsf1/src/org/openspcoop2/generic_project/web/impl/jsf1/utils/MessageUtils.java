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
package org.openspcoop2.generic_project.web.impl.jsf1.utils;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * MessageUtils Classe di utilita' per i @{link {@link FacesMessage}
 *
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtils {

	public static void addInfoMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}

	public static void addErrorMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}

	public static void addWarnMsg(String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,message,null);
		FacesContext.getCurrentInstance().addMessage(null, m);
	}

	public static void addInfoMsg(String clientId, String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_INFO,message,null);
		FacesContext.getCurrentInstance().addMessage(clientId, m);
	}

	public static void addErrorMsg(String clientId, String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_ERROR,message,null);
		FacesContext.getCurrentInstance().addMessage(clientId, m);
	}

	public static void addWarnMsg(String clientId, String message){
		FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN,message,null);
		FacesContext.getCurrentInstance().addMessage(clientId, m);
	}
}