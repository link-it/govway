/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.ctrlstat.servlet.pd;

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.costanti.CostantiControlStation;
import org.openspcoop2.web.ctrlstat.servlet.GeneralHelper;
import org.openspcoop2.web.lib.mvc.Costanti;
import org.openspcoop2.web.lib.mvc.DataElement;
import org.openspcoop2.web.lib.mvc.ForwardParams;
import org.openspcoop2.web.lib.mvc.GeneralData;
import org.openspcoop2.web.lib.mvc.MessageType;
import org.openspcoop2.web.lib.mvc.PageData;
import org.openspcoop2.web.lib.mvc.Parameter;
import org.openspcoop2.web.lib.mvc.ServletUtils;
import org.openspcoop2.web.lib.mvc.TipoOperazione;

/**
 * PorteDelegateDumpConfigurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class PorteDelegateDumpConfigurazione extends Action {

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		TipoOperazione tipoOperazione = TipoOperazione.OTHER;

		HttpSession session = request.getSession(true);

		// Inizializzo PageData
		PageData pd = new PageData();

		GeneralHelper generalHelper = new GeneralHelper(session);

		// Inizializzo GeneralData
		GeneralData gd = generalHelper.initGeneralData(request);

		String userLogin = ServletUtils.getUserLoginFromSession(session);	

		// prelevo il flag che mi dice da quale pagina ho acceduto la sezione delle porte delegate
		Integer parentPD = ServletUtils.getIntegerAttributeFromSession(PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT, session);
		if(parentPD == null) parentPD = PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_NONE;

		try {
			PorteDelegateHelper porteDelegateHelper = new PorteDelegateHelper(request, pd, session);
			String id = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID);
			int idInt = Integer.parseInt(id);
			String idSoggFruitore = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_SOGGETTO);
			String idAsps = porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";
			String idFruizione= porteDelegateHelper.getParameter(PorteDelegateCostanti.PARAMETRO_PORTE_DELEGATE_ID_FRUIZIONE);
			if(idFruizione == null) 
				idFruizione = "";
			
			String idTab = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_ID_TAB);
			if(!porteDelegateHelper.isModalitaCompleta() && StringUtils.isNotEmpty(idTab)) {
				ServletUtils.setObjectIntoSession(session, idTab, CostantiControlStation.PARAMETRO_ID_TAB);
			}

			// Preparo il menu
			porteDelegateHelper.makeMenu();
			
			// Prendo il nome della porta
			PorteDelegateCore porteDelegateCore = new PorteDelegateCore();

			PortaDelegata portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			String idporta = portaDelegata.getNome();
			
			boolean showStato = true;
			String statoDump = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_STATO);
			boolean showRealtime = porteDelegateCore.isDump_showConfigurazioneDumpRealtime();
			String realtime = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_REALTIME);
			String statoDumpRichiesta = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO);
			String statoDumpRisposta = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO);
			
			String dumpRichiestaIngressoHeader = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
			String dumpRichiestaIngressoPayload = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD);
			String dumpRichiestaIngressoPayloadParsing = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING);
			String dumpRichiestaIngressoBody = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
			String dumpRichiestaIngressoAttachments = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
			
			String dumpRichiestaUscitaHeader = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
			String dumpRichiestaUscitaPayload = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD);
			String dumpRichiestaUscitaPayloadParsing = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING);
			String dumpRichiestaUscitaBody = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
			String dumpRichiestaUscitaAttachments = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
			
			String dumpRispostaIngressoHeader = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
			String dumpRispostaIngressoPayload = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD);
			String dumpRispostaIngressoPayloadParsing = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING);
			String dumpRispostaIngressoBody = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
			String dumpRispostaIngressoAttachments = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
			
			String dumpRispostaUscitaHeader = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
			String dumpRispostaUscitaPayload = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD);
			String dumpRispostaUscitaPayloadParsing = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING);
			String dumpRispostaUscitaBody = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
			String dumpRispostaUscitaAttachments = porteDelegateHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
			String actionConferma = porteDelegateHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			DumpConfigurazione oldConfigurazione = portaDelegata.getDump();
			
			boolean initConfigurazione = false;
			String postBackElementName = porteDelegateHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_DUMP_STATO)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO)) {
					if(!porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) && statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						
						dumpRichiestaIngressoHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRichiestaIngressoPayload = StatoFunzionalita.ABILITATO.getValue();
						dumpRichiestaIngressoPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRichiestaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRichiestaUscitaPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO)) {
					if(!porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) && statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						
						dumpRispostaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaIngressoPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRispostaUscitaHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRispostaUscitaPayload = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaUscitaPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}
			}
			
			// setto la barra del titolo
			List<Parameter> lstParam = porteDelegateHelper.getTitoloPD(parentPD, idSoggFruitore, idAsps, idFruizione);
			
			String labelPerPorta = null;
			if(parentPD!=null && (parentPD.intValue() == PorteDelegateCostanti.ATTRIBUTO_PORTE_DELEGATE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteDelegateCore.getLabelRegolaMappingFruizionePortaDelegata(
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE_CONFIG_DI,
						PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE,
						portaDelegata);
			}
			else {
				labelPerPorta = PorteDelegateCostanti.LABEL_PARAMETRO_PORTE_DELEGATE_DUMP_CONFIGURAZIONE_CONFIG_DI+idporta;
			}
			
			lstParam.add(new Parameter(labelPerPorta,  null));
			
			// edit in progress
			if (porteDelegateHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(statoDump == null) {
					if(oldConfigurazione == null) {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
					} else {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRichiestaIngressoHeader = oldConfigurazione.getRichiestaIngresso().get_value_headers();	
						dumpRichiestaIngressoPayload = oldConfigurazione.getRichiestaIngresso().get_value_payload();
						dumpRichiestaIngressoPayloadParsing = oldConfigurazione.getRichiestaIngresso().get_value_payloadParsing();
						dumpRichiestaIngressoBody = oldConfigurazione.getRichiestaIngresso().get_value_body();
						dumpRichiestaIngressoAttachments = oldConfigurazione.getRichiestaIngresso().get_value_attachments();
						
						dumpRichiestaUscitaHeader = oldConfigurazione.getRichiestaUscita().get_value_headers();	
						dumpRichiestaUscitaPayload = oldConfigurazione.getRichiestaUscita().get_value_payload();
						dumpRichiestaUscitaPayloadParsing = oldConfigurazione.getRichiestaUscita().get_value_payloadParsing();
						dumpRichiestaUscitaBody = oldConfigurazione.getRichiestaUscita().get_value_body();
						dumpRichiestaUscitaAttachments = oldConfigurazione.getRichiestaUscita().get_value_attachments();
						
						dumpRispostaIngressoHeader = oldConfigurazione.getRispostaIngresso().get_value_headers();	
						dumpRispostaIngressoPayload = oldConfigurazione.getRispostaIngresso().get_value_payload();
						dumpRispostaIngressoPayloadParsing = oldConfigurazione.getRispostaIngresso().get_value_payloadParsing();
						dumpRispostaIngressoBody = oldConfigurazione.getRispostaIngresso().get_value_body();
						dumpRispostaIngressoAttachments = oldConfigurazione.getRispostaIngresso().get_value_attachments();
						
						dumpRispostaUscitaHeader = oldConfigurazione.getRispostaUscita().get_value_headers();	
						dumpRispostaUscitaPayload = oldConfigurazione.getRispostaUscita().get_value_payload();
						dumpRispostaUscitaPayloadParsing = oldConfigurazione.getRispostaUscita().get_value_payloadParsing();
						dumpRispostaUscitaBody = oldConfigurazione.getRispostaUscita().get_value_body();
						dumpRispostaUscitaAttachments = oldConfigurazione.getRispostaUscita().get_value_attachments();
					}
				}
				
				if(initConfigurazione) {
					if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
						oldConfigurazione = new DumpConfigurazione();
						oldConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
						
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteDelegateHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRichiestaIngressoHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRichiestaIngressoPayload = StatoFunzionalita.ABILITATO.getValue();
						dumpRichiestaIngressoPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRichiestaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRichiestaUscitaPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRispostaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaIngressoPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRispostaUscitaHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRispostaUscitaPayload = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaUscitaPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteDelegateHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
						dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
						false);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteDelegateHelper.checkDataConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				porteDelegateHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
						dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
						false);
				
				dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
						idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}
			
			boolean showConfermaRichiesta = false;
			boolean showConfermaRisposta = false;
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				// se ho abilitato entrambi i dump di ingresso e uscita per richiesta o risposta informo l'utente che lo spazio occupato sara' il doppio
				if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
					
					boolean bodyIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaIngressoPayload);
					boolean attachmentsIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaIngressoPayload);
					if(StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaIngressoPayloadParsing)) {
						bodyIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaIngressoBody);
						attachmentsIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaIngressoAttachments);
					}
					boolean bodyUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaUscitaPayload);
					boolean attachmentsUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaUscitaPayload);
					if(StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaUscitaPayloadParsing)) {
						bodyUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaUscitaBody);
						attachmentsUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRichiestaUscitaAttachments);
					}
					
					// doppio body
					if(bodyIngresso && bodyUscita) {
						showConfermaRichiesta = true;
					}
					
					// doppi attachments
					if(attachmentsIngresso && attachmentsUscita) {
						showConfermaRichiesta = true;
					}
				}
				
				// se ho abilitato entrambi i dump di ingresso e uscita per richiesta o risposta informo l'utente che lo spazio occupato sara' il doppio
				if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
					
					boolean bodyIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaIngressoPayload);
					boolean attachmentsIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaIngressoPayload);
					if(StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaIngressoPayloadParsing)) {
						bodyIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaIngressoBody);
						attachmentsIngresso = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaIngressoAttachments);
					}
					boolean bodyUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaUscitaPayload);
					boolean attachmentsUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaUscitaPayload);
					if(StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaUscitaPayloadParsing)) {
						bodyUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaUscitaBody);
						attachmentsUscita = StatoFunzionalita.ABILITATO.getValue().equals(dumpRispostaUscitaAttachments);
					}
					
					// doppio body
					if(bodyIngresso && bodyUscita) {
						showConfermaRisposta = true;
					}
					
					// doppi attachments
					if(attachmentsIngresso && attachmentsUscita) {
						showConfermaRisposta = true;
					}
				}
			}
			
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				if(showConfermaRichiesta  || showConfermaRisposta) {
					if(actionConferma == null) {
						
						ServletUtils.setPageDataTitle(pd, lstParam);
	
						// preparo i campi
						Vector<DataElement> dati = new Vector<DataElement>();
						dati.addElement(ServletUtils.getDataElementForEditModeFinished());
						
						porteDelegateHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
								dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
								dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
								dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
								dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
								false);
						
						
						porteDelegateHelper.addConfigurazioneDumpToDatiAsHidden(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
								dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
								dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
								dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
								dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
								false);
						
						dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
								idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);
						
						pd.setDati(dati);
						
						
						String msg ="";
						if(showConfermaRichiesta)
							msg = CostantiControlStation.LABEL_PARAMETRO_RICHIESTA;
	
						if(showConfermaRichiesta && showConfermaRisposta)
							msg += " e ";
						
						if(showConfermaRisposta)
							msg = CostantiControlStation.LABEL_PARAMETRO_RISPOSTA;
						
						String messaggio =  MessageFormat.format(CostantiControlStation.MESSAGGIO_CONFERMA_REGISTRAZIONE_MESSAGGI_DOPPIO_SPAZIO, msg);
						
						pd.setMessage(messaggio, MessageType.CONFIRM);
						
						String[][] bottoni = { 
								{ Costanti.LABEL_MONITOR_BUTTON_ANNULLA, 
									Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_PREFIX +
									Costanti.LABEL_MONITOR_BUTTON_ANNULLA_CONFERMA_SUFFIX
									
								},
								{ Costanti.LABEL_MONITOR_BUTTON_CONFERMA,
									Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_PREFIX +
									Costanti.LABEL_MONITOR_BUTTON_ESEGUI_OPERAZIONE_CONFERMA_SUFFIX }};
	
						pd.setBottoni(bottoni);
						
						// disabilito la form
						pd.disableEditMode();
	
						ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
	
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
					} 
				}
			}
			
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
				if( !(showConfermaRichiesta  || showConfermaRisposta) 
						|| 
					(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK))
						) {
					DumpConfigurazione newDumpConfigurazione = porteDelegateHelper.getConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime,
							statoDumpRichiesta, statoDumpRisposta, 
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
							dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
							
					portaDelegata.setDump(newDumpConfigurazione);

					porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
				
					// Preparo la lista
					pd.setMessage(PorteDelegateCostanti.LABEL_PORTE_DELEGATE_ARCHIVIO_MESSAGGI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
				}
			} else {
				portaDelegata.setDump(null);

				porteDelegateCore.performUpdateOperation(userLogin, porteDelegateHelper.smista(), portaDelegata);
				
				// Preparo la lista
				pd.setMessage(PorteDelegateCostanti.LABEL_PORTE_DELEGATE_ARCHIVIO_MESSAGGI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			}

			portaDelegata = porteDelegateCore.getPortaDelegata(idInt);
			idporta = portaDelegata.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());
			
			DumpConfigurazione configurazioneAggiornata = portaDelegata.getDump();
			
			if(configurazioneAggiornata == null) {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			} else {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
				realtime = configurazioneAggiornata.getRealtime().getValue();
				statoDumpRichiesta = porteDelegateHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				statoDumpRisposta = porteDelegateHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				
				dumpRichiestaIngressoHeader = configurazioneAggiornata.getRichiestaIngresso().get_value_headers();	
				dumpRichiestaIngressoPayload = configurazioneAggiornata.getRichiestaIngresso().get_value_payload();
				dumpRichiestaIngressoPayloadParsing = configurazioneAggiornata.getRichiestaIngresso().get_value_payloadParsing();
				dumpRichiestaIngressoBody = configurazioneAggiornata.getRichiestaIngresso().get_value_body();
				dumpRichiestaIngressoAttachments = configurazioneAggiornata.getRichiestaIngresso().get_value_attachments();
				
				dumpRichiestaUscitaHeader = configurazioneAggiornata.getRichiestaUscita().get_value_headers();	
				dumpRichiestaUscitaPayload = configurazioneAggiornata.getRichiestaUscita().get_value_payload();
				dumpRichiestaUscitaPayloadParsing = configurazioneAggiornata.getRichiestaUscita().get_value_payloadParsing();
				dumpRichiestaUscitaBody = configurazioneAggiornata.getRichiestaUscita().get_value_body();
				dumpRichiestaUscitaAttachments = configurazioneAggiornata.getRichiestaUscita().get_value_attachments();
				
				dumpRispostaIngressoHeader = configurazioneAggiornata.getRispostaIngresso().get_value_headers();	
				dumpRispostaIngressoPayload = configurazioneAggiornata.getRispostaIngresso().get_value_payload();
				dumpRispostaIngressoPayloadParsing = configurazioneAggiornata.getRispostaIngresso().get_value_payloadParsing();
				dumpRispostaIngressoBody = configurazioneAggiornata.getRispostaIngresso().get_value_body();
				dumpRispostaIngressoAttachments = configurazioneAggiornata.getRispostaIngresso().get_value_attachments();
				
				dumpRispostaUscitaHeader = configurazioneAggiornata.getRispostaUscita().get_value_headers();	
				dumpRispostaUscitaPayload = configurazioneAggiornata.getRispostaUscita().get_value_payload();
				dumpRispostaUscitaPayloadParsing = configurazioneAggiornata.getRispostaUscita().get_value_payloadParsing();
				dumpRispostaUscitaBody = configurazioneAggiornata.getRispostaUscita().get_value_body();
				dumpRispostaUscitaAttachments = configurazioneAggiornata.getRispostaUscita().get_value_attachments();
			}
			
			porteDelegateHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
					false);
			
			dati = porteDelegateHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idSoggFruitore, null,idAsps, 
					idFruizione, portaDelegata.getTipoSoggettoProprietario(), portaDelegata.getNomeSoggettoProprietario(), dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteDelegateCostanti.OBJECT_NAME_PORTE_DELEGATE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		}
	}

}
