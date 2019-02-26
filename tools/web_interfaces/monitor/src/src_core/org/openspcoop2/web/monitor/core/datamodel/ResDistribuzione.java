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
package org.openspcoop2.web.monitor.core.datamodel;

import java.io.Serializable;
import java.util.Map;

/**
 * ResDistribuzione
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ResDistribuzione extends ResBase implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private String risultato;
	
		
	public ResDistribuzione() {
		super();
	}
	
	public ResDistribuzione(String risultato, Number somma) {
		this.risultato=risultato;
		this.setSomma(somma);
	}
	
	public ResDistribuzione(Map<String, Object> map) {
		this.risultato = (String)map.get("risultato");
		this.setSomma((Number)map.get("somma"));
	}

	public String getRisultato() {
		return this.risultato;
	}

	public void setRisultato(String risultato) {
		this.risultato = risultato;
	}	
	
	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer(super.toString());
		bf.append("\n");
		if(this.risultato!=null){
			bf.append("\tRisultato: ["+this.risultato+"]");
		}
		else{
			bf.append("\tRisultato: [undefined]");
		}
		return bf.toString();
	}
}