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



package org.openspcoop2.pdd.core.integrazione;

import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;

/**
 * Classe utilizzata per rappresentare le informazioni della busta inserite in una header di integrazione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderIntegrazioneBusta implements java.io.Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** Tipo del Mittente. */
    private String tipoMittente;
    /** Mittente. */
    private String mittente;

    /** Tipo del Destinatario (Destinatario). */
    private String tipoDestinatario;
    /** Destinatario (Destinatario). */
    private String destinatario;
    
    /** Servizio. */
    private String servizio;
    /** Tipo di Servizio. */
    private String tipoServizio; 

    /** Azione. */
    private String azione;
    
    /** Identificativo Messaggio. */
    private String ID;
    /** Riferimento Messaggio. */
    private String riferimentoMessaggio;
	/** ID Collaborazione */
	private String idCollaborazione;
    
	/** Profilo di Collaborazione */
	private ProfiloDiCollaborazione profiloDiCollaborazione;
    
    /**
	 * Imposta il tipo del Mittente.
	 *
	 * @param type tipo del Mittente.
	 * 
	 */
	public void setTipoMittente(String type ){
		this.tipoMittente = type;
	}

	/**
	 * Imposta il Mittente.
	 *
	 * @param m Mittente.
	 * 
	 */
	public void setMittente(String m ){
		this.mittente = m;
	}


	/**
	 * Imposta il tipo del Destinatario (Destinatario).
	 *
	 * @param type tipo del Destinatario.
	 * 
	 */
	public void setTipoDestinatario(String type ){
		this.tipoDestinatario = type;
	}
	
	/**
	 * Imposta il Destinatario (Destinatario).
	 *
	 * @param s Destinatario.
	 * 
	 */
	public void setDestinatario(String s ){
		this.destinatario = s;
	}
	
	/**
	 * Imposta il Servizio.
	 *
	 * @param s Servizio.
	 * 
	 */
	public void setServizio(String s ){
		this.servizio = s;
	}
	/**
	 * Imposta il tipo del Servizio.
	 *
	 * @param type tipo del Servizio.
	 * 
	 */
	public void setTipoServizio(String type ){
		this.tipoServizio = type;
	}

	/**
	 * Imposta l'Azione.
	 *
	 * @param a Azione.
	 * 
	 */
	public void setAzione(String a ){
		this.azione = a;
	}
	
	/**
	 * Imposta l'identificativo del messaggio.
	 *
	 * @param id identificativo del messaggio.
	 * 
	 */
	public void setID(String id ){
		this.ID = id;
	}

	/**
	 * Imposta il Riferimento Messaggio.
	 *
	 * @param rif riferimento messaggio.
	 * 
	 */
	public void setRiferimentoMessaggio(String rif ){
		this.riferimentoMessaggio = rif;
	} 
	

	/**
	 * @param idCollaborazione the idCollaborazione to set
	 */
	public void setIdCollaborazione(String idCollaborazione) {
		this.idCollaborazione = idCollaborazione;
	}
	
	/**
	 * Ritornaa il tipo del Mittente.
	 *
	 * @return tipo del Mittente.
	 * 
	 */
	public String getTipoMittente(){
		return this.tipoMittente;
	}

	/**
	 * Ritorna il Mittente.
	 *
	 * @return Mittente.
	 * 
	 */
	public String getMittente(){
		return this.mittente;
	}


	/**
	 * Ritorna il tipo del Destinatario (Destinatario).
	 *
	 * @return tipo del Destinatario.
	 * 
	 */
	public String getTipoDestinatario(){
		return this.tipoDestinatario;
	}

	/**
	 * Ritorna il Destinatario (Destinatario).
	 *
	 * @return Destinatario.
	 * 
	 */
	public String getDestinatario(){
		return this.destinatario;
	}
	
	/**
	 * Ritorna il Servizio.
	 *
	 * @return Servizio.
	 * 
	 */
	public String getServizio(){
		return this.servizio;
	}
	/**
	 * Ritorna il tipo del Servizio.
	 *
	 * @return tipo del Servizio.
	 * 
	 */
	public String getTipoServizio(){
		return this.tipoServizio;
	}


	/**
	 * Ritorna l'Azione.
	 *
	 * @return Azione.
	 * 
	 */
	public String getAzione(){
		return this.azione;
	}


	/**
	 * Ritorna l'identificativo del messaggio.
	 *
	 * @return identificativo del messaggio.
	 * 
	 */
	public String getID(){
		return this.ID;
	}

	/**
	 * Ritorna il Riferimento Messaggio.
	 *
	 * @return riferimento messaggio.
	 * 
	 */
	public String getRiferimentoMessaggio(){
		return this.riferimentoMessaggio;
	} 
	
	/**
	 * @return the idCollaborazione
	 */
	public String getIdCollaborazione() {
		return this.idCollaborazione;
	}

	/**
	 * @return the profiloDiCollaborazione
	 */
	public ProfiloDiCollaborazione getProfiloDiCollaborazione() {
		return this.profiloDiCollaborazione;
	}

	/**
	 * @param profiloDiCollaborazione the profiloDiCollaborazione to set
	 */
	public void setProfiloDiCollaborazione(ProfiloDiCollaborazione profiloDiCollaborazione) {
		this.profiloDiCollaborazione = profiloDiCollaborazione;
	}
}
