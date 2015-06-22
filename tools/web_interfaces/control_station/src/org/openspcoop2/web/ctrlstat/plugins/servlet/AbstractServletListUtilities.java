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

package org.openspcoop2.web.ctrlstat.plugins.servlet;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;


/**
 * AbstractServletListUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractServletListUtilities extends Action {

	protected abstract ConsoleHelper getConsoleHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception;
	
	protected abstract ControlStationCore getConsoleCore() throws Exception;
	
	protected abstract IExtendedListServlet getExtendedServlet(ControlStationCore core) throws Exception;
	
	protected abstract Object getObject(ControlStationCore core, HttpServletRequest request) throws Exception;
	
	protected abstract List<Parameter> getTitle(Object object, HttpServletRequest request, HttpSession session) throws Exception;
	
	protected abstract int getIdList() throws Exception;
	
	protected abstract Parameter[] getParameterList(HttpServletRequest request, HttpSession session) throws Exception;
	
	protected abstract String getObjectName() throws Exception;
	
	protected abstract UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper,HttpServletRequest request) throws Exception;
	
	protected void prepareList(ConsoleHelper consoleHelper, Search ricerca, Object object, IExtendedListServlet extendedServlet, List<IExtendedBean> lista,
			Logger log, HttpServletRequest request)
			throws Exception {
		try {

			HttpSession session = consoleHelper.getSession();
			PageData pd = consoleHelper.getPd();
			ControlStationCore consoleCore = consoleHelper.getCore();
			
			ServletUtils.addListElementIntoSession(session, this.getObjectName(),
					this.getParameterList(request,session));

			int idLista = this.getIdList();
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			pd.setIndex(offset);
			pd.setPageSize(limit);
			pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo
			List<Parameter> lstParam = this.getTitle(object,request, session);

			if(search.equals("")){
				pd.setSearchDescription("");
				lstParam.add(new Parameter(extendedServlet.getListTitle(), null));
			}
			else{
				lstParam.add(new Parameter(extendedServlet.getListTitle(), null));
				lstParam.add(new Parameter(AbstractServletCostanti.RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				pd.setSearch("on");
				pd.setSearchDescription("Elementi contenenti la stringa '" + search + "'");
			}

			// setto le label delle colonne
			String[] labels = extendedServlet.getColumnLabels();
			pd.setLabels(labels);

			// preparo i dati
			Vector<Vector<DataElement>> dati = new Vector<Vector<DataElement>>();

			if (lista != null) {
				Iterator<IExtendedBean> it = lista.iterator();
				while (it.hasNext()) {
					IExtendedBean extendendBean = it.next();

					Vector<DataElement> e = new Vector<DataElement>();

					extendedServlet.addDatiToList(e, consoleHelper, consoleCore, object, extendendBean, 
							this.getUrlExtendedChange(consoleHelper, request));
										
					dati.addElement(e);
				}
			}

			pd.setDati(dati);
			pd.setAddButton(true);
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
}
