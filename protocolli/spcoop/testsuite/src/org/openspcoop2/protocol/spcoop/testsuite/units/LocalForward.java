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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.logger.LogLevels;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test sui profili LocalForward
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LocalForward {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "LocalForward";

	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
			new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
					org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
					DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	private static boolean addIDUnivoco = true;

	private static boolean use_axis14_engine = Utilities.testSuiteProperties.isSoapEngineAxis14();
	private static boolean use_cxf_engine = Utilities.testSuiteProperties.isSoapEngineCxf();


	private Date dataAvvioGruppoTest = null;
	private boolean doTestStateful = true;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();

		String version_jbossas = Utilities.readApplicationServerVersion();
		if(version_jbossas.startsWith("tomcat")){
			System.out.println("WARNING: Verifiche Stateful disabilitate per Tomcat");
			this.doTestStateful = false;
		}
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



	private static final String MSG_LOCAL_FORWARD = "Modalità 'local-forward' attiva, messaggio ruotato direttamente verso la porta applicativa";







	/* ********************* CASI OK ************************ */



	/***
	 * Test per ONEWAY_STATEFUL
	 */
	private Date testONEWAY_STATEFUL;
	Repository repositoryONEWAY_STATEFUL=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATEFUL"})
	public void invokePD_ONEWAY_STATEFUL() throws TestSuiteException, Exception{

		if(this.doTestStateful==false){
			return;
		}

		this.testONEWAY_STATEFUL = new Date();

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientOneWay client=new ClientOneWay(this.repositoryONEWAY_STATEFUL);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATEFUL);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);

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
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="Provider_ONEWAY_STATEFUL")
	public Object[][]provider_ONEWAY_STATEFUL() throws Exception{

		if(this.doTestStateful==false){
			return new Object[][]{
					{null,null,null}	
			};
		}

		String id=this.repositoryONEWAY_STATEFUL.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATEFUL"},dataProvider="Provider_ONEWAY_STATEFUL",dependsOnMethods={"invokePD_ONEWAY_STATEFUL"})
	public void verifica_ONEWAY_STATEFUL(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{

		if(this.doTestStateful==false){
			return;
		}

		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATEFUL, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATEFUL, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","001005","007011","007012") || 
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012", "001005") ||
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","001005", "007012"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}






	/***
	 * Test per ONEWAY_STATELESS
	 */
	private Date testONEWAY_STATELESS;
	Repository repositoryONEWAY_STATELESS=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATELESS"})
	public void invokePD_ONEWAY_STATELESS() throws TestSuiteException, Exception{

		this.testONEWAY_STATELESS = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryONEWAY_STATELESS);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATELESS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));

	}
	@DataProvider (name="Provider_ONEWAY_STATELESS")
	public Object[][]provider_ONEWAY_STATELESS() throws Exception{
		String id=this.repositoryONEWAY_STATELESS.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATELESS"},dataProvider="Provider_ONEWAY_STATELESS",dependsOnMethods={"invokePD_ONEWAY_STATELESS"})
	public void verifica_ONEWAY_STATELESS(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATELESS , "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATELESS, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}




	/***
	 * Test per ONEWAY_STATELESS_PA_STATEFUL
	 */
	Date testONEWAY_STATELESS_PA_STATEFUL;
	Repository repositoryONEWAY_STATELESS_PA_STATEFUL=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATELESS_PA_STATEFUL"})
	public void invokePD_ONEWAY_STATELESS_PA_STATEFUL() throws TestSuiteException, Exception{

		this.testONEWAY_STATELESS_PA_STATEFUL = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryONEWAY_STATELESS_PA_STATEFUL);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_STATELESS_PA_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));

	}
	@DataProvider (name="Provider_ONEWAY_STATELESS_PA_STATEFUL")
	public Object[][]provider_ONEWAY_STATELESS_PA_STATEFUL() throws Exception{
		String id=this.repositoryONEWAY_STATELESS_PA_STATEFUL.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_STATELESS_PA_STATEFUL"},dataProvider="Provider_ONEWAY_STATELESS_PA_STATEFUL",dependsOnMethods={"invokePD_ONEWAY_STATELESS_PA_STATEFUL"})
	public void verifica_ONEWAY_STATELESS_PA_STATEFUL(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATELESS_PA_STATEFUL, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_STATELESS_PA_STATEFUL, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}




	/***
	 * Test per SINCRONO
	 */
	Date testSINCRONO;
	Repository repositorySINCRONO=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SINCRONO"})
	public void invokePD_SINCRONO() throws TestSuiteException, Exception{

		this.testSINCRONO = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositorySINCRONO);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage());

	}
	@DataProvider (name="Provider_SINCRONO")
	public Object[][]provider_SINCRONO() throws Exception{
		String id=this.repositorySINCRONO.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SINCRONO"},dataProvider="Provider_SINCRONO",dependsOnMethods={"invokePD_SINCRONO"})
	public void verifica_SINCRONO(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testSINCRONO, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testSINCRONO, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}





	/***
	 * Test per ONEWAY_INTEGRATION_MANAGER
	 */
	Date testONEWAY_INTEGRATION_MANAGER;
	Repository repositoryONEWAY_INTEGRATION_MANAGER=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_INTEGRATION_MANAGER"})
	public void invokePD_ONEWAY_INTEGRATION_MANAGER() throws TestSuiteException, Exception{

		this.testONEWAY_INTEGRATION_MANAGER = new Date();
		
		// IntegrationManager
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop1_axis14 = null;
		org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop3_axis14 = null;
		if(use_axis14_engine){
			imSilGop1_axis14 = IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop1","123456");
			imSilGop3_axis14 = IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop3","123456");

		}
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop1_cxf = null;
		org.openspcoop2.pdd.services.cxf.MessageBox imSilGop3_cxf = null;
		if(use_cxf_engine){
			imSilGop1_cxf = IntegrationManager.getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop1","123456");
			imSilGop3_cxf = IntegrationManager.getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
					"gop3","123456");
		}

		// Clean
		if(use_axis14_engine){
			try{
				imSilGop1_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
			try{
				imSilGop3_axis14.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
				Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
			}
		}
		else if(use_cxf_engine){
			try{
				imSilGop1_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
			try{
				imSilGop3_cxf.deleteAllMessages();
			}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
				Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
			}
		}


		//		DatabaseComponent dbComponentFruitore = null;
		//		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientOneWay client=new ClientOneWay(this.repositoryONEWAY_INTEGRATION_MANAGER);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ONEWAY_INTEGRATION_MANAGER);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);

			// AttesaTerminazioneMessaggi
			//			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
			//				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
			//				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

			//				client.setAttesaTerminazioneMessaggi(true);
			//				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			//				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			//			}
			client.run();
		}catch(Exception e){
			throw e;
		}finally{
			//			try{
			//				dbComponentFruitore.close();
			//			}catch(Exception eClose){}
			//			try{
			//				dbComponentErogatore.close();
			//			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="Provider_ONEWAY_INTEGRATION_MANAGER")
	public Object[][]provider_ONEWAY_INTEGRATION_MANAGER() throws Exception{
		String id=this.repositoryONEWAY_INTEGRATION_MANAGER.getNext();

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}	
				//				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ONEWAY_INTEGRATION_MANAGER"},dataProvider="Provider_ONEWAY_INTEGRATION_MANAGER",dependsOnMethods={"invokePD_ONEWAY_INTEGRATION_MANAGER"})
	public void verifica_ONEWAY_INTEGRATION_MANAGER(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_INTEGRATION_MANAGER, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testONEWAY_INTEGRATION_MANAGER, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007001","007001","001005"));

			// IntegrationManager
			org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop1_axis14 = null;
			org.openspcoop2.pdd.services.axis14.MessageBox_PortType imSilGop3_axis14 = null;
			if(use_axis14_engine){
				imSilGop1_axis14 = IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
						"gop1","123456");
				imSilGop3_axis14 = IntegrationManager.getIntegrationManagerMessageBox_axis14(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
						"gop3","123456");

			}
			org.openspcoop2.pdd.services.cxf.MessageBox imSilGop1_cxf = null;
			org.openspcoop2.pdd.services.cxf.MessageBox imSilGop3_cxf = null;
			if(use_cxf_engine){
				imSilGop1_cxf = IntegrationManager.getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
						"gop1","123456");
				imSilGop3_cxf = IntegrationManager.getIntegrationManagerMessageBox_cxf(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"), 
						"gop3","123456");
			}

			// Prelievo
			if(use_axis14_engine){
				String[]ids = imSilGop1_axis14.getAllMessagesId();
				Assert.assertTrue(ids!=null);
				Assert.assertTrue(ids.length==1);
				Assert.assertTrue(ids[0].equals(id));

				ids = imSilGop3_axis14.getAllMessagesId();
				Assert.assertTrue(ids!=null);
				Assert.assertTrue(ids.length==1);
				Assert.assertTrue(ids[0].equals(id));
			}
			else if(use_cxf_engine){
				List<String> idsList = imSilGop1_cxf.getAllMessagesId();
				Assert.assertTrue(idsList!=null);
				Assert.assertTrue(idsList.size()==1);
				Assert.assertTrue(idsList.get(0).equals(id));

				idsList = imSilGop3_cxf.getAllMessagesId();
				Assert.assertTrue(idsList!=null);
				Assert.assertTrue(idsList.size()==1);
				Assert.assertTrue(idsList.get(0).equals(id));
			}

			// Clean
			if(use_axis14_engine){
				try{
					imSilGop1_axis14.deleteAllMessages();
				}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
					Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
				}
				try{
					imSilGop3_axis14.deleteAllMessages();
				}catch(org.openspcoop2.pdd.services.axis14.IntegrationManagerException e){
					Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getCodiceEccezione());
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getCodiceEccezione()));
				}
			}
			else if(use_cxf_engine){
				try{
					imSilGop1_cxf.deleteAllMessages();
				}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
					Reporter.log("Pulizia repository SilGop1 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
				}
				try{
					imSilGop3_cxf.deleteAllMessages();
				}catch(org.openspcoop2.pdd.services.cxf.IntegrationManagerException_Exception e){
					Reporter.log("Pulizia repository SilGop3 MessageBox non riuscito, codice eccezione: "+e.getFaultInfo().getCodiceEccezione());
					Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_406_INTEGRATION_MANAGER_MESSAGGI_FOR_SIL_NON_TROVATI).equals(e.getFaultInfo().getCodiceEccezione()));
				}
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}









	/* ************** CASI ERRATI ********************* */

	/***
	 * Test per ASINCRONI
	 */
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".ASINCRONI"})
	public void invokePD_ASINCRONI() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_ASINCRONI+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String infoServizio = "( Servizio v1 SPC/RichiestaStatoAvanzamentoAsincronoAsimmetrico Azione richiestaAsincrona Erogatore SPC/MinisteroErogatore )";
				String msgErrore = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" profilo di collaborazione AsincronoAsimmetrico non supportato";

				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR), 
							msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (435): " + error.getFaultCode().getLocalPart());
			}

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






	/***
	 * Test per SINCRONO_STATEFUL
	 */
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SINCRONO_STATEFUL"})
	public void invokePD_SINCRONO_STATEFUL() throws TestSuiteException, Exception{

		if(this.doTestStateful==false){
			return;
		}

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO_STATEFUL);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO_STATEFUL+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SINCRONO_STATEFUL+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String infoServizio = "( Servizio v1 SPC/RichiestaStatoAvanzamento Azione stateful Erogatore SPC/MinisteroErogatore )";
				String msgErrore = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" profilo di collaborazione Sincrono non supportato nella modalità stateful";

				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR), 
							msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (435): " + error.getFaultCode().getLocalPart());
			}

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






	/***
	 * Test per SOGGETTO_NON_LOCALE
	 */
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SOGGETTO_NON_LOCALE"})
	public void invokePD_SOGGETTO_NON_LOCALE() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOGGETTO_NON_LOCALE);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOGGETTO_NON_LOCALE+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOGGETTO_NON_LOCALE+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String infoServizio = "( Servizio v1 SPC/ComunicazioneVariazione Erogatore SPC/SoggettoConnettoreErrato )";
				String msgErrore = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" il soggetto erogatore non risulta essere gestito localmente dalla Porta";
				String msgErrore2 = CostantiErroriIntegrazione.MSG_435_LOCAL_FORWARD_CONFIG_ERRORE+ infoServizio+" non risulta esistere una porta applicativa associata al servizio richiesto";

				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR).equals(error.getFaultCode().getLocalPart())){
					try{
						Utilities.verificaFaultIntegrazione(error, 
								this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
								Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR), 
								msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
					}catch(Throwable e){
						// test per la configurazione su db
						Utilities.verificaFaultIntegrazione(error, 
								this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
								Utilities.toString(CodiceErroreIntegrazione.CODICE_435_LOCAL_FORWARD_CONFIG_ERROR), 
								msgErrore2, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);	
					}
				}
				else{
					Assert.assertTrue(false,"FaultCode non tra quelli attesi (435): " + error.getFaultCode().getLocalPart());
				}
			}

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











	/* *********** WSS ENCRYPT **************** */

	/***
	 * Test per WSS_ENCRYPT_REQUEST
	 */
	Date testWSS_ENCRYPT_REQUEST;
	Repository repositoryWSS_ENCRYPT_REQUEST=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_REQUEST"})
	public void invokePD_WSS_ENCRYPT_REQUEST() throws TestSuiteException, Exception{

		this.testWSS_ENCRYPT_REQUEST = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_ENCRYPT_REQUEST);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_REQUEST);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()==false); // per via dell'encrypt il body sara' criptato, poiche' c'e' il servizio di Echo.
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getLocalName());
		Assert.assertTrue("EncryptedData".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getLocalName()));
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getNamespaceURI());
		Assert.assertTrue("http://www.w3.org/2001/04/xmlenc#".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getNamespaceURI()));
		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList!=null && nodeList.getLength()==1);
	}
	@DataProvider (name="Provider_WSS_ENCRYPT_REQUEST")
	public Object[][]provider_WSS_ENCRYPT_REQUEST() throws Exception{
		String id=this.repositoryWSS_ENCRYPT_REQUEST.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_REQUEST"},dataProvider="Provider_WSS_ENCRYPT_REQUEST",dependsOnMethods={"invokePD_WSS_ENCRYPT_REQUEST"})
	public void verifica_WSS_ENCRYPT_REQUEST(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_REQUEST, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_REQUEST, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}





	/***
	 * Test per WSS_ENCRYPT_DECRYPT_REQUEST
	 */
	Date testWSS_ENCRYPT_DECRYPT_REQUEST;
	Repository repositoryWSS_ENCRYPT_DECRYPT_REQUEST=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_DECRYPT_REQUEST"})
	public void invokePD_WSS_ENCRYPT_DECRYPT_REQUEST() throws TestSuiteException, Exception{

		this.testWSS_ENCRYPT_DECRYPT_REQUEST = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_ENCRYPT_DECRYPT_REQUEST);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_DECRYPT_REQUEST);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getLocalName());
		Assert.assertFalse("EncryptedData".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getLocalName()));
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getNamespaceURI());
		Assert.assertFalse("http://www.w3.org/2001/04/xmlenc#".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getNamespaceURI()));
		//		client.getResponseMessage().writeTo(System.out);
		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList==null || nodeList.getLength()==0);
	}
	@DataProvider (name="Provider_WSS_ENCRYPT_DECRYPT_REQUEST")
	public Object[][]provider_WSS_ENCRYPT_DECRYPT_REQUEST() throws Exception{
		String id=this.repositoryWSS_ENCRYPT_DECRYPT_REQUEST.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_DECRYPT_REQUEST"},dataProvider="Provider_WSS_ENCRYPT_DECRYPT_REQUEST",dependsOnMethods={"invokePD_WSS_ENCRYPT_DECRYPT_REQUEST"})
	public void verifica_WSS_ENCRYPT_DECRYPT_REQUEST(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_DECRYPT_REQUEST, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_DECRYPT_REQUEST, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}






	/***
	 * Test per DECRYPT_REQUEST
	 */
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".DECRYPT_REQUEST"})
	public void invokePD_DECRYPT_REQUEST() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_DECRYPT_REQUEST);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_DECRYPT_REQUEST+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_DECRYPT_REQUEST+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String msgErrore = "Header Message Security, richiesto dalla configurazione (action:Encrypt), non riscontrato nella SOAPEnvelope ricevuta";

				if(Utilities.toString(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_PRESENTE).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreCooperazione.SICUREZZA_CIFRATURA_NON_PRESENTE), 
							msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (200): " + error.getFaultCode().getLocalPart());
			}

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




	/***
	 * Test per WSS_ENCRYPT_RESPONSE
	 */
	Date testWSS_ENCRYPT_RESPONSE;
	Repository repositoryWSS_ENCRYPT_RESPONSE=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_RESPONSE"})
	public void invokePD_WSS_ENCRYPT_RESPONSE() throws TestSuiteException, Exception{

		this.testWSS_ENCRYPT_RESPONSE = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_ENCRYPT_RESPONSE);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_RESPONSE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()==false); // per via dell'encrypt il body sara' criptato
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getLocalName());
		Assert.assertTrue("EncryptedData".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getLocalName()));
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getNamespaceURI());
		Assert.assertTrue("http://www.w3.org/2001/04/xmlenc#".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getNamespaceURI()));
		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList!=null && nodeList.getLength()==1);
	}
	@DataProvider (name="Provider_WSS_ENCRYPT_RESPONSE")
	public Object[][]provider_WSS_ENCRYPT_RESPONSE() throws Exception{
		String id=this.repositoryWSS_ENCRYPT_RESPONSE.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_RESPONSE"},dataProvider="Provider_WSS_ENCRYPT_RESPONSE",dependsOnMethods={"invokePD_WSS_ENCRYPT_RESPONSE"})
	public void verifica_WSS_ENCRYPT_RESPONSE(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_RESPONSE, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_RESPONSE, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}




	/***
	 * Test per WSS_ENCRYPT_DECRYPT_RESPONSE
	 */
	Date testWSS_ENCRYPT_DECRYPT_RESPONSE;
	Repository repositoryWSS_ENCRYPT_DECRYPT_RESPONSE=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_DECRYPT_RESPONSE"})
	public void invokePD_WSS_ENCRYPT_DECRYPT_RESPONSE() throws TestSuiteException, Exception{

		this.testWSS_ENCRYPT_DECRYPT_RESPONSE = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_ENCRYPT_DECRYPT_RESPONSE);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_ENCRYPT_DECRYPT_RESPONSE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getLocalName());
		Assert.assertFalse("EncryptedData".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getLocalName()));
		//System.out.println(client.getResponseMessage().getSOAPBody().getFirstChild().getNamespaceURI());
		Assert.assertFalse("http://www.w3.org/2001/04/xmlenc#".equals(SoapUtils.getFirstNotEmptyChildNode(client.getResponseMessage().getSOAPBody()).getNamespaceURI()));
		//		client.getResponseMessage().writeTo(System.out);
		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList==null || nodeList.getLength()==0);
	}
	@DataProvider (name="Provider_WSS_ENCRYPT_DECRYPT_RESPONSE")
	public Object[][]provider_WSS_ENCRYPT_DECRYPT_RESPONSE() throws Exception{
		String id=this.repositoryWSS_ENCRYPT_DECRYPT_RESPONSE.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_ENCRYPT_DECRYPT_RESPONSE"},dataProvider="Provider_WSS_ENCRYPT_DECRYPT_RESPONSE",dependsOnMethods={"invokePD_WSS_ENCRYPT_DECRYPT_RESPONSE"})
	public void verifica_WSS_ENCRYPT_DECRYPT_RESPONSE(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_DECRYPT_RESPONSE, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_ENCRYPT_DECRYPT_RESPONSE, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}











	/* *********** WSS SIGNATURE **************** */

	/***
	 * Test per WSS_SIGNATURE_REQUEST
	 */
	Date testWSS_SIGNATURE_REQUEST;
	Repository repositoryWSS_SIGNATURE_REQUEST=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_REQUEST"})
	public void invokePD_WSS_SIGNATURE_REQUEST() throws TestSuiteException, Exception{

		this.testWSS_SIGNATURE_REQUEST = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_SIGNATURE_REQUEST);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_REQUEST);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 

		NamedNodeMap attributi = client.getResponseMessage().getSOAPBody().getAttributes();
		Assert.assertTrue(attributi!=null && attributi.getLength()>0);
		boolean foundId = false;
		for (int i = 0; i < attributi.getLength(); i++) {
			Node n = attributi.item(i);
			if("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(n.getNamespaceURI()) &&
					"Id".equals(n.getLocalName())){
				foundId = true;
			}
			//System.out.println("["+n.getLocalName()+"]["+n.getNamespaceURI()+"]");
		}
		Assert.assertTrue(foundId);

		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList!=null && nodeList.getLength()==1);
	}
	@DataProvider (name="Provider_WSS_SIGNATURE_REQUEST")
	public Object[][]provider_WSS_SIGNATURE_REQUEST() throws Exception{
		String id=this.repositoryWSS_SIGNATURE_REQUEST.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_REQUEST"},dataProvider="Provider_WSS_SIGNATURE_REQUEST",dependsOnMethods={"invokePD_WSS_SIGNATURE_REQUEST"})
	public void verifica_WSS_SIGNATURE_REQUEST(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_REQUEST, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_REQUEST, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}





	/***
	 * Test per WSS_SIGNATURE_VERIFY_REQUEST
	 */
	Date testWSS_SIGNATURE_VERIFY_REQUEST;
	Repository repositoryWSS_SIGNATURE_VERIFY_REQUEST=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_VERIFY_REQUEST"})
	public void invokePD_WSS_SIGNATURE_VERIFY_REQUEST() throws TestSuiteException, Exception{

		this.testWSS_SIGNATURE_VERIFY_REQUEST = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_SIGNATURE_VERIFY_REQUEST);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_VERIFY_REQUEST);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 

		NamedNodeMap attributi = client.getResponseMessage().getSOAPBody().getAttributes();
		//Assert.assertTrue(attributi==null || attributi.getLength()<=0);
		boolean foundId = false;
		if(attributi!=null){
			for (int i = 0; i < attributi.getLength(); i++) {
				Node n = attributi.item(i);
				if("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(n.getNamespaceURI()) &&
						"Id".equals(n.getLocalName())){
					foundId = true;
				}
				//System.out.println("["+n.getLocalName()+"]["+n.getNamespaceURI()+"]");
			}
		}
		Assert.assertTrue(foundId==false);

		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList==null || nodeList.getLength()==0);
	}
	@DataProvider (name="Provider_WSS_SIGNATURE_VERIFY_REQUEST")
	public Object[][]provider_WSS_SIGNATURE_VERIFY_REQUEST() throws Exception{
		String id=this.repositoryWSS_SIGNATURE_VERIFY_REQUEST.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_VERIFY_REQUEST"},dataProvider="Provider_WSS_SIGNATURE_VERIFY_REQUEST",dependsOnMethods={"invokePD_WSS_SIGNATURE_VERIFY_REQUEST"})
	public void verifica_WSS_SIGNATURE_VERIFY_REQUEST(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_VERIFY_REQUEST, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_VERIFY_REQUEST, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}





	/***
	 * Test per VERIFY_REQUEST
	 */
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".VERIFY_REQUEST"})
	public void invokePD_VERIFY_REQUEST() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_VERIFY_REQUEST);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_VERIFY_REQUEST+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_VERIFY_REQUEST+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				String msgErrore = "Header Message Security, richiesto dalla configurazione (action:Signature), non riscontrato nella SOAPEnvelope ricevuta";

				if(Utilities.toString(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"RicezioneContenutiApplicativiSOAP", 
							Utilities.toString(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_PRESENTE), 
							msgErrore, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (200): " + error.getFaultCode().getLocalPart());
			}

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





	/***
	 * Test per WSS_SIGNATURE_RESPONSE
	 */
	Date testWSS_SIGNATURE_RESPONSE;
	Repository repositoryWSS_SIGNATURE_RESPONSE=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_RESPONSE"})
	public void invokePD_WSS_SIGNATURE_RESPONSE() throws TestSuiteException, Exception{

		this.testWSS_SIGNATURE_RESPONSE = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_SIGNATURE_RESPONSE);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_RESPONSE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 

		NamedNodeMap attributi = client.getResponseMessage().getSOAPBody().getAttributes();
		Assert.assertTrue(attributi!=null && attributi.getLength()>0);
		boolean foundId = false;
		for (int i = 0; i < attributi.getLength(); i++) {
			Node n = attributi.item(i);
			if("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(n.getNamespaceURI()) &&
					"Id".equals(n.getLocalName())){
				foundId = true;
			}
			//System.out.println("["+n.getLocalName()+"]["+n.getNamespaceURI()+"]");
		}
		Assert.assertTrue(foundId);

		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList!=null && nodeList.getLength()==1);
	}
	@DataProvider (name="Provider_WSS_SIGNATURE_RESPONSE")
	public Object[][]provider_WSS_SIGNATURE_RESPONSE() throws Exception{
		String id=this.repositoryWSS_SIGNATURE_RESPONSE.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_RESPONSE"},dataProvider="Provider_WSS_SIGNATURE_RESPONSE",dependsOnMethods={"invokePD_WSS_SIGNATURE_RESPONSE"})
	public void verifica_WSS_SIGNATURE_RESPONSE(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_RESPONSE, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_RESPONSE, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}




	/***
	 * Test per WSS_SIGNATURE_VERIFY_RESPONSE
	 */
	Date testWSS_SIGNATURE_VERIFY_RESPONSE;
	Repository repositoryWSS_SIGNATURE_VERIFY_RESPONSE=new Repository();
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_VERIFY_RESPONSE"})
	public void invokePD_WSS_SIGNATURE_VERIFY_RESPONSE() throws TestSuiteException, Exception{

		this.testWSS_SIGNATURE_VERIFY_RESPONSE = new Date();
		
		ClientSincrono client=new ClientSincrono(this.repositoryWSS_SIGNATURE_VERIFY_RESPONSE);
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_WSS_SIGNATURE_VERIFY_RESPONSE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false,addIDUnivoco);
		client.run();

		Assert.assertTrue(client.isEqualsSentAndResponseMessage()); 

		NamedNodeMap attributi = client.getResponseMessage().getSOAPBody().getAttributes();
		//Assert.assertTrue(attributi==null || attributi.getLength()<=0);
		boolean foundId = false;
		if(attributi!=null){
			for (int i = 0; i < attributi.getLength(); i++) {
				Node n = attributi.item(i);
				if("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd".equals(n.getNamespaceURI()) &&
						"Id".equals(n.getLocalName())){
					foundId = true;
				}
				//System.out.println("["+n.getLocalName()+"]["+n.getNamespaceURI()+"]");
			}
		}
		Assert.assertTrue(foundId==false);

		NodeList nodeList = client.getResponseMessage().getSOAPHeader().getElementsByTagNameNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security");
		Assert.assertTrue(nodeList==null || nodeList.getLength()==0);
	}
	@DataProvider (name="Provider_WSS_SIGNATURE_VERIFY_RESPONSE")
	public Object[][]provider_WSS_SIGNATURE_VERIFY_RESPONSE() throws Exception{
		String id=this.repositoryWSS_SIGNATURE_VERIFY_RESPONSE.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".WSS_SIGNATURE_VERIFY_RESPONSE"},dataProvider="Provider_WSS_SIGNATURE_VERIFY_RESPONSE",dependsOnMethods={"invokePD_WSS_SIGNATURE_VERIFY_RESPONSE"})
	public void verifica_WSS_SIGNATURE_VERIFY_RESPONSE(DatabaseComponent data,DatabaseMsgDiagnosticiComponent msgDiag,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento non eseguito per id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==false);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_VERIFY_RESPONSE, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testWSS_SIGNATURE_VERIFY_RESPONSE, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}
	}














	/* ************** CASI ERRORE **************** */

	/***
	 * Test per CONNETTORE_ERRATO_STATEFUL
	 */
	Date testPD_CONNETTORE_ERRATO_STATEFUL;
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".CONNETTORE_ERRATO_STATEFUL"})
	public void invokePD_CONNETTORE_ERRATO_STATEFUL() throws TestSuiteException, Exception{

		this.testPD_CONNETTORE_ERRATO_STATEFUL = new Date();
		
		if(this.doTestStateful==false){
			return;
		}

		Date dataInizioTest = DateManager.getDate();

		//		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent msgDiag = null;
		try{
			Repository r = new Repository();
			ClientHttpGenerico client=new ClientHttpGenerico(r);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATEFUL);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			//			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
			//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
			dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			//
			//				client.setAttesaTerminazioneMessaggi(true);
			//				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			//				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			//			}
			client.run();

			try{
				Thread.sleep(3000);
			}catch(Exception e){}

			String id = r.getNext();

			String motivoErroreProcessamento = dbComponentErogatore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "INBOX");
			//System.out.println("A:"+motivoErroreProcessamento);
			Assert.assertTrue(motivoErroreProcessamento!=null && motivoErroreProcessamento.contains("Errore avvenuto durante la consegna HTTP: Connection refused"));

			dbComponentErogatore.getVerificatoreMessaggi().deleteMessage(id, "INBOX", Utilities.testSuiteProperties.isUseTransazioni());

			msgDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==true);

			Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_CONNETTORE_ERRATO_STATEFUL, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_CONNETTORE_ERRATO_STATEFUL, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","001005","007011","007013") ||
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","001005", "007013") || 
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007013", "001005"));

		}catch(Exception e){
			throw e;
		}finally{
			//			try{
			//				dbComponentFruitore.close();
			//			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}

		Date dataFineTest = DateManager.getDate();

		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}





	/***
	 * Test per CONNETTORE_ERRATO_STATELESS
	 */
	Date testPD_CONNETTORE_ERRATO_STATELESS;
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".CONNETTORE_ERRATO_STATELESS"})
	public void invokePD_CONNETTORE_ERRATO_STATELESS() throws TestSuiteException, Exception{
		
		this.testPD_CONNETTORE_ERRATO_STATELESS = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent msgDiag = null;
		Date dataInizioTest = DateManager.getDate();

		try{

			Repository r = new Repository();

			ClientHttpGenerico client=new ClientHttpGenerico(r);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATELESS);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATELESS+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_CONNETTORE_ERRATO_STATELESS+") non ha causato errori.");

			} catch (AxisFault error) {

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				if(Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE).equals(error.getFaultCode().getLocalPart()))
					Utilities.verificaFaultIntegrazione(error, 
							this.collaborazioneSPCoopBase.getMittente().getCodicePorta(),"ConsegnaContenutiApplicativi", 
							Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE), 
							CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_CONTAINS);	
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (516): " + error.getFaultCode().getLocalPart());
				
				try{
					Thread.sleep(3000); // versione streaming necessita di un po di tempo
				}catch(Exception e){}
				
				msgDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();

				String id = r.getNext();

				Reporter.log("Controllo msg diag local forward per id: " +id);
				Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

				Reporter.log("Controllo msg diag local forward per id senza errori: " +id);
				Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==true);

				Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
				Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_CONNETTORE_ERRATO_STATELESS, "001039"));
				Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_CONNETTORE_ERRATO_STATELESS, "001003"));
				Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007013","001006"));
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}


		Date dataFineTest = DateManager.getDate();

		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}





	/***
	 * Test per SOAP_FAULT_STATEFUL
	 */
	Date testPA_CONNETTORE_ERRATO_STATEFUL;
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SOAP_FAULT_STATEFUL"})
	public void invokePD_SOAP_FAULT_STATEFUL() throws TestSuiteException, Exception{

		this.testPA_CONNETTORE_ERRATO_STATEFUL = new Date();
		
		if(this.doTestStateful==false){
			return;
		}

		//
		//		Date dataInizioTest = DateManager.getDate();
		//		
		//		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent msgDiag = null;
		try{
			Repository r = new Repository();
			ClientHttpGenerico client=new ClientHttpGenerico(r);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATEFUL);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			//			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
			//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
			dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
			//
			//				client.setAttesaTerminazioneMessaggi(true);
			//				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			//				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			//			}
			client.run();

			try{
				Thread.sleep(3000);
			}catch(Exception e){}

			String id = r.getNext();

			//String motivoErroreProcessamento = dbComponentErogatore.getVerificatoreMessaggi().getMotivoErroreProcessamentoMessaggio(id, "INBOX");
			//System.out.println("A:"+motivoErroreProcessamento);
			//Assert.assertTrue(motivoErroreProcessamento==null);

			//dbComponentErogatore.getVerificatoreMessaggi().deleteMessage(id, "INBOX");

			msgDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();

			Reporter.log("Controllo msg diag local forward per id: " +id);
			Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

			Reporter.log("Controllo msg diag local forward per id con errori (segnalazioneFault): " +id);
			Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==true);

			Reporter.log("Controllo che vi sia solo un errore di livello error (segnalazioneFault): " +id);
			Assert.assertTrue(msgDiag.countSeveritaLessEquals(id, LogLevels.SEVERITA_ERROR_INTEGRATION)==1);

			Reporter.log("Controllo msg diag local forward solo codici attesi (tra cui quello che segnala il fault): " +id);
			Assert.assertTrue(msgDiag.isTracedCodice(this.testPA_CONNETTORE_ERRATO_STATEFUL, "001039"));
			Assert.assertTrue(msgDiag.isTracedCodice(this.testPA_CONNETTORE_ERRATO_STATEFUL, "001003"));
			Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","001005","007011","007012","007014") ||
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","001005","007012","007014") ||
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","001005","007014") || 
					msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","007014","001005"));

		}catch(Exception e){
			throw e;
		}finally{
			//			try{
			//				dbComponentFruitore.close();
			//			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}

		//		Date dataFineTest = DateManager.getDate();

		//		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		//		err.setIntervalloInferiore(dataInizioTest);
		//		err.setIntervalloSuperiore(dataFineTest);
		//		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		//		this.erroriAttesiOpenSPCoopCore.add(err);
	}







	/***
	 * Test per SOAP_FAULT_STATELESS
	 */
	Date testPD_SOAP_FAULT_STATELESS;
	@Test(groups={LocalForward.ID_GRUPPO,LocalForward.ID_GRUPPO+".SOAP_FAULT_STATELESS"})
	public void invokePD_SOAP_FAULT_STATELESS() throws TestSuiteException, Exception{
		
		this.testPD_SOAP_FAULT_STATELESS = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		DatabaseMsgDiagnosticiComponent msgDiag = null;
		//		Date dataInizioTest = DateManager.getDate();

		try{

			Repository r = new Repository();

			ClientHttpGenerico client=new ClientHttpGenerico(r);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATELESS);
			client.connectToSoapEngine();
			// NOTA: invio un messaggio malformato forzando il test in modo da mangare un soap with attachments come fosse un messaggio senza attachments
			client.setMessageFromFile(Utilities.testSuiteProperties.getLocalForwardFileName(), false);
			//client.setMessaggioXMLRichiesta("CONTENTUO_ERRATO".getBytes()); QUESTO non rilancia il soap fault
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();

				Reporter.log("Invocazione porta delegata  (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATELESS+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_LOCAL_FORWARD_SOAP_FAULT_STATELESS+") non ha causato errori.");

			} catch (AxisFault error) {
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);

				if("Server.faultExample".equals(error.getFaultCode().getLocalPart()))
					Assert.assertTrue("Fault ritornato dalla servlet di esempio di OpenSPCoop".equals(error.getFaultString()));
				else Assert.assertTrue(false,"FaultCode non tra quelli attesi (FaultExample): " + error.getFaultCode().getLocalPart());

				try{
					Thread.sleep(3000); // versione streaming necessita di un po di tempo
				}catch(Exception e){}
				
				msgDiag = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();

				String id = r.getNext();

				Reporter.log("Controllo msg diag local forward per id: " +id);
				Assert.assertTrue(msgDiag.isTracedMessaggioWithLike(id, MSG_LOCAL_FORWARD));

				Reporter.log("Controllo msg diag local forward per id con errori (segnalazioneFault): " +id);
				Assert.assertTrue(msgDiag.isTracedErrorMsg(id)==true);

				Reporter.log("Controllo che vi sia solo un errore di livello error (segnalazioneFault): " +id);
				Assert.assertTrue(msgDiag.countSeveritaLessEquals(id, LogLevels.SEVERITA_ERROR_INTEGRATION)==1);

				Reporter.log("Controllo msg diag local forward solo codici attesi: " +id);
				Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_SOAP_FAULT_STATELESS, "001039"));
				Assert.assertTrue(msgDiag.isTracedCodice(this.testPD_SOAP_FAULT_STATELESS, "001003"));
				Assert.assertTrue(msgDiag.isTracedMessaggiWithCode(id, "001034","007011","007012","007014","001006"));
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				msgDiag.close();
			}catch(Exception eClose){}
		}

		//		
		//		Date dataFineTest = DateManager.getDate();
		//		
		//		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		//		err.setIntervalloInferiore(dataInizioTest);
		//		err.setIntervalloSuperiore(dataFineTest);
		//		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		//		this.erroriAttesiOpenSPCoopCore.add(err);
	}
}
