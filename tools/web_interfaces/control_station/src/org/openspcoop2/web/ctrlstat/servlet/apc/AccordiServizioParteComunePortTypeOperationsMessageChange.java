/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.Message;
import org.openspcoop2.core.registry.MessagePart;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.ctrlstat.servlet.apc.api.ApiCostanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * AccordiServizioParteComunePortTypeOperationsMessageChange
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AccordiServizioParteComunePortTypeOperationsMessageChange  extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		// Inizializzo GeneralData
		GeneralHelper generalHelper = new GeneralHelper(session);

		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = (String) ServletUtils.getUserLoginFromSession(session);

		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String tipoAccordo = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			String tipoMessage = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);

			boolean isMessageInput = tipoMessage.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);

			String messagePartName = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME);
			String messagePartLocalName = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME);
			String messagePartNs = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS);
			String messagePartType = apcHelper.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome dal db
			AccordoServizioParteComune as = apcCore.getAccordoServizioFull(idInt);
			String labelASTitle = apcHelper.getLabelIdAccordo(as); 

			// Prendo il port-type e l'operation
			PortType pt = null;
			Operation operation = null;
			boolean found = false;
			for (int i = 0; i < as.sizePortTypeList(); i++) {
				pt = as.getPortType(i);
				if (nomept.equals(pt.getNome())){
					for (Operation opTmp : pt.getAzioneList()) {
						if(opTmp.getNome().equals(nomeop)){
							operation = opTmp;
							found = true;
							break;
						}
					}
				}
				if(found){
					break;
				}
			}

			Message m = null;
			if(isMessageInput)
				m = operation.getMessageInput();
			else 
				m = operation.getMessageOutput();

			MessagePart part = null;

			for (MessagePart partTmp : m.getPartList()) {
				if(partTmp.getName().equals(messagePartName)){
					part = partTmp;
					break;
				}
			}

			if(messagePartType == null)
				messagePartType = part.getTypeName() != null ? AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE :
					AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT;

			if(messagePartLocalName == null){
				if(messagePartType.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT))
					messagePartLocalName = part.getElementName();
				else 
					messagePartLocalName = part.getTypeName();
			}

			if(messagePartNs == null){
				if(messagePartType.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_ELEMENT))
					messagePartNs = part.getElementNamespace();
				else 
					messagePartNs = part.getTypeNamespace();
			}

			Parameter pIdAccordo  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID, id);
			Parameter pNomeAccordo  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_NOME, as.getNome());
			Parameter pNomePt  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME, nomept);
			Parameter pNomeOp  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME, nomeop);
			Parameter pTipoMsg  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE, tipoMessage);
			Parameter pTipoAccordo = AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo);
			
			Boolean isModalitaVistaApiCustom = ServletUtils.getBooleanAttributeFromSession(ApiCostanti.SESSION_ATTRIBUTE_VISTA_APC_API, session, false);
			List<Parameter> listaParams = apcHelper.getTitoloApc(TipoOperazione.ADD, as, tipoAccordo, labelASTitle, null, false);
		
			// porttypes
			String labelPortTypes = isModalitaVistaApiCustom ? AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES: AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + labelASTitle;
			listaParams.add(new Parameter(labelPortTypes, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo));

			// azioni
			String labelOperations = AccordiServizioParteComuneCostanti.LABEL_AZIONI  + " di " + nomept;
			listaParams.add(new Parameter(labelOperations, AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST, pIdAccordo, pNomeAccordo, pTipoAccordo,pNomePt));
			
			// azione
			String labelOperation = nomeop;
			listaParams.add(new Parameter(labelOperation,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE,pIdAccordo,pNomeOp,pNomePt,pTipoAccordo));

			String labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_INPUT;
			if(!isMessageInput){
				labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_OUTPUT;
			}
			
			labelMessage = isModalitaVistaApiCustom ? labelMessage : labelMessage + " di " + nomeop;

			listaParams.add(new Parameter(labelMessage,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST,	pIdAccordo,pNomeAccordo,pNomeOp, pNomePt , pTipoMsg,pTipoAccordo));
			listaParams.add(new Parameter(messagePartName, null));

			if(apcHelper.isEditModeInProgress()){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle( pd,listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiPorttypeOperationMessageToDati(TipoOperazione.CHANGE, dati,
						id, tipoAccordo, nomept, nomeop, messagePartName, messagePartType, messagePartLocalName, messagePartNs, tipoMessage);


				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.CHANGE());
			}

			// Controlli sui campi immessi
			boolean isOk = apcHelper.accordiPorttypeOperationMessageCheckData(TipoOperazione.CHANGE);
			if (!isOk) {

				// setto la barra del titolo
				ServletUtils.setPageDataTitle( pd,listaParams);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();

				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				dati = apcHelper.addAccordiPorttypeOperationMessageToDati(TipoOperazione.CHANGE, dati,
						id, tipoAccordo, nomept, nomeop, messagePartName, messagePartType, messagePartLocalName, messagePartNs, tipoMessage);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.CHANGE());
			}

			if(isMessageInput)
				m = operation.getMessageInput();
			else 
				m = operation.getMessageOutput();

			for (MessagePart partTmp : m.getPartList()) {
				if(partTmp.getName().equals(messagePartName)){
					part = partTmp;
					break;
				}
			}

			if(messagePartType.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE_TYPE)){
				part.setTypeName(messagePartLocalName);
				part.setTypeNamespace(messagePartNs);
				part.setElementName(null);
				part.setElementNamespace(null);
			}else {
				part.setTypeName(null);
				part.setTypeNamespace(null);
				part.setElementName(messagePartLocalName);
				part.setElementNamespace(messagePartNs);
			}


			// effettuo le operazioni
			apcCore.performUpdateOperation(userLogin, apcHelper.smista(), pt);

			// Preparo la lista
			Search ricerca = (Search) ServletUtils.getSearchObjectFromSession(session, Search.class);

			List<MessagePart> lista = apcCore.accordiPorttypeOperationMessagePartList(operation.getId().intValue(), isMessageInput, ricerca);

			apcHelper.prepareAccordiPorttypeOperationMessagePartList(ricerca, lista, as, tipoAccordo, nomept, nomeop, isMessageInput);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.CHANGE());

		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					AccordiServizioParteComuneCostanti.OBJECT_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE, ForwardParams.CHANGE());
		} 
	}

}
