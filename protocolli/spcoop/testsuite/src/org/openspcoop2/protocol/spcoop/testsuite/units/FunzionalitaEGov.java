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




package org.openspcoop2.protocol.spcoop.testsuite.units;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

//import org.apache.axis.AxisFault;
import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Test sulle funzionalita' E-Gov
 *  
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FunzionalitaEGov {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "FunzionalitaEGov";


	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	

	/**
	 * Test per la Consegna affidabile
	 */
	Repository repositoryConsegnaAffidabile=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabile"})
	public void ConsegnaAffidabile() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaAffidabile);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_COOPERAZIONE_AFFIDABILE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="ConsegnaAffidabile")
	public Object[][]testConsegna()throws Exception{
		String id=this.repositoryConsegnaAffidabile.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabile"},dataProvider="ConsegnaAffidabile",dependsOnMethods="ConsegnaAffidabile")
	public void testDBConsegnaAffidabile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * Consegna con filtro duplicati
	 */
	Repository repositoryConsengaConFiltroDuplicati=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaConFiltroDuplicati"})
	public void ConsegnaConFiltroDuplicati()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryConsengaConFiltroDuplicati.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI);

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
		runClient(msg);
		// istanza duplicata per messaggio ancora in processamento
		runClient(msg);
		// istanza duplicata per history
		try{
			Thread.sleep(2000);
		}catch(InterruptedException e){}
		runClient(msg);
	}
	@DataProvider (name="ConsegnaConFiltroDuplicati")
	public Object[][]testFiltroDuplicati()throws Exception{
		String id=this.repositoryConsengaConFiltroDuplicati.getNext();
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
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaConFiltroDuplicati"},dataProvider="ConsegnaConFiltroDuplicati",dependsOnMethods="ConsegnaConFiltroDuplicati")
	public void testDBFiltroDuplicati(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_FILTRO_DUPLICATI));
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
	private void runClient(Message msg)throws SOAPException, Exception{
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConsengaConFiltroDuplicati);
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
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}





	/**
	 * Consegna affidabile con filtro duplicati
	 */
	Repository repositoryConsegnaAffidabileConFiltroDuplicati=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabileConFiltroDuplicati"})
	public void ConsegnaAffidabileConFiltroDuplicati()throws TestSuiteException,SOAPException, Exception{
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryConsegnaAffidabileConFiltroDuplicati.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				true,
				SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
				false,
				CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_FILTRO_DUPLICATI);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConsegnaAffidabileConFiltroDuplicati);
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
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="ConsegnaAffidabileConFiltroDuplicati")
	public Object[][] testConsegnaAffidabileConFiltroDuplicati()throws Exception{
		String id=this.repositoryConsegnaAffidabileConFiltroDuplicati.getNext();
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
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabileConFiltroDuplicati"},
			dataProvider="ConsegnaAffidabileConFiltroDuplicati",
			dependsOnMethods="ConsegnaAffidabileConFiltroDuplicati")
			public void testDBAffidabileConFiltroDuplicati(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_FILTRO_DUPLICATI));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
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
	 * Cooperazione con idegov che porta ora registrazione scaduta
	 */
	Repository repositoryBustaConOraRegistrazioneIntoIDEGovScaduta=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".BustaOraRegistrazioneIntoIDEGovScaduta"})
	public void bustaConOraRegistrazioneIntoIDEGovScaduta()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		long MILLISECONDI_SCADENZA = 500;
		Date dataScaduta = new Date(DateManager.getTimeMillis()-(7200*60*1000)-(MILLISECONDI_SCADENZA));
		// build idEgov con data scaduta
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO,
				dataScaduta);
		
		this.repositoryBustaConOraRegistrazioneIntoIDEGovScaduta.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaConOraRegistrazioneIntoIDEGovScaduta);
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
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con busta scaduta.");
					throw new TestSuiteException("Invocazione PA con busta scaduta, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}

		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("più vecchia della data minima attesa");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="bustaConOraRegistrazioneIntoIDEGovScaduta")
	public Object[][] testBustaConOraRegistrazioneIntoIDEGovScaduta()throws Exception{
		String id=this.repositoryBustaConOraRegistrazioneIntoIDEGovScaduta.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){}
		}	
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".BustaOraRegistrazioneIntoIDEGovScaduta"},
			dataProvider="bustaConOraRegistrazioneIntoIDEGovScaduta",
			dependsOnMethods="bustaConOraRegistrazioneIntoIDEGovScaduta")
			public void testDBBustaConOraRegistrazioneIntoIDEGovScaduta(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MESSAGGIO_SCADUTO + " rappresentante una Busta e-Gov scaduta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.MESSAGGIO_SCADUTO));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Cooperazione con ora registrazione scaduta
	 */
	Repository repositoryBustaConOraRegistrazioneScaduta=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".BustaOraRegistrazioneScaduta"})
	public void bustaConOraRegistrazioneScaduta()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryBustaConOraRegistrazioneScaduta.add(egov);
		long MILLISECONDI_SCADENZA = 500;
		Date oraRegistrazioneScaduta = new Date(DateManager.getTimeMillis()-(7200*60*1000)-(MILLISECONDI_SCADENZA));
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,
				null,
				null,
				SPCoopCostanti.TIPO_TEMPO_SPC,oraRegistrazioneScaduta);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaConOraRegistrazioneScaduta);
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
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con busta scaduta.");
					throw new TestSuiteException("Invocazione PA con busta scaduta, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}


		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("più vecchia della data minima attesa");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="bustaConOraRegistrazioneScaduta")
	public Object[][] testBustaConOraRegistrazioneScaduta()throws Exception{
		String id=this.repositoryBustaConOraRegistrazioneScaduta.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){}
		}	
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".BustaOraRegistrazioneScaduta"},
			dataProvider="bustaConOraRegistrazioneScaduta",
			dependsOnMethods="bustaConOraRegistrazioneScaduta")
			public void testDBBustaConOraRegistrazioneScaduta(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MESSAGGIO_SCADUTO + " rappresentante una Busta e-Gov scaduta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.MESSAGGIO_SCADUTO));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	




	/**
	 * Cooperazione affidabile con scadenza
	 */
	Repository repositoryConsegnaAffidabileConScadenza=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabileConScadenza"})
	public void ConsegnaAffidabileConScadenza()throws TestSuiteException, SOAPException, Exception{
		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryConsegnaAffidabileConScadenza.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				true,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				true,
				CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_SCADENZA);

		// costruzione busta
		java.io.ByteArrayInputStream bin =
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConsegnaAffidabileConScadenza);
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
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con busta scaduta.");
					throw new TestSuiteException("Invocazione PA con busta scaduta, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}


	}
	@DataProvider (name="ConsegnaAffidabileConScadenza")
	public Object[][] testConsegnaAffidabileConScadenza()throws Exception{
		String id=this.repositoryConsegnaAffidabileConScadenza.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){}
		}	
		try{
			Thread.sleep(1000);
		}catch(InterruptedException e){}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaAffidabileConScadenza"},
			dataProvider="ConsegnaAffidabileConScadenza",
			dependsOnMethods="ConsegnaAffidabileConScadenza")
			public void testDBAffidabileConScadenza(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_AFFIDABILE_CON_SCADENZA));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MESSAGGIO_SCADUTO + " rappresentante una Busta e-Gov scaduta");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.MESSAGGIO_SCADUTO));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	/**
	 * Cooperazione con id di collaborazione
	 */
	Repository repositoryCooperazioneConIDDiCollaborazione=new Repository();
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".CooperazioneConIDDiCollaborazione"})
	public void CooperazioneConIDDiCollaborazione() throws TestSuiteException, IOException{

		ClientSincrono client=new ClientSincrono(this.repositoryCooperazioneConIDDiCollaborazione);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_COOPERAZIONE_CON_ID_DI_COLLABORAZIONE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
		client.run();
	}
	@DataProvider (name="CooperazioneConIDDiCollaborazione")
	public Object[][] testCooperazioneConIDDiCollaborazione()throws Exception{
		String id=this.repositoryCooperazioneConIDDiCollaborazione.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".CooperazioneConIDDiCollaborazione"},
			dataProvider="CooperazioneConIDDiCollaborazione",
			dependsOnMethods="CooperazioneConIDDiCollaborazione")         
			public void testDBCooperazioneConIDDiCollaborazione(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id); 
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);  
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE));                
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id,SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo presenza IDCollaborazione nella Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, id));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
			Reporter.log("----------------------------------------------------------");

			Reporter.log("Controllo tracciamento risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Destinatario Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_COLLABORAZIONE));                
			Reporter.log("Controllo valore Profilo di Collaborazione Busta della risposta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Controllo presenza IDCollaborazione nella Busta con riferimento id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, id));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione, riferimento messaggio: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	// TEST
	// 0. Reset tabelle consegna in ordine
	// 1. Pubblico 1 messaggio, verifico che idegov=idcollaborazione e sequenza=1
	//       Verifico che sequenzaAttesa e nextSequenza sia 2
	// 2. Pubblico messaggio, verifico che idegov!=idcollaborazione, ma idegovprecedente=idcollaborazione e sequenza=2
	//       Verifico che sequenzaAttesa e nextSequenza sia 2
	// 3. Pubblico messaggio, verra' attuato lo sleep della testsuite...
	//	  Pubblico immediato altro messaggio, verifico che idegov1=idcollaborazione e sequenza=4
	//	  Verifico anche congelamento per msg sequenza = 4
	//	  Sleep(120)
	//	  Verifico che messaggio con sequenza 3 sia consegnato
	//	  Verifico che messaggio con sequenza 4 sia consegnato
	// 4.Altero sequenza attesa incrementandola di uno
	//   Pubblico messaggio, questo verra' scartato poiche' in ordine minore di quello atteso.
	//   rimetto apposto sequenza attesa.
	// 5. Test finale che tutto vada.
	
	Repository repositoryConsegnaInOrdine=new Repository();
	String collaborazione = null;
	boolean doTest = true;
	
	/**
	 * Test per Consegna in ordine
	 * 0. Reset tabelle consegna in ordine
	 */	
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"})
	public void consegnaInOrdine_test0() throws TestSuiteException, Exception{
		
		try{
			String version_jbossas = Utilities.readApplicationServerVersion();
			if(version_jbossas.startsWith("tomcat")){
				System.out.println("WARNING: Verifiche di ConsegnaInOrdine disabilitate per Tomcat");
				this.doTest = false;
			}
		}catch(Exception e){
			System.err.println("Comprensione A.S. non riuscita: "+e.getMessage());
			e.printStackTrace(System.out);
		}
		
		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
			dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			dbComponentFruitore.getVerificatoreMessaggi().resetSequenzeDaInviare();
			dbComponentErogatore.getVerificatoreMessaggi().resetSequenzeDaRicevere();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	/**
	 * Test 1.
	 * 1. Pubblico 1 messaggio, verifico che idegov=idcollaborazione e sequenza=1
	 *       Verifico che sequenzaAttesa e nextSequenza sia 2
	 * @throws TestSuiteException
	 * @throws Exception
	 */
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},dependsOnMethods="consegnaInOrdine_test0")
	public void consegnaInOrdine_test1() throws TestSuiteException, Exception{
		
		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="providerConsegnaInOrdine_test1")
	public Object[][]providerConsegnaInOrdine_test1()throws Exception{
			
		if(!this.doTest){
			return new Object[][]{
					{null,null,false}
				};
		}
		
		String id=this.repositoryConsegnaInOrdine.getNext();
		
		// Assegno collaborazione
		this.collaborazione = id;
		
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="providerConsegnaInOrdine_test1",dependsOnMethods="consegnaInOrdine_test1")
	public void testDBConsegnaInOrdine_test1(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		
		if(!this.doTest){
			return;
		}
		
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
			
			// Check sequenza
			
			Reporter.log("Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, this.collaborazione));
			Reporter.log("Controllo valore Sequenza (1) Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(id, 1));
						
						
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 2 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==2);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 2 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==2);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	/**
	 * Test .2
	 * 2. Pubblico messaggio, verifico che idegov!=idcollaborazione, ma idegovprecedente=idcollaborazione e sequenza=2
	 *       Verifico che sequenzaAttesa e nextSequenza sia 3
	 * @throws TestSuiteException
	 * @throws Exception
	 */
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},dependsOnMethods="testDBConsegnaInOrdine_test1")
	public void consegnaInOrdine_test2() throws TestSuiteException, Exception{
		
		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="providerConsegnaInOrdine_test2")
	public Object[][]providerConsegnaInOrdine_test2()throws Exception{
		
		if(!this.doTest){
			return new Object[][]{
					{null,null,false}
				};
		}
		
		String id=this.repositoryConsegnaInOrdine.getNext();
		
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="providerConsegnaInOrdine_test2",dependsOnMethods="consegnaInOrdine_test2")
	public void testDBConsegnaInOrdine_test2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		
		if(!this.doTest){
			return;
		}
		
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
			
			// Check sequenza
			
			Reporter.log("Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, this.collaborazione));
			Reporter.log("Controllo valore Sequenza (2) Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(id, 2));
						
						
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 3 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==3);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 3 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==3);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	/**
	 * Test .3
	 * 3. Pubblico messaggio, verra' attuato lo sleep della testsuite...
	 *	  Pubblico immediato altro messaggio, verifico che idegov1=idcollaborazione e sequenza=4
	 *	  Verifico anche congelamento per msg sequenza = 4
	 *	  Sleep(120)
	 *	  Verifico che messaggio con sequenza 3 sia consegnato
	 *	  Verifico che messaggio con sequenza 4 sia consegnato
	 *
	 * @throws TestSuiteException
	 * @throws Exception
	 */
	Repository repositoryConsegnaInOrdinePubblicazioneFuoriOrdine=new Repository();
	
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},dependsOnMethods="testDBConsegnaInOrdine_test2")
	public void consegnaInOrdine_test3() throws TestSuiteException, Exception{
		
		if(!this.doTest){
			return;
		}
		
		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setAttesaTerminazioneMessaggi(false);
			client.run();
		}catch(Exception e){
			throw e;
		}
		
		try{
			Thread.sleep(3000);
		}catch(Exception e){}
		
		// Pubblicazione fuori ordine
		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdinePubblicazioneFuoriOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			client.setAttesaTerminazioneMessaggi(false);
			client.run();
		}catch(Exception e){
			throw e;
		}
	}
	@DataProvider (name="providerConsegnaInOrdine_test3")
	public Object[][]providerConsegnaInOrdine_test3()throws Exception{
		
		if(!this.doTest){
			return new Object[][]{
					{null,null,null,null,false}
				};
		}
		
		String idSequenza3=this.repositoryConsegnaInOrdine.getNext();
		String idSequenza4=this.repositoryConsegnaInOrdinePubblicazioneFuoriOrdine.getNext();

		// Attendo fine gestione messaggio congelato
		try{
			Thread.sleep(5000);
		}catch(InterruptedException e){}

		this.repositoryConsegnaInOrdine.add(idSequenza3);
		this.repositoryConsegnaInOrdinePubblicazioneFuoriOrdine.add(idSequenza4);
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),idSequenza3,idSequenza4,true},
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),idSequenza3,idSequenza4,false}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="providerConsegnaInOrdine_test3",dependsOnMethods="consegnaInOrdine_test3")
	public void testDBConsegnaInOrdine_test3(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String idSequenza3, String idSequenza4,boolean checkServizioApplicativo) throws Exception{

		if(!this.doTest){
			return;
		}
		
		try{
			
			// Sequenza 3
			Reporter.log("(Sequenza 3) Controllo tracciamento richiesta con id non effettuato: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(idSequenza3)==false);
			
			// Sequenza 4
			Reporter.log("(Sequenza 4) Controllo tracciamento richiesta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(idSequenza4));
			if(checkServizioApplicativo){
				// check congelamento
				Reporter.log("(Sequenza 4) Controllo messaggio in processamento (congelato) id: " +idSequenza4);
				Assert.assertTrue(data.getVerificatoreMessaggi().existsMessaggioInProcessamento(idSequenza4, Costanti.INBOX));
				String motivoErroreProcessamento = data.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(idSequenza4, Costanti.INBOX);
				Reporter.log("(Sequenza 4) Controllo motivo messaggio in processamento (congelato) id: " +idSequenza4 +"  ["+motivoErroreProcessamento+"]");
				Assert.assertTrue(motivoErroreProcessamento.endsWith("viene effettuato il suo congelamento fino all'arrivo dei messaggi che lo precedono"));
				// Check msg diagnostico
				Reporter.log("(Sequenza 4) Controllo messaggio diagnostico emesso per congelamento) id: " +idSequenza4);
				Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggioWithLike(idSequenza4, "viene effettuato il suo congelamento fino all'arrivo dei messaggi che lo precedono"));
			}
				
			// Database
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 3 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==3);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 5 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==5);
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				dataMsgDiagnostici.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="testDBConsegnaInOrdine_test3_dopoScongelamento")
	public Object[][]testDBConsegnaInOrdine_test3_dopoScongelamento()throws Exception{
		
		if(!this.doTest){
			return new Object[][]{
					{null,null,null,false}
				};
		}
		
		String idSequenza3=this.repositoryConsegnaInOrdine.getNext();
		String idSequenza4=this.repositoryConsegnaInOrdinePubblicazioneFuoriOrdine.getNext();

		// Aspetto scongelamento
		try{
			Thread.sleep(130000);
		}catch(Exception e){}
		

		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),idSequenza3,idSequenza4,true},
				{DatabaseProperties.getDatabaseComponentFruitore(),idSequenza3,idSequenza4,false}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="testDBConsegnaInOrdine_test3_dopoScongelamento",dependsOnMethods="testDBConsegnaInOrdine_test3")
	public void testDBConsegnaInOrdine_test3_dopoScongelamento(DatabaseComponent data,String idSequenza3, String idSequenza4,boolean checkServizioApplicativo) throws Exception{

		if(!this.doTest){
			return;
		}
		
		try{
			
			// Verifica Consegna sequenza 3
			Reporter.log("(Sequenza 3) Controllo tracciamento richiesta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(idSequenza3));
			Reporter.log("(Sequenza 3) Controllo valore Mittente Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(idSequenza3,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("(Sequenza 3) Controllo valore Destinatario Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(idSequenza3,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(idSequenza3));
			Reporter.log("(Sequenza 3) Controllo valore Servizio Busta con id: " +idSequenza3);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(idSequenza3, datiServizio));
			Reporter.log("(Sequenza 3) Controllo valore Azione Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(idSequenza3, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("(Sequenza 3) Controllo valore Profilo di Collaborazione Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(idSequenza3, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("(Sequenza 3) Controllo Riscontro con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(idSequenza3,null));
			Reporter.log("(Sequenza 3) Controllo che la busta non abbia generato eccezioni, id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(idSequenza3)==false);
			Reporter.log("(Sequenza 3) Controllo lista trasmissione con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(idSequenza3, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			if(checkServizioApplicativo){
				Reporter.log("(Sequenza 3) Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(idSequenza3));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSequenza3)==1);
			}
			Reporter.log("(Sequenza 3) Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(idSequenza3, this.collaborazione));
			Reporter.log("(Sequenza 3) Controllo valore Sequenza (3) Busta con id: " +idSequenza3);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(idSequenza3, 3));
			
			
			// Verifica Consegna sequenza 4
			Reporter.log("(Sequenza 4) Controllo tracciamento richiesta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(idSequenza4));
			Reporter.log("(Sequenza 4) Controllo valore Mittente Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(idSequenza4,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("(Sequenza 4) Controllo valore Destinatario Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(idSequenza4,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(idSequenza4));
			Reporter.log("(Sequenza 4) Controllo valore Servizio Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(idSequenza4, datiServizio));
			Reporter.log("(Sequenza 4) Controllo valore Azione Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(idSequenza4, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("(Sequenza 4) Controllo valore Profilo di Collaborazione Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(idSequenza4, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("(Sequenza 4) Controllo Riscontro con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(idSequenza4,null));
			Reporter.log("(Sequenza 4) Controllo che la busta non abbia generato eccezioni, id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(idSequenza4)==false);
			Reporter.log("(Sequenza 4) Controllo lista trasmissione con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(idSequenza4, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			if(checkServizioApplicativo){
				Reporter.log("(Sequenza 4) Numero messaggi arrivati al servizio applicativo ("+idSequenza4+"): "+data.getVerificatoreTracciaRichiesta().isArrivedCount(idSequenza4));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(idSequenza4)==1);
			}
			Reporter.log("(Sequenza 4) Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(idSequenza4, this.collaborazione));
			Reporter.log("(Sequenza 4) Controllo valore Sequenza (4) Busta con id: " +idSequenza4);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(idSequenza4, 4));
			
			
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 5 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==5);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 5 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==5);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	/**
	 * Test .4
	 * 4. Altero sequenza attesa incrementandola di uno
	 *   Pubblico messaggio, questo verra' scartato poiche' in ordine minore di quello atteso.
	 *   rimetto apposto sequenza attesa.
	 * @throws TestSuiteException
	 * @throws Exception
	 */
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},dependsOnMethods="testDBConsegnaInOrdine_test3_dopoScongelamento")
	public void consegnaInOrdine_test4_incrementoSequenzaAttesa() throws TestSuiteException, Exception{

		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentErogatore = null;

		try{
			dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			dbComponentErogatore.getVerificatoreMessaggi().aggiornaSequenzaAttesaDaRicevere(this.collaborazione, 6); // era 5
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"}
		,dependsOnMethods="consegnaInOrdine_test4_incrementoSequenzaAttesa")
	public void consegnaInOrdine_test4() throws TestSuiteException, Exception{
	
		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="providerConsegnaInOrdine_test4")
	public Object[][]providerConsegnaInOrdine_test4()throws Exception{
		
		if(!this.doTest){
			return new Object[][]{
					{null,null,null,false}
				};
		}
		
		String id=this.repositoryConsegnaInOrdine.getNext();
		
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="providerConsegnaInOrdine_test4",dependsOnMethods="consegnaInOrdine_test4")
	public void testDBConsegnaInOrdine_test4(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id,boolean checkServizioApplicativo) throws Exception{

		if(!this.doTest){
			return;
		}
		
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			
			// Check sequenza
			
			Reporter.log("Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, this.collaborazione));
			Reporter.log("Controllo valore Sequenza (5) Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(id, 5));
			
			
			// Check messaggio non consegnato
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			
			if(checkServizioApplicativo){
				// check congelamento non effettuato perche' di ordine minore
				Reporter.log("(Sequenza 4) Controllo non esistenza messaggio in processamento (congelato) id: " +id);
				Assert.assertTrue(data.getVerificatoreMessaggi().existsMessaggioInProcessamento(id, Costanti.INBOX)==false);
				// Check msg diagnostico
				Reporter.log("(Sequenza 4) Controllo messaggio diagnostico emesso per messaggio fuori ordine id: " +id);
				Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggioWithLike(id, "è già stato consegnato al servizio applicativo"));
				Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggioWithLike(id, "è minore della sequenza attesa"));
			}
			
			
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 6 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==6);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 6 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==6);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				dataMsgDiagnostici.close();
			}catch(Exception e){}
		}
	}
	
	/**
	 * Test finale che tutto rifunzioni
	 * @throws TestSuiteException
	 * @throws Exception
	 */
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},dependsOnMethods="testDBConsegnaInOrdine_test4")
	public void consegnaInOrdine_test5() throws TestSuiteException, Exception{

		if(!this.doTest){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(this.repositoryConsegnaInOrdine);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_CONSEGNA_IN_ORDINE);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="providerConsegnaInOrdine_test5")
	public Object[][]providerConsegnaInOrdine_test5()throws Exception{
		
		if(!this.doTest){
			return new Object[][]{
					{null,null,false}
				};
		}
		
		String id=this.repositoryConsegnaInOrdine.getNext();
		
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		// Attendo tempo perso per la nuova connessione
		//if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
		try{
			Thread.sleep(3000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={FunzionalitaEGov.ID_GRUPPO,FunzionalitaEGov.ID_GRUPPO+".ConsegnaInOrdine"},
			dataProvider="providerConsegnaInOrdine_test5",dependsOnMethods="consegnaInOrdine_test5")
	public void testDBConsegnaInOrdine_test5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{

		if(!this.doTest){
			return;
		}
		
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_CONSEGNA_IN_ORDINE));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo Riscontro con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedRiscontro(id,null));
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_TEST_CONSEGNA_IN_ORDINE, null));
			if(checkServizioApplicativo){
				Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			}
			
			// Check sequenza
			
			Reporter.log("Controllo valore Collaborazione ("+this.collaborazione+") Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, this.collaborazione));
			Reporter.log("Controllo valore Sequenza (6) Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedSequenza(id, 6));
						
						
			if(checkServizioApplicativo){
				// erogatore
				Reporter.log("Controllo sequenza attesa sia 7 nel database della porta di dominio erogatore: "+
						data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getSequenzaAttesaDaRicevere(this.collaborazione)==7);
			}else{
				// fruitore
				Reporter.log("Controllo prossima sequenza sia 7 nel database della porta di dominio fruitore: "+
						data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione));
				Assert.assertTrue(data.getVerificatoreMessaggi().getNextSequenzaDaInviare(this.collaborazione)==7);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}


