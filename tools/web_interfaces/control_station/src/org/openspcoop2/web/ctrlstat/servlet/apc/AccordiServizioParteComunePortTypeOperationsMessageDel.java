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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.govway.struts.action.Action;
import org.govway.struts.action.ActionForm;
import org.govway.struts.action.ActionForward;
import org.govway.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.core.Utilities;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AccordiServizioParteComunePortTypeOperationsMessageDel
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComunePortTypeOperationsMessageDel extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
			
			// Preparo il menu
			apcHelper.makeMenu();
			
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();

			String id = apcHelper.getParametroLong(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			long idAccordoLong = Long.parseLong(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String objToRemove = apcHelper.getParameter(Costanti.PARAMETER_NAME_OBJECTS_FOR_REMOVE);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			String tipoMessagge = apcHelper.getParametroPortTypeOperationMessageType(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);

			boolean isMessageInput = tipoMessagge.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);
						
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			ArrayList<String> optsToRemove = Utilities.parseIdsToRemove(objToRemove);
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAccordoLong);

			// Prendo il port-type e l'operation
			PortType pt = null;
			Operation operation = null;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomept.equals(pt.getNome())){
					for (Operation opTmp : pt.getAzioneList()) {
						if(opTmp.getNome().equals(nomeop)){
							operation = opTmp;
							break;
						}
					}
				}
			}

			String nomeopToRemove = "";

			for (int i = 0; i < optsToRemove.size(); i++) {

				nomeopToRemove = optsToRemove.get(i);
				
				Message m = null;
				if(isMessageInput)
					m = operation.getMessageInput();
				else 
					m = operation.getMessageOutput();
				
				for (int j = 0; j < m.sizePartList(); j++) {
					MessagePart mp = m.getPart(j);
					if (nomeopToRemove.equals(mp.getName())) {
						m.removePart(j);
						break;
					}
				}
			}

			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);

			// Preparo la lista
			ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);
					
			List<MessagePart> lista = apcCore.accordiPorttypeOperationMessagePartList(operation.getId().intValue(), isMessageInput, ricerca);

			apcHelper.prepareAccordiPorttypeOperationMessagePartList(ricerca, lista, as, tipoAccordo, nomept, nomeop, isMessageInput);

			ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
			
			return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.DEL());
			
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.DEL());
		} 
	}

}
