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
public class IntegrazioneConnettoreSAAJ {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "IntegrazioneConnettoreSAAJ";
	
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
	
	
	
	
	
		
	
	
	
	
	
	
	
	
	// ************************ CONNETTORE SAAJ **************************
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWaySAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_SAAJ"})
	public void oneWaySAAJ() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWaySAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="OneWaySAAJ")
	public Object[][]testOneWaySAAJ() throws Exception{
		String id=this.repositoryOneWaySAAJ.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_SAAJ"},dataProvider="OneWaySAAJ",dependsOnMethods={"oneWaySAAJ"})
	public void testOneWaySAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWayWithWSASAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_WSADDRESSING_SAAJ"})
	public void oneWayWithWSASAAJ() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithWSASAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header WSAddressing
		    Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="OneWayWithWSASAAJ")
	public Object[][]testOneWayWithWSASAAJ() throws Exception{
		String id=this.repositoryOneWayWithWSASAAJ.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_WSADDRESSING_SAAJ"},dataProvider="OneWayWithWSASAAJ",dependsOnMethods={"oneWayWithWSASAAJ"})
	public void testOneWayWithWSASAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_SAAJ"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoSAAJ() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoSAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="SincronoSAAJ")
	public Object[][]testSincronoSAAJ()throws Exception{
		String id=this.repositorySincronoSAAJ.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_SAAJ"},dataProvider="SincronoSAAJ",dependsOnMethods={"sincronoSAAJ"})
	public void testSincronoSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ, checkServizioApplicativo,
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
	Repository repositorySincronoWithWSASAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_WSADDRESSING_SAAJ"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSASAAJ() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoWithWSASAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="SincronoWithWSASAAJ")
	public Object[][]testSincronoWithWSASAAJ()throws Exception{
		String id=this.repositorySincronoWithWSASAAJ.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_WSADDRESSING_SAAJ"},dataProvider="SincronoWithWSASAAJ",dependsOnMethods={"sincronoWithWSASAAJ"})
	public void testSincronoWithWSASAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ, checkServizioApplicativo,
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
	Repository repositorysincronoWithWSAVerificaClientSincronoSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO_SAAJ"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSAVerificaClientSincronoSAAJ() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientSincrono client=new ClientSincrono(this.repositorysincronoWithWSAVerificaClientSincronoSAAJ);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="sincronoWithWSAVerificaClientSincronoSAAJ")
	public Object[][]testsincronoWithWSAVerificaClientSincronoSAAJ()throws Exception{
		String id=this.repositorysincronoWithWSAVerificaClientSincronoSAAJ.getNext();
		try{
			Thread.sleep(2000);
		}catch(Exception e){}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_WSADDRESSING_VERIFICA_CLIENT_SINCRONO_SAAJ"},
			dataProvider="sincronoWithWSAVerificaClientSincronoSAAJ",
			dependsOnMethods={"sincronoWithWSAVerificaClientSincronoSAAJ"})
	public void testsincronoWithWSAVerificaClientSincronoSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ, checkServizioApplicativo,
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
	Repository repositoryOneWayWithAttachmentsSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_ATTACHMENTS_SAAJ"})
	public void oneWayWithAttachmentsSAAJ() throws TestSuiteException, Exception{
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachmentsSAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
		    
		    // Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="OneWayWithAttachmentsSAAJ")
	public Object[][]testOneWayWithAttachmentsSAAJ() throws Exception{
		String id=this.repositoryOneWayWithAttachmentsSAAJ.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_ATTACHMENTS_SAAJ"},dataProvider="OneWayWithAttachmentsSAAJ",dependsOnMethods={"oneWayWithAttachmentsSAAJ"})
	public void testOneWayWithAttachmentsSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_SAAJ, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWayWithAttachments
	 */
	Repository repositoryOneWayWithAttachmentsAndWSAddressingSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING_SAAJ"})
	public void oneWayWithAttachmentsAndWSAddressingSAAJ() throws TestSuiteException, Exception{
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithAttachmentsAndWSAddressingSAAJ);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
		    
			// Check header proprietario OpenSPCoop
		    Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header WSAddressing
		    Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="OneWayWithAttachmentsAndWSAddressingSAAJ")
	public Object[][]testOneWayWithAttachmentsAndWSAddressingSAAJ() throws Exception{
		String id=this.repositoryOneWayWithAttachmentsAndWSAddressingSAAJ.getNext();
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
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".ONEWAY_ATTACHMENTS_WSADDRESSING_SAAJ"},
			dataProvider="OneWayWithAttachmentsAndWSAddressingSAAJ",dependsOnMethods={"oneWayWithAttachmentsAndWSAddressingSAAJ"})
	public void testOneWayWithAttachmentsAndWSAddressingSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione SincronoWithAttachments
	 */
	Repository repositorySincronoWithAttachmentsSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_ATTACHMENTS_SAAJ"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachmentsSAAJ() throws TestSuiteException, IOException, Exception{
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
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachmentsSAAJ);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
		    // Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ,client.getIdMessaggio());
			
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
	@DataProvider (name="SincronoWithAttachmentsSAAJ")
	public Object[][]testSincronoWithAttachmentsSAAJ()throws Exception{
		String id=this.repositorySincronoWithAttachmentsSAAJ.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_ATTACHMENTS_SAAJ"},dataProvider="SincronoWithAttachmentsSAAJ",dependsOnMethods={"sincronoWithAttachmentsSAAJ"})
	public void testSincronoWithAttachmentsSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_SAAJ, checkServizioApplicativo,
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
	Repository repositorySincronoWithAttachmentsAndWSAddressingSAAJ=new Repository();
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING_SAAJ"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithAttachmentsAndWSAddressingSAAJ() throws TestSuiteException, IOException, Exception{
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
			
			ClientSincrono client=new ClientSincrono(this.repositorySincronoWithAttachmentsAndWSAddressingSAAJ);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING_SAAJ);
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
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header proprietario OpenSPCoop
			Integrazione.checkMessaggioRisposta(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(),CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());
			
			// Check header WSAddressing
			Integrazione.checkMessaggioRispostaWSAddressing(this.collaborazioneSPCoopBase,
		    		client.getResponseMessage(), CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,  CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, 1, 
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ,client.getIdMessaggio());		
			
			
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
	@DataProvider (name="SincronoWithAttachmentsAndWSAddressingSAAJ")
	public Object[][]testSincronoWithAttachmentsAndWSAddressingSAAJ()throws Exception{
		String id=this.repositorySincronoWithAttachmentsAndWSAddressingSAAJ.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={CostantiIntegrazione.ID_GRUPPO_INTEGRAZIONE,IntegrazioneConnettoreSAAJ.ID_GRUPPO,IntegrazioneConnettoreSAAJ.ID_GRUPPO+".SINCRONO_ATTACHMENTS_WSADDRESSING_SAAJ"},
			dataProvider="SincronoWithAttachmentsAndWSAddressingSAAJ",dependsOnMethods={"sincronoWithAttachmentsAndWSAddressingSAAJ"})
	public void testSincronoWithAttachmentsAndWSAddressingSAAJ(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING_SAAJ, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
}
