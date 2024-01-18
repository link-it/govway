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


package org.openspcoop2.core.id;

import java.io.Serializable;

import org.openspcoop2.utils.Utilities;


/**
 * Classe utilizzata per rappresentare un identificatore di un operation di un port type di un Accordo di Servizio nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDPortTypeAzione implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String nome;

	protected IDPortType idPortType;
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public IDPortType getIdPortType() {
		return this.idPortType;
	}
	public void setIdPortType(IDPortType idPortType) {
		this.idPortType = idPortType;
	}

	
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(this.nome);
		if(this.idPortType!=null)
			bf.append("["+this.idPortType+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDPortTypeAzione id = (IDPortTypeAzione) object;
		
		if(this.nome==null){
			if(id.nome!=null)
				return false;
		}else{
			if(this.nome.equals(id.nome)==false)
				return false;
		}

		if(this.idPortType==null){
			if(id.idPortType!=null)
				return false;
		}else{
			if(this.idPortType.equals(id.idPortType)==false)
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
	public IDPortTypeAzione clone(){
		IDPortTypeAzione idPT = new IDPortTypeAzione();
		if(this.idPortType!=null){
			idPT.idPortType = this.idPortType.clone();
		}
		if(this.nome!=null){
			idPT.nome = new String(this.nome);
		}
		return idPT;
	}
}


