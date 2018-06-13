/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
			String dumpRichiestaIngressoBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
			String dumpRichiestaIngressoAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
			String dumpRichiestaUscitaHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
			String dumpRichiestaUscitaBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
			String dumpRichiestaUscitaAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
			String dumpRispostaIngressoHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
			String dumpRispostaIngressoBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
			String dumpRispostaIngressoAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
			String dumpRispostaUscitaHeader = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
			String dumpRispostaUscitaBody = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
			String dumpRispostaUscitaAttachments = confHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
			String actionConferma = confHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			Configurazione configurazioneGenerale = confCore.getConfigurazioneGenerale();
			
			Dump dump = configurazioneGenerale.getDump();
			DumpConfigurazione oldConfigurazione = dump.getConfigurazione();
			
			// creo una configurazione di default
			if(oldConfigurazione == null) {
				oldConfigurazione = new DumpConfigurazione();
				oldConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
				oldConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
				oldConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
				oldConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
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
						dumpRichiestaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRichiestaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO)) {
					if(!confHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) && statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						dumpRispostaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
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
					dumpRichiestaIngressoBody = oldConfigurazione.getRichiestaIngresso().get_value_body();
					dumpRichiestaIngressoAttachments = oldConfigurazione.getRichiestaIngresso().get_value_attachments();
					dumpRichiestaUscitaHeader = oldConfigurazione.getRichiestaUscita().get_value_headers();	
					dumpRichiestaUscitaBody = oldConfigurazione.getRichiestaUscita().get_value_body();
					dumpRichiestaUscitaAttachments = oldConfigurazione.getRichiestaUscita().get_value_attachments();
					dumpRispostaIngressoHeader = oldConfigurazione.getRispostaIngresso().get_value_headers();	
					dumpRispostaIngressoBody = oldConfigurazione.getRispostaIngresso().get_value_body();
					dumpRispostaIngressoAttachments = oldConfigurazione.getRispostaIngresso().get_value_attachments();
					dumpRispostaUscitaHeader = oldConfigurazione.getRispostaUscita().get_value_headers();	
					dumpRispostaUscitaBody = oldConfigurazione.getRispostaUscita().get_value_body();
					dumpRispostaUscitaAttachments = oldConfigurazione.getRispostaUscita().get_value_attachments();
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = confHelper.checkDataConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}
			
			boolean showConfermaRichiesta = false;
			boolean showConfermaRisposta = false;
			// se ho abilitato entrambi i dump di ingresso e uscita per richiesta o risposta informo l'utente che lo spazio occupato sara' il doppio
			if(statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// doppio body
				if(dumpRichiestaIngressoBody.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRichiestaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue())) {
					showConfermaRichiesta = true;
				}
				
				// doppi attachments
				if(dumpRichiestaIngressoAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRichiestaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue())) {
					showConfermaRichiesta = true;
				}
			}
			
			// se ho abilitato entrambi i dump di ingresso e uscita per richiesta o risposta informo l'utente che lo spazio occupato sara' il doppio
			if(statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
				// doppio body
				if(dumpRispostaIngressoBody.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRispostaUscitaBody.equals(StatoFunzionalita.ABILITATO.getValue())) {
					showConfermaRisposta = true;
				}
				
				// doppi attachments
				if(dumpRispostaIngressoAttachments.equals(StatoFunzionalita.ABILITATO.getValue()) && dumpRispostaUscitaAttachments.equals(StatoFunzionalita.ABILITATO.getValue())) {
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
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
							dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
					
					
					confHelper.addConfigurazioneDumpToDatiAsHidden(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
							dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
							dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
							dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
					
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
			if(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK)) {

				Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
				
				DumpConfigurazione newDumpConfigurazione = confHelper.getConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime,
							statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, 
							dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				newConfigurazione.getDump().setConfigurazione(newDumpConfigurazione); 
	
				confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);
	
				// Preparo la lista
				pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_DUMP_CONFIGURAZIONE_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
	
				// ricarico la configurazione
				newConfigurazione = confCore.getConfigurazioneGenerale();
				
				DumpConfigurazione configurazioneAggiornata = newConfigurazione.getDump().getConfigurazione();
				statoDump = "";
				realtime = configurazioneAggiornata.getRealtime().getValue();
				statoDumpRichiesta = confHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				statoDumpRisposta = confHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				dumpRichiestaIngressoHeader = configurazioneAggiornata.getRichiestaIngresso().get_value_headers();	
				dumpRichiestaIngressoBody = configurazioneAggiornata.getRichiestaIngresso().get_value_body();
				dumpRichiestaIngressoAttachments = configurazioneAggiornata.getRichiestaIngresso().get_value_attachments();
				dumpRichiestaUscitaHeader = configurazioneAggiornata.getRichiestaUscita().get_value_headers();	
				dumpRichiestaUscitaBody = configurazioneAggiornata.getRichiestaUscita().get_value_body();
				dumpRichiestaUscitaAttachments = configurazioneAggiornata.getRichiestaUscita().get_value_attachments();
				dumpRispostaIngressoHeader = configurazioneAggiornata.getRispostaIngresso().get_value_headers();	
				dumpRispostaIngressoBody = configurazioneAggiornata.getRispostaIngresso().get_value_body();
				dumpRispostaIngressoAttachments = configurazioneAggiornata.getRispostaIngresso().get_value_attachments();
				dumpRispostaUscitaHeader = configurazioneAggiornata.getRispostaUscita().get_value_headers();	
				dumpRispostaUscitaBody = configurazioneAggiornata.getRispostaUscita().get_value_body();
				dumpRispostaUscitaAttachments = configurazioneAggiornata.getRispostaUscita().get_value_attachments();
			
			}
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			confHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);

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
