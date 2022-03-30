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
package org.openspcoop2.utils.sql;

import java.util.ArrayList;
import java.util.List;

/**
 * Case
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class Case {

	private List<String> condizioni = new ArrayList<String>();
	private List<String> valori = new ArrayList<String>();
	private CastColumnType tipoColonna;
	private int dimensioneColonna = 255;
	private String valoreDefault;

	private boolean stringValueType = false;

	public Case(CastColumnType type) throws SQLQueryObjectException {
		this.stringValueType = false;
		this.tipoColonna = type;
		if(this.tipoColonna==null) {
			throw new SQLQueryObjectException("Tipo Colonna non fornito");
		}
	}
	public Case(CastColumnType type, String valoreDefault) throws SQLQueryObjectException {
		this(type, false, valoreDefault);
	}
	public Case(CastColumnType type, boolean stringValueType, String valoreDefault) throws SQLQueryObjectException{
		this.stringValueType = stringValueType;
		if(valoreDefault==null) {
			throw new SQLQueryObjectException("Valore di default non fornito");
		}
		this.valoreDefault = valoreDefault;
		
		this.tipoColonna = type;
		if(this.tipoColonna==null) {
			throw new SQLQueryObjectException("Tipo Colonna non fornito");
		}
	}
	
	public void addCase(String condizione, String valore) throws SQLQueryObjectException {
		this.condizioni.add(condizione);
		this.valori.add(valore);
		
		if(condizione==null) {
			throw new SQLQueryObjectException("Condizione non fornita");
		}
		if(valore==null) {
			throw new SQLQueryObjectException("Valore non fornito");
		}
	}

	public List<String> getCondizioni() {
		return this.condizioni;
	}

	public List<String> getValori() {
		return this.valori;
	}
	
	public boolean isStringValueType() {
		return this.stringValueType;
	}

	public void setStringValueType(boolean stringValueType) {
		this.stringValueType = stringValueType;
	}
	
	public String getValoreDefault() {
		return this.valoreDefault;
	}
	
	public CastColumnType getTipoColonna() {
		return this.tipoColonna;
	}

	public int getDimensioneColonna() {
		return this.dimensioneColonna;
	}
	public void setDimensioneColonna(int dimensioneColonna) {
		this.dimensioneColonna = dimensioneColonna;
	}
}
