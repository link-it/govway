/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

import org.slf4j.Logger;

import java.util.Date;

import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;


/**
 * PostOutResponseContext
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class PostOutResponseContext extends OutResponseContext {

	public PostOutResponseContext(Logger logger,IProtocolFactory<?> protocolFactory){
		super(logger,protocolFactory, null);
	}
	
	/** Esito */
	private EsitoTransazione esito;
	
	/** ReturnCode */
	private int returnCode;
	
	/** Eventuale errore di Consegna */
	private String erroreConsegna;
	
	/** Dimensione dei messaggi scambiati */
	private Long inputRequestMessageSize;
	private Long outputRequestMessageSize;
	private Long inputResponseMessageSize;
	private Long outputResponseMessageSize;
	
	/** Data prima della spedizione della risposta */
	private Date dataPrimaSpedizioneRisposta;
	/** Data risposta spedita */
	private Date dataRispostaSpedita;
	
	/** Transazione da aggiornare */
	private Transazione transazioneDaAggiornare;


	public EsitoTransazione getEsito() {
		return this.esito;
	}

	public void setEsito(EsitoTransazione esito) {
		this.esito = esito;
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

	public Long getInputRequestMessageSize() {
		return this.inputRequestMessageSize;
	}

	public void setInputRequestMessageSize(Long inputRequestMessageSize) {
		if(inputRequestMessageSize!=null && inputRequestMessageSize>0){
			this.inputRequestMessageSize = inputRequestMessageSize;
		}
	}

	public Long getOutputRequestMessageSize() {
		return this.outputRequestMessageSize;
	}

	public void setOutputRequestMessageSize(Long outputRequestMessageSize) {
		if(outputRequestMessageSize!=null && outputRequestMessageSize>0){
			this.outputRequestMessageSize = outputRequestMessageSize;
		}
	}

	public Long getInputResponseMessageSize() {
		return this.inputResponseMessageSize;
	}

	public void setInputResponseMessageSize(Long inputResponseMessageSize) {
		if(inputResponseMessageSize!=null && inputResponseMessageSize>0){
			this.inputResponseMessageSize = inputResponseMessageSize;
		}
	}

	public Long getOutputResponseMessageSize() {
		return this.outputResponseMessageSize;
	}

	public void setOutputResponseMessageSize(Long outputResponseMessageSize) {
		if(outputResponseMessageSize!=null && outputResponseMessageSize>0){
			this.outputResponseMessageSize = outputResponseMessageSize;
		}
	}
	
	public Date getDataPrimaSpedizioneRisposta() {
		return this.dataPrimaSpedizioneRisposta;
	}

	public void setDataPrimaSpedizioneRisposta(Date dataPrimaSpedizioneRisposta) {
		this.dataPrimaSpedizioneRisposta = dataPrimaSpedizioneRisposta;
	}
	
	public Date getDataRispostaSpedita() {
		return this.dataRispostaSpedita;
	}

	public void setDataRispostaSpedita(Date dataRispostaSpedita) {
		this.dataRispostaSpedita = dataRispostaSpedita;
	}
	
	public Transazione getTransazioneDaAggiornare() {
		return this.transazioneDaAggiornare;
	}
	public void setTransazioneDaAggiornare(Transazione transazioneDaAggiornare) {
		this.transazioneDaAggiornare = transazioneDaAggiornare;
	}
}
