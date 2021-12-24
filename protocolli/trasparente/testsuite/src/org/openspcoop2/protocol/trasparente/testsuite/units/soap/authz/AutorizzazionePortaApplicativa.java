/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
import java.util.Vector;

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
public class AutorizzazionePortaApplicativa extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutorizzazionePortaApplicativa";
	public static final String ID_GRUPPO_NO_PRINCIPAL = "AutorizzazionePortaApplicativaNoPrincipal";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutorizzazionePortaApplicativa() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	private static boolean addIDUnivoco = true;

	boolean doTestSpecificCodeContainer = true;
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL})
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
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
	
	@DataProvider(name="authenticatedProvider")
	public Object[][] authenticatedProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
					null, 
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"), 
					IntegrationFunctionError.AUTHORIZATION_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_3,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_3.toString()),	
						CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
				
				// applicativi
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparente1_utilizzatoErogazione", "123456"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE, 
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparente2_utilizzatoErogazione", "123456"), 
					IntegrationFunctionError.AUTHORIZATION_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_FRUITORE,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_APPLICATIVO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_FRUITORE.toString()).
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_APPLICATIVO_TEMPLATE, "EsempioFruitoreTrasparente2_utilizzatoErogazione"),	
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED"},dataProvider="authenticatedProvider")
	public void test_authenticated_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_authenticated(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED"},dataProvider="authenticatedProvider")
	public void test_authenticated_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_authenticated(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_authenticated(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED, fruitore,
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
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_internalRolesAllProvider")
	public Object[][] Authenticated_or_internalRolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC, 
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"),
					null,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"),
					IntegrationFunctionError.AUTHORIZATION_DENY,
					CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_3, 
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SOGGETTO_TEMPLATE, CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_3.toString()),
					CodiceErroreCooperazione.SICUREZZA_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_internalRolesAllProvider")
	public void test_Authenticated_or_internalRolesAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_internalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_internalRolesAllProvider")
	public void test_Authenticated_or_internalRolesAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_internalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, fruitore, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_Authenticated_or_internalRolesAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,  
			IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ALL, fruitore,
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
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_internalRolesAnyProvider")
	public Object[][] Authenticated_or_internalRolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC, 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_2, 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"), CostantiTestSuite.PROXY_SOGGETTO_TRASPARENTE_BASIC_3, 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaApplicativa.ID_GRUPPO,AutorizzazionePortaApplicativa.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaApplicativa.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ANY"},dataProvider="Authenticated_or_internalRolesAnyProvider")
	public void test_Authenticated_or_internalRolesAny(CredenzialiInvocazione credenzialiInvocazione, IDSoggetto fruitore, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaApplicativa(CostantiTestSuite.PORTA_APPLICATIVA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ANY, fruitore,
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
	
	
	
	
}
