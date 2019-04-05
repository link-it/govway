/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it).
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

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
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
public class AutenticazionePortaApplicativa {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaApplicativa";
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=AutenticazionePortaApplicativa.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=AutenticazionePortaApplicativa.ID_GRUPPO)
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".NONE"},dataProvider="noneProvider")
	public void testNone(CredenzialiInvocazione credenzialiInvocazione) throws Exception{
		// Qualsiasi siano le credenziali l'invocazione va a buon fine
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_NONE, null,
				credenzialiInvocazione, addIDUnivoco, 
				null, null, false, null,200);
	}
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="basicProvider")
	public Object[][] basicProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_2,
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"), null,
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreCooperazione.MITTENTE_SCONOSCIUTO.getCodice(), true, 500} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_OPTIONAL"},dataProvider="basicProvider")
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_BASIC, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				200);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_BASIC_FORWARD_AUTHORIZATION, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null,  
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500,
					null, null},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), 
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL,
						null, -1,true, 200,
						new String[] {"CN=client, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), 
							CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_SSL_2,
						null, -1,true, 200,
						new String[] {"CN=client2, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), 
							null, 
						null,-1, true, 200,
						new String[] {"CN=client3, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // credenziali corrette (anche se non registrate sul registro)
				
				// Credenziali corrette con caricamento certificato
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto1_multipleOU.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT1,
						null, -1,true, 200,
						new String[] {"CN=soggetto1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT1_SERIALNUMBERDIFFERENTE,
						null, -1,true, 200,
						new String[] {"CN=soggetto1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto2_multipleOU.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT2,
						null, -1,true, 200,
						new String[] {"CN=soggetto2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						null},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/soggetto2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_CERT2_MULTIPLEOU_SERIALNUMBERDIFFERENTE,
						null, -1,true, 200,
						new String[] {"CN=soggetto2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						null},
				
				// Credenziali corrette con caricamento certificato (applicativo)
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1_serialNumberDifferente"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2_serialNumberDifferente"},
		};
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo  ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".SSL_OPTIONAL"},dataProvider="sslProvider")
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeSoggetto  ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		Utilities.sleep(5000);
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_SSL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
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
	
	
	
	
	
	
	@DataProvider(name="principalProvider")
	public Object[][] principalProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL,
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"), null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), null, 
						null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"), null, 
						null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_CLEAN, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderNotClean(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalHeaderOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_HEADER, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_CLEAN, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryNotClean(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalQueryOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_QUERY, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_URL, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_URL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalUrlOptional(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_URL, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_IP"})
	public void testPrincipalIp() throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_IP, null,
				CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, null, 
				addIDUnivoco, 
				null, null, true, dataInizioTest,
				200);
		
	}
	
}
