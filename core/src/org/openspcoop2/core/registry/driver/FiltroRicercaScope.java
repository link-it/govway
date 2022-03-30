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



package org.openspcoop2.core.registry.driver;

import java.io.Serializable;

import org.openspcoop2.core.registry.constants.ScopeContesto;

/**
 * Permette il filtro di ricerca attraverso i driver che implementano l'interfaccia 'get'
 * 
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author Nardi Lorenzo (nardi@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class FiltroRicercaScope extends FiltroRicerca implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/** ScopeTipologia */
	private String tipologia;
	/** ScopeContesto */
	private ScopeContesto contesto;
		
	public ScopeContesto getContesto() {
		return this.contesto;
	}

	public void setContesto(ScopeContesto contesto) {
		this.contesto = contesto;
	}

	public String getTipologia() {
		return this.tipologia;
	}

	public void setTipologia(String tipologia) {
		this.tipologia = tipologia;
	}

	@Override
	public String getTipo() {
		if(this.tipologia!=null){
			return this.tipologia;
		}
		return null;
	}

	@Override
	public void setTipo(String tipo) {
		this.tipologia = tipo;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();
		super.toString(false);
		if(this.contesto!=null)
			bf.append(" [contesto:"+this.contesto.getValue()+"]");
//		Tipo gia' visualizzato con il super.toString		
//		if(this.tipologia!=null)
//			bf.append(" [tipologia:"+this.tipologia.getValue()+"]");
		if(bf.length()=="Filtro:".length())
			bf.append(" nessun filtro presente");
		return bf.toString();
	}
}
