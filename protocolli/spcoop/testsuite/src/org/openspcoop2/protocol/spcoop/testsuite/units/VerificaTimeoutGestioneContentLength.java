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
import java.io.IOException;
import java.util.Date;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
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
 * Test sui profili di collaborazione implementati nella Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class VerificaTimeoutGestioneContentLength {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "VerificaTimeoutGestioneContentLength";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());



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
	
	
	
	
	

	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWay=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY"})
	public void oneWay() throws TestSuiteException, Exception{	
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
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
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="OneWay")
	public Object[][]testOneWay() throws Exception{
		String id=this.repositoryOneWay.getNext();
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
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay",dependsOnMethods={"oneWay"})
	public void testOneWay(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY"},dependsOnMethods={"testOneWay"})
	public void oneWay_Attachments() throws TestSuiteException, Exception{	
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY);
			client.connectToSoapEngine();
			client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
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
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="OneWay_Attachments")
	public Object[][]testOneWay_Attachments() throws Exception{
		String id=this.repositoryOneWay.getNext();
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
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY"},dataProvider="OneWay_Attachments",dependsOnMethods={"oneWay_Attachments"})
	public void testOneWay_Attachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,null, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione OneWay in modalita stateless
	 */
	Repository repositoryOneWayStateless=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS"})
	public void oneWayStateless() throws TestSuiteException, Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayStateless);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
	}
	@DataProvider (name="OneWayStateless")
	public Object[][]testOneWayStateless() throws Exception{
		String id=this.repositoryOneWayStateless.getNext();
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
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS"},dataProvider="OneWayStateless",dependsOnMethods={"oneWayStateless"})
	public void testOneWayStateless(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS"},dependsOnMethods={"testOneWayStateless"})
	public void oneWayStateless_Attachments() throws TestSuiteException, Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayStateless);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
	}
	@DataProvider (name="OneWayStateless_Attachments")
	public Object[][]testOneWayStateless_Attachments() throws Exception{
		String id=this.repositoryOneWayStateless.getNext();
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
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS"},
			dataProvider="OneWayStateless_Attachments",dependsOnMethods={"oneWayStateless_Attachments"})
	public void testOneWayStateless_Attachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione oneway trasmissione sincrona affidabile
	 */
	private CooperazioneBaseInformazioni infoStatelessOnewayAffidabile = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			true,SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,Inoltro.SENZA_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseStatelessOnewayAffidabile = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.infoStatelessOnewayAffidabile, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());

	Repository repositoryOneWay_statelessAffidabile=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS_AFFIDABILE"},description="Test di tipo oneway, con trasmissione sincrona")
	public void oneway_statelessAffidabile() throws Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay_statelessAffidabile);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AFFIDABILE);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
	}
	@DataProvider (name="oneway_statelessProvider_affidabile")
	public Object[][]oneway_statelessProvider_affidabile()throws Exception{
		String id=this.repositoryOneWay_statelessAffidabile.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS_AFFIDABILE"},dataProvider="oneway_statelessProvider_affidabile",
			dependsOnMethods={"oneway_statelessAffidabile"})
	public void testOneway_statelessProvider_affidabile(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseStatelessOnewayAffidabile.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS_AFFIDABILE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS_AFFIDABILE"},
			description="Test di tipo oneway, con trasmissione sincrona",
			dependsOnMethods={"testOneway_statelessProvider_affidabile"})
	public void oneway_statelessAffidabile_Attachments() throws Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWay_statelessAffidabile);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_STATELESS_AFFIDABILE);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		Assert.assertTrue(org.openspcoop2.testsuite.core.Utilities.isOpenSPCoopOKMessage(client.getResponseMessage()));
	}
	@DataProvider (name="oneway_statelessProvider_affidabile_Attachments")
	public Object[][]oneway_statelessProvider_affidabile_Attachments()throws Exception{
		String id=this.repositoryOneWay_statelessAffidabile.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_STATELESS_AFFIDABILE"},
			dataProvider="oneway_statelessProvider_affidabile_Attachments",
			dependsOnMethods={"oneway_statelessAffidabile_Attachments"})
	public void testOneway_statelessProvider_affidabile_Attachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseStatelessOnewayAffidabile.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_STATELESS_AFFIDABILE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	



	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincrono=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincrono() throws Exception{
	
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincrono);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());

	}
	@DataProvider (name="Sincrono")
	public Object[][]testSincrono()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono",dependsOnMethods={"sincrono"})
	public void testSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",dependsOnMethods={"testSincrono"})
	public void sincrono_Attachments() throws Exception{
	
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincrono);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		//GLI ATTACHMENTS DI RICHIESTA SONO STATI BRUCIATI con ClientHttpGenerico
		//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());

	}
	@DataProvider (name="Sincrono_Attachments")
	public Object[][]testSincrono_Attachments()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono_Attachments",
			dependsOnMethods={"sincrono_Attachments"})
	public void testSincrono_Attachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,null, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono stateful
	 */
	Repository repositorySincronoStateful=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_STATEFUL"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoStateful() throws Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoStateful);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
	}
	@DataProvider (name="SincronoStateful")
	public Object[][]testSincronoStateful()throws Exception{
		String id=this.repositorySincronoStateful.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_STATEFUL"},dataProvider="SincronoStateful",dependsOnMethods={"sincronoStateful"})
	public void testSincronoStateful(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_STATEFUL"},
			description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali",
			dependsOnMethods={"testSincronoStateful"})
	public void sincronoStateful_Attachments() throws Exception{
		
		// Creazione client Sincrono
		ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoStateful);
		client.setSoapAction("\"TEST\"");
		client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
		client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_STATEFUL);
		client.connectToSoapEngine();
		client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoap11WithAttachmentsFileName(), false,addIDUnivoco);
		client.setRispostaDaGestire(true);
		client.run();

		// Test uguaglianza Body (e attachments)
		Assert.assertTrue(client.isEqualsSentAndResponseMessage());
		//GLI ATTACHMENTS DI RICHIESTA SONO STATI BRUCIATI con ClientHttpGenerico
		//Assert.assertTrue(client.isEqualsSentAndResponseAttachments());
	}
	@DataProvider (name="SincronoStateful_Attachments")
	public Object[][]testSincronoStateful_Attachments()throws Exception{
		String id=this.repositorySincronoStateful.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_STATEFUL"},dataProvider="SincronoStateful_Attachments",
			dependsOnMethods={"sincronoStateful_Attachments"})
	public void testSincronoStateful_Attachments(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_STATEFUL, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/***
	 * Test per il profilo di collaborazione OneWay
	 */
	Repository repositoryOneWayWithWSA=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_WSADDRESSING"})
	public void oneWayWithWSA() throws TestSuiteException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOneWayWithWSA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_INTEGRAZIONE_WSADDRESSING);
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
	@DataProvider (name="OneWayWithWSA")
	public Object[][]testOneWayWithWSA() throws Exception{
		String id=this.repositoryOneWayWithWSA.getNext();
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
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".ONEWAY_WSADDRESSING"},dataProvider="OneWayWithWSA",dependsOnMethods={"oneWayWithWSA"})
	public void testOneWayWithWSA(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testOneWay(data,id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
					CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincronoWithWSA=new Repository();
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_WSADDRESSING"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoWithWSA() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getSoap11FileName()));

			Message msg=new Message(fin);
			msg.getSOAPPartAsBytes();
			
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySincronoWithWSA);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_INTEGRAZIONE_WSADDRESSING);
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
	@DataProvider (name="SincronoWithWSA")
	public Object[][]testSincronoWithWSA()throws Exception{
		String id=this.repositorySincronoWithWSA.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SINCRONO_WSADDRESSING"},dataProvider="SincronoWithWSA",dependsOnMethods={"sincronoWithWSA"})
	public void testSincronoWithWSA(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_INTEGRAZIONE_WSADDRESSING, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Funzione "Scarta Body" 
	 */
	@Test(groups={VerificaTimeoutGestioneContentLength.ID_GRUPPO,VerificaTimeoutGestioneContentLength.ID_GRUPPO+".SCARTA_BODY"})
	public void testScartaBodyNonRiuscito() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_SCARTA_BODY);
			client.connectToSoapEngine();
			client.setMessageWithAttachmentsFromFile(Utilities.testSuiteProperties.getSoapWithAttachments_attachAsXML_FileName(), false);
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

				Message msg = client.getResponseMessage();
				
				// Verifico che siano presenti solo 1 attachments, a fronte dei 2 inviati.
				// Questo poiche' lo sbustamento della PA aveva la gestione manifest attiva. Quindi ha riportato un attachments di ruolo richiesta come body
				// Tale messaggio con 1 attach e 1 body e' ritornato indietro dal servizio di Echo, ritornato alla PA con funzionalita' scarta body che quindi ha bruciato a sua volta il body
				// che conteneva uno dei due attach.
				// Quindi il messaggio di risposta deve contenere solo il secondo dei due attachments ritornato come body e nessun attachments.
				Assert.assertTrue(msg.getSOAPBody()!=null);
				Assert.assertTrue(msg.getSOAPBody().hasChildNodes());
				Assert.assertTrue(msg.getSOAPBody().hasFault()==false);
				Assert.assertTrue("test2".equals(msg.getSOAPBody().getFirstChild().getLocalName()));
				Assert.assertTrue(msg.countAttachments()==0);
				
			} catch (AxisFault error) {
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
	}
}
