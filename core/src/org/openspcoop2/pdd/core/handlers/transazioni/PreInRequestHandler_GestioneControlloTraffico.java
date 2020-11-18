/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.core.handlers.transazioni;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.constants.TipoErrore;
import org.openspcoop2.core.eventi.constants.CodiceEventoControlloTraffico;
import org.openspcoop2.core.eventi.constants.TipoEvento;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.controllo_traffico.GeneratoreMessaggiErrore;
import org.openspcoop2.pdd.core.controllo_traffico.GestoreControlloTraffico;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.messages.ConnectorInMessage;
import org.openspcoop2.utils.transport.Credential;
import org.slf4j.Logger;

/**     
 * PreInRequestHandler_GestioneControlloTraffico
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PreInRequestHandler_GestioneControlloTraffico {

	
	public void process(PreInRequestContext context) throws HandlerException{
		
		ConfigurazioneGenerale configurazioneGenerale = null;
		GestoreControlloTraffico gestore = null;
		Boolean maxThreadsEnabled = false;
		Boolean maxThreadsWarningOnly = false;
		Long maxThreads = null;
		Integer threshold = null;
		TipoErrore tipoErrore = TipoErrore.FAULT;
		boolean includiDescrizioneErrore = true;
		Logger logControlloTraffico = null;
		ServiceBinding serviceBinding = ServiceBinding.REST;
		try{
		
			// Prelevo la configurazione del Controllo del Traffico
			OpenSPCoop2Properties propertiesReader = OpenSPCoop2Properties.getInstance();
			logControlloTraffico = OpenSPCoop2Logger.getLoggerOpenSPCoopControlloTraffico(propertiesReader.isControlloTrafficoDebug());
			ConfigurazionePdDManager configPdDManager = ConfigurazionePdDManager.getInstance();
			configurazioneGenerale = configPdDManager.getConfigurazioneControlloTraffico();
			if(configurazioneGenerale.getControlloTraffico()==null){
				throw new Exception("Impostazione maxThreads non corretta?? ControlloTraffico is null");
			}
			
			if(context!=null && context.getTipoPorta()!=null && context.getRequestInfo()!=null) {
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta())) {
					if(context.getRequestInfo().getIntegrationServiceBinding()!=null) {
						serviceBinding = context.getRequestInfo().getIntegrationServiceBinding();
					}
				}else {
					if(context.getRequestInfo().getProtocolServiceBinding()!=null) {
						serviceBinding = context.getRequestInfo().getProtocolServiceBinding();
					}
				}
			}
			
			// Indicazione se il sistema di controllo dei max threads risulta attivo
			maxThreadsEnabled = configurazioneGenerale.getControlloTraffico().isControlloMaxThreadsEnabled();
			maxThreadsWarningOnly = configurazioneGenerale.getControlloTraffico().isControlloMaxThreadsWarningOnly();
			
			if(maxThreadsEnabled) {
			
				// Numero Massimo di richieste simultanee
				maxThreads = configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsSoglia();
				if(maxThreads==null || maxThreads<=0l){
					throw new Exception("Impostazione maxThreads non corretta?? ["+maxThreads+"]");
				}
				
				// Indicazione se abilitare o meno il controllo del traffico ed eventuale threshold
				if(configurazioneGenerale.getControlloTraffico()!=null &&
						configurazioneGenerale.getControlloTraffico().isControlloCongestioneEnabled()){
					threshold = configurazioneGenerale.getControlloTraffico().getControlloCongestioneThreshold();
				}
				
				// Tipo di errore in caso di superamento del max numero di threads
				if(configurazioneGenerale.getControlloTraffico()!=null) {
					if(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsTipoErrore()!=null) {
						TipoErrore tipoErrorTmp = TipoErrore.toEnumConstant(configurazioneGenerale.getControlloTraffico().getControlloMaxThreadsTipoErrore());
						if(tipoErrorTmp!=null) {
							tipoErrore = tipoErrorTmp;
						}
					}
					includiDescrizioneErrore = configurazioneGenerale.getControlloTraffico().isControlloMaxThreadsTipoErroreIncludiDescrizione();
				}
				
				
				// Gestore del Controllo del Traffico
				gestore = GestoreControlloTraffico.getInstance();
				
			}
			
		}catch(Exception e){
			throw new HandlerException("Configurazione non disponibile: "+e.getMessage(), e);
		}
		
		
		MsgDiagnostico msgDiag = this.buildMsgDiagnostico(context);	
		try{
			
			// salvataggio informazioni lette
			if(maxThreadsEnabled) {
				context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_MAX_THREADS_THRESHOLD, maxThreads);
				if(threshold!=null){
					context.getPddContext().addObject(GeneratoreMessaggiErrore.TEMPLATE_CONTROLLO_TRAFFICO_THRESHOLD, threshold);
				}
			
				// registro nuovo in thread in ingresso
				gestore.addThread(serviceBinding, maxThreads,threshold,maxThreadsWarningOnly,context.getPddContext(),msgDiag,tipoErrore,includiDescrizioneErrore,logControlloTraffico);
			
				// se il metodo precedente non ha sollevato una eccezione registro nel contesto che è stato registrato il thread
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO, true);	
			}
			
		}catch(Exception e){
			
			// il metodo gestore.addThread((..) ha sollevato una eccezione.
			
			if(maxThreadsWarningOnly) {
				
				// Devo comunque impostare questa variabile nel contesto, in modo che l'handler di uscita rimuovi il thread.
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO, true);
				
				// salvo nel contesto che è stato violato il parametro max-threads e salvo anche l'evento 'LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE' con severita' warning only
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_EVENTO, 
						TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE.getValue()+"_"+CodiceEventoControlloTraffico.VIOLAZIONE_WARNING_ONLY.getValue());
			}
			else {
							
				// salvo nel contesto che è stato violato il parametro max-threads in modo che l'handler di uscita sappia che non deve decrementare il numero di threads, 
				// essendo stato bloccato in questa fase
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_THREAD_REGISTRATO, false);
				
				// salvo nel contesto che è stato violato il parametro max-threads e salvo anche l'evento 'LIMITE_RICHIESTE_SIMULTANEE_VIOLAZIONE' con violazione effettiva
				context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_EVENTO, 
						TipoEvento.CONTROLLO_TRAFFICO_NUMERO_MASSIMO_RICHIESTE_SIMULTANEE.getValue()+"_"+CodiceEventoControlloTraffico.VIOLAZIONE.getValue());
				
				// Per registrare nel database anche le transazioni che vengono bloccate per maxThreads (default è false)
				// viene salvato nel contesto le informazioni riguardanti la url invocata e le credenziali
				// tali informazioni sono utili per aggiungerle come informazioni avanzate nella transazione.
				ConnectorInMessage req = null;
				try{
					req = (ConnectorInMessage) context.getTransportContext().get(PreInRequestContext.SERVLET_REQUEST);
				}catch(Exception eIgnore){}
				if(req!=null){
					try{
						String urlInvocazione = req.getURLProtocolContext().getUrlInvocazione_formBased();
						if(urlInvocazione!=null){
							if(req.getURLProtocolContext().getFunction()!=null){
								urlInvocazione = "["+req.getURLProtocolContext().getFunction()+"] "+urlInvocazione;
							}
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_URL_INVOCAZIONE, urlInvocazione);
						}
					}catch(Exception eIgnore){}
					try{
						Credential credenziali = req.getCredential();
						String credenzialiFornite = "";
						if(credenziali!=null){
							credenzialiFornite = credenziali.toString();
							context.getPddContext().addObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_CREDENZIALI, credenzialiFornite);
						}
					}catch(Exception eIgnore){}
				}
				
				// Se l'eccezione è di tipo 'HandlerException' posso risollevarla immediatamente essendo stata generata dal metodo gestore.addThread((..)
				// Altrimenti la faccio trattatare dal Gestore dei Messaggi di Errore.
				if(e instanceof HandlerException){
					throw (HandlerException)e;
				}
				else{
					try{
						logControlloTraffico.error(e.getMessage(),e);
					}catch(Exception eLog){
						context.getLogCore().error(e.getMessage(),e);
					}
					
					GeneratoreMessaggiErrore.addPddContextInfo_ControlloTrafficoGenericError(context.getPddContext());
					
					throw GeneratoreMessaggiErrore.getErroreProcessamento(e,context.getPddContext());
				}
			}
		}
			
		
		
	}


	// Ritorna un MsgDiagnostico generator
	private MsgDiagnostico buildMsgDiagnostico(PreInRequestContext context) throws HandlerException{
		try{
			String nomePorta = null;
			if(context.getRequestInfo()!=null && context.getRequestInfo().getProtocolContext()!=null) {
				nomePorta = context.getRequestInfo().getProtocolContext().getInterfaceName();
			}
			MsgDiagnostico msgDiag = MsgDiagnostico.newInstance(context.getTipoPorta(),context.getIdModulo(),nomePorta);
			msgDiag.setPddContext(context.getPddContext(), context.getProtocolFactory());
			if(org.openspcoop2.core.constants.TipoPdD.DELEGATA.equals(context.getTipoPorta())){
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
			}
			else{
				msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE);
			}
			return msgDiag;
		}catch(Exception e){
			throw new HandlerException("Generazione Messaggio Diagnostico non riuscita: "+e.getMessage(), e);
		}
	}
}
