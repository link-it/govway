/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.statistiche.constants;


/****
 * 
 * Classe per gestire l'enumerazione dei tipi di ricerca statistiche pdnd
 * 
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public enum ModalitaRicercaStatistichePdnd {

	ANDAMENTO_TEMPORALE ("intervalloTemporale"),
	TRACING_ID ("tracingId");
	
	private String value;
	ModalitaRicercaStatistichePdnd(String ruolo) {
		this.value = ruolo;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public String getValue() {
		return this.value;
	}

	public static ModalitaRicercaStatistichePdnd getFromString(String v){
		ModalitaRicercaStatistichePdnd res = null;
		for (ModalitaRicercaStatistichePdnd tmp : values()) {
			if(tmp.getValue().equals(v)){
				res = tmp;
				break;
			}
		}
		return res;
	}
	
	
	public static int getLivello(String modalita) {
		ModalitaRicercaStatistichePdnd modalitaEnum = getFromString(modalita); 
		if(modalitaEnum!=null) {
			switch (modalitaEnum) {
				case ANDAMENTO_TEMPORALE:
				case TRACING_ID:
				default:
					return 1;
			}
		}
		
		return 1;
	}
}
