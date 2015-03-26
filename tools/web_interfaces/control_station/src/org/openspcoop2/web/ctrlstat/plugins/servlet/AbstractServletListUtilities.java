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
