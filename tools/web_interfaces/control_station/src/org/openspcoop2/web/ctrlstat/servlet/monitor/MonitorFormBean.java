/*
 * OpenSPCoop v2 - Customizable SOAP Message Broker 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2016 Link.it srl (http://link.it). 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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


package org.openspcoop2.web.ctrlstat.servlet.monitor;

import org.openspcoop2.pdd.monitor.BustaServizio;
import org.openspcoop2.pdd.monitor.BustaSoggetto;

/**
 * Dati di input del form Monitor
 * 
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author Stefano Corallo (corallo@link.it)
 * @author Sandra Giangrandi (sandra@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class MonitorFormBean {

	private String idMessaggio;
	private String messagePattern;
	private BustaSoggetto mittente;
	private BustaSoggetto destinatario;
	private BustaServizio servizio;
	private String azione;
	private long soglia;
	private String profiloCollaborazione;
	private String method;
	private boolean riscontro;
	private String stato;
	private String pdd;
	private String tipo;
	private String correlazioneApplicativa;

	public String getTipo() {
		return this.tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public boolean isRiscontro() {
		return this.riscontro;
	}

	public void setRiscontro(boolean riscontro) {
		this.riscontro = riscontro;
	}

	public String getStato() {
		return this.stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public String getPdd() {
		return this.pdd;
	}

	public void setPdd(String pdd) {
		this.pdd = pdd;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getProfiloCollaborazione() {
		return this.profiloCollaborazione;
	}

	public void setProfiloCollaborazione(String profiloCollaborazione) {
		this.profiloCollaborazione = profiloCollaborazione;
	}

	public String getIdMessaggio() {
		return this.idMessaggio;
	}

	public void setIdMessaggio(String idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public String getMessagePattern() {
		return this.messagePattern;
	}

	public void setMessagePattern(String messagePattern) {
		this.messagePattern = messagePattern;
	}

	public BustaSoggetto getMittente() {
		return this.mittente;
	}

	public void setMittente(BustaSoggetto mittente) {
		this.mittente = mittente;
	}

	public BustaSoggetto getDestinatario() {
		return this.destinatario;
	}

	public void setDestinatario(BustaSoggetto destinatario) {
		this.destinatario = destinatario;
	}

	public BustaServizio getServizio() {
		return this.servizio;
	}

	public void setServizio(BustaServizio servizio) {
		this.servizio = servizio;
	}

	public String getAzione() {
		return this.azione;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public long getSoglia() {
		return this.soglia;
	}

	public void setSoglia(long soglia) {
		this.soglia = soglia;
	}

	public String getCorrelazioneApplicativa() {
		return this.correlazioneApplicativa;
	}

	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.correlazioneApplicativa = correlazioneApplicativa;
	}

}
