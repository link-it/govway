/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.autenticazione;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.openspcoop2.message.OpenSPCoop2Message;


/**
 * Esito di un processo di autorizzazione.
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoAutenticazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Indicazione se il client e' autenticato */
	private boolean clientAuthenticated = false;
	
	/** Credenziale */
	private String credential; // id autenticato
	private String fullCredential; // descrizione estesa con nome e valore
	private boolean enrichPrincipal = false; // serve ad indicare che la credenziale e' un principal

	/** Indicazione se il client e' identificato */
	private boolean clientIdentified = false;
	
	/** Dettagli aggiuntivi */
	private String details;
		
	private Exception eccezioneProcessamento;
	
	private boolean esitoPresenteInCache = false;
	
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
	
	public String getCredential() {
		return this.credential;
	}
	public void setCredential(String credential) {
		this.credential = credential;
	}
	
	public String getFullCredential() {
		return this.fullCredential;
	}
	public void setFullCredential(String fullCredential) {
		this.fullCredential = fullCredential;
	}
	
	public boolean isEnrichPrincipal() {
		return this.enrichPrincipal;
	}
	public void setEnrichPrincipal(boolean enrichPrincipal) {
		this.enrichPrincipal = enrichPrincipal;
	}
	
	public boolean isClientAuthenticated() {
		return this.clientAuthenticated;
	}

	public void setClientAuthenticated(boolean clientAuthenticated) {
		this.clientAuthenticated = clientAuthenticated;
	}
	
	public boolean isClientIdentified() {
		return this.clientIdentified;
	}

	public void setClientIdentified(boolean clientIdentified) {
		this.clientIdentified = clientIdentified;
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
	
	protected String getHeader(){
		if(this.clientIdentified){
			return "AUTENTICATO";
		}
		else{
			return "NON_AUTENTICATO";
		}
	}
	
	public boolean isEsitoPresenteInCache() {
		return this.esitoPresenteInCache;
	}
	public void setEsitoPresenteInCache(boolean esitoPresenteInCache) {
		this.esitoPresenteInCache = esitoPresenteInCache;
	}
	
	public boolean isNoCache() {
		return this.noCache;
	}

	public void setNoCache(boolean noCache) {
		this.noCache = noCache;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder(this.getHeader());
		
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
