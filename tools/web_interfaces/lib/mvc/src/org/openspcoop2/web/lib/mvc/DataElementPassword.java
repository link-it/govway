package org.openspcoop2.web.lib.mvc;

import java.io.Serializable;

import org.openspcoop2.utils.crypt.PasswordGenerator;

/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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

/**
 * DataElementPassword
 * 
 * @author Giuliano Puntori (pintori@link.it)
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
	private String labelButtonGeneraPassword = Costanti.LABEL_MONITOR_BUTTON_GENERA;
	private String tooltipButtonGeneraPassword = Costanti.TOOLTIP_MONITOR_BUTTON_GENERA_PWD;
	
	public DataElementPassword() {
		this.visualizzaBottoneGeneraPassword = false;
		this.visualizzaPasswordChiaro = false;
		this.passwordGenerator = PasswordGenerator.DEFAULT;
		this.numeroSample = 20;
		this.labelButtonGeneraPassword = Costanti.LABEL_MONITOR_BUTTON_GENERA;
		this.tooltipButtonGeneraPassword = Costanti.TOOLTIP_MONITOR_BUTTON_GENERA_PWD;
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
		return this.labelButtonGeneraPassword;
	}

	public void setLabelButtonGeneraPassword(String labelButtonGeneraPassword) {
		this.labelButtonGeneraPassword = labelButtonGeneraPassword;
	}

	public String getTooltipButtonGeneraPassword() {
		return this.tooltipButtonGeneraPassword;
	}

	public void setTooltipButtonGeneraPassword(String tooltipButtonGeneraPassword) {
		this.tooltipButtonGeneraPassword = tooltipButtonGeneraPassword;
	}
}
