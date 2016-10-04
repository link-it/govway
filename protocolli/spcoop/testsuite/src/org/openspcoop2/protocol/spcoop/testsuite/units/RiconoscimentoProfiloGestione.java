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
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.BusteEGovDaFile;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test per il riconoscimento del profilo di gestione
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RiconoscimentoProfiloGestione {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RiconoscimentoProfiloGestione";

	private static boolean addIDUnivoco = true;
	
	
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	
	/** Lettura della batteria di test sulle buste eGov errate */
	private BusteEGovDaFile busteEGov = null;

	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST1",ID_GRUPPO+".FRTEST2",ID_GRUPPO+".FRTEST3"})
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



	
	
	/* ---------------------- EROGAZIONE --------------------- */
	/*
	 * Header e-Gov con richieste asincrone, che servono a testare il riconoscimento del profilo di gestione in base all'erogatore,
	 * tra bustaEGov1.1 e lineeGuida1.1-bustaEGov1.1.
	 * Il test si basa sul fatto che i soggetti erogatore o il servizio che hanno profilo lineeGuida 
	 * causeranno una produzione della busta di richiesta da parte dellla PdD mittente, che possiede l'elemento collaborazione,
	 * ANCHE se l'elemento collaborazione non è stato richiesto nell'accordo.
	 */
	
	
	
	// TEST 1. Invocazione servizio	TestProfiloLineeGuida di MinisteroErogatore: 
	//            non verra prodotto l'elemento collaborazione
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoTEST1 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseTEST1 = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.infoTEST1, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona (azione correlata)
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST1 = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST1"})
	public void asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBaseTEST1.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_EROGATORE,
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_EROGATORE,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST1,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST1.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST1"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1",
			dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST1.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE, checkServizioApplicativo,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1")
	public Object[][]testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST1.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST1.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST1"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1",
			dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST1(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST1.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, idCorrelazioneAsincrona,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE_CORRELATA, 
					checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	// TEST 2. Invocazione servizio	TestProfiloLineeGuida di MinisteroErogatoreLineeGuida: 
	//            verra prodotto l'elemento collaborazione poiche' il soggetto possiede un profilo lineeGuida1.1-bustaEGov1.1
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoTEST2 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE_LINEE_GUIDA_11,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseTEST2 = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.infoTEST2, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona (azione correlata)
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST2 = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST2"})
	public void asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBaseTEST2.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_EROGATORE_LINEE_GUIDA,
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_EROGATORE_LINEE_GUIDA,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST2,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST2.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST2"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2",
			dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST2.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE, checkServizioApplicativo,
					null,
					null,
					id);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2")
	public Object[][]testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST2.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST2.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST2"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2",
			dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST2(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST2.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, null,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE_CORRELATA, 
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	// TEST 3. Invocazione servizio	TestProfiloLineeGuida di MinisteroTmp: 
	//            verra prodotto l'elemento collaborazione poiche' il servizio possiede un profilo lineeGuida1.1-bustaEGov1.1
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni infoTEST3 = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE_LINEE_GUIDA_11,
			CostantiTestSuite.SPCOOP_SOGGETTO_TMP,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseTEST3 = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.infoTEST3, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona (azione correlata)
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST3 = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST3"})
	public void asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBaseTEST3.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RICHIESTA_MINISTERO_TMP,
				CostantiTestSuite.PORTA_DELEGATA_RICONOSCIMENTO_PROFILO_RISPOSTA_MINISTERO_TMP,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST3,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3")
	public Object[][]testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST3.getNextIDRichiesta();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST3"},dataProvider="AsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3",
			dependsOnMethods={"asincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3"})
	public void testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST3.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE, checkServizioApplicativo,
					null,
					null,
					id);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3")
	public Object[][]testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST3.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_AzioneCorrelata_modalitaSincronaTEST3.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ERTEST3"},dataProvider="RispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3",
			dependsOnMethods={"testAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3"})
	public void testRispostaAsincronoAsimmetrico_AzioneCorrelata_ModalitaSincronaTEST3(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseTEST3.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id, null,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TEST_PROFILO_AZIONE_CORRELATA, 
					checkServizioApplicativo,
					idCorrelazioneAsincrona);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/* ---------------------- FRUITORE --------------------- */
	/*
	 * Header e-Gov con richieste asincrone, che servono a testare il riconoscimento del profilo di gestione in base al fruitore,
	 * tra bustaEGov1.1 e lineeGuida1.1-bustaEGov1.1.
	 * Il test si basa sul fatto che i soggetti/fruitori che hanno profilo lineeGuida causeranno una ricevuta asincrona 
	 * che non possiede gli attributi correlati
	 */
	
	
	
	// TEST 1. Invocazione servizioTestProfiloLineeGuida con fruitore MinisteroFruitore 
	//             verranno prodotti gli attributi correlati poichè il fruitore possiede un profilo bustaEGov1.1
	Repository repositoryCollaborazioneAsincronoSimmetricoTEST1=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST1"},
			dependsOnMethods="init")
			public void EGovCollaborazioneAsincronoSimmetricoTEST1()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetrico.xml");

		this.repositoryCollaborazioneAsincronoSimmetricoTEST1.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneAsincronoSimmetricoTEST1);
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
	@DataProvider (name="EGovCollaborazioneAsincronoSimmetricoTEST1")
	public Object[][] testEGovCollaborazioneAsincronoSimmetricoTEST1()throws Exception{
		String id=this.repositoryCollaborazioneAsincronoSimmetricoTEST1.getNext();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST1"},
			dataProvider="EGovCollaborazioneAsincronoSimmetricoTEST1",
			dependsOnMethods="EGovCollaborazioneAsincronoSimmetricoTEST1")
			public void testDBEGovCollaborazioneAsincronoSimmetricoTEST1(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetrico.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index), this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			// NOTA se newConnectionForREsponse, verra' prodotto un po' di errori, non validi, che devono essere ripuliti dal db.
			//       Questo succede perche' questo test impersonifica una PA che invia una busta. La ricevuta viene invece inviata realmente alla porta di dominio OpenSPCoop
			//       che pero' non ha le info nel db per gestirla
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),this.busteEGov.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGov.getProfiloTrasmissioneInoltro(index), this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
				String tipoServizioCorrelato = this.busteEGov.getDatiServizio(index).getTipoServizio();
				String servizioCorrelato = this.busteEGov.getDatiServizio(index).getNomeServizio();
				Reporter.log("Controllo valore tipo["+tipoServizioCorrelato+"] e servizio correlato["+servizioCorrelato+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, this.busteEGov.getDatiServizio(index)));
				
				Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			}
			
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
	
	
	
	
	// TEST 2. Invocazione servizioTestProfiloLineeGuida con fruitore MinisteroFruitoreLineeGuida
	//             non verranno prodotti gli attributi correlati poichè il soggetto fruitore possiede un profilo bustaEGov1.1-lineeGuida1.1
	Repository repositoryCollaborazioneAsincronoSimmetricoTEST2=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST2"},
			dependsOnMethods="init")
			public void EGovCollaborazioneAsincronoSimmetricoTEST2()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetricoSoggettoLineeGuida.xml");

		this.repositoryCollaborazioneAsincronoSimmetricoTEST2.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneAsincronoSimmetricoTEST2);
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
	@DataProvider (name="EGovCollaborazioneAsincronoSimmetricoTEST2")
	public Object[][] testEGovCollaborazioneAsincronoSimmetricoTEST2()throws Exception{
		String id=this.repositoryCollaborazioneAsincronoSimmetricoTEST2.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST2"},
			dataProvider="EGovCollaborazioneAsincronoSimmetricoTEST2",
			dependsOnMethods="EGovCollaborazioneAsincronoSimmetricoTEST2")
			public void testDBEGovCollaborazioneAsincronoSimmetricoTEST2(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetricoSoggettoLineeGuida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			// NOTA se newConnectionForREsponse, verra' prodotto un po' di errori, non validi, che devono essere ripuliti dal db.
			//       Questo succede perche' questo test impersonifica una PA che invia una busta. La ricevuta viene invece inviata realmente alla porta di dominio OpenSPCoop
			//       che pero' non ha le info nel db per gestirla
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGov.getProfiloTrasmissioneInoltro(index),this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
				String tipoServizioCorrelato = null;
				String servizioCorrelato = null;
				Reporter.log("Controllo valore tipo["+tipoServizioCorrelato+"] e servizio correlato["+servizioCorrelato+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, null));
				
				Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			}
			
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
	
	
	
	
	
	// TEST 3. Invocazione servizioTestProfiloLineeGuida con fruitore MinisteroTmp 
	//             non verranno prodotti gli attributi correlati poichè il fruitore associato al servizio possiede un profilo bustaEGov1.1-lineeGuida1.1
	Repository repositoryCollaborazioneAsincronoSimmetricoTEST3=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST3"},
			dependsOnMethods="init")
			public void EGovCollaborazioneAsincronoSimmetricoTEST3()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetricoFruitoreLineeGuida.xml");

		this.repositoryCollaborazioneAsincronoSimmetricoTEST3.add(this.busteEGov.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGov.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneAsincronoSimmetricoTEST3);
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
	@DataProvider (name="EGovCollaborazioneAsincronoSimmetricoTEST3")
	public Object[][] testEGovCollaborazioneAsincronoSimmetricoTEST3()throws Exception{
		String id=this.repositoryCollaborazioneAsincronoSimmetricoTEST3.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".FRTEST3"},
			dataProvider="EGovCollaborazioneAsincronoSimmetricoTEST3",
			dependsOnMethods="EGovCollaborazioneAsincronoSimmetricoTEST3")
			public void testDBEGovCollaborazioneAsincronoSimmetricoTEST3(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGov.getIndexFromNomeFile("bustaTestProfiloAsincronoAsimmetricoFruitoreLineeGuida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGov.getMittente(index), this.busteEGov.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGov.getDestinatario(index),this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGov.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index), this.busteEGov.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGov.getProfiloTrasmissioneInoltro(index), this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));

			// NOTA se newConnectionForREsponse, verra' prodotto un po' di errori, non validi, che devono essere ripuliti dal db.
			//       Questo succede perche' questo test impersonifica una PA che invia una busta. La ricevuta viene invece inviata realmente alla porta di dominio OpenSPCoop
			//       che pero' non ha le info nel db per gestirla
			Reporter.log("-------------- RISPOSTA --------------------");

			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGov.getDestinatario(index), this.busteEGov.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGov.getMittente(index),this.busteEGov.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGov.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGov.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGov.getProfiloCollaborazione(index),  this.busteEGov.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGov.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGov.getProfiloTrasmissioneInoltro(index), this.busteEGov.getProfiloTrasmissioneInoltroSdk(index)));
				String tipoServizioCorrelato = null;
				String servizioCorrelato = null;
				Reporter.log("Controllo valore tipo["+tipoServizioCorrelato+"] e servizio correlato["+servizioCorrelato+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, null));
				
				Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
			}
			
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
}


