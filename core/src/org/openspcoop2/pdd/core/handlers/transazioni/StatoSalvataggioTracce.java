package org.openspcoop2.pdd.core.handlers.transazioni;

public class StatoSalvataggioTracce {

	private String informazione;
	private String informazioneCompressa;

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
	public String getInformazioneCompressa() {
		return this.informazioneCompressa;
	}

	public void setInformazioneCompressa(String informazioneCompressa) {
		this.informazioneCompressa = informazioneCompressa;
	}
}
