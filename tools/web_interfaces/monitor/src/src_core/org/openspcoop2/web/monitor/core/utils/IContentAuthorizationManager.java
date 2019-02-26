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
package org.openspcoop2.web.monitor.core.utils;

/***
 * 
 * Metodi che definiscono le autorizzazioni sui contenuti della console
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
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
