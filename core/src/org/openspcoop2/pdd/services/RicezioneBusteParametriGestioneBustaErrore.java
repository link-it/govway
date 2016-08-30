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


package org.openspcoop2.pdd.services;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.state.IOpenSPCoopState;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.Busta;

/**
 * Informazioni per la gestione della risposta inviata come errore
 * 
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneBusteParametriGestioneBustaErrore {

	private Busta busta; 
	private IOpenSPCoopState openspcoop;
	private IDSoggetto identitaPdD;
	private OpenSPCoop2Properties propertiesReader;
	private MsgDiagnostico msgDiag;
	private Logger logCore;
	private String profiloGestione;
	private String correlazioneApplicativa;
	private String correlazioneApplicativaRisposta;
	private String servizioApplicativoFruitore;
	private String implementazionePdDMittente;
	private String implementazionePdDDestinatario;
	public Busta getBusta() {
		return this.busta;
	}
	public void setBusta(Busta busta) {
		this.busta = busta;
	}
	public IOpenSPCoopState getOpenspcoop() {
		return this.openspcoop;
	}
	public void setOpenspcoop(IOpenSPCoopState openspcoop) {
		this.openspcoop = openspcoop;
	}
	public IDSoggetto getIdentitaPdD() {
		return this.identitaPdD;
	}
	public void setIdentitaPdD(IDSoggetto identitaPdD) {
		this.identitaPdD = identitaPdD;
	}
	public OpenSPCoop2Properties getPropertiesReader() {
		return this.propertiesReader;
	}
	public void setPropertiesReader(OpenSPCoop2Properties propertiesReader) {
		this.propertiesReader = propertiesReader;
	}
	public MsgDiagnostico getMsgDiag() {
		return this.msgDiag;
	}
	public void setMsgDiag(MsgDiagnostico msgDiag) {
		this.msgDiag = msgDiag;
	}
	public Logger getLogCore() {
		return this.logCore;
	}
	public void setLogCore(Logger logCore) {
		this.logCore = logCore;
	}
	public String getProfiloGestione() {
		return this.profiloGestione;
	}
	public void setProfiloGestione(String profiloGestione) {
		this.profiloGestione = profiloGestione;
	}
	public String getCorrelazioneApplicativa() {
		return this.correlazioneApplicativa;
	}
	public void setCorrelazioneApplicativa(String correlazioneApplicativa) {
		this.correlazioneApplicativa = correlazioneApplicativa;
	}
	public String getServizioApplicativoFruitore() {
		return this.servizioApplicativoFruitore;
	}
	public void setServizioApplicativoFruitore(String servizioApplicativoFruitore) {
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
	}
	public String getImplementazionePdDMittente() {
		return this.implementazionePdDMittente;
	}
	public void setImplementazionePdDMittente(String implementazionePdDMittente) {
		this.implementazionePdDMittente = implementazionePdDMittente;
	}
	public String getImplementazionePdDDestinatario() {
		return this.implementazionePdDDestinatario;
	}
	public void setImplementazionePdDDestinatario(
			String implementazionePdDDestinatario) {
		this.implementazionePdDDestinatario = implementazionePdDDestinatario;
	}
	public String getCorrelazioneApplicativaRisposta() {
		return this.correlazioneApplicativaRisposta;
	}
	public void setCorrelazioneApplicativaRisposta(
			String correlazioneApplicativaRisposta) {
		this.correlazioneApplicativaRisposta = correlazioneApplicativaRisposta;
	}
}
