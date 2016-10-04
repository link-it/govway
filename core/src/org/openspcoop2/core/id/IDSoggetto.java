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

/**
 * Classe utilizzata per rappresentare un Soggetto nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDSoggetto implements java.io.Serializable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

    protected String tipo;

    protected String nome;

    protected String codicePorta;
    
	
	/* ********  C O S T R U T T O R E  ******** */

	
	/**
	 * Costruttore. 
	 *
	 * @param tipo Tipo del Soggetto
	 * @param nome Nome del Soggetto
	 * 
	 */
	public IDSoggetto(String tipo, String nome){
		this.tipo = tipo;
		this.nome = nome;
	}
	/**
	 * Costruttore. 
	 *
	 * @param tipo Tipo del Soggetto
	 * @param nome Nome del Soggetto
	 * @param codicePorta identificativo del dominio
	 * 
	 */
	public IDSoggetto(String tipo, String nome , String codicePorta){
		this.tipo = tipo;
		this.nome = nome;
		this.codicePorta = codicePorta;
	}
	/**
	 * Costruttore. 
	 *
	 * 
	 */
	public IDSoggetto(){}




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
	/**
	 * Imposta il tipo del Soggetto
	 *
	 * @param tipo Tipo del Soggetto
	 * 
	 */
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	/**
	 * Imposta il codice porta del Soggetto
	 *
	 * @param codicePorta Codice porta del Soggetto
	 * 
	 */
	public void setCodicePorta(String codicePorta){
		this.codicePorta = codicePorta;
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
	/**
	 * Ritorna il tipo del Soggetto
	 *
	 * @return Tipo del Soggetto
	 * 
	 */
	public String getTipo(){
		return this.tipo;
	}

	/**
	 * Ritorna il codice porta del Soggetto
	 *
	 * @return Codice porta del Soggetto
	 * 
	 */
	public String getCodicePorta(){
		return this.codicePorta;
	}

	
	
	@Override 
	public String toString(){
		String print = this.tipo + "/" + this.nome;
		return print;
	}
	
	@Override 
	public boolean equals(Object soggetto){
		if(soggetto == null)
			return false;
		if(soggetto.getClass().getName().equals(this.getClass().getName()) == false)
			return false;
		IDSoggetto id = (IDSoggetto) soggetto;
		// TIPO
		if(this.getTipo()==null){
			if(id.getTipo()!=null)
				return false;
		}else{
			if(this.getTipo().equals(id.getTipo())==false)
				return false;
		}
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
	public IDSoggetto clone(){
		IDSoggetto clone = new IDSoggetto();
		
		clone.setCodicePorta(this.codicePorta!=null ? new String(this.codicePorta) : null);
		clone.setTipo(this.tipo!=null ? new String(this.tipo) : null);
		clone.setNome(this.nome!=null ? new String(this.nome) : null);
		
		return clone;
	}
}






