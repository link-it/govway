/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

/**	
 * CHttpUtilsException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class HttpUtilsException extends Exception {

	private int returnCode;
	
	public int getReturnCode() {
		return this.returnCode;
	}
	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}
	public HttpUtilsException(int returnCode, String message, Throwable cause)
	{
		super(message, cause);
		// TODO Auto-generated constructor stub
		this.returnCode = returnCode;
	}
	public HttpUtilsException(int returnCode, Throwable cause)
	{
		super(cause);
		// TODO Auto-generated constructor stub
		this.returnCode = returnCode;
	}
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public HttpUtilsException() {
		super();
	}
	public HttpUtilsException(int returnCode, String msg) {
		super(msg);
		this.returnCode = returnCode;
	}
}
