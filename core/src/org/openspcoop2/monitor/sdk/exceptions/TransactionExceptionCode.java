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
package org.openspcoop2.monitor.sdk.exceptions;

/**
 * TransactionExceptionCode
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public enum TransactionExceptionCode {

	DEL_RES_NOT_EXIST(0),
	UPD_RES_NOT_EXIST(1),
	ADD_RES_EXIST(2),
	NOT_FOUND(3),
	GENERIC_ERROR(4);
	
	private static String[] MSG = {
		"La risorsa che si vuole eliminare non esiste", 
		"La risorsa che si vuole aggiornare non esiste", 
		"La risorsa che si vuole aggiungere è già esistente",
		"La transazione richieste non esiste",
		"Errore generico"
		};
	
	private int value = -1;
	TransactionExceptionCode(int code){
		this.value = code;
	}
	
	public int getValue() {
		return this.value;
	}
	
	public String getMessage(){
		return MSG[this.value];
	}
	
	public boolean equals(TransactionExceptionCode code){
		return this.value == code.getValue();
	}
}
