/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
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
import org.openspcoop2.core.config.MessaggiDiagnostici;
import org.openspcoop2.core.config.Tracciamento;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.constants.Severita;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiConfigUtils;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
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
 * ConfigurazioneTracciamentoTransazioni
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConfigurazioneTracciamentoTransazioni extends Action {

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
			
			Boolean contaListe = ServletUtils.getContaListeFromSession(session);
			
			// Preparo il menu
			confHelper.makeMenu();
			
			ConfigurazioneCore confCore = new ConfigurazioneCore();
			
			Configurazione oldConfigurazione = confCore.getConfigurazioneGenerale();
			
			// Stato [si usa per capire se sono entrato per la prima volta nella schermata]		
			boolean first = confHelper.isFirstTimeFromHttpParameters(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
			String nuovaConfigurazioneEsiti = confHelper.readConfigurazioneRegistrazioneEsitiFromHttpParameters(oldConfigurazione.getTracciamento().getEsiti(), first);
			String severita = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA);
			String severita_log4j = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_LIVELLO_SEVERITA_LOG4J);
			String registrazioneTracce = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRACCE);
			String dumpApplicativo = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_APPLICATIVO);
			String dumpPD = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PD);
			String dumpPA = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_DUMP_CONNETTORE_PA);
			
			String tracciamentoEsitiSelezionePersonalizzataOk = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_OK);
			String tracciamentoEsitiSelezionePersonalizzataFault = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FAULT);
			String tracciamentoEsitiSelezionePersonalizzataFallite = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_FALLITE);
			String tracciamentoEsitiSelezionePersonalizzataMax = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_MAX_REQUEST);
			String tracciamentoEsitiSelezionePersonalizzataCors = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_ESITI_CORS);
			
			String transazioniTempiElaborazione = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TEMPI);
			String transazioniToken = confHelper.getParameter(ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_REGISTRAZIONE_TRANSAZIONE_TOKEN);
			
			// setto la barra del titolo
			List<Parameter> lstParam = new ArrayList<Parameter>();
			lstParam.add(new Parameter(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO, null));
			
			// edit in progress
			if (confHelper.isEditModeInProgress()) {
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				if(severita == null) {
					if(oldConfigurazione.getMessaggiDiagnostici().getSeverita()!=null)
						severita = oldConfigurazione.getMessaggiDiagnostici().getSeverita().toString();
					if(oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
						severita_log4j = oldConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
					if(oldConfigurazione.getDump().getStato()!=null)
						dumpApplicativo = oldConfigurazione.getDump().getStato().toString();
					if(oldConfigurazione.getDump().getDumpBinarioPortaDelegata()!=null)
						dumpPD = oldConfigurazione.getDump().getDumpBinarioPortaDelegata().toString();
					if(oldConfigurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
						dumpPA = oldConfigurazione.getDump().getDumpBinarioPortaApplicativa().toString();
					if(oldConfigurazione.getTracciamento().getStato()!=null)
						registrazioneTracce = oldConfigurazione.getTracciamento().getStato().toString();
					if(oldConfigurazione.getTransazioni()!=null) {
						if(oldConfigurazione.getTransazioni().getTempiElaborazione()!=null) {
							transazioniTempiElaborazione = oldConfigurazione.getTransazioni().getTempiElaborazione().toString();
						}
						if(oldConfigurazione.getTransazioni().getToken()!=null) {
							transazioniToken = oldConfigurazione.getTransazioni().getToken().toString();
						}
					}
				}
				
				if(tracciamentoEsitiSelezionePersonalizzataOk==null) {
					
					List<String> attivi = new ArrayList<String>();
					if(nuovaConfigurazioneEsiti!=null){
						String [] tmp = nuovaConfigurazioneEsiti.split(",");
						if(tmp!=null){
							for (int i = 0; i < tmp.length; i++) {
								attivi.add(tmp[i].trim());
							}
						}
					}
					
					EsitiProperties esiti = EsitiConfigUtils.getEsitiPropertiesForConfiguration(ControlStationCore.getLog());
					
					List<Integer> listOk = confHelper.getListaEsitiOkSenzaCors(esiti);
					if(confHelper.isCompleteEnabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					else if(confHelper.isCompleteDisabled(attivi, listOk)) {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataOk = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					List<Integer> listFault = esiti.getEsitiCodeFaultApplicativo();
					if(confHelper.isCompleteEnabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					else if(confHelper.isCompleteDisabled(attivi, listFault)) {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFault = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					List<Integer> listFalliteSenzaMax = confHelper.getListaEsitiFalliteSenzaMaxThreads(esiti);
					if(confHelper.isCompleteEnabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					else if(confHelper.isCompleteDisabled(attivi, listFalliteSenzaMax)) {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataFallite = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
					if(attivi.contains((esiti.convertoToCode(EsitoTransazioneName.CONTROLLO_TRAFFICO_MAX_THREADS)+""))) {
						tracciamentoEsitiSelezionePersonalizzataMax = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}	
					else {
						tracciamentoEsitiSelezionePersonalizzataMax = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					
					List<Integer> listCors = confHelper.getListaEsitiCors(esiti);
					if(confHelper.isCompleteEnabled(attivi, listCors)) {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_ABILITATO;
					}
					else if(confHelper.isCompleteDisabled(attivi, listCors)) {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.DEFAULT_VALUE_DISABILITATO;
					}
					else {
						tracciamentoEsitiSelezionePersonalizzataCors = ConfigurazioneCostanti.TRACCIAMENTO_ESITI_PERSONALIZZATO;
					}
					
				}
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, null, nuovaConfigurazioneEsiti, 
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataMax,
						tracciamentoEsitiSelezionePersonalizzataCors); 
				
				confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
						transazioniTempiElaborazione, transazioniToken); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
				
				confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, oldConfigurazione, dati, contaListe);

				confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
				
				confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeInProgress(mapping,	ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			// Controlli sui campi immessi
			boolean isOk = confHelper.checkConfigurazioneTracciamento(tipoOperazione, nuovaConfigurazioneEsiti);
			if (!isOk) {
				
				ServletUtils.setPageDataTitle(pd, lstParam);
				
				// preparo i campi
				Vector<DataElement> dati = new Vector<DataElement>();
				dati.addElement(ServletUtils.getDataElementForEditModeFinished());
				
				confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, null, nuovaConfigurazioneEsiti, 
						tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
						tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataMax,
						tracciamentoEsitiSelezionePersonalizzataCors); 
				
				confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
						transazioniTempiElaborazione, transazioniToken); 
				
				confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, oldConfigurazione, dati, contaListe);

				confHelper.addTracciamentoToDati(registrazioneTracce, oldConfigurazione, dati, contaListe);
				
				confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, oldConfigurazione, dati, contaListe); 
				
				// Set First is false
				confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
				
				pd.setDati(dati);

				ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);

				return ServletUtils.getStrutsForwardEditModeCheckError(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI,	ForwardParams.OTHER(""));
			}
			
			Configurazione newConfigurazione = confCore.getConfigurazioneGenerale();
			
			newConfigurazione.getTracciamento().setEsiti(nuovaConfigurazioneEsiti);
			
			if (newConfigurazione.getMessaggiDiagnostici() != null) {
				newConfigurazione.getMessaggiDiagnostici().setSeverita(Severita.toEnumConstant(severita));
				newConfigurazione.getMessaggiDiagnostici().setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
			} else {
				MessaggiDiagnostici md = new MessaggiDiagnostici();
				md.setSeverita(Severita.toEnumConstant(severita));
				md.setSeveritaLog4j(Severita.toEnumConstant(severita_log4j));
				newConfigurazione.setMessaggiDiagnostici(md);
			}
			
			if (newConfigurazione.getTracciamento() != null) {
				newConfigurazione.getTracciamento().setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
			} else {
				Tracciamento t = new Tracciamento();
				t.setStato(StatoFunzionalita.toEnumConstant(registrazioneTracce));
				newConfigurazione.setTracciamento(t);
			}
			
			if (newConfigurazione.getDump() != null) {
				newConfigurazione.getDump().setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
				newConfigurazione.getDump().setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
				newConfigurazione.getDump().setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
			} else {
				Dump d = new Dump();
				d.setStato(StatoFunzionalita.toEnumConstant(dumpApplicativo));
				d.setDumpBinarioPortaDelegata(StatoFunzionalita.toEnumConstant(dumpPD));
				d.setDumpBinarioPortaApplicativa(StatoFunzionalita.toEnumConstant(dumpPA));
				newConfigurazione.setDump(d);
			}
			
			if(StatoFunzionalita.DISABILITATO.equals(newConfigurazione.getDump().getStato())) {
				newConfigurazione.getDump().setConfigurazione(null);
			}
			
			if(newConfigurazione.getTransazioni()==null) {
				newConfigurazione.setTransazioni( new Transazioni() );
			}
			newConfigurazione.getTransazioni().setTempiElaborazione(StatoFunzionalita.toEnumConstant(transazioniTempiElaborazione));
			newConfigurazione.getTransazioni().setToken(StatoFunzionalita.toEnumConstant(transazioniToken));
			
			confCore.performUpdateOperation(userLogin, confHelper.smista(), newConfigurazione);

			// Preparo la lista
			pd.setMessage(ConfigurazioneCostanti.LABEL_CONFIGURAZIONE_TRACCIAMENTO_ESITI_CON_SUCCESSO, Costanti.MESSAGE_TYPE_INFO);
			
			ServletUtils.setPageDataTitle(pd, lstParam);
			
			// preparo i campi
			Vector<DataElement> dati = new Vector<DataElement>();
			
			// ricarico la configurazione
			newConfigurazione = confCore.getConfigurazioneGenerale();
			
			if(newConfigurazione.getMessaggiDiagnostici().getSeverita()!=null)
				severita = newConfigurazione.getMessaggiDiagnostici().getSeverita().toString();
			if(newConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j()!=null)
				severita_log4j = newConfigurazione.getMessaggiDiagnostici().getSeveritaLog4j().toString();
			if(newConfigurazione.getDump().getStato()!=null)
				dumpApplicativo = newConfigurazione.getDump().getStato().toString();
			if(newConfigurazione.getDump().getDumpBinarioPortaDelegata()!=null)
				dumpPD = newConfigurazione.getDump().getDumpBinarioPortaDelegata().toString();
			if(newConfigurazione.getDump().getDumpBinarioPortaApplicativa()!=null)
				dumpPA = newConfigurazione.getDump().getDumpBinarioPortaApplicativa().toString();
			if(newConfigurazione.getTracciamento().getStato()!=null)
				registrazioneTracce = newConfigurazione.getTracciamento().getStato().toString();
			if(newConfigurazione.getTransazioni()!=null) {
				if(newConfigurazione.getTransazioni().getTempiElaborazione()!=null) {
					transazioniTempiElaborazione = newConfigurazione.getTransazioni().getTempiElaborazione().toString();
				}
				if(newConfigurazione.getTransazioni().getToken()!=null) {
					transazioniToken = newConfigurazione.getTransazioni().getToken().toString();
				}
			}
			
			confHelper.addToDatiRegistrazioneEsiti(dati, tipoOperazione, null, newConfigurazione.getTracciamento().getEsiti(), 
					tracciamentoEsitiSelezionePersonalizzataOk, tracciamentoEsitiSelezionePersonalizzataFault, 
					tracciamentoEsitiSelezionePersonalizzataFallite, tracciamentoEsitiSelezionePersonalizzataMax,
					tracciamentoEsitiSelezionePersonalizzataCors); 
			
			confHelper.addToDatiRegistrazioneTransazione(dati, tipoOperazione, 
					transazioniTempiElaborazione, transazioniToken); 
			
			confHelper.addMessaggiDiagnosticiToDati(severita, severita_log4j, newConfigurazione, dati, contaListe);

			confHelper.addTracciamentoToDati(registrazioneTracce, newConfigurazione, dati, contaListe);
			
			confHelper.addRegistrazioneMessaggiToDati(dumpApplicativo, dumpPD, dumpPA, newConfigurazione, dati, contaListe); 
			
			// Set First is false
			confHelper.addToDatiFirstTimeDisabled(dati,ConfigurazioneCostanti.PARAMETRO_CONFIGURAZIONE_FIRST_TIME);
			
			pd.setDati(dati);
			pd.disableEditMode();

			ServletUtils.setGeneralAndPageDataIntoSession(session, gd, pd);
			
			return ServletUtils.getStrutsForwardEditModeFinished(mapping, ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		} catch (Exception e) {
			return ServletUtils.getStrutsForwardError(ControlStationCore.getLog(), e, pd, session, gd, mapping, 
					ConfigurazioneCostanti.OBJECT_NAME_CONFIGURAZIONE_TRACCIAMENTO_TRANSAZIONI, ForwardParams.OTHER(""));
		}
	}
}
