/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.protocol.engine;


/**
 * Classe utilizzata per rappresentare le informazioni che interessa leggere di una busta
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */


public class LetturaParametriBusta implements java.io.Serializable{
   
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

    /* ********  F I E L D S  P R I V A T I  ******** */

	private boolean mittente;
	private boolean destinatario;
	private boolean indirizziTelematici;
	private boolean servizio;
	private boolean azione;
	private boolean profiloDiCollaborazione;
	private boolean servizioCorrelato;
	private boolean collaborazione;
	private boolean sequenza;
	private boolean profiloTrasmissione;
	private boolean oraRegistrazione;
	private boolean riferimentoMessaggio;
	private boolean scadenza;
	/**
	 * @return the mittente
	 */
	public boolean isMittente() {
		return this.mittente;
	}
	/**
	 * @param mittente the mittente to set
	 */
	public void setMittente(boolean mittente) {
		this.mittente = mittente;
	}
	/**
	 * @return the destinatario
	 */
	public boolean isDestinatario() {
		return this.destinatario;
	}
	/**
	 * @param destinatario the destinatario to set
	 */
	public void setDestinatario(boolean destinatario) {
		this.destinatario = destinatario;
	}
	/**
	 * @return the readIndirizziTelematici
	 */
	public boolean isIndirizziTelematici() {
		return this.indirizziTelematici;
	}
	/**
	 * @param readIndirizziTelematici the readIndirizziTelematici to set
	 */
	public void setIndirizziTelematici(boolean readIndirizziTelematici) {
		this.indirizziTelematici = readIndirizziTelematici;
	}
	/**
	 * @return the servizio
	 */
	public boolean isServizio() {
		return this.servizio;
	}
	/**
	 * @param servizio the servizio to set
	 */
	public void setServizio(boolean servizio) {
		this.servizio = servizio;
	}
	/**
	 * @return the azione
	 */
	public boolean isAzione() {
		return this.azione;
	}
	/**
	 * @param azione the azione to set
	 */
	public void setAzione(boolean azione) {
		this.azione = azione;
	}
	/**
	 * @return the profiloDiCollaborazione
	 */
	public boolean isProfiloDiCollaborazione() {
		return this.profiloDiCollaborazione;
	}
	/**
	 * @param profiloDiCollaborazione the profiloDiCollaborazione to set
	 */
	public void setProfiloDiCollaborazione(boolean profiloDiCollaborazione) {
		this.profiloDiCollaborazione = profiloDiCollaborazione;
	}
	/**
	 * @return the collaborazione
	 */
	public boolean isCollaborazione() {
		return this.collaborazione;
	}
	/**
	 * @param collaborazione the collaborazione to set
	 */
	public void setCollaborazione(boolean collaborazione) {
		this.collaborazione = collaborazione;
	}
	/**
	 * @return the sequenza
	 */
	public boolean isSequenza() {
		return this.sequenza;
	}
	/**
	 * @param sequenza the sequenza to set
	 */
	public void setSequenza(boolean sequenza) {
		this.sequenza = sequenza;
	}
	/**
	 * @return the profiloTrasmissione
	 */
	public boolean isProfiloTrasmissione() {
		return this.profiloTrasmissione;
	}
	/**
	 * @param profiloTrasmissione the profiloTrasmissione to set
	 */
	public void setProfiloTrasmissione(boolean profiloTrasmissione) {
		this.profiloTrasmissione = profiloTrasmissione;
	}
	/**
	 * @return the oraRegistrazione
	 */
	public boolean isOraRegistrazione() {
		return this.oraRegistrazione;
	}
	/**
	 * @param oraRegistrazione the oraRegistrazione to set
	 */
	public void setOraRegistrazione(boolean oraRegistrazione) {
		this.oraRegistrazione = oraRegistrazione;
	}
	/**
	 * @return the riferimentoMessaggio
	 */
	public boolean isRiferimentoMessaggio() {
		return this.riferimentoMessaggio;
	}
	/**
	 * @param riferimentoMessaggio the riferimentoMessaggio to set
	 */
	public void setRiferimentoMessaggio(boolean riferimentoMessaggio) {
		this.riferimentoMessaggio = riferimentoMessaggio;
	}
	/**
	 * @return the scadenza
	 */
	public boolean isScadenza() {
		return this.scadenza;
	}
	/**
	 * @param scadenza the scadenza to set
	 */
	public void setScadenza(boolean scadenza) {
		this.scadenza = scadenza;
	}
	/**
	 * @return the servizioCorrelato
	 */
	public boolean isServizioCorrelato() {
		return this.servizioCorrelato;
	}
	/**
	 * @param servizioCorrelato the servizioCorrelato to set
	 */
	public void setServizioCorrelato(boolean servizioCorrelato) {
		this.servizioCorrelato = servizioCorrelato;
	}
	
   

}





