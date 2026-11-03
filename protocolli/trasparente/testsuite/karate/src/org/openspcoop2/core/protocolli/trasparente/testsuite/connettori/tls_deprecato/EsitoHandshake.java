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
 * Esito, atteso o rilevato, di un handshake tra un connettore e una {@link VarianteServerTLS}.
 *
 * @author Andrea Poli (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EsitoHandshake {

	private final boolean ok;
	/** protocollo negoziato, se ok */
	private final String protocollo;
	/** cipher negoziata, se ok (null nell'esito atteso se non verificata) */
	private final String cipher;
	/** motivo dell'errore, atteso o rilevato */
	private final String dettaglio;

	private EsitoHandshake(boolean ok, String protocollo, String cipher, String dettaglio) {
		this.ok = ok;
		this.protocollo = protocollo;
		this.cipher = cipher;
		this.dettaglio = dettaglio;
	}

	public static EsitoHandshake ok(String protocollo, String cipher) {
		return new EsitoHandshake(true, protocollo, cipher, null);
	}
	public static EsitoHandshake ko(String dettaglio) {
		return new EsitoHandshake(false, null, null, dettaglio);
	}

	public boolean isOk() {
		return this.ok;
	}
	public String getProtocollo() {
		return this.protocollo;
	}
	public String getCipher() {
		return this.cipher;
	}
	public String getDettaglio() {
		return this.dettaglio;
	}

	/**
	 * Verifica che l'esito rilevato corrisponda a quello atteso (this): stesso esito e, se ok, stesso protocollo
	 * e, se indicata nell'atteso, stessa cipher.
	 */
	public boolean corrisponde(EsitoHandshake rilevato) {
		if(this.ok != rilevato.ok) {
			return false;
		}
		if(!this.ok) {
			return true;
		}
		if(this.protocollo!=null && !this.protocollo.equals(rilevato.protocollo)) {
			return false;
		}
		return this.cipher==null || this.cipher.equals(rilevato.cipher);
	}

	@Override
	public String toString() {
		if(this.ok) {
			return "OK " + this.protocollo + (this.cipher!=null ? " " + this.cipher : "");
		}
		return "KO" + (this.dettaglio!=null ? " (" + this.dettaglio + ")" : "");
	}
}
