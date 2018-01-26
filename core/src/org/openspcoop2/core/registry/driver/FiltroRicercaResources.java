/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
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

public class FiltroRicercaResources extends FiltroRicercaAccordi implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Nome */
	private String resourceName;

	/** ProtocolProperty */
	private List<FiltroRicercaProtocolProperty> protocolPropertiesResources = new ArrayList<FiltroRicercaProtocolProperty>();

	public List<FiltroRicercaProtocolProperty> getProtocolPropertiesResources() {
		return this.protocolPropertiesResources;
	}

	public void setProtocolPropertiesResources(
			List<FiltroRicercaProtocolProperty> list) {
		this.protocolPropertiesResources = list;
	}

	public void addProtocolPropertyResource(FiltroRicercaProtocolProperty filtro){
		this.protocolPropertiesResources.add(filtro);
	}
	
	public String getResourceName() {
		return this.resourceName;
	}

	public void setResourceName(String nome) {
		this.resourceName = nome;
	}
	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		bf.append("Filtro Resources: ");
		this.addDetails(bf);
		if(bf.length()=="Filtro Resources: ".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
	@Override
	public void addDetails(StringBuffer bf){
		if(this.resourceName!=null)
			bf.append(" [nome-risorsa:"+this.resourceName+"]");
		if(this.protocolPropertiesResources!=null && this.protocolPropertiesResources.size()>0){
			bf.append(" [protocol-properties-resource:"+this.protocolPropertiesResources.size()+"]");
			for (int i = 0; i < this.protocolPropertiesResources.size(); i++) {
				bf.append(" [protocol-properties-resource["+i+"]:"+this.protocolPropertiesResources.get(i).toString()+"]");
			}
		}
		super.addDetails(bf);
	}

}
