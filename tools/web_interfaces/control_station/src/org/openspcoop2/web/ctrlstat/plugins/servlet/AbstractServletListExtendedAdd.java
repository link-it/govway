/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.core.UrlParameters;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedException;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedList;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedBean;
import org.openspcoop2.web.ctrlstat.plugins.IExtendedListServlet;
import org.openspcoop2.web.ctrlstat.plugins.WrapperExtendedBean;
import org.openspcoop2.web.ctrlstat.servlet.ConsoleHelper;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AbstractServletListExtendedAdd
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public abstract class AbstractServletListExtendedAdd extends AbstractServletListUtilities {

	protected abstract void addToHiddenDati(Vector<DataElement> dati,ConsoleHelper consoleHelper) throws ExtendedException;
	
	protected abstract UrlParameters getUrlExtendedList(ConsoleHelper consoleHelper) throws Exception;
		
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);


		try {
			ConsoleHelper consoleHelper = this.getConsoleHelper(request, pd, session);
			
			ControlStationCore consoleCore = this.getConsoleCore();
			
			IExtendedListServlet extendedServlet = this.getExtendedServlet(consoleHelper);
			
			Object object = this.getObject(consoleHelper);
			
			IExtendedBean extendedBean = extendedServlet.readHttpParameters(object, TipoOperazione.ADD, null, request);
			
			// Preparo il menu
			consoleHelper.makeMenu();

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			if (consoleHelper.isEditModeInProgress()) {
	
				// setto la barra del titolo
				this.setFormTitle(object, consoleHelper, extendedServlet, extendedBean, pd, TipoOperazione.ADD, 
						this.getUrlExtendedList(consoleHelper));
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				this.addToHiddenDati(dati,consoleHelper);
				
				extendedServlet.addToDati(dati, TipoOperazione.ADD, consoleHelper, consoleCore, object, extendedBean);
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, 
						this.getObjectName(), 
						ForwardParams.ADD());

			}

			// Controlli sui campi immessi
			boolean isOk = true;
			try{
				extendedServlet.checkDati(TipoOperazione.ADD, consoleHelper, consoleCore, object, extendedBean);
			}catch(Exception e){
				isOk = false;
				pd.setMessage(e.getMessage());
			}
			if (!isOk) {

				// setto la barra del titolo
				this.setFormTitle(object, consoleHelper, extendedServlet, extendedBean, pd, TipoOperazione.ADD, 
						this.getUrlExtendedList(consoleHelper));
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				this.addToHiddenDati(dati,consoleHelper);
				
				extendedServlet.addToDati(dati, TipoOperazione.ADD, consoleHelper, consoleCore, object, extendedBean);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, 
						this.getObjectName(),
						ForwardParams.ADD());
			}

			// Rendo persistente la modifica
			String userLogin = ServletUtils.getUserLoginFromSession(session);

			WrapperExtendedBean wrapper = new WrapperExtendedBean();
			wrapper.setExtendedBean(extendedBean);
			wrapper.setExtendedServlet(extendedServlet);
			wrapper.setOriginalBean(object);
			wrapper.setManageOriginalBean(false);
			
			consoleCore.performCreateOperation(userLogin, consoleHelper.smista(), wrapper);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(request, session, Search.class);
			int idLista = this.getIdList();
			int limit = ricerca.getPageSize(idLista);
			int offset = ricerca.getIndexIniziale(idLista);
			String search = (org.openspcoop2.core.constants.Costanti.SESSION_ATTRIBUTE_VALUE_RICERCA_UNDEFINED.equals(ricerca.getSearchString(idLista)) ? "" : ricerca.getSearchString(idLista));
			ExtendedList extendedList = extendedServlet.extendedBeanList(TipoOperazione.ADD, consoleHelper, consoleCore,
					object, limit, offset, search);
			ricerca.setNumEntries(idLista,extendedList.getSize());

			this.prepareList(TipoOperazione.ADD, consoleHelper, ricerca, object, extendedServlet, extendedList.getExtendedBean(), ControlStationCore.getLog(),
					this.getUrlExtendedFather(consoleHelper));

			String msgCompletato = extendedServlet.getTestoModificaEffettuata(TipoOperazione.ADD, consoleHelper);
			if(msgCompletato!=null && !"".equals(msgCompletato)){
				pd.setMessage(msgCompletato,Costanti.MESSAGE_TYPE_INFO);
			}
			
			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, 
					this.getObjectName(),
					ForwardParams.ADD());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					this.getObjectName(), 
					ForwardParams.ADD());
		}  
	}

}
