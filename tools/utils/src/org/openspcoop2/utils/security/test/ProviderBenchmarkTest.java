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

import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.MGF1ParameterSpec;
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
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.openspcoop2.utils.BouncyCastleUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.security.ProviderUtils;

/**
 * ProviderBenchmarkTest
 *
 * Presidia i vincoli che determinano la posizione del provider BouncyCastle nella lista dei provider JCE, registrata da
 * 'BouncyCastleUtilities.addBouncyCastleAfterSun' e utilizzata a runtime da tutti i processi GovWay, e ne misura il costo.
 *
 * Il provider viene registrato alla posizione 2, davanti a tutti i provider del jdk tranne 'SUN', e privato dei tre alias
 * che impedirebbero al jdk di leggere i keystore PKCS12. Le due cose sono verificate rispettivamente da
 * 'testParametriOaep' e da 'testServiziCondivisi'.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProviderBenchmarkTest {

	private ProviderBenchmarkTest(){}

	private static final String PROVIDER_BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
	private static final String PROVIDER_SUN = "SUN";
	private static final String PROVIDER_SUN_RSA_SIGN = "SunRsaSign";
	private static final String PROVIDER_SUN_EC = "SunEC";

	/** Trasformazione su cui BouncyCastle e SunJCE non sono interoperabili: SunJCE utilizza MGF1 su SHA-1, che e' il default
	 *  di 'OAEPParameterSpec', mentre BouncyCastle utilizza MGF1 sul digest indicato nel nome. Da essa dipendono il formato
	 *  dei segreti cifrati con BYOK e la conformita' a RFC 7518 dei JWE prodotti tramite 'cxf-rt-rs-security-jose'. */
	private static final String TRASFORMAZIONE_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	private static final String DIGEST_OAEP = "SHA-256";
	private static final String MGF_OAEP = "MGF1";

	/** Servizi che devono restare forniti da BouncyCastle: sono quelli su cui si e' verificata una divergenza di formato
	 *  oppure quelli usati come sentinella per accorgersi di uno spostamento involontario del provider.
	 *  NOTA: non vi rientrano i 'MessageDigest', forniti dal provider 'SUN' che precede BouncyCastle e che quindi li serve
	 *  comunque; a BouncyCastle arrivano solo se richiesto esplicitamente, come fa 'MessageDigestFactory' quando abilitato. */
	private static final String [] SERVIZI_ATTESI_BOUNCY_CASTLE = new String[] {
		"Cipher."+TRASFORMAZIONE_OAEP,
		"Cipher.AES/GCM/NoPadding",
		"Signature.SHA256withRSA",
		"SecretKeyFactory.PBKDF2WithHmacSHA256"
	};

	/** Prefisso dei provider PKCS11, esclusi dal censimento: non fanno parte dell'installazione statica del jdk, vengono
	 *  registrati a runtime da 'HSMManager.providerInit' per ogni keystore hardware configurato, quindi la loro presenza
	 *  dipende dalla configurazione e, nella testsuite, dall'ordine di esecuzione dei test. */
	private static final String PREFISSO_PROVIDER_PKCS11 = "SunPKCS11";
	private static final String PREFISSO_ALIAS = "Alg.Alias.";

	private static final int DIMENSIONE_PAYLOAD = 1024 * 1024;
	/** NOTA: l'intrinsic HotSpot di AES-GCM entra in gioco solo dopo alcune centinaia di invocazioni. Con un warmup ridotto
	 *  la misura risulta falsata di oltre un ordine di grandezza e SunJCE appare piu' lento di BouncyCastle. */
	private static final int ITERAZIONI_WARMUP = 800;
	private static final int ITERAZIONI_MISURA = 50;
	private static final int RIPETIZIONI = 3;

	/* NOTA: i metodi di benchmark non asseriscono soglie temporali, ma verificano unicamente che le operazioni si concludano
	 * correttamente e ne riportano la misura. I rapporti fra i due provider dipendono in modo determinante dalla versione del
	 * jdk e dalla cpu: ad esempio la firma ECDSA e' circa 8 volte piu' lenta con 'SunEC' sul jdk 11, mentre sul jdk 21, dove
	 * l'implementazione e' stata riscritta, e' piu' veloce. Un'asserzione sui tempi sarebbe quindi instabile. */

	private static final String KEYSTORE_TLS = "govway_test.p12";
	private static final String KEYSTORE_TLS_PASSWORD = "123456";
	private static final String CIPHER_SUITE_TLS = "TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384";
	private static final int MEGABYTE_TRASFERITI_TLS = 100;


	public static void main(String[] args) throws Exception {

		testServiziCondivisi();

		testParametriOaep();

		testInteroperabilitaOaep();

		testBenchmarkServiziCondivisi();

		testBenchmarkDimensioniMessaggio();

		testBenchmarkTls();

		System.out.println("Testsuite terminata");

	}


	// ===== Servizi condivisi fra BouncyCastle e i provider del jdk =====

	public static void testServiziCondivisi() throws UtilsException {

		System.out.println("========================= Servizi condivisi con i provider del jdk ==============================");

		initBouncyCastle();
		try {

			List<Provider> providers = ProviderUtils.getProviders();

			int posizioneBouncyCastle = -1;
			for (int i = 0; i < providers.size(); i++) {
				if(PROVIDER_BC.equals(providers.get(i).getName())) {
					posizioneBouncyCastle = i;
				}
			}

			System.out.println("Provider registrati: "+ProviderUtils.getProviderNames());

			if(posizioneBouncyCastle!=1) {
				throw new UtilsException("Atteso il provider '"+PROVIDER_BC+"' alla posizione 2, subito dopo '"+PROVIDER_SUN
						+"', rilevato invece alla posizione "+(posizioneBouncyCastle+1));
			}

			// censimento: quanti servizi sono forniti anche dal jdk e quanti solamente da BouncyCastle
			Set<String> serviziJdk = new TreeSet<>();
			for (Provider provider : providers) {
				if(!PROVIDER_BC.equals(provider.getName()) && !provider.getName().startsWith(PREFISSO_PROVIDER_PKCS11)) {
					serviziJdk.addAll(getServizi(provider));
				}
			}
			Set<String> serviziBouncyCastle = getServizi(providers.get(posizioneBouncyCastle));

			Set<String> condivisi = new TreeSet<>(serviziBouncyCastle);
			condivisi.retainAll(serviziJdk);
			Set<String> esclusivi = new TreeSet<>(serviziBouncyCastle);
			esclusivi.removeAll(serviziJdk);

			System.out.println("Servizi dichiarati da '"+PROVIDER_BC+"': "+serviziBouncyCastle.size()
					+"; condivisi con i provider del jdk: "+condivisi.size()
					+"; forniti esclusivamente da '"+PROVIDER_BC+"': "+esclusivi.size());

			Map<String,Integer> perTipo = new TreeMap<>();
			for (String servizio : condivisi) {
				perTipo.merge(servizio.substring(0, servizio.indexOf('.')), 1, Integer::sum);
			}
			System.out.println("Ripartizione per tipo dei servizi condivisi: "+perTipo);

			// gli alias rimossi non devono piu' essere serviti da BouncyCastle: e' cio' che consente al jdk di leggere i keystore PKCS12
			checkProvider("SecretKeyFactory", "PBE", false);
			checkProvider("Cipher", "PBEWithSHA1AndDESede", false);
			checkProvider("SecretKeyFactory", "PBEWithSHA1AndDESede", false);
			System.out.println("Alias rimossi dall'istanza del provider: "+BouncyCastleUtilities.getBouncyCastleAliasRimossiPkcs12());

			// tutti gli altri servizi devono continuare ad essere forniti da BouncyCastle
			for (String servizio : SERVIZI_ATTESI_BOUNCY_CASTLE) {
				int separatore = servizio.indexOf('.');
				checkProvider(servizio.substring(0, separatore), servizio.substring(separatore+1), true);
			}

		}finally {
			releaseBouncyCastle();
		}

	}

	private static void checkProvider(String tipo, String algoritmo, boolean attesoBouncyCastle) throws UtilsException {
		String provider = getProvider(tipo, algoritmo);
		boolean bouncyCastle = PROVIDER_BC.equals(provider);
		System.out.println("   "+tipo+"."+algoritmo+" -> "+provider);
		if(bouncyCastle!=attesoBouncyCastle) {
			throw new UtilsException("Servizio '"+tipo+"."+algoritmo+"': atteso "+(attesoBouncyCastle?"":"un provider differente da ")
					+"'"+PROVIDER_BC+"', rilevato invece il provider '"+provider+"'");
		}
	}

	private static String getProvider(String tipo, String algoritmo) throws UtilsException {
		try {
			switch (tipo) {
				case "Cipher": return Cipher.getInstance(algoritmo).getProvider().getName();
				case "SecretKeyFactory": return SecretKeyFactory.getInstance(algoritmo).getProvider().getName();
				case "Signature": return Signature.getInstance(algoritmo).getProvider().getName();
				case "MessageDigest": return MessageDigest.getInstance(algoritmo).getProvider().getName();
				default: throw new UtilsException("Tipo di servizio '"+tipo+"' non gestito");
			}
		}catch(UtilsException e) {
			throw e;
		}catch(Exception e) {
			throw new UtilsException("Risoluzione del servizio '"+tipo+"."+algoritmo+"' non riuscita: "+e.getMessage(),e);
		}
	}


	// ===== Parametri OAEP =====

	public static void testParametriOaep() throws UtilsException {

		System.out.println("========================= Parametri OAEP ==============================");

		initBouncyCastle();
		try {

			// la risoluzione generica della trasformazione deve produrre MGF1 sul medesimo digest indicato nel nome:
			// e' l'invariante da cui dipendono il formato dei segreti BYOK gia' scritti e la conformita' a RFC 7518 dei JWE
			Cipher cipher = Cipher.getInstance(TRASFORMAZIONE_OAEP);
			KeyPair keyPair = generaKeyPairRsa();
			try {
				cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}

			OAEPParameterSpec spec = getOaepParameterSpec(cipher);
			String digest = spec.getDigestAlgorithm();
			String mgfDigest = ((MGF1ParameterSpec)spec.getMGFParameters()).getDigestAlgorithm();

			System.out.println("Trasformazione '"+TRASFORMAZIONE_OAEP+"' risolta dal provider '"+cipher.getProvider().getName()
					+"'; digest="+digest+", MGF1="+mgfDigest);

			if(!DIGEST_OAEP.equals(digest) || !DIGEST_OAEP.equals(mgfDigest)) {
				throw new UtilsException("Trasformazione '"+TRASFORMAZIONE_OAEP+"' risolta dal provider '"+cipher.getProvider().getName()
						+"' con digest '"+digest+"' e MGF1 '"+mgfDigest+"'; attesi entrambi '"+DIGEST_OAEP
						+"'. Con MGF1 su un digest differente diventano illeggibili i segreti gia' cifrati con BYOK ed i contenuti"
						+" JOSE prodotti non sono conformi a RFC 7518: verificare la posizione del provider '"+PROVIDER_BC+"'");
			}

			// round trip con la sola risoluzione generica
			byte [] dati = "GovWay OAEP round trip".getBytes();
			try {
				byte [] cifrato = cipher.doFinal(dati);
				Cipher decipher = Cipher.getInstance(TRASFORMAZIONE_OAEP);
				decipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
				if(!java.util.Arrays.equals(dati, decipher.doFinal(cifrato))) {
					throw new UtilsException("Round trip OAEP non riuscito");
				}
			}catch(UtilsException e) {
				throw e;
			}catch(Exception e) {
				throw new UtilsException("Round trip OAEP non riuscito: "+e.getMessage(),e);
			}
			System.out.println("Round trip con la risoluzione generica: ok");

		}catch(UtilsException e) {
			throw e;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}finally {
			releaseBouncyCastle();
		}

	}

	public static void testInteroperabilitaOaep() throws UtilsException {

		System.out.println("========================= Interoperabilita' OAEP fra i provider ==============================");

		initBouncyCastle();
		try {

			KeyPair keyPair = generaKeyPairRsa();
			byte [] dati = "GovWay OAEP interop".getBytes();

			String providerJdk = ProviderUtils.PROVIDER_SUN_JCE;

			byte [] cifratoBouncyCastle = cifraOaep(PROVIDER_BC, keyPair, dati, null);
			byte [] cifratoJdk = cifraOaep(providerJdk, keyPair, dati, null);

			// senza parametri espliciti i due provider non si intendono: e' la ragione per cui la posizione del provider e' portante
			boolean jdkLeggeBouncyCastle = decifraOaep(providerJdk, keyPair, cifratoBouncyCastle, null, dati);
			boolean bouncyCastleLeggeJdk = decifraOaep(PROVIDER_BC, keyPair, cifratoJdk, null, dati);
			System.out.println("Senza parametri espliciti: '"+providerJdk+"' legge '"+PROVIDER_BC+"'="+jdkLeggeBouncyCastle
					+", '"+PROVIDER_BC+"' legge '"+providerJdk+"'="+bouncyCastleLeggeJdk);
			if(jdkLeggeBouncyCastle || bouncyCastleLeggeJdk) {
				throw new UtilsException("Attesa incompatibilita' fra '"+providerJdk+"' e '"+PROVIDER_BC+"' sulla trasformazione '"
						+TRASFORMAZIONE_OAEP+"' in assenza di parametri espliciti; se la situazione e' cambiata va rivalutato il"
						+" vincolo sulla posizione del provider");
			}

			// indicando i parametri, il provider del jdk riproduce esattamente la semantica di BouncyCastle
			OAEPParameterSpec spec = new OAEPParameterSpec(DIGEST_OAEP, MGF_OAEP, MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
			boolean jdkConParametri = decifraOaep(providerJdk, keyPair, cifratoBouncyCastle, spec, dati);
			System.out.println("Con OAEPParameterSpec esplicito (MGF1 su "+DIGEST_OAEP+"): '"+providerJdk+"' legge '"+PROVIDER_BC+"'="+jdkConParametri);
			if(!jdkConParametri) {
				throw new UtilsException("Il provider '"+providerJdk+"', con parametri OAEP espliciti, deve riprodurre la semantica di '"+PROVIDER_BC+"'");
			}

		}finally {
			releaseBouncyCastle();
		}

	}

	private static OAEPParameterSpec getOaepParameterSpec(Cipher cipher) throws UtilsException {
		try {
			if(cipher.getParameters()==null) {
				throw new UtilsException("Il provider '"+cipher.getProvider().getName()+"' non espone i parametri utilizzati per la trasformazione '"+TRASFORMAZIONE_OAEP+"'");
			}
			return cipher.getParameters().getParameterSpec(OAEPParameterSpec.class);
		}catch(UtilsException e) {
			throw e;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private static byte[] cifraOaep(String provider, KeyPair keyPair, byte [] dati, OAEPParameterSpec spec) throws UtilsException {
		try {
			Cipher cipher = Cipher.getInstance(TRASFORMAZIONE_OAEP, provider);
			if(spec!=null) {
				cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic(), spec);
			}
			else {
				cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
			}
			return cipher.doFinal(dati);
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private static boolean decifraOaep(String provider, KeyPair keyPair, byte [] cifrato, OAEPParameterSpec spec, byte [] atteso) {
		try {
			Cipher cipher = Cipher.getInstance(TRASFORMAZIONE_OAEP, provider);
			if(spec!=null) {
				cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate(), spec);
			}
			else {
				cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			}
			return java.util.Arrays.equals(atteso, cipher.doFinal(cifrato));
		}catch(Exception e) {
			return false;
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

	/** Le dimensioni tipiche dei messaggi trattati dal gateway sono nell'ordine delle decine di KB; il rapporto fra i due
	 *  provider si mantiene costante gia' a partire da 10 KB, mentre cambia sensibilmente il costo assoluto per operazione. */
	public static void testBenchmarkDimensioniMessaggio() throws UtilsException {

		System.out.println("========================= Benchmark al variare della dimensione del messaggio ==============================");

		initBouncyCastle();
		try {

			final byte [] chiave = new byte[32];
			final SecretKey secretKeyAes = generaSecretKeyAes();
			final byte [] iv12 = new byte[12];

			int [] dimensioni = new int[] {10*1024, 50*1024, 500*1024};
			String [] etichette = new String[] {"10 KB", "50 KB", "500 KB"};

			System.out.println(String.format("%-16s %-12s %14s %14s   %s", "servizio", "payload", "jdk", PROVIDER_BC, "esito"));
			for (int i = 0; i < dimensioni.length; i++) {
				final byte [] payload = new byte[dimensioni[i]];
				new SecureRandom().nextBytes(payload);
				confronta(String.format("%-16s %-12s", "SHA-256", etichette[i]), PROVIDER_SUN,
						() -> MessageDigest.getInstance("SHA-256", PROVIDER_SUN).digest(payload),
						() -> MessageDigest.getInstance("SHA-256", PROVIDER_BC).digest(payload));
				confronta(String.format("%-16s %-12s", "HmacSHA256", etichette[i]), ProviderUtils.PROVIDER_SUN_JCE,
						() -> eseguiMac(ProviderUtils.PROVIDER_SUN_JCE, chiave, payload),
						() -> eseguiMac(PROVIDER_BC, chiave, payload));
				confronta(String.format("%-16s %-12s", "AES-256/GCM", etichette[i]), ProviderUtils.PROVIDER_SUN_JCE,
						() -> eseguiCipherGcm(ProviderUtils.PROVIDER_SUN_JCE, secretKeyAes, iv12, payload),
						() -> eseguiCipherGcm(PROVIDER_BC, secretKeyAes, iv12, payload));
			}

		}finally {
			releaseBouncyCastle();
		}

	}

	/** La classe 'sun.security.ssl.SSLCipher' del jdk risolve il cipher di record in modo generico: la posizione del provider
	 *  determina quindi anche il throughput di ogni connessione TLS gestita dal gateway. */
	public static void testBenchmarkTls() throws UtilsException {

		System.out.println("========================= Benchmark TLS ==============================");

		initBouncyCastle();
		try {

			System.out.println("Cipher 'AES/GCM/NoPadding' risolto dal provider '"+getProvider("Cipher", "AES/GCM/NoPadding")+"'");

			SSLContext sslContext = getSSLContext();

			try(SSLServerSocket serverSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(0, 1, java.net.InetAddress.getLoopbackAddress())){

				serverSocket.setEnabledCipherSuites(new String[] {CIPHER_SUITE_TLS});

				final byte [] blocco = new byte[64*1024];
				final int blocchi = MEGABYTE_TRASFERITI_TLS * 16;
				Thread server = new Thread(() -> {
					try(SSLSocket socket = (SSLSocket) serverSocket.accept();
						OutputStream out = socket.getOutputStream()){
						for (int i = 0; i < blocchi; i++) {
							out.write(blocco);
						}
						out.flush();
					}catch(Exception e) {
						System.out.println("Errore nel server TLS di prova: "+e.getMessage());
					}
				});
				server.setDaemon(true);
				server.start();

				long letti = 0;
				double secondi;
				String cipherSuite;
				try(SSLSocket socket = (SSLSocket) sslContext.getSocketFactory().createSocket(serverSocket.getInetAddress(), serverSocket.getLocalPort())){
					socket.setEnabledCipherSuites(new String[] {CIPHER_SUITE_TLS});
					socket.startHandshake();
					cipherSuite = socket.getSession().getCipherSuite();
					byte [] buffer = new byte[64*1024];
					InputStream in = socket.getInputStream();
					long inizio = System.nanoTime();
					int n;
					while ( (n=in.read(buffer)) > 0 ) {
						letti += n;
					}
					secondi = (System.nanoTime()-inizio) / 1000000000d;
				}
				server.join();

				if(!CIPHER_SUITE_TLS.equals(cipherSuite)) {
					throw new UtilsException("Attesa la cipher suite '"+CIPHER_SUITE_TLS+"', negoziata invece '"+cipherSuite+"'");
				}
				long attesi = (long)blocchi * blocco.length;
				if(letti!=attesi) {
					throw new UtilsException("Attesi "+attesi+" byte, ricevuti "+letti);
				}
				System.out.println(String.format("Cipher suite '%s': trasferiti %d MB in %.2f s -> %.0f MB/s",
						cipherSuite, MEGABYTE_TRASFERITI_TLS, secondi, letti/1048576d/secondi));

			}catch(UtilsException e) {
				throw e;
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}

		}finally {
			releaseBouncyCastle();
		}

	}

	private static SSLContext getSSLContext() throws UtilsException {
		try {
			java.security.KeyStore keystore = java.security.KeyStore.getInstance("PKCS12");
			try(InputStream is = org.openspcoop2.utils.certificate.test.KeystoreTest.class.getResourceAsStream(
					org.openspcoop2.utils.certificate.test.KeystoreTest.PREFIX+KEYSTORE_TLS)){
				keystore.load(is, KEYSTORE_TLS_PASSWORD.toCharArray());
			}
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
			keyManagerFactory.init(keystore, KEYSTORE_TLS_PASSWORD.toCharArray());
			TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
			trustManagerFactory.init(keystore);
			SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
			sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
			return sslContext;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}


	// ===== Misura =====

	private static void confronta(String servizio, String providerJdk, Operazione operazioneJdk, Operazione operazioneBouncyCastle) throws UtilsException {

		double tempoJdk = misura(operazioneJdk);
		double tempoBouncyCastle = misura(operazioneBouncyCastle);

		double rapporto = tempoJdk / tempoBouncyCastle;
		String esito = rapporto>1
				? String.format("'%s' piu' lento di %.1fx", providerJdk, rapporto)
				: String.format("'%s' piu' veloce di %.1fx", providerJdk, 1/rapporto);

		System.out.println(String.format("%-38s %11.3f ms %11.3f ms   %s", servizio, tempoJdk, tempoBouncyCastle, esito));

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

	@FunctionalInterface
	private interface Operazione {
		void esegui() throws Exception;
	}


	// ===== Operazioni misurate =====

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
	 *  alias 'DH', mentre BouncyCastle utilizza 'DH' come nome canonico). */
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
		BouncyCastleUtilities.addBouncyCastleAfterSun(true);
	}
	private static void releaseBouncyCastle() {
		Security.removeProvider(PROVIDER_BC);
	}

}
