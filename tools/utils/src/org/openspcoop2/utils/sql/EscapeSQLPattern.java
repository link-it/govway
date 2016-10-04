/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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

/**
 * EscapeSQLPattern
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EscapeSQLPattern {

	private String escapeValue;
	private boolean useEscapeClausole = false;
	private char escapeClausole;
	
	public String getEscapeValue() {
		return this.escapeValue;
	}
	public void setEscapeValue(String escapeValue) {
		this.escapeValue = escapeValue;
	}
	public boolean isUseEscapeClausole() {
		return this.useEscapeClausole;
	}
	public void setUseEscapeClausole(boolean useEscapeClausole) {
		this.useEscapeClausole = useEscapeClausole;
	}
	public char getEscapeClausole() {
		return this.escapeClausole;
	}
	public void setEscapeClausole(char escapeClausole) {
		this.escapeClausole = escapeClausole;
	}

}
