/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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


package org.openspcoop2.core.id;

import java.io.Serializable;

import org.openspcoop2.utils.Utilities;


/**
 * Classe utilizzata per rappresentare un identificatore di una risorsa REST di un Accordo di Servizio nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDResource implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String nome;

	protected IDAccordo idAccordo;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public IDAccordo getIdAccordo() {
		return this.idAccordo;
	}
	public void setIdAccordo(IDAccordo idAccordo) {
		this.idAccordo = idAccordo;
	}
	
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(this.nome);
		if(this.idAccordo!=null)
			bf.append("["+this.idAccordo+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDResource id = (IDResource) object;
		
		if(this.nome==null){
			if(id.nome!=null)
				return false;
		}else{
			if(this.nome.equals(id.nome)==false)
				return false;
		}

		if(this.idAccordo==null){
			if(id.idAccordo!=null)
				return false;
		}else{
			if(this.idAccordo.equals(id.idAccordo)==false)
				return false;
		}
		
		return true;
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDResource clone(){
		IDResource idPT = new IDResource();
		if(this.idAccordo!=null){
			idPT.idAccordo = this.idAccordo.clone();
		}
		if(this.nome!=null){
			idPT.nome = new String(this.nome);
		}
		return idPT;
	}
}


