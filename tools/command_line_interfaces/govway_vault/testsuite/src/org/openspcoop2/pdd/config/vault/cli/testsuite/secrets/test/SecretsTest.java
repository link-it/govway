/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.pdd.config.vault.cli.testsuite.secrets.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.pdd.config.vault.cli.testsuite.ConfigLoader;
import org.openspcoop2.pdd.config.vault.cli.testsuite.TipoServizio;
import org.openspcoop2.pdd.config.vault.cli.testsuite.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilsException;

/**
* ConnettoriTest
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(CustomRunner.class)
public class SecretsTest extends ConfigLoader {

	private static final String API = "VaultTestConnettori";
	
	private static final String  OP_HTTP = "http";
	private static final String  OP_HTTP_2 = "http2";
	private static final String  OP_HTTP_SERVER = "httpServer";
	private static final String NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE = "gw_SoggettoInternoVaultTest/gw_VaultTestConnettori/v1";
	
	private static final String  OP_RICHIESTA_ASINCRONA = "asincronoRichiesta";
	private static final String  OP_RISPOSTA_ASINCRONA = "asincronoRisposta";
	private static final String NOME_SERVER_ASINCRNO_ASIMMETRICO = "TestServerAsincronoAsimmetrico";
	
	private String getMessageExpectedStartsWith(String found, String prefix) {
		return "Found '"+found+"'; expected start with '"+prefix+"'";
	}
	
	@Test
	public void step0aVerificaInizialeDatabase() throws UtilsException {
				
		logCoreInfo("@step0aVerificaInizialeDatabase");
		
		verificheDatabaseInCharo();
		
	}
	
	@Test
	public void step0bInvocazioneServiziGovway() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step0bInvocazioneServiziGovway");
		
		invocazioneGovWay();
		
	}
	
	@Test
	public void step1aVaultDefaultPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step1aVaultDefaultPolicy");
		
		// Vault dei secrets
		vaultSecrets(null, DEFAULT_POLICY, false);
		
	}
	
	@Test
	public void step1bVerificaSecretsDatabaseDefaultPolicy() throws UtilsException {
		
		logCoreInfo("@step1bVerificaSecretsDatabaseDefaultPolicy");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		
		verificheDatabaseCifrato(prefix);
	}
	
	@Test
	public void step1cInvocazioneServiziGovwayConSecretLockedByDefaultPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step1cInvocazioneServiziGovwayConSecretLockedByDefaultPolicy");
		
		invocazioneGovWay();
		
	}
	
	@Test
	public void step2aVaultGwKeysPolicySrcPlainUncorrect() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova policy, dicendo però che i secrets originali sono in chiaro: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step2aVaultGwKeysPolicy");
		
		// Vault dei secrets
		vaultSecrets(null, GW_KEYS_POLICY, true);
		
	}
	
	@Test
	public void step2bVerificaSecretsDatabaseGwKeysPolicySianoRimastiComeInPrecedenza() throws UtilsException {
		
		logCoreInfo("@step2bVerificaSecretsDatabaseGwKeysPolicySianoRimastiComeInPrecedenza");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		
		verificheDatabaseCifrato(prefix);
	}

	@Test
	public void step2cVaultGwKeysPolicy() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step2cVaultGwKeysPolicy");
		
		// Vault dei secrets
		vaultSecrets(DEFAULT_POLICY, GW_KEYS_POLICY, true);
		
	}
	
	@Test
	public void step2dVerificaSecretsDatabaseGwKeysPolicy() throws UtilsException {
		
		logCoreInfo("@step2dVerificaSecretsDatabaseGwKeysPolicy");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_KEYS_POLICY);
		
		verificheDatabaseCifrato(prefix);
	}
	
	@Test
	public void step2eInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step2eInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy");
		
		invocazioneGovWay();
		
	}
	
	@Test
	public void step3aVaultPlainPolicySrcUncorrect() throws UtilsException, HttpUtilsException {
		
		// Aggiorno ripristinando i dati in chiaro, indicando però una policy sorgente non corretta: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step3aVaultPlainPolicySrcUncorrect");
		
		// Vault dei secrets
		vaultSecrets(DEFAULT_POLICY, null, true);
		
	}
	
	@Test
	public void step3bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza() throws UtilsException {
		
		logCoreInfo("@step3bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_KEYS_POLICY);
		
		verificheDatabaseCifrato(prefix);
	}

	@Test
	public void step3cVaultPlainPolicy() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step3cVaultPlainPolicy");
		
		// Vault dei secrets
		vaultSecrets(GW_KEYS_POLICY, null, true);
		
	}
	
	@Test
	public void step3dVerificaSecretsDatabasePlainPolicy() throws UtilsException {
		
		logCoreInfo("@step3dVerificaSecretsDatabasePlainPolicy");
		
		verificheDatabaseInCharo();
	}
	
	@Test
	public void step3eInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step3eInvocazioneServiziGovwayConSecretsPlain");
		
		invocazioneGovWay();
	}
	
	private void invocazioneGovWay() throws UtilsException, HttpUtilsException {
		resetCache();
		
		String prefixLog = "invocazioneGovWay API:"+API + " operazione:";
		
		logCoreInfo(prefixLog+OP_HTTP);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API, OP_HTTP);
		
		logCoreInfo(prefixLog+OP_HTTP_2);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API, OP_HTTP_2);
		
		logCoreInfo(prefixLog+OP_HTTP_SERVER);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API, OP_HTTP_SERVER);
		
		logCoreInfo(prefixLog+OP_RICHIESTA_ASINCRONA);		
		HttpResponse response = Utilities.testSpcoop(logCore, TipoServizio.FRUIZIONE, API, OP_RICHIESTA_ASINCRONA, null);
		String idMessaggio = response.getHeaderFirstValue("GovWay-Message-ID");
		
		logCoreInfo(prefixLog+OP_RISPOSTA_ASINCRONA);		
		Utilities.testSpcoop(logCore, TipoServizio.FRUIZIONE, API, OP_RISPOSTA_ASINCRONA, idMessaggio);
	}
	
	private void verificheDatabaseInCharo() throws UtilsException {
		
		// Verifiche database in chiaro
		
		// ** VERIFICHE colonne enc_passwordinv, passwordinv
		verificheDatabaseInCharoServiziApplicativiPasswordInv();
		
		// ** VERIFICHE colonne enc_passwordrisp, passwordrisp
		verificheDatabaseInCharoServiziApplicativiPasswordRisp();

	}
	private void verificheDatabaseInCharoServiziApplicativiPasswordInv() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInCharoServiziApplicativiPasswordInv");
		
		// gruppo di default
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		assertEquals("PasswordVaultTestConnettoreHttp", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		assertEquals(null, pwd);
		
		// gruppo differente dal default
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals("PasswordVaultTestConnettoreHttpRidefinito", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals(null, pwd);
		
		// gruppo utilizza applicativo server
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals("PasswordVaultTestConnettoreHttpServer", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals(null, pwd);
		
	}
	private void verificheDatabaseInCharoServiziApplicativiPasswordRisp() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInCharoServiziApplicativiPasswordRisp");
		
		// richiesta
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals("PasswordVaultTestConnettoreAsincronoRichiesta", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(null, pwd);
		
		// risposta
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals("PasswordVaultTestConnettoreAsincronoRisposta", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(null, pwd);
			
	}
	
	private void verificheDatabaseCifrato(String prefix) throws UtilsException {
		
		// Verifiche database cifrato
	
		// ** VERIFICHE colonne enc_passwordinv, passwordinv
		verificheDatabaseCifratoPasswordInv(prefix);
		
		// ** VERIFICHE colonne enc_passwordrisp, passwordrisp
		verificheDatabaseCifratoPasswordRisp(prefix);
	}
	private void verificheDatabaseCifratoPasswordInv(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoPasswordInv");
		
		// gruppo di default
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
		
		// gruppo differente dal default
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
		
		// gruppo utilizza applicativo server
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
	}
	private void verificheDatabaseCifratoPasswordRisp(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoPasswordRisp");
		
		// richiesta
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
		
		// risposta
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
	}
}