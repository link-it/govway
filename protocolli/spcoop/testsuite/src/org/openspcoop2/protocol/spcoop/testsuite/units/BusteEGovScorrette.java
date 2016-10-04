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
import java.io.FileInputStream;
import java.util.Date;
import java.util.Vector;

import javax.xml.soap.SOAPException;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.Inoltro;
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
import org.openspcoop2.testsuite.db.DatiServizio;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test sulle buste E-Gov errate
 *  
 * @author Carlo Ciurli (ciurli@openspcoop.org)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BusteEGovScorrette {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "BusteEGovScorrette";

	
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

	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BNP", ID_GRUPPO+".SAC", ID_GRUPPO+".SNC",
			ID_GRUPPO+".ICANE",ID_GRUPPO+".SACMU", ID_GRUPPO+".SAc", ID_GRUPPO+".AcS",
			ID_GRUPPO+".SMU",ID_GRUPPO+".NS", ID_GRUPPO+".SIM", ID_GRUPPO+".SoIM",
			ID_GRUPPO+".SM",ID_GRUPPO+".SD",ID_GRUPPO+".SIPM",ID_GRUPPO+".SIPD",
			ID_GRUPPO+".MS",ID_GRUPPO+".DS",ID_GRUPPO+".PM",ID_GRUPPO+".PD",
			ID_GRUPPO+".PMMS",ID_GRUPPO+".PDDS", ID_GRUPPO+".TMNP", ID_GRUPPO+".TDNP",
			ID_GRUPPO+".TMS",ID_GRUPPO+".TDS",ID_GRUPPO+".PCNP",ID_GRUPPO+".PCNV",
			ID_GRUPPO+".PCTSCNV",ID_GRUPPO+".PCDR",ID_GRUPPO+".CNV",ID_GRUPPO+".SSe",
			ID_GRUPPO+".SS",ID_GRUPPO+".TSNP",ID_GRUPPO+".TSE",ID_GRUPPO+".SA",ID_GRUPPO+".AS",
			ID_GRUPPO+".SMe", ID_GRUPPO+".SNV",ID_GRUPPO+".S",ID_GRUPPO+".SI",ID_GRUPPO+".INV",
			ID_GRUPPO+".ID", ID_GRUPPO+".IDSS", ID_GRUPPO+".SOR",ID_GRUPPO+".ORNV",ID_GRUPPO+".PTINV",
			ID_GRUPPO+".PTCRNV",ID_GRUPPO+".SSNP",ID_GRUPPO+".SENP",
			ID_GRUPPO+".LTTNP",ID_GRUPPO+".LTOTNP",ID_GRUPPO+".LTDTNP",ID_GRUPPO+".LTORTNP",ID_GRUPPO+".LTOTNV",
			ID_GRUPPO+".LTDTNV",ID_GRUPPO+".LTORTNV",ID_GRUPPO+".LTOTIPNP",ID_GRUPPO+".LTDTIPNP",
			ID_GRUPPO+".LTOTIPTNP",ID_GRUPPO+".LTDTIPTNP",ID_GRUPPO+".LTOTIPTNV",ID_GRUPPO+".LTDTIPTNV",
			ID_GRUPPO+".MITE",ID_GRUPPO+".DITE",ID_GRUPPO+".LTOTIPITE",ID_GRUPPO+".LTDTIPITE",
			ID_GRUPPO+".LTORTTNP",ID_GRUPPO+".LTORTTNV",ID_GRUPPO+".LRSR",ID_GRUPPO+".LRRSI",
			ID_GRUPPO+".LRRIE", ID_GRUPPO+".LRRORE",ID_GRUPPO+".LRRSOR",ID_GRUPPO+".LRRORST",ID_GRUPPO+".LRRORTE",
			ID_GRUPPO+".TORNonPresente",ID_GRUPPO+".TORNonValido",ID_GRUPPO+".MUgualeZERO",
			ID_GRUPPO+".MittenteSenzaValoreIdentificativoParte",ID_GRUPPO+".DestinatarioSenzaValoreIdentificativoParte",
			ID_GRUPPO+".ErroreSintattico1",ID_GRUPPO+".ErroreSintattico2",ID_GRUPPO+".LTOTNP_OrigineVuota"
			})
	public void  init() throws Exception{
		try{
			File[] dir =  new java.io.File(Utilities.testSuiteProperties.getPathBusteErrate()).listFiles();
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
	 * 
	 * Header e-Gov non presente. Deve ritornare un SOAPFault con code=soap:Client e faultString="EGOV_IT_001 - Formato Busta non corretto"
	 * busta = buste_errate/bustaNonPresente.xml
	 * 
	 * Test equivalente al Test N.32 Della Certificazione DigitPA (Busta Errata 101)
	 */
	Repository repositorybustaNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".BNP"},
			dependsOnMethods="init")
			public void EGovConbustaNonPresente()throws TestSuiteException, SOAPException, Exception{

		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaNonPresente.xml");
		this.repositorybustaNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorybustaNonPresente);
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
					Reporter.log("Invocazione PA con una busta Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta Non Presente, non ha causato errori.");
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



	
	
	
	
	
	
	/**
	 * 
	 * Header e-Gov sintatticamente errato.
	 * tag Mittente errato. 
	 *  Deve ritornare un SOAPFault con code=soap:Client e faultString="EGOV_IT_001 - Formato Busta non corretto"
	 * busta = buste_errate/bustaErrataSintatticamente1_Test15Certificazione.xml
	 * 
	 * Test equivalente al Test N.15 Della Certificazione DigitPA (Busta Errata 112)
	 */
	Repository repositorybustaSintatticamenteErrata1=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ErroreSintattico1"},
			dependsOnMethods="init")
			public void EGovConbustaSintatticamenteErrata1()throws TestSuiteException, SOAPException, Exception{

		Date dataInizioTest = DateManager.getDate();
		
		Message msg=new Message(new FileInputStream(Utilities.testSuiteProperties.getPathBusteErrate()+File.separatorChar+"bustaErrataSintatticamente1_Test15Certificazione.xml"));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorybustaSintatticamenteErrata1);
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
					Reporter.log("Invocazione PA con una busta Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta Non Presente, non ha causato errori.");
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		// cxf
		ErroreAttesoOpenSPCoopLogCore errCXF = new ErroreAttesoOpenSPCoopLogCore();
		errCXF.setIntervalloInferiore(dataInizioTest);
		errCXF.setIntervalloSuperiore(dataFineTest);
		errCXF.setMsgErrore("The element type \"eGov_IT:IntestazioneMessaggio\" must be terminated by the matching end-tag \"</eGov_IT:IntestazioneMessaggio>\".");
		this.erroriAttesiOpenSPCoopCore.add(errCXF);
		
		// other
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Unexpected close tag </eGov_IT:Mittente>; expected </eGov_IT:IntestazioneMessaggio>.");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("parsingExceptionRichiesta");
		this.erroriAttesiOpenSPCoopCore.add(err4);	
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Header e-Gov sintatticamente errato.
	 * tag Mittente con due attributi tipo. 
	 *  Deve ritornare un SOAPFault con code=soap:Client e faultString="EGOV_IT_001 - Formato Busta non corretto"
	 * busta = buste_errate/bustaErrataSintatticamente2_Test17Certificazione.xml
	 * 
	 * Test equivalente al Test N.17 Della Certificazione DigitPA (Busta Errata 110)
	 */
	Repository repositorybustaSintatticamenteErrata2=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ErroreSintattico2"},
			dependsOnMethods="init")
			public void EGovConbustaSintatticamenteErrata2()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		Message msg=new Message(new FileInputStream(Utilities.testSuiteProperties.getPathBusteErrate()+File.separatorChar+"bustaErrataSintatticamente2_Test17Certificazione.xml"));
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorybustaSintatticamenteErrata2);
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
					Reporter.log("Invocazione PA con una busta Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta Non Presente, non ha causato errori.");
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
		err.setMsgErrore("ErroreGenerale");
		this.erroriAttesiOpenSPCoopCore.add(err);
		
		// cxf
		ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
		err2.setIntervalloInferiore(dataInizioTest);
		err2.setIntervalloSuperiore(dataFineTest);
		err2.setMsgErrore("Attribute \"tipo\" was already specified for element \"eGov_IT:IdentificativoParte\".");
		this.erroriAttesiOpenSPCoopCore.add(err2);
		
		// other
		ErroreAttesoOpenSPCoopLogCore err3 = new ErroreAttesoOpenSPCoopLogCore();
		err3.setIntervalloInferiore(dataInizioTest);
		err3.setIntervalloSuperiore(dataFineTest);
		err3.setMsgErrore("Duplicate attribute 'tipo'.");
		this.erroriAttesiOpenSPCoopCore.add(err3);	
		
		ErroreAttesoOpenSPCoopLogCore err4 = new ErroreAttesoOpenSPCoopLogCore();
		err4.setIntervalloInferiore(dataInizioTest);
		err4.setIntervalloSuperiore(dataFineTest);
		err4.setMsgErrore("parsingExceptionRichiesta");
		this.erroriAttesiOpenSPCoopCore.add(err4);	
	}
	
	
	
	
	
	
	



	/**
	 * 
	 * Header e-Gov con solo actor corretto, ma tutto il resto errato
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: ?/?
	 * - lista eccezioni con eccezione di codice 001 e posizione "Intestazione"
	 * busta = buste_errate/bustaConSoloActorCorretto.xml
	 * 
	 */
	Repository repositorySoloActorCorretto=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SAC"},
			dependsOnMethods="init")
			public void EGovConSoloActorCorretto()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloActorCorretto.xml");
		this.repositorySoloActorCorretto.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoloActorCorretto);
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
					Reporter.log("Invocazione PA con una busta con Destinatario Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Destinatario Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConSoloActorCorretto")
	public Object[][] testEGovConSoloActorCorretto()throws Exception{
		String id=this.repositorySoloActorCorretto.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SAC"},
			dataProvider="EGovConSoloActorCorretto",
			dependsOnMethods="EGovConSoloActorCorretto")
			public void testDBEGovConSoloActorCorretto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloActorCorretto.xml");
		try{
			Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_NON_CORRETTO+"], " +
					"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_NON_CORRETTO_POSIZIONE+"]");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
					new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
					new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
					CodiceErroreCooperazione.FORMATO_NON_CORRETTO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_NON_CORRETTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * Header e-Gov con solo namespace corretto, ma tutto il resto errato
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: ?/?
	 * - lista eccezioni con eccezione di codice 001 e posizione "Intestazione"
	 * busta = buste_errate/bustaConSoloNamespaceCorretto.xml
	 * 
	 */
	Repository repositorySoloNamespaceCorretto=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SNC"},
			dependsOnMethods="init")
			public void EGovConSoloNamespaceCorretto()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloNamespaceCorretto.xml");
		this.repositorySoloNamespaceCorretto.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySoloNamespaceCorretto);
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
					Reporter.log("Invocazione PA con una busta con Destinatario Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Destinatario Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConSoloNamespaceCorretto")
	public Object[][] testEGovConSoloNamespaceCorretto()throws Exception{
		String id=this.repositorySoloNamespaceCorretto.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SNC"},
			dataProvider="EGovConSoloNamespaceCorretto",
			dependsOnMethods="EGovConSoloNamespaceCorretto")
			public void testDBEGovConSoloNamespaceCorretto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloNamespaceCorretto.xml");
		try{

			Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_NON_CORRETTO+"], " +
					"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_NON_CORRETTO_POSIZIONE+"]");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
					new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
					new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
					CodiceErroreCooperazione.FORMATO_NON_CORRETTO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_NON_CORRETTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * Header e-Gov con actor e namespace scorretto
	 * Deve ritornare un SOAPFault con code=soap:Client e faultString="EGOV_IT_001 - Formato Busta non corretto"
	 * busta = buste_errate/bustaConIntestazioneCorretta_ActorENamespaceErrati.xml
	 * 
	 */
	Repository repositoryIntestazioneCorretta_ActorENamespaceErrati=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ICANE"},
			dependsOnMethods="init")
			public void EGovConIntestazioneCorretta_ActorENamespaceErrati()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConIntestazioneCorretta_ActorENamespaceErrati.xml");
		this.repositoryIntestazioneCorretta_ActorENamespaceErrati.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIntestazioneCorretta_ActorENamespaceErrati);
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
					Reporter.log("Invocazione PA con una busta Con Intestazione Corretta Actor e Namespace Errati.");
					throw new TestSuiteException("Invocazione PA con busta Con Intestazione Corretta Actor e Namespace Errati, non ha causato errori.");
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







	/**
	 *
	 * Header e-Gov senza actor, e con mustUnderstand=0
	 * La busta ritornata possiedera':
	 * - lista eccezioni con 2 eccezioni di codice 002 e posizione "Intestazione/actor" e "Intestazione/mustUnderstand"
	 * busta = buste_errate/bustaSenzaActor_MustUnderstand0.xml
	 *
	 */
	Repository repositorySenzaActor_MustUnderstand0=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SACMU"},
			dependsOnMethods="init")
			public void EGovSenzaActor_MustUnderstand0()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaActor_MustUnderstand0.xml");
		this.repositorySenzaActor_MustUnderstand0.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaActor_MustUnderstand0);
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
					Reporter.log("Invocazione PA con una busta con Senza Actor MustUnderstand 0.");
					throw new TestSuiteException("Invocazione PA con busta con Senza Actor MustUnderstand 0, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con actor scorretto");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaActor_MustUnderstand0")
	public Object[][] testEGovSenzaActor_MustUnderstand0()throws Exception{
		String id=this.repositorySenzaActor_MustUnderstand0.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SACMU"},
			dataProvider="EGovSenzaActor_MustUnderstand0",
			dependsOnMethods="EGovSenzaActor_MustUnderstand0")
			public void testDBEGovSenzaActor_MustUnderstand0(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaActor_MustUnderstand0.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopActorNonCorretto()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index) ));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
		
				Reporter.log("-------------- RISPOSTA --------------------");
		
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
		
		
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
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR.toString().toString()));
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND.toString().toString()));

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
	 * Header e-Gov senza actor
	 * La busta ritornata possiedera':
	 * - lista eccezioni con eccezione di codice 002 e posizione "Intestazione/actor"
	 * NOTA: per generare questo test e' necessario impostare la seguente proprieta' di OpenSPCoop al valore 'true': org.openspcoop2.pdd.services.BypassMustUnderstandHandler.allHeaders
	 *       o in alternativa permettere il processamento dell'header con namespace 'http://www.cnipa.it/schemas/2003/eGovIT/Busta1_0/'
	 *       da parte dell'engine axis di OpenSPCoop anche con mustUnderstand=1 e actor non definito
	 * In OpenSPCoop2 il byPassMustUnderstandHandler e' stato implementato come personalizzazione del protocollo
	 * busta = buste_errate/bustaSenzaActor.xml
	 * 
	 */
	Repository repositorySenzaActor=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SAc"},
			dependsOnMethods="init")
			public void EGovSenzaActor()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaActor.xml");
		this.repositorySenzaActor.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaActor);
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
					Reporter.log("Invocazione PA con una busta Senza Actor.");
					throw new TestSuiteException("Invocazione PA con busta Senza Actor, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con actor scorretto");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaActor")
	public Object[][] testEGovSenzaActor()throws Exception{
		String id=this.repositorySenzaActor.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SAc"},
			dataProvider="EGovSenzaActor",
			dependsOnMethods="EGovSenzaActor")
			public void testDBEGovSenzaActor(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaActor.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopActorNonCorretto()){
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
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR.toString().toString()));

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
	 * Header e-Gov con actor scorretto
	 * La busta ritornata possiedera':
	 * - lista eccezioni con eccezione di codice 002 e posizione "Intestazione/actor"
	 * busta = buste_errate/bustaActorScorretto.xml
	 *
	 * Test equivalente al Test N.13 Della Certificazione DigitPA (Busta Errata 2)
	 */
	Repository repositoryActorScorretto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".AcS"},
			dependsOnMethods="init")
			public void EGovActorScorretto()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaActorScorretto.xml");
		this.repositoryActorScorretto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryActorScorretto);
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
					Reporter.log("Invocazione PA con una busta con Actor Scorretto.");
					throw new TestSuiteException("Invocazione PA con busta con Actor Scorretto, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con actor scorretto");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovActorScorretto")
	public Object[][] testEGovActorScorretto()throws Exception{
		String id=this.repositoryActorScorretto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".AcS"},
			dataProvider="EGovActorScorretto",
			dependsOnMethods="EGovActorScorretto")
			public void testDBEGovActorScorretto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaActorScorretto.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopActorNonCorretto()){
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
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
		
				Reporter.log("-------------- RISPOSTA --------------------");
		
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
		
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_ACTOR.toString()));

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
	 * Header e-Gov senza mustUnderstand
	 * La busta ritornata possiedera':
	 * - lista eccezioni con eccezione di codice 002 e posizione "Intestazione/mustUnderstand"
	 * busta = buste_errate/bustaSenzaMustUnderstand.xml
	 *
	 */
	Repository repositorySenzaMustUnderstand=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SMU"},
			dependsOnMethods="init")
			public void EGovSenzaMustUnderstand()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMustUnderstand.xml");
		this.repositorySenzaMustUnderstand.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaMustUnderstand);
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
					Reporter.log("Invocazione PA con una busta Senza MustUnderstand.");
					throw new TestSuiteException("Invocazione PA con busta Senza MustUnderstand, non ha causato errori.");
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
	@DataProvider (name="EGovSenzaMustUnderstand")
	public Object[][] testEGovSenzaMustUnderstand()throws Exception{
		String id=this.repositorySenzaMustUnderstand.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SMU"},
			dataProvider="EGovSenzaMustUnderstand",
			dependsOnMethods="EGovSenzaMustUnderstand")
			public void testDBEGovSenzaMustUnderstand(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMustUnderstand.xml");
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * Header e-Gov con mustUnderstand=0
	 * La busta ritornata possiedera':
	 * - lista eccezioni con eccezione di codice 002 e posizione "Intestazione/mustUnderstand"
	 * busta = buste_errate/bustaMustUnderstand0.xml
	 *
	 * Test equivalente al Test N.12 Della Certificazione DigitPA (Busta Errata 1)
	 */
	Repository repositoryConMustUnderstandZero=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MUgualeZERO"},
			dependsOnMethods="init")
			public void EGovConMustUnderstandZero()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMustUnderstand0.xml");
		this.repositoryConMustUnderstandZero.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConMustUnderstandZero);
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
					Reporter.log("Invocazione PA con una busta Senza MustUnderstand.");
					throw new TestSuiteException("Invocazione PA con busta Senza MustUnderstand, non ha causato errori.");
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
	@DataProvider (name="EGovConMustUnderstandZero")
	public Object[][] testEGovConMustUnderstandZero()throws Exception{
		String id=this.repositoryConMustUnderstandZero.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MUgualeZERO"},
			dataProvider="EGovConMustUnderstandZero",
			dependsOnMethods="EGovConMustUnderstandZero")
			public void testDBEGovConMustUnderstandZero(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMustUnderstand0.xml");
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MUST_UNDERSTAND.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	






	/**
	 *
	 * Header e-Gov con namespace scorretto
	 * La busta ritornata possiedera':
	 * - lista eccezioni con eccezione di codice 002 e posizione "Intestazione/namespace"
	 * busta = buste_errate/bustaNamespaceScorretto.xml
	 * 
	 */
	Repository repositoryNamespaceScorretto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".NS"},
			dependsOnMethods="init")
			public void EGovNamespaceScorretto()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaNamespaceScorretto.xml");
		this.repositoryNamespaceScorretto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryNamespaceScorretto);
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
					Reporter.log("Invocazione PA con una busta con Namespace Scorretto.");
					throw new TestSuiteException("Invocazione PA con busta con Namespace Scorretto, non ha causato errori.");
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
	@DataProvider (name="EGovNamespaceScorretto")
	public Object[][] testEGovNamespaceScorretto()throws Exception{
		String id=this.repositoryNamespaceScorretto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".NS"},
			dataProvider="EGovNamespaceScorretto",
			dependsOnMethods="EGovNamespaceScorretto")
			public void testDBEGovNamespaceScorretto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaNamespaceScorretto.xml");
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_NAMESPACE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_NAMESPACE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header e-Gov senza IntestazioneMessaggio
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: ?/?
	 * - lista eccezioni con eccezione di codice 002 e posizione "IntestazioneMessaggio"
	 * busta = buste_errate/bustaSenzaIntestazioneMessaggio.xml
	 * 
	 */
	Repository repositorySenzaIntestazioneMessaggio=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SIM"},
			dependsOnMethods="init")
			public void EGovSenzaIntestazioneMessaggio()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIntestazioneMessaggio.xml");
		this.repositorySenzaIntestazioneMessaggio.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaIntestazioneMessaggio);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovSenzaIntestazioneMessaggio")
	public Object[][] testEGovSenzaIntestazioneMessaggio()throws Exception{
		String id=this.repositorySenzaIntestazioneMessaggio.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SIM"},
			dataProvider="EGovSenzaIntestazioneMessaggio",
			dependsOnMethods="EGovSenzaIntestazioneMessaggio")
			public void testDBEGovSenzaIntestazioneMessaggio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIntestazioneMessaggio.xml");
		try{

			Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+"], " +
					"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO+"]");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
					new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
					new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
					CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO,
					SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
					SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_INTESTAZIONE_MESSAGGIO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header e-Gov con solo IntestazioneMessaggio, senza elementi interni
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: ?/?
	 * - lista eccezioni con
	 *         [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente
	 *         [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario
	 *         [EGOV_IT_002] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio
	 * busta = buste_errate/bustaConSoloIntestazioneMessaggio.xml
	 *
	 */
	Repository repositoryConSoloIntestazioneMessaggio=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SoIM"},
			dependsOnMethods="init")
			public void EGovConSoloIntestazioneMessaggio()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloIntestazioneMessaggio.xml");
		this.repositoryConSoloIntestazioneMessaggio.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryConSoloIntestazioneMessaggio);
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
					Reporter.log("Invocazione PA con una busta con Solo Intestazione Messaggio.");
					throw new TestSuiteException("Invocazione PA con busta con Solo Intestazione Messaggio, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Mittente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConSoloIntestazioneMessaggio")
	public Object[][] testEGovConSoloIntestazioneMessaggio()throws Exception{
		String id=this.repositoryConSoloIntestazioneMessaggio.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SoIM"},
			dataProvider="EGovConSoloIntestazioneMessaggio",
			dependsOnMethods="EGovConSoloIntestazioneMessaggio")
			public void testDBEGovConSoloIntestazioneMessaggio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConSoloIntestazioneMessaggio.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+"], " +
						"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
						new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
						new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
						CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO,
						SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString()));
	
				Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+"], " +
						"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
						new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
						new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
						CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO,
						SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString()));
	
				Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+"], " +
						"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
						new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),
						new IDSoggetto(Utilities.testSuiteProperties.getKeywordTipoMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordMittenteSconosciuto(),Utilities.testSuiteProperties.getKeywordDominioSconosciuto()),
						CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO,
						SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO.toString()));
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
	 * Header e-Gov senza Mittente
	 * La busta ritornata possiedera':
	 * - mittente: soggetto destinatario della busta
	 * - destinatario: ?/?
	 * - lista eccezioni con [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente
	 * busta = buste_errate/bustaSenzaMittente.xml
	 *
	 *
	 *
	 */
	Repository repositorySenzaMittente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SM"},
			dependsOnMethods="init")
			public void EGovSenzaMittente()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMittente.xml");
		this.repositorySenzaMittente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaMittente);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta Senza Mittente.");
					throw new TestSuiteException("Invocazione PA con busta Senza Mittente, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Mittente");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaMittente")
	public Object[][] testEGovSenzaMittente()throws Exception{
		String id=this.repositorySenzaMittente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SM"},
			dataProvider="EGovSenzaMittente",
			dependsOnMethods="EGovSenzaMittente")
			public void testDBEGovSenzaMittente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMittente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,new IDSoggetto("?","?","?"),null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				DatiServizio datiServizio = this.busteEGovErrate.getDatiServizio(index);
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,new IDSoggetto("?","?","?"),null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE.toString()));

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
	 * Header e-Gov senza Destinatario
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: soggetto mittente della busta
	 * - lista eccezioni con [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario
	 * busta = buste_errate/bustaSenzaDestinatario.xml
	 *
	 *
	 *
	 */
	Repository repositorySenzaDestinatario=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SD"},
			dependsOnMethods="init")
			public void EGovSenzaDestinatario()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaDestinatario.xml");
		this.repositorySenzaDestinatario.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaMittente);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta Senza Destinatario.");
					throw new TestSuiteException("Invocazione PA con busta Senza Destinatario, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Destinatario");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaDestinatario")
	public Object[][] testEGovSenzaDestinatario()throws Exception{
		String id=this.repositorySenzaDestinatario.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SD"},
			dataProvider="EGovSenzaDestinatario",
			dependsOnMethods="EGovSenzaDestinatario")
			public void testDBEGovSenzaDestinatario(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaDestinatario.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,new IDSoggetto("?","?","?"),null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				DatiServizio datiServizio = this.busteEGovErrate.getDatiServizio(index);
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
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
				Assert.assertTrue(num==1);
				
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE.toString()));

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
	 * Header e-Gov senza identificativo parte Mittente
	 * La busta ritornata possiedera':
	 * - mittente: soggetto destinatario della busta
	 * - destinatario: ?/?
	 * - lista eccezioni con [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte
	 *busta = buste_errate/bustaSenzaIdentificativoParteMittente.xml
	 *
	 *
	 *
	 */
	Repository repositorySenzaIdentificativoParteMittente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SIPM"},
			dependsOnMethods="init")
			public void EGovSenzaIdentificativoParteMittente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificativoParteMittente.xml");
		this.repositorySenzaIdentificativoParteMittente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaIdentificativoParteMittente);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Mittente/IdentificativoParte");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaIdentificativoParteMittente")
	public Object[][] testEGovSenzaIdentificativoParteMittente()throws Exception{
		String id=this.repositorySenzaIdentificativoParteMittente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SIPM"},
			dataProvider="EGovSenzaIdentificativoParteMittente",
			dependsOnMethods="EGovSenzaIdentificativoParteMittente")
			public void testDBEGovSenzaIdentificativoParteMittente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificativoParteMittente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,new IDSoggetto("?","?","?"),null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				DatiServizio datiServizio = this.busteEGovErrate.getDatiServizio(index);
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,new IDSoggetto("?", "?", "?"),null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

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
	 * Header e-Gov senza identificativo parte Destinatario
	 * La busta ritornata possiedera':
	 * - mittente: soggetto default della porta di dominio
	 * - destinatario: soggetto mittente della busta
	 * - lista eccezioni con [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte
	 *busta = buste_errate/bustaSenzaIdentificativoParteDestinatario.xml
	 *
	 *
	 * 
	 */
	Repository repositorySenzaIdentificativoParteDestinatario=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SIPD"},
			dependsOnMethods="init")
			public void EGovSenzaIdentificativoParteDestinatario()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificativoParteDestinatario.xml");
		this.repositorySenzaIdentificativoParteDestinatario.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaIdentificativoParteDestinatario);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Destinatario/IdentificativoParte");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaIdentificativoParteDestinatario")
	public Object[][] testEGovSenzaIdentificativoParteDestinatario()throws Exception{
		String id=this.repositorySenzaIdentificativoParteDestinatario.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SIPD"},
			dataProvider="EGovSenzaIdentificativoParteDestinatario",
			dependsOnMethods="EGovSenzaIdentificativoParteDestinatario")
			public void testDBEGovSenzaIdentificativoParteDestinatario(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificativoParteDestinatario.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,new IDSoggetto("?","?","?"),null));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				DatiServizio datiServizio = this.busteEGovErrate.getDatiServizio(index);
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, datiServizio));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio ["+datiServizio.getTipoServizio()+"]["+datiServizio.getNomeServizio()+"] Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, datiServizio));
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
				Assert.assertTrue(num==1);
				
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

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
	 * Header e-Gov con Mittente sconosciuto
	 * La busta ritornata possiedera':
	 * - lista eccezioni con [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte
	 * busta = buste_errate/bustaMittenteSconosciuto.xml
	 * 
	 */
	Repository repositoryMittenteSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MS"},
			dependsOnMethods="init")
			public void EGovConMittenteSconosciuto()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteSconosciuto.xml");
		this.repositoryMittenteSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryMittenteSconosciuto);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
		err.setMsgErrore("Riconoscimento profilo di gestione non riuscito");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConMittenteSconosciuto")
	public Object[][] testEGovConMittenteSconosciuto()throws Exception{
		String id=this.repositoryMittenteSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MS"},
			dataProvider="EGovConMittenteSconosciuto",
			dependsOnMethods="EGovConMittenteSconosciuto")
			public void testDBEGovConMittenteSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			IDSoggetto mittente = this.busteEGovErrate.getMittente(index);
			mittente.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	


	/**
	 * 
	 * Header e-Gov con Destinatario sconosciuto
	 * La busta ritornata possiedera':
	 * - lista eccezioni con [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte
	 * busta = buste_errate/bustaDestinatarioSconosciuto.xml
	 * 
	 */
	Repository repositoryDestinatarioSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DS"},
			dependsOnMethods="init")
			public void EGovConDestinatarioSconosciuto()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioSconosciuto.xml");
		this.repositoryDestinatarioSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryDestinatarioSconosciuto);
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
					Reporter.log("Invocazione PA con una busta con Destinatario Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Destinatario Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConDestinatarioSconosciuto")
	public Object[][] testEGovConDestinatarioSconosciuto()throws Exception{
		String id=this.repositoryDestinatarioSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DS"},
			dataProvider="EGovConDestinatarioSconosciuto",
			dependsOnMethods="EGovConDestinatarioSconosciuto")
			public void testDBEGovConDestinatarioSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,destinatario,this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			
			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,destinatario,this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));
		
		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}



	
	
	
	/**
	 * 
	 * Header e-Gov con Mittente senza un valore nell'identificativo parte
	 * La busta ritornata possiedera':
	 * - lista eccezioni con [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte
	 * busta = buste_errate/bustaMittenteSenzaValoreIdentificativoParte.xml
	 * 
	 * Test equivalente al Test N.14 Della Certificazione DigitPA (Busta Errata 6)
	 */
	Repository repositoryMittenteSenzaValoreIdentificativoParte=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MittenteSenzaValoreIdentificativoParte"},
			dependsOnMethods="init")
			public void EGovConMittenteSenzaValoreIdentificativoParte()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteSenzaValoreIdentificativoParte.xml");
		this.repositoryMittenteSenzaValoreIdentificativoParte.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryMittenteSenzaValoreIdentificativoParte);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConMittenteSenzaValoreIdentificativoParte")
	public Object[][] testEGovConMittenteSenzaValoreIdentificativoParte()throws Exception{
		String id=this.repositoryMittenteSenzaValoreIdentificativoParte.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MittenteSenzaValoreIdentificativoParte"},
			dataProvider="EGovConMittenteSenzaValoreIdentificativoParte",
			dependsOnMethods="EGovConMittenteSenzaValoreIdentificativoParte")
			public void testDBEGovConMittenteSenzaValoreIdentificativoParte(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteSenzaValoreIdentificativoParte.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			IDSoggetto mittenteSconosciuto = this.busteEGovErrate.getMittente(index);
			mittenteSconosciuto.setNome(Utilities.testSuiteProperties.getKeywordMittenteSconosciuto());
			mittenteSconosciuto.setCodicePorta(Utilities.testSuiteProperties.getKeywordDominioSconosciuto());
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,mittenteSconosciuto,null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,mittenteSconosciuto,null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	/**
	 * 
	 * Header e-Gov con Destinatario senza un valore nell'identificativo parte
	 * La busta ritornata possiedera':
	 * - lista eccezioni con [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte
	 * busta = buste_errate/bustaDestinatarioSenzaValoreIdentificativoParte.xml
	 * 
	 * Test equivalente al Test N.28 Della Certificazione DigitPA (Busta Errata 10)
	 */
	Repository repositoryDestinatarioSenzaValoreIdentificativoParte=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DestinatarioSenzaValoreIdentificativoParte"},
			dependsOnMethods="init")
			public void EGovConDestinatarioSenzaValoreIdentificativoParte()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioSenzaValoreIdentificativoParte.xml");
		this.repositoryDestinatarioSenzaValoreIdentificativoParte.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryDestinatarioSenzaValoreIdentificativoParte);
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
					Reporter.log("Invocazione PA con una busta con Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Mittent Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConDestinatarioSenzaValoreIdentificativoParte")
	public Object[][] testEGovConDestinatarioSenzaValoreIdentificativoParte()throws Exception{
		String id=this.repositoryDestinatarioSenzaValoreIdentificativoParte.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DestinatarioSenzaValoreIdentificativoParte"},
			dataProvider="EGovConDestinatarioSenzaValoreIdentificativoParte",
			dependsOnMethods="EGovConDestinatarioSenzaValoreIdentificativoParte")
			public void testDBEGovConDestinatarioSenzaValoreIdentificativoParte(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioSenzaValoreIdentificativoParte.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			IDSoggetto destinatarioSconosciuto = this.busteEGovErrate.getDestinatario(index);
			destinatarioSconosciuto.setNome(Utilities.testSuiteProperties.getKeywordMittenteSconosciuto());
			destinatarioSconosciuto.setCodicePorta(Utilities.testSuiteProperties.getKeywordDominioSconosciuto());
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,destinatarioSconosciuto,null));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta ("+Utilities.testSuiteProperties.getIdentitaDefault_tipo()+"/"+Utilities.testSuiteProperties.getIdentitaDefault_nome()+") con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
			Assert.assertTrue(num==1);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	

	/**
	 * 
	 * Header e-Gov con Mittente conosciuto, ma con altri mittenti
	 * La busta ritornata possiedera' una eccezione per ogni mittente ulteriore:
	 * [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte:SPC/MinisteroFruitoreX (busta con piu' di un mittente non gestita)
	 *busta = buste_errate/bustaConPiuMittenti.xml
	 * 
	 * Test equivalente al Test N.10 Della Certificazione DigitPA (Busta Errata 111)
	 *
	 */
	Repository repositoryPiuMittenti=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PM"},
			dependsOnMethods="init")
			public void EGovConPiuMittenti()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuMittenti.xml");
		this.repositoryPiuMittenti.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryPiuMittenti);
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
					Reporter.log("Invocazione PA con una busta con Piu Mittenti.");
					throw new TestSuiteException("Invocazione PA con busta con Piu Mittenti, non ha causato errori.");
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
	@DataProvider (name="EGovConPiuMittenti")
	public Object[][] testEGovConPiuMittenti()throws Exception{
		String id=this.repositoryPiuMittenti.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PM"},
			dataProvider="EGovConPiuMittenti",
			dependsOnMethods="EGovConPiuMittenti")
			public void testDBEGovConPiuMittenti(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuMittenti.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+" 3 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==3);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 3 eccezioni me lo garantisce il controllo soprastante.

			/*
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore2 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore2 (busta con piu' di un mittente non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore3 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore3 (busta con piu' di un mittente non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore4 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore4 (busta con piu' di un mittente non gestita)"));
			 */

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));


		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header e-Gov con Destinatario conosciuto, ma con altri destinatari
	 * La busta ritornata possiedera' una eccezione per ogni destinatario ulteriore:
	 * [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte:SPC/MinisteroFruitoreX (busta con piu' di un destinatario non gestita)
	 * busta = buste_errate/bustaConPiuDestinatari.xml
	 * 
	 *
	 * 
	 */
	Repository repositoryPiuDestinatari=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PD"},
			dependsOnMethods="init")
			public void EGovConPiuDestinatari()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuDestinatari.xml");
		this.repositoryPiuDestinatari.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryPiuDestinatari);
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
					Reporter.log("Invocazione PA con una busta con Piu Destinatari .");
					throw new TestSuiteException("Invocazione PA con busta con Piu Destinatari , non ha causato errori.");
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
	@DataProvider (name="EGovConPiuDestinatari")
	public Object[][] testEGovConPiuDestinatari()throws Exception{
		String id=this.repositoryPiuDestinatari.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PD"},
			dataProvider="EGovConPiuDestinatari",
			dependsOnMethods="EGovConPiuDestinatari")
			public void testDBEGovConPiuDestinatari(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuDestinatari.xml");
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+" 3 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==3);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 3 eccezioni me lo garantisce il controllo soprastante.
			/*
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore2 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore2 (busta con piu' di un destinatario non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore3 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore3 (busta con piu' di un destinatario non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore4 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore4 (busta con piu' di un destinatario non gestita)"));
			 */

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,  CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con Mittente sconosciuto e con altri mittenti
	 * La busta ritornata possiedera' una eccezione per ogni mittente ulteriore:
	 * [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte:SPC/MinisteroFruitoreX (busta con piu' di un mittente non gestita)
	 * busta = buste_errate/bustaConPiuMittenti_mittenteSconosciuto.xml
	 * 
	 * 
	 *
	 */
	Repository repositoryPiuMittenti_mittenteSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PMMS"},
			dependsOnMethods="init")
			public void EGovConPiuMittenti_mittenteSconosciuto()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuMittenti_mittenteSconosciuto.xml");
		this.repositoryPiuMittenti_mittenteSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryPiuMittenti_mittenteSconosciuto);
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
					Reporter.log("Invocazione PA con una busta con Piu Mittenti mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta Con Piu Mittenti mittente Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConPiuMittenti_mittenteSconosciuto")
	public Object[][] testEGovConPiuMittenti_mittenteSconosciuto()throws Exception{
		String id=this.repositoryPiuMittenti_mittenteSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PMMS"},
			dataProvider="EGovConPiuMittenti_mittenteSconosciuto",
			dependsOnMethods="EGovConPiuMittenti_mittenteSconosciuto")
			public void testDBEGovConPiuMittenti_mittenteSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuMittenti_mittenteSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			IDSoggetto mittente = this.busteEGovErrate.getMittente(index);
			mittente.setCodicePorta(Utilities.testSuiteProperties.getKeywordDominioSconosciuto());
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO+" 3 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==3);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 3 eccezioni me lo garantisce il controllo soprastante.

			/*Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore2 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore2 (busta con piu' di un mittente non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore3 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore3 (busta con piu' di un mittente non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore4 (busta con piu' di un mittente non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroFruitore4 (busta con piu' di un mittente non gestita)"));
			 */


			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * Header e-Gov con Destinatario conosciuto, ma con altri destinatari
	 * La busta conterra come mittente il soggetto di default della porta
	 * La busta ritornata possiedera' una eccezione per ogni destinatario ulteriore:
	 * [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte:SPC/MinisteroFruitoreX (busta con piu' di un destinatario non gestita)
	 * busta = buste_errate/bustaConPiuDestinatari_destinatarioSconosciuto.xml
	 * 
	 * 
	 *
	 */
	Repository repositoryPiuDestinatari_destinatarioSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PDDS"},
			dependsOnMethods="init")
			public void EGovConPiuDestinatari_destinatarioSconosciuto()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuDestinatari_destinatarioSconosciuto.xml");
		this.repositoryPiuDestinatari_destinatarioSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryPiuDestinatari_destinatarioSconosciuto);
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
					Reporter.log("Invocazione PA con una busta con Piu Destinatari destinatarioSconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Piu Destinatari destinatario Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConPiuDestinatari_destinatarioSconosciuto")
	public Object[][] testEGovConPiuDestinatari_destinatarioSconosciuto()throws Exception{
		String id=this.repositoryPiuDestinatari_destinatarioSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PDDS"},
			dataProvider="EGovConPiuDestinatari_destinatarioSconosciuto",
			dependsOnMethods="EGovConPiuDestinatari_destinatarioSconosciuto")
			public void testDBEGovConPiuDestinatari_destinatarioSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaConPiuDestinatari_destinatarioSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setCodicePorta(Utilities.testSuiteProperties.getKeywordDominioSconosciuto());
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,destinatario,this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_tipo(), Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),null));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO+" 3 volte");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==3);

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 3 eccezioni me lo garantisce il controllo soprastante.

			/*
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore2 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore2 (busta con piu' di un destinatario non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore3 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore3 (busta con piu' di un destinatario non gestita)"));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore4 (busta con piu' di un destinatario non gestita)");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerAlmenoUnaEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, Costanti.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE+":SPC/MinisteroErogatore4 (busta con piu' di un destinatario non gestita)"));
			 */

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header e-Gov senza tipo Mittente
	 * La busta possiedera' come destinatario ?/?
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte/tipo
	 * busta = buste_errate/bustaTipoMittenteNonPresente.xml
	 * 
	 * Test equivalente al Test N.9 Della Certificazione DigitPA (Busta Errata 4)
	 */
	Repository repositoryTipoMittenteNonPresente=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TMNP"},
			dependsOnMethods="init")
			public void EGovTipoMittenteNonPresente()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoMittenteNonPresente.xml");
		this.repositoryTipoMittenteNonPresente.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);

		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoMittenteNonPresente);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con Tipo Mittente Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Tipo Mittente Non Presente, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Mittente/IdentificativoParte/tipo");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovTipoMittenteNonPresente")
	public Object[][] testEGovTipoMittenteNonPresente()throws Exception{
		String id=this.repositoryTipoMittenteNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TMNP"},
			dataProvider="EGovTipoMittenteNonPresente",
			dependsOnMethods="EGovTipoMittenteNonPresente")
			public void testDBEGovTipoMittenteNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoMittenteNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), "?"));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index), "?"));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

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
	 * Header e-Gov senza tipo Destinatario
	 * La busta possiedera' come mittente il mittente di default della porta
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte/tipo
	 * busta = buste_errate/bustaTipoDestinatarioNonPresente.xml
	 * 
	 * Test equivalente al Test N.25 Della Certificazione DigitPA (Busta Errata 8)
	 */
	Repository repositoryTipoDestinatarioNonPresente=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TDNP"},
			dependsOnMethods="init")
			public void EGovTipoDestinatarioNonPresente()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoDestinatarioNonPresente.xml");
		this.repositoryTipoDestinatarioNonPresente.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);

		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoDestinatarioNonPresente);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con Senza Tipo Destinatario.");
					throw new TestSuiteException("Invocazione PA con busta con Senza Tipo Destinatario, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Destinatario/IdentificativoParte/tipo");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovTipoDestinatarioNonPresente")
	public Object[][] testEGovTipoDestinatarioNonPresente()throws Exception{
		String id=this.repositoryTipoDestinatarioNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TDNP"},
			dataProvider="EGovTipoDestinatarioNonPresente",
			dependsOnMethods="EGovTipoDestinatarioNonPresente")
			public void testDBEGovTipoDestinatarioNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoDestinatarioNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),"?"));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
				Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,new IDSoggetto(Utilities.testSuiteProperties.getIdentitaDefault_nome(),Utilities.testSuiteProperties.getIdentitaDefault_tipo(),Utilities.testSuiteProperties.getIdentitaDefault_dominio()),null));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
				Assert.assertTrue(num==1);
				
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

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
	 * Header e-Gov con tipo Mittente sconosciuto
	 * La busta possiedera' come tipo destinatario il destinatario di default della porta
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte/tipo
	 * busta = buste_errate/bustaTipoMittenteSconosciuto.xml
	 * 
	 * Test equivalente al Test N.4 Della Certificazione DigitPA (Busta Errata 3)
	 */
	Repository repositoryTipoMittenteSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TMS"},
			dependsOnMethods="init")
			public void EGovTipoMittenteSconosciuto()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoMittenteSconosciuto.xml");
		this.repositoryTipoMittenteSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);

		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoMittenteSconosciuto);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con Tipo Mittente Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Tipo Mittente Sconosciuto, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Riconoscimento profilo di gestione non riuscito");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovTipoMittenteSconosciuto")
	public Object[][] testEGovTipoMittenteSconosciuto()throws Exception{
		String id=this.repositoryTipoMittenteSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TMS"},
			dataProvider="EGovTipoMittenteSconosciuto",
			dependsOnMethods="EGovTipoMittenteSconosciuto")
			public void testDBEGovTipoMittenteSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoMittenteSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			IDSoggetto mittente = this.busteEGovErrate.getMittente(index);
			mittente.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,mittente, this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con tipo Destinatario sconosciuto
	 * La busta possiedera' come mittente il mittente di default della porta
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte/tipo
	 * busta = buste_errate/bustaTipoDestinatarioSconosciuto.xml
	 * 
	 * Test equivalente al Test N.3 Della Certificazione DigitPA (Busta Errata 7)
	 */
	Repository repositoryTipoDestinatarioSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TDS"},
			dependsOnMethods="init")
			public void EGovTipoDestinatarioSconosciuto()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoDestinatarioSconosciuto.xml");
		this.repositoryTipoDestinatarioSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoDestinatarioSconosciuto);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta Tipo Destinatario Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta Tipo Destinatario Sconosciuto, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
	}
	@DataProvider (name="EGovTipoDestinatarioSconosciuto")
	public Object[][] testEGovTipoDestinatarioSconosciuto()throws Exception{
		String id=this.repositoryTipoDestinatarioSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TDS"},
			dataProvider="EGovTipoDestinatarioSconosciuto",
			dependsOnMethods="EGovTipoDestinatarioSconosciuto")
			public void testDBEGovTipoDestinatarioSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoDestinatarioSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,destinatario, this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,destinatario, this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
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
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	/**
	 * 	 
	 * Header e-Gov con profilo di collaborazione non presente
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione
	 * busta = buste_errate/bustaProfiloDiCollaborazioneNonPresente.xml
	 * 
	 */
	Repository repositoryProfiloDiCollaborazioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCNP"},
			dependsOnMethods="init")
			public void EGovConProfiloDiCollaborazioneNonPresente()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloDiCollaborazioneNonPresente.xml");
		this.repositoryProfiloDiCollaborazioneNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloDiCollaborazioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Profilo Di Collaborazione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Profilo Di Collaborazione Non Presente, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloDiCollaborazioneNonPresente")
	public Object[][] testEGovConProfiloDiCollaborazioneNonPresente()throws Exception{
		String id=this.repositoryProfiloDiCollaborazioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCNP"},
			dataProvider="EGovConProfiloDiCollaborazioneNonPresente",
			dependsOnMethods="EGovConProfiloDiCollaborazioneNonPresente")
			public void testDBEGovConProfiloDiCollaborazioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloDiCollaborazioneNonPresente.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 *
	 * Header e-Gov con profilo di collaborazione con valore scorretto
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione
	 * busta = buste_errate/bustaProfiloDiCollaborazioneNonValido.xml
	 * 
	 * Test equivalente al Test N.29 Della Certificazione DigitPA (Busta Errata 12)
	 */
	Repository repositoryProfiloDiCollaborazioneNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCNV"},
			dependsOnMethods="init")
			public void EGovConProfiloDiCollaborazioneNonValido()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloDiCollaborazioneNonValido.xml");
		this.repositoryProfiloDiCollaborazioneNonValido.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloDiCollaborazioneNonValido);
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
					Reporter.log("Invocazione PA con una busta con Profilo Di Collaborazione Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Profilo Di Collaborazione Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloDiCollaborazioneNonValido")
	public Object[][] testEGovConProfiloDiCollaborazioneNonValido()throws Exception{
		String id=this.repositoryProfiloDiCollaborazioneNonValido.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCNV"},
			dataProvider="EGovConProfiloDiCollaborazioneNonValido",
			dependsOnMethods="EGovConProfiloDiCollaborazioneNonValido")
			public void testDBEGovConProfiloDiCollaborazioneNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloDiCollaborazioneNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
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
			if(Utilities.testSuiteProperties.isGenerazioneElementiXSDNonValidabili()){
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			}else{
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				boolean check1 = data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, null, null);
				boolean check2 = data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), null);
				Reporter.log("Controllo valore Profilo di Collaborazione Busta check1["+check1+"] check2["+check2+"] con id: " +id);
				Assert.assertTrue(check1 || check2);
			}
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 *
	 * Header e-Gov con profilo di collaborazione, con tipo (servizio correlato) non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione/tipo
	 * busta = buste_errate/bustaProfiloCollaborazione_tipoServizioCorrelatoNonValido.xml
	 * 
	 * Test equivalente al Test N.8 Della Certificazione DigitPA (Busta Errata 15)
	 */
	Repository repositoryProfiloDiCollaborazioneTipoServizioCorrelatoNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCTSCNV"},
			dependsOnMethods="init")
			public void EGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloCollaborazione_tipoServizioCorrelatoNonValido.xml");
		this.repositoryProfiloDiCollaborazioneTipoServizioCorrelatoNonValido.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloDiCollaborazioneNonValido);
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
					Reporter.log("Invocazione PA con una busta con Profilo Di Collaborazione TipoServizioCorrelato Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Profilo Di Collaborazione TipoServizioCorrelato Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido")
	public Object[][] testEGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido()throws Exception{
		String id=this.repositoryProfiloDiCollaborazioneTipoServizioCorrelatoNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCTSCNV"},
			dataProvider="EGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido",
			dependsOnMethods="EGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido")
			public void testDBEGovConProfiloDiCollaborazioneTipoServizioCorrelatoNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloCollaborazione_tipoServizioCorrelatoNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			DatiServizio datiServizioCorrelato = this.busteEGovErrate.getDatiServizioCorrelatoProfiloCollaborazione(index);
			Reporter.log("Controllo valore Profilo di Collaborazione ["+datiServizioCorrelato.getTipoServizio()+"] ["+datiServizioCorrelato.getNomeServizio()+"], attributi servizi correlati Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizioCorrelato(id, datiServizioCorrelato));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			datiServizioCorrelato = this.busteEGovErrate.getDatiServizioCorrelatoProfiloCollaborazione(index);
			Reporter.log("Controllo valore Profilo di Collaborazione ["+datiServizioCorrelato.getTipoServizio()+"] ["+datiServizioCorrelato.getNomeServizio()+"], attributi servizi correlati Busta con id: " +id);
			if(Utilities.testSuiteProperties.isGenerazioneElementiXSDNonValidabili()){
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, datiServizioCorrelato));
			}else{
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizioCorrelato(id, null));
			}
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE_TIPO_SERVIZIO_CORRELATO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * Header e-Gov con profilo di collaborazione diverso da quello registrato sul registro dei servizi
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_103] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloCollaborazione  diverso da quanto specificato nel registro
	 * busta = buste_errate/bustaProfiloCollaborazioneDiversoRegistrato.xml
	 * 
	 * Devi abilitare l'elemento validazione-buste-egov ed in particolar modo devi abilitare il profiloCollaborazione.
	 * <validazione-buste-egov stato="abilitato" controllo="rigido"
	 *                                  profiloCollaborazione="abilitato"
	 *                                  manifestAttachments="abilitato" />
	 * 
	 */
	Repository repositoryProfiloDiCollaborazioneDiversoRegistrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCDR"},
			dependsOnMethods="init")
			public void EGovConProfiloDiCollaborazioneDiversoRegistrato()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloCollaborazioneDiversoRegistrato.xml");
		this.repositoryProfiloDiCollaborazioneDiversoRegistrato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloDiCollaborazioneDiversoRegistrato);
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
					Reporter.log("Invocazione PA con una busta con Profilo Di Collaborazione con Diverso Registrato.");
					throw new TestSuiteException("Invocazione PA con busta con Profilo Di Collaborazione con Diverso Registrato, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloDiCollaborazioneDiversoRegistrato")
	public Object[][] testEGovConProfiloDiCollaborazioneDiversoRegistrato()throws Exception{
		String id=this.repositoryProfiloDiCollaborazioneDiversoRegistrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PCDR"},
			dataProvider="EGovConProfiloDiCollaborazioneDiversoRegistrato",
			dependsOnMethods="EGovConProfiloDiCollaborazioneDiversoRegistrato")
			public void testDBEGovConProfiloDiCollaborazioneDiversoRegistrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloCollaborazioneDiversoRegistrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
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
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.PROFILO_COLLABORAZIONE_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE.toString()+" diverso da quanto specificato nel registro");
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_COLLABORAZIONE_SCONOSCIUTO_POSIZIONE+" diverso da quanto specificato nel registro"));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 *
	 * Header e-Gov con collaborazione non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_104] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Collaborazione
	 * busta = buste_errate/bustaCollaborazioneNonValida.xml
	 *
	 */
	Repository repositoryCollaborazioneNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CNV"},
			dependsOnMethods="init")
			public void EGovConCollaborazioneNonValida()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaCollaborazioneNonValida.xml");
		this.repositoryCollaborazioneNonValida.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);


		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryCollaborazioneNonValida);
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
					Reporter.log("Invocazione PA con una busta con Collaborazione Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Collaborazione Non Valida, non ha causato errori.");
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
	@DataProvider (name="EGovConCollaborazioneNonValida")
	public Object[][] testEGovConCollaborazioneNonValida()throws Exception{
		String id=this.repositoryCollaborazioneNonValida.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".CNV"},
			dataProvider="EGovConCollaborazioneNonValida",
			dependsOnMethods="EGovConCollaborazioneNonValida")
			public void testDBEGovConCollaborazioneNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaCollaborazioneNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedCollaborazione(id, this.busteEGovErrate.getCollaborazione(index)));


			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo valore Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedCollaborazione(id, this.busteEGovErrate.getCollaborazione(index)));

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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_COLLABORAZIONE_SCONOSCIUTA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header e-Gov senza servizio
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio
	 * busta = buste_errate/bustaSenzaServizio.xml
	 * 
	 */
	Repository repositorySenzaServizio=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SSe"},
			dependsOnMethods="init")
			public void EGovSenzaServizio()throws TestSuiteException, SOAPException, Exception{	
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaServizio.xml");
		this.repositorySenzaServizio.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaServizio);
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
					Reporter.log("Invocazione PA con una busta Senza Servizio.");
					throw new TestSuiteException("Invocazione PA con busta Senza Servizio, non ha causato errori.");
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
	@DataProvider (name="EGovSenzaServizio")
	public Object[][] testEGovSenzaServizio()throws Exception{
		String id=this.repositorySenzaServizio.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SSe"},
			dataProvider="EGovSenzaServizio",
			dependsOnMethods="EGovSenzaServizio")
			public void testDBEGovSenzaServizio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaServizio.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 * 
	 * Header e-Gov con servizio sconosciuto
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio
	 * busta = buste_errate/bustaServizioSconosciuto.xml
	 * 
	 * Test equivalente al Test N.21 Della Certificazione DigitPA (Busta Errata 19)
	 */
	Repository repositoryServizioSconosciuto=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SS"},
			dependsOnMethods="init")
			public void EGovConServizioSconosciuto()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaServizioSconosciuto.xml");
		this.repositoryServizioSconosciuto.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryServizioSconosciuto);
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
					Reporter.log("Invocazione PA con una busta con Servizio Sconosciuto.");
					throw new TestSuiteException("Invocazione PA con busta con Servizio Sconosciuto, non ha causato errori.");
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
	@DataProvider (name="EGovConServizioSconosciuto")
	public Object[][] testEGovConServizioSconosciutao()throws Exception{
		String id=this.repositoryServizioSconosciuto.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SS"},
			dataProvider="EGovConServizioSconosciuto",
			dependsOnMethods="EGovConServizioSconosciuto")
			public void testDBEGovConServizioSconosciuto(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaServizioSconosciuto.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con tipo servizio non presente
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio/tipo
	 * busta = buste_errate/bustaTipoServizioNonPresente.xml
	 * 
	 * Test equivalente al Test N.20 Della Certificazione DigitPA (Busta Errata 20)
	 */
	Repository repositoryTipoServizioNonPresente=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TSNP"},
			dependsOnMethods="init")
			public void EGovConTipoServizioNonPresente()throws TestSuiteException, SOAPException, Exception{	
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoServizioNonPresente.xml");
		this.repositoryTipoServizioNonPresente.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoServizioNonPresente);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta con Tipo Servizio Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Tipo Servizio Non Presente, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
		
		Date dataFineTest = DateManager.getDate();
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Struttura della busta non corretta: Header egov con elemento Servizio senza tipo");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConTipoServizioNonPresente")
	public Object[][] testEGovConTipoServizioNonPresente()throws Exception{
		String id=this.repositoryTipoServizioNonPresente.getNext();
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
				{DatabaseProperties.getDatabaseComponentErogatore(),id}//,
				//{DatabaseProperties.getDatabaseComponentFruitore(),id}
		};
	}
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".TSNP"},
			dataProvider="EGovConTipoServizioNonPresente",
			dependsOnMethods="EGovConTipoServizioNonPresente")
			public void testDBEGovConTipoServizioNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoServizioNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
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
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.SERVIZIO_SCONOSCIUTO));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));
	
				// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString()));

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
	 * Header e-Gov con tipo servizio non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_105] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Servizio/tipo
	 * busta = buste_errate/bustaTipoServizioErrato.xml
	 * 
	 * Test equivalente al Test N.7 Della Certificazione DigitPA (Busta Errata 21)
	 */
	Repository repositoryTipoServizioErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TSE"},
			dependsOnMethods="init")
			public void EGovConTipoServizioErrato()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoServizioErrato.xml");
		this.repositoryTipoServizioErrato.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);


		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoServizioErrato);
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
					Reporter.log("Invocazione PA con una busta con Tipo Servizio Errato.");
					throw new TestSuiteException("Invocazione PA con busta con Tipo Servizio Errato, non ha causato errori.");
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
	@DataProvider (name="EGovConTipoServizioErrato")
	public Object[][] testEGovConTipoServizioErrato()throws Exception{
		String id=this.repositoryTipoServizioErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TSE"},
			dataProvider="EGovConTipoServizioErrato",
			dependsOnMethods="EGovConTipoServizioErrato")
			public void testDBEGovConTipoServizioErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoServizioErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SERVIZIO_SCONOSCIUTO_POSIZIONE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * * Header e-Gov con azione non presente, e accordo che non permette l'invocazione senza azione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_106] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Azione
	 * busta = buste_errate/bustaSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione.xml
	 *
	 */
	Repository repositorySenzaAzione_servizioNonPermetteInvocazioneSenzaAzione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SA"},
			dependsOnMethods="init")
			public void EGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione.xml");
		this.repositorySenzaAzione_servizioNonPermetteInvocazioneSenzaAzione.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaAzione_servizioNonPermetteInvocazioneSenzaAzione);
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
					Reporter.log("Invocazione PA con una busta Senza Azione dove il servizio Non Permette Invocazione Senza Azione.");
					throw new TestSuiteException("Invocazione PA con busta Senza Azione dove servizio Non Permette Invocazione Senza Azione, non ha causato errori.");
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
	@DataProvider (name="EGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione")
	public Object[][] testEGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione()throws Exception{
		String id=this.repositorySenzaAzione_servizioNonPermetteInvocazioneSenzaAzione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SA"},
			dataProvider="EGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione",
			dependsOnMethods="EGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione")
			public void testDBEGovSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaAzione_servizioNonPermetteInvocazioneSenzaAzione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.AZIONE_SCONOSCIUTA+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==1);
			//}else{
			//Assert.assertTrue(num==2);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.AZIONE_SCONOSCIUTA);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.AZIONE_SCONOSCIUTA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/** 
	 * 
	 * Header e-Gov con azione sconosciuta
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_106] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Azione
	 * busta = buste_errate/bustaAzioneSconosciuta.xml
	 * 
	 * Test equivalente al Test N.23 Della Certificazione DigitPA (Busta Errata 23)
	 */
	Repository repositoryAzioneSconosciuta=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".AS"},
			dependsOnMethods="init")
			public void EGovConAzioneSconosciuta()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaAzioneSconosciuta.xml");
		this.repositoryAzioneSconosciuta.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryAzioneSconosciuta);
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
					Reporter.log("Invocazione PA con una busta con Azione Sconosciuta.");
					throw new TestSuiteException("Invocazione PA con busta con Azione Sconosciuta, non ha causato errori.");
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
	@DataProvider (name="EGovConAzioneSconosciuta")
	public Object[][] testEGovConAzioneSconosciuta()throws Exception{
		String id=this.repositoryAzioneSconosciuta.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".AS"},
			dataProvider="EGovConAzioneSconosciuta",
			dependsOnMethods="EGovConAzioneSconosciuta")
			public void testDBEGovConAzioneSconosciuta(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaAzioneSconosciuta.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));			
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_AZIONE_SCONOSCIUTA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header e-Gov senza Messaggio
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_002] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio
	 * busta = buste_errate/bustaSenzaMessaggio.xml
	 * 
	 */
	Repository repositorySenzaMessaggio=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SMe"},
			dependsOnMethods="init")
			public void EGovSenzaMessaggio()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMessaggio.xml");
		this.repositorySenzaMessaggio.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaMessaggio);
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
					Reporter.log("Invocazione PA con una busta Senza Messaggio.");
					throw new TestSuiteException("Invocazione PA con busta Senza Messaggio, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Messaggio");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaMessaggio")
	public Object[][] testEGovSenzaMessaggio()throws Exception{
		String id=this.repositorySenzaMessaggio.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SMe"},
			dataProvider="EGovSenzaMessaggio",
			dependsOnMethods="EGovSenzaMessaggio")
			public void testDBEGovSenzaMessaggio(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaMessaggio.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO+"], " +
						"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
						this.busteEGovErrate.getDestinatario(index),
						this.busteEGovErrate.getMittente(index),
						CodiceErroreCooperazione.FORMATO_INTESTAZIONE_NON_CORRETTO,
						SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_FORMATO_INTESTAZIONE_NON_CORRETTO_POSIZIONE_MESSAGGIO.toString()));
				
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
	 * Header e-Gov con scadenza non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_112] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Scadenza
	 * busta = buste_errate/bustaScadenzaNonValida.xml
	 * 
	 * Test equivalente al Test N.33 Della Certificazione DigitPA (Busta Errata 25)
	 */
	Repository repositoryScadenzaNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SNV"},
			dependsOnMethods="init")
			public void EGovConScadenzaNonValida()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaScadenzaNonValida.xml");

		this.repositoryScadenzaNonValida.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryScadenzaNonValida);
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
					Reporter.log("Invocazione PA con una busta con Scadenza Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Scadenza Non Valida, non ha causato errori.");
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
	@DataProvider (name="EGovConScadenzaNonValida")
	public Object[][] testEGovConScadenzaNonValida()throws Exception{
		String id=this.repositoryScadenzaNonValida.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SNV"},
			dataProvider="EGovConScadenzaNonValida",
			dependsOnMethods="EGovConScadenzaNonValida")
			public void testDBEGovConScadenzaNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaScadenzaNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SCADENZA_NON_VALIDA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con scadenza 'scaduta'
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_301] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Scadenza
	 * busta = buste_errate/bustaScaduta.xml
	 * 
	 * Test equivalente al Test N.22 Della Certificazione DigitPA (Busta Errata 26)
	 */
	Repository repositoryScaduta=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".S"},
			dependsOnMethods="init")
			public void EGovScaduta()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaScaduta.xml");
		this.repositoryScaduta.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryScaduta);
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
					Reporter.log("Invocazione PA con una busta Scaduta.");
					throw new TestSuiteException("Invocazione PA con busta Scaduta, non ha causato errori.");
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
	@DataProvider (name="EGovScaduta")
	public Object[][] testEGovScaduta()throws Exception{
		String id=this.repositoryScaduta.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".S"},
			dataProvider="EGovScaduta",
			dependsOnMethods="EGovScaduta")
			public void testDBEGovScaduta(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaScaduta.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.MESSAGGIO_SCADUTO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.MESSAGGIO_SCADUTO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
				Assert.assertTrue(num==1);
			}else{
				Assert.assertTrue(num==2);
			}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.MESSAGGIO_SCADUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.MESSAGGIO_SCADUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MESSAGGIO_SCADUTO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,  CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MESSAGGIO_SCADUTO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * Header e-Gov senza Identificatore
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_107] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_errate/bustaSenzaIdentificatore.xml
	 * 
	 * Test equivalente al Test N.16 Della Certificazione DigitPA (Busta Errata 27)
	 */
	Repository repositorySenzaIdentificatore=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SI"},
			dependsOnMethods="init")
			public void EGovSenzaIdentificatore()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificatore.xml");
		this.repositorySenzaIdentificatore.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaIdentificatore);
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
					Reporter.log("Invocazione PA con una busta Senza Identificatore.");
					throw new TestSuiteException("Invocazione PA con busta Senza Identificatore, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov con struttura errata, Messaggio/Identificatore");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovSenzaIdentificatore")
	public Object[][] testEGovSenzaIdentificatore()throws Exception{
		String id=this.repositorySenzaIdentificatore.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".SI"},
			dataProvider="EGovSenzaIdentificatore",
			dependsOnMethods="EGovSenzaIdentificatore")
			public void testDBEGovSenzaIdentificatore(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaIdentificatore.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo L'eccezione, con codice["+CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE+"], " +
						"contesto["+SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE+"] e posizione["+SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE+"]");
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(this.busteEGovErrate.getMinGDO(index),
						this.busteEGovErrate.getDestinatario(index), 
						this.busteEGovErrate.getMittente(index),
						CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE,
						SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,
						SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_DEFINITO_POSIZIONE.toString()));
				
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
	 * Header e-Gov con Identificatore non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_110] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_errate/bustaIdentificatoreNonValido.xml
	 *
	 * Test equivalente al Test N.26 Della Certificazione DigitPA (Busta Errata 28)
	 */
	Repository repositoryIdentificatoreNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".INV"},
			dependsOnMethods="init")
			public void EGovConIdentificatoreNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreNonValido.xml");
		this.repositoryIdentificatoreNonValido.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIdentificatoreNonValido);
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
					Reporter.log("Invocazione PA con una busta con Identificatore Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Identificatore Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConIdentificatoreNonValido")
	public Object[][] testEGovConIdentificatoreNonValido()throws Exception{
		String id=this.repositoryIdentificatoreNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".INV"},
			dataProvider="EGovConIdentificatoreNonValido",
			dependsOnMethods="EGovConIdentificatoreNonValido")
			public void testDBEGovConIdentificatoreNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato eccezioni, id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().existsListaEccezioni(id));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			//if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
			Assert.assertTrue(num==1);
			//}else{
			//	Assert.assertTrue(num==2);
			//}

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	/**
	 *
	 * Header e-Gov con Identificatore Duplicati (uso servizio con filtro duplicati)
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_110] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_errate/bustaIdentificatoreDuplicato.xml
	 *
	 */
	Repository repositoryIdentificatoreDuplicato=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".ID"},
			dependsOnMethods="init")
			public void EGovConIdentificatoreDuplicato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreDuplicato.xml");

		this.repositoryIdentificatoreDuplicato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());		
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim())==false);
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString())==false);

		}catch(NullPointerException e){}


		try{
			Thread.sleep(5000);
		}catch(Exception e){}

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));

		}catch(NullPointerException e){}

	}
	private void runClient(Message msg)throws SOAPException, Exception{
		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryIdentificatoreDuplicato);
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
	@DataProvider (name="EGovConIdentificatoreDuplicato")
	public Object[][] testEGovConIdentificatoreDuplicato()throws Exception{
		String id=this.repositoryIdentificatoreDuplicato.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{
				Thread.sleep(3000);
			}catch(InterruptedException e){}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".ID"},
			dataProvider="EGovConIdentificatoreDuplicato",
			dependsOnMethods="EGovConIdentificatoreDuplicato")
			public void testDBEGovConIdentificatoreDuplicato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreDuplicato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			// Il fatto che al servizio applicativo sia arrivata una sola copia, mi garantisce il buon funzionamento del filtro duplicati
			Reporter.log("Numero duplicati: "+data.getVerificatoreTracciaRichiesta().countTracce(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().countTracce(id)==2);

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 *
	 * Header e-Gov con Identificatore Duplicati (uso servizio con filtro duplicati)
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_110] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/Identificatore
	 * busta = buste_errate/bustaIdentificatoreDuplicato_ServizioSincrono.xml
	 * 
	 */

	Repository repositoryIdentificatoreDuplicato_ServizioSincrono=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".IDSS"},
			dependsOnMethods="init")
			public void EGovConIdentificatoreDuplicato_ServizioSincrono()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreDuplicato_ServizioSincrono.xml");

		this.repositoryIdentificatoreDuplicato_ServizioSincrono.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());		
		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim())==false);
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString())==false);

		}catch(NullPointerException e){}


		try{
			Thread.sleep(5000);
		}catch(Exception e){}

		try {
			runClient(msg);
			Reporter.log("Invocazione PA con una busta con Identificatore Duplicato.");

		} catch (AxisFault error) {
			Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
			Reporter.log("Controllo fault code [Client]");
			Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
			Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
			Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));

		}catch(NullPointerException e){}
	}
	@DataProvider (name="EGovConIdentificatoreDuplicato_ServizioSincrono")
	public Object[][] testEGovConIdentificatoreDuplicato_ServizioSincrono()throws Exception{
		String id=this.repositoryIdentificatoreDuplicato_ServizioSincrono.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Attendo tempo perso per la nuova connessione
		if(Utilities.testSuiteProperties.isNewConnectionForResponse()){
			try{Thread.sleep(3000);}catch(InterruptedException e){}
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".IDSS"},
			dataProvider="EGovConIdentificatoreDuplicato_ServizioSincrono",
			dependsOnMethods="EGovConIdentificatoreDuplicato_ServizioSincrono")
			public void testDBEGovConIdentificatoreDuplicato_ServizioSincrono(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaIdentificatoreDuplicato_ServizioSincrono.xml");
		try{
			Reporter.log("Aspettiamo: " +id);
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Numero messaggi arrivati al servizio applicativo: "+data.getVerificatoreTracciaRichiesta().isArrivedCount(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isArrivedCount(id)==1);
			// Il fatto che al servizio applicativo sia arrivata una sola copia, mi garantisce il buon funzionamento del filtro duplicati
			Reporter.log("Numero duplicati: "+data.getVerificatoreTracciaRichiesta().countTracce(id));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().countTracce(id)==2);


			Reporter.log("-------------- RISPOSTA --------------------");

			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));

			Reporter.log("Controllo che la busta abbia generato l'eccezione " + CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO+" 1 volta");
			int num = data.getVerificatoreTracciaRisposta().countEccezioniTraced(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,false);
			Reporter.log("Check ha trovato eccezioni num:"+num);
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE,true,false));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE,true,false));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedEccezione(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ID_MESSAGGIO_NON_VALIDO_POSIZIONE.toString(),true,false));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}










	/**
	 * 
	 * Header e-Gov senza OraRegistrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione
	 * busta = buste_errate/bustaSenzaOraRegistrazione.xml
	 * 
	 */
	Repository repositorySenzaOraRegistrazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SOR"},
			dependsOnMethods="init")
			public void EGovSenzaOraRegistrazione()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaOraRegistrazione.xml");
		this.repositorySenzaOraRegistrazione.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySenzaOraRegistrazione);
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
					Reporter.log("Invocazione PA con una busta Senza Ora Registrazione.");
					throw new TestSuiteException("Invocazione PA con busta Senza Ora Registrazione, non ha causato errori.");
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
	@DataProvider (name="EGovSenzaOraRegistrazione")
	public Object[][] testEGovSenzaOraRegistrazione()throws Exception{
		String id=this.repositorySenzaOraRegistrazione.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SOR"},
			dataProvider="EGovSenzaOraRegistrazione",
			dependsOnMethods="EGovSenzaOraRegistrazione")
			public void testDBEGovSenzaOraRegistrazione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaOraRegistrazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id)==false);
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 *
	 * Header e-Gov con OraRegistrazione non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione
	 * busta = buste_errate/bustaOraRegistrazioneNonValida.xml
	 * 
	 * Test equivalente al Test N.31 Della Certificazione DigitPA (Busta Errata 31)
	 */
	Repository repositoryOraRegistrazioneNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ORNV"},
			dependsOnMethods="init")
			public void EGovConOraRegistrazioneNonValida()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaOraRegistrazioneNonValida.xml");
		this.repositoryOraRegistrazioneNonValida.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryOraRegistrazioneNonValida);
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
					Reporter.log("Invocazione PA con una busta con Ora Registrazione Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Ora Registrazione Non Valida, non ha causato errori.");
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
	@DataProvider (name="EGovConOraRegistrazioneNonValida")
	public Object[][] testEGovConOraRegistrazioneNonValida()throws Exception{
		String id=this.repositoryOraRegistrazioneNonValida.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".ORNV"},
			dataProvider="EGovConOraRegistrazioneNonValida",
			dependsOnMethods="EGovConOraRegistrazioneNonValida")
			public void testDBEGovConOraRegistrazioneNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaOraRegistrazioneNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id)==false);
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	/**
	 *
	 * Header e-Gov senza tipo OraRegistrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione/tempo
	 * busta = buste_errate/bustaSenzaTipoOraRegistrazione.xml
	 * 
	 */
	Repository repositoryTipoOraRegistrazioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORNonPresente"},
			dependsOnMethods="init")
			public void EGovConTipoOraRegistrazioneNonPresente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaTipoOraRegistrazione.xml");
		this.repositoryTipoOraRegistrazioneNonPresente.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoOraRegistrazioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Ora Registrazione Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Ora Registrazione Non Valida, non ha causato errori.");
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
	@DataProvider (name="EGovConTipoOraRegistrazioneNonPresente")
	public Object[][] testEGovConTipoOraRegistrazioneNonPresente()throws Exception{
		String id=this.repositoryTipoOraRegistrazioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORNonPresente"},
			dataProvider="EGovConTipoOraRegistrazioneNonPresente",
			dependsOnMethods="EGovConTipoOraRegistrazioneNonPresente")
			public void testDBEGovConTipoOraRegistrazioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSenzaTipoOraRegistrazione.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id)==false);
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 *
	 * Header e-Gov con tipo OraRegistrazione errato
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_108] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Messaggio/OraRegistrazione/tempo
	 * busta = buste_errate/bustaTipoOraRegistrazioneNonValida.xml
	 * 
	 * Test equivalente al Test N.5 Della Certificazione DigitPA (Busta Errata 33)
	 */
	Repository repositoryTipoOraRegistrazioneNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORNonValido"},
			dependsOnMethods="init")
			public void EGovConTipoOraRegistrazioneNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoOraRegistrazioneNonValida.xml");
		this.repositoryTipoOraRegistrazioneNonValido.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryTipoOraRegistrazioneNonValido);
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
					Reporter.log("Invocazione PA con una busta con Ora Registrazione Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Ora Registrazione Non Valida, non ha causato errori.");
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
	@DataProvider (name="EGovConTipoOraRegistrazioneNonValido")
	public Object[][] testEGovConTipoOraRegistrazioneNonValido()throws Exception{
		String id=this.repositoryTipoOraRegistrazioneNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".TORNonValido"},
			dataProvider="EGovConTipoOraRegistrazioneNonValido",
			dependsOnMethods="EGovConTipoOraRegistrazioneNonValido")
			public void testDBEGovConTipoOraRegistrazioneNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaTipoOraRegistrazioneNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id)==false);
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_ORA_REGISTRAZIONE_NON_VALIDA_POSIZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}
	
	
	
	
	
	
	
	









	/**
	 * 
	 * Header e-Gov con ProfiloTramissione/inoltro non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/inoltro
	 * busta = buste_errate/bustaProfiloTramissioneInoltroNonValido.xml
	 * 
	 */
	Repository repositoryProfiloTramissioneInoltroNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PTINV"},
			dependsOnMethods="init")
			public void EGovConProfiloTramissioneInoltroNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloTramissioneInoltroNonValido.xml");

		this.repositoryProfiloTramissioneInoltroNonValido.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloTramissioneInoltroNonValido);
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
					Reporter.log("Invocazione PA con una busta con Profilo Tramissione Inoltro Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con ProfiloTramissione Inoltro Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloTramissioneInoltroNonValido")
	public Object[][] testEGovConProfiloTramissioneInoltroNonValido()throws Exception{
		String id=this.repositoryProfiloTramissioneInoltroNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PTINV"},
			dataProvider="EGovConProfiloTramissioneInoltroNonValido",
			dependsOnMethods="EGovConProfiloTramissioneInoltroNonValido")
			public void testDBEGovConProfiloTramissioneInoltroNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloTramissioneInoltroNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
			Reporter.log("Controllo valore Profilo di Trasmissione Busta con id: " +id);
			// Versione 1.0
			boolean check10 = data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					SPCoopCostanti.PROFILO_TRASMISSIONE_CON_DUPLICATI,Inoltro.CON_DUPLICATI);
			// Versione 1.1	
			boolean check11 = false;
			if(Utilities.testSuiteProperties.isGenerazioneElementiXSDNonValidabili()){
				check11 = data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index));
			}else{
				check11 = data.getVerificatoreTracciaRisposta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						null,null);
			}
			Assert.assertTrue((check10 || check11));

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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_INOLTRO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con ProfiloTramissione/inoltro non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_113] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ProfiloTrasmissione/confermaRicezione
	 * busta = buste_errate/bustaProfiloTramissioneConfermaRicezioneNonValido.xml
	 * 
	 */
	Repository repositoryProfiloTramissioneConfermaRicezioneNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PTCRNV"},
			dependsOnMethods="init")
			public void EGovConProfiloTramissioneConfermaRicezioneNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloTramissioneConfermaRicezioneNonValido.xml");
		this.repositoryProfiloTramissioneConfermaRicezioneNonValido.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryProfiloTramissioneConfermaRicezioneNonValido);
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
					Reporter.log("Invocazione PA con una busta con Profilo Tramissione Conferma Ricezione Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Profilo Tramissione Conferma Ricezione Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConProfiloTramissioneConfermaRicezioneNonValido")
	public Object[][] testEGovConProfiloTramissioneConfermaRicezioneNonValido()throws Exception{
		String id=this.repositoryProfiloTramissioneConfermaRicezioneNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".PTCRNV"},
			dataProvider="EGovConProfiloTramissioneConfermaRicezioneNonValido",
			dependsOnMethods="EGovConProfiloTramissioneConfermaRicezioneNonValido")
			public void testDBEGovConProfiloTramissioneConfermaRicezioneNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaProfiloTramissioneConfermaRicezioneNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,  CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_PROFILO_TRASMISSIONE_NON_VALIDO_POSIZIONE_CONFERMA_RICEZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	/**
	 * 
	 * Header e-Gov con Sequenza senza numeroProgressivo
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_114] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Sequenza/numeroProgressivo
	 * busta = buste_errate/bustaSequenzaSenzaNumeroProgressivo.xml
	 *
	 */
	Repository repositorySequenzaSenzaNumeroProgressivo=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SSNP"},
			dependsOnMethods="init")
			public void EGovConSequenzaSenzaNumeroProgressivo()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSequenzaSenzaNumeroProgressivo.xml");
		this.repositorySequenzaSenzaNumeroProgressivo.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySequenzaSenzaNumeroProgressivo);
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
					Reporter.log("Invocazione PA con una busta con Sequenza Senza Numero Progressivo.");
					throw new TestSuiteException("Invocazione PA con busta con Sequenza Senza Numero Progressivo, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza elemento Sequenza numeroProgressivo");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConSequenzaSenzaNumeroProgressivo")
	public Object[][] testEGovConSequenzaSenzaNumeroProgressivo()throws Exception{
		String id=this.repositorySequenzaSenzaNumeroProgressivo.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SSNP"},
			dataProvider="EGovConSequenzaSenzaNumeroProgressivo",
			dependsOnMethods="EGovConSequenzaSenzaNumeroProgressivo")
			public void testDBEGovConSequenzaSenzaNumeroProgressivo(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSequenzaSenzaNumeroProgressivo.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO.toString()));

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
	 * Header e-Gov con Sequenza, con numeroProgressivo errato
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_114] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Sequenza/numeroProgressivo
	 * busta = buste_errate/bustaSequenzaErratoNumeroProgressivo.xml
	 * 
	 */
	Repository repositorySequenzaErratoNumeroProgressivo=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SENP"},
			dependsOnMethods="init")
			public void EGovConSequenzaErratoNumeroProgressivo()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSequenzaErratoNumeroProgressivo.xml");
		this.repositorySequenzaErratoNumeroProgressivo.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositorySequenzaErratoNumeroProgressivo);
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
					Reporter.log("Invocazione PA con una busta con Sequenza Errato Numero Progressivo.");
					throw new TestSuiteException("Invocazione PA con busta con SequenzaErratoNumeroProgressivo, non ha causato errori.");
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
	@DataProvider (name="EGovConSequenzaErratoNumeroProgressivo")
	public Object[][] testEGovConSequenzaErratoNumeroProgressivo()throws Exception{
		String id=this.repositorySequenzaErratoNumeroProgressivo.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".SENP"},
			dataProvider="EGovConSequenzaErratoNumeroProgressivo",
			dependsOnMethods="EGovConSequenzaErratoNumeroProgressivo")
			public void testDBEGovConSequenzaErratoNumeroProgressivo(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaSequenzaErratoNumeroProgressivo.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_SEQUENZA_NON_VALIDA_POSIZIONE_NUMERO_PROGRESSIVO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni senza Trasmissioni
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione
	 * busta = buste_errate/bustaListaTrasmissione_trasmissioneNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_trasmissioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_trasmissioneNonPresente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_trasmissioneNonPresente.xml");
		this.repositoryListaTrasmissione_trasmissioneNonPresente.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_trasmissioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione trasmissione Non Presente, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_trasmissioneNonPresente")
	public Object[][] testEGovConListaTrasmissione_trasmissioneNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_trasmissioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTTNP"},
			dataProvider="EGovConListaTrasmissione_trasmissioneNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_trasmissioneNonPresente")
			public void testDBEGovConListaTrasmissione_trasmissioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_trasmissioneNonPresente.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	

	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni, Trasmissione senza origine
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneNonPresente.xml
	 *
	 *   ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte
	 *
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneNonPresente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonPresente.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origine Trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine Trasmissione Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneOrigine");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneNonPresente")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNP"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneNonPresente")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
	
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, null, null, this.busteEGovErrate.getDestinatario(index),null));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con elemento origine vuoto
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneNonPresenteVersione2.xml
	 *
	 *   ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte
	 *
	 * Test equivalente al Test N.30 Della Certificazione DigitPA (Busta Errata 36)
	 */
	Repository repositoryListaTrasmissione_origineTrasmissionePresenteMaVuota=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNP_OrigineVuota"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissionePresenteMaVuota()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonPresenteVersione2.xml");
		this.repositoryListaTrasmissione_origineTrasmissionePresenteMaVuota.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissionePresenteMaVuota);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origine Trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine Trasmissione Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneOrigine IdentificativoParte");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissionePresenteMaVuota")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissionePresenteMaVuota()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissionePresenteMaVuota.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNP_OrigineVuota"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissionePresenteMaVuota",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissionePresenteMaVuota")
			public void testDBEGovConListaTrasmissione_origineTrasmissionePresenteMaVuota(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonPresenteVersione2.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index),this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore OraRegistrazione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedOraRegistrazione(id));
				Reporter.log("Controllo valore Servizio Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
				Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));
	
				Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
						this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, null, null, this.busteEGovErrate.getDestinatario(index),null));
	
	
				Reporter.log("-------------- RISPOSTA --------------------");
	
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedMittente(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
				Reporter.log("Controllo valore Destinatario Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedDestinatario(id,this.busteEGovErrate.getMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione senza origine
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione
	 *busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneNonPresente.xml
	 *
	 * ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte
	 *
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneNonPresente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneNonPresente.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione trasmissione Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneDestinazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneNonPresente")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTNP"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneNonPresente")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,null,null));
	
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione senza ora registrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione
	 *busta = buste_errate/bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente.xml");
		this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione ora Registrazione trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione ora Registrazione trasmissione Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneOraRegistrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente")
	public Object[][] testEGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTNP"},
			dataProvider="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente")
			public void testDBEGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,this.busteEGovErrate.getDestinatario(index),null));
	
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con origine non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneNonValida.xml
	 *
	 *
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneNonValida()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonValida.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneNonValida.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneNonValida);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origine trasmissione Non Valida.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine trasmissione Non valida, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneNonValida")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneNonValida()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneNonValida.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTNV"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneNonValida",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneNonValida")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto mittente = this.busteEGovErrate.getMittente(index);
			mittente.setNome("Errato");
			mittente.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null,this.busteEGovErrate.getDestinatario(index),null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	



	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con destinazione non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte
	 * busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneNonValida.xml
	 * 
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneNonValida()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneNonValida.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneNonValida.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneNonValida);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione trasmissione Non valida.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione trasmissione Non valida, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneNonValida")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneNonValida()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneNonValida.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTNV"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneNonValida",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneNonValida")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneNonValida.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setNome("Errato");
			destinatario.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,destinatario,null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	



	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con ora registrazione non valida
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione
	 * busta = buste_errate/bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonValida.xml
	 *
	 */
	Repository repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonValida=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonValida.xml");
		this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonValida.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonValida);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione ora Registrazione trasmissione Non valida.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione ora Registrazione trasmissione Non valida, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida")
	public Object[][] testEGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida()throws Exception{
		String id=this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneNonValida.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTNV"},
			dataProvider="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida",
			dependsOnMethods="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida")
			public void testDBEGovConListaTrasmissione_oraRegistrazioneTrasmissioneNonValida(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneNonValida.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,
					this.busteEGovErrate.getDestinatario(index),null,false));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	

	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con origine senza identificativo parte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione trasmissione Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione trasmissione Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneOrigine IdentificativoParte");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPNP"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, null, null, this.busteEGovErrate.getDestinatario(index),null));
	
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con destinazione senza identificativo parte
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte
	 * busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione trasmissione identificativo parte  Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione trasmissione identificativo parte Non Presente, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza TrasmissioneDestinazione IdentificativoParte");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPNP"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteNonPresente.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo lista trasmissione con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,null,null));
	
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE.toString()));
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
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con origine, con identificativo parte senza tipo
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte/tipo
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origine Trasmissione Identificativo Parte Tipo Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine Trasmissione Identificativo Parte Tipo Non Presente, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPTNP"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonPresente.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto mittente =  this.busteEGovErrate.getMittente(index);
			mittente.setTipo(null);
			mittente.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null, this.busteEGovErrate.getDestinatario(index),null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	

	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con destinazione, con identificativo parte senza tipo
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte/tipo
	 * busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente.xml
	 *
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione Trasmissione Identificativo Parte Tipo Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione Trasmissione Identificativo Parte Tipo Non Presente, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPTNP"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonPresente.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setTipo(null);
			destinatario.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,destinatario,null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	

	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con origine, con identificativo parte, con tipo non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte/tipo
	 *busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido.xml
	 *
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origine Trasmissione Identificativo Parte Tipo Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine Trasmissione Identificativo Parte Tipo Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPTNV"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteTipoNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto mittente = this.busteEGovErrate.getMittente(index);
			mittente.setTipo("Errato");
			mittente.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, mittente, null , this.busteEGovErrate.getDestinatario(index),null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	

	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con destinazione, con identificativo parte, con tipo non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte/tipo
	 * busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido.xml
	 * 
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione Trasmissione Identificativo Parte Tipo Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione Trasmissione Identificativo Parte Tipo Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPTNV"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteTipoNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			IDSoggetto destinatario = this.busteEGovErrate.getDestinatario(index);
			destinatario.setTipo("Errato");
			destinatario.setCodicePorta(null);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,destinatario,null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_TIPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}






	

	/**
	 *
	 * Header e-Gov con Mittente e indirizzo telematico errato
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_101] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Mittente/IdentificativoParte/indirizzoTelematico
	 * busta = buste_errate/bustaMittenteIndirizzoTelematicoErrato.xml
	 *
	 */
	Repository repositoryMittenteIndirizzoTelematicoErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MITE"},
			dependsOnMethods="init")
			public void EGovMittenteIndirizzoTelematicoErrato()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteIndirizzoTelematicoErrato.xml");
		this.repositoryMittenteIndirizzoTelematicoErrato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);

		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryMittenteIndirizzoTelematicoErrato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta Mittente Indirizzo Telematico Errato.");
					throw new TestSuiteException("Invocazione PA con busta Mittente Indirizzo Telematico Errato, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
	}
	@DataProvider (name="EGovMittenteIndirizzoTelematicoErrato")
	public Object[][] testEGovMittenteIndirizzoTelematicoErrato()throws Exception{
		String id=this.repositoryMittenteIndirizzoTelematicoErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".MITE"},
			dataProvider="EGovMittenteIndirizzoTelematicoErrato",
			dependsOnMethods="EGovMittenteIndirizzoTelematicoErrato")
			public void testDBEGovMittenteIndirizzoTelematicoErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaMittenteIndirizzoTelematicoErrato.xml");
		try{
			/*EGovErrate.vediT(data, id);*/
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			//Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index),this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));

			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,this.busteEGovErrate.getDestinatario(index),null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_MITTENTE_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	

	/**
	 *
	 * Header e-Gov con Destinatario e indirizzo telematico errato
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_102] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: Destinatario/IdentificativoParte/indirizzoTelematico
	 * busta = buste_errate/bustaDestinatarioIndirizzoTelematicoErrato.xml
	 * 
	 */
	Repository repositoryDestinatarioIndirizzoTelematicoErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DITE"},
			dependsOnMethods="init")
			public void EGovDestinatarioIndirizzoTelematicoErrato()throws TestSuiteException, SOAPException, Exception{	
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioIndirizzoTelematicoErrato.xml");
		this.repositoryDestinatarioIndirizzoTelematicoErrato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());
		Message msg=new Message(bin);

		msg.getSOAPPartAsBytes();
		DatabaseComponent dbComponentErogatore = null;
		//DatabaseComponent dbComponentFruitore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryDestinatarioIndirizzoTelematicoErrato);
			client.setUrlPortaDiDominio(Utilities.testSuiteProperties.getServizioRicezioneBusteErogatore());
			client.connectToSoapEngine();
			client.setMessage(msg);
			client.setRispostaDaGestire(true);
			// AttesaTerminazioneMessaggi
			if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()){
				dbComponentErogatore = DatabaseProperties.getDatabaseComponentErogatore();
				//dbComponentFruitore = DatabaseProperties.getDatabaseComponentFruitore();

				client.setAttesaTerminazioneMessaggi(true);
				client.setDbAttesaTerminazioneMessaggiErogatore(dbComponentErogatore);
				//client.setDbAttesaTerminazioneMessaggiFruitore(dbComponentFruitore);
			}
			try {
				client.run();
				if(Utilities.testSuiteProperties.isNewConnectionForResponse()==false){
					Reporter.log("Invocazione PA con una busta Destinatario Indirizzo Telematico Errato.");
					throw new TestSuiteException("Invocazione PA con busta Destinatario Indirizzo Telematico Errato, non ha causato errori.");
				}
			} catch (AxisFault error) {
				Reporter.log("Ricevuto SoapFAULT codice["+error.getFaultCode().getLocalPart()+"] actor["+error.getFaultActor()+"]: "+error.getFaultString());
				Reporter.log("Controllo fault code [Client]");
				Assert.assertTrue("Client".equals(error.getFaultCode().getLocalPart().trim()));
				Reporter.log("Controllo fault string ["+SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP+"]");
				Assert.assertTrue(SPCoopCostanti.FAULT_STRING_VALIDAZIONE_SPCOOP.equals(error.getFaultString()));
			}finally{
				dbComponentErogatore.close();
				//dbComponentFruitore.close();
			}
		}catch(Exception e){
			throw e;
		}finally{
			dbComponentErogatore.close();
			//dbComponentFruitore.close();
		}
	}
	@DataProvider (name="EGovDestinatarioIndirizzoTelematicoErrato")
	public Object[][] testEGovDestinatarioIndirizzoTelematicoErrato()throws Exception{
		String id=this.repositoryDestinatarioIndirizzoTelematicoErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".DITE"},
			dataProvider="EGovDestinatarioIndirizzoTelematicoErrato",
			dependsOnMethods="EGovDestinatarioIndirizzoTelematicoErrato")
			public void testDBEGovDestinatarioIndirizzoTelematicoErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaDestinatarioIndirizzoTelematicoErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
			Reporter.log("Controllo valore Destinatario Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedDestinatario(id,this.busteEGovErrate.getDestinatario(index), this.busteEGovErrate.getIndirizzoTelematicoDestinatario(index)));
			Reporter.log("Controllo valore OraRegistrazione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedOraRegistrazione(id));
			Reporter.log("Controllo valore Servizio Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedServizio(id, this.busteEGovErrate.getDatiServizio(index)));
			Reporter.log("Controllo valore Profilo di Collaborazione Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloDiCollaborazione(id, this.busteEGovErrate.getProfiloCollaborazione(index), this.busteEGovErrate.getProfiloCollaborazioneSdk(index)));

			Reporter.log("Controllo valore Profilo di Trasmissione Busta ["+this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index)+"] con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedProfiloTrasmissione(id, this.busteEGovErrate.getProfiloTrasmissioneConfermaRicezione(index),
					this.busteEGovErrate.getProfiloTrasmissioneInoltro(index),this.busteEGovErrate.getProfiloTrasmissioneInoltroSdk(index)));
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,this.busteEGovErrate.getDestinatario(index),null));


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
			Assert.assertTrue(num==1);
			
			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CODICE_ECCEZIONE +": "+ CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CodiceErroreCooperazione.DESTINATARIO_SCONOSCIUTO));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_DESTINATARIO_SCONOSCIUTO_POSIZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}




	


	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con origine, con identificativo parte, con indirizzo telematico non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Origine/IdentificativoParte/indirizzoTelematico
	 * busta = buste_errate/bustaListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml
	 * 
	 */
	Repository repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPITE"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml");
		this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione origin eTrasmissione Identificativo Parte Indirizzo Telematico Errato.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione origine Trasmissione Identificativo Parte Indirizzo Telematico Errato, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato")
	public Object[][] testEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato()throws Exception{
		String id=this.repositoryListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.getNext();
		if(Utilities.testSuiteProperties.attendiTerminazioneMessaggi_verificaDatabase()==false){
			try {
				Thread.sleep(Utilities.testSuiteProperties.timeToSleep_verificaDatabase());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {}
		return new Object[][]{
				{DatabaseProperties.getDatabaseComponentErogatore(),id}
		};
	}
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTOTIPITE"},
			dataProvider="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato",
			dependsOnMethods="EGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato")
			public void testDBEGovConListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_origineTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),"Errato",this.busteEGovErrate.getDestinatario(index),null));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORIGINE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con destinazione, con identificativo parte, con indirizzo telematico non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/Destinazione/IdentificativoParte/indirizzoTelematico
	 * busta = buste_errate/bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml
	 *
	 */
	Repository repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPITE"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml");
		this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione destinazione Trasmissione IdentificativoParte Indirizzo Telematico Errato.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione destinazione Trasmissione IdentificativoParte Indirizzo Telematico Errato, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato")
	public Object[][] testEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato()throws Exception{
		String id=this.repositoryListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTDTIPITE"},
			dataProvider="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato",
			dependsOnMethods="EGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato")
			public void testDBEGovConListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_destinazioneTrasmissioneIdentificativoParteIndirizzoTelematicoErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,
					this.busteEGovErrate.getDestinatario(index),"Errato"));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_DESTINAZIONE_IDENTIFICATIVO_PARTE_IND_TELEMATICO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}



	

	/**
	 *
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con oraRegistrazione, con tempo non presente
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione/tempo
	 * busta = buste_errate/bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente.xml
	 * 
	 */
	Repository repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTTNP"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente.xml");
		this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione ora Registrazione Trasmissione Tempo Non Presente.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione ora Registrazione Trasmissione Tempo Non Presente, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente")
	public Object[][] testEGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente()throws Exception{
		String id=this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTTNP"},
			dataProvider="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente",
			dependsOnMethods="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente")
			public void testDBEGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonPresente.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,
					this.busteEGovErrate.getDestinatario(index),null,false));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	

	/**
	 * 
	 * Header e-Gov con ListaTrasmissioni, Trasmissione con oraRegistrazione, con tempo non valido
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_116] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaTrasmissioni/Trasmissione/OraRegistrazione/tempo
	 * busta = buste_errate/bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido.xml
	 * 
	 * Test equivalente al Test N.19 Della Certificazione DigitPA (Busta Errata 46)
	 */
	Repository repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTTNV"},
			dependsOnMethods="init")
			public void EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido.xml");
		this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido);
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
					Reporter.log("Invocazione PA con una busta con Lista Trasmissione ora Registrazione Trasmissione Tempo Non Valido.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Trasmissione ora Registrazione Trasmissione Tempo Non Valido, non ha causato errori.");
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
	@DataProvider (name="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido")
	public Object[][] testEGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido()throws Exception{
		String id=this.repositoryListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LTORTTNV"},
			dataProvider="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido",
			dependsOnMethods="EGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido")
			public void testDBEGovConListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListaTrasmissione_oraRegistrazioneTrasmissioneTempoNonValido.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo lista trasmissione con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedTrasmissione(id, this.busteEGovErrate.getMittente(index),null,
					this.busteEGovErrate.getDestinatario(index),null,false));


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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_TRASMISSIONI_NON_VALIDA_POSIZIONE_TRASMISSIONE_ORA_REGISTRAZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}








	

	/**
	 *
	 * Header eGov con elemento 'ListaRiscontri' senza Riscontro
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro
	 * busta = buste_errate/bustaListeRiscontri_senzaRiscontro.xml
	 *
	 */
	Repository repositoryListeRiscontri_senzaRiscontro=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRSR"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_senzaRiscontro()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_senzaRiscontro.xml");
		this.repositoryListeRiscontri_senzaRiscontro.add(this.busteEGovErrate.getID(index));
		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_senzaRiscontro);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
	@DataProvider (name="EGovConListeRiscontri_senzaRiscontro")
	public Object[][] testEGovConListeRiscontri_senzaRiscontro()throws Exception{
		String id=this.repositoryListeRiscontri_senzaRiscontro.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRSR"},
			dataProvider="EGovConListeRiscontri_senzaRiscontro",
			dependsOnMethods="EGovConListeRiscontri_senzaRiscontro")
			public void testDBEGovConListeRiscontri_senzaRiscontro(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_senzaRiscontro.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}





	


	/**
	 *
	 * Header eGov con elemento 'ListaRiscontri', Riscontro senza identificatore
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/Identificatore
	 * busta = buste_errate/bustaListeRiscontri_RiscontroSenzaIdentificatore.xml
	 *
	 */
	Repository repositoryListeRiscontri_RiscontroSenzaIdentificatore=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRSI"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroSenzaIdentificatore()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroSenzaIdentificatore.xml");
		this.repositoryListeRiscontri_RiscontroSenzaIdentificatore.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroSenzaIdentificatore);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza RiscontroIdentificatore");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListeRiscontri_RiscontroSenzaIdentificatore")
	public Object[][] testEGovConListeRiscontri_RiscontroSenzaIdentificatore()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroSenzaIdentificatore.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRSI"},
			dataProvider="EGovConListeRiscontri_RiscontroSenzaIdentificatore",
			dependsOnMethods="EGovConListeRiscontri_RiscontroSenzaIdentificatore")
			public void testDBEGovConListeRiscontri_RiscontroSenzaIdentificatore(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroSenzaIdentificatore.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString()));
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
	 * Header eGov con elemento 'ListaRiscontri', Riscontro con identificatore errato
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/Identificatore
	 * busta = buste_errate/bustaListeRiscontri_RiscontroIdentificatoreErrato.xml
	 * 
	 * Test equivalente al Test N.2 Della Certificazione DigitPA (Busta Errata 52)
	 * 
	 */
	Repository repositoryListeRiscontri_RiscontroIdentificatoreErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRIE"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroIdentificatoreErrato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroIdentificatoreErrato.xml");
		this.repositoryListeRiscontri_RiscontroIdentificatoreErrato.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroIdentificatoreErrato);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
	@DataProvider (name="EGovConListeRiscontri_RiscontroIdentificatoreErrato")
	public Object[][] testEGovConListeRiscontri_RiscontroIdentificatoreErrato()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroIdentificatoreErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRIE"},
			dataProvider="EGovConListeRiscontri_RiscontroIdentificatoreErrato",
			dependsOnMethods="EGovConListeRiscontri_RiscontroIdentificatoreErrato")
			public void testDBEGovConListeRiscontri_RiscontroIdentificatoreErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroIdentificatoreErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo Riscontro");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("Errato",id));

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
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id,CodiceErroreCooperazione.LISTA_RISCONTRI_NON_VALIDA));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA +": "+ SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_CONTESTO_CODIFICA,SPCoopCostanti.CONTESTO_CODIFICA_ECCEZIONE_VALIDAZIONE));

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA+": "+SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_RILEVANZA, SPCoopCostanti.ECCEZIONE_RILEVANZA_GRAVE));

			// Che esistano SOLAMENTE 1 eccezioni me lo garantisce il controllo soprastante.

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_IDENTIFICATORE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}







	

	/**
	 *
	 * Header eGov con elemento 'ListaRiscontri', Riscontro con ora registrazione errata
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione
	 * busta = buste_errate/bustaListeRiscontri_RiscontroOraRegistrazioneErrato.xml
	 * 
	 */
	Repository repositoryListeRiscontri_RiscontroOraRegistrazioneErrato=new Repository();
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".LRRORE"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroOraRegistrazioneErrato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneErrato.xml");
		this.repositoryListeRiscontri_RiscontroOraRegistrazioneErrato.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroOraRegistrazioneErrato);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
	@DataProvider (name="EGovConListeRiscontri_RiscontroOraRegistrazioneErrato")
	public Object[][] testEGovConListeRiscontri_RiscontroOraRegistrazioneErrato()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroOraRegistrazioneErrato.getNext();
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
	@Test(groups={ID_GRUPPO, ID_GRUPPO+".LRRORE"},
			dataProvider="EGovConListeRiscontri_RiscontroOraRegistrazioneErrato",
			dependsOnMethods="EGovConListeRiscontri_RiscontroOraRegistrazioneErrato")
			public void testDBEGovConListeRiscontri_RiscontroOraRegistrazioneErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneErrato.xml");
		try{
			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo Riscontro");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("MinisteroFruitore_MinisteroFruitoreSPCoopIT_2411206_2005-11-10_11:35",id,false));

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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}

	}







	/**
	 * 
	 * Header eGov con elemento 'ListaRiscontri', Riscontro senza ora registrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione
	 * busta = buste_errate/bustaListeRiscontri_RiscontroSenzaOraRegistrazione.xml
	 *
	 */
	Repository repositoryListeRiscontri_RiscontroSenzaOraRegistrazione=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRSOR"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroSenzaOraRegistrazione()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroSenzaOraRegistrazione.xml");
		this.repositoryListeRiscontri_RiscontroSenzaOraRegistrazione.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroSenzaOraRegistrazione);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza RiscontroOraRegistrazione");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListeRiscontri_RiscontroSenzaOraRegistrazione")
	public Object[][] testEGovConListeRiscontri_RiscontroSenzaOraRegistrazione()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroSenzaOraRegistrazione.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRSOR"},
			dataProvider="EGovConListeRiscontri_RiscontroSenzaOraRegistrazione",
			dependsOnMethods="EGovConListeRiscontri_RiscontroSenzaOraRegistrazione")
			public void testDBEGovConListeRiscontri_RiscontroSenzaOraRegistrazione(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroSenzaOraRegistrazione.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo Riscontro");
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("MinisteroFruitore_MinisteroFruitoreSPCoopIT_2411206_2005-11-10_11:35",id));
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE.toString()));

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
	 * Header eGov con elemento 'ListaRiscontri', Riscontro senza tempo nell'ora registrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione/tempo
	 * busta = buste_errate/bustaListeRiscontri_RiscontroOraRegistrazioneSenzaTempo.xml
	 *
	 */
	Repository repositoryListeRiscontri_RiscontroOraRegistrazioneSenzaTempo=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRORST"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo()throws TestSuiteException, SOAPException, Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneSenzaTempo.xml");
		this.repositoryListeRiscontri_RiscontroOraRegistrazioneSenzaTempo.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroOraRegistrazioneSenzaTempo);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
		err.setMsgErrore("Struttura della busta non corretta: Header egov senza RiscontroOraRegistrazioneTempo");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	@DataProvider (name="EGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo")
	public Object[][] testEGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroOraRegistrazioneSenzaTempo.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRORST"},
			dataProvider="EGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo",
			dependsOnMethods="EGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo")
			public void testDBEGovConListeRiscontri_RiscontroOraRegistrazioneSenzaTempo(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneSenzaTempo.xml");
		try{
			if(Utilities.testSuiteProperties.isGenerazioneRispostaSPCoopStrutturaHeaderNonCorretta()){
				
				Reporter.log("Controllo tracciamento richiesta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTraced(id));		
				Reporter.log("Controllo valore Mittente Busta con id: " +id);
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
				Reporter.log("Controllo Riscontro");
				Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("MinisteroFruitore_MinisteroFruitoreSPCoopIT_2411206_2005-11-10_11:35",id));
	
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
	
				Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO);
				Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString()));
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
	 * Header eGov con elemento 'ListaRiscontri', Riscontro con tempo errato nell'ora registrazione
	 * La busta ritornata possiedera' una eccezione [EGOV_IT_115] - ErroreIntestazioneMessaggioSPCoop, descrizione errore: ListaRiscontri/Riscontro/OraRegistrazione/tempo
	 * busta = buste_errate/bustaListeRiscontri_RiscontroOraRegistrazioneTempoErrato.xml
	 * 
	 */
	Repository repositoryListeRiscontri_RiscontroOraRegistrazioneTempoErrato=new Repository();
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRORTE"},
			dependsOnMethods="init")
			public void EGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato()throws TestSuiteException, SOAPException, Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneTempoErrato.xml");
		this.repositoryListeRiscontri_RiscontroOraRegistrazioneTempoErrato.add(this.busteEGovErrate.getID(index));

		java.io.ByteArrayInputStream bin = new java.io.ByteArrayInputStream(this.busteEGovErrate.getBusta(index).getBytes());

		Message msg=new Message(bin);
		msg.getSOAPPartAsBytes();

		DatabaseComponent dbComponentErogatore = null;
		try{
			ClientHttpGenerico client=new ClientHttpGenerico(this.repositoryListeRiscontri_RiscontroOraRegistrazioneTempoErrato);
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
					Reporter.log("Invocazione PA con una busta con Lista Riscontri.");
					throw new TestSuiteException("Invocazione PA con busta con Lista Riscontri, non ha causato errori.");
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
	@DataProvider (name="EGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato")
	public Object[][] testEGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato()throws Exception{
		String id=this.repositoryListeRiscontri_RiscontroOraRegistrazioneTempoErrato.getNext();
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
	@Test(groups={ID_GRUPPO,ID_GRUPPO+".LRRORTE"},
			dataProvider="EGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato",
			dependsOnMethods="EGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato")
			public void testDBEGovConListeRiscontri_RiscontroOraRegistrazioneTempoErrato(DatabaseComponent data,String id) throws Exception{
		int index = this.busteEGovErrate.getIndexFromNomeFile("bustaListeRiscontri_RiscontroOraRegistrazioneTempoErrato.xml");
		try{

			Reporter.log("Controllo tracciamento richiesta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTraced(id));
			Reporter.log("Controllo valore Mittente Busta con id: " +id);
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedMittente(id,this.busteEGovErrate.getMittente(index), this.busteEGovErrate.getIndirizzoTelematicoMittente(index)));
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
			Reporter.log("Controllo Riscontro");
			Assert.assertTrue(data.getVerificatoreTracciaRichiesta().isTracedRiscontro("MinisteroFruitore_MinisteroFruitoreSPCoopIT_2411206_2005-11-10_11:35",id,false));

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

			Reporter.log("Controllo che la busta abbia generato l'eccezione  con " +CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE+": "+ SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO);
			Assert.assertTrue(data.getVerificatoreTracciaRisposta().isTracedPerTutteLeEccezioni(id, CostantiDB.TRACCE_ECCEZIONI_COLUMN_POSIZIONE, SPCoopCostantiPosizioneEccezione.ECCEZIONE_LISTA_RISCONTRI_NON_VALIDA_POSIZIONE_RISCONTRO_ORA_REGISTRAZIONE_TEMPO.toString()));

		}catch(Exception e){
			throw e;
		}finally{
			data.close();
		}
	}

}


