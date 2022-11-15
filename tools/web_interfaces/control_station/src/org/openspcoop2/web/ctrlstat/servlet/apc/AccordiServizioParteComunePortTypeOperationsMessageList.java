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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.ConsoleSearch;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * AccordiServizioParteComunePortTypeOperationsMessageList
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComunePortTypeOperationsMessageList extends Action {

		@Override
		public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

			HttpSession session = request.getSession(true);

			// Inizializzo PageData
			PageData pd = new PageData();

			// Inizializzo GeneralData
			GeneralHelper generalHelper = new GeneralHelper(session);

			GeneralData gd = generalHelper.initGeneralData(request);

			try {
				AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
				
				AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);
				
				String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
				int idAcc = Integer.parseInt(id);
				String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
				
				String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
				if("".equals(tipoAccordo))
					tipoAccordo = null;
				
				String nomeop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
				
				String tipoMessagge = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);

				boolean isMessageInput = tipoMessagge.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);
				
				// Preparo il menu
				apcHelper.makeMenu();

				// Prendo l'id del port-type e dell'azione
				int idOperation = 0;
				AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idAcc);
				for (int i = 0; i < as.sizePortTypeList(); i++) {
					PortType pt = as.getPortType(i);
					if (nomept.equals(pt.getNome())) {
						for (Operation azione : pt.getAzioneList()) {
							if(azione.getNome().equals(nomeop)){
								idOperation = azione.getId().intValue();
								break;
							}
						}
						
					}
				}

				// Preparo la lista
				ConsoleSearch ricerca = (ConsoleSearch) ServletUtils.getSearchObjectFromSession(request, session, ConsoleSearch.class);

				int idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_INPUT;
				if(!isMessageInput)
					idLista = Liste.ACCORDI_PORTTYPE_AZIONI_MESSAGE_OUTPUT;
				

				ricerca = apcHelper.checkSearchParameters(idLista, ricerca);

				List<MessagePart> lista = apcCore.accordiPorttypeOperationMessagePartList(idOperation, isMessageInput, ricerca);

				apcHelper.prepareAccordiPorttypeOperationMessagePartList(ricerca, lista, as, tipoAccordo,nomept, nomeop, isMessageInput);

				// salvo l'oggetto ricerca nella sessione
				ServletUtils.setSearchObjectIntoSession(request, session, ricerca);

				ServletUtils.setGeneralAndPageDataIntoSession(request, session, gd, pd);
				
				return ServletUtils.getStrutsForward(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.LIST());
				
			} catch (Exception e) {
				return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, request, session, gd, mapping, 
						AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.LIST());
			} 
		}
}
