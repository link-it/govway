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

import org.openspcoop2.utils.Utilities;

/**
 * Classe utilizzata per rappresentare un identificatore di una Porta Applicativa nella configurazione
 * 
 * </pre>
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDPortaApplicativa implements java.io.Serializable, Cloneable{
	
	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** Nome della Porta Applicativa */
	private String nome;
		
	/** Identificazioni Erogazione (opzionali) */
	private IdentificativiErogazione identificativiErogazione;
	
	
	
	public String getNome() {
		return this.nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public IdentificativiErogazione getIdentificativiErogazione() {
		return this.identificativiErogazione;
	}
	public void setIdentificativiErogazione(IdentificativiErogazione identificativiErogazione) {
		this.identificativiErogazione = identificativiErogazione;
	}
	
	public static final String PORTA_APPLICATIVA_PREFIX = "PA:";
	public static final String PORTA_APPLICATIVA_SUFFIX = " ";
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.nome!=null)
			bf.append(PORTA_APPLICATIVA_PREFIX+this.nome);
		else
			bf.append(PORTA_APPLICATIVA_PREFIX+"NonDefinita");
		bf.append(PORTA_APPLICATIVA_SUFFIX);
		if(this.identificativiErogazione!=null)
			bf.append("Erogazione:"+this.identificativiErogazione.toString());
		else
			bf.append("Erogazione:NonDefinita");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		if(object == null)
			return false;
		if(!Utilities.equalsClass(object,this))
			return false;
		IDPortaApplicativa id = (IDPortaApplicativa) object;
		
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
	public IDPortaApplicativa clone(){
		IDPortaApplicativa idPA = new IDPortaApplicativa();
		if(this.nome!=null){
			idPA.nome = new String(this.nome);
		}
		if(this.identificativiErogazione!=null){
			idPA.identificativiErogazione = this.identificativiErogazione.clone();
		}
		return idPA;
	}
}
