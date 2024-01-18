/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.protocol.sdk.properties;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * IConsoleHelper
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IConsoleHelper {

	public HttpServletRequest getRequest();
	public HttpSession getSession();
	public <T> T getAttributeFromSession(String attributeName, Class<T> type);
	
	public boolean isEditModeInProgress() throws Exception;
	public boolean isEditModeFinished() throws Exception;
	
	public boolean isPostBack() throws Exception;
	public String getPostBackElementName() throws Exception;
	public boolean isPostBackFilterElement() throws Exception;
	
	public Enumeration<?> getParameterNames() throws Exception;
	public String [] getParameterValues(String parameterName) throws Exception;
	public String getParameter(String parameterName) throws Exception;
	public <T> T getParameter(String parameterName, Class<T> type) throws Exception;
	public <T> T getParameter(String parameterName, Class<T> type, T defaultValue) throws Exception;
	public byte[] getBinaryParameterContent(String parameterName) throws Exception;
	public String getFileNameParameter(String parameterName) throws Exception;
	
	public boolean isModalitaCompleta();
	public boolean isModalitaAvanzata();
	public boolean isModalitaStandard();
	
	public String getSoggettoMultitenantSelezionato();
	
	public void setMessage(String message, boolean append) throws Exception;
	public void setMessage(String message, boolean append, String type) throws Exception;
}
