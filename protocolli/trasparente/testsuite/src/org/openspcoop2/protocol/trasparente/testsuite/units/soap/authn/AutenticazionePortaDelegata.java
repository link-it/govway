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

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.DatabaseProperties;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.db.DatabaseMsgDiagnosticiComponent;
import org.openspcoop2.utils.date.DateManager;
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
public class AutenticazionePortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutenticazionePortaDelegata";
	
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
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("credenzialeErrata", "credenzialeErrata"), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_CORRETTE,CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(), true, 500} // credenziali errate
		};
	}
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC"},dataProvider="basicProvider")
	public void testBasic(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
	public void testBasicOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_BASIC, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
				200);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".BASIC_FORWARD_AUTHORIZATION"},dataProvider="basicProvider")
	public void testBasicForwardAuthorization(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_BASIC_FORWARD_AUTHORIZATION, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500,
					null,null},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client1_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null, -1,true, 200,
						new String[] {"CN=client, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						"EsempioFruitoreTrasparenteSsl"}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client2_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null, -1,true, 200,
						new String[] {"CN=client2, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						"EsempioFruitoreTrasparenteSsl2"}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/client3_trasparente.jks", "openspcoopjks", "openspcoop"), 
						null,-1, true, 200,
						new String[] {"CN=client3, OU=trasparente, O=openspcoop.org, L=Pisa, ST=Italy, C=IT, EMAILADDRESS=apoli@link.it"},
						null}, // credenziali corrette (anche se non registrate sul registro)
				
				// Credenziali corrette con caricamento certificato
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU.jks", "123456", "123456"), 
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo1_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null, -1,true, 200,
						new String[] {"CN=applicativo1_multipleOU","OU=\" Piano=2, Scala=B, porta=3\""},
						"EsempioFruitoreTrasparenteCert1_serialNumberDifferente"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU.jks", "123456", "123456"), 
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2"},
				{CredenzialiInvocazione.getAutenticazioneSsl("/etc/govway/keys/applicativo2_multipleOU_serialNumberDifferente.jks", "123456", "123456"), 
						null, -1,true, 200,
						new String[] {"CN=applicativo2_multipleOU","OU=\" Piano=2, Scala=B, porta=3\"","caratteri accentati"},
						"EsempioFruitoreTrasparenteCert2_serialNumberDifferente"},
		};
	}

	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".SSL"},dataProvider="sslProvider")
	public void testSsl(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_SSL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
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
	public void testSslOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso,
			String [] credenziale, 
			String nomeServizioApplicativo ) throws Exception{
		
		Date dataInizioTest = DateManager.getDate();
				
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_SSL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),  
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
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"), 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreIntegrazione.CODICE_402_AUTENTICAZIONE_FALLITA.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"), 
						null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(), 
				stato,
				30000); // readTimeout);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_CLEAN,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalHeaderNotClean(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalHeaderOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
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
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_HEADER,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_CLEAN,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalQueryNotClean(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalQueryOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
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
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_QUERY,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_URL,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	public void testPrincipalUrlOptional(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
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
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_OPTIONAL_PRINCIPAL_URL,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaDelegata.ID_GRUPPO,AutenticazionePortaDelegata.ID_GRUPPO+".PRINCIPAL_IP"})
	public void testPrincipalIp() throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTH_PRINCIPAL_IP,
				CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, null, 
				addIDUnivoco, 
				null, null, true, dataInizioTest,
				200);
		
	}
	
	
}
