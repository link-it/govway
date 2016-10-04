/*
 * OpenSPCoop - Customizable API Gateway 
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



package org.openspcoop2.protocol.engine.builder;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.eccezione.details.DettaglioEccezione;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.AbstractEccezioneBuilderParameter;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.EccezioneIntegrazioneBuilderParameters;
import org.openspcoop2.protocol.sdk.EccezioneProtocolloBuilderParameters;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.IErroreApplicativoBuilder;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.config.IProtocolManager;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.utils.LoggerWrapperFactory;
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
	protected OpenSPCoop2MessageFactory fac = OpenSPCoop2MessageFactory.getMessageFactory();
	private IProtocolFactory protocolFactory;
	private IProtocolManager protocolManager;
	private IErroreApplicativoBuilder erroreApplicativoBuilder;
	private org.openspcoop2.message.XMLUtils xmlUtils;
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

	private SOAPVersion soapVersion;
	private DettaglioEccezioneOpenSPCoop2Builder dettaglioEccezioneOpenSPCoop2Builder;

	private String servizioApplicativo;	
	public void setServizioApplicativo(String servizioApplicativo) {
		this.servizioApplicativo = servizioApplicativo;
	}
	
	private TipoPdD tipoPdD = null;
	public void setTipoPdD(TipoPdD tipoPdD) {
		this.tipoPdD = tipoPdD;
	}

	public ErroreApplicativoBuilder(Logger aLog, IProtocolFactory protocolFactory,
			IDSoggetto dominio,IDSoggetto mittente,IDServizio servizio,String idFunzione,
			ProprietaErroreApplicativo proprietaErroreApplicativo,SOAPVersion soapVersion,
			TipoPdD tipoPdD,String servizioApplicativo) throws ProtocolException{
		if(aLog!=null)
			this.log = aLog;
		else
			this.log = LoggerWrapperFactory.getLogger(ErroreApplicativoBuilder.class);
		this.protocolFactory = protocolFactory;
		
		this.xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
		
		this.protocolManager = this.protocolFactory.createProtocolManager();
		this.erroreApplicativoBuilder = this.protocolFactory.createErroreApplicativoBuilder();
		
		this.dominio = dominio;
		this.mittente = mittente;
		this.servizio = servizio;
		
		this.idFunzione = idFunzione;
		
		this.proprietaErroreApplicato = proprietaErroreApplicativo;
		
		this.soapVersion = soapVersion;

		this.dettaglioEccezioneOpenSPCoop2Builder = new DettaglioEccezioneOpenSPCoop2Builder(aLog, protocolFactory);
		
		this.tipoPdD = tipoPdD;
		this.servizioApplicativo = servizioApplicativo;
	}

	public IProtocolFactory getProtocolFactory(){
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
		
		parameters.setVersioneSoap(this.soapVersion);
		
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
			if(errore.getCodiceErrore().getCodice() < 500){
				produciDettaglioEccezione = this.protocolManager.isGenerazioneDetailsSOAPFaultIntegratione_erroreClient();
			}else{
				produciDettaglioEccezione = this.protocolManager.isGenerazioneDetailsSOAPFaultIntegratione_erroreServer();
			}
			
			// uso byte per avere eraser type...
			String msgErroreTrasformato = this.proprietaErroreApplicato.transformFaultMsg(errore,this.protocolFactory);
			String codErroreTrasformato = this.protocolFactory.createTraduttore().
					toString(errore.getCodiceErrore(), this.proprietaErroreApplicato.getFaultPrefixCode(), 
													   this.proprietaErroreApplicato.isFaultAsGenericCode());
				
			// Creo Dettaglio Eccezione
			DettaglioEccezione dettaglioEccezione = null;
			if(produciDettaglioEccezione){
				
				dettaglioEccezione = this.dettaglioEccezioneOpenSPCoop2Builder.buildDettaglioEccezione(this.dominio, this.tipoPdD, this.idFunzione, 
						codErroreTrasformato, msgErroreTrasformato, false);
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
			return this.fac.createFaultMessage(this.soapVersion, "ErroreDiProcessamento");
		}
	}

	public OpenSPCoop2Message toMessage(Eccezione eccezione,IDSoggetto soggettoProduceEccezione, ParseException parseException){
		EccezioneProtocolloBuilderParameters parameters =
			this.getEccezioneProtocolloBuilderParameters(eccezione, soggettoProduceEccezione , null, parseException);
		try{
			return this.erroreApplicativoBuilder.toMessage(parameters);
		}catch(Exception e){
			this.log.error("Errore durante la costruzione del messaggio di eccezione busta",e);
			return this.fac.createFaultMessage(this.soapVersion, "ErroreDiProcessamento");
		}
	}
	
	public OpenSPCoop2Message toMessage(Eccezione eccezione,IDSoggetto soggettoProduceEccezione,DettaglioEccezione dettaglioEccezione, ParseException parseException){
		try{
			EccezioneProtocolloBuilderParameters parameters =
				this.getEccezioneProtocolloBuilderParameters(eccezione, soggettoProduceEccezione, dettaglioEccezione, parseException);
			return this.erroreApplicativoBuilder.toMessage(parameters);
		} catch (Exception e) {
			this.log.error("Errore durante la costruzione del messaggio di eccezione busta",e);
			return this.fac.createFaultMessage(this.soapVersion, "ErroreDiProcessamento");
		}
	}

	
}
