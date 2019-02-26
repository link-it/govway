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
package org.openspcoop2.web.ctrlstat.servlet.pa;

import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.openspcoop2.core.config.DumpConfigurazione;
import org.openspcoop2.core.config.DumpConfigurazioneRegola;
import org.openspcoop2.core.config.PortaApplicativa;
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
public class PorteApplicativeDumpConfigurazione extends Action {

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
		Integer parentPA = ServletUtils.getIntegerAttributeFromSession(PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT, session);
		if(parentPA == null) parentPA = PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_NONE;

		try {

			PorteApplicativeHelper porteApplicativeHelper = new PorteApplicativeHelper(request, pd, session);
			String id = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID);
			int idInt = Integer.parseInt(id);
			String idsogg = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_SOGGETTO);
			String idAsps = porteApplicativeHelper.getParameter(PorteApplicativeCostanti.PARAMETRO_PORTE_APPLICATIVE_ID_ASPS);
			if(idAsps == null) 
				idAsps = "";

			// Preparo il menu
			porteApplicativeHelper.makeMenu();

			PorteApplicativeCore porteApplicativeCore = new PorteApplicativeCore();

			boolean showStato = true;
			String statoDump = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_STATO);
			boolean showRealtime = porteApplicativeCore.isDump_showConfigurazioneDumpRealtime();
			String realtime = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_REALTIME);
			String statoDumpRichiesta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO);
			String statoDumpRisposta = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO);
			String dumpRichiestaIngressoHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_HEADERS);
			String dumpRichiestaIngressoBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_BODY);
			String dumpRichiestaIngressoAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_INGRESSO_ATTACHMENTS);
			String dumpRichiestaUscitaHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_HEADERS);
			String dumpRichiestaUscitaBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_BODY);
			String dumpRichiestaUscitaAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_USCITA_ATTACHMENTS);
			String dumpRispostaIngressoHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_HEADERS);
			String dumpRispostaIngressoBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_BODY);
			String dumpRispostaIngressoAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_INGRESSO_ATTACHMENTS);
			String dumpRispostaUscitaHeader = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_HEADERS);
			String dumpRispostaUscitaBody = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_BODY);
			String dumpRispostaUscitaAttachments = porteApplicativeHelper.getParameter(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_USCITA_ATTACHMENTS);
			String actionConferma = porteApplicativeHelper.getParameter(Costanti.PARAMETRO_ACTION_CONFIRM);

			PortaApplicativa pa = porteApplicativeCore.getPortaApplicativa(idInt);
			String idporta = pa.getNome();

			DumpConfigurazione oldConfigurazione = pa.getDump();
			
			boolean initConfigurazione = false;
			String postBackElementName = porteApplicativeHelper.getPostBackElementName();
			if(postBackElementName != null ){
				if(postBackElementName.equalsIgnoreCase(CostantiControlStation.PARAMETRO_DUMP_STATO)){
					initConfigurazione = true;
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RICHIESTA_STATO)) {
					if(!porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) && statoDumpRichiesta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						dumpRichiestaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRichiestaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRichiestaUscitaBody = StatoFunzionalita.ABILITATO.getValue();
						dumpRichiestaUscitaAttachments = StatoFunzionalita.ABILITATO.getValue();
					}
				}
				
				if(postBackElementName.equals(CostantiControlStation.PARAMETRO_DUMP_RISPOSTA_STATO)) {
					if(!porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) && statoDumpRisposta.equals(StatoFunzionalita.ABILITATO.getValue())) {
						dumpRispostaIngressoHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRispostaIngressoBody = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}
			}

			List<Parameter> lstParam = porteApplicativeHelper.getTitoloPA(parentPA, idsogg, idAsps);

			String labelPerPorta = null;
			if(parentPA!=null && (parentPA.intValue() == PorteApplicativeCostanti.ATTRIBUTO_PORTE_APPLICATIVE_PARENT_CONFIGURAZIONE)) {
				labelPerPorta = porteApplicativeCore.getLabelRegolaMappingErogazionePortaApplicativa(
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE_CONFIG_DI,
						PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,
						pa);
			}
			else {
				labelPerPorta = PorteApplicativeCostanti.LABEL_PARAMETRO_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE_CONFIG_DI+idporta;
			}

			lstParam.add(new Parameter(labelPerPorta,  null));

			// edit in progress
			if (porteApplicativeHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);

				if(statoDump == null) {
					if(oldConfigurazione == null) {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
					} else {
						statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
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
				}
				
				if(initConfigurazione) {
					if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
						oldConfigurazione = new DumpConfigurazione();
						oldConfigurazione.setRichiestaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRichiestaUscita(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaIngresso(new DumpConfigurazioneRegola());
						oldConfigurazione.setRispostaUscita(new DumpConfigurazioneRegola());
						
						realtime = oldConfigurazione.getRealtime().getValue();
						statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(oldConfigurazione, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
						
						dumpRichiestaIngressoHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRichiestaIngressoBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaIngressoAttachments = StatoFunzionalita.DISABILITATO.getValue();
						dumpRichiestaUscitaHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRichiestaUscitaBody = StatoFunzionalita.ABILITATO.getValue();
						dumpRichiestaUscitaAttachments = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaIngressoHeader = StatoFunzionalita.ABILITATO.getValue();	
						dumpRispostaIngressoBody = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaIngressoAttachments = StatoFunzionalita.ABILITATO.getValue();
						dumpRispostaUscitaHeader = StatoFunzionalita.DISABILITATO.getValue();	
						dumpRispostaUscitaBody = StatoFunzionalita.DISABILITATO.getValue();
						dumpRispostaUscitaAttachments = StatoFunzionalita.DISABILITATO.getValue();
					}
				}

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}

			// Controlli sui campi immessi
			boolean isOk = porteApplicativeHelper.checkDataConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader,
					dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody,
					dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments,
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			if (!isOk) {

				ServletUtils.setPageDataTitle(pd, lstParam);

				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());

				porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
						dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
						dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
						dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
						dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
				
				dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
			}
			
			boolean showConfermaRichiesta = false;
			boolean showConfermaRisposta = false;
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
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
			}
				
			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				if(showConfermaRichiesta  || showConfermaRisposta) {
					if(actionConferma == null) {
						
						ServletUtils.setPageDataTitle(pd, lstParam);
	
						// preparo i campi
						Vector<DataElement> dati = new Vector<DataElement>();
						dati.addElement(ServletUtils.getDataElementForEditModeFinished());
						
						porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
								dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
								dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
								dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
								dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
						
						
						porteApplicativeHelper.addConfigurazioneDumpToDatiAsHidden(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
								dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
								dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
								dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
								dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
						
						dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);
						
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
	
						return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE,	ForwardParams.OTHER(""));
					} 
				}
			}

			if(statoDump.equals(CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO)) {
				// se ho confermato effettuo la modifica altrimenti torno direttamente alla lista
				if( !(showConfermaRichiesta  || showConfermaRisposta) 
						|| 
					(actionConferma != null && actionConferma.equals(Costanti.PARAMETRO_ACTION_CONFIRM_VALUE_OK))
						) {
					DumpConfigurazione newDumpConfigurazione = porteApplicativeHelper.getConfigurazioneDump(tipoOperazione, showStato, statoDump, showRealtime, realtime,
							statoDumpRichiesta, statoDumpRisposta, dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
							dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, dumpRispostaIngressoHeader, 
							dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
							
					pa.setDump(newDumpConfigurazione);

					porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
				
					// Preparo la lista
					pd.setMessage(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_ARCHIVIO_MESSAGGI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
				}
			} else {
				pa.setDump(null);

				porteApplicativeCore.performUpdateOperation(userLogin, porteApplicativeHelper.smista(), pa);
				
				// Preparo la lista
				pd.setMessage(PorteApplicativeCostanti.LABEL_PORTE_APPLICATIVE_ARCHIVIO_MESSAGGI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);

			}
				
			pa = porteApplicativeCore.getPortaApplicativa(idInt);
			idporta = pa.getNome();

			ServletUtils.setPageDataTitle(pd, lstParam);

			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			dati.addElement(ServletUtils.getDataElementForEditModeFinished());

			// ricarico la configurazione
			
			DumpConfigurazione configurazioneAggiornata = pa.getDump();
			
			if(configurazioneAggiornata == null) {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_DEFAULT;
			} else {
				statoDump = CostantiControlStation.VALUE_PARAMETRO_DUMP_STATO_RIDEFINITO;
				realtime = configurazioneAggiornata.getRealtime().getValue();
				statoDumpRichiesta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, false) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
				statoDumpRisposta = porteApplicativeHelper.isDumpConfigurazioneAbilitato(configurazioneAggiornata, true) ? StatoFunzionalita.ABILITATO.getValue() : StatoFunzionalita.DISABILITATO.getValue();
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

			porteApplicativeHelper.addConfigurazioneDumpToDati(tipoOperazione, dati, showStato, statoDump, showRealtime, realtime, statoDumpRichiesta, statoDumpRisposta, 
					dumpRichiestaIngressoHeader, dumpRichiestaIngressoBody, dumpRichiestaIngressoAttachments, 
					dumpRichiestaUscitaHeader, dumpRichiestaUscitaBody, dumpRichiestaUscitaAttachments, 
					dumpRispostaIngressoHeader, dumpRispostaIngressoBody, dumpRispostaIngressoAttachments, 
					dumpRispostaUscitaHeader, dumpRispostaUscitaBody, dumpRispostaUscitaAttachments);
			
			dati = porteApplicativeHelper.addHiddenFieldsToDati(TipoOperazione.OTHER,id, idsogg, null, idAsps, dati);

			pd.setDati(dati);

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

			return ServletUtils.getStrutsForwardEditModeFinished(mapping, PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					PorteApplicativeCostanti.OBJECT_NAME_PORTE_APPLICATIVE_DUMP_CONFIGURAZIONE, ForwardParams.OTHER(""));
		}
	}

}
