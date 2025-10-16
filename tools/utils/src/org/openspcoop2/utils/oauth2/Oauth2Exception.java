/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it).
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
package org.openspcoop2.utils.oauth2;

import java.io.Serializable;

/**
 * Classe base per le eccezioni del package
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Oauth2Exception extends Exception implements Serializable{

	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private final String errorDetail;
	
	public Oauth2Exception(String errorCode, String errorDetail) {
		super();
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}
	
	public Oauth2Exception(String errorCode, String errorDetail, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}
	
	public Oauth2Exception(String errorCode, String errorDetail, Throwable t) {
		super(t);
		this.errorCode = errorCode;
		this.errorDetail = errorDetail;
	}
	
	public String getErrorCode() {
		return this.errorCode;
	}
	
	public String getErrorDetail() {
		return this.errorDetail;
	}
}
