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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openspcoop2.core.protocolli.trasparente.testsuite.ConfigLoader;

/**
 * Base dei test 'tls_deprecato': avvia il backend {@link TLSDeprecatoMockServer} e legge da GovWay, via JMX,
 * protocolli e cipher abilitati per ogni tipologia SSL ({@link InformazioniSSLGovWay}).
 *
 * I test richiedono che la JVM di GovWay sia avviata con TLSv1 e TLSv1.1 abilitati, tramite
 * '-Djava.security.properties=.../tls_deprecato/java.security.tls_deprecato' (vedi RUN.README):
 * altrimenti nessuna libreria può negoziarli e le differenze tra le librerie non sarebbero verificabili.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class TLSDeprecatoBase extends ConfigLoader {

	private static final String PROP_MOCK_PORT = "connettori.tls_deprecato.mock.port";
	private static final String FILE_JAVA_SECURITY = "java.security.tls_deprecato";

	protected static TLSDeprecatoMockServerControl mock;
	protected static InformazioniSSLGovWay informazioniSSL;

	@BeforeClass
	public static void startMock() throws Exception {
		informazioniSSL = InformazioniSSLGovWay.read(logCore);
		verificaJvmGovWay();
		int port = Integer.parseInt(System.getProperty(PROP_MOCK_PORT, "8097"));
		mock = new TLSDeprecatoMockServerControl(logCore, port);
	}

	@AfterClass
	public static void stopMock() {
		if(mock!=null) {
			mock.close();
			mock = null;
		}
	}

	private static void verificaJvmGovWay() {
		for (TipologiaSSL t : TipologiaSSL.values()) {
			if(!TipologiaSSL.SSLV2HELLO.equals(t)) {
				assertTrue("Tipologia '"+t.getSslType()+"' non presente nelle informazioni SSL lette via JMX:\n"+informazioniSSL.getInformazioni(),
						informazioniSSL.isPresente(t));
			}
		}
		if(!informazioniSSL.getProtocolli(TipologiaSSL.TLSV1).contains("TLSv1") ||
				!informazioniSSL.getProtocolli(TipologiaSSL.TLSV1_1).contains("TLSv1.1")) {
			fail("La JVM di GovWay non abilita TLSv1 e TLSv1.1: avviarla con '-Djava.security.properties=<testsuite>/src/"+
					TLSDeprecatoBase.class.getPackageName().replace('.', '/')+"/"+FILE_JAVA_SECURITY+"' (vedi RUN.README).\n"+
					"Protocolli abilitati per tipologia:\n"+informazioniSSL);
		}
	}

	/**
	 * Raccoglie gli esiti delle verifiche di una tipologia su tutte le varianti del backend e, al termine,
	 * fallisce riportando la tabella degli scostamenti.
	 */
	protected static class Risultati {

		private final String titolo;
		private final List<String> righe = new ArrayList<>();
		private final List<String> scostamenti = new ArrayList<>();

		public Risultati(String titolo) {
			this.titolo = titolo;
		}

		public void add(VarianteServerTLS variante, EsitoHandshake atteso, EsitoHandshake rilevato, String erroreVerifica) {
			boolean corrisponde = atteso.corrisponde(rilevato) && erroreVerifica==null;
			String riga = String.format("%-32s | atteso: %-55s | rilevato: %s%s",
					variante.name(), atteso, rilevato, erroreVerifica!=null ? " | verifica: "+erroreVerifica : "");
			this.righe.add((corrisponde ? "   " : "!! ") + riga);
			if(!corrisponde) {
				this.scostamenti.add(riga);
			}
		}

		public void verifica() {
			String msg = report();
			if(msg!=null) {
				fail(msg);
			}
		}

		/**
		 * Registra nel log la tabella completa e restituisce la tabella degli scostamenti, null se non ve ne sono.
		 */
		public String report() {
			StringBuilder sb = new StringBuilder();
			sb.append(this.titolo).append(":\n");
			for (String r : this.righe) {
				sb.append(r).append("\n");
			}
			logCore.info(sb.toString());
			if(this.scostamenti.isEmpty()) {
				return null;
			}
			StringBuilder msg = new StringBuilder();
			msg.append(this.titolo).append(": ").append(this.scostamenti.size()).append(" scostamenti su ").append(this.righe.size()).append(" varianti\n");
			for (String s : this.scostamenti) {
				msg.append(s).append("\n");
			}
			return msg.toString();
		}
	}

	protected static String abbrevia(String s) {
		if(s==null) {
			return null;
		}
		String v = s.replace('\n', ' ').replace('\r', ' ');
		return v.length()>300 ? v.substring(0, 300)+"..." : v;
	}
}
