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

import org.openspcoop2.utils.Utilities;

/**
 * IdentificativiFruizione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificativiFruizione implements java.io.Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto soggettoFruitore;
	private IDServizio idServizio;
	
	public IDSoggetto getSoggettoFruitore() {
		return this.soggettoFruitore;
	}
	public void setSoggettoFruitore(IDSoggetto soggettoFruitore) {
		this.soggettoFruitore = soggettoFruitore;
	}
	public IDServizio getIdServizio() {
		return this.idServizio;
	}
	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.soggettoFruitore!=null)
			bf.append("SoggettoFruitore:"+this.soggettoFruitore);
		else
			bf.append("SoggettoFruitore:NonDefinita");
		bf.append(" ");
		if(this.idServizio!=null)
			bf.append("Servizio:"+this.idServizio.toString());
		else
			bf.append("Servizio:NonDefinito");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IdentificativiFruizione id = (IdentificativiFruizione) object;
		
		if(this.soggettoFruitore==null){
			if(id.soggettoFruitore!=null)
				return false;
		}else{
			if(this.soggettoFruitore.equals(id.soggettoFruitore)==false)
				return false;
		}
		
		if(this.idServizio==null){
			if(id.idServizio!=null)
				return false;
		}else{
			if(this.idServizio.equals(id.idServizio)==false)
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
	public IdentificativiFruizione clone(){
		IdentificativiFruizione id = new IdentificativiFruizione();
		if(this.soggettoFruitore!=null){
			id.soggettoFruitore = this.soggettoFruitore.clone();
		}
		if(this.idServizio!=null){
			id.idServizio = this.idServizio.clone();
		}
		return id;
	}
}
