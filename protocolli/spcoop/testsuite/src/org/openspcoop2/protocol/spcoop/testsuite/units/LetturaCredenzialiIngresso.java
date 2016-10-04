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
import java.util.Hashtable;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.autenticazione.GestoreCredenzialiTest;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LetturaCredenzialiIngresso {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "LetturaCredenzialiIngresso";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());


	
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
	
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC = "Ottenute credenziali di accesso ( Basic Username: [@ID1@] ) fornite da GestoreCredenziali di test anonimo";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL = "Ottenute credenziali di accesso ( SSL Subject: [@ID1@] ) fornite da GestoreCredenziali di test anonimo";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC = "Ottenute credenziali di accesso ( Basic Username: [@ID1@] ) fornite da GestoreCredenziali di test ( Basic Username: [@ID2@] )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL = "Ottenute credenziali di accesso ( SSL Subject: [@ID1@] ) fornite da GestoreCredenziali di test ( Basic Username: [@ID2@] )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC = "Ottenute credenziali di accesso ( Basic Username: [@ID1@] ) fornite da GestoreCredenziali di test ( SSL Subject: [@ID2@] )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL = "Ottenute credenziali di accesso ( SSL Subject: [@ID1@] ) fornite da GestoreCredenziali di test ( SSL Subject: [@ID2@] )";
	
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC = "Ottenute credenziali di accesso ( Basic Username: [@ID1@] ) fornite da Proxy";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL = "Ottenute credenziali di accesso ( SSL Subject: [@ID1@] ) fornite da Proxy";
	
	private final static String MSG_COMPRENSIONE_IDENTITA_SA_BASIC = "Ricevuta richiesta di servizio dal Servizio Applicativo ( Basic Username: [@ID@] ) @ID_SIL@ verso la porta delegata @PD@";
	private final static String MSG_COMPRENSIONE_IDENTITA_SA_SSL = "Ricevuta richiesta di servizio dal Servizio Applicativo ( SSL Subject: [@ID@] ) @ID_SIL@ verso la porta delegata @PD@";
	
	private final static String MSG_COMPRENSIONE_IDENTITA_SOGGETTO_BASIC = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviata dalla parte mittente [@TIPO_MITTENTE@/@MITTENTE@] ( Basic Username: [@ID@] )";
	private final static String MSG_COMPRENSIONE_IDENTITA_SOGGETTO_SSL = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviata dalla parte mittente [@TIPO_MITTENTE@/@MITTENTE@] ( SSL Subject: [@ID@] )";
	
	private final static String MSG_COMPRENSIONE_FALLITO_IM_BASIC = "Autenticazione del servizio applicativo non riuscita ( Basic Username: @ID@ )";
	private final static String MSG_COMPRENSIONE_FALLITO_IM_SSL = "Autenticazione del servizio applicativo non riuscita ( SSL Subject: @ID@ )";
	private final static String MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE = "(Autenticazione basic) Autenticazione del servizio applicativo non riuscita: Credenziali fornite non corrette";	
	private final static String MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE = "(Autenticazione basic) Autenticazione del servizio applicativo non riuscita: Credenziali non fornite";	
	private final static String MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE = "(Autenticazione ssl) Autenticazione del servizio applicativo non riuscita: Credenziali fornite non corrette";	
	private final static String MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE = "(Autenticazione ssl) Autenticazione del servizio applicativo non riuscita: Credenziali non fornite";	
	
	private boolean checkFaultCode(CodiceErroreIntegrazione faultCodeIntegrazione,Element[] faults) throws ProtocolException{
		String faultCode = Utilities.toString(faultCodeIntegrazione);
		for(int i=0; i<faults.length; i++){
			Reporter.log("Ricevuto details ["+faults[i].getLocalName()+"] ["+faults[i].getNamespaceURI()+"]");
			if("IntegrationManagerException".equals(faults[i].getLocalName()) && "http://services.pdd.openspcoop2.org".equals(faults[i].getNamespaceURI())){
				NodeList faultsInterni = faults[i].getChildNodes();
				if(faultsInterni!=null){
					for (int j = 0; j < faultsInterni.getLength(); j++) {
						Node f = faultsInterni.item(j);
						if("codiceEccezione".equals(f.getLocalName())){
							String codice = f.getTextContent();
							//System.out.println("CODE ["+codice+"]");
							if(codice.equals(faultCode)){
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
	
	
	
	
	
	
	
	
	
	/* ****************** PORTE DELEGATE ******************* */
	
	
	
	
	/**
	 * Test anonimo
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_PD=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PD"})
	public void testLetturaCredenzialeNONEtoBASIC_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_PD")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PD"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_PD",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_PD"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "adminSilY");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PD, msg1) || 
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PD, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	/**
	 * Test NONEtoSSL
	 **/
	Repository repositoryLetturaCredenzialeNONEtoSSL_PD=new Repository();
	Date dataLetturaCredenzialeNONEtoSSL_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PD"})
	public void testLetturaCredenzialeNONEtoSSL_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoSSL_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoSSL_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoSSL_PD")
	public Object[][]providerLetturaCredenzialeNONEtoSSL_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoSSL_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PD"},
			dataProvider="providerLetturaCredenzialeNONEtoSSL_PD",
			dependsOnMethods={"testLetturaCredenzialeNONEtoSSL_PD"})
	public void verificaDBLetturaCredenzialeNONEtoSSL_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL.replace("@ID1@", "CN=clientkey");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML:"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB:"+identitaDB+" ["+configurazioneDb+"]");
				
				Assert.assertTrue(identitaXML || identitaDB);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Test BASICtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeBASICtoBASIC_PD=new Repository();
	Date dataLetturaCredenzialeBASICtoBASIC_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PD"})
	public void testLetturaCredenzialeBASICtoBASIC_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoBASIC_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoBASIC_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoBASIC_PD")
	public Object[][]providerLetturaCredenzialeBASICtoBASIC_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoBASIC_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PD"},
			dataProvider="providerLetturaCredenzialeBASICtoBASIC_PD",
			dependsOnMethods={"testLetturaCredenzialeBASICtoBASIC_PD"})
	public void verificaDBLetturaCredenzialeBASICtoBASIC_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC.replace("@ID1@", "adminSilY");
				msg1 = msg1.replace("@ID2@", "adminSilX");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PD, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PD, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	/**
	 * Test BASICtoSSL
	 **/
	Repository repositoryLetturaCredenzialeBASICtoSSL_PD=new Repository();
	Date dataLetturaCredenzialeBASICtoSSL_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PD"})
	public void testLetturaCredenzialeBASICtoSSL_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoSSL_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoSSL_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilY", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoSSL_PD")
	public Object[][]providerLetturaCredenzialeBASICtoSSL_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoSSL_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PD"},
			dataProvider="providerLetturaCredenzialeBASICtoSSL_PD",
			dependsOnMethods={"testLetturaCredenzialeBASICtoSSL_PD"})
	public void verificaDBLetturaCredenzialeBASICtoSSL_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL.replace("@ID1@", "CN=clientkey");
				msg1 = msg1.replace("@ID2@", "adminSilY");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML:"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB:"+identitaDB+" ["+configurazioneDb+"]");
				
				Assert.assertTrue(identitaXML || identitaDB);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	

	
	
	
	
	/**
	 * Test SSLtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeSSLtoBASIC_PD=new Repository();
	Date dataLetturaCredenzialeSSLtoBASIC_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PD"})
	public void testLetturaCredenzialeSSLtoBASIC_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoBASIC_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoBASIC_PD,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoBASIC_PD")
	public Object[][]providerLetturaCredenzialeSSLtoBASIC_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeSSLtoBASIC_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PD"},
			dataProvider="providerLetturaCredenzialeSSLtoBASIC_PD",
			dependsOnMethods={"testLetturaCredenzialeSSLtoBASIC_PD"})
	public void verificaDBLetturaCredenzialeSSLtoBASIC_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC.replace("@ID1@", "adminSilY");
				msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PD, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PD, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Test SSLtoSSL
	 **/
	Repository repositoryLetturaCredenzialeSSLtoSSL_PD=new Repository();
	Date dataLetturaCredenzialeSSLtoSSL_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PD"})
	public void testLetturaCredenzialeSSLtoSSL_PD() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoSSL_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoSSL_PD,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				try{
					dbComponentFruitore.close();
				}catch(Exception e){}
				try{
					dbComponentErogatore.close();
				}catch(Exception e){}
			}
			
			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoSSL_PD")
	public Object[][]providerLetturaCredenzialeSSLtoSSL_PD() throws Exception{
		String id=this.repositoryLetturaCredenzialeSSLtoSSL_PD.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PD"},
			dataProvider="providerLetturaCredenzialeSSLtoSSL_PD",
			dependsOnMethods={"testLetturaCredenzialeSSLtoSSL_PD"})
	public void verificaDBLetturaCredenzialeSSLtoSSL_PD(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL.replace("@ID1@", "CN=clientkey");
				msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SA_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML:"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB:"+identitaDB+" ["+configurazioneDb+"]");
				
				Assert.assertTrue(identitaXML || identitaDB);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	/**
	 * Test Errore Configurazione
	 **/
	Repository repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PD=new Repository();
	Date dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_PD"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR).equals(error.getFaultCode().getLocalPart()));
				
				String msgErrore = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				msgErrore = msgErrore+ "Eccezione, di configurazione, richiesta dalla testsuite";
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}

		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]: Eccezione, di configurazione, richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test Errore Generico
	 **/
	Repository repositoryLetturaCredenzialeERRORE_GENERALE_PD=new Repository();
	Date dataLetturaCredenzialeERRORE_GENERALE_PD = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PD"})
	public void testLetturaCredenzialeERRORE_GENERALE_PD() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeERRORE_GENERALE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeERRORE_GENERALE_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor code [OpenSPCoop]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO).equals(error.getFaultCode().getLocalPart()));
				
				String msgErrore = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
				Reporter.log("Controllo fault string ["+msgErrore+"]");
				Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Eccezione generale richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ****************** PORTE APPLICATIVE ******************* */
	
	
	
	
	
	/**
	 * Test NONEtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_PA=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PA"})
	public void testLetturaCredenzialeNONEtoBASIC_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoBASIC_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_PA")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_PA() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_PA.getNext();
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
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PA"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_PA",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_PA"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "adminSilY");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Test NONEtoSSL
	 **/
	Repository repositoryLetturaCredenzialeNONEtoSSL_PA=new Repository();
	Date dataLetturaCredenzialeNONEtoSSL_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PA"})
	public void testLetturaCredenzialeNONEtoSSL_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoSSL_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoSSL_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoSSL_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoSSL_PA")
	public Object[][]providerLetturaCredenzialeNONEtoSSL_PA() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoSSL_PA.getNext();
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
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PA"},
			dataProvider="providerLetturaCredenzialeNONEtoSSL_PA",
			dependsOnMethods={"testLetturaCredenzialeNONEtoSSL_PA"})
	public void verificaDBLetturaCredenzialeNONEtoSSL_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL.replace("@ID1@", "CN=clientkey");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test BASICtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeBASICtoBASIC_PA=new Repository();
	Date dataLetturaCredenzialeBASICtoBASIC_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PA"})
	public void testLetturaCredenzialeBASICtoBASIC_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoBASIC_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeBASICtoBASIC_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoBASIC_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoBASIC_PA")
	public Object[][]providerLetturaCredenzialeBASICtoBASIC_PA() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoBASIC_PA.getNext();
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
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PA"},
			dataProvider="providerLetturaCredenzialeBASICtoBASIC_PA",
			dependsOnMethods={"testLetturaCredenzialeBASICtoBASIC_PA"})
	public void verificaDBLetturaCredenzialeBASICtoBASIC_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC.replace("@ID1@", "adminSilY");
				msg1 = msg1.replace("@ID2@", "adminSilX");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Test BASICtoSSL
	 **/
	Repository repositoryLetturaCredenzialeBASICtoSSL_PA=new Repository();
	Date dataLetturaCredenzialeBASICtoSSL_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PA"})
	public void testLetturaCredenzialeBASICtoSSL_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoSSL_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeBASICtoSSL_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoSSL_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilY", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoSSL_PA")
	public Object[][]providerLetturaCredenzialeBASICtoSSL_PA() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoSSL_PA.getNext();
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
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PA"},
			dataProvider="providerLetturaCredenzialeBASICtoSSL_PA",
			dependsOnMethods={"testLetturaCredenzialeBASICtoSSL_PA"})
	public void verificaDBLetturaCredenzialeBASICtoSSL_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL.replace("@ID1@", "CN=clientkey");
				msg1 = msg1.replace("@ID2@", "adminSilY");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test SSLtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeSSLtoBASIC_PA=new Repository();
	Date dataLetturaCredenzialeSSLtoBASIC_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PA"})
	public void testLetturaCredenzialeSSLtoBASIC_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoBASIC_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeSSLtoBASIC_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoBASIC_PA,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "adminSilY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoBASIC_PA")
	public Object[][]providerLetturaCredenzialeSSLtoBASIC_PA() throws Exception{
		String id=this.repositoryLetturaCredenzialeSSLtoBASIC_PA.getNext();
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
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PA"},
			dataProvider="providerLetturaCredenzialeSSLtoBASIC_PA",
			dependsOnMethods={"testLetturaCredenzialeSSLtoBASIC_PA"})
	public void verificaDBLetturaCredenzialeSSLtoBASIC_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC.replace("@ID1@", "adminSilY");
				msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "adminSilY");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test SSLtoSSL
	 **/
	Repository repositoryLetturaCredenzialeSSLtoSSL_PA=new Repository();
	Date dataLetturaCredenzialeSSLtoSSL_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PA"})
	public void testLetturaCredenzialeSSLtoSSL_PA() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoSSL_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeSSLtoSSL_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoSSL_PA,sslContext);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore_httpsConAutenticazioneClient());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=clientkey");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoSSL_PA")
	public Object[][]providerLetturaCredenzialeSSLtoSSL_PA() throws Exception{
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		String id=this.repositoryLetturaCredenzialeSSLtoSSL_PA.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		try {
			Thread.sleep(6000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PA"},
			dataProvider="providerLetturaCredenzialeSSLtoSSL_PA",
			dependsOnMethods={"testLetturaCredenzialeSSLtoSSL_PA"})
	public void verificaDBLetturaCredenzialeSSLtoSSL_PA(DatabaseComponent data,DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, true,null);
			
			if(dataMsg!=null){
				String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL.replace("@ID1@", "CN=clientkey");
				msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
				String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=clientkey");
				Reporter.log("Verifica log msg1 ["+msg1+"]");
				Reporter.log("Verifica log msg2 ["+msg2+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PA, msg1) ||
						dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PA, msg2));
				
				String msg = MSG_COMPRENSIONE_IDENTITA_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@IDEGOV@", id);
				msg = msg.replace("@TIPO_MITTENTE@", CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE);
				msg = msg.replace("@MITTENTE@", CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE);
				Reporter.log("Verifica log identita ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PA, msg));
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception eClose){}
			if(dataMsg!=null){
				try{
					dataMsg.close();
				}catch(Exception eClose){}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test Errore Configurazione
	 **/
	Repository repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PA=new Repository();
	Date dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_PA"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PA() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeERRORE_CONFIGURAZIONE_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeERRORE_CONFIGURAZIONE_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]: Eccezione, di configurazione, richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	

	
	
	
	
	
	
	
	/**
	 * Test Errore Generico
	 **/
	Repository repositoryLetturaCredenzialeERRORE_GENERALE_PA=new Repository();
	Date dataLetturaCredenzialeERRORE_GENERALE_PA = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PA"})
	public void testLetturaCredenzialeERRORE_GENERALE_PA() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeERRORE_GENERALE_PA = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeERRORE_GENERALE_PA.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null);

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeERRORE_GENERALE_PA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Server]");
				Assert.assertTrue("Server".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_PROCESSAMENTO_SPCOOP.equals(error.getFaultString()));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]: Eccezione generale richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* ****************** INTEGRATION MANAGER ******************* */
	
	
	
	
	
	/**
	 * Test NONEtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_IM=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoBASIC_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_IM")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_IM"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	/**
	 * Test NONEtoSSL
	 **/
	Repository repositoryLetturaCredenzialeNONEtoSSL_IM=new Repository();
	Date dataLetturaCredenzialeNONEtoSSL_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"})
	public void testLetturaCredenzialeNONEtoSSL_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoSSL_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoSSL_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoSSL_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=IdentitaInesistente");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoSSL_IM")
	public Object[][]providerLetturaCredenzialeNONEtoSSL_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoSSL_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoSSL_IM"})
	public void verificaDBLetturaCredenzialeNONEtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	/**
	 * Test BASICtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeBASICtoBASIC_IM=new Repository();
	Date dataLetturaCredenzialeBASICtoBASIC_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"})
	public void testLetturaCredenzialeBASICtoBASIC_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoBASIC_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeBASICtoBASIC_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoBASIC_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoBASIC_IM")
	public Object[][]providerLetturaCredenzialeBASICtoBASIC_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoBASIC_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoBASIC_IM"})
	public void verificaDBLetturaCredenzialeBASICtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			msg1 = msg1.replace("@ID2@", "adminSilX");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	/**
	 * Test BASICtoSSL
	 **/
	Repository repositoryLetturaCredenzialeBASICtoSSL_IM=new Repository();
	Date dataLetturaCredenzialeBASICtoSSL_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"})
	public void testLetturaCredenzialeBASICtoSSL_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeBASICtoSSL_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeBASICtoSSL_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeBASICtoSSL_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=IdentitaInesistente");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeBASICtoSSL_IM")
	public Object[][]providerLetturaCredenzialeBASICtoSSL_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeBASICtoSSL_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoSSL_IM"})
	public void verificaDBLetturaCredenzialeBASICtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			msg1 = msg1.replace("@ID2@", "adminSilX");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Test SSLtoBASIC
	 **/
	Repository repositoryLetturaCredenzialeSSLtoBASIC_IM=new Repository();
	Date dataLetturaCredenzialeSSLtoBASIC_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"})
	public void testLetturaCredenzialeSSLtoBASIC_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoBASIC_IM = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeSSLtoBASIC_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoBASIC_IM,sslContext);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoBASIC_IM")
	public Object[][]providerLetturaCredenzialeSSLtoBASIC_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeSSLtoBASIC_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoBASIC_IM"})
	public void verificaDBLetturaCredenzialeSSLtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	/**
	 * Test SSLtoSSL
	 **/
	Repository repositoryLetturaCredenzialeSSLtoSSL_IM=new Repository();
	Date dataLetturaCredenzialeSSLtoSSL_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"})
	public void testLetturaCredenzialeSSLtoSSL_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeSSLtoSSL_IM = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeSSLtoSSL_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			// Contesto SSL
			java.util.Hashtable<String, String> sslContext = null;
			sslContext = new Hashtable<String, String>();
			sslContext.put("trustStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/openspcoop2/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoSSL_IM,sslContext);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SSL_SUBJECT, "CN=IdentitaInesistente");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeSSLtoSSL_IM")
	public Object[][]providerLetturaCredenzialeSSLtoSSL_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeSSLtoSSL_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoSSL_IM"})
	public void verificaDBLetturaCredenzialeSSLtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test ErroreConfigurazione
	 **/
	Repository repositoryLetturaCredenzialeErroreConfigurazione_IM=new Repository();
	Date dataLetturaCredenzialeErroreConfigurazione_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_IM"})
	public void testLetturaCredenzialeErroreConfigurazione_IM() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeErroreConfigurazione_IM = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeErroreConfigurazione_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
						
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeErroreConfigurazione_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_CONFIGURAZIONE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR, faults));
				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}

		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]: Eccezione, di configurazione, richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	/**
	 * Test Errore generale
	 **/
	Repository repositoryLetturaCredenzialeErroreGenerale_IM=new Repository();
	Date dataLetturaCredenzialeErroreGenerale_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_IM"})
	public void testLetturaCredenzialeErroreGenerale_IM() throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeErroreGenerale_IM = new Date();
		
		DatabaseComponent dbComponentFruitore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeErroreGenerale_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getAllMessagesId xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
						
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeErroreGenerale_IM);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE, "errore");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Utilities.verificaIntegrationManagerException(error, Utilities.testSuiteProperties.getIdentitaDefault_dominio(), "IntegrationManager", 
						Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO), 
						CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE, true);				
			}finally{
				dbComponentFruitore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentFruitore.close();
			}catch(Exception e){}
		}

		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore durante l'identificazione delle credenziali [testOpenSPCoop2]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Eccezione generale richiesta dalla testsuite");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	/**
	 * Test NONEtoBASIC_OperationGetMessage
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<getMessage xmlns=\"http://services.pdd.openspcoop2.org\"><idMessaggio>IDINESISTENTE</idMessaggio></getMessage>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM);
			client.setSoapAction("\"getMessage\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test NONEtoBASIC_OperationDeleteMessage
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<deleteMessage xmlns=\"http://services.pdd.openspcoop2.org\"><idMessaggio>IDINESISTENTE</idMessaggio></deleteMessage>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM);
			client.setSoapAction("\"deleteMessage\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test NONEtoBASIC_OperationDeleteAllMessages
	 **/
	Repository repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM=new Repository();
	Date dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM = null;
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM() throws TestSuiteException, Exception{

		this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM = new Date();
		
		DatabaseComponent dbComponentErogatore = null;
		try{
		
			String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
			this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM.add(egov);
			String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
					CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
					CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
					SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					egov,
					false,
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,
					false,null,null,
					SPCoopCostanti.TIPO_TEMPO_LOCALE,new Date(),
					"<deleteAllMessages xmlns=\"http://services.pdd.openspcoop2.org\"/>");

			java.io.ByteArrayInputStream bin = 
				new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
			Message msg=new Message(bin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM);
			client.setSoapAction("\"deleteAllMessages\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("PD","IntegrationManager/MessageBox"));
			client.connectToSoapEngine();
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "IdentitaInesistenteY");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_PASSWORD, "123456");
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore= DatabaseProperties.getDatabaseComponentErogatore();
	
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			try {
				client.run();
				throw new Exception("Atteso errore");
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA)+"]");
				Assert.assertTrue(checkFaultCode(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA, faults));
				
			}finally{
				dbComponentErogatore.close();
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dbComponentErogatore.close();
			}catch(Exception e){}
		}

	}
	@DataProvider (name="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM")
	public Object[][]providerLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM() throws Exception{
		String id=this.repositoryLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg2));
				
			String msg = MSG_COMPRENSIONE_FALLITO_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, MSG_COMPRENSIONE_FALLITO_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, MSG_COMPRENSIONE_FALLITO_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
}
