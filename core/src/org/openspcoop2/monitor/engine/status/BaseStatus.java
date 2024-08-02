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
package org.openspcoop2.monitor.engine.status;

import org.openspcoop2.monitor.engine.constants.SondaStatus;

/**
 * BaseStatus
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class BaseStatus implements IStatus {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L; 

	private String idNodoRuntime;
	
	private String nome;

	private SondaStatus stato;
	
	private String descrizione;

	public BaseStatus() {
		// nop
	}

	@Override
	public String getIdNodoRuntime() {
		return this.idNodoRuntime;
	}

	@Override
	public void setIdNodoRuntime(String idNodoRuntime) {
		this.idNodoRuntime = idNodoRuntime;
	}
	
	@Override
	public String getNome() {
		return this.nome;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public SondaStatus getStato() {
		return this.stato;
	}

	@Override
	public void setStato(SondaStatus stato) {
		this.stato = stato;
	}

	@Override
	public String getDescrizione() {
		return this.descrizione;
	}

	@Override
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getStatoRawEnumValue() {
		if(this.stato == null){
	    	return null;
	    }else{
	    	return this.stato.toString();
	    }
	}

	public void setStatoRawEnumValue(String v) {
		this.stato = (SondaStatus) SondaStatus.toEnumConstantFromString(v);
	}

 

}
