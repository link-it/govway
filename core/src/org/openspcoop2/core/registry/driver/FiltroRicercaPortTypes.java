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
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro PortType: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro PortType: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuffer bf){
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
