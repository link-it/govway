/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import java.io.Serializable;

import org.openspcoop2.message.constants.MessageRole;

/**
 * ParseException
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ParseException implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private MessageRole messageRole;
	private Throwable sourceException;
	private Throwable parseException;
	
	public MessageRole getMessageRole() {
		return this.messageRole;
	}
	
	public Throwable getSourceException() {
		return this.sourceException;
	}
	public void setSourceException(Throwable sourceException, MessageRole messageRole) {
		this.sourceException = sourceException;
		this.messageRole = messageRole;
	}
	public Throwable getParseException() {
		return this.parseException;
	}
	public void setParseException(Throwable parseException, MessageRole messageRole) {
		this.parseException = parseException;
		this.messageRole = messageRole;
	}
}
