/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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



package org.openspcoop2.protocol.spcoop.testsuite.units.errori_applicativi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.axis.AxisFault;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiErroriIntegrazione;
import org.openspcoop2.protocol.spcoop.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.spcoop.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.spcoop.testsuite.core.SPCoopTestsuiteLogger;
import org.openspcoop2.protocol.spcoop.testsuite.core.Utilities;
import org.openspcoop2.protocol.utils.ErroriProperties;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.testsuite.units.utils.OpenSPCoopDetailsUtilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.w3c.dom.Element;

/**
 * Test su richieste applicative malformate indirizzate alla Porta di Dominio
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoopDetail extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "OpenSPCoopDetail";

	
	
	private Logger log = SPCoopTestsuiteLogger.getInstance();
	
	private boolean genericCode = false;
	private boolean unwrap = false;
	
	protected OpenSPCoopDetail(boolean genericCode, boolean unwrap) {
		super(org.openspcoop2.protocol.spcoop.testsuite.core.TestSuiteProperties.getInstance());
		this.genericCode = genericCode;
		this.unwrap = unwrap;
	}
	

	
	private Date dataAvvioGruppoTest = null;
	protected void _testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	protected void _testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	
	// TODO: Il tunnel SOAP, avendo come default, la restituzione di un errore XML (errore cnipa = xml), non viene mai generato un detail openspcoop.
	
	
	
	
	
	
	
	
	/** ERRORI 4XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi4XX(){
		return new Object[][]{
				{null,null,"PORTA_DELEGATA_NON_ESISTENTE"},
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_ERRORE_APPLICATIVO_CNIPA}
		};
	}
	
	protected void _testErroriApplicativi4XX(String username,String password,String portaDelegata) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testErroriApplicativi4XX(username, password, portaDelegata);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = ""+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"405";
		String msg = CostantiErroriIntegrazione.MSG_405_SERVIZIO_NON_TROVATO;
		boolean equalsMatch = true;
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.OPERATION_UNDEFINED;
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		
		if(username==null){
			codice = ""+org.openspcoop2.protocol.basic.Costanti.ERRORE_INTEGRAZIONE_PREFIX_CODE+"401";
			msg = CostantiErroriIntegrazione.MSG_401_PD_INESISTENTE;
			equalsMatch = false;
			if(this.genericCode) {
				IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.API_OUT_UNKNOWN;
				codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
				if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
					msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
					equalsMatch = true;
				}
			}
		}
		else if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			if(!this.genericCode) {
				codice = "PREFIX_PERSONALIZZATO_405";
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", "Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 1", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 2", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 3", true));
			}
		}
	
		
		if(username==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(username) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)==false); // non vengono generati in caso di 4XX
			
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false); // non vengono generati in caso di 4XX
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		// Aggiungo errori attesi
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("La porta delegata invocata non esiste pd["+portaDelegata+"] urlInvocazione[/govway/spcoop/out/"+portaDelegata+"]");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORI 5XX */
	
	protected Object[][] _personalizzazioniErroriApplicativi5XX(){
		return new Object[][]{
				{"erroreApplicativoAsSoapFaultDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapFaultRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsSoapXmlDefault","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX},
				{"erroreApplicativoAsXmlRidefinito","123456",CostantiTestSuite.PORTA_DELEGATA_VERIFICA_ERRORE_PROCESSAMENTO_5XX}
		};
	}
	
	protected void _testErroriApplicativi5XX(String username,String password,String portaDelegata) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testErroriApplicativi5XX(username, password, portaDelegata);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_500_ERRORE_INTERNO);
		String msg = CostantiErroriIntegrazione.MSG_5XX_SISTEMA_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR;
			if(this.unwrap) {
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			
			if(!this.genericCode) {
				codice = "PREFIX_PERSONALIZZATO_504";
				//	msg = "Autorizzazione non concessa al servizio applicativo ["+username+"] di utilizzare la porta delegata [TestErroreProcessamento5XX]: processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
				msg = "processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)";
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();	
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(username) || "erroreApplicativoAsXmlRidefinito".equals(username)){
			if(!this.genericCode) {
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", "Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 1", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 2", true));
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causato da", "Eccezione processamento Test Livello 3", true));
			}
		}
		
		if("erroreApplicativoAsSoapFaultDefault".equals(username) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(username)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"RicezioneContenutiApplicativi", 
					eccezioni, dettagli);
			
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("processo di autorizzazione [testOpenSPCoop2] fallito, Autorizzazione fallita per verifica Errore Processamento (TestSuiteOpenSPCoop)");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	/** SOAP FAULT APPLICATIVO ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativi(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testFaultApplicativoArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testFaultApplicativoArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
								
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		Assert.assertTrue(o instanceof AxisFault);
		AxisFault error = (AxisFault)o;
		
		if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)==false); // non vengono aggiunti dettagli, se il SOAP Fault e' generato dal servizio applicativo
			
		}
		else{
			
			// vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)==false);
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	/** SOAP FAULT PDD ARRICCHITO */
	
	protected Object[][] _personalizzazioniFaultApplicativiPdD(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testFaultPddArricchitoFAULTCNIPA_Default(String servizioApplicativoFruitore) throws Exception{
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testFaultPddArricchitoFAULTCNIPA_Default(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		Assert.assertTrue(o instanceof AxisFault);
		AxisFault error = (AxisFault)o;
		
		if("erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
				"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)==false); // non vengono aggiunti dettagli, se il SOAP Fault e' generato dal servizio applicativo
			
		}
		else{
			
			// vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)==false);
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefused(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoConnectionRefused(String servizioApplicativoFruitore) throws Exception{
	
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testServizioApplicativoConnectionRefused(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, "spc-SoggettoConnettoreErrato");
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();	
		if(!this.genericCode) {
			dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", "Connection refused", true));
		}
			
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"InoltroBuste", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: connection refused ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreConnectionRefusedServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoConnectionRefusedServizioApplicativo(String servizioApplicativoFruitore) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testServizioApplicativoConnectionRefusedServizioApplicativo(servizioApplicativoFruitore);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE;
		//String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
		}
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();	
		if(!this.genericCode) {
			dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", "Connection refused", true));
		}
		
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.APPLICATIVA,"ConsegnaContenutiApplicativi", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Connection refused");
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE PDD: Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOut(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoTimedOut(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testServizioApplicativoTimedOut(servizioApplicativoFruitore, readTimedOut);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_FRUITORE;
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_PDD_NON_DISPONIBILE.replace(CostantiProtocollo.KEYWORDPDD_NON_DISPONIBILE, 
				readTimedOut ? "spc-SoggettoConnettoreErratoConnectReadTimedOut" : "spc-SoggettoConnettoreErratoConnectTimedOut");
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
			codice = "PREFIX_PERSONALIZZATO_516";
		}
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			if(readTimedOut) {
				integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			}
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		if(!this.genericCode) {
			if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", readTimedOut ? "read timed out" : "connect timed out", true));
			}else{
				dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", readTimedOut ? "Read timed out" : "Connect timed out", true));
			}
		}
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.DELEGATA,"InoltroBuste", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		if(readTimedOut) {
			err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
		}
		else {
			err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");	
		}
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/** ERRORE CONNETTORE SA: Connect Timed Out ARRICCHITO */
	
	protected Object[][] _personalizzazioniErroreTimedOutServizioApplicativo(){
		return new Object[][]{{null},
				{"erroreApplicativoAsSoapFaultDefault"},
				{"erroreApplicativoAsSoapFaultRidefinito"},
				{"erroreApplicativoAsSoapXmlDefault"},
				{"erroreApplicativoAsXmlRidefinito"},
		};
	}
	protected void _testServizioApplicativoTimedOutServizioApplicativo(String servizioApplicativoFruitore, boolean readTimedOut) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
		
		ErroreApplicativoCNIPA erroreApplicativoCNIPA = new ErroreApplicativoCNIPA(this.genericCode, this.unwrap);
		Object o = erroreApplicativoCNIPA._testServizioApplicativoTimedOutServizioApplicativo(servizioApplicativoFruitore, readTimedOut);
		Assert.assertTrue(o!=null);
		Reporter.log("Response: "+o.getClass().getName());
		
		// dati generali
		IDSoggetto dominio = CostantiTestSuite.SPCOOP_SOGGETTO_EROGATORE;
		//String codice = Utilities.toString(CodiceErroreCooperazione.ERRORE_GENERICO_PROCESSAMENTO_MESSAGGIO);
		String codice = Utilities.toString(CodiceErroreIntegrazione.CODICE_516_CONNETTORE_UTILIZZO_CON_ERRORE);
		String msg = CostantiErroriIntegrazione.MSG_516_SERVIZIO_APPLICATIVO_NON_DISPONIBILE;
		boolean equalsMatch = true;
		
		if("erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore) || "erroreApplicativoAsXmlRidefinito".equals(servizioApplicativoFruitore)){
		}
		
		ErroriProperties erroriProperties = ErroriProperties.getInstance(this.log);
		if(this.genericCode) {
			IntegrationFunctionError integrationFunctionError = IntegrationFunctionError.SERVICE_UNAVAILABLE;
			if(readTimedOut) {
				integrationFunctionError = IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT;
			}
			codice = erroriProperties.getErrorType_noWrap(integrationFunctionError);
			if(erroriProperties.isForceGenericDetails_noWrap(integrationFunctionError)) {
				msg = erroriProperties.getGenericDetails_noWrap(integrationFunctionError);
				equalsMatch = true;
			}
		}
		
		// detail OpenSPCoop
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> eccezioni = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();
		org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail ecc = new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail();
		ecc.setCodice(codice);
		ecc.setDescrizione(msg);
		ecc.setCheckDescrizioneTramiteMatchEsatto(equalsMatch);
		eccezioni.add(ecc);
		
		List<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail> dettagli = 
			new ArrayList<org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail>();	
		if(!this.genericCode) {
			dettagli.add(new org.openspcoop2.testsuite.units.utils.OpenSPCoopDetail("causa", readTimedOut ? "Read timed out" : "Connect timed out", true));
		}
		
		
		if(servizioApplicativoFruitore==null ||
				"erroreApplicativoAsSoapFaultDefault".equals(servizioApplicativoFruitore) ||
			"erroreApplicativoAsSoapFaultRidefinito".equals(servizioApplicativoFruitore)){
		
			Assert.assertTrue(o instanceof AxisFault);
			AxisFault error = (AxisFault)o;
			
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(error)); // vengono generati in caso di 5XX
			
			OpenSPCoopDetailsUtilities.verificaFaultOpenSPCoopDetail(error, 
					dominio,TipoPdD.APPLICATIVA,"ConsegnaContenutiApplicativi", 
					eccezioni, dettagli);
		}
		else{
			
			Assert.assertTrue(o instanceof Element || o instanceof byte[]);
			Element element = null;
			if(o instanceof Element) {
				element = (Element) o;
			}
			else {
				element = org.openspcoop2.utils.xml.XMLUtils.getInstance().newElement((byte[])o);
			}
			
			 // vengono generati in caso di 5XX ma non vengono generati in caso il servizio applicativo voglia un errore compatibile in formato CNIPA (XML di ritorno standard)
			Assert.assertTrue(OpenSPCoopDetailsUtilities.existsOpenSPCoopDetails(element)==false);
			
		}
		
		Date dataFineTest = DateManager.getDate();
		
		ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
		err.setIntervalloInferiore(dataInizioTest);
		err.setIntervalloSuperiore(dataFineTest);
		if(readTimedOut) {
			err.setMsgErrore("Errore avvenuto durante la consegna HTTP: Read timed out");
		}
		else {
			err.setMsgErrore("Errore avvenuto durante la consegna HTTP: connect timed out");
		}
		this.erroriAttesiOpenSPCoopCore.add(err);
	}
}
