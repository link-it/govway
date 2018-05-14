package org.openspcoop2.web.monitor.transazioni.bean;



import it.link.pdd.core.transazioni.DumpMessaggio;
import it.link.pdd.core.transazioni.Transazione;

import java.util.List;

import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
 

public class PdDMonitorTransazioneDTO {

	private Transazione transazione;
	private Traccia tracciaRichiesta;
	private boolean tracciaRichiestaReaded;
	private Traccia tracciaRisposta;
	private boolean tracciaRispostaReaded;
	private List<MsgDiagnostico> messaggiDiagnostici;
	private boolean messaggiDiagnosticiReaded;
	private DumpMessaggio dumpRichiesta;
	private boolean dumpRichiestaReaded;
	private DumpMessaggio dumpRisposta;
	private boolean dumpRispostaReaded;
	
	public Transazione getTransazione() {
		return this.transazione;
	}
	public Traccia getTracciaRichiesta() {
		return this.tracciaRichiesta;
	}
	public Traccia getTracciaRisposta() {
		return this.tracciaRisposta;
	}
	public List<MsgDiagnostico> getMessaggiDiagnostici() {
		return this.messaggiDiagnostici;
	}
	public DumpMessaggio getDumpRichiesta() {
		return this.dumpRichiesta;
	}
	public DumpMessaggio getDumpRisposta() {
		return this.dumpRisposta;
	}
	public void setTransazione(Transazione transazione) {
		this.transazione = transazione;
	}
	public void setTracciaRichiesta(Traccia tracciaRichiesta) {
		this.tracciaRichiesta = tracciaRichiesta;
	}
	public void setTracciaRisposta(Traccia tracciaRisposta) {
		this.tracciaRisposta = tracciaRisposta;
	}
	public void setMessaggiDiagnostici(List<MsgDiagnostico> messaggiDiagnostici) {
		this.messaggiDiagnostici = messaggiDiagnostici;
	}
	public void setDumpRichiesta(DumpMessaggio dumpRichiesta) {
		this.dumpRichiesta = dumpRichiesta;
	}
	public void setDumpRisposta(DumpMessaggio dumpRisposta) {
		this.dumpRisposta = dumpRisposta;
	}
	public boolean isTracciaRichiestaReaded() {
		return this.tracciaRichiestaReaded;
	}
	public boolean isTracciaRispostaReaded() {
		return this.tracciaRispostaReaded;
	}
	public boolean isDumpRichiestaReaded() {
		return this.dumpRichiestaReaded;
	}
	public boolean isDumpRispostaReaded() {
		return this.dumpRispostaReaded;
	}
	public void setTracciaRichiestaReaded(boolean tracciaRichiestaReaded) {
		this.tracciaRichiestaReaded = tracciaRichiestaReaded;
	}
	public void setTracciaRispostaReaded(boolean tracciaRispostaReaded) {
		this.tracciaRispostaReaded = tracciaRispostaReaded;
	}
	public void setDumpRichiestaReaded(boolean dumpRichiestaReaded) {
		this.dumpRichiestaReaded = dumpRichiestaReaded;
	}
	public void setDumpRispostaReaded(boolean dumpRispostaReaded) {
		this.dumpRispostaReaded = dumpRispostaReaded;
	}
	public boolean isMessaggiDiagnosticiReaded() {
		return this.messaggiDiagnosticiReaded;
	}
	public void setMessaggiDiagnosticiReaded(boolean messaggiDiagnosticiReaded) {
		this.messaggiDiagnosticiReaded = messaggiDiagnosticiReaded;
	}
}
