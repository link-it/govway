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

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openspcoop2.core.config.constants.TipoAutenticazione;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;
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
public class AutenticazionePortaApplicativaPrincipal extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO_PRINCIPAL = "AutenticazionePortaApplicativaPrincipal";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutenticazionePortaApplicativaPrincipal() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	private boolean expected401_wildfly_security_domain = false;
	@BeforeGroups (alwaysRun=true , groups= {AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
		try{
			String version_jbossas = org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.readApplicationServerVersion();
			if(version_jbossas.startsWith("wildfly")){
				String s = version_jbossas.substring("wildfly".length());
				int v = Integer.valueOf(s);
				if(v>=25) {
					System.out.println("WARNING: Verifica 401 abilitata per wildfly>=25");
					this.expected401_wildfly_security_domain = true;
				}
			}
		}catch(Exception e){
			System.err.println("Identificazione A.S. non riuscita: "+e.getMessage());
			e.printStackTrace(System.out);
		}
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	
	
	
	
	@DataProvider(name="principalProvider")
	public Object[][] principalProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneDisabilitata(), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL,
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparentePrincipal1", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHENTICATION_CREDENTIALS_NOT_FOUND,
					null, 
					CostantiTestSuite.MESSAGGIO_AUTENTICAZIONE_FALLITA_CREDENZIALI_NON_FORNITE,	CodiceErroreCooperazione.MITTENTE_NON_VALORIZZATO.getCodice(),true, 500},// nessuna credenziale (non si passa tramite il container e il principal non viene valorizzato)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					null,
					null, 
					null,-1, true, 200}, // credenziali corrette (anche se non registrate sul registro)
				{CredenzialiInvocazione.getAutenticazionePrincipal("credenzialeErrata", "credenzialeErrata"), 
					null,
					null, 
					null, -1,true, 401} // credenziali errate (non registrate nel container)
		};
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipal(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL"},dataProvider="principalProvider")
	public void testPrincipal_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipal(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipal(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				stato = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato,
				30000); // readTimeout);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalHeaderClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_CLEAN, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError ,erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderNotClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalHeaderNotClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalHeaderNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalHeaderNotClean(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_HEADER_NOT_CLEAN, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_HEADER_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalHeaderOptional_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				stato = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = "PRINCIPAL-HEADER";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_HEADER, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato,
				30000); // readTimeout
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalQueryClean(CredenzialiInvocazione credenzialiInvocazione,boolean genericCode, IntegrationFunctionError integrationFunctionError,   
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}

		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_CLEAN, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryNotClean_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_NOT_CLEAN"},dataProvider="principalProvider")
	public void testPrincipalQueryNotClean_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalQueryNotClean(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalQueryNotClean(CredenzialiInvocazione credenzialiInvocazione,boolean genericCode, IntegrationFunctionError integrationFunctionError,    
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_QUERY_NOT_CLEAN, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_QUERY_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalQueryOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,     
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				stato = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_QUERY, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalUrl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_URL"},dataProvider="principalProvider")
	public void testPrincipalUrl_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalUrl(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalUrl(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,     
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				returnCodeAtteso = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_URL, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_URL_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalUrlOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,    
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				stato = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String query = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			query = "TEST_PRINCIPAL";
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_URL, fruitore,
				credenzialiInvocazione, null, query, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato,
				30000); // readTimeout);
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_IP"})
	public void testPrincipalIp() throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_IP, null,
				CredenzialiInvocazione.getAutenticazioneDisabilitata(), null, null, 
				addIDUnivoco, 
				true, null, null, null, true, dataInizioTest,
				200,
				30000); // readTimeout);
		
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_IP_FORWARDED"},dataProvider="principalProvider")
	public void testPrincipalIpForwarded_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalIpForwarded(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_IP_FORWARDED"},dataProvider="principalProvider")
	public void testPrincipalIpForwarded_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_testPrincipalIpForwarded(credenzialiInvocazione, genericCode, integrationFunctionError, 
					fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _testPrincipalIpForwarded(CredenzialiInvocazione credenzialiInvocazione,boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		
		List<String> listHeaders = HttpUtilities.getClientAddressHeaders(); 
		for (String headerTest : listHeaders) {
			
			Reporter.log("** '"+headerTest+"' **");
			
			Date dataInizioTest = DateManager.getDate();
			
			if(this.expected401_wildfly_security_domain) {
				if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
					System.out.println("Force 401 per security-domain wildfly>=25");
					returnCodeAtteso = 401;
					codiceErrore = -1;
					erroreAtteso = null;
					integrationFunctionError = null;
				}
			}
			
			String header = null;
			if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
					TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
				header = headerTest;
				credenzialiInvocazione.setUsername("192.168.2.3/255.255.255.0");
			}
			
			AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_PRINCIPAL_IP_FORWARDED, fruitore,
					credenzialiInvocazione, header, null, 
					addIDUnivoco, 
					genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest,
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
	}
	
	@Test(groups={AutenticazionePortaApplicativa.ID_GRUPPO,AutenticazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutenticazionePortaApplicativa.ID_GRUPPO+".PRINCIPAL_IP_FORWARDED_OPTIONAL"},dataProvider="principalProvider")
	public void testPrincipalIpForwardedOptional(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		// Con autenticazione opzionale tutte le invocazioni avvengono con successo.
		// Fatta eccezione per le credenziali non riconosciute dal container ssl
		int stato = 200;
		if(returnCodeAtteso==401) {
			stato = 401;
		}
		ricercaEsatta = false; // il diagnostico e' arricchito dell'informazione che l'autenticazione e' opzionale
		boolean genericCode = false; // il dettaglio finisce nel diagnostico		
		
		if(this.expected401_wildfly_security_domain) {
			if(credenzialiInvocazione!=null && TipoAutenticazione.BASIC.equals(credenzialiInvocazione.getAutenticazione()) && !"esempioFruitoreTrasparentePrincipal1".equals(credenzialiInvocazione.getUsername())) {
				System.out.println("Force 401 per security-domain wildfly>=25");
				stato = 401;
				codiceErrore = -1;
				erroreAtteso = null;
				integrationFunctionError = null;
			}
		}
		
		String header = null;
		if(credenzialiInvocazione!=null && credenzialiInvocazione.getUsername()!=null && returnCodeAtteso!=401 &&
				TipoAutenticazione.PRINCIPAL.equals(credenzialiInvocazione.getAutenticazione())) {
			header = HttpUtilities.getClientAddressHeaders().get(0);
			credenzialiInvocazione.setUsername("192.168.2.3/255.255.255.0");
		}
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTH_OPTIONAL_PRINCIPAL_URL, fruitore,
				credenzialiInvocazione, header, null, 
				addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta,  DateManager.getDate(),
				stato,
				30000); // readTimeout);
	}
	

	
	
}
