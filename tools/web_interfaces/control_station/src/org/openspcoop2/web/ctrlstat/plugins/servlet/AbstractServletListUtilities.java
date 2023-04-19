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

package org.openspcoop2.web.ctrlstat.plugins.servlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.apache.struts.action.Action;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;


/**
 * AbstractServletListUtilities
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class AbstractServletListUtilities extends Action {

	protected abstract UrlParameters getUrlExtendedFather(ConsoleHelper consoleHelper) throws Exception;
	
	protected abstract ConsoleHelper getConsoleHelper(HttpServletRequest request, PageData pd, HttpSession session) throws Exception;
	
	protected abstract ControlStationCore getConsoleCore() throws Exception;
	
	protected abstract IExtendedListServlet getExtendedServlet(ConsoleHelper consoleHelper) throws Exception;
	
	protected abstract Object getObject(ConsoleHelper consoleHelper) throws Exception;
	
	protected abstract List<Parameter> getTitle(Object object, ConsoleHelper consoleHelper) throws Exception;
	
	protected abstract int getIdList() throws Exception;
	
	protected abstract Parameter[] getParameterList(ConsoleHelper consoleHelper) throws Exception;
	
	protected abstract String getObjectName() throws Exception;
	
	protected abstract UrlParameters getUrlExtendedChange(ConsoleHelper consoleHelper) throws Exception;
	
	protected void prepareList(TipoOperazione tipoOperazione, ConsoleHelper consoleHelper, ConsoleSearch ricerca, Object object, IExtendedListServlet extendedServlet, List<IExtendedBean> lista,
			Logger log, UrlParameters extendedFather)
			throws Exception {
		try {

			HttpSession session = consoleHelper.getSession();
			HttpServletRequest request = consoleHelper.getRequest();
			PageData pd = consoleHelper.getPd();
			ControlStationCore consoleCore = consoleHelper.getCore();
			
			List<Parameter> newLista = new ArrayList<Parameter>();
			
			Parameter[] pSession = this.getParameterList(consoleHelper);
			if(pSession!=null && pSession.length>0){
				for (int i = 0; i < pSession.length; i++) {
					newLista.add(pSession[i]);
				}
			}
			
			List<Parameter> p = extendedServlet.getParameterForListElementIntoSession(tipoOperazione,consoleHelper);
			if(p!=null && p.size()>0){
				for (Parameter parameter : p) {
					newLista.add(parameter);
				}
			}
			
			String tmp = consoleHelper.getRequest().getParameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
			if(tmp!=null && !"".equals(tmp)){
				newLista.add(new Parameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID, tmp));
			}
			
			Parameter[] pTmp = null;
			if(newLista.size()>0){
				pTmp = newLista.toArray(new Parameter[1]);
			}
			
			ServletUtils.addListElementIntoSession(request, session, this.getObjectName(),
					pTmp);

			int idLista = this.getIdList();
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = ServletUtils.getSearchFromSession(ricerca, idLista);

			pd.setIndex(offset);
			pd.setPageSize(limit);
			pd.setNumEntries(ricerca.getNumEntries(idLista));

			// setto la barra del titolo 1
			List<Parameter> lstParam = this.getTitle(object,consoleHelper);
			List<Parameter> listP = new ArrayList<Parameter>();
			if(extendedFather.sizeParameter()>0){
				listP.addAll(extendedFather.getParameter());
			}
			String uniqueId = consoleHelper.getRequest().getParameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
			if(uniqueId!=null && !"".equals(uniqueId)){
				listP.add(new Parameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID, uniqueId));
			}
			List<Parameter> listPTitleDynamic = extendedServlet.getListTitleUrlParameters(tipoOperazione, consoleHelper);
			if(listPTitleDynamic!=null && listPTitleDynamic.size()>0){
				listP.addAll(listPTitleDynamic);
			}
			Parameter [] pLinkTitle = null;
			if(listP.size()>0){
				pLinkTitle = listP.toArray(new Parameter[1]);
			}
			String title1 = extendedServlet.getListTitle(tipoOperazione,consoleHelper);
			
			String urlTitolo1 = extendedServlet.getListTitleUrl(tipoOperazione, consoleHelper);
			if(urlTitolo1==null || "".equals(urlTitolo1)){
				urlTitolo1 = extendedFather.getUrl();
			}
			
			// Titolo 2
			String title2 = extendedServlet.getListItemTitle(tipoOperazione,consoleHelper);
			
			if(search.equals("")){
				pd.setSearchDescription("");
				lstParam.add(new Parameter(title1, urlTitolo1,pLinkTitle));
				if(title2!=null){
					lstParam.add(new Parameter(title2, null));
				}
			}
			else{
				lstParam.add(new Parameter(title1, urlTitolo1,pLinkTitle));
				if(title2!=null){
					lstParam.add(new Parameter(title2, null));
				}
				lstParam.add(new Parameter(AbstractServletCostanti.RISULTATI_RICERCA, null));
			}

			ServletUtils.setPageDataTitle(pd, lstParam.toArray(new Parameter[lstParam.size()]));

			// controllo eventuali risultati ricerca
			if (!search.equals("")) {
				ServletUtils.enabledPageDataSearch(pd, "Elementi", search);
			}

			// setto le label delle colonne
			String[] labels = extendedServlet.getColumnLabels(tipoOperazione, consoleHelper, consoleCore, object);
			pd.setLabels(labels);

			// preparo i dati
			List<List<DataElement>> dati = new ArrayList<>();

			pd.setAddButton(true); // prima della chiamata ad addDatiToList, in modo da permettere di disattivarlo
			
			if (lista != null) {
				Iterator<IExtendedBean> it = lista.iterator();
				while (it.hasNext()) {
					IExtendedBean extendendBean = it.next();

					List<DataElement> e = new ArrayList<DataElement>();

					extendedServlet.addDatiToList(e, tipoOperazione, consoleHelper, consoleCore, object, extendendBean, 
							this.getUrlExtendedChange(consoleHelper));
										
					dati.add(e);
				}
			}

			pd.setDati(dati);
			
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
			throw new Exception(e);
		}
	}
	
	protected void setFormTitle(Object object,ConsoleHelper consoleHelper,
			IExtendedListServlet extendedServlet,IExtendedBean extendedBean,PageData pd,TipoOperazione tipoOperazione,UrlParameters extendedList) throws Exception{
		List<Parameter> lstParam = this.getTitle(object,consoleHelper);
		
		List<Parameter> listP = new ArrayList<Parameter>();
		if(extendedList.sizeParameter()>0){
			listP.addAll(extendedList.getParameter());
		}
		String uniqueId = consoleHelper.getRequest().getParameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID);
		if(uniqueId!=null && !"".equals(uniqueId)){
			listP.add(new Parameter(CostantiControlStation.PARAMETRO_EXTENDED_FORM_ID, uniqueId));
		}
		List<Parameter> listPTitleDynamic = extendedServlet.getFormTitleUrlParameters(tipoOperazione, consoleHelper);
		if(listPTitleDynamic!=null && listPTitleDynamic.size()>0){
			listP.addAll(listPTitleDynamic);
		}
		
		Parameter [] p = null;
		if(listP.size()>0){
			p = listP.toArray(new Parameter[1]);
		}
		
		String urlTitolo1 = extendedServlet.getFormTitleUrl(tipoOperazione, consoleHelper);
		if(urlTitolo1==null || "".equals(urlTitolo1)){
			urlTitolo1 = extendedList.getUrl();
		}
		
		lstParam.add(new Parameter(extendedServlet.getFormTitle(tipoOperazione, consoleHelper),urlTitolo1,p));
		
		lstParam.add(new Parameter(extendedServlet.getFormItemTitle(tipoOperazione, consoleHelper,extendedBean), null));

		ServletUtils.setPageDataTitle(pd, lstParam);
	}
}
