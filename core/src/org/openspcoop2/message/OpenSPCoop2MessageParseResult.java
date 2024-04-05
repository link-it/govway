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

package org.openspcoop2.message;

import org.openspcoop2.message.exception.ParseException;

/**
 * OpenSPCoop2MessageParseResult
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OpenSPCoop2MessageParseResult {

	private OpenSPCoop2Message message;
	private ParseException parseException;

	public OpenSPCoop2Message getMessage_throwParseThrowable() throws Throwable {
		if(this.parseException!=null){
			throw this.parseException.getSourceException();		
		}
		return this.message;
	}
	public OpenSPCoop2Message getMessage_throwParseException() throws Exception {
		if(this.parseException!=null){
			if(this.parseException.getSourceException() instanceof Exception){
				throw (Exception) this.parseException.getSourceException();
			}else if(this.parseException.getSourceException() instanceof RuntimeException){
				throw (RuntimeException) this.parseException.getSourceException();
			}
			else{
				throw new RuntimeException(this.parseException.getSourceException());
			}
				
		}
		return this.message;
	}
	
	public OpenSPCoop2Message getMessage() {
		return this.message;
	}
	public void setMessage(OpenSPCoop2Message message) {
		this.message = message;
	}
	public ParseException getParseException() {
		return this.parseException;
	}
	public void setParseException(ParseException parseException) {
		this.parseException = parseException;
	}
}
