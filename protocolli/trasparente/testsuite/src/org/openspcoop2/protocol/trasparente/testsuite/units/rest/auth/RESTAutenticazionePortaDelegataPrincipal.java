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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.auth;

import java.util.Date;
import java.util.List;

import org.openspcoop2.core.config.constants.TipoAutenticazionePrincipal;
import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.protocol.trasparente.testsuite.units.utils.WWWAuthenticateUtils;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
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
public class RESTAutenticazionePortaDelegataPrincipal {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO_PRINCIPAL = "RESTAutenticazionePortaDelegataPrincipal";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups= {RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegataPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private List<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new java.util.ArrayList<>();
	@AfterGroups (alwaysRun=true , groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegataPrincipal.ID_GRUPPO_PRINCIPAL})
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	
	
//	principal 1) Invocazione dove il client non fornisce le credenziali richieste dall'autenticazione 'principal'. 
//	Si attende un 401 ritornato da GovWay e un corretto WWW-Authenticate header.
	
	@DataProvider(name="principalWWWAuthenticateProvider")
	public Object[][] principalWWWAuthenticateProvider(){
		return new Object[][]{
				{TipoAutenticazionePrincipal.CONTAINER},
				{TipoAutenticazionePrincipal.HEADER},
				{TipoAutenticazionePrincipal.FORM},
				{TipoAutenticazionePrincipal.URL},
				// non può fallire {TipoAutenticazionePrincipal.INDIRIZZO_IP},
				{TipoAutenticazionePrincipal.INDIRIZZO_IP_X_FORWARDED_FOR},
				{TipoAutenticazionePrincipal.TOKEN}
		};
	}
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegataPrincipal.ID_GRUPPO_PRINCIPAL,RESTAutenticazionePortaDelegata.ID_GRUPPO+".AuthPrincipal_WWWAuthenticate_credenzialiNonFornite"},dataProvider="principalWWWAuthenticateProvider")
	public void testAuthPrincipalWWWAuthenticate_credenzialiNonFornite(TipoAutenticazionePrincipal tipo) throws TestSuiteException, Exception{
		
		String nomePorta = null;
		String authType = null;
		String realm = null;
		String error_description = null;
		switch (tipo) {
		case CONTAINER:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_CONTAINER;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_CONTAINER_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_CONTAINER_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_CONTAINER_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case HEADER:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_HEADER;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_HEADER_BASED_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_HEADER_BASED_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_HEADER_BASED_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case FORM:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_QUERY;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORM_BASED_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORM_BASED_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORM_BASED_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case URL:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_URL;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_URL_BASED_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_URL_BASED_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_URL_BASED_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case INDIRIZZO_IP:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_IPADDRESS;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_INDIRIZZO_IP_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_INDIRIZZO_IP_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_INDIRIZZO_IP_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case INDIRIZZO_IP_X_FORWARDED_FOR:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_IPFORWARDED;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORWARDED_FOR_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORWARDED_FOR_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_FORWARDED_FOR_ERROR_DESCRIPTION_NOTFOUND;
			break;
		case TOKEN:
			nomePorta = CostantiTestSuite.PORTA_DELEGATA_REST_PRINCIPAL_TOKEN;
			authType = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_TOKEN_AUTHTYPE;
			realm = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_TOKEN_REALM;
			error_description = CostantiTestSuite.TEST_WWWAUTHENTICATE_PRINCIPAL_TOKEN_ERROR_DESCRIPTION_NOTFOUND;
			break;
		}
		
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(nomePorta);
		HttpResponse httpResponse = restCore.invokeAuthenticationError("json", 401, true, true, false, "text/json", null);
		String wwwAuthenticateGovWay = httpResponse.getHeaderFirstValue(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		WWWAuthenticateUtils.verify(wwwAuthenticateGovWay, 
				authType, 
				realm, 
				error_description);
	}
}
