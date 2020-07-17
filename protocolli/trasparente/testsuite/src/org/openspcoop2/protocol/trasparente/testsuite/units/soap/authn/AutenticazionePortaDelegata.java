/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.authn;

import java.io.IOException;
import java.util.Date;
import java.util.Vector;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.PosizioneCredenziale;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AutenticazionePortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazionePortaDelegata extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaDelegata";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutenticazionePortaDelegata() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=AutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=AutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="basicProvider")
	public Object[][] basicProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"),
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456Errata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasicErrata", "123456"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"),
					IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasic(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasic(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testBasic(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC_OPTIONAL"},dataProvider="basicProvider")
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false;
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_BASIC, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
				200);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasicForwardAuthorization(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasicForwardAuthorization(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testBasicForwardAuthorization(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC_FORWARD_AUTHORIZATION, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	@DataProvider(name="sslProvider")
	public Object[][] sslProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500,
					null,null},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null,
						null, -1,true, 200,
						new String[] {"CN=client, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						"EsempioFruitoreTrasparenteSsl"}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null, 
						null, -1,true, 200,
						new String[] {"CN=client2, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						"EsempioFruitoreTrasparenteSsl2"}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null, 
						null,-1, true, 200,
						new String[] {"CN=client3, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // credenziali corrette (anche se non registrate sul registro)
				
				// Credenziali corrette con caricamento certificato
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU.jks", "123456", "123456"), 
						null, 
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null, 
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1_serialNumberDifferente"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU.jks", "123456", "123456"), 
						null, 
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null, 
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2_serialNumberDifferente"},
		};
	}

	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso, 
			String [] credenziale, 
			String nomeServizioApplicativo) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testSsl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso,
					credenziale,
					nomeServizioApplicativo);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso, 
			String [] credenziale, 
			String nomeServizioApplicativo) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testSsl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso,
					credenziale,
					nomeServizioApplicativo);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testSsl(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_SSL, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
				30000); // readTimeout);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
		
		if(credenziale!=null || nomeServizioApplicativo!=null) {
			DatabaseMsgDiagnosticiComponent dataMsg = null;
			try{
				dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
				
				if(credenziale!=null && credenziale.length>0) {
					for (int i = 0; i < credenziale.length; i++) {
						Reporter.log("Cerco credenziale-"+i+" ["+credenziale[i]+"] nei log ...");
						Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, credenziale[i]));		
					}
				}
				
				if(nomeServizioApplicativo!=null) {
					Reporter.log("Cerco nomeServizioApplicativo ["+nomeServizioApplicativo+"] nei log ...");
					Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, nomeServizioApplicativo));
				}
				
			}finally{
				dataMsg.close();
			}
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL_OPTIONAL"},dataProvider="sslProvider")
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo ) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
				
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_SSL, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
				200);
		
		if(credenziale!=null || nomeServizioApplicativo!=null) {
			DatabaseMsgDiagnosticiComponent dataMsg = null;
			try{
				dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaFruitore();
				
				if(credenziale!=null && credenziale.length>0) {
					for (int i = 0; i < credenziale.length; i++) {
						Reporter.log("Cerco credenziale-"+i+" ["+credenziale[i]+"] nei log ...");
						Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, credenziale[i]));		
					}
				}
				
				if(nomeServizioApplicativo!=null) {
					Reporter.log("Cerco nomeServizioApplicativo ["+nomeServizioApplicativo+"] nei log ...");
					Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, nomeServizioApplicativo));
				}
				
			}finally{
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	@DataProvider(name="principalProvider")
	public Object[][] principalProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"),
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"),
						IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
						CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"),
						null,
						null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"),
						null,
						null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipal(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipal(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipal(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				stato,
				30000); // readTimeout);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalHeaderClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_CLEAN,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderNotClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderNotClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalHeaderNotClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalHeaderOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_HEADER,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalQueryClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_CLEAN,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryNotClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryNotClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalQueryNotClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalQueryOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_QUERY,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalUrl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalUrl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalUrl(CredenzialiInvocazione credenzialiInvocazione,boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_URL,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
				returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_URL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalUrlOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico		
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_URL,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_IP"})
	public void testPrincipalIp() throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_IP,
				CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, null, 
				addIDUnivoco, 
				true, null, null, null, true, dataInizioTest,
				200);
		
	}
	

	
	
	
	
	
	
	
	@DataProvider(name="apiKeyProvider")
	public Object[][] apiKeyProvider() throws Exception{
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456"),
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey2@MinisteroFruitore.gw", "123456"), 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456Errata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKeyErrato@MinisteroFruitore.gw", "123456"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata", "credenzialeErrata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata@MinisteroFruitore.gw", "credenzialeErrata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate		
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY"},dataProvider="apiKeyProvider")
	public void testApiKey_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKey(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY"},dataProvider="apiKeyProvider")
	public void testApiKey_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKey(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKey(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, 
			int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
			
			AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY, credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		}
			
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY_OPTIONAL"},dataProvider="apiKeyProvider")
	public void testApiKeyOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false;
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
		
			AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_APIKEY, credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
					200);
			
		}
	}
	
	
	@DataProvider(name="apiKeyForwardProvider")
	public Object[][] apiKeyForwardProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY_FORWARD"},dataProvider="apiKeyForwardProvider")
	public void testApiKeyForwardAuthorization_genericCode(PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardAuthorization(genericCode, posizione, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY_FORWARD"},dataProvider="apiKeyForwardProvider")
	public void testApiKeyForwardAuthorization_specificCode(PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardAuthorization(genericCode, posizione, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKeyForwardAuthorization(boolean genericCode, PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		
		AuthUtilities.testPortaDelegata(nomePortaDelegata, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	@DataProvider(name="apiKeyForwardCustomProvider")
	public Object[][] apiKeyForwardCustomProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY_CUSTOM,
								null,
								null, -1,true, 200}, // crendeziali corrette
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.HEADER, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER_CUSTOM,
								IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
								CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APIKEY_COOKIE_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY_FORWARD_CUSTOM"},dataProvider="apiKeyForwardCustomProvider")
	public void testApiKeyForwardCustomAuthorization_genericCode(PosizioneCredenziale posizione, String customName, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardCustomAuthorization(genericCode, posizione, customName, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APIKEY_FORWARD_CUSTOM"},dataProvider="apiKeyForwardCustomProvider")
	public void testApiKeyForwardCustomAuthorization_specificCode(PosizioneCredenziale posizione,  String customName, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardCustomAuthorization(genericCode, posizione, customName, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKeyForwardCustomAuthorization(boolean genericCode, PosizioneCredenziale posizione,  String customName, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		if(customName!=null) {
			credenzialiInvocazione.setNomePosizioneApiKey(customName);
		}
		
		AuthUtilities.testPortaDelegata(nomePortaDelegata, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}

	
	
	
	
	@DataProvider(name="appIdProvider")
	public Object[][] appIdProvider() throws Exception{
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456"),
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId2@MinisteroFruitore.gw", "123456"), 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456Errata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppIdErrato@MinisteroFruitore.gw", "123456"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata", "credenzialeErrata"),
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456"),
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata@MinisteroFruitore.gw", "credenzialeErrata"),
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID"},dataProvider="appIdProvider")
	public void testAppId_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppId(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID"},dataProvider="appIdProvider")
	public void testAppId_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppId(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppId(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, 
			int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
			
			AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID, credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		}
			
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID_OPTIONAL"},dataProvider="appIdProvider")
	public void testAppIdOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false;
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
		
			AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_APPID, credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
					200);
			
		}
	}
	
	
	
	
	
	
	
	@DataProvider(name="appIdForwardProvider")
	public Object[][] appIdForwardProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID_FORWARD"},dataProvider="appIdForwardProvider")
	public void testAppIdForwardAuthorization_genericCode(PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardAuthorization(genericCode, posizione, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID_FORWARD"},dataProvider="appIdForwardProvider")
	public void testAppIdForwardAuthorization_specificCode(PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardAuthorization(genericCode, posizione, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppIdForwardAuthorization(boolean genericCode, PosizioneCredenziale posizione, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		
		AuthUtilities.testPortaDelegata(nomePortaDelegata, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	@DataProvider(name="appIdForwardCustomProvider")
	public Object[][] appIdForwardCustomProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, null, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY_CUSTOM,
								null,
								null, -1,true, 200}, // crendeziali corrette
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.HEADER, null, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER_CUSTOM,
								IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
								CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,  CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, null, null, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_DELEGATA_AUTH_APPID_COOKIE_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID_FORWARD_CUSTOM"},dataProvider="appIdForwardCustomProvider")
	public void testAppIdForwardCustomAuthorization_genericCode(PosizioneCredenziale posizione, String customNameApiKey, String customNameAppId, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardCustomAuthorization(genericCode, posizione, customNameApiKey, customNameAppId, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".APPID_FORWARD_CUSTOM"},dataProvider="appIdForwardCustomProvider")
	public void testAppIdForwardCustomAuthorization_specificCode(PosizioneCredenziale posizione, String customNameApiKey, String customNameAppId, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardCustomAuthorization(genericCode, posizione, customNameApiKey, customNameAppId, nomePortaDelegata, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppIdForwardCustomAuthorization(boolean genericCode, PosizioneCredenziale posizione,  String customNameApiKey, String customNameAppId, String nomePortaDelegata, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		if(customNameApiKey!=null) {
			credenzialiInvocazione.setNomePosizioneApiKey(customNameApiKey);
		}
		if(customNameAppId!=null) {
			credenzialiInvocazione.setNomePosizioneAppId(customNameAppId);
		}
		
		AuthUtilities.testPortaDelegata(nomePortaDelegata, credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}

}
