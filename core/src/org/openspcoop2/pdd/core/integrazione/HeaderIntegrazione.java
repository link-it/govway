/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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



package org.openspcoop2.pdd.core.integrazione;


/**
 * Classe utilizzata per rappresentare le informazioni inserite in una header di integrazione.
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class HeaderIntegrazione implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Informazioni */
	private HeaderIntegrazioneBusta busta;
	
	/** ID Applicativo */
	private String idApplicativo;
	private String riferimentoIdApplicativoRichiesta;
		
	/** Identita del servizio applicativo */
	private String servizioApplicativo;

	/** ID Transazione */
	private String idTransazione;
		
	/** costruttore */
	public HeaderIntegrazione(String idTransazione){
		this.busta = new HeaderIntegrazioneBusta();
		this.idTransazione = idTransazione;
	}
	
	
	/**
	 * @return the HeaderIntegrazioneBusta
	 */
	public HeaderIntegrazioneBusta getBusta() {
		return this.busta;
	}

	/**
	 * @return the idApplicativo
	 */
	public String getIdApplicativo() {
		return this.idApplicativo;
	}


	/**
	 * @return the servizioApplicativo
	 */
	public String getServizioApplicativo() {
		return this.servizioApplicativo;
	}

	/**
	 * @param busta HeaderIntegrazioneBusta
	 */
	public void setBusta(HeaderIntegrazioneBusta busta) {
		this.busta = busta;
	}

	/**
	 * @param idApplicativo the idApplicativo to set
	 */
	public void setIdApplicativo(String idApplicativo) {
		this.idApplicativo = idApplicativo;
	}


	/**
	 * @param servizioApplicativo the servizioApplicativo to set
	 */
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}


	public String getRiferimentoIdApplicativoRichiesta() {
		return this.riferimentoIdApplicativoRichiesta;
	}


	public void setRiferimentoIdApplicativoRichiesta(
			String riferimentoIdApplicativoRichiesta) {
		this.riferimentoIdApplicativoRichiesta = riferimentoIdApplicativoRichiesta;
	}


	public String getIdTransazione() {
		return this.idTransazione;
	}


	public void setIdTransazione(String idTransazione) {
		this.idTransazione = idTransazione;
	}
}
