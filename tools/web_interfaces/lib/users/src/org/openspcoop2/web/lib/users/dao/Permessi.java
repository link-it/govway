/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
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


package org.openspcoop2.web.lib.users.dao;
/**
 * Permessi associabili ad un utente
 *
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public enum Permessi {
	SERVIZI("S"),DIAGNOSTICA("D"),SISTEMA("C"),CODE_MESSAGGI("M"),AUDITING("A"),UTENTI("U"),ACCORDI_COOPERAZIONE("P");
	
	private String value = null;
	private Permessi(String s){
		this.value = s;
	}
	
	public static Permessi toPermessi(String value){
		if("S".equals(value))
			return SERVIZI;
		else if("D".equals(value))
			return DIAGNOSTICA;
		else if("C".equals(value))
			return SISTEMA;
		else if("M".equals(value))
			return CODE_MESSAGGI;
		else if("A".equals(value))
			return AUDITING;
		else if("U".equals(value))
			return UTENTI;
		else if("P".equals(value))
			return ACCORDI_COOPERAZIONE;
		else
			return null;
	}
	
	public static String toString(Permessi value){
		return value.toString();
	}
	public static String toString_HumanReadable(Permessi value){
		return value.toString_HumanReadable();
	}
	
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public String toString_HumanReadable(){
		if("S".equals(this.value))
			return "Servizi";
		else if("D".equals(this.value))
			return "Diagnostica";
		else if("C".equals(this.value))
			return "Sistema";
		else if("M".equals(this.value))
			return "Code Messaggi";
		else if("A".equals(this.value))
			return "Auditing";
		else if("U".equals(this.value))
			return "Utenti";
		else if("P".equals(this.value))
			return "Accordi Cooperazione";
		else
			return null;
	}
}


