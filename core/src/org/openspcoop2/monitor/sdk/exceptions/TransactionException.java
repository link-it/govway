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
package org.openspcoop2.monitor.sdk.exceptions;

import org.openspcoop2.monitor.sdk.constants.TransactionExceptionCode;

/**
 * TransactionException
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionException extends Exception {
	private TransactionExceptionCode CODE;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	

	public TransactionException(TransactionExceptionCode code,String msg) {
       this(code, msg, null);
    }

	public TransactionException(TransactionExceptionCode code) {
		this(code, code.getMessage(), null);
	}
	
	public TransactionException(TransactionExceptionCode code,String msg, Throwable cause) {
        super("["+code.getMessage()+"] "+msg,cause);
        this.CODE = code;
    }

	public TransactionException(TransactionExceptionCode code, Throwable cause) {
		super(code.getMessage(),cause);
		this.CODE = code;
	}
	
	public TransactionExceptionCode getCode() {
		return this.CODE;
	}
}
