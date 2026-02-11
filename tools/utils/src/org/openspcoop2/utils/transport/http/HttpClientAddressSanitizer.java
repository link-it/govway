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

package org.openspcoop2.utils.transport.http;

/**
 * HttpClientAddressSanitizer
 *
 * Sanitizza gli indirizzi IP di trasporto rimuovendo le informazioni sulla porta.
 *
 * Gestisce i formati prodotti dai seguenti header HTTP:
 *
 * 1) X-Forwarded-For / Forwarded-For / X-Forwarded / X-Client-IP / Client-IP / X-Cluster-Client-IP / Cluster-Client-IP
 *    Formato: lista di IP separati da virgola
 *    - IPv4:                192.168.1.1
 *    - IPv4 con porta:     192.168.1.1:8080
 *    - IPv6:               2001:db8::1
 *    - IPv6 con porta:     [2001:db8::1]:8080
 *    - IPv6 con brackets:  [2001:db8::1]
 *    - Multipli:           192.168.1.1:8080, 10.0.0.1:9090
 *
 * 2) Forwarded (RFC 7239)
 *    Formato: parametri chiave=valore separati da ';', entries separate da ','
 *    - for=192.0.2.43
 *    - for=192.0.2.43:47011
 *    - for="[2001:db8:cafe::17]:4711"
 *    - for="[2001:db8:cafe::17]"
 *    - for=192.0.2.60;proto=http;by=203.0.113.43
 *    - for=192.0.2.43, for=198.51.100.17
 *    Il parametro 'by' indica l'indirizzo del proxy (non del client) e viene scartato.
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HttpClientAddressSanitizer {

	private HttpClientAddressSanitizer() {}

	/**
	 * Sanitizza l'indirizzo di trasporto rimuovendo le informazioni sulla porta.
	 * Gestisce indirizzi multipli separati da virgola e il formato Forwarded (RFC 7239).
	 */
	public static String sanitizeTransportAddressPort(String transportAddress) {
		if(transportAddress==null || "".equals(transportAddress)) {
			return transportAddress;
		}
		// Gestisce indirizzi multipli separati da virgola
		// (es. "X-Forwarded-For: ip1, ip2" oppure "Forwarded: for=ip1, for=ip2")
		String[] addresses = transportAddress.split(",");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < addresses.length; i++) {
			String addr = addresses[i].trim();
			if("".equals(addr)) {
				continue;
			}
			if(result.length() > 0) {
				result.append(", ");
			}
			// Gestione formato Forwarded (RFC 7239): estrae l'IP dal parametro "for="
			// Il check evita di applicare la logica RFC 7239 a valori provenienti da altri header (X-Forwarded-For, X-Client-IP, ecc.)
			if(isForwardedHeaderFormat(addr)) {
				addr = parseForwardedHeaderValue(addr);
			}
			result.append(stripPort(addr));
		}
		return result.toString();
	}

	/*
	 * Gestisce il formato dell'header Forwarded (RFC 7239).
	 *
	 * Il valore puo' contenere parametri separati da ';' (es. "for=192.0.2.60;proto=http;by=203.0.113.43").
	 * Viene estratto il valore del parametro 'for' che contiene l'indirizzo del client.
	 * Il parametro 'by' (indirizzo del proxy) e 'proto' (protocollo) vengono scartati.
	 *
	 * Se il valore e' tra doppi apici (RFC 7239 richiede quoting per IPv6 e porte),
	 * i doppi apici vengono rimossi (es. for="[2001:db8::17]:4711" -> [2001:db8::17]:4711).
	 *
	 * Se il valore non e' in formato Forwarded (nessun parametro 'for='), viene restituito invariato.
	 */
	static String parseForwardedHeaderValue(String value) {
		String forValue = extractForParameter(value);
		if(forValue==null) {
			return value;
		}
		// Rimuove i doppi apici (RFC 7239: for="[2001:db8::17]:4711")
		if(forValue.length()>1 && forValue.startsWith("\"") && forValue.endsWith("\"")) {
			forValue = forValue.substring(1, forValue.length() - 1);
		}
		return forValue;
	}

	private static final String FORWARDED_FOR_PREFIX = "for=";
	private static final int FORWARDED_FOR_PREFIX_LENGTH = FORWARDED_FOR_PREFIX.length();

	/*
	 * Verifica se il valore ha il formato dell'header Forwarded (RFC 7239),
	 * cioe' inizia con 'for=' (case insensitive).
	 * Permette di distinguerlo dai valori provenienti da X-Forwarded-For, X-Client-IP, ecc.
	 * che contengono direttamente l'indirizzo IP senza prefissi.
	 */
	private static boolean isForwardedHeaderFormat(String value) {
		return value.length()>FORWARDED_FOR_PREFIX_LENGTH &&
				value.substring(0, FORWARDED_FOR_PREFIX_LENGTH).equalsIgnoreCase(FORWARDED_FOR_PREFIX);
	}

	/*
	 * Cerca il parametro 'for=' tra i parametri separati da ';' di un'entry dell'header Forwarded (RFC 7239).
	 * Restituisce il valore dopo 'for=' oppure null se non trovato.
	 */
	private static String extractForParameter(String value) {
		if(value.length()<=FORWARDED_FOR_PREFIX_LENGTH) {
			return null;
		}
		String[] params = value.split(";");
		for (int i = 0; i < params.length; i++) {
			String param = params[i].trim();
			if(param.length()>FORWARDED_FOR_PREFIX_LENGTH &&
					param.substring(0, FORWARDED_FOR_PREFIX_LENGTH).equalsIgnoreCase(FORWARDED_FOR_PREFIX)) {
				return param.substring(FORWARDED_FOR_PREFIX_LENGTH);
			}
		}
		return null;
	}

	/*
	 * Rimuove la porta da un singolo indirizzo IP.
	 *
	 * Logica di riconoscimento:
	 * - Inizia con '[':  IPv6 con brackets, eventualmente con porta -> [2001:db8::1]:8080 -> 2001:db8::1
	 * - Un solo ':':     IPv4 con porta (le cifre dopo ':' sono il numero di porta) -> 192.168.1.1:8080 -> 192.168.1.1
	 * - Piu' di un ':':  IPv6 senza porta (i ':' sono separatori di gruppi IPv6) -> 2001:db8::1 -> invariato
	 * - Zero ':':        indirizzo senza porta -> 192.168.1.1 -> invariato
	 */
	static String stripPort(String address) {
		if(address==null || "".equals(address)) {
			return address;
		}
		// IPv6 con brackets: [2001:db8::1]:8080 -> 2001:db8::1 ; [2001:db8::1] -> 2001:db8::1
		if(address.startsWith("[")) {
			int closeBracket = address.indexOf(']');
			if(closeBracket > 0) {
				return address.substring(1, closeBracket);
			}
			return address;
		}
		// IPv4 con porta: esattamente un ':' seguito da sole cifre
		if(isIpv4WithPort(address)) {
			return address.substring(0, address.indexOf(':'));
		}
		return address;
	}

	/*
	 * Verifica se l'indirizzo e' un IPv4 con porta (es. 192.168.1.1:8080).
	 * Un IPv4 con porta contiene esattamente un ':' e dopo il ':' ci sono solo cifre.
	 * Se ci sono piu' di un ':' si tratta di un indirizzo IPv6 (es. 2001:db8::1).
	 */
	private static boolean isIpv4WithPort(String address) {
		int colonCount = 0;
		int lastColon = -1;
		for (int i = 0; i < address.length(); i++) {
			if(address.charAt(i) == ':') {
				colonCount++;
				lastColon = i;
			}
		}
		if(colonCount != 1 || lastColon <= 0) {
			return false;
		}
		String afterColon = address.substring(lastColon + 1);
		if(afterColon.isEmpty()) {
			return false;
		}
		for (int i = 0; i < afterColon.length(); i++) {
			if(!Character.isDigit(afterColon.charAt(i))) {
				return false;
			}
		}
		return true;
	}
}
