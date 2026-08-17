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

package org.openspcoop2.utils.security;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.openspcoop2.utils.Utilities;

/**
 * ProviderUtils
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author  $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ProviderUtils {
	
	private ProviderUtils(){}

	public static void addBouncyCastle() {
		add(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static void add(java.security.Provider provider) {
		java.security.Security.addProvider(provider);
	}
	
	public static final String PROVIDER_SUN_JCE = Utilities.PROVIDER_SUN_JCE;
	
	/**
	 * Registra il provider BouncyCastle subito dopo i provider standard del jdk, in particolare dopo 'SunJCE'.
	 *
	 * La posizione non e' un dettaglio: la classe 'sun.security.pkcs12.PKCS12KeyStore', utilizzata per leggere i keystore PKCS12,
	 * risolve in modo generico (senza indicare un provider) sia i servizi 'SecretKeyFactory' sia i servizi 'Cipher' necessari
	 * a decifrare il contenuto del keystore. Se una parte di quei servizi viene servita da BouncyCastle e un'altra da SunJCE,
	 * le due implementazioni non si accordano sulla codifica della password - UTF-8 per SunJCE, UTF-16BE seguito da 0x0000
	 * secondo la convenzione PKCS#12 per BouncyCastle - e la lettura fallisce con 'BadPaddingException', segnalata come
	 * "keystore password was incorrect" oppure "Get Key failed: pad block corrupted", pur essendo file e password corretti.
	 *
	 * Anteponendo BouncyCastle a SunJCE si verificano entrambi i disallineamenti:
	 * - sui keystore cifrati con algoritmi moderni (PBES2/AES-256, default di OpenSSL 3 e di keytool dal jdk 16) la chiave viene
	 *   prodotta da BouncyCastle tramite l'alias generico 'SecretKeyFactory.PBE' e utilizzata dal cipher di SunJCE;
	 * - sui keystore cifrati con gli algoritmi PKCS#12 tradizionali la chiave privata viene decifrata dal cipher di BouncyCastle.
	 * Posponendolo a SunJCE, tutti i servizi coinvolti restano serviti da SunJCE e la lettura funziona in entrambi i casi,
	 * mentre BouncyCastle continua a fornire tutti gli algoritmi non presenti nei provider del jdk.
	 **/
	public static void addBouncyCastleAfterSun(boolean overrideIfExists) {
		Utilities.addBouncyCastleAfterSun(overrideIfExists);
	}

	public static void addIfNotExists(java.security.Provider provider, int position) {
		if(Security.getProvider(provider.getName())==null) {
			Security.insertProviderAt(provider, position); 
		}
	}
	// NOTA: utility Security.insertProviderAt utilizza una posizione vera e non da programmatore che parte da 0!!!!!!!!!!!
	public static void addOverrideIfExists(java.security.Provider provider, int position) { 
		if(Security.getProvider(provider.getName())!=null) {
			List<java.security.Provider> l = getProviders();
			if(!l.isEmpty() && l.size()>(position-1) && l.get((position-1)).getName().equals(provider.getName())) {
				return; // gia presente alla posizione due
			}			
			Security.removeProvider(provider.getName());
		}
		Security.insertProviderAt(provider, position); 
	}
	
	public static boolean existsBouncyCastle() {
		return exists(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static boolean exists(java.security.Provider provider) {
		return Security.getProvider(provider.getName())!=null;
	}
	
	public static boolean existsBouncyCastle(int position) {
		return exists(new org.bouncycastle.jce.provider.BouncyCastleProvider(), position);
	}
	public static boolean exists(java.security.Provider provider, int position) {
		List<java.security.Provider> l = getProviders();
		if(l.isEmpty() || l.size()<position) {
			return false;
		}
		return l.get(position).getName().equals(provider.getName());
	}
	
	public static void removeBouncyCastle() {
		remove(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	public static void remove(java.security.Provider provider) {
		Security.removeProvider(provider.getName());
	}
	
	public static List<java.security.Provider> getProviders(){
		return Utilities.getProviders();
	}
	public static List<String> getProviderNames(){
		List<String> l = new ArrayList<>();
		List<java.security.Provider> lp = getProviders();
		if(!lp.isEmpty()) {
			for (java.security.Provider provider : lp) {
				l.add(provider.getName());
			}
		}
		return l;
	}
 }
