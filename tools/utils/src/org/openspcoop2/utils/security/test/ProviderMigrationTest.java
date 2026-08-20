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
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.openspcoop2.utils.BouncyCastleUtilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.security.ProviderUtils;
import org.openspcoop2.utils.certificate.test.KeystoreTest;
import org.openspcoop2.utils.random.RandomUtilities;
import org.openspcoop2.utils.security.DecryptWrapKey;
import org.openspcoop2.utils.security.EncryptWrapKey;
import org.openspcoop2.utils.security.JOSESerialization;
import org.openspcoop2.utils.security.JWEOptions;
import org.openspcoop2.utils.security.JWTOptions;
import org.openspcoop2.utils.security.JsonDecrypt;
import org.openspcoop2.utils.security.JsonEncrypt;

/**
 * ProviderMigrationTest
 *
 * Verifica che uno spostamento del provider BouncyCastle nella lista dei provider JCE non alteri i dati prodotti.
 *
 * Il provider viene attualmente registrato alla posizione 2, quindi davanti a tutti i provider del jdk tranne 'SUN'.
 * Spostarlo dopo 'SunJCE' consentirebbe al jdk di servire gli algoritmi che implementa in modo accelerato, ma cambierebbe
 * il provider che risolve 310 servizi: per ognuno di essi il formato dei dati prodotti deve restare identico, altrimenti
 * i contenuti gia' scritti diventerebbero illeggibili.
 *
 * I test non utilizzano alcun valore precalcolato: chiavi, payload ed alea vengono rigenerati ad ogni esecuzione, e la
 * migrazione viene simulata dentro la medesima jvm registrando il provider nelle due posizioni e incrociando produttore
 * e consumatore. Per gli algoritmi deterministici il confronto avviene sui byte prodotti, per quelli randomizzati -
 * dove due esecuzioni non producono mai lo stesso risultato - avviene per consumo incrociato nei due versi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ProviderMigrationTest {

	private ProviderMigrationTest(){}

	private static final String PROVIDER_BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;
	private static final String PROVIDER_SUN = "SUN";
	private static final String PROVIDER_SUN_JCE = "SunJCE";

	private static final String KEYSTORE = "govway_test.p12";
	private static final String KEYSTORE_TIPO = "PKCS12";
	private static final String ALIAS = "govway_test";
	private static final String PASSWORD = "123456";

	private static final String ALGO_KEY_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
	private static final String ALGO_CONTENT_CBC = "AES/CBC/PKCS5Padding";
	private static final String JWE_KEY_ALGORITHM = "RSA-OAEP-256";
	private static final String JWE_CONTENT_ALGORITHM = "A256GCM";

	/** Servizi noti come non interoperabili fra BouncyCastle ed i provider del jdk.
	 *
	 *  'Cipher.RSA' senza modo e padding: SunJCE utilizza ECB/PKCS1Padding, BouncyCastle NONE/NoPadding.
	 *
	 *  Le 'SecretKeyFactory' della famiglia PBE, indicate per nome oppure tramite OID: le due implementazioni codificano
	 *  la password in modo differente, UTF-8 per SunJCE e UTF-16BE secondo la convenzione PKCS#12 per BouncyCastle, e
	 *  producono quindi chiavi differenti. E' la medesima causa per cui l'istanza del provider viene privata di tre alias
	 *  in 'BouncyCastleUtilities', altrimenti il jdk non riesce a leggere i keystore PKCS12.
	 *
	 *  Nessuno di questi servizi viene richiesto per nome da GovWay, che indica sempre la trasformazione completa.
	 *  Se il differenziale ne segnala altri, lo spostamento del provider non e' sicuro e va rivalutato. */
	private static final Set<String> SERVIZI_NOTI_NON_INTEROPERABILI = new TreeSet<>(Arrays.asList(
			"Cipher.RSA",
			"SecretKeyFactory.PBEWITHMD5ANDDES",
			"SecretKeyFactory.1.2.840.113549.1.5.3",              // pbeWithMD5AndDES
			"SecretKeyFactory.1.2.840.113549.1.12.1.1",           // pbeWithSHAAnd128BitRC4
			"SecretKeyFactory.1.2.840.113549.1.12.1.2",           // pbeWithSHAAnd40BitRC4
			"SecretKeyFactory.1.2.840.113549.1.12.1.3",           // pbeWithSHAAnd3-KeyTripleDES-CBC
			"SecretKeyFactory.1.2.840.113549.1.12.1.5",           // pbeWithSHAAnd128BitRC2-CBC
			"SecretKeyFactory.1.2.840.113549.1.12.1.6",           // pbeWithSHAAnd40BitRC2-CBC
			"SecretKeyFactory.OID.1.2.840.113549.1.12.1.1",
			"SecretKeyFactory.OID.1.2.840.113549.1.12.1.2"));

	/** Trasformazioni note come non interoperabili: la trasformazione indica il digest di OAEP ma non quello di MGF1,
	 *  che SunJCE assume SHA-1 e BouncyCastle uguale al digest indicato. GovWay non ne risente perche' i punti che le
	 *  utilizzano indicano esplicitamente i parametri, si veda 'OAEPUtils'. */
	private static final Set<String> TRASFORMAZIONI_NOTE_NON_INTEROPERABILI = new TreeSet<>(Arrays.asList(
			"RSA",
			"RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
			"RSA/ECB/OAEPWithSHA-384AndMGF1Padding",
			"RSA/ECB/OAEPWithSHA-512AndMGF1Padding"));

	private static final String PREFISSO_ALIAS = "Alg.Alias.";
	private static final byte [] PAYLOAD = "GovWay verifica migrazione provider".getBytes();

	private static KeyPair keyPairRsa;
	private static KeyPair keyPairEc;
	private static SecretKey secretKeyAes;


	public static void main(String[] args) throws Exception {

		testDifferenzialeServizi();

		testDifferenzialeTrasformazioni();

		testMigrazioneByok();

		testMigrazioneJwe();

		testInteroperabilitaJweNimbus();

		System.out.println("Testsuite terminata");

	}


	// ===== Registrazione del provider nelle due posizioni =====

	/** Posizione storica, precedente allo spostamento: subito dopo il provider 'SUN'.
	 *  Viene indicata esplicitamente e non tramite la utility, poiche' il confronto deve continuare ad avere significato
	 *  anche dopo che la utility ha cambiato posizione. */
	private static void registraPosizioneAttuale() {
		rimuoviBouncyCastle();
		Security.insertProviderAt(BouncyCastleUtilities.newBouncyCastleProvider(), 2);
	}

	/** Posizione attuale, utilizzata a runtime da GovWay: subito dopo 'SunJCE' */
	private static void registraPosizioneSuccessivaSunJce() {
		BouncyCastleUtilities.addBouncyCastleAfterSun(true);
	}

	private static void rimuoviBouncyCastle() {
		Security.removeProvider(PROVIDER_BC);
	}


	// ===== 1) Differenziale esaustivo sui servizi che cambiano provider =====

	public static void testDifferenzialeServizi() throws UtilsException {

		System.out.println("========================= Differenziale sui servizi che cambiano provider ==============================");

		try {
			inizializzaChiavi();

			Set<String> impattati = getServiziImpattati();
			System.out.println("Servizi che cambierebbero provider: "+impattati.size());

			Set<String> nonInteroperabili = new TreeSet<>();
			List<String> nonEsercitabili = new ArrayList<>();
			int verificati = 0;

			for (String servizio : impattati) {
				int separatore = servizio.indexOf('.');
				String tipo = servizio.substring(0, separatore);
				String algoritmo = servizio.substring(separatore+1);
				Esito esito = verificaServizio(tipo, algoritmo);
				if(Esito.INTEROPERABILE.equals(esito)) {
					verificati++;
				}
				else if(Esito.NON_INTEROPERABILE.equals(esito)) {
					nonInteroperabili.add(servizio);
				}
				else {
					nonEsercitabili.add(servizio);
				}
			}

			System.out.println("   interoperabili        : "+verificati);
			System.out.println("   NON interoperabili    : "+nonInteroperabili.size()+" "+nonInteroperabili);
			System.out.println("   non esercitabili      : "+nonEsercitabili.size()+" "+getRipartizionePerTipo(nonEsercitabili));

			checkAtteso("servizi", nonInteroperabili, SERVIZI_NOTI_NON_INTEROPERABILI);

		}finally {
			rimuoviBouncyCastle();
		}

	}

	private enum Esito { INTEROPERABILE, NON_INTEROPERABILE, NON_ESERCITABILE }

	/** Servizi oggi serviti da BouncyCastle che, spostandolo dopo 'SunJCE', verrebbero serviti da un provider del jdk:
	 *  sono quelli dichiarati da BouncyCastle e da un provider compreso fra 'SunRsaSign' e 'SunJCE', ma non da 'SUN',
	 *  che precede comunque BouncyCastle. L'elenco viene ricavato dai provider stessi, quindi si aggiorna da solo. */
	private static Set<String> getServiziImpattati() {
		Set<String> sun = new TreeSet<>();
		Set<String> intermedi = new TreeSet<>();
		List<String> nomiIntermedi = Arrays.asList("SunRsaSign", "SunEC", "SunJSSE", PROVIDER_SUN_JCE);
		for (Provider provider : ProviderUtils.getProviders()) {
			if(PROVIDER_SUN.equals(provider.getName())) {
				sun.addAll(getServizi(provider));
			}
			else if(nomiIntermedi.contains(provider.getName())) {
				intermedi.addAll(getServizi(provider));
			}
		}
		Set<String> impattati = new TreeSet<>(getServizi(BouncyCastleUtilities.newBouncyCastleProvider()));
		impattati.retainAll(intermedi);
		impattati.removeAll(sun);
		return impattati;
	}

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

	/** Le trasformazioni raccolte dalle librerie non sono tutte cifrature: vi compaiono anche digest, HMAC e firme, ed un
	 *  medesimo nome puo' corrispondere a piu' tipi di servizio: 'RSA', ad esempio, e' sia un 'Cipher' sia una 'Signature'.
	 *  Vengono percio' verificati tutti i tipi disponibili, ed e' sufficiente che uno solo non sia interoperabile. */
	private static Esito verificaAlgoritmo(String algoritmo) {

		List<Esito> esiti = new ArrayList<>();

		if(disponibile("MessageDigest", algoritmo)) {
			esiti.add(confrontaDeterministico(() -> MessageDigest.getInstance(algoritmo).digest(PAYLOAD)));
		}
		if(disponibile("Mac", algoritmo)) {
			esiti.add(confrontaDeterministico(() -> {
				Mac mac = Mac.getInstance(algoritmo);
				mac.init(new SecretKeySpec(new byte[32], algoritmo));
				return mac.doFinal(PAYLOAD);
			}));
		}
		if(disponibile("Signature", algoritmo)) {
			esiti.add(verificaFirma(algoritmo));
		}
		esiti.add(verificaCipher(algoritmo));

		if(esiti.contains(Esito.NON_INTEROPERABILE)) {
			return Esito.NON_INTEROPERABILE;
		}
		return esiti.contains(Esito.INTEROPERABILE) ? Esito.INTEROPERABILE : Esito.NON_ESERCITABILE;
	}

	private static boolean disponibile(String tipo, String algoritmo) {
		try {
			switch (tipo) {
				case "MessageDigest": MessageDigest.getInstance(algoritmo); return true;
				case "Mac": Mac.getInstance(algoritmo); return true;
				case "Signature": Signature.getInstance(algoritmo); return true;
				default: return false;
			}
		}catch(Exception e) {
			return false;
		}
	}

	private static Esito verificaServizio(String tipo, String algoritmo) {
		switch (tipo) {
			case "MessageDigest": return confrontaDeterministico(() -> MessageDigest.getInstance(algoritmo).digest(PAYLOAD));
			case "Mac": return confrontaDeterministico(() -> {
					Mac mac = Mac.getInstance(algoritmo);
					mac.init(new SecretKeySpec(new byte[32], algoritmo));
					return mac.doFinal(PAYLOAD);
				});
			case "SecretKeyFactory": return confrontaDeterministico(() ->
					SecretKeyFactory.getInstance(algoritmo).generateSecret(getKeySpec(algoritmo)).getEncoded());
			case "KeyFactory": return confrontaDeterministico(() -> {
					PublicKey pubblica = getKeyPair(algoritmo).getPublic();
					return KeyFactory.getInstance(algoritmo).generatePublic(new X509EncodedKeySpec(pubblica.getEncoded())).getEncoded();
				});
			case "Signature": return verificaFirma(algoritmo);
			case "Cipher": return verificaCipher(algoritmo);
			default: return Esito.NON_ESERCITABILE; // KeyGenerator, KeyPairGenerator, AlgorithmParameters, ...: non producono dati confrontabili
		}
	}


	// ===== 2) Differenziale sulle trasformazioni =====

	/** Le trasformazioni, cioe' le forme 'algoritmo/modo/padding', non sono servizi registrati dai provider e non sono
	 *  quindi enumerabili come tali: JCA risolve il servizio corrispondente all'algoritmo ed applica modo e padding
	 *  all'implementazione ottenuta. L'elenco viene percio' ricavato dalle librerie che le utilizzano - le costanti di
	 *  'AlgorithmUtils' di CXF e la mappatura URI/JCEName di santuario - in modo che si aggiorni con esse. */
	public static void testDifferenzialeTrasformazioni() throws UtilsException {

		System.out.println("========================= Differenziale sulle trasformazioni ==============================");

		try {
			inizializzaChiavi();

			Set<String> trasformazioni = getTrasformazioni();
			System.out.println("Trasformazioni verificate: "+trasformazioni.size()+" "+trasformazioni);

			Set<String> nonInteroperabili = new TreeSet<>();
			List<String> nonEsercitabili = new ArrayList<>();
			for (String trasformazione : trasformazioni) {
				Esito esito = verificaAlgoritmo(trasformazione);
				if(Esito.NON_INTEROPERABILE.equals(esito)) {
					nonInteroperabili.add(trasformazione);
				}
				else if(Esito.NON_ESERCITABILE.equals(esito)) {
					nonEsercitabili.add(trasformazione);
				}
			}

			System.out.println("   NON interoperabili : "+nonInteroperabili.size()+" "+nonInteroperabili);
			System.out.println("   non esercitabili   : "+nonEsercitabili.size()+" "+nonEsercitabili);

			checkAtteso("trasformazioni", nonInteroperabili, TRASFORMAZIONI_NOTE_NON_INTEROPERABILI);

		}finally {
			rimuoviBouncyCastle();
		}

	}

	private static Set<String> getTrasformazioni() {

		Set<String> trasformazioni = new TreeSet<>();

		// trasformazioni utilizzate da GovWay e dalle librerie, indipendentemente dalla loro presenza nel classpath
		trasformazioni.add("RSA");
		trasformazioni.add("RSA/ECB/PKCS1Padding");
		trasformazioni.add("RSA/ECB/OAEPPadding");
		trasformazioni.add("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
		trasformazioni.add(ALGO_KEY_OAEP);
		trasformazioni.add("RSA/ECB/OAEPWithSHA-384AndMGF1Padding");
		trasformazioni.add("RSA/ECB/OAEPWithSHA-512AndMGF1Padding");
		trasformazioni.add("AES/CBC/PKCS5Padding");
		trasformazioni.add("AES/GCM/NoPadding");
		trasformazioni.add("AES/CTR/NoPadding");
		trasformazioni.add("AESWrap");

		// costanti '*_JAVA' dichiarate da 'AlgorithmUtils' di CXF: sono i nomi java su cui vengono mappati gli algoritmi JWA
		aggiungiTrasformazioniCxf(trasformazioni);

		// attributi 'JCEName' dichiarati nella configurazione di santuario: sono i nomi java su cui vengono mappati gli URI XML
		aggiungiTrasformazioniSantuario(trasformazioni);

		return trasformazioni;
	}

	private static void aggiungiTrasformazioniCxf(Set<String> trasformazioni) {
		try {
			Class<?> algorithmUtils = Class.forName("org.apache.cxf.rs.security.jose.jwa.AlgorithmUtils");
			for (java.lang.reflect.Field field : algorithmUtils.getDeclaredFields()) {
				if(field.getName().endsWith("_JAVA") && String.class.equals(field.getType())) {
					Object valore = field.get(null);
					if(valore instanceof String) {
						trasformazioni.add((String)valore);
					}
				}
			}
		}catch(Exception e) {
			System.out.println("   NOTA: costanti di CXF non raccolte ("+e.getClass().getSimpleName()+")");
		}
	}

	private static void aggiungiTrasformazioniSantuario(Set<String> trasformazioni) {
		try (InputStream is = ProviderMigrationTest.class.getResourceAsStream("/org/apache/xml/security/resource/config.xml")){
			if(is==null) {
				System.out.println("   NOTA: configurazione di santuario non presente nel classpath");
				return;
			}
			String configurazione = Utilities.getAsString(is, "UTF-8");
			Matcher matcher = Pattern.compile("JCEName=\"([^\"]+)\"").matcher(configurazione);
			while (matcher.find()) {
				trasformazioni.add(matcher.group(1));
			}
		}catch(Exception e) {
			System.out.println("   NOTA: configurazione di santuario non raccolta ("+e.getClass().getSimpleName()+")");
		}
	}


	// ===== 3) Migrazione dei segreti cifrati con BYOK =====

	public static void testMigrazioneByok() throws UtilsException {

		System.out.println("========================= Migrazione dei segreti BYOK ==============================");

		try {
			inizializzaChiavi();

			// scritto con la posizione attuale, letto con quella successiva a SunJCE
			verificaByok(true);
			// e il verso opposto, perche' un nodo non aggiornato deve continuare a leggere quanto scritto da uno aggiornato
			verificaByok(false);

		}finally {
			rimuoviBouncyCastle();
		}

	}

	private static void verificaByok(boolean scritturaConPosizioneAttuale) throws UtilsException {

		if(scritturaConPosizioneAttuale) {
			registraPosizioneAttuale();
		}
		else {
			registraPosizioneSuccessivaSunJce();
		}
		EncryptWrapKey encrypt = new EncryptWrapKey(keyPairRsa.getPublic());
		byte [] cifrato = encrypt.encrypt(PAYLOAD, ALGO_KEY_OAEP, ALGO_CONTENT_CBC);
		byte [] chiaveIncapsulata = encrypt.getWrappedKey();
		byte [] iv = encrypt.getIV();
		String provider = getProviderCipher(ALGO_KEY_OAEP);

		if(scritturaConPosizioneAttuale) {
			registraPosizioneSuccessivaSunJce();
		}
		else {
			registraPosizioneAttuale();
		}
		byte [] letto = new DecryptWrapKey(keyPairRsa.getPrivate()).decrypt(cifrato, chiaveIncapsulata, iv, ALGO_KEY_OAEP, ALGO_CONTENT_CBC);

		System.out.println("   scritto con BouncyCastle "+(scritturaConPosizioneAttuale?"alla posizione 2":"dopo SunJCE")
				+" (cipher risolto da "+provider+"), letto con l'altra posizione (cipher risolto da "+getProviderCipher(ALGO_KEY_OAEP)+")");

		if(!Arrays.equals(PAYLOAD, letto)) {
			throw new UtilsException("Il segreto letto non corrisponde a quello scritto");
		}
	}


	// ===== 4) Migrazione dei contenuti JWE =====

	public static void testMigrazioneJwe() throws UtilsException {

		System.out.println("========================= Migrazione dei contenuti JWE ==============================");

		try {
			java.security.KeyStore keystore = getKeystore();

			verificaJwe(keystore, true);
			verificaJwe(keystore, false);

		}finally {
			rimuoviBouncyCastle();
		}

	}

	private static void verificaJwe(java.security.KeyStore keystore, boolean scritturaConPosizioneAttuale) throws UtilsException {

		String contenuto = "{\"verifica\":\"migrazione provider\"}";

		if(scritturaConPosizioneAttuale) {
			registraPosizioneAttuale();
		}
		else {
			registraPosizioneSuccessivaSunJce();
		}
		JsonEncrypt encrypt = new JsonEncrypt(keystore, ALIAS, JWE_KEY_ALGORITHM, JWE_CONTENT_ALGORITHM,
				new JWEOptions(JOSESerialization.COMPACT));
		String jwe = encrypt.encrypt(contenuto);
		String provider = getProviderCipher(ALGO_KEY_OAEP);

		if(scritturaConPosizioneAttuale) {
			registraPosizioneSuccessivaSunJce();
		}
		else {
			registraPosizioneAttuale();
		}
		JsonDecrypt decrypt = new JsonDecrypt(keystore, ALIAS, PASSWORD, JWE_KEY_ALGORITHM, JWE_CONTENT_ALGORITHM,
				new JWTOptions(JOSESerialization.COMPACT));
		decrypt.decrypt(jwe);

		System.out.println("   prodotto con BouncyCastle "+(scritturaConPosizioneAttuale?"alla posizione 2":"dopo SunJCE")
				+" (cipher risolto da "+provider+"), letto con l'altra posizione (cipher risolto da "+getProviderCipher(ALGO_KEY_OAEP)+")");

		if(!contenuto.equals(decrypt.getDecodedPayload())) {
			throw new UtilsException("Il contenuto JWE letto non corrisponde a quello prodotto: "+decrypt.getDecodedPayload());
		}
	}


	// ===== 5) Interoperabilita' JWE con una implementazione indipendente =====

	/** I confronti fra i due ordinamenti dimostrano che il formato non cambia, ma non che sia quello giusto: produttore e
	 *  consumatore sono comunque le stesse librerie. Nimbus, gia' presente nel classpath, implementa RFC 7518 indicando
	 *  esplicitamente i parametri OAEP, quindi non risente dell'ordine dei provider ed e' un riferimento indipendente. */
	public static void testInteroperabilitaJweNimbus() throws UtilsException {

		System.out.println("========================= Interoperabilita' JWE con Nimbus ==============================");

		try {
			java.security.KeyStore keystore = getKeystore();
			java.security.cert.Certificate certificato = keystore.getCertificate(ALIAS);
			Key privata = keystore.getKey(ALIAS, PASSWORD.toCharArray());
			String contenuto = "{\"verifica\":\"interoperabilita RFC 7518\"}";

			for (boolean posizioneAttuale : new boolean[] {true, false}) {

				if(posizioneAttuale) {
					registraPosizioneAttuale();
				}
				else {
					registraPosizioneSuccessivaSunJce();
				}
				String posizione = posizioneAttuale ? "posizione 2" : "dopo SunJCE";

				// Nimbus produce, GovWay legge
				String jweNimbus = cifraConNimbus(certificato.getPublicKey(), contenuto);
				JsonDecrypt decrypt = new JsonDecrypt(keystore, ALIAS, PASSWORD, JWE_KEY_ALGORITHM, JWE_CONTENT_ALGORITHM,
						new JWTOptions(JOSESerialization.COMPACT));
				decrypt.decrypt(jweNimbus);
				if(!contenuto.equals(decrypt.getDecodedPayload())) {
					throw new UtilsException("Contenuto prodotto da Nimbus non letto correttamente ("+posizione+")");
				}

				// GovWay produce, Nimbus legge
				JsonEncrypt encrypt = new JsonEncrypt(keystore, ALIAS, JWE_KEY_ALGORITHM, JWE_CONTENT_ALGORITHM,
						new JWEOptions(JOSESerialization.COMPACT));
				String letto = decifraConNimbus(privata, encrypt.encrypt(contenuto));
				if(!contenuto.equals(letto)) {
					throw new UtilsException("Contenuto prodotto da GovWay non letto correttamente da Nimbus ("+posizione+")");
				}

				System.out.println("   con BouncyCastle in "+posizione+": Nimbus e GovWay si leggono a vicenda");
			}

		}catch(UtilsException e) {
			throw e;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}finally {
			rimuoviBouncyCastle();
		}

	}

	private static String cifraConNimbus(PublicKey pubblica, String contenuto) throws Exception {
		com.nimbusds.jose.JWEObject jwe = new com.nimbusds.jose.JWEObject(
				new com.nimbusds.jose.JWEHeader(com.nimbusds.jose.JWEAlgorithm.RSA_OAEP_256, com.nimbusds.jose.EncryptionMethod.A256GCM),
				new com.nimbusds.jose.Payload(contenuto));
		jwe.encrypt(new com.nimbusds.jose.crypto.RSAEncrypter((java.security.interfaces.RSAPublicKey)pubblica));
		return jwe.serialize();
	}

	private static String decifraConNimbus(Key privata, String contenuto) throws Exception {
		com.nimbusds.jose.JWEObject jwe = com.nimbusds.jose.JWEObject.parse(contenuto);
		jwe.decrypt(new com.nimbusds.jose.crypto.RSADecrypter((java.security.PrivateKey)privata));
		return jwe.getPayload().toString();
	}


	// ===== Confronti =====

	@FunctionalInterface
	private interface Produzione {
		byte [] esegui() throws Exception;
	}

	/** Algoritmi deterministici: il risultato prodotto nelle due posizioni deve coincidere byte per byte */
	private static Esito confrontaDeterministico(Produzione produzione) {
		byte [] conPosizioneAttuale;
		byte [] conPosizioneSuccessiva;
		try {
			registraPosizioneAttuale();
			conPosizioneAttuale = produzione.esegui();
			registraPosizioneSuccessivaSunJce();
			conPosizioneSuccessiva = produzione.esegui();
		}catch(Exception e) {
			return Esito.NON_ESERCITABILE;
		}
		if(conPosizioneAttuale==null || conPosizioneSuccessiva==null) {
			return Esito.NON_ESERCITABILE;
		}
		return Arrays.equals(conPosizioneAttuale, conPosizioneSuccessiva) ? Esito.INTEROPERABILE : Esito.NON_INTEROPERABILE;
	}

	/** Firme: essendo in generale randomizzate si verifica che ciascuna posizione validi la firma prodotta dall'altra */
	private static Esito verificaFirma(String algoritmo) {
		KeyPair keyPair = getKeyPair(algoritmo);
		if(keyPair==null) {
			return Esito.NON_ESERCITABILE;
		}
		try {
			registraPosizioneAttuale();
			byte [] firmaAttuale = firma(algoritmo, keyPair);
			registraPosizioneSuccessivaSunJce();
			byte [] firmaSuccessiva = firma(algoritmo, keyPair);
			boolean successivaVerificaAttuale = verifica(algoritmo, keyPair, firmaAttuale);
			registraPosizioneAttuale();
			boolean attualeVerificaSuccessiva = verifica(algoritmo, keyPair, firmaSuccessiva);
			return successivaVerificaAttuale && attualeVerificaSuccessiva ? Esito.INTEROPERABILE : Esito.NON_INTEROPERABILE;
		}catch(Exception e) {
			return Esito.NON_ESERCITABILE;
		}
	}

	private static byte[] firma(String algoritmo, KeyPair keyPair) throws Exception {
		Signature signature = Signature.getInstance(algoritmo);
		signature.initSign(keyPair.getPrivate());
		signature.update(PAYLOAD);
		return signature.sign();
	}
	private static boolean verifica(String algoritmo, KeyPair keyPair, byte [] firma) throws Exception {
		Signature signature = Signature.getInstance(algoritmo);
		signature.initVerify(keyPair.getPublic());
		signature.update(PAYLOAD);
		return signature.verify(firma);
	}

	/** Cifrature: si verifica che ciascuna posizione decifri il testo cifrato dall'altra */
	private static Esito verificaCipher(String trasformazione) {
		try {
			registraPosizioneAttuale();
			Cifratura conPosizioneAttuale = cifra(trasformazione);
			registraPosizioneSuccessivaSunJce();
			Cifratura conPosizioneSuccessiva = cifra(trasformazione);
			boolean successivaDecifraAttuale = decifra(trasformazione, conPosizioneAttuale);
			registraPosizioneAttuale();
			boolean attualeDecifraSuccessiva = decifra(trasformazione, conPosizioneSuccessiva);
			return successivaDecifraAttuale && attualeDecifraSuccessiva ? Esito.INTEROPERABILE : Esito.NON_INTEROPERABILE;
		}catch(Exception e) {
			return Esito.NON_ESERCITABILE;
		}
	}

	private static class Cifratura {
		private byte [] dati;
		private byte [] iv;
	}

	private static Cifratura cifra(String trasformazione) throws Exception {
		Cipher cipher = Cipher.getInstance(trasformazione);
		Key chiave = getChiaveCifratura(trasformazione, true);
		if(chiave==null) {
			throw new UtilsException("Chiave non determinata per '"+trasformazione+"'");
		}
		cipher.init(Cipher.ENCRYPT_MODE, chiave);
		Cifratura cifratura = new Cifratura();
		cifratura.dati = cipher.doFinal(PAYLOAD);
		cifratura.iv = cipher.getIV();
		return cifratura;
	}

	private static boolean decifra(String trasformazione, Cifratura cifratura) {
		try {
			Cipher cipher = Cipher.getInstance(trasformazione);
			Key chiave = getChiaveCifratura(trasformazione, false);
			if(cifratura.iv!=null) {
				if(trasformazione.contains("/GCM/")) {
					cipher.init(Cipher.DECRYPT_MODE, chiave, new GCMParameterSpec(128, cifratura.iv));
				}
				else {
					cipher.init(Cipher.DECRYPT_MODE, chiave, new IvParameterSpec(cifratura.iv));
				}
			}
			else {
				cipher.init(Cipher.DECRYPT_MODE, chiave);
			}
			return Arrays.equals(PAYLOAD, cipher.doFinal(cifratura.dati));
		}catch(Exception e) {
			return false;
		}
	}

	private static Key getChiaveCifratura(String trasformazione, boolean cifratura) {
		String algoritmo = trasformazione.toUpperCase();
		if(algoritmo.startsWith("RSA")) {
			return cifratura ? keyPairRsa.getPublic() : keyPairRsa.getPrivate();
		}
		if(algoritmo.startsWith("AES")) {
			return secretKeyAes;
		}
		return null;
	}


	// ===== Utilita' =====

	/** I servizi non esercitabili vanno dichiarati, non taciuti: sono quelli che richiedono chiavi o parametri specifici
	 *  ed i tipi che non producono dati confrontabili, come i generatori di chiavi. */
	private static java.util.Map<String,Integer> getRipartizionePerTipo(List<String> servizi){
		java.util.Map<String,Integer> perTipo = new java.util.TreeMap<>();
		for (String servizio : servizi) {
			perTipo.merge(servizio.substring(0, servizio.indexOf('.')), 1, Integer::sum);
		}
		return perTipo;
	}

	private static void checkAtteso(String cosa, Set<String> rilevati, Set<String> attesi) throws UtilsException {
		Set<String> inattesi = new TreeSet<>(rilevati);
		inattesi.removeAll(attesi);
		if(!inattesi.isEmpty()) {
			throw new UtilsException("Rilevati "+cosa+" non interoperabili non previsti: "+inattesi
					+"; lo spostamento del provider '"+PROVIDER_BC+"' altererebbe i dati prodotti e va rivalutato");
		}
	}

	private static String getProviderCipher(String trasformazione) {
		try {
			return Cipher.getInstance(trasformazione).getProvider().getName();
		}catch(Exception e) {
			return "?";
		}
	}

	private static java.security.spec.KeySpec getKeySpec(String algoritmo) {
		if(algoritmo.toUpperCase().startsWith("PBKDF2")) {
			return new PBEKeySpec(PASSWORD.toCharArray(), new byte[16], 4096, 256);
		}
		return new PBEKeySpec(PASSWORD.toCharArray(), new byte[8], 2048, 128);
	}

	private static KeyPair getKeyPair(String algoritmo) {
		String a = algoritmo.toUpperCase();
		if(a.contains("ECDSA") || "EC".equals(a) || a.contains("ECDDSA")) {
			return keyPairEc;
		}
		if(a.contains("RSA")) {
			return keyPairRsa;
		}
		return null;
	}

	private static void inizializzaChiavi() throws UtilsException {
		if(keyPairRsa!=null) {
			return;
		}
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2048);
			keyPairRsa = keyPairGenerator.generateKeyPair();
			keyPairGenerator = KeyPairGenerator.getInstance("EC");
			keyPairGenerator.initialize(new ECGenParameterSpec("secp256r1"));
			keyPairEc = keyPairGenerator.generateKeyPair();
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(256);
			secretKeyAes = keyGenerator.generateKey();
			RandomUtilities.getSecureRandom();
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	private static java.security.KeyStore getKeystore() throws UtilsException {
		try {
			java.security.KeyStore keystore = java.security.KeyStore.getInstance(KEYSTORE_TIPO);
			try(InputStream is = KeystoreTest.class.getResourceAsStream(KeystoreTest.PREFIX+KEYSTORE)){
				keystore.load(is, PASSWORD.toCharArray());
			}
			return keystore;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

}
