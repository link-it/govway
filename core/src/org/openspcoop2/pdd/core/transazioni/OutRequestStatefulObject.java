package org.openspcoop2.pdd.core.transazioni;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OutRequestStatefulObject {

	private Date dataUscitaRichiesta;
	private String scenarioCooperazione;
	private String tipoConnettore;
	private String location;
	private List<String> serviziApplicativiErogatore = new ArrayList<String>();
	
	private List<String> eventiGestione = new ArrayList<String>();
	
	public List<String> getServiziApplicativiErogatore() {
		return this.serviziApplicativiErogatore;
	}
	public void addServizioApplicativoErogatore(String servizioApplicativoErogatore) {
		this.serviziApplicativiErogatore.add(servizioApplicativoErogatore);
	}
	public Date getDataUscitaRichiesta() {
		return this.dataUscitaRichiesta;
	}
	public void setDataUscitaRichiesta(Date dataUscitaRichiesta) {
		this.dataUscitaRichiesta = dataUscitaRichiesta;
	}
	public String getScenarioCooperazione() {
		return this.scenarioCooperazione;
	}
	public void setScenarioCooperazione(String scenarioCooperazione) {
		this.scenarioCooperazione = scenarioCooperazione;
	}
	public String getTipoConnettore() {
		return this.tipoConnettore;
	}
	public void setTipoConnettore(String tipoConnettore) {
		this.tipoConnettore = tipoConnettore;
	}
	public String getLocation() {
		return this.location;
	}
	public void setLocation(String location) {
		this.location = location;
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
