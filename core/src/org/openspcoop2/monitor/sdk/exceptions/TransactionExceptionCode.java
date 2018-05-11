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
