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

package org.openspcoop2.protocol.trasparente.testsuite.units.rest.auth;

import java.util.Date;
import java.util.Vector;

import org.openspcoop2.protocol.trasparente.testsuite.core.CostantiTestSuite;
import org.openspcoop2.protocol.trasparente.testsuite.core.FileSystemUtilities;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore;
import org.openspcoop2.protocol.trasparente.testsuite.units.rest.RESTCore.RUOLO;
import org.openspcoop2.testsuite.core.ErroreAttesoOpenSPCoopLogCore;
import org.openspcoop2.testsuite.core.Repository;
import org.openspcoop2.testsuite.core.TestSuiteException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

/**
 * AutenticazionePortaApplicativa
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class RESTAutenticazionePortaDelegata {

	/** Identificativo del gruppo */
	public static final String ID_GRUPPO = "RESTAutenticazionePortaDelegata";
	

	
	private Date dataAvvioGruppoTest = null;
	@BeforeGroups (alwaysRun=true , groups=RESTAutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog_raccoltaTempoAvvioTest() throws Exception{
		this.dataAvvioGruppoTest = DateManager.getDate();
	} 	
	private Vector<ErroreAttesoOpenSPCoopLogCore> erroriAttesiOpenSPCoopCore = new Vector<ErroreAttesoOpenSPCoopLogCore>();
	@AfterGroups (alwaysRun=true , groups=RESTAutenticazionePortaDelegata.ID_GRUPPO)
	public void testOpenspcoopCoreLog() throws Exception{
		if(this.erroriAttesiOpenSPCoopCore.size()>0){
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest,
					this.erroriAttesiOpenSPCoopCore.toArray(new ErroreAttesoOpenSPCoopLogCore[1]));
		}else{
			FileSystemUtilities.verificaOpenspcoopCore(this.dataAvvioGruppoTest);
		}
	} 
	
	

//	1) Invocazione dove il client fornisce le credenziali corrette che non vengono "bruciate" dalla PdD (autenticazione:none) e vengono inoltrate al servizio. 
//	Si attende 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".ServiceWithBasicAuth_OK"})
	public void testServiceWithBasicAuth_ok() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	
	
//	2) Invocazione dove il client fornisce le credenziali errate che non vengono "bruciate" dalla PdD (autenticazione:none) e vengono inoltrate al servizio. 
//	Si attende 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiErrate"})
	public void testServiceWithBasicAuth_ko_credentialErrate() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
//	3) Invocazione dove il client non fornisce le credenziali (autenticazione:none). 
//	Si attende 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiNonFornite"})
	public void testServiceWithBasicAuth_ko_noCredential() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH);
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
	
//	4) Invocazione come 2 o 3 dove però il servizio finale ritorna anche l'header WWW-Authenticate
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiErrate_Domain"})
	public void testServiceWithBasicAuth_ko_credentialErrate_domain() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		HttpResponse response = restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		String domain = response.getHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		if(domain==null || "".equals(domain)) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' non trovato nella risposta");
		}
		String atteso = HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX+"TestSuiteOpenSPCoop2"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX; 
		if(atteso.equals(domain)==false) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' con un valore ("+domain+") differente da quello atteso("+atteso+")");
		}
		restCore.postInvoke(repository);
	}
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".ServiceWithBasicAuth_KO_CredenzialiNonFornite_Domain"})
	public void testServiceWithBasicAuth_ko_noCredential_domain() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_SERVICE_WITH_BASIC_AUTH_DOMAIN);
		restCore.setCredenziali("testsuiteOp2","ERRATA");
		HttpResponse response = restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		String domain = response.getHeader(HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE);
		if(domain==null || "".equals(domain)) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' non trovato nella risposta");
		}
		String atteso = HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_PREFIX+"TestSuiteOpenSPCoop2"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE_BASIC_REALM_SUFFIX; 
		if(atteso.equals(domain)==false) {
			throw new Exception("Header '"+HttpConstants.AUTHORIZATION_RESPONSE_WWW_AUTHENTICATE+"' con un valore ("+domain+") differente da quello atteso("+atteso+")");
		}
		restCore.postInvoke(repository);
	}
	
	
	
//	5) Invocazione dove il client fornisce le credenziali corrette che però devono essere "bruciate" dall'autenticazione 'basic'. Queste non vengono inoltrate quindi al servizio.
//	Si attende un 401 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".AuthBasic_ServiceWithBasicAuth_KO_CredenzialiBruciate"})
	public void testAuthBasicServiceWithBasicAuth_ko_credentialBruciate() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 401, repository, true, true, false, "text/json", true);
		restCore.postInvoke(repository);
	}
	
	
//	6) Invocazione dove il client fornisce le credenziali corrette che vengono sia utilizzate dalla PdD per autenticazione basic
	// sia vengono forwardate (non sono "bruciate") all'applicativo finale che che può quindi effettuare anche lui a sua volta l'autenticazione.
//	Si attende un 200 ritornato dal servizio finale.
	
	@Test(groups={RESTAutenticazionePortaDelegata.ID_GRUPPO,RESTAutenticazionePortaDelegata.ID_GRUPPO+".AuthBasic_ServiceWithBasicAuth_OK_CredenzialiInoltrate"})
	public void testAuthBasicServiceWithBasicAuth_ko_credentialForwarded() throws TestSuiteException, Exception{
		Repository repository=new Repository();
		RESTCore restCore = new RESTCore(HttpRequestMethod.GET, RUOLO.PORTA_DELEGATA);
		restCore.setPortaApplicativaDelegata(CostantiTestSuite.PORTA_DELEGATA_REST_BASIC_PDD_SERVICE_WITH_BASIC_AUTH_FORWARD_CREDENTIALS);
		restCore.setCredenziali("testsuiteOp2","12345678");
		restCore.invoke("json", 200, repository, true, true, "text/json");
		restCore.postInvoke(repository);
	}
	
	
}
