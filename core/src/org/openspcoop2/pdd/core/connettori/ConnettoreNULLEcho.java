/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2015 Link.it srl (http://link.it). 
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




package org.openspcoop2.pdd.core.connettori;

import java.sql.Connection;
import java.util.Vector;

import javax.xml.soap.SOAPElement;

import org.apache.log4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.builder.Sbustamento;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.engine.validator.Validatore;
import org.openspcoop2.protocol.engine.validator.ValidazioneSintattica;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.Riscontro;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
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

	/** Logger utilizzato per debug. */
	private Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
	
	public final static String LOCATION = "openspcoop2://echo";
    
	


	/* ********  METODI  ******** */

	/**
	 * Si occupa di effettuare la consegna.
	 *
	 * @param request Messaggio da Consegnare
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	@Override
	public boolean send(ConnettoreMsg request){

		// Context per invocazioni handler
		this.outRequestContext = request.getOutRequestContext();
		this.msgDiagnostico = request.getMsgDiagnostico();
		
		
		this.codice = 200;
		DBManager dbManager = DBManager.getInstance();
		Resource resource = null;
		OpenSPCoop2Properties properties = OpenSPCoop2Properties.getInstance();
		
		StatefulMessage state = new StatefulMessage(null, this.log);
		
		ValidazioneSintattica validatoreSintattico = null;
		Validatore validatoreProtocollo = null;
		@SuppressWarnings("unused")
		SOAPElement headerProtocolloRisposta = null;
		String protocol = null;
		try{
			IProtocolFactory protocolFactory = this.getProtocolFactory();
			IProtocolManager protocolManager = protocolFactory.createProtocolManager();
			protocol = protocolFactory.getProtocol();
			
			//this.response = request.getRequestMessage();
			java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
			request.getRequestMessage().writeTo(bout, true);
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
			
			
			this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(request.getRequestMessage().getVersioneSoap(),(bout.toByteArray()),notifierInputStreamParams);
			
			validatoreSintattico = new ValidazioneSintattica(state,this.responseMsg, properties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory); 

			if(validatoreSintattico.verifyProtocolPresence(TipoPdD.APPLICATIVA,null,false)){
				
				// getBusta
				ProprietaValidazione property = new ProprietaValidazione();
				property.setValidazioneConSchema(false);
				property.setValidazioneProfiloCollaborazione(false);
				property.setValidazioneManifestAttachments(false);
				
				validatoreProtocollo = new Validatore(this.responseMsg,property,null,
						properties.isReadQualifiedAttribute(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD), protocolFactory);
				
				if(validatoreProtocollo.validazioneSintattica() == false){
					throw new Exception("Busta non presente: "+validatoreProtocollo.getErrore().getDescrizione(protocolFactory));
				}
				
				// Leggo busta di richiesta
				Busta busta = validatoreProtocollo.getBusta();
				
				// informazione spcoopErrore
				IValidatoreErrori validatoreErrori = protocolFactory.createValidatoreErrori();
				ProprietaValidazioneErrori pValidazioneErrori = new ProprietaValidazioneErrori();
				pValidazioneErrori.setIgnoraEccezioniNonGravi(protocolManager.isIgnoraEccezioniNonGravi());
				boolean isBustaSPCoopErrore = validatoreErrori.isBustaErrore(busta,this.responseMsg,pValidazioneErrori);
				
				// rimozione vecchia busta
				Sbustamento sbustatore = new Sbustamento(protocolFactory);
				headerProtocolloRisposta = sbustatore.sbustamento(state,this.responseMsg,busta,true, true, 
						properties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD));
				
				// Creo busta di risposta solo se la busta di richiesta non conteneva una busta Errore
				if(!isBustaSPCoopErrore){
			
					TipoOraRegistrazione tipoOraRegistrazione = properties.getTipoTempoBusta(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD);
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
							properties.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD)){
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
							resource = dbManager.getResource(properties.getIdentitaPortaDefault(protocolFactory.getProtocol()),"ConnettoreNullEcho",busta.getID());
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
						repositoryBuste.registraBustaIntoInBox(busta, new Vector<Eccezione>() ,
								OpenSPCoop2Properties.getInstance().getRepositoryIntervalloScadenzaMessaggi());
						Integrazione infoIntegrazione = new Integrazione();
						infoIntegrazione.setIdModuloInAttesa(null);
						repositoryBuste.aggiornaInfoIntegrazioneIntoInBox(busta.getID(),infoIntegrazione);
					
						// asincrono
						ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(state,protocolFactory);
						profiloCollaborazione.asincronoSimmetrico_registraRichiestaRicevuta(busta.getID(),busta.getCollaborazione(),
								busta.getTipoServizioCorrelato(),busta.getServizioCorrelato(),true,
								properties.getRepositoryIntervalloScadenzaMessaggi());
					
						// commit
						try{
							connectionDB.setAutoCommit(false);
							state.executePreparedStatement(); 
				
							connectionDB.commit();
							connectionDB.setAutoCommit(true);
						}catch (Exception e) {	
							this.log.error("Riscontrato errore durante la gestione transazione del DB per la richiesta: "+e.getMessage());
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
					Imbustamento imbustatore = new Imbustamento(protocolFactory);
					try{
						idBustaRisposta = 
							imbustatore.buildID(state,new IDSoggetto(busta.getTipoDestinatario(), busta.getDestinatario(), dominio), 
									null, 
									properties.getGestioneSerializableDB_AttesaAttiva(),
									properties.getGestioneSerializableDB_CheckInterval(),
									Boolean.FALSE);
					}catch(Exception e){
						// rilancio
						throw e;
					}
					bustaRisposta.setID(idBustaRisposta);
								
					// imbustamento nuova busta
					Integrazione integrazione = new Integrazione();
					integrazione.setStateless(true);
					imbustatore.imbustamento(state,this.responseMsg,bustaRisposta,integrazione,false,false,false,
							properties.getProprietaManifestAttachments(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD));

				}
				else{
					// rimuovo il FAULT.
					if(this.responseMsg.getSOAPBody()!=null && this.responseMsg.getSOAPBody().hasFault()){
						this.responseMsg.getSOAPBody().removeChild(this.responseMsg.getSOAPBody().getFault());
					}
				}
			}
			
			// content length
			if(this.responseMsg!=null){
				this.contentLength = this.responseMsg.getIncomingMessageContentLength();
			}
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.log.error("Riscontrato errore durante l'echo del msg soap");
			this.errore = "Riscontrato errore durante l'echo del msg soap:" +e.getMessage();
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
			dbManager.releaseResource(properties.getIdentitaPortaDefault(protocol),
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
	public String getLocation(){
    	return LOCATION;
    }
    
}





