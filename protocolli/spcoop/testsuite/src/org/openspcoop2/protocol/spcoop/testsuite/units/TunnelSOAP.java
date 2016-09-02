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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.axis.message.MessageElement;
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
import org.w3c.dom.Element;

/**
 * Test sui tunnel soap implementati nella porta
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TunnelSOAP {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "TunnelSOAP";
	
	/** Gestore della Collaborazione di Base */
	
	private CooperazioneBaseInformazioni infoWithAttach = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseWithAttach = 
		new CooperazioneBase(true, SOAPVersion.SOAP11, this.infoWithAttach, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	
	private CooperazioneBaseInformazioni infoWithoutAttach = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBaseWithoutAttach = 
		new CooperazioneBase(false, SOAPVersion.SOAP11, this.infoWithoutAttach, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	




	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 
	@AfterGroups (alwaysRun=true , groups=ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
	} 
	
	
	
	
	private boolean isEqualsRichiestaRisposta(String xmlRichiestaString,String xmlRispostaString) throws Exception{
		// Check richiesta e risposta
		String prefix = "<wrapper>";
		String suffix = "</wrapper>";
		
		if(xmlRichiestaString.charAt(xmlRichiestaString.length()-1) == '\n')
			xmlRichiestaString = xmlRichiestaString.substring(0,(xmlRichiestaString.length()-1));
		String tmpRichiesta = prefix + xmlRichiestaString + suffix;
		//System.out.println("RICHIESTA TMP ["+tmpRichiesta+"]");
		Element xmlRichiesta = org.openspcoop2.message.XMLUtils.getInstance().newElement(tmpRichiesta.getBytes());
		MessageElement xmlRichiestaAxis = new MessageElement(xmlRichiesta);
			
		String tmpRisposta = prefix + xmlRispostaString + suffix;
		//System.out.println("RISPOSTA TMP ["+tmpRisposta+"]");
		Element xmlRisposta = org.openspcoop2.message.XMLUtils.getInstance().newElement(tmpRisposta.getBytes());
		MessageElement xmlRispostaAxis = new MessageElement(xmlRisposta);
		
		
		boolean value = org.openspcoop2.testsuite.core.Utilities.
		equalsSoapElements(xmlRichiestaAxis, 
				   xmlRispostaAxis,
				   new Vector<String>());
		if(!value){
			System.out.println("RICHIESTA ["+xmlRichiestaAxis.getAsString()+"]");
			System.out.println("RISPOSTA  ["+xmlRispostaAxis.getAsString()+"]");
		}
				
		return value;
	}
	
	private boolean isEqualsRichiestaRisposta(byte[] richiesta,byte[]risposta) throws Exception{
		if(richiesta==null){
			return false;
		}
		if(risposta==null){
			return false;
		}
		if(richiesta.length!=risposta.length){
			File tmpRichiesta = File.createTempFile("check", ".richiesta");
			File tmpRisposta = File.createTempFile("check", ".risposta");
			org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpRichiesta, richiesta);
			org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpRisposta, risposta);
			System.out.println("DIMENSIONI DIVERSE, msg salvati in ["+tmpRichiesta.getAbsolutePath()+"] e ["+tmpRisposta.getAbsolutePath()+"]");
			return false;
		}
		for(int i=0;i<richiesta.length;i++){
			if(richiesta[i]!=risposta[i]){
				File tmpRichiesta = File.createTempFile("check", ".richiesta");
				File tmpRisposta = File.createTempFile("check", ".risposta");
				org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpRichiesta, richiesta);
				org.openspcoop2.utils.resources.FileSystemUtilities.writeFile(tmpRisposta, risposta);
				System.out.println("DIMENSIONI DIVERSE, msg salvati in ["+tmpRichiesta.getAbsolutePath()+"] e ["+tmpRisposta.getAbsolutePath()+"]");
				return false;
			}
		}
		return true;
	}
	
	
	


	/***
	 * Test per il profilo di collaborazione Sincrono
	 */
	Repository repositorySincrono=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincrono() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincrono);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		
		// Controllo uguaglianza richiesta e risposta
		Assert.assertTrue(this.isEqualsRichiestaRisposta(bout.toString(), new String(client.getMessaggioXMLRisposta())));
		
		// Vecchio controllo basato su stringhe: 
		/*String xmlRichiesta = bout.toString();
		String xmlRisposta = new String(client.getMessaggioXMLRisposta());
		if(xmlRichiesta.charAt(xmlRichiesta.length()-1) == '\n')
			xmlRichiesta = xmlRichiesta.substring(0,(xmlRichiesta.length()-1));
		//System.out.println("Messaggio di richiesta: ["+xmlRichiesta+"]");
		//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		Assert.assertTrue(xmlRichiesta.equals(xmlRisposta));*/
		
	}
	@DataProvider (name="Sincrono")
	public Object[][]testSincrono()throws Exception{
		String id=this.repositorySincrono.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO"},dataProvider="Sincrono",dependsOnMethods={"sincrono"})
	public void testSincrono(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Oneway
	 */
	Repository repositorynotifica=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY"},description="Test di tipo notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void notifica() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorynotifica);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="notifica")
	public Object[][]testnotifica()throws Exception{
		String id=this.repositorynotifica.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY"},dataProvider="notifica",dependsOnMethods={"notifica"})
	public void testnotifica(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con xml multipart related mime
	 */
	Repository repositorySincronoMultipartRelatedMIME=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_MUTIPART_RELATED"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoMultipartRelatedMIME() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlMultipartRelatedSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoMultipartRelatedMIME);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_MULTIPART_RELATED_MIME);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		
		String xmlRichiesta = bout.toString();
		if(xmlRichiesta.indexOf("<eseguiServizio")<0){
			throw new Exception("XML richiesta malformato");
		}
		if(xmlRichiesta.indexOf("eseguiServizio>")<0){
			throw new Exception("XML richiesta malformato (2)");
		}
		xmlRichiesta = xmlRichiesta.substring(xmlRichiesta.indexOf("<eseguiServizio"),(xmlRichiesta.indexOf("eseguiServizio>")+"eseguiServizio>".length()));
		
		String xmlRisposta = new String(client.getMessaggioXMLRisposta());
		if(xmlRisposta.indexOf("<eseguiServizio")<0){
			throw new Exception("XML richiesta malformato");
		}
		if(xmlRisposta.indexOf("eseguiServizio>")<0){
			throw new Exception("XML richiesta malformato (2)");
		}
		xmlRisposta = xmlRisposta.substring(xmlRisposta.indexOf("<eseguiServizio"),(xmlRisposta.indexOf("eseguiServizio>")+"eseguiServizio>".length()));
		//System.out.println("Messaggio di richiesta: ["+xmlRichiesta+"]");
		//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		
		// Controllo uguaglianza richiesta e risposta
		Assert.assertTrue(this.isEqualsRichiestaRisposta(xmlRichiesta, xmlRisposta));
		
		// Vecchio controllo basato su stringhe: 
		/*
		Assert.assertTrue(xmlRichiesta.equals(xmlRisposta));
		*/
		

		
		// Checks attachments
		Assert.assertTrue(new String(client.getMessaggioXMLRisposta()).contains("HELLO WORLD 1"));
		Assert.assertTrue(new String(client.getMessaggioXMLRisposta()).contains("HELLO WORLD 2"));
		
	}
	@DataProvider (name="SincronoMultipartRelatedMIME")
	public Object[][]testSincronoMultipartRelatedMIME()throws Exception{		
		String id=this.repositorySincronoMultipartRelatedMIME.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_MUTIPART_RELATED"},dataProvider="SincronoMultipartRelatedMIME",dependsOnMethods={"sincronoMultipartRelatedMIME"})
	public void testSincronoMultipartRelatedMIME(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseWithAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_MULTIPART_RELATED_MIME, checkServizioApplicativo,null,
					null,null,true,2,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Oneway con xml multipart related mime
	 */
	Repository repositorynotificaMultipartRelatedMIME=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_MUTIPART_RELATED"},description="Test di tipo notificaMultipartRelatedMIME, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void notificaMultipartRelatedMIME() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlMultipartRelatedSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorynotificaMultipartRelatedMIME);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_MULTIPART_RELATED_MIME);
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="notificaMultipartRelatedMIME")
	public Object[][]testnotificaMultipartRelatedMIME()throws Exception{
		String id=this.repositorynotificaMultipartRelatedMIME.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_MUTIPART_RELATED"},dataProvider="notificaMultipartRelatedMIME",dependsOnMethods={"notificaMultipartRelatedMIME"})
	public void testnotificaMultipartRelatedMIME(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBaseWithAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_MULTIPART_RELATED_MIME_NOTIFICA, checkServizioApplicativo,null,
					null,null,true,2,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositorySincronoAttachmentsOpenSPCoopDoc=new Repository();
	Hashtable<String, Message> repositorySincronoAttachmentsOpenSPCoopDoc_message = new Hashtable<String, Message>();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_DOC"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsOpenSPCoopDoc() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getDOCFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsOpenSPCoopDoc);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo uguaglianza byte spediti e ricevuti
		Assert.assertTrue(this.isEqualsRichiestaRisposta(bout.toByteArray(), client.getMessaggioXMLRisposta()));
		
	}
	@DataProvider (name="SincronoAttachmentsOpenSPCoopDoc")
	public Object[][]testSincronoAttachmentsOpenSPCoopDoc()throws Exception{
		String id=this.repositorySincronoAttachmentsOpenSPCoopDoc.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_DOC"},dataProvider="SincronoAttachmentsOpenSPCoopDoc",dependsOnMethods={"sincronoAttachmentsOpenSPCoopDoc"})
	public void testSincronoAttachmentsOpenSPCoopDoc(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositorySincronoAttachmentsOpenSPCoopZip=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_ZIP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsOpenSPCoopZip() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getZIPFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsOpenSPCoopZip);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo uguaglianza byte spediti e ricevuti
		Assert.assertTrue(this.isEqualsRichiestaRisposta(bout.toByteArray(), client.getMessaggioXMLRisposta()));
	}
	@DataProvider (name="SincronoAttachmentsOpenSPCoopZip")
	public Object[][]testSincronoAttachmentsOpenSPCoopZip()throws Exception{
		String id=this.repositorySincronoAttachmentsOpenSPCoopZip.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_ZIP"},dataProvider="SincronoAttachmentsOpenSPCoopZip",dependsOnMethods={"sincronoAttachmentsOpenSPCoopZip"})
	public void testSincronoAttachmentsOpenSPCoopZip(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositorySincronoAttachmentsOpenSPCoopPdf=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_PDF"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsOpenSPCoopPdf() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getPDFFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsOpenSPCoopPdf);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo uguaglianza byte spediti e ricevuti
		Assert.assertTrue(this.isEqualsRichiestaRisposta(bout.toByteArray(), client.getMessaggioXMLRisposta()));
	}
	@DataProvider (name="SincronoAttachmentsOpenSPCoopPdf")
	public Object[][]testSincronoAttachmentsOpenSPCoopPdf()throws Exception{
		String id=this.repositorySincronoAttachmentsOpenSPCoopPdf.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_PDF"},dataProvider="SincronoAttachmentsOpenSPCoopPdf",dependsOnMethods={"sincronoAttachmentsOpenSPCoopPdf"})
	public void testSincronoAttachmentsOpenSPCoopPdf(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositorySincronoAttachmentsOpenSPCoopFormMultipart=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_FORM_MULTIPART"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsOpenSPCoopFormMultipart() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlMultipartRelatedSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
				
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsOpenSPCoopFormMultipart);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			// SetContentType per il file impostato
			client.setContentType("multipart/related; type=\"text/xml\"; start=\"<56D2051AED8F9598BB61721D8C95BA6F>\";         boundary=\"----=_Part_0_6330713.1171639717331\"");
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
			}finally{
				dbComponentFruitore.close();
			}
			
			Reporter.log("Controllo contenuto ritornato");
			
			// Controllo uguaglianza richiesta e risposta
			String xmlRichiesta = bout.toString();
			if(xmlRichiesta.indexOf("<eseguiServizio")<0){
				throw new Exception("XML richiesta malformato");
			}
			if(xmlRichiesta.indexOf("eseguiServizio>")<0){
				throw new Exception("XML richiesta malformato (2)");
			}
			xmlRichiesta = xmlRichiesta.substring(xmlRichiesta.indexOf("<eseguiServizio"),(xmlRichiesta.indexOf("eseguiServizio>")+"eseguiServizio>".length()));
			
			String xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if(xmlRisposta.indexOf("<eseguiServizio")<0){
				throw new Exception("XML richiesta malformato");
			}
			if(xmlRisposta.indexOf("eseguiServizio>")<0){
				throw new Exception("XML richiesta malformato (2)");
			}
			xmlRisposta = xmlRisposta.substring(xmlRisposta.indexOf("<eseguiServizio"),(xmlRisposta.indexOf("eseguiServizio>")+"eseguiServizio>".length()));
			//System.out.println("Messaggio di richiesta: ["+xmlRichiesta+"]");
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
			
			// Controllo uguaglianza richiesta e risposta
			Assert.assertTrue(this.isEqualsRichiestaRisposta(xmlRichiesta, xmlRisposta));
			
			// Vecchio controllo basato su stringhe: 
			/*
			Assert.assertTrue(bout.toString().equals(new String(client.getMessaggioXMLRisposta())));
			*/
			
			Reporter.log("Controllo contentType ["+client.getContentType()+"] con tipo risposta["+client.getContentTypeRisposta()+"]");
			//Assert.assertTrue(client.getContentType().equals(client.getContentTypeRisposta()));
			Assert.assertTrue(client.getContentTypeRisposta().contains("multipart/related"));
			
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
	@DataProvider (name="SincronoAttachmentsOpenSPCoopFormMultipart")
	public Object[][]testSincronoAttachmentsOpenSPCoopFormMultipart()throws Exception{
		String id=this.repositorySincronoAttachmentsOpenSPCoopFormMultipart.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_OPENSPCOOP_FORM_MULTIPART"},
			dataProvider="SincronoAttachmentsOpenSPCoopFormMultipart",dependsOnMethods={"sincronoAttachmentsOpenSPCoopFormMultipart"})
	public void testSincronoAttachmentsOpenSPCoopFormMultipart(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositoryNotificaAttachmentsOpenSPCoopDoc=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_DOC"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaAttachmentsOpenSPCoopDoc() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getDOCFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaAttachmentsOpenSPCoopDoc);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaAttachmentsOpenSPCoopDoc")
	public Object[][]testNotificaAttachmentsOpenSPCoopDoc()throws Exception{
		String id=this.repositoryNotificaAttachmentsOpenSPCoopDoc.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_DOC"},dataProvider="NotificaAttachmentsOpenSPCoopDoc",dependsOnMethods={"NotificaAttachmentsOpenSPCoopDoc"})
	public void testNotificaAttachmentsOpenSPCoopDoc(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositoryNotificaAttachmentsOpenSPCoopZip=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_ZIP"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaAttachmentsOpenSPCoopZip() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getZIPFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaAttachmentsOpenSPCoopZip);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaAttachmentsOpenSPCoopZip")
	public Object[][]testNotificaAttachmentsOpenSPCoopZip()throws Exception{
		String id=this.repositoryNotificaAttachmentsOpenSPCoopZip.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_ZIP"},dataProvider="NotificaAttachmentsOpenSPCoopZip",dependsOnMethods={"NotificaAttachmentsOpenSPCoopZip"})
	public void testNotificaAttachmentsOpenSPCoopZip(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment 
 	 * con mimeType application/openspcoop2 (codificato in BASE64 il contenuto)
	 */
	Repository repositoryNotificaAttachmentsOpenSPCoopPdf=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_PDF"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaAttachmentsOpenSPCoopPdf() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getPDFFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaAttachmentsOpenSPCoopPdf);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP+"?OpenSPCoop2TunnelSOAP=true");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaAttachmentsOpenSPCoopPdf")
	public Object[][]testNotificaAttachmentsOpenSPCoopPdf()throws Exception{
		String id=this.repositoryNotificaAttachmentsOpenSPCoopPdf.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_OPENSPCOOP_PDF"},dataProvider="NotificaAttachmentsOpenSPCoopPdf",dependsOnMethods={"NotificaAttachmentsOpenSPCoopPdf"})
	public void testNotificaAttachmentsOpenSPCoopPdf(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_TUNNEL_SOAP_ATTACHMENT_OPENSPCOOP_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositorySincronoAttachmentsCustomDoc=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_DOC"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsCustomDoc() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getDOCFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsCustomDoc);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo byte spediti e ricevuti
		String richiesta = bout.toString();
		String risposta = new String(client.getMessaggioXMLRisposta());
		Assert.assertTrue(risposta.contains(richiesta));

	}
	@DataProvider (name="SincronoAttachmentsCustomDoc")
	public Object[][]testSincronoAttachmentsCustomDoc()throws Exception{
		String id=this.repositorySincronoAttachmentsCustomDoc.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_DOC"},dataProvider="SincronoAttachmentsCustomDoc",dependsOnMethods={"sincronoAttachmentsCustomDoc"})
	public void testSincronoAttachmentsCustomDoc(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositorySincronoAttachmentsCustomZip=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_ZIP"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsCustomZip() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getZIPFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsCustomZip);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo byte spediti e ricevuti
		String richiesta = bout.toString();
		String risposta = new String(client.getMessaggioXMLRisposta());
		Assert.assertTrue(risposta.contains(richiesta));
	}
	@DataProvider (name="SincronoAttachmentsCustomZip")
	public Object[][]testSincronoAttachmentsCustomZip()throws Exception{
		String id=this.repositorySincronoAttachmentsCustomZip.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_ZIP"},dataProvider="SincronoAttachmentsCustomZip",dependsOnMethods={"sincronoAttachmentsCustomZip"})
	public void testSincronoAttachmentsCustomZip(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Sincrono Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositorySincronoAttachmentsCustomPdf=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_PDF"},description="Test di tipo sincrono, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoAttachmentsCustomPdf() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getPDFFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositorySincronoAttachmentsCustomPdf);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ATTACHMENT_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		// Controllo byte spediti e ricevuti
		String richiesta = bout.toString();
		String risposta = new String(client.getMessaggioXMLRisposta());
		Assert.assertTrue(risposta.contains(richiesta));
	}
	@DataProvider (name="SincronoAttachmentsCustomPdf")
	public Object[][]testSincronoAttachmentsCustomPdf()throws Exception{
		String id=this.repositorySincronoAttachmentsCustomPdf.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SINCRONO_ATTACHMENT_CUSTOM_PDF"},dataProvider="SincronoAttachmentsCustomPdf",dependsOnMethods={"sincronoAttachmentsCustomPdf"})
	public void testSincronoAttachmentsCustomPdf(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositoryNotificaCustomDoc=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_DOC"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaCustomDoc() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getDOCFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaCustomDoc);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaCustomDoc")
	public Object[][]testNotificaCustomDoc()throws Exception{
		String id=this.repositoryNotificaCustomDoc.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_DOC"},dataProvider="NotificaCustomDoc",dependsOnMethods={"NotificaCustomDoc"})
	public void testNotificaCustomDoc(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositoryNotificaCustomZip=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_ZIP"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaCustomZip() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getZIPFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaCustomZip);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaCustomZip")
	public Object[][]testNotificaCustomZip()throws Exception{
		String id=this.repositoryNotificaCustomZip.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_ZIP"},dataProvider="NotificaCustomZip",dependsOnMethods={"NotificaCustomZip"})
	public void testNotificaCustomZip(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione Notifica con Imbustamento del contenuto in un Attachment con mimeType personalizzato
     * Es: application/octet-stream
	 */
	Repository repositoryNotificaCustomPdf=new Repository();
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_PDF"},description="Test di tipo Notifica, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void NotificaCustomPdf() throws TestSuiteException, IOException, Exception{
		java.io.FileInputStream fin = null;
		DatabaseComponent dbComponentFruitore = null;
		ByteArrayOutputStream bout = null;
		ClientHttpGenerico client=null;
		try{
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getPDFFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			client=new ClientHttpGenerico(this.repositoryNotificaCustomPdf);
			client.setSoapAction("\"TEST\"");
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_ONEWAY_TUNNEL_SOAP_CUSTOM_MIME_TYPE+"?OpenSPCoop2TunnelSOAP=true&OpenSPCoop2TunnelSOAPMimeType=application/octet-stream");
			client.connectToSoapEngine();
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore= DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==202);
				
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				throw error;
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
		
		String xmlRisposta = null;
		if(client.getMessaggioXMLRisposta()!=null){
			xmlRisposta = new String(client.getMessaggioXMLRisposta());
			if("".equals(xmlRisposta))
				xmlRisposta = null;
			//System.out.println("Messaggio di risposta: ["+xmlRisposta+"]");
		}	
		Assert.assertTrue(xmlRisposta==null);
	}
	@DataProvider (name="NotificaCustomPdf")
	public Object[][]testNotificaCustomPdf()throws Exception{
		String id=this.repositoryNotificaCustomPdf.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ONEWAY_ATTACHMENT_CUSTOM_PDF"},dataProvider="NotificaCustomPdf",dependsOnMethods={"NotificaCustomPdf"})
	public void testNotificaCustomPdf(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			// Non riesco a recuperare il messaggio sul quale dovrei ottenere poi gli identificativi per fare il check sul databse delle tracce allegati.
			// Non e' cmq obiettivo di questo test e quindi uso without attach
			this.collaborazioneSPCoopBaseWithoutAttach.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_TUNNEL_SOAP,
					CostantiTestSuite.SPCOOP_SERVIZIO_TUNNEL_SOAP_AZIONE_ATTACHMENT_CUSTOM_MIME_TYPE_NOTIFICA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Funzione "Allega Body" 
	 */
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".ALLEGA_BODY"})
	public void testAllegaBody() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_PROFILO_SINCRONO_TUNNEL_SOAP_ALLEGA_BODY);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
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

				Assert.assertTrue(client.isEqualsSentAndResponseMessage());
				
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
	
	
	
	
	
	
	
	
	
	
	/**
	 * Funzione "Scarta Body" 
	 */
	@Test(groups={TunnelSOAP.ID_GRUPPO,TunnelSOAP.ID_GRUPPO+".SCARTA_BODY"})
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
