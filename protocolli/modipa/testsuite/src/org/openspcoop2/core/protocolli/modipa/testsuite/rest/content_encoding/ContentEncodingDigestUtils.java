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

package org.openspcoop2.core.protocolli.modipa.testsuite.rest.content_encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Costanti e utilità per i test del Digest ModI su contenuti compressi.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ContentEncodingDigestUtils {

	private ContentEncodingDigestUtils() {}

	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
	public static final String HEADER_CONTENT_LENGTH = "Content-Length";
	public static final String HEADER_DIGEST = "Digest";
	public static final String HEADER_INTEGRITY = "Agid-JWT-Signature";

	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String GZIP = "gzip";

	/** Offset e lunghezza del campo MTIME nell'header gzip (RFC 1952). */
	private static final int GZIP_MTIME_OFFSET = 4;
	private static final int GZIP_MTIME_LENGTH = 4;

	/**
	 * Payload JSON di test. La ripetizione lo rende comprimibile, in modo che la versione gzip sia
	 * significativamente diversa (e più piccola) del contenuto in chiaro.
	 */
	public static byte[] jsonPayload(String label) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"label\":\"").append(label).append("\",\"items\":[");
		for (int i = 0; i < 200; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append("{\"id\":").append(i).append(",\"descrizione\":\"elemento di test per il digest su contenuto compresso\"}");
		}
		sb.append("]}");
		return sb.toString().getBytes(StandardCharsets.UTF_8);
	}

	public static byte[] gzip(byte[] content) {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try (GZIPOutputStream gz = new GZIPOutputStream(bout)) {
			gz.write(content);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return bout.toByteArray();
	}

	public static byte[] gunzip(byte[] content) {
		try (GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(content))) {
			return gz.readAllBytes();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public static boolean isGzip(byte[] content) {
		return content != null && content.length > 10 && (content[0] & 0xff) == 0x1f && (content[1] & 0xff) == 0x8b;
	}

	/**
	 * Restituisce una copia dello stream gzip con il campo MTIME dell'header modificato.
	 * Lo stream resta valido e il contenuto decompresso è identico (il CRC copre solo i dati),
	 * mentre i byte compressi sono sicuramente diversi: un Digest calcolato sui byte compressi originali
	 * non corrisponde più, uno calcolato (erroneamente) sul contenuto in chiaro continuerebbe a corrispondere.
	 */
	public static byte[] alterGzipMtime(byte[] content) {
		if (!isGzip(content)) {
			throw new IllegalArgumentException("Content is not a gzip stream");
		}
		byte[] altered = content.clone();
		for (int i = 0; i < GZIP_MTIME_LENGTH; i++) {
			altered[GZIP_MTIME_OFFSET + i] = (byte) (altered[GZIP_MTIME_OFFSET + i] ^ 0x5a);
		}
		return altered;
	}
}
