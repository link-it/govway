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
import java.util.ArrayList;
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
	 * Registra il provider BouncyCastle subito dopo 'SunJCE', quindi dopo i provider del jdk che implementano gli algoritmi
	 * di uso piu' frequente.
	 *
	 * I provider del jdk vengono accelerati dagli intrinsics di HotSpot, che sostituiscono i metodi delle loro classi con
	 * codice macchina che utilizza le istruzioni dedicate della cpu; BouncyCastle e' java puro e non puo' beneficiarne.
	 * Lasciandolo davanti a 'SunJCE' verrebbe utilizzato anche per gli algoritmi che il jdk implementa in modo accelerato,
	 * fra cui la cifratura dei record TLS, con un costo misurato di circa 7 volte sul throughput. Posponendolo, il jdk
	 * serve i 310 servizi che entrambi dichiarano, mentre BouncyCastle continua a fornire i circa 3200 che il jdk non ha.
	 *
	 * La posizione non e' arbitraria e non va modificata senza rieseguire 'ProviderMigrationTest': su alcuni servizi le due
	 * implementazioni non producono dati interoperabili, in particolare sulle trasformazioni 'RSA/ECB/OAEPWith&lt;digest&gt;AndMGF1Padding'
	 * con digest superiore a SHA-1, dove SunJCE utilizza MGF1 su SHA-1 - il default di 'OAEPParameterSpec' - mentre BouncyCastle
	 * utilizza il digest indicato nel nome. Per questo motivo i punti di GovWay che le utilizzano indicano esplicitamente i
	 * parametri, si veda 'OAEPUtils', ed altrettanto fa la libreria 'cxf-rt-rs-security-jose' nella versione gov4j-2.
	 *
	 * Dall'istanza del provider vengono inoltre rimossi tre alias. La classe 'sun.security.pkcs12.PKCS12KeyStore' del jdk
	 * risolve in modo generico sia i servizi 'SecretKeyFactory' sia i servizi 'Cipher' necessari a decifrare un keystore
	 * PKCS12: se una parte di quei servizi venisse fornita da BouncyCastle e un'altra da SunJCE, le due implementazioni non
	 * si accorderebbero sulla codifica della password - UTF-8 per SunJCE, UTF-16BE seguito da 0x0000 secondo la convenzione
	 * PKCS#12 per BouncyCastle - e la lettura fallirebbe con 'BadPaddingException'. Con la posizione attuale la situazione
	 * non si presenta, poiche' 'SunJCE' precede BouncyCastle, ma la rimozione viene mantenuta come salvaguardia nel caso in
	 * cui il provider venisse nuovamente anteposto. Gli alias rimossi non sono utilizzati da GovWay ed il nome canonico
	 * BouncyCastle 'PBEWITHSHAAND3-KEYTRIPLEDES-CBC' resta comunque disponibile.
	 *
	 * La copertura e' verificata da 'ProviderMigrationTest', 'ProviderBenchmarkTest' e 'KeystoreTest'.
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
		Security.insertProviderAt(provider, getPosizioneSuccessivaSunJCE());
	}

	/** Posizione immediatamente successiva a 'SunJCE'; se il provider non fosse presente, il provider viene accodato */
	private static int getPosizioneSuccessivaSunJCE() {
		List<java.security.Provider> providers = getProviders();
		for (int i = 0; i < providers.size(); i++) {
			if(PROVIDER_SUN_JCE.equals(providers.get(i).getName())) {
				return i+2;
			}
		}
		return providers.size()+1;
	}

	public static final String PROVIDER_SUN_JCE = "SunJCE";

	private static List<java.security.Provider> getProviders(){
		List<java.security.Provider> l = new ArrayList<>();
		java.security.Provider [] p = Security.getProviders();
		if(p!=null && p.length>0) {
			l.addAll(Arrays.asList(p));
		}
		return l;
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
