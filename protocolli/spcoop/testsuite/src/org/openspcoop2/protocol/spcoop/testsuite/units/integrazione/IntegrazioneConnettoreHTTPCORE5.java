/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.integrazione;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle informazioni di integrazione
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IntegrazioneConnettoreHTTPCORE5 {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "IntegrazioneConnettoreHTTPCORE5";
	
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
	
	
	
	
	
		
	
	
	
	
	
	
	
	
	// ************************ CONNETTORE HTTPCORE5 **************************
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWayHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_HTTPCORE5"})
	public void oneWayHTTPCORE5() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="OneWayHTTPCORE5")
	public Object[][]testOneWayHTTPCORE5() throws Exception{
		String id=this.repositoryOneWayHTTPCORE5.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_HTTPCORE5"},dataProvider="OneWayHTTPCORE5",dependsOnMethods={"oneWayHTTPCORE5"})
	public void testOneWayHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWayWithWSAHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_WSADDRESSING_HTTPCORE5"})
	public void oneWayWithWSAHTTPCORE5() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithWSAHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header WSAddressing
		    Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 1,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="OneWayWithWSAHTTPCORE5")
	public Object[][]testOneWayWithWSAHTTPCORE5() throws Exception{
		String id=this.repositoryOneWayWithWSAHTTPCORE5.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_WSADDRESSING_HTTPCORE5"},dataProvider="OneWayWithWSAHTTPCORE5",dependsOnMethods={"oneWayWithWSAHTTPCORE5"})
	public void testOneWayWithWSAHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_HTTPCORE5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoHTTPCORE5() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			 // Header HTTP
			Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="SincronoHTTPCORE5")
	public Object[][]testSincronoHTTPCORE5()throws Exception{
		String id=this.repositorySincronoHTTPCORE5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_HTTPCORE5"},dataProvider="SincronoHTTPCORE5",dependsOnMethods={"sincronoHTTPCORE5"})
	public void testSincronoHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoWithWSAHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_WSADDRESSING_HTTPCORE5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSAHTTPCORE5() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoWithWSAHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
			Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="SincronoWithWSAHTTPCORE5")
	public Object[][]testSincronoWithWSAHTTPCORE5()throws Exception{
		String id=this.repositorySincronoWithWSAHTTPCORE5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_WSADDRESSING_HTTPCORE5"},dataProvider="SincronoWithWSAHTTPCORE5",dependsOnMethods={"sincronoWithWSAHTTPCORE5"})
	public void testSincronoWithWSAHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorysincronoWithWSAVerificaClientSincronoHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO_HTTPCORE5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSAVerificaClientSincronoHTTPCORE5() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorysincronoWithWSAVerificaClientSincronoHTTPCORE5);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_HTTPCORE5);
			client.connectToSoapEngine();
			client.setMessage(msg);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
			Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="sincronoWithWSAVerificaClientSincronoHTTPCORE5")
	public Object[][]testsincronoWithWSAVerificaClientSincronoHTTPCORE5()throws Exception{
		String id=this.repositorysincronoWithWSAVerificaClientSincronoHTTPCORE5.getNext();
		try{
			Thread.sleep(2000);
		}catch(Exception e){}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO_HTTPCORE5"},
			dataProvider="sincronoWithWSAVerificaClientSincronoHTTPCORE5",
			dependsOnMethods={"sincronoWithWSAVerificaClientSincronoHTTPCORE5"})
	public void testsincronoWithWSAVerificaClientSincronoHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWayWithAttachments
	 */
	Repository repositoryOneWayWithAttachmentsHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_ATTACHMENTS_HTTPCORE5"})
	public void oneWayWithAttachmentsHTTPCORE5() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachmentsHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
			
		    // Header HTTP
		    Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="OneWayWithAttachmentsHTTPCORE5")
	public Object[][]testOneWayWithAttachmentsHTTPCORE5() throws Exception{
		String id=this.repositoryOneWayWithAttachmentsHTTPCORE5.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_ATTACHMENTS_HTTPCORE5"},dataProvider="OneWayWithAttachmentsHTTPCORE5",dependsOnMethods={"oneWayWithAttachmentsHTTPCORE5"})
	public void testOneWayWithAttachmentsHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_HTTPCORE5, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWayWithAttachments
	 */
	Repository repositoryOneWayWithAttachmentsAndWSAddressingHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING_HTTPCORE5"})
	public void oneWayWithAttachmentsAndWSAddressingHTTPCORE5() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachmentsAndWSAddressingHTTPCORE5);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_HTTPCORE5);
			client.connectToSoapEngine();
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
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			// Preleva l'id Egov dal body della risposta
		    try{
		    	// Il body puo' essere null in caso si voglia che il oneway non ritorna contenuto applicativo.
			    if(client.getResponseMessage().getSOAPBody()!=null && client.getResponseMessage().getSOAPBody().hasChildNodes()){
				    String idBody=org.openspcoop2.testsuite.core.Utilities.getIDFromOpenSPCoopOKMessage(SPCoopTestsuiteLogger.getInstance(),client.getResponseMessage());
				    // Controlla che sia uguale a quello ritornato nell'header della risposta
				    if(idBody==null)
				    	throw new TestSuiteException("ID e-Gov non presenta nella risposta OpenSPCoopOK.");
				    if(client.getIdMessaggio().equals(idBody)==false)
				    	throw new TestSuiteException("ID e-Gov presente nell'header del trasporto della risposta differisce dall'id egov presente nel messaggio OpenSPCoopOK della risposta.");
					
			    }
		    }catch(Exception e){
		    	throw new TestSuiteException("Check msg openspcoop ok non riuscita: "+e.getMessage());
		    }
				
		    // Header HTTP
		    Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
		    
			// Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header WSAddressing
		    Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
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
	}
	@DataProvider (name="OneWayWithAttachmentsAndWSAddressingHTTPCORE5")
	public Object[][]testOneWayWithAttachmentsAndWSAddressingHTTPCORE5() throws Exception{
		String id=this.repositoryOneWayWithAttachmentsAndWSAddressingHTTPCORE5.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING_HTTPCORE5"},
			dataProvider="OneWayWithAttachmentsAndWSAddressingHTTPCORE5",dependsOnMethods={"oneWayWithAttachmentsAndWSAddressingHTTPCORE5"})
	public void testOneWayWithAttachmentsAndWSAddressingHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione SincronoWithAttachments
	 */
	Repository repositorySincronoWithAttachmentsHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_ATTACHMENTS_HTTPCORE5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachmentsHTTPCORE5() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachmentsHTTPCORE5);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_HTTPCORE5);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.run();

			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
			Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5,client.getIdMessaggio());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null)
					dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWithAttachmentsHTTPCORE5")
	public Object[][]testSincronoWithAttachmentsHTTPCORE5()throws Exception{
		String id=this.repositorySincronoWithAttachmentsHTTPCORE5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_ATTACHMENTS_HTTPCORE5"},dataProvider="SincronoWithAttachmentsHTTPCORE5",dependsOnMethods={"sincronoWithAttachmentsHTTPCORE5"})
	public void testSincronoWithAttachmentsHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_HTTPCORE5, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione SincronoWithAttachments
	 */
	Repository repositorySincronoWithAttachmentsAndWSAddressingHTTPCORE5=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING_HTTPCORE5"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachmentsAndWSAddressingHTTPCORE5() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName()));
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();

			Message msg=Axis14SoapUtils.build(bout.toByteArray(), false);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachmentsAndWSAddressingHTTPCORE5);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_HTTPCORE5);
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.run();

			// Test uguaglianza Body (e attachments)
			Assert.assertTrue(client.isEqualsSentAndResponseMessage());
			Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
			
			// Header HTTP
			Integrazione.checkHttpRisposta(this.collaborazioneSPCoopBase,
		    		client.getPropertiesTrasportoRisposta(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5,client.getIdMessaggio());		
			
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null)
					dbComponentFruitore.close();
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWithAttachmentsAndWSAddressingHTTPCORE5")
	public Object[][]testSincronoWithAttachmentsAndWSAddressingHTTPCORE5()throws Exception{
		String id=this.repositorySincronoWithAttachmentsAndWSAddressingHTTPCORE5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO,IntegrazioneConnettoreHTTPCORE5.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING_HTTPCORE5"},
			dataProvider="SincronoWithAttachmentsAndWSAddressingHTTPCORE5",dependsOnMethods={"sincronoWithAttachmentsAndWSAddressingHTTPCORE5"})
	public void testSincronoWithAttachmentsAndWSAddressingHTTPCORE5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_HTTPCORE5, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
}
