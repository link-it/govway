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
package org.openspcoop2.web.ctrlstat.servlet.config;

import java.text.MessageFormat;
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
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.Dump;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.TipoPdD;
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
 * ConfigurazioneDumpConfigurazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneDumpConfigurazione extends Action {

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

		try {
			ConfigurazioneHelper confHelper = new ConfigurazioneHelper(request, pd, session);

			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			boolean showStato = false;
			String statoDump = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_STATO);
			boolean showRealtime = confCore.isDump_showConfigurazioneDumpRealtime();
			String realtime = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_REALTIME);
			String statoDumpRichiesta = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO);
			String statoDumpRisposta = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO);
			
			String dumpRichiestaIngressoHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
			String dumpRichiestaIngressoPayload = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD);
			String dumpRichiestaIngressoPayloadParsing = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_PAYLOAD_PARSING);
			String dumpRichiestaIngressoBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
			String dumpRichiestaIngressoAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
			
			String dumpRichiestaUscitaHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
			String dumpRichiestaUscitaPayload = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD);
			String dumpRichiestaUscitaPayloadParsing = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_PAYLOAD_PARSING);
			String dumpRichiestaUscitaBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
			String dumpRichiestaUscitaAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
			
			String dumpRispostaIngressoHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
			String dumpRispostaIngressoPayload = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD);
			String dumpRispostaIngressoPayloadParsing = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_PAYLOAD_PARSING);
			String dumpRispostaIngressoBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
			String dumpRispostaIngressoAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
			
			String dumpRispostaUscitaHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
			String dumpRispostaUscitaPayload = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD);
			String dumpRispostaUscitaPayloadParsing = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_PAYLOAD_PARSING);
			String dumpRispostaUscitaBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
			String dumpRispostaUscitaAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
			
			String actionConferma = confHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			String tipoConfigurazione = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_TIPO_CONFIGURAZIONE);
			TipoPdD tipoPdD = TipoPdD.toTipoPdD(tipoConfigurazione);
			boolean portaApplicativa = TipoPdD.APPLICATIVA.equals(tipoPdD);
			
			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			
			Dump dump = configurazioneGenerale.getDump();
			DumpConfigurazione oldConfigurazione = portaApplicativa ? dump.getConfigurazionePortaApplicativa() : dump.getConfigurazionePortaDelegata();
			
			// creo una configurazione di default
			if(oldConfigurazione == null) {
				oldConfigurazione = new DumpConfigurazione();
				
				oldConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
				oldConfigurazione.getRichiestaIngresso().setPayload(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				
				oldConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
				oldConfigurazione.getRichiestaUscita().setPayload(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaUscita().setBody(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRichiestaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
				
				oldConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
				oldConfigurazione.getRispostaIngresso().setPayload(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaIngresso().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaIngresso().setBody(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaIngresso().setAttachments(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaIngresso().setHeaders(StatoFunzionalita.DISABILITATO);
				
				oldConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
				oldConfigurazione.getRispostaUscita().setPayload(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaUscita().setPayloadParsing(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaUscita().setBody(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaUscita().setAttachments(StatoFunzionalita.DISABILITATO);
				oldConfigurazione.getRispostaUscita().setHeaders(StatoFunzionalita.DISABILITATO);
			}

			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();

			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, 
					ConfigurazioneCostanti.SERVLET_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI));
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_REGISTRAZIONE_MESSAGGI, null));
			
			String postBackElementName = confHelper.getPostBackElementName();
			if(postBackElementName != null) {
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO)) {
					if(!confHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) && statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						
						dumpRichiestaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoPayload = StatoFunzionalita.DISABILITATO.getValue();
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
					if(!confHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) && statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						
						dumpRispostaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaIngressoPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRispostaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaUscitaPayload = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaPayloadParsing = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}
			}

			// edit in progress
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(statoDump == null) {
					statoDump = "";
					realtime = oldConfigurazione.getRealtime().getValue();
					statoDumpRichiesta = confHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
					statoDumpRisposta = confHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
					
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

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
						dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
						portaApplicativa);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.checkDataConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
						dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
						portaApplicativa);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}
			
			boolean showConfermaRichiesta = false;
			boolean showConfermaRisposta = false;
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
			
			if(showConfermaRichiesta  || showConfermaRisposta) {
				if(actionConferma == null) {
					
					ServletUtils.setPageDataTitle(pd, lstParam);

					// preparo i campi
					Vector<DataElement> dati = new Vector<DataElement>();
					dati.addElement(ServletUtils.getDataElementForEditModeFinished());
					
					confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
							dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
							portaApplicativa);
					
					
					confHelper.addConfigurazioneDumpToDatiAsHidden(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
							dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
							portaApplicativa);
					
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

					return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
				} 
			}
			
			// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
			if( !(showConfermaRichiesta  || showConfermaRisposta)
				||
				(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK))
				) {

				Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
				
				DumpConfigurazione newDumpConfigurazione = confHelper.getConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime,
							statoDumpRichiesta, statoDumpRisposta, 
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
							dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				if(portaApplicativa) {
					newConfigurazione.getDump().setConfigurazionePortaApplicativa(newDumpConfigurazione);
				}
				else {
					newConfigurazione.getDump().setConfigurazionePortaDelegata(newDumpConfigurazione);
				}
	
				confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);
	
				// Preparo la lista
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DUMP_CONFIGURAZIONE_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
	
				// ricarico la configurazione
				newConfigurazione = confCore.getConfigurazioneGenerale();
				
				DumpConfigurazione configurazioneAggiornata = portaApplicativa ? newConfigurazione.getDump().getConfigurazionePortaApplicativa() : newConfigurazione.getDump().getConfigurazionePortaDelegata();
				statoDump = "";
				realtime = configurazioneAggiornata.getRealtime().getValue();
				statoDumpRichiesta = confHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				statoDumpRisposta = confHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				
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
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoPayload, dumpRichiestaIngressoPayloadParsing, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaPayload, dumpRichiestaUscitaPayloadParsing, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoPayload, dumpRispostaIngressoPayloadParsing, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaPayload, dumpRispostaUscitaPayloadParsing, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments,
					portaApplicativa);

			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		}
	}

}
