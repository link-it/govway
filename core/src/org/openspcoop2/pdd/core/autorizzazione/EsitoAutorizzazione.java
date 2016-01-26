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

	/** Indicazione se il servizio e' autorizzato */
	private boolean servizioAutorizzato;
	
	/** Dettagli aggiuntivi */
	private String details;
		
	private Exception eccezioneProcessamento;
	
	private boolean noCache = false;
		
	/**
	 * Ritorna l'indicazione se il servizio e' autorizzato
	 * 
	 * @return indicazione se il servizio e' autorizzato
	 */
	public boolean isServizioAutorizzato() {
		return this.servizioAutorizzato;
	}
	
	/**
	 * Imposta l'indicazione se il servizio e' autorizzato
	 * 
	 * @param servizioAutorizzato indicazione se il servizio e' autorizzato
	 */
	public void setServizioAutorizzato(boolean servizioAutorizzato) {
		this.servizioAutorizzato = servizioAutorizzato;
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
	}
	
	public String getHeader(){
		if(this.servizioAutorizzato){
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
