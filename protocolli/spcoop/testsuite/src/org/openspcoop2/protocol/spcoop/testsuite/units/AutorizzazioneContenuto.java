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

import java.io.IOException;
import java.util.Date;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.testsuite.core.CooperazioneSPCoopBase;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.UtilitiesEGov;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.clients.ClientOneWay;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatiServizio;
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
 * Testsuite per autorizzazione contenuto
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class AutorizzazioneContenuto {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutorizzazioneContenuto";
	
	/** Gestore della Collaborazione di Base */
	private CooperazioneBaseInformazioni info = CooperazioneSPCoopBase.getCooperazioneBaseInformazioni(CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,
				false,SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);	
	private CooperazioneBase collaborazioneSPCoopBase = 
			new CooperazioneBase(false, SOAPVersion.SOAP11, this.info, 
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
	 * Test per il profilo di collaborazione sincronoPD_OK
	 */
	Repository repositorysincronoPD_OK=new Repository();
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PD_OK"},description="Test di tipo sincronoPD_OK, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoPD_OK() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorysincronoPD_OK,CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_OK,addIDUnivoco);
	}
	@DataProvider (name="sincronoPD_OK")
	public Object[][]testsincronoPD_OK()throws Exception{
		String id=this.repositorysincronoPD_OK.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PD_OK"},dataProvider="sincronoPD_OK",dependsOnMethods={"sincronoPD_OK"})
	public void testsincronoPD_OK(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
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
	 * Test per il profilo di collaborazione sincronoPD_KO
	 */
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PD_KO"})
	public void testsincronoPD_KO() throws Exception{
		DatabaseComponent dbComponentFruitore = null;
		DatabaseComponent dbComponentErogatore = null;

		try{
			ClientOneWay client=new ClientOneWay(new Repository());
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneContenutiApplicativiFruitore());
			client.setPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO);
			client.connectToSoapEngine();
			client.setMessageFromFile(Utilities.testSuiteProperties.getSoap11FileName(), false);
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

				Reporter.log("Invocazione porta delegata con autorizzazione contenuto sincrono KO (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO+") non ha causato errori.");
				throw new TestSuiteException("Invocazione porta delegata con autorizzazione contenuto sincrono KO (PortaDelegata: "+CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SINCRONO_KO+") non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo actor ["+CostantiPdD.OPENSPCOOP2+"]");
				Assert.assertTrue(CostantiPdD.OPENSPCOOP2.equals(error.getFaultActor()));
				Reporter.log("Controllo fault code ["+Utilities.toString(CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA)+"]");
				Assert.assertTrue(Utilities.toString(CodiceErroreIntegrazione.CODICE_428_AUTORIZZAZIONE_CONTENUTO_FALLITA).equals(error.getFaultCode().getLocalPart()));
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
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione sincronoPA_OK
	 */
	Repository repositorysincronoPA_OK=new Repository();
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PA_OK"},description="Test di tipo sincronoPA_OK, Viene controllato se i body sono uguali e se gli attachment sono uguali")
	public void sincronoPA_OK() throws TestSuiteException, IOException, SOAPException{
		this.collaborazioneSPCoopBase.sincrono(this.repositorysincronoPA_OK,CostantiTestSuite.PORTA_DELEGATA_AUTORIZZAZIONE_CONTENUTO_SPCOOP_SINCRONO_OK,addIDUnivoco);
	}
	@DataProvider (name="sincronoPA_OK")
	public Object[][]testsincronoPA_OK()throws Exception{
		String id=this.repositorysincronoPA_OK.getNext();
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentFruitore(),id,false},	
				{DatabaseProperties.getDatabaseComponentErogatore(),id,true}	
		};
	}
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PA_OK"},dataProvider="sincronoPA_OK",dependsOnMethods={"sincronoPA_OK"})
	public void testsincronoPA_OK(DatabaseComponent data,String id,boolean checkServizioApplicativo) throws Exception{
		try{
			this.collaborazioneSPCoopBase.testSincrono(data, id, CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
					CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_AUTORIZZAZIONE_CONTENUTO_OK, checkServizioApplicativo,
					null);
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/***
	 * Test per il profilo di collaborazione sincronoPA_KO
	 */
	Repository repositorysincronoPA_KO=new Repository();
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PA_KO"})
	public void sincronoPA_KO()throws TestSuiteException, SOAPException, Exception{

		String egov=UtilitiesEGov.getIDEGov(CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE+CostantiTestSuite.SPCOOP_PORTA_DOMINIO);
		this.repositorysincronoPA_KO.add(egov);
		String bustaSOAP=UtilitiesEGov.getBustaEGov(CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_FRUITORE,
				CostantiTestSuite.SPCOOP_TIPO_SOGGETTO_EROGATORE,
				CostantiTestSuite.SPCOOP_NOME_SOGGETTO_EROGATORE,
				SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO,
				CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO,
				CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO,
				egov,
				false,
				SPCoopCostanti.PROFILO_TRASMISSIONE_SENZA_DUPLICATI,
				false,CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_AUTORIZZAZIONE_CONTENUTO_KO);

		// costruzione busta
		java.io.ByteArrayInputStream bin = 
			new java.io.ByteArrayInputStream(bustaSOAP.getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		// Log
		//java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
		//msg.writeTo(out);
		//Reporter.log("Busta utilizzata per il test: "+out.toString());

		// prima istanza
		runClient(msg);
	}
	private void runClient(Message msg)throws SOAPException, Exception{
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorysincronoPA_KO);
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
			try {
				client.run();
				throw new TestSuiteException("Invocazione PA con autorizzazione contenuto SPCoop KO, non ha causato errori.");
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
		}
	}
	@DataProvider (name="sincronoPA_KO")
	public Object[][]testSincronoPA_KO()throws Exception{
		String id=this.repositorysincronoPA_KO.getNext();
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
	@Test(groups={AutorizzazioneContenuto.ID_GRUPPO,AutorizzazioneContenuto.ID_GRUPPO+".SINCRONO_PA_KO"},
			dataProvider="sincronoPA_KO",dependsOnMethods="sincronoPA_KO")
	public void testSincronoPA_KO(DatabaseComponent data,String id) throws Exception{
		try{
			
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,null));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, new DatiServizio(CostantiTestSuite.SPCOOP_TIPO_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_NOME_SERVIZIO_SINCRONO, CostantiTestSuite.SPCOOP_VERSIONE_SERVIZIO_DEFAULT)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, CostantiTestSuite.SPCOOP_SERVIZIO_SINCRONO_AZIONE_AUTORIZZAZIONE_CONTENUTO_KO));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, SPCoopCostanti.PROFILO_COLLABORAZIONE_SINCRONO, ProfiloDiCollaborazione.SINCRONO));
			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==0);
			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, 
					CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE,null, 
					CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE,null));
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
}
