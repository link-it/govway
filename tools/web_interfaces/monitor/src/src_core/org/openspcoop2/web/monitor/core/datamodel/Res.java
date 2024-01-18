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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Res
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class Res extends ResBase implements Serializable{
	
	private static final long serialVersionUID = 4601647394984864541L;
	private Date risultato;
	private Long id;
	private List<Number> sommeMediaPesata;
	
	public Res() {
		super();
	}
	
	public Res(Map<String, Object> map) { 
		this.risultato = (Date)map.get("risultato");
		this.setSomma((Long)map.get("somma"));
		this.id = this.risultato.getTime();
	}

	public Date getRisultato() {
		return this.risultato;
	}

	public void setRisultato(Date risultato) {
		this.risultato = risultato;
	}

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Number> getSommeMediaPesata() {
		return this.sommeMediaPesata;
	}

	public void setSommeMediaPesata(List<Number> sommeMediaPesata) {
		this.sommeMediaPesata = sommeMediaPesata;
	}
	
	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder(super.toString());
		bf.append("\n");
		if(this.risultato!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			bf.append("\tRisultato-Date: ["+sdf.format(this.risultato)+"]");
		}
		else{
			bf.append("\tRisultatoDate: [undefined]");
		}
		return bf.toString();
	}
	
	
}