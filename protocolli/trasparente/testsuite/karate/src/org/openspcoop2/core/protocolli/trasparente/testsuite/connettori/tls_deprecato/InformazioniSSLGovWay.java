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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.protocolli.trasparente.testsuite.Utils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.jmx.ConfigurazioneSistema;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.slf4j.Logger;

/**
 * Protocolli e cipher suite che la JVM di GovWay abilita per ogni {@link TipologiaSSL}, letti tramite la risorsa JMX
 * 'ConfigurazioneSistema' (operazione 'getInformazioniCompleteSSL', le stesse informazioni riportate nella sezione
 * 'Runtime' della console e nel file 'govway_configurazioneSistema.log').
 *
 * Da queste informazioni viene calcolato l'esito atteso dell'handshake verso una {@link VarianteServerTLS}:
 * il comportamento atteso è quello del JDK (es. libreria 'java.net.HttpURLConnection'), dove la tipologia è il nome
 * dello SSLContext e determina protocolli e cipher abilitati, nel rispetto di 'jdk.tls.disabledAlgorithms'.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniSSLGovWay {

	private static final String PREFIX_CIPHER_SUITES = "CipherSuites: ";
	/** Ordine di preferenza nella negoziazione: viene scelta la versione più alta comune a client e server */
	private static final String[] ORDINE_PROTOCOLLI = {"TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1", "SSLv3"};

	private final Map<TipologiaSSL, List<String>> protocolli = new EnumMap<>(TipologiaSSL.class);
	private final Map<TipologiaSSL, List<String>> cipherSuites = new EnumMap<>(TipologiaSSL.class);
	private final Map<TipologiaSSL, String> errori = new EnumMap<>(TipologiaSSL.class);
	private final String informazioni;

	public static InformazioniSSLGovWay read(Logger log) throws Exception {
		Map<String,String> queryParams = Map.of(
				CostantiPdD.CHECK_STATO_PDD_RESOURCE_NAME, CostantiPdD.JMX_CONFIGURAZIONE_SISTEMA,
				CostantiPdD.CHECK_STATO_PDD_METHOD_NAME, ConfigurazioneSistema.INFORMAZIONI_COMPLETE_SSL
			);
		String jmxUrl = Utils.buildUrl(queryParams, System.getProperty("govway_base_path") + "/check");
		String info = new String(HttpUtilities.getHTTPResponse(jmxUrl, System.getProperty("jmx_username"), System.getProperty("jmx_password")).getContent());
		InformazioniSSLGovWay i = new InformazioniSSLGovWay(info);
		log.info("Informazioni SSL della JVM di GovWay per tipologia:\n{}", i.toString());
		return i;
	}

	InformazioniSSLGovWay(String informazioni) {
		this.informazioni = informazioni;
		TipologiaSSL corrente = null;
		for (String line : informazioni.split("\n")) {
			String l = line.trim();
			if(corrente!=null && l.startsWith(PREFIX_CIPHER_SUITES)) {
				this.cipherSuites.put(corrente, parseLista(l.substring(PREFIX_CIPHER_SUITES.length())));
				corrente = null;
				continue;
			}
			corrente = null;
			for (TipologiaSSL t : TipologiaSSL.values()) {
				String prefix = t.getSslType()+": ";
				if(l.startsWith(prefix)) {
					String valore = l.substring(prefix.length()).trim();
					if(valore.startsWith("[")) {
						this.protocolli.put(t, parseLista(valore));
					}
					else {
						// es. 'SSLv2Hello SSLContext not available'
						this.protocolli.put(t, new ArrayList<>());
						this.errori.put(t, valore);
					}
					corrente = t;
					break;
				}
			}
		}
	}

	private static List<String> parseLista(String valore) {
		List<String> l = new ArrayList<>();
		String v = valore.trim();
		if(v.startsWith("[") && v.endsWith("]")) {
			v = v.substring(1, v.length()-1);
			for (String s : v.split(",")) {
				if(!s.trim().isEmpty()) {
					l.add(s.trim());
				}
			}
		}
		return l;
	}

	public List<String> getProtocolli(TipologiaSSL tipologia) {
		List<String> l = this.protocolli.get(tipologia);
		return l!=null ? l : new ArrayList<>();
	}
	public List<String> getCipherSuites(TipologiaSSL tipologia) {
		List<String> l = this.cipherSuites.get(tipologia);
		return l!=null ? l : new ArrayList<>();
	}
	public boolean isPresente(TipologiaSSL tipologia) {
		return this.protocolli.containsKey(tipologia);
	}
	public String getInformazioni() {
		return this.informazioni;
	}

	public EsitoHandshake calcolaAtteso(TipologiaSSL tipologia, VarianteServerTLS variante) {
		List<String> client = getProtocolli(tipologia);
		if(client.isEmpty()) {
			return EsitoHandshake.ko("tipologia senza protocolli abilitati"+(this.errori.containsKey(tipologia) ? " ("+this.errori.get(tipologia)+")" : ""));
		}
		String protocollo = null;
		List<String> server = List.of(variante.getProtocolli());
		for (String p : ORDINE_PROTOCOLLI) {
			if(client.contains(p) && server.contains(p)) {
				protocollo = p;
				break;
			}
		}
		if(protocollo==null) {
			return EsitoHandshake.ko("nessun protocollo comune (client "+client+")");
		}
		String cipher = variante.getCipherSuite();
		if(cipher!=null && !getCipherSuites(tipologia).contains(cipher)) {
			return EsitoHandshake.ko("cipher "+cipher+" non abilitata per la tipologia");
		}
		// con le cipher di default del server non viene verificata la cipher negoziata
		return EsitoHandshake.ok(protocollo, cipher);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (TipologiaSSL t : TipologiaSSL.values()) {
			sb.append("- ").append(t.getSslType()).append(": ");
			if(this.errori.containsKey(t)) {
				sb.append(this.errori.get(t));
			}
			else {
				sb.append(getProtocolli(t)).append(" (").append(getCipherSuites(t).size()).append(" cipher suites)");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
