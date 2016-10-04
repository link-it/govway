/*
 * OpenSPCoop - Customizable API Gateway 
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




package org.openspcoop2.protocol.spcoop.testsuite.units;


import java.util.Date;

import javax.xml.soap.SOAPException;
//import org.apache.axis.AxisFault;
import org.apache.axis.Message;


import org.openspcoop2.testsuite.clients.ClientHttpGenerico;

import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;

import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;

import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.utils.date.DateManager;

import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Test sul filtro duplicati
 *  
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FiltroDuplicatiEGov {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "FiltroDuplicatiEGov";

	// NOTA: la registrazione per il filtro egov di risposta viene verificato in ProfiliDiCollaborazioneLineeGuida11
	

	private static final boolean CONFERMA_RICEZIONE_FALSE = false;
	private static final boolean CONFERMA_RICEZIONE_TRUE = true;
	private static final boolean SCADENZA_FALSE = false;
	
	private static final boolean CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA = true;
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	
	
	
	
	private void runClient(Message msg,boolean attesoErrore,int codiceTrasporto, boolean attesaRispostaXML,Repository repository)throws SOAPException, Exception{
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(repository);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();				
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			Reporter.log("Invocazione (atteso errore: "+attesoErrore+")....");
			client.run();
			Reporter.log("Invocazione (atteso errore: "+attesoErrore+") ok");
			Reporter.log("CODICE: "+client.getCodiceStatoHTTP());
			
			if(attesoErrore){
				Assert.assertEquals(codiceTrasporto, client.getCodiceStatoHTTP());
			}
			else{
				Assert.assertEquals(codiceTrasporto, client.getCodiceStatoHTTP());
			}
			
			if(!attesaRispostaXML){
				if(client.getResponseMessage()!=null && client.getResponseMessage().getSOAPBody()!=null){
					// Se c'e' un body e' permesso solo un fault
					Assert.assertTrue(client.getResponseMessage().getSOAPBody().hasFault() ||
							client.getResponseMessage().getSOAPBody().hasChildNodes()==false);
				}
				if(client.getMessaggioXMLRisposta()!=null){
					throw new Exception("Risposta come byte[] ??");
				}
			}
			else{
				if(client.getResponseMessage()==null){
					throw new Exception("Risposta is null?");
				}
				if(client.getResponseMessage().getSOAPBody()==null){
					throw new Exception("Risposta.getSOAPBody() is null?");
				}
				Assert.assertTrue(client.getResponseMessage().getSOAPBody().hasChildNodes());
				Assert.assertTrue(client.getResponseMessage().getSOAPBody().hasFault()==false);
			}
			
		}catch(Exception e){
			Reporter.log("Invocazione (atteso errore: "+attesoErrore+") error: "+e.getMessage());
			if(attesoErrore==false)
				throw e;
			else
				return;
		}finally{
			dbComponentErogatore.close();
		}
		
		if(attesoErrore)
			throw new Exception("Errore atteso di busta duplicata non avvenuto");
	}
	
	
	
	

	
	
	// ---------------- CASO 1a di Sbustamento -------------------------------

	/**
	 * Consegna con filtro duplicati e riscontri stateful Oneway
	 */
	Repository repositoryFiltroDuplicatiStateful_OnewayConRiscontri=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY_CON_RISCONTRO"})
	public void filtroDuplicatiStateful_OnewayConRiscontri()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_TRUE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL_CON_RISCONTRO);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}else{
			this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}
		// istanza duplicata per messaggio ancora in processamento
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}else{
			this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}else{
			this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}else{
			this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri);
		}
	}
	@DataProvider (name="filtroDuplicatiStateful_OnewayConRiscontri")
	public Object[][]testFiltroDuplicatiStateful_OnewayConRiscontri()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_OnewayConRiscontri.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY_CON_RISCONTRO"},
			dataProvider="filtroDuplicatiStateful_OnewayConRiscontri",dependsOnMethods="filtroDuplicatiStateful_OnewayConRiscontri")
	public void testDBFiltroDuplicatiStateful_OnewayConRiscontri(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio ));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL_CON_RISCONTRO));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo che la busta abbia generato un riscontro per l'id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro(id,null));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati e riscontri stateless Oneway
	 */
	Repository repositoryFiltroDuplicatiStateless_OnewayConRiscontri=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY_CON_RISCONTRO"})
	public void filtroDuplicatiStateless_OnewayConRiscontri()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_TRUE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS_CON_RISCONTRO);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri);
	}
	@DataProvider (name="filtroDuplicatiStateless_OnewayConRiscontri")
	public Object[][]testFiltroDuplicatiStateless_OnewayConRiscontri()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_OnewayConRiscontri.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY_CON_RISCONTRO"},
			dataProvider="filtroDuplicatiStateless_OnewayConRiscontri",dependsOnMethods="filtroDuplicatiStateless_OnewayConRiscontri")
	public void testDBFiltroDuplicatiStateless_OnewayConRiscontri(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS_CON_RISCONTRO));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo che la busta abbia generato un riscontro per l'id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro(id,null));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 1b di Sbustamento -------------------------------
	
	/**
	 * Consegna con filtro duplicati stateful Oneway Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_Oneway_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_Oneway_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}else{
			this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}else{
			this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}else{
			this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida);
		}
	}
	@DataProvider (name="filtroDuplicatiStateful_Oneway_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_Oneway_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_Oneway_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_Oneway_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_Oneway_LineeGuida")
	public void testDBFiltroDuplicatiStateful_Oneway_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 6 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==6);
			}else{
				Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==3);
			}
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless Oneway Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_Oneway_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_Oneway_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_Oneway_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_Oneway_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_Oneway_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_Oneway_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_Oneway_LineeGuida")
	public void testDBFiltroDuplicatiStateless_Oneway_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio)) ;
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 2 di Sbustamento -------------------------------
	

	/**
	 * Consegna con filtro duplicati stateful Sincrono
	 */
	Repository repositoryFiltroDuplicatiStateful_Sincrono=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_SINCRONO"})
	public void filtroDuplicatiStateful_Sincrono()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_Sincrono.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_Sincrono);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono);
	}
	@DataProvider (name="filtroDuplicatiStateful_Sincrono")
	public Object[][]testFiltroDuplicatiStateful_Sincrono()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_Sincrono.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_SINCRONO"},
			dataProvider="filtroDuplicatiStateful_Sincrono",dependsOnMethods="filtroDuplicatiStateful_Sincrono")
	public void testDBFiltroDuplicatiStateful_Sincrono(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless Sincrono 
	 */
	Repository repositoryFiltroDuplicatiStateless_Sincrono=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_SINCRONO"})
	public void filtroDuplicatiStateless_Sincrono()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_Sincrono.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_Sincrono);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono);
	}
	@DataProvider (name="filtroDuplicatiStateless_Sincrono")
	public Object[][]testFiltroDuplicatiStateless_Sincrono()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_Sincrono.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_SINCRONO"},
			dataProvider="filtroDuplicatiStateless_Sincrono",dependsOnMethods="filtroDuplicatiStateless_Sincrono")
	public void testDBFiltroDuplicatiStateless_Sincrono(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	/**
	 * Consegna con filtro duplicati stateful Sincrono Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_SINCRONO_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_Sincrono_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateful_Sincrono_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_Sincrono_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_Sincrono_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_SINCRONO_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_Sincrono_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_Sincrono_LineeGuida")
	public void testDBFiltroDuplicatiStateful_Sincrono_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless Sincrono Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_SINCRONO_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_Sincrono_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_Sincrono_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_Sincrono_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_Sincrono_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_SINCRONO_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_Sincrono_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_Sincrono_LineeGuida")
	public void testDBFiltroDuplicatiStateless_Sincrono_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 3 Asincrono Simmetrico di Sbustamento con ricevuta asincrona abilitata -------------------------------
	

	/**
	 * Consegna con filtro duplicati stateful AsincronoSimmetrico
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA"})
	public void filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		//		 prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}
			else{
				this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			
			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata")
	public Object[][]testFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA"},
			dataProvider="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata",dependsOnMethods="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata")
	public void testDBFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 6 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==6);
			}else{
				Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==3);
			}
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoSimmetrico 
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA"})
	public void filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata")
	public Object[][]testFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA"},
			dataProvider="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata",dependsOnMethods="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata")
	public void testDBFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	/**
	 * Consegna con filtro duplicati stateful AsincronoSimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}
			else{
				this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			
			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 6 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==6);
			}else{
				Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==3);
			}
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoSimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaAbilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 3 Asincrono Simmetrico di Sbustamento con ricevuta asincrona disabilitata -------------------------------
	

	/**
	 * Consegna con filtro duplicati stateful AsincronoSimmetrico
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA"})
	public void filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata")
	public Object[][]testFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA"},
			dataProvider="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata",dependsOnMethods="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata")
	public void testDBFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 8 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==8);
			}else{
				Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==4);
			}
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoSimmetrico 
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA"})
	public void filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata")
	public Object[][]testFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA"},
			dataProvider="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata",dependsOnMethods="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata")
	public void testDBFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
			Assert.assertTrue(countRicevute==4);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	/**
	 * Consegna con filtro duplicati stateful AsincronoSimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateful_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 8 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==8);
			}else{
				Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==4);
			}
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoSimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_SIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateless_AsincronoSimmetrico_RicevutaDisabilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_SIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_SIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
			Assert.assertTrue(countRicevute==4);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	//	 ---------------- CASO 3 Asincrono Asimmetrico di Sbustamento con ricevuta asincrona abilitata -------------------------------
	

	/**
	 * Consegna con filtro duplicati stateful AsincronoAsimmetrico
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA"})
	public void filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}
			else{
				this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			
			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata")
	public Object[][]testFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA"},
			dataProvider="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata",dependsOnMethods="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata")
	public void testDBFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 6 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==6);
			}else{
				Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==3);
			}
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoAsimmetrico 
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA"})
	public void filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata")
	public Object[][]testFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA"},
			dataProvider="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata",dependsOnMethods="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata")
	public void testDBFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	/**
	 * Consegna con filtro duplicati stateful AsincronoAsimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());
		
		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}
			else{
				this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			
			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}else{
				this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 6 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==6);
			}else{
				Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
				Assert.assertTrue(countEccezioni==3);
			}
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoAsimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,true,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,true,500,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_SINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaAbilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_SINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countEccezioni = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Controllo che la busta abbia generato 3 eccezioni, id: " +id+" , trovate: "+countEccezioni);
			Assert.assertTrue(countEccezioni==3);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 3 Asincrono Asimmetrico di Sbustamento con ricevuta asincrona disabilitata -------------------------------
	

	/**
	 * Consegna con filtro duplicati stateful AsincronoAsimmetrico
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA"})
	public void filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata")
	public Object[][]testFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA"},
			dataProvider="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata",dependsOnMethods="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata")
	public void testDBFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 8 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==8);
			}else{
				Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==4);
			}
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoAsimmetrico 
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA"})
	public void filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				null,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata")
	public Object[][]testFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA"},
			dataProvider="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata",dependsOnMethods="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata")
	public void testDBFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
			Assert.assertTrue(countRicevute==4);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	/**
	 * Consegna con filtro duplicati stateful AsincronoAsimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		DatabaseComponent db = null;
		try{
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				String id = this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
				this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.add(id);
				db = DatabaseProperties.getDatabaseComponentErogatore();
				// Elimino invio in corso della ricevuta asincrona. 
				// Tale comportamente rimane in loop poichè siamo in pdd loopback
				// Inoltre la PdD Mittente in realta non conosce la comunicazione asincrona, 
				// poichè questo test invia direttamente la busta al servizio openspcoop/PA
				// Per questo la PdD Mittente segnala le eccezioni Servizio e RiferimentoMessaggio.
				try{
					Thread.sleep(5000);
				}catch(InterruptedException e){}
				db.getVerificatoreMessaggi().deleteMessageByRiferimentoMessaggio(id, Costanti.OUTBOX, false);
			}

			// istanza duplicata per messaggio ancora in processamento
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			// istanza duplicata per history
			try{
				Thread.sleep(2000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}
			// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
			try{
				Thread.sleep(5000);
			}catch(InterruptedException e){}
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}else{
				this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
			}
		}finally{
			try{
				if(db!=null)
					db.close();
			}catch(Exception e){}
		}	
	}
	@DataProvider (name="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateful_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
				Reporter.log("Controllo che la busta abbia generato 8 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==8);
			}else{
				Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
				Assert.assertTrue(countRicevute==4);
			}
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	


	
	
	/**
	 * Consegna con filtro duplicati stateless AsincronoAsimmetrico Linee Guida
	 */
	Repository repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"})
	public void filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS,
				egov,SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,200,false,this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida);
	}
	@DataProvider (name="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida")
	public Object[][]testFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ASINCRONO_ASIMMETRICO_RICEVUTA_ASINCRONA_LINEE_GUIDA"},
			dataProvider="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida",dependsOnMethods="filtroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida")
	public void testDBFiltroDuplicatiStateless_AsincronoAsimmetrico_RicevutaDisabilitata_LineeGuida(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_MODALITA_ASINCRONA_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ASINCRONO_ASIMMETRICO, ProfiloDiCollaborazione.ASINCRONO_ASIMMETRICO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			int  countRicevute = data.getVerificatoreTracciaRisposta().countTracce(id);
			Reporter.log("Controllo che la busta abbia generato 4 ricevute, id: " +id+" , trovate: "+countRicevute);
			Assert.assertTrue(countRicevute==4);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Elimino utilizzo profilo di collaborazione per id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//	 ---------------- CASO 4 con solo segnalazione dell'errore di Sbustamento -------------------------------
	
	/**
	 * Consegna con filtro duplicati stateful Oneway
	 */
	Repository repositoryFiltroDuplicatiStateful_Oneway=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY"})
	public void filtroDuplicatiStateful_Oneway()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateful_Oneway.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateful_Oneway);
	}
	@DataProvider (name="filtroDuplicatiStateful_Oneway")
	public Object[][]testFiltroDuplicatiStateful_Oneway()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateful_Oneway.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATEFUL_ONEWAY"},
			dataProvider="filtroDuplicatiStateful_Oneway",dependsOnMethods="filtroDuplicatiStateful_Oneway")
	public void testDBFiltroDuplicatiStateful_Oneway(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATEFUL));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	

	
	
	/**
	 * Consegna con filtro duplicati e riscontri stateless Oneway
	 */
	Repository repositoryFiltroDuplicatiStateless_Oneway=new Repository();
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY"})
	public void filtroDuplicatiStateless_Oneway()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryFiltroDuplicatiStateless_Oneway.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				FiltroDuplicatiEGov.CONFERMA_RICEZIONE_FALSE,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				FiltroDuplicatiEGov.SCADENZA_FALSE,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		
		// prima istanza
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateless_Oneway);
		// istanza duplicata per messaggio ancora in processamento
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateless_Oneway);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateless_Oneway);
		// ri-testo istanza duplicata per history, per verificare bug che cancella dati di history dopo aver generato l'errore
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		this.runClient(msg,false,Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse(),false,this.repositoryFiltroDuplicatiStateless_Oneway);
	}
	@DataProvider (name="filtroDuplicatiStateless_Oneway")
	public Object[][]testFiltroDuplicatiStateless_Oneway()throws Exception{
		String id=this.repositoryFiltroDuplicatiStateless_Oneway.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={FiltroDuplicatiEGov.ID_GRUPPO,FiltroDuplicatiEGov.ID_GRUPPO+".STATELESS_ONEWAY"},
			dataProvider="filtroDuplicatiStateless_Oneway",dependsOnMethods="filtroDuplicatiStateless_Oneway")
	public void testDBFiltroDuplicatiStateless_Oneway(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo che la busta sia registrata nell'history (id "+id+")");
			Assert.assertTrue(data.getVerificatoreMessaggi().isBustaRegistrataHistory(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni()));
			long duplicati = data.getVerificatoreMessaggi().getNumeroDuplicatiRicevuti(Costanti.INBOX, id, CONTROLLO_REGISTRAZIONE_HISTORY_BUSTE_RICHIESTA,
					Utilities.testSuiteProperties.isUseTransazioni());
			Reporter.log("Controllo numero duplicati attesi = 3 per  id: " +id+" , trovati: "+duplicati);
			Assert.assertEquals(3, duplicati);
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}


