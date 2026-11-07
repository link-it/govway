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
package org.openspcoop2.core.protocolli.trasparente.testsuite.connettori.tls_deprecato;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;

/**
 * Avvia e pilota il processo del backend {@link TLSDeprecatoMockServer}.
 *
 * All'avvio vengono generati, con il 'keytool' del JDK in uso, due keystore PKCS12 con un certificato
 * autofirmato RSA ed EC, e un file 'java.security' che non disabilita alcun algoritmo TLS, passato al processo
 * tramite '-Djava.security.properties'.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TLSDeprecatoMockServerControl implements Closeable {

	private static final String PS = "openspcoop";
	private static final long TIMEOUT_RISPOSTA_SECONDI = 30;
	private static final long TIMEOUT_KEYTOOL_SECONDI = 60;

	private final Logger log;
	private final File dir;
	private final File logFile;
	private Process process;
	private Writer comandi;
	private final BlockingQueue<String> risposte = new LinkedBlockingQueue<>();

	public TLSDeprecatoMockServerControl(Logger log, int port) throws Exception {
		this.log = log;
		this.dir = Files.createTempDirectory("govway-testsuite-tls-deprecato").toFile();
		File keystoreRSA = new File(this.dir, "rsa.p12");
		File keystoreEC = new File(this.dir, "ec.p12");
		keytool(keystoreRSA, "-keyalg", "RSA", "-keysize", "2048");
		keytool(keystoreEC, "-keyalg", "EC", "-groupname", "secp256r1");

		File javaSecurity = new File(this.dir, "java.security.mock");
		Files.writeString(javaSecurity.toPath(),
				"# Backend dei test tls_deprecato: nessun protocollo o algoritmo TLS disabilitato\n"+
				"jdk.tls.disabledAlgorithms=\n", StandardCharsets.UTF_8);

		this.logFile = new File(this.dir, "mock.log");
		String classpath = new File(TLSDeprecatoMockServer.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
		List<String> cmd = new ArrayList<>();
		cmd.add(javaBin("java"));
		cmd.add("-Djava.security.properties="+javaSecurity.getAbsolutePath());
		cmd.add("-cp");
		cmd.add(classpath);
		cmd.add(TLSDeprecatoMockServer.class.getName());
		cmd.add(String.valueOf(port));
		cmd.add(keystoreRSA.getAbsolutePath());
		cmd.add(keystoreEC.getAbsolutePath());
		cmd.add(PS);
		this.log.info("Avvio backend TLS: {} (log: {})", cmd, this.logFile.getAbsolutePath());

		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectError(this.logFile);
		this.process = pb.start();
		this.comandi = new OutputStreamWriter(this.process.getOutputStream(), StandardCharsets.UTF_8);
		Thread reader = new Thread(() -> {
			try(BufferedReader br = new BufferedReader(new InputStreamReader(this.process.getInputStream(), StandardCharsets.UTF_8))){
				String line = null;
				while((line = br.readLine())!=null) {
					this.risposte.add(line);
				}
			}catch(IOException e) {
				// processo terminato
			}
		}, "tls-deprecato-mock-control");
		reader.setDaemon(true);
		reader.start();

		// verifica che il processo risponda ai comandi
		invia(TLSDeprecatoMockServer.CMD_STOP);
	}

	private static String javaBin(String tool) {
		return System.getProperty("java.home") + File.separator + "bin" + File.separator + tool;
	}

	private void keytool(File keystore, String ... keyalg) throws Exception {
		List<String> cmd = new ArrayList<>();
		cmd.add(javaBin("keytool"));
		cmd.add("-genkeypair");
		cmd.add("-alias");
		cmd.add("server");
		for (String a : keyalg) {
			cmd.add(a);
		}
		cmd.add("-dname");
		cmd.add("CN=127.0.0.1, OU=test, O=openspcoop.org, C=IT");
		cmd.add("-validity");
		cmd.add("3650");
		cmd.add("-storetype");
		cmd.add("PKCS12");
		cmd.add("-keystore");
		cmd.add(keystore.getAbsolutePath());
		cmd.add("-storepass");
		cmd.add(PS);
		cmd.add("-keypass");
		cmd.add(PS);
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		pb.redirectOutput(new File(this.dir, "keytool.log"));
		Process p = pb.start();
		if(!p.waitFor(TIMEOUT_KEYTOOL_SECONDI, TimeUnit.SECONDS)) {
			p.destroyForcibly();
			throw new IOException("keytool non terminato entro "+TIMEOUT_KEYTOOL_SECONDI+" secondi");
		}
		if(p.exitValue()!=0 || !keystore.exists()) {
			throw new IOException("Generazione keystore '"+keystore+"' fallita (exit "+p.exitValue()+"): "+
					Files.readString(new File(this.dir, "keytool.log").toPath()));
		}
	}

	private synchronized String invia(String comando) throws IOException, InterruptedException {
		this.risposte.clear();
		this.comandi.write(comando+"\n");
		this.comandi.flush();
		String risposta = this.risposte.poll(TIMEOUT_RISPOSTA_SECONDI, TimeUnit.SECONDS);
		if(risposta==null) {
			throw new IOException("Il backend TLS non ha risposto al comando '"+comando+"' entro "+TIMEOUT_RISPOSTA_SECONDI+" secondi"+
					(this.process.isAlive() ? "" : " (processo terminato con codice "+this.process.exitValue()+")")+": vedi "+this.logFile);
		}
		if(risposta.startsWith(TLSDeprecatoMockServer.RISPOSTA_ERRORE)) {
			throw new IOException("Comando '"+comando+"' fallito sul backend TLS: "+risposta);
		}
		return risposta;
	}

	public void start(VarianteServerTLS variante) throws IOException, InterruptedException {
		invia(TLSDeprecatoMockServer.CMD_START+" "+variante.name());
	}

	/**
	 * Restituisce protocollo e cipher dell'ultimo handshake completato dall'avvio della variante corrente, null se nessuno.
	 */
	public String[] getUltimoHandshake() throws IOException, InterruptedException {
		String risposta = invia(TLSDeprecatoMockServer.CMD_LAST);
		String valore = risposta.substring(TLSDeprecatoMockServer.RISPOSTA_LAST.length()).trim();
		if(TLSDeprecatoMockServer.NESSUN_HANDSHAKE.equals(valore)) {
			return null;
		}
		return valore.split(" ");
	}

	/**
	 * Come {@link #getUltimoHandshake()}, ma da usare quando l'handshake è certamente avvenuto (es. verifica di connettività riuscita):
	 * con TLSv1.3 l'ultimo messaggio dell'handshake (Finished) è inviato dal client, che può quindi considerarlo concluso e chiudere
	 * la connessione prima che il backend lo abbia elaborato e registrato. Si attende fino a 'timeoutMs' che l'handshake risulti registrato.
	 */
	public String[] attendiUltimoHandshake(long timeoutMs) throws IOException, InterruptedException {
		long scadenza = System.currentTimeMillis() + timeoutMs;
		String[] handshake = getUltimoHandshake();
		while(handshake==null && System.currentTimeMillis() < scadenza) {
			Thread.sleep(50);
			handshake = getUltimoHandshake();
		}
		return handshake;
	}

	@Override
	public void close() {
		if(this.process!=null) {
			try {
				this.comandi.write(TLSDeprecatoMockServer.CMD_EXIT+"\n");
				this.comandi.flush();
				if(!this.process.waitFor(10, TimeUnit.SECONDS)) {
					this.process.destroyForcibly();
				}
			}catch(Exception e) {
				this.process.destroyForcibly();
				if(e instanceof InterruptedException) {
					Thread.currentThread().interrupt();
				}
			}
			this.process = null;
		}
		eliminaDir();
	}

	private void eliminaDir() {
		File[] files = this.dir.listFiles();
		if(files!=null) {
			for (File f : files) {
				if(f.getName().equals(this.logFile.getName()) && f.length()>0 && this.log.isDebugEnabled()) {
					try {
						this.log.debug("Log del backend TLS:\n{}", Files.readString(f.toPath()));
					}catch(IOException e) {
						// ignore
					}
				}
				deleteQuietly(f.toPath());
			}
		}
		deleteQuietly(this.dir.toPath());
	}

	private static void deleteQuietly(Path p) {
		try {
			Files.deleteIfExists(p);
		}catch(IOException e) {
			// ignore
		}
	}
}
