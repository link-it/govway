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

import java.io.FileInputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.handler.TestContext;
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
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Handlers {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "Handlers";
	



	// TODO:
	
	// AGGIUNGERE CONTROLLO EQUALS NEI SINCRONI (attachments dava errore)
	
	// IMPLEMENTARE CONTROLLI PER PROFILI ASINCRONI STATELESS/STATEFULL  e corrispettivi LINEE GUIDA (collaborazione!)!
	
	// ERRORI DI VARIO GENERE
		
	// INTEGRATION MANAGER
	
	// IDENTITA SERVIZIO APPLICATIVO FRUIORE E EROGATORE!
	
	// CORRELAZIONE APPLICATIVA
	
	
	private Date dataAvvioGruppoTest = null;
	private boolean doTestStateful = true;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		verificaInstallazioneHandler();
		
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
	
	//  !!!! NOTA !!!: i test con attachments sono implementati tramite data provider!
	@DataProvider (name="TipologieTests")
	public Object[][]TipologieTests() throws Exception{
		return new Object[][]{
				{false}, // senza attachments	
				{true}	// con attachments
		};
	}
	
	public void verificaInstallazioneHandler() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = DateManager.getDate();
		
		FileInputStream fin = null;
		ClientHttpGenerico client=null;
		try{
			Message msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
			msg.getSOAPPartAsBytes();
			
			client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);
			client.setProperty(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_GENERA_ERRORE, "true"); // genero errore 
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			client.run();
			
		}catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			if(!Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO).equals(error.getFaultCode().getLocalPart())){
				throw new Exception("Archivio 'openspcoop2_spcoop-testsuite_*.jar' non sembra essere installato correttamente nella Porta di Dominio. L'archivio e' fondamentale per la riuscita del test. Errore avvenuto: ("+error.getFaultCode().getLocalPart()+") "+error.toString(),error);
			}
		}catch(Exception e){
			throw new Exception("Archivio 'openspcoop2_spcoop-testsuite_*.jar' non sembra essere installato correttamente nella Porta di Dominio. L'archivio e' fondamentale per la riuscita del test. Errore avvenuto: "+e.getMessage(),e);
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception eClose){}
			try{
				dbComponentErogatore.close();
			}catch(Exception eClose){}
			try{
				fin.close();
			}catch(Exception eClose){}
		}
		
		DatabaseMsgDiagnosticiComponent dataFruitore = null;
		try{
			dataFruitore = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
			Reporter.log("Verifico msg diag per id: "+client.getIdMessaggio());
			if(!dataFruitore.isTracedMessaggio(client.getIdMessaggio(), "Riscontrato errore durante la gestione del messaggio [inRequestProtocolHandlers[testsuite]]: "+org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE+": ERRORE RICHIESTO DA TESTSUITE HANDLER")){
				throw new Exception("Archivio 'openspcoop2_spcoop-testsuite_*.jar' non sembra essere installato correttamente nella Porta di Dominio. L'archivio e' fondamentale per la riuscita del test");
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataFruitore.close();
			}catch(Exception eClose){}
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("ERRORE RICHIESTO DA TESTSUITE HANDLER");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/***
	 * Test per il profilo di collaborazione OneWay stateful
	 */
	Repository repositoryOneWay=new Repository();

	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_STATEFUL"},dataProvider="TipologieTests")
	public void oneWay(Boolean attachments) throws TestSuiteException, Exception{
		
		if(this.doTestStateful==false){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		FileInputStream fin = null;
		try{
			
			TestContext test = new TestContext();
			test.setDataInizioTest(DateManager.getDate());
			test.setEsito(EsitoTransazioneName.OK);
			test.setRispostaVuotaSA_PD(false); // header di integrazione OpenSPCoop
			test.setRispostaVuotaPD_PA(true);
			test.setRispostaVuotaPA_SA(false); // testsuite ritorna l'echo :-( Implementare poi un vero test vuoto
			test.setRispostaVuotaPA_PD(true);
			test.setReturnCodePDReq(200);
			test.setReturnCodePDRes(200);
			test.setReturnCodePAReq(200);
			test.setReturnCodePARes(200);
			test.setLocationPD("http://localhost:8080/openspcoop2/spcoop/PA");
			test.setLocationPA("http://localhost:8080/OpenSPCoop2TestSuite/server");
			test.setTipoConnettorePD("http");
			test.setTipoConnettorePA("http");
			ProtocolContext egovContext = new ProtocolContext();
			egovContext.setFruitore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE));
			egovContext.setErogatore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE));
			egovContext.setIdRichiesta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setIdRisposta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_NULL);
			egovContext.setProfiloCollaborazione(ProfiloDiCollaborazione.ONEWAY, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY);
			egovContext.setCollaborazione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL);
			egovContext.setScenarioCooperazione(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO);
			egovContext.setTipoServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY);
			egovContext.setServizio(CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
			egovContext.setAzione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_AZIONE_VERIFICA_NULL);
			test.setEgovContext(egovContext);
			test.setCorrelazioneApplicativaPDReq(null);
			test.setCorrelazioneApplicativaPAReq(null);
			test.setServizioApplicativoFruitore(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO);
			test.setServizioApplicativoErogatore("OpenSPCoop2TestSuite");
			test.setStatelessPD(false);
			test.setStatelessPA(false);
			Properties testP = new Properties();
			test.writeTo(testP);
			
			Message msg=null;
			if(attachments){
				msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			}else{
				msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
				msg.getSOAPPartAsBytes();
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);			
			client.setProperties(testP);
			client.setRispostaDaGestire(true);
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
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="OneWay")
	public Object[][]testOneWay() throws Exception{
		
		if(this.doTestStateful==false){
			return new Object[][]{
					{null,null}	
			};
		}
		
		String id=this.repositoryOneWay.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_STATEFUL"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
	public void testOneWay(DatabaseMsgDiagnosticiComponent data,String id) throws Exception{
		
		if(this.doTestStateful==false){
			return;
		}
		
		try{
			Assert.assertTrue(!data.isTracedMessaggioWithLike(id,org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay stateless
	 */
	Repository repositoryOneWay_Stateless=new Repository();
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_STATELESS"},dataProvider="TipologieTests")
	public void oneWay_Stateless(boolean attachments) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		FileInputStream fin = null;
		try{
			
			TestContext test = new TestContext();
			test.setDataInizioTest(DateManager.getDate());
			test.setEsito(EsitoTransazioneName.OK);
			test.setRispostaVuotaSA_PD(false); // header di integrazione OpenSPCoop
			test.setRispostaVuotaPD_PA(true);
			test.setRispostaVuotaPA_SA(false); // testsuite ritorna l'echo :-( Implementare poi un vero test vuoto
			test.setRispostaVuotaPA_PD(true);
			test.setReturnCodePDReq(200);
			test.setReturnCodePDRes(200);
			test.setReturnCodePAReq(200);
			test.setReturnCodePARes(200);
			test.setLocationPD("http://localhost:8080/openspcoop2/spcoop/PA");
			test.setLocationPA("http://localhost:8080/OpenSPCoop2TestSuite/server");
			test.setTipoConnettorePD("http");
			test.setTipoConnettorePA("http");
			ProtocolContext egovContext = new ProtocolContext();
			egovContext.setFruitore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE));
			egovContext.setErogatore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE));
			egovContext.setIdRichiesta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setIdRisposta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_NULL);
			egovContext.setProfiloCollaborazione(ProfiloDiCollaborazione.ONEWAY, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY);
			egovContext.setCollaborazione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL);
			egovContext.setScenarioCooperazione(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO);
			egovContext.setTipoServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY);
			egovContext.setServizio(CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
			egovContext.setAzione(CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS);
			test.setEgovContext(egovContext);
			test.setCorrelazioneApplicativaPDReq(null);
			test.setCorrelazioneApplicativaPAReq(null);
			test.setServizioApplicativoFruitore(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO);
			test.setServizioApplicativoErogatore("OpenSPCoop2TestSuite");
			test.setStatelessPD(true);
			test.setStatelessPA(true);
			Properties testP = new Properties();
			test.writeTo(testP);
			
			Message msg=null;
			if(attachments){
				msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			}else{
				msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
				msg.getSOAPPartAsBytes();
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay_Stateless);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);			
			client.setProperties(testP);
			client.setRispostaDaGestire(true);
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
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="OneWay_Stateless")
	public Object[][]testOneWay_Stateless() throws Exception{
		String id=this.repositoryOneWay_Stateless.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_STATELESS"},dataProvider="OneWay_Stateless",dependsOnMethods={"oneWay_Stateless"})
	public void testOneWay_Stateless(DatabaseMsgDiagnosticiComponent data,String id) throws Exception{
		try{
			Assert.assertTrue(!data.isTracedMessaggioWithLike(id,org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay con risposta vuota
	 */
	Repository repositoryOneWay_RispostaCompletamenteVuota=new Repository();
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_RISPOSTA_VUOTA"},dataProvider="TipologieTests")
	public void oneWay_RispostaCompletamenteVuota(boolean attachments) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		FileInputStream fin = null;
		try{
			
			TestContext test = new TestContext();
			test.setDataInizioTest(DateManager.getDate());
			test.setEsito(EsitoTransazioneName.OK);
			test.setRispostaVuotaSA_PD(true); 
			test.setRispostaVuotaPD_PA(true);
			test.setRispostaVuotaPA_SA(true);
			test.setRispostaVuotaPA_PD(true);
			test.setReturnCodePDReq(200);
			test.setReturnCodePDRes(200);
			test.setReturnCodePAReq(200);
			test.setReturnCodePARes(200);
			test.setLocationPD("http://localhost:8080/openspcoop2/spcoop/PA");
			test.setLocationPA("http://localhost:8080/OpenSPCoop2TestSuite/server?generazioneRisposta=false");
			test.setTipoConnettorePD("http");
			test.setTipoConnettorePA("http");
			ProtocolContext egovContext = new ProtocolContext();
			egovContext.setFruitore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE));
			egovContext.setErogatore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE));
			egovContext.setIdRichiesta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setIdRisposta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_NULL);
			egovContext.setProfiloCollaborazione(ProfiloDiCollaborazione.ONEWAY, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY);
			egovContext.setCollaborazione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL);
			egovContext.setScenarioCooperazione(Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO);
			egovContext.setTipoServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY);
			egovContext.setServizio(CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY);
			egovContext.setAzione(CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_RISPOSTA_VUOTA);
			test.setEgovContext(egovContext);
			test.setCorrelazioneApplicativaPDReq(null);
			test.setCorrelazioneApplicativaPAReq(null);
			test.setServizioApplicativoFruitore(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO);
			test.setServizioApplicativoErogatore("OpenSPCoop2TestSuite_InputOnly");
			test.setStatelessPD(true);
			test.setStatelessPA(true);
			Properties testP = new Properties();
			test.writeTo(testP);
			
			Message msg=null;
			if(attachments){
				msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			}else{
				msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
				msg.getSOAPPartAsBytes();
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay_RispostaCompletamenteVuota);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_RISPOSTA_VUOTA);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);			
			client.setProperties(testP);
			client.setRispostaDaGestire(true);
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
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="OneWay_RispostaCompletamenteVuota")
	public Object[][]testOneWay_RispostaCompletamenteVuota() throws Exception{
		String id=this.repositoryOneWay_RispostaCompletamenteVuota.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".ONEWAY_RISPOSTA_VUOTA"},dataProvider="OneWay_RispostaCompletamenteVuota",dependsOnMethods={"oneWay_RispostaCompletamenteVuota"})
	public void testOneWay_RispostaCompletamenteVuota(DatabaseMsgDiagnosticiComponent data,String id) throws Exception{
		try{
			Assert.assertTrue(!data.isTracedMessaggioWithLike(id,org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono stateful
	 */
	Repository repositorySincrono_Stateful=new Repository();
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".SINCRONO_STATEFUL"},dataProvider="TipologieTests")
	public void sincrono_Stateful(boolean attachments) throws TestSuiteException, Exception{
		
		if(this.doTestStateful==false){
			return;
		}
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		FileInputStream fin = null;
		try{
			
			TestContext test = new TestContext();
			test.setDataInizioTest(DateManager.getDate());
			test.setEsito(EsitoTransazioneName.OK);
			test.setRispostaVuotaSA_PD(false); 
			test.setRispostaVuotaPD_PA(false);
			test.setRispostaVuotaPA_SA(false); 
			test.setRispostaVuotaPA_PD(false);
			test.setReturnCodePDReq(200);
			test.setReturnCodePDRes(200);
			test.setReturnCodePAReq(200);
			test.setReturnCodePARes(200);
			test.setLocationPD("http://localhost:8080/openspcoop2/spcoop/PA");
			test.setLocationPA("http://localhost:8080/OpenSPCoop2TestSuite/server");
			test.setTipoConnettorePD("http");
			test.setTipoConnettorePA("http");
			ProtocolContext egovContext = new ProtocolContext();
			egovContext.setFruitore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE));
			egovContext.setErogatore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE));
			egovContext.setIdRichiesta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setIdRisposta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setProfiloCollaborazione(ProfiloDiCollaborazione.SINCRONO, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO);
			egovContext.setCollaborazione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL);
			egovContext.setScenarioCooperazione(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO);
			egovContext.setTipoServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO);
			egovContext.setServizio(CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
			egovContext.setAzione(CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_STATEFUL);
			test.setEgovContext(egovContext);
			test.setCorrelazioneApplicativaPDReq(null);
			test.setCorrelazioneApplicativaPAReq(null);
			test.setServizioApplicativoFruitore(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO);
			test.setServizioApplicativoErogatore("OpenSPCoop2TestSuite");
			test.setStatelessPD(false);
			test.setStatelessPA(false);
			Properties testP = new Properties();
			test.writeTo(testP);
			
			Message msg=null;
			if(attachments){
				msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			}else{
				msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
				msg.getSOAPPartAsBytes();
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincrono_Stateful);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);			
			client.setProperties(testP);
			client.setRispostaDaGestire(true);
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
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="Sincrono_Stateful")
	public Object[][]testSincrono_Stateful() throws Exception{
		
		if(this.doTestStateful==false){
			return new Object[][]{
					{null,null}	
			};
		}
		
		String id=this.repositorySincrono_Stateful.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".SINCRONO_STATEFUL"},dataProvider="Sincrono_Stateful",dependsOnMethods={"sincrono_Stateful"})
	public void testSincrono_Stateful(DatabaseMsgDiagnosticiComponent data,String id) throws Exception{
		
		if(this.doTestStateful==false){
			return;
		}
		
		try{
			Assert.assertTrue(!data.isTracedMessaggioWithLike(id,org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono stateless
	 */
	Repository repositorySincrono_Stateless=new Repository();
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".SINCRONO_STATELESS"},dataProvider="TipologieTests")
	public void sincrono_Stateless(boolean attachments) throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		FileInputStream fin = null;
		try{
			
			TestContext test = new TestContext();
			test.setDataInizioTest(DateManager.getDate());
			test.setEsito(EsitoTransazioneName.OK);
			test.setRispostaVuotaSA_PD(false); 
			test.setRispostaVuotaPD_PA(false);
			test.setRispostaVuotaPA_SA(false); 
			test.setRispostaVuotaPA_PD(false);
			test.setReturnCodePDReq(200);
			test.setReturnCodePDRes(200);
			test.setReturnCodePAReq(200);
			test.setReturnCodePARes(200);
			test.setLocationPD("http://localhost:8080/openspcoop2/spcoop/PA");
			test.setLocationPA("http://localhost:8080/OpenSPCoop2TestSuite/server");
			test.setTipoConnettorePD("http");
			test.setTipoConnettorePA("http");
			ProtocolContext egovContext = new ProtocolContext();
			egovContext.setFruitore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE));
			egovContext.setErogatore(new IDSoggetto(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE));
			egovContext.setIdRichiesta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setIdRisposta(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_ID_VERIFICA_BASE_MITTENTE_DESTINATARIO);
			egovContext.setProfiloCollaborazione(ProfiloDiCollaborazione.SINCRONO, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO);
			egovContext.setCollaborazione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_COLLABORAZIONE_VERIFICA_NULL);
			egovContext.setScenarioCooperazione(Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO);
			egovContext.setTipoServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO);
			egovContext.setServizio(CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO);
			egovContext.setAzione(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_EGOV_AZIONE_VERIFICA_NULL);
			test.setEgovContext(egovContext);
			test.setCorrelazioneApplicativaPDReq(null);
			test.setCorrelazioneApplicativaPAReq(null);
			test.setServizioApplicativoFruitore(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_SERVIZIO_APPLICATIVO_FRUITORE_VALUE_ANONIMO);
			test.setServizioApplicativoErogatore("OpenSPCoop2TestSuite");
			test.setStatelessPD(true);
			test.setStatelessPA(true);
			Properties testP = new Properties();
			test.writeTo(testP);
			
			Message msg=null;
			if(attachments){
				msg = org.openspcoop2.testsuite.core.Utilities.createMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false);
			}else{
				msg=new Message(fin = new FileInputStream(Utilities.testSuiteProperties.getSoap11FileName()));
				msg.getSOAPPartAsBytes();
			}
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincrono_Stateless);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
			client.connectToSoapEngine();			
			client.setSoapAction(org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.SOAP_ACTION_TEST_HANDLER_QUOTED);
			client.setMessage(msg);			
			client.setProperties(testP);
			client.setRispostaDaGestire(true);
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
			try{
				fin.close();
			}catch(Exception eClose){}
		}
	}
	@DataProvider (name="Sincrono_Stateless")
	public Object[][]testSincrono_Stateless() throws Exception{
		String id=this.repositorySincrono_Stateless.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id},	
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}	
		};
	}
	@Test(groups={Handlers.ID_GRUPPO,Handlers.ID_GRUPPO+".SINCRONO_STATELESS"},dataProvider="Sincrono_Stateless",dependsOnMethods={"sincrono_Stateless"})
	public void testSincrono_Stateless(DatabaseMsgDiagnosticiComponent data,String id) throws Exception{
		try{
			Assert.assertTrue(!data.isTracedMessaggioWithLike(id,org.openspcoop2.protocol.spcoop.testsuite.handler.Costanti.TEST_CONTEXT_PREFISSO_ERRORE));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	

	
}
