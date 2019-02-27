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

package org.openspcoop2.protocol.trasparente.testsuite.units.soap.authz;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.AuthUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.CredenzialiInvocazione;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.utils.date.DateManager;
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
public class AutorizzazionePortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "AutorizzazionePortaDelegata";
	
	private static boolean addIDUnivoco = true;

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=AutorizzazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=AutorizzazionePortaDelegata.ID_GRUPPO)
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
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparenteBasic2"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED"},dataProvider="authenticatedProvider")
	public void test_authenticated(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	@DataProvider(name="rolesAllProvider")
	public Object[][] rolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_ALL"},dataProvider="rolesAllProvider")
	public void test_rolesAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	@DataProvider(name="rolesAnyProvider")
	public Object[][] rolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_ANY"},dataProvider="rolesAnyProvider")
	public void test_rolesAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	@DataProvider(name="rolesInternalAllProvider")
	public Object[][] rolesInternalAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
								replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
							CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_INTERNAL_ALL"},dataProvider="rolesInternalAllProvider")
	public void test_rolesInternalAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_INTERNAL_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	@DataProvider(name="rolesInternalAnyProvider")
	public Object[][] rolesInternalAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_INTERNAL_ANY"},dataProvider="rolesInternalAnyProvider")
	public void test_rolesInternalAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_INTERNAL_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	@DataProvider(name="rolesExternalAllProvider")
	public Object[][] rolesExternalAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
								replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
							CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_EXTERNAL_ALL"},dataProvider="rolesExternalAllProvider")
	public void test_rolesExternalAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	@DataProvider(name="rolesExternalAnyProvider")
	public Object[][] rolesExternalAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_EXTERNAL_ANY"},dataProvider="rolesExternalAnyProvider")
	public void test_rolesExternalAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso,
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
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
								replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "Anonimo"),	
							CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_EXTERNAL_ALL_NO_AUTHENTICATION"},dataProvider="rolesExternalNoAuthenticationAllProvider")
	public void test_rolesExternalNoAuthenticationAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ALL_NO_AUTHENTICATION, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	@DataProvider(name="rolesExternalNoAuthenticationAnyProvider")
	public Object[][] rolesExternalNoAuthenticationAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
							null, -1,true, 200}, // crendeziali corrette
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".ROLES_EXTERNAL_ANY_NO_AUTHENTICATION"},dataProvider="rolesExternalNoAuthenticationAnyProvider")
	public void test_rolesExternalNoAuthenticationAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_ROLES_ANY_NO_AUTHENTICATION, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_rolesAllProvider")
	public Object[][] Authenticated_or_rolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // (perchè autenticato e nella lista dei s.a. autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "Anonimo"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_ROLES_ALL"},dataProvider="Authenticated_or_rolesAllProvider")
	public void test_Authenticated_or_rolesAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_rolesAnyProvider")
	public Object[][] Authenticated_or_rolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_ROLES_ANY"},dataProvider="Authenticated_or_rolesAnyProvider")
	public void test_Authenticated_or_rolesAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_internalRolesAllProvider")
	public Object[][] Authenticated_or_internalRolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic", "123456"), 
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic2", "123456"), 
						null, -1,true, 200}, // (perchè autenticato e nella lista dei s.a. autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazioneBasic("esempioFruitoreTrasparenteBasic3", "123456"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparenteBasic3"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_internalRolesAllProvider")
	public void test_Authenticated_or_internalRolesAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_INTERNAL_ROLES_ANY"},dataProvider="Authenticated_or_internalRolesAnyProvider")
	public void test_Authenticated_or_internalRolesAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_INTERNAL_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_externalRolesAllProvider")
	public Object[][] Authenticated_or_externalRolesAllProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // (perchè autenticato e nella lista dei s.a. autorizzati per il servizio)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "Anonimo"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_EXTERNAL_ROLES_ALL"},dataProvider="Authenticated_or_externalRolesAllProvider")
	public void test_Authenticated_or_externalRolesAll(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ALL, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	@DataProvider(name="Authenticated_or_externalRolesAnyProvider")
	public Object[][] Authenticated_or_externalRolesAnyProvider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // crendeziali corrette (possiede almeno un ruolo)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // (perchè autenticato e nella lista dei soggetti autorizzati per il servizio) Inoltre in questo caso ha anche i ruoli
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal3", "Op3nSPC@@p2"), 
					null, -1,true, 200}, // possiede almeno un ruolo: roleTerzaUtenzaAllOpenSPCoopTest
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".AUTHENTICATED_OR_EXTERNAL_ROLES_ANY"},dataProvider="Authenticated_or_externalRolesAnyProvider")
	public void test_Authenticated_or_externalRolesAny(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_AUTHENTICATED_OR_EXTERNAL_ROLES_ANY, credenzialiInvocazione, addIDUnivoco, 
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@DataProvider(name="XacmlPolicy_Provider")
	public Object[][] XacmlPolicy_Provider(){
		return new Object[][]{
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal", "Op3nSPC@@p2"), 
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO_XACML_POLICY.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".XACML_POLICY"},dataProvider="XacmlPolicy_Provider")
	public void test_XacmlPolicy_(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_XACML_POLICY, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO_XACML_POLICY.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".INTERNAL_XACML_POLICY"},dataProvider="InternalXacmlPolicy_Provider")
	public void test_InternalXacmlPolicy_(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_INTERNAL_XACML_POLICY, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO_XACML_POLICY.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "EsempioFruitoreTrasparentePrincipal2"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".EXTERNAL_XACML_POLICY"},dataProvider="ExternalXacmlPolicy_Provider")
	public void test_ExternalXacmlPolicy_(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_XACML_POLICY, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
						null, -1,true, 200}, // crendeziali corrette (possiede i ruoli)
				{CredenzialiInvocazione.getAutenticazionePrincipal("esempioFruitoreTrasparentePrincipal2", "Op3nSPC@@p2"), 
						CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_NON_AUTORIZZATO_XACML_POLICY.
							replace(CostantiTestSuite.MESSAGGIO_AUTORIZZAZIONE_FALLITA_SA_TEMPLATE, "Anonimo"),	
						CodiceErroreIntegrazione.CODICE_404_AUTORIZZAZIONE_FALLITA.getCodice(),false, 500}, // non autorizzato
		};
	}
	@Test(groups={AutorizzazionePortaDelegata.ID_GRUPPO,AutorizzazionePortaDelegata.ID_GRUPPO+".EXTERNAL_XACML_POLICY_NO_AUTHENTICATION"},dataProvider="ExternalXacmlPolicy_noAuthentication_Provider")
	public void test_ExternalXacmlPolicy_noAuthentication_(CredenzialiInvocazione credenzialiInvocazione, String erroreAtteso, int codiceErrore, boolean ricercaEsatta, int returnCodeAtteso ) throws Exception{
		Date dataInizioTest = DateManager.getDate();
		
		AuthUtilities.testPortaDelegata(CostantiTestSuite.PORTA_DELEGATA_AUTHZ_EXTERNAL_XACML_POLICY_NO_AUTHENTICATION, credenzialiInvocazione, addIDUnivoco, 
				erroreAtteso, CodiceErroreIntegrazione.toCodiceErroreIntegrazione(codiceErrore), ricercaEsatta, dataInizioTest, returnCodeAtteso);
		
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
