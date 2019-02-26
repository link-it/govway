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
package org.openspcoop2.web.monitor.core.constants;


/****
 * 
 * Classe per gestire l'enumerazione dei ruoli utente possibili
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public enum ModalitaRicercaTransazioni {

	ANDAMENTO_TEMPORALE ("intervalloTemporale"),
	ID_APPLICATIVO ("idApplicativo"), 
	ID_MESSAGGIO ("idMessaggio"),
	ID_TRANSAZIONE ("idTransazione");
	
	private String value;
	ModalitaRicercaTransazioni(String ruolo) {
		this.value = ruolo;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public String getValue() {
		return this.value;
	}

	public static ModalitaRicercaTransazioni getFromString(String v){
		if(v.equals(ANDAMENTO_TEMPORALE.value)){
			return ANDAMENTO_TEMPORALE;
		} else if(v.equals(ID_APPLICATIVO.value)){
			return ID_APPLICATIVO;
		} else if(v.equals(ID_MESSAGGIO.value)){
			return ID_MESSAGGIO;
		} else if(v.equals(ID_TRANSAZIONE.value)){
			return ID_TRANSAZIONE;
		} 
		
		return null;
		
	}
	
	
	public static String getValue(ModalitaRicercaTransazioni ruolo){
		if(ruolo.equals(ANDAMENTO_TEMPORALE))
			return ANDAMENTO_TEMPORALE.value;
		else if(ruolo.equals(ID_APPLICATIVO))
			return ID_APPLICATIVO.value; 
		else if(ruolo.equals(ID_MESSAGGIO))
			return ID_MESSAGGIO.value;
		else if(ruolo.equals(ID_TRANSAZIONE))
			return ID_TRANSAZIONE.value;
		
		return null;
	}
	
	
}
