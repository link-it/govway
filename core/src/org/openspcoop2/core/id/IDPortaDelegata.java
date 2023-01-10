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


package org.openspcoop2.core.id;

import org.openspcoop2.utils.Utilities;

/**
 * Classe utilizzata per rappresentare un identificatore di una Porta Delegata nella configurazione
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDPortaDelegata implements java.io.Serializable, Cloneable{

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/** Nome della Porta Delegata */
	private String nome;
		
	/** Identificazioni Fruizione (opzionali) */
	private IdentificativiFruizione identificativiFruizione;

	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public IdentificativiFruizione getIdentificativiFruizione() {
		return this.identificativiFruizione;
	}
	public void setIdentificativiFruizione(IdentificativiFruizione identificativiFruizione) {
		this.identificativiFruizione = identificativiFruizione;
	}

	
	public static final String PORTA_DELEGATA_PREFIX = "PD:";
	public static final String PORTA_DELEGATA_SUFFIX = " ";

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.nome!=null)
			bf.append(PORTA_DELEGATA_PREFIX+this.nome);
		else
			bf.append(PORTA_DELEGATA_PREFIX+"NonDefinita");
		bf.append(PORTA_DELEGATA_SUFFIX);
		if(this.identificativiFruizione!=null)
			bf.append("Fruizione:"+this.identificativiFruizione.toString());
		else
			bf.append("Fruizione:NonDefinita");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDPortaDelegata id = (IDPortaDelegata) object;
		
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
	public IDPortaDelegata clone(){
		IDPortaDelegata idPD = new IDPortaDelegata();
		if(this.nome!=null){
			idPD.nome = new String(this.nome);
		}
		if(this.identificativiFruizione!=null){
			idPD.identificativiFruizione = this.identificativiFruizione.clone();
		}
		return idPD;
	}
}
