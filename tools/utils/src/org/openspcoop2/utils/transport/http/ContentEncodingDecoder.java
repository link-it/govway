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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.openspcoop2.utils.UtilsException;

/**
 * Utility centralizzata per la gestione dell'header HTTP 'Content-Encoding' lato decompressione
 * automatica del body, condivisa tra:
 * <ul>
 *   <li>connettori in uscita di GovWay (lato response del backend),</li>
 *   <li>HttpUtilities (UrlConnectionConnection + HttpCoreConnection),</li>
 *   <li>moduli server-side di GovWay (lato request in ingresso dal client).</li>
 * </ul>
 *
 * <h2>Encoding gestiti</h2>
 * Allineata al default di Apache HttpClient 5 (ContentCompressionExec):
 * <ul>
 *   <li>gzip (RFC 1952)</li>
 *   <li>x-gzip (alias storico di gzip)</li>
 *   <li>deflate con autodetect zlib-wrapped (RFC 1950) / raw (RFC 1951)</li>
 * </ul>
 * Encoding <em>non</em> gestiti (richiederebbero dipendenze esterne dedicate, idem Apache HC5):
 * br (Brotli), zstd, compress. Quando il chiamante richiede la decompressione e ne incontra uno,
 * {@link #decode(InputStream, String)} solleva {@link UtilsException} con il messaggio
 * "Unsupported Content-Encoding: ...", coerente con il comportamento di Apache HC5.
 *
 * <h2>Cascade close</h2>
 * Gli stream restituiti dai vari wrap propagano correttamente {@code close()} fino allo stream
 * sorgente: GZIPInputStream/InflaterInputStream ereditano da FilterInputStream e invocano
 * {@code in.close()}; il SequenceInputStream usato per la variante deflate raw chiude entrambi
 * gli stream sorgente (ByteArrayInputStream del byte peekato e socket stream originale).
 *
 * @author Poli Andrea (apoli@link.it)
 *
 */
public class ContentEncodingDecoder {

	private ContentEncodingDecoder() {
		// utility class
	}

	/**
	 * Lista (CSV human-readable) degli encoding gestiti dalla decompressione automatica.
	 * Usata nei diagnostici e nei messaggi di errore per dichiarare al chiamante quali encoding
	 * GovWay sa gestire quando rifiuta un Content-Encoding non supportato. Single source of
	 * truth: per aggiungere il supporto a br/zstd/compress basta aggiornare questa costante
	 * (oltre a implementare il decoder corrispondente in {@link #decode(InputStream, String)}).
	 */
	public static final String SUPPORTED_DECOMPRESS_LIST =
			HttpConstants.CONTENT_ENCODING_VALUE_GZIP + ", " +
			HttpConstants.CONTENT_ENCODING_VALUE_X_GZIP + ", " +
			HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE;

	/**
	 * Verifica se il valore di Content-Encoding e' gestito dalla decompressione automatica.
	 * Match case-insensitive. I valori {@code null}, vuoto e {@code identity} sono considerati
	 * "nessuna decompressione necessaria" e ritornano false (non c'e' nulla da decomprimere).
	 *
	 * @param contentEncoding valore dell'header HTTP 'Content-Encoding'
	 * @return true se gzip/x-gzip/deflate; false altrimenti
	 */
	public static boolean isSupported(String contentEncoding) {
		if (contentEncoding == null) {
			return false;
		}
		String ce = contentEncoding.trim().toLowerCase();
		if (ce.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equals(ce)) {
			return false;
		}
		return HttpConstants.CONTENT_ENCODING_VALUE_GZIP.equals(ce)
				|| HttpConstants.CONTENT_ENCODING_VALUE_X_GZIP.equals(ce)
				|| HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE.equals(ce);
	}

