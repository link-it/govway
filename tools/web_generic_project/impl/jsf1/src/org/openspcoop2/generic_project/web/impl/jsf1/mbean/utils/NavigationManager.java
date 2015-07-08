package org.openspcoop2.generic_project.web.impl.jsf1.mbean.utils;

import java.io.Serializable;

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
