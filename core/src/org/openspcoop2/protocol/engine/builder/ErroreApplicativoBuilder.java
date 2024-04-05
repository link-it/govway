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



package org.openspcoop2.protocol.engine.builder;

import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.config.ConfigurationRFC7807;
import org.openspcoop2.message.config.IntegrationErrorReturnConfiguration;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * Classe per la gestione degli errori applicativi :
 * <ul>
 * <li> Tracciamento
 * <li> Diagnostica
 * <li> Eccezioni
 * </ul>
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class ErroreApplicativoBuilder  {


	/** Logger utilizzato per debug. */
	protected Logger log = null;
	protected OpenSPCoop2MessageFactory errorFactory = OpenSPCoop2MessageFactory.getDefaultMessageFactory();
	private IProtocolFactory<?> protocolFactory;
	private IProtocolManager protocolManager;
	private IErroreApplicativoBuilder erroreApplicativoBuilder;
	private org.openspcoop2.message.xml.MessageXMLUtils xmlUtils;
	private IDSoggetto dominio;
	public void setDominio(IDSoggetto dominio) {
		this.dominio = dominio;
	}
	private IDSoggetto mittente;
	public void setMittente(IDSoggetto mittente) {
		this.mittente = mittente;
	}
	private IDServizio servizio;
	public void setServizio(IDServizio servizio) {
		this.servizio = servizio;
	}
	private String idFunzione;
	private ProprietaErroreApplicativo proprietaErroreApplicato;
	public void setProprietaErroreApplicato(
			ProprietaErroreApplicativo proprietaErroreApplicato) {
		this.proprietaErroreApplicato = proprietaErroreApplicato;
	}

	private MessageType messageType;
	public MessageType getMessageType() {
		return this.messageType;
	}
	
	private ConfigurationRFC7807 rfc7807;
	private boolean useProblemRFC7807;
	private IntegrationErrorReturnConfiguration returnConfig;
	private IntegrationFunctionError functionError;
	private String nomePorta;
	
	private DettaglioEccezioneOpenSPCoop2Builder dettaglioEccezioneOpenSPCoop2Builder;

	private String servizioApplicativo;	
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	
	private TipoPdD tipoPdD = null;
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}
	
	private String idTransazione;
	private Context context;

	public ErroreApplicativoBuilder(Logger aLog, IProtocolFactory<?> protocolFactory,
			IDSoggetto dominio,IDSoggetto mittente,IDServizio servizio,String idFunzione,
			ProprietaErroreApplicativo proprietaErroreApplicativo,MessageType messageType,
			ConfigurationRFC7807 rfc7807, IntegrationErrorReturnConfiguration returnConfig,
			IntegrationFunctionError functionError, String nomePorta,
			TipoPdD tipoPdD,String servizioApplicativo,
			String idTransazione, Context context) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(ErroreApplicativoBuilder.class);
		this.protocolFactory = protocolFactory;
		
		this.xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(this.errorFactory);
		
		this.protocolManager = this.protocolFactory.createProtocolManager();
		this.erroreApplicativoBuilder = this.protocolFactory.createErroreApplicativoBuilder();
		
		this.dominio = dominio;
		this.mittente = mittente;
		this.servizio = servizio;
		
		this.idFunzione = idFunzione;
		
		this.proprietaErroreApplicato = proprietaErroreApplicativo;
		
		this.messageType = messageType;

		this.rfc7807 = rfc7807;
		this.useProblemRFC7807 = this.rfc7807!=null;
		this.returnConfig = returnConfig;
		this.functionError = functionError;
		this.nomePorta = nomePorta;
		
		this.dettaglioEccezioneOpenSPCoop2Builder = new DettaglioEccezioneOpenSPCoop2Builder(aLog, protocolFactory);
		
		this.tipoPdD = tipoPdD;
		this.servizioApplicativo = servizioApplicativo;
		
		this.idTransazione = idTransazione;
		
		this.context = context;
	}

	public IProtocolFactory<?> getProtocolFactory(){
		return this.protocolFactory;
	}
	
	private void setEccezioneBuilderParameter(AbstractEccezioneBuilderParameter parameters,
			DettaglioEccezione dettaglio, ParseException parseException){
		
		parameters.setDettaglioEccezionePdD(dettaglio);
		
		parameters.setDominioPorta(this.dominio);
		parameters.setMittente(this.mittente);
		parameters.setServizio(this.servizio);
		
		parameters.setIdFunzione(this.idFunzione);
		
		parameters.setProprieta(this.proprietaErroreApplicato);
		
		parameters.setMessageType(this.messageType);
		
		parameters.setRfc7807(this.rfc7807);
		parameters.setReturnConfig(this.returnConfig);
		parameters.setFunctionError(this.functionError);
		
		parameters.setNomePorta(this.nomePorta);
		if(this.nomePorta==null) {
			if(this.context!=null && this.context.containsKey(org.openspcoop2.core.constants.Costanti.REQUEST_INFO)) {
				Object o = this.context.getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
				if(o!=null && o instanceof RequestInfo) {
					RequestInfo requestInfo = (RequestInfo) o;
					if(requestInfo.getProtocolContext()!=null) {
						this.nomePorta = requestInfo.getProtocolContext().getInterfaceName();
					}
				}
			}
		}
		
		parameters.setTransactionId(this.idTransazione);
		parameters.setContext(this.context);
		
		parameters.setTipoPorta(this.tipoPdD);
		
		parameters.setServizioApplicativo(this.servizioApplicativo);
		
		parameters.setParseException(parseException);
		
	}
	
	private EccezioneProtocolloBuilderParameters getEccezioneProtocolloBuilderParameters(
			Eccezione eccezioneProtocollo,IDSoggetto soggettoProduceEccezione,
			DettaglioEccezione dettaglio, ParseException parseException){
		
		EccezioneProtocolloBuilderParameters parameters = new EccezioneProtocolloBuilderParameters();
		
		this.setEccezioneBuilderParameter(parameters, dettaglio, parseException);
		
		parameters.setEccezioneProtocollo(eccezioneProtocollo);
		parameters.setSoggettoProduceEccezione(soggettoProduceEccezione);

		return parameters;
	}
	
	private EccezioneIntegrazioneBuilderParameters getEccezioneIntegrazioneBuilderParameters(
			ErroreIntegrazione erroreIntegrazione,
			DettaglioEccezione dettaglio, ParseException parseException){
		
		EccezioneIntegrazioneBuilderParameters parameters = new EccezioneIntegrazioneBuilderParameters();
				
		this.setEccezioneBuilderParameter(parameters, dettaglio, parseException);
		
		parameters.setErroreIntegrazione(erroreIntegrazione);
		
		return parameters;
	}
	
	
	
	
	
	
	
	
	
	/* ---------------- MESSAGGI DI ERRORE APPLICATIVO --------------------- */

	public Element toElement(ErroreIntegrazione errore)throws ProtocolException{
		EccezioneIntegrazioneBuilderParameters parameters = 
			this.getEccezioneIntegrazioneBuilderParameters(errore, null, null);
		return this.erroreApplicativoBuilder.toElement(parameters);
	}

	public byte[] toByteArray(ErroreIntegrazione errore) throws ProtocolException{
		Element eccezione = this.toElement(errore);
		if(eccezione == null){
			throw new ProtocolException("Elemento non generato");
		}
		try{
			return this.xmlUtils.toByteArray(eccezione,true);
		} catch(Exception e) {
			this.log.error("XMLBuilder.buildBytes_Eccezione error: "+e.getMessage(),e);
			throw new ProtocolException("toByte failed: "+e.getMessage(),e);
		}
	}


	
	
	/* ---------------- UTILITY PER L'INSERIMENTO DEL MSG DI ERRORE APPLICATIVO NEI DETAILS DI UN SOAP FAULT --------------------- */

	public void insertInSOAPFault(ErroreIntegrazione errore,
			OpenSPCoop2Message msg) throws ProtocolException{
		EccezioneIntegrazioneBuilderParameters parameters = 
			this.getEccezioneIntegrazioneBuilderParameters(errore, null, null);
		this.erroreApplicativoBuilder.insertInSOAPFault(parameters, msg);
	}
	
	public void insertRoutingErrorInSOAPFault(IDSoggetto identitaRouter,String idFunzione,String msgErrore,OpenSPCoop2Message msg) throws ProtocolException{
		this.erroreApplicativoBuilder.insertRoutingErrorInSOAPFault(identitaRouter, idFunzione,msgErrore,msg);
	}	
	




	/* ---------------- UTILITY PER LA COSTRUZIONE DI MESSAGGI DI ERRORE APPLICATIVO --------------------- */
	public OpenSPCoop2Message toMessage(ErroreIntegrazione errore,
			Throwable eProcessamento, ParseException parseException){
		try{
			
			boolean produciDettaglioEccezione = false;
			if(errore.getCodiceErrore().getCodice() < 500 && 
					CodiceErroreIntegrazione.CODICE_5XX_CUSTOM.getCodice()!=errore.getCodiceErrore().getCodice()){ // CODICE_5XX_CUSTOM = 5
				produciDettaglioEccezione = this.protocolManager.isGenerazioneDetailsFaultIntegratione_erroreClient();
			}else{
				produciDettaglioEccezione = this.protocolManager.isGenerazioneDetailsFaultIntegratione_erroreServer();
			}
			
			// uso byte per avere eraser type...
			String msgErroreTrasformato = this.proprietaErroreApplicato.transformFaultMsg(errore,this.protocolFactory);
			String codErroreTrasformato = this.protocolFactory.createTraduttore().
					toCodiceErroreIntegrazioneAsString(errore, this.proprietaErroreApplicato.getFaultPrefixCode(), 
													   this.proprietaErroreApplicato.isFaultAsGenericCode());
				
			// Creo Dettaglio Eccezione
			DettaglioEccezione dettaglioEccezione = null;
			if(produciDettaglioEccezione){
				
				dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(this.dominio, this.tipoPdD, this.idFunzione, 
						codErroreTrasformato, msgErroreTrasformato, false,
						this.returnConfig, this.functionError);
				if(eProcessamento!=null){
					// internamente, se l'informazioni sul details di OpenSPCoop non e' definita viene usato come comportamento quello del servizio applicativo
					boolean informazioniGeneriche = this.proprietaErroreApplicato.isInformazioniGenericheDetailsOpenSPCoop(); 
					this.dettaglioEccezioneOpenSPCoop2Builder.gestioneDettaglioEccezioneIntegrazione(eProcessamento, dettaglioEccezione, informazioniGeneriche);
				}
			}
			
			EccezioneIntegrazioneBuilderParameters parameters = 
				this.getEccezioneIntegrazioneBuilderParameters(errore, dettaglioEccezione, parseException);
			OpenSPCoop2Message msg = this.erroreApplicativoBuilder.toMessage(parameters);
			
			return msg;
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione integrazione",e);
			return this.errorFactory.createFaultMessage(this.messageType, this.useProblemRFC7807, "ErroreDiProcessamento");
		}
	}

	public OpenSPCoop2Message toMessage(Eccezione eccezione,IDSoggetto soggettoProduceEccezione, ParseException parseException){
		return toMessage(eccezione, soggettoProduceEccezione, null, parseException);
	}
	
	public OpenSPCoop2Message toMessage(Eccezione eccezione,IDSoggetto soggettoProduceEccezione,DettaglioEccezione dettaglioEccezione, ParseException parseException){
		try{
			EccezioneProtocolloBuilderParameters parameters =
				this.getEccezioneProtocolloBuilderParameters(eccezione, soggettoProduceEccezione, dettaglioEccezione, parseException);
			return this.erroreApplicativoBuilder.toMessage(parameters);
		} catch (Exception e) {
			this.log.error("Errore durante la costruzione del messaggio di eccezione busta",e);
			return this.errorFactory.createFaultMessage(this.messageType, this.useProblemRFC7807, "ErroreDiProcessamento");
		}
	}

	
}
