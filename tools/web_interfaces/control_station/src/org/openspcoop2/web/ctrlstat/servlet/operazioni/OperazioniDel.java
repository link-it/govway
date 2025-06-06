/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.queue.costanti.OperationStatus;
import org.openspcoop2.web.lib.queue.dao.Operation;

/**
 * OperazioniDel
 *
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperazioniDel  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		try {
			OperazioniHelper opHelper = new OperazioniHelper(request, pd, session);
			
			ArrayList<String> errors = new ArrayList<>();
			OperazioniFormBean formBean = opHelper.getBeanForm(errors );
			
			OperazioniCore opCore = new OperazioniCore();

			String objToRemove =  opHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE); 
			
			ArrayList<String> idsToRemove = Utilities.parseIdsToRemove(objToRemove);
			for (int i = 0; i < idsToRemove.size(); i++) {

				// Elimino l'operazione
				Operation operation = opCore.getOperation(Long.parseLong(idsToRemove.get(i)));

				// Rimozione operazione
				operation.setStatus(OperationStatus.SUCCESS);
				operation.setDeleted(true);

				opCore.updateOperation(operation);
 
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

			return ServletUtils.getStrutsForward (mapping, OperazioniCostanti.OBJECT_NAME_OPERAZIONI,ForwardParams.DEL());
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					OperazioniCostanti.OBJECT_NAME_OPERAZIONI, ForwardParams.DEL());
		}
	}
}
 