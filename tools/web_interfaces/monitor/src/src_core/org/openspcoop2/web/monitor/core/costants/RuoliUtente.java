package org.openspcoop2.web.monitor.core.costants;


/****
 * 
 * Classe per gestire l'enumerazione dei ruoli utente possibili
 * 
 * 
 * @author pintori
 *
 */
public enum RuoliUtente {

	ROLE_USER ("user"),
	ROLE_ADMIN ("amministratore"), 
	ROLE_CONFIG ("configuratore"),
	ROLE_OPERATORE ("operatore");

	
	private String value;
	RuoliUtente(String ruolo) {
		this.value = ruolo;
	}
	
	@Override
	public String toString(){
		return this.value;
	}
	
	public static RuoliUtente getFromString(String v){
		if(v.equals(ROLE_ADMIN.value)){
			return ROLE_ADMIN;
		}else if(v.equals(ROLE_CONFIG.value)){
			return ROLE_CONFIG;
		} else if(v.equals(ROLE_OPERATORE.value)){
			return ROLE_OPERATORE;
		} 
		
		return ROLE_USER;
		
	}
	
	
	public static String getValue(RuoliUtente ruolo){
		if(ruolo.equals(ROLE_ADMIN))
			return ROLE_ADMIN.value;
		else if(ruolo.equals(ROLE_CONFIG))
		return ROLE_CONFIG.value; 
		else if(ruolo.equals(ROLE_OPERATORE))
			return ROLE_OPERATORE.value;
		
		return ROLE_USER.value;
	}
	
	
}
