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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;
import java.util.Date;

import org.openspcoop2.core.id.IDAccordoCooperazione;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaAccordi implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome Accordo */
	private String nomeAccordo;
	
	/** Versione */
	private String versione;
	
	/** Nome */
	private String nomeSoggettoReferente;
	
	/** Tipo */
	private String tipoSoggettoReferente;

	private IDAccordoCooperazione idAccordoCooperazione;
	private Boolean servizioComposto;

	
	/**
	 * @return the maxDate
	 */
	public Date getMaxDate() {
		return this.maxDate;
	}

	/**
	 * @param maxDate the maxDate to set
	 */
	public void setMaxDate(Date maxDate) {
		this.maxDate = maxDate;
	}

	/**
	 * @return the minDate
	 */
	public Date getMinDate() {
		return this.minDate;
	}

	/**
	 * @param minDate the minDate to set
	 */
	public void setMinDate(Date minDate) {
		this.minDate = minDate;
	}


	public String getNomeAccordo() {
		return this.nomeAccordo;
	}

	public void setNomeAccordo(String nomeAccordo) {
		this.nomeAccordo = nomeAccordo;
	}

	public String getVersione() {
		return this.versione;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}

	public String getNomeSoggettoReferente() {
		return this.nomeSoggettoReferente;
	}

	public void setNomeSoggettoReferente(String nomeSoggettoReferente) {
		this.nomeSoggettoReferente = nomeSoggettoReferente;
	}

	public String getTipoSoggettoReferente() {
		return this.tipoSoggettoReferente;
	}

	public void setTipoSoggettoReferente(String tipoSoggettoReferente) {
		this.tipoSoggettoReferente = tipoSoggettoReferente;
	}

	public IDAccordoCooperazione getIdAccordoCooperazione() {
		return this.idAccordoCooperazione;
	}

	public void setIdAccordoCooperazione(IDAccordoCooperazione idAccordoCooperazione) {
		this.idAccordoCooperazione = idAccordoCooperazione;
	}
	
	public Boolean isServizioComposto() {
		return this.servizioComposto;
	}

	public void setServizioComposto(Boolean servizioComposto) {
		this.servizioComposto = servizioComposto;
	}
	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro Accordi:");
		if(this.minDate!=null)
			bf.append(" [intervallo-inferiore-data:"+this.minDate+"]");
		if(this.maxDate!=null)
			bf.append(" [intervallo-superiore-data:"+this.maxDate+"]");
		if(this.nomeAccordo!=null)
			bf.append(" [nome-accordo:"+this.nomeAccordo+"]");
		if(this.tipoSoggettoReferente!=null)
			bf.append(" [tipo-soggetto-referente:"+this.tipoSoggettoReferente+"]");
		if(this.nomeSoggettoReferente!=null)
			bf.append(" [nome-soggetto-referente:"+this.nomeSoggettoReferente+"]");
		if(this.versione!=null)
			bf.append(" [versione:"+this.versione+"]");
		if(this.idAccordoCooperazione!=null)
			bf.append(" [idAccordoCooperazione:"+this.idAccordoCooperazione+"]");
		if(this.servizioComposto!=null)
			bf.append(" [servizioComposto:"+this.servizioComposto+"]");
		if(bf.length()=="Filtro Accordi:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}

}
