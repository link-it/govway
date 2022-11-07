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


/**
 * Classe utilizzata per rappresentare un identificatore di un Accordo di Servizio nel registro dei Servizi.
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDAccordo implements java.io.Serializable, Cloneable {

	 /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/** Nome dell'accordo */
	protected String nome;
	/** Soggetto Referente (Puo' non essere definito). */
	protected IDSoggetto soggettoReferente;
	/** Versione. */
	protected Integer versione = 1;


	public String getNome() {
		return this.nome;
	}
	public IDSoggetto getSoggettoReferente() {
		return this.soggettoReferente;
	}
	public Integer getVersione() {
		return this.versione;
	}
	
	@Deprecated
	public void setNome(String nome) {
		this.nome = nome;
	}
	@Deprecated
	public void setSoggettoReferente(IDSoggetto soggettoReferente) {
		this.soggettoReferente = soggettoReferente;
	}
	@Deprecated
	public void setVersione(Integer versione) {
		this.versione = versione;
	}
	
		
	@Override 
	public String toString(){
		StringBuilder bf = new StringBuilder();
		if(this.soggettoReferente!=null){
			bf.append(this.soggettoReferente.toString());
			bf.append(":");
		}
		bf.append(this.nome);
		bf.append(":");
		bf.append(this.versione);
		return bf.toString();
	}
		
	@Override 
	public boolean equals(Object object){
		return _equals(object, true);
	}
	protected boolean _equals(Object object, boolean verifyClass){
		if(object == null)
			return false;
		if(verifyClass) {
			if(object.getClass().getName().equals(this.getClass().getName()) == false) {
				return false;
			}
		}
		IDAccordo id = (IDAccordo) object;
		
		if(this.nome==null){
			if(id.nome!=null)
				return false;
		}else{
			if(this.nome.equals(id.nome)==false)
				return false;
		}

		if(this.getVersione()==null) {
			if(id.getVersione()!=null) {
				return false;
			}
		}
		else {
			if(id.getVersione()==null) {
				return false;
			}
			if(this.getVersione().intValue()!=id.getVersione().intValue()){
				return false;
			}
		}
		
		if(this.soggettoReferente==null){
			if(id.soggettoReferente!=null)
				return false;
		}else{
			if(this.soggettoReferente.equals(id.soggettoReferente)==false)
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
	public IDAccordo clone(){
		IDAccordo idAccordo = new IDAccordo();
		
		if(this.nome!=null){
			idAccordo.nome = new String(this.nome);
		}
		
		idAccordo.versione = this.versione;
		
		if(this.soggettoReferente!=null){
			idAccordo.soggettoReferente = this.soggettoReferente.clone();
		}
		
		return idAccordo;
	}
}






