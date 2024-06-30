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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.constants.CostantiConnettori;
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

	public static final String API_PROPRIETA = "VaultTestProprieta";
	
	private static final String OP_PROPRIETA_CONFIG = "config";
	public static final String OP_PROPRIETA_APPLICATIVO = "applicativo";
	public static final String OP_PROPRIETA_SOGGETTO = "soggetto";
	
	private static final String PROPRIETA_CONFIG_PREFIX = "ConfigProp:";
	private static final String PROPRIETA_SOGGETTO_PREFIX = "SoggettoProp:";
	private static final String PROPRIETA_APPLICATIVO_PREFIX = "ApplicativoProp:";
	
	
	public static final String API_CONNETTORI = "VaultTestConnettori";
	
	private static final String OP_HTTP = "http";
	private static final String OP_HTTP_2 = "http2";
	private static final String OP_HTTP_SERVER = "httpServer";
	private static final String OP_HTTPS = "https";
	private static final String OP_MTLS = "mtls";
	private static final String OP_PROXY = "proxy"; // non invocata realmente
	private static final String OP_API_KEY = "apiKey";
	private static final String NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE = "gw_SoggettoInternoVaultTest/gw_VaultTestConnettori/v1";
	private static final String NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE = "CNT_SF_gw/SoggettoInternoVaultTestFruitore_gw/SoggettoInternoVaultTest_gw/VaultTestConnettori/1";
	private static String getNomeConnettoreFruizioneTestAzione(String azione) {
		return NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE.replace("CNT_SF", "CNT_SF_AZIONE")+"_"+azione;
	}
	
	private static final String OP_RICHIESTA_ASINCRONA = "asincronoRichiesta";
	private static final String OP_RISPOSTA_ASINCRONA = "asincronoRisposta";
	private static final String NOME_SERVER_ASINCRNO_ASIMMETRICO = "TestServerAsincronoAsimmetrico";
	
	private static final String SA_PREFIX = "SA:";
	private static final String CONNETTORE_PREFIX = "Connettore:";
	
	private static final String STORE_PASSWORD_OPENSPCOOP = "openspcoop";
	
	private String getMessagePrefix(String origine, String found) {
		return "["+origine+"] Found '"+found+"'; expected";
	}
	private String getMessageExpected(String origine, String found, String expected) {
		return getMessagePrefix(origine, found)+" start with '"+expected+"'";
	}
	private String getMessageExpectedNull(String origine, String found) {
		return getMessagePrefix(origine, found)+" null";
	}
	private String getMessageExpectedStartsWith(String origine, String found, String expectedPrefix) {
		return getMessagePrefix(origine, found)+" start with '"+expectedPrefix+"'";
	}
	
	
	
	// ** STEP 0 //
	
	@Test
	public void step0aVerificaInizialeDatabase() throws UtilsException {
				
		logCoreInfo("@step0aVerificaInizialeDatabase");
		
		verificheDatabaseInCharo();
		
		verificheProprietaCifrate(DEFAULT_POLICY);
		
	}
	
	@Test
	public void step0bInvocazioneServiziGovway() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step0bInvocazioneServiziGovway");
		
		invocazioneGovWay();
		
	}
	
	
	
	
	// ** STEP 1 //
	
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
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
	}
	
	@Test
	public void step1cInvocazioneServiziGovwayConSecretLockedByDefaultPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step1cInvocazioneServiziGovwayConSecretLockedByDefaultPolicy");
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	
	// ** STEP 2 //
	
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
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
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
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_KEYS_POLICY);
	}
	
	@Test
	public void step2eInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step2eInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy");
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	
	
	// ** STEP 3 //
	
	@Test
	public void step3aVaultGwRemotePolicySrcPlainUncorrect() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova policy, dicendo però che i secrets originali sono in chiaro: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step3aVaultGwRemotePolicySrcPlainUncorrect");
		
		// Vault dei secrets
		vaultSecrets(null, GW_REMOTE_POLICY, false);
		
	}
	
	@Test
	public void step3bVerificaSecretsDatabaseGwRemotePolicySianoRimastiComeInPrecedenza() throws UtilsException {
		
		logCoreInfo("@step3bVerificaSecretsDatabaseGwRemotePolicySianoRimastiComeInPrecedenza");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_KEYS_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_KEYS_POLICY);
	}

	@Test
	public void step3cVaultRemotePolicy() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step3cVaultRemotePolicy");
		
		// Vault dei secrets
		vaultSecrets(GW_KEYS_POLICY, GW_REMOTE_POLICY, false);
		
	}
	
	@Test
	public void step3dVerificaSecretsDatabaseRemotePolicy() throws UtilsException {
		
		logCoreInfo("@step3dVerificaSecretsDatabaseRemotePolicy");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_REMOTE_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_REMOTE_POLICY);
	}
	
	@Test
	public void step3eInvocazioneServiziGovwayConSecretLockedByRemotePolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step3eInvocazioneServiziGovwayConSecretLockedByRemotePolicy");
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	// ** STEP 4 //
	
	@Test
	public void step4aVaultPlainPolicySrcUncorrect() throws UtilsException, HttpUtilsException {
		
		// Aggiorno ripristinando i dati in chiaro, indicando però una policy sorgente non corretta: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step4aVaultPlainPolicySrcUncorrect");
		
		// Vault dei secrets
		vaultSecrets(DEFAULT_POLICY, null, true);
		
	}
	
	@Test
	public void step4bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza() throws UtilsException {
		
		logCoreInfo("@step4bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_REMOTE_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_REMOTE_POLICY);
	}

	@Test
	public void step4cVaultPlainPolicy() throws UtilsException, HttpUtilsException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step4cVaultPlainPolicy");
		
		// Vault dei secrets
		vaultSecrets(GW_REMOTE_POLICY, null, true);
		
	}
	
	@Test
	public void step4dVerificaSecretsDatabasePlainPolicy() throws UtilsException {
		
		logCoreInfo("@step4dVerificaSecretsDatabasePlainPolicy");
		
		verificheDatabaseInCharo();
		
		verificheProprietaChiaro();
	}
	
	@Test
	public void step4eInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step4eInvocazioneServiziGovwayConSecretsPlain");
		
		invocazioneGovWay();
	}
	
	
	
	
	
	
	
	
	// ** STEP 5 //
	
	@Test
	public void step5aVaultByConfigLoaderWithBYOK() throws UtilsException, HttpUtilsException, IOException {
			
		logCoreInfo("@step5aVaultByConfigLoaderWithBYOK");
		
		prepareConfig(true, DEFAULT_POLICY, TESTSUITE_BUNDLE_PLAIN_PATH);
		prepareConfig(true, DEFAULT_POLICY, TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
		dbUtils.updateEncSystemProperty(SYSTEM_ENC_PROP_NAME,SYSTEM_ENC_PROP_PLAIN_VALUE,SYSTEM_ENC_PROP_ENC_VALUE);
		
	}
	
	@Test
	public void step5bVerificaSecretsByDefaultPolicy() throws UtilsException {
		
		logCoreInfo("@step5VerificaSecretsByDefaultPolicy");
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
		
	}
	
	@Test
	public void step5cInvocazioneServiziGovwayConSecretsCifrati() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step5cInvocazioneServiziGovwayConSecretsCifrati");
		
		invocazioneGovWay();
	}
	
	@Test
	public void step5dVaultByConfigLoaderWithoutBYOK() throws UtilsException, HttpUtilsException, IOException {
			
		logCoreInfo("@step5dVaultByConfigLoaderWithoutBYOK");
		
		prepareConfig(false, null, ConfigLoader.TESTSUITE_BUNDLE_PLAIN_PATH);
		/**
		 * prepareConfig(false, null, TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
		 * dbUtils.updateEncSystemProperty(SYSTEM_ENC_PROP_NAME,SYSTEM_ENC_PROP_PLAIN_VALUE,SYSTEM_ENC_PROP_ENC_VALUE);
		 **/ // Le proprietà le lascio cifrate normalmente
		
	}
	
	@Test
	public void step5eVerificaSecretsDatabasePlainPolicy() throws UtilsException {
		
		logCoreInfo("@step5eVerificaSecretsDatabasePlainPolicy");
		
		verificheDatabaseInCharo();
		
		verificheProprietaCifrate(DEFAULT_POLICY);
	}
	
	@Test
	public void step5eInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step5eInvocazioneServiziGovwayConSecretsPlain");
		
		invocazioneGovWay();
	}
	
	
	
	
	
	
	// UTILS
	
	// ** INVOCAZIONI GOVWAY **
	
	private void invocazioneGovWay() throws UtilsException, HttpUtilsException {
		resetCache();
		
		String operazione = " operazione:";
		
		String prefixLogErogazione = "invocazioneGovWay erogazione API:"+API_CONNETTORI + operazione;
		String prefixLogFruizione = "invocazioneGovWay fruizione API:"+API_CONNETTORI + operazione;
		
		logCoreInfo(prefixLogErogazione+OP_HTTP);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_HTTP);
		
		logCoreInfo(prefixLogErogazione+OP_HTTP_2);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_HTTP_2);
		
		logCoreInfo(prefixLogErogazione+OP_HTTP_SERVER);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_HTTP_SERVER);
		
		logCoreInfo(prefixLogErogazione+OP_MTLS);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_MTLS);
		
		logCoreInfo(prefixLogErogazione+OP_API_KEY);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_API_KEY);
		
		
		logCoreInfo(prefixLogFruizione+OP_HTTP);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_HTTP);
		
		logCoreInfo(prefixLogFruizione+OP_HTTP_2);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_HTTP_2);
		
		logCoreInfo(prefixLogFruizione+OP_HTTPS);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_HTTPS);
		
		logCoreInfo(prefixLogFruizione+OP_API_KEY);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_API_KEY);
		
		
		
		logCoreInfo(prefixLogFruizione+OP_RICHIESTA_ASINCRONA);		
		HttpResponse response = Utilities.testSpcoop(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_RICHIESTA_ASINCRONA, null);
		String idMessaggio = response.getHeaderFirstValue("GovWay-Message-ID");
		
		logCoreInfo(prefixLogFruizione+OP_RISPOSTA_ASINCRONA);		
		Utilities.testSpcoop(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI, OP_RISPOSTA_ASINCRONA, idMessaggio);
		
		
		// proprieta
		
		prefixLogErogazione = "invocazioneGovWay erogazione API:"+API_PROPRIETA + operazione;
		/**prefixLogFruizione = "invocazioneGovWay fruizione API:"+API_PROPRIETA + operazione;*/
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_CONFIG);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_APPLICATIVO);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_SOGGETTO);
	}
	
	
	
	
	
	
	// ** VERIFICHE DB CHIARO **
	
	private void verificheDatabaseInCharo() throws UtilsException {
		
		// Verifiche database in chiaro
		
		// ** VERIFICHE colonne enc_passwordinv, passwordinv su servizi_applicativi
		verificheDatabaseInChiaroServiziApplicativiPasswordInv();
		
		// ** VERIFICHE colonne enc_passwordrisp, passwordrisp su servizi_applicativi
		verificheDatabaseInChiaroServiziApplicativiPasswordRisp();
		
		// ** VERIFICHE colonne enc_password, password su connettori
		verificheDatabaseInChiaroConnettoriHttp();
		
		// ** VERIFICHE colonne enc_value, value su connettori_custom
		verificheDatabaseInChiaroConnettoriCustom();
		
		// ** VERIFICHE colonne enc_proxy_username, proxy_username su connettori
		verificheDatabaseInChiaroUtilizzoProxySuConnettori();
		
		// ** VERIFICHE colonne api_key su connettori
		verificheDatabaseInChiaroUtilizzoApiKeySuConnettori();

	}
	private void verificheDatabaseInChiaroServiziApplicativiPasswordInv() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInCharoServiziApplicativiPasswordInv");
		
		// gruppo di default
		String nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		/**System.out.println("CHIARO '"+nomeSA+"' ["+pwd+"]");*/
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, "PasswordVaultTestConnettoreHttp"), 
				"PasswordVaultTestConnettoreHttp", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		assertEquals(getMessageExpectedNull(SA_PREFIX+nomeSA, pwd), 
				null, pwd);
		
		// gruppo differente dal default
		nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, "PasswordVaultTestConnettoreHttpRidefinito"), 
				"PasswordVaultTestConnettoreHttpRidefinito", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals(getMessageExpectedNull(SA_PREFIX+nomeSA, pwd), 
				null, pwd);
		
		// gruppo utilizza applicativo server
		nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, "PasswordVaultTestConnettoreHttpServer"), 
				"PasswordVaultTestConnettoreHttpServer", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals(getMessageExpectedNull(SA_PREFIX+nomeSA, pwd), 
				null, pwd);
		
	}
	private void verificheDatabaseInChiaroServiziApplicativiPasswordRisp() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInCharoServiziApplicativiPasswordRisp");
		
		String nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_SERVER_ASINCRNO_ASIMMETRICO);
				
		// richiesta
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, "PasswordVaultTestConnettoreAsincronoRichiesta"), 
				"PasswordVaultTestConnettoreAsincronoRichiesta", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpectedNull(SA_PREFIX+nomeSA, pwd), 
				null, pwd);
		
		// risposta
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, "PasswordVaultTestConnettoreAsincronoRisposta"), 
				"PasswordVaultTestConnettoreAsincronoRisposta", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpectedNull(SA_PREFIX+nomeSA, pwd), 
				null, pwd);
			
	}
	private void verificheDatabaseInChiaroConnettoriHttp() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroConnettoriHttp");
		
		// gruppo di default
		String nomeConnettore = NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE;
		String v = ConfigLoader.dbUtils.getConnettorePassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "PasswordVaultTestConnettoreHttpFruitore"), 
				"PasswordVaultTestConnettoreHttpFruitore", v);
		v = ConfigLoader.dbUtils.getConnettoreEncPassword(nomeConnettore);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		
		// gruppo differente dal default
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_HTTP_2);
		v = ConfigLoader.dbUtils.getConnettorePassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "PasswordVaultTestConnettoreHttpRidefinitoFruitore"), 
				"PasswordVaultTestConnettoreHttpRidefinitoFruitore", v);
		v = ConfigLoader.dbUtils.getConnettoreEncPassword(nomeConnettore);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
			
	}
	private void verificheDatabaseInChiaroConnettoriCustom() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroConnettoriCustom");
		
		// fruizione
		verificheDatabaseInChiaroConnettoriCustomFruizione();
		
		// erogazione
		verificheDatabaseInChiaroConnettoriCustomErogazione();
			
	}
	private void verificheDatabaseInChiaroConnettoriCustomFruizione() throws UtilsException {
		String nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_HTTPS);
		// password
		String v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore, CostantiConnettori.CONNETTORE_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "PasswordVaultTestConnettoreHttpsRidefinitoFruitore"), 
				"PasswordVaultTestConnettoreHttpsRidefinitoFruitore", v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore, CostantiConnettori.CONNETTORE_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		// trustStorePassword
		v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore, CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, STORE_PASSWORD_OPENSPCOOP), 
				STORE_PASSWORD_OPENSPCOOP, v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore, CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
	}
	private void verificheDatabaseInChiaroConnettoriCustomErogazione() throws UtilsException {
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_MTLS);
		// password
		String v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore,  CostantiConnettori.CONNETTORE_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "PasswordVaultTestConnettoreHttpsRidefinito"), 
				"PasswordVaultTestConnettoreHttpsRidefinito", v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore,  CostantiConnettori.CONNETTORE_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		// trustStorePassword
		v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, STORE_PASSWORD_OPENSPCOOP), 
				STORE_PASSWORD_OPENSPCOOP, v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		// keyStorePassword
		v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "openspcoopjks"), 
				"openspcoopjks", v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		// keyPassword
		v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, STORE_PASSWORD_OPENSPCOOP), 
				STORE_PASSWORD_OPENSPCOOP, v);
		v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore,  CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
	}
	private void verificheDatabaseInChiaroUtilizzoProxySuConnettori() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroUtilizzoProxySuConnettori");
		
		// erogazione
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_PROXY);
		String v = ConfigLoader.dbUtils.getConnettoreProxyPassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "passwordProxyTestVault"), 
				"passwordProxyTestVault", v);
		v = ConfigLoader.dbUtils.getConnettoreEncProxyPassword(nomeConnettore);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);
		
		// fruizione
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_PROXY);
		v = ConfigLoader.dbUtils.getConnettoreProxyPassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "passwordProxyTestVaultFruitore"), 
				"passwordProxyTestVaultFruitore", v);
		v = ConfigLoader.dbUtils.getConnettoreEncProxyPassword(nomeConnettore);
		assertEquals(getMessageExpectedNull(CONNETTORE_PREFIX+nomeConnettore, v), 
				null, v);

	}
	private void verificheDatabaseInChiaroUtilizzoApiKeySuConnettori() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroUtilizzoApiKeySuConnettori");
		
		// erogazione
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_API_KEY);
		String v = ConfigLoader.dbUtils.getConnettoreApiKey(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "XXX-API_KEY-TestVault"), 
				"XXX-API_KEY-TestVault", v);
		
		// fruizione
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_API_KEY);
		v = ConfigLoader.dbUtils.getConnettoreApiKey(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, "XXX-API_KEY-TestVault-Fruitore"), 
				"XXX-API_KEY-TestVault-Fruitore", v);

	}
	
	
	
	// ** VERIFICHE DB CIFRATO **
	
	private void verificheDatabaseCifrato(String prefix) throws UtilsException {
		
		// Verifiche database cifrato
	
		// ** VERIFICHE colonne enc_passwordinv, passwordinv su servizi_applicativi
		verificheDatabaseCifratoPasswordInv(prefix);
		
		// ** VERIFICHE colonne enc_passwordrisp, passwordrisp su servizi_applicativi
		verificheDatabaseCifratoPasswordRisp(prefix);
		
		// ** VERIFICHE colonne enc_password, password su connettori
		verificheDatabaseCifratoConnettoriHttp(prefix);
		
		// ** VERIFICHE colonne enc_value, value su connettori_custom
		verificheDatabaseCifratoConnettoriCustom(prefix);
		
		// ** VERIFICHE colonne enc_proxy_username, proxy_username su connettori
		verificheDatabaseCifratoUtilizzoProxySuConnettori(prefix);
		
		// ** VERIFICHE colonne api_key su connettori
		verificheDatabaseCifratoUtilizzoApiKeySuConnettori(prefix);
	}
	private void verificheDatabaseCifratoPasswordInv(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoPasswordInv");
		
		// gruppo di default
		String nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		/**System.out.println("CIFRATO '"+nomeSA+"' ["+pwd+"]");*/
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(SA_PREFIX+nomeSA, pwd, prefix), 
				expected);
		
		// gruppo differente dal default
		nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_2);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(SA_PREFIX+nomeSA, pwd, prefix), 
				expected);
		
		// gruppo utilizza applicativo server
		nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_HTTP_SERVER);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(SA_PREFIX+nomeSA,pwd, prefix), 
				expected);
	}
	private void verificheDatabaseCifratoPasswordRisp(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoPasswordRisp");
		
		String nomeSA = ConfigLoader.dbUtils.getServiziApplicativiNome(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		
		// richiesta
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(SA_PREFIX+nomeSA,pwd, prefix), 
				expected);
		
		// risposta
		pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		assertEquals(getMessageExpected(SA_PREFIX+nomeSA, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordRisp(NOME_SERVER_ASINCRNO_ASIMMETRICO);
		expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(SA_PREFIX+nomeSA,pwd, prefix), 
				expected);
	}
	private void verificheDatabaseCifratoConnettoriHttp(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoConnettoriHttp");
		
		// gruppo di default
		String nomeConnettore = NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE;
		String v = ConfigLoader.dbUtils.getConnettorePassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, prefix), 
				prefix, v);
		v = ConfigLoader.dbUtils.getConnettoreEncPassword(nomeConnettore);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);
		
		// gruppo differente dal default
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_HTTP_2);
		v = ConfigLoader.dbUtils.getConnettorePassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, prefix), 
				prefix, v);
		v = ConfigLoader.dbUtils.getConnettoreEncPassword(nomeConnettore);
		expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);
			
	}
	private void verificheDatabaseCifratoConnettoriCustom(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoConnettoriCustom");
		
		// fruizione
		verificheDatabaseCifratoConnettoriCustomFruizione(prefix);
		
		// erogazione
		verificheDatabaseCifratoConnettoriCustomErogazione(prefix);
			
	}
	private void verificheDatabaseCifratoConnettoriCustomFruizione(String prefix) throws UtilsException {
		String nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_HTTPS);
		
		List<String> verifiche = new ArrayList<>();
		verifiche.add(CostantiConnettori.CONNETTORE_PASSWORD);
		verifiche.add(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		
		verificheDatabaseCifratoConnettoriCustom(prefix, verifiche, nomeConnettore);
		
	}
	private void verificheDatabaseCifratoConnettoriCustomErogazione(String prefix) throws UtilsException {
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_MTLS);
		
		List<String> verifiche = new ArrayList<>();
		verifiche.add(CostantiConnettori.CONNETTORE_PASSWORD);
		verifiche.add(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD);
		verifiche.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD);
		verifiche.add(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD);
		
		verificheDatabaseCifratoConnettoriCustom(prefix, verifiche, nomeConnettore);
		
	}
	private void verificheDatabaseCifratoConnettoriCustom(String prefix, List<String> verifiche, String nomeConnettore) throws UtilsException {
		for (String nomeProprieta : verifiche) {
			String v = ConfigLoader.dbUtils.getConnettoreCustomValue(nomeConnettore, nomeProprieta);
			assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, prefix), 
					prefix, v);
			v = ConfigLoader.dbUtils.getConnettoreCustomEncValue(nomeConnettore, nomeProprieta);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
					expected);
		}
		
	}
	
	private void verificheDatabaseCifratoUtilizzoProxySuConnettori(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoUtilizzoProxySuConnettori");
		
		// erogazione
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_PROXY);
		String v = ConfigLoader.dbUtils.getConnettoreProxyPassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, prefix), 
				prefix, v);
		v = ConfigLoader.dbUtils.getConnettoreEncProxyPassword(nomeConnettore);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);
		
		// fruizione
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_PROXY);
		v = ConfigLoader.dbUtils.getConnettoreProxyPassword(nomeConnettore);
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, prefix), 
				prefix, v);
		v = ConfigLoader.dbUtils.getConnettoreEncProxyPassword(nomeConnettore);
		expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);

	}
	
	private void verificheDatabaseCifratoUtilizzoApiKeySuConnettori(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoUtilizzoApiKeySuConnettori");
		
		// erogazione
		String nomeConnettore = ConfigLoader.dbUtils.getNomeConnettoreByPortaApplicativa(NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE, OP_API_KEY);
		String v = ConfigLoader.dbUtils.getConnettoreApiKey(nomeConnettore);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);
		
		// fruizione
		nomeConnettore = getNomeConnettoreFruizioneTestAzione(OP_API_KEY);
		v = ConfigLoader.dbUtils.getConnettoreApiKey(nomeConnettore);
		expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(CONNETTORE_PREFIX+nomeConnettore,v, prefix), 
				expected);

	}

	
	
	
	
	
	
	// ** VERIFICHE PROPRIETA CHIARO **
	
	private void verificheProprietaChiaro() throws UtilsException {
		
		// ** VERIFICHE colonne env_value, valore su pdd_sys_props
		verificheDatabaseProprietaChiaroConfigurazione();
		
	}
	private void verificheDatabaseProprietaChiaroConfigurazione() throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaChiaroConfigurazione");
		
		String nomeProprieta = "vaultTestNomeCifrato";
		String pwd = ConfigLoader.dbUtils.getConfigPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_CONFIG_PREFIX+nomeProprieta, pwd, "vaultTestValoreCifrato"), 
				"vaultTestValoreCifrato", pwd);
				
		pwd = ConfigLoader.dbUtils.getConfigPropertyEncValue(nomeProprieta);
		assertEquals(getMessageExpectedNull(PROPRIETA_CONFIG_PREFIX+nomeProprieta, pwd), 
				null, pwd);
		
	}
	
	
	
	
	
	// ** VERIFICHE PROPRIETA CIFRATE **
	
	private void verificheProprietaCifrate(String policy) throws UtilsException {
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(policy);
		prefix = prefix.substring(0, prefix.length()-1);
		
		// ** VERIFICHE colonne env_value, valore su pdd_sys_props
		verificheDatabaseProprietaCifrateConfigurazione(prefix);
		
		// ** VERIFICHE colonne env_value, valore su soggetti_properties
		verificheDatabaseProprietaCifrateSoggetto(prefix);
		
		// ** VERIFICHE colonne env_value, valore su sa_properties
		verificheDatabaseProprietaCifrateApplicativo(prefix);
		
	}
	private void verificheDatabaseProprietaCifrateConfigurazione(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifrateConfigurazione");
		
		String nomeProprieta = "vaultTestNomeCifrato";
		String pwd = ConfigLoader.dbUtils.getConfigPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_CONFIG_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getConfigPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_CONFIG_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaCifrateSoggetto(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifrateSoggetto");
		
		String nomeProprieta = "vaultTestNomeCifratoSoggetto";
		String pwd = ConfigLoader.dbUtils.getSoggettoPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_SOGGETTO_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getSoggettoPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_SOGGETTO_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaCifrateApplicativo(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifrateApplicativo");
		
		String nomeProprieta = "vaultTestNomeCifratoApplicativo";
		String pwd = ConfigLoader.dbUtils.getApplicativoPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_APPLICATIVO_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getApplicativoPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_APPLICATIVO_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	
}