package org.openspcoop2.testsuite.units;

import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.protocol.sdk.constants.Inoltro;

public class CooperazioneBaseInformazioni {


	private IDSoggetto mittente;
	private IDSoggetto destinatario;
	private boolean confermaRicezione;
	private String inoltro;
	private Inoltro inoltroSdk;
	
	private int servizio_versioneDefault = 1;
	
	private String profiloCollaborazione_protocollo_oneway;
	private String profiloCollaborazione_protocollo_sincrono;
	private String profiloCollaborazione_protocollo_asincronoAsimmetrico;
	private String profiloCollaborazione_protocollo_asincronoSimmetrico;
	
	
	public IDSoggetto getMittente() {
		return this.mittente;
	}
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	public IDSoggetto getDestinatario() {
		return this.destinatario;
	}
	public void setDestinatario(IDSoggetto destinatario) {
		this.destinatario = destinatario;
	}
	public boolean isConfermaRicezione() {
		return this.confermaRicezione;
	}
	public void setConfermaRicezione(boolean confermaRicezione) {
		this.confermaRicezione = confermaRicezione;
	}
	public String getInoltro() {
		return this.inoltro;
	}
	public void setInoltro(String inoltro) {
		this.inoltro = inoltro;
	}
	public Inoltro getInoltroSdk() {
		return this.inoltroSdk;
	}
	public void setInoltroSdk(Inoltro inoltroSdk) {
		this.inoltroSdk = inoltroSdk;
	}
	public int getServizio_versioneDefault() {
		return this.servizio_versioneDefault;
	}
	public void setServizio_versioneDefault(int servizio_versioneDefault) {
		this.servizio_versioneDefault = servizio_versioneDefault;
	}
	public String getProfiloCollaborazione_protocollo_oneway() {
		return this.profiloCollaborazione_protocollo_oneway;
	}
	public void setProfiloCollaborazione_protocollo_oneway(
			String profiloCollaborazione_protocollo_oneway) {
		this.profiloCollaborazione_protocollo_oneway = profiloCollaborazione_protocollo_oneway;
	}
	public String getProfiloCollaborazione_protocollo_sincrono() {
		return this.profiloCollaborazione_protocollo_sincrono;
	}
	public void setProfiloCollaborazione_protocollo_sincrono(
			String profiloCollaborazione_protocollo_sincrono) {
		this.profiloCollaborazione_protocollo_sincrono = profiloCollaborazione_protocollo_sincrono;
	}
	public String getProfiloCollaborazione_protocollo_asincronoAsimmetrico() {
		return this.profiloCollaborazione_protocollo_asincronoAsimmetrico;
	}
	public void setProfiloCollaborazione_protocollo_asincronoAsimmetrico(
			String profiloCollaborazione_protocollo_asincronoAsimmetrico) {
		this.profiloCollaborazione_protocollo_asincronoAsimmetrico = profiloCollaborazione_protocollo_asincronoAsimmetrico;
	}
	public String getProfiloCollaborazione_protocollo_asincronoSimmetrico() {
		return this.profiloCollaborazione_protocollo_asincronoSimmetrico;
	}
	public void setProfiloCollaborazione_protocollo_asincronoSimmetrico(
			String profiloCollaborazione_protocollo_asincronoSimmetrico) {
		this.profiloCollaborazione_protocollo_asincronoSimmetrico = profiloCollaborazione_protocollo_asincronoSimmetrico;
	}
}
