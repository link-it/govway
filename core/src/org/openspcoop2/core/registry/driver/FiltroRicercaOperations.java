/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.util.ArrayList;
import java.util.List;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaOperations extends FiltroRicercaPortTypes implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Nome */
	private String nomeAzione;

	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolPropertiesAzione = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolPropertiesAzione() {
		return this.protocolPropertiesAzione;
	}

	public void setProtocolPropertiesAzione(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolPropertiesAzione = list;
	}

	public void addProtocolPropertyAzione(FiltroRicercaProtocolProperty filtro){
		this.protocolPropertiesAzione.add(filtro);
	}
	
	public String getNomeAzione() {
		return this.nomeAzione;
	}

	public void setNomeAzione(String nome) {
		this.nomeAzione = nome;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro Azione: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro Azione: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuilder bf){
		if(this.nomeAzione!=null)
			bf.append(" [nome-azione:"+this.nomeAzione+"]");
		if(this.protocolPropertiesAzione!=null && this.protocolPropertiesAzione.size()>0){
			bf.append(" [protocol-properties-azione:"+this.protocolPropertiesAzione.size()+"]");
			for (int i = 0; i < this.protocolPropertiesAzione.size(); i++) {
				bf.append(" [protocol-properties-azione["+i+"]:"+this.protocolPropertiesAzione.get(i).toString()+"]");
			}
		}
		super.addDetails(bf);
	}

}
