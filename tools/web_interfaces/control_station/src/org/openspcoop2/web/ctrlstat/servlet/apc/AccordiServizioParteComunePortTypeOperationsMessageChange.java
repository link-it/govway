/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.apc;

import java.util.ArrayList;
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
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
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

		IDAccordoFactory idAccordoFactory = IDAccordoFactory.getInstance();

		try {
			AccordiServizioParteComuneCore apcCore = new AccordiServizioParteComuneCore();
			AccordiServizioParteComuneHelper apcHelper = new AccordiServizioParteComuneHelper(request, pd, session);

			String id = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_ID);
			int idInt = Integer.parseInt(id);
			String nomept = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPES_NOME);
			String nomeop = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_NOME);
			String tipoAccordo = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_TIPO_ACCORDO);
			if("".equals(tipoAccordo))
				tipoAccordo = null;

			String tipoMessage = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE);

			boolean isMessageInput = tipoMessage.equals(AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);

			String messagePartName = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NAME);
			String messagePartLocalName = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_LOCAL_NAME);
			String messagePartNs = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_NS);
			String messagePartType = request.getParameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_PART_TYPE);

			// Preparo il menu
			apcHelper.makeMenu();

			// Prendo il nome dal db
			AccordoServizioParteComune as = apcCore.getAccordoServizio(idInt);
			String uriAS = idAccordoFactory.getUriFromAccordo(as);

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
			Parameter pTipoMsg  = new Parameter(AccordiServizioParteComuneCostanti.PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_TYPE, 
					AccordiServizioParteComuneCostanti.DEFAULT_VALUE_PARAMETRO_APC_PORT_TYPE_OPERATION_MESSAGE_INPUT);

			// Se idhid = null, devo visualizzare la pagina per l'inserimento
			// dati
			List<Parameter> listaParametri = new ArrayList<Parameter>();

			listaParametri.add(new Parameter(AccordiServizioParteComuneUtilities.getTerminologiaAccordoServizio(tipoAccordo),null));
			listaParametri.add(new Parameter(Costanti.PAGE_DATA_TITLE_LABEL_ELENCO, 
					AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_LIST , AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(new Parameter(AccordiServizioParteComuneCostanti.LABEL_PORT_TYPES + " di " + uriAS,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPES_LIST,
					pIdAccordo,pNomeAccordo,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(	new Parameter(AccordiServizioParteComuneCostanti.LABEL_AZIONI + " di " + nomept,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_LIST,
					pIdAccordo,pNomeAccordo,pNomePt,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			listaParametri.add(	new Parameter(nomeop,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_CHANGE,
					pIdAccordo,pNomeOp,pNomePt,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));

			String labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_INPUT;
			if(!isMessageInput){
				labelMessage = AccordiServizioParteComuneCostanti.LABEL_OPERATION_MESSAGE_OUTPUT;
			}

			// setto la barra del titolo			

			listaParametri.add(	new Parameter(labelMessage + " di " + nomeop,AccordiServizioParteComuneCostanti.SERVLET_NAME_APC_PORT_TYPE_OPERATIONS_MESSAGE_LIST,
					pIdAccordo,pNomeAccordo,pNomePt, pNomePt, pNomeOp, pTipoMsg,AccordiServizioParteComuneUtilities.getParametroAccordoServizio(tipoAccordo)));
			listaParametri.add (new Parameter(messagePartName, null));


			if(ServletUtils.isEditModeInProgress(request)){

				// setto la barra del titolo
				ServletUtils.setPageDataTitle( pd,listaParametri);

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
				ServletUtils.setPageDataTitle( pd,listaParametri);

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
