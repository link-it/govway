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

package org.openspcoop2.web.lib.mvc.security;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.mail.BodyPart;

import org.apache.commons.io.FilenameUtils;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * FormDataMultipartUtils
 *
 * Utility per la lettura delle richieste 'multipart/form-data' inviate dalle form delle console
 * che contengono campi di tipo file. Il nome ed il filename di ogni parte vengono estratti
 * sempre tramite questa classe, sia dal livello di validazione (SecurityWrappedHttpServletRequest)
 * sia dagli helper che leggono i contenuti binari, in modo che entrambi classifichino le parti
 * allo stesso modo.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class FormDataMultipartUtils {

	private FormDataMultipartUtils() {}

	private static final String QUOTE = "\"";
	private static final String PREFIX_CONTENT_DISPOSITION = HttpConstants.CONTENT_DISPOSITION_FORM_DATA_NAME_PREFIX + QUOTE;
	private static final String PREFIX_FILENAME = HttpConstants.CONTENT_DISPOSITION_FILENAME_PREFIX + QUOTE;

	/** Dimensione massima di default di una richiesta multipart: 250 MB, analoga al limite di default utilizzato dalle azioni struts */
	public static final long DEFAULT_MAX_SIZE = 250L * 1024L * 1024L;

	/**
	 * Eccezione sollevata quando il body di una richiesta multipart supera la dimensione massima consentita
	 */
	public static class MaxSizeExceededException extends IOException {
		private static final long serialVersionUID = 1L;
		private final long maxSize;
		public MaxSizeExceededException(long maxSize) {
			super("La dimensione della richiesta supera il limite massimo consentito ("+formatSize(maxSize)+")");
			this.maxSize = maxSize;
		}
		public long getMaxSize() {
			return this.maxSize;
		}
	}

	/**
	 * Converte la dimensione massima indicata in configurazione (in byte o con suffisso, es. '250M'); un valore negativo indica l'assenza di limite.
	 *
	 * @param value valore da convertire
	 * @param defaultValue valore restituito se il parametro non è valorizzato
	 * @return dimensione in byte
	 */
	public static long parseMaxSize(String value, long defaultValue) {
		if(value == null || value.trim().isEmpty()) {
			return defaultValue;
		}
		long size = Utilities.convertFormatStringToBytes(value);
		return size < 0 ? -1L : size;
	}

	public static String formatSize(long size) {
		return Utilities.convertBytesToFormatString(size, true, " ");
	}

	/**
	 * Legge il contenuto dello stream verificando che non superi la dimensione massima indicata.
	 *
	 * @param is stream da leggere
	 * @param contentLength lunghezza dichiarata nella richiesta (-1 se non disponibile)
	 * @param maxSize dimensione massima consentita (valore negativo = nessun limite)
	 * @return contenuto letto
	 * @throws MaxSizeExceededException se la dimensione massima viene superata
	 */
	public static byte[] readWithLimit(InputStream is, long contentLength, long maxSize) throws IOException {
		if(maxSize >= 0 && contentLength > maxSize) {
			throw new MaxSizeExceededException(maxSize);
		}
		if(maxSize < 0) {
			return is.readAllBytes();
		}
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buf = new byte[8192];
		long letti = 0;
		int n;
		while((n = is.read(buf)) != -1) {
			letti += n;
			if(letti > maxSize) {
				throw new MaxSizeExceededException(maxSize);
			}
			bout.write(buf, 0, n);
		}
		return bout.toByteArray();
	}

	public static boolean isMultipartFormData(String contentType) {
		return contentType != null && contentType.toLowerCase(Locale.ROOT).contains(HttpConstants.CONTENT_TYPE_MULTIPART_FORM_DATA);
	}

	/**
	 * Restituisce il nome del parametro associato alla parte (attributo 'name' dell'header Content-Disposition).
	 */
	public static String getBodyPartName(BodyPart bodyPart) throws UtilsException {
		String header = getContentDisposition(bodyPart);
		if(header == null) {
			return null;
		}
		int prefixIndex = header.indexOf(PREFIX_CONTENT_DISPOSITION);
		if(prefixIndex < 0) {
			return null;
		}
		// in due parti perchè il suffisso con solo " imbrogliava il controllo
		String partName = header.substring(prefixIndex + PREFIX_CONTENT_DISPOSITION.length());
		int suffixIndex = partName.indexOf(QUOTE);
		if(suffixIndex < 0) {
			return null;
		}
		return partName.substring(0,suffixIndex);
	}

	/**
	 * Restituisce il nome del file associato alla parte (attributo 'filename' dell'header Content-Disposition),
	 * oppure null se la parte non rappresenta un file.
	 */
	public static String getBodyPartFileName(BodyPart bodyPart) throws UtilsException {
		String header = getContentDisposition(bodyPart);
		if(header == null) {
			return null;
		}
		int prefixIndex = header.indexOf(PREFIX_FILENAME);
		if(prefixIndex < 0) {
			return null;
		}
		String fileName = header.substring(prefixIndex + PREFIX_FILENAME.length());
		int suffixIndex = fileName.indexOf(QUOTE);
		if(suffixIndex < 0) {
			return null;
		}
		fileName = fileName.substring(0,suffixIndex);

		// 20220621 IE invia l'intero path invece che il solo filename, bisogna estrarlo
		return FilenameUtils.getName(fileName);
	}

	private static String getContentDisposition(BodyPart bodyPart) throws UtilsException {
		try {
			String[] headers = bodyPart.getHeader(HttpConstants.CONTENT_DISPOSITION);
			if(headers != null && headers.length > 0){
				return headers[0];
			}
			return null;
		}catch(Exception e) {
			throw new UtilsException(e.getMessage(),e);
		}
	}

	/**
	 * Estrae dal body di una richiesta 'multipart/form-data' i soli parametri testuali, cioè le parti prive
	 * dell'attributo 'filename'. Il contenuto dei file non viene restituito: viene letto come binario
	 * dagli helper e non è soggetto alla validazione prevista per i parametri testuali.
	 *
	 * @param body contenuto della richiesta
	 * @param contentType content type della richiesta (comprensivo del boundary)
	 * @return mappa nome parametro → valori, nell'ordine in cui compaiono nella richiesta
	 */
	public static Map<String, List<String>> readTextParameters(byte[] body, String contentType) throws UtilsException {
		Map<String, List<String>> map = new LinkedHashMap<>();
		if(body == null || body.length <= 0) {
			return map;
		}
		MimeMultipart mm = new MimeMultipart(new ByteArrayInputStream(body), contentType);
		for (int i = 0; i < mm.countBodyParts(); i++) {
			BodyPart bodyPart = mm.getBodyPart(i);
			String name = getBodyPartName(bodyPart);
			if(name == null || getBodyPartFileName(bodyPart) != null) {
				continue;
			}
			String value = null;
			try(InputStream is = bodyPart.getInputStream()){
				value = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			}catch(Exception e) {
				throw new UtilsException(e.getMessage(),e);
			}
			map.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
		}
		return map;
	}
}
