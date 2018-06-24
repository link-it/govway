/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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
 * EscapeSQLConfiguration
 * 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EscapeSQLConfiguration {

	// Alcuni caratteri possono essere 'escaped' tramite un comune carattere di escape (con in aggiunta una eventuale clausola di escape)
	// Altri devono essere 'escaped' tramite un carattere di escape di default senza la clausola di escape
	
	private List<EscapeWithOtherEscapeChar> escapeWithOtherEscapeChar = new ArrayList<EscapeWithOtherEscapeChar>(); 
	private List<Character> escapeChar = new ArrayList<Character>(); 
	private char escape;
	private boolean useEscapeClausole = false;
	
	
	public char getEscape() {
		return this.escape;
	}
	public void setEscape(char escape) {
		this.escape = escape;
	}
	
	public boolean isUseEscapeClausole() {
		return this.useEscapeClausole;
	}
	public void setUseEscapeClausole(boolean useEscapeClausole) {
		this.useEscapeClausole = useEscapeClausole;
	}
	
	public void addCharacter(char c){
		this.escapeChar.add(c);
	}
	public void addCharacterWithOtherEscapeChar(char c,char escape){
		EscapeWithOtherEscapeChar e = new EscapeWithOtherEscapeChar();
		e.setCharacter(c);
		e.setEscape(escape);
		this.escapeWithOtherEscapeChar.add(e);
	}
	
	public boolean isDefaultEscape(char c){
		if(this.escapeChar!=null && this.escapeChar.size()>0){
			for (Character check : this.escapeChar) {
				if(check.charValue() == c){
					return true;
				}
			}
		}
		return false;
	}
	public boolean isOtherEscape(char c){
		if(this.escapeWithOtherEscapeChar!=null && this.escapeWithOtherEscapeChar.size()>0){
			for (EscapeWithOtherEscapeChar check : this.escapeWithOtherEscapeChar) {
				if(check.getCharacter() == c){
					return true;
				}
			}
		}
		return false;
	}
	public char getOtherEscapeCharacter(char c) throws SQLQueryObjectException{
		if(this.escapeWithOtherEscapeChar!=null && this.escapeWithOtherEscapeChar.size()>0){
			for (EscapeWithOtherEscapeChar check : this.escapeWithOtherEscapeChar) {
				if(check.getCharacter() == c){
					return check.getEscape();
				}
			}
		}
		throw new SQLQueryObjectException("Not exists escape char for character ["+c+"]");
	}
}

class EscapeWithOtherEscapeChar {
	
	private char character;
	private char escape;
	
	public char getCharacter() {
		return this.character;
	}
	public void setCharacter(char character) {
		this.character = character;
	}
	public char getEscape() {
		return this.escape;
	}
	public void setEscape(char escape) {
		this.escape = escape;
	}
	
}
