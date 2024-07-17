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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.wss4j.common.crypto.Merlin;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openspcoop2.core.byok.BYOKUtilities;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.CostantiProprieta;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.pdd.config.vault.cli.testsuite.ConfigLoader;
import org.openspcoop2.pdd.config.vault.cli.testsuite.TipoServizio;
import org.openspcoop2.pdd.config.vault.cli.testsuite.Utilities;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.security.keystore.MerlinProvider;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.certificate.ArchiveLoader;
import org.openspcoop2.utils.certificate.Certificate;
import org.openspcoop2.utils.resources.FileSystemUtilities;
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

	public static final String SOGGETTO_INTERNO = "SoggettoInternoVaultTest";
	public static final String SOGGETTO_INTERNO_FRUITORE = "SoggettoInternoVaultTestFruitore";
	
	private static final String APPLICATIVO_CLIENT_2 = "ExampleClient2";
	
	private static final String PASSWORD_123456 = "123456";
	
	private static final String PASSWORD_SYNC_KEY123456 = "key123456";
	
	private static final String PASSWORD_123456_BASIC = "123456basic";
	
	private static final String PASSWORD_PROXY = "123proxy";
	
	private static final String TOKEN_BEARER = "TOKEN-BEARER";
	
	public static final String API_PROPRIETA = "VaultTestProprieta";
	
	private static final String OP_PROPRIETA_CONFIG = "config";
	public static final String OP_PROPRIETA_APPLICATIVO = "applicativo";
	public static final String OP_PROPRIETA_SOGGETTO = "soggetto";
	public static final String OP_PROPRIETA_PORTA = "porta";
	public static final String OP_PROPRIETA_AUTHN_AUTHZ_AUTHC = "plugin-authn-authz-authc";
	
	private static final String PROPRIETA_CONFIG_PREFIX = "ConfigProp:";
	private static final String PROPRIETA_SOGGETTO_PREFIX = "SoggettoProp:";
	private static final String PROPRIETA_APPLICATIVO_PREFIX = "ApplicativoProp:";
	private static final String PROPRIETA_PORTA_PREFIX = "PortaProp:";
	private static final String PROPRIETA_PORTA_AUTHN_PREFIX = "PortaAuthnProp:";
	private static final String PROPRIETA_PORTA_AUTHZ_PREFIX = "PortaAuthzProp:";
	private static final String PROPRIETA_PORTA_AUTHC_PREFIX = "PortaAuthcProp:";
	
	
	public static final String API_CONNETTORI = "VaultTestConnettori";
	
	private static final String OP_HTTP = "http";
	private static final String OP_HTTP_2 = "http2";
	private static final String OP_HTTP_SERVER = "httpServer";
	private static final String OP_HTTPS = "https";
	private static final String OP_MTLS = "mtls";
	private static final String OP_PROXY = "proxy"; // non invocata realmente
	private static final String OP_API_KEY = "apiKey";
	private static final String OP_SECRETS_IN_GOVWAY_ETC_FILE = "secrets-in-govway-etc-file";
	private static final String NOME_PORTA_DEFAULT_TEST_CONNETTORI_EROGAZIONE = "gw_SoggettoInternoVaultTest/gw_VaultTestConnettori/v1";
	private static final String NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE = "CNT_SF_gw/SoggettoInternoVaultTestFruitore_gw/SoggettoInternoVaultTest_gw/VaultTestConnettori/1";
	private static String getNomeConnettoreFruizioneTestAzione(String azione) {
		return NOME_CONNETTORE_DEFAULT_TEST_FRUIZIONE.replace("CNT_SF", "CNT_SF_AZIONE")+"_"+azione;
	}
	
	private static final String OP_RICHIESTA_ASINCRONA = "asincronoRichiesta";
	private static final String OP_RISPOSTA_ASINCRONA = "asincronoRisposta";
	private static final String NOME_SERVER_ASINCRNO_ASIMMETRICO = "TestServerAsincronoAsimmetrico";
	
	public static final String API_CONNETTORI_PATH= "VaultTestConnettoriPath";
	public static final String API_CONNETTORI_ARCHIVIO = "VaultTestConnettoriArchivio";
	
	public static final String API_CONNETTORI_PATH_FRUIZIONE = "VaultTestConnettoriPathFruizione";
	public static final String API_CONNETTORI_ARCHIVIO_FRUIZIONE = "VaultTestConnettoriArchivioFruizione";
	public static final String API_CONNETTORI_APPLICATIVO = "VaultTestConnettoriApplicativo";
	
	public static final String OP_MODI_PATH = "modi-path";
	public static final String OP_MODI_ARCHIVIO = "modi-archivio";
	
	
	public static final String API_TOKEN_POLICY_VALIDAZIONE = "TestVaultValidazioneAuthorizationServer";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_INTROSPECTION_USER_INFO_TLS = "TLS-introspection-userInfo";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWS = "Vault-ValidazioneJWS";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE = "Vault-ValidazioneJWE";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWS = "Vault-ForwardJWS";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE = "Vault-ForwardJWE";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_GOVWAY_JWS= "Vault-ForwardGovWayJWS";
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES = "Vault-OtherProperties"; // non invocata realmente
	public static final String API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES_2 = "Vault-OtherProperties2"; // non invocata realmente
	
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE = "TestVaultNegoziazioneAuthorizationServer";
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE_OP_TLS = "Vault-TLS";
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE_OP_SIGNED_JWT = "NegoziazioneSignedJWT";
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES = "Vault-OtherProperties"; // non invocata realmente
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_2 = "Vault-OtherProperties2"; // non invocata realmente
	public static final String API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_3 = "Vault-OtherProperties3"; // non invocata realmente
	
	public static final String API_ATTRIBUTE_AUTHORITY = "TestAttributeAuthorityVault";
	public static final String API_ATTRIBUTE_AUTHORITY_OP_TLS = "TLS-Trust-ServerAlias";
	public static final String API_ATTRIBUTE_AUTHORITY_OP_JWS_REQUEST = "JWSRequest";
	public static final String API_ATTRIBUTE_AUTHORITY_OP_JWS_RESPONSE = "JWSResponse";
	public static final String API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES = "Vault-OtherProperties"; // non invocata realmente
	public static final String API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES_2 = "Vault-OtherProperties2"; // non invocata realmente
	
	public static final String API_MESSAGE_SECURITY_JOSE = "TestVaultJoseSecurity";
	public static final String API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT = "encrypt";
	public static final String API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC = "encrypt-symm";
	public static final String API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER = "encrypt-header";
	public static final String API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE = "signature";
	public static final String PORTA_DELEGATA_MESSAGE_SECURITY_JOSE = "gw_SoggettoInternoVaultTestFruitore/gw_SoggettoInternoVaultTest/gw_TestVaultJoseSecurity/v1";
	public static final String PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE = "gw_SoggettoInternoVaultTest/gw_TestVaultJoseSecurity/v1";
	
	public static final String API_MESSAGE_SECURITY_XML = "TestVaultXmlSecurity";
	public static final String API_MESSAGE_SECURITY_XML_OP_ENCRYPT = "encrypt";
	public static final String API_MESSAGE_SECURITY_XML_OP_SIGNATURE = "signature";
	public static final String PORTA_DELEGATA_MESSAGE_SECURITY_XML = "gw_SoggettoInternoVaultTestFruitore/gw_SoggettoInternoVaultTest/gw_TestVaultXmlSecurity/v1";
	public static final String PORTA_APPLICATIVA_MESSAGE_SECURITY_XML = "gw_SoggettoInternoVaultTest/gw_TestVaultXmlSecurity/v1";
	
	public static final String API_MESSAGE_SECURITY_WSS = "TestVaultWSSecurity";
	public static final String API_MESSAGE_SECURITY_WSS_OP_ENCRYPT = "encrypt";
	public static final String API_MESSAGE_SECURITY_WSS_OP_SIGNATURE = "signature";
	public static final String API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER = "saml-bearer";
	public static final String API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY = "saml-holder-of-key";
	public static final String API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES = "saml-sender-vouches";
	public static final String PORTA_DELEGATA_MESSAGE_SECURITY_WSS = "gw_SoggettoInternoVaultTestFruitore/gw_SoggettoInternoVaultTest/gw_TestVaultWSSecurity/v1";
	public static final String PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS = "gw_SoggettoInternoVaultTest/gw_TestVaultWSSecurity/v1";
	
	private static final String TESTO_INPUT = "!HelloWorld!";
	private static final String TESTO_PREFIX = "Text:";
	
	private static final String SA_PREFIX = "SA:";
	private static final String CONNETTORE_PREFIX = "Connettore:";
	
	private static final String PP_PREFIX = "ProtocolProperty:";
	
	private static final String TOKEN_POLICY_VALIDAZIONE_PREFIX = "TokenPolicyValidazione:";
	private static final String TOKEN_POLICY_NEGOZIAZIONE_PREFIX = "TokenPolicyNegoziazione:";
	private static final String ATTRIBUTE_AUTHORITY_PREFIX = "AttributeAuthority:";
	
	private static final String MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX = "FruizioneMessageSecurityRequest:";
	private static final String MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX = "FruizioneMessageSecurityResponse:";
	private static final String MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX = "ErogazioneMessageSecurityRequest:";
	private static final String MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX = "ErogazioneMessageSecurityResponse:";
	
	private static final String NOME_PROPRIETA = " nome: ";

	
	private static final String STORE_PASSWORD_OPENSPCOOP = "openspcoop";
	private static final String STORE_PASSWORD_OPENSPCOOP_JKS = "openspcoopjks";
	
	private static final String SIGNATURE_PROP_REF_ID = "signaturePropRefId";
	private static final String DECRYPTION_PROP_REF_ID = "decryptionPropRefId";
	private static final String ENCRYPTION_PROP_REF_ID = "encryptionPropRefId";
	
	private String getMessagePrefix(String origine, String found) {
		return "["+origine+"] Found '"+found+"'; expected";
	}
	private String getMessageExpected(String origine, String found, String expected) {
		return getMessagePrefix(origine, found)+" start with '"+expected+"'";
	}
	private String getMessageDifferent(String origine, String found, String expected) {
		return getMessagePrefix(origine, found)+" expected <> '"+expected+"'";
	}
	private String getMessageExpectedNull(String origine, String found) {
		return getMessagePrefix(origine, found)+" null";
	}
	private String getMessageExpectedStartsWith(String origine, String found, String expectedPrefix) {
		return getMessagePrefix(origine, found)+" start with '"+expectedPrefix+"'";
	}
	/**private String getMessageExpectedNotDefined(String origine, String found) {
		return getMessagePrefix(origine, found)+" null or empty or -";
	}*/
	private String getMessageExpectedNotEmpty(String origine, List<String> found) {
		return getMessagePrefix(origine, found!=null ? found.toString() : null)+" not empty";
	}
	private String getMessageExpectedEmptyOrNull(String origine, List<String> found) {
		return getMessagePrefix(origine, found!=null ? found.toString() : null)+" empty or null";
	}
	
	
	
	// ** STEP 0 //
	
	@Test
	public void step0aVerificaEncryptDecryptTool() throws UtilsException, IOException {
		
		logCoreInfo("@step0aVerificaEncryptDecryptTool");
		verificaEncryptDecryptTool();
		
	}

	
	@Test
	public void step0bVerificaInizialeDatabase() throws UtilsException, DriverRegistroServiziException {
				
		logCoreInfo("@step0bVerificaInizialeDatabase");
		
		checkExternalToolError();
		
		verificheDatabaseInCharo();
		
		verificheProprietaCifrate(DEFAULT_POLICY);
		
	}
		
	
	@Test
	public void step0cCifraturaKeystoreFileViaSecurity() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step0cCifraturaKeystoreFileViaSecurity");
		
		checkExternalToolError();
		
		copyEncryptedFile(DEFAULT_POLICY, true);
		/** già impostati all'interno dell'archivio updateByokPolicy(DEFAULT_POLICY, DEFAULT_POLICY);*/
		resetCache();
	}
	
	
	@Test
	public void step0dInvocazioneServiziGovway() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step0dInvocazioneServiziGovway");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	
	
	
	// ** STEP 1 //
	
	@Test
	public void step1aVaultDefaultPolicy() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		logCoreInfo("@step1aVaultDefaultPolicy");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(null, DEFAULT_POLICY, false);
		
	}
	
	@Test
	public void step1bVerificaSecretsDatabaseDefaultPolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step1bVerificaSecretsDatabaseDefaultPolicy");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
	}
	
	@Test
	public void step1cCifraturaKeystoreFileViaKsm() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step1cCifraturaKeystoreFileViaKsm");
		
		checkExternalToolError();
		
		copyEncryptedFile(KSM_PBKDF2_WRAP, false); 
		// verifico utilizzo di un ksm non agganciato a security
		updateByokPolicy(SECURITY_PBKDF2_UNWRAP, KSM_PBKDF2_UNWRAP);
		resetCache();
	}
	
	@Test
	public void step1dInvocazioneServiziGovwayConSecretLockedByDefaultPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step1dInvocazioneServiziGovwayConSecretLockedByDefaultPolicy");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	
	// ** STEP 2 //
	
	@Test
	public void step2aVaultGwKeysPolicySrcPlainUncorrect() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno con una nuova policy, dicendo però che i secrets originali sono in chiaro: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step2aVaultGwKeysPolicy");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(null, GW_KEYS_POLICY, true);
		
	}
	
	@Test
	public void step2bVerificaSecretsDatabaseGwKeysPolicySianoRimastiComeInPrecedenza() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step2bVerificaSecretsDatabaseGwKeysPolicySianoRimastiComeInPrecedenza");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
	}

	@Test
	public void step2cVaultGwKeysPolicy() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step2cVaultGwKeysPolicy");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(DEFAULT_POLICY, GW_KEYS_POLICY, true);
		
	}
	
	@Test
	public void step2dVerificaSecretsDatabaseGwKeysPolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step2dVerificaSecretsDatabaseGwKeysPolicy");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_KEYS_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_KEYS_POLICY);
	}
	
	@Test
	public void step2eCifraturaKeystoreFileViaSecurity() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step2eCifraturaKeystoreFileViaSecurity");
		
		checkExternalToolError();
		
		copyEncryptedFile(GW_KEYS_POLICY, true);
		updateByokPolicy(KSM_PBKDF2_UNWRAP, SECURITY_ASYNC_KEYS_UNWRAP);
		resetCache();
	}
	
	@Test
	public void step2fInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step2fInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	@Test
	public void step2gCifraturaKeystoreFileViaKsm() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step2gCifraturaKeystoreFileViaKsm");
		
		checkExternalToolError();
		
		copyEncryptedFile(KSM_ASYNC_KEYS_WRAP, false);
		updateByokPolicy(SECURITY_ASYNC_KEYS_UNWRAP, KSM_ASYNC_KEYS_UNWRAP);
		resetCache();
	}
	
	@Test
	public void step2hInvocazioneServiziGovwayConSecretLockedByGwKeysPolicyKeystoreFileViaKsm() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step2hInvocazioneServiziGovwayConSecretLockedByGwKeysPolicyKeystoreFileViaKsm");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	
	
	// ** STEP 3 //
	
	@Test
	public void step3aVaultGwRemotePolicySrcPlainUncorrect() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno con una nuova policy, dicendo però che i secrets originali sono in chiaro: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step3aVaultGwRemotePolicySrcPlainUncorrect");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(null, GW_REMOTE_POLICY, false);
		
	}
	
	@Test
	public void step3bVerificaSecretsDatabaseGwRemotePolicySianoRimastiComeInPrecedenza() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step3bVerificaSecretsDatabaseGwRemotePolicySianoRimastiComeInPrecedenza");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_KEYS_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_KEYS_POLICY);
	}

	@Test
	public void step3cVaultRemotePolicy() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step3cVaultRemotePolicy");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(GW_KEYS_POLICY, GW_REMOTE_POLICY, false);
		
	}
	
	@Test
	public void step3dVerificaSecretsDatabaseRemotePolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step3dVerificaSecretsDatabaseRemotePolicy");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_REMOTE_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_REMOTE_POLICY);
	}
	
	@Test
	public void step3eCifraturaKeystoreFileViaSecurity() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step3eCifraturaKeystoreFileViaSecurity");
		
		checkExternalToolError();
		
		copyEncryptedFile(GW_REMOTE_POLICY, true);
		updateByokPolicy(KSM_ASYNC_KEYS_UNWRAP, SECURITY_GOVWAY_REMOTE_UNWRAP);
		resetCache();
	}
	
	@Test
	public void step3fInvocazioneServiziGovwayConSecretLockedByRemotePolicy() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step3fInvocazioneServiziGovwayConSecretLockedByRemotePolicy");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	@Test
	public void step3gCifraturaKeystoreFileViaKsm() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step3gCifraturaKeystoreFileViaKsm");
		
		checkExternalToolError();
		
		copyEncryptedFile(KSM_GOVWAY_REMOTE_WRAP, false);
		updateByokPolicy(SECURITY_GOVWAY_REMOTE_UNWRAP, KSM_GOVWAY_REMOTE_UNWRAP);
		resetCache();
	}
	
	@Test
	public void step3hInvocazioneServiziGovwayConSecretLockedByRemotePolicyKeystoreFileViaKsm() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step3hInvocazioneServiziGovwayConSecretLockedByRemotePolicyKeystoreFileViaKsm");
		
		checkExternalToolError();
		
		invocazioneGovWay();
		
	}
	
	
	
	
	
	// ** STEP 4 //
	
	@Test
	public void step4aVaultPlainPolicySrcUncorrect() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno ripristinando i dati in chiaro, indicando però una policy sorgente non corretta: così facendo non troverò nulla da aggiornare
		
		logCoreInfo("@step4aVaultPlainPolicySrcUncorrect");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(DEFAULT_POLICY, null, true);
		
	}
	
	@Test
	public void step4bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step4bVerificaSecretsDatabasePlainPolicySianoRimastiComeInPrecedenza");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(GW_REMOTE_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(GW_REMOTE_POLICY);
	}

	@Test
	public void step4cVaultPlainPolicy() throws UtilsException, HttpUtilsException, FileNotFoundException {
		
		// Aggiorno con una nuova polic
		
		logCoreInfo("@step4cVaultPlainPolicy");
		
		checkExternalToolError();
		
		// Vault dei secrets
		vaultSecrets(GW_REMOTE_POLICY, null, true);
		
	}
	
	@Test
	public void step4dVerificaSecretsDatabasePlainPolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step4dVerificaSecretsDatabasePlainPolicy");
		
		checkExternalToolError();
		
		verificheDatabaseInCharo();
		
		verificheProprietaChiaro();
	}
	
	@Test
	public void step4ePlainKeystoreFile() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step4ePlainKeystoreFile");
		
		checkExternalToolError();
		
		copyEncryptedFile(null, false); // riversa i file con contennuto in chiaro
		updateByokPolicy(KSM_GOVWAY_REMOTE_UNWRAP, "-");
		resetCache();
	}
	
	@Test
	public void step4fInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step4fInvocazioneServiziGovwayConSecretsPlain");
		
		checkExternalToolError();
		
		invocazioneGovWay();
	}
	
	
	
	
	
	
	
	
	// ** STEP 5 //
	
	@Test
	public void step5aVaultByConfigLoaderWithBYOK() throws UtilsException, HttpUtilsException, IOException {
			
		logCoreInfo("@step5aVaultByConfigLoaderWithBYOK");
		
		checkExternalToolError();
		
		prepareConfig(true, DEFAULT_POLICY, TESTSUITE_BUNDLE_PLAIN_PATH);
		prepareConfig(true, DEFAULT_POLICY, TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
		dbUtils.updateEncSystemProperty(SYSTEM_ENC_PROP_NAME,SYSTEM_ENC_PROP_PLAIN_VALUE,SYSTEM_ENC_PROP_ENC_VALUE);
		
	}
	
	@Test
	public void step5bVerificaSecretsByDefaultPolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step5VerificaSecretsByDefaultPolicy");
		
		checkExternalToolError();
		
		String prefix = BYOKUtilities.newPrefixWrappedValue(DEFAULT_POLICY);
		prefix = prefix.substring(0, prefix.length()-1);
		
		verificheDatabaseCifrato(prefix);
		
		verificheProprietaCifrate(DEFAULT_POLICY);
		
	}
	
	@Test
	public void step5cCifraturaKeystoreFileViaSecurity() throws UtilsException, HttpUtilsException, IOException {
		
		logCoreInfo("@step5cCifraturaKeystoreFileViaSecurity");
		
		checkExternalToolError();
		
		copyEncryptedFile(DEFAULT_POLICY, true);
		updateByokPolicy("-", DEFAULT_POLICY);
		resetCache();
	}
	
	@Test
	public void step5dInvocazioneServiziGovwayConSecretsCifrati() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step5dInvocazioneServiziGovwayConSecretsCifrati");
		
		checkExternalToolError();
		
		invocazioneGovWay();
	}
	
	@Test
	public void step5eVaultByConfigLoaderWithoutBYOK() throws UtilsException, HttpUtilsException, IOException {
			
		logCoreInfo("@step5eVaultByConfigLoaderWithoutBYOK");
		
		checkExternalToolError();
		
		deleteConfig(ConfigLoader.TESTSUITE_BUNDLE_PLAIN_PATH); // per avere copertura su test JACOCO
		
		prepareConfig(false, null, ConfigLoader.TESTSUITE_BUNDLE_PLAIN_PATH);
		/**
		 * prepareConfig(false, null, TESTSUITE_BUNDLE_PROPRIETA_CIFRATE_PATH);
		 * dbUtils.updateEncSystemProperty(SYSTEM_ENC_PROP_NAME,SYSTEM_ENC_PROP_PLAIN_VALUE,SYSTEM_ENC_PROP_ENC_VALUE);
		 **/ // Le proprietà le lascio cifrate normalmente
		
	}
	
	@Test
	public void step5fVerificaSecretsDatabasePlainPolicy() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("@step5fVerificaSecretsDatabasePlainPolicy");
		
		checkExternalToolError();
		
		verificheDatabaseInCharo();
		
		verificheProprietaCifrate(DEFAULT_POLICY);
	}
	
	@Test
	public void step5gInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, HttpUtilsException {
		
		logCoreInfo("@step5gInvocazioneServiziGovwayConSecretsPlain");
		
		checkExternalToolError();
		
		invocazioneGovWay();
	}
	
	
	
	
	
	
	// UTILS
	
	
	
	// ** VERIFICA ENCRYPT / DECRYPT **
	
	public void verificaEncryptDecryptTool() throws UtilsException, IOException {
		
		// Default security
		verificaEncryptDecryptToolBySecurity(DEFAULT_POLICY, null);
		
		// altre policy security
		verificaEncryptDecryptToolBySecurity(GW_KEYS_POLICY, GW_KEYS_POLICY);
		verificaEncryptDecryptToolBySecurity(GW_REMOTE_POLICY, GW_REMOTE_POLICY);
		
		// ksm
		verificaEncryptDecryptToolByKsm(null, KSM_PBKDF2_WRAP, KSM_PBKDF2_UNWRAP);
		verificaEncryptDecryptToolByKsm(null, KSM_ASYNC_KEYS_WRAP, KSM_ASYNC_KEYS_UNWRAP);
		verificaEncryptDecryptToolByKsm(null, KSM_GOVWAY_REMOTE_WRAP, KSM_GOVWAY_REMOTE_UNWRAP);
		
	}
	private void verificaEncryptDecryptToolBySecurity(String idPolicy, String idPolicyFornitaAlTool) throws UtilsException, IOException {
		verificaEncryptDecryptTool(idPolicy, true, idPolicyFornitaAlTool, idPolicyFornitaAlTool);
	}
	private void verificaEncryptDecryptToolByKsm(String idPolicy, String idWrap, String idUnwrap) throws UtilsException, IOException {
		verificaEncryptDecryptTool(idPolicy, false, idWrap, idUnwrap);
	}
	private void verificaEncryptDecryptTool(String idPolicy, boolean security,String idWrap, String idUnwrap) throws UtilsException, IOException {
		
		String encryptedValue = vaultEncryptBySystem(idWrap, security, TESTO_INPUT);
		checkExternalToolError();
		if(security) {
			verifyEncryptedValue(idPolicy, encryptedValue);
		}
		else {
			verifyEncryptedValueAgainstPlain(idPolicy, TESTO_INPUT, encryptedValue);
		}
		
		String decryptedValue = vaultDecryptBySystem(idUnwrap, security, encryptedValue);
		checkExternalToolError();
		verifyDecryptedValue(idPolicy, decryptedValue, TESTO_INPUT);
		
		File fTmp = File.createTempFile("keystore", ".test");
		File fTmp2 = File.createTempFile("keystore", ".test");
		try {
			String plainContent = FileSystemUtilities.readFile(KEYSTORE_JWK);
			
			FileSystemUtilities.deleteFile(fTmp); // non deve esistere, prendo solo il nome
			vaultEncryptByFile(idWrap, security, KEYSTORE_JWK, fTmp.getAbsolutePath());
			checkExternalToolError();
			String encryptedContent = FileSystemUtilities.readFile(fTmp);
			if(security) {
				verifyEncryptedValue(idPolicy, encryptedContent);
			}
			else {
				verifyEncryptedValueAgainstPlain(idPolicy, plainContent, encryptedContent);
			}
			
			FileSystemUtilities.deleteFile(fTmp2); // non deve esistere, prendo solo il nome
			vaultDecryptByFile(idUnwrap, security, fTmp.getAbsolutePath(), fTmp2.getAbsolutePath());
			checkExternalToolError();
			String decryptedContent = FileSystemUtilities.readFile(fTmp2);
			verifyDecryptedValue(idPolicy, decryptedContent, plainContent);
			
			// li elimino solo se va tutto bene, in modo che possa fare le verifiche
			FileSystemUtilities.deleteFile(fTmp);
			FileSystemUtilities.deleteFile(fTmp2);
			
		}finally {
			// una volta li eliminavo qua fTmp e fTmp2
		}
	}
	private void verifyEncryptedValue(String idPolicy, String encryptedValue) {
		String prefix = BYOKUtilities.newPrefixWrappedValue(idPolicy);
		prefix = prefix.substring(0, prefix.length()-1);
		boolean expected = encryptedValue!=null && encryptedValue.startsWith(prefix) && encryptedValue.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(TESTO_PREFIX+idPolicy, encryptedValue, prefix), 
				expected);
	}
	private void verifyEncryptedValueAgainstPlain(String idPolicy, String plainValue, String encryptedValue) {
		assertNotEquals(getMessageDifferent(TESTO_PREFIX+idPolicy, plainValue, encryptedValue), 
				encryptedValue, plainValue);
	}
	private void verifyDecryptedValue(String idPolicy, String decryptedValue, String expectedValue) {
		assertEquals(getMessageExpected(TESTO_PREFIX+idPolicy, decryptedValue, expectedValue), 
				expectedValue, decryptedValue);
	}
	
	
	
	// ** VERIFICA ENCRYPT BYOK KEYSTORE **
	
	private void copyEncryptedFile(String idWrap, boolean security) throws UtilsException, IOException {
		List<String> keystores = getKeystoresBYOK();
		for (String fileChiaro : keystores) {
			String fileCifrato = fileChiaro+KEYSTORE_BYOK_SUFFIX;
			File f = new File(fileCifrato);
			FileSystemUtilities.deleteFile(f); // non deve esistere
			if(security || idWrap!=null) {
				vaultEncryptByFile(idWrap, security, fileChiaro, fileCifrato);
			}
			else {
				FileSystemUtilities.writeFile(fileCifrato, FileSystemUtilities.readBytesFromFile(fileChiaro));
			}
			checkExternalToolError();
		}
	}
	
	private void updateByokPolicy(String oldPolicy, String newPolicy) {
		
		ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, DECRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, ENCRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, SIGNATURE_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		
		List<String> listMerlinKeystore = new ArrayList<>();
		listMerlinKeystore.add(Merlin.PREFIX+Merlin.KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		listMerlinKeystore.add(Merlin.OLD_PREFIX+Merlin.KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		listMerlinKeystore.add(Merlin.PREFIX+Merlin.OLD_KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		listMerlinKeystore.add(Merlin.OLD_PREFIX+Merlin.OLD_KEYSTORE_FILE+MerlinProvider.SUFFIX_BYOK);
		for (String p : listMerlinKeystore) {
			ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, DECRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+p, newPolicy);
			ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, ENCRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+p, newPolicy);
			ConfigLoader.dbUtils.updateByokPolicyPorteMessageSecurity(oldPolicy, SIGNATURE_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+p, newPolicy);
		}
		
		ConfigLoader.dbUtils.updateByokPolicyConnettoriCustom(oldPolicy, CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY, newPolicy);
		
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, DECRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, ENCRYPTION_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, SIGNATURE_PROP_REF_ID+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiProprieta.RS_SECURITY_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, org.openspcoop2.pdd.core.token.Costanti.POLICY_ENDPOINT_SSL_CLIENT_CONFIG+CostantiProprieta.KEY_PROPERTIES_CUSTOM_SEPARATOR+CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, org.openspcoop2.pdd.core.token.Costanti.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_BYOK_POLICY, newPolicy);
		ConfigLoader.dbUtils.updateByokPolicyGenericProperties(oldPolicy, org.openspcoop2.pdd.core.token.attribute_authority.Costanti.AA_REQUEST_JWT_SIGN_KEYSTORE_BYOK_POLICY, newPolicy);
		
		ConfigLoader.dbUtils.updateByokPolicyProtocolProperties(oldPolicy, CostantiDB.MODIPA_KEYSTORE_BYOK_POLICY, newPolicy);
		
	}
	
	
	
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
		
		logCoreInfo(prefixLogErogazione+OP_API_KEY);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_CONNETTORI, OP_SECRETS_IN_GOVWAY_ETC_FILE);
		
		
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
		
		
		logCoreInfo(prefixLogFruizione+OP_MODI_PATH);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI_PATH_FRUIZIONE, OP_MODI_PATH);
		
		logCoreInfo(prefixLogFruizione+OP_MODI_PATH);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI_ARCHIVIO_FRUIZIONE, OP_MODI_ARCHIVIO);

		logCoreInfo(prefixLogFruizione+OP_MODI_PATH);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI_APPLICATIVO, OP_MODI_PATH);
		
		logCoreInfo(prefixLogFruizione+OP_MODI_PATH);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_CONNETTORI_APPLICATIVO, OP_MODI_ARCHIVIO);
		
		
		logCoreInfo(prefixLogErogazione+API_TOKEN_POLICY_VALIDAZIONE_OP_INTROSPECTION_USER_INFO_TLS);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_INTROSPECTION_USER_INFO_TLS);
		
		logCoreInfo(prefixLogErogazione+API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWS);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWS);
		
		logCoreInfo(prefixLogErogazione+API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE);
		
		logCoreInfo(prefixLogFruizione+API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWS);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWS);
		
		logCoreInfo(prefixLogFruizione+API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_GOVWAY_JWS);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_GOVWAY_JWS);
		
		logCoreInfo(prefixLogFruizione+API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_TOKEN_POLICY_VALIDAZIONE, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE);
		
		
		logCoreInfo(prefixLogErogazione+API_TOKEN_POLICY_NEGOZIAZIONE_OP_TLS);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_TOKEN_POLICY_NEGOZIAZIONE, API_TOKEN_POLICY_NEGOZIAZIONE_OP_TLS);
		
		logCoreInfo(prefixLogErogazione+API_TOKEN_POLICY_NEGOZIAZIONE_OP_SIGNED_JWT);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_TOKEN_POLICY_NEGOZIAZIONE, API_TOKEN_POLICY_NEGOZIAZIONE_OP_SIGNED_JWT);
		

		logCoreInfo(prefixLogErogazione+API_ATTRIBUTE_AUTHORITY_OP_TLS);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_ATTRIBUTE_AUTHORITY, API_ATTRIBUTE_AUTHORITY_OP_TLS);
		
		logCoreInfo(prefixLogErogazione+API_ATTRIBUTE_AUTHORITY_OP_JWS_REQUEST);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_ATTRIBUTE_AUTHORITY, API_ATTRIBUTE_AUTHORITY_OP_JWS_REQUEST);
		
		logCoreInfo(prefixLogErogazione+API_ATTRIBUTE_AUTHORITY_OP_JWS_RESPONSE);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_ATTRIBUTE_AUTHORITY, API_ATTRIBUTE_AUTHORITY_OP_JWS_RESPONSE);
		
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE);
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT);
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER);
			
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_XML_OP_SIGNATURE);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE);
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_XML_OP_ENCRYPT);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT);
		
		
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);		
		Utilities.testSoap(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);
	
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);		
		Utilities.testSoap(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);
	
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);		
		Utilities.testSoap(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);
	
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);		
		Utilities.testSoap(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);
	
		logCoreInfo(prefixLogFruizione+API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);		
		Utilities.testSoap(logCore, TipoServizio.FRUIZIONE, API_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);

		
		
		// proprieta
		
		prefixLogErogazione = "invocazioneGovWay erogazione API:"+API_PROPRIETA + operazione;
		prefixLogFruizione = "invocazioneGovWay fruizione API:"+API_PROPRIETA + operazione;
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_CONFIG);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_APPLICATIVO);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_CONFIG);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_SOGGETTO);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_PORTA);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_PORTA);
		
		logCoreInfo(prefixLogErogazione+OP_PROPRIETA_AUTHN_AUTHZ_AUTHC);		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API_PROPRIETA, OP_PROPRIETA_AUTHN_AUTHZ_AUTHC);
		
		
		logCoreInfo(prefixLogFruizione+OP_PROPRIETA_PORTA);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_PROPRIETA, OP_PROPRIETA_PORTA);
		
		logCoreInfo(prefixLogFruizione+OP_PROPRIETA_AUTHN_AUTHZ_AUTHC);		
		Utilities.testRest(logCore, TipoServizio.FRUIZIONE, API_PROPRIETA, OP_PROPRIETA_AUTHN_AUTHZ_AUTHC);
	}
	
	
	
	
	
	
	// ** VERIFICHE DB CHIARO **
	
	private void verificheDatabaseInCharo() throws UtilsException, DriverRegistroServiziException {
		
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
		
		// ** VERIFICHE colonne name, value_binary o enc_value_string,value_string per protocol_properties
		verificheDatabaseInChiaroProtocolProperties();
		
		// ** VERIFICHE colonne nome,valore,enc_value su generic_property
		verificheDatabaseInChiaroGenericProperties();
		
		// ** VERIFICHE colonne nome,valore,enc_value su pd_security_request,pd_security_response,pa_security_request e pa_security_response
		verificheDatabaseInChiaroMessageSecurity();

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
		assertEquals(getMessageExpected(CONNETTORE_PREFIX+nomeConnettore, v, STORE_PASSWORD_OPENSPCOOP_JKS), 
				STORE_PASSWORD_OPENSPCOOP_JKS, v);
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
	private void verificheDatabaseInChiaroProtocolProperties() throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("verificheDatabaseInChiaroProtocolProperties");
		
		// -- erogazioni --
		verificheDatabaseInChiaroProtocolPropertiesErogazioni();
		
		// -- fruizioni -- 
		verificheDatabaseInChiaroProtocolPropertiesFruizioni();
		
		// -- applicativi -- 
		verificheDatabaseInChiaroProtocolPropertiesApplicativi();
		
	}
	private String getPrefissoServizio(IDServizio idServizio) {
		return "servizio '"+idServizio.toFormatString()+"' ";
	}
	private void verificheDatabaseInChiaroProtocolPropertiesErogazioni() throws UtilsException, DriverRegistroServiziException {
		
		Map<String, String > verifiche = new HashMap<>();
		// richiesta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		// risposta
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_PATH, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		String prefissoServizio = getPrefissoServizio(idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringValue(idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringEncValue(idServizio, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_ARCHIVIO, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoServizio(idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringValue(idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringEncValue(idServizio, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioBinaryValue(idServizio, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		Certificate cert = ArchiveLoader.loadFromKeystorePKCS12(pkcs12, "ExampleServer", PASSWORD_123456);
		String v = cert.getCertificate().getSubject().getCN();
		String vAtteso = "ExampleServer";
		assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, vAtteso), 
				vAtteso, v);
	}
	private String getPrefissoFruizione(IDSoggetto idFruitore, IDServizio idServizio) {
		return "fruitore: "+idFruitore+" servizio '"+idServizio.toFormatString()+"' ";
	}
	private void verificheDatabaseInChiaroProtocolPropertiesFruizioni() throws UtilsException, DriverRegistroServiziException {
		
		Map<String, String > verifiche = new HashMap<>();
		// richiesta
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		// risposta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		
		IDSoggetto idFruitore = new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO_FRUITORE);
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_PATH_FRUIZIONE, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		String prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_ARCHIVIO_FRUIZIONE, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreBinaryValue(idFruitore, idServizio, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		Certificate cert = ArchiveLoader.loadFromKeystorePKCS12(pkcs12, APPLICATIVO_CLIENT_2, PASSWORD_123456);
		String v = cert.getCertificate().getSubject().getCN();
		String vAtteso = APPLICATIVO_CLIENT_2;
		assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, vAtteso), 
				vAtteso, v);
		
		verifiche = new HashMap<>();
		// richiesta
		// definito sull'applicativo		
		// risposta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_APPLICATIVO, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			vAtteso = entry.getValue();
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
	}
	private String getPrefissoApplicativo(IDServizioApplicativo idServizioApplicativo) {
		return "applicativo '"+idServizioApplicativo.toFormatString()+"' ";
	}
	private void verificheDatabaseInChiaroProtocolPropertiesApplicativi() throws UtilsException {
		
		Map<String, String > verifiche = new HashMap<>();
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		
		IDSoggetto idFruitore = new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO_FRUITORE);
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(idFruitore);
		
		idServizioApplicativo.setNome("ApplicativoVaultModiPath");
		String prefissoServizio = getPrefissoApplicativo(idServizioApplicativo);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringEncValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		
		idServizioApplicativo.setNome("ApplicativoVaultModiArchivio");
		prefissoServizio = getPrefissoApplicativo(idServizioApplicativo);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = entry.getValue();
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringEncValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpectedNull(prefissoServizio+PP_PREFIX+pName, v), 
					null, v);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoBinaryValue(idServizioApplicativo, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		Certificate cert = ArchiveLoader.loadFromKeystorePKCS12(pkcs12, APPLICATIVO_CLIENT_2, PASSWORD_123456);
		String v = cert.getCertificate().getSubject().getCN();
		String vAtteso = APPLICATIVO_CLIENT_2;
		assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, vAtteso), 
				vAtteso, v);
	}
	private void verificheDatabaseInChiaroGenericProperties() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroGenericProperties");
		
		// -- tokenPolicy validazione --
		verificheDatabaseInChiaroTokenPolicyValidazione();
		
		// -- tokenPolicy negoziazione -- 
		verificheDatabaseInChiaroTokenPolicyNegoziazione();
		
		// -- attribute authority -- 
		verificheDatabaseInChiaroAttributeAuthority();
		
	}
	private void verificheDatabaseInChiaroTokenPolicyValidazione() throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, STORE_PASSWORD_OPENSPCOOP);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, "Vault-TLS-IntrospectionUserInfo");
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWS);
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, "Vault-ValidazioneJWS-GovWay-JWT");
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE);
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWS);
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE);
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_GOVWAY_JWS);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD, "123456basicintro"); 
		verifiche.put(CostantiProprieta.POLICY_USER_INFO_AUTH_BASIC_PASSWORD, "123456basicuser"); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN, "TOKEN-BEARER-INTRO"); 
		verifiche.put(CostantiProprieta.POLICY_USER_INFO_AUTH_BEARER_TOKEN, "TOKEN-BEARER-USER"); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES_2);
		}
		
	}
	private void verificheDatabaseInChiaroTokenPolicyValidazione(Entry<String, String> entry, String nomePolicy) throws UtilsException {
		String pName = entry.getKey();
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getTokenPolicyValidazioneValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getTokenPolicyValidazioneEncValue(pName, nomePolicy);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	
	private void verificheDatabaseInChiaroTokenPolicyNegoziazione() throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, STORE_PASSWORD_OPENSPCOOP);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_TLS);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyNegoziazione(entry, "Vault-"+API_TOKEN_POLICY_NEGOZIAZIONE_OP_SIGNED_JWT);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD, PASSWORD_123456_BASIC); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_PASSWORD, "passwordOwnerCredentials"); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET, "secretSimmetrico"); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_2);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN, TOKEN_BEARER); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_3);
		}
		
	}
	private void verificheDatabaseInChiaroTokenPolicyNegoziazione(Entry<String, String> entry, String nomePolicy) throws UtilsException {
		String pName = entry.getKey();
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getTokenPolicyNegoziazioneValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getTokenPolicyNegoziazioneEncValue(pName, nomePolicy);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	
	private void verificheDatabaseInChiaroAttributeAuthority() throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, STORE_PASSWORD_OPENSPCOOP);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroAttributeAuthority(entry, "Vault-TLS-Trust-ServerAlias");
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.AA_REQUEST_JWT_SIGN_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroAttributeAuthority(entry, "Vault-JwsRequest");
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroAttributeAuthority(entry, "Vault-JwsResponse");
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.AA_AUTH_BASIC_PASSWORD, PASSWORD_123456_BASIC); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroAttributeAuthority(entry, API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.AA_AUTH_BEARER_TOKEN, TOKEN_BEARER); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroAttributeAuthority(entry, API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES_2);
		}
		
	}
	private void verificheDatabaseInChiaroAttributeAuthority(Entry<String, String> entry, String nomePolicy) throws UtilsException {
		String pName = entry.getKey();
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getAttributeAuthorityValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getAttributeAuthorityEncValue(pName, nomePolicy);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	
	private void verificheDatabaseInChiaroMessageSecurity() throws UtilsException {
		
		logCoreInfo("verificheDatabaseInChiaroMessageSecurity");
		
		// -- jose --
		verificheDatabaseInChiaroMessageSecurityJOSE();
		
		// -- xml --
		verificheDatabaseInChiaroMessageSecurityXML();
		
		// -- wssecurity --
		verificheDatabaseInChiaroMessageSecurityWSSecurity();
		
	}
	private void verificheDatabaseInChiaroMessageSecurityJOSE() throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE);
			// response c'è JWK, senza password
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);  
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT);
			// response c'è JWK, senza password
		}
		
		
		// ** encrypt-sync **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_SYNC_KEY123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC);
		}
		
		
		// ** encrypt-header **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_KEYSTORE_PASSWORD, PASSWORD_123456);  
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_KEY1_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_TRUSTSTORE_PASSWORD, PASSWORD_123456);  
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER);
			// response c'è JWK, senza password
		}
		
	}
	private void verificheDatabaseInChiaroMessageSecurityXML() throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE);
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.DECRYPTION_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT);
		}
		
	}
	
	private void verificheDatabaseInChiaroMessageSecurityWSSecurity() throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE);
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.DECRYPTION_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT);
		}
		
		
		// ** saml-bearer **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);
			verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER);
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES);
			verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);
			verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY);
		}

		
	}
	
	private void verificheDatabaseInChiaroMessageSecurityPortaDelegataRequest(Entry<String, String> entry, String nomePortaDefault, String nomeAzione) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_DELEGATE, nomePortaDefault, nomeAzione);
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataRequestValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataRequestEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	private void verificheDatabaseInChiaroMessageSecurityPortaDelegataResponse(Entry<String, String> entry, String nomePortaDefault, String nomeAzione) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_DELEGATE, nomePortaDefault, nomeAzione);
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataResponseValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataResponseEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	private void verificheDatabaseInChiaroMessageSecurityPortaApplicativaRequest(Entry<String, String> entry, String nomePortaDefault, String nomeAzione) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_APPLICATIVE, nomePortaDefault, nomeAzione);
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaRequestValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaRequestEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
	}
	private void verificheDatabaseInChiaroMessageSecurityPortaApplicativaResponse(Entry<String, String> entry, String nomePortaDefault, String nomeAzione) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_APPLICATIVE, nomePortaDefault, nomeAzione);
		
		String vAtteso = entry.getValue();
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaResponseValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, vAtteso), 
					vAtteso, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaResponseEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList==null || vList.isEmpty() || vList.get(0)==null;
		assertTrue(getMessageExpectedEmptyOrNull(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
	}

	
	
	
	
	// ** VERIFICHE DB CIFRATO **
	
	private void verificheDatabaseCifrato(String prefix) throws UtilsException, DriverRegistroServiziException {
		
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
		
		// ** VERIFICHE colonne name, value_binary o enc_value_string,value_string per protocol_properties
		verificheDatabaseCifratoProtocolProperties(prefix);
		
		// ** VERIFICHE colonne nome,valore,enc_value su generic_property
		verificheDatabaseCifratoGenericProperties(prefix);
		
		// ** VERIFICHE colonne nome,valore,enc_value su pd_security_request,pd_security_response,pa_security_request e pa_security_response
		verificheDatabaseCifratoMessageSecurity(prefix);
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
	private void verificheDatabaseCifratoProtocolProperties(String prefix) throws UtilsException, DriverRegistroServiziException {
		
		logCoreInfo("verificheDatabaseCifratoProtocolProperties");
		
		// -- erogazioni --
		verificheDatabaseCifratoProtocolPropertiesErogazioni(prefix);
		
		// -- fruizioni -- 
		verificheDatabaseCifratoProtocolPropertiesFruizioni(prefix);
		
		// -- applicativi -- 
		verificheDatabaseCifratoProtocolPropertiesApplicativi(prefix);

	}
	private void verificheDatabaseCifratoProtocolPropertiesErogazioni(String prefix) throws UtilsException, DriverRegistroServiziException {
		
		Map<String, String > verifiche = new HashMap<>();
		// richiesta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		// risposta
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_PATH, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		String prefissoServizio = getPrefissoServizio(idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringValue(idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringEncValue(idServizio, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_ARCHIVIO, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoServizio(idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringValue(idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioStringEncValue(idServizio, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyServizioBinaryValue(idServizio, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		String v = new String(pkcs12);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, prefix), 
				expected);
	}
	private void verificheDatabaseCifratoProtocolPropertiesFruizioni(String prefix) throws UtilsException, DriverRegistroServiziException {
		
		Map<String, String > verifiche = new HashMap<>();
		// richiesta
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		// risposta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		
		IDSoggetto idFruitore = new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO_FRUITORE);
		
		IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_PATH_FRUIZIONE, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		String prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_ARCHIVIO_FRUIZIONE, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreBinaryValue(idFruitore, idServizio, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		String v = new String(pkcs12);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, prefix), 
				expected);
		
		verifiche = new HashMap<>();
		// richiesta
		// definito sull'applicativo		
		// risposta
		verifiche.put(CostantiDB.MODIPA_PROFILO_SICUREZZA_MESSAGGIO_CERTIFICATI_TRUSTSTORE_PASSWORD, PASSWORD_123456); 
		idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(Costanti.MODIPA_PROTOCOL_NAME, API_CONNETTORI_APPLICATIVO, 
				Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO,
				1);
		prefissoServizio = getPrefissoFruizione(idFruitore, idServizio);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringValue(idFruitore, idServizio, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyFruitoreStringEncValue(idFruitore, idServizio, pName);
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
	}
	private void verificheDatabaseCifratoProtocolPropertiesApplicativi(String prefix) throws UtilsException {
		
		Map<String, String > verifiche = new HashMap<>();
		verifiche.put(CostantiDB.MODIPA_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiDB.MODIPA_KEY_PASSWORD, PASSWORD_123456);
		
		IDSoggetto idFruitore = new IDSoggetto(Costanti.MODIPA_PROTOCOL_NAME, SOGGETTO_INTERNO_FRUITORE);
		
		IDServizioApplicativo idServizioApplicativo = new IDServizioApplicativo();
		idServizioApplicativo.setIdSoggettoProprietario(idFruitore);
		
		idServizioApplicativo.setNome("ApplicativoVaultModiPath");
		String prefissoServizio = getPrefissoApplicativo(idServizioApplicativo);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringEncValue(idServizioApplicativo, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		
		idServizioApplicativo.setNome("ApplicativoVaultModiArchivio");
		prefissoServizio = getPrefissoApplicativo(idServizioApplicativo);
		for (Entry<String, String> entry : verifiche.entrySet()) {
			String pName = entry.getKey();
			String vAtteso = prefix;
			String v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringValue(idServizioApplicativo, pName);
			assertEquals(getMessageExpected(prefissoServizio+PP_PREFIX+pName, v, vAtteso), 
					vAtteso, v);
			v = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoStringEncValue(idServizioApplicativo, pName);
			boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+pName, v, prefix), 
					expected);
		}
		// binary
		byte[] pkcs12 = ConfigLoader.dbUtils.getProtocolPropertyPropertyApplicativoBinaryValue(idServizioApplicativo, CostantiDB.MODIPA_KEYSTORE_ARCHIVE);
		String v = new String(pkcs12);
		boolean expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(prefissoServizio+PP_PREFIX+CostantiDB.MODIPA_KEYSTORE_ARCHIVE, v, prefix), 
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
		
		// ** VERIFICHE colonne env_value, valore su pd_properties
		verificheDatabaseProprietaCifratePortaDelegata(prefix);
		// ** VERIFICHE colonne env_value, valore su pd_auth_properties
		verificheDatabaseProprietaAutenticazioneCifratePortaDelegata(prefix);
		// ** VERIFICHE colonne env_value, valore su pd_authz_properties
		verificheDatabaseProprietaAutorizzazioneCifratePortaDelegata(prefix);
		// ** VERIFICHE colonne env_value, valore su pd_authzc_properties
		verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaDelegata(prefix);
		
		// ** VERIFICHE colonne env_value, valore su pa_properties
		verificheDatabaseProprietaCifratePortaApplicativa(prefix);
		// ** VERIFICHE colonne env_value, valore su pa_auth_properties
		verificheDatabaseProprietaAutenticazioneCifratePortaApplicativa(prefix);
		// ** VERIFICHE colonne env_value, valore su pa_authz_properties
		verificheDatabaseProprietaAutorizzazioneCifratePortaApplicativa(prefix);
		// ** VERIFICHE colonne env_value, valore su pa_authzc_properties
		verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaApplicativa(prefix);
		
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
	private void verificheDatabaseProprietaCifratePortaDelegata(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifratePortaDelegata");
		
		String nomeProprieta = "vaultTestNomeCifratoPorta";
		String pwd = ConfigLoader.dbUtils.getPortaDelegataPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaDelegataPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutenticazioneCifratePortaDelegata(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutenticazioneCifratePortaDelegata");
		
		String nomeProprieta = "vaultTestNomeCifratoAutenticazione";
		String pwd = ConfigLoader.dbUtils.getPortaDelegataAuthnPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHN_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaDelegataAuthnPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHN_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutorizzazioneCifratePortaDelegata(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutorizzazioneCifratePortaDelegata");
		
		String nomeProprieta = "vaultTestNomeCifratoAutorizzazione";
		String pwd = ConfigLoader.dbUtils.getPortaDelegataAuthzPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHZ_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaDelegataAuthzPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHZ_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaDelegata(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaDelegata");
		
		String nomeProprieta = "vaultTestNomeCifratoAutorizzazioneContenuti";
		String pwd = ConfigLoader.dbUtils.getPortaDelegataAuthcPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHC_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaDelegataAuthcPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHC_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaCifratePortaApplicativa(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifratePortaApplicativa");
		
		String nomeProprieta = "vaultTestNomeCifratoPorta";
		String pwd = ConfigLoader.dbUtils.getPortaApplicativaPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaApplicativaPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutenticazioneCifratePortaApplicativa(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutenticazioneCifratePortaApplicativa");
		
		String nomeProprieta = "vaultTestNomeCifratoAutenticazione";
		String pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthnPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHN_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthnPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHN_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutorizzazioneCifratePortaApplicativa(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutorizzazioneCifratePortaApplicativa");
		
		String nomeProprieta = "vaultTestNomeCifratoAutorizzazione";
		String pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthzPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHZ_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthzPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHZ_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	private void verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaApplicativa(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaAutorizzazioneContenutiCifratePortaApplicativa");
		
		String nomeProprieta = "vaultTestNomeCifratoAutorizzazioneContenuti";
		String pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthcPropertyValue(nomeProprieta);
		assertEquals(getMessageExpected(PROPRIETA_PORTA_AUTHC_PREFIX+nomeProprieta, pwd, prefix), 
				prefix, pwd);
		pwd = ConfigLoader.dbUtils.getPortaApplicativaAuthcPropertyEncValue(nomeProprieta);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(PROPRIETA_PORTA_AUTHC_PREFIX+nomeProprieta, pwd, prefix), 
				expected);
		
	}
	
	private void verificheDatabaseCifratoGenericProperties(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseProprietaCifrateGenericProperties");
		
		// -- tokenPolicy validazione --
		verificheDatabaseProprietaCifrateTokenPolicyValidazione(prefix);
		
		// -- tokenPolicy negoziazione -- 
		verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(prefix);
		
		// -- attribute authority -- 
		verificheDatabaseProprietaCifrateAttributeAuthority(prefix);
		
	}
	private void verificheDatabaseProprietaCifrateTokenPolicyValidazione(String prefix) throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, PASSWORD_123456);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, PASSWORD_123456);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, "Vault-TLS-IntrospectionUserInfo", prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWS, prefix);
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, "Vault-ValidazioneJWS-GovWay-JWT", prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_VALIDAZIONE_JWE, prefix);
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWS, prefix);
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_JWE, prefix);
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_FORWARD_GOVWAY_JWS, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.POLICY_INTROSPECTION_AUTH_BASIC_PASSWORD, "123456basicintro"); 
		verifiche.put(CostantiProprieta.POLICY_USER_INFO_AUTH_BASIC_PASSWORD, "123456basicuser"); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_INTROSPECTION_AUTH_BEARER_TOKEN, "TOKEN-BEARER-INTRO"); 
		verifiche.put(CostantiProprieta.POLICY_USER_INFO_AUTH_BEARER_TOKEN, "TOKEN-BEARER-USER"); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyValidazione(entry, API_TOKEN_POLICY_VALIDAZIONE_OP_OTHER_PROPERTIES_2, prefix);
		}
		
	}
	private void verificheDatabaseProprietaCifrateTokenPolicyValidazione(Entry<String, String> entry, String nomePolicy, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		List<String> vList = ConfigLoader.dbUtils.getTokenPolicyValidazioneValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getTokenPolicyValidazioneEncValue(pName, nomePolicy);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(TOKEN_POLICY_VALIDAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
		
	}
	
	private void verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(String prefix) throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, STORE_PASSWORD_OPENSPCOOP);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_TLS, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_SIGN_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(entry, "Vault-"+API_TOKEN_POLICY_NEGOZIAZIONE_OP_SIGNED_JWT, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_AUTH_BASIC_PASSWORD, PASSWORD_123456_BASIC); 
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_PASSWORD, "passwordOwnerCredentials"); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_JWT_CLIENT_SECRET, "secretSimmetrico"); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_2, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.POLICY_RETRIEVE_TOKEN_AUTH_BEARER_TOKEN, TOKEN_BEARER); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(entry, API_TOKEN_POLICY_NEGOZIAZIONE_OP_OTHER_PROPERTIES_3, prefix);
		}
		
	}
	private void verificheDatabaseProprietaCifrateTokenPolicyNegoziazione(Entry<String, String> entry, String nomePolicy, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		List<String> vList = ConfigLoader.dbUtils.getTokenPolicyNegoziazioneValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getTokenPolicyNegoziazioneEncValue(pName, nomePolicy);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(TOKEN_POLICY_NEGOZIAZIONE_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
		
	}
	
	private void verificheDatabaseProprietaCifrateAttributeAuthority(String prefix) throws UtilsException {
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP); 
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_STORE_PASSWORD, STORE_PASSWORD_OPENSPCOOP_JKS);
		verifiche.put(CostantiConnettori.CONNETTORE_HTTPS_KEY_PASSWORD, STORE_PASSWORD_OPENSPCOOP);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateAttributeAuthority(entry, "Vault-TLS-Trust-ServerAlias", prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.AA_REQUEST_JWT_SIGN_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.AA_REQUEST_JWT_SIGN_KEY_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateAttributeAuthority(entry, "Vault-JwsRequest", prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 

		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateAttributeAuthority(entry, "Vault-JwsResponse", prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD, PASSWORD_PROXY); 
		verifiche.put(CostantiProprieta.AA_AUTH_BASIC_PASSWORD, PASSWORD_123456_BASIC); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateAttributeAuthority(entry, API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.AA_AUTH_BEARER_TOKEN, TOKEN_BEARER); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseProprietaCifrateAttributeAuthority(entry, API_ATTRIBUTE_AUTHORITY_OP_OTHER_PROPERTIES_2, prefix);
		}
		
	}
	private void verificheDatabaseProprietaCifrateAttributeAuthority(Entry<String, String> entry, String nomePolicy, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		List<String> vList = ConfigLoader.dbUtils.getAttributeAuthorityValue(pName, nomePolicy);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getAttributeAuthorityEncValue(pName, nomePolicy);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(ATTRIBUTE_AUTHORITY_PREFIX+nomePolicy+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
		
	}

	
	private void verificheDatabaseCifratoMessageSecurity(String prefix) throws UtilsException {
		
		logCoreInfo("verificheDatabaseCifratoMessageSecurity");
		
		// -- jose --
		verificheDatabaseCifratoMessageSecurityJOSE(prefix);
		
		// -- xml --
		verificheDatabaseCifratoMessageSecurityXML(prefix);
		
		// -- wssecurity --
		verificheDatabaseCifratoMessageSecurityWSSecurity(prefix);
		
	}
	private void verificheDatabaseCifratoMessageSecurityJOSE(String prefix) throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE, prefix);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_SIGNATURE, prefix);
			// response c'è JWK, senza password
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT, prefix);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);  
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT, prefix);
			// response c'è JWK, senza password
		}
		
		
		// ** encrypt-sync **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		verifiche.put(CostantiProprieta.RS_SECURITY_KEY_PASSWORD, PASSWORD_SYNC_KEY123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC, prefix);
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_SYMMETRIC, prefix);
		}
		
		
		// ** encrypt-header **
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.RS_SECURITY_KEYSTORE_PASSWORD, PASSWORD_123456);
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER, prefix);
			// response c'è JWK, senza password
		}
		
		verifiche = new HashMap<>();
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_KEYSTORE_PASSWORD, PASSWORD_123456);  
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_KEY1_PASSWORD, PASSWORD_123456); 
		verifiche.put(CostantiProprieta.MESSAGE_SECURITY_JOSE_TRUSTSTORE_PASSWORD, PASSWORD_123456);  
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_JOSE, API_MESSAGE_SECURITY_JOSE_OP_ENCRYPT_HEADER, prefix);
			// response c'è JWK, senza password
		}
		
	}
	
	private void verificheDatabaseCifratoMessageSecurityXML(String prefix) throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_SIGNATURE, prefix);
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.DECRYPTION_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_XML, API_MESSAGE_SECURITY_XML_OP_ENCRYPT, prefix);
		}
		
	}
	
	private void verificheDatabaseCifratoMessageSecurityWSSecurity(String prefix) throws UtilsException {
		
		// ** signature **
		
		Map<String, String> verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SIGNATURE, prefix);
		}
		
		
		// ** encrypt **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.DECRYPTION_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_ENCRYPT, prefix);
		}
		
		
		// ** saml-bearer **
		
		verifiche = new HashMap<>();
		verifiche.put(SecurityConstants.SIGNATURE_PASSWORD, PASSWORD_123456); 
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER, prefix);
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES, prefix);
			verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY, prefix);
		}
		
		verifiche = new HashMap<>();
		verifiche.put(Merlin.OLD_PREFIX+Merlin.KEYSTORE_PASSWORD, PASSWORD_123456); 
		
		for (Entry<String, String> entry : verifiche.entrySet()) {
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_BEARER, prefix);
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_SENDER_VOUCHES, prefix);
			verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(entry, PORTA_DELEGATA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY, prefix);
			verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(entry, PORTA_APPLICATIVA_MESSAGE_SECURITY_WSS, API_MESSAGE_SECURITY_WSS_OP_SAML_HOLDER_OF_KEY, prefix);
		}

		
	}
	
	
	private void verificheDatabaseCifratoMessageSecurityPortaDelegataRequest(Entry<String, String> entry, String nomePortaDefault, String nomeAzione, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_DELEGATE, nomePortaDefault, nomeAzione);
		
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataRequestValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataRequestEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(MESSAGE_SECURITY_FRUIZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
		
	}
	private void verificheDatabaseCifratoMessageSecurityPortaDelegataResponse(Entry<String, String> entry, String nomePortaDefault, String nomeAzione, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_DELEGATE, nomePortaDefault, nomeAzione);
		
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataResponseValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaDelegataResponseEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(MESSAGE_SECURITY_FRUIZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
	}
	private void verificheDatabaseCifratoMessageSecurityPortaApplicativaRequest(Entry<String, String> entry, String nomePortaDefault, String nomeAzione, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_APPLICATIVE, nomePortaDefault, nomeAzione);
		
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaRequestValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaRequestEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(MESSAGE_SECURITY_EROGAZIONE_RICHIESTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
	}
	private void verificheDatabaseCifratoMessageSecurityPortaApplicativaResponse(Entry<String, String> entry, String nomePortaDefault, String nomeAzione, String prefix) throws UtilsException {
		String pName = entry.getKey();
		
		String nomePorta = ConfigLoader.dbUtils.getNomePorta(CostantiDB.PORTE_APPLICATIVE, nomePortaDefault, nomeAzione);
		
		List<String> vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaResponseValue(pName, nomePortaDefault, nomeAzione);
		boolean expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			assertEquals(getMessageExpected(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					prefix, v);
		}
		
		vList = ConfigLoader.dbUtils.getMessageSecurityPortaApplicativaResponseEncValue(pName, nomePortaDefault, nomeAzione);
		expected = vList!=null && !vList.isEmpty();
		assertTrue(getMessageExpectedNotEmpty(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, vList), 
				expected);
		for (String v : vList) {
			expected = v!=null && v.startsWith(prefix) && v.length()>prefix.length();
			assertTrue(getMessageExpectedStartsWith(MESSAGE_SECURITY_EROGAZIONE_RISPOSTA_PREFIX+nomePorta+NOME_PROPRIETA+pName, v, prefix), 
					expected);
		}
	}
}