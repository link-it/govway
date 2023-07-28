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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.DBManager;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedList;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AbstractServletListExtendedDel
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class AbstractServletListExtendedDel extends AbstractServletListUtilities {

	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
		// PageData pdold = ServletUtils.getObjectFromSession(request, session, PageData.class, Costanti.SESSION_ATTRIBUTE_PAGE_DATA);


		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			ConsoleHelper consoleHelper = this.getConsoleHelper(request, pd, session);
			
			String objToRemove = consoleHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			
			ControlStationCore consoleCore = this.getConsoleCore();
			
			IExtendedListServlet extendedServlet = this.getExtendedServlet(consoleHelper);
			
			Object object = this.getObject(consoleHelper);
			
			DBManager dbManager = null;
			Connection con = null;
			List<WrapperExtendedBean> listDati = new ArrayList<WrapperExtendedBean>();
			StringBuilder bfInUseTotal = new StringBuilder();
			try{
				dbManager = DBManager.getInstance();
				con = dbManager.getConnection();
				
				for (int i = 0; i < idsToRemove.size(); i++) {

					StringBuilder bfInUse = new StringBuilder();
					boolean inUse = extendedServlet.inUse(con, idsToRemove.get(i), bfInUse);
					if(inUse){
						
						if(bfInUseTotal.length()>0){
							bfInUseTotal.append(org.openspcoop2.core.constants.Costanti.WEB_NEW_LINE);
						}
						bfInUseTotal.append("- "+bfInUse.toString());
						
					}
					else{
					
						IExtendedBean extendedBean = extendedServlet.getExtendedBean(con, idsToRemove.get(i));
						
						WrapperExtendedBean wrapper = new WrapperExtendedBean();
						wrapper.setExtendedBean(extendedBean);
						wrapper.setExtendedServlet(extendedServlet);
						wrapper.setOriginalBean(object);
						wrapper.setManageOriginalBean(false);
						listDati.add(wrapper);
					
					}
						
				}
				
			}finally{
				dbManager.releaseConnection(con);
			}
			
			String msgErrore = "";
			if(bfInUseTotal.length()>0){
				if(listDati.size()>0){
					msgErrore = "Non è stato possibile completare l'eliminazione di tutti gli elementi selezionati:<br/>"+bfInUseTotal.toString();
				}
				else{
					msgErrore = "Non è stato possibile eliminare gli elementi selezionati:<br/>"+bfInUseTotal.toString();
				}
			}
			
			if(listDati.size()>0){
				String userLogin = ServletUtils.getUserLoginFromSession(session);
				consoleCore.performDeleteOperation(userLogin, consoleHelper.smista(), (Object[]) listDati.toArray(new WrapperExtendedBean[1]));
			}

			// Preparo il menu
			consoleHelper.makeMenu();

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
			int idLista = this.getIdList();
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			ExtendedList extendedList = extendedServlet.extendedBeanList(TipoOperazione.DEL, consoleHelper, consoleCore,
					object, limit, offset, search);
			ricerca.setNumEntries(idLista,extendedList.getSize());

			this.prepareList(TipoOperazione.DEL, consoleHelper, ricerca, object, extendedServlet, extendedList.getExtendedBean(), ControlStationCore.getLog(),
					this.getUrlExtendedFather(consoleHelper));

			String msgCompletato = extendedServlet.getTestoModificaEffettuata(TipoOperazione.DEL, consoleHelper);
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(msgErrore!=null && !"".equals(msgErrore)){
					if(listDati.size()>0){
						msgCompletato = msgCompletato+"<br/><br/>"+msgErrore;
					}
					else{
						msgCompletato = msgErrore;
					}
				}
			}
			else{
				msgCompletato = msgErrore;
			}
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				if(msgErrore!=null && !"".equals(msgErrore))
					pd.setMessage(msgCompletato);
				else
					pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			// Forward control to the specified success URI
		 	return ServletUtils.getStrutsForward(mapping, this.getObjectName(),
		 			ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					this.getObjectName(), 
					ForwardParams.DEL());
		}  
	}
}
