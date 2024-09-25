/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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
package org.openspcoop2.pdd.logger.transazioni;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.CoreException;
import org.openspcoop2.core.config.Transazioni;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiLabel;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.GruppoAccordo;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.core.transazioni.CredenzialeMittente;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.RuoloTransazione;
import org.openspcoop2.core.transazioni.constants.TipoAPI;
import org.openspcoop2.core.transazioni.constants.TipoMessaggio;
import org.openspcoop2.core.transazioni.utils.PropertiesSerializator;
import org.openspcoop2.core.transazioni.utils.TempiElaborazioneUtils;
import org.openspcoop2.core.transazioni.utils.credenziali.AbstractCredenzialeList;
import org.openspcoop2.message.OpenSPCoop2RestMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.monitor.engine.condition.EsitoUtils;
import org.openspcoop2.monitor.sdk.transaction.FaseTracciamento;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.EJBUtils;
import org.openspcoop2.pdd.core.EJBUtilsMessaggioInConsegna;
import org.openspcoop2.pdd.core.GestoreMessaggi;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.GestoreAutenticazione;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.ConfigurazioneMultiDeliver;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.handlers.ExtendedTransactionInfo;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.core.state.OpenSPCoopStateful;
import org.openspcoop2.pdd.core.token.InformazioniNegoziazioneToken;
import org.openspcoop2.pdd.core.token.InformazioniToken;
import org.openspcoop2.pdd.core.token.TokenUtilities;
import org.openspcoop2.pdd.core.token.attribute_authority.InformazioniAttributi;
import org.openspcoop2.pdd.core.transazioni.DateUtility;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.DiagnosticInputStream;
import org.openspcoop2.pdd.logger.DumpUtility;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.logger.info.InfoEsitoTransazioneFormatUtils;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.dump.Messaggio;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.MapKey;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.json.JSONUtils;
import org.openspcoop2.utils.transport.TransportUtils;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**     
 * TransazioneUtilities
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransazioneUtilities {

	private Logger logger;
	private boolean transazioniRegistrazioneTracceHeaderRawEnabled;
	private boolean transazioniRegistrazioneTracceDigestEnabled;
	private boolean transazioniRegistrazioneTracceProtocolPropertiesEnabled;
	private boolean transazioniRegistrazioneTokenInformazioniNormalizzate;
	private boolean transazioniRegistrazioneAttributiInformazioniNormalizzate;
	private boolean transazioniRegistrazioneTempiElaborazione;
	private boolean transazioniRegistrazioneRetrieveTokenSaveAsTokenInfo;
	
	public TransazioneUtilities(Logger log, OpenSPCoop2Properties openspcoopProperties,
			boolean transazioniRegistrazioneTracceHeaderRawEnabled,
			boolean transazioniRegistrazioneTracceDigestEnabled,
			boolean transazioniRegistrazioneTracceProtocolPropertiesEnabled,
			Transazioni configTransazioni){
		this.logger = log;
		this.transazioniRegistrazioneTracceHeaderRawEnabled = transazioniRegistrazioneTracceHeaderRawEnabled;
		this.transazioniRegistrazioneTracceDigestEnabled = transazioniRegistrazioneTracceDigestEnabled;
		this.transazioniRegistrazioneTracceProtocolPropertiesEnabled = transazioniRegistrazioneTracceProtocolPropertiesEnabled;
		
		this.transazioniRegistrazioneTempiElaborazione = configTransazioni!=null && StatoFunzionalita.ABILITATO.equals(configTransazioni.getTempiElaborazione());
		this.transazioniRegistrazioneTokenInformazioniNormalizzate = configTransazioni!=null && StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken());
		this.transazioniRegistrazioneAttributiInformazioniNormalizzate = configTransazioni!=null && StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken()) &&
				openspcoopProperties.isGestioneAttributeAuthority_transazioniRegistrazioneAttributiInformazioniNormalizzate(); // per adesso la configurazione avviene via govway.properties
		this.transazioniRegistrazioneRetrieveTokenSaveAsTokenInfo = configTransazioni!=null && StatoFunzionalita.ABILITATO.equals(configTransazioni.getToken()) &&
				openspcoopProperties.isGestioneRetrieveToken_saveAsTokenInfo(); // per adesso la configurazione avviene via govway.properties
	}
	
	public static boolean isConsegnaMultipla(Context context) {
		int connettoriMultipli = getNumeroConnettoriMultipli(context);
		return ConfigurazioneTracciamentoUtils.isConsegnaMultipla(connettoriMultipli);
	}
	public static boolean isConsegnaMultipla(int connettoriMultipli) {
		return ConfigurazioneTracciamentoUtils.isConsegnaMultipla(connettoriMultipli);
	}
	public static int getNumeroConnettoriMultipli(Context context) {
		return ConfigurazioneTracciamentoUtils.getNumeroConnettoriMultipli(context);
	}
	public static String getConnettoriMultipli(Context context) {
		return ConfigurazioneTracciamentoUtils.getConnettoriMultipli(context);
	}
	
	public Transazione fillTransaction(InformazioniTransazione info, Transaction transaction, IDSoggetto idDominio,
			TransazioniProcessTimes times, FaseTracciamento fase, 
			EsitoTransazione esito, String esitoContext,
			Transazione transazioneDaAggiornare ) throws HandlerException{

		// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
		
		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
		if(transaction==null) {
			throw new HandlerException("Transaction is null");
		}
		if(info==null) {
			throw new HandlerException("Informazioni sulla richiesta is null");
		}
		
		Traccia tracciaRichiesta = transaction.getTracciaRichiesta();
		Traccia tracciaRisposta = transaction.getTracciaRisposta();

		boolean richiestaDuplicata = false;
		ProfiloDiCollaborazione profiloCollaborazioneBustaTracciaRichiesta = null;
		String profiloCollaborazioneValueBustaTracciaRichiesta = null;
		if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null){
			richiestaDuplicata = transaction.containsIdProtocolloDuplicato(tracciaRichiesta.getBusta().getID());
			if(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()!=null){
				profiloCollaborazioneBustaTracciaRichiesta = tracciaRichiesta.getBusta().getProfiloDiCollaborazione();
				profiloCollaborazioneValueBustaTracciaRichiesta = tracciaRichiesta.getBusta().getProfiloDiCollaborazioneValue(); 
			}
		}
		boolean rispostaDuplicata = false;
		if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null){
			rispostaDuplicata = transaction.containsIdProtocolloDuplicato(tracciaRisposta.getBusta().getID());
		}

		boolean schedulaNotificheDopoConsegnaSincrona = false;
		boolean consegnaMultiplaProfiloSincrono = false;
		MsgDiagnostico msgDiag = null;
		String idTransazione = null;
		String nomePorta = null;
		
		String identificativoSaveTransactionContext = null;
		
		long timeStart = -1;
		try {

			if(times!=null) {
				times.fillTransactionDetails = new ArrayList<>();
				timeStart = DateManager.getTimeMillis();
			}
			
			IProtocolFactory<?> protocolFactory = info.getProtocolFactory();

			IBustaBuilder<?> protocolBustaBuilder = protocolFactory.createBustaBuilder(info.getStato());

			RequestInfo requestInfo = null;
			if(info.getContext()!=null && info.getContext().containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				requestInfo = (RequestInfo) info.getContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			}

			if(FaseTracciamento.POST_OUT_RESPONSE.equals(fase) && 
					info.getContext()!=null && info.getContext().containsKey(CostantiPdD.SALVA_CONTESTO_IDENTIFICATIVO_MESSAGGIO_NOTIFICA)) {
				identificativoSaveTransactionContext = (String) info.getContext().getObject(CostantiPdD.SALVA_CONTESTO_IDENTIFICATIVO_MESSAGGIO_NOTIFICA);
			}
			
			Transazione transactionDTO = transazioneDaAggiornare!=null ? transazioneDaAggiornare : new Transazione();

			EsitiProperties esitiProperties = EsitiProperties.getInstance(this.logger, protocolFactory);
						
			// ** Consegna Multipla **
			// NOTA: l'esito deve essere compreso solo dopo aver capito se le notifiche devono essere consegna o meno poichè le notifiche stesse si basano sullo stato di come è terminata la transazione sincrona
			int connettoriMultipli = getNumeroConnettoriMultipli(info.getContext());
			boolean consegnaMultipla = isConsegnaMultipla(connettoriMultipli);
			
			ConfigurazioneMultiDeliver configurazioneConsegnaMultiplaProfiloSincrono = null;
			if(FaseTracciamento.POST_OUT_RESPONSE.equals(fase) && consegnaMultipla) {
				Object oConnettoreSync = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_SINCRONA );
				if (oConnettoreSync instanceof Boolean){
					consegnaMultiplaProfiloSincrono = (Boolean) oConnettoreSync;
				}
				else if (oConnettoreSync instanceof String){
					consegnaMultiplaProfiloSincrono = Boolean.valueOf( (String) oConnettoreSync );
				}
				
				if(consegnaMultiplaProfiloSincrono) {
					Object oConnettoreSyncConfig = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.CONSEGNA_MULTIPLA_SINCRONA_CONFIGURAZIONE );
					if (oConnettoreSyncConfig instanceof ConfigurazioneMultiDeliver){
						configurazioneConsegnaMultiplaProfiloSincrono = (ConfigurazioneMultiDeliver) oConnettoreSyncConfig;
					}
				}
			}

			if(consegnaMultiplaProfiloSincrono) {
				if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getProtocolContext()!=null){
					nomePorta = transaction.getRequestInfo().getProtocolContext().getInterfaceName();
				}
				msgDiag = MsgDiagnostico.newInstance(info.getTipoPorta(),idDominio, info.getIdModulo(), nomePorta, requestInfo, info.getStato());
			}
			if(consegnaMultiplaProfiloSincrono && configurazioneConsegnaMultiplaProfiloSincrono!=null &&
					esito!=null && esito.getCode()!=null) {
				if(configurazioneConsegnaMultiplaProfiloSincrono.isNotificheByEsito()) {
					int esitoSincrono = esito.getCode();
					if(isEsito(esitiProperties.getEsitiCodeOk_senzaFaultApplicativo(), esitoSincrono)) {
						schedulaNotificheDopoConsegnaSincrona = configurazioneConsegnaMultiplaProfiloSincrono.isNotificheByEsito_ok();
					}
					else if(isEsito(esitiProperties.getEsitiCodeFaultApplicativo(), esitoSincrono)) {
						schedulaNotificheDopoConsegnaSincrona = configurazioneConsegnaMultiplaProfiloSincrono.isNotificheByEsito_fault();
					}
					else if(isEsito(esitiProperties.getEsitiCodeErroriConsegna(), esitoSincrono)) {
						schedulaNotificheDopoConsegnaSincrona = configurazioneConsegnaMultiplaProfiloSincrono.isNotificheByEsito_erroriConsegna();
					}
					// le richieste scartate non arrivano alla gestione della consegna in smistatore e quindi non potranno nemmeno essere notifiate
					/**else if(isEsito(esitiProperties.getEsitiCodeRichiestaScartate(), esitoSincrono)) {
					//	schedulaNotificheDopoConsegnaSincrona = configurazione_consegnaMultipla_profiloSincrono.isNotificheByEsito_richiesteScartate();
					//}*/
					else {
						schedulaNotificheDopoConsegnaSincrona = configurazioneConsegnaMultiplaProfiloSincrono.isNotificheByEsito_erroriProcessamento();
					}
				}
				else {
					schedulaNotificheDopoConsegnaSincrona = true;
				}
			}
			

			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("connettoreMultipli:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			
			// ** Identificativo di transazione **
			if (info.getContext().getObject(Costanti.ID_TRANSAZIONE)!=null){
				idTransazione = ((String)info.getContext().getObject(Costanti.ID_TRANSAZIONE));
				transactionDTO.setIdTransazione(idTransazione);
			}else
				throw new HandlerException("ID Transazione Assente");


			// ** Esito Transazione **
			if (esito!=null){
				if(consegnaMultipla) {
					if(consegnaMultiplaProfiloSincrono) {
						if(schedulaNotificheDopoConsegnaSincrona) {
							transactionDTO.setEsito(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA));
							if(esito.getCode()!=null){
								transactionDTO.setEsitoSincrono(esito.getCode());
							}
							transactionDTO.setConsegneMultipleInCorso(connettoriMultipli);
						}
						else {
							if(esito.getCode()!=null){
								transactionDTO.setEsito(esito.getCode());
							}
						}
					}
					else {
						transactionDTO.setEsito(esitiProperties.convertoToCode(EsitoTransazioneName.CONSEGNA_MULTIPLA));
						transactionDTO.setConsegneMultipleInCorso(connettoriMultipli);	
					}
				}
				else {
					if(esito.getCode()!=null){
						transactionDTO.setEsito(esito.getCode());
					}
				}
				transactionDTO.setEsitoContesto(esito.getContextType());
			}
			else {
				transactionDTO.setEsitoContesto(esitoContext);
			}
			// aggiungo fase all'esito
			transactionDTO.setEsitoContesto(EsitoUtils.buildEsitoContext(transactionDTO.getEsitoContesto(), fase));
						
			
			// ** Codice Risposta Uscita **
			if(info.getReturnCode()>0)
				transactionDTO.setCodiceRispostaUscita(info.getReturnCode()+"");
			
			// ** eventi di gestione **
			if(FaseTracciamento.POST_OUT_RESPONSE.equals(fase)){
				setEventi(transactionDTO, transaction,
						op2Properties,
						requestInfo,
						idDominio, info, idTransazione,
						times, timeStart, fase);
			}
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			
			// ** data_uscita_risposta **
			if(FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				boolean calcolaDataUscitaRispostaConDateAfterResponseSent = op2Properties.isTransazioniValorizzaDataUscitaRispostaUseDateAfterResponseSent();
				if(calcolaDataUscitaRispostaConDateAfterResponseSent && info.getDataRispostaSpedita()!=null) {
					if(FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
						transactionDTO.setDataUscitaRisposta(info.getDataRispostaSpedita());
					}
				}
				else if(!calcolaDataUscitaRispostaConDateAfterResponseSent && info.getDataPrimaSpedizioneRisposta()!=null) {
					transactionDTO.setDataUscitaRisposta(info.getDataPrimaSpedizioneRisposta());
				}
				else if (transaction.getDataUscitaRisposta()!=null){
					transactionDTO.setDataUscitaRisposta(transaction.getDataUscitaRisposta());
				}
				else{
					// creo sempre una data di risposta.
					transactionDTO.setDataUscitaRisposta(DateManager.getDate());
				}
				
				// ** data_uscita_risposta_stream **
				if(calcolaDataUscitaRispostaConDateAfterResponseSent && info.getDataPrimaSpedizioneRisposta()!=null) {
					transactionDTO.setDataUscitaRispostaStream(info.getDataPrimaSpedizioneRisposta());
				}
				else if(!calcolaDataUscitaRispostaConDateAfterResponseSent && info.getDataRispostaSpedita()!=null &&
					FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
					transactionDTO.setDataUscitaRispostaStream(info.getDataRispostaSpedita());
				}
				
				if(calcolaDataUscitaRispostaConDateAfterResponseSent && transactionDTO.getDataUscitaRispostaStream()==null) {
					transactionDTO.setDataUscitaRispostaStream(transactionDTO.getDataUscitaRisposta()); // uso la stessa
				}
			}
			
			// ** dimensione_ingresso_risposta **
			if(FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				if (info.getInputResponseMessageSize()!=null && info.getInputResponseMessageSize()>0 &&
					transaction!=null && transaction.getDataIngressoRisposta()!=null) { // altrimenti non ha senso, poichè non c'è stato un vero inoltro verso il backend
					
					boolean add = false;
					if(info.getContext()!=null) {
						Object o = info.getContext().get(DiagnosticInputStream.DIAGNOSTIC_INPUT_STREAM_RESPONSE_START_DATE);
						if(o instanceof Date) {
							add = true;
						}
					}
					
					if(add) {
						transactionDTO.setRispostaIngressoBytes(info.getInputResponseMessageSize());
					}
				}
	
				// ** dimensione_uscita_risposta **
				if (info.getOutputResponseMessageSize()!=null && info.getOutputResponseMessageSize()>0){
					transactionDTO.setRispostaUscitaBytes(info.getOutputResponseMessageSize());
				}
			}
			
			// ** FAULT **
			if(FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				transactionDTO.setFaultIntegrazione(transaction.getFaultIntegrazione());
				transactionDTO.setFormatoFaultIntegrazione(transaction.getFormatoFaultIntegrazione());
				transactionDTO.setFaultCooperazione(transaction.getFaultCooperazione());
				transactionDTO.setFormatoFaultCooperazione(transaction.getFormatoFaultCooperazione());
			}
