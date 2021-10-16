/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it).
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

import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
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
 * AutorizzazionePortaDelegata
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AutorizzazionePortaDelegata extends GestioneViaJmx {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutorizzazionePortaDelegata";
	public static final String ID_GRUPPO_NO_PRINCIPAL = "AutorizzazionePortaDelegataNoPrincipal";
	
	
	@SuppressWarnings("unused")
	private Logger log = TrasparenteTestsuiteLogger.getInstance();
	
	protected AutorizzazionePortaDelegata() {
		super(org.openspcoop2.protocol.trasparente.testsuite.core.TestSuiteProperties.getInstance());
	}
	
	
	
	private static boolean addIDUnivoco = true;

	boolean doTestSpecificCodeContainer = true;
	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {AutorizzazionePortaDelegata.ID_GRUPPO, AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL})
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
	@AfterGroups (alwaysRun=true , groups={AutorizzazionePortaDelegata.ID_GRUPPO, AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL})
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
					null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"),
					IntegrationFunctionError.AUTHORIZATION_DENY,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparenteBasic2"),	
					CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED"},dataProvider="authenticatedProvider")
	public void test_authenticated_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_authenticated(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED"},dataProvider="authenticatedProvider")
	public void test_authenticated_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_authenticated(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	
	private void _test_authenticated(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_internalRolesAllProvider")
	public Object[][] Authenticated_or_internalRolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
					null,
					null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
					null,
					null, -1,true, 200}, // (perchè autenticato e nella lista dei s.a. autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"), 
					IntegrationFunctionError.AUTHORIZATION_DENY,
					CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
						replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparenteBasic3"),	
					CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_internalRolesAllProvider")
	public void test_Authenticated_or_internalRolesAll_genericCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = true;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_internalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_internalRolesAllProvider")
	public void test_Authenticated_or_internalRolesAll_specificCode(CredenzialiInvocazione credenzialiInvocazione, IntegrationFunctionError integrationFunctionError, 
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso) throws TestSuiteException, IOException, Exception{
		boolean genericCode = false;
		boolean unwrap = false;
		try {
			super.lockForCode(genericCode, unwrap);
			
			_test_Authenticated_or_internalRolesAll(credenzialiInvocazione, genericCode, integrationFunctionError, erroreAtteso, codiceErrore, ricercaEsatta, returnCodeAtteso);
		}finally {
			super.unlockForCode(genericCode);
		}
	}
	private void _test_Authenticated_or_internalRolesAll(CredenzialiInvocazione credenzialiInvocazione, boolean genericCode, IntegrationFunctionError integrationFunctionError,    
			String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_internalRolesAnyProvider")
	public Object[][] Authenticated_or_internalRolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"), 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO_NO_PRINCIPAL,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ANY"},dataProvider="Authenticated_or_internalRolesAnyProvider")
	public void test_Authenticated_or_internalRolesAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
				false, null, erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
