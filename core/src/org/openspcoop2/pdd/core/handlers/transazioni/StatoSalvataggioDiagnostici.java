package org.openspcoop2.pdd.core.handlers.transazioni;

import java.util.HashMap;

import org.openspcoop2.core.transazioni.constants.DiagnosticColumnType;

public class StatoSalvataggioDiagnostici {

	private String informazione;
	private HashMap<DiagnosticColumnType, String> informazioneCompressa = new HashMap<>();
	private boolean errore;
	private boolean compresso;
	
	public String getInformazione() {
		return this.informazione;
	}

	public void setInformazione(String informazione) {
		this.informazione = informazione;
	}

	public boolean isErrore() {
		return this.errore;
	}

	public void setErrore(boolean errore) {
		this.errore = errore;
	}

	public boolean isCompresso() {
		return this.compresso;
	}

	public void setCompresso(boolean compresso) {
		this.compresso = compresso;
	}
	
	public HashMap<DiagnosticColumnType, String> getInformazioneCompressa() {
		return this.informazioneCompressa;
	}

	public void setInformazioneCompressa(HashMap<DiagnosticColumnType, String> informazioneCompressa) {
		this.informazioneCompressa = informazioneCompressa;
	}
}
