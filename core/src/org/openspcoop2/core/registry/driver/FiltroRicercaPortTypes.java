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

public class FiltroRicercaPortTypes extends FiltroRicercaAccordi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Nome */
	private String nomePortType;

	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolPropertiesPortType = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolPropertiesPortType() {
		return this.protocolPropertiesPortType;
	}

	public void setProtocolPropertiesPortType(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolPropertiesPortType = list;
	}

	public void addProtocolPropertyPortType(FiltroRicercaProtocolProperty filtro){
		this.protocolPropertiesPortType.add(filtro);
	}
	
	public String getNomePortType() {
		return this.nomePortType;
	}

	public void setNomePortType(String nome) {
		this.nomePortType = nome;
	}
	
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append("Filtro PortType: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro PortType: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuilder bf){
		if(this.nomePortType!=null)
			bf.append(" [nome-port-type:"+this.nomePortType+"]");
		if(this.protocolPropertiesPortType!=null && this.protocolPropertiesPortType.size()>0){
			bf.append(" [protocol-properties-port-type:"+this.protocolPropertiesPortType.size()+"]");
			for (int i = 0; i < this.protocolPropertiesPortType.size(); i++) {
				bf.append(" [protocol-properties-port-type["+i+"]:"+this.protocolPropertiesPortType.get(i).toString()+"]");
			}
		}
		super.addDetails(bf);
	}

}
