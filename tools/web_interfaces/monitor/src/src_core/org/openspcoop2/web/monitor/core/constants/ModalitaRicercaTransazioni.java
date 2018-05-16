package org.openspcoop2.web.monitor.core.constants;


/****
 * 
 * Classe per gestire l'enumerazione dei ruoli utente possibili
 * 
 * 
 * @author pintori
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
