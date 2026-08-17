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

package org.openspcoop2.utils.security.test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.security.ProviderUtils;

/**
 * ProviderBenchmarkTest
 *
 * Verifica e documenta la posizione in cui viene registrato il provider BouncyCastle da
 * 'Utilities.addBouncyCastleAfterSun', utilizzata a runtime da tutti i processi GovWay.
 *
 * Il provider viene registrato dopo 'SunJCE' e non davanti ai provider del jdk, poiche' la classe
 * 'sun.security.pkcs12.PKCS12KeyStore' risolve in modo generico, cioe' senza indicare un provider, sia i servizi
 * 'SecretKeyFactory' sia i servizi 'Cipher' necessari a decifrare un keystore PKCS12: se una parte di quei servizi
 * viene servita da BouncyCastle e un'altra da SunJCE, le due implementazioni non si accordano sulla codifica della
 * password e la lettura del keystore fallisce con 'BadPaddingException'.
 *
 * Questa classe verifica che la scelta non comporti effetti collaterali indesiderati:
 * - 'testServiziCondivisi' elenca i servizi che, per effetto del posizionamento, passano da BouncyCastle a un provider
 *   del jdk, e verifica che i provider registrati DOPO 'SunJCE' non vengano scavalcati (fatta eccezione per i servizi
 *   documentati nella lista sottostante, che erano gia' serviti da BouncyCastle anche in precedenza);
 * - 'testBenchmarkServiziCondivisi' misura i medesimi servizi sulle due implementazioni, per verificare che il
 *   posizionamento non introduca un degrado prestazionale.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProviderBenchmarkTest {

	private ProviderBenchmarkTest(){}

	/** Servizi che BouncyCastle continua a fornire scavalcando un provider registrato dopo 'SunJCE'.
	 *  Erano serviti da BouncyCastle anche con il posizionamento precedente, quindi non costituiscono una variazione di comportamento. */
	private static final Set<String> SERVIZI_ATTESI_SCAVALCATI = new TreeSet<>(List.of("CertStore.LDAP"));

	/** Prefisso dei provider PKCS11, esclusi dal confronto.
	 *  Non fanno parte dell'installazione statica del jdk: vengono registrati a runtime da 'HSMManager.providerInit' per ogni
	 *  keystore hardware configurato, quindi la loro presenza e la loro posizione dipendono dalla configurazione e, nella
	 *  testsuite, dall'ordine di esecuzione dei test. In GovWay sono inoltre sempre utilizzati indicando esplicitamente
	 *  l'istanza del provider (si veda 'HSMKeystore.getInstance'), quindi la precedenza di BouncyCastle e' ininfluente. */
	private static final String PREFISSO_PROVIDER_PKCS11 = "SunPKCS11";

	private static final String PREFISSO_ALIAS = "Alg.Alias.";

	/** Soglia oltre la quale un servizio servito da un provider del jdk viene considerato in degrado rispetto a BouncyCastle.
	 *  Volutamente ampia: il test deve intercettare un degrado strutturale, non le oscillazioni di misura di una macchina carica.
	 *  NOTA: l'unico servizio per cui SunJCE puo' risultare piu' lento di BouncyCastle e' 'Cipher AES/GCM'. Dal jdk 18 la relativa
	 *  implementazione e' stata riscritta e il percorso ottimizzato richiede il supporto AVX-512: su cpu che non lo espongono
	 *  il throughput risulta sensibilmente inferiore sia a quello del jdk 11 sia a quello di BouncyCastle. Si tratta di una
	 *  caratteristica del jdk, non del posizionamento del provider. */
	private static final double SOGLIA_DEGRADO = 3.0d;

	private static final int DIMENSIONE_PAYLOAD = 1024 * 1024;
	private static final int ITERAZIONI_WARMUP = 5;
	private static final int ITERAZIONI_MISURA = 10;
	private static final int RIPETIZIONI = 3;

	private static final String PROVIDER_BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
	private static final String PROVIDER_SUN = "SUN";
	private static final String PROVIDER_SUN_RSA_SIGN = "SunRsaSign";
	private static final String PROVIDER_SUN_EC = "SunEC";

	public static void main(String[] args) throws Exception {

		testServiziCondivisi();

		testBenchmarkServiziCondivisi();

		System.out.println("Testsuite terminata");

	}


	// ===== Verifica dei servizi condivisi fra BouncyCastle e i provider del jdk =====

	public static void testServiziCondivisi() throws UtilsException {

		System.out.println("========================= Servizi condivisi con i provider del jdk ==============================");

		initBouncyCastle();
		try {

			List<Provider> providers = Utilities.getProviders();

			int posizioneSunJCE = -1;
			int posizioneBouncyCastle = -1;
			for (int i = 0; i < providers.size(); i++) {
				if(ProviderUtils.PROVIDER_SUN_JCE.equals(providers.get(i).getName())) {
					posizioneSunJCE = i;
				}
				else if(PROVIDER_BC.equals(providers.get(i).getName())) {
					posizioneBouncyCastle = i;
				}
			}

			System.out.println("Provider registrati: "+ProviderUtils.getProviderNames());

			if(posizioneSunJCE<0) {
				throw new UtilsException("Provider '"+ProviderUtils.PROVIDER_SUN_JCE+"' non registrato");
			}
			if(posizioneBouncyCastle<0) {
				throw new UtilsException("Provider '"+PROVIDER_BC+"' non registrato");
			}
			if(posizioneBouncyCastle!=(posizioneSunJCE+1)) {
				throw new UtilsException("Atteso il provider '"+PROVIDER_BC+"' immediatamente dopo '"+ProviderUtils.PROVIDER_SUN_JCE
						+"' (posizione "+(posizioneSunJCE+2)+"), rilevato invece alla posizione "+(posizioneBouncyCastle+1));
			}

			// servizi forniti dai provider che precedono BouncyCastle e da quelli che lo seguono
			Set<String> serviziPrecedenti = new TreeSet<>();
			Set<String> serviziSuccessivi = new TreeSet<>();
			List<String> nomiSuccessivi = new ArrayList<>();
			for (int i = 0; i < providers.size(); i++) {
				if(providers.get(i).getName().startsWith(PREFISSO_PROVIDER_PKCS11)) {
					continue;
				}
				if(i<posizioneBouncyCastle) {
					serviziPrecedenti.addAll(getServizi(providers.get(i)));
				}
				else if(i>posizioneBouncyCastle) {
					serviziSuccessivi.addAll(getServizi(providers.get(i)));
					nomiSuccessivi.add(providers.get(i).getName());
				}
			}

			Set<String> serviziBouncyCastle = getServizi(providers.get(posizioneBouncyCastle));

			// servizi che, per effetto del posizionamento, sono ora serviti da un provider del jdk
			Set<String> cedutiAlJdk = new TreeSet<>(serviziBouncyCastle);
			cedutiAlJdk.retainAll(serviziPrecedenti);

			// servizi che restano appannaggio esclusivo di BouncyCastle
			Set<String> esclusiviBouncyCastle = new TreeSet<>(serviziBouncyCastle);
			esclusiviBouncyCastle.removeAll(serviziPrecedenti);
			esclusiviBouncyCastle.removeAll(serviziSuccessivi);

			// servizi per i quali BouncyCastle scavalca un provider registrato dopo 'SunJCE'
			Set<String> scavalcati = new TreeSet<>(serviziBouncyCastle);
			scavalcati.retainAll(serviziSuccessivi);
			scavalcati.removeAll(serviziPrecedenti);

			System.out.println("Provider successivi a '"+PROVIDER_BC+"': "+nomiSuccessivi);
			System.out.println("Servizi dichiarati da '"+PROVIDER_BC+"': "+serviziBouncyCastle.size());
			System.out.println("   serviti da un provider del jdk       : "+cedutiAlJdk.size());
			System.out.println("   forniti esclusivamente da '"+PROVIDER_BC+"': "+esclusiviBouncyCastle.size());
			System.out.println("   che scavalcano un provider successivo: "+scavalcati.size()+" "+scavalcati);

			Map<String,Integer> perTipo = new TreeMap<>();
			for (String servizio : cedutiAlJdk) {
				perTipo.merge(servizio.substring(0, servizio.indexOf('.')), 1, Integer::sum);
			}
			System.out.println("Ripartizione per tipo dei servizi serviti da un provider del jdk: "+perTipo);

			if(!SERVIZI_ATTESI_SCAVALCATI.containsAll(scavalcati)) {
				Set<String> inattesi = new TreeSet<>(scavalcati);
				inattesi.removeAll(SERVIZI_ATTESI_SCAVALCATI);
				throw new UtilsException("Il provider '"+PROVIDER_BC+"' scavalca provider del jdk registrati dopo '"
						+ProviderUtils.PROVIDER_SUN_JCE+"' per servizi non previsti: "+inattesi
						+"; verificare se il posizionamento del provider vada rivisto");
			}

		}finally {
			releaseBouncyCastle();
		}

	}


	// ===== Benchmark dei servizi condivisi =====

	public static void testBenchmarkServiziCondivisi() throws UtilsException {

		System.out.println("========================= Benchmark dei servizi condivisi ==============================");

		initBouncyCastle();
		try {

			final byte [] payload = new byte[DIMENSIONE_PAYLOAD];
			final byte [] payloadPiccolo = new byte[256];
			SecureRandom secureRandom = new SecureRandom();
			secureRandom.nextBytes(payload);
			secureRandom.nextBytes(payloadPiccolo);

			final KeyPair keyPairRsa = generaKeyPairRsa();
			final KeyPair keyPairEc = generaKeyPairEc();
			final SecretKey secretKeyAes = generaSecretKeyAes();
			final byte [] firmaRsa = firmaRsa(keyPairRsa, payloadPiccolo);
			final byte [] iv12 = new byte[12];
			final byte [] iv16 = new byte[16];

			System.out.println(String.format("%-38s %14s %14s   %s", "servizio", "jdk", PROVIDER_BC, "esito"));

			confronta("MessageDigest SHA-256 (1MB)", PROVIDER_SUN,
					() -> MessageDigest.getInstance("SHA-256", PROVIDER_SUN).digest(payload),
					() -> MessageDigest.getInstance("SHA-256", PROVIDER_BC).digest(payload));

			confronta("Mac HmacSHA256 (1MB)", ProviderUtils.PROVIDER_SUN_JCE,
					() -> eseguiMac(ProviderUtils.PROVIDER_SUN_JCE, payloadPiccolo, payload),
					() -> eseguiMac(PROVIDER_BC, payloadPiccolo, payload));

			confronta("Cipher AES-256/GCM (1MB)", ProviderUtils.PROVIDER_SUN_JCE,
					() -> eseguiCipherGcm(ProviderUtils.PROVIDER_SUN_JCE, secretKeyAes, iv12, payload),
					() -> eseguiCipherGcm(PROVIDER_BC, secretKeyAes, iv12, payload));

			confronta("Cipher AES-256/CBC (1MB)", ProviderUtils.PROVIDER_SUN_JCE,
					() -> eseguiCipherCbc(ProviderUtils.PROVIDER_SUN_JCE, secretKeyAes, iv16, payload),
					() -> eseguiCipherCbc(PROVIDER_BC, secretKeyAes, iv16, payload));

			confronta("Signature SHA256withRSA (firma)", PROVIDER_SUN_RSA_SIGN,
					() -> eseguiFirmaRsa(PROVIDER_SUN_RSA_SIGN, keyPairRsa, payloadPiccolo),
					() -> eseguiFirmaRsa(PROVIDER_BC, keyPairRsa, payloadPiccolo));

			confronta("Signature SHA256withRSA (verifica)", PROVIDER_SUN_RSA_SIGN,
					() -> eseguiVerificaRsa(PROVIDER_SUN_RSA_SIGN, keyPairRsa, payloadPiccolo, firmaRsa),
					() -> eseguiVerificaRsa(PROVIDER_BC, keyPairRsa, payloadPiccolo, firmaRsa));

			confronta("Signature SHA256withECDSA (firma)", PROVIDER_SUN_EC,
					() -> eseguiFirmaEc(PROVIDER_SUN_EC, keyPairEc, payloadPiccolo),
					() -> eseguiFirmaEc(PROVIDER_BC, keyPairEc, payloadPiccolo));

			confronta("SecretKeyFactory PBKDF2 (10k iter)", ProviderUtils.PROVIDER_SUN_JCE,
					() -> eseguiPbkdf2(ProviderUtils.PROVIDER_SUN_JCE),
					() -> eseguiPbkdf2(PROVIDER_BC));

		}finally {
			releaseBouncyCastle();
		}

	}

	private static void confronta(String servizio, String providerJdk, Operazione operazioneJdk, Operazione operazioneBouncyCastle) throws UtilsException {

		double tempoJdk = misura(operazioneJdk);
		double tempoBouncyCastle = misura(operazioneBouncyCastle);

		double rapporto = tempoJdk / tempoBouncyCastle;
		String esito = rapporto>1
				? String.format("'%s' piu' lento di %.1fx", providerJdk, rapporto)
				: String.format("'%s' piu' veloce di %.1fx", providerJdk, 1/rapporto);

		System.out.println(String.format("%-38s %11.3f ms %11.3f ms   %s", servizio, tempoJdk, tempoBouncyCastle, esito));

		if(rapporto>SOGLIA_DEGRADO) {
			throw new UtilsException("Servizio '"+servizio+"': il provider '"+providerJdk+"' risulta piu' lento di "
					+String.format("%.1f", rapporto)+"x rispetto a '"+PROVIDER_BC+"' (soglia "+SOGLIA_DEGRADO+"x);"
					+" verificare se il posizionamento del provider vada rivisto");
		}

	}

	private static double misura(Operazione operazione) throws UtilsException {
		try {
			for (int i = 0; i < ITERAZIONI_WARMUP; i++) {
				operazione.esegui();
			}
			long migliore = Long.MAX_VALUE;
			for (int r = 0; r < RIPETIZIONI; r++) {
				long inizio = System.nanoTime();
				for (int i = 0; i < ITERAZIONI_MISURA; i++) {
					operazione.esegui();
				}
				long trascorso = System.nanoTime() - inizio;
				if(trascorso<migliore) {
					migliore = trascorso;
				}
			}
			return migliore / (double)ITERAZIONI_MISURA / 1000000d;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


	// ===== Operazioni misurate =====

	@FunctionalInterface
	private interface Operazione {
		void esegui() throws Exception;
	}

	private static void eseguiMac(String provider, byte [] chiave, byte [] payload) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256", provider);
		mac.init(new SecretKeySpec(chiave, "HmacSHA256"));
		mac.doFinal(payload);
	}
	private static void eseguiCipherGcm(String provider, SecretKey key, byte [] iv, byte [] payload) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", provider);
		cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(128, iv));
		cipher.doFinal(payload);
	}
	private static void eseguiCipherCbc(String provider, SecretKey key, byte [] iv, byte [] payload) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", provider);
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		cipher.doFinal(payload);
	}
	private static void eseguiFirmaRsa(String provider, KeyPair keyPair, byte [] payload) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA", provider);
		signature.initSign(keyPair.getPrivate());
		signature.update(payload);
		signature.sign();
	}
	private static void eseguiVerificaRsa(String provider, KeyPair keyPair, byte [] payload, byte [] firma) throws Exception {
		Signature signature = Signature.getInstance("SHA256withRSA", provider);
		signature.initVerify(keyPair.getPublic());
		signature.update(payload);
		if(!signature.verify(firma)) {
			throw new UtilsException("Verifica della firma non riuscita tramite il provider '"+provider+"'");
		}
	}
	private static void eseguiFirmaEc(String provider, KeyPair keyPair, byte [] payload) throws Exception {
		Signature signature = Signature.getInstance("SHA256withECDSA", provider);
		signature.initSign(keyPair.getPrivate());
		signature.update(payload);
		signature.sign();
	}
	private static void eseguiPbkdf2(String provider) throws Exception {
		SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256", provider)
			.generateSecret(new PBEKeySpec("123456".toCharArray(), new byte[16], 10000, 256));
	}


	// ===== Utilita' =====

	/** Servizi forniti da un provider, nella forma 'tipo.algoritmo'.
	 *  Vengono raccolti sia i nomi canonici sia gli alias: la risoluzione JCA considera entrambi, e diversi provider
	 *  registrano il medesimo algoritmo con nomi canonici differenti (ad esempio SunJCE fornisce 'DiffieHellman' con
	 *  alias 'DH', mentre BouncyCastle utilizza 'DH' come nome canonico). Ignorare gli alias porterebbe a considerare
	 *  come non condivisi servizi che invece lo sono. */
	private static Set<String> getServizi(Provider provider){
		Set<String> servizi = new TreeSet<>();
		for (Provider.Service service : provider.getServices()) {
			servizi.add(service.getType()+"."+service.getAlgorithm().toUpperCase());
		}
		for (String proprieta : provider.stringPropertyNames()) {
			if(proprieta.startsWith(PREFISSO_ALIAS)) {
				String tipoEdAlias = proprieta.substring(PREFISSO_ALIAS.length());
				int separatore = tipoEdAlias.indexOf('.');
				if(separatore>0) {
					servizi.add(tipoEdAlias.substring(0, separatore)+"."+tipoEdAlias.substring(separatore+1).toUpperCase());
				}
			}
		}
		return servizi;
	}

	private static KeyPair generaKeyPairRsa() throws UtilsException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			return keyPairGenerator.generateKeyPair();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static KeyPair generaKeyPairEc() throws UtilsException {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
			keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
			return keyPairGenerator.generateKeyPair();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static SecretKey generaSecretKeyAes() throws UtilsException {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			return keyGenerator.generateKey();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}
	private static byte[] firmaRsa(KeyPair keyPair, byte [] payload) throws UtilsException {
		try {
			Signature signature = Signature.getInstance("SHA256withRSA", PROVIDER_SUN_RSA_SIGN);
			signature.initSign(keyPair.getPrivate());
			signature.update(payload);
			return signature.sign();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private static void initBouncyCastle() {
		// registra il provider nella medesima modalita' utilizzata a runtime da GovWay
		Utilities.addBouncyCastleAfterSun(true);
	}
	private static void releaseBouncyCastle() {
		Security.removeProvider(PROVIDER_BC);
	}

}
