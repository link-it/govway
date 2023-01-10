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


package org.openspcoop2.core.registry.driver.db;

import java.io.Serializable;

import org.openspcoop2.core.id.IDSoggetto;

/**
 * Classe utilizzata per rappresentare un identificatore di un Soggetto nel Registro
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDSoggettoDB extends IDSoggetto implements Serializable {

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
	
	public IDSoggettoDB() {
		
	}
	public IDSoggettoDB(String tipoSoggetto, String nomeSoggetto) {
		super.setNome(nomeSoggetto);
		super.setTipo(tipoSoggetto);
	}
	public IDSoggettoDB(IDSoggetto idSoggetto) {
		super.setNome(idSoggetto.getNome());
		super.setTipo(idSoggetto.getTipo());
		super.setCodicePorta(idSoggetto.getCodicePorta());
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
		return super._equals(object, false);
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDSoggettoDB clone(){
		IDSoggettoDB idSoggetto = new IDSoggettoDB();
		if(this.getNome()!=null){
			idSoggetto.setNome(new String(this.getNome()));
		}
		if(this.getTipo()!=null){
			idSoggetto.setTipo(new String(this.getTipo()));
		}
		if(this.getCodicePorta()!=null){
			idSoggetto.setCodicePorta(new String(this.getCodicePorta()));
		}
		if(this.id!=null){
			idSoggetto.setId(Long.valueOf(this.id));
		}
		return idSoggetto;
	}
}
