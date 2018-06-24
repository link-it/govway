/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
 * Classe utilizzata per rappresentare un Ruolo nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDRuolo implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

    protected String nome;
    
	
	/* ********  C O S T R U T T O R E  ******** */

	
	/**
	 * Costruttore. 
	 *
	 * @param nome Nome del Ruolo
	 * 
	 */
	public IDRuolo(String nome){
		this.nome = nome;
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public IDRuolo(){}




	/* ********  S E T T E R   ******** */

	/**
	 * Imposta il nome del Soggetto.
	 *
	 * @param nome Nome del Soggetto.
	 * 
	 */
	public void setNome(String nome){
		this.nome = nome;
	}
	

	

	/* ********  G E T T E R   ******** */

	/**
	 * Ritorna il nome del Soggetto.
	 *
	 * @return Nome del Soggetto
	 * 
	 */
	public String getNome(){
		return this.nome;
	}


	
	
	
	@Override 
	public String toString(){
		return this.nome;
	}
	
	@Override 
	public boolean equals(Object ruolo){
		if(ruolo == null)
			return false;
		if(ruolo.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDRuolo id = (IDRuolo) ruolo;
		// NOME
		if(this.getNome()==null){
			if(id.getNome()!=null)
				return false;
		}else{
			if(this.getNome().equals(id.getNome())==false)
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
	public IDRuolo clone(){
		IDRuolo clone = new IDRuolo();
		
		clone.setNome(this.nome!=null ? new String(this.nome) : null);
		
		return clone;
	}
}






