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

import java.io.File;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoOraRegistrazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.testsuite.core.BusteEGovDaFile;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
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
 * Test sulle buste E-Gov con eccezioni
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGov11LineeGuida11 {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "BusteEGov11LineeGuida11";

	
	
	
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



	@Test(groups={ID_GRUPPO,ID_GRUPPO+".OWENG", ID_GRUPPO+".SENG",
			ID_GRUPPO+".OWEAG",ID_GRUPPO+".SEAG",ID_GRUPPO+".OWEAG_FC", 
			ID_GRUPPO+".OWEAG_FC_CDATA",ID_GRUPPO+".TMDSPC",ID_GRUPPO+".ITM"
			,ID_GRUPPO+".DTDSPC",ID_GRUPPO+".ITD",ID_GRUPPO+".BOWSTS",ID_GRUPPO+".ROWSS",ID_GRUPPO+".TServDSPC",
			ID_GRUPPO+".BOWSAz"
			, ID_GRUPPO+".IDD",ID_GRUPPO+".BSPC",ID_GRUPPO+".BTSC", ID_GRUPPO+".BSC",ID_GRUPPO+".BTESC",ID_GRUPPO+".Collaborazione"
			,ID_GRUPPO+".BCASCC",ID_GRUPPO+".CBASSC",ID_GRUPPO+".TORDSPC",ID_GRUPPO+".CR",ID_GRUPPO+".SFD",ID_GRUPPO+".BSCR"
			,ID_GRUPPO+".BSSFD",ID_GRUPPO+".SEQ",ID_GRUPPO+".SEQs",ID_GRUPPO+".TR",ID_GRUPPO+".BLTTMDSPC",ID_GRUPPO+".BLTTDDSPC"
			,ID_GRUPPO+".BLTORDSPC",ID_GRUPPO+".BLTITM",ID_GRUPPO+".BLTITD",ID_GRUPPO+".BRiscontro",ID_GRUPPO+".RTDSPC", ID_GRUPPO+".SPT",
			ID_GRUPPO+".VerificaReturnCodeOneway"
	})
	
	public void  init() throws Exception{
		try{
			File[] dir =  new java.io.File(Utilities.testSuiteProperties.getPathBusteLineeGuida11()).listFiles();
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





	
	
	
	/* *********** VERIFICA PROFILO ONEWAY (RETURN CODE = 200) ************** */
	
	/**
	 * Il return code di un profilo oneway, deve essere 200 e non 202.
	 * Tale requisito e' stato aggiunto dalla Certificazione DigitPA.
	 * 
	 * Test equivalente al Test N.34 Della Certificazione DigitPA (Busta testOneway)
	 */
	Repository repositoryVerificaReturnCodeOneway=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".VerificaReturnCodeOneway"})
	public void VerificaReturnCodeOneway()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryVerificaReturnCodeOneway.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS);
		bustaSOAP = bustaSOAP.replaceAll(SPCoopCostanti.TIPO_TEMPO_LOCALE, SPCoopCostanti.TIPO_TEMPO_SPC);
		
		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryVerificaReturnCodeOneway);
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
			
			Reporter.log("Verifico return code = "+Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse()+" (trovato:"+client.getCodiceStatoHTTP()+")");
			Assert.assertTrue(client.getCodiceStatoHTTP()==Utilities.testSuiteProperties.getHttpReturnCode_onewayHttpResponse());
			
			Reporter.log("Verifico risposta vuota");
			Assert.assertTrue(client.getResponseMessage()==null);
			
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="testVerificaReturnCodeOneway")
	public Object[][]testVerificaReturnCodeOneway()throws Exception{
		String id=this.repositoryVerificaReturnCodeOneway.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".VerificaReturnCodeOneway"},dataProvider="testVerificaReturnCodeOneway",dependsOnMethods="VerificaReturnCodeOneway")
	public void testVerificaReturnCodeOneway(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().existsListaEccezioni(id)==false);
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11, null,
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11, null,
					true,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			
			Reporter.log("Controllo tracciamento risposta==false con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	

	
	
	/* ********** BUSTE CONTENENTI ECCEZIONI ***************** */
	

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
			Thread.sleep(500);
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo la non esistenza dell'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString())==false);
			
			Reporter.log("controllo l'esistenza dell'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					"Destinatario/IdentificativoParte test esempio"));
			
			Reporter.log("controllo l'esistenza dell'eccezione nei messaggi diagnostici: ");
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
			Thread.sleep(500);
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("controllo la non esistenza dell'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE_SOAP_FAULT.toString())==false);
			
			Reporter.log("controllo l'esistenza dell'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					"Destinatario/IdentificativoParte test esempio"));
			
			Reporter.log("controllo l'esistenza dell'eccezione nei messaggi diagnostici: ");
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
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
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
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


	
	

	
	/* ----------------- BUSTE CHE NON SONO ECCEZIONI ------------------ */
	
	/**
	 * 
	 * Mittente con tipo diverso da SPC
	 * La busta ritornata possiedera' una eccezione GRAVE con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte/tipo
	 * busta = buste_linee_guida_1.1/bustaTipoMittenteDiversoSPC.xml
	 * 
	 */
	Repository repositoryTipoMittenteDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TMDSPC"},
			dependsOnMethods="init")
			public void EGovTipoMittenteDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoMittenteDiversoSPC.xml");

		this.repositoryTipoMittenteDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoMittenteDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovTipoMittenteDiversoSPC")
	public Object[][] testEGovTipoMittenteDiversoSPC()throws Exception{
		String id=this.repositoryTipoMittenteDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TMDSPC"},
			dataProvider="EGovTipoMittenteDiversoSPC",
			dependsOnMethods="EGovTipoMittenteDiversoSPC")
			public void testDBEGovTipoMittenteDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoMittenteDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	/**
	 * Heder e-Gov con indirizzo telematico mittente
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte/indirizzoTelematico
	 * busta = buste_linee_guida_1.1/bustaIndirizzoTelematicoMittente.xml
	 * 
	 * Test equivalente al Test N.18 Della Certificazione DigitPA (Busta Errata 5)
	 */
	Repository repositoryIndirizzoTelematicoMittente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ITM"},
			dependsOnMethods="init")
			public void EGovIndirizzoTelematicoMittente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIndirizzoTelematicoMittente.xml");

		this.repositoryIndirizzoTelematicoMittente.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIndirizzoTelematicoMittente);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovIndirizzoTelematicoMittente")
	public Object[][] testEGovIndirizzoTelematicoMittente()throws Exception{
		String id=this.repositoryIndirizzoTelematicoMittente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ITM"},
			dataProvider="EGovIndirizzoTelematicoMittente",
			dependsOnMethods="EGovIndirizzoTelematicoMittente")
			public void testDBEGovIndirizzoTelematicoMittente(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIndirizzoTelematicoMittente.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));
			
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
	 * Destinatario con tipo diverso da SPC
	 * La busta ritornata possiedera' una eccezione GRAVE con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte/tipo
	 * busta = buste_linee_guida_1.1/bustaTipoDestinatarioDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryTipoDestinatarioDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DTDSPC"},
			dependsOnMethods="init")
			public void EGovTipoDestinatarioDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoDestinatarioDiversoSPC.xml");

		this.repositoryTipoDestinatarioDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoDestinatarioDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovTipoDestinatarioDiversoSPC")
	public Object[][] testEGovTipoDestinatarioDiversoSPC()throws Exception{
		String id=this.repositoryTipoDestinatarioDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DTDSPC"},
			dataProvider="EGovTipoDestinatarioDiversoSPC",
			dependsOnMethods="EGovTipoDestinatarioDiversoSPC")
			public void testDBEGovTipoDestinatarioDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoDestinatarioDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	/**
	 * 
	 * Heder e-Gov con indirizzo telematico destinatario
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte/indirizzoTelematico
	 * busta = buste_linee_guida_1.1/bustaIndirizzoTelematicoDestinatario.xml
	 * 
	 */
	Repository repositoryIndirizzoTelematicoDestinatario=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ITD"},
			dependsOnMethods="init")
			public void EGovIndirizzoTelematicoDestinatario()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIndirizzoTelematicoDestinatario.xml");

		this.repositoryIndirizzoTelematicoDestinatario.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIndirizzoTelematicoDestinatario);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovIndirizzoTelematicoDestinatario")
	public Object[][] testEGovIndirizzoTelematicoDestinatario()throws Exception{
		String id=this.repositoryIndirizzoTelematicoDestinatario.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ITD"},
			dataProvider="EGovIndirizzoTelematicoDestinatario",
			dependsOnMethods="EGovIndirizzoTelematicoDestinatario")
			public void testDBEGovIndirizzoTelematicoDestinatario(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIndirizzoTelematicoDestinatario.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));
			
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
	 * Il profilo delle linee guida 1.1 prevedono che una richiesta possieda obbligatoriamente il servizio (e tipo)
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio/tipo
	 * busta = buste_linee_guida_1.1/bustaOneWaySenzaTipoServizio.xml
	 * 
	 */
	Repository repositoryOneWaySenzaTipoServizio=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BOWSTS"},
			dependsOnMethods="init")
			public void EGovOneWaySenzaTipoServizio()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaTipoServizio.xml");

		this.repositoryOneWaySenzaTipoServizio.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWaySenzaTipoServizio);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con elemento Servizio senza tipo");
		this.erroriAttesiOpenSPCoopCore.add(err);

	}
	@DataProvider (name="EGovOneWaySenzaTipoServizio")
	public Object[][] testEGovOneWaySenzaTipoServizio()throws Exception{
		String id=this.repositoryOneWaySenzaTipoServizio.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BOWSTS"},
			dataProvider="EGovOneWaySenzaTipoServizio",
			dependsOnMethods="EGovOneWaySenzaTipoServizio")
			public void testDBEGovOneWaySenzaTipoServizio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaTipoServizio.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				Assert.assertTrue(num==1);
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString()));
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	
	
	
	/**
	 * Il profilo delle linee guida 1.1 prevedono che una richiesta possieda obbligatoriamente il servizio
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio
	 * busta = buste_linee_guida_1.1/bustaOneWaySenzaServizio.xml
	 * 
	 * 
	 */
	Repository repositoryBustaOneWaySenzaServizio=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ROWSS"},
			dependsOnMethods="init")
			public void EGovBustaOneWaySenzaServizio()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaServizio.xml");

		this.repositoryBustaOneWaySenzaServizio.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaOneWaySenzaServizio);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovBustaOneWaySenzaServizio")
	public Object[][] testEGovBustaOneWaySenzaServizio()throws Exception{
		String id=this.repositoryBustaOneWaySenzaServizio.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ROWSS"},
			dataProvider="EGovBustaOneWaySenzaServizio",
			dependsOnMethods="EGovBustaOneWaySenzaServizio")
			public void testDBEGovBustaOneWaySenzaServizio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaServizio.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	
	/**
	 *  Header eGov con tipo servizio diverso da SPC
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio/tipo
	 * busta = buste_linee_guida_1.1/bustaTipoServizioDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryTipoServizioDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TServDSPC"},
			dependsOnMethods="init")
			public void EGovTipoServizioDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoServizioDiversoSPC.xml");

		this.repositoryTipoServizioDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoServizioDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovTipoServizioDiversoSPC")
	public Object[][] testEGovTipoServizioDiversoSPC()throws Exception{
		String id=this.repositoryTipoServizioDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TServDSPC"},
			dataProvider="EGovTipoServizioDiversoSPC",
			dependsOnMethods="EGovTipoServizioDiversoSPC")
			public void testDBEGovTipoServizioDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoServizioDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Il profilo delle linee guida 1.1 prevedono che una richiesta possieda obbligatoriamente il campo azione
	 * La busta ritornata possiedera' una eccezione GRAVE [EGOV_IT_106] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Azione
	 * busta = buste_linee_guida_1.1/bustaOneWaySenzaAzione.xml
	 * 
	 */
	Repository repositoryBustaOneWaySenzaAzione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BOWSAz"},
			dependsOnMethods="init")
			public void EGovBustaOneWaySenzaAzione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaAzione.xml");

		this.repositoryBustaOneWaySenzaAzione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaOneWaySenzaAzione);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovBustaOneWaySenzaAzione")
	public Object[][] testEGovBustaOneWaySenzaAzione()throws Exception{
		String id=this.repositoryBustaOneWaySenzaAzione.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BOWSAz"},
			dataProvider="EGovBustaOneWaySenzaAzione",
			dependsOnMethods="EGovBustaOneWaySenzaAzione")
			public void testDBEGovBustaOneWaySenzaAzione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaOneWaySenzaAzione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.AZIONE_SCONOSCIUTA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	
	/**
	 *
	 * Header e-Gov con Identificatore Duplicati (uso servizio con filtro duplicati)
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_110] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_errate/bustaIdentificatoreDuplicato.xml
	 * 
	 */

	Repository repositoryIdentificatoreDuplicato=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".IDD"},
			dependsOnMethods="init")
			public void EGovConIdentificatoreDuplicato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIdentificatoreDuplicato.xml");

		this.repositoryIdentificatoreDuplicato.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());		
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim())==false);
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString())==false);

		}catch(NullPointerException e){}


		try{
			Thread.sleep(5000);
		}catch(Exception e){}

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));

		}catch(NullPointerException e){}
	}
	private void runClient(Message msg)throws SOAPException, Exception{
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIdentificatoreDuplicato);
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
	@DataProvider (name="EGovConIdentificatoreDuplicato")
	public Object[][] testEGovConIdentificatoreDuplicato()throws Exception{
		String id=this.repositoryIdentificatoreDuplicato.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{Thread.sleep(3000);}catch(InterruptedException e){}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".IDD"},
			dataProvider="EGovConIdentificatoreDuplicato",
			dependsOnMethods="EGovConIdentificatoreDuplicato")
			public void testDBEGovConIdentificatoreDuplicato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaIdentificatoreDuplicato.xml");
		try{
			Reporter.log("Aspettiamo: " +id);
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			// Il fatto che al servizio applicativo sia arrivata una sola copia, mi garantisce il buon funzionamento del filtro duplicati
			Reporter.log("Numero duplicati: "+data.getVerificatoreTracciaRichiesta().countTracce(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().countTracce(id)==2);


			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,true,false));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString(),true,false));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	

	
	
	
	/**
	 * 
	 * Header e-Gov senza Profilo di Collaborazione, obbligatorio per le linee guida 1.1
	 * La busta ritornata possiedera' una eccezione GRAVE con codice [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione
	 * busta = buste_linee_guida_1.1/bustaSenzaProfiloCollaborazione.xml
	 * 
	 */
	Repository repositoryBustaSenzaProfiloCollaborazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSPC"},
			dependsOnMethods="init")
			public void EGovBustaSenzaProfiloCollaborazione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaProfiloCollaborazione.xml");

		this.repositoryBustaSenzaProfiloCollaborazione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaSenzaProfiloCollaborazione);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovBustaSenzaProfiloCollaborazione")
	public Object[][] testEGovBustaSenzaProfiloCollaborazione()throws Exception{
		String id=this.repositoryBustaSenzaProfiloCollaborazione.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSPC"},
			dataProvider="EGovBustaSenzaProfiloCollaborazione",
			dependsOnMethods="EGovBustaSenzaProfiloCollaborazione")
			public void testDBEGovBustaSenzaProfiloCollaborazione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaProfiloCollaborazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()));

			Reporter.log("Elimino utilizzo da parte del profilo di gestione, id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	
	
	
	
	/**
	 *  Header e-Gov con tipo servizio correlato
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione/tipo
	 * busta = buste_linee_guida_1.1/bustaTipoServizioCorrelato.xml
	 * 
	 * 
	 */
	Repository repositoryBustaTipoServizioCorrelato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BTSC"},
			dependsOnMethods="init")
			public void EGovBustaTipoServizioCorrelato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoServizioCorrelato.xml");

		this.repositoryBustaTipoServizioCorrelato.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaTipoServizioCorrelato);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovBustaTipoServizioCorrelato")
	public Object[][] testEGovBustaTipoServizioCorrelato()throws Exception{
		String id=this.repositoryBustaTipoServizioCorrelato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BTSC"},
			dataProvider="EGovBustaTipoServizioCorrelato",
			dependsOnMethods="EGovBustaTipoServizioCorrelato")
			public void testDBEGovBustaTipoServizioCorrelato(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoServizioCorrelato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==1);
			//}else{
			//	Assert.assertTrue(num==2);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString()));
			
			Reporter.log("Elimino utilizzo da parte del profilo di gestione, id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
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
	 * Header e-Gov con servizio correlato
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione/servizioCorrelato
	 * busta = buste_linee_guida_1.1/bustaServizioCorrelato.xml
	 * 
	 * 
	 */
	Repository repositoryBustaServizioCorrelato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSC"},
			dependsOnMethods="init")
			public void EGovBustaServizioCorrelato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaServizioCorrelato.xml");

		this.repositoryBustaServizioCorrelato.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaServizioCorrelato);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovBustaServizioCorrelato")
	public Object[][] testEGovBustaServizioCorrelato()throws Exception{
		String id=this.repositoryBustaServizioCorrelato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSC"},
			dataProvider="EGovBustaServizioCorrelato",
			dependsOnMethods="EGovBustaServizioCorrelato")
			public void testDBEGovBustaServizioCorrelato(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaServizioCorrelato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==1);
			//}else{
			//	Assert.assertTrue(num==2);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString()));
			
			Reporter.log("Elimino utilizzo da parte del profilo di gestione, id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
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
	 * Header e-Gov con tipo e servizio correlato
	 * La busta ritornata possiederà due eccezioni:
	 * INFO con codice [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione/servizioCorrelato
	 * INFO con codice [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione/tipo
	 * busta = buste_linee_guida_1.1/bustaServizioETipoCorrelato.xml
	 * 
	 * 
	 */
	Repository repositoryBustaTipoEServizioCorrelato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BTESC"},
			dependsOnMethods="init")
			public void EGovBustaTipoEServizioCorrelato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaServizioETipoCorrelato.xml");

		this.repositoryBustaTipoEServizioCorrelato.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaTipoEServizioCorrelato);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovBustaTipoEServizioCorrelato")
	public Object[][] testEGovBustaTipoEServizioCorrelato()throws Exception{
		String id=this.repositoryBustaTipoEServizioCorrelato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BTESC"},
			dataProvider="EGovBustaTipoEServizioCorrelato",
			dependsOnMethods="EGovBustaTipoEServizioCorrelato")
			public void testDBEGovBustaTipoEServizioCorrelato(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaServizioETipoCorrelato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO+" 2 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==2);
			//}else{
			//	Assert.assertTrue(num==4);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString()));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString()));
			
			Reporter.log("controllo l'eccezione nei messaggi diagnostici tipo servizio correlato: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_SERVIZIO_CORRELATO.toString()));
			
			Reporter.log("controllo l'eccezione nei messaggi diagnostici servizio correlato: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString()));
			
			Reporter.log("Elimino utilizzo da parte del profilo di gestione, id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
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
	 * Header e-Gov con una collaborazione
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_104] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Collaborazione
	 * busta = buste_linee_guida_1.1/bustaCollaborazione.xml
	 * 
	 * 
	 */
	Repository repositoryBustaCollaborazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".Collaborazione"},
			dependsOnMethods="init")
			public void EGovBustaCollaborazione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazione.xml");

		this.repositoryBustaCollaborazione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryBustaCollaborazione);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovBustaCollaborazione")
	public Object[][] testEGovBustaCollaborazione()throws Exception{
		String id=this.repositoryBustaCollaborazione.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".Collaborazione"},
			dataProvider="EGovBustaCollaborazione",
			dependsOnMethods="EGovBustaCollaborazione")
			public void testDBEGovBustaCollaborazione(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()));
			
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
	 * Header e-Gov asincrono con una collaborazione (non deve generare alcun errore)
	 * busta = buste_linee_guida_1.1/bustaCollaborazioneAsincronoSimmetricoConCollaborazione.xml
	 * 
	 * 
	 */
	Repository repositoryCollaborazioneAsincronoSimmetricoConCollaborazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BCASCC"},
			dependsOnMethods="init")
			public void EGovCollaborazioneAsincronoSimmetricoConCollaborazione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazioneAsincronoSimmetricoConCollaborazione.xml");

		this.repositoryCollaborazioneAsincronoSimmetricoConCollaborazione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneAsincronoSimmetricoConCollaborazione);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovCollaborazioneAsincronoSimmetricoConCollaborazione")
	public Object[][] testEGovCollaborazioneAsincronoSimmetricoConCollaborazione()throws Exception{
		String id=this.repositoryCollaborazioneAsincronoSimmetricoConCollaborazione.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BCASCC"},
			dataProvider="EGovCollaborazioneAsincronoSimmetricoConCollaborazione",
			dependsOnMethods="EGovCollaborazioneAsincronoSimmetricoConCollaborazione")
			public void testDBEGovCollaborazioneAsincronoSimmetricoConCollaborazione(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazioneAsincronoSimmetricoConCollaborazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().existsListaEccezioni(id)==false);

			Reporter.log("Elimino utilizzo da parte del profilo di gestione, id: " +id);
			data.getVerificatoreMessaggi().deleteUtilizzoProfiloCollaborazione(id, Costanti.INBOX, Utilities.testSuiteProperties.isUseTransazioni());
			
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
	 * Header e-Gov asincrono senza collaborazione
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_104] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Collaborazione 
	 * busta = buste_linee_guida_1.1/bustaCollaborazioneAsincronoSimmetricoSenzaCollaborazione.xml
	 * 
	 * 
	 */
	Repository repositoryCollaborazioneAsincronoSimmetricoSenzaCollaborazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CBASSC"},
			dependsOnMethods="init")
			public void EGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazioneAsincronoSimmetricoSenzaCollaborazione.xml");

		this.repositoryCollaborazioneAsincronoSimmetricoSenzaCollaborazione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneAsincronoSimmetricoSenzaCollaborazione);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione")
	public Object[][] testEGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione()throws Exception{
		String id=this.repositoryCollaborazioneAsincronoSimmetricoSenzaCollaborazione.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CBASSC"},
			dataProvider="EGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione",
			dependsOnMethods="EGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione")
			public void testDBEGovCollaborazioneAsincronoSimmetricoSenzaCollaborazione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaCollaborazioneAsincronoSimmetricoSenzaCollaborazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==1);
			//}else{
			//	Assert.assertTrue(num==2);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	
	
	
	/**
	 * Header e-Gov con tipo di ora registrazione diverso da
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione/tempo
	 * busta = buste_linee_guida_1.1/bustaTipoOraRegistrazioneDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryTipoOraRegistrazioneDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORDSPC"},
			dependsOnMethods="init")
			public void EGovTipoOraRegistrazioneDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoOraRegistrazioneDiversoSPC.xml");

		this.repositoryTipoOraRegistrazioneDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoOraRegistrazioneDiversoSPC);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovTipoOraRegistrazioneDiversoSPC")
	public Object[][] testEGovTipoOraRegistrazioneDiversoSPC()throws Exception{
		String id=this.repositoryTipoOraRegistrazioneDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORDSPC"},
			dataProvider="EGovTipoOraRegistrazioneDiversoSPC",
			dependsOnMethods="EGovTipoOraRegistrazioneDiversoSPC")
			public void testDBEGovTipoOraRegistrazioneDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTipoOraRegistrazioneDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_LOCALE, TipoOraRegistrazione.LOCALE));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	/**
	 *  Header e-Gov con Profilo di Trasmissione confermaRicezione=true
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/confermaRicezione
	 * busta = buste_linee_guida_1.1/bustaConfermaRicezione.xml
	 */
	Repository repositoryConfermaRicezione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CR"},
			dependsOnMethods="init")
			public void EGovConfermaRicezione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaConfermaRicezione.xml");

		this.repositoryConfermaRicezione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConfermaRicezione);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovConfermaRicezione")
	public Object[][] testEGovConfermaRicezione()throws Exception{
		String id=this.repositoryConfermaRicezione.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CR"},
			dataProvider="EGovConfermaRicezione",
			dependsOnMethods="EGovConfermaRicezione")
			public void testDBEGovConfermaRicezione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaConfermaRicezione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	
	
	/**
	 * Header e-Gov con Profilo di Trasmissione inoltro=EGOV_IT_PIUDIUNAVOLTA
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/inoltro
	 * busta = buste_linee_guida_1.1/bustaSenzaFiltroDuplicati.xml
	 * 
	 * Test equivalente al Test N.6 Della Certificazione DigitPA (Busta Errata 47)
	 */
	Repository repositorySenzaFiltroDuplicati=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SFD"},
			dependsOnMethods="init")
			public void EGovSenzaFiltroDuplicati()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaFiltroDuplicati.xml");

		this.repositorySenzaFiltroDuplicati.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaFiltroDuplicati);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovSenzaFiltroDuplicati")
	public Object[][] testEGovSenzaFiltroDuplicati()throws Exception{
		String id=this.repositorySenzaFiltroDuplicati.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SFD"},
			dataProvider="EGovSenzaFiltroDuplicati",
			dependsOnMethods="EGovSenzaFiltroDuplicati")
			public void testDBEGovSenzaFiltroDuplicati(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaFiltroDuplicati.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	

	
	
	/**
	 * Header e-Gov con Profilo di Trasmissione confermaRicezione=true e profilo Sincrono
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/confermaRicezione
	 * busta = buste_linee_guida_1.1/bustaSincronaConfermaRicezione.xml
	 * 
	 * Test equivalente al Test N.27 Della Certificazione DigitPA (Busta Errata 48)
	 */
	Repository repositorySincronaConfermaRicezione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSCR"},
			dependsOnMethods="init")
			public void EGovSincronaConfermaRicezione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaConfermaRicezione.xml");

		this.repositorySincronaConfermaRicezione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronaConfermaRicezione);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovSincronaConfermaRicezione")
	public Object[][] testEGovSincronaConfermaRicezione()throws Exception{
		String id=this.repositorySincronaConfermaRicezione.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSCR"},
			dataProvider="EGovSincronaConfermaRicezione",
			dependsOnMethods="EGovSincronaConfermaRicezione")
			public void testDBEGovSincronaConfermaRicezione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaConfermaRicezione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	

	
	
	
	/**
	 * 
	 * Header e-Gov con Profilo di Trasmissione inoltro=EGOV_IT_PIUDIUNAVOLTA e profilo Sincrono
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/inoltro
	 * busta = buste_linee_guida_1.1/bustaSincronaSenzaFiltroDuplicati.xml
	 * 
	 */
	Repository repositorySincronaSenzaFiltroDuplicati=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSSFD"},
			dependsOnMethods="init")
			public void EGovSincronaSenzaFiltroDuplicati()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaSenzaFiltroDuplicati.xml");

		this.repositorySincronaSenzaFiltroDuplicati.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronaSenzaFiltroDuplicati);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovSincronaSenzaFiltroDuplicati")
	public Object[][] testEGovSincronaSenzaFiltroDuplicati()throws Exception{
		String id=this.repositorySincronaSenzaFiltroDuplicati.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BSSFD"},
			dataProvider="EGovSincronaSenzaFiltroDuplicati",
			dependsOnMethods="EGovSincronaSenzaFiltroDuplicati")
			public void testDBEGovSincronaSenzaFiltroDuplicati(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaSenzaFiltroDuplicati.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
	
	
	
	
	/**
	 * Header e-Gov con sequenza
	 * La busta causera un msg diagnostico con una eccezione INFO con codice [EGOV_IT_401] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Sequenza
	 * busta = buste_linee_guida_1.1/bustaSequenza.xml
	 * 
	 * 
	 */
	Repository repositorySequenza=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEQ"},
			dependsOnMethods="init")
			public void EGovSequenza()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSequenza.xml");

		this.repositorySequenza.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySequenza);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovSequenza")
	public Object[][] testEGovSequenza()throws Exception{
		String id=this.repositorySequenza.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEQ"},
			dataProvider="EGovSequenza",
			dependsOnMethods="EGovSequenza")
			public void testDBEGovSequenza(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSequenza.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
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
					this.busteEGov.getID(index),CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE.toString()));
			
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
	 * Header e-Gov con sequenza
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_401] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Sequenza
	 * busta = buste_linee_guida_1.1/bustaSincronaSequenza.xml
	 * 
	 * Test equivalente al Test N.24 Della Certificazione DigitPA (Busta Errata 49)
	 */
	Repository repositorySincronaSequenza=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEQs"},
			dependsOnMethods="init")
			public void EGovSincronaSequenza()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaSequenza.xml");

		this.repositorySincronaSequenza.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronaSequenza);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovSincronaSequenza")
	public Object[][] testEGovSincronaSequenza()throws Exception{
		String id=this.repositorySincronaSequenza.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SEQs"},
			dataProvider="EGovSincronaSequenza",
			dependsOnMethods="EGovSincronaSequenza")
			public void testDBEGovSincronaSequenza(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSincronaSequenza.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_SUPPORTATA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_TRASPARENZA_TEMPORALE_NON_SUPPORTATA_POSIZIONE.toString()));
			
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
	 * Header e-Gov con lista trasmissione puo' tranquillamente esistere
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissione.xml
	 * 
	 * 
	 */
	Repository repositoryListaTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TR"},
			dependsOnMethods="init")
			public void EGovListaTrasmissione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissione.xml");

		this.repositoryListaTrasmissione.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovListaTrasmissione")
	public Object[][] testEGovListaTrasmissione()throws Exception{
		String id=this.repositoryListaTrasmissione.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TR"},
			dataProvider="EGovListaTrasmissione",
			dependsOnMethods="EGovListaTrasmissione")
			public void testDBEGovListaTrasmissione(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index),
					this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index),
					true,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));			
			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedTrasmissione(id, this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index),
					this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index),
					true,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().existsListaEccezioni(id)==false);

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
	 *  Header e-Gov con lista trasmissione e tipo soggetto mittente diverso da SPC
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, 
	 *    descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte/tipo
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissioneTipoMittenteDiversoSPC.xml
	 * 
	 */
	Repository repositoryListaTrasmissioneTipoMittenteDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTTMDSPC"},
			dependsOnMethods="init")
			public void EGovListaTrasmissioneTipoMittenteDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneTipoMittenteDiversoSPC.xml");

		this.repositoryListaTrasmissioneTipoMittenteDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissioneTipoMittenteDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovListaTrasmissioneTipoMittenteDiversoSPC")
	public Object[][] testEGovListaTrasmissioneTipoMittenteDiversoSPC()throws Exception{
		String id=this.repositoryListaTrasmissioneTipoMittenteDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTTMDSPC"},
			dataProvider="EGovListaTrasmissioneTipoMittenteDiversoSPC",
			dependsOnMethods="EGovListaTrasmissioneTipoMittenteDiversoSPC")
			public void testDBEGovListaTrasmissioneTipoMittenteDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneTipoMittenteDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	/**
	 * Header e-Gov con lista trasmissione e tipo soggetto destinatario diverso da SPC
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte/tipo
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissioneTipoDestinatarioDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryListaTrasmissioneTipoDestinatarioDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTTDDSPC"},
			dependsOnMethods="init")
			public void EGovListaTrasmissioneTipoDestinatarioDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneTipoDestinatarioDiversoSPC.xml");

		this.repositoryListaTrasmissioneTipoDestinatarioDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissioneTipoDestinatarioDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovListaTrasmissioneTipoDestinatarioDiversoSPC")
	public Object[][] testEGovListaTrasmissioneTipoDestinatarioDiversoSPC()throws Exception{
		String id=this.repositoryListaTrasmissioneTipoDestinatarioDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTTDDSPC"},
			dataProvider="EGovListaTrasmissioneTipoDestinatarioDiversoSPC",
			dependsOnMethods="EGovListaTrasmissioneTipoDestinatarioDiversoSPC")
			public void testDBEGovListaTrasmissioneTipoDestinatarioDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneTipoDestinatarioDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	

	
	
	
	/**
	 * Header e-Gov con lista trasmissione e tipo soggetto mittente diverso da SPC
	 * La busta ritornata possiederà una eccezione GRAVE con codice [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione/tempo
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissioneOraRegistrazioneDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryListaTrasmissioneOraRegistrazioneDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTORDSPC"},
			dependsOnMethods="init")
			public void EGovListaTrasmissioneOraRegistrazioneDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneOraRegistrazioneDiversoSPC.xml");

		this.repositoryListaTrasmissioneOraRegistrazioneDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissioneOraRegistrazioneDiversoSPC);
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
				Reporter.log("Invocazione PA");
				throw new TestSuiteException("Invocazione PA non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovListaTrasmissioneOraRegistrazioneDiversoSPC")
	public Object[][] testEGovListaTrasmissioneOraRegistrazioneDiversoSPC()throws Exception{
		String id=this.repositoryListaTrasmissioneOraRegistrazioneDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTORDSPC"},
			dataProvider="EGovListaTrasmissioneOraRegistrazioneDiversoSPC",
			dependsOnMethods="EGovListaTrasmissioneOraRegistrazioneDiversoSPC")
			public void testDBEGovListaTrasmissioneOraRegistrazioneDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneOraRegistrazioneDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	

	
	
	/**
	 * Header e-Gov con lista trasmissione e indirizzo telematico mittente
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte/indirizzoTelematico
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissioneIndirizzoTelematicoMittente.xml
	 * 
	 * 
	 */
	Repository repositoryListaTrasmissioneIndirizzoTelematicoMittente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTITM"},
			dependsOnMethods="init")
			public void EGovListaTrasmissioneIndirizzoTelematicoMittente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneIndirizzoTelematicoMittente.xml");

		this.repositoryListaTrasmissioneIndirizzoTelematicoMittente.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissioneIndirizzoTelematicoMittente);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovListaTrasmissioneIndirizzoTelematicoMittente")
	public Object[][] testEGovListaTrasmissioneIndirizzoTelematicoMittente()throws Exception{
		String id=this.repositoryListaTrasmissioneIndirizzoTelematicoMittente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTITM"},
			dataProvider="EGovListaTrasmissioneIndirizzoTelematicoMittente",
			dependsOnMethods="EGovListaTrasmissioneIndirizzoTelematicoMittente")
			public void testDBEGovListaTrasmissioneIndirizzoTelematicoMittente(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneIndirizzoTelematicoMittente.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));
			
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
	 * Header e-Gov con lista trasmissione e indirizzo telematico destinatario
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte/indirizzoTelematico
	 * busta = buste_linee_guida_1.1/bustaListaTrasmissioneIndirizzoTelematicoDestinatario.xml
	 * 
	 * 
	 */
	Repository repositoryListaTrasmissioneIndirizzoTelematicoDestinatario=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTITD"},
			dependsOnMethods="init")
			public void EGovListaTrasmissioneIndirizzoTelematicoDestinatario()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneIndirizzoTelematicoDestinatario.xml");

		this.repositoryListaTrasmissioneIndirizzoTelematicoDestinatario.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissioneIndirizzoTelematicoDestinatario);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovListaTrasmissioneIndirizzoTelematicoDestinatario")
	public Object[][] testEGovListaTrasmissioneIndirizzoTelematicoDestinatario()throws Exception{
		String id=this.repositoryListaTrasmissioneIndirizzoTelematicoDestinatario.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BLTITD"},
			dataProvider="EGovListaTrasmissioneIndirizzoTelematicoDestinatario",
			dependsOnMethods="EGovListaTrasmissioneIndirizzoTelematicoDestinatario")
			public void testDBEGovListaTrasmissioneIndirizzoTelematicoDestinatario(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaListaTrasmissioneIndirizzoTelematicoDestinatario.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));
			
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
	 * Header e-Gov con lista riscontri
	 * La busta ritornata possiederà una eccezione INFO con codice [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri
	 * busta = buste_linee_guida_1.1/bustaRiscontro.xml
	 * 
	 */
	Repository repositoryRiscontro=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BRiscontro"},
			dependsOnMethods="init")
			public void EGovRiscontro()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaRiscontro.xml");

		this.repositoryRiscontro.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryRiscontro);
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
				Reporter.log("Invocazione PA");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				throw new Exception("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="EGovRiscontro")
	public Object[][] testEGovRiscontro()throws Exception{
		String id=this.repositoryRiscontro.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BRiscontro"},
			dataProvider="EGovRiscontro",
			dependsOnMethods="EGovRiscontro")
			public void testDBEGovRiscontro(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaRiscontro.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo Riscontro");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("MinisteroFruitore_MinisteroFruitoreSPCoopIT_2411206_2005-11-10_11:35",id,true,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));

			
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().existsListaEccezioni(id)==false);

			Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
			Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
					this.busteEGov.getID(index),CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA,
					SPCoopCostanti.ECCEZIONE_RILEVANZA_INFO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString()));
			
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
	 * Header e-Gov con lista riscontri con tempo diverso da EGOV_IT_SPC
	 * La busta ritornata possiederà le eccezioni:
	 * Eccezione INFO con codice [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri
	 * Eccezione GRAVE con codice [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione/tempo
	 * busta = buste_linee_guida_1.1/bustaRiscontroTempoDiversoSPC.xml
	 * 
	 * 
	 */
	Repository repositoryRiscontroTempoDiversoSPC=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".RTDSPC"},
			dependsOnMethods="init")
			public void EGovRiscontroTempoDiversoSPC()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaRiscontroTempoDiversoSPC.xml");

		this.repositoryRiscontroTempoDiversoSPC.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryRiscontroTempoDiversoSPC);
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
					Reporter.log("Invocazione PA");
					throw new TestSuiteException("Invocazione PA non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGovRiscontroTempoDiversoSPC")
	public Object[][] testEGovRiscontroTempoDiversoSPC()throws Exception{
		String id=this.repositoryRiscontroTempoDiversoSPC.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".RTDSPC"},
			dataProvider="EGovRiscontroTempoDiversoSPC",
			dependsOnMethods="EGovRiscontroTempoDiversoSPC")
			public void testDBEGovRiscontroTempoDiversoSPC(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaRiscontroTempoDiversoSPC.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			
			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString()));

			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	

	
	
	
	

	
	
	
	
	
	
	
	/**
	 *
	 * Se non c'e' profilo di collaborazione, deve essere automaticamente utilizzato il filtro duplicati
	 * Quindi se la busta è gia stata inviata ritornerà una eccezione
	 *   eccezione GRAVE [EGOV_IT_110] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_linee_guida_1.1/bustaSenzaProfiloTrasmissione.xml
	 * 
	 */

	Repository repositorySenzaProfiloTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SPT"},
			dependsOnMethods="init")
			public void EGovSenzaProfiloTrasmissione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaProfiloTrasmissione.xml");

		this.repositorySenzaProfiloTrasmissione.add(this.busteEGov.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());		
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta SenzaProfiloTrasmissione");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim())==false);
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString())==false);

		}catch(NullPointerException e){}


		try{
			Thread.sleep(5000);
		}catch(Exception e){}

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta SenzaProfiloTrasmissione");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));

		}catch(NullPointerException e){}
	}
	@DataProvider (name="EGovSenzaProfiloTrasmissione")
	public Object[][] testEGovSenzaProfiloTrasmissione()throws Exception{
		String id=this.repositorySenzaProfiloTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{Thread.sleep(3000);}catch(InterruptedException e){}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SPT"},
			dataProvider="EGovSenzaProfiloTrasmissione",
			dependsOnMethods="EGovSenzaProfiloTrasmissione")
			public void testDBEGovSenzaProfiloTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaSenzaProfiloTrasmissione.xml");
		try{
			Reporter.log("Aspettiamo: " +id);
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			// Il fatto che al servizio applicativo sia arrivata una sola copia, mi garantisce il buon funzionamento del filtro duplicati
			Reporter.log("Numero duplicati: "+data.getVerificatoreTracciaRichiesta().countTracce(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().countTracce(id)==2);


			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id,SPCoopCostanti.TIPO_TEMPO_SPC,TipoOraRegistrazione.SINCRONIZZATO));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,true,false));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString(),true,false));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}


