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
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

/**
 * Backend HTTPS usato dai test 'tls_deprecato', eseguito in un processo separato ({@link TLSDeprecatoMockServerControl}).
 *
 * Il processo viene avviato con un 'java.security' che non disabilita alcun algoritmo, così da poter esporre anche
 * protocolli e cipher che la JVM di GovWay rifiuta (SSLv3, 3DES, ...): la JVM della testsuite ha già inizializzato
 * JSSE e i vincoli non possono essere modificati a runtime.
 *
 * Il server ascolta su un'unica porta ed espone una {@link VarianteServerTLS} alla volta, scelta tramite comandi
 * ricevuti sullo standard input (una riga per comando, risposta su una riga dello standard output):
 * <ul>
 *   <li>'start VARIANTE': (ri)avvia il server con la variante indicata; risponde 'OK' o 'ERROR messaggio';</li>
 *   <li>'last': risponde 'LAST protocollo cipher' dell'ultimo handshake completato dall'avvio della variante, 'LAST -' se nessuno;</li>
 *   <li>'stop': ferma il server; risponde 'OK';</li>
 *   <li>'exit' (o chiusura dello standard input): termina il processo.</li>
 * </ul>
 *
 * Ad ogni richiesta HTTP risponde 200 riportando protocollo e cipher negoziati negli header
 * {@link #HEADER_PROTOCOL} e {@link #HEADER_CIPHER} e nel body JSON. Le risposte sono sempre 'Connection: close',
 * così che ogni invocazione esegua un nuovo handshake e un pool di connessioni non riutilizzi una connessione
 * negoziata con la variante precedente.
 *
 * La classe dipende solo dal JDK: il processo viene avviato con il solo classpath della testsuite compilata.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TLSDeprecatoMockServer {

	public static final String HEADER_PROTOCOL = "govway-testsuite-tls-protocol";
	public static final String HEADER_CIPHER = "govway-testsuite-tls-cipher";

	static final String CMD_START = "start";
	static final String CMD_LAST = "last";
	static final String CMD_STOP = "stop";
	static final String CMD_EXIT = "exit";
	static final String RISPOSTA_OK = "OK";
	static final String RISPOSTA_ERRORE = "ERROR";
	static final String RISPOSTA_LAST = "LAST";
	static final String NESSUN_HANDSHAKE = "-";

	private static final int MAX_HEADER_BYTES = 64*1024;

	private final int port;
	private final KeyStore keystoreRSA;
	private final KeyStore keystoreEC;
	private final char[] ps;

	private SSLServerSocket serverSocket;
	private Thread acceptThread;
	private ExecutorService executor;
	private volatile String ultimoHandshake = NESSUN_HANDSHAKE;

	private TLSDeprecatoMockServer(int port, String keystoreRSA, String keystoreEC, String ps) throws Exception {
		this.port = port;
		this.ps = ps.toCharArray();
		this.keystoreRSA = load(keystoreRSA, this.ps);
		this.keystoreEC = load(keystoreEC, this.ps);
	}

	private static KeyStore load(String path, char[] ps) throws Exception {
		KeyStore ks = KeyStore.getInstance("PKCS12");
		try(InputStream is = new FileInputStream(path)){
			ks.load(is, ps);
		}
		return ks;
	}

	/**
	 * Argomenti: porta, keystore PKCS12 con chiave RSA, keystore PKCS12 con chiave EC, password dei keystore
	 */
	public static void main(String[] args) throws Exception {
		if(args.length!=4) {
			System.err.println("Usage: "+TLSDeprecatoMockServer.class.getName()+" <port> <keystoreRSA.p12> <keystoreEC.p12> <password>");
			System.exit(2);
		}
		TLSDeprecatoMockServer server = new TLSDeprecatoMockServer(Integer.parseInt(args[0]), args[1], args[2], args[3]);
		server.comandi(System.in, System.out);
		System.exit(0);
	}

	private void comandi(InputStream in, PrintStream out) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		String line = null;
		while((line = reader.readLine())!=null) {
			String cmd = line.trim();
			if(cmd.isEmpty()) {
				continue;
			}
			String risposta = null;
			try {
				if(cmd.startsWith(CMD_START+" ")) {
					stop();
					start(VarianteServerTLS.valueOf(cmd.substring(CMD_START.length()+1).trim()));
					risposta = RISPOSTA_OK;
				}
				else if(CMD_LAST.equals(cmd)) {
					risposta = RISPOSTA_LAST+" "+this.ultimoHandshake;
				}
				else if(CMD_STOP.equals(cmd)) {
					stop();
					risposta = RISPOSTA_OK;
				}
				else if(CMD_EXIT.equals(cmd)) {
					stop();
					return;
				}
				else {
					risposta = RISPOSTA_ERRORE+" comando sconosciuto '"+cmd+"'";
				}
			}catch(Exception e) {
				e.printStackTrace(System.err);
				risposta = RISPOSTA_ERRORE+" "+String.valueOf(e.getMessage()).replace('\n', ' ');
			}
			out.println(risposta);
			out.flush();
		}
		stop();
	}

	private void start(VarianteServerTLS variante) throws Exception {
		KeyStore ks = VarianteServerTLS.Certificato.EC.equals(variante.getCertificato()) ? this.keystoreEC : this.keystoreRSA;
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		kmf.init(ks, this.ps);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(kmf.getKeyManagers(), null, null);

		SSLServerSocket ss = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket();
		try {
			ss.setReuseAddress(true);
			ss.setEnabledProtocols(variante.getProtocolli());
			if(variante.getCipherSuite()!=null) {
				ss.setEnabledCipherSuites(new String[] {variante.getCipherSuite()});
			}
			ss.bind(new InetSocketAddress(this.port));
		}catch(Exception e) {
			ss.close();
			throw e;
		}
		this.serverSocket = ss;
		this.ultimoHandshake = NESSUN_HANDSHAKE;
		this.executor = Executors.newCachedThreadPool();
		ExecutorService ex = this.executor;
		this.acceptThread = new Thread(() -> accept(ss, ex), "tls-deprecato-mock-"+variante.name());
		this.acceptThread.setDaemon(true);
		this.acceptThread.start();
		System.err.println("Variante "+variante.name()+" ("+variante.descrizione()+") in ascolto sulla porta "+this.port);
	}

	private void stop() {
		if(this.serverSocket!=null) {
			try {
				this.serverSocket.close();
			}catch(IOException e) {
				// ignore
			}
			this.serverSocket = null;
		}
		if(this.acceptThread!=null) {
			try {
				this.acceptThread.join(5000);
			}catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			this.acceptThread = null;
		}
		if(this.executor!=null) {
			this.executor.shutdown();
			try {
				if(!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
					this.executor.shutdownNow();
				}
			}catch(InterruptedException e) {
				this.executor.shutdownNow();
				Thread.currentThread().interrupt();
			}
			this.executor = null;
		}
	}

	private void accept(SSLServerSocket ss, ExecutorService ex) {
		while(!ss.isClosed()) {
			try {
				SSLSocket socket = (SSLSocket) ss.accept();
				ex.execute(() -> gestisci(socket));
			}catch(SocketException e) {
				// server chiuso
			}catch(Exception e) {
				if(!ss.isClosed()) {
					e.printStackTrace(System.err);
				}
			}
		}
	}

	private void gestisci(SSLSocket socket) {
		try(SSLSocket s = socket){
			s.setSoTimeout(30000);
			s.startHandshake();
			SSLSession session = s.getSession();
			String protocol = session.getProtocol();
			String cipher = session.getCipherSuite();
			this.ultimoHandshake = protocol+" "+cipher;

			InputStream is = s.getInputStream();
			String headers = leggiHeaders(is);
			if(headers==null) {
				return; // connessione chiusa dopo l'handshake (es. verifica di sola connettività)
			}
			leggiBody(is, headers);

			byte[] body = ("{\"protocol\":\""+protocol+"\",\"cipher\":\""+cipher+"\"}").getBytes(StandardCharsets.UTF_8);
			String risposta = "HTTP/1.1 200 OK\r\n"+
					"Content-Type: application/json\r\n"+
					"Content-Length: "+body.length+"\r\n"+
					HEADER_PROTOCOL+": "+protocol+"\r\n"+
					HEADER_CIPHER+": "+cipher+"\r\n"+
					"Connection: close\r\n"+
					"\r\n";
			OutputStream os = s.getOutputStream();
			os.write(risposta.getBytes(StandardCharsets.US_ASCII));
			os.write(body);
			os.flush();
		}catch(Exception e) {
			System.err.println("Connessione terminata con errore: "+e.getMessage());
		}
	}

	private static String leggiHeaders(InputStream is) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		// ultimi 4 byte letti, per riconoscere la riga vuota (CRLF CRLF) che chiude gli header
		int ultimi = 0;
		int b;
		while((b = is.read())!=-1) {
			bout.write(b);
			ultimi = (ultimi << 8) | (b & 0xFF);
			if(ultimi == 0x0D0A0D0A) {
				return bout.toString(StandardCharsets.ISO_8859_1);
			}
			if(bout.size()>MAX_HEADER_BYTES) {
				throw new IOException("Header della richiesta oltre "+MAX_HEADER_BYTES+" bytes");
			}
		}
		return bout.size()>0 ? bout.toString(StandardCharsets.ISO_8859_1) : null;
	}

	private static void leggiBody(InputStream is, String headers) throws IOException {
		long contentLength = -1;
		boolean chunked = false;
		for (String h : headers.split("\r\n")) {
			int index = h.indexOf(':');
			if(index<=0) {
				continue;
			}
			String nome = h.substring(0, index).trim().toLowerCase(Locale.ROOT);
			String valore = h.substring(index+1).trim();
			if("content-length".equals(nome)) {
				contentLength = Long.parseLong(valore);
			}
			else if("transfer-encoding".equals(nome) && valore.toLowerCase(Locale.ROOT).contains("chunked")) {
				chunked = true;
			}
		}
		if(chunked) {
			// i chunk vengono consumati fino al chunk finale di dimensione 0 (senza trailer)
			while(true) {
				String size = leggiRiga(is);
				int index = size.indexOf(';');
				long len = Long.parseLong((index>0 ? size.substring(0, index) : size).trim(), 16);
				if(len==0) {
					leggiRiga(is);
					return;
				}
				skip(is, len);
				leggiRiga(is);
			}
		}
		if(contentLength>0) {
			skip(is, contentLength);
		}
	}

	private static String leggiRiga(InputStream is) throws IOException {
		StringBuilder sb = new StringBuilder();
		int b;
		while((b = is.read())!=-1 && b!='\n') {
			if(b!='\r') {
				sb.append((char)b);
			}
		}
		return sb.toString();
	}

	private static void skip(InputStream is, long len) throws IOException {
		long rimanenti = len;
		byte[] buf = new byte[8192];
		while(rimanenti>0) {
			int r = is.read(buf, 0, (int)Math.min(buf.length, rimanenti));
			if(r==-1) {
				throw new IOException("Body troncato");
			}
			rimanenti -= r;
		}
	}
}
