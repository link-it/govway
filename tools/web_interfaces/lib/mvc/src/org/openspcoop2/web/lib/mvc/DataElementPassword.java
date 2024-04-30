/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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
package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;

import org.openspcoop2.utils.crypt.PasswordGenerator;


/**
 * DataElementPassword
 * 
 * @author Giuliano Pintori (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class DataElementPassword implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean visualizzaBottoneGeneraPassword = false;
	private boolean visualizzaPasswordChiaro = false;
	private PasswordGenerator passwordGenerator = null;
	private int numeroSample = 20;
	private String labelButtonGeneraPw = Costanti.LABEL_MONITOR_BUTTON_GENERA;
	private String tooltipButtonGeneraPw = Costanti.TOOLTIP_MONITOR_BUTTON_GENERA_PWD;
	private boolean visualizzaIconaMostraPassword = true;
	private boolean lockVisualizzaInformazioniCifrate = false;
	private boolean lockReadOnly = false;
	private String lockWarningMessage = null;
	
	public DataElementPassword() {
		this.visualizzaBottoneGeneraPassword = false;
		this.visualizzaPasswordChiaro = false;
		this.passwordGenerator = PasswordGenerator.DEFAULT;
		this.numeroSample = 20;
		this.labelButtonGeneraPw = Costanti.LABEL_MONITOR_BUTTON_GENERA;
		this.tooltipButtonGeneraPw = Costanti.TOOLTIP_MONITOR_BUTTON_GENERA_PWD;
		this.setVisualizzaIconaMostraPassword(true);
		this.setLockVisualizzaInformazioniCifrate(false);
		this.setLockReadOnly(false);
		this.setLockWarningMessage(null);
	}

	public boolean isVisualizzaBottoneGeneraPassword() {
		return this.visualizzaBottoneGeneraPassword;
	}
	public void setVisualizzaBottoneGeneraPassword(boolean visualizzaBottoneGeneraPassword) {
		this.visualizzaBottoneGeneraPassword = visualizzaBottoneGeneraPassword;
	}

	public boolean isVisualizzaPasswordChiaro() {
		return this.visualizzaPasswordChiaro;
	}

	public void setVisualizzaPasswordChiaro(boolean visualizzaPasswordChiaro) {
		this.visualizzaPasswordChiaro = visualizzaPasswordChiaro;
	}

	public PasswordGenerator getPasswordGenerator() {
		return this.passwordGenerator;
	}

	public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
		this.passwordGenerator = passwordGenerator;
	}

	public int getNumeroSample() {
		return this.numeroSample;
	}

	public void setNumeroSample(int numeroSample) {
		this.numeroSample = numeroSample;
	}

	public String getLabelButtonGeneraPassword() {
		return this.labelButtonGeneraPw;
	}

	public void setLabelButtonGeneraPassword(String labelButtonGeneraPw) {
		this.labelButtonGeneraPw = labelButtonGeneraPw;
	}

	public String getTooltipButtonGeneraPassword() {
		return this.tooltipButtonGeneraPw;
	}

	public void setTooltipButtonGeneraPassword(String tooltipButtonGeneraPw) {
		this.tooltipButtonGeneraPw = tooltipButtonGeneraPw;
	}

	public boolean isVisualizzaIconaMostraPassword() {
		return this.visualizzaIconaMostraPassword;
	}

	public void setVisualizzaIconaMostraPassword(boolean visualizzaIconaMostraPassword) {
		this.visualizzaIconaMostraPassword = visualizzaIconaMostraPassword;
	}

	public boolean isLockReadOnly() {
		return this.lockReadOnly;
	}

	public void setLockReadOnly(boolean lockReadOnly) {
		this.lockReadOnly = lockReadOnly;
	}

	public String getLockWarningMessage() {
		return this.lockWarningMessage;
	}

	public void setLockWarningMessage(String lockWarningMessage) {
		this.lockWarningMessage = lockWarningMessage;
	}

	public boolean isLockVisualizzaInformazioniCifrate() {
		return this.lockVisualizzaInformazioniCifrate;
	}

	public void setLockVisualizzaInformazioniCifrate(boolean lockVisualizzaInformazioniCifrate) {
		this.lockVisualizzaInformazioniCifrate = lockVisualizzaInformazioniCifrate;
	}
}
