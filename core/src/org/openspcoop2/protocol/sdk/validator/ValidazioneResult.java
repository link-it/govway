/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.protocol.sdk.validator;



/**
 * Risultato della Validazione dei Documenti
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ValidazioneResult {

	private boolean esito;
	private String messaggioErrore;
	private String messaggioWarning;	
	private Exception exception;
	
	public boolean isEsito() {
		return this.esito;
	}
	public void setEsito(boolean esito) {
		this.esito = esito;
	}
	public String getMessaggioErrore() {
		return this.messaggioErrore;
	}
	public void setMessaggioErrore(String messaggioErrore) {
		this.messaggioErrore = messaggioErrore;
	}
	public String getMessaggioWarning() {
		return this.messaggioWarning;
	}
	public void setMessaggioWarning(String messaggioWarning) {
		this.messaggioWarning = messaggioWarning;
	}
	public Exception getException() {
		return this.exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}

}
