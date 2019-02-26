/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.token;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.openspcoop2.message.OpenSPCoop2Message;


/**
 * Esito di un processo di gestione token.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoToken implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String token;
	
	/** Dettagli aggiuntivi */
	private String details;
		
	private Exception eccezioneProcessamento;
	
	private boolean noCache = false;
	
	private OpenSPCoop2Message errorMessage;
	private String wwwAuthenticateErrorHeader;
	
	
	public String getWwwAuthenticateErrorHeader() {
		return this.wwwAuthenticateErrorHeader;
	}
	public void setWwwAuthenticateErrorHeader(String wwwAuthenticateErrorHeader) {
		this.wwwAuthenticateErrorHeader = wwwAuthenticateErrorHeader;
	}
	public OpenSPCoop2Message getErrorMessage() {
		return this.errorMessage;
	}
	public void setErrorMessage(OpenSPCoop2Message errorMessage) {
		this.errorMessage = errorMessage;
	}
	public String getToken() {
		return this.token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getDetails() {
		return this.details;
	}
	public void setDetails(String details) {
		this.details = details;
	}
	
	public Exception getEccezioneProcessamento() {
		return this.eccezioneProcessamento;
	}
	public void setEccezioneProcessamento(Exception eccezioneProcessamento) {
		this.eccezioneProcessamento = eccezioneProcessamento;
		this.noCache = true; // per default quando si imposta una eccezione di processamento il risultato non sarà salvato. Se si vuole cacharlo richiamare il metodo setNoCache(false);
	}
	
	public boolean isNoCache() {
		return this.noCache;
	}

	public void setNoCache(boolean noCache) {
		this.noCache = noCache;
	}
	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		if(this.details!=null){
			bf.append(" ");
			bf.append("details["+this.details+"]");
		}
		
		if(this.eccezioneProcessamento!=null){
			bf.append(" ");
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(bout);
			try{
				this.eccezioneProcessamento.printStackTrace(ps);
			}finally{
				try{
					ps.flush();
					ps.close();
					bout.flush();
					bout.close();
				}catch(Exception eClose){}
			}
			bf.append("stackTraceEccezioneProcessamento: \n"+bout.toString());
		}
		return bf.toString();
	}
}
