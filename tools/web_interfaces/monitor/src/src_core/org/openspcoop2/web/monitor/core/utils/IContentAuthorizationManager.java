package org.openspcoop2.web.monitor.core.utils;

/***
 * 
 * Metodi che definiscono le autorizzazioni sui contenuti della console
 * 
 * @author pintori
 *
 */
public interface IContentAuthorizationManager {

	/**
	 * 
	 * @return Elenco dei path che non richiedono autorizzazioni
	 */
	public String [] getListaPathConsentiti();
	
	/**
	 *  
	 * @return Elenco dei path accessibili per le utenze con ruolo amministratore
	 */
	public String [] getListaPagineRuoloAmministratore();
	
	/**
	 * 
	 * @return Elenco dei path accessibili per le utenze con ruolo configuratore
	 */
	public String [] getListaPagineRuoloConfiguratore();
	
	/**
	 * 
	 * @return Elenco dei path accessibili per le utenze con ruolo operatore
	 */
	public String [] getListaPagineRuoloOperatore();
	
	/**
	 * 
	 * @return Elenco dei path accessibili per ogni modulo caricato sulla console
	 */
	public String[][] getListaPagineModuli(); 
	
	/**
	 * 
	 * @return Elenco dei path da non visualizzare in modalita' compatibile IE8
	 */
	public String [] getListaPagineNoIE8();
}
