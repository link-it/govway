/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.core.config.driver;

import java.io.Serializable;
import java.util.Date;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaBase implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Intervallo inferiore */
	private Date minDate;
	
	/** Intervallo superiore */
	private Date maxDate;
	
	/** Nome */
	private String nome;
	

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

	/**
	 * @return the nome
	 */
	public String getNome() {
		return this.nome;
	}

	/**
	 * @param nome the nome to set
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}

	

	
	@Override
	public String toString(){
		return this.toString(true);
	}
	public String toString(boolean checkEmpty){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro:");
		if(this.minDate!=null)
			bf.append(" [intervallo-inferiore-data:"+this.minDate+"]");
		if(this.maxDate!=null)
			bf.append(" [intervallo-superiore-data:"+this.maxDate+"]");
		if(this.nome!=null)
			bf.append(" [nome:"+this.nome+"]");
		if(checkEmpty){
			if(bf.length()=="Filtro:".length()){
				bf.append(" nessun filtro presente");
			}
		}
		return bf.toString();
	}
}