/**			boolean readFault = false;
//			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
//				if(transactionDTO.getFaultIntegrazione()==null){
//					readFault = true;
//				}
//			}
//			else{
//				if(transactionDTO.getFaultCooperazione()==null){
//					readFault = true;
//				}
//			}*/
			// Il fault potrebbe essere cambiato durante la fase di gestione dalla consegna della risposta
			boolean readFault = true;
			if(readFault && FaseTracciamento.POST_OUT_RESPONSE.equals(fase)){
				setFaultInfo(transactionDTO, 
						op2Properties, 
						info);
			}
			
			// ** diagnostici **
			if(esito!=null) {
				String errore = InfoEsitoTransazioneFormatUtils.getMessaggioDiagnosticoErroreRilevante(this.logger, transactionDTO.getEsito(), protocolFactory, transaction.getMsgDiagnostici(), true);			
				String warning = InfoEsitoTransazioneFormatUtils.getMessaggioDiagnosticoWarning(this.logger, transactionDTO.getEsito(), protocolFactory, transaction.getMsgDiagnostici());			
				transactionDTO.setErrorLog(errore);
				transactionDTO.setWarningLog(warning);
			}

			
			// ***** NOTA: tutte le informazioni sopra devono essere "gestite in UPDATE" nel metodo TracciamentoManager.registraTransazione
			
			/*** ESCO SE SONO IN FASE POST OUT DI AGGIORNAMENTO DEI DATI */
			
			if(transazioneDaAggiornare!=null) {
				return transactionDTO; // nell'oggetto ci sono già tutti i dati eccetto l'esito corretto
			}
			
			
			
			
			

			// ** stato **
			// Stato di una transazione (marca la transazione con uno stato tramite la configurazione plugin)
			if(transaction.getStato()!=null) {
				transactionDTO.setStato(transaction.getStato());	
			}

			// ** Ruolo transazione **
			// invocazioneOneway (1)
			// invocazioneSincrona (2)
			// invocazioneAsincronaSimmetrica (3)
			// rispostaAsincronaSimmetrica (4)
			// invocazioneAsincronaAsimmetrica (5)
			// richiestaStatoAsincronaAsimmetrica (6)
			// integrationManager (7)
			/**System.out.println("SCENARIO DI COOPERAZIONE ["+transaction.getScenarioCooperazione()+"]");*/
			RuoloTransazione ruolo = RuoloTransazione.getEnumConstantFromOpenSPCoopValue(transaction.getScenarioCooperazione());
			if(ruolo!=null)
				transactionDTO.setRuoloTransazione(ruolo.getValoreAsInt());
			else
				transactionDTO.setRuoloTransazione(-1);


			// ** protocollo **
			transactionDTO.setProtocollo(info.getProtocolFactory().getProtocol());

			
			// ** header HTTP **
			if(transaction.getRequestInfo()!=null && 
					transaction.getRequestInfo().getProtocolContext()!=null &&
					transaction.getRequestInfo().getProtocolContext().getRequestType()!=null) {
				transactionDTO.setTipoRichiesta(transaction.getRequestInfo().getProtocolContext().getRequestType());
			}
			// ** Codice Risposta Ingresso **
			transactionDTO.setCodiceRispostaIngresso(transaction.getCodiceTrasportoRichiesta());
			// ** Codice Risposta Uscita **
			// Gestite prima per la doppia fase, come per l'esito

			// ** Tempi di latenza **

			boolean valorizzataDataIngressoConDataAccettazione = op2Properties.isTransazioniValorizzaDataIngressoConDataAccettazione();
			
			// Se data_accettazione_richiesta è null viene impostata a CURRENT_TIMESTAMP
			if (transaction.getDataAccettazioneRichiesta()!=null){
				transactionDTO.setDataAccettazioneRichiesta(transaction.getDataAccettazioneRichiesta());
			}else{
				Object o = info.getContext().getObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA);
				if(o instanceof Date){
					transactionDTO.setDataAccettazioneRichiesta((Date) o);
				}
				else{
					transactionDTO.setDataAccettazioneRichiesta(DateManager.getDate());
				}
			}
			
			// Se data_ingresso_richiesta è null viene impostata a CURRENT_TIMESTAMP
			if(valorizzataDataIngressoConDataAccettazione) {
				transactionDTO.setDataIngressoRichiesta(transactionDTO.getDataAccettazioneRichiesta());
			}
			else {
				if (transaction.getDataIngressoRichiesta()!=null){
					transactionDTO.setDataIngressoRichiesta(transaction.getDataIngressoRichiesta());
				}else{
					Object o = info.getContext().getObject(CostantiPdD.DATA_INGRESSO_RICHIESTA);
					if(o instanceof Date){
						transactionDTO.setDataIngressoRichiesta((Date) o);
					}
					else{
						transactionDTO.setDataIngressoRichiesta(DateManager.getDate());
					}
				}
			}
			
			// data_ingresso_richiesta_stream
			if(info.getContext()!=null) {
				Object o = info.getContext().get(DiagnosticInputStream.DIAGNOSTIC_INPUT_STREAM_REQUEST_COMPLETE_DATE);
				if(o==null) {
					o = info.getContext().get(DiagnosticInputStream.DIAGNOSTIC_INPUT_STREAM_REQUEST_ERROR_DATE);
				}
				if(o instanceof Date) {
					Date d = (Date) o;
					transactionDTO.setDataIngressoRichiestaStream(d);
				}
			}
			
			// data_uscita_richiesta si imposta se e' diversa da null
			if(FaseTracciamento.OUT_REQUEST.equals(fase) || FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				if (transaction.getDataUscitaRichiesta()!=null) {
					transactionDTO.setDataUscitaRichiesta(transaction.getDataUscitaRichiesta());
				}
				
				// data_uscita_richiesta_stream
				if(transaction.getDataRichiestaInoltrata()!=null) {
					transactionDTO.setDataUscitaRichiestaStream(transaction.getDataRichiestaInoltrata());
				}
			}

			if(FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				// data_accettazione_risposta
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				/**if (transaction.getDimensioneIngressoRispostaBytes()!=null && transaction.getDimensioneIngressoRispostaBytes()>0){*/
				// L'INFORMAZIONE DEVE INVECE ESSERE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				if (transaction.getDataAccettazioneRisposta()!=null){
					transactionDTO.setDataAccettazioneRisposta(transaction.getDataAccettazioneRisposta());
				}
				
				// data_ingresso_risposta
				// La porta di dominio mi passa sempre questa informazione.
				// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
				/**if (transaction.getDimensioneIngressoRispostaBytes()!=null && transaction.getDimensioneIngressoRispostaBytes()>0){*/
				// L'INFORMAZIONE DEVE INVECE ESSERE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
				if(valorizzataDataIngressoConDataAccettazione && transactionDTO.getDataAccettazioneRisposta()!=null) {
					transactionDTO.setDataIngressoRisposta(transactionDTO.getDataAccettazioneRisposta());
				}
				else {
					if (transaction.getDataIngressoRisposta()!=null){
						transactionDTO.setDataIngressoRisposta(transaction.getDataIngressoRisposta());
					}
				}
				
				// data_ingresso_risposta_stream
				if(info.getContext()!=null) {
					Object o = info.getContext().get(DiagnosticInputStream.DIAGNOSTIC_INPUT_STREAM_RESPONSE_COMPLETE_DATE);
					if(o==null) {
						o = info.getContext().get(DiagnosticInputStream.DIAGNOSTIC_INPUT_STREAM_RESPONSE_ERROR_DATE);
					}
					if(o instanceof Date) {
						Date d = (Date) o;
						transactionDTO.setDataIngressoRispostaStream(d);
					}
				}
	
				// data_uscita_risposta
				// data_uscita_risposta_stream
				// Gestite prima per la doppia fase, come per l'esito
			}


			// ** Dimensione messaggi gestiti **
			// Se le dimensioni (BIGINT) sono null occorre impostarle a zero

			// dimensione_ingresso_richiesta
			if (info.getInputRequestMessageSize()!=null && info.getInputRequestMessageSize()>0){
				transactionDTO.setRichiestaIngressoBytes(info.getInputRequestMessageSize());
			}
			if(transactionDTO.getRichiestaIngressoBytes()==null &&
					FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				transactionDTO.setRichiestaIngressoBytes(readDimensioneFromDumpBinario(TipoMessaggio.RICHIESTA_INGRESSO_DUMP_BINARIO, transaction));
			}

			// dimensione_uscita_richiesta
			if( 
					(FaseTracciamento.OUT_REQUEST.equals(fase) || FaseTracciamento.OUT_RESPONSE.equals(fase) || FaseTracciamento.POST_OUT_RESPONSE.equals(fase))
					&& 
					(info.getOutputRequestMessageSize()!=null && info.getOutputRequestMessageSize()>0 &&
						transactionDTO.getDataUscitaRichiesta()!=null) // altrimenti non ha senso, poichè non c'è stato un vero inoltro verso il backend
				) {
				transactionDTO.setRichiestaUscitaBytes(info.getOutputRequestMessageSize());
			}
			if(transactionDTO.getRichiestaUscitaBytes()==null &&
					FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				transactionDTO.setRichiestaUscitaBytes(readDimensioneFromDumpBinario(TipoMessaggio.RICHIESTA_USCITA_DUMP_BINARIO, transaction));
			}

			// dimensione_ingresso_risposta
			// dimensione_uscita_risposta
			// Gestite prima per la doppia fase, come per l'esito: faccio una ulteriore verifica se siamo nell'ultima fase e sono null
			if(transactionDTO.getRispostaIngressoBytes()==null &&
					FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				transactionDTO.setRispostaIngressoBytes(readDimensioneFromDumpBinario(TipoMessaggio.RISPOSTA_INGRESSO_DUMP_BINARIO, transaction));
			}
			if(transactionDTO.getRispostaUscitaBytes()==null &&
					FaseTracciamento.POST_OUT_RESPONSE.equals(fase)) {
				transactionDTO.setRispostaUscitaBytes(readDimensioneFromDumpBinario(TipoMessaggio.RISPOSTA_USCITA_DUMP_BINARIO, transaction));
			}


			// ** Dati Pdd **
			transactionDTO.setPddCodice(idDominio.getCodicePorta());
			transactionDTO.setPddTipoSoggetto(idDominio.getTipo());
			transactionDTO.setPddNomeSoggetto(idDominio.getNome());
			if(info.getTipoPorta()!=null){
				transactionDTO.setPddRuolo(PddRuolo.toEnumConstant(info.getTipoPorta().getTipo()));
			}

			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("baseContext:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			

			// ** FAULT **
			// Gestite prima per la doppia fase, come per l'esito
			
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("fault:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}


			// ** Soggetto Fruitore **
			if (info.getProtocollo()!=null && info.getProtocollo().getFruitore()!=null) {
				transactionDTO.setTipoSoggettoFruitore(info.getProtocollo().getFruitore().getTipo());
				transactionDTO.setNomeSoggettoFruitore(info.getProtocollo().getFruitore().getNome());
				transactionDTO.setIdportaSoggettoFruitore(info.getProtocollo().getFruitore().getCodicePorta());
				transactionDTO.setIndirizzoSoggettoFruitore(info.getProtocollo().getIndirizzoFruitore());
			}
			else if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getFruitore()!=null) {
				transactionDTO.setTipoSoggettoFruitore(transaction.getRequestInfo().getFruitore().getTipo());
				transactionDTO.setNomeSoggettoFruitore(transaction.getRequestInfo().getFruitore().getNome());
				transactionDTO.setIdportaSoggettoFruitore(transaction.getRequestInfo().getFruitore().getCodicePorta());
			}


			// ** Soggetto Erogatore **
			if (info.getProtocollo()!=null && info.getProtocollo().getErogatore()!=null) {
				transactionDTO.setTipoSoggettoErogatore(info.getProtocollo().getErogatore().getTipo());
				transactionDTO.setNomeSoggettoErogatore(info.getProtocollo().getErogatore().getNome());
				transactionDTO.setIdportaSoggettoErogatore(info.getProtocollo().getErogatore().getCodicePorta());
				transactionDTO.setIndirizzoSoggettoErogatore(info.getProtocollo().getIndirizzoErogatore());
			}
			else if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null &&
					transaction.getRequestInfo().getIdServizio().getSoggettoErogatore()!=null) {
				transactionDTO.setTipoSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getTipo());
				transactionDTO.setNomeSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getNome());
				transactionDTO.setIdportaSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getCodicePorta());
			}


			// ** Identificativi Messaggi **
			if (info.getProtocollo()!=null) {

				// Richiesta
				String idMessaggioRichiesta = info.getProtocollo().getIdRichiesta();
				if(
						//TipoPdD.APPLICATIVA.equals(context.getTipoPorta()) &&
						CostantiLabel.MODIPA_PROTOCOL_NAME.equals(protocolFactory.getProtocol()) &&
						tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getID()!=null &&
						!tracciaRichiesta.getBusta().getID().equals(idMessaggioRichiesta)) {
					// modificato durante la gestione della validazione semantica
					idMessaggioRichiesta = tracciaRichiesta.getBusta().getID();
				}
				Timestamp dateInternaIdProtocolloRichiesta = null;
				if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory) &&
					idMessaggioRichiesta!=null){
					dateInternaIdProtocolloRichiesta = DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRichiesta);
				}

				// Risposta
				String idMessaggioRisposta = null;
				Timestamp dateInternaIdProtocolloRisposta = null;
				// Nel contesto l'id di riposta puo' essere null o un valore generato per la confermaPresa in carico.
				// Se e' presenta una busta di risposta nella transazione deve pero' finire l'id di protocollo della busta.
				if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getID()!=null){
					idMessaggioRisposta = tracciaRisposta.getBusta().getID();
					if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						dateInternaIdProtocolloRisposta = DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRisposta);
					}
				}
				else if(info.getProtocollo().getIdRisposta()!=null){
					idMessaggioRisposta = info.getProtocollo().getIdRisposta();
					if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
						dateInternaIdProtocolloRisposta =  DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRisposta);
					}
				}

				// Set
				transactionDTO.setIdMessaggioRichiesta(idMessaggioRichiesta);
				transactionDTO.setIdMessaggioRisposta(idMessaggioRisposta);
				if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled(protocolFactory)){
					transactionDTO.setDataIdMsgRichiesta(dateInternaIdProtocolloRichiesta);
					transactionDTO.setDataIdMsgRisposta(dateInternaIdProtocolloRisposta);
				}

			}


			// ** altre informazioni **
			if (info.getProtocollo()!=null && info.getProtocollo().getProfiloCollaborazione()!=null) {
				transactionDTO.setProfiloCollaborazioneOp2(info.getProtocollo().getProfiloCollaborazione().getEngineValue());		
			}
			else if(profiloCollaborazioneBustaTracciaRichiesta!=null){
				transactionDTO.setProfiloCollaborazioneOp2(profiloCollaborazioneBustaTracciaRichiesta.getEngineValue());
			}
			if (info.getProtocollo()!=null && info.getProtocollo().getProfiloCollaborazioneValue()!=null) {
				transactionDTO.setProfiloCollaborazioneProt(info.getProtocollo().getProfiloCollaborazioneValue());
			}
			else if(profiloCollaborazioneValueBustaTracciaRichiesta!=null){
				transactionDTO.setProfiloCollaborazioneProt(profiloCollaborazioneValueBustaTracciaRichiesta);
			}
			IDAccordo idAccordo = null;
			if (info.getProtocollo()!=null){

				transactionDTO.setIdCollaborazione(info.getProtocollo().getCollaborazione());

				idAccordo = info.getProtocollo().getIdAccordo();
				if(idAccordo!=null){
					transactionDTO.setUriAccordoServizio(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}

				transactionDTO.setTipoServizio(info.getProtocollo().getTipoServizio());
				transactionDTO.setNomeServizio(info.getProtocollo().getServizio());
				if(info.getProtocollo().getVersioneServizio()!=null && info.getProtocollo().getVersioneServizio().intValue()>0) {
					transactionDTO.setVersioneServizio(info.getProtocollo().getVersioneServizio());
				}
				transactionDTO.setAzione(info.getProtocollo().getAzione());
			}
			if(transactionDTO.getTipoServizio()==null &&
				(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) 
				){
				transactionDTO.setTipoServizio(transaction.getRequestInfo().getIdServizio().getTipo());
			}
			if(transactionDTO.getNomeServizio()==null &&
				(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) 
				){
				transactionDTO.setNomeServizio(transaction.getRequestInfo().getIdServizio().getNome());
			}
			if(
				(info.getProtocollo()==null || info.getProtocollo().getVersioneServizio()==null || info.getProtocollo().getVersioneServizio().intValue()<=0) 
				&&
				(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) 
				){
				transactionDTO.setVersioneServizio(transaction.getRequestInfo().getIdServizio().getVersione());
			}
			if(transactionDTO.getAzione()==null &&
				(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) 
				){
				transactionDTO.setAzione(transaction.getRequestInfo().getIdServizio().getAzione());
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("identificativi:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			RegistroServiziManager registroServiziManager = RegistroServiziManager.getInstance(info.getStato());
			if(idAccordo==null &&
				(transactionDTO.getNomeServizio()!=null && transactionDTO.getTipoServizio()!=null && 
				transactionDTO.getNomeSoggettoErogatore()!=null && transactionDTO.getTipoSoggettoErogatore()!=null) 
				) {
				idAccordo = getIdAccordo(transactionDTO, registroServiziManager, requestInfo);
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("id-api:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			if(idAccordo!=null) {
				setAccordoInfo(idAccordo, transactionDTO, 
						op2Properties, registroServiziManager, requestInfo,
						idDominio, info, idTransazione,
						times, timeStart, fase);
				if(times!=null) {
					timeStart = DateManager.getTimeMillis();
				}
			}


			// ** Identificativo asincrono se utilizzato come riferimento messaggio nella richiesta (2 fase asincrona) **
				
			if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null &&
					(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()) || ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()) ) ){
				Busta busta = tracciaRichiesta.getBusta();
				if(busta.getRiferimentoMessaggio()!=null){
					// Seconda fase dei profili asincroni
					transactionDTO.setIdAsincrono(busta.getRiferimentoMessaggio());
				}
				else{
					transactionDTO.setIdAsincrono(busta.getCollaborazione());
				}
			}
			if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null &&
					ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()) ){
				Busta busta = tracciaRichiesta.getBusta();
				if(busta!=null){
					transactionDTO.setTipoServizioCorrelato(busta.getTipoServizioCorrelato());
					transactionDTO.setNomeServizioCorrelato(busta.getServizioCorrelato());
				}
			}
			else if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null &&
					ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(tracciaRisposta.getBusta().getProfiloDiCollaborazione()) ){
				Busta busta = tracciaRisposta.getBusta();
				if(busta!=null){
					transactionDTO.setTipoServizioCorrelato(busta.getTipoServizioCorrelato());
					transactionDTO.setNomeServizioCorrelato(busta.getServizioCorrelato());
				}
			}
			
			if(transactionDTO.getIdCollaborazione()==null) {
				if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getCollaborazione()!=null) {
					transactionDTO.setIdCollaborazione(tracciaRichiesta.getBusta().getCollaborazione());
				}
				else if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getCollaborazione()!=null) {
					transactionDTO.setIdCollaborazione(tracciaRisposta.getBusta().getCollaborazione());
				}
			}
			
			boolean asincrono = tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null &&
					(ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()) || ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(tracciaRichiesta.getBusta().getProfiloDiCollaborazione()) );
			if(!asincrono) {
				if(transactionDTO.getIdAsincrono()==null &&
					(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getRiferimentoMessaggio()!=null) 
					){
					transactionDTO.setIdAsincrono(tracciaRichiesta.getBusta().getRiferimentoMessaggio());
				}
				if(transactionDTO.getIdAsincrono()==null &&
					(info.getProtocollo()!=null)
					){
					transactionDTO.setIdAsincrono(info.getProtocollo().getRiferimentoAsincrono());
				}
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("async:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}

			// ** info protocollo **
			if(tracciaRichiesta!=null){
				if(this.transazioniRegistrazioneTracceHeaderRawEnabled){
					if(tracciaRichiesta.getBustaAsString()!=null){
						transactionDTO.setHeaderProtocolloRichiesta(tracciaRichiesta.getBustaAsString());
					}
					else if(tracciaRichiesta.getBustaAsByteArray()!=null){
						transactionDTO.setHeaderProtocolloRichiesta(new String(tracciaRichiesta.getBustaAsByteArray()));
					}
					else if(tracciaRichiesta.getBustaAsRawContent()!=null){
						setHeaderProtocolloRichiesta(transactionDTO, tracciaRichiesta);
					}
				}
				if(tracciaRichiesta.getBusta()!=null){
					if(this.transazioniRegistrazioneTracceDigestEnabled){
						transactionDTO.setDigestRichiesta(tracciaRichiesta.getBusta().getDigest());
					}
					if(this.transazioniRegistrazioneTracceProtocolPropertiesEnabled &&
						tracciaRichiesta.getBusta().sizeProperties()>0){
						Map<String, List<String>> propertiesBusta = new HashMap<>();
						String [] pNames = tracciaRichiesta.getBusta().getPropertiesNames();
						if(pNames!=null){
							for (int i = 0; i < pNames.length; i++) {
								String key = pNames[i];
								String value = tracciaRichiesta.getBusta().getProperty(key);
								if(key!=null && value!=null){
									TransportUtils.put(propertiesBusta, key, value, false);
								}
							}
						}
						PropertiesSerializator ps = new PropertiesSerializator(propertiesBusta);
						setProtocolloExtInfoRichiesta(transactionDTO, ps);
					}
				}
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.fillTransactionDetails.add("traccia-richiesta:"+timeProcess);
					
					timeStart = DateManager.getTimeMillis();
				}
			}
			if(tracciaRisposta!=null){				
				if(this.transazioniRegistrazioneTracceHeaderRawEnabled){
					if(tracciaRisposta.getBustaAsString()!=null){
						transactionDTO.setHeaderProtocolloRisposta(tracciaRisposta.getBustaAsString());
					}
					else if(tracciaRisposta.getBustaAsByteArray()!=null){
						transactionDTO.setHeaderProtocolloRisposta(new String(tracciaRisposta.getBustaAsByteArray()));
					}
					else if(tracciaRisposta.getBustaAsRawContent()!=null){
						setHeaderProtocolloRisposta(transactionDTO, tracciaRisposta);
					}
				}
				if(tracciaRisposta.getBusta()!=null){
					if(this.transazioniRegistrazioneTracceDigestEnabled){
						transactionDTO.setDigestRisposta(tracciaRisposta.getBusta().getDigest());
					}
					if(this.transazioniRegistrazioneTracceProtocolPropertiesEnabled &&
						tracciaRisposta.getBusta().sizeProperties()>0){
						Map<String, List<String>> propertiesBusta = new HashMap<>();
						String [] pNames = tracciaRisposta.getBusta().getPropertiesNames();
						if(pNames!=null){
							for (int i = 0; i < pNames.length; i++) {
								String key = pNames[i];
								String value = tracciaRisposta.getBusta().getProperty(key);
								if(key!=null && value!=null){
									TransportUtils.put(propertiesBusta, key, value, false);
								}
							}
						}
						PropertiesSerializator ps = new PropertiesSerializator(propertiesBusta);
						setProtocolloExtInfoRisposta(transactionDTO, ps);
					}
				}
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.fillTransactionDetails.add("traccia-risposta:"+timeProcess);
					
					timeStart = DateManager.getTimeMillis();
				}
			}
			
			// ** diagnostici **
			// Gestite prima per la doppia fase, come per l'esito
			
			// ** informazioni di integrazione **
			if(info.getIntegrazione()!=null){
				transactionDTO.setIdCorrelazioneApplicativa(info.getIntegrazione().getIdCorrelazioneApplicativa());
				transactionDTO.setIdCorrelazioneApplicativaRisposta(transaction.getCorrelazioneApplicativaRisposta());
				transactionDTO.setServizioApplicativoFruitore(info.getIntegrazione().getServizioApplicativoFruitore());
				if(info!=null && info.getContext()!=null &&
						info.getContext().containsKey(CostantiPdD.CONNETTORE_MULTIPLO_SELEZIONATO)) {
					Object o = info.getContext().getObject(CostantiPdD.CONNETTORE_MULTIPLO_SELEZIONATO);
					if(o instanceof String) {
						String sa = (String) o;
						if(sa!=null && !"".equals(sa)) {
							transactionDTO.setServizioApplicativoErogatore(sa);
						}
					}
				}
				if(transactionDTO.getServizioApplicativoErogatore()==null) {
					// in caso di porta applicativa si salva l'elenco dei servizi applicativi erogatori
					if(info.getIntegrazione().sizeServiziApplicativiErogatori()>0){
						StringBuilder saErogatori = new StringBuilder();
						for (int i=0; i<info.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
							if (i>0){
								saErogatori.append(",");
							}
							saErogatori.append( info.getIntegrazione().getServizioApplicativoErogatore(i) );
						}
						transactionDTO.setServizioApplicativoErogatore(saErogatori.toString().length()>2000 ? saErogatori.toString().substring(0, 1999) : saErogatori.toString());
					}
					else if(transaction.getServiziApplicativiErogatore()!=null && !transaction.getServiziApplicativiErogatore().isEmpty()){
						StringBuilder saErogatori = new StringBuilder();
						for (int i=0; i<transaction.getServiziApplicativiErogatore().size(); i++) {
							if (i>0){
								saErogatori.append(",");
							}
							saErogatori.append( transaction.getServiziApplicativiErogatore().get(i) );
						}
						transactionDTO.setServizioApplicativoErogatore(saErogatori.toString().length()>2000 ? saErogatori.toString().substring(0, 1999) : saErogatori.toString());
					}
				}
			}
			
			if(info.getContext()!=null){
				Object operazioneIM = info.getContext().getObject(CostantiPdD.TIPO_OPERAZIONE_IM);
				if(operazioneIM instanceof String){
					String op = (String) operazioneIM;
					transactionDTO.setOperazioneIm(op);
				}
			}
			
			if(transactionDTO.getServizioApplicativoFruitore()==null &&
				// provo a vedere se fosse nella traccia di richiesta
				(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getServizioApplicativoFruitore()!=null)
				){
				transactionDTO.setServizioApplicativoFruitore(tracciaRichiesta.getBusta().getServizioApplicativoFruitore());
			}
			if(transactionDTO.getServizioApplicativoFruitore()==null &&
				// provo a vedere se fosse nella traccia di risposta
				(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getServizioApplicativoFruitore()!=null)
				){
				transactionDTO.setServizioApplicativoFruitore(tracciaRisposta.getBusta().getServizioApplicativoFruitore());
			}
			// correggo anonimo
			if(transactionDTO.getServizioApplicativoFruitore()!=null && CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(transactionDTO.getServizioApplicativoFruitore())){
				transactionDTO.setServizioApplicativoFruitore(null);
			}
			
			if(transactionDTO.getServizioApplicativoErogatore()==null &&
				// provo a vedere se fosse nella traccia di richiesta
				(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getServizioApplicativoErogatore()!=null)
				){
				transactionDTO.setServizioApplicativoErogatore(tracciaRichiesta.getBusta().getServizioApplicativoErogatore());
			}
			if(transactionDTO.getServizioApplicativoErogatore()==null &&
				// provo a vedere se fosse nella traccia di risposta
				(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getServizioApplicativoErogatore()!=null)
				){
				transactionDTO.setServizioApplicativoErogatore(tracciaRisposta.getBusta().getServizioApplicativoErogatore());
			}
			
			transactionDTO.setLocationRichiesta(tracciaRichiesta!=null ? tracciaRichiesta.getLocation() : null);
			transactionDTO.setLocationRisposta(tracciaRisposta!=null ? tracciaRisposta.getLocation() : null);
			
			if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getProtocolContext()!=null){
				transactionDTO.setNomePorta(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
			}
			
			if(transaction.getCredenziali()!=null){
				transactionDTO.setCredenziali(transaction.getCredenziali());
			}else{
				Object o = info.getContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_CREDENZIALI);
				if(o instanceof String){
					transactionDTO.setCredenziali((String)o);
				}
				else{
					o = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE);
					if(o instanceof String){
						transactionDTO.setCredenziali((String)o);
					}
				}
			}
			
			transactionDTO.setLocationConnettore(transaction.getLocation());
			
			if(transaction.getUrlInvocazione()!=null){
				transactionDTO.setUrlInvocazione(transaction.getUrlInvocazione());
			}else{
				Object o = info.getContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_URL_INVOCAZIONE);
				if(o instanceof String){
					transactionDTO.setUrlInvocazione((String)o);
				}
				else{
					o = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE);
					if(o instanceof String){
						transactionDTO.setUrlInvocazione((String)o);
					}
				}
			}
			
			// ** cluster-id **
			transactionDTO.setClusterId(op2Properties.getClusterId(false));
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("integration:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			// credenziali
			if(transaction.getCredenzialiMittente()!=null) {
				
				// trasporto
				if(transaction.getCredenzialiMittente().getTrasporto()!=null) {
					transactionDTO.setTrasportoMittente(transaction.getCredenzialiMittente().getTrasporto().getId()+"");
				}
				
				// token
				if(transaction.getCredenzialiMittente().getTokenIssuer()!=null) {
					transactionDTO.setTokenIssuer(transaction.getCredenzialiMittente().getTokenIssuer().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getTokenClientId()!=null) {
					transactionDTO.setTokenClientId(transaction.getCredenzialiMittente().getTokenClientId().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getTokenSubject()!=null) {
					transactionDTO.setTokenSubject(transaction.getCredenzialiMittente().getTokenSubject().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getTokenUsername()!=null) {
					transactionDTO.setTokenUsername(transaction.getCredenzialiMittente().getTokenUsername().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getTokenEMail()!=null) {
					transactionDTO.setTokenMail(transaction.getCredenzialiMittente().getTokenEMail().getId()+"");
				}
			}
			
			// token negoziazione
			InformazioniNegoziazioneToken informazioniNegoziazioneToken = null;
			if(this.transazioniRegistrazioneRetrieveTokenSaveAsTokenInfo) {
				if(transaction.getInformazioniNegoziazioneToken()!=null) {
					informazioniNegoziazioneToken = transaction.getInformazioniNegoziazioneToken();
					if(
							(
									op2Properties.isGestioneRetrieveToken_saveAsTokenInfo_excludeJwtSignature() &&
									(informazioniNegoziazioneToken.getAccessToken()!=null || informazioniNegoziazioneToken.getRefreshToken()!=null)
							)
							||
							(
									op2Properties.isGestioneRetrieveToken_grantType_rfc7523_saveClientAssertionJWTInfo_excludeJwtSignature() &&
									informazioniNegoziazioneToken.getRequest()!=null
							)
					) {
						// clone for fix 'Caused by: java.util.ConcurrentModificationException' e non modificare informazione in cache dovuto alla deleteSignature e alla sostituzione del token
						informazioniNegoziazioneToken = (InformazioniNegoziazioneToken) informazioniNegoziazioneToken.clone();
					}
				}
				
				if(informazioniNegoziazioneToken!=null) {
					if(op2Properties.isGestioneRetrieveToken_saveAsTokenInfo_excludeJwtSignature()) {
						if(informazioniNegoziazioneToken.getAccessToken()!=null) {
							String originale = informazioniNegoziazioneToken.getAccessToken();
							String senzaSignature = TokenUtilities.deleteSignature(informazioniNegoziazioneToken.getAccessToken());
							informazioniNegoziazioneToken.setAccessToken(senzaSignature);
							informazioniNegoziazioneToken.setClaims(TokenUtilities.replaceTokenInMapByValue(informazioniNegoziazioneToken.getClaims(), originale, senzaSignature));
							informazioniNegoziazioneToken.replaceInRawResponse(originale, senzaSignature);
						}
						if(informazioniNegoziazioneToken.getRefreshToken()!=null) {
							String originale = informazioniNegoziazioneToken.getRefreshToken();
							String senzaSignature = TokenUtilities.deleteSignature(informazioniNegoziazioneToken.getRefreshToken());
							informazioniNegoziazioneToken.setRefreshToken(senzaSignature);
							informazioniNegoziazioneToken.setClaims(TokenUtilities.replaceTokenInMapByValue(informazioniNegoziazioneToken.getClaims(), originale, senzaSignature));
							informazioniNegoziazioneToken.replaceInRawResponse(originale, senzaSignature);
						}
					}
					
					if(informazioniNegoziazioneToken.getRequest()!=null &&
						informazioniNegoziazioneToken.getRequest().getJwtClientAssertion()!=null && 
						informazioniNegoziazioneToken.getRequest().getJwtClientAssertion().getToken()!=null &&
						op2Properties.isGestioneRetrieveToken_grantType_rfc7523_saveClientAssertionJWTInfo_excludeJwtSignature()) {
						informazioniNegoziazioneToken.getRequest().getJwtClientAssertion().setToken(TokenUtilities.deleteSignature(informazioniNegoziazioneToken.getRequest().getJwtClientAssertion().getToken()));
					}
				}
			}
									
			// token info
			if(this.transazioniRegistrazioneTokenInformazioniNormalizzate && transaction.getInformazioniToken()!=null) {
				
				InformazioniToken informazioniToken = transaction.getInformazioniToken();
				if(informazioniToken.getToken()!=null && op2Properties.isGestioneTokenSaveTokenInfoValidationFailedExcludeJwtSignature()) {
					// clone for fix 'Caused by: java.util.ConcurrentModificationException' e non modificare informazione in cache dovuto alla deleteSignature e alla sostituzione del token
					informazioniToken = (InformazioniToken) informazioniToken.clone();
				}
				
				// token validazione
				if(informazioniToken.getToken()!=null && op2Properties.isGestioneTokenSaveTokenInfoValidationFailedExcludeJwtSignature()) {
					informazioniToken.setToken(TokenUtilities.deleteSignature(informazioniToken.getToken()));
				}
				
				// token negoziazione
				if(informazioniNegoziazioneToken!=null) {
					informazioniToken.setRetrievedToken(informazioniNegoziazioneToken);
				}
				
				// attributi
				InformazioniAttributi informazioniAttributi = null;
				if(!this.transazioniRegistrazioneAttributiInformazioniNormalizzate) {
					informazioniAttributi = informazioniToken.getAa();
					informazioniToken.setAa(null);
				}
				setTokenInfo(transactionDTO, informazioniToken);
				if(informazioniAttributi!=null) {
					informazioniToken.setAa(informazioniAttributi);
				}
								
			}
			if(transactionDTO.getTokenInfo()==null && this.transazioniRegistrazioneAttributiInformazioniNormalizzate &&
					transaction.getInformazioniAttributi()!=null) {
				if(informazioniNegoziazioneToken!=null) {
					setTokenInfo(transactionDTO, informazioniNegoziazioneToken, transaction);
				}
				else {
					setTokenInfo(transactionDTO, transaction);
				}
			}
			if(transactionDTO.getTokenInfo()==null && informazioniNegoziazioneToken!=null) {
				setTokenInfo(transactionDTO, informazioniNegoziazioneToken);
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("token:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			// tempi elaborazione
			if(this.transazioniRegistrazioneTempiElaborazione && transaction.getTempiElaborazione()!=null) {
				setTempiElaborazione(transactionDTO, transaction);
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("times:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			// ** Indirizzo IP **
			setAccordoInfoClientAddress(transactionDTO,
					requestInfo,
					idDominio, info, idTransazione,
					times, timeStart, fase);
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			
			// ** eventi di gestione **
			if(!FaseTracciamento.POST_OUT_RESPONSE.equals(fase)){ // altrimenti gestito prima come per l'esito
				setEventi(transactionDTO, transaction,
						op2Properties,
						requestInfo,
						idDominio, info, idTransazione,
						times, timeStart, fase);
			}
			if(times!=null) {
				timeStart = DateManager.getTimeMillis();
			}
			
			// ** filtro duplicati **
			if(richiestaDuplicata){
				transactionDTO.setDuplicatiRichiesta(-1);
			}
			else{
				transactionDTO.setDuplicatiRichiesta(0);
			}
			if(rispostaDuplicata){
				transactionDTO.setDuplicatiRisposta(-1);
			}
			else{
				transactionDTO.setDuplicatiRisposta(0);
			}
			
			
			// ** Extended Info **
			ExtendedTransactionInfo transactionExtendedInfo = null;
			if (info.getContext().getObject(Costanti.EXTENDED_INFO_TRANSAZIONE)!=null){
				transactionExtendedInfo = ((ExtendedTransactionInfo)info.getContext().getObject(Costanti.EXTENDED_INFO_TRANSAZIONE));
				List<String> keys = transactionExtendedInfo.keys();
				if(keys!=null && !keys.isEmpty()){
					for (String key : keys) {
						String value = transactionExtendedInfo.getValue(key);
						TransazioneExtendedInfo transazioneExtendedInfo = new TransazioneExtendedInfo();
						transazioneExtendedInfo.setNome(key);
						transazioneExtendedInfo.setValore(value);
						transactionDTO.addTransazioneExtendedInfo(transazioneExtendedInfo);
					}
				}
			}
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("ext-info:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			
			return transactionDTO;


		} catch (Exception e) {
			throw new HandlerException("Errore durante il popolamento della transazione da salvare su database: " + e.getLocalizedMessage(), e);
		} 
		finally{
		
			if(consegnaMultiplaProfiloSincrono) {
				timeStart = consegnaMultiplaProfiloSincrono(info, idDominio, idTransazione,
						times, timeStart,
						schedulaNotificheDopoConsegnaSincrona, msgDiag, nomePorta);
			}
			
			if(identificativoSaveTransactionContext!=null) {
				timeStart = saveTransactionContext(identificativoSaveTransactionContext, info, idDominio, idTransazione,
						times, timeStart,
						msgDiag);
			}
		}
	}
	
	private Long readDimensioneFromDumpBinario(TipoMessaggio tipoMessaggio, Transaction transaction) {
		if(transaction!=null && !transaction.getMessaggi().isEmpty()) {
			for (Messaggio msg : transaction.getMessaggi()) {
				if(tipoMessaggio.equals(msg.getTipoMessaggio()) && msg.getBody()!=null){
					return Long.valueOf(msg.getBody().size());
				}
			}
		}
		return null;
	}
	
	private void setFaultInfo(Transazione transactionDTO, 
			OpenSPCoop2Properties op2Properties, 
			InformazioniTransazione info) {
		String fault = null;
		String formatoFault = null;
		try{
			if(info.getResponse()!=null){
				if(ServiceBinding.SOAP.equals(info.getResponse().getServiceBinding())) {
					OpenSPCoop2SoapMessage soapMsg = info.getResponse().castAsSoap();
					if(soapMsg.hasSOAPFault()){
						
						ByteArrayOutputStream bout = new ByteArrayOutputStream();
						bout.write(info.getResponse().getAsByte(soapMsg.getSOAPPart().getEnvelope(), false));
						bout.flush();
						bout.close();
						
						Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(op2Properties.isTransazioniDebug());
						if(op2Properties.isTransazioniFaultPrettyPrint()){
							// Faccio una pretty-print: potevo fare anche direttamente passando il fault a metodo prettyPrint,
							// Pero' non veniva stampato correttamente il SOAPFault. Mi appoggio allora a SoapUtils.
							/**byte [] content = org.openspcoop2.message.soap.TunnelSoapUtils.sbustamentoMessaggio(context.getMessaggio());*/
							byte [] content = bout.toByteArray();
							fault = DumpUtility.toString(MessageXMLUtils.getInstance(soapMsg.getFactory()).newDocument(content), log, info.getResponse());
							/**System.out.println("IMPOSTATO FAULT IN TRANSACTION ["+fault+"]");*/
						}
						else{
							
							fault = bout.toString();
						}
						
						formatoFault = soapMsg.getMessageType().name();
					}

				}
				else {
					OpenSPCoop2RestMessage<?> restMsg = info.getResponse().castAsRest();
					if(restMsg.isProblemDetailsForHttpApis_RFC7807() || MessageRole.FAULT.equals(restMsg.getMessageRole())) {
						switch (restMsg.getMessageType()) {
						case XML:
							
							ByteArrayOutputStream bout = new ByteArrayOutputStream();
							restMsg.writeTo(bout, false);
							bout.flush();
							bout.close();
							
							Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(op2Properties.isTransazioniDebug());
							if(op2Properties.isTransazioniFaultPrettyPrint()){
								// Faccio una pretty-print: potevo fare anche direttamente passando il fault a metodo prettyPrint,
								// Pero' non veniva stampato correttamente il SOAPFault. Mi appoggio allora a SoapUtils.
								/**byte [] content = org.openspcoop2.message.soap.TunnelSoapUtils.sbustamentoMessaggio(context.getMessaggio());*/
								byte [] content = bout.toByteArray();
								fault = DumpUtility.toString(MessageXMLUtils.getInstance(restMsg.getFactory()).newDocument(content), log, info.getResponse());
								/**System.out.println("IMPOSTATO FAULT IN TRANSACTION ["+fault+"]");*/
							}
							else{
								
								fault = bout.toString();
							}
							
							formatoFault = restMsg.getMessageType().name();
							
							break;
							
						case JSON:
							
							bout = new ByteArrayOutputStream();
							restMsg.writeTo(bout, false);
							bout.flush();
							bout.close();
							
							if(op2Properties.isTransazioniFaultPrettyPrint()){
								
								JSONUtils jsonUtils = JSONUtils.getInstance(true);
								byte [] content = bout.toByteArray();
								JsonNode jsonNode = jsonUtils.getAsNode(content);
								fault = jsonUtils.toString(jsonNode);
								
							}
							else{
								
								fault = bout.toString();
							}
							
							formatoFault = restMsg.getMessageType().name();
							
							break;

						default:
							break;
						}
					}
				}
			}
		}catch(Exception e){
			this.logger.error("Errore durante il dump del soap fault",e);
		}
		if(TipoPdD.DELEGATA.equals(info.getTipoPorta())){
			transactionDTO.setFaultIntegrazione(fault);
			transactionDTO.setFormatoFaultIntegrazione(formatoFault);
		}
		else{
			transactionDTO.setFaultCooperazione(fault);
			transactionDTO.setFormatoFaultCooperazione(formatoFault);
		}
	}
	
	private IDAccordo getIdAccordo(Transazione transactionDTO, RegistroServiziManager registroServiziManager, RequestInfo requestInfo) {
		try {
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(transactionDTO.getTipoServizio(), transactionDTO.getNomeServizio(), 
				transactionDTO.getTipoSoggettoErogatore(), transactionDTO.getNomeSoggettoErogatore(), transactionDTO.getVersioneServizio());
			AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false, requestInfo);
			return  IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
		}catch(Exception e) {
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("Errore durante l'identificazione delle caratteristiche dell'API (Accesso servizio): "+e.getMessage(),e);
			return null;
		}
	}
		
	private void setAccordoInfo(IDAccordo idAccordo, Transazione transactionDTO, 
			OpenSPCoop2Properties op2Properties, RegistroServiziManager registroServiziManager, RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, long timeStart, FaseTracciamento fase) {
		try {
			AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(idAccordo, null, false, false, requestInfo);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
				transactionDTO.setTipoApi(TipoAPI.REST.getValoreAsInt());
			}
			else {
				transactionDTO.setTipoApi(TipoAPI.SOAP.getValoreAsInt());
			}
				
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("api:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
			String conflict = setAccordoInfoGruppi(aspc, transactionDTO,
					op2Properties,
					requestInfo,
					idDominio, info, idTransazione,
					times, fase);
			
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("tags:"+timeProcess+"/c"+conflict);
				
				timeStart = DateManager.getTimeMillis();
			}
			
		}catch(Exception e) {
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("Errore durante l'identificazione delle caratteristiche dell'API (Accesso servizio): "+e.getMessage(),e);
		}
		
		String conflict = setAccordoInfoUriAPC(idAccordo, transactionDTO,
				requestInfo,
				idDominio, info, idTransazione,
				times, fase);
		
		if(times!=null) {
			long timeEnd =  DateManager.getTimeMillis();
			long timeProcess = timeEnd-timeStart;
			times.fillTransactionDetails.add("uri-api:"+timeProcess+"/c"+conflict);
		}
	}
	
	private static final MapKey<String> CREDENZIALI_MITTENTE_GRUPPI_RESOLVED = org.openspcoop2.utils.Map.newMapKey("CREDENZIALI_MITTENTE_GRUPPI_RESOLVED");
	private String getCredenzialiMittenteGruppi(InformazioniTransazione info) {
		return (info!=null && info.getContext()!=null) ? (String) info.getContext().get(CREDENZIALI_MITTENTE_GRUPPI_RESOLVED) : null;
	}
	private void addCredenzialiMittenteGruppi(InformazioniTransazione info, String cred) {
		if(info!=null && info.getContext()!=null) {
			info.getContext().put(CREDENZIALI_MITTENTE_GRUPPI_RESOLVED, cred);
		}
	}
	private String setAccordoInfoGruppi(AccordoServizioParteComune aspc, Transazione transactionDTO,
			OpenSPCoop2Properties op2Properties,
			RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, FaseTracciamento fase) throws CoreException {
		String conflict = "0";
		if(aspc.getGruppi()!=null && aspc.getGruppi().sizeGruppoList()>0) {
			
			String cred = getCredenzialiMittenteGruppi(info);
			if(cred!=null && StringUtils.isNotEmpty(cred)) {
				transactionDTO.setGruppi(cred);
			}
			else {
				List<String> gruppi = new ArrayList<>();
				int count = 0;
				int maxLengthCredenziali = op2Properties.getTransazioniCredenzialiMittenteMaxLength()-AbstractCredenzialeList.PREFIX.length();
				for (int i=0; i<aspc.getGruppi().sizeGruppoList(); i++) {
					GruppoAccordo gruppoAccordo = aspc.getGruppi().getGruppo(i);
					String dbValue = AbstractCredenzialeList.getDBValue(gruppoAccordo.getNome());
					if(count+dbValue.length()<maxLengthCredenziali) {
						gruppi.add( gruppoAccordo.getNome() );
						count = count+dbValue.length();
					}
					else {
						// tronco i gruppi ai primi trovati. Sono troppi eventi gruppi associati alla api
					}
				}
				if(!gruppi.isEmpty()){
					StringBuilder sbConflict = null;
					if(times!=null) {
						sbConflict = new StringBuilder();
					}
					CredenzialeMittente credGruppi = null;
					try {
						credGruppi = GestoreAutenticazione.convertGruppiToCredenzialiMittenti(idDominio, info.getIdModulo(), idTransazione, gruppi, 
							null, fase.name()+".gruppi",
							sbConflict, requestInfo);
					}catch(Exception e) {
						throw new CoreException(e.getMessage(),e);
					}
					if(sbConflict!=null && sbConflict.length()>0) {
						conflict = sbConflict.toString();
					}
					if(credGruppi!=null) {
						cred = credGruppi.getId()+"";
						transactionDTO.setGruppi(cred);
						addCredenzialiMittenteGruppi(info, cred);
					}
				}
			}
		}
		return conflict;
	}
	
	
	private static final MapKey<String> CREDENZIALI_MITTENTE_URI_APC_RESOLVED = org.openspcoop2.utils.Map.newMapKey("CREDENZIALI_MITTENTE_URI_APC_RESOLVED");
	private String getCredenzialiMittenteUriAPC(InformazioniTransazione info) {
		return (info!=null && info.getContext()!=null) ? (String) info.getContext().get(CREDENZIALI_MITTENTE_URI_APC_RESOLVED) : null;
	}
	private void addCredenzialiMittenteUriAPC(InformazioniTransazione info, String cred) {
		if(info!=null && info.getContext()!=null) {
			info.getContext().put(CREDENZIALI_MITTENTE_URI_APC_RESOLVED, cred);
		}
	}
	private String setAccordoInfoUriAPC(IDAccordo idAccordo, Transazione transactionDTO,
			RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, FaseTracciamento fase) {
		String conflict = "0";
		try {
			String cred = getCredenzialiMittenteUriAPC(info);
			if(cred!=null && StringUtils.isNotEmpty(cred)) {
				transactionDTO.setUriApi(cred);
			}
			else {
				if(transactionDTO.getUriAccordoServizio()==null){
					transactionDTO.setUriAccordoServizio(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}
				StringBuilder sbConflict = null;
				if(times!=null) {
					sbConflict = new StringBuilder();
				}
				CredenzialeMittente credAPI = GestoreAutenticazione.convertAPIToCredenzialiMittenti(idDominio, info.getIdModulo(), idTransazione, transactionDTO.getUriAccordoServizio(), 
						null, fase.name()+".api",
						sbConflict, requestInfo);
				if(sbConflict!=null && sbConflict.length()>0) {
					conflict = sbConflict.toString();
				}
				if(credAPI!=null) {
					cred = credAPI.getId()+"";
					transactionDTO.setUriApi(cred);
					addCredenzialiMittenteUriAPC(info, cred);
				}
			}
		}catch(Exception e) {
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("Errore durante l'identificazione dell'identificativo dell'API (Accesso servizio parte comune): "+e.getMessage(),e);
		}
		return conflict;
	}
	
	private void setHeaderProtocolloRichiesta(Transazione transactionDTO, Traccia tracciaRichiesta) {
		try{
			transactionDTO.setHeaderProtocolloRichiesta(tracciaRichiesta.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT));
		}catch(Exception e){
			/// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			String msg = "Errore durante la conversione dell'oggetto Busta di richiesta ["+tracciaRichiesta.getBustaAsRawContent().getClass().getName()+"] in stringa";
			this.logger.error(msg);
		}
	}
	
	private void setProtocolloExtInfoRichiesta(Transazione transactionDTO, PropertiesSerializator ps) {
		try{
			transactionDTO.setProtocolloExtInfoRichiesta(ps.convertToDBColumnValue());
		}catch(Exception e){
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("Errore durante la conversione delle proprieta della Busta di richiesta: "+e.getMessage(),e);
		}
	}
	
	private void setHeaderProtocolloRisposta(Transazione transactionDTO, Traccia tracciaRisposta) {
		try{
			transactionDTO.setHeaderProtocolloRisposta(tracciaRisposta.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT));
		}catch(Exception e){
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			String msg = "Errore durante la conversione dell'oggetto Busta di risposta ["+tracciaRisposta.getBustaAsRawContent().getClass().getName()+"] in stringa";
			this.logger.error(msg);
		}
	}
	
	private void setProtocolloExtInfoRisposta(Transazione transactionDTO, PropertiesSerializator ps) {
		try{
			transactionDTO.setProtocolloExtInfoRisposta(ps.convertToDBColumnValue());
		}catch(Exception e){
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("Errore durante la conversione delle proprieta della Busta di risposta: "+e.getMessage(),e);
		}
	}
	
	private void setTokenInfo(Transazione transactionDTO, InformazioniToken informazioniToken) {
		try {
			transactionDTO.setTokenInfo(informazioniToken.toJson());
		}catch(Exception t) {
			this.logger.error("Serializzazione informazioni token non riuscita: "+t.getMessage(),t);
		}
	}
	private void setTokenInfo(Transazione transactionDTO, InformazioniNegoziazioneToken informazioniNegoziazioneToken, Transaction transaction) {
		try {
			InformazioniToken infoToken = new InformazioniToken(); // uso come aggregatore
			infoToken.setRetrievedToken(informazioniNegoziazioneToken);
			infoToken.setAa(transaction.getInformazioniAttributi());
			transactionDTO.setTokenInfo(infoToken.toJson());
		}catch(Exception t) {
			this.logger.error("Serializzazione informazioni attributi (aggregato insieme a client assertion) non riuscita: "+t.getMessage(),t);
		}
	}
	private void setTokenInfo(Transazione transactionDTO, Transaction transaction) {
		try {
			transactionDTO.setTokenInfo(transaction.getInformazioniAttributi().toJson());
		}catch(Exception t) {
			this.logger.error("Serializzazione informazioni attributi non riuscita: "+t.getMessage(),t);
		}
	}
	private void setTokenInfo(Transazione transactionDTO, InformazioniNegoziazioneToken informazioniNegoziazioneToken) {
		try {
			transactionDTO.setTokenInfo(informazioniNegoziazioneToken.toJson());
		}catch(Exception t) {
			this.logger.error("Serializzazione informazioni client assertion non riuscita: "+t.getMessage(),t);
		}
	}
	
	private void setTempiElaborazione(Transazione transactionDTO, Transaction transaction) {
		try {
			transactionDTO.setTempiElaborazione(TempiElaborazioneUtils.convertToDBValue(transaction.getTempiElaborazione()));
		}catch(Exception e) {
			// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
			this.logger.error("TempiElaborazioneUtils.convertToDBValue failed: "+e.getMessage(),e);
		}
	}
	
	private static final MapKey<String> CREDENZIALI_MITTENTE_CLIENT_ADDRESS_RESOLVED = org.openspcoop2.utils.Map.newMapKey("CREDENZIALI_MITTENTE_CLIENT_ADDRESS_RESOLVED");
	private String getCredenzialiMittenteClientAddress(InformazioniTransazione info) {
		return (info!=null && info.getContext()!=null) ? (String) info.getContext().get(CREDENZIALI_MITTENTE_CLIENT_ADDRESS_RESOLVED) : null;
	}
	private void addCredenzialiMittenteClientAddress(InformazioniTransazione info, String cred) {
		if(info!=null && info.getContext()!=null) {
			info.getContext().put(CREDENZIALI_MITTENTE_CLIENT_ADDRESS_RESOLVED, cred);
		}
	}
	private void setAccordoInfoClientAddress(Transazione transactionDTO,
			RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, long timeStart, FaseTracciamento fase) {
		Object oIpAddressRemote = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
		if(oIpAddressRemote instanceof String){
			String ipAddress = (String)oIpAddressRemote;
			if(ipAddress.length()<=255){
				transactionDTO.setSocketClientAddress(ipAddress);
			}
			else{
				transactionDTO.setSocketClientAddress(ipAddress.substring(0, 250)+"...");
			}
		}
		Object oIpAddressTransport = info.getContext().getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
		if(oIpAddressTransport instanceof String){
			String ipAddress = (String)oIpAddressTransport;
			if(ipAddress.length()<=255){
				transactionDTO.setTransportClientAddress(ipAddress);
			}
			else{
				transactionDTO.setTransportClientAddress(ipAddress.substring(0, 250)+"...");
			}
		}
		setAccordoInfoClientAddressCredenzialiMittente(transactionDTO,
				requestInfo,
				idDominio, info, idTransazione,
				times, timeStart, fase);
	}
	private void setAccordoInfoClientAddressCredenzialiMittente(Transazione transactionDTO,
			RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, long timeStart, FaseTracciamento fase) {
		String conflict = "0";
		if(transactionDTO.getSocketClientAddress()!=null || transactionDTO.getTransportClientAddress()!=null) {
			String cred = getCredenzialiMittenteClientAddress(info);
			if(cred!=null && StringUtils.isNotEmpty(cred)) {
				transactionDTO.setClientAddress(cred);
			}
			else {
				try {
					StringBuilder sbConflict = null;
					if(times!=null) {
						sbConflict = new StringBuilder();
					}
					CredenzialeMittente credClientAddress =GestoreAutenticazione.convertClientCredentialToCredenzialiMittenti(idDominio, info.getIdModulo(), idTransazione, 
							transactionDTO.getSocketClientAddress(), transactionDTO.getTransportClientAddress(), 
							null, fase.name()+".clientAddress",
							sbConflict, requestInfo); 
					if(sbConflict!=null && sbConflict.length()>0) {
						conflict = sbConflict.toString();
					}
					if(credClientAddress!=null) {
						cred = credClientAddress.getId()+"";
						transactionDTO.setClientAddress(cred);
						addCredenzialiMittenteClientAddress(info, cred);
					}
				}catch(Exception e) {
					// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
					this.logger.error("Errore durante la scrittura dell'indice per la ricerca del client address: "+e.getMessage(),e);
				}
			}
		}
		
		if(times!=null) {
			long timeEnd =  DateManager.getTimeMillis();
			long timeProcess = timeEnd-timeStart;
			times.fillTransactionDetails.add("ip-address:"+timeProcess+"/c"+conflict);
		}
		
	}
	
	private void setEventi(Transazione transactionDTO, Transaction transaction,
			OpenSPCoop2Properties op2Properties,
			RequestInfo requestInfo,
			IDSoggetto idDominio, InformazioniTransazione info, String idTransazione,
			TransazioniProcessTimes times, long timeStart, FaseTracciamento fase) throws CoreException {
		List<String> eventiGestione = new ArrayList<>();
		int count = 0;
		int maxLengthCredenziali = op2Properties.getTransazioniCredenzialiMittenteMaxLength()-AbstractCredenzialeList.PREFIX.length();
		
		if(op2Properties.isTransazioniHttpStatusAsEvent_outResponseCode() && transactionDTO.getCodiceRispostaUscita()!=null && 
				!"".equals(transactionDTO.getCodiceRispostaUscita())) {
			String evento = CostantiPdD.PREFIX_HTTP_STATUS_CODE_OUT+transactionDTO.getCodiceRispostaUscita();
			String dbValue = AbstractCredenzialeList.getDBValue(evento);
			if(count+dbValue.length()<maxLengthCredenziali) {
				eventiGestione.add( evento );
				count = count+dbValue.length();
			}
			else {
				// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
			}
		}
		if(op2Properties.isTransazioniHttpStatusAsEvent_inResponseCode() && transactionDTO.getCodiceRispostaIngresso()!=null && 
				!"".equals(transactionDTO.getCodiceRispostaIngresso())) {
			String evento = CostantiPdD.PREFIX_HTTP_STATUS_CODE_IN+transactionDTO.getCodiceRispostaIngresso();
			String dbValue = AbstractCredenzialeList.getDBValue(evento);
			if(count+dbValue.length()<maxLengthCredenziali) {
				eventiGestione.add( evento );
				count = count+dbValue.length();
			}
			else {
				// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
			}
		}
		if(op2Properties.isTransazioniTipoApiAsEvent() && transactionDTO.getTipoApi()>0) {
			String evento = CostantiPdD.PREFIX_API+transactionDTO.getTipoApi();
			String dbValue = AbstractCredenzialeList.getDBValue(evento);
			if(count+dbValue.length()<maxLengthCredenziali) {
				eventiGestione.add( evento );
				count = count+dbValue.length();
			}
			else {
				// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
			}
		}
		String consegneMultipleById = getConnettoriMultipli(info.getContext());
		if(op2Properties.isTransazioniConnettoriMultipliAsEvent() && consegneMultipleById!=null) {
			String evento = CostantiPdD.PREFIX_CONNETTORI_MULTIPLI+consegneMultipleById;
			String dbValue = AbstractCredenzialeList.getDBValue(evento);
			if(count+dbValue.length()<maxLengthCredenziali) {
				eventiGestione.add( evento );
				count = count+dbValue.length();
			}
			else {
				// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
			}
		}
		
		if(transaction.getEventiGestione()!=null && !transaction.getEventiGestione().isEmpty()){
			for (int i=0; i<transaction.getEventiGestione().size(); i++) {
				String dbValue = AbstractCredenzialeList.getDBValue(transaction.getEventiGestione().get(i));
				if(count+dbValue.length()<maxLengthCredenziali) {
					eventiGestione.add( transaction.getEventiGestione().get(i) );
					count = count+dbValue.length();
				}
				else {
					// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
				}
			}
		}
		Object oEventoMax = info.getContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_EVENTO);
		if(oEventoMax instanceof String){
			String max = (String)oEventoMax;
			String dbValue = AbstractCredenzialeList.getDBValue(max);
			if(count+dbValue.length()<maxLengthCredenziali) {
				eventiGestione.add( max );
				/**count = count+dbValue.length();*/
			}
			else {
				// tronco gli eventi ai primi trovati. Sono troppi eventi successi sulla transazione.
			}
		}
		String conflict = "0";
		if(!eventiGestione.isEmpty()){
			try {
				StringBuilder sbConflict = null;
				if(times!=null) {
					sbConflict = new StringBuilder();
				}
				CredenzialeMittente credEventi = GestoreAutenticazione.convertEventiToCredenzialiMittenti(idDominio, info.getIdModulo(), idTransazione, eventiGestione, 
						null, fase.name()+".eventi",
						sbConflict, requestInfo);
				if(sbConflict!=null && sbConflict.length()>0) {
					conflict = sbConflict.toString();
				}
				if(credEventi!=null) {
					transactionDTO.setEventiGestione(credEventi.getId()+"");
				}
			}catch(Exception e) {
				// NOTA: questo metodo dovrebbe non lanciare praticamente mai eccezione
				this.logger.error("Errore durante la definizione dell'indice per la ricerca degli eventi: "+e.getMessage(),e);
			}
		}
		if(times!=null) {
			long timeEnd =  DateManager.getTimeMillis();
			long timeProcess = timeEnd-timeStart;
			times.fillTransactionDetails.add("eventi:"+timeProcess+"/c"+conflict);
		}
	}
	
	
	private boolean isEsito(List<Integer> esiti, int esitoCheck) {
		for (int esito : esiti) {
			if(esitoCheck == esito){
				return true;
			}
		}
		return false;
	}
	
	private long consegnaMultiplaProfiloSincrono(InformazioniTransazione info, IDSoggetto idDominio, String idTransazione,
			TransazioniProcessTimes times, long timeStart,
			boolean schedulaNotificheDopoConsegnaSincrona, MsgDiagnostico msgDiag, String nomePorta) {
		IOpenSPCoopState openspcoopState = null;
		try {
			OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
			
			EJBUtilsMessaggioInConsegna messaggiInConsegna = null;
			Object oConnettoreConfig = info.getContext().getObject(CostantiPdD.TIMER_RICONSEGNA_CONTENUTI_APPLICATIVI_MESSAGGI_SPEDIRE );
			if (oConnettoreConfig instanceof EJBUtilsMessaggioInConsegna){
				messaggiInConsegna = (EJBUtilsMessaggioInConsegna) oConnettoreConfig;
			}
			if(messaggiInConsegna==null) {
				this.logger.error("Non è stato possibile gestire lo scheduling delle notifiche (connettori multipli): configurazione non disponibile");
			}
			else {
				openspcoopState = new OpenSPCoopStateful();
				openspcoopState.initResource(idDominio, info.getIdModulo(), idTransazione);
				
				if(times!=null) {
					long timeEnd =  DateManager.getTimeMillis();
					long timeProcess = timeEnd-timeStart;
					times.fillTransactionDetails.add("async-send-getConnection:"+timeProcess);
					
					timeStart = DateManager.getTimeMillis();
				}
				
				GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopState,true, messaggiInConsegna.getBusta().getID(),
						org.openspcoop2.protocol.engine.constants.Costanti.INBOX,msgDiag,(PdDContext) info.getContext());
				
				if(schedulaNotificheDopoConsegnaSincrona) {
					
					RepositoryBuste repositoryBuste = null;
					boolean spedizioneConsegnaContenuti = false; // sarà il timer a far partire effettivamente la spedizione
					/**
						RepositoryBuste repositoryBuste = new RepositoryBuste(openspcoopState.getStatoRichiesta(), true, context.getProtocolFactory());
					 */
						
					// Devo rilasciare l'attendi esito
					Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioniSql(op2Properties.isTransazioniDebug());
					msgRequest.releaseAttesaEsiti(op2Properties.isTransazioniDebug(), log);
					
					EJBUtils.sendMessages(this.logger, msgDiag, openspcoopState, idTransazione, 
							repositoryBuste, msgRequest, null, 
							info.getProtocolFactory(), idDominio, nomePorta, messaggiInConsegna,
							spedizioneConsegnaContenuti,
							(PdDContext) info.getContext(),
							ConfigurazionePdDManager.getInstance());
					
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
					
				}
				else {
					
					for (String servizioApplicativo : messaggiInConsegna.getServiziApplicativi()) {
						msgRequest.eliminaDestinatarioMessaggio(servizioApplicativo, null, messaggiInConsegna.getOraRegistrazioneMessaggio());
					}
				
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send-del:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
				}
			}
			
		}catch(Exception e) {
			this.logger.error("Non è stato possibile gestire lo scheduling delle notifiche (connettori multipli): "+e.getMessage(), e);
		}
		finally {
			try{
				if(openspcoopState!=null && !openspcoopState.resourceReleased()){
					openspcoopState.commit();
					
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send-commit:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
					
					openspcoopState.releaseResource();
					
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send-finish:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
				}
			}catch(Exception e){
				// ignore
			}
		}
		
		return timeStart;
	}
	
	private long saveTransactionContext(String identificativoSaveTransactionContext, InformazioniTransazione info, IDSoggetto idDominio, String idTransazione,
			TransazioniProcessTimes times, long timeStart,
			MsgDiagnostico msgDiag) {
		IOpenSPCoopState openspcoopState = null;
		try {
			openspcoopState = new OpenSPCoopStateful();
			openspcoopState.initResource(idDominio, info.getIdModulo(), idTransazione);
				
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("async-send-saveTransactionContext:"+timeProcess);
					
				timeStart = DateManager.getTimeMillis();
			}
				
			GestoreMessaggi msgRequest = new GestoreMessaggi(openspcoopState,true, identificativoSaveTransactionContext,
					org.openspcoop2.protocol.engine.constants.Costanti.INBOX,msgDiag,(PdDContext) info.getContext());
				
			msgRequest.registraTransactionContext_statelessEngine(identificativoSaveTransactionContext, info.getContext());
								
			if(times!=null) {
				long timeEnd =  DateManager.getTimeMillis();
				long timeProcess = timeEnd-timeStart;
				times.fillTransactionDetails.add("async-send-saveTransactionContext-saved:"+timeProcess);
				
				timeStart = DateManager.getTimeMillis();
			}
			
		}catch(Exception e) {
			this.logger.error("Non è stato possibile salvare il contesto della transazione per le notifiche (connettori multipli): "+e.getMessage(), e);
		}
		finally {
			try{
				if(openspcoopState!=null && !openspcoopState.resourceReleased()){
					openspcoopState.commit();
					
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send-saveTransactionContext-commit:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
					
					openspcoopState.releaseResource();
					
					if(times!=null) {
						long timeEnd =  DateManager.getTimeMillis();
						long timeProcess = timeEnd-timeStart;
						times.fillTransactionDetails.add("async-send-saveTransactionContext-finish:"+timeProcess);
						
						timeStart = DateManager.getTimeMillis();
					}
				}
			}catch(Exception e){
				// ignore
			}
		}
		
		return timeStart;
	}
	
	
	
	public static Busta convertToBusta(Transazione transazioneDTO) {
		Busta busta = new Busta(transazioneDTO.getProtocollo());
		
		busta.setTipoMittente(transazioneDTO.getTipoSoggettoFruitore());
		busta.setMittente(transazioneDTO.getNomeSoggettoFruitore());
		busta.setIdentificativoPortaMittente(transazioneDTO.getIdportaSoggettoFruitore());
		busta.setIndirizzoMittente(transazioneDTO.getIndirizzoSoggettoFruitore());
		
		busta.setTipoDestinatario(transazioneDTO.getTipoSoggettoErogatore());
		busta.setDestinatario(transazioneDTO.getNomeSoggettoErogatore());
		busta.setIdentificativoPortaDestinatario(transazioneDTO.getIdportaSoggettoErogatore());
		busta.setIndirizzoDestinatario(transazioneDTO.getIndirizzoSoggettoErogatore());
		
		busta.setTipoServizio(transazioneDTO.getTipoServizio());
		busta.setServizio(transazioneDTO.getNomeServizio());
		busta.setVersioneServizio(transazioneDTO.getVersioneServizio());
		busta.setAzione(transazioneDTO.getAzione());
		
		busta.setID(transazioneDTO.getIdMessaggioRichiesta());
		busta.setRiferimentoMessaggio(transazioneDTO.getIdMessaggioRisposta());
		
		busta.setCollaborazione(transazioneDTO.getIdCollaborazione());
		
		busta.setServizioApplicativoFruitore(transazioneDTO.getServizioApplicativoFruitore());
		busta.setServizioApplicativoErogatore(transazioneDTO.getServizioApplicativoErogatore());
		
		return busta;
	}
}
