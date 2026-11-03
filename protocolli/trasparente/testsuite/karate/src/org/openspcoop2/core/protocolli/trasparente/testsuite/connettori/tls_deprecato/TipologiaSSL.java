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

/**
 * Tipologie SSL selezionabili su un connettore https (proprietà 'sslType') e relative configurazioni di test.
 *
 * Ogni tipologia ha, nell'erogazione {@link #API} e nell'erogazione {@link #API_CONNETTIVITA}, un gruppo dedicato
 * con un connettore che punta al backend {@link TLSDeprecatoMockServer}:
 * <ul>
 *   <li>{@link #API}: gruppo 'SpecificN' (N = posizione della tipologia), azione {@link #getAzione()}, connettore senza
 *       libreria http impostata: la libreria viene scelta dall'header della testsuite o, nella verifica di connettività,
 *       è quella di default ('org.openspcoop2.pdd.connettori.remoteAccessUtility.library');</li>
 *   <li>{@link #API_CONNETTIVITA}: gruppi 'SpecificN' con libreria 'java.net.HttpURLConnection' (azione 'URLConn_'+azione)
 *       e 'SpecificN+8' con libreria 'org.apache.hc.client5' (azione 'HC5_'+azione), usati solo per la verifica di connettività.</li>
 * </ul>
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TipologiaSSL {

	SSL("SSL", "SSL"),
	SSLV3("SSLv3", "SSLv3"),
	TLS("TLS", "TLS"),
	TLSV1("TLSv1", "TLSv1"),
	TLSV1_1("TLSv1.1", "TLSv1_1"),
	TLSV1_2("TLSv1.2", "TLSv1_2"),
	TLSV1_3("TLSv1.3", "TLSv1_3"),
	/** Proposta dalla console, ma non è un algoritmo di SSLContext: atteso sempre un errore */
	SSLV2HELLO("SSLv2Hello", "SSLv2Hello");

	public static final String SOGGETTO = "SoggettoInternoTest";
	public static final String API = "TestTLSDeprecato";
	public static final String API_CONNETTIVITA = "TestTLSDeprecatoConnettivita";
	public static final String AZIONE_PREFIX_URLCONN = "URLConn_";
	public static final String AZIONE_PREFIX_HTTPCORE = "HC5_";

	private final String sslType;
	private final String azione;

	TipologiaSSL(String sslType, String azione) {
		this.sslType = sslType;
		this.azione = azione;
	}

	public String getSslType() {
		return this.sslType;
	}
	public String getAzione() {
		return this.azione;
	}

	private int indice() {
		return this.ordinal()+1;
	}

	/** Connettore dell'erogazione {@link #API} (libreria non impostata) */
	public String getNomeConnettore() {
		return nomeConnettore(API, indice());
	}
	/** Connettore dell'erogazione {@link #API_CONNETTIVITA} con libreria 'java.net.HttpURLConnection' */
	public String getNomeConnettoreURLConnection() {
		return nomeConnettore(API_CONNETTIVITA, indice());
	}
	/** Connettore dell'erogazione {@link #API_CONNETTIVITA} con libreria 'org.apache.hc.client5' */
	public String getNomeConnettoreHttpCore() {
		return nomeConnettore(API_CONNETTIVITA, indice()+values().length);
	}

	private static String nomeConnettore(String api, int indice) {
		return "ConnettoreINV___gw_"+SOGGETTO+"/gw_"+api+"/v1__Specific"+indice+"_gw"+SOGGETTO;
	}
}
