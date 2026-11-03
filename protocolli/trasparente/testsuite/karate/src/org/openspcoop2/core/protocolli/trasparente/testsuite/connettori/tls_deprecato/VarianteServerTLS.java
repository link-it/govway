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
 * Configurazioni TLS esposte, una alla volta, dal backend {@link TLSDeprecatoMockServer}.
 *
 * Ogni variante abilita un solo protocollo e una sola cipher suite, valida per quel protocollo e per il tipo
 * di certificato presentato; fa eccezione {@link #TLS_TUTTI_DEFAULT}, che abilita tutti i protocolli TLS con le
 * cipher di default, per verificare che la tipologia del connettore limiti la versione massima negoziata.
 *
 * La classe viene usata anche dal processo del mock: deve dipendere solo dal JDK.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum VarianteServerTLS {

	/** Nessuna tipologia abilita SSLv3 lato client: atteso sempre un errore */
	SSLV3_RSA_AES128_CBC(Certificato.RSA, "TLS_RSA_WITH_AES_128_CBC_SHA", "SSLv3"),

	/** Configurazione del backend segnalato (nginx con OpenSSL 1.1.0, solo TLSv1) */
	TLSV1_ECDHE_RSA_AES256_CBC(Certificato.RSA, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA", "TLSv1"),
	/** Scambio chiavi RSA statico: disabilitato ('TLS_RSA_*') a partire da JDK 24 e negli aggiornamenti recenti di JDK 21 */
	TLSV1_RSA_AES128_CBC(Certificato.RSA, "TLS_RSA_WITH_AES_128_CBC_SHA", "TLSv1"),

	TLSV1_1_ECDHE_RSA_AES128_CBC(Certificato.RSA, "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA", "TLSv1.1"),
	TLSV1_1_ECDHE_ECDSA_AES256_CBC(Certificato.EC, "TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA", "TLSv1.1"),

	TLSV1_2_ECDHE_RSA_AES128_GCM(Certificato.RSA, "TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256", "TLSv1.2"),
	TLSV1_2_ECDHE_ECDSA_AES256_GCM(Certificato.EC, "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384", "TLSv1.2"),
	TLSV1_2_DHE_RSA_AES256_GCM(Certificato.RSA, "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384", "TLSv1.2"),
	TLSV1_2_ECDHE_RSA_CHACHA20(Certificato.RSA, "TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256", "TLSv1.2"),
	TLSV1_2_ECDHE_RSA_AES256_CBC(Certificato.RSA, "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384", "TLSv1.2"),
	TLSV1_2_RSA_AES256_GCM(Certificato.RSA, "TLS_RSA_WITH_AES_256_GCM_SHA384", "TLSv1.2"),
	/** Cipher disabilitata ('3DES_EDE_CBC') nella JVM di GovWay: atteso sempre un errore */
	TLSV1_2_ECDHE_RSA_3DES(Certificato.RSA, "TLS_ECDHE_RSA_WITH_3DES_EDE_CBC_SHA", "TLSv1.2"),

	TLSV1_3_AES128_GCM(Certificato.RSA, "TLS_AES_128_GCM_SHA256", "TLSv1.3"),
	TLSV1_3_AES256_GCM_EC(Certificato.EC, "TLS_AES_256_GCM_SHA384", "TLSv1.3"),
	TLSV1_3_CHACHA20(Certificato.RSA, "TLS_CHACHA20_POLY1305_SHA256", "TLSv1.3"),

	/** Tutti i protocolli TLS con le cipher di default: viene negoziata la versione più alta abilitata dalla tipologia */
	TLS_TUTTI_DEFAULT(Certificato.RSA, null, "TLSv1.3", "TLSv1.2", "TLSv1.1", "TLSv1");

	public enum Certificato { RSA, EC }

	private final Certificato certificato;
	private final String cipherSuite;
	private final String [] protocolli;

	VarianteServerTLS(Certificato certificato, String cipherSuite, String ... protocolli) {
		this.certificato = certificato;
		this.cipherSuite = cipherSuite;
		this.protocolli = protocolli;
	}

	public Certificato getCertificato() {
		return this.certificato;
	}
	/** null se vengono usate le cipher di default */
	public String getCipherSuite() {
		return this.cipherSuite;
	}
	public String[] getProtocolli() {
		return this.protocolli.clone();
	}

	public String descrizione() {
		return String.join("+", this.protocolli) + " " + (this.cipherSuite!=null ? this.cipherSuite : "(cipher di default)") + " cert:" + this.certificato;
	}
}
