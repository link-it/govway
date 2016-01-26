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
		
	/** costruttore */
	public HeaderIntegrazione(){
		this.busta = new HeaderIntegrazioneBusta();
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

}
