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
 * Classe utilizzata per rappresentare un identificatore di un Servizio Applicativo nella configurazione
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDConnettore extends IDServizioApplicativo implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String nomeConnettore;
	
	
	public String getNomeConnettore() {
		return this.nomeConnettore;
	}
	public void setNomeConnettore(String nomeConnettore) {
		this.nomeConnettore = nomeConnettore;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString());
		bf.append(" ");
		if(this.nomeConnettore!=null)
			bf.append("Connettore:"+this.nomeConnettore);
		else
			bf.append("Connettore:NonDefinito");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDConnettore id = (IDConnettore) object;
		
		if(this.nomeConnettore==null){
			if(id.nomeConnettore!=null)
				return false;
		}else{
			if(this.nomeConnettore.equals(id.nomeConnettore)==false)
				return false;
		}

		return super.equals(object);
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@Override
	public IDConnettore clone(){
		IDConnettore idSA = new IDConnettore();
		if(this.nome!=null){
			idSA.nome = new String(this.nome);
		}
		if(this.idSoggettoProprietario!=null){
			idSA.idSoggettoProprietario = this.idSoggettoProprietario.clone();
		}
		if(this.nomeConnettore!=null){
			idSA.nomeConnettore = new String(this.nomeConnettore);
		}
		return idSA;
	}
}
