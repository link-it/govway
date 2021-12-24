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

package org.openspcoop2.utils.json;

import java.util.ArrayList;
import java.util.List;

/**
 * ValidationResponse
 *
 * @author Giovanni Bussu (bussu@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ValidationResponse {

	public enum ESITO {OK, KO}
	private ESITO esito;
	private Exception exception;
	private List<String> errors;
	public ESITO getEsito() {
		return this.esito;
	}
	public void setEsito(ESITO esito) {
		this.esito = esito;
	}
	public List<String> getErrors() {
		if(this.errors == null) this.errors = new ArrayList<String>();
		return this.errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public Exception getException() {
		return this.exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
}
