/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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

/**
 * IdentificativiErogazione
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class IdentificativiErogazione implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private IDSoggetto soggettoVirtuale;
	private IDServizio idServizio;
	
	public IDSoggetto getSoggettoVirtuale() {
		return this.soggettoVirtuale;
	}
	public void setSoggettoVirtuale(IDSoggetto soggettoVirtuale) {
		this.soggettoVirtuale = soggettoVirtuale;
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
		if(this.soggettoVirtuale!=null){
			bf.append("SoggettoVirtuale:"+this.soggettoVirtuale);
			bf.append(" ");
		}
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
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IdentificativiErogazione id = (IdentificativiErogazione) object;
		
		if(this.soggettoVirtuale==null){
			if(id.soggettoVirtuale!=null)
				return false;
		}else{
			if(this.soggettoVirtuale.equals(id.soggettoVirtuale)==false)
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
	public IdentificativiErogazione clone(){
		IdentificativiErogazione id = new IdentificativiErogazione();
		if(this.soggettoVirtuale!=null){
			id.soggettoVirtuale = this.soggettoVirtuale.clone();
		}
		if(this.idServizio!=null){
			id.idServizio = this.idServizio.clone();
		}
		return id;
	}
}
