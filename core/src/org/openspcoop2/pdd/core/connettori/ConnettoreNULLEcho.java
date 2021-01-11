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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.core.Utilities;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.builder.Sbustamento;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.BustaRawContent;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.Trasmissione;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.FaseImbustamento;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.protocol.sdk.validator.IValidatoreErrori;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazioneErrori;
import org.openspcoop2.utils.date.DateManager;




/**
 * Classe utilizzata per effettuare consegne di messaggi Soap, attraverso
 * l'invocazione di un server http. 
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ConnettoreNULLEcho extends ConnettoreBase {
	
	public final static String LOCATION = "govway://echo";
    

	
	


	/* ********  METODI  ******** */

	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, false, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	protected boolean send(ConnettoreMsg request) {

		boolean generaTrasmissione = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE).trim()))
				generaTrasmissione = true;
		}
		boolean generaTrasmissioneInvertita = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_INVERTITA).trim()))
				generaTrasmissioneInvertita = true;
		}
		boolean generaTrasmissioneAndataRitorno = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_NULL_ECHO_GENERA_TRASMISSIONE_ANDATA_RITORNO).trim()))
				generaTrasmissioneAndataRitorno = true;
		}
		
		
		this.codice = 200;
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		
		StatefulMessage state = new StatefulMessage(null, this.logger.getLogger());
		
		ValidazioneSintattica validatoreSintattico = null;
		Validatore validatoreProtocollo = null;
		@SuppressWarnings("unused")
		BustaRawContent<?> headerProtocolloRisposta = null;
		String protocol = null;
		try{
			IProtocolFactory<?> protocolFactory = this.getProtocolFactory();
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			protocol = protocolFactory.getProtocol();
			
			//this.response = request.getRequestMessage();
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			this.requestMsg.writeTo(bout, true);
			bout.flush();
			bout.close();
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			OpenSPCoop2MessageFactory messageFactory = Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.RESPONSE);
			OpenSPCoop2MessageParseResult pr = messageFactory.createMessage(this.requestMsg.getMessageType(),MessageRole.RESPONSE,
					this.requestMsg.getContentType(),
					bin,notifierInputStreamParams,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			// Non funziona con gli attachments: this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(request.getRequestMessage().getVersioneSoap(),(bout.toByteArray()),notifierInputStreamParams);
			if(pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			this.responseMsg = pr.getMessage_throwParseException();
			
			validatoreSintattico = new ValidazioneSintattica(this.getPddContext(),state,this.responseMsg, this.openspcoopProperties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory); 

			if(validatoreSintattico.verifyProtocolPresence(TipoPdD.APPLICATIVA,null,RuoloMessaggio.RISPOSTA) && 
					!"sdi".equals(protocolFactory.getProtocol())){ // evitare sdi per far funzionare il protocollo sdi con la sonda.
				
				// getBusta
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(false);
				property.setValidazioneProfiloCollaborazione(false);
				property.setValidazioneManifestAttachments(false);
				
				validatoreProtocollo = new Validatore(this.responseMsg,this.getPddContext(),property,null,
						this.openspcoopProperties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory);
				
				if(validatoreProtocollo.validazioneSintattica() == false){
					throw new Exception("Busta non presente: "+validatoreProtocollo.getErrore().getDescrizione(protocolFactory));
				}
				
				// Leggo busta di richiesta
				Busta busta = validatoreProtocollo.getBusta();
				
				// informazione spcoopErrore
				IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori(state);
				ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
				pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
				boolean isBustaSPCoopErrore = validatoreErrori.isBustaErrore(busta,this.responseMsg,pValidazioneErrori);
				
				// Gestisco il manifest se il messaggio lo possiede
				boolean gestioneManifest = false;
				// La gestione manifest non la devo fare a questo livello.
				// Se mi arriva un messaggio senza manifest, vuol dire che era disabilitata e quindi anche nella risposta non ci deve essere
				// In egual misura se arriva un messaggio con manifest, significa che ci deve essere anche nella risposta
				// Poiche' la risposta e' esattamente uguale (nel body e negli allegati) alla richiesta, 
				// venendo costruita dai bytes della richiesta 
//				if(this.responseMsg.countAttachments()>0){
//					javax.xml.soap.SOAPBody soapBody = this.responseMsg.getSOAPBody();
//					if(soapBody!=null){
//						Node childNode = org.openspcoop2.message.SoapUtils.getFirstNotEmptyChildNode(soapBody);
//						System.out.println("LocalName["+childNode.getLocalName()+"] ["+childNode.getNamespaceURI()+"]");
//						
//					}
//				}
				
				// rimozione vecchia busta
				Sbustamento sbustatore = new Sbustamento(protocolFactory,state);
				ProtocolMessage protocolMessage = sbustatore.sbustamento(this.responseMsg,this.getPddContext(),
						busta,RuoloMessaggio.RICHIESTA, gestioneManifest, 
						this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
						FaseSbustamento.PRE_CONSEGNA_RICHIESTA,this.requestInfo);
				if(protocolMessage!=null) {
					headerProtocolloRisposta = protocolMessage.getBustaRawContent();
					this.responseMsg = protocolMessage.getMessage(); // updated
				}
				
				// Creo busta di risposta solo se la busta di richiesta non conteneva una busta Errore
				if(!isBustaSPCoopErrore){
			
					TipoOraRegistrazione tipoOraRegistrazione = this.openspcoopProperties.getTipoTempoBusta(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
					Busta bustaRisposta = busta.invertiBusta(tipoOraRegistrazione,protocolFactory.createTraduttore().toString(tipoOraRegistrazione));
				
					bustaRisposta.setProfiloDiCollaborazione(busta.getProfiloDiCollaborazione());
					bustaRisposta.setServizio(busta.getServizio());
					bustaRisposta.setVersioneServizio(busta.getVersioneServizio());
					bustaRisposta.setTipoServizio(busta.getTipoServizio());
					bustaRisposta.setAzione(busta.getAzione());
					
					bustaRisposta.setInoltro(busta.getInoltro(),busta.getInoltroValue());
					bustaRisposta.setConfermaRicezione(busta.isConfermaRicezione());
					
					if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(busta.getProfiloDiCollaborazione()) &&
							busta.isConfermaRicezione() &&
							this.openspcoopProperties.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD)){
						// Attendono riscontro
						Riscontro r = new Riscontro();
						r.setID(busta.getID());
						r.setOraRegistrazione(DateManager.getDate());
						r.setTipoOraRegistrazione(TipoOraRegistrazione.SINCRONIZZATO);
						bustaRisposta.addRiscontro(r);
						
					} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
							busta.getRiferimentoMessaggio()==null){
						// devo generare ricevuta
						bustaRisposta.setTipoServizioCorrelato("SPC");
						bustaRisposta.setServizioCorrelato(busta.getServizio()+"Correlato");
					} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(busta.getProfiloDiCollaborazione()) &&
							busta.getRiferimentoMessaggio()==null){
					
						// salvo messaggio sul database asincrono/repositoryBuste
					
						// get database
						try{
							resource = dbManager.getResource(this.openspcoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol()),"ConnettoreNullEcho",busta.getID());
						}catch(Exception e){
							throw new Exception("Risorsa non ottenibile",e);
						}
						if(resource==null)
							throw new Exception("Risorsa is null");
						if(resource.getResource() == null)
							throw new Exception("Connessione is null");
						Connection connectionDB = (Connection) resource.getResource();
						//POOL,TRANSACTIONISOLATION:connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
						
						state.setConnectionDB(connectionDB);
						
						// repository
						RepositoryBuste repositoryBuste = new RepositoryBuste(state, true,protocolFactory);
						repositoryBuste.registraBustaIntoInBox(busta, new ArrayList<Eccezione>() ,
								OpenSPCoop2Properties.getInstance().getRepositoryIntervalloScadenzaMessaggi());
						Integrazione infoIntegrazione = new Integrazione();
						infoIntegrazione.setIdModuloInAttesa(null);
						repositoryBuste.aggiornaInfoIntegrazioneIntoInBox(busta.getID(),infoIntegrazione);
					
						// asincrono
						ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);
						profiloCollaborazione.asincronoSimmetrico_registraRichiestaRicevuta(busta.getID(),busta.getCollaborazione(),
								busta.getTipoServizioCorrelato(),busta.getServizioCorrelato(),busta.getVersioneServizioCorrelato(),true,
								this.openspcoopProperties.getRepositoryIntervalloScadenzaMessaggi());
					
						// commit
						try{
							connectionDB.setAutoCommit(false);
							state.executePreparedStatement(); 
				
							connectionDB.commit();
							connectionDB.setAutoCommit(true);
						}catch (Exception e) {	
							this.logger.error("Riscontrato errore durante la gestione transazione del DB per la richiesta: "+e.getMessage());
							// Rollback quanto effettuato (se l'errore e' avvenuto sul commit, o prima nell'execute delle PreparedStatement)
							try{
								connectionDB.rollback();
							}catch(Exception er){}
							// Ripristino connessione
							try{
								connectionDB.setAutoCommit(true);
							}catch(Exception er){}
							state.closePreparedStatement(); // Chiude le PreparedStatement aperte(e non eseguite) per il save del Msg
						}
					
					}
				
					// Riferimento Messaggio
					bustaRisposta.setRiferimentoMessaggio(busta.getID());
					
					// Identificativo Messaggio
					String dominio = null;
					if(request.getConnectorProperties()!=null)
						dominio = request.getConnectorProperties().get("identificativo-porta");
					if(dominio==null){
						dominio = protocolFactory.createTraduttore().getIdentificativoPortaDefault(new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario()));
					}
					String idBustaRisposta = null;
					Imbustamento imbustatore = new Imbustamento(this.logger.getLogger(), protocolFactory, state);
					try{
						idBustaRisposta = 
							imbustatore.buildID(new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario(), dominio), 
									null, 
									this.openspcoopProperties.getGestioneSerializableDB_AttesaAttiva(),
									this.openspcoopProperties.getGestioneSerializableDB_CheckInterval(),
									RuoloMessaggio.RISPOSTA);
					}catch(Exception e){
						// rilancio
						throw e;
					}
					bustaRisposta.setID(idBustaRisposta);
					
					if(generaTrasmissioneAndataRitorno){
						Trasmissione t = new Trasmissione();
						t.setTipoOrigine(busta.getTipoMittente());
						t.setOrigine(busta.getMittente());
						t.setIdentificativoPortaOrigine(busta.getIdentificativoPortaMittente());
						t.setIndirizzoOrigine(busta.getIndirizzoMittente());
						
						t.setTipoDestinazione(busta.getTipoDestinatario());
						t.setDestinazione(busta.getDestinatario());
						t.setIdentificativoPortaDestinazione(busta.getIdentificativoPortaDestinatario());
						t.setIndirizzoDestinazione(busta.getIndirizzoDestinatario());
						
						t.setOraRegistrazione(busta.getOraRegistrazione());
						t.setTempo(busta.getTipoOraRegistrazione(), busta.getTipoOraRegistrazioneValue());
						bustaRisposta.addTrasmissione(t);
					}
					if(generaTrasmissione || generaTrasmissioneInvertita || generaTrasmissioneAndataRitorno){
						Trasmissione t = new Trasmissione();
						if(generaTrasmissione || generaTrasmissioneAndataRitorno){
							t.setTipoOrigine(bustaRisposta.getTipoMittente());
							t.setOrigine(bustaRisposta.getMittente());
							t.setIdentificativoPortaOrigine(bustaRisposta.getIdentificativoPortaMittente());
							t.setIndirizzoOrigine(bustaRisposta.getIndirizzoMittente());
							
							t.setTipoDestinazione(bustaRisposta.getTipoDestinatario());
							t.setDestinazione(bustaRisposta.getDestinatario());
							t.setIdentificativoPortaDestinazione(bustaRisposta.getIdentificativoPortaDestinatario());
							t.setIndirizzoDestinazione(bustaRisposta.getIndirizzoDestinatario());
						}
						if(generaTrasmissioneInvertita){
							// invertita
							t.setTipoOrigine(bustaRisposta.getTipoDestinatario());
							t.setOrigine(bustaRisposta.getDestinatario());
							t.setIdentificativoPortaOrigine(bustaRisposta.getIdentificativoPortaDestinatario());
							t.setIndirizzoOrigine(bustaRisposta.getIndirizzoDestinatario());
							
							t.setTipoDestinazione(bustaRisposta.getTipoMittente());
							t.setDestinazione(bustaRisposta.getMittente());
							t.setIdentificativoPortaDestinazione(bustaRisposta.getIdentificativoPortaMittente());
							t.setIndirizzoDestinazione(bustaRisposta.getIndirizzoMittente());
						}
						t.setOraRegistrazione(bustaRisposta.getOraRegistrazione());
						t.setTempo(bustaRisposta.getTipoOraRegistrazione(), bustaRisposta.getTipoOraRegistrazioneValue());
						bustaRisposta.addTrasmissione(t);
					}
								
					// imbustamento nuova busta
					Integrazione integrazione = new Integrazione();
					integrazione.setStateless(true);
					
					ProtocolMessage protocolMessageRisposta = imbustatore.imbustamentoRisposta(this.responseMsg,this.getPddContext(),
							bustaRisposta,busta,integrazione,gestioneManifest,false,
							this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
							FaseImbustamento.PRIMA_SICUREZZA_MESSAGGIO);
					if(protocolMessageRisposta!=null && !protocolMessageRisposta.isPhaseUnsupported()) {
						this.responseMsg = protocolMessageRisposta.getMessage(); // updated
					}
					
					protocolMessageRisposta = imbustatore.imbustamentoRisposta(this.responseMsg,this.getPddContext(),
							bustaRisposta,busta,integrazione,gestioneManifest,false,
							this.openspcoopProperties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD),
							FaseImbustamento.DOPO_SICUREZZA_MESSAGGIO);
					if(protocolMessageRisposta!=null && !protocolMessageRisposta.isPhaseUnsupported()) {
						this.responseMsg = protocolMessageRisposta.getMessage(); // updated
					}

				}
				else{
					// rimuovo il FAULT.
					if(this.responseMsg instanceof OpenSPCoop2SoapMessage){
						OpenSPCoop2SoapMessage soapMessage = this.responseMsg.castAsSoap();
						if(soapMessage.getSOAPBody()!=null && soapMessage.getSOAPBody().hasFault()){
							soapMessage.getSOAPBody().removeChild(soapMessage.getSOAPBody().getFault());
						}
					}
				}
			}
			
			// content length
			if(this.responseMsg!=null){
				this.contentLength = this.responseMsg.getIncomingMessageContentLength();
			}
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			String msgErrore = this.readExceptionMessageFromException(e);
			if(this.generateErrorWithConnectorPrefix) {
				this.errore = "Riscontrato errore durante l'echo del messaggio: "+msgErrore;
			}
			else {
				this.errore = msgErrore;
			}
			this.logger.error("Riscontrato errore durante l'echo del messaggio: "+msgErrore,e);
			return false;
		}finally{
			
			// *** GB ***
			if(validatoreSintattico!=null){
				validatoreSintattico.setHeaderSOAP(null);
			}
			validatoreSintattico = null;
			if(validatoreProtocollo!=null){
				if(validatoreProtocollo.getValidatoreSintattico()!=null){
					validatoreProtocollo.getValidatoreSintattico().setHeaderSOAP(null);
				}
				validatoreProtocollo.setValidatoreSintattico(null);
			}
			validatoreProtocollo = null;
			headerProtocolloRisposta = null;
			// *** GB ***
			
			// release database
			dbManager.releaseResource(this.openspcoopProperties.getIdentitaPortaDefault(protocol),
					"ConnettoreNullEcho", resource);
		}
		
		return true;
	}
    
    /**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation() throws ConnettoreException {
    	// Il connettore NULL_ECHO ho possiede possibilita' di consegnare su di una url
    	//return ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, this.propertiesUrlBased, LOCATION);
    	return LOCATION;
    }
    
}





