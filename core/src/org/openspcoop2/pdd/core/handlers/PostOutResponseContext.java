/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.pdd.core.handlers;

import org.slf4j.Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;


/**
 * PostOutResponseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseContext extends OutResponseContext {

	public PostOutResponseContext(Logger logger,IProtocolFactory protocolFactory){
		super(logger,protocolFactory);
	}
	
	/** Esito */
	private EsitoTransazione esito;
	
	/** ReturnCode */
	private int returnCode;
	
	/** Eventuale errore di Consegna */
	private String erroreConsegna;
	
	/** Dimensione dei messaggi scambiati */
	private Long inputRequestMessageSize;
	private Long outputRequestMessageSize;
	private Long inputResponseMessageSize;
	private Long outputResponseMessageSize;
	
	public EsitoTransazione getEsito() {
		return this.esito;
	}

	public void setEsito(EsitoTransazione esito) {
		this.esito = esito;
	}

	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getErroreConsegna() {
		return this.erroreConsegna;
	}

	public void setErroreConsegna(String erroreConsegna) {
		this.erroreConsegna = erroreConsegna;
	}

	public Long getInputRequestMessageSize() {
		return this.inputRequestMessageSize;
	}

	public void setInputRequestMessageSize(Long inputRequestMessageSize) {
		if(inputRequestMessageSize!=null){
			if(inputRequestMessageSize>0){
				this.inputRequestMessageSize = inputRequestMessageSize;
			}
		}
	}

	public Long getOutputRequestMessageSize() {
		return this.outputRequestMessageSize;
	}

	public void setOutputRequestMessageSize(Long outputRequestMessageSize) {
		if(outputRequestMessageSize!=null){
			if(outputRequestMessageSize>0){
				this.outputRequestMessageSize = outputRequestMessageSize;
			}
		}
	}

	public Long getInputResponseMessageSize() {
		return this.inputResponseMessageSize;
	}

	public void setInputResponseMessageSize(Long inputResponseMessageSize) {
		if(inputResponseMessageSize!=null){
			if(inputResponseMessageSize>0){
				this.inputResponseMessageSize = inputResponseMessageSize;
			}
		}
	}

	public Long getOutputResponseMessageSize() {
		return this.outputResponseMessageSize;
	}

	public void setOutputResponseMessageSize(Long outputResponseMessageSize) {
		if(outputResponseMessageSize!=null){
			if(outputResponseMessageSize>0){
				this.outputResponseMessageSize = outputResponseMessageSize;
			}
		}
	}
}
