/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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


package org.openspcoop2.pdd.core.handlers;

import java.util.Date;

import org.slf4j.Logger;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.connettori.InfoConnettoreUscita;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;


/**
 * InResponseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InResponseContext extends BaseContext {

	public InResponseContext(Logger logger,IProtocolFactory<?> protocolFactory, IState state){
		super.setLogCore(logger);
		super.setProtocolFactory(protocolFactory);
		super.setStato(state);
	}
	
	/** Informazioni sul connettore di uscita */
	private InfoConnettoreUscita connettore;
	
	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;
	
	/** ReturnCode */
	private int returnCode;
	
	/** Eventuale errore di Consegna */
	private String erroreConsegna;
	
	/** Proprieta' di trasporto della risposta */
	private java.util.Properties propertiesRispostaTrasporto;
	
	/** Data accettazione Risposta (prima della lettura della risposta) */
	private Date dataAccettazioneRisposta;
	
	/** Data prima dell'invocazione del connettore send */
	private Date dataPrimaInvocazioneConnettore = null;

	/** Data dopo aver terminato l'invocazione del connettore */
	private Date dataTerminataInvocazioneConnettore = null;
	
	public Date getDataAccettazioneRisposta() {
		return this.dataAccettazioneRisposta;
	}

	public void setDataAccettazioneRisposta(Date dataAccettazioneRisposta) {
		this.dataAccettazioneRisposta = dataAccettazioneRisposta;
	}
	
	public Date getDataPrimaInvocazioneConnettore() {
		return this.dataPrimaInvocazioneConnettore;
	}

	public void setDataPrimaInvocazioneConnettore(Date dataPrimaInvocazioneConnettore) {
		this.dataPrimaInvocazioneConnettore = dataPrimaInvocazioneConnettore;
	}

	public Date getDataTerminataInvocazioneConnettore() {
		return this.dataTerminataInvocazioneConnettore;
	}

	public void setDataTerminataInvocazioneConnettore(Date dataTerminataInvocazioneConnettore) {
		this.dataTerminataInvocazioneConnettore = dataTerminataInvocazioneConnettore;
	}
	
	public InfoConnettoreUscita getConnettore() {
		return this.connettore;
	}

	public void setConnettore(InfoConnettoreUscita connettore) {
		this.connettore = connettore;
	}

	public ProtocolContext getProtocollo() {
		return this.protocollo;
	}

	public void setProtocollo(ProtocolContext p) {
		this.protocollo = p;
	}

	public IntegrationContext getIntegrazione() {
		return this.integrazione;
	}

	public void setIntegrazione(IntegrationContext integrazione) {
		this.integrazione = integrazione;
	}

	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
	}

	public String getErroreConsegna() {
		return this.erroreConsegna;
	}

	public void setErroreConsegna(String erroreConsegna) {
		this.erroreConsegna = erroreConsegna;
	}

	public java.util.Properties getPropertiesRispostaTrasporto() {
		return this.propertiesRispostaTrasporto;
	}

	public void setPropertiesRispostaTrasporto(
			java.util.Properties propertiesRispostaTrasporto) {
		this.propertiesRispostaTrasporto = propertiesRispostaTrasporto;
	}

}
