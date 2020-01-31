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
 * Classe utilizzata per rappresentare un identificatore di un gruppo di proprietà
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDGenericProperties implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/** Nome del gruppo di proprieta */
	protected String nome;
	/** Tipologia del gruppo di proprieta */
	protected String tipologia;


	public String getNome() {
		return this.nome;
	}
	public String getTipologia() {
		return this.tipologia;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}
	
		
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(this.tipologia);
		bf.append(":");
		bf.append(this.nome);
		return bf.toString();
	}
		
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(object.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDGenericProperties id = (IDGenericProperties) object;
		
		if(this.tipologia==null){
			if(id.tipologia!=null)
				return false;
		}else{
			if(this.tipologia.equals(id.tipologia)==false)
				return false;
		}

		if(this.nome==null){
			if(id.nome!=null)
				return false;
		}else{
			if(this.nome.equals(id.nome)==false)
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
	public IDGenericProperties clone(){
		IDGenericProperties idGP = new IDGenericProperties();
		
		if(this.nome!=null){
			idGP.nome = new String(this.nome);
		}
		
		if(this.tipologia!=null){
			idGP.tipologia = new String(this.tipologia);
		}
		
		return idGP;
	}
}






