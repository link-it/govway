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

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.openspcoop2.testsuite.axis14.Axis14SoapUtils;
import org.openspcoop2.testsuite.axis14.Axis14WSSBaseUtils;
import org.openspcoop2.testsuite.clients.ClientCore;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.clients.ClientSincrono;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.asincrono.RepositoryConsegnaRisposteAsincroneSimmetriche;
import org.openspcoop2.testsuite.core.asincrono.RepositoryCorrelazioneIstanzeAsincrone;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.testsuite.server.ServerRicezioneRispostaAsincronaSimmetrica;
import org.openspcoop2.testsuite.units.CooperazioneBase;
import org.openspcoop2.testsuite.units.CooperazioneBaseInformazioni;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle funzionalita' di WS-Security
 *  
 * @author Andi Rexha (rexha@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class WSSecurity {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "WSSecurity";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
			CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
			false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
		new CooperazioneBase(false,SOAPVersion.SOAP11,  this.info, 
				org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance(), 
				DatabaseProperties.getInstance(), SPCoopTestsuiteLogger.getInstance());
	

	private static boolean addIDUnivoco = false;
	

	
	
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
	 * Test per un profilo oneway con WSSecurity
	 */
	Repository repositoryOneWayWSS=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ONEWAY"})
	public void oneWayWSS() throws TestSuiteException, Exception{
		this.collaborazioneSPCoopBase.oneWay(this.repositoryOneWayWSS,CostantiTestSuite.PORTA_DELEGATA_WSS_HELLO_WORLD,addIDUnivoco);
	}
	@DataProvider (name="OneWayWSS")
	public Object[][]testOneWayWSS()throws Exception{
		String id=this.repositoryOneWayWSS.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ONEWAY"},dataProvider="OneWayWSS",dependsOnMethods="oneWayWSS")
	public void testDBWSS(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testOneWay(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY,
				CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	
	
	/**
	 * Test per un profilo oneway con WSSecurity, con autorizzazione non permessa.
	 */
	Repository repositoryOneWayWSSNonAutorizzato=new Repository();
	private boolean faultCorrettoRicevuto = false;
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".AutorizzazioneKO"})
	public void oneWayWSSNonAutorizzato() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			// Creazione client OneWay
			ClientOneWay client=new ClientOneWay(this.repositoryOneWayWSSNonAutorizzato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_HELLO_WORLD_AUTORIZZAZIONE_OK);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
	
			client.run();
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA)+"]");
			Assert.assertTrue(Utilities.toString(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA).equals(error.getFaultCode().getLocalPart()));
			this.faultCorrettoRicevuto = true;
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
	}
	@DataProvider (name="OneWayWSSNonAutorizzato")
	public Object[][]testOneWayWSSNonAutorizzato()throws Exception{
		String id=this.repositoryOneWayWSSNonAutorizzato.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".AutorizzazioneKO"},dataProvider="OneWayWSSNonAutorizzato",dependsOnMethods="oneWayWSSNonAutorizzato")
	public void testDBWSSNonAutorizzato(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		
			if(id==null && this.faultCorrettoRicevuto){
				// Test con stato stateless. L'errore viene ritornato al client
				return;
			}
			
			Reporter.log("[WSSecurity] Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("[WSSecurity] Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id, this.collaborazioneSPCoopBase.getMittente(), null));
			Reporter.log("[WSSecurity] Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id, this.collaborazioneSPCoopBase.getDestinatario(), null));
			Reporter.log("[WSSecurity] Controllo valore Servizio Busta con id: " +id);
			DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id,
					datiServizio));
			Reporter.log("[WSSecurity] Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS_AUTORIZZAZIONE_KO));
			
			Reporter.log("[WSSecurity] Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
			Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, null));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.collaborazioneSPCoopBase.isConfermaRicezione(),this.collaborazioneSPCoopBase.getInoltro(), this.collaborazioneSPCoopBase.getInoltroSdk()));
			Reporter.log("[WSSecurity] Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.collaborazioneSPCoopBase.getMittente(), 
					null,
					this.collaborazioneSPCoopBase.getDestinatario(), 
					null));
			if(checkServizioApplicativo){
				Reporter.log("[WSSecurity] Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			}
			
			
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
			Reporter.log("Controllo che la busta abbia generato l'eccezione " + Utilities.toString(CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA)+" 2 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==2);
	
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA));
	
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA+": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
			
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	



	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity Timestampt
	 */
	Repository repositorySincronoWSSTimestamp=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_TIMESTAMP"},description="Test per il profilo di collaborazione Sincrono con WSSecurity Timestampt")
	public void sincronoWSSTimestamp() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSSTimestamp,CostantiTestSuite.PORTA_DELEGATA_WSS_TIMESTAMP,addIDUnivoco);
	}
	@DataProvider (name="SincronoWSSTimestamp")
	public Object[][]testSincronoWSSTimestamp()throws Exception{
		String id=this.repositorySincronoWSSTimestamp.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_TIMESTAMP"},dataProvider="SincronoWSSTimestamp",dependsOnMethods={"sincronoWSSTimestamp"})
	public void testSincronoWSSTimestamp(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_TIMESTAMP, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity Encrypt
	 */
	Repository repositorySincronoWSSEncrypt=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ENCRYPT"},description="Test per il profilo di collaborazione Sincrono con WSSecurity Encryptt")
	public void sincronoWSSEncrypt() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSSEncrypt,CostantiTestSuite.PORTA_DELEGATA_WSS_ENCRYPT,addIDUnivoco);
	}
	@DataProvider (name="SincronoWSSEncrypt")
	public Object[][]testSincronoWSSEncrypt()throws Exception{
		String id=this.repositorySincronoWSSEncrypt.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ENCRYPT"},dataProvider="SincronoWSSEncrypt",dependsOnMethods={"sincronoWSSEncrypt"})
	public void testSincronoWSSEncrypt(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity Encrypt
	 */
	Repository repositorySincronoWSSEncryptP12=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ENCRYPT_P12"},description="Test per il profilo di collaborazione Sincrono con WSSecurity Encrypt e keystore P12")
	public void sincronoWSSEncryptP12() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSSEncryptP12,CostantiTestSuite.PORTA_DELEGATA_WSS_ENCRYPT_P12,addIDUnivoco);
	}
	@DataProvider (name="SincronoWSSEncrypt_P12")
	public Object[][]testSincronoWSSEncryptP12()throws Exception{
		String id=this.repositorySincronoWSSEncryptP12.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ENCRYPT_P12"},dataProvider="SincronoWSSEncrypt_P12",dependsOnMethods={"sincronoWSSEncryptP12"})
	public void testSincronoWSSEncryptP12(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ENCRYPT_P12, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity Signature
	 */
	Repository repositorySincronoWSSSignature=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_SIGNATURE"},description="Test per il profilo di collaborazione Sincrono con WSSecurity Signature")
	public void sincronoWSSSignature() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSSSignature,CostantiTestSuite.PORTA_DELEGATA_WSS_SIGNATURE,addIDUnivoco);
	}
	@DataProvider (name="SincronoWSSSignature")
	public Object[][]testSincronoWSSSignature()throws Exception{
		String id=this.repositorySincronoWSSSignature.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_SIGNATURE"},dataProvider="SincronoWSSSignature",dependsOnMethods={"sincronoWSSSignature"})
	public void testSincronoWSSSignature(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity Signature
	 */
	Repository repositorySincronoWSSSignature_P12=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_SIGNATURE_P12"},description="Test per il profilo di collaborazione Sincrono con WSSecurity Signature P12")
	public void sincronoWSSSignatureP12() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSSSignature_P12,CostantiTestSuite.PORTA_DELEGATA_WSS_SIGNATURE_P12,addIDUnivoco);
	}
	@DataProvider (name="SincronoWSSSignature_P12")
	public Object[][]testSincronoWSSSignatureP12()throws Exception{
		String id=this.repositorySincronoWSSSignature_P12.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_SIGNATURE_P12"},dataProvider="SincronoWSSSignature_P12",dependsOnMethods={"sincronoWSSSignatureP12"})
	public void testSincronoWSSSignatureP12(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_SIGNATURE_P12, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	
	
	
	
	
	
	
	
	/**
	 * Test per un profilo sincrono con WSSecurityEncrypt che riceve un messaggio alterato
	 */
	Repository repositoryWSSEncryptMessaggioAlterato=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".WSSEncryptMessaggioAlterato"})
	public void wssEncryptMessaggioAlterato() throws TestSuiteException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		ClientSincrono client=null;
		try{
			// Creazione client Sincrono
			client=new ClientSincrono(this.repositoryWSSEncryptMessaggioAlterato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_ENCRYPT_MESSAGGIO_ALTERATO);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
	
			client.run();
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreCooperazione.SICUREZZA)+"]");
			Assert.assertTrue(Utilities.toString(CodiceErroreCooperazione.SICUREZZA).equals(error.getFaultCode().getLocalPart()));			
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Generatosi errore durante il processamento Message-Security(Receiver): Element with 'Id' attribute value"); // Element with 'Id' attribute value (#ED-21) not found [EncryptSearch]?
		this.erroriAttesiOpenSPCoopCore.add(err);
		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Test per un profilo sincrono con WSSecuritySignature che riceve un messaggio alterato
	 */
	Repository repositoryWSSSignatureMessaggioAlterato=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".WSSSignatureMessaggioAlterato"})
	public void wssSignatureMessaggioAlterato() throws TestSuiteException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		ClientSincrono client=null;
		try{
			// Creazione client Sincrono
			client=new ClientSincrono(this.repositoryWSSSignatureMessaggioAlterato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_SIGNATURE_MESSAGGIO_ALTERATO);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false,addIDUnivoco);

			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
			}
	
			client.run();
			
		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA)+"]");
			Assert.assertTrue(Utilities.toString(CodiceErroreCooperazione.SICUREZZA_FIRMA_NON_VALIDA).equals(error.getFaultCode().getLocalPart()));			
		}finally{
			dbComponentFruitore.close();
			dbComponentErogatore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Generatosi errore durante il processamento Message-Security(Receiver) [code: {http://schemas.xmlsoap.org/soap/envelope/}Server.generalException]");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Generatosi errore durante il processamento Message-Security(Receiver): The signature or decryption was invalid");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("A security error was encountered when verifying the message");
		this.erroriAttesiOpenSPCoopCore.add(err3);
		
		
	}
	
	
	
	
	
	
	
	
	
	




	/***
	 * Test per il profilo di collaborazione Sincrono con WSSecurity  Encrypt/Signature/Timestamp
	 */
	Repository repositorySincronoWSS=new Repository();
	boolean repositorySincronoWSS_CHECK = true;
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ALL"},description="Test per il profilo di collaborazione Sincrono con WSSecurity t")
	public void sincronoWSS() throws TestSuiteException, IOException, SOAPException{
		
		Date dataInizioTest = DateManager.getDate();
		
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			try{
				this.collaborazioneSPCoopBase.sincrono(this.repositorySincronoWSS,CostantiTestSuite.PORTA_DELEGATA_WSS_ALL_FUNCTION,addIDUnivoco);
			}catch(Exception e){
				this.repositorySincronoWSS_CHECK = false;
				System.out.println("WARNING: WSS_ALL 'No certificates for user useReqSigCert were found for encryption' unsupported by CXF");
			}
		}
		
		Date dataFineTest = DateManager.getDate();
		
		if(this.repositorySincronoWSS_CHECK==false){
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore("Generatosi errore durante il processamento Message-Security(Sender): Security processing failed");
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@DataProvider (name="SincronoWSS")
	public Object[][]testSincronoWSS()throws Exception{
		String id=this.repositorySincronoWSS.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".SINCRONO_ALL"},dataProvider="SincronoWSS",dependsOnMethods={"sincronoWSS"})
	public void testSincronoWSS(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		if(this.repositorySincronoWSS_CHECK && Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, 
					CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS, checkServizioApplicativo,null);
			}catch(Exception e){
				throw e;
			}finally{
				data.close();
			}
		}
	}







	/**
	 * Test per la Consegna affidabile con WSSecurity
	 */
	Repository repositoryConsengaAffidabileWSS=new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".CONSEGNA_AFFIDABILE"})
	public void ConsegnaAffidabileWSS() throws TestSuiteException, Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{


			ClientOneWay client=new ClientOneWay(this.repositoryConsengaAffidabileWSS);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_AFFIDABILE_CON_FILTRO_DUPLICATI);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
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
	@DataProvider (name="ConsegnaAffidabileWSS")
	public Object[][]testConsegnaWSS()throws Exception{
		String id=this.repositoryConsengaAffidabileWSS.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try{
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			}catch(InterruptedException e){}
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".CONSEGNA_AFFIDABILE"},dataProvider="ConsegnaAffidabileWSS",dependsOnMethods="ConsegnaAffidabileWSS")
	public void testDBConsegnaAffidabileWSS(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		Reporter.log("Controllo tracciamento richiesta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
		Reporter.log("Controllo valore Mittente Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null));
		Reporter.log("Controllo valore Destinatario Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		Reporter.log("Controllo valore Servizio Busta con id: " +id);
		DatiServizio datiServizio = new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ONEWAY, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
		Reporter.log("Controllo valore Azione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_ONEWAY_AZIONE_WSS_AFFIDABILE_CON_FILTRO_DUPLICATI));
		Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_ONEWAY, ProfiloDiCollaborazione.ONEWAY));
		Reporter.log("Controllo Riscontro con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro(id,null));
		Reporter.log("Controllo che la busta non abbia generato eccezioni, id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id)==false);
		Reporter.log("Controllo lista trasmissione con id: " +id);
		Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE, null, CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE, null));
		if(checkServizioApplicativo){
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
		}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/***
	 * Test per il profilo di collaborazione Asincrono Simmetrico, modalita sincrona con WSSecurity
	 */
	RepositoryConsegnaRisposteAsincroneSimmetriche repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincronaWSS = new RepositoryConsegnaRisposteAsincroneSimmetriche(Utilities.testSuiteProperties.timeToSleep_repositoryAsincronoSimmetrico());
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincronaWSS = new RepositoryCorrelazioneIstanzeAsincrone();
	ServerRicezioneRispostaAsincronaSimmetrica serverRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS = 
		new ServerRicezioneRispostaAsincronaSimmetrica(Utilities.testSuiteProperties.getWorkerNumber(),Utilities.testSuiteProperties.getSocketAsincronoSimmetrico_modalitaSincrona_WSSecurity(),
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincronaWSS);
	@Test (groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_SIMMETRICO"})
	public void startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS.start();
	}
	@Test (groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_SIMMETRICO"},description="Test di tipo asincrono simmetrico con modalita asincrona e WSSecurity",dependsOnMethods="startServerRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS")
	public void asincronoSimmetrico_ModalitaSincronaWSS() throws Exception{		
		this.collaborazioneSPCoopBase.asincronoSimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_WSS_ASINCRONA_SIMMETRICA,
				CostantiTestSuite.PORTA_DELEGATA_WSS_ASINCRONA_SIMMETRICA_CORRELATA,
				"profiloAsincrono_richiestaSincronaWss","123456",
				this.repositoryConsegnaRisposteAsincroneSimmetriche_modalitaSincronaWSS, 
				this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincronaWSS,addIDUnivoco);
	}
	@DataProvider (name="AsincronoSimmetrico_ModalitaSincronaWSS")
	public Object[][]testAsincronoSimmetrico_ModalitaSincronaWSS() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincronaWSS.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_SIMMETRICO"},dataProvider="AsincronoSimmetrico_ModalitaSincronaWSS",dependsOnMethods={"asincronoSimmetrico_ModalitaSincronaWSS"})
	public void testAsincronoSimmetrico_ModalitaSincronaWSS(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testAsincronoSimmetrico_ModalitaSincrona(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA, checkServizioApplicativo,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoSimmetrico_ModalitaSincronaWSS")
	public Object[][]testRispostaAsincronoSimmetrico_ModalitaSincronaWSS() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincronaWSS.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneSimmetriche_modalitaSincronaWSS.getIDRichiestaByReference(id);
		try {
			Thread.sleep(2000); // aspetto tempo per tracciamento ricevuta asincrona simmetrica risposta (caso particolare per ASmodAsincrona)
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_SIMMETRICO"},dataProvider="RispostaAsincronoSimmetrico_ModalitaSincronaWSS",dependsOnMethods={"testAsincronoSimmetrico_ModalitaSincronaWSS"})
	public void testRispostaAsincronoSimmetrico_ModalitaSincronaWSS(DatabaseComponent data,String id, String idCorrelazioneAsincrona, boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testRispostaAsincronoSimmetrico_ModalitaSincrona(data, id, idCorrelazioneAsincrona,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_SIMMETRICO,
				CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_SIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@Test (groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_SIMMETRICO"},dependsOnMethods={"testRispostaAsincronoSimmetrico_ModalitaSincronaWSS"})
	public void stopServerRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS(){
		this.serverRicezioneRispostaAsincronaSimmetrica_modalitaSincronaWSS.closeSocket();
	}







	/***
	 * Test per il profilo di collaborazione Asincrono Asimmetrico, modalita sincrona e WSSecurity
	 */
	RepositoryCorrelazioneIstanzeAsincrone repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincronaWSS = new RepositoryCorrelazioneIstanzeAsincrone();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_ASIMMETRICO"})
	public void asincronoAsimmetrico_modalitaSincronaWSS() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.asincronoAsimmetrico_modalitaSincrona(
				CostantiTestSuite.PORTA_DELEGATA_WSS_ASINCRONA_ASIMMETRICA,
				CostantiTestSuite.PORTA_DELEGATA_WSS_ASINCRONA_ASIMMETRICA_CORRELATA,
				this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincronaWSS,addIDUnivoco);
	}
	@DataProvider (name="AsincronoAsimmetrico_modalitaSincronaWSS")
	public Object[][]testAsincronoAsimmetrico_modalitaSincronaWSS()throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincronaWSS.getNextIDRichiesta();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_ASIMMETRICO"},dataProvider="AsincronoAsimmetrico_modalitaSincronaWSS",dependsOnMethods={"asincronoAsimmetrico_modalitaSincronaWSS"})
	public void testAsincronoAsimmetrico_modalitaSincronaWSS(DatabaseComponent data,String id, boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testAsincronoAsimmetrico_modalitaSincrona(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA,checkServizioApplicativo,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	@DataProvider (name="RispostaAsincronoAsimmetrico_modalitaSincronaWSS")
	public Object[][]testRispostaAsincronoAsimmetrico_modalitaSincronaWSS() throws Exception{
		String id=this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincronaWSS.getNextIDRisposta();
		String idCorrelazioneAsincrona = this.repositoryCorrelazioneIstanzeAsincroneAsimmetriche_modalitaSincronaWSS.getIDRichiestaByReference(id);
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,idCorrelazioneAsincrona,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,idCorrelazioneAsincrona,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".ASINCRONO_ASIMMETRICO"},dataProvider="RispostaAsincronoAsimmetrico_modalitaSincronaWSS",dependsOnMethods={"testAsincronoAsimmetrico_modalitaSincronaWSS"})
	public void testRispostaAsincronoAsimmetrico_modalitaSincronaWSS(DatabaseComponent data,String id,String idCorrelazioneAsincrona,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testRispostaAsincronoAsimmetrico_modalitaSincrona(data, id,idCorrelazioneAsincrona, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_CORRELATO_ASINCRONO_ASIMMETRICO,
				CostantiTestSuite.SPCOOP_SERVIZIO_ASINCRONO_ASIMMETRICO_AZIONE_WSS_MODALITA_SINCRONA,checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	/* ********************** Test per Verifica Bug 18 ************************ */
	
	
	
	
	/***
	 * Test1, Client MustUnderstand1, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	Repository repositoryWSSBUG18_Test1 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test1"})
	public void testBUG18_Test1() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test1);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test1")
	public Object[][]testSincronoWSSBUG18_Test1()throws Exception{
		String id=this.repositoryWSSBUG18_Test1.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test1"},dataProvider="SincronoWSSBUG18_Test1",dependsOnMethods={"testBUG18_Test1"})
	public void testSincronoWSSBUG18_Test1(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test2, Client MustUnderstand1, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test2 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test2"})
	public void testBUG18_Test2() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test2);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test2")
	public Object[][]testSincronoWSSBUG18_Test2()throws Exception{
		String id=this.repositoryWSSBUG18_Test2.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test2"},dataProvider="SincronoWSSBUG18_Test2",dependsOnMethods={"testBUG18_Test2"})
	public void testSincronoWSSBUG18_Test2(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test3, Client MustUnderstand0 e Actor Client, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	Repository repositoryWSSBUG18_Test3 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test3"})
	public void testBUG18_Test3() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test3);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "false");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "false");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test3")
	public Object[][]testSincronoWSSBUG18_Test3()throws Exception{
		String id=this.repositoryWSSBUG18_Test3.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test3"},dataProvider="SincronoWSSBUG18_Test3",dependsOnMethods={"testBUG18_Test3"})
	public void testSincronoWSSBUG18_Test3(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test4, Client MustUnderstand0 e Actor Client, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test4 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test4"})
	public void testBUG18_Test4() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test4);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "false");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "false");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test4")
	public Object[][]testSincronoWSSBUG18_Test4()throws Exception{
		String id=this.repositoryWSSBUG18_Test4.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test4"},dataProvider="SincronoWSSBUG18_Test4",dependsOnMethods={"testBUG18_Test4"})
	public void testSincronoWSSBUG18_Test4(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test5, Client MustUnderstand1 e Actor Client, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	Repository repositoryWSSBUG18_Test5 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test5"})
	public void testBUG18_Test5() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test5);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test5")
	public Object[][]testSincronoWSSBUG18_Test5()throws Exception{
		String id=this.repositoryWSSBUG18_Test5.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test5"},dataProvider="SincronoWSSBUG18_Test5",dependsOnMethods={"testBUG18_Test5"})
	public void testSincronoWSSBUG18_Test5(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test6, Client MustUnderstand1 e Actor Client, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test6 = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test6"})
	public void testBUG18_Test6() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test6);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_ENCRYPT+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Encrypt);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test6")
	public Object[][]testSincronoWSSBUG18_Test6()throws Exception{
		String id=this.repositoryWSSBUG18_Test6.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test6"},dataProvider="SincronoWSSBUG18_Test6",dependsOnMethods={"testBUG18_Test6"})
	public void testSincronoWSSBUG18_Test6(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Encrypt, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********************** Test per Verifica Bug 18, invertiti: Client utilizza encrypt, PdD signature ************************ */
	
	/***
	 * Test1, Client MustUnderstand1, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	
	Repository repositoryWSSBUG18_Test1_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test1_FunzioniWSSInvertite"})
	public void testBUG18_Test1_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test1_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test1_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test1_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test1_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test1_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test1_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test1_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test1_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActorMU_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test2, Client MustUnderstand1, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test2_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test2_FunzioniWSSInvertite"})
	public void testBUG18_Test2_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test2_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test2_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test2_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test2_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test2_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test2_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test2_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test2_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMU_PdDActor_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test3, Client MustUnderstand0 e Actor Client, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	Repository repositoryWSSBUG18_Test3_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test3_FunzioniWSSInvertite"})
	public void testBUG18_Test3_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test3_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "false");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "false");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test3_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test3_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test3_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test3_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test3_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test3_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test3_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDMU_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test4, Client MustUnderstand0 e Actor Client, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test4_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test4_FunzioniWSSInvertite"})
	public void testBUG18_Test4_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test4_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "false");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "false");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test4_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test4_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test4_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test4_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test4_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test4_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test4_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientActor_PdDActor_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test5, Client MustUnderstand1 e Actor Client, PdD MustUnderstand1
	 * NOTA: La porta di dominio aggiungerà un header con default actor 'openspcoop'
	 */
	Repository repositoryWSSBUG18_Test5_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test5_FunzioniWSSInvertite"})
	public void testBUG18_Test5_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test5_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_MU_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test5_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test5_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test5_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test5_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test5_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test5_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test5_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDMU_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test6, Client MustUnderstand1 e Actor Client, PdD ActorOpenSPCoop
	 */
	Repository repositoryWSSBUG18_Test6_FunzioniWSSInvertite = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test6_FunzioniWSSInvertite"})
	public void testBUG18_Test6_FunzioniWSSInvertite() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryWSSBUG18_Test6_FunzioniWSSInvertite);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Actor_SIGNATURE+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Signature);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();

			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Encrypt");
			wssPropertiesResponse.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSBUG18_Test6_FunzioniWSSInvertite")
	public Object[][]testSincronoWSSBUG18_Test6_FunzioniWSSInvertite()throws Exception{
		String id=this.repositoryWSSBUG18_Test6_FunzioniWSSInvertite.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".BUG18_Test6_FunzioniWSSInvertite"},dataProvider="SincronoWSSBUG18_Test6_FunzioniWSSInvertite",dependsOnMethods={"testBUG18_Test6_FunzioniWSSInvertite"})
	public void testSincronoWSSBUG18_Test6_FunzioniWSSInvertite(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_BUG18_ClientMUActor_PdDActor_Signature, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/* ********************** Test per XML Annidato ************************ */
	
	/***
	 * Test per funzionalita' con xml annidato
	 */
	Repository repositoryAnnidamento = new Repository();
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".Annidamento"})
	public void annidamento() throws TestSuiteException, Exception, SOAPException{
		
		DatabaseComponent dbComponentErogatore = null;
		DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryAnnidamento);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_WSS_Annidamento+"/"+CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ANNIDAMENTO);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityAnnidato(), false);
			
			// Applicazione WSSecurity
			java.util.Hashtable<String,String> wssPropertiesRequest= new java.util.Hashtable<String,String>();
			wssPropertiesRequest.put("action", "Timestamp Signature Encrypt");
			wssPropertiesRequest.put("timeToLive", "600");
			wssPropertiesRequest.put("user", "testsuiteclient");
			wssPropertiesRequest.put("encryptionUser", "testsuiteserver");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesRequest.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesRequest.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("encryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesRequest.put("signatureParts", "{Content}{http://www.openspcoop.org}client_sign;{Content}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp;");
			wssPropertiesRequest.put("encryptionParts", "{Content}{http://www.openspcoop.org}client_encrypt;");
			wssPropertiesRequest.put("mustUnderstand", "true");
			wssPropertiesRequest.put("actor", "client");
			Axis14WSSBaseUtils baseWSS = new Axis14WSSBaseUtils(Axis14SoapUtils.getAxisClient(),false,"TESTSUITE",client.getLog(),"ClientTest-");
			baseWSS.setUseActorDefaultIfNotDefined(false);
			client.processWSSRequest(wssPropertiesRequest, baseWSS);
			
			// Invocazione
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();
				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			client.run();
			
			// Ricezione Rispota WSSecurity
			java.util.Hashtable<String,String> wssPropertiesResponse= new java.util.Hashtable<String,String>();
			wssPropertiesResponse.put("action", "Timestamp Signature Encrypt");
			wssPropertiesRequest.put("passwordCallbackClass", "org.openspcoop2.testsuite.wssecurity.PWCallbackSend");
			wssPropertiesResponse.put("signatureKeyIdentifier", "DirectReference");
			wssPropertiesResponse.put("signaturePropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("decryptionPropFile", "testsuiteClient-crypto.properties");
			wssPropertiesResponse.put("mustUnderstand", "true");
			wssPropertiesResponse.put("actor", "client");
			client.processWSSResponse(wssPropertiesResponse, baseWSS);
			
			// Test uguaglianza Body (e attachments)
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoapTestWSSecurityAnnidato(), false); // reinizializzo messaggio prima di averlo cambiato con WSS
			
			Assert.assertTrue(ClientCore.isEqualsSentAndResponseMessage(client.getSentMessage(), client.getResponseMessage()));
			
		}catch(Exception e){
			throw e;
		}finally{
			try{
				if(dbComponentErogatore!=null){
					dbComponentErogatore.close();
				}
			}catch(Exception e){}
			try{
				if(dbComponentFruitore!=null){
					dbComponentFruitore.close();
				}
			}catch(Exception e){}
		}
	}
	@DataProvider (name="SincronoWSSAnnidamento")
	public Object[][]testSincronoWSSAnnidamento()throws Exception{
		String id=this.repositoryAnnidamento.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={WSSecurity.ID_GRUPPO,WSSecurity.ID_GRUPPO+".Annidamento"},dataProvider="SincronoWSSAnnidamento",dependsOnMethods={"annidamento"})
	public void testSincronoWSSAnnidamento(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
		this.collaborazioneSPCoopBase.testSincrono(data, id, 
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_WSS_ANNIDAMENTO, checkServizioApplicativo,null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
}
