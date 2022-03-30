/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.generic_project.web.impl.jsf1.mbean.utils;

import java.io.Serializable;

/***
 * 
 * NavigationManager
 * 
 * @author Pintori Giuliano (pintori@link.it)
 *  @author $Author$
 * @version $Rev$, $Date$ 
 * 
 */
public class NavigationManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	// outcome jsf per la navigazione, 
	private String deleteOutcome = null;
	private String inviaOutcome = null;
	private String modificaOutcome = null;
	private String dettaglioOutcome = null;
	private String nuovoOutcome = null;
	private String menuActionOutcome = null;
	private String filtraOutcome = null;
	private String restoreSearchOutcome = null;
	private String annullaOutcome = null;
	private String resetOutcome = null;
	
	
	public NavigationManager(){
		resetOutcomes();
	}
	
	public void resetOutcomes(){
		this.annullaOutcome = null;
		this.deleteOutcome = null;
		this.dettaglioOutcome = null;
		this.filtraOutcome = null;
		this.inviaOutcome = null;
		this.menuActionOutcome= null;
		this.modificaOutcome = null;
		this.nuovoOutcome = null;
		this.resetOutcome = null;
		this.restoreSearchOutcome = null;
	}
	
	public String getDeleteOutcome() {
		return this.deleteOutcome;
	}
	public void setDeleteOutcome(String deleteOutcome) {
		this.deleteOutcome = deleteOutcome;
	}
	public String getInviaOutcome() {
		return this.inviaOutcome;
	}
	public void setInviaOutcome(String inviaOutcome) {
		this.inviaOutcome = inviaOutcome;
	}
	public String getModificaOutcome() {
		return this.modificaOutcome;
	}
	public void setModificaOutcome(String modificaOutcome) {
		this.modificaOutcome = modificaOutcome;
	}
	public String getDettaglioOutcome() {
		return this.dettaglioOutcome;
	}
	public void setDettaglioOutcome(String dettaglioOutcome) {
		this.dettaglioOutcome = dettaglioOutcome;
	}
	public String getNuovoOutcome() {
		return this.nuovoOutcome;
	}
	public void setNuovoOutcome(String nuovoOutcome) {
		this.nuovoOutcome = nuovoOutcome;
	}
	public String getMenuActionOutcome() {
		return this.menuActionOutcome;
	}
	public void setMenuActionOutcome(String menuActionOutcome) {
		this.menuActionOutcome = menuActionOutcome;
	}
	public String getFiltraOutcome() {
		return this.filtraOutcome;
	}
	public void setFiltraOutcome(String filtraOutcome) {
		this.filtraOutcome = filtraOutcome;
	}
	public String getRestoreSearchOutcome() {
		return this.restoreSearchOutcome;
	}
	public void setRestoreSearchOutcome(String restoreSearchOutcome) {
		this.restoreSearchOutcome = restoreSearchOutcome;
	}
	public String getAnnullaOutcome() {
		return this.annullaOutcome;
	}
	public void setAnnullaOutcome(String annullaOutcome) {
		this.annullaOutcome = annullaOutcome;
	}
	public String getResetOutcome() {
		return this.resetOutcome;
	}
	public void setResetOutcome(String resetOutcome) {
		this.resetOutcome = resetOutcome;
	}

}
