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



package org.openspcoop2.core.config.driver;

import java.io.Serializable;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaSoggetti extends FiltroRicercaBase implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** Tipo */
	private String tipo;
	
	
	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}



	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		bf.append(super.toString(false));
		if(this.tipo!=null)
			bf.append(" [tipo:"+this.tipo+"]");
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
}
