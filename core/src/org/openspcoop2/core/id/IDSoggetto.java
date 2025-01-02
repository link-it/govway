/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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
 * Classe utilizzata per rappresentare un Soggetto nel registro dei Servizi.
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDSoggetto implements java.io.Serializable, Cloneable {

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
		return this.tipo + "/" + this.nome;
	}
	
	@Override 
	public boolean equals(Object soggetto){
		return equalsEngine(soggetto, true);
	}
	protected boolean equalsEngine(Object soggetto, boolean verifyClass){
		if(soggetto == null)
			return false;
		if(verifyClass) {
			String objectClassName = soggetto.getClass().getName() + "";
			if(!objectClassName.equals(this.getClass().getName())) {
				return false;
			}
		}
		IDSoggetto id = (IDSoggetto) soggetto;
		return equasEngine(id);
	}
	private boolean equasEngine(IDSoggetto id) {
		// TIPO
		if(this.getTipo()==null){
			if(id.getTipo()!=null)
				return false;
		}else{
			if(!this.getTipo().equals(id.getTipo()))
				return false;
		}
		// NOME
		if(this.getNome()==null){
			if(id.getNome()!=null)
				return false;
		}else{
			if(!this.getNome().equals(id.getNome()))
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
		IDSoggetto clone = null;
		try {
			clone = (IDSoggetto) super.clone();
		}catch(Exception t) {
			clone = new IDSoggetto();
		}
		
		clone.setCodicePorta(this.codicePorta!=null ? this.codicePorta+"" : null);
		clone.setTipo(this.tipo!=null ? this.tipo+"" : null);
		clone.setNome(this.nome!=null ? this.nome+"" : null);
		
		return clone;
	}
	
	
	public String toFormatString(){
		StringBuilder sb = new StringBuilder();
		sb.append(this.tipo);
		sb.append("/");
		sb.append(this.nome);
		return sb.toString();
	}
	
	public static IDSoggetto toIDSoggetto(String formatString) throws IDException {
		String [] tmp = formatString.split("/");
		if(tmp.length!=2) {
			throw new IDException("Formato non supportato, attesi 2 valori, trovati "+tmp.length);
		}
		String tipo = tmp[0];
		String nome = tmp[1];
		IDSoggetto idSoggetto = new IDSoggetto();
		idSoggetto.tipo=tipo;
		idSoggetto.nome=nome;
		return idSoggetto;
	}
}






