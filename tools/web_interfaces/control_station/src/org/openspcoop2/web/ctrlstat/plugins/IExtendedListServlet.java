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

package org.openspcoop2.web.ctrlstat.plugins;

import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * IExtendedListServlet
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IExtendedListServlet extends IExtendedCoreServlet {

	public boolean showExtendedInfo(HttpServletRequest request,HttpSession session);
	
	public String getFormTitle(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	public String getFormTitleUrl(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	public List<Parameter> getFormTitleUrlParameters(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public String getFormItemTitle(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, IExtendedBean extendedBean);
	
	public String getListTitle(ConsoleHelper consoleHelper);
	
	public String getListTitle(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	public String getListTitleUrl(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	public List<Parameter> getListTitleUrlParameters(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public String getListItemTitle(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public void addToDati(Vector<DataElement> dati,TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	public void checkDati(TipoOperazione tipoOperazione, ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject,IExtendedBean extendedBean) throws ExtendedException;
	
	public void addDatiToList(Vector<DataElement> dati,TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, 
			Object originalObject, IExtendedBean extendedBean, UrlParameters urlExtendedChange);
	
	public int sizeList(Object originalObject);

	public ExtendedList extendedBeanList(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, Object originalObject, int limit, int offset, String search) throws ExtendedException; // ritorna la lista di oggetti extended associati che corrispondono ai criteri di filtro
	public List<IExtendedBean> extendedBeanList(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, Object originalObject) throws ExtendedException; // ritorna la lista completa di oggetti extended associati all'oggetto principale
	public String[] getColumnLabels(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper, ControlStationCore core, Object originalObject) throws ExtendedException; 
	
	public List<Parameter> getParameterForListElementIntoSession(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);
	
	public String getTestoModificaEffettuata(TipoOperazione tipoOperazione,ConsoleHelper consoleHelper);

}
