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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.authz;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.core.TrasparenteTestsuiteLogger;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.testsuite.units.GestioneViaJmx;
import org.openspcoop2.utils.date.DateManager;
import org.slf4j.Logger;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * AutorizzazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutorizzazionePortaApplicativaPrincipal extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO_PRINCIPAL = "AutorizzazionePortaApplicativaPrincipal";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutorizzazionePortaApplicativaPrincipal() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	private static boolean addIDUnivoco = true;

	boolean doTestSpecificCodeContainer = true;
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
		
		try{
			String version_jbossas = org.openspcoop2.protocol.trasparente.testsuite.core.Utilities.readApplicationServerVersion();
			if(version_jbossas.startsWith("tomcat")){
				System.out.println("WARNING: Verifiche ruoli disabilitate per Tomcat");
				this.doTestSpecificCodeContainer = false;
			}
		}catch(Exception e){
			System.err.println("Identificazione A.S. non riuscita: "+e.getMessage());
			e.printStackTrace(System.out);
		}
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun=true , groups= {AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="rolesAllProvider")
	public Object[][] rolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_ALL"},dataProvider="rolesAllProvider")
	public void test_rolesAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_ALL"},dataProvider="rolesAllProvider")
	public void test_rolesAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_rolesAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_ROLES_ALL, fruitore,
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
	
	
	
	
	
	@DataProvider(name="rolesAnyProvider")
	public Object[][] rolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_ANY"},dataProvider="rolesAnyProvider")
	public void test_rolesAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_ROLES_ANY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	@DataProvider(name="rolesInternalAllProvider")
	public Object[][] rolesInternalAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_INTERNAL_ALL"},dataProvider="rolesInternalAllProvider")
	public void test_rolesInternalAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesInternalAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_INTERNAL_ALL"},dataProvider="rolesInternalAllProvider")
	public void test_rolesInternalAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesInternalAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_rolesInternalAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_INTERNAL_ROLES_ALL, fruitore,
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
	
	
	
	
	
	
	
	
	@DataProvider(name="rolesInternalAnyProvider")
	public Object[][] rolesInternalAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_INTERNAL_ANY"},dataProvider="rolesInternalAnyProvider")
	public void test_rolesInternalAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_INTERNAL_ROLES_ANY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	@DataProvider(name="rolesExternalAllProvider")
	public Object[][] rolesExternalAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ALL"},dataProvider="rolesExternalAllProvider")
	public void test_rolesExternalAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesExternalAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ALL"},dataProvider="rolesExternalAllProvider")
	public void test_rolesExternalAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesExternalAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_rolesExternalAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ALL, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
				30000); // readTimeout);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	@DataProvider(name="rolesExternalAnyProvider")
	public Object[][] rolesExternalAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ANY"},dataProvider="rolesExternalAnyProvider")
	public void test_rolesExternalAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ANY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
				30000);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	@DataProvider(name="rolesExternalNoAuthenticationAllProvider")
	public Object[][] rolesExternalNoAuthenticationAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					null, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					null,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO,	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ALL_NO_AUTHENTICATION"},dataProvider="rolesExternalNoAuthenticationAllProvider")
	public void test_rolesExternalNoAuthenticationAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesExternalNoAuthenticationAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ALL_NO_AUTHENTICATION"},dataProvider="rolesExternalNoAuthenticationAllProvider")
	public void test_rolesExternalNoAuthenticationAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_rolesExternalNoAuthenticationAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_rolesExternalNoAuthenticationAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ALL_NO_AUTHENTICATION, fruitore,
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
	
	
	
	
	
	
	
	
	@DataProvider(name="rolesExternalNoAuthenticationAnyProvider")
	public Object[][] rolesExternalNoAuthenticationAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), null, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), null, 
					null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".ROLES_EXTERNAL_ANY_NO_AUTHENTICATION"},dataProvider="rolesExternalNoAuthenticationAnyProvider")
	public void test_rolesExternalNoAuthenticationAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_ROLES_ANY_NO_AUTHENTICATION, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_rolesAllProvider")
	public Object[][] Authenticated_or_rolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"),
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					null,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO,	
						CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_ROLES_ALL"},dataProvider="Authenticated_or_rolesAllProvider")
	public void test_Authenticated_or_rolesAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_rolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_ROLES_ALL"},dataProvider="Authenticated_or_rolesAllProvider")
	public void test_Authenticated_or_rolesAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_rolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_Authenticated_or_rolesAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_ROLES_ALL, fruitore,
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
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_rolesAnyProvider")
	public Object[][] Authenticated_or_rolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), null, 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_ROLES_ANY"},dataProvider="Authenticated_or_rolesAnyProvider")
	public void test_Authenticated_or_rolesAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_ROLES_ANY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_externalRolesAllProvider")
	public Object[][] Authenticated_or_externalRolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"),
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_MISSING_ROLE,
					null,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO,	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_EXTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_externalRolesAllProvider")
	public void test_Authenticated_or_externalRolesAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_externalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_EXTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_externalRolesAllProvider")
	public void test_Authenticated_or_externalRolesAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		
		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_externalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_Authenticated_or_externalRolesAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ALL, fruitore,
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
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_externalRolesAnyProvider")
	public Object[][] Authenticated_or_externalRolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), null, 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_EXTERNAL_ROLES_ANY"},dataProvider="Authenticated_or_externalRolesAnyProvider")
	public void test_Authenticated_or_externalRolesAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ANY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
		}
	}
	
	
	
	
	
	
	
	
	
	@DataProvider(name="XacmlPolicy_Provider")
	public Object[][] XacmlPolicy_Provider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_POLICY_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO_XACML_POLICY.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".XACML_POLICY"},dataProvider="XacmlPolicy_Provider")
	public void test_XacmlPolicy_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_XacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".XACML_POLICY"},dataProvider="XacmlPolicy_Provider")
	public void test_XacmlPolicy_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_XacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_XacmlPolicy(CredenzialiInvocazione credenzialiInvocazione,  boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_XACML_POLICY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
				30000);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
			
			ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
			err2.setIntervalloInferiore(dataInizioTest);
			err2.setIntervalloSuperiore(dataFineTest);
			err2.setMsgErrore("Autorizzazione con XACMLPolicy fallita");
			this.erroriAttesiOpenSPCoopCore.add(err2);
			
		}
	}
	
	
	
	
	
	
	
	
	
	@DataProvider(name="InternalXacmlPolicy_Provider")
	public Object[][] InternalXacmlPolicy_Provider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"),
					IntegrationFunctionError.AUTHORIZATION_POLICY_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO_XACML_POLICY.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".INTERNAL_XACML_POLICY"},dataProvider="InternalXacmlPolicy_Provider")
	public void test_InternalXacmlPolicy_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_InternalXacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".INTERNAL_XACML_POLICY"},dataProvider="InternalXacmlPolicy_Provider")
	public void test_InternalXacmlPolicy_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_InternalXacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_InternalXacmlPolicy(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,   
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_INTERNAL_XACML_POLICY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
			
			ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
			err2.setIntervalloInferiore(dataInizioTest);
			err2.setIntervalloSuperiore(dataFineTest);
			err2.setMsgErrore("Autorizzazione con XACMLPolicy fallita");
			this.erroriAttesiOpenSPCoopCore.add(err2);
			
		}
	}
	
	
	
	
	
	
	
	
	
	@DataProvider(name="ExternalXacmlPolicy_Provider")
	public Object[][] ExternalXacmlPolicy_Provider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_POLICY_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2, 
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO_XACML_POLICY.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_PRINCIPAL_2.toString()),
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".EXTERNAL_XACML_POLICY"},dataProvider="ExternalXacmlPolicy_Provider")
	public void test_ExternalXacmlPolicy_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_ExternalXacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".EXTERNAL_XACML_POLICY"},dataProvider="ExternalXacmlPolicy_Provider")
	public void test_ExternalXacmlPolicy_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_ExternalXacmlPolicy(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_ExternalXacmlPolicy(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_XACML_POLICY, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
			
			ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
			err2.setIntervalloInferiore(dataInizioTest);
			err2.setIntervalloSuperiore(dataFineTest);
			err2.setMsgErrore("Autorizzazione con XACMLPolicy fallita");
			this.erroriAttesiOpenSPCoopCore.add(err2);
			
		}
	}
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="ExternalXacmlPolicy_noAuthentication_Provider")
	public Object[][] ExternalXacmlPolicy_noAuthentication_Provider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null,
					null, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					IntegrationFunctionError.AUTHORIZATION_POLICY_DENY,
					null, 
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_ANONIMO_NON_AUTORIZZATO_XACML_POLICY,
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".EXTERNAL_XACML_POLICY_NO_AUTHENTICATION"},dataProvider="ExternalXacmlPolicy_noAuthentication_Provider")
	public void test_ExternalXacmlPolicy_noAuthentication_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_ExternalXacmlPolicy_noAuthentication(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativaPrincipal.ID_GRUPPO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".EXTERNAL_XACML_POLICY_NO_AUTHENTICATION"},dataProvider="ExternalXacmlPolicy_noAuthentication_Provider")
	public void test_ExternalXacmlPolicy_noAuthentication_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{

		if(!this.doTestSpecificCodeContainer) {
			return;
		}
		
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_ExternalXacmlPolicy_noAuthentication(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_ExternalXacmlPolicy_noAuthentication(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_EXTERNAL_XACML_POLICY_NO_AUTHENTICATION, fruitore,
				credenzialiInvocazione, addIDUnivoco, 
				genericCode, integrationFunctionError, erroreAtteso, CodiceErroreCooperazione.toCodiceErroreCooperazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
		if(erroreAtteso!=null) {
			Date dataFineTest = DateManager.getDate();
			
			ErroreAttesoOpenSPCoopLogCore err = new ErroreAttesoOpenSPCoopLogCore();
			err.setIntervalloInferiore(dataInizioTest);
			err.setIntervalloSuperiore(dataFineTest);
			err.setMsgErrore(erroreAtteso);
			this.erroriAttesiOpenSPCoopCore.add(err);
			
			ErroreAttesoOpenSPCoopLogCore err2 = new ErroreAttesoOpenSPCoopLogCore();
			err2.setIntervalloInferiore(dataInizioTest);
			err2.setIntervalloSuperiore(dataFineTest);
			err2.setMsgErrore("Autorizzazione con XACMLPolicy fallita");
			this.erroriAttesiOpenSPCoopCore.add(err2);
			
		}
	}
	
}
