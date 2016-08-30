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

import java.io.File;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.Message;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.testsuite.core.BusteEGovDaFile;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle buste E-Gov con eccezioni
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGovConEccezioni {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "BusteEGovConEccezioni";

	
	
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
	
	
	
	
	/** Lettura della batteria di test sulle buste eGov errate */
	private BusteEGovDaFile busteEGov = null;



	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWENG",ID_GRUPPO+".SENG",ID_GRUPPO+".OWEAG",ID_GRUPPO+".SEAG",ID_GRUPPO+".OWEAG_FC",ID_GRUPPO+".OWEAG_FC_CDATA",ID_GRUPPO+".OWEAG_FSS",ID_GRUPPO+".OWEAG_FCS"})
	public void  init() throws Exception{
		try{
			File[] dir =  new java.io.File(Utilities.testSuiteProperties.getPathBusteConEccezioni()).listFiles();
			Vector<File> dirV = new Vector<File>();
			for(int i=0; i<dir.length; i++){
				if(".svn".equals(dir[i].getName())==false)
					dirV.add(dir[i]);
			}
			dir = new File[1];
			dir = dirV.toArray(dir);

			this.busteEGov = new BusteEGovDaFile(dir);
		}catch(Exception e){
			Reporter.log("Inizializzazione utility BusteEGovDaFile non riscita: "+e.getMessage());
			throw e;
		}
	}







	/**
	 * 
	 * busta oneway (senza fault) con eccezioni di livello INFO e LIEVE
	 * La busta verra' correttamente processata solo se 'org.openspcoop2.egov.spcoopErrore.ignoraNonGravi.enable=true'
	 * In tal caso viene emesso dalla PdD il seguente msg diagnostico:
	 *  Eccezione INFO con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio
	 *  Eccezione LIEVE con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio 2
	 *
	 * Altrimenti la PdD segnalera' con un msg diagnostico che il msg SPCoop Errore e' malformato.
	 * [EGOV_IT_001] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniNonGravi.xml
	 *
	 *nel primo e secondo caso Il codice eccezione e' diverso si tratta di [EGOV_IT_003]
	 *
	 */
	Repository repositoryOneWayEccezioniNonGravi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWENG"},
			dependsOnMethods="init")
			public void EGovOneWayEccezioniNonGravi()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniNonGravi.xml");
		this.repositoryOneWayEccezioniNonGravi.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniNonGravi);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniNonGravi.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovOneWayEccezioniNonGravi")
	public Object[][] testEGovOneWayEccezioniNonGravi()throws Exception{
		String id=this.repositoryOneWayEccezioniNonGravi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWENG"},
			dataProvider="EGovOneWayEccezioniNonGravi",
			dependsOnMethods="EGovOneWayEccezioniNonGravi")
			public void testDBEGovOneWayEccezioniNonGravi(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniNonGravi.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index), this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString()));


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
	 *
	 * busta oneway (senza fault) con eccezioni di livello INFO e LIEVE
	 * La busta verra' correttamente processata solo se 'org.openspcoop2.egov.spcoopErrore.ignoraNonGravi.enable=true'
	 * In tal caso viene emesso dalla PdD il seguente msg diagnostico:
	 *  Eccezione INFO con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio
	 *  Eccezione LIEVE con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio 2
	 *
	 * Altrimenti la PdD segnalera' con un msg diagnostico che il msg SPCoop Errore e' malformato.
	 * [EGOV_IT_001] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault
	 *busta = buste_con_eccezioni/bustaSincronaEccezioniNonGravi.xml
	 *
	 *nel primo e secondo caso Il codice eccezione e' diverso si tratta di [EGOV_IT_003]
	 *
	 */
	Repository repositorySincronaEccezioniNonGravi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SENG"},
			dependsOnMethods="init")
			public void EGovSincronaEccezioniNonGravi()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaEccezioniNonGravi.xml");

		this.repositorySincronaEccezioniNonGravi.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronaEccezioniNonGravi);
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
			Reporter.log("Invocazione PA con una busta con SincronaEccezioniNonGravi.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovSincronaEccezioniNonGravi")
	public Object[][] testEGovSincronaEccezioniNonGravi()throws Exception{
		String id=this.repositorySincronaEccezioniNonGravi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SENG"},
			dataProvider="EGovSincronaEccezioniNonGravi",
			dependsOnMethods="EGovSincronaEccezioniNonGravi")
			public void testDBEGovSincronaEccezioniNonGravi(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaEccezioniNonGravi.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString()));


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
	 *
	 * busta oneway (senza fault) con eccezioni di livello anche gravi
	 * La porta di dominio segnalera' con un msg diagnostico che il msg SPCoop Errore e' malformato.
	 * [EGOV_IT_001] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniAlcuneGravi.xml
	 *
	 *Il codice eccezione e' diverso si tratta di [EGOV_IT_003]
	 *
	 */
	Repository repositoryOneWayEccezioniAlcuneGravi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG"},
			dependsOnMethods="init")
			public void EGovOneWayEccezioniAlcuneGravi()throws TestSuiteException, SOAPException, Exception{

		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi.xml");
		this.repositoryOneWayEccezioniAlcuneGravi.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniAlcuneGravi);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniAlcuneGravi.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovOneWayEccezioniAlcuneGravi")
	public Object[][] testEGovOneWayEccezioniAlcuneGravi()throws Exception{
		String id=this.repositoryOneWayEccezioniAlcuneGravi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG"},
			dataProvider="EGovOneWayEccezioniAlcuneGravi",
			dependsOnMethods="EGovOneWayEccezioniAlcuneGravi")
			public void testDBEGovOneWayEccezioniAlcuneGravi(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString()));


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
	 *
	 * busta sincrona (senza fault) con eccezioni di livello anche gravi
	 * La porta di dominio segnalera' con un msg diagnostico che il msg SPCoop Errore e' malformato.
	 * [EGOV_IT_001] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault
	 *busta = buste_con_eccezioni/bustaSincronaEccezioniAlcuneGravi.xml
	 *
	 * Il codice eccezione e' diverso si tratta di [EGOV_IT_003]
	 *
	 */
	Repository repositorySincronaEccezioniAlcuneGravi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEAG"},
			dependsOnMethods="init")
			public void EGovSincronaEccezioniAlcuneGravi()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaEccezioniAlcuneGravi.xml");
		this.repositorySincronaEccezioniAlcuneGravi.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronaEccezioniAlcuneGravi);
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
			Reporter.log("Invocazione PA con una busta con SincronaEccezioniAlcuneGravi.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovSincronaEccezioniAlcuneGravi")
	public Object[][] testEGovSincronaEccezioniAlcuneGravi()throws Exception{
		String id=this.repositorySincronaEccezioniAlcuneGravi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEAG"},
			dataProvider="EGovSincronaEccezioniAlcuneGravi",
			dependsOnMethods="EGovSincronaEccezioniAlcuneGravi")
			public void testDBEGovSincronaEccezioniAlcuneGravi(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaEccezioniAlcuneGravi.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString()));

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
	 *
	 * busta oneway (con fault e-Gov correttamente formato) con eccezioni di livello anche gravi
	 * Generato msg diagnostico che elenca le eccezioni portate:
	 *  Eccezione INFO con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio
	 *  Eccezione GRAVE con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio grave
	 *  Eccezione LIEVE con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio 2
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniAlcuneGravi_faultCorretto.xml
	 *
	 */
	Repository repositoryOneWayEccezioniAlcuneGravi_faultCorretto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FC"},
			dependsOnMethods="init")
			public void EGovOneWayEccezioniAlcuneGravi_faultCorretto()throws TestSuiteException, SOAPException, Exception{
			
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCorretto.xml");
		this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniAlcuneGravi_faultCorretto.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovOneWayEccezioniAlcuneGravi_faultCorretto")
	public Object[][] testEGovOneWayEccezioniAlcuneGravi_faultCorretto()throws Exception{
		String id=this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FC"},
			dataProvider="EGovOneWayEccezioniAlcuneGravi_faultCorretto",
			dependsOnMethods="EGovOneWayEccezioniAlcuneGravi_faultCorretto")
			public void testDBEGovOneWayEccezioniAlcuneGravi_faultCorretto(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCorretto.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio"));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio grave"));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_LIEVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio 2"));

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
	 *
	 * busta oneway (con fault e-Gov correttamente formato con CDATA) con eccezioni di livello anche gravi
	 * Generato msg diagnostico che elenca le eccezioni portate:
	 *  Eccezione INFO con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio
	 *  Eccezione GRAVE con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio grave
	 *  Eccezione LIEVE con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte test esempio 2
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniAlcuneGravi_faultCorretto_CDATA.xml
	 *
	 */
	Repository repositoryOneWayEccezioniAlcuneGravi_faultCorretto_CDATA=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FC_CDATA"},
			dependsOnMethods="init")
			public void EGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCorretto_CDATA.xml");
		this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto_CDATA.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto_CDATA);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniAlcuneGravi_faultCorretto_CDATA.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}

	}
	@DataProvider (name="EGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA")
	public Object[][] testEGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA()throws Exception{
		String id=this.repositoryOneWayEccezioniAlcuneGravi_faultCorretto_CDATA.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FC_CDATA"},
			dataProvider="EGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA",
			dependsOnMethods="EGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA")
			public void testDBEGovOneWayEccezioniAlcuneGravi_faultCorretto_CDATA(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCorretto_CDATA.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio"));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio grave"));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_LIEVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Destinatario/IdentificativoParte test esempio 2"));

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
	 *
	 * busta oneway (con fault e-Gov con faultString sbagliato) con eccezioni di livello anche gravi
	 * Generato msg diagnostico che rileva:
	 * eccezione [EGOV_IT_003] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault/FaultString
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniAlcuneGravi_faultStringScorretto.xml
	 *
	 */
	Repository repositoryOneWayEccezioniAlcuneGravi_faultStringScorretto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FSS"},
			dependsOnMethods="init")
			public void EGovOneWayEccezioniAlcuneGravi_faultStringScorretto()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultStringScorretto.xml");
		this.repositoryOneWayEccezioniAlcuneGravi_faultStringScorretto.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniAlcuneGravi_faultStringScorretto);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniAlcuneGravi_faultStringScorretto.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="EGovOneWayEccezioniAlcuneGravi_faultStringScorretto")
	public Object[][] testEGovOneWayEccezioniAlcuneGravi_faultStringScorretto()throws Exception{
		String id=this.repositoryOneWayEccezioniAlcuneGravi_faultStringScorretto.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FSS"},
			dataProvider="EGovOneWayEccezioniAlcuneGravi_faultStringScorretto",
			dependsOnMethods="EGovOneWayEccezioniAlcuneGravi_faultStringScorretto")
			public void testDBEGovOneWayEccezioniAlcuneGravi_faultStringScorretto(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultStringScorretto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_STRING.toString()));

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
	 *
	 * busta oneway (con fault e-Gov con faultCode sbagliato) con eccezioni di livello anche gravi
	 * Generato msg diagnostico che rileva:
	 * eccezione [EGOV_IT_003] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni/SOAPFault/FaultCode
	 *busta = buste_con_eccezioni/bustaOneWayEccezioniAlcuneGravi_faultCodeScorretto.xml
	 *  
	 */
	Repository repositoryOneWayEccezioniAlcuneGravi_faultCodeScorretto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FCS"},
			dependsOnMethods="init")
	public void EGovOneWayEccezioniAlcuneGravi_faultCodeScorretto()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCodeScorretto.xml");
		this.repositoryOneWayEccezioniAlcuneGravi_faultCodeScorretto.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayEccezioniAlcuneGravi_faultCodeScorretto);
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
			Reporter.log("Invocazione PA con una busta con OneWayEccezioniAlcuneGravi_faultCodeScorretto.");

			dbComponentErogatore.close();
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}

	}
	@DataProvider (name="EGovOneWayEccezioniAlcuneGravi_faultCodeScorretto")
	public Object[][] testEGovOneWayEccezioniAlcuneGravi_faultCodeScorretto()throws Exception{
		String id=this.repositoryOneWayEccezioniAlcuneGravi_faultCodeScorretto.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWEAG_FCS"},
			dataProvider="EGovOneWayEccezioniAlcuneGravi_faultCodeScorretto",
			dependsOnMethods="EGovOneWayEccezioniAlcuneGravi_faultCodeScorretto")
			public void testDBEGovOneWayEccezioniAlcuneGravi_faultCodeScorretto(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWayEccezioniAlcuneGravi_faultCodeScorretto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT_CODE.toString()));

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
}


