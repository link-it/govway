/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
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



package org.openspcoop2.testsuite.units;

import java.io.IOException;
import java.util.Iterator;

import javax.xml.soap.SOAPException;

import org.apache.axis.Message;
import org.apache.axis.attachments.AttachmentPart;
import org.slf4j.Logger;
import org.openspcoop2.testsuite.clients.ClientAsincronoAsimmetrico_ModalitaAsincrona;
import org.openspcoop2.testsuite.clients.ClientAsincronoAsimmetrico_ModalitaSincrona;
import org.openspcoop2.testsuite.clients.ClientAsincronoSimmetrico_ModalitaAsincrona;
import org.openspcoop2.testsuite.clients.ClientAsincronoSimmetrico_ModalitaSincrona;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.db.DatiServizioAzione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.testng.Assert;
import org.testng.Reporter;


/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * utilizzabili con messaggi Soap normali o con attachments
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class CooperazioneBase {

	/** Tipo di gestione */
	protected boolean soapWithAttachments;
	protected SOAPVersion soapVersion;
	/** String tipoCooperazione */
	protected String tipoCooperazione;
	
	/** Mittente */
	protected IDSoggetto mittente;
	/** Destinatario */
	protected IDSoggetto destinatario;
	/** Conferma Ricezione */
	protected boolean confermaRicezione;
	/** Inoltro */
	protected String inoltro;
	protected Inoltro inoltroSdk;
	
	protected CooperazioneBaseInformazioni info;
	
	protected UnitsTestSuiteProperties unitsTestsuiteProperties;
	protected UnitsDatabaseProperties unitsDatabaseProperties;
	protected Logger log;
	
	protected boolean portaDelegata;
	
	private boolean responseIsFault = false;
	public void setResponseIsFault(boolean responseIsFault) {
		this.responseIsFault = responseIsFault;
	}
	
	public boolean isSoapWithAttachments() {
		return this.soapWithAttachments;
	}
	public void setSoapWithAttachments(boolean soapWithAttachments) {
		this.soapWithAttachments = soapWithAttachments;
	}
	public CooperazioneBase(boolean soapWithAttachments,SOAPVersion soapVersion,CooperazioneBaseInformazioni info,
			UnitsTestSuiteProperties unitsTestsuiteProperties, UnitsDatabaseProperties unitsDatabaseProperties, Logger log) {
		this(soapWithAttachments, soapVersion, info, unitsTestsuiteProperties, unitsDatabaseProperties, log, true);
	}
	
	public CooperazioneBase(boolean soapWithAttachments,SOAPVersion soapVersion,CooperazioneBaseInformazioni info,
			UnitsTestSuiteProperties unitsTestsuiteProperties, UnitsDatabaseProperties unitsDatabaseProperties, Logger log, boolean portaDelegata) {
		super();
		this.portaDelegata = portaDelegata;
		this.soapWithAttachments = soapWithAttachments;
		this.soapVersion = soapVersion;
		if(this.soapWithAttachments)
			this.tipoCooperazione = "SOAPWithAttachments";
		else
			this.tipoCooperazione = "SOAP";
		this.mittente = info.getMittente();
		this.destinatario = info.getDestinatario();
		this.confermaRicezione = info.isConfermaRicezione();
		this.inoltro = info.getInoltro();
		this.inoltroSdk = info.getInoltroSdk();
		
		this.info = info;
		
		this.unitsTestsuiteProperties = unitsTestsuiteProperties;
		this.unitsDatabaseProperties = unitsDatabaseProperties;
		this.log = log;
	}




	
	private void checkAttachmentsRequest(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione,Message msgFromCheck){
		if(this.isSoapWithAttachments()){
			
			try{
				Thread.sleep(500);
			}catch(Exception e){}
			
			try{
				Message msg = null;
				if(msgFromCheck!=null){
					msg = msgFromCheck;
				}else{
					switch (this.soapVersion) {
					case SOAP11:
						msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false);
						break;
					case SOAP12:
						msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false);
						break;
					}
				}
				Iterator<?> itAp = msg.getAttachments();
				while (itAp.hasNext()) {
					AttachmentPart attachmentPart = (AttachmentPart) itAp.next();
					String contentId = attachmentPart.getContentId();
					String contentLocation = attachmentPart.getContentLocation();
					String contentType = attachmentPart.getContentType();
					Reporter.log("["+this.tipoCooperazione+"] Controllo allegato idMessaggio["+id+"] contentId["+contentId
							+"] contentLocation["+contentLocation+"] contentType["+contentType+"]");
					if(datiServizioAzione!=null){
						Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAllegato(id, datiServizioAzione, contentId, contentLocation, contentType, false));
					}
					else{
						Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAllegato(id, contentId, contentLocation, contentType, false));
					}
				
				}
			}catch(Exception e){
				this.log.error("Errore durante la verifica degli attachments: "+e.getMessage(),e);
				throw new  TestSuiteException(e.getMessage());
			}
		}
	}
	private void checkCountAttachmentsRequest(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest){
		_checkAttachments(true, data, id, datiServizioAzione, withManifest, null);
	}
	private void checkCountAttachmentsRequest(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
		_checkAttachments(true, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	@SuppressWarnings("unused")
	private void checkCountAttachmentsResponse(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest){
		_checkAttachments(false, data, id, datiServizioAzione, withManifest, null);
	}
	private void checkCountAttachmentsResponse(DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
		_checkAttachments(false, data, id, datiServizioAzione, withManifest, numeroAttachments);
	}
	private void _checkAttachments(boolean isRequest,DatabaseComponent data, String id, DatiServizioAzione datiServizioAzione ,boolean withManifest, Integer numeroAttachments){
		if(this.isSoapWithAttachments()){
			
			try{
				Thread.sleep(1250);
			}catch(Exception e){}
			
			try{
				Message msg = null;
				switch (this.soapVersion) {
				case SOAP11:
					msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false);
					break;
				case SOAP12:
					msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false);
					break;
				}
				
				int countAttachments = msg.countAttachments();
				if(numeroAttachments!=null){
					countAttachments = numeroAttachments;
				}
				if(withManifest){
					countAttachments = countAttachments+1; //manifest comporta che il body originale finisca come allegato
				}
				
				if(isRequest){
					if(datiServizioAzione!=null){
						long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id, datiServizioAzione);
						Reporter.log("["+this.tipoCooperazione+"] Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
						Assert.assertTrue(count==countAttachments);
					}
					else{
						long count = data.getVerificatoreTracciaRichiesta().countTracedAllegati(id);
						Reporter.log("["+this.tipoCooperazione+"] Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
						Assert.assertTrue(count==countAttachments);
					}
				}
				else{
					if(datiServizioAzione!=null){
						long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id, datiServizioAzione);
						Reporter.log("["+this.tipoCooperazione+"] Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
						Assert.assertTrue(count==countAttachments);
					}
					else{
						long count = data.getVerificatoreTracciaRisposta().countTracedAllegati(id);
						Reporter.log("["+this.tipoCooperazione+"] Controllo numero allegati attesi("+countAttachments+") trovati ("+count+")");
						Assert.assertTrue(count==countAttachments);
					}
				}
			}catch(Exception e){
				this.log.error("Errore durante la verifica degli attachments: "+e.getMessage(),e);
				throw new  TestSuiteException(e.getMessage());
			}
		}
	}
	
	
	private String getUrlPortaDiDominio() {
		return (this.portaDelegata) ? this.unitsTestsuiteProperties.getServizioRicezioneContenutiApplicativiFruitore() :this.unitsTestsuiteProperties.getServizioRicezioneBusteErogatore();
	}
	
	


	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	public void oneWay(Repository repository,String portaDelegata,boolean addIDUnivoco) throws TestSuiteException, Exception{
		this.oneWay(repository, portaDelegata, addIDUnivoco, null, null, null);
	}
	public void oneWay(Repository repository,String portaDelegata,boolean addIDUnivoco,Boolean attesaTerminazioneMessaggi) throws TestSuiteException, Exception{
		this.oneWay(repository, portaDelegata, addIDUnivoco, null, null,attesaTerminazioneMessaggi);
	}
	public void oneWay(Repository repository,String portaDelegata,boolean addIDUnivoco,String username,String password) throws TestSuiteException, Exception{
		this.oneWay(repository, portaDelegata, addIDUnivoco, username, password, null);
	}
	public void oneWay(Repository repository,String portaDelegata,boolean addIDUnivoco,String username,String password,Boolean attesaTerminazioneMessaggi) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientOneWay client=new ClientOneWay(repository);
			client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine(this.soapVersion);
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
			if(this.soapWithAttachments){
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				}
			}
			else{
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
					break;
				}
			}			

			// AttesaTerminazioneMessaggi
			if(this.unitsTestsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase() && 
					(attesaTerminazioneMessaggi==null || attesaTerminazioneMessaggi)){
				dbComponentFruitore = this.unitsDatabaseProperties.newInstanceDatabaseComponentFruitore();
				dbComponentErogatore = this.unitsDatabaseProperties.newInstanceDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
		}
	}
	public void testOneWay(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione) throws TestSuiteException{
		testOneWay(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, null, null, false, null, null);
	}
	public void testOneWay(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testOneWay(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, null, null, manifestAbilitato, null, null);
	}
	public void testOneWay(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testOneWay(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk, false, null, null);
	}
	public void testOneWay(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		testOneWay(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk,manifestAbilitato,null,null);
	}
	public void testOneWay(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato, 
			Integer numeroAttachments,Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_oneway(), ProfiloDiCollaborazione.ONEWAY));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato,numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
	}




	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	public void sincrono(Repository repository,String portaDelegata,boolean addIDUnivoco) throws TestSuiteException, IOException, SOAPException{
		sincrono(repository,portaDelegata,addIDUnivoco,false);
	}
	public void sincrono(Repository repository,String portaDelegata,boolean addIDUnivoco,String username,String password) throws TestSuiteException, IOException, SOAPException{
		sincrono(repository,portaDelegata,addIDUnivoco,false, username, password);
	}
	public void sincrono(Repository repository,String portaDelegata,boolean addIDUnivoco,boolean isOneWay) throws TestSuiteException, IOException, SOAPException{
		this.sincrono(repository, portaDelegata, addIDUnivoco, isOneWay, null, null);
	}
	public void sincrono(Repository repository,String portaDelegata,boolean addIDUnivoco,boolean isOneWay,String username,String password) throws TestSuiteException, IOException, SOAPException{
		// Creazione client Sincrono
		ClientSincrono client=new ClientSincrono(repository);
		client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
		client.setPortaDelegata(portaDelegata);
		client.connectToSoapEngine(this.soapVersion);
		if(username!=null && password!=null)
			client.setAutenticazione(username,password);
		if(this.soapWithAttachments){
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			}
		}
		else{
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
				break;
			}
		}
		client.run();

		// Test uguaglianza Body (e attachments)
		if(isOneWay==false){
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			if(this.soapWithAttachments)
				Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
		}else{
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
		}

	}
	public void testSincrono(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String collaborazione) throws TestSuiteException{
		testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, null, null,false);
	}
	public void testSincrono(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String collaborazione,
			boolean manifestAbilitato) throws TestSuiteException{
		testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, null, null,manifestAbilitato);
	}
	public void testSincrono(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,tipoTempoAtteso,tipoTempoAttesoSdk,false);
	}
	public void testSincrono(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,
			boolean manifestAbilitato) throws TestSuiteException{
		testSincrono(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk, manifestAbilitato,null,null);
	}
	public void testSincrono(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String collaborazione,String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,
			boolean manifestAbilitato, Integer numeroAttachments, Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_sincrono(), ProfiloDiCollaborazione.SINCRONO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, this.destinatario,null, this.mittente, null));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, this.destinatario,null, this.mittente, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}
		
		if(!this.responseIsFault)
			checkCountAttachmentsResponse(data, id, null, manifestAbilitato,  numeroAttachments);
	}






	/***
	 * Test per il profilo di collaborazione Asincrono Simmetrico, modalita asincrona
	 */
	public void asincronoSimmetrico_modalitaAsincrona(String portaDelegata,String portaDelegataCorrelata,String user,String password,RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,boolean addIDUnivoco) throws Exception{		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{

			ClientAsincronoSimmetrico_ModalitaAsincrona client = 
				new ClientAsincronoSimmetrico_ModalitaAsincrona(repositoryConsegnaRisposteAsincroneSimmetriche_modalitaAsincrona,
						repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,
						portaDelegataCorrelata);
			client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine(this.soapVersion);
			client.setAutenticazione(user,password);
			if(this.soapWithAttachments){
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				}
			}
			else{
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
					break;
				}
			}
			
			// AttesaTerminazioneMessaggi
			if(this.unitsTestsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = this.unitsDatabaseProperties.newInstanceDatabaseComponentFruitore();
				dbComponentErogatore = this.unitsDatabaseProperties.newInstanceDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
		}
	}
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione,null,null,false,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione,null,null,manifestAbilitato,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk,false,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaAsincrona(data,id,tipoServizio,servizio,azione,
				checkServizioApplicativo,tipoServizioCorrelato,servizioCorrelato,collaborazione,
				tipoTempoAtteso,tipoTempoAttesoSdk,manifestAbilitato, null,null);
	}
	public void testAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato, 
			Integer numeroAttachments, Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore tipo e servizio correlato");
		DatiServizio datiServizioCorrelato = new DatiServizio(tipoServizioCorrelato,servizioCorrelato, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizioCorrelato(id, datiServizioCorrelato));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null,manifestAbilitato, numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(tipoServizio, servizio, this.info.getServizio_versioneDefault(),azione);
		
		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta richiesta asincrona simmetrica con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione,this.destinatario,null ));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione,this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione,datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione,this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(),ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione,collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id,datiServizioAzione, this.destinatario, null, this.mittente, null));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id,datiServizioAzione, this.destinatario, null, this.mittente, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}
		
	}
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione, boolean checkServizioApplicativo,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,String collaborazione) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, 
				repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona, collaborazione,null,null,false);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione, boolean checkServizioApplicativo,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,String collaborazione,
			boolean manifestAbilitato) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, 
				repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona, collaborazione,null,null,manifestAbilitato);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione, boolean checkServizioApplicativo,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, 
				repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona, collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk, false);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione, boolean checkServizioApplicativo,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,
			boolean manifestAbilitato) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta risposta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta risposta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore RiferimentoMessaggio (valore atteso: "+idCorrelazioneAsincrona+")  della risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiferimentoMessaggio(id, idCorrelazioneAsincrona));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.destinatario, null, this.mittente, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.destinatario, null, this.mittente, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato); 
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		//String id = repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.getNextInvAssociate(id);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(tipoServizio, servizio, this.info.getServizio_versioneDefault(),azione);
				
		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta risposta asincrona simmetrica con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione,datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.mittente, null, this.destinatario, null,true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}
		else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.mittente, null, this.destinatario, null));
		}
	}






	/***
	 * Test per il profilo di collaborazione Asincrono Simmetrico, modalita sincrona
	 */
	public void asincronoSimmetrico_modalitaSincrona(String portaDelegata,String portaDelegataCorrelata, String user,String password,RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona,boolean addIDUnivoco) throws Exception{		
		ClientAsincronoSimmetrico_ModalitaSincrona client = 
			new ClientAsincronoSimmetrico_ModalitaSincrona(repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincrona,
					repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincrona,
					portaDelegataCorrelata);
		client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
		client.setPortaDelegata(portaDelegata);
		client.connectToSoapEngine(this.soapVersion);
		client.setAutenticazione(user,password);
		if(this.soapWithAttachments){
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			}
		}
		else{
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
				break;
			}
		}
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		if(this.soapWithAttachments)
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, null, null,false,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, null, null,manifestAbilitato,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk,false,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoSimmetrico_ModalitaSincrona(data,id,tipoServizio,servizio,azione,
				checkServizioApplicativo,tipoServizioCorrelato,servizioCorrelato,collaborazione,
				tipoTempoAtteso,tipoTempoAttesoSdk,manifestAbilitato,null,null);
	}
	public void testAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato,
			Integer numeroAttachments, Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore tipo e servizio correlato");
		DatiServizio datiServizioCorrelato = new DatiServizio(tipoServizioCorrelato,servizioCorrelato, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizioCorrelato(id, datiServizioCorrelato));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null,  this.destinatario, null, true, tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta richiesta asincrona simmetrica con riferimento messaggio: " +id);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio,azione);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null));
		}
	}
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				false);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				manifestAbilitato);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, 
				tipoTempoAtteso, tipoTempoAttesoSdk, false);
	}
	public void testRispostaAsincronoSimmetrico_ModalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta risposta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta risposta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore RiferimentoMessaggio (valore atteso: "+idCorrelazioneAsincrona+")  della risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiferimentoMessaggio(id, idCorrelazioneAsincrona));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(), ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.destinatario, null, this.mittente, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.destinatario, null, this.mittente, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato); // asincrono e' una risposta!
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		//String id = repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaAsincrona.getNextInvAssociate(id);
		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta risposta asincrona simmetrica con riferimento messaggio: " +id);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoSimmetrico(),ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.mittente, null, this.destinatario, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.mittente, null, this.destinatario, null));
		}
	}







	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita asincrona
	 */
	public void asincronoAsimmetrico_modalitaAsincrona(String portaDelegata, String portaDelegataCorrelata, 
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona,boolean addIDUnivoco) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{

			ClientAsincronoAsimmetrico_ModalitaAsincrona client = new ClientAsincronoAsimmetrico_ModalitaAsincrona(repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaAsincrona);
			client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
			client.setPortaDelegata(portaDelegata);
			client.setPortaDelegataCorrelata(portaDelegataCorrelata);
			client.connectToSoapEngine(this.soapVersion);
			client.setGeneraIDUnivoco(addIDUnivoco);
			if(this.soapWithAttachments){
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
					break;
				}
			}
			else{
				switch (this.soapVersion) {
				case SOAP11:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
					break;
				case SOAP12:
					client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
					break;
				}
			}
			
			// AttesaTerminazioneMessaggi
			if(this.unitsTestsuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = this.unitsDatabaseProperties.newInstanceDatabaseComponentFruitore();
				dbComponentErogatore = this.unitsDatabaseProperties.newInstanceDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
		}
	}
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,
			String servizio,String azione, boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione) throws TestSuiteException{
		testAsincronoAsimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, collaborazione, null, null,
				false,null,null);
	}
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,
			String servizio,String azione, boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoAsimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, collaborazione, null, null,
				manifestAbilitato,null,null);
	}
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,
			String servizio,String azione, boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testAsincronoAsimmetrico_ModalitaAsincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk,false,null,null);
	}
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,
			String servizio,String azione, boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoAsimmetrico_ModalitaAsincrona(data,id,tipoServizio,
				servizio,azione, checkServizioApplicativo,tipoServizioCorrelato,servizioCorrelato,collaborazione,
				tipoTempoAtteso,tipoTempoAttesoSdk,manifestAbilitato,null,null);
	}
	public void testAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String tipoServizio,
			String servizio,String azione, boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato,
			Integer numeroAttachments, Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null, true, tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta richiesta asincrona asimmetrica con riferimento messaggio: " +id);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore tipo e servizio correlato");
		DatiServizio datiServizioCorrelato = new DatiServizio(tipoServizioCorrelato, servizioCorrelato, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, datiServizioAzione, datiServizioCorrelato));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null,true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null));
		}
	}
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				false);
	}
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione, boolean manifestAbilitato) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				manifestAbilitato);
	}
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_ModalitaAsincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione, 
				tipoTempoAtteso, tipoTempoAttesoSdk, false);
	}
	public void testRispostaAsincronoAsimmetrico_ModalitaAsincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk, boolean manifestAbilitato) throws TestSuiteException{
		
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore RiferimentoMessaggio (valore atteso: "+idCorrelazioneAsincrona+")  della risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiferimentoMessaggio(id, idCorrelazioneAsincrona));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato); // asincrono e' una risposta!
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta risposta asincrona asimmetrica con riferimento messaggio: " +id);
		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,datiServizioAzione,this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id,datiServizioAzione, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario,null, this.mittente,null,true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario,null, this.mittente,null));
		}
	}






	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona
	 */
	public void asincronoAsimmetrico_modalitaSincrona(String portaDelegata,String portaDelegataCorrelata,
			RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona,boolean addIDUnivoco) throws TestSuiteException, IOException, SOAPException{
		ClientAsincronoAsimmetrico_ModalitaSincrona client = new ClientAsincronoAsimmetrico_ModalitaSincrona(repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincrona);
		client.setUrlPortaDiDominio(this.getUrlPortaDiDominio());
		client.setPortaDelegata(portaDelegata);
		client.setGeneraIDUnivoco(addIDUnivoco);
		client.setPortaDelegataCorrelata(portaDelegataCorrelata);
		client.connectToSoapEngine(this.soapVersion);
		if(this.soapWithAttachments){
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageWithAttachmentsFromFile(this.unitsTestsuiteProperties.getSoap12WithAttachmentsFileName(), false,addIDUnivoco);
				break;
			}
		}
		else{
			switch (this.soapVersion) {
			case SOAP11:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap11FileName(), false,addIDUnivoco);
				break;
			case SOAP12:
				client.setMessageFromFile(this.unitsTestsuiteProperties.getSoap12FileName(), false,addIDUnivoco);
				break;
			}
		}
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		if(this.soapWithAttachments)
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione) throws TestSuiteException{
		testAsincronoAsimmetrico_modalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, collaborazione, 
				null,null,false,null,null);
	}
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoAsimmetrico_modalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, collaborazione, 
				null,null,manifestAbilitato,null,null);
	}
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testAsincronoAsimmetrico_modalitaSincrona(data, id, tipoServizio, servizio, azione, checkServizioApplicativo, tipoServizioCorrelato, servizioCorrelato, 
				collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk, false,null,null);
	}
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		testAsincronoAsimmetrico_modalitaSincrona(data,id,tipoServizio,servizio,azione,
				checkServizioApplicativo,tipoServizioCorrelato,servizioCorrelato,collaborazione,
				tipoTempoAtteso,tipoTempoAttesoSdk,manifestAbilitato,null,null);
	}
	public void testAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String tipoServizio,String servizio,String azione,
			boolean checkServizioApplicativo,String tipoServizioCorrelato,String servizioCorrelato,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato,
			Integer numeroAttachments, Message msg) throws TestSuiteException{
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(), ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null,  true, tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null, this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato, numeroAttachments);
		checkAttachmentsRequest(data, id, null, msg);
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta richiesta asincrona asimmetrica con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, datiServizioAzione, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore tipo["+tipoServizioCorrelato+"] e servizio correlato["+servizioCorrelato+"]");
		DatiServizio datiServizioCorrelato = new DatiServizio(tipoServizioCorrelato, servizioCorrelato, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, datiServizioAzione,datiServizioCorrelato));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null, true, tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null));
		}
	}
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				false);
	}
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,boolean manifestAbilitato) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, collaborazione,null,null,
				manifestAbilitato);
	}
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk) throws TestSuiteException{
		testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona, tipoServizio, servizio, azione, checkServizioApplicativo, 
				collaborazione, tipoTempoAtteso, tipoTempoAttesoSdk, false);
	}
	public void testRispostaAsincronoAsimmetrico_modalitaSincrona(DatabaseComponent data,String id,String idCorrelazioneAsincrona,
			String tipoServizio,String servizio,String azione,boolean checkServizioApplicativo,String collaborazione,
			String tipoTempoAtteso,TipoOraRegistrazione tipoTempoAttesoSdk,boolean manifestAbilitato) throws TestSuiteException{
		
		Reporter.log("["+this.tipoCooperazione+"] Controllo tracciamento risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.mittente, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.destinatario, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(tipoServizio,servizio, this.info.getServizio_versioneDefault());
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore RiferimentoMessaggio (valore atteso: "+idCorrelazioneAsincrona+")  della risposta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiferimentoMessaggio(id, idCorrelazioneAsincrona));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(), ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.confermaRicezione,this.inoltro, this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null , this.destinatario, null, true,tipoTempoAtteso,tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.mittente, null,  this.destinatario, null));
		}
		checkCountAttachmentsRequest(data, id, null, manifestAbilitato); // asincrono e' una risposta!
		if(checkServizioApplicativo){
			Reporter.log("["+this.tipoCooperazione+"] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		Reporter.log("["+this.tipoCooperazione+"] ----------------------------------------------------------");

		DatiServizioAzione datiServizioAzione = new DatiServizioAzione(datiServizio, azione);
		Reporter.log("["+this.tipoCooperazione+"] Controllo ricevuta risposta asincrona asimmetrica con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id,datiServizioAzione));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Mittente Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, datiServizioAzione, this.destinatario, null));
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Destinatario Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, datiServizioAzione, this.mittente, null));
		Reporter.log("Controllo valore OraRegistrazione con id: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Servizio Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizioAzione, datiServizio));
		if(azione!=null){
			Reporter.log("["+this.tipoCooperazione+"] Controllo valore Azione Busta con riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, datiServizioAzione,azione));
		}
		Reporter.log("["+this.tipoCooperazione+"] Controllo valore Profilo di Collaborazione Busta con riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id,datiServizioAzione,this.info.getProfiloCollaborazione_protocollo_asincronoAsimmetrico(),ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
		Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, datiServizioAzione, collaborazione));
		Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, datiServizioAzione, this.confermaRicezione,this.inoltro,this.inoltroSdk));
		Reporter.log("["+this.tipoCooperazione+"] Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("["+this.tipoCooperazione+"] Controllo lista trasmissione, riferimento messaggio: " +id);
		if(tipoTempoAtteso!=null){
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null, true,tipoTempoAtteso, tipoTempoAttesoSdk));
		}else{
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, datiServizioAzione, this.destinatario, null, this.mittente, null));
		}
	}
	public boolean isConfermaRicezione() {
		return this.confermaRicezione;
	}
	public void setConfermaRicezione(boolean confermaRicezione) {
		this.confermaRicezione = confermaRicezione;
	}
	public IDSoggetto getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(IDSoggetto destinatario) {
		this.destinatario = destinatario;
	}
	public String getInoltro() {
		return this.inoltro;
	}
	public void setInoltro(String inoltro) {
		this.inoltro = inoltro;
	}
	public Inoltro getInoltroSdk() {
		return this.inoltroSdk;
	}
	public void setInoltroSdk(Inoltro inoltro) {
		this.inoltroSdk = inoltro;
	}
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	
}
