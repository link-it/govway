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
package org.openspcoop2.pdd.logger.transazioni;

import java.util.Date;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.pdd.core.IntegrationContext;
import org.openspcoop2.pdd.core.ProtocolContext;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.state.IState;

/**
 * InformazioniTransazione
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class InformazioniTransazione {

	private Context context;
	private TipoPdD tipoPorta; 
	private IProtocolFactory<?> protocolFactory;

	/** Informazioni protocollo */
	private ProtocolContext protocollo;
	
	/** Informazioni di integrazione */
	private IntegrationContext integrazione;
	
	/** Stato */
	private IState stato;
	
	/** IDModulo */
	private String idModulo;
	
	/** ReturnCode */
	private int returnCode;
	
	/** Data prima della spedizione della risposta */
	private Date dataPrimaSpedizioneRisposta;

	/** Data risposta spedita */
	private Date dataRispostaSpedita;
	
	/** Dimensione dei messaggi scambiati */
	private Long inputRequestMessageSize;
	private Long outputRequestMessageSize;
	private Long inputResponseMessageSize;
	private Long outputResponseMessageSize;
	
	/** Messaggio */
	private OpenSPCoop2Message response;
	
	/** Transazione da aggiornare */
	private Transazione transazioneDaAggiornare;
	
	
	public InformazioniTransazione(){
	}
	public InformazioniTransazione(PostOutResponseContext context){
		this.context = context.getPddContext();
		this.tipoPorta = context.getTipoPorta();
		this.protocolFactory = context.getProtocolFactory();

		this.protocollo = context.getProtocollo();
		
		this.integrazione = context.getIntegrazione();
		
		this.stato = context.getStato();
		
		this.idModulo = context.getIdModulo();
		
		this.returnCode = context.getReturnCode();
		
		this.dataPrimaSpedizioneRisposta = context.getDataPrimaSpedizioneRisposta();

		this.dataRispostaSpedita = context.getDataRispostaSpedita();
		
		this.inputRequestMessageSize = context.getInputRequestMessageSize();
		this.outputRequestMessageSize = context.getOutputRequestMessageSize();
		this.inputResponseMessageSize = context.getInputResponseMessageSize();
		this.outputResponseMessageSize = context.getOutputResponseMessageSize();
		
		this.response = context.getMessaggio();
		
		this.transazioneDaAggiornare = context.getTransazioneDaAggiornare();
	}
	
	
	public Context getContext() {
		return this.context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	public TipoPdD getTipoPorta() {
		return this.tipoPorta;
	}
	public void setTipoPorta(TipoPdD tipoPorta) {
		this.tipoPorta = tipoPorta;
	}
	public IProtocolFactory<?> getProtocolFactory() {
		return this.protocolFactory;
	}
	public void setProtocolFactory(IProtocolFactory<?> protocolFactory) {
		this.protocolFactory = protocolFactory;
	}
	
	public ProtocolContext getProtocollo() {
		return this.protocollo;
	}
	public void setProtocollo(ProtocolContext protocollo) {
		this.protocollo = protocollo;
	}
	
	public IntegrationContext getIntegrazione() {
		return this.integrazione;
	}
	public void setIntegrazione(IntegrationContext integrazione) {
		this.integrazione = integrazione;
	}

	public IState getStato() {
		return this.stato;
	}
	public void setStato(IState stato) {
		this.stato = stato;
	}
	
	public String getIdModulo() {
		return this.idModulo;
	}

	public void setIdModulo(String idModulo) {
		this.idModulo = idModulo;
	}
	
	public int getReturnCode() {
		return this.returnCode;
	}

	public void setReturnCode(int returnCode) {
		this.returnCode = returnCode;
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
	
	public OpenSPCoop2Message getResponse() {
		return this.response;
	}
	public void setResponse(OpenSPCoop2Message response) {
		this.response = response;
	}
	
	public Transazione getTransazioneDaAggiornare() {
		return this.transazioneDaAggiornare;
	}
	public void setTransazioneDaAggiornare(Transazione transazioneDaAggiornare) {
		this.transazioneDaAggiornare = transazioneDaAggiornare;
	}
}
