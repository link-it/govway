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
 * Classe utilizzata per rappresentare un identificatore di una fruizione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDFruizione implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected IDServizio idServizio;
	
	protected IDSoggetto idFruitore;
	
	
	public IDServizio getIdServizio() {
		return this.idServizio;
	}

	public void setIdServizio(IDServizio idServizio) {
		this.idServizio = idServizio;
	}

	public IDSoggetto getIdFruitore() {
		return this.idFruitore;
	}

	public void setIdFruitore(IDSoggetto idFruitore) {
		this.idFruitore = idFruitore;
	}

	
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.idServizio!=null)
			bf.append(" idServizio["+this.idServizio+"]");
		if(this.idFruitore!=null)
			bf.append(" idFruitore["+this.idFruitore+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDFruizione id = (IDFruizione) object;
		
		if(this.idServizio==null){
			if(id.idServizio!=null)
				return false;
		}else{
			if(this.idServizio.equals(id.idServizio)==false)
				return false;
		}
		
		if(this.idFruitore==null){
			if(id.idFruitore!=null)
				return false;
		}else{
			if(this.idFruitore.equals(id.idFruitore)==false)
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
	public IDFruizione clone(){
		IDFruizione idFruizione = new IDFruizione();
		if(this.idServizio!=null){
			idFruizione.idServizio = this.idServizio.clone();
		}
		if(this.idFruitore!=null){
			idFruizione.idFruitore = this.idFruitore.clone();
		}
		return idFruizione;
	}
	
	public String toFormatString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.idFruitore.toFormatString());
		sb.append(" ");
		sb.append(this.idServizio.toFormatString());
		return sb.toString();
	}
	
	public static IDFruizione toIDFruizione(String formatString) throws Exception {
		String [] tmp = formatString.split(" ");
		if(tmp.length!=2) {
			throw new Exception("Formato non supportato, attesi 2 valori, trovati "+tmp.length);
		}
		String fruitore = tmp[0];
		String servizio = tmp[1];
		IDFruizione idFruizione = new IDFruizione();
		idFruizione.setIdFruitore(IDSoggetto.toIDSoggetto(fruitore));
		idFruizione.setIdServizio(IDServizio.toIDServizio(servizio));
		return idFruizione;
	}
}


