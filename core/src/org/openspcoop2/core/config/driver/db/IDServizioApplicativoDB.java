/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.config.driver.db;

import java.io.Serializable;

import org.openspcoop2.core.id.IDServizioApplicativo;

/**
 * Classe utilizzata per rappresentare un identificatore di un Servizio Applicativo nella configurazione
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDServizioApplicativoDB extends IDServizioApplicativo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public IDServizioApplicativoDB() {
		
	}
	public IDServizioApplicativoDB(IDServizioApplicativo idSA) {
		super.setNome(idSA.getNome());
		super.setIdSoggettoProprietario(idSA.getIdSoggettoProprietario());
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString());
		bf.append(" ");
		if(this.id!=null)
			bf.append("Id:"+this.id);
		else
			bf.append("Id");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		return super.equals(object);
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDServizioApplicativoDB clone(){
		IDServizioApplicativoDB idSA = new IDServizioApplicativoDB();
		if(this.getNome()!=null){
			idSA.setNome(new String(this.getNome()));
		}
		if(this.getIdSoggettoProprietario()!=null){
			idSA.setIdSoggettoProprietario(this.getIdSoggettoProprietario().clone());
		}
		if(this.id!=null){
			idSA.setId(Long.valueOf(this.id.longValue()+""));
		}
		return idSA;
	}
}
