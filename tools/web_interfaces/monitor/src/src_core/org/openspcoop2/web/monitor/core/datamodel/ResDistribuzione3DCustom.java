/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it).
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

/**
 * ResDistribuzione3DCustom
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ResDistribuzione3DCustom extends ResDistribuzione implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private String datoCustom;
	
		
	public ResDistribuzione3DCustom() {
		super();
	}
	
	public ResDistribuzione3DCustom(String risultato, String datoCustom, Number somma) {
		super(risultato, somma);
		this.datoCustom = datoCustom;
	}
	
	public String getDatoCustom() {
		return this.datoCustom;
	}

	public void setDatoCustom(String datoCustom) {
		this.datoCustom = datoCustom;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder(super.toString());
		bf.append("\n");
		if(this.datoCustom!=null){
			bf.append("\tInfo: ["+this.datoCustom+"]");
		}
		else{
			bf.append("\tInfo: [undefined]");
		}
		return bf.toString();
	}
}