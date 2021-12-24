/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.transazioni.bean;

import java.util.List;

import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
 
/**
 * PdDMonitorTransazioneDTO
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
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
