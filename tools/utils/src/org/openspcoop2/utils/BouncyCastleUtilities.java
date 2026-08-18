/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.utils;

import java.security.Security;
import java.util.Arrays;
import java.util.List;

/**
 * Utilita' per la registrazione del provider BouncyCastle.
 *
 * NOTA: questa classe e' l'unica del package a referenziare 'org.bouncycastle'. La verifica del bytecode effettuata dalla jvm
 * al caricamento di una classe carica anche i tipi utilizzati nelle assegnazioni: se il riferimento a BouncyCastle risiedesse
 * in 'Utilities', o in un'altra classe di uso generale, ogni strumento che non distribuisce 'bcprov' non riuscirebbe piu' ad
 * utilizzarla, fallendo con 'NoClassDefFoundError'. Tenendo il riferimento confinato qui, la classe viene caricata solamente
 * dove il provider serve davvero.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class BouncyCastleUtilities {

	private BouncyCastleUtilities() {}

	/** Alias con cui BouncyCastle intercetta le uniche lookup che la classe 'sun.security.pkcs12.PKCS12KeyStore' del jdk
	 *  effettua in modo generico, cioe' senza indicare un provider, per decifrare un keystore PKCS12.
	 *  Vengono rimossi dall'istanza del provider prima della registrazione: si veda 'addBouncyCastleAfterSun'. */
	private static final String [] BOUNCY_CASTLE_ALIAS_PKCS12 = new String[] {
		"Alg.Alias.SecretKeyFactory.PBE",
		"Alg.Alias.Cipher.PBEWITHSHA1ANDDESEDE",
		"Alg.Alias.SecretKeyFactory.PBEWITHSHA1ANDDESEDE"
	};

	/**
	 * Registra il provider BouncyCastle subito dopo il provider 'SUN', quindi davanti a tutti gli altri provider del jdk.
	 *
	 * La posizione e' portante e non va modificata: sulla trasformazione 'RSA/ECB/OAEPWithSHA-256AndMGF1Padding' (e sulle
	 * varianti con SHA-384 e SHA-512) BouncyCastle e SunJCE non sono interoperabili, poiche' SunJCE utilizza MGF1 su SHA-1,
	 * che e' il default di 'OAEPParameterSpec', mentre BouncyCastle utilizza MGF1 sul digest indicato nel nome. Posponendo
	 * BouncyCastle a SunJCE diventerebbero illeggibili tutti i segreti gia' cifrati con BYOK e le librerie che non indicano
	 * parametri OAEP espliciti - fra cui 'cxf-rt-rs-security-jose', che mappa 'RSA-OAEP-256' su quella trasformazione -
	 * produrrebbero contenuti non conformi a RFC 7518.
	 *
	 * Vengono pero' rimossi tre alias dall'istanza del provider. La classe 'sun.security.pkcs12.PKCS12KeyStore' risolve in
	 * modo generico sia i servizi 'SecretKeyFactory' sia i servizi 'Cipher' necessari a decifrare un keystore PKCS12: se una
	 * parte di quei servizi viene fornita da BouncyCastle e un'altra da SunJCE, le due implementazioni non si accordano sulla
	 * codifica della password - UTF-8 per SunJCE, UTF-16BE seguito da 0x0000 secondo la convenzione PKCS#12 per BouncyCastle -
	 * e la lettura fallisce con 'BadPaddingException', segnalata come "keystore password was incorrect" sui keystore cifrati
	 * con algoritmi moderni (PBES2/AES-256, default di OpenSSL 3 e di keytool dal jdk 16) oppure come "Get Key failed: pad
	 * block corrupted" su quelli cifrati con gli algoritmi PKCS#12 tradizionali, pur essendo file e password corretti.
	 * Rimuovendo i tre alias, quelle sole lookup vengono servite da SunJCE in modo coerente e la lettura funziona su entrambi
	 * i formati, mentre tutti gli altri algoritmi restano forniti da BouncyCastle. Gli alias rimossi non sono utilizzati da
	 * GovWay ed il nome canonico BouncyCastle 'PBEWITHSHAAND3-KEYTRIPLEDES-CBC' resta comunque disponibile.
	 *
	 * NOTA: l'elenco dipende da quali nomi la classe del jdk risolve internamente; una versione futura del jdk potrebbe
	 * richiederne altri. La copertura e' verificata da 'KeystoreTest' e da 'ProviderBenchmarkTest'.
	 **/
	public static void addBouncyCastleAfterSun(boolean overrideIfExists) {
		/**Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());*/
		java.security.Provider provider = newBouncyCastleProvider();
		if(Security.getProvider(provider.getName())!=null) {
			if(!overrideIfExists) {
				return;
			}
			Security.removeProvider(provider.getName());
		}
		// NOTA: utility Security.insertProviderAt utilizza una posizione vera e non da programmatore che parte da 0!!!!!!!!!!!
		Security.insertProviderAt(provider, 2); // lasciare alla posizione 1 il provider 'SUN'
	}

	/** Istanza del provider BouncyCastle priva degli alias che interferiscono con la lettura dei keystore PKCS12 */
	public static java.security.Provider newBouncyCastleProvider() {
		java.security.Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
		for (String alias : BOUNCY_CASTLE_ALIAS_PKCS12) {
			provider.remove(alias);
		}
		return provider;
	}

	/** Alias rimossi da 'newBouncyCastleProvider' */
	public static List<String> getBouncyCastleAliasRimossiPkcs12(){
		return Arrays.asList(BOUNCY_CASTLE_ALIAS_PKCS12);
	}
}
