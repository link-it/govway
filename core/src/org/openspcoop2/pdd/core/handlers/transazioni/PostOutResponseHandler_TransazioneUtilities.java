package org.openspcoop2.pdd.core.handlers.transazioni;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.controllo_traffico.CostantiControlloTraffico;
import org.openspcoop2.pdd.core.handlers.ExtendedTransactionInfo;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.builder.IBustaBuilder;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoSerializzazione;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.core.transazioni.TransazioneExtendedInfo;
import org.openspcoop2.core.transazioni.constants.PddRuolo;
import org.openspcoop2.core.transazioni.constants.RuoloTransazione;
import org.openspcoop2.pdd.core.transazioni.DateUtility;
import org.openspcoop2.pdd.core.transazioni.PropertiesSerializator;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.DumpUtility;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;

public class PostOutResponseHandler_TransazioneUtilities {

	private Logger logger;
	private boolean transazioniRegistrazioneTracceHeaderRawEnabled;
	private boolean transazioniRegistrazioneTracceDigestEnabled;
	private boolean transazioniRegistrazioneTracceProtocolPropertiesEnabled;
	private boolean transazioniRegistrazioneTokenInformazioniNormalizzate;
	
	public PostOutResponseHandler_TransazioneUtilities(Logger log, 
			boolean transazioniRegistrazioneTracceHeaderRawEnabled,
			boolean transazioniRegistrazioneTracceDigestEnabled,
			boolean transazioniRegistrazioneTracceProtocolPropertiesEnabled,
			boolean transazioniRegistrazioneTokenInformazioniNormalizzate){
		this.logger = log;
		this.transazioniRegistrazioneTracceHeaderRawEnabled = transazioniRegistrazioneTracceHeaderRawEnabled;
		this.transazioniRegistrazioneTracceDigestEnabled = transazioniRegistrazioneTracceDigestEnabled;
		this.transazioniRegistrazioneTracceProtocolPropertiesEnabled = transazioniRegistrazioneTracceProtocolPropertiesEnabled;
		this.transazioniRegistrazioneTokenInformazioniNormalizzate = transazioniRegistrazioneTokenInformazioniNormalizzate;
	}
	
	public Transazione fillTransaction(PostOutResponseContext context,
			Transaction transaction,
			IDSoggetto idDominio) throws HandlerException{

		OpenSPCoop2Properties op2Properties = OpenSPCoop2Properties.getInstance();
		
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

		try {


			IBustaBuilder<?> protocolBustaBuilder = context.getProtocolFactory().createBustaBuilder(context.getStato());


			Transazione transactionDTO = new Transazione();


			// ** Identificativo di transazione **
			String idTransazione = null;
			if (context.getPddContext().getObject(Costanti.ID_TRANSAZIONE)!=null){
				idTransazione = ((String)context.getPddContext().getObject(Costanti.ID_TRANSAZIONE));
				transactionDTO.setIdTransazione(idTransazione);
			}else
				throw new HandlerException("ID Transazione Assente");


			// ** stato **
			// Stato di una transazione (marca la transazione con uno stato tramite la configurazione plugin)
			transactionDTO.setStato(transaction.getStato());


			// ** Ruolo transazione **
			// invocazioneOneway (1)
			// invocazioneSincrona (2)
			// invocazioneAsincronaSimmetrica (3)
			// rispostaAsincronaSimmetrica (4)
			// invocazioneAsincronaAsimmetrica (5)
			// richiestaStatoAsincronaAsimmetrica (6)
			// integrationManager (7)
			//System.out.println("SCENARIO DI COOPERAZIONE ["+transaction.getScenarioCooperazione()+"]");
			RuoloTransazione ruolo = RuoloTransazione.getEnumConstantFromOpenSPCoopValue(transaction.getScenarioCooperazione());
			if(ruolo!=null)
				transactionDTO.setRuoloTransazione(ruolo.getValoreAsInt());
			else
				transactionDTO.setRuoloTransazione(-1);


			// ** Esito Transazione **
			if (context.getEsito()!=null){
				if(context.getEsito().getCode()!=null){
					transactionDTO.setEsito(context.getEsito().getCode());
				}
				transactionDTO.setEsitoContesto(context.getEsito().getContextType());
			}


			// ** protocollo **
			transactionDTO.setProtocollo(context.getProtocolFactory().getProtocol());

			
			// ** header HTTP **
			if(transaction.getRequestInfo()!=null && 
					transaction.getRequestInfo().getProtocolContext()!=null &&
					transaction.getRequestInfo().getProtocolContext().getRequestType()!=null) {
				transactionDTO.setTipoRichiesta(transaction.getRequestInfo().getProtocolContext().getRequestType());
			}
			transactionDTO.setCodiceRispostaIngresso(transaction.getCodiceTrasportoRichiesta());
			if(context.getReturnCode()>0)
				transactionDTO.setCodiceRispostaUscita(context.getReturnCode()+"");

			// ** Tempi di latenza **

			// Se data_accettazione_richiesta è null viene impostata a CURRENT_TIMESTAMP
			if (transaction.getDataAccettazioneRichiesta()!=null){
				transactionDTO.setDataAccettazioneRichiesta(transaction.getDataAccettazioneRichiesta());
			}else{
				Object o = context.getPddContext().getObject(CostantiPdD.DATA_ACCETTAZIONE_RICHIESTA);
				if(o!=null && o instanceof Date){
					transactionDTO.setDataAccettazioneRichiesta((Date) o);
				}
				else if(context.getDataElaborazioneMessaggio()!=null){
					transactionDTO.setDataAccettazioneRichiesta(context.getDataElaborazioneMessaggio());
				}
				else{
					transactionDTO.setDataAccettazioneRichiesta(DateManager.getDate());
				}
			}
			
			// Se data_ingresso_richiesta è null viene impostata a CURRENT_TIMESTAMP
			if (transaction.getDataIngressoRichiesta()!=null){
				transactionDTO.setDataIngressoRichiesta(transaction.getDataIngressoRichiesta());
			}else{
				Object o = context.getPddContext().getObject(CostantiPdD.DATA_INGRESSO_RICHIESTA);
				if(o!=null && o instanceof Date){
					transactionDTO.setDataIngressoRichiesta((Date) o);
				}
				else if(context.getDataElaborazioneMessaggio()!=null){
					transactionDTO.setDataIngressoRichiesta(context.getDataElaborazioneMessaggio());
				}
				else{
					transactionDTO.setDataIngressoRichiesta(DateManager.getDate());
				}
			}
			
			// data_uscita_richiesta si imposta se e' diversa da null
			if (transaction.getDataUscitaRichiesta()!=null)
				transactionDTO.setDataUscitaRichiesta(transaction.getDataUscitaRichiesta());

			// data_accettazione_risposta
			// La porta di dominio mi passa sempre questa informazione.
			// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
			//if (transaction.getDimensioneIngressoRispostaBytes()!=null && transaction.getDimensioneIngressoRispostaBytes()>0){
			// L'INFORMAZIONE DEVE INVECE ESSERE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
			if (transaction.getDataAccettazioneRisposta()!=null){
				transactionDTO.setDataAccettazioneRisposta(transaction.getDataAccettazioneRisposta());
			}
			
			// data_ingresso_risposta
			// La porta di dominio mi passa sempre questa informazione.
			// Nel PddMonitor, invece, la data deve essere visualizzata solo se la dimensione e' diverso da 0 e cioe' se c'e' un messaggio di risposta.
			//if (transaction.getDimensioneIngressoRispostaBytes()!=null && transaction.getDimensioneIngressoRispostaBytes()>0){
			// L'INFORMAZIONE DEVE INVECE ESSERE SALVATA PER LA SIMULAZIONE DEI MESSAGGI DIAGNOSTICI
			if (transaction.getDataIngressoRisposta()!=null){
				transactionDTO.setDataIngressoRisposta(transaction.getDataIngressoRisposta());
			}

			// data_uscita_risposta
			if(context.getDataPrimaSpedizioneRisposta()!=null) {
				transactionDTO.setDataUscitaRisposta(context.getDataPrimaSpedizioneRisposta());
			}
			else if (transaction.getDataUscitaRisposta()!=null){
				transactionDTO.setDataUscitaRisposta(transaction.getDataUscitaRisposta());
			}
			else{
				// creo sempre una data di risposta.
				if(context.getDataElaborazioneMessaggio()!=null){
					transactionDTO.setDataUscitaRisposta(context.getDataElaborazioneMessaggio());
				}
				else{
					transactionDTO.setDataUscitaRisposta(DateManager.getDate());
				}
			}


			// ** Dimensione messaggi gestiti **
			// Se le dimensioni (BIGINT) sono null occorre impostarle a zero

			// dimensione_ingresso_richiesta
			if (context.getInputRequestMessageSize()!=null && context.getInputRequestMessageSize()>0){
				transactionDTO.setRichiestaIngressoBytes(context.getInputRequestMessageSize().longValue());
			}

			// dimensione_uscita_richiesta
			if (context.getOutputRequestMessageSize()!=null && context.getOutputRequestMessageSize()>0){
				transactionDTO.setRichiestaUscitaBytes(context.getOutputRequestMessageSize().longValue());
			}

			// dimensione_ingresso_risposta
			if (context.getInputResponseMessageSize()!=null && context.getInputResponseMessageSize()>0){
				transactionDTO.setRispostaIngressoBytes(context.getInputResponseMessageSize().longValue());
			}

			// dimensione_uscita_risposta
			if (context.getOutputResponseMessageSize()!=null && context.getOutputResponseMessageSize()>0){
				transactionDTO.setRispostaUscitaBytes(context.getOutputResponseMessageSize().longValue());
			}


			// ** Dati Pdd **
			transactionDTO.setPddCodice(idDominio.getCodicePorta());
			transactionDTO.setPddTipoSoggetto(idDominio.getTipo());
			transactionDTO.setPddNomeSoggetto(idDominio.getNome());
			if(context.getTipoPorta()!=null){
				transactionDTO.setPddRuolo(PddRuolo.toEnumConstant(context.getTipoPorta().getTipo()));
			}


			// ** FAULT **
			transactionDTO.setFaultIntegrazione(transaction.getFaultIntegrazione());
			transactionDTO.setFormatoFaultIntegrazione(transaction.getFormatoFaultIntegrazione());
			transactionDTO.setFaultCooperazione(transaction.getFaultCooperazione());
			transactionDTO.setFormatoFaultCooperazione(transaction.getFormatoFaultCooperazione());
//			boolean readFault = false;
//			if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
//				if(transactionDTO.getFaultIntegrazione()==null){
//					readFault = true;
//				}
//			}
//			else{
//				if(transactionDTO.getFaultCooperazione()==null){
//					readFault = true;
//				}
//			}
			// Il fault potrebbe essere cambiato durante la fase di gestione dalla consegna della risposta
			boolean readFault = true;
			if(readFault){
				String fault = null;
				String formatoFault = null;
				try{
					if(context.getMessaggio()!=null){
						if(ServiceBinding.SOAP.equals(context.getMessaggio().getServiceBinding())) {
							OpenSPCoop2SoapMessage soapMsg = context.getMessaggio().castAsSoap();
							if(soapMsg.getSOAPBody()!=null && soapMsg.getSOAPBody().hasFault()){
								
								ByteArrayOutputStream bout = new ByteArrayOutputStream();
								bout.write(context.getMessaggio().getAsByte(soapMsg.getSOAPPart().getEnvelope(), false));
								bout.flush();
								bout.close();
								
								Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopTransazioni(op2Properties.isTransazioniDebug());
								if(op2Properties.isTransazioniFaultPrettyPrint()){
									// Faccio una pretty-print: potevo fare anche direttamente passando il fault a metodo prettyPrint,
									// Pero' non veniva stampato correttamente il SOAPFault. Mi appoggio allora a SoapUtils.
									//byte [] content = org.openspcoop2.message.soap.TunnelSoapUtils.sbustamentoMessaggio(context.getMessaggio());
									byte [] content = bout.toByteArray();
									fault = DumpUtility.toString(XMLUtils.getInstance().newDocument(content), log, context.getMessaggio());
									//System.out.println("IMPOSTATO FAULT IN TRANSACTION ["+fault+"]");
								}
								else{
									
									fault = bout.toString();
								}
								
								formatoFault = soapMsg.getMessageType().name();
							}

						}
					}
				}catch(Throwable e){
					this.logger.error("Errore durante il dump del soap fault",e);
				}
				if(TipoPdD.DELEGATA.equals(context.getTipoPorta())){
					transactionDTO.setFaultIntegrazione(fault);
					transactionDTO.setFormatoFaultIntegrazione(formatoFault);
				}
				else{
					transactionDTO.setFaultCooperazione(fault);
					transactionDTO.setFormatoFaultCooperazione(formatoFault);
				}
			}
			


			// ** Soggetto Fruitore **
			if (context.getProtocollo()!=null && context.getProtocollo().getFruitore()!=null) {
				transactionDTO.setTipoSoggettoFruitore(context.getProtocollo().getFruitore().getTipo());
				transactionDTO.setNomeSoggettoFruitore(context.getProtocollo().getFruitore().getNome());
				transactionDTO.setIdportaSoggettoFruitore(context.getProtocollo().getFruitore().getCodicePorta());
				transactionDTO.setIndirizzoSoggettoFruitore(context.getProtocollo().getIndirizzoFruitore());
			}
			else if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getFruitore()!=null) {
				transactionDTO.setTipoSoggettoFruitore(transaction.getRequestInfo().getFruitore().getTipo());
				transactionDTO.setNomeSoggettoFruitore(transaction.getRequestInfo().getFruitore().getNome());
				transactionDTO.setIdportaSoggettoFruitore(transaction.getRequestInfo().getFruitore().getCodicePorta());
			}


			// ** Soggetto Erogatore **
			if (context.getProtocollo()!=null && context.getProtocollo().getErogatore()!=null) {
				transactionDTO.setTipoSoggettoErogatore(context.getProtocollo().getErogatore().getTipo());
				transactionDTO.setNomeSoggettoErogatore(context.getProtocollo().getErogatore().getNome());
				transactionDTO.setIdportaSoggettoErogatore(context.getProtocollo().getErogatore().getCodicePorta());
				transactionDTO.setIndirizzoSoggettoErogatore(context.getProtocollo().getIndirizzoErogatore());
			}
			else if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null &&
					transaction.getRequestInfo().getIdServizio().getSoggettoErogatore()!=null) {
				transactionDTO.setTipoSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getTipo());
				transactionDTO.setNomeSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getNome());
				transactionDTO.setIdportaSoggettoErogatore(transaction.getRequestInfo().getIdServizio().getSoggettoErogatore().getCodicePorta());
			}


			// ** Identificativi Messaggi **
			if (context.getProtocollo()!=null) {

				// Richiesta
				String idMessaggioRichiesta = context.getProtocollo().getIdRichiesta();
				Timestamp dateInternaIdProtocolloRichiesta = null;
				if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					if(idMessaggioRichiesta!=null){
						dateInternaIdProtocolloRichiesta = DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRichiesta);
					}
				}

				// Risposta
				String idMessaggioRisposta = null;
				Timestamp dateInternaIdProtocolloRisposta = null;
				// Nel contesto l'id di riposta puo' essere null o un valore generato per la confermaPresa in carico.
				// Se e' presenta una busta di risposta nella transazione deve pero' finire l'id di protocollo della busta.
				if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getID()!=null){
					idMessaggioRisposta = tracciaRisposta.getBusta().getID();
					if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
						dateInternaIdProtocolloRisposta = DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRisposta);
					}
				}
				else if(context.getProtocollo().getIdRisposta()!=null){
					idMessaggioRisposta = context.getProtocollo().getIdRisposta();
					if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
						dateInternaIdProtocolloRisposta =  DateUtility.getTimestampIntoIdProtocollo(this.logger,protocolBustaBuilder,idMessaggioRisposta);
					}
				}

				// Set
				transactionDTO.setIdMessaggioRichiesta(idMessaggioRichiesta);
				transactionDTO.setIdMessaggioRisposta(idMessaggioRisposta);
				if(op2Properties.isTransazioniFiltroDuplicatiSaveDateEnabled()){
					transactionDTO.setDataIdMsgRichiesta(dateInternaIdProtocolloRichiesta);
					transactionDTO.setDataIdMsgRisposta(dateInternaIdProtocolloRisposta);
				}

			}


			// ** altre informazioni **
			if (context.getProtocollo()!=null && context.getProtocollo().getProfiloCollaborazione()!=null) {
				transactionDTO.setProfiloCollaborazioneOp2(context.getProtocollo().getProfiloCollaborazione().getEngineValue());		
			}
			else if(profiloCollaborazioneBustaTracciaRichiesta!=null){
				transactionDTO.setProfiloCollaborazioneOp2(profiloCollaborazioneBustaTracciaRichiesta.getEngineValue());
			}
			if (context.getProtocollo()!=null && context.getProtocollo().getProfiloCollaborazioneValue()!=null) {
				transactionDTO.setProfiloCollaborazioneProt(context.getProtocollo().getProfiloCollaborazioneValue());
			}
			else if(profiloCollaborazioneValueBustaTracciaRichiesta!=null){
				transactionDTO.setProfiloCollaborazioneProt(profiloCollaborazioneValueBustaTracciaRichiesta);
			}
			if (context.getProtocollo()!=null){

				transactionDTO.setIdCollaborazione(context.getProtocollo().getCollaborazione());

				IDAccordo idAccordo = context.getProtocollo().getIdAccordo();
				if(idAccordo!=null){
					transactionDTO.setUriAccordoServizio(IDAccordoFactory.getInstance().getUriFromIDAccordo(idAccordo));
				}

				transactionDTO.setTipoServizio(context.getProtocollo().getTipoServizio());
				transactionDTO.setNomeServizio(context.getProtocollo().getServizio());
				if(context.getProtocollo().getVersioneServizio()!=null && context.getProtocollo().getVersioneServizio().intValue()>0) {
					transactionDTO.setVersioneServizio(context.getProtocollo().getVersioneServizio());
				}
				transactionDTO.setAzione(context.getProtocollo().getAzione());
			}
			if(transactionDTO.getTipoServizio()==null) {
				if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) {
					transactionDTO.setTipoServizio(transaction.getRequestInfo().getIdServizio().getTipo());
				}
			}
			if(transactionDTO.getNomeServizio()==null) {
				if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) {
					transactionDTO.setNomeServizio(transaction.getRequestInfo().getIdServizio().getNome());
				}
			}
			if(context.getProtocollo()==null || context.getProtocollo().getVersioneServizio()==null || context.getProtocollo().getVersioneServizio().intValue()<=0) {
				if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) {
					transactionDTO.setVersioneServizio(transaction.getRequestInfo().getIdServizio().getVersione());
				}
			}
			if(transactionDTO.getAzione()==null) {
				if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getIdServizio()!=null) {
					transactionDTO.setAzione(transaction.getRequestInfo().getIdServizio().getAzione());
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
						try{
							transactionDTO.setHeaderProtocolloRichiesta(tracciaRichiesta.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT));
						}catch(Exception e){
							this.logger.error("Errore durante la conversione dell'oggetto Busta di richiesta ["+tracciaRichiesta.getBustaAsRawContent().getClass().getName()+"] in stringa");
						}
					}
				}
				if(tracciaRichiesta.getBusta()!=null){
					if(this.transazioniRegistrazioneTracceDigestEnabled){
						transactionDTO.setDigestRichiesta(tracciaRichiesta.getBusta().getDigest());
					}
					if(this.transazioniRegistrazioneTracceProtocolPropertiesEnabled){
						if(tracciaRichiesta.getBusta().sizeProperties()>0){
							Hashtable<String, String> propertiesBusta = new Hashtable<String, String>();
							String [] pNames = tracciaRichiesta.getBusta().getPropertiesNames();
							if(pNames!=null){
								for (int i = 0; i < pNames.length; i++) {
									String key = pNames[i];
									String value = tracciaRichiesta.getBusta().getProperty(key);
									if(key!=null && value!=null){
										propertiesBusta.put(key, value);
									}
								}
							}
							PropertiesSerializator ps = new PropertiesSerializator(propertiesBusta);
							try{
								transactionDTO.setProtocolloExtInfoRichiesta(ps.convertToDBColumnValue());
							}catch(Exception e){
								this.logger.error("Errore durante la conversione delle proprieta della Busta di richiesta: "+e.getMessage(),e);
							}
						}
					}
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
						try{
							transactionDTO.setHeaderProtocolloRisposta(tracciaRisposta.getBustaAsRawContent().toString(TipoSerializzazione.DEFAULT));
						}catch(Exception e){
							this.logger.error("Errore durante la conversione dell'oggetto Busta di risposta ["+tracciaRisposta.getBustaAsRawContent().getClass().getName()+"] in stringa");
						}
					}
				}
				if(tracciaRisposta.getBusta()!=null){
					if(this.transazioniRegistrazioneTracceDigestEnabled){
						transactionDTO.setDigestRisposta(tracciaRisposta.getBusta().getDigest());
					}
					if(this.transazioniRegistrazioneTracceProtocolPropertiesEnabled){
						if(tracciaRisposta.getBusta().sizeProperties()>0){
							Hashtable<String, String> propertiesBusta = new Hashtable<String, String>();
							String [] pNames = tracciaRisposta.getBusta().getPropertiesNames();
							if(pNames!=null){
								for (int i = 0; i < pNames.length; i++) {
									String key = pNames[i];
									String value = tracciaRisposta.getBusta().getProperty(key);
									if(key!=null && value!=null){
										propertiesBusta.put(key, value);
									}
								}
							}
							PropertiesSerializator ps = new PropertiesSerializator(propertiesBusta);
							try{
								transactionDTO.setProtocolloExtInfoRisposta(ps.convertToDBColumnValue());
							}catch(Exception e){
								this.logger.error("Errore durante la conversione delle proprieta della Busta di risposta: "+e.getMessage(),e);
							}
						}
					}
				}
			}
			
								
			// ** informazioni di integrazione **
			if(context.getIntegrazione()!=null){
				transactionDTO.setIdCorrelazioneApplicativa(context.getIntegrazione().getIdCorrelazioneApplicativa());
				transactionDTO.setIdCorrelazioneApplicativaRisposta(transaction.getCorrelazioneApplicativaRisposta());
				transactionDTO.setServizioApplicativoFruitore(context.getIntegrazione().getServizioApplicativoFruitore());
				// in caso di porta applicativa si salva l'elenco dei servizi applicativi erogatori
				if(context.getIntegrazione().sizeServiziApplicativiErogatori()>0){
					StringBuffer sa_erogatori = new StringBuffer();
					for (int i=0; i<context.getIntegrazione().sizeServiziApplicativiErogatori(); i++) {
						if (i>0){
							sa_erogatori.append(",");
						}
						sa_erogatori.append( context.getIntegrazione().getServizioApplicativoErogatore(i) );
					}
					transactionDTO.setServizioApplicativoErogatore(sa_erogatori.toString());
				}
				else if(transaction.getServiziApplicativiErogatore()!=null && transaction.getServiziApplicativiErogatore().size()>0){
					StringBuffer sa_erogatori = new StringBuffer();
					for (int i=0; i<transaction.getServiziApplicativiErogatore().size(); i++) {
						if (i>0){
							sa_erogatori.append(",");
						}
						sa_erogatori.append( transaction.getServiziApplicativiErogatore().get(i) );
					}
					transactionDTO.setServizioApplicativoErogatore(sa_erogatori.toString());
				}
			}
			
			if(context.getPddContext()!=null){
				Object operazioneIM = context.getPddContext().getObject(CostantiPdD.KEY_TIPO_OPERAZIONE_IM);
				if(operazioneIM!=null && operazioneIM instanceof String){
					String op = (String) operazioneIM;
					transactionDTO.setOperazioneIm(op);
				}
			}
			
			if(transactionDTO.getServizioApplicativoFruitore()==null){
				// provo a vedere se fosse nella traccia di richiesta
				if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getServizioApplicativoFruitore()!=null){
					transactionDTO.setServizioApplicativoFruitore(tracciaRichiesta.getBusta().getServizioApplicativoFruitore());
				}
			}
			if(transactionDTO.getServizioApplicativoFruitore()==null){
				// provo a vedere se fosse nella traccia di risposta
				if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getServizioApplicativoFruitore()!=null){
					transactionDTO.setServizioApplicativoFruitore(tracciaRisposta.getBusta().getServizioApplicativoFruitore());
				}
			}
			
			if(transactionDTO.getServizioApplicativoErogatore()==null){
				// provo a vedere se fosse nella traccia di richiesta
				if(tracciaRichiesta!=null && tracciaRichiesta.getBusta()!=null && tracciaRichiesta.getBusta().getServizioApplicativoErogatore()!=null){
					transactionDTO.setServizioApplicativoErogatore(tracciaRichiesta.getBusta().getServizioApplicativoErogatore());
				}
			}
			if(transactionDTO.getServizioApplicativoErogatore()==null){
				// provo a vedere se fosse nella traccia di risposta
				if(tracciaRisposta!=null && tracciaRisposta.getBusta()!=null && tracciaRisposta.getBusta().getServizioApplicativoErogatore()!=null){
					transactionDTO.setServizioApplicativoErogatore(tracciaRisposta.getBusta().getServizioApplicativoErogatore());
				}
			}
			
			transactionDTO.setLocationRichiesta(tracciaRichiesta!=null ? tracciaRichiesta.getLocation() : null);
			transactionDTO.setLocationRisposta(tracciaRisposta!=null ? tracciaRisposta.getLocation() : null);
			
			if(transaction!=null && transaction.getRequestInfo()!=null && transaction.getRequestInfo().getProtocolContext()!=null){
				transactionDTO.setNomePorta(transaction.getRequestInfo().getProtocolContext().getInterfaceName());
			}
			
			if(transaction.getCredenziali()!=null){
				transactionDTO.setCredenziali(transaction.getCredenziali());
			}else{
				Object o = context.getPddContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_CREDENZIALI);
				if(o!=null && (o instanceof String)){
					transactionDTO.setCredenziali((String)o);
				}
				else{
					o = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE);
					if(o!=null && (o instanceof String)){
						transactionDTO.setCredenziali((String)o);
					}
				}
			}
			
			transactionDTO.setLocationConnettore(transaction.getLocation());
			
			if(transaction.getUrlInvocazione()!=null){
				transactionDTO.setUrlInvocazione(transaction.getUrlInvocazione());
			}else{
				Object o = context.getPddContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_URL_INVOCAZIONE);
				if(o!=null && (o instanceof String)){
					transactionDTO.setUrlInvocazione((String)o);
				}
				else{
					o = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE);
					if(o!=null && (o instanceof String)){
						transactionDTO.setUrlInvocazione((String)o);
					}
				}
			}
			
			// ** cluster-id **
			transactionDTO.setClusterId(op2Properties.getClusterId(false));
			
			// credenziali
			if(transaction.getCredenzialiMittente()!=null) {
				
				// trasporto
				if(transaction.getCredenzialiMittente().getTrasporto()!=null) {
					transactionDTO.setTrasportoMittente(transaction.getCredenzialiMittente().getTrasporto().getId()+"");
				}
				
				// token
				if(transaction.getCredenzialiMittente().getToken_issuer()!=null) {
					transactionDTO.setTokenIssuer(transaction.getCredenzialiMittente().getToken_issuer().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getToken_clientId()!=null) {
					transactionDTO.setTokenClientId(transaction.getCredenzialiMittente().getToken_clientId().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getToken_subject()!=null) {
					transactionDTO.setTokenSubject(transaction.getCredenzialiMittente().getToken_subject().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getToken_username()!=null) {
					transactionDTO.setTokenUsername(transaction.getCredenzialiMittente().getToken_username().getId()+"");
				}
				if(transaction.getCredenzialiMittente().getToken_eMail()!=null) {
					transactionDTO.setTokenMail(transaction.getCredenzialiMittente().getToken_eMail().getId()+"");
				}
			}
			
			// token info
			if(this.transazioniRegistrazioneTokenInformazioniNormalizzate && transaction.getInformazioniToken()!=null) {
				transactionDTO.setTokenInfo(transaction.getInformazioniToken().toJson());
			}
			
			// ** Indirizzo IP **
			Object oIpAddressRemote = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_REMOTE_ADDRESS);
			if(oIpAddressRemote!=null && (oIpAddressRemote instanceof String)){
				String ipAddress = (String)oIpAddressRemote;
				if(ipAddress!=null && ipAddress.length()<=255){
					transactionDTO.setSocketClientAddress(ipAddress);
				}
				else{
					transactionDTO.setSocketClientAddress(ipAddress.substring(0, 250)+"...");
				}
			}
			Object oIpAddressTransport = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLIENT_IP_TRANSPORT_ADDRESS);
			if(oIpAddressTransport!=null && (oIpAddressTransport instanceof String)){
				String ipAddress = (String)oIpAddressTransport;
				if(ipAddress!=null && ipAddress.length()<=255){
					transactionDTO.setTransportClientAddress(ipAddress);
				}
				else{
					transactionDTO.setTransportClientAddress(ipAddress.substring(0, 250)+"...");
				}
			}
			
			
			// ** eventi di gestione **
			StringBuffer eventi_gestione = new StringBuffer();
			if(transaction.getEventiGestione()!=null && transaction.getEventiGestione().size()>0){
				for (int i=0; i<transaction.getEventiGestione().size(); i++) {
					if (i>0){
						eventi_gestione.append(", ");
					}
					eventi_gestione.append( transaction.getEventiGestione().get(i) );
				}
			}
			Object oEventoMax = context.getPddContext().getObject(CostantiControlloTraffico.PDD_CONTEXT_MAX_REQUEST_VIOLATED_EVENTO);
			if(oEventoMax!=null && (oEventoMax instanceof String)){
				if(eventi_gestione.length()>0)
					eventi_gestione.append(",");
				eventi_gestione.append((String)oEventoMax);
			}
			if(eventi_gestione.length()>0){
				if(eventi_gestione.length()<=1000){
					transactionDTO.setEventiGestione(eventi_gestione.toString());
				}
				else{
					// tronco gli eventi a 1000 caratteri. Sono troppi eventi successi sulla transazione. Eventualmente accorciare le stringhe degli eventi.
					transactionDTO.setEventiGestione(eventi_gestione.toString().substring(0, 996)+"...");	
				}
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
			if (context.getPddContext().getObject(Costanti.EXTENDED_INFO_TRANSAZIONE)!=null){
				transactionExtendedInfo = ((ExtendedTransactionInfo)context.getPddContext().getObject(Costanti.EXTENDED_INFO_TRANSAZIONE));
				List<String> keys = transactionExtendedInfo.keys();
				if(keys!=null && keys.size()>0){
					for (String key : keys) {
						String value = transactionExtendedInfo.getValue(key);
						TransazioneExtendedInfo transazioneExtendedInfo = new TransazioneExtendedInfo();
						transazioneExtendedInfo.setNome(key);
						transazioneExtendedInfo.setValore(value);
						transactionDTO.addTransazioneExtendedInfo(transazioneExtendedInfo);
					}
				}
			}
			
			
			
			return transactionDTO;


		} catch (Exception e) {
			throw new HandlerException("Errore durante il popolamento della transazione da salvare su database: " + e.getLocalizedMessage(), e);
		} 
	}
	
}
