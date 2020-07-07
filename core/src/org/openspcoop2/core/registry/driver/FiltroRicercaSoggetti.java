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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;

import org.openspcoop2.core.id.IDRuolo;
import org.openspcoop2.core.registry.CredenzialiSoggetto;
import org.openspcoop2.utils.crypt.CryptConfig;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaSoggetti extends FiltroRicerca implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Pdd */
	private String nomePdd;
		
	public String getNomePdd() {
		return this.nomePdd;
	}

	public void setNomePdd(String nomePdd) {
		this.nomePdd = nomePdd;
	}
	
	/** Ruolo */
	private IDRuolo idRuolo;
		
	public IDRuolo getIdRuolo() {
		return this.idRuolo;
	}

	public void setIdRuolo(IDRuolo idRuolo) {
		this.idRuolo = idRuolo;
	}

	
	/** CredenzialeSoggetto */
	private CredenzialiSoggetto credenzialiSoggetto;
	private CryptConfig cryptConfig;
	
	public CryptConfig getCryptConfig() {
		return this.cryptConfig;
	}
	public CredenzialiSoggetto getCredenzialiSoggetto() {
		return this.credenzialiSoggetto;
	}

	public void setCredenzialiSoggetto(CredenzialiSoggetto credenzialiSoggetto, CryptConfig cryptConfig) {
		this.credenzialiSoggetto = credenzialiSoggetto;
		this.cryptConfig = cryptConfig;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro:");
		this.addDetails(bf);
		if(this.idRuolo!=null)
			bf.append(" [ruolo:"+this.idRuolo+"]");
		if(this.credenzialiSoggetto!=null){
			if(this.credenzialiSoggetto.getTipo()!=null){
				bf.append(" [credenziali-tipo:"+this.credenzialiSoggetto.getTipo().getValue()+"]");
			}
			if(this.credenzialiSoggetto.getUser()!=null){
				bf.append(" [credenziali-user:"+this.credenzialiSoggetto.getUser()+"]");
			}
			if(this.credenzialiSoggetto.getPassword()!=null){
				bf.append(" [credenziali-password:"+this.credenzialiSoggetto.getPassword()+"]");
			}
			if(this.credenzialiSoggetto.getSubject()!=null){
				bf.append(" [credenziali-subject:"+this.credenzialiSoggetto.getSubject()+"]");
			}
		}
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuilder bf){
		if(this.nomePdd!=null)
			bf.append(" [nomePdd:"+this.nomePdd+"]");
		super.addDetails(bf);
	}
}
