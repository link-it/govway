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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.utils.date.DateManager;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.Element;

/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopDetail {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "OpenSPCoopDetail";


	
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
	
	
	
	
	
	// TODO: Il tunnel SOAP, avendo come default, la restituzione di un errore XML (errore cnipa = xml), non viene mai generato un detail openspcoop.
	
	
	
	
	
	
	
	
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
	
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".ErroriApplicativi4XX"},dataProvider="personalizzazioniErroriApplicativi4XX")
	public void testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testErroriApplicativi4XX_engine(username, password, portaDelegata);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = "OPENSPCOOP_ORG_405";
		String msg = CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO;
		boolean equalsMatch = true;
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		
		if(username==null){
			codice = "OPENSPCOOP_ORG_401";
			msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
			equalsMatch = false;
		}
		else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			codice = "PREFIX_PERSONALIZZATO_405";
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 1", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 2", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 3", true));
		}
	
		
		if(username==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(username) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)==false); // non vengono generati in caso di 4XX
			
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false); // non vengono generati in caso di 4XX
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		// Aggiungo errori attesi
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/openspcoop2/spcoop/PD/"+portaDelegata+"]");
		this.erroriAttesiOpenSPCoopCore.add(err);
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
	
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".ErroriApplicativi5XX"},dataProvider="personalizzazioniErroriApplicativi5XX")
	public void testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testErroriApplicativi5XX_engine(username, password, portaDelegata);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
		String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			codice = "PREFIX_PERSONALIZZATO_504";
			//msg = "Autorizzazione non concessa al servizio applicativo ["+username+"] di utilizzare la porta delegata [TestErroreProcessamento5XX]: processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
			msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();	
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 1", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 2", true));
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 3", true));
		}
		
		if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			Utilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"RicezioneContenutiApplicativiSOAP", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false);
			
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
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".SOAP_FAULT_APPLICATIVO"},dataProvider="personalizzazioniFaultApplicativi")
	public void testFaultApplicativoArricchitoFAULTCNIPA_Default_engine(String servizioApplicativoFruitore) throws Exception{
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testFaultApplicativoArricchitoFAULTCNIPA_Default_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		Assert.assertTrue(o instanceof AxisFault);
		AxisFault error = (AxisFault)o;
		
		if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)==false); // non vengono aggiunti dettagli, se il SOAP Fault e' generato dal servizio applicativo
			
		}
		else{
			
			// vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)==false);
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	@DataProvider (name="personalizzazioniFaultApplicativiPdD")
	public Object[][] personalizzazioniFaultApplicativiPdD(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".SOAP_FAULT_PDD"},dataProvider="personalizzazioniFaultApplicativiPdD")
	public void testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testFaultPddArricchitoFAULTCNIPA_Default_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		Assert.assertTrue(o instanceof AxisFault);
		AxisFault error = (AxisFault)o;
		
		if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)==false); // non vengono aggiunti dettagli, se il SOAP Fault e' generato dal servizio applicativo
			
		}
		else{
			
			// vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)==false);
			
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
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECTION_REFUSED_PDD"},dataProvider="personalizzazioniErroreConnectionRefused")
	public void testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{
	
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testServizioApplicativoConnectionRefused_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErrato");
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();	
		dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Connection refused", true));
			
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			Utilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"InoltroBuste", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
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
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECTION_REFUSED_SA"},dataProvider="personalizzazioniErroreConnectionRefusedServizioApplicativo")
	public void testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testServizioApplicativoConnectionRefusedServizioApplicativo_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE;
		String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();	
		dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Connection refused", true));
		
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			Utilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.APPLICATIVA,"ConsegnaContenutiApplicativi", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
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
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECT_TIMED_OUT_PDD"},dataProvider="personalizzazioniErroreConnectTimedOut")
	public void testServizioApplicativoConnectTimedOut(String servizioApplicativoFruitore) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testServizioApplicativoConnectTimedOut_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "SPCSoggettoConnettoreErratoConnectTimedOut");
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();	
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "connect timed out", true));
		}else{
			dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Connect timed out", true));
		}
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			Utilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"InoltroBuste", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
		this.erroriAttesiOpenSPCoopCore.add(err);
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
	@Test(groups={OpenSPCoopDetail.ID_GRUPPO,OpenSPCoopDetail.ID_GRUPPO+".CONNECT_TIMED_OUT_SA"},dataProvider="personalizzazioniErroreConnectTimedOutServizioApplicativo")
	public void testServizioApplicativoConnectTimedOutServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA();
		Object o = erroreApplicativoCNIPA.testServizioApplicativoConnectTimedOutServizioApplicativo_engine(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE;
		String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();
		org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail ecc = new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail>();	
		dettagli.add(new org.openspcoop2.protocol.spcoop.testsuite.core.OpenSPCoopDetail("causa", "Connect timed out", true));
		
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			Utilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.APPLICATIVA,"ConsegnaContenutiApplicativi", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element);
			Element element = (Element) o;
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(Utilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
}
