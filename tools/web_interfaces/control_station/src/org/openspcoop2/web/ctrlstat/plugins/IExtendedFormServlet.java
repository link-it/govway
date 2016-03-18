/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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

package org.openspcoop2.web.ctrlstat.plugins;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * IExtendedFormServlet
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IExtendedFormServlet extends IExtendedCoreServlet {

	public boolean showExtendedInfoAdd(HttpServletRequest request,HttpSession session);
	public boolean showExtendedInfoUpdate(HttpServletRequest request,HttpSession session);
	public boolean extendedUpdateToNewWindow(HttpServletRequest request,HttpSession session); // la gestione dei dati avviene all'interno di una nuova finestra.
		
	public String getFormTitle(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public void addToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	// metodi utilizzati solo se extendedUpdateToNewWindow() == 'true'
	public void addToDatiNewWindow(Vector<DataElement> dati,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject, IExtendedBean extendedBean, String urlExtendedPage) throws ExtendedException;
	
	public void checkDati(TipoOperazione tipoOperazione, ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	public String getTestoModificaEffettuata(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public String getUniqueID();
	
	public IExtendedListServlet getExtendedInternalList(); // dovrebbe essere associato allo uniqueID per gestire liste interne
	
}
