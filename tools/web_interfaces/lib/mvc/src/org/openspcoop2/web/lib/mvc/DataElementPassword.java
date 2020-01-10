package org.openspcoop2.web.lib.mvc;

import org.openspcoop2.utils.crypt.PasswordGenerator;

public class DataElementPassword {

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
