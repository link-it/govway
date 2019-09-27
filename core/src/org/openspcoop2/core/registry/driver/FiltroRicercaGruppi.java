/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

import org.openspcoop2.core.registry.constants.ServiceBinding;


/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaGruppi extends FiltroRicerca implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** ServiceBinding */
	private ServiceBinding serviceBinding;
	private boolean ordinaDataRegistrazione = false;
	

	public ServiceBinding getServiceBinding() {
		return this.serviceBinding;
	}

	public void setServiceBinding(ServiceBinding serviceBinding) {
		this.serviceBinding = serviceBinding;
	}

	@Override
	public String getTipo() {
		if(this.serviceBinding!=null){
			return this.serviceBinding.getValue();
		}
		return null;
	}

	@Override
	public void setTipo(String tipo) {
		this.serviceBinding = ServiceBinding.toEnumConstant(tipo);
	}

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		super.toString(false);
		if(this.serviceBinding!=null)
			bf.append(" [serviceBinding:"+this.serviceBinding.getValue()+"]");
//		Tipo gia' visualizzato con il super.toString		
//		if(this.tipologia!=null)
//			bf.append(" [tipologia:"+this.tipologia.getValue()+"]");
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}

	public boolean isOrdinaDataRegistrazione() {
		return this.ordinaDataRegistrazione;
	}

	public void setOrdinaDataRegistrazione(boolean ordinaDataRegistrazione) {
		this.ordinaDataRegistrazione = ordinaDataRegistrazione;
	}
}
