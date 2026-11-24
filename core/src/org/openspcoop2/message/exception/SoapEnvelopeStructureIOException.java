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

import java.io.IOException;

/**
 * Anomalia della struttura dell'Envelope SOAP rilevata durante la lettura in streaming del messaggio (es. durante l'inoltro):
 * viene sollevata come IOException dallo stream che si sta leggendo, conservando come causa la {@link SoapEnvelopeStructureException}.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SoapEnvelopeStructureIOException extends IOException {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public SoapEnvelopeStructureIOException(SoapEnvelopeStructureException cause) {
		super(cause.getMessage(), cause);
	}

	public SoapEnvelopeStructureException getStructureException() {
		return (SoapEnvelopeStructureException) this.getCause();
	}
}
