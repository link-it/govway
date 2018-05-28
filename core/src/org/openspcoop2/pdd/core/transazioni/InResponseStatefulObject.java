package org.openspcoop2.pdd.core.transazioni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InResponseStatefulObject {

	private Date dataUscitaRichiesta;
	private Date dataAccettazioneRisposta;
	private Date dataIngressoRisposta;
	private String returnCode;
	private String location;
	private String faultIntegrazione;
	private String faultCooperazione;
	
	private List<String> eventiGestione = new ArrayList<String>();
	
	public Date getDataAccettazioneRisposta() {
		return this.dataAccettazioneRisposta;
	}
	public void setDataAccettazioneRisposta(Date dataAccettazioneRisposta) {
		this.dataAccettazioneRisposta = dataAccettazioneRisposta;
	}
	public Date getDataIngressoRisposta() {
		return this.dataIngressoRisposta;
	}
	public void setDataIngressoRisposta(Date dataIngressoRisposta) {
		this.dataIngressoRisposta = dataIngressoRisposta;
	}
	public Date getDataUscitaRichiesta() {
		return this.dataUscitaRichiesta;
	}
	public void setDataUscitaRichiesta(Date dataUscitaRichiesta) {
		this.dataUscitaRichiesta = dataUscitaRichiesta;
	}
	public String getReturnCode() {
		return this.returnCode;
	}
	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getFaultIntegrazione() {
		return this.faultIntegrazione;
	}
	public void setFaultIntegrazione(String faultIntegrazione) {
		this.faultIntegrazione = faultIntegrazione;
	}
	public String getFaultCooperazione() {
		return this.faultCooperazione;
	}
	public void setFaultCooperazione(String faultCooperazione) {
		this.faultCooperazione = faultCooperazione;
	}
	public List<String> getEventiGestione() {
		return this.eventiGestione;
	}
	public void addEventoGestione(String evento) {
		if(evento.contains(",")){
			throw new RuntimeException("EventoGestione ["+evento+"] non deve contenere il carattere ','");
		}
		this.eventiGestione.add(evento);
	}
	
}
