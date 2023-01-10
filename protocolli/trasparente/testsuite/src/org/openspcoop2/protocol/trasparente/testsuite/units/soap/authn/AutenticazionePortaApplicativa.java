/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
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
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AutenticazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutenticazionePortaApplicativa extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaApplicativa";
	public static final String ID_GRUPPO_NO_PRINCIPAL = "AutenticazionePortaApplicativaNoPrincipal";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutenticazionePortaApplicativa() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {AutenticazionePortaApplicativa.ID_GRUPPO, AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups= {AutenticazionePortaApplicativa.ID_GRUPPO, AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="noneProvider")
	public Object[][] noneProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata()}, // nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456")}, 
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456")}, 
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata")} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".NONE"},dataProvider="noneProvider")
	public void testNone(CredenzialiInvocazione credenzialiInvocazione) throws Exception{
		// Qualsiasi siano le credenziali l'invocazione va a buon fine
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_NONE, null,
				credenzialiInvocazione, addIDUnivoco, 
				true, null, null, null, false, null,200);
	}
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="basicProvider")
	public Object[][] basicProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(),
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND, 
					null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC,
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_2,
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"), 
					IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS, 
					null,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic4", "123456"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
					null, -1,true, 200}, // crendeziali corrette di un applicativo
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,  IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasic(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,  IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasic(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testBasic(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_OPTIONAL"},dataProvider="basicProvider")
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				200);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,  IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasicForwardAuthorization(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,  IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testBasicForwardAuthorization(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testBasicForwardAuthorization(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_FORWARD_AUTHORIZATION, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
					null,  
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500,
					null, null},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL,
						null, -1,true, 200,
						new String[] {"CN=client, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL_2,
						null, -1,true, 200,
						new String[] {"CN=client2, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null,
						null, 
						null,-1, true, 200,
						new String[] {"CN=client3, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // credenziali corrette (anche se non registrate sul registro)
				
				// Credenziali corrette con caricamento certificato
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto1_multipleOU.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT1,
						null, -1,true, 200,
						new String[] {"CN=soggetto1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE,
						null, -1,true, 200,
						new String[] {"CN=soggetto1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto2_multipleOU.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT2,
						null, -1,true, 200,
						new String[] {"CN=soggetto2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT2_MULTIPLEOU_SERIALNUMBERDIFFERENTE,
						null, -1,true, 200,
						new String[] {"CN=soggetto2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						null},
				
				// Credenziali corrette con caricamento certificato doppio associato allo stesso applicativo
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto1_alternativo.jks", "openspcoopjks", "openspcoop"), 
							null,
							CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT1,
							null, -1,true, 200,
							new String[] {"CN=soggetto1alternativo","C=IT","L=Pisa"},
							null},
				
				// Credenziali corrette con caricamento certificato (applicativo)
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1_serialNumberDifferente"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null,
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2_serialNumberDifferente"},
				
				// Credenziali corrette con caricamento certificato doppio associato allo stesso applicativo
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_alternativo.jks", "openspcoopjks", "openspcoop"), 
							null,
							CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
							null, -1,true, 200,
							new String[] {"CN=applicativo1alternativo","C=IT","L=Pisa"},
							"EsempioFruitoreTrasparenteCert1"},
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso, 
			String [] credenziale, 
			String nomeServizioApplicativo) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testSsl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso,
					credenziale,
					nomeServizioApplicativo);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso, 
			String [] credenziale, 
			String nomeServizioApplicativo) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testSsl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso,
					credenziale,
					nomeServizioApplicativo);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testSsl(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo  ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
				dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
				
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL_OPTIONAL"},dataProvider="sslProvider")
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeSoggetto  ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		Utilities.sleep(5000);
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				200);
		
		if(credenziale!=null || nomeSoggetto!=null) {
			DatabaseMsgDiagnosticiComponent dataMsg = null;
			try{
				dataMsg = DatabaseProperties.getDatabaseComponentDiagnosticaErogatore();
				
				if(credenziale!=null && credenziale.length>0) {
					for (int i = 0; i < credenziale.length; i++) {
						Reporter.log("Cerco credenziale-"+i+" ["+credenziale[i]+"] nei log ...");
						Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, credenziale[i]));		
					}
				}
				
				if(nomeSoggetto!=null) {
					Reporter.log("Cerco nomeSoggetto ["+nomeSoggetto+"] nei log ...");
					Assert.assertTrue(dataMsg.isTracedMessaggioWithLike(dataInizioTest, nomeSoggetto));
				}
				
			}finally{
				dataMsg.close();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	@DataProvider(name="apiKeyProvider")
	public Object[][] apiKeyProvider() throws Exception{
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null,
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				// **  applicativi **
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey2@MinisteroFruitore.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456Errata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKeyErrato@MinisteroFruitore.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata@MinisteroFruitore.gw", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				
				// ** soggetti **
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APIKEY,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey2.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APIKEY_2, 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey.gw", "123456Errata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKeyErratos.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate

		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY"},dataProvider="apiKeyProvider")
	public void testApiKey_genericCode(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKey(credenzialiInvocazione, fruitore, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY"},dataProvider="apiKeyProvider")
	public void testApiKey_specificCode(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKey(credenzialiInvocazione, fruitore, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKey(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, 
			int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
			
			AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY, fruitore,
					credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
			
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY_OPTIONAL"},dataProvider="apiKeyProvider")
	public void testApiKeyOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false;
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {

			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
		
			AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_APIKEY, fruitore,
					credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
					200);
			
		}
	}
	
	
	
	@DataProvider(name="apiKeyForwardProvider")
	public Object[][] apiKeyForwardProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY_FORWARD"},dataProvider="apiKeyForwardProvider")
	public void testApiKeyForwardAuthorization_genericCode(PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardAuthorization(genericCode, posizione, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY_FORWARD"},dataProvider="apiKeyForwardProvider")
	public void testApiKeyForwardAuthorization_specificCode(PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardAuthorization(genericCode, posizione, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKeyForwardAuthorization(boolean genericCode, PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		IDSoggetto fruitore = CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APIKEY;
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		
		AuthUtilities.testPortaApplicativa(nomePortaApplicativa, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
	
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
			    {PosizioneCredenziale.QUERY, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY_CUSTOM,
								null,
								null, -1,true, 200}, // crendeziali corrette
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.HEADER, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER_CUSTOM,
								IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
								CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APIKEY_COOKIE_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY_FORWARD_CUSTOM"},dataProvider="apiKeyForwardCustomProvider")
	public void testApiKeyForwardCustomAuthorization_genericCode(PosizioneCredenziale posizione, String customName, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardCustomAuthorization(genericCode, posizione, customName, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APIKEY_FORWARD_CUSTOM"},dataProvider="apiKeyForwardCustomProvider")
	public void testApiKeyForwardCustomAuthorization_specificCode(PosizioneCredenziale posizione,  String customName, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testApiKeyForwardCustomAuthorization(genericCode, posizione, customName, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testApiKeyForwardCustomAuthorization(boolean genericCode, PosizioneCredenziale posizione,  String customName, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		IDSoggetto fruitore = CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APIKEY;
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		if(customName!=null) {
			credenzialiInvocazione.setNomePosizioneApiKey(customName);
		}
		
		AuthUtilities.testPortaApplicativa(nomePortaApplicativa, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		
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
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null,
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				// ** applicativi **
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId2@MinisteroFruitore.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppId@MinisteroFruitore.gw", "123456Errata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioFruitoreTrasparenteApiKeyAppIdErrato@MinisteroFruitore.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata", "credenzialeErrata"),null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioFruitoreTrasparenteApiKey@MinisteroFruitore.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata@MinisteroFruitore.gw", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale

				// ** soggetti **
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APPID,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId2.gw", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APPID_2, 
						null,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId.gw", "123456Errata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppIdErratos.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneMultipleApiKey("credenzialeErrata22", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_INVALID_CREDENTIALS,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500}, // credenziali errate
				
				{CredenzialiInvocazione.getAutenticazioneApiKey("EsempioSoggettoTrasparenteApiKey.gw", "123456"), null,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(), true, 500}, // credenziali errate
				{CredenzialiInvocazione.getAutenticazioneApiKey("credenzialeErrata.gw", "credenzialeErrata"), null,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(), true, 500}, // credenziali errate

		
		
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID"},dataProvider="appIdProvider")
	public void testAppId_genericCode(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppId(credenzialiInvocazione, fruitore, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID"},dataProvider="appIdProvider")
	public void testAppId_specificCode(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppId(credenzialiInvocazione, fruitore, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppId(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, boolean genericCode, IntegrationFunctionError integrationFunctionError, String erroreAtteso, 
			int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
			
			AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID, fruitore,
					credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID_OPTIONAL"},dataProvider="appIdProvider")
	public void testAppIdOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false;
		
		PosizioneCredenziale [] posizione = PosizioneCredenziale.values();
		
		for (int i = 0; i < posizione.length; i++) {
			
			PosizioneCredenziale pos = posizione[i];
			Reporter.log("Test per posizione '"+pos+"' ...");
			credenzialiInvocazione.setPosizioneApiKey(pos);
			credenzialiInvocazione.setPosizioneAppId(pos);
		
			AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_APPID, fruitore,
					credenzialiInvocazione, addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
					200);
			
		}
	}
	
	
	
	
	
	
	
	@DataProvider(name="appIdForwardProvider")
	public Object[][] appIdForwardProvider() throws Exception{
		return new Object[][]{
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY,
						null,
						null, -1,true, 200}, // crendeziali corrette
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID_FORWARD"},dataProvider="appIdForwardProvider")
	public void testAppIdForwardAuthorization_genericCode(PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardAuthorization(genericCode, posizione, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID_FORWARD"},dataProvider="appIdForwardProvider")
	public void testAppIdForwardAuthorization_specificCode(PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardAuthorization(genericCode, posizione, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppIdForwardAuthorization(boolean genericCode, PosizioneCredenziale posizione, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		IDSoggetto fruitore = CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APPID;
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		
		AuthUtilities.testPortaApplicativa(nomePortaApplicativa, fruitore, 
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
			    {PosizioneCredenziale.QUERY, null, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY_CUSTOM,
								null,
								null, -1,true, 200}, // crendeziali corrette
			    {PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.QUERY, CostantiTestSuite.APIKEY_QUERY_CUSTOM, CostantiTestSuite.APPID_QUERY_CUSTOM , CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				
				{PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.HEADER, null, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER_CUSTOM,
								IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
								CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
				 {PosizioneCredenziale.HEADER, CostantiTestSuite.APIKEY_HEADER_CUSTOM, CostantiTestSuite.APPID_HEADER_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
					
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,  CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_QUERY_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_HEADER_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				 {PosizioneCredenziale.COOKIE, null, null, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE_CUSTOM,
							IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
							CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{PosizioneCredenziale.COOKIE, CostantiTestSuite.APIKEY_COOKIE_CUSTOM,CostantiTestSuite.APPID_COOKIE_CUSTOM, CostantiTestSuite.PORTA_APPLICATIVA_AUTH_APPID_COOKIE_CUSTOM,
							null,
							null, -1,true, 200}, // crendeziali corrette
			};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID_FORWARD_CUSTOM"},dataProvider="appIdForwardCustomProvider")
	public void testAppIdForwardCustomAuthorization_genericCode(PosizioneCredenziale posizione, String customNameApiKey, String customNameAppId, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardCustomAuthorization(genericCode, posizione, customNameApiKey, customNameAppId, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".APPID_FORWARD_CUSTOM"},dataProvider="appIdForwardCustomProvider")
	public void testAppIdForwardCustomAuthorization_specificCode(PosizioneCredenziale posizione, String customNameApiKey, String customNameAppId, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testAppIdForwardCustomAuthorization(genericCode, posizione, customNameApiKey, customNameAppId, nomePortaApplicativa, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testAppIdForwardCustomAuthorization(boolean genericCode, PosizioneCredenziale posizione,  String customNameApiKey, String customNameAppId, String nomePortaApplicativa, IntegrationFunctionError integrationFunctionError, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		IDSoggetto fruitore = CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_APPID;
		CredenzialiInvocazione credenzialiInvocazione = CredenzialiInvocazione.getAutenticazioneMultipleApiKey("EsempioSoggettoTrasparenteApiKeyAppId.gw", "123456");
		credenzialiInvocazione.setPosizioneApiKey(posizione);
		credenzialiInvocazione.setPosizioneAppId(posizione);
		if(customNameApiKey!=null) {
			credenzialiInvocazione.setNomePosizioneApiKey(customNameApiKey);
		}
		if(customNameAppId!=null) {
			credenzialiInvocazione.setNomePosizioneAppId(customNameAppId);
		}
		
		AuthUtilities.testPortaApplicativa(nomePortaApplicativa, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
