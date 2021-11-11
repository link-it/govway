/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.connettori;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.pdd.core.credenziali.GestoreCredenzialiTest;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
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
public class LetturaCredenzialiIngresso extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "LetturaCredenzialiIngresso";
	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	protected LetturaCredenzialiIngresso() {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,MessageType.SOAP_11,  this.info, 
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
	
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC = "Ottenute credenziali di accesso ( BasicUsername '@ID1@' ) fornite da GestoreCredenziali di test anonimo";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL = "Ottenute credenziali di accesso ( SSL-Subject '@ID1@' ) fornite da GestoreCredenziali di test anonimo";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC = "Ottenute credenziali di accesso ( BasicUsername '@ID1@' ) fornite da GestoreCredenziali di test ( BasicUsername '@ID2@' )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL = "Ottenute credenziali di accesso ( SSL-Subject '@ID1@' ) fornite da GestoreCredenziali di test ( BasicUsername '@ID2@' )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC = "Ottenute credenziali di accesso ( BasicUsername '@ID1@' ) fornite da GestoreCredenziali di test ( SSL-Subject '@ID2@' )";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL = "Ottenute credenziali di accesso ( SSL-Subject '@ID1@' ) fornite da GestoreCredenziali di test ( SSL-Subject '@ID2@' )";
	
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC = "Ottenute credenziali di accesso ( BasicUsername 'ID1@' ) fornite da Proxy";
	private final static String MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL = "Ottenute credenziali di accesso ( SSL-Subject '@ID1@' ) fornite da Proxy";
	
	private final static String MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_IN_CORSO = "Autenticazione [basic] in corso ( BasicUsername '@ID@' ) ...";	
	private final static String MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_COMPLETATA = "Autenticazione [basic] effettuata con successo";
	private final static String MSG_IDENTIFICAZIONE_SA_BASIC_RICEVUTA = "Ricevuta richiesta di servizio dal Servizio Applicativo @ID_SIL@ verso la porta delegata @PD@";
	
	private final static String MSG_IDENTIFICAZIONE_SA_SSL_AUTH_IN_CORSO = "Autenticazione [ssl] in corso ( SSL-Subject '@ID@' ) ...";
	private final static String MSG_IDENTIFICAZIONE_SA_SSL_AUTH_COMPLETATA = "Autenticazione [ssl] effettuata con successo";
	private final static String MSG_IDENTIFICAZIONE_SA_SSL_RICEVUTA = "Ricevuta richiesta di servizio dal Servizio Applicativo @ID_SIL@ verso la porta delegata @PD@";
	
	private final static String MSG_IDENTIFICAZIONE_SOGGETTO_BASIC = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviato dalla parte mittente [@TIPO_MITTENTE@/@MITTENTE@]";
	private final static String MSG_IDENTIFICAZIONE_SOGGETTO_SSL = "Ricevuto messaggio di cooperazione con identificativo [@IDEGOV@] inviato dalla parte mittente [@TIPO_MITTENTE@/@MITTENTE@]";
	
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC = "Autenticazione del servizio applicativo non riuscita ( BasicUsername '@ID@' )";
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_SSL = "Autenticazione del servizio applicativo non riuscita ( SSL-Subject '@ID@' )";
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE = "(Autenticazione basic) Autenticazione fallita, credenziali fornite non corrette";	
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE = "(Autenticazione basic) Autenticazione fallita, credenziali non fornite";	
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE = "(Autenticazione ssl) non ha identificato alcun servizio applicativo";	
	private final static String MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE = "(Autenticazione ssl) Autenticazione fallita, credenziali non fornite";	
	
	private boolean checkFaultCode(String faultCode,Element[] faults) throws ProtocolException{
		Reporter.log("check...["+faults.length+"]");
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
							Reporter.log("Trovato codice eccezione ["+codice+"]");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PD"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PD"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_IN_CORSO.replace("@ID@", "adminSilY");
				Reporter.log("Verifica log identita identificazione ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_COMPLETATA;
				Reporter.log("Verifica log identita identificazione ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_RICEVUTA.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita ricevuta ["+msg+"]");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PD"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PD"},
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
				
				
				
				String msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_IN_CORSO.replace("@ID@", "CN=clientkey");
				boolean check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_COMPLETATA.replace("@ID@", "CN=clientkey");
				check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_RICEVUTA.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML (ricevuta):"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB (ricevuta):"+identitaDB+" ["+configurazioneDb+"]");
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PD"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PD"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_IN_CORSO.replace("@ID@", "adminSilY");
				Reporter.log("Verifica log identita (identificazione) ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_COMPLETATA;
				Reporter.log("Verifica log identita (identificazione) ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_RICEVUTA.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita (ricevuta) ["+msg+"]");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PD"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PD"},
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
				
				
				
				String msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_IN_CORSO.replace("@ID@", "CN=clientkey");
				boolean check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_COMPLETATA.replace("@ID@", "CN=clientkey");
				check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				

				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_RICEVUTA.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML (ricevuta):"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB (ricevuta):"+identitaDB+" ["+configurazioneDb+"]");
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PD"})
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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PD"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_IN_CORSO.replace("@ID@", "adminSilY");
				Reporter.log("Verifica log identita (identificazione) ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_AUTH_COMPLETATA;
				Reporter.log("Verifica log identita (identificazione) ["+msg+"]");
				Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_PD, msg));
				
				msg = MSG_IDENTIFICAZIONE_SA_BASIC_RICEVUTA.replace("@ID@", "adminSilY");
				msg = msg.replace("@ID_SIL@", "silY");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
				Reporter.log("Verifica log identita (ricevuta) ["+msg+"]");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PD"})
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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),DatabaseProperties.getDatabaseComponentDiagnosticaFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),null,id,true}	
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PD"},
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
				
				
				String msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_IN_CORSO.replace("@ID@", "CN=clientkey");
				boolean check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_AUTH_COMPLETATA.replace("@ID@", "CN=clientkey");
				check = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, msg);
				Reporter.log("Verifica log identita (identificazione):"+check+" ["+msg+"]");
				Assert.assertTrue(check);
				
				
				
				msg = MSG_IDENTIFICAZIONE_SA_SSL_RICEVUTA.replace("@ID@", "CN=clientkey");
				msg = msg.replace("@PD@", CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_SSL);
				
				String configurazioneXml = new String(msg);
				configurazioneXml = configurazioneXml.replace("@ID_SIL@", "silX");
				
				String configurazioneDb = new String(msg);
				configurazioneDb = configurazioneDb.replace("@ID_SIL@", "silX_SSL");
				
				boolean identitaXML = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, configurazioneXml);
				Reporter.log("Verifica log identita XML (ricevuta):"+identitaXML+" ["+configurazioneXml+"]");
				
				boolean identitaDB = dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_PD, configurazioneDb);
				Reporter.log("Verifica log identita DB (ricevuta):"+identitaDB+" ["+configurazioneDb+"]");
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_PD"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_PD"})
	public void testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeERRORE_CONFIGURAZIONE_PD(boolean genericCode) throws TestSuiteException, Exception{

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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				msgErrore = msgErrore+ "Eccezione, di configurazione, richiesta dalla testsuite";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	 * Test Errore Proxy Credentials Not Found
	 **/
	Repository repositoryLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD=new Repository();
	Date dataLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD = null;
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_NOT_FOUND_PD"})
	public void testLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_NOT_FOUND_PD"})
	public void testLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD(boolean genericCode) throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeNOT_FOUND_CONFIGURAZIONE_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_BASIC_USERNAME, "username");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				msgErrore = msgErrore+ "Password value non fornito";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.PROXY_AUTHENTICATION_CREDENTIALS_NOT_FOUND;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	 * Test Errore Forward
	 **/
	Repository repositoryLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD=new Repository();
	Date dataLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD = null;
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_FORWARD_PD"})
	public void testLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_FORWARD_PD"})
	public void testLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD(boolean genericCode) throws TestSuiteException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		this.dataLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD = new Date();
		
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
		
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeFORWARD_CONFIGURAZIONE_PD);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTENTICAZIONE_BASIC);
			client.connectToSoapEngine();
			client.setAutenticazione("adminSilX", "123456");
			client.setProperty(GestoreCredenzialiTest.TEST_CREDENZIALI_SIMULAZIONE_ERRORE_FORWARD, "errore");
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR);
				String msgErrore = CostantiErroriIntegrazione.MSG_431_GESTORE_CREDENZIALI_ERROR.replace(CostantiErroriIntegrazione.MSG_431_TIPO_GESTORE_CREDENZIALI_KEY, "testOpenSPCoop2");
				msgErrore = msgErrore+ "Eccezione, di configurazione, richiesta dalla testsuite";
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.PROXY_AUTHENTICATION_FORWARDED_CREDENTIALS_NOT_FOUND;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_CLIENT +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PD"})
	public void testLetturaCredenzialeERRORE_GENERALE_PD_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeERRORE_GENERALE_PD(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PD"})
	public void testLetturaCredenzialeERRORE_GENERALE_PD_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeERRORE_GENERALE_PD(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PD"})
	public void testLetturaCredenzialeERRORE_GENERALE_PD_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeERRORE_GENERALE_PD(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeERRORE_GENERALE_PD(boolean genericCode, boolean unwrap) throws TestSuiteException, Exception{

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
				
				String msgErrore = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SERVER +
							org.openspcoop2.message.constants.Costanti.SOAP11_FAULT_CODE_SEPARATOR+erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault actor ["+org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR+"]");
				Assert.assertTrue(org.openspcoop2.testsuite.core.CostantiTestSuite.OPENSPCOOP2_INTEGRATION_ACTOR.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(codiceErrore.equals(error.getFaultCode().getLocalPart()));
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PA"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PA"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PA"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PA"})
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PA"})
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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
		else {
			org.openspcoop2.utils.Utilities.sleep(2000);
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_BASIC.replace("@ID@", "adminSilY");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PA"})
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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_PA"},
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
				
				String msg = MSG_IDENTIFICAZIONE_SOGGETTO_SSL.replace("@ID@", "CN=clientkey");
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_PA"})
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_PA"})
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _testLetturaCredenzialeNONEtoBASIC_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_IM_genericCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_IM_specificCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeNONEtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"})
	public void testLetturaCredenzialeNONEtoSSL_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"})
	public void testLetturaCredenzialeNONEtoSSL_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeNONEtoSSL_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
								
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoSSL_IM_genericCode"})
	public void verificaDBLetturaCredenzialeNONEtoSSL_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoSSL_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoSSL_IM_specificCode"})
	public void verificaDBLetturaCredenzialeNONEtoSSL_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoSSL_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeNONEtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"})
	public void testLetturaCredenzialeBASICtoBASIC_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeBASICtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"})
	public void testLetturaCredenzialeBASICtoBASIC_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeBASICtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeBASICtoBASIC_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
								
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoBASIC_IM_genericCode"})
	public void verificaDBLetturaCredenzialeBASICtoBASIC_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeBASICtoBASIC_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoBASIC_IM_specificCode"})
	public void verificaDBLetturaCredenzialeBASICtoBASIC_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeBASICtoBASIC_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeBASICtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			msg1 = msg1.replace("@ID2@", "adminSilX");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"})
	public void testLetturaCredenzialeBASICtoSSL_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeBASICtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"})
	public void testLetturaCredenzialeBASICtoSSL_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeBASICtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeBASICtoSSL_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoSSL_IM_genericCode"})
	public void verificaDBLetturaCredenzialeBASICtoSSL_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeBASICtoSSL_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_BASICtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeBASICtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeBASICtoSSL_IM_specificCode"})
	public void verificaDBLetturaCredenzialeBASICtoSSL_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeBASICtoSSL_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeBASICtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_BASIC_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			msg1 = msg1.replace("@ID2@", "adminSilX");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeBASICtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"})
	public void testLetturaCredenzialeSSLtoBASIC_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeSSLtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"})
	public void testLetturaCredenzialeSSLtoBASIC_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeSSLtoBASIC_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _testLetturaCredenzialeSSLtoBASIC_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoBASIC_IM,sslContext);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoBASIC_IM_genericCode"})
	public void verificaDBLetturaCredenzialeSSLtoBASIC_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		verificaDBLetturaCredenzialeSSLtoBASIC_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoBASIC_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoBASIC_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoBASIC_IM_specificCode"})
	public void verificaDBLetturaCredenzialeSSLtoBASIC_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		verificaDBLetturaCredenzialeSSLtoBASIC_IM(dataMsg, id);
	}
	private void verificaDBLetturaCredenzialeSSLtoBASIC_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoBASIC_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"})
	public void testLetturaCredenzialeSSLtoSSL_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeSSLtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"})
	public void testLetturaCredenzialeSSLtoSSL_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeSSLtoSSL_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeSSLtoSSL_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			java.util.Map<String, String> sslContext = null;
			sslContext = new HashMap<String, String>();
			sslContext.put("trustStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("keyStoreLocation", "/etc/govway/keys/sil1.jks");
			sslContext.put("trustStorePassword", "openspcoopjks");
			sslContext.put("keyStorePassword", "openspcoopjks");
			sslContext.put("keyPassword", "openspcoop");
			sslContext.put("hostnameVerifier", "false");
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryLetturaCredenzialeSSLtoSSL_IM,sslContext);
			client.setSoapAction("\"getAllMessagesId\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore_httpsConAutenticazioneClient().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoSSL_IM_genericCode"})
	public void verificaDBLetturaCredenzialeSSLtoSSL_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeSSLtoSSL_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_SSLtoSSL_IM"},
			dataProvider="providerLetturaCredenzialeSSLtoSSL_IM",
			dependsOnMethods={"testLetturaCredenzialeSSLtoSSL_IM_specificCode"})
	public void verificaDBLetturaCredenzialeSSLtoSSL_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeSSLtoSSL_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeSSLtoSSL_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_SSL_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			msg1 = msg1.replace("@ID2@", "CN=sil1, OU=test, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_SSL.replace("@ID1@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_SSL.replace("@ID@", "CN=IdentitaInesistente");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_FORNITE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeSSLtoSSL_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_CORRETTE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_IM"})
	public void testLetturaCredenzialeErroreConfigurazione_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeErroreConfigurazione_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_CONFIGURAZIONE_IM"})
	public void testLetturaCredenzialeErroreConfigurazione_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeErroreConfigurazione_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _testLetturaCredenzialeErroreConfigurazione_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_431_GESTORE_CREDENZIALI_ERROR);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.PROXY_AUTHENTICATION_INVALID_CREDENTIALS;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_IM"})
	public void testLetturaCredenzialeErroreGenerale_IM_genericCode_wrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeErroreGenerale_IM(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_IM"})
	public void testLetturaCredenzialeErroreGenerale_IM_genericCode_unwrap() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = true;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeErroreGenerale_IM(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_ERRORE_GENERALE_IM"})
	public void testLetturaCredenzialeErroreGenerale_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeErroreGenerale_IM(genericCode, unwrap);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _testLetturaCredenzialeErroreGenerale_IM(boolean genericCode, boolean unwrap) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("out","IntegrationManager/MessageBox"));
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
				
				String msgErrore = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
					if(unwrap) {
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Utilities.verificaIntegrationManagerException(error, Utilities.testSuiteProperties.getIdentitaDefault_dominio(), "IntegrationManager", 
						codiceErrore, 
						msgErrore, true);				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_genericCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationGetMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_specificCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(dataMsg, id);
	}
	private void verificaDBLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationGetMessage_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));

				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_genericCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteMessage_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_specificCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteMessage_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_genericCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"})
	public void testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_specificCode() throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(genericCode);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(boolean genericCode) throws TestSuiteException, Exception{

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
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiErogatore().replace("out","IntegrationManager/MessageBox"));
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
				
				String codiceErrore = Utilities.toString(CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA);
				String msgErrore = null;
				
				if(genericCode) {
					IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.AUTHENTICATION;
					ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
					codiceErrore = erroriProperties.getErrorType_noWrap(integrationFunctionError);
					if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
						msgErrore = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					}
				}

				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				if(msgErrore!=null) {
					Reporter.log("Controllo fault string ["+msgErrore+"]");
					Assert.assertTrue(msgErrore.equals(error.getFaultString()));
				}
					
				Element [] faults = error.getFaultDetails();
				Assert.assertTrue(faults!=null);
				Reporter.log("Controllo fault code ["+codiceErrore+"]");
				Assert.assertTrue(checkFaultCode(codiceErrore, faults));
				
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
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_genericCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_genericCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(dataMsg, id);
	}
	@Test(groups={CostantiConnettori.ID_GRUPPO_CONNETTORI,LetturaCredenzialiIngresso.ID_GRUPPO,LetturaCredenzialiIngresso.ID_GRUPPO+".LETTURA_CREDENZIALI_NONEtoBASIC_OperationDeleteAllMessages_IM"},
			dataProvider="providerLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM",
			dependsOnMethods={"testLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_specificCode"})
	public void verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM_specificCode(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		_verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(dataMsg, id);
	}
	private void _verificaDBLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM(DatabaseMsgDiagnosticiComponent dataMsg,String id) throws Exception{
		try{

			String msg1 = MSG_LETTURA_NUOVE_CREDENZIALI_NONE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			String msg2 = MSG_LETTURA_NUOVE_CREDENZIALI_ENTERPRISE_TO_BASIC.replace("@ID1@", "IdentitaInesistenteY");
			Reporter.log("Verifica log msg1 ["+msg1+"]");
			Reporter.log("Verifica log msg2 ["+msg2+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg1) ||
					dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg2));
				
			String msg = MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC.replace("@ID@", "IdentitaInesistenteY");
			Reporter.log("Verifica log identita ["+msg+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, msg));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_BASIC_CREDENZIALI_NON_CORRETTE));
			
			Reporter.log("Verifica log identita ["+MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE+"]");
			Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(this.dataLetturaCredenzialeNONEtoBASIC_OperationDeleteAllMessages_IM, MSG_IDENTIFICAZIONE_FALLITA_IM_SSL_CREDENZIALI_NON_FORNITE));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				dataMsg.close();
			}catch(Exception eClose){}
		}
	}
}
