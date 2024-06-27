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
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.UtilsException;
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
	private static final String NOME_SA_HTTP = "gw_SoggettoInternoVaultTest/gw_VaultTestConnettori/v1";
	
	private String getMessageExpectedStartsWith(String found, String prefix) {
		return "Found '"+found+"'; expected start with '"+prefix+"'";
	}
	
	@Test
	public void step0aVerificaInizialeDatabase() throws UtilsException {
				
		logCoreInfo("@step0aVerificaInizialeDatabase");
		
		verificheDatabaseInCharo();
		
	}
	
	@Test
	public void step0bInvocazioneServiziGovway() throws UtilsException, ProtocolException, HttpUtilsException {
		
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
	public void step1cInvocazioneServiziGovwayConSecretLockedByDefaultPolicy() throws UtilsException, ProtocolException, HttpUtilsException {
		
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
	public void step2eInvocazioneServiziGovwayConSecretLockedByGwKeysPolicy() throws UtilsException, ProtocolException, HttpUtilsException {
		
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
	public void step3eInvocazioneServiziGovwayConSecretsPlain() throws UtilsException, ProtocolException, HttpUtilsException {
		
		logCoreInfo("@step3eInvocazioneServiziGovwayConSecretsPlain");
		
		invocazioneGovWay();
	}
	
	private void invocazioneGovWay() throws UtilsException, HttpUtilsException, ProtocolException {
		resetCache();
		
		Utilities.testRest(logCore, TipoServizio.EROGAZIONE, API, OP_HTTP);
	}
	
	private void verificheDatabaseInCharo() throws UtilsException {
		// Verifiche database in chiaro
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SA_HTTP);
		assertEquals("PasswordVaultTestConnettoreHttp", pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SA_HTTP);
		assertEquals(null, pwd);
	}
	
	private void verificheDatabaseCifrato(String prefix) throws UtilsException {
		// Verifiche database cifrato
		String pwd = ConfigLoader.dbUtils.getServiziApplicativiPasswordInv(NOME_SA_HTTP);
		assertEquals(prefix, pwd);
		pwd = ConfigLoader.dbUtils.getServiziApplicativiEncPasswordInv(NOME_SA_HTTP);
		boolean expected = pwd!=null && pwd.startsWith(prefix) && pwd.length()>prefix.length();
		assertTrue(getMessageExpectedStartsWith(pwd, prefix), expected);
	}
	
}