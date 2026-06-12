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

package org.openspcoop2.pdd.logger.record;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.openspcoop2.core.commons.CoreException;

/**
 * AllegatiDatoRicostruzione
 *
 * @author Poli Andrea (poli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AllegatiDatoRicostruzione extends AbstractDatoRicostruzione<List<AllegatiDatoRicostruzione.DatiAllegato>> {

	/** Prefisso che identifica il record allegati. Va confrontato SEMPRE con operazioni letterali
	 *  (startsWith/substring), mai tramite regex: contiene il metacarattere '$'. */
	public static final String PREFISSO = "$ATT$";

	/** Separatore tra i 4 campi di un allegato. */
	private static final String FIELD_SEPARATOR = ",";

	/** Separatore tra allegati. */
	private static final String ALLEGATO_SEPARATOR = ";";

	/** Sentinella per il valore null (fuori dall'alfabeto Base64). */
	private static final String NULL_VALUE = "-";

	private static final InfoDato INFO = new InfoDato(null, "Allegati traccia (record finale auto-identificato)");

	public AllegatiDatoRicostruzione(List<DatiAllegato> dati) {
		super(INFO, dati);
	}

	public AllegatiDatoRicostruzione(String token) throws CoreException {
		super(token, INFO);
	}

	/** Verifica (in modo letterale) se il token fornito e' un record allegati. */
	public static boolean isRecord(String token) {
		return token!=null && token.startsWith(PREFISSO);
	}

	@Override
	public String convertToString() {
		StringBuilder bf = new StringBuilder(PREFISSO);
		if(this.dato!=null) {
			for (int i = 0; i < this.dato.size(); i++) {
				if(i>0) {
					bf.append(ALLEGATO_SEPARATOR);
				}
				DatiAllegato a = this.dato.get(i);
				bf.append(encode(a.getContentId())).append(FIELD_SEPARATOR);
				bf.append(encode(a.getContentLocation())).append(FIELD_SEPARATOR);
				bf.append(encode(a.getContentType())).append(FIELD_SEPARATOR);
				bf.append(encode(a.getDigest()));
			}
		}
		return bf.toString();
	}

	@Override
	protected List<DatiAllegato> convertToObject(String token) throws CoreException {
		if(token==null) {
			throw new CoreException("Dato non fornito");
		}
		if(!isRecord(token)) {
			throw new CoreException("Il token ["+token+"] non e' un record allegati valido (prefisso ["+PREFISSO+"] atteso)");
		}
		List<DatiAllegato> list = new ArrayList<>();
		String body = token.substring(PREFISSO.length());
		if(!body.isEmpty()) {
			String [] allegati = body.split(ALLEGATO_SEPARATOR, -1);
			for (String allegato : allegati) {
				String [] campi = allegato.split(FIELD_SEPARATOR, -1);
				if(campi.length!=4) {
					throw new CoreException("Record allegato ["+allegato+"] con un numero di campi ("+campi.length+") diverso da quello atteso (4)");
				}
				list.add(new DatiAllegato(decode(campi[0]), decode(campi[1]), decode(campi[2]), decode(campi[3])));
			}
		}
		return list;
	}

	private static String encode(String value) {
		if(value==null) {
			return NULL_VALUE;
		}
		return Base64.getEncoder().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private static String decode(String value) {
		if(value==null || NULL_VALUE.equals(value)) {
			return null;
		}
		if(value.isEmpty()) {
			return "";
		}
		return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
	}

	/**
	 * Holder dei 4 campi di un allegato di traccia, neutro rispetto al modello dati.
	 */
	public static class DatiAllegato {

		private final String contentId;
		private final String contentLocation;
		private final String contentType;
		private final String digest;

		public DatiAllegato(String contentId, String contentLocation, String contentType, String digest) {
			this.contentId = contentId;
			this.contentLocation = contentLocation;
			this.contentType = contentType;
			this.digest = digest;
		}

		public String getContentId() {
			return this.contentId;
		}
		public String getContentLocation() {
			return this.contentLocation;
		}
		public String getContentType() {
			return this.contentType;
		}
		public String getDigest() {
			return this.digest;
		}
	}

}