	/**
	 * Verifica se la decompressione deve essere effettivamente applicata, dato un flag di
	 * abilitazione e il valore di Content-Encoding ricevuto. Ritorna true solo se il flag e'
	 * abilitato e l'encoding e' uno di quelli gestiti.
	 *
	 * @param decompressEnabled flag opt-in lato chiamante
	 * @param contentEncoding valore dell'header 'Content-Encoding' nel body
	 * @return true se occorre decomprimere
	 */
	public static boolean isDecompressionApplicable(boolean decompressEnabled, String contentEncoding) {
		return decompressEnabled && isSupported(contentEncoding);
	}

	/**
	 * Restituisce uno stream decomprimente che wrappa lo stream sorgente in base all'encoding.
	 * <ul>
	 *   <li>{@code gzip} / {@code x-gzip} → {@link GZIPInputStream}</li>
	 *   <li>{@code deflate} → {@link InflaterInputStream} con autodetect zlib/raw</li>
	 *   <li>{@code identity}, null, vuoto → ritorna {@code in} invariato (no-op)</li>
	 *   <li>Encoding non gestito (br, zstd, compress, ...) → {@link UtilsException}</li>
	 * </ul>
	 *
	 * Il chiamante e' responsabile della close: chiudere lo stream restituito propaga la close
	 * fino allo stream sorgente (vedi documentazione di classe).
	 *
	 * @param in stream sorgente raw (es. da socket/pipe)
	 * @param contentEncoding valore dell'header HTTP 'Content-Encoding' del body
	 * @return stream da cui leggere il body decompresso (o {@code in} se nessuna decompressione)
	 * @throws UtilsException se l'encoding e' non null/identity e non e' gestito
	 * @throws IOException se la lettura iniziale dello stream fallisce
	 */
	public static InputStream decode(InputStream in, String contentEncoding) throws UtilsException, IOException {
		if (in == null || contentEncoding == null) {
			return in;
		}
		String ce = contentEncoding.trim().toLowerCase();
		if (ce.isEmpty() || HttpConstants.CONTENT_ENCODING_VALUE_IDENTITY.equals(ce)) {
			return in;
		}
		if (HttpConstants.CONTENT_ENCODING_VALUE_GZIP.equals(ce)
				|| HttpConstants.CONTENT_ENCODING_VALUE_X_GZIP.equals(ce)) {
			return new GZIPInputStream(in);
		}
		if (HttpConstants.CONTENT_ENCODING_VALUE_DEFLATE.equals(ce)) {
			return wrapDeflate(in);
		}
		throw new UtilsException("Unsupported Content-Encoding: " + contentEncoding);
	}

	/**
	 * Wrap di uno stream 'deflate' con autodetect tra zlib-wrapped (RFC 1950) e raw (RFC 1951).
	 * Apache HttpClient 5 fa lo stesso fallback perche' molti server interpretano in modo
	 * incoerente l'encoding 'deflate'. L'autodetect si basa sul primo byte: 0x78 e' il valore
	 * standard del CMF zlib (CINFO=7, CM=8) e copre il 99% dei casi reali di output prodotto
	 * da java.util.zip.DeflaterOutputStream. Tutti gli altri valori vengono trattati come raw
	 * deflate (Inflater con nowrap=true).
	 * <p>
	 * Il byte peekato viene ricomposto in testa allo stream tramite SequenceInputStream
	 * (convenzione del progetto, cfr. Utilities/MultipartRelatedHeuristics) anziche'
	 * PushbackInputStream.
	 */
	private static InputStream wrapDeflate(InputStream in) throws IOException {
		int b0 = in.read();
		if (b0 < 0) {
			// stream vuoto: lascia che sia il chiamante a gestire l'EOF
			return in;
		}
		InputStream reconstructed = new SequenceInputStream(new ByteArrayInputStream(new byte[] { (byte) b0 }), in);
		if (b0 == 0x78) {
			return new InflaterInputStream(reconstructed);
		}
		return new InflaterInputStream(reconstructed, new Inflater(true));
	}
}
