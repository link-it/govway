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


package org.openspcoop2.core.registry.driver.db;

import java.io.Serializable;

import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDSoggetto;

/**
 * Classe utilizzata per rappresentare un identificatore di un Accordo nel Registro
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class IDAccordoDB extends IDAccordo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public IDAccordoDB() {
		
	}
	@SuppressWarnings("deprecation")
	public IDAccordoDB(IDAccordo idAccordo) {
		super.setNome(idAccordo.getNome());
		if(idAccordo.getSoggettoReferente() instanceof IDSoggettoDB) {
			super.setSoggettoReferente(idAccordo.getSoggettoReferente());
		}
		else {
			IDSoggettoDB soggettoReferente = new IDSoggettoDB(idAccordo.getSoggettoReferente());
			super.setSoggettoReferente(soggettoReferente);
		}
		super.setVersione(idAccordo.getVersione());
	}
	@SuppressWarnings("deprecation")
	public IDAccordoDB(String nomeAccordo, IDSoggettoDB soggettoReferente, int versioneAccordo) {
		super.setNome(nomeAccordo);
		super.setSoggettoReferente(soggettoReferente);
		super.setVersione(versioneAccordo);
	}

	public IDSoggettoDB getSoggettoReferenteDB() {
		return (IDSoggettoDB) this.soggettoReferente;
	}
	@Override
	@SuppressWarnings("deprecation")
	@Deprecated
	public void setSoggettoReferente(IDSoggetto soggettoReferente) {
		if(soggettoReferente instanceof IDSoggettoDB) {
			this.soggettoReferente = soggettoReferente;
		}
		else {
			IDSoggettoDB soggettoReferenteDB = new IDSoggettoDB(soggettoReferente);
			this.soggettoReferente = soggettoReferenteDB;
		}
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString());
		bf.append(" ");
		if(this.id!=null)
			bf.append("Id:"+this.id);
		else
			bf.append("Id");
		return bf.toString();
	}
	
	@Override 
	public boolean equals(Object object){
		return super._equals(object, false);
	}
	
	// Utile per usare l'oggetto in hashtable come chiave
	@Override
	public int hashCode(){
		return this.toString().hashCode();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public IDAccordoDB clone(){
		IDAccordoDB idAccordo = new IDAccordoDB();
		if(this.getNome()!=null){
			idAccordo.setNome(new String(this.getNome()));
		}
		if(this.getVersione()!=null){
			idAccordo.setVersione(Integer.valueOf(this.getVersione().intValue()+""));
		}
		if(this.getSoggettoReferente()!=null){
			idAccordo.setSoggettoReferente((IDSoggettoDB)this.getSoggettoReferente().clone());
		}
		if(this.id!=null){
			idAccordo.setId(Long.valueOf(this.id+""));
		}
		return idAccordo;
	}
}
