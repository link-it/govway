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
import java.util.Date;
import java.util.Map;

/**
 * ResDistribuzione
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class ResDistribuzione3D extends ResDistribuzione implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private Date data;
	private String dataFormattata;
	
		
	public ResDistribuzione3D() {
		super();
	}
	
	public ResDistribuzione3D(String risultato, Date data, Number somma) {
		super(risultato, somma);
		this.data = data;
	}
	
	public ResDistribuzione3D(Map<String, Object> map) {
		super(map);
		this.data = (Date) map.get("data");
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}
	
	public void setDataFormattata(String dataFormattata) {
		this.dataFormattata = dataFormattata;
	}
	
	public String getDataFormattata() {
		return this.dataFormattata;
	}

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder(super.toString());
		bf.append("\n");
		if(this.data!=null){
			bf.append("\tData: ["+this.data+"]");
		}
		else{
			bf.append("\tData: [undefined]");
		}
		return bf.toString();
	}
}