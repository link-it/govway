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
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostanti;
import org.openspcoop2.protocol.spcoop.constants.SPCoopCostantiPosizioneEccezione;
import org.openspcoop2.protocol.spcoop.testsuite.core.BusteEGovDaFile;
import org.openspcoop2.protocol.spcoop.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.clients.ClientHttpGenerico;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.db.DatabaseComponent;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle buste E-Gov con campi duplicati
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGovCampiDuplicati {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "BusteEGovCampiDuplicati";

	
	
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
	
	
	
	/** Lettura della batteria di test sulle buste eGov errate */
	private BusteEGovDaFile busteEGovErrate = null;

	private boolean segnalazioneElementoPresentePiuVolte = false;


	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2H",ID_GRUPPO+".2IM",ID_GRUPPO+".2M",ID_GRUPPO+".2D",ID_GRUPPO+".2PC",
			ID_GRUPPO+".2C",ID_GRUPPO+".2S",ID_GRUPPO+".2A",ID_GRUPPO+".2Me",ID_GRUPPO+".2I",ID_GRUPPO+".2RM",
			ID_GRUPPO+".2OM",ID_GRUPPO+".2Sc",ID_GRUPPO+".2PT",ID_GRUPPO+".2LT",ID_GRUPPO+".2Se",ID_GRUPPO+".2OT",
			ID_GRUPPO+".2DT",ID_GRUPPO+".2OR",ID_GRUPPO+".2IPOT",ID_GRUPPO+".2IPDT"})
	public void  init() throws Exception{
		try{
			File[] dir =  new java.io.File(Utilities.testSuiteProperties.getPathBusteConCampiDuplicati()).listFiles();
			Vector<File> dirV = new Vector<File>();
			for(int i=0; i<dir.length; i++){
				if(".svn".equals(dir[i].getName())==false)
					dirV.add(dir[i]);
			}
			dir = new File[1];
			dir = dirV.toArray(dir);

			this.busteEGovErrate = new BusteEGovDaFile(dir);
			
		}catch(Exception e){
			Reporter.log("Inizializzazione utility BusteEGovDaFile non riscita: "+e.getMessage());
			throw e;
		}
	}





	/**
	 * Msg SOAP con piu' di un header eGov
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: ?/?
	 * - lista eccezioni con [EGOV_IT_001] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Piu' di un header e-Gov presente
	 * busta = buste_campi_duplicati/busta_2headerEGov.xml
	 */
	Repository repository2headerEGov=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2H"},
			dependsOnMethods="init")
			public void EGov2headerEGov()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2headerEGov.xml");
		this.repository2headerEGov.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2headerEGov);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2headerEGov.");
					throw new TestSuiteException("Invocazione PA con busta con 2headerEGov, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGov2headerEGov")
	public Object[][] testEGov2headerEGov()throws Exception{
		String id=this.repository2headerEGov.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2H"},
			dataProvider="EGov2headerEGov",
			dependsOnMethods="EGov2headerEGov")
			public void testDBEGov2headerEGov(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2headerEGov.xml");
		try{

			Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_NON_CORRETTO+"], " +
					"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione[Piu' di un header e-Gov presente]");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
					new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
					new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
					CodiceErroreCooperazione.FORMATO_NON_CORRETTO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
			"Più di un header e-Gov presente"));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	/**
	 * 
	 * Header eGov con elemento 'IntestazioneMessaggio' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_002] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: IntestazioneMessaggio elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2IntestazioneMessaggio.xml
	 * 
	 */
	Repository repository2IntestazioneMessaggio=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IM"},
			dependsOnMethods="init")
			public void EGov2IntestazioneMessaggio()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IntestazioneMessaggio.xml");
		this.repository2IntestazioneMessaggio.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2IntestazioneMessaggio);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2IntestazioneMessaggio.");
					throw new TestSuiteException("Invocazione PA con busta con 2IntestazioneMessaggio, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento IntestazioneMessaggio");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2IntestazioneMessaggio")
	public Object[][] testEGov2IntestazioneMessaggio()throws Exception{
		String id=this.repository2IntestazioneMessaggio.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IM"},
			dataProvider="EGov2IntestazioneMessaggio",
			dependsOnMethods="EGov2IntestazioneMessaggio")
			public void testDBEGov2IntestazioneMessaggio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IntestazioneMessaggio.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE , SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header eGov con elemento 'Mittente' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Mittenti.xml
	 * 
	 * Test equivalente al Test N.11 Della Certificazione DigitPA (Busta Errata 109)
	 */
	Repository repository2Mittenti=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2M"},
			dependsOnMethods="init")
			public void EGov2Mittenti()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Mittenti.xml");
		this.repository2Mittenti.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Mittenti);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Mittenti.");
					throw new TestSuiteException("Invocazione PA con busta con 2Mittenti, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Mittente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Mittenti")
	public Object[][] testEGov2Mittenti()throws Exception{
		String id=this.repository2Mittenti.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2M"},
			dataProvider="EGov2Mittenti",
			dependsOnMethods="EGov2Mittenti")
			public void testDBEGov2Mittenti(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Mittenti.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				Assert.assertTrue(num==1);
				
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/**
	 * 
	 * Header eGov con elemento 'Destinatario' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Destinatari.xml
	 * 
	 */
	Repository repository2Destinatari=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2D"},
			dependsOnMethods="init")
			public void EGov2Destinatari()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Destinatari.xml");
		this.repository2Destinatari.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Destinatari);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Destinatari.");
					throw new TestSuiteException("Invocazione PA con busta con 2Destinatari, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Destinatario");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Destinatari")
	public Object[][] testEGov2Destinatari()throws Exception{
		String id=this.repository2Destinatari.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2D"},
			dataProvider="EGov2Destinatari",
			dependsOnMethods="EGov2Destinatari")
			public void testDBEGov2Destinatari(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Destinatari.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE,  SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/**
	 * 
	 * Header eGov con elemento 'ProfiloCollaborazione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione elemento presente piu' volte nell'header
	 *busta = buste_campi_duplicati/busta_2ProfiliCollaborazione.xml
	 * 
	 */
	Repository repository2ProfiliCollaborazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2PC"},
			dependsOnMethods="init")
			public void EGov2ProfiliCollaborazione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ProfiliCollaborazione.xml");
		this.repository2ProfiliCollaborazione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2ProfiliCollaborazione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2ProfiliCollaborazione.");
					throw new TestSuiteException("Invocazione PA con busta con 2ProfiliCollaborazione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento ProfiloCollaborazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2ProfiliCollaborazione")
	public Object[][] testEGov2ProfiliCollaborazione()throws Exception{
		String id=this.repository2ProfiliCollaborazione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2PC"},
			dataProvider="EGov2ProfiliCollaborazione",
			dependsOnMethods="EGov2ProfiliCollaborazione")
			public void testDBEGov2ProfiliCollaborazione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ProfiliCollaborazione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}











	/**
	 * 
	 * Header eGov con elemento 'Collaborazione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_104] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Collaborazione elemento presente piu' volte nell'header
	 *busta = buste_campi_duplicati/busta_2Collaborazioni.xml
	 * 
	 */
	Repository repository2Collaborazioni=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2C"},
			dependsOnMethods="init")
			public void EGov2Collaborazioni()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Collaborazioni.xml");
		this.repository2Collaborazioni.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Collaborazioni);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Collaborazioni.");
					throw new TestSuiteException("Invocazione PA con busta con 2Collaborazioni, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Collaborazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Collaborazioni")
	public Object[][] testEGov2Collaborazioni()throws Exception{
		String id=this.repository2Collaborazioni.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2C"},
			dataProvider="EGov2Collaborazioni",
			dependsOnMethods="EGov2Collaborazioni")
			public void testDBEGov2Collaborazioni(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Collaborazioni.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.COLLABORAZIONE_SCONOSCIUTA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header eGov con elemento 'Servizio' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_104] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio elemento presente piu' volte nell'header
	 *busta = buste_campi_duplicati/busta_2Servizi.xml
	 * 
	 */
	Repository repository2Servizi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2S"},
			dependsOnMethods="init")
			public void EGov2Servizi()throws TestSuiteException, SOAPException, Exception{
	
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Servizi.xml");
		this.repository2Servizi.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Servizi);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Servizi.");
					throw new TestSuiteException("Invocazione PA con busta con 2Servizi, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Servizio");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Servizi")
	public Object[][] testEGov2Servizi()throws Exception{
		String id=this.repository2Servizi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2S"},
			dataProvider="EGov2Servizi",
			dependsOnMethods="EGov2Servizi")
			public void testDBEGov2Servizi(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Servizi.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				Assert.assertTrue(num==1);
				
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,  CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header eGov con elemento 'Azione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_106] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Azione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Azioni.xml
	 * 
	 */
	Repository repository2Azioni=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2A"},
			dependsOnMethods="init")
			public void EGov2Azioni()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Azioni.xml");
		this.repository2Azioni.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Azioni);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Azioni.");
					throw new TestSuiteException("Invocazione PA con busta con 2Azione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Azione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Azioni")
	public Object[][] testEGov2Azioni()throws Exception{
		String id=this.repository2Azioni.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2A"},
			dataProvider="EGov2Azioni",
			dependsOnMethods="EGov2Azioni")
			public void testDBEGov2Azioni(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Azioni.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.AZIONE_SCONOSCIUTA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()));
				}
				
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}


		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header eGov con elemento 'Messaggio' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_002] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Messaggi.xml
	 * 
	 */
	Repository repository2Messaggi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Me"},
			dependsOnMethods="init")
			public void EGov2Messaggi()throws TestSuiteException, SOAPException, Exception{
	
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Messaggi.xml");
		this.repository2Messaggi.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Messaggi);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Messaggi.");
					throw new TestSuiteException("Invocazione PA con busta con 2Messaggi, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Messaggio");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Messaggi")
	public Object[][] testEGov2Messaggi()throws Exception{
		String id=this.repository2Messaggi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Me"},
			dataProvider="EGov2Messaggi",
			dependsOnMethods="EGov2Messaggi")
			public void testDBEGov2Messaggi(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Messaggi.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header eGov con elemento 'Identificatore' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_107] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Identificatori.xml
	 * 
	 */
	Repository repository2Identificatori=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2I"},
			dependsOnMethods="init")
			public void EGov2Identificatori()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Identificatori.xml");
		this.repository2Identificatori.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Identificatori);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Identificatori.");
					throw new TestSuiteException("Invocazione PA con busta con 2Identificatori, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Identificatore");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Identificatori")
	public Object[][] testEGov2Identificatori()throws Exception{
		String id=this.repository2Identificatori.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2I"},
			dataProvider="EGov2Identificatori",
			dependsOnMethods="EGov2Identificatori")
			public void testDBEGov2Identificatori(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Identificatori.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header eGov con elemento 'OraRegistrazione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2OreRegistrazioni.xml
	 * 
	 */
	Repository repository2OreRegistrazioni=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2OR"},
			dependsOnMethods="init")
			public void EGov2OreRegistrazioni()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OreRegistrazioni.xml");
		this.repository2OreRegistrazioni.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2OreRegistrazioni);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2OreRegistrazioni.");
					throw new TestSuiteException("Invocazione PA con busta con 2OreRegistrazioni, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento OraRegistrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2OreRegistrazioni")
	public Object[][] testEGov2OreRegistrazioni()throws Exception{
		String id=this.repository2OreRegistrazioni.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2OR"},
			dataProvider="EGov2OreRegistrazioni",
			dependsOnMethods="EGov2OreRegistrazioni")
			public void testDBEGov2OreRegistrazioni(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OreRegistrazioni.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.ORA_REGISTRAZIONE_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/**
	 * 
	 * Header eGov con elemento 'RiferimentoMessaggio' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_109] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/RiferimentoMessaggio elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2RiferimentiMessaggi.xml
	 * 
	 */
	Repository repository2RiferimentiMessaggi=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2RM"},
			dependsOnMethods="init")
			public void EGov2RiferimentiMessaggi()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2RiferimentiMessaggi.xml");
		this.repository2RiferimentiMessaggi.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2RiferimentiMessaggi);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2RiferimentiMessaggi.");
					throw new TestSuiteException("Invocazione PA con busta con 2RiferimentiMessaggi, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento RiferimentoMessaggio");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2RiferimentiMessaggi")
	public Object[][] testEGov2RiferimentiMessaggi()throws Exception{
		String id=this.repository2RiferimentiMessaggi.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2RM"},
			dataProvider="EGov2RiferimentiMessaggi",
			dependsOnMethods="EGov2RiferimentiMessaggi")
			public void testDBEGov2RiferimentiMessaggi(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2RiferimentiMessaggi.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.RIFERIMENTO_MESSAGGIO_NON_PRESENTE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_RIFERIMENTO_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 *
	 * Header eGov con elemento 'Scadenza' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_112] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Scadenza elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Scadenze.xml
	 * 
	 */
	Repository repository2Scadenze=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Sc"},
			dependsOnMethods="init")
			public void EGov2Scadenze()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Scadenze.xml");
		this.repository2Scadenze.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Scadenze);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Scadenze.");
					throw new TestSuiteException("Invocazione PA con busta con 2Scadenze, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Scadenza");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Scadenze")
	public Object[][] testEGov2Scadenze()throws Exception{
		String id=this.repository2Scadenze.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Sc"},
			dataProvider="EGov2Scadenze",
			dependsOnMethods="EGov2Scadenze")
			public void testDBEGov2Scadenze(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Scadenze.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.SCADENZA_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.SCADENZA_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.SCADENZA_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.SCADENZA_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header eGov con elemento 'ProfiloTrasmissione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2ProfiliTrasmissione.xml
	 *
	 */
	Repository repository2ProfiliTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2PT"},
			dependsOnMethods="init")
			public void EGov2ProfiliTrasmissione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ProfiliTrasmissione.xml");
		this.repository2ProfiliTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2ProfiliTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2ProfiliTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2ProfiliTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento ProfiloTrasmissione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2ProfiliTrasmissione")
	public Object[][] testEGov2ProfiliTrasmissione()throws Exception{
		String id=this.repository2ProfiliTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2PT"},
			dataProvider="EGov2ProfiliTrasmissione",
			dependsOnMethods="EGov2ProfiliTrasmissione")
			public void testDBEGov2ProfiliTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ProfiliTrasmissione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_TRASMISSIONE_NON_VALIDO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}










	/**
	 * 
	 * Header eGov con elemento 'Sequenza' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_114] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Sequenza elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2Sequenze.xml
	 *
	 */
	Repository repository2Sequenze=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Se"},
			dependsOnMethods="init")
			public void EGov2Sequenze()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Sequenze.xml");
		this.repository2Sequenze.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2Sequenze);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2Sequenze.");
					throw new TestSuiteException("Invocazione PA con busta con 2Sequenze, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento Sequenza");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2Sequenze")
	public Object[][] testEGov2Sequenze()throws Exception{
		String id=this.repository2Sequenze.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2Se"},
			dataProvider="EGov2Sequenze",
			dependsOnMethods="EGov2Sequenze")
			public void testDBEGov2Sequenze(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2Sequenze.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.CONSEGNA_IN_ORDINE_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,  CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/**
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2ListeTrasmissioni.xml
	 *
	 */
	Repository repository2ListeTrasmissioni=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LT"},
			dependsOnMethods="init")
			public void EGov2ListeTrasmissioni()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeTrasmissioni.xml");
		this.repository2ListeTrasmissioni.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2ListeTrasmissioni);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2ListeTrasmissioni.");
					throw new TestSuiteException("Invocazione PA con busta con 2ListeTrasmissioni, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento ListaTrasmissioni");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2ListeTrasmissioni")
	public Object[][] testEGov2ListeTrasmissioni()throws Exception{
		String id=this.repository2ListeTrasmissioni.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LT"},
			dataProvider="EGov2ListeTrasmissioni",
			dependsOnMethods="EGov2ListeTrasmissioni")
			public void testDBEGov2ListeTrasmissioni(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeTrasmissioni.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index), this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}
				
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni/Trasmissione/Origine' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2OrigineTrasmissione.xml
	 *
	 */
	Repository repository2OrigineTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2OT"},
			dependsOnMethods="init")
			public void EGov2OrigineTrasmissione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OrigineTrasmissione.xml");
		this.repository2OrigineTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2OrigineTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2OrigineTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2OrigineTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento TrasmissioneOrigine");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2OrigineTrasmissione")
	public Object[][] testEGov2OrigineTrasmissione()throws Exception{
		String id=this.repository2OrigineTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2OT"},
			dataProvider="EGov2OrigineTrasmissione",
			dependsOnMethods="EGov2OrigineTrasmissione")
			public void testDBEGov2OrigineTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OrigineTrasmissione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index), this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni/Trasmissione/Destinazione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2DestinazioneTrasmissione.xml
	 *
	 */
	Repository repository2DestinazioneTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2DT"},
			dependsOnMethods="init")
			public void EGov2DestinazioneTrasmissione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2DestinazioneTrasmissione.xml");
		this.repository2DestinazioneTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2DestinazioneTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2DestinazioneTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2DestinazioneTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento TrasmissioneDestinazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2DestinazioneTrasmissione")
	public Object[][] testEGov2DestinazioneTrasmissione()throws Exception{
		String id=this.repository2DestinazioneTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2DT"},
			dataProvider="EGov2DestinazioneTrasmissione",
			dependsOnMethods="EGov2DestinazioneTrasmissione")
			public void testDBEGov2DestinazioneTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2DestinazioneTrasmissione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index), this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni/Trasmissione/OraRegistrazione' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2OraRegistrazioneTrasmissione.xml
	 * 
	 */
	Repository repository2OraRegistrazioneTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2ORT"},
			dependsOnMethods="init")
			public void EGov2OraRegistrazioneTrasmissione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OraRegistrazioneTrasmissione.xml");
		this.repository2OraRegistrazioneTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2OraRegistrazioneTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2OraRegistrazioneTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2OraRegistrazioneTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento TrasmissioneOraRegistrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2OraRegistrazioneTrasmissione")
	public Object[][] testEGov2OraRegistrazioneTrasmissione()throws Exception{
		String id=this.repository2OraRegistrazioneTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2ORT"},
			dataProvider="EGov2OraRegistrazioneTrasmissione",
			dependsOnMethods="EGov2OraRegistrazioneTrasmissione")
			public void testDBEGov2OraRegistrazioneTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OraRegistrazioneTrasmissione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index), this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2IdentificativiParteOrigineTrasmissione.xml
	 * 
	 */
	Repository repository2IdentificativiParteOrigineTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IPOT"},
			dependsOnMethods="init")
			public void EGov2IdentificativiParteOrigineTrasmissione()throws TestSuiteException, SOAPException, Exception{
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificativiParteOrigineTrasmissione.xml");
		this.repository2IdentificativiParteOrigineTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2IdentificativiParteOrigineTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2IdentificativiParteOrigineTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2IdentificativiParteOrigineTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGov2IdentificativiParteOrigineTrasmissione")
	public Object[][] testEGov2IdentificativiParteOrigineTrasmissione()throws Exception{
		String id=this.repository2IdentificativiParteOrigineTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IPOT"},
			dataProvider="EGov2IdentificativiParteOrigineTrasmissione",
			dependsOnMethods="EGov2IdentificativiParteOrigineTrasmissione")
			public void testDBEGov2IdentificativiParteOrigineTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificativiParteOrigineTrasmissione.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			if(this.segnalazioneElementoPresentePiuVolte){
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
			}else{
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString()));
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/** 
	 * 
	 * Header eGov con elemento 'ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2IdentificativiParteDestinazioneTrasmissione.xml
	 * 
	 */
	Repository repository2IdentificativiParteDestinazioneTrasmissione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IPDT"},
			dependsOnMethods="init")
			public void EGov2IdentificativiParteDestinazioneTrasmissione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificativiParteDestinazioneTrasmissione.xml");
		this.repository2IdentificativiParteDestinazioneTrasmissione.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2IdentificativiParteDestinazioneTrasmissione);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2IdentificativiParteDestinazioneTrasmissione.");
					throw new TestSuiteException("Invocazione PA con busta con 2IdentificativiParteDestinazioneTrasmissione, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
	@DataProvider (name="EGov2IdentificativiParteDestinazioneTrasmissione")
	public Object[][] testEGov2IdentificativiParteDestinazioneTrasmissione()throws Exception{
		String id=this.repository2IdentificativiParteDestinazioneTrasmissione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IPDT"},
			dataProvider="EGov2IdentificativiParteDestinazioneTrasmissione",
			dependsOnMethods="EGov2IdentificativiParteDestinazioneTrasmissione")
			public void testDBEGov2IdentificativiParteDestinazioneTrasmissione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificativiParteDestinazioneTrasmissione.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Azione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.LISTA_TRASMISSIONI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			if(this.segnalazioneElementoPresentePiuVolte){
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
			}else{
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString()));
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/** 
	 * 
	 * Header eGov con elemento 'ListaEccezioni' presente piu' volte
	 * Verra' segnalata una eccezione [EGOV_IT_002] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaEccezioni elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2ListeEccezioni.xml
	 * 
	 */
	Repository repository2ListeEccezioni=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LE"},
			dependsOnMethods="init")
			public void EGov2ListeEccezioni()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeEccezioni.xml");
		this.repository2ListeEccezioni.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2ListeEccezioni);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2ListeEccezioni.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento ListaEccezioni");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2ListeEccezioni")
	public Object[][] testEGov2ListeEccezioni()throws Exception{
		String id=this.repository2ListeEccezioni.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),DatabaseProperties.getDatabaseComponentDiagnosticaErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LE"},
			dataProvider="EGov2ListeEccezioni",
			dependsOnMethods="EGov2ListeEccezioni")
			public void testDBEGov2ListeEccezioni(DatabaseComponent data, DatabaseMsgDiagnosticiComponent dataMsgDiagnostici,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeEccezioni.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("controllo l'eccezione nei messaggi diagnostici: ");
				if(this.segnalazioneElementoPresentePiuVolte){
					Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
							this.busteEGovErrate.getID(index),CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO,
							SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
							SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
							SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Assert.assertTrue(dataMsgDiagnostici.isTracedMessaggio(
							this.busteEGovErrate.getID(index),CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO,
							SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,
							SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
							SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_ECCEZIONI_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			try{
				data.close();
			}catch(Exception e){}
			try{
				dataMsgDiagnostici.close();
			}catch(Exception e){}
		}
	}


















	/** 
	 * 
	 * Header eGov con elemento 'ListaRiscontri' presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2ListeRiscontri.xml
	 * 
	 */
	Repository repository2ListeRiscontri=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LR"},
			dependsOnMethods="init")
			public void EGov2ListeRiscontri()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeRiscontri.xml");
		this.repository2ListeRiscontri.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2ListeRiscontri);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2ListeRiscontri.");
					throw new TestSuiteException("Invocazione PA con busta con 2ListeRiscontri, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento ListaRiscontri");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2ListeRiscontri")
	public Object[][] testEGov2ListeRiscontri()throws Exception{
		String id=this.repository2ListeRiscontri.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2LR"},
			dataProvider="EGov2ListeRiscontri",
			dependsOnMethods="EGov2ListeRiscontri")
			public void testDBEGov2ListeRiscontri(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2ListeRiscontri.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/** 
	 * 
	 * Header eGov con elemento 'ListaRiscontri/Riscontro' con elemento identificatore presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/Identificatore elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2IdentificatoriRiscontro_ListeRiscontri.xml
	 * 
	 */
	Repository repository2IdentificatoriRiscontro_ListeRiscontri=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IR_LR"},
			dependsOnMethods="init")
			public void EGov2IdentificatoriRiscontro_ListeRiscontri()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificatoriRiscontro_ListeRiscontri.xml");
		this.repository2IdentificatoriRiscontro_ListeRiscontri.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2IdentificatoriRiscontro_ListeRiscontri);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2IdentificatoriRiscontro_ListeRiscontri.");
					throw new TestSuiteException("Invocazione PA con busta con 2IdentificatoriRiscontro_ListeRiscontri, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento RiscontroIdentificatore");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2IdentificatoriRiscontro_ListeRiscontri")
	public Object[][] testEGov2IdentificatoriRiscontro_ListeRiscontri()throws Exception{
		String id=this.repository2IdentificatoriRiscontro_ListeRiscontri.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2IR_LR"},
			dataProvider="EGov2IdentificatoriRiscontro_ListeRiscontri",
			dependsOnMethods="EGov2IdentificatoriRiscontro_ListeRiscontri")
			public void testDBEGov2IdentificatoriRiscontro_ListeRiscontri(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2IdentificatoriRiscontro_ListeRiscontri.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}


		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}









	/** 
	 * 
	 * Header eGov con elemento 'ListaRiscontri/Riscontro' con elemento oraRegistrazione presente piu' volte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione elemento presente piu' volte nell'header
	 * busta = buste_campi_duplicati/busta_2OraRegistrazioneRiscontro_ListeRiscontri.xml
	 * 
	 */
	Repository repository2OraRegistrazioneRiscontro_ListeRiscontri=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2ORR_LR"},
			dependsOnMethods="init")
	public void EGov2OraRegistrazioneRiscontro_ListeRiscontri()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OraRegistrazioneRiscontro_ListeRiscontri.xml");
		this.repository2OraRegistrazioneRiscontro_ListeRiscontri.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repository2OraRegistrazioneRiscontro_ListeRiscontri);
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
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con 2OraRegistrazioneRiscontro_ListeRiscontri.");
					throw new TestSuiteException("Invocazione PA con busta con 2OraRegistrazioneRiscontro_ListeRiscontri, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
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
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con più di un elemento RiscontroOraRegistrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGov2OraRegistrazioneRiscontro_ListeRiscontri")
	public Object[][] testEGov2OraRegistrazioneRiscontro_ListeRiscontri()throws Exception{
		String id=this.repository2OraRegistrazioneRiscontro_ListeRiscontri.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(2500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".2ORR_LR"},
			dataProvider="EGov2OraRegistrazioneRiscontro_ListeRiscontri",
			dependsOnMethods="EGov2OraRegistrazioneRiscontro_ListeRiscontri")
			public void testDBEGov2OraRegistrazioneRiscontro_ListeRiscontri(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("busta_2OraRegistrazioneRiscontro_ListeRiscontri.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Azione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedAzione(id, this.busteEGovErrate.getAzione(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA+" 1 volta");
				int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Reporter.log("Check ha trovato eccezioni num:"+num);
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Assert.assertTrue(num==1);
				}else{
					Assert.assertTrue(num==2);
				}
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				if(this.segnalazioneElementoPresentePiuVolte){
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString()+" "+CostantiProtocollo.ECCEZIONE_ELEMENTO_PRESENTE_PIU_VOLTE));
				}else{
					Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE);
					Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString()));
				}
			}else{
				Reporter.log("Controllo tracciamento richiesta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id)==false);
				
				Reporter.log("Controllo tracciamento risposta == false con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id)==false);
			}

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
}


