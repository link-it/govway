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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
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
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroreApplicativoCNIPA {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "ErroreApplicativoCNIPA";


	
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
	
	
	
	
	
	
	
	
	
	/** ERRORI 4XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi4XX")
	public Object[][] personalizzazioniErroriApplicativi4XX(){
		return new Object[][]{
				{null,null,"PORTA_DELEGATA_NON_ESISTENTE"},
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA}
		};
	}
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi4XX"},dataProvider="personalizzazioniErroriApplicativi4XX")
	public void testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{
		testErroriApplicativi4XX_engine(username, password, portaDelegata);
	}
	public Object testErroriApplicativi4XX_engine(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = DateManager.getDate();
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO);
			String msg = CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO;
			boolean equalsMatch = true;
			if(username==null){
				idPorta = Utilities.testSuiteProperties.getIdentitaDefault_dominio();
				codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PD_INESISTENTE);
				msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
				equalsMatch = false;
			}
			else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				actor = "ACTOR_RIDEFINITO";
				codice = "PREFIX_PERSONALIZZATO_405";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiSOAP", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"RicezioneContenutiApplicativiSOAP", 
						codice, 
						msg, equalsMatch);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
			
			Date dataFineTest = DateManager.getDate();
			
			// Aggiungo errori attesi
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PD/"+portaDelegata+"]: verificare i parametri di accesso utilizzati: [getSoggetto] Soggetto che possiede la porta delegata ["+portaDelegata+"] non esistente");
			//System.out.println("CHECK ["+err.getMsgErrore()+"]");
			this.erroriAttesiOpenSPCoopCore.add(err);
			
			// implementazione su db
			ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
			err2.setIntervalloInferiore(dataInizioTest);
			err2.setIntervalloSuperiore(dataFineTest);
			err2.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PD/"+portaDelegata+"]: verificare i parametri di accesso utilizzati: [DriverConfigurazioneDB::getSoggetto] Soggetto associato alla porta delegata [PORTA_DELEGATA_NON_ESISTENTE] non esistente.");
			//System.out.println("CHECK ["+err.getMsgErrore()+"]");
			this.erroriAttesiOpenSPCoopCore.add(err2);
		}	
				
		return response;
	}
	
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 4XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi4XXTunnelSOAP")
	public Object[][] personalizzazioniErroriApplicativi4XXTunnelSOAP(){
		return new Object[][]{
				{null,null,"PORTA_DELEGATA_NON_ESISTENTE"},
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA}
		};
	}
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi4XX_TUNNEL_SOAP"},dataProvider="personalizzazioniErroriApplicativi4XXTunnelSOAP")
	public void testErroriApplicativi4XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Date dataInizioTest = DateManager.getDate();
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_405_SERVIZIO_NON_TROVATO);
			String msg = CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO;
			boolean equalsMatch = true;
			if(username==null){
				idPorta = Utilities.testSuiteProperties.getIdentitaDefault_dominio();
				codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_401_PD_INESISTENTE);
				msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
				equalsMatch = false;
			}
			else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				codice = "PREFIX_PERSONALIZZATO_405";
			}
			
			try {
				client.run();

				Reporter.log("CODICE HTTP ["+client.getCodiceStatoHTTP()+"]==500");
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiHTTP", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		Date dataFineTest = DateManager.getDate();
		
		// Aggiungo errori attesi
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PDtoSOAP/"+portaDelegata+"]: verificare i parametri di accesso utilizzati: [getSoggetto] Soggetto che possiede la porta delegata ["+portaDelegata+"] non esistente");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		// Errore db
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PDtoSOAP/"+portaDelegata+"]: verificare i parametri di accesso utilizzati: [DriverConfigurazioneDB::getSoggetto] Soggetto associato alla porta delegata [PORTA_DELEGATA_NON_ESISTENTE] non esistente.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORI 5XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi5XX")
	public Object[][] personalizzazioniErroriApplicativi5XX(){
		return new Object[][]{
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX}
		};
	}
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi5XX"},dataProvider="personalizzazioniErroriApplicativi5XX")
	public void testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{
		testErroriApplicativi5XX_engine(username, password, portaDelegata);
	}
	public Object testErroriApplicativi5XX_engine(String username,String password,String portaDelegata) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
			String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				actor = "ACTOR_RIDEFINITO";
				codice = "PREFIX_PERSONALIZZATO_504";
				msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiSOAP", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"RicezioneContenutiApplicativiSOAP", 
						codice, 
						msg, equalsMatch);				
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
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		return response;
	}
	
	
	/** SERVIZIO TUNNEL SOAP: ERRORI 5XX */
	
	@DataProvider (name="personalizzazioniErroriApplicativi5XXTunnelSOAP")
	public Object[][] personalizzazioniErroriApplicativi5XXTunnelSOAP(){
		return new Object[][]{
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX}
		};
	}
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".ErroriApplicativi5XX_TUNNEL_SOAP"},dataProvider="personalizzazioniErroriApplicativi5XXTunnelSOAP")
	public void testErroriApplicativi5XXTunnelSOAP(String username,String password,String portaDelegata) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(username!=null && password!=null)
				client.setAutenticazione(username,password);
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
			String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
				codice = "PREFIX_PERSONALIZZATO_504";
				msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
			}
			
			try {
				client.run();

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"RicezioneContenutiApplicativiHTTP", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	/** SOAP FAULT APPLICATIVO ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativi")
	public Object[][] personalizzazioniFaultApplicativi(){
		return new Object[][]{
				{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		testFaultApplicativoArricchitoFAULTCNIPA_Default_engine(servizioApplicativoFruitore);
	}
	public Object testFaultApplicativoArricchitoFAULTCNIPA_Default_engine(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO;
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				Reporter.log("Controllo details openspcoop example");
				Element [] details = error.getFaultDetails();
				Assert.assertTrue(details!=null);
				Assert.assertTrue(details.length>0);
				Element erroreDetailsOpenSPCoop = null;
				Reporter.log("Details presenti: "+details.length);
				for(int i=0; i<details.length; i++){
					Element detail = details[i];
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
						break;
					}
				}
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
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
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT APPLICATIVO ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiTunnelSOAP")
	public Object[][] personalizzazioniFaultApplicativiTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO_TUNNEL_SOAP"},dataProvider="personalizzazioniFaultApplicativiTunnelSOAP")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_SERVIZIO_APPLICATIVO_SINCRONO;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);

				Element fault = org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo);
				Assert.assertTrue("Fault".equals(fault.getLocalName()));
				Assert.assertTrue("http://schemas.xmlsoap.org/soap/envelope/".equals(fault.getNamespaceURI()));
				
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(fault.getElementsByTagName("faultactor")==null || fault.getElementsByTagName("faultactor").getLength()==0);
				
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue(fault.getElementsByTagName("faultcode")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultcode").getLength()==1);
				String code = fault.getElementsByTagName("faultcode").item(0).getTextContent();
				Reporter.log("Controllo fault code [Server.faultExample]: ["+code+"]");
				Assert.assertTrue(code!=null && ("Server.faultExample".equals(code) || code.endsWith(":Server.faultExample")));
				
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(fault.getElementsByTagName("faultstring")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultstring").getLength()==1);
				Assert.assertTrue("Fault ritornato dalla servlet di esempio di OpenSPCoop".equals(fault.getElementsByTagName("faultstring").item(0).getTextContent()));
				
				Reporter.log("Controllo details ...");
				Assert.assertTrue(fault.getElementsByTagName("detail")!=null);
				NodeList nodeListDetails = fault.getElementsByTagName("detail");
				Assert.assertTrue(nodeListDetails!=null);
				Assert.assertTrue(nodeListDetails.getLength()>0);
				
				Reporter.log("Controllo details childs ...");
				NodeList nodeListDetailsChilds = nodeListDetails.item(0).getChildNodes();
				Assert.assertTrue(nodeListDetailsChilds!=null);
				Assert.assertTrue(nodeListDetailsChilds.getLength()>0);
				Node erroreDetailsOpenSPCoop = null;
				Node erroreCNIPA = null;
				Reporter.log("Details presenti: "+nodeListDetailsChilds.getLength());
				for(int i=0; i<nodeListDetailsChilds.getLength(); i++){
					Node detail = nodeListDetailsChilds.item(i);
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
					}
					if("MessaggioDiErroreApplicativo".equals(detail.getLocalName())){
						erroreCNIPA = detail;
					}
				}
				
				Reporter.log("Controllo details openspcoop example");
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Assert.assertTrue(erroreCNIPA!=null);
				Utilities.verificaErroreApplicativoCnipa(erroreCNIPA, "MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE, Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
				
			} catch (AxisFault error) {
				
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
			fin.close();
		}
				
	}
	
	
	
	
	
	
	
	
	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdD")
	public Object[][] personalizzazioniFaultApplicativiPdD(){
		return new Object[][]{
				{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		testFaultPddArricchitoFAULTCNIPA_Default_engine(servizioApplicativoFruitore);
	}
	public Object testFaultPddArricchitoFAULTCNIPA_Default_engine(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO;
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				Reporter.log("Controllo details openspcoop example");
				Element [] details = error.getFaultDetails();
				Assert.assertTrue(details!=null);
				Assert.assertTrue(details.length>0);
				Element erroreDetailsOpenSPCoop = null;
				Reporter.log("Details presenti: "+details.length);
				for(int i=0; i<details.length; i++){
					Element detail = details[i];
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
						break;
					}
				}
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreSOAPFaultServer"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
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
		return response;	
	}
	
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_2"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale(String servizioApplicativoFruitore) throws Exception{
		testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale_engine(servizioApplicativoFruitore);
	}
	public Object testFaultPddArricchitoFAULTCNIPA_FaultGeneratoTramiteServizioWEBReale_engine(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_IM;
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				String msgAtteso = "No such operation 'getQuote'";
				String msgAtteso2 = "getQuote was not recognized.";
				Reporter.log("Controllo fault string");
				Assert.assertTrue(error.getFaultString().equals(msgAtteso) || error.getFaultString().contains(msgAtteso2));
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSOAPFaultIM"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
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
		return response;	
	}
	
	
	
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_3"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails(String servizioApplicativoFruitore) throws Exception{
		testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails_engine(servizioApplicativoFruitore);
	}
	public Object testFaultPddArricchitoFAULTCNIPA_FaultGeneratoSenzaDetails_engine(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_SOAP_FAULT_SENZA_DETAILS;
		Object response = null;
		try{

			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");

			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(error.getFaultActor()==null);
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue("Server.faultExample".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(error.getFaultString().equals("Fault ritornato dalla servlet di esempio di OpenSPCoop"));
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Utilities.verificaFaultDetailsRispettoErroreApplicativoCnipa(error,"MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSOAPFaultSenzaDetails"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
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
		return response;	
	}
	
	
	/** SERVIZIO TUNNEL SOAP: SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdDTunnelSOAP")
	public Object[][] personalizzazioniFaultApplicativiPdDTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".SOAP_FAULT_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniFaultApplicativiPdDTunnelSOAP")
	public void testFaultPddArricchitoFAULTCNIPA_Default_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_SOAP_FAULT_PDD_DESTINAZIONE_SINCRONO;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
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
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
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

				Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);

				Element fault = org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo);
				Assert.assertTrue("Fault".equals(fault.getLocalName()));
				Assert.assertTrue("http://schemas.xmlsoap.org/soap/envelope/".equals(fault.getNamespaceURI()));
				
				Reporter.log("Controllo actor ["+null+"]");
				Assert.assertTrue(fault.getElementsByTagName("faultactor")==null || fault.getElementsByTagName("faultactor").getLength()==0);
				
				Reporter.log("Controllo fault code [Server.faultExample]");
				Assert.assertTrue(fault.getElementsByTagName("faultcode")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultcode").getLength()==1);
				String code = fault.getElementsByTagName("faultcode").item(0).getTextContent();
				Reporter.log("Controllo fault code [Server.faultExample]: ["+code+"]");
				Assert.assertTrue(code!=null && ("Server.faultExample".equals(code) || code.endsWith(":Server.faultExample")));
				
				Reporter.log("Controllo fault string [Fault ritornato dalla servlet di esempio di OpenSPCoop]");
				Assert.assertTrue(fault.getElementsByTagName("faultstring")!=null);
				Assert.assertTrue(fault.getElementsByTagName("faultstring").getLength()==1);
				Assert.assertTrue("Fault ritornato dalla servlet di esempio di OpenSPCoop".equals(fault.getElementsByTagName("faultstring").item(0).getTextContent()));
				
				Reporter.log("Controllo details ...");
				Assert.assertTrue(fault.getElementsByTagName("detail")!=null);
				NodeList nodeListDetails = fault.getElementsByTagName("detail");
				Assert.assertTrue(nodeListDetails!=null);
				Assert.assertTrue(nodeListDetails.getLength()>0);
				
				Reporter.log("Controllo details childs ...");
				NodeList nodeListDetailsChilds = nodeListDetails.item(0).getChildNodes();
				Assert.assertTrue(nodeListDetailsChilds!=null);
				Assert.assertTrue(nodeListDetailsChilds.getLength()>0);
				Node erroreDetailsOpenSPCoop = null;
				Node erroreCNIPA = null;
				Reporter.log("Details presenti: "+nodeListDetailsChilds.getLength());
				for(int i=0; i<nodeListDetailsChilds.getLength(); i++){
					Node detail = nodeListDetailsChilds.item(i);
					Reporter.log("Detail["+detail.getLocalName()+"]");
					if("detailEsempioOpenSPCoop".equals(detail.getLocalName())){
						erroreDetailsOpenSPCoop = detail;
					}
					if("MessaggioDiErroreApplicativo".equals(detail.getLocalName())){
						erroreCNIPA = detail;
					}
				}
				
				Reporter.log("Controllo details openspcoop example");
				Assert.assertTrue(erroreDetailsOpenSPCoop!=null);
				Utilities.verificaDetailsOpenSPCoopExample(erroreDetailsOpenSPCoop);
				
				String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
				if("erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					codice = "PREFIX_PERSONALIZZATO_516";
				}
				
				Reporter.log("Controllo xml errore applicativo cnipa definito nei details (Codice:"+codice+")");
				Assert.assertTrue(erroreCNIPA!=null);
				Utilities.verificaErroreApplicativoCnipa(erroreCNIPA, "MinisteroFruitoreSPCoopIT","InoltroBuste", 
						codice, 
						CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreSOAPFaultServer"), Utilities.CONTROLLO_DESCRIZIONE_TRAMITE_METODO_EQUALS);				
		
			} catch (AxisFault error) {
				
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
			fin.close();
		}
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefused")
	public Object[][] personalizzazioniErroreConnectionRefused(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_PDD"},dataProvider="personalizzazioniErroreConnectionRefused")
	public void testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{
		testServizioApplicativoConnectionRefused_engine(servizioApplicativoFruitore);
	}
	public Object testServizioApplicativoConnectionRefused_engine(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErrato");
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
				codice = "PREFIX_PERSONALIZZATO_516";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectionRefusedTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniErroreConnectionRefusedTunnelSOAP")
	public void testServizioApplicativoConnectionRefused_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SINCRONO_STATELESS;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErrato");
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				codice = "PREFIX_PERSONALIZZATO_516";
			}
			
			try {
				client.run();

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectionRefusedServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_SA"},dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		testServizioApplicativoConnectionRefusedServizioApplicativo_engine(servizioApplicativoFruitore);
	}
	public Object testServizioApplicativoConnectionRefusedServizioApplicativo_engine(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, connection refused ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECTION_REFUSED_SA_TUNNEL_SOAP"},
			dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativoTunnelSOAP")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			}
			
			try {
				client.run();

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOut")
	public Object[][] personalizzazioniErroreConnectTimedOut(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreConnectTimedOut")
	public void testServizioApplicativoConnectTimedOut(String servizioApplicativoFruitore) throws Exception{
		testServizioApplicativoConnectTimedOut_engine(servizioApplicativoFruitore);
	}
	public Object testServizioApplicativoConnectTimedOut_engine(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErratoConnectTimedOut");
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
				codice = "PREFIX_PERSONALIZZATO_516";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE, Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectTimedOutTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD_TUNNEL_SOAP"},dataProvider="personalizzazioniErroreConnectTimedOutTunnelSOAP")
	public void testServizioApplicativoConnectTimedOut_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_PDD_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
			String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErratoConnectTimedOut");
			boolean equalsMatch = true;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				codice = "PREFIX_PERSONALIZZATO_516";
			}
			
			try {
				client.run();

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"InoltroBuste", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
				
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public Object[][] personalizzazioniErroreConnectTimedOutServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		testServizioApplicativoConnectTimedOutServizioApplicativo_engine(servizioApplicativoFruitore);
	}
	public Object testServizioApplicativoConnectTimedOutServizioApplicativo_engine(String servizioApplicativoFruitore) throws Exception{

		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		Object response = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
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
			
			String actor = CostantiPdD.OPENSPCOOP2;
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				actor = "ACTOR_RIDEFINITO";
			}
			
			try {
				client.run();

				if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
						"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
					Reporter.log("Invocazione porta delegata inesistente non ha causato errori.");
					throw new TestSuiteException("Invocazione porta delegata inesistente non ha causato errori.");
				}

				Assert.assertTrue(client.getCodiceStatoHTTP()==200);
				
				Message msgRisposta = client.getResponseMessage();
				Assert.assertTrue(msgRisposta!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody()!=null);
				Assert.assertTrue(msgRisposta.getSOAPBody().hasChildNodes());
				
				byte [] xmlErroreApplicativo =org.openspcoop2.message.XMLUtils.getInstance().toByteArray(msgRisposta.getSOAPBody().getFirstChild(),true);
				response = msgRisposta.getSOAPBody();
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);	
				
			} catch (AxisFault error) {
				
				response = error;
				
				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
								
				Utilities.verificaFaultIntegrazione(error, actor,
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);				
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
							
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
		
		return response;
	}
	
	/** SERVIZIO TUNNEL SOAP: ERRORE CONNETTORE SA, Connect Timed Out ARRICCHITO */
	
	@DataProvider (name="personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP")
	public Object[][] personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={ErroreApplicativoCNIPA.ID_GRUPPO,ErroreApplicativoCNIPA.ID_GRUPPO+".CONNECT_TIMED_OUT_SA_TUNNEL_SOAP"},
			dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativoTunnelSOAP")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo_TunnelSOAP(String servizioApplicativoFruitore) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;
		ByteArrayOutputStream bout = null;
		java.io.FileInputStream fin = null;
		try{

			String portaDelegata = CostantiTestSuite.PORTA_DELEGATA_CONNETTORE_ERRATO_SA_SINCRONO_STATELESS_CONNECT_TIMED_OUT;
			
			fin = new java.io.FileInputStream(new File(Utilities.testSuiteProperties.getXmlSenzaSoapFileName()));
			bout = new ByteArrayOutputStream();
			byte[]bytes = new byte[2048];
			int letti = 0;
			while( (letti = fin.read(bytes)) != -1 ){
				bout.write(bytes, 0, letti);
			}
			bout.flush();
			bout.close();
			
			ClientHttpGenerico client=new ClientHttpGenerico(new Repository());
			client.setConnectionReadTimeout(60000);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore().replace("PD", "PDtoSOAP"));
			client.setPortaDelegata(portaDelegata);
			client.connectToSoapEngine();
			if(servizioApplicativoFruitore!=null){
				client.setProperty("X-OpenSPCoop2-ServizioApplicativo", servizioApplicativoFruitore);
			}
			client.setMessaggioXMLRichiesta(bout.toByteArray());
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
			
			String idPorta = "MinisteroFruitoreSPCoopIT";
			String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
			String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
			boolean equalsMatch = false;
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			}
			
			try {
				client.run();

				Assert.assertTrue(client.getCodiceStatoHTTP()==500);
				
				byte [] xmlErroreApplicativo = client.getMessaggioXMLRisposta();
				Assert.assertTrue(xmlErroreApplicativo!=null);
				
				Utilities.verificaErroreApplicativoCnipa(org.openspcoop2.message.XMLUtils.getInstance().newElement(xmlErroreApplicativo), 
						idPorta,"SbustamentoRisposte", 
						codice, 
						msg, equalsMatch);	
								
			} catch (AxisFault error) {
				throw error;			
			}finally{
				dbComponentFruitore.close();
				dbComponentErogatore.close();
							
				Date dataFineTest = DateManager.getDate();
				
				ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
				err.setIntervalloInferiore(dataInizioTest);
				err.setIntervalloSuperiore(dataFineTest);
				err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
				this.erroriAttesiOpenSPCoopCore.add(err);
			}
		}catch(Exception e){
			throw e;
		}finally{
			try{
				fin.close();
			}catch(Exception e){}
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}	
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
