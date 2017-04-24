/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2017 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.autorizzazione;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * Esito di un processo di autorizzazione.
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public abstract class EsitoAutorizzazione implements java.io.Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Indicazione se il e' autorizzato */
	private boolean autorizzato;
	
	/** Dettagli aggiuntivi */
	private String details;
		
	private Exception eccezioneProcessamento;
	
	private boolean noCache = false;
		
	/**
	 * Ritorna l'indicazione se e' autorizzato
	 * 
	 * @return indicazione se e' autorizzato
	 */
	public boolean isAutorizzato() {
		return this.autorizzato;
	}
	
	/**
	 * Imposta l'indicazione se il servizio e' autorizzato
	 * 
	 * @param servizioAutorizzato indicazione se il servizio e' autorizzato
	 */
	public void setAutorizzato(boolean servizioAutorizzato) {
		this.autorizzato = servizioAutorizzato;
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
	
	public String getHeader(){
		if(this.autorizzato){
			return "AUTORIZZATO";
		}
		else{
			return "NON_AUTORIZZATO";
		}
	}
	
	public boolean isNoCache() {
		return this.noCache;
	}

	public void setNoCache(boolean noCache) {
		this.noCache = noCache;
	}
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer(this.getHeader());
		
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
