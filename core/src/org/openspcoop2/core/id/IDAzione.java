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


package org.openspcoop2.core.id;

import java.io.Serializable;


/**
 * Classe utilizzata per rappresentare un identificatore di una azione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author: apoli $
 * @version $Rev: 12237 $, $Date: 2016-10-04 11:41:45 +0200 (Tue, 04 Oct 2016) $
 */

public class IDAzione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String nome;

	protected IDAccordo idAccordo;
	
	protected String portType;
	
	public String getPortType() {
		return this.portType;
	}
	public void setPortType(String idPortType) {
		this.portType = idPortType;
	}
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
		StringBuffer bf = new StringBuffer();
		bf.append(this.nome);
		if(this.idAccordo!=null)
			bf.append(" idAccordo["+this.idAccordo+"]");
		if(this.portType!=null)
			bf.append(" portType["+this.portType+"]");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDAzione id = (IDAzione) object;
		
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
		
		if(this.portType==null){
			if(id.portType!=null)
				return false;
		}else{
			if(this.portType.equals(id.portType)==false)
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
	public IDAzione clone(){
		IDAzione idAzione = new IDAzione();
		if(this.nome!=null){
			idAzione.nome = new String(this.nome);
		}
		if(this.idAccordo!=null){
			idAzione.idAccordo = this.idAccordo.clone();
		}
		if(this.portType!=null){
			idAzione.portType = new String(this.portType);
		}
		return idAzione;
	}
}


