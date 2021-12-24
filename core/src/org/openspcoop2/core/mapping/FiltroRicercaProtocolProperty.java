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



package org.openspcoop2.core.mapping;

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

public class FiltroRicercaProtocolProperty implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Nome */
	private String name;
	
	/** Valore */
	private String valueAsString;
	
	/** ValoreNumerico */
	private Long valueAsLong;
	
	/** ValoreBoolean */
	private Boolean valueAsBoolean;
	
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValueAsString() {
		return this.valueAsString;
	}
	public void setValueAsString(String valore) {
		this.valueAsString = valore;
	}
	public Long getValueAsLong() {
		return this.valueAsLong;
	}
	public void setValueAsLong(Long valoreNumerico) {
		this.valueAsLong = valoreNumerico;
	}
	public Boolean getValueAsBoolean() {
		return this.valueAsBoolean;
	}
	public void setValueAsBoolean(Boolean valueAsBoolean) {
		this.valueAsBoolean = valueAsBoolean;
	}
	
	@Override
	public String toString(){
		return this.toString(true);
	}
	public String toString(boolean checkEmpty){
		StringBuilder bf = new StringBuilder();
		bf.append("FiltroProtocolProperty: ");
		this.addDetails(bf);
		if(checkEmpty){
			if(bf.length()=="FiltroProtocolProperty: ".length()){
				bf.append(" nessun filtro presente");
			}
		}
		return bf.toString();
	}
	public void addDetails(StringBuilder bf){
		if(this.name!=null)
			bf.append(" [name:"+this.name+"]");
		if(this.valueAsString!=null)
			bf.append(" [value-string:"+this.valueAsString+"]");
		if(this.valueAsLong!=null)
			bf.append(" [value-long:"+this.valueAsLong+"]");
		if(this.valueAsBoolean!=null)
			bf.append(" [value-boolean:"+this.valueAsBoolean+"]");
	}
}
