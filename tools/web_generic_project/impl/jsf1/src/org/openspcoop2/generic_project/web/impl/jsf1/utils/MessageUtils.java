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