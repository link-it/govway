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

package org.openspcoop2.web.ctrlstat.servlet.operazioni;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.queue.dao.Operation;

/**
 * Operazioni
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Operazioni extends Action{

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Salvo il vecchio PageData
	//	PageData pdold = ServletUtils.getPageDataFromSession(session);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			OperazioniCore opCore = new OperazioniCore();

			OperazioniHelper opHelper = new OperazioniHelper(request, pd, session);

			ArrayList<String> errors = new ArrayList<>();

			OperazioniFormBean formBean = opHelper.getBeanForm(errors);
			
			String metodo = opHelper.getParameter(OperazioniCostanti.PARAMETRO_OPERAZIONI_METHOD);

			// prima volta che accedo quindi show form
			if (opHelper.isEditModeInProgress() && (metodo == null || metodo.equals(OperazioniCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_FORM))){
				opHelper.makeMenu();

				opHelper.showForm("", "", formBean);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,
						OperazioniCostanti.OBJECT_NAME_OPERAZIONI,OperazioniCostanti.TIPO_OPERAZIONE_OPERAZIONI);
			}

			// controllo se visualizzazione dettaglio operazione
			if (formBean.getMethod().equals(OperazioniCostanti.DEFAULT_VALUE_FORM_BEAN_METHOD_DETAILS)) {

				OperazioniFormBean oldFormBeanRicerca = ServletUtils.getObjectFromSession(request, session, OperazioniFormBean.class, OperazioniCostanti.SESSION_ATTRIBUTE_FORM_BEAN);

				String idOperazione = formBean.getIdOperazione();

				long idOp = -1; 

				try{
					idOp = Long.parseLong(idOperazione);
				}catch (Exception e){
					
				}

				Operation operazione = opCore.getOperation(idOp);

				opHelper.makeMenu();

				if (operazione  != null) {
					// visualizzo dettagli Operazione
					opHelper.showDettagliMessaggio(operazione, oldFormBeanRicerca);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeFinished(mapping,
							OperazioniCostanti.OBJECT_NAME_OPERAZIONI, 
							OperazioniCostanti.TIPO_OPERAZIONE_DETAIL);
				} else {
					// non ho l'operazione
					pd.setMessage("L'operazione [" + formBean.getIdOperazione() + "] non e' piu presente. "
							+ "Impossibile visualizzare il dettaglio.");
					// rinvio alla visulizzazione del form in quanto il
					// messaggio non e' piu presente


					opHelper.showForm("", "", formBean);

					ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping, OperazioniCostanti.OBJECT_NAME_OPERAZIONI, 
							OperazioniCostanti.TIPO_OPERAZIONE_DETAIL);
				}
			}  

			opHelper.makeMenu();

			// Eseguo la ricerca
			int idLista = opCore.getIdLista(formBean);
			
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

			ricerca = opHelper.checkSearchParameters(idLista, ricerca);
			List<Operation> lista = opCore.operationsList(ricerca,formBean,ServletUtils.getUserLoginFromSession(session));

			opHelper.prepareOperazioniList(ricerca, lista);

			// salvo l'oggetto ricerca nella sessione
			ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

			ServletUtils.setObjectIntoSession(request, session, formBean, OperazioniCostanti.SESSION_ATTRIBUTE_FORM_BEAN);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);

			return ServletUtils.getStrutsForward(mapping, OperazioniCostanti.OBJECT_NAME_OPERAZIONI,
					ForwardParams.LIST());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					OperazioniCostanti.OBJECT_NAME_OPERAZIONI, 
					OperazioniCostanti.TIPO_OPERAZIONE_OPERAZIONI);
		}  

	}
}
