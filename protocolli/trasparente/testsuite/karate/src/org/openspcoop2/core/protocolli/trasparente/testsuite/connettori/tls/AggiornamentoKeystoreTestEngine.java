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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.junit.Test;
import org.openspcoop2.core.protocolli.trasparente.testsuite.Bodies;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.DBVerifier;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibrary;
import org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.utils.HttpLibraryMode;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.protocol.utils.EsitiProperties;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequest;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpResponse;
import org.openspcoop2.utils.transport.http.HttpUtilities;

/**
 * AggiornamentoKeystoreTestEngine
 *
 * Verifica che un connettore https utilizzi il contenuto aggiornato del truststore (autenticazione server)
 * e del keystore (autenticazione client) quando il file viene sostituito allo stesso percorso
 * e la cache dei keystore viene svuotata, come avviene da console.
 *
 * I file referenziati dalla fruizione 'TestTLSAggiornamentoKeystore' vengono riscritti dal test in {@link #DIR}:
 * - truststore.jks: contiene o meno il certificato 'Erogatore' presentato dalle porte 8444/8445;
 * - keystore.p12: contiene la chiave 'soggetto1' (presente nel truststore 'erogatore.jks' della porta 8444)
 *   oppure una chiave autofirmata generata dal test e non riconosciuta dalla porta 8444.
 *
 * Per i connettori HTTPCore5 (BIO e NIO) viene verificato anche lo stato dei pool di connessioni, letto via JMX:
 * - un aggiornamento del contenuto deve far nascere un nuovo pool e dismettere quello precedente;
 * - una riscrittura del file con lo stesso contenuto, o un reset della cache, deve continuare a usare lo stesso pool;
 * - un pool dismesso con una richiesta ancora in corso deve essere chiuso solo al termine della richiesta;
 * - un pool dismesso viene chiuso dal timer dei connettori quando è inutilizzato da almeno 'closeIdleConnectionsAfterSeconds'.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AggiornamentoKeystoreTestEngine extends ConfigLoader {

	static final String DIR = "/tmp/govway-testsuite-tls-aggiornamento-keystore";
	private static final String TRUSTSTORE = "truststore.jks";
	private static final String KEYSTORE = "keystore.p12";
	private static final String PS = "openspcoop";
	private static final String KEY_ALIAS = "client";

	private static final String OPERAZIONE_SERVER_AUTH = "serverAuth";
	private static final String OPERAZIONE_CLIENT_AUTH = "clientAuth";
	/** stesso pool dell'operazione 'serverAuth', con 'readConnectionTimeout' a 4000ms */
	private static final String OPERAZIONE_SERVER_AUTH_TIMEOUT = "serverAuthTimeout";

	private static final String MSG_ERRORE_PKIX = "PKIX path building failed";

	/** Durata della richiesta che mantiene occupata una connessione del pool che verrà dismesso */
	private static final int SLEEP_RICHIESTA_IN_CORSO_MS = 6000;
	/** Durata della richiesta lenta oltre il read timeout (4000ms) dell'operazione 'serverAuthTimeout' */
	private static final int SLEEP_RICHIESTA_IN_TIMEOUT_MS = 10000;
	private static final String DIAGNOSTICO_READ_TIMEOUT_BIO = "Read timed out";
	private static final String DIAGNOSTICO_READ_TIMEOUT_NIO = "read timed out after 4000 MILLISECONDS";
	/** Attesa massima della chiusura di un pool dismesso: inattività richiesta (closeIdleConnectionsAfterSeconds, default 30s)
	 *  più l'intervallo di controllo del timer (closeIdleConnectionsCheckIntervalSeconds, default 60s) e un margine */
	private static final int ATTESA_CHIUSURA_POOL_DISMESSO_MS = 100000;
	private static final String CHIAVE_POOL_IMPRONTA = "contentFingerprint:";

	private enum Truststore {
		/** truststore non vuoto, ma che non contiene il certificato presentato dal server */
		SENZA_CERTIFICATO_SERVER,
		CON_CERTIFICATO_SERVER,
		/** contenuto differente da CON_CERTIFICATO_SERVER, ma sempre valido per il server */
		CON_CERTIFICATO_SERVER_E_SOGGETTO1
	}

	private static KeyPair chiaveNonFidata;
	private static X509Certificate certificatoNonFidato;

	private HttpLibraryMode mode;

	public AggiornamentoKeystoreTestEngine(HttpLibraryMode mode) {
		this.mode = mode;
	}



	// *** Autenticazione server (truststore) ***

	@Test
	public void serverAuthAggiuntaCertificatoTruststore() throws Exception {
		scriviTruststore(Truststore.SENZA_CERTIFICATO_SERVER);
		invokeKo(OPERAZIONE_SERVER_AUTH, MSG_ERRORE_PKIX);
		String pool = verificaPoolUnico(TRUSTSTORE, null, false);

		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER);
		invokeOk(OPERAZIONE_SERVER_AUTH);
		verificaPoolUnico(TRUSTSTORE, pool, true);
	}

	@Test
	public void serverAuthRimozioneCertificatoTruststore() throws Exception {
		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER);
		invokeOk(OPERAZIONE_SERVER_AUTH);
		String pool = verificaPoolUnico(TRUSTSTORE, null, false);

		scriviTruststore(Truststore.SENZA_CERTIFICATO_SERVER);
		invokeKo(OPERAZIONE_SERVER_AUTH, MSG_ERRORE_PKIX);
		verificaPoolUnico(TRUSTSTORE, pool, true);
	}

	@Test
	public void serverAuthTruststoreInvariato() throws Exception {
		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER);
		invokeOk(OPERAZIONE_SERVER_AUTH);
		String pool = verificaPoolUnico(TRUSTSTORE, null, false);

		// file riscritto (byte differenti) con gli stessi certificati: il pool non deve cambiare
		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER);
		invokeOk(OPERAZIONE_SERVER_AUTH);
		verificaPoolUnico(TRUSTSTORE, pool, false);

		// solo reset della cache: il pool non deve cambiare
		resetCacheKeystore();
		invokeOk(OPERAZIONE_SERVER_AUTH);
		verificaPoolUnico(TRUSTSTORE, pool, false);
	}

	@Test
	public void serverAuthPoolDismessoConRichiestaInCorso() throws Exception {
		// la richiesta in corso termina correttamente sul pool dismesso
		verificaPoolDismessoConRichiestaInCorso(OPERAZIONE_SERVER_AUTH, SLEEP_RICHIESTA_IN_CORSO_MS, 200, null);
	}

	@Test
	public void serverAuthPoolDismessoConRichiestaInTimeout() throws Exception {
		// la richiesta in corso sul pool dismesso termina per read timeout (azione con 'readConnectionTimeout' a 4000ms): 504 con esito ERRORE_RESPONSE_TIMEOUT
		verificaPoolDismessoConRichiestaInCorso(OPERAZIONE_SERVER_AUTH_TIMEOUT, SLEEP_RICHIESTA_IN_TIMEOUT_MS, 504,
				this.mode.isNIO() ? DIAGNOSTICO_READ_TIMEOUT_NIO : DIAGNOSTICO_READ_TIMEOUT_BIO);
	}

	private void verificaPoolDismessoConRichiestaInCorso(String operazioneLenta, int sleepMs, 
			int codiceAttesoRichiestaLenta, String diagnosticoAttesoRichiestaLenta) throws Exception {
		if(!isPoolConnessioni()) {
			return; // HttpURLConnection non gestisce pool di connessioni
		}

		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER);
		invokeOk(OPERAZIONE_SERVER_AUTH);
		String pool = verificaPoolUnico(TRUSTSTORE, null, false);

		// richiesta lenta che mantiene occupata una connessione del pool corrente
		HttpResponse [] rispostaInCorso = new HttpResponse[1];
		Exception [] erroreInCorso = new Exception[1];
		Thread richiestaInCorso = new Thread(() -> {
			try {
				rispostaInCorso[0] = invoke(operazioneLenta, "sleep="+sleepMs);
			}catch(Exception e) {
				erroreInCorso[0] = e;
			}
		});
		richiestaInCorso.start();
		Utilities.sleep(1000);

		// aggiornamento del contenuto mentre la richiesta lenta è in corso
		scriviTruststore(Truststore.CON_CERTIFICATO_SERVER_E_SOGGETTO1);
		invokeOk(OPERAZIONE_SERVER_AUTH);

		StatoPoolConnessioni stato = readStatoPool(TRUSTSTORE);
		// letto lo stato mentre la richiesta lenta è ancora in corso, altrimenti la verifica dei pool dismessi non è significativa
		assertTrue("La richiesta lenta è terminata prima della lettura dello stato dei pool", richiestaInCorso.isAlive());
		assertEquals("Pool attivi inattesi:\n"+stato.getStato(), 1, stato.getAttivi().size());
		assertNotEquals("Il pool doveva essere sostituito:\n"+stato.getStato(), pool, stato.getAttivi().get(0));
		assertTrue("Il pool con la richiesta in corso doveva risultare dismesso e non chiuso:\n"+stato.getStato(), stato.getDismessi().contains(pool));

		richiestaInCorso.join(sleepMs + 20000l);
		if(erroreInCorso[0]!=null) {
			throw erroreInCorso[0];
		}
		HttpResponse response = rispostaInCorso[0];
		assertNotNull("Richiesta lenta non terminata", response);
		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		assertEquals("Transazione "+idTransazione+": la richiesta in corso sul pool dismesso non doveva essere interrotta dalla dismissione", 
				codiceAttesoRichiestaLenta, response.getResultHTTPOperation());
		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(
				codiceAttesoRichiestaLenta==200 ? EsitoTransazioneName.OK : EsitoTransazioneName.ERRORE_RESPONSE_TIMEOUT);
		DBVerifier.verify(idTransazione, esitoExpected, diagnosticoAttesoRichiestaLenta, this.mode);

		// terminata la richiesta, il pool dismesso viene chiuso dal timer (evictor)
		long scadenza = System.currentTimeMillis() + ATTESA_CHIUSURA_POOL_DISMESSO_MS;
		while(stato.getDismessi().contains(pool) && System.currentTimeMillis() < scadenza) {
			Utilities.sleep(2000);
			stato = readStatoPool(TRUSTSTORE);
		}
		assertTrue("Il pool dismesso doveva essere chiuso dal timer entro "+ATTESA_CHIUSURA_POOL_DISMESSO_MS+"ms:\n"+stato.getStato(), !stato.getDismessi().contains(pool));
		assertTrue("Il pool dismesso non deve tornare attivo:\n"+stato.getStato(), !stato.getAttivi().contains(pool));
		assertEquals("Pool attivi inattesi:\n"+stato.getStato(), 1, stato.getAttivi().size());
	}



	// *** Autenticazione client (keystore) ***

	@Test
	public void clientAuthSostituzioneCertificatoNonFidato() throws Exception {
		scriviKeystore(false);
		invokeKo(OPERAZIONE_CLIENT_AUTH, null);
		String pool = verificaPoolUnico(KEYSTORE, null, false);

		scriviKeystore(true);
		invokeOk(OPERAZIONE_CLIENT_AUTH);
		verificaPoolUnico(KEYSTORE, pool, true);
	}

	@Test
	public void clientAuthSostituzioneConCertificatoNonFidato() throws Exception {
		scriviKeystore(true);
		invokeOk(OPERAZIONE_CLIENT_AUTH);
		String pool = verificaPoolUnico(KEYSTORE, null, false);

		scriviKeystore(false);
		invokeKo(OPERAZIONE_CLIENT_AUTH, null);
		verificaPoolUnico(KEYSTORE, pool, true);
	}



	// *** Gestione dei keystore ***

	private static void scriviTruststore(Truststore tipo) throws Exception {
		KeyStore ts = KeyStore.getInstance("JKS");
		ts.load(null, null);
		if(Truststore.CON_CERTIFICATO_SERVER.equals(tipo) || Truststore.CON_CERTIFICATO_SERVER_E_SOGGETTO1.equals(tipo)) {
			KeyStore erogatore = load("erogatore.jks", "JKS", PS);
			ts.setCertificateEntry("erogatore", erogatore.getCertificate("erogatore"));
		}
		if(Truststore.SENZA_CERTIFICATO_SERVER.equals(tipo) || Truststore.CON_CERTIFICATO_SERVER_E_SOGGETTO1.equals(tipo)) {
			KeyStore soggetto1 = load("soggetto1.p12", "PKCS12", PS);
			ts.setCertificateEntry("soggetto1", soggetto1.getCertificate("soggetto1"));
		}
		scrivi(ts, TRUSTSTORE);
	}

	private static void scriviKeystore(boolean chiaveFidata) throws Exception {
		Key key = null;
		Certificate[] chain = null;
		if(chiaveFidata) {
			KeyStore soggetto1 = load("soggetto1.p12", "PKCS12", PS);
			key = soggetto1.getKey("soggetto1", PS.toCharArray());
			chain = soggetto1.getCertificateChain("soggetto1");
		}
		else {
			initChiaveNonFidata();
			key = chiaveNonFidata.getPrivate();
			chain = new Certificate[] {certificatoNonFidato};
		}
		KeyStore ks = KeyStore.getInstance("PKCS12");
		ks.load(null, null);
		ks.setKeyEntry(KEY_ALIAS, key, PS.toCharArray(), chain);
		scrivi(ks, KEYSTORE);
	}

	private static synchronized void initChiaveNonFidata() throws Exception {
		if(chiaveNonFidata==null) {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			X500Name dn = new X500Name("CN=TestTLSAggiornamentoKeystoreNonFidato, OU=test, O=openspcoop.org, C=IT");
			long now = System.currentTimeMillis();
			JcaX509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(dn, BigInteger.valueOf(now),
					new Date(now - 3600000l), new Date(now + 86400000l), dn, kp.getPublic());
			PrivateKey pk = kp.getPrivate();
			certificatoNonFidato = new JcaX509CertificateConverter().getCertificate(builder.build(new JcaContentSignerBuilder("SHA256withRSA").build(pk)));
			chiaveNonFidata = kp;
		}
	}

	private static KeyStore load(String nome, String tipo, String ps) throws Exception {
		KeyStore ks = KeyStore.getInstance(tipo);
		try(InputStream is = new FileInputStream(new File(ConfigLoader.getGovwayCfgKeys(), nome))){
			ks.load(is, ps.toCharArray());
		}
		return ks;
	}

	private static void scrivi(KeyStore ks, String nome) throws Exception {
		File dir = new File(DIR);
		if(!dir.exists()) {
			Files.createDirectories(dir.toPath());
		}
		// il runtime GovWay e la testsuite possono girare con utenze diverse (es. Jenkins):
		// directory e file accessibili a tutti, così che il runtime li legga e un'altra utenza possa riscriverli
		dir.setReadable(true, false);
		dir.setWritable(true, false);
		dir.setExecutable(true, false);

		// la sostituzione avviene allo stesso percorso, in modo atomico, come un aggiornamento del file in esercizio
		// (createTempFile crea il file con permessi 0600: vanno allargati prima dello spostamento, che li mantiene)
		Path tmp = Files.createTempFile(dir.toPath(), nome, ".tmp");
		try(OutputStream os = new FileOutputStream(tmp.toFile())){
			ks.store(os, PS.toCharArray());
		}
		tmp.toFile().setReadable(true, false);
		tmp.toFile().setWritable(true, false);
		Files.move(tmp, new File(dir, nome).toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);

		resetCacheKeystore();
	}

	private static void resetCacheKeystore() throws Exception {
		// svuotamento delle cache dei keystore e dei dati delle richieste, come da console
		ConfigLoader.resetCache(false, "Keystore", "DatiRichieste");
	}



	// *** Stato dei pool di connessioni ***

	private boolean isPoolConnessioni() {
		return HttpLibrary.HTTPCORE.equals(this.mode.getLibrary());
	}

	private StatoPoolConnessioni readStatoPool(String file) throws Exception {
		return StatoPoolConnessioni.read(logCore, this.mode.isNIO(), DIR+"/"+file);
	}

	/**
	 * Verifica che per il keystore indicato esista un solo pool attivo, la cui chiave comprende l'impronta del contenuto.
	 * Un pool sostituito resta tra i dismessi finché il timer non lo chiude (verificato nei test dedicati).
	 * Restituisce la chiave del pool attivo, da confrontare con quella del passo successivo.
	 */
	private String verificaPoolUnico(String file, String poolPrecedente, boolean attesoNuovoPool) throws Exception {
		if(!isPoolConnessioni()) {
			return null;
		}
		StatoPoolConnessioni stato = readStatoPool(file);
		assertEquals("Pool attivi inattesi per '"+file+"':\n"+stato.getStato(), 1, stato.getAttivi().size());
		String pool = stato.getAttivi().get(0);
		assertTrue("Chiave del pool senza impronta del contenuto:\n"+stato.getStato(), pool.contains(CHIAVE_POOL_IMPRONTA));
		if(poolPrecedente!=null) {
			if(attesoNuovoPool) {
				assertNotEquals("Il contenuto di '"+file+"' è cambiato: atteso un nuovo pool:\n"+stato.getStato(), poolPrecedente, pool);
				assertTrue("Il pool precedente doveva risultare dismesso o già chiuso:\n"+stato.getStato(), !stato.getAttivi().contains(poolPrecedente));
			}
			else {
				assertEquals("Il contenuto di '"+file+"' non è cambiato: atteso lo stesso pool:\n"+stato.getStato(), poolPrecedente, pool);
			}
		}
		return pool;
	}



	// *** Invocazioni ***

	private void invokeOk(String operazione) throws Exception {
		HttpResponse response = invoke(operazione);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		assertEquals("Transazione "+idTransazione, 200, response.getResultHTTPOperation());
		assertEquals(HttpConstants.CONTENT_TYPE_JSON, response.getContentType());

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.OK);
		DBVerifier.verify(idTransazione, esitoExpected, this.mode);
	}

	private void invokeKo(String operazione, String msgErrore) throws Exception {
		HttpResponse response = invoke(operazione);

		String idTransazione = response.getHeaderFirstValue("GovWay-Transaction-ID");
		assertNotNull(idTransazione);
		assertEquals("Transazione "+idTransazione, 503, response.getResultHTTPOperation());

		long esitoExpected = EsitiProperties.getInstanceFromProtocolName(logCore, Costanti.TRASPARENTE_PROTOCOL_NAME).convertoToCode(EsitoTransazioneName.ERRORE_INVOCAZIONE);
		DBVerifier.verify(idTransazione, esitoExpected, msgErrore, this.mode);
	}

	private HttpResponse invoke(String operazione) throws Exception {
		return invoke(operazione, null);
	}
	private HttpResponse invoke(String operazione, String queryString) throws Exception {
		String url = System.getProperty("govway_base_path") + "/out/SoggettoInternoTestFruitore/SoggettoInternoTest/TestTLSAggiornamentoKeystore/v1/"+operazione;
		if(queryString!=null) {
			url = url + "?" + queryString;
		}

		HttpRequest request = new HttpRequest();
		request.setReadTimeout(20000);
		request.setMethod(HttpRequestMethod.POST);
		request.setContentType(HttpConstants.CONTENT_TYPE_JSON);
		request.setContent(Bodies.getJson(Bodies.SMALL_SIZE).getBytes());
		request.setUrl(url);

		if (this.mode != null)
			this.mode.patchRequest(request);
		return HttpUtilities.httpInvoke(request);
	}

}
