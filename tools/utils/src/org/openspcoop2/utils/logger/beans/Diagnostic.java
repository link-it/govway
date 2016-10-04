/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it).
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
package org.openspcoop2.utils.logger.beans;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.utils.logger.constants.Severity;

/**
 * Diagnostic
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Diagnostic implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date date;
	private String function;
	private Severity severity;
	private String message;
	private String idTransaction;
	private String code;
	
	public Date getDate() {
		return this.date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFunction() {
		return this.function;
	}
	public void setFunction(String function) {
		this.function = function;
	}
	public Severity getSeverity() {
		return this.severity;
	}
	public void setSeverity(Severity severity) {
		this.severity = severity;
	}
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getIdTransaction() {
		return this.idTransaction;
	}
	public void setIdTransaction(String idTransaction) {
		this.idTransaction = idTransaction;
	}
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}
