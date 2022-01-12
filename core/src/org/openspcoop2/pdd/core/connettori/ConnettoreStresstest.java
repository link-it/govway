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




package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Random;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.constants.CostantiRegistroServizi;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.pdd.config.DBManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.Resource;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.builder.Imbustamento;
import org.openspcoop2.protocol.engine.driver.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.engine.driver.RepositoryBuste;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.Integrazione;
import org.openspcoop2.protocol.sdk.config.ITraduttore;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.StatefulMessage;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;





/**
 * ConnettoreStresstest
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreStresstest extends ConnettoreBase {

    @Override
	public String getProtocollo() {
    	return "HTTP";
    }
	
	public final static String ENDPOINT_TYPE = "stresstest";
	
	public final static String LOCATION = "openspcoop2://stresstest";
	

	/* ********  F I E L D S  P R I V A T I  ******** */

	private static final String HEADER_APPLICATIVO = "<thdr:headerApplicativo xmlns:thdr=\"http://example.org/test\" tipo=\"TEST\" soapenv:actor=\"http://example.org/test/actor\">\n"+
													 "<identificativoDominio>ITALIA</identificativoDominio>\n"+
													 "<thdr:identificatore>RISP@SERIAL@</thdr:identificatore>\n"+
													 "</thdr:headerApplicativo>";
	
	private static final String SOAP_ENVELOPE_RISPOSTA = 
		"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
		"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><soapenv:Header>@HDR@</soapenv:Header>";
	private static final String SOAP_ENVELOPE_RISPOSTA_END = 
		"<soapenv:Body><prova>test</prova></soapenv:Body></soapenv:Envelope>";


    private Resource resource = null;
    private DBManager dbManager = null;
   
    public ConnettoreStresstest(){
    	super();
    	this.dbManager = DBManager.getInstance();
    }
    
	
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
		
		// - Header Applicativo nella risposta
		boolean headerApplicativoRisposta = false;
		if(this.properties.get(CostantiConnettori.CONNETTORE_STRESS_TEST_HEADER_APPLICATIVO)!=null){
			if("true".equalsIgnoreCase(this.properties.get(CostantiConnettori.CONNETTORE_STRESS_TEST_HEADER_APPLICATIVO).trim()))
				headerApplicativoRisposta = true;
		}
			
		this.codice = 200;
				
		try{
			
			// SIMULAZIONE WRITE_TO
			
			org.apache.commons.io.output.NullOutputStream nullOutputStream = org.apache.commons.io.output.NullOutputStream.NULL_OUTPUT_STREAM;
			this.requestMsg.writeTo(nullOutputStream,true);
			nullOutputStream.flush();
			nullOutputStream.close();
				
			//this.response = request.getRequestMessage();
			//java.io.FileOutputStream fout = new java.io.FileOutputStream("/dev/null");
			//request.getRequestMessage().writeTo(fout,true);
			//fout.flush();
			//fout.close();
			
			
			
			
			
			/* ------------  PostOutRequestHandler ------------- */
			this.postOutRequest();
			
			
			
			
			
			
			
			// SLEEP
			// Connettore custom, impostare nei parametri i tempi di sleep
			if(request.getConnectorProperties()!=null){
//				java.util.Enumeration<String> enValues = request.getConnectorProperties().keys();
//				while (enValues.hasMoreElements()) {
//					String key = (String) enValues.nextElement();
//					System.out.println("KEY["+key+"]=["+request.getConnectorProperties().get(key)+"]");
//				}
				
				Object max = request.getConnectorProperties().get(CostantiConnettori.CONNETTORE_STRESS_TEST_SLEEP_MAX);
				Object min = request.getConnectorProperties().get(CostantiConnettori.CONNETTORE_STRESS_TEST_SLEEP_MIN);
				if(max!=null){
					int maxSleep = Integer.parseInt((String)max);
					int minSleep = 0;
					if(min!=null){
						minSleep = Integer.parseInt((String)min);
					}
					Random r = new Random();
					int sleep = minSleep + r.nextInt(maxSleep-minSleep);
					if(sleep>1000){
						int count = sleep/1000;
						int resto = sleep%1000;
						this.logger.info("sleep "+sleep+"ms ...",false);
						for (int i = 0; i < count; i++) {
							Utilities.sleep(1000);
						}
						Utilities.sleep(resto);
						this.logger.info("sleep "+sleep+"ms terminated", false);
					}else{
						this.logger.info("sleep "+sleep+"ms ...", false);
						Utilities.sleep(sleep);
						this.logger.info("sleep "+sleep+"ms terminated", false);
					}
				}
				else{
					Object sleepConstant = request.getConnectorProperties().get(CostantiConnettori.CONNETTORE_STRESS_TEST_SLEEP);
					if(sleepConstant!=null){
						int millisecond = Integer.parseInt((String)sleepConstant);
						if(millisecond>1000){
							int count = millisecond/1000;
							int resto = millisecond%1000;
							this.logger.info("sleep "+millisecond+"ms ...", false);
							for (int i = 0; i < count; i++) {
								Utilities.sleep(1000);
							}
							Utilities.sleep(resto);
							this.logger.info("sleep "+millisecond+"ms terminated", false);
						}else{
							this.logger.info("sleep "+millisecond+"ms ...", false);
							Utilities.sleep(millisecond);
							this.logger.info("sleep "+millisecond+"ms terminated", false);
						}
					}
				}
			}
			
			
			
			
			
			
			/* ------------  PreInResponseHandler ------------- */
			this.preInResponse();
			
			
			
			// Lettura risposta parametri NotifierInputStream per la risposta
			org.openspcoop2.utils.io.notifier.NotifierInputStreamParams notifierInputStreamParams = null;
			if(this.preInResponseContext!=null){
				notifierInputStreamParams = this.preInResponseContext.getNotifierInputStreamParams();
			}
			
			
    		if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(request.getBusta().getProfiloDiCollaborazione())){
    			// se non devo generare un riscontro, non e' prevista una risposta
    			if ( ! (request.getBusta().isConfermaRicezione() &&
    					this.openspcoopProperties.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD))){
    				return true; // non devo generare alcuna risposta
    			}
    		}
			String protocolHeader = this.buildProtocolHeader(request);
			if(headerApplicativoRisposta){
				String hdrApplicativo = HEADER_APPLICATIVO.replace("@SERIAL@", DateManager.getTimeMillis()+"");
				protocolHeader = hdrApplicativo + "\n" + protocolHeader;
			}
			String messaggio = SOAP_ENVELOPE_RISPOSTA.replace("@HDR@", protocolHeader) + SOAP_ENVELOPE_RISPOSTA_END;
			byte [] messaggioArray = messaggio.getBytes();
			
			ByteArrayInputStream bin = new ByteArrayInputStream(messaggioArray);
			OpenSPCoop2MessageFactory messageFactory = org.openspcoop2.pdd.core.Utilities.getOpenspcoop2MessageFactory(this.logger.getLogger(),this.requestMsg, this.requestInfo, MessageRole.RESPONSE);
			OpenSPCoop2MessageParseResult pr = messageFactory.createMessage(this.requestMsg.getMessageType(),MessageRole.RESPONSE,
					this.requestMsg.getContentType(),
					bin,notifierInputStreamParams,
					this.openspcoopProperties.getAttachmentsProcessingMode());
			// Non funziona con gli attachments: this.responseMsg = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(request.getRequestMessage().getVersioneSoap(),(bout.toByteArray()),notifierInputStreamParams);
			if(pr.getParseException()!=null){
				this.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
			}
			this.responseMsg = pr.getMessage_throwParseException();
						
			// content length
			if(this.responseMsg!=null){
				this.contentLength = messaggioArray.length;
			}
			
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("Riscontrato errore durante l'echo del msg soap",e);
			this.errore = "Riscontrato errore durante l'echo del msg soap:" +this.readExceptionMessageFromException(e);
			return false;
		}finally{
			// release database
			this.dbManager.releaseResource(this.openspcoopProperties.getIdentitaPortaDefault(this.getProtocolFactory().getProtocol()),"ConnettoreStresstest", this.resource);
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
    	return ConnettoreUtils.buildLocationWithURLBasedParameter(this.logger!=null ? this.logger.getLogger() : null, this.requestMsg, ConnettoreStresstest.ENDPOINT_TYPE, this.propertiesUrlBased, LOCATION,
				this.getProtocolFactory(), this.idModulo);
    }
 
    
    
    private String buildProtocolHeader(ConnettoreMsg request) throws Exception{
		Busta bustaRichiesta = request.getBusta();
    	if( "spcoop".equals(bustaRichiesta.getProtocollo())){
    		return buildSPCoopProtocolHeader(request);
    	}
    	else{
    		return "";
    	}
    }
    

    
    private String buildSPCoopProtocolHeader(ConnettoreMsg request) throws Exception{	
    	
		Busta bustaRichiesta = request.getBusta();
		
		StatefulMessage state = new StatefulMessage(null, this.logger.getLogger());
    	
		Object id = this.getPddContext().getObject(Costanti.ID_TRANSAZIONE);
		String idTransazione = null;
		if(id!=null){
			idTransazione = (String) id;
		}
    			
		StringBuilder protocolHeader = new StringBuilder();
		
    	if(bustaRichiesta!=null && (bustaRichiesta.sizeListaEccezioni()==0) && !ConsegnaContenutiApplicativi.ID_MODULO.equals(request.getIdModulo())){
			// Creo busta di risposta solo se la busta di richiesta non conteneva una busta SPCoop Errore e se il profilo lo richiede.
    		    		
			String idRiscontro = null;	
			
			IProtocolFactory<?> protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(bustaRichiesta.getProtocollo());
			ITraduttore traduttore = protocolFactory.createTraduttore();
			
			protocolHeader.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" " +
					"xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\"><eGov_IT:IntestazioneMessaggio>");
			
			// mittente
			protocolHeader.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+traduttore.toProtocolOrganizationType(bustaRichiesta.getTipoDestinatario())+"\">"+bustaRichiesta.getDestinatario()+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>");
		
			// destinatario
			protocolHeader.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+traduttore.toProtocolOrganizationType(bustaRichiesta.getTipoMittente())+"\">"+bustaRichiesta.getMittente()+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>");
		
			// ProfiloCollaborazione
			if(bustaRichiesta.getProfiloDiCollaborazione()!=null){

				
				if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ONEWAY.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
						bustaRichiesta.isConfermaRicezione() &&
						this.openspcoopProperties.isGestioneRiscontri(CostantiRegistroServizi.IMPLEMENTAZIONE_STANDARD)){
					
						protocolHeader.append("<eGov_IT:ProfiloCollaborazione>"+traduttore.toString(bustaRichiesta.getProfiloDiCollaborazione())+"</eGov_IT:ProfiloCollaborazione>");
						// Attendono riscontro
						idRiscontro = bustaRichiesta.getID();
				} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
						bustaRichiesta.getRiferimentoMessaggio()==null){
					
						// devo generare ricevuta
						protocolHeader.append("<eGov_IT:ProfiloCollaborazione tipo=\""+traduttore.toProtocolServiceType(bustaRichiesta.getTipoServizio())+"\" servizioCorrelato=\""+
								(bustaRichiesta.getServizio()+"Correlato")+"\" >"+traduttore.toString(bustaRichiesta.getProfiloDiCollaborazione())+"</eGov_IT:ProfiloCollaborazione>");
				} else if(org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO.equals(bustaRichiesta.getProfiloDiCollaborazione()) &&
						bustaRichiesta.getRiferimentoMessaggio()==null){
				
					// salvo messaggio sul database asincrono/repositoryEGov
					// get database
					try{
						this.resource = this.dbManager.getResource(this.openspcoopProperties.getIdentitaPortaDefault(this.getProtocolFactory().getProtocol()),"ConnettoreStresstest",idTransazione);
					}catch(Exception e){
						throw new Exception("Risorsa non ottenibile",e);
					}
					if(this.resource==null)
						throw new Exception("Risorsa is null");
					if(this.resource.getResource() == null)
						throw new Exception("Connessione is null");
					Connection connectionDB = (Connection) this.resource.getResource();
					//POOL,TRANSACTIONISOLATION:connectionDB.setTransactionIsolation(DBManager.getTransactionIsolationLevel());
					
					state.setConnectionDB(connectionDB);
					
					// repository
					RepositoryBuste repositoryBuste = new RepositoryBuste(state, true,this.getProtocolFactory());
					repositoryBuste.registraBustaIntoInBox(bustaRichiesta, new ArrayList<Eccezione>() ,
							OpenSPCoop2Properties.getInstance().getRepositoryIntervalloScadenzaMessaggi());
					Integrazione infoIntegrazione = new Integrazione();
					infoIntegrazione.setIdModuloInAttesa(null);
					repositoryBuste.aggiornaInfoIntegrazioneIntoInBox(bustaRichiesta.getID(),infoIntegrazione);
				
					// asincrono
					ProfiloDiCollaborazione profiloCollaborazione = new ProfiloDiCollaborazione(state,this.getProtocolFactory());
					profiloCollaborazione.asincronoSimmetrico_registraRichiestaRicevuta(bustaRichiesta.getID(),bustaRichiesta.getCollaborazione(),
							bustaRichiesta.getTipoServizioCorrelato(),bustaRichiesta.getServizioCorrelato(),bustaRichiesta.getVersioneServizioCorrelato(),true,
							this.openspcoopProperties.getRepositoryIntervalloScadenzaMessaggi());
				
					// commit
					try{
						connectionDB.setAutoCommit(false);
						state.executePreparedStatement(); 
			
						connectionDB.commit();
						connectionDB.setAutoCommit(true);
					}catch (Exception e) {	
						this.logger.error("Riscontrato errore durante la gestione transazione del DB per la richiesta: "+e.getMessage(),e);
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
					protocolHeader.append("<eGov_IT:ProfiloCollaborazione>"+traduttore.toString(bustaRichiesta.getProfiloDiCollaborazione())+"</eGov_IT:ProfiloCollaborazione>");
				}
				else{
					protocolHeader.append("<eGov_IT:ProfiloCollaborazione>"+traduttore.toString(bustaRichiesta.getProfiloDiCollaborazione())+"</eGov_IT:ProfiloCollaborazione>");
				}
			}
			
			// servizio
			if(bustaRichiesta.getTipoServizio()!=null && bustaRichiesta.getServizio()!=null)
				protocolHeader.append("<eGov_IT:Servizio tipo=\""+traduttore.toProtocolServiceType(bustaRichiesta.getTipoServizio())+"\">"+bustaRichiesta.getServizio()+"</eGov_IT:Servizio>");
			
			// azione
			if(bustaRichiesta.getAzione()!=null)
				protocolHeader.append("<eGov_IT:Azione>"+bustaRichiesta.getAzione()+"</eGov_IT:Azione>");
			
			// Messaggio
			protocolHeader.append("<eGov_IT:Messaggio>");
			
			// Identificativo egov
			String dominio = null;
			if(request.getConnectorProperties()!=null)
				dominio = request.getConnectorProperties().get("identificativo-porta");
			if(dominio==null)
				dominio=bustaRichiesta.getDestinatario()+"SPCoopIT";
			String idBustaRisposta = null;
			Imbustamento imbustatore = new Imbustamento(this.logger.getLogger(), this.getProtocolFactory(),state);
			try{
				idBustaRisposta = 
					imbustatore.buildID(new IDSoggetto(bustaRichiesta.getTipoDestinatario(), bustaRichiesta.getDestinatario(), dominio), 
							null, 
							this.openspcoopProperties.getGestioneSerializableDB_AttesaAttiva(),
							this.openspcoopProperties.getGestioneSerializableDB_CheckInterval(),
							RuoloMessaggio.RISPOSTA);
			}catch(Exception e){
				// rilancio
				throw e;
			}
			protocolHeader.append("<eGov_IT:Identificatore>"+idBustaRisposta+"</eGov_IT:Identificatore>");
		
			// OraRegistrazione
			String oraS = traduttore.getDate_protocolFormat(bustaRichiesta.getOraRegistrazione());
			protocolHeader.append("<eGov_IT:OraRegistrazione tempo=\""+traduttore.toString(bustaRichiesta.getTipoOraRegistrazione())+"\">"+
					oraS+"</eGov_IT:OraRegistrazione>");
			
			// RiferimentoMessaggio
			protocolHeader.append("<eGov_IT:RiferimentoMessaggio>"+bustaRichiesta.getID()+"</eGov_IT:RiferimentoMessaggio>");
			
			protocolHeader.append("</eGov_IT:Messaggio>");
		
			protocolHeader.append("</eGov_IT:IntestazioneMessaggio>");
			
			if(idRiscontro!=null){
				
				protocolHeader.append("<eGov_IT:ListaRiscontri><eGov_IT:Riscontro>" +
						"<eGov_IT:Identificatore>"+idRiscontro+"</eGov_IT:Identificatore>" +
						"<eGov_IT:OraRegistrazione tempo=\""+traduttore.toString(bustaRichiesta.getTipoOraRegistrazione())+"\">"+oraS+"</eGov_IT:OraRegistrazione>" +
								"</eGov_IT:Riscontro> </eGov_IT:ListaRiscontri>");
			}
			
			protocolHeader.append("</eGov_IT:Intestazione>");
			
    	}
    	
    	return protocolHeader.toString();
    }
    
}





