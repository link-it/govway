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

package org.openspcoop2.message.exception;

/**
 * Eccezione sollevata quando l'Envelope SOAP, pur essendo XML ben formato, non rispetta la struttura prevista
 * (più Header o Body, ordine errato, Fault multipli o non unici, elementi non previsti come figli dell'Envelope).
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureException extends MessageException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public static final String PREFIX = "Invalid SOAP Envelope: ";

	public SoapEnvelopeStructureException(String msg) {
		super(PREFIX + msg);
	}
}
