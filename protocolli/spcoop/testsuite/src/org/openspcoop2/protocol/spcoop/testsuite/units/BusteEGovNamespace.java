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
import java.util.Vector;

import javax.activation.DataHandler;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.protocol.spcoop.utils.SPCoopUtils;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


/**
 * Test sulle funzionalita' E-Gov
 *  
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGovNamespace {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "BusteEGovNamespace";


	
	
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
	
	
	

	





	/**
	 * Controllo namespace 1: prefisso standard eGov_IT
	 */
	Repository repositoryNamespace1=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace1"})
	public void clientNamespace1()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace1.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace1);
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
	
	@DataProvider (name="clientNamespace1")
	public Object[][]testNamespace1()throws Exception{
		String id=this.repositoryNamespace1.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace1"},dataProvider="clientNamespace1",dependsOnMethods="clientNamespace1")
	public void testNamespace1(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}



	
	
	
	
	
	
	/**
	 * Controllo namespace 2: prefisso utilizzato per TUTTA la busta: EGOV_PERSONALIZZATO
	 */
	Repository repositoryNamespace2=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace2"})
	public void clientNamespace2()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace2.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<EGOV_PERSONALIZZATO:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:EGOV_PERSONALIZZATO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<EGOV_PERSONALIZZATO:IntestazioneMessaggio>\n");
		busta.append("<EGOV_PERSONALIZZATO:Mittente><EGOV_PERSONALIZZATO:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</EGOV_PERSONALIZZATO:IdentificativoParte></EGOV_PERSONALIZZATO:Mittente>\n");
		busta.append("<EGOV_PERSONALIZZATO:Destinatario><EGOV_PERSONALIZZATO:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</EGOV_PERSONALIZZATO:IdentificativoParte></EGOV_PERSONALIZZATO:Destinatario>\n");
		busta.append("<EGOV_PERSONALIZZATO:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</EGOV_PERSONALIZZATO:ProfiloCollaborazione>\n");
		busta.append("<EGOV_PERSONALIZZATO:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</EGOV_PERSONALIZZATO:Servizio>\n");
		busta.append("<EGOV_PERSONALIZZATO:Messaggio>\n");
		busta.append("<EGOV_PERSONALIZZATO:Identificatore>"+idegov+"</EGOV_PERSONALIZZATO:Identificatore>\n");
		busta.append("<EGOV_PERSONALIZZATO:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</EGOV_PERSONALIZZATO:OraRegistrazione>\n");
		busta.append("</EGOV_PERSONALIZZATO:Messaggio>\n");
		busta.append("</EGOV_PERSONALIZZATO:IntestazioneMessaggio>\n");
		busta.append("</EGOV_PERSONALIZZATO:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace2);
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
	
	@DataProvider (name="clientNamespace2")
	public Object[][]testNamespace2()throws Exception{
		String id=this.repositoryNamespace2.getNext();
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
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace2"},dataProvider="clientNamespace2",dependsOnMethods="clientNamespace2")
	public void testNamespace2(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace 3: prefisso utilizzato per TUTTA la busta: ""
	 */
	Repository repositoryNamespace3=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace3"})
	public void clientNamespace3()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace3.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<IntestazioneMessaggio>\n");
		busta.append("<Mittente><IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</IdentificativoParte></Mittente>\n");
		busta.append("<Destinatario><IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</IdentificativoParte></Destinatario>\n");
		busta.append("<ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</ProfiloCollaborazione>\n");
		busta.append("<Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</Servizio>\n");
		busta.append("<Messaggio>\n");
		busta.append("<Identificatore>"+idegov+"</Identificatore>\n");
		busta.append("<OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</OraRegistrazione>\n");
		busta.append("</Messaggio>\n");
		busta.append("</IntestazioneMessaggio>\n");
		busta.append("</Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace3);
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
	
	@DataProvider (name="clientNamespace3")
	public Object[][]testNamespace3()throws Exception{
		String id=this.repositoryNamespace3.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace3"},dataProvider="clientNamespace3",dependsOnMethods="clientNamespace3")
	public void testNamespace3(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	/**
	 * Controllo namespace 4: prefissi ibridi, default eGov_IT
	 */
	Repository repositoryNamespace4=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace4"})
	public void clientNamespace4()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace4.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:RIDEFINITO_ENVELOPE=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente xmlns:PROVA_RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\"><PROVA_RIDEFINITO:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</PROVA_RIDEFINITO:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<RIDEFINITO_ENVELOPE:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</RIDEFINITO_ENVELOPE:ProfiloCollaborazione>\n");
		busta.append("<RIDEFINITO:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\" xmlns:RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</RIDEFINITO:Servizio>\n");
		busta.append("<Messaggio xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<Identificatore>"+idegov+"</Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace4);
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
	
	@DataProvider (name="clientNamespace4")
	public Object[][]testNamespace4()throws Exception{
		String id=this.repositoryNamespace4.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace4"},dataProvider="clientNamespace4",dependsOnMethods="clientNamespace4")
	public void testNamespace4(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace 5: prefissi ibridi, default ""
	 */
	Repository repositoryNamespace5=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace5"})
	public void clientNamespace5()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace5.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:RIDEFINITO_ENVELOPE=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<IntestazioneMessaggio>\n");
		busta.append("<Mittente xmlns:PROVA_RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\"><PROVA_RIDEFINITO:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</PROVA_RIDEFINITO:IdentificativoParte></Mittente>\n");
		busta.append("<Destinatario><IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</IdentificativoParte></Destinatario>\n");
		busta.append("<RIDEFINITO_ENVELOPE:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</RIDEFINITO_ENVELOPE:ProfiloCollaborazione>\n");
		busta.append("<RIDEFINITO:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\" xmlns:RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</RIDEFINITO:Servizio>\n");
		busta.append("<Messaggio xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<Identificatore>"+idegov+"</Identificatore>\n");
		busta.append("<OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</OraRegistrazione>\n");
		busta.append("</Messaggio>\n");
		busta.append("</IntestazioneMessaggio>\n");
		busta.append("</Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace5);
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
	
	@DataProvider (name="clientNamespace5")
	public Object[][]testNamespace5()throws Exception{
		String id=this.repositoryNamespace5.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace5"},dataProvider="clientNamespace5",dependsOnMethods="clientNamespace5")
	public void testNamespace5(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace 6: prefissi ibridi, default "", namespace root definito in header
	 */
	Repository repositoryNamespace6=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace6"})
	public void clientNamespace6()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespace6.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:RIDEFINITO_ENVELOPE=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n");
		busta.append("<IntestazioneMessaggio>\n");
		busta.append("<Mittente xmlns:PROVA_RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\"><PROVA_RIDEFINITO:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</PROVA_RIDEFINITO:IdentificativoParte></Mittente>\n");
		busta.append("<Destinatario><IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</IdentificativoParte></Destinatario>\n");
		busta.append("<RIDEFINITO_ENVELOPE:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</RIDEFINITO_ENVELOPE:ProfiloCollaborazione>\n");
		busta.append("<RIDEFINITO:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\" xmlns:RIDEFINITO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</RIDEFINITO:Servizio>\n");
		busta.append("<Messaggio xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<Identificatore>"+idegov+"</Identificatore>\n");
		busta.append("<OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</OraRegistrazione>\n");
		busta.append("</Messaggio>\n");
		busta.append("</IntestazioneMessaggio>\n");
		busta.append("</Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespace6);
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
	
	@DataProvider (name="clientNamespace6")
	public Object[][]testNamespace6()throws Exception{
		String id=this.repositoryNamespace6.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Namespace6"},dataProvider="clientNamespace6",dependsOnMethods="clientNamespace6")
	public void testNamespace6(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace errato 1: prefisso utilizzato non legato a namespace egov
	 */
	Repository repositoryNamespaceErrato1=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".NamespaceErrato1"})
	public void clientNamespaceErrato1()throws TestSuiteException, SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryNamespaceErrato1.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT_ERRATO:Identificatore xmlns:eGov_IT_ERRATO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/ERRATO\">"+idegov+"</eGov_IT_ERRATO:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespaceErrato1);
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
			throw new Exception("Attesa eccezione per actor e must understand non qualificati");
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Messaggio/Identificatore");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	@DataProvider (name="clientNamespaceErrato1")
	public Object[][]testNamespaceErrato1()throws Exception{
		String id=this.repositoryNamespaceErrato1.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".NamespaceErrato1"},dataProvider="clientNamespaceErrato1",dependsOnMethods="clientNamespaceErrato1")
	public void testNamespaceErrato1(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo SOAP ACTOR e MustUnderstand: prefisso standard 
	 */
	Repository repositorySoapActorMustUnderstand1=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand1"})
	public void clientSoapActorMustUnderstand1()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorySoapActorMustUnderstand1.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActorMustUnderstand1);
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
	
	@DataProvider (name="clientSoapActorMustUnderstand1")
	public Object[][]testSoapActorMustUnderstand1()throws Exception{
		String id=this.repositorySoapActorMustUnderstand1.getNext();
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
			Thread.sleep(5000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand1"},dataProvider="clientSoapActorMustUnderstand1",dependsOnMethods="clientSoapActorMustUnderstand1")
	public void testSoapActorMustUnderstand1(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo SOAP ACTOR e MustUnderstand: utilizzo prefisso soapenv definito nell'Envelope
	 */
	Repository repositorySoapActorMustUnderstand2=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand2"})
	public void clientSoapActorMustUnderstand2()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorySoapActorMustUnderstand2.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione soapenv:actor=\"http://www.cnipa.it/eGov_it/portadominio\" soapenv:mustUnderstand=\"1\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActorMustUnderstand2);
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
	
	@DataProvider (name="clientSoapActorMustUnderstand2")
	public Object[][]testSoapActorMustUnderstand2()throws Exception{
		String id=this.repositorySoapActorMustUnderstand2.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand2"},dataProvider="clientSoapActorMustUnderstand2",dependsOnMethods="clientSoapActorMustUnderstand2")
	public void testSoapActorMustUnderstand2(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo SOAP ACTOR e MustUnderstand: utilizzo prefisso ""
	 */
	Repository repositorySoapActorMustUnderstand3=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand3"})
	public void clientSoapActorMustUnderstand3()throws TestSuiteException, SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorySoapActorMustUnderstand3.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione actor=\"http://www.cnipa.it/eGov_it/portadominio\" mustUnderstand=\"1\" xmlns=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActorMustUnderstand3);
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
			
			throw new Exception("Attesa eccezione per actor e must understand non qualificati");
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con actor scorretto");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
	}
	
	@DataProvider (name="clientSoapActorMustUnderstand3")
	public Object[][]testSoapActorMustUnderstand3()throws Exception{
		String id=this.repositorySoapActorMustUnderstand3.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand3"},dataProvider="clientSoapActorMustUnderstand3",dependsOnMethods="clientSoapActorMustUnderstand3")
	public void testSoapActorMustUnderstand3(DatabaseComponent data,String id) throws Exception{
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopActorNonCorretto()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY,ProfiloDiCollaborazione.ONEWAY));
		
				Reporter.log("-------------- RISPOSTA --------------------");
		
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
		
		
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+" 2 volte");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==2);
				}else{
					Assert.assertTrue(num==4);
				}
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO));
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
		
				// Che esistano SOLAMENTE 2 eccezioni me lo garantisce il controllo soprastante.
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR.toString()));
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND.toString()));

				
			}else{
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo SOAP ACTOR e MustUnderstand: utilizzo prefisso "PREFIX_PERSONALIZZATO"
	 */
	Repository repositorySoapActorMustUnderstand4=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand4"})
	public void clientSoapActorMustUnderstand4()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorySoapActorMustUnderstand4.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione PREFIX_PERSONALIZZATO:actor=\"http://www.cnipa.it/eGov_it/portadominio\" PREFIX_PERSONALIZZATO:mustUnderstand=\"1\" xmlns:PREFIX_PERSONALIZZATO=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActorMustUnderstand4);
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
	
	@DataProvider (name="clientSoapActorMustUnderstand4")
	public Object[][]testSoapActorMustUnderstand4()throws Exception{
		String id=this.repositorySoapActorMustUnderstand4.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand4"},dataProvider="clientSoapActorMustUnderstand4",dependsOnMethods="clientSoapActorMustUnderstand4")
	public void testSoapActorMustUnderstand4(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo SOAP ACTOR e MustUnderstand: prefisso ibrido 
	 */
	Repository repositorySoapActorMustUnderstand5=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand5"})
	public void clientSoapActorMustUnderstand5()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorySoapActorMustUnderstand5.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" soapenv:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		busta.append("<soapenv:Body>\n");
		busta.append("<helloworld>Hello World</helloworld>\n");
		busta.append("</soapenv:Body>\n");
		busta.append("</soapenv:Envelope>\n");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoapActorMustUnderstand5);
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
	
	@DataProvider (name="clientSoapActorMustUnderstand5")
	public Object[][]testSoapActorMustUnderstand5()throws Exception{
		String id=this.repositorySoapActorMustUnderstand5.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".SoapActorMustUnderstand5"},dataProvider="clientSoapActorMustUnderstand5",dependsOnMethods="clientSoapActorMustUnderstand5")
	public void testSoapActorMustUnderstand5(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest 1: standard
	 */
	Repository repositoryManifest1=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest1"})
	public void clientManifest1()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryManifest1.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<eGov_IT:Descrizione xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<eGov_IT:DescrizioneMessaggio>");
		busta.append("<eGov_IT:Riferimento href=\"cid:Request\" role=\"Richiesta\" eGov_IT:id=\"attachment1\">");
		busta.append("<eGov_IT:Schema posizione=\"text/plain\"/>");
		busta.append("<eGov_IT:Titolo Lingua=\"it\">Richiesta</eGov_IT:Titolo>");
		busta.append("</eGov_IT:Riferimento>");
		busta.append("</eGov_IT:DescrizioneMessaggio>");
		busta.append("</eGov_IT:Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryManifest1);
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
	
	@DataProvider (name="clientManifest1")
	public Object[][]testManifest1()throws Exception{
		String id=this.repositoryManifest1.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest1"},dataProvider="clientManifest1",dependsOnMethods="clientManifest1")
	public void testManifest1(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest 2: prefisso PERSONALIZZATO
	 */
	Repository repositoryManifest2=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest2"})
	public void clientManifest2()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryManifest2.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<PERSONALIZZATO:Descrizione xmlns:PERSONALIZZATO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<PERSONALIZZATO:DescrizioneMessaggio>");
		busta.append("<PERSONALIZZATO:Riferimento href=\"cid:Request\" role=\"Richiesta\" PERSONALIZZATO:id=\"attachment1\">");
		busta.append("<PERSONALIZZATO:Schema posizione=\"text/plain\"/>");
		busta.append("<PERSONALIZZATO:Titolo Lingua=\"it\">Richiesta</PERSONALIZZATO:Titolo>");
		busta.append("</PERSONALIZZATO:Riferimento>");
		busta.append("</PERSONALIZZATO:DescrizioneMessaggio>");
		busta.append("</PERSONALIZZATO:Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryManifest2);
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
	
	@DataProvider (name="clientManifest2")
	public Object[][]testManifest2()throws Exception{
		String id=this.repositoryManifest2.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest2"},dataProvider="clientManifest2",dependsOnMethods="clientManifest2")
	public void testManifest2(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest 1: prefisso "" ma attributo id qualificato
	 */
	Repository repositoryManifest3=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest3"})
	public void clientManifest3()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryManifest3.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<Descrizione xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<DescrizioneMessaggio>");
		busta.append("<Riferimento href=\"cid:Request\" role=\"Richiesta\" ns1:id=\"attachment1\" xmlns:ns1=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<Schema posizione=\"text/plain\"/>");
		busta.append("<Titolo Lingua=\"it\">Richiesta</Titolo>");
		busta.append("</Riferimento>");
		busta.append("</DescrizioneMessaggio>");
		busta.append("</Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryManifest3);
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
	
	@DataProvider (name="clientManifest3")
	public Object[][]testManifest3()throws Exception{
		String id=this.repositoryManifest3.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest3"},dataProvider="clientManifest3",dependsOnMethods="clientManifest3")
	public void testManifest3(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest 4: mixed prefix
	 */
	Repository repositoryManifest4=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest4"})
	public void clientManifest4()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryManifest4.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<eGov_IT:Descrizione xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\" xmlns:NAMESPACEPERID=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<AAA:DescrizioneMessaggio xmlns:AAA=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<BBB:Riferimento href=\"cid:Request\" role=\"Richiesta\" NAMESPACEPERID:id=\"attachment1\" xmlns:BBB=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<AAA:Schema posizione=\"text/plain\"/>");
		busta.append("<eGov_IT:Titolo Lingua=\"it\">Richiesta</eGov_IT:Titolo>");
		busta.append("</BBB:Riferimento>");
		busta.append("</AAA:DescrizioneMessaggio>");
		busta.append("</eGov_IT:Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryManifest4);
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
	
	@DataProvider (name="clientManifest4")
	public Object[][]testManifest4()throws Exception{
		String id=this.repositoryManifest4.getNext();
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
			Thread.sleep(6000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest4"},dataProvider="clientManifest4",dependsOnMethods="clientManifest4")
	public void testManifest4(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * Controllo namespace Manifest 5: namespace eGov_IT definito nel body e ridefinito nell'elemento descrizione con namespace diverso e non utilizzato (Ma comunque corretto!).
	 */
	Repository repositoryManifest5=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest5"})
	public void clientManifest5()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryManifest5.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>\n");
		
		// TEST1
		busta.append("<soapenv:Body xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:Descrizione xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");

		busta.append("<eGov_IT:DescrizioneMessaggio>\n");
		busta.append("<eGov_IT:Riferimento eGov_IT:id=\"attachment2\" href=\"cid:Request\" role=\"Richiesta\">\n");
		busta.append("<eGov_IT:Schema posizione=\"text/xml\" />\n");
		busta.append("<eGov_IT:Titolo Lingua=\"it\">Richiesta</eGov_IT:Titolo>\n");
		busta.append("</eGov_IT:Riferimento>\n");
		busta.append("</eGov_IT:DescrizioneMessaggio>\n");
		busta.append("</eGov_IT:Descrizione>\n");
		busta.append("</soapenv:Body>\n");
		
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryManifest5);
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
	
	@DataProvider (name="clientManifest5")
	public Object[][]testManifest5()throws Exception{
		String id=this.repositoryManifest5.getNext();
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
			Thread.sleep(6000);
		}catch(InterruptedException e){}
		//}	
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}		
		};
	}
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".Manifest5"},dataProvider="clientManifest5",dependsOnMethods="clientManifest5")
	public void testManifest5(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest Errore 1: prefisso "" e attributo id non qualificato in Riferimento
	 */
	Repository repositoryErroreManifest1=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".ErroreManifest1"})
	public void clientErroreManifest1()throws TestSuiteException, SOAPException, Exception{
		
		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryErroreManifest1.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<Descrizione xmlns=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">");
		busta.append("<DescrizioneMessaggio>");
		busta.append("<Riferimento href=\"cid:Request\" role=\"Richiesta\" id=\"attachment1\">");
		busta.append("<Schema posizione=\"text/plain\"/>");
		busta.append("<Titolo Lingua=\"it\">Richiesta</Titolo>");
		busta.append("</Riferimento>");
		busta.append("</DescrizioneMessaggio>");
		busta.append("</Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryErroreManifest1);
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
			
			throw new Exception("Attesa eccezione per attributo id non qualificato");
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	
	@DataProvider (name="clientErroreManifest1")
	public Object[][]testErroreManifest1()throws Exception{
		String id=this.repositoryErroreManifest1.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".ErroreManifest1"},dataProvider="clientErroreManifest1",dependsOnMethods="clientErroreManifest1")
	public void testErroreManifest1(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Controllo namespace Manifest Errato 2: namespace errato in manifest
	 */
	Repository repositoryErroreManifest2=new Repository();
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".ErroreManifest2"})
	public void clientErroreManifest2()throws TestSuiteException, SOAPException, Exception{

		String idegov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositoryErroreManifest2.add(idegov);
		
		StringBuffer busta = new StringBuffer();
		busta.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
		busta.append("<soapenv:Header>\n");
		busta.append("<eGov_IT:Intestazione SOAP_ENV:actor=\"http://www.cnipa.it/eGov_it/portadominio\" SOAP_ENV:mustUnderstand=\"1\" xmlns:SOAP_ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\">\n");
		busta.append("<eGov_IT:IntestazioneMessaggio>\n");
		busta.append("<eGov_IT:Mittente><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+"</eGov_IT:IdentificativoParte></eGov_IT:Mittente>\n");
		busta.append("<eGov_IT:Destinatario><eGov_IT:IdentificativoParte tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE+"\">"+CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE+"</eGov_IT:IdentificativoParte></eGov_IT:Destinatario>\n");
		busta.append("<eGov_IT:ProfiloCollaborazione>"+SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY+"</eGov_IT:ProfiloCollaborazione>\n");
		busta.append("<eGov_IT:Servizio tipo=\""+CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY+"\">"+CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY+"</eGov_IT:Servizio>\n");
		busta.append("<eGov_IT:Messaggio>\n");
		busta.append("<eGov_IT:Identificatore>"+idegov+"</eGov_IT:Identificatore>\n");
		busta.append("<eGov_IT:OraRegistrazione tempo=\""+"EGOV_IT_Locale"+"\">"+SPCoopUtils.getDate_eGovFormat(new Date())+"</eGov_IT:OraRegistrazione>\n");
		busta.append("</eGov_IT:Messaggio>\n");
		busta.append("</eGov_IT:IntestazioneMessaggio>\n");
		busta.append("</eGov_IT:Intestazione>\n");
		busta.append("</soapenv:Header>");
		busta.append("<soapenv:Body>");
		busta.append("<eGov_IT:Descrizione xmlns:eGov_IT=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/\" xmlns:PREFIX_ERRATO=\"http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0ERRATO/\">");
		busta.append("<eGov_IT:DescrizioneMessaggio>");
		busta.append("<eGov_IT:Riferimento href=\"cid:Request\" role=\"Richiesta\" eGov_IT:id=\"attachment1\">");
		busta.append("<PREFIX_ERRATO:Schema posizione=\"text/plain\"/>");
		busta.append("<eGov_IT:Titolo Lingua=\"it\">Richiesta</eGov_IT:Titolo>");
		busta.append("</eGov_IT:Riferimento>");
		busta.append("</eGov_IT:DescrizioneMessaggio>");
		busta.append("</eGov_IT:Descrizione>");
		busta.append("</soapenv:Body>");
		busta.append("</soapenv:Envelope>");
		String bustaSOAP=busta.toString();

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		
		AttachmentPart ap = msg.createAttachmentPart();
		ap.setContentId("Request");
		ap.setDataHandler(new DataHandler("<helloworld>Hello World</helloworld>", "text/xml"));
		msg.addAttachmentPart(ap);
		
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryErroreManifest2);
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
			throw new Exception("Attesa eccezione per prefix namespace errato");
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
		
	}
	
	@DataProvider (name="clientErroreManifest2")
	public Object[][]testErroreManifest2()throws Exception{
		String id=this.repositoryErroreManifest2.getNext();
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
	@Test(groups={BusteEGovNamespace.ID_GRUPPO,BusteEGovNamespace.ID_GRUPPO+".ErroreManifest2"},dataProvider="clientErroreManifest2",dependsOnMethods="clientErroreManifest2")
	public void testErroreManifest2(DatabaseComponent data,String id) throws Exception{
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, null));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}


