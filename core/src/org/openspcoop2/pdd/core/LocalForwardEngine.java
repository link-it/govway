/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.dom.DOMSource;

import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.ServizioApplicativo;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.exception.ParseException;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.message.xml.MessageXMLUtils;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.handlers.HandlerException;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.core.transazioni.TransactionContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.pdd.services.ServicesUtils;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneBusteExternalErrorGenerator;
import org.openspcoop2.pdd.services.error.RicezioneContenutiApplicativiInternalErrorGenerator;
import org.openspcoop2.protocol.engine.constants.Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.SecurityInfo;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriCooperazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.date.DateUtils;
import org.openspcoop2.utils.digest.IDigestReader;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Node;

/**	
 * LocalForwardEngine
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class LocalForwardEngine {

	private LocalForwardParameter localForwardParameter;
	private EJBUtils ejbUtils = null;
	private RicezioneContenutiApplicativiInternalErrorGenerator generatoreErrorePortaDelegata = null;
	private RicezioneBusteExternalErrorGenerator generatoreErrorePortaApplicativa = null;
	private Busta busta = null;
	private IDPortaApplicativa idPA = null;
	private PortaApplicativa pa = null;
	private RichiestaApplicativa richiestaApplicativa = null;
	private RichiestaDelegata richiestaDelegata = null;
	private PortaDelegata pd =null;
	private ServizioApplicativo sa = null;
	private OpenSPCoop2Properties propertiesReader = null;
	private RequestInfo requestInfo = null;
	private IntegrationFunctionError _integrationFunctionError;
	private IntegrationFunctionError getIntegrationFunctionError(PdDContext pddContext) {
		if(this._integrationFunctionError!=null) {
			return this._integrationFunctionError;
		}
		else {
			return AbstractErrorGenerator.getIntegrationInternalError(pddContext); // default
		}
	}
	
	
	/* ***** COSTRUTTORE ******** */
	
	public LocalForwardEngine(LocalForwardParameter localForwardParameter) throws LocalForwardException{
		this.localForwardParameter = localForwardParameter;
		try{
			
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
			
			this.richiestaDelegata = this.localForwardParameter.getRichiestaDelegata();
			this.richiestaApplicativa = this.localForwardParameter.getRichiestaApplicativa();
			
			((OpenSPCoopState)this.localForwardParameter.getOpenspcoopstate()).setIDMessaggioSessione(this.localForwardParameter.getIdRequest());
			IState state = ((OpenSPCoopState)this.localForwardParameter.getOpenspcoopstate()).getStatoRichiesta();
			
			String profiloGestione = null;
			IDSoggetto soggettoFruitore = null;
			IDServizio idServizio = null;
			ProprietaErroreApplicativo fault = null;
			String servizioApplicativo = null;
			String scenarioCooperazione = null;
			if(this.richiestaApplicativa!=null){
				profiloGestione = this.richiestaApplicativa.getProfiloGestione();
				soggettoFruitore = this.richiestaApplicativa.getSoggettoFruitore();
				idServizio = this.richiestaApplicativa.getIDServizio();
				fault = null;
				servizioApplicativo = this.richiestaApplicativa.getServizioApplicativo();
				scenarioCooperazione = this.richiestaApplicativa.getScenario();
				
				this.idPA = this.richiestaApplicativa.getIdPortaApplicativa();
				
				this.busta = this.localForwardParameter.getBusta();
								
			}else{
				profiloGestione = this.richiestaDelegata.getProfiloGestione();
				soggettoFruitore = this.richiestaDelegata.getIdSoggettoFruitore();
				idServizio = this.richiestaDelegata.getIdServizio();
				fault = this.richiestaDelegata.getFault();
				servizioApplicativo = this.richiestaDelegata.getServizioApplicativo();
				if(ProfiloDiCollaborazione.ONEWAY.equals(this.localForwardParameter.getInfoServizio().getProfiloDiCollaborazione())){
					scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;
				}
				else if(ProfiloDiCollaborazione.SINCRONO.equals(this.localForwardParameter.getInfoServizio().getProfiloDiCollaborazione())){
					scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;
				}
				
				this.idPA = this.localForwardParameter.getIdPortaApplicativaIndirizzata();
				
				this.richiestaApplicativa = 
						new RichiestaApplicativa(soggettoFruitore,  
								this.localForwardParameter.getIdModulo(), this.localForwardParameter.getIdentitaPdD(),this.idPA);
				this.richiestaApplicativa.setIdCorrelazioneApplicativa(this.localForwardParameter.getIdCorrelazioneApplicativa());
				this.richiestaApplicativa.setProfiloGestione(profiloGestione);
				this.richiestaApplicativa.setRicevutaAsincrona(false);
				this.richiestaApplicativa.setLocalForward(true);
				this.richiestaApplicativa.setScenario(scenarioCooperazione);
				if(!CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(servizioApplicativo)){
					this.richiestaApplicativa.setIdentitaServizioApplicativoFruitore(servizioApplicativo);
				}
								
				this.busta = new Busta(this.localForwardParameter.getProtocolFactory(), 
						this.localForwardParameter.getInfoServizio(), 
						soggettoFruitore,idServizio.getSoggettoErogatore(), 
						this.localForwardParameter.getIdRequest(), false);
				this.busta.setID(this.localForwardParameter.getIdRequest());
			}
			
			this.pd = this.localForwardParameter.getConfigurazionePdDReader().getPortaDelegata(this.richiestaDelegata.getIdPortaDelegata(), this.requestInfo);	
			try{
				IDServizioApplicativo idSA = new IDServizioApplicativo();
				idSA.setNome(this.richiestaDelegata.getServizioApplicativo());
				idSA.setIdSoggettoProprietario(this.richiestaDelegata.getIdSoggettoFruitore());
				this.sa = this.localForwardParameter.getConfigurazionePdDReader().getServizioApplicativo(idSA, this.requestInfo);
			}catch(DriverConfigurazioneNotFound e){
				if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.richiestaDelegata.getServizioApplicativo())==false)
					throw e;
			}
			
			this.requestInfo = (RequestInfo) this.localForwardParameter.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.REQUEST_INFO);
			
			if(fault==null){
				fault = this.propertiesReader.getProprietaGestioneErrorePD(this.localForwardParameter.getProtocolFactory().createProtocolManager());
				fault.setDominio(this.localForwardParameter.getIdentitaPdD().getCodicePorta());
				fault.setIdModulo(this.localForwardParameter.getIdModulo());
				this.localForwardParameter.getConfigurazionePdDReader().aggiornaProprietaGestioneErrorePD(fault, this.sa);
			}
			
			this.ejbUtils = new EJBUtils(this.localForwardParameter.getIdentitaPdD(), TipoPdD.DELEGATA, 
					this.localForwardParameter.getIdModulo(), this.localForwardParameter.getIdRequest(), this.localForwardParameter.getIdRequest(), Costanti.OUTBOX, 
					this.localForwardParameter.getOpenspcoopstate(), this.localForwardParameter.getMsgDiag(), false, 
					this.localForwardParameter.getImplementazionePdDMittente(), this.localForwardParameter.getImplementazionePdDDestinatario(), 
					profiloGestione, this.localForwardParameter.getPddContext());
			this.ejbUtils.setOneWayVersione11(this.localForwardParameter.isOneWayVersione11());
			this.ejbUtils.setPortaDiTipoStateless_esclusoOneWay11(this.localForwardParameter.isStateless() && !this.localForwardParameter.isOneWayVersione11());
			
			this.generatoreErrorePortaDelegata = new RicezioneContenutiApplicativiInternalErrorGenerator(this.localForwardParameter.getLog(), 
					this.localForwardParameter.getIdModulo(), this.requestInfo);
			this.generatoreErrorePortaDelegata.updateInformazioniCooperazione(soggettoFruitore, idServizio);
			this.generatoreErrorePortaDelegata.updateInformazioniCooperazione(servizioApplicativo);
			this.generatoreErrorePortaDelegata.updateProprietaErroreApplicativo(fault);
				
			this.generatoreErrorePortaApplicativa = new RicezioneBusteExternalErrorGenerator(this.localForwardParameter.getLog(), 
					this.localForwardParameter.getIdModulo(), this.requestInfo, state);
			this.generatoreErrorePortaApplicativa.updateInformazioniCooperazione(soggettoFruitore, idServizio);
			this.generatoreErrorePortaApplicativa.updateInformazioniCooperazione(servizioApplicativo);
			
			this.ejbUtils.setGeneratoreErrorePortaApplicativa(this.generatoreErrorePortaApplicativa);
			
			this.pa = this.localForwardParameter.getConfigurazionePdDReader().getPortaApplicativa(this.idPA, this.requestInfo);
			
			
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}
	}
	
	public void updateLocalForwardParameter(LocalForwardParameter localForwardParameter) throws LocalForwardException{
		this.localForwardParameter = localForwardParameter;
	}
	
	public void setIntegrationFunctionError(IntegrationFunctionError integrationError) {
		this._integrationFunctionError = integrationError;
	}
	
	private Transaction getTransactionNullable() {
		String idTransazione = (String) this.localForwardParameter.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.ID_TRANSAZIONE);
		Transaction transactionNullable = null;
		try{
			transactionNullable = TransactionContext.getTransaction(idTransazione);
		}catch(Exception e){
			// puo' non essere presente in comunicazioni stateful
		}
		return transactionNullable;
	}
	
	
	
	
	
	
	/* ***** PROCESS REQUEST ******** */
	
	private SecurityInfo securityInfoRequest = null;
	public SecurityInfo getSecurityInfoRequest() {
		return this.securityInfoRequest;
	}
	private OpenSPCoop2Message responseMessageError = null;
	public OpenSPCoop2Message getResponseMessageError() {
		return this.responseMessageError;
	}
	private OpenSPCoop2Message requestMessageAfterProcess = null;
	public OpenSPCoop2Message getRequestMessageAfterProcess() {
		return this.requestMessageAfterProcess;
	}

	private MessageSecurityConfig securityConfigPDRequest = null;
	private MessageSecurityConfig securityConfigPARequest = null;
	
	public boolean processRequest(OpenSPCoop2Message requestMessage) throws LocalForwardException{
		
		try{

			ErroreIntegrazione erroreIntegrazione = null;
			CodiceErroreCooperazione codiceErroreCooperazione = null;
			Exception configException = null;
			MessageSecurityFactory messageSecurityFactory = new MessageSecurityFactory();
		
			IState state = ((OpenSPCoopState)this.localForwardParameter.getOpenspcoopstate()).getStatoRichiesta();
			
			PdDContext pddContext = this.localForwardParameter.getPddContext();
			
			
			/* ****************** PORTA DELEGATA **************************** */
			
			
			/* *** Init MTOM Processor / SecurityContext *** */
			boolean messageSecurityApply = false;
			this.localForwardParameter.getMsgDiag().mediumDebug("init MTOM Processor / SecurityContext (PD) ...");
			MTOMProcessorConfig mtomConfig = null;
			String msgErrore = null;
			String posizione = null;
			MTOMProcessor mtomProcessor = null;
			boolean logDiagnosticError = true;
			IntegrationFunctionError integrationFunctionError = null;
			try{
				this.securityConfigPDRequest=this.localForwardParameter.getConfigurazionePdDReader().getMessageSecurityForSender(this.pd, this.localForwardParameter.getLog(), requestMessage, this.busta, this.requestInfo, this.localForwardParameter.getPddContext());
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPDRequest";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getMTOMProcessorForSender(this.pd);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
			}		
			if(erroreIntegrazione==null){
				mtomProcessor = new MTOMProcessor(mtomConfig, this.securityConfigPDRequest, TipoPdD.DELEGATA, 
						this.localForwardParameter.getMsgDiag(), this.localForwardParameter.getLog(), 
						this.localForwardParameter.getPddContext());
			}
					
			/* *** MTOM Processor BeforeSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomBeforeSecurity(requestMessage, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED;
				}
			}
			
			/* *** MessageSecurity *** */
			MessageSecurityContext messageSecurityContext = null;
			if(erroreIntegrazione==null){
				if(this.securityConfigPDRequest!=null && this.securityConfigPDRequest.getFlowParameters()!=null &&
						this.securityConfigPDRequest.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PD) della richiesta ...");
						messageSecurityApply = true;
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
						contextParameters.setIdFruitore(this.richiestaDelegata.getIdSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(this.securityConfigPDRequest.getFlowParameters());
						
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PD) della richiesta completata con successo");
						
						if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
							this.localForwardParameter.getMsgDiag().mediumDebug("Normalize to saajImpl");
							//System.out.println("InoltroBusteEgov.request.normalize");
							requestMessage = requestMessage.normalizeToSaajImpl();
							this.requestMessageAfterProcess = requestMessage; // per aggiornare il messaggio fuori dall'engine
						}
						
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getOutgoingProperties());
						this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRichiestaInCorso");					
						DynamicMapBuilderUtils.injectDynamicMap(this.busta, this.requestInfo, pddContext, this.localForwardParameter.getLog());
						if(messageSecurityContext.processOutgoing(requestMessage,this.localForwardParameter.getPddContext(),
								getTransactionNullable()!=null ? getTransactionNullable().getTempiElaborazione() : null) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
							
							this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
							this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRichiestaInErrore");
						}
						else{
							this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRichiestaEffettuato");
						}
					}catch(Exception e){
						
						this.localForwardParameter.getMsgDiag().addKeywordErroreProcessamento(e);
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRichiestaInErrore");
						this.localForwardParameter.getLog().error("[MessageSecurityRequest]" + e.getMessage(),e);
						
						posizione = "MessageSecurityPortaDelegataRequestFlow";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED;
					}
				}
				else{
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRichiestaDisabilitato");
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && codiceErroreCooperazione==null){
				IDigestReader digestReader = null;
				if(messageSecurityContext != null) {
					digestReader = messageSecurityContext.getDigestReader(requestMessage!=null ? requestMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				}
				if(digestReader!=null){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PD) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica(state);
						this.securityInfoRequest = validazioneSemantica.readSecurityInformation(digestReader,requestMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPDRequest";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED;
					}
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(requestMessage, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							erroreIntegrazione,configException,null);
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
				}
				if(logDiagnosticError==false){
					// comunque effettuo log nel core. Puo' darsi in alcuni casi che non venga registrato (es. NullPointer)
					this.localForwardParameter.getLog().error("("+posizione+") "+Utilities.readFirstErrorValidMessageFromException(configException),configException);
				}

			}
			if(this.responseMessageError!=null){
				return false;
			}

			
			
			
			
			
			
			
			
			/* ****************** PORTA APPLICATIVA **************************** */
			
			/* *** Init MTOM Processor / SecurityContext *** */
			this.localForwardParameter.getMsgDiag().mediumDebug("init MTOM Processor / SecurityContext (PA) ...");
			erroreIntegrazione = null;
			codiceErroreCooperazione = null;
			configException = null;
			mtomConfig = null;
			mtomProcessor = null;
			msgErrore = null;
			posizione = null;
			messageSecurityContext = null;
			logDiagnosticError = true;
			integrationFunctionError = null;
			try{
				this.securityConfigPARequest=this.localForwardParameter.getConfigurazionePdDReader().getMessageSecurityForReceiver(this.pa, this.localForwardParameter.getLog(), requestMessage, this.busta, this.requestInfo, this.localForwardParameter.getPddContext());
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPARequest";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
				integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getMTOMProcessorForReceiver(this.pa);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
			}		
			if(erroreIntegrazione==null){
				mtomProcessor = new MTOMProcessor(mtomConfig, this.securityConfigPARequest, TipoPdD.APPLICATIVA, 
						this.localForwardParameter.getMsgDiag(), this.localForwardParameter.getLog(),
						this.localForwardParameter.getPddContext());
			}
			
			/* *** MTOM Processor BeforeSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomBeforeSecurity(requestMessage, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED;
				}
			}
			
			/* *** MessageSecurity (InitContext) *** */
			if(erroreIntegrazione==null && this.securityConfigPARequest!=null && this.securityConfigPARequest.getFlowParameters()!=null){
				if(this.securityConfigPARequest.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PA) della richiesta ...");
						
						if(messageSecurityApply){
							
							OpenSPCoop2SoapMessage soapMessage = requestMessage.castAsSoap();
							
							// Per poter applicare anche questa config devo rigenerare tutto il messaggio
//							requestMessage.writeTo(System.out, false);
							DOMSource s = (DOMSource) soapMessage.getSOAPPart().getContent();
							Node n = s.getNode();
							MessageXMLUtils xmlUtils = MessageXMLUtils.getInstance(requestMessage.getFactory());
							byte[] bytes = xmlUtils.toByteArray(n);
//							System.out.println("A:"+new String(bytes));
							soapMessage.getSOAPPart().setContent(new DOMSource(xmlUtils.newElement(bytes)));
//							System.out.println("B");
//							requestMessage.writeTo(System.out, false);
						}
	
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setRemoveAllWsuIdRef(this.propertiesReader.isRemoveAllWsuIdRef());
						contextParameters.setIdFruitore(this.richiestaDelegata.getIdSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setIncomingProperties(this.securityConfigPARequest.getFlowParameters());
						
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PA) della richiesta completata con successo");
						
					}catch(Exception e){
						posizione = "MessageSecurityPortaApplicativaRequestFlowInitContext";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				IDigestReader digestReader = null;
				if(messageSecurityContext != null) {
					digestReader = messageSecurityContext.getDigestReader(requestMessage!=null ? requestMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				}
				if(digestReader!=null) {
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PA) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica(state);
						this.securityInfoRequest = validazioneSemantica.readSecurityInformation(digestReader,requestMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPARequest";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED;
					}
				}
			}
			
			/* *** MessageSecurity *** */
			List<Eccezione> eccezioniSicurezza = new ArrayList<>();
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				try{
					if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
						this.localForwardParameter.getMsgDiag().mediumDebug("Normalize to saajImpl");
						//System.out.println("RicezioneBuste.request.normalize");
						requestMessage = requestMessage.normalizeToSaajImpl();
						this.requestMessageAfterProcess = requestMessage; // per aggiornare il messaggio fuori dall'engine
					}
					
					String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getIncomingProperties());
					this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RICHIESTA, tipoSicurezza);
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRichiestaInCorso");					
					DynamicMapBuilderUtils.injectDynamicMap(this.busta, this.requestInfo, this.localForwardParameter.getPddContext(), this.localForwardParameter.getLog());
					if(messageSecurityContext.processIncoming(requestMessage,this.busta,this.localForwardParameter.getPddContext(),
							getTransactionNullable()!=null ? getTransactionNullable().getTempiElaborazione() : null) == false){  
						if(messageSecurityContext.getListaSubCodiceErrore()!=null && messageSecurityContext.getListaSubCodiceErrore().size()>0){
							List<SubErrorCodeSecurity> subCodiciErrore = messageSecurityContext.getListaSubCodiceErrore();
							for (Iterator<?> iterator = subCodiciErrore.iterator(); iterator.hasNext();) {
								SubErrorCodeSecurity subCodiceErrore = (SubErrorCodeSecurity) iterator.next();
								Eccezione ecc = new Eccezione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(subCodiceErrore.getMsgErrore(), messageSecurityContext.getCodiceErrore()),true,null, 
										this.localForwardParameter.getProtocolFactory());
								ecc.setSubCodiceEccezione(subCodiceErrore);
								eccezioniSicurezza.add(ecc);
							}
						}else{
							Eccezione ecc = new Eccezione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(messageSecurityContext.getMsgErrore(),messageSecurityContext.getCodiceErrore()), true, null, 
									this.localForwardParameter.getProtocolFactory());
							eccezioniSicurezza.add(ecc);
						}
						Eccezione ecc = eccezioniSicurezza.get(0); // prendo la prima disponibile.
						this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+ecc.getCodiceEccezione()+"] "+ecc.getDescrizione(this.localForwardParameter.getProtocolFactory() ));
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRichiestaInErrore");
					}
					else{
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRichiestaEffettuato");
					}										
				}catch(Exception e){
					
					this.localForwardParameter.getMsgDiag().addKeywordErroreProcessamento(e);
	
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRichiestaInErrore");
					
					posizione = "MessageSecurityPortaApplicativaRequestFlow";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_REQUEST_FAILED;
				}
			}
			else{
				if(erroreIntegrazione==null){
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRichiestaDisabilitato");
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(requestMessage, RuoloMessaggio.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_REQUEST_FAILED;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || eccezioniSicurezza.size()>0){
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_REQUEST_ERROR;
				}
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							erroreIntegrazione,configException,null);
				}else{
					Eccezione ecc = eccezioniSicurezza.get(0); // prendo la prima disponibile.
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
				}
				if(logDiagnosticError==false){
					// comunque effettuo log nel core. Puo' darsi in alcuni casi che non venga registrato (es. NullPointer)
					this.localForwardParameter.getLog().error("("+posizione+") "+Utilities.readFirstErrorValidMessageFromException(configException),configException);
				}
			}
			if(this.responseMessageError!=null){
				return false;
			}

			
			
			
			
			return true;
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	/* ***** SEND REQUEST ******** */
	public void sendRequest(GestoreMessaggi gestoreMessaggiRequest) throws LocalForwardException{
		
		try{

			Behaviour behaviour = this.ejbUtils.sendToConsegnaContenutiApplicativi(this.requestInfo, this.richiestaApplicativa, this.busta, gestoreMessaggiRequest, this.pa, 
					this.localForwardParameter.getRepositoryBuste(), this.richiestaDelegata);
			boolean behaviourResponseTo = behaviour!=null && behaviour.isResponseTo();
			
			GestoreMessaggi msgOK = null;
			
			if(behaviourResponseTo){
			
				this.localForwardParameter.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
						DateUtils.getSimpleDateFormatMs().format(DateManager.getDate()));
				
				this.localForwardParameter.getMsgDiag().mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Behaviour)...");	
				
				OpenSPCoop2Message responseToMessage = null;
				if(behaviour.getResponseTo()!=null){
					responseToMessage = behaviour.getResponseTo().getMessage();
				}
				if(responseToMessage==null){
					if(this.localForwardParameter.getProtocolFactory().createProtocolManager().isHttpEmptyResponseOneWay()){
						responseToMessage =  MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE);
					}
					else{
						responseToMessage = this.ejbUtils.buildOpenSPCoopOK(this.requestInfo.getIntegrationRequestMessageType(), this.localForwardParameter.getIdRequest());
					}
				}
				msgOK = this.ejbUtils.sendRispostaApplicativaOK(responseToMessage,
						this.richiestaDelegata,this.pd,this.sa);			
				
			}else if(this.localForwardParameter.isStateless()==false || this.localForwardParameter.isOneWayVersione11() || this.ejbUtils.isGestioneSolamenteConIntegrationManager()){
				
				this.localForwardParameter.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
						DateUtils.getSimpleDateFormatMs().format(DateManager.getDate()));
				
				this.localForwardParameter.getMsgDiag().mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi...");	
				
				if(this.localForwardParameter.getProtocolFactory().createProtocolManager().isHttpEmptyResponseOneWay())
					msgOK = this.ejbUtils.sendRispostaApplicativaOK(MessageUtilities.buildEmptyMessage(OpenSPCoop2MessageFactory.getDefaultMessageFactory(), this.requestInfo.getIntegrationRequestMessageType(),MessageRole.RESPONSE),this.richiestaDelegata,this.pd,this.sa);
				else
					msgOK = this.ejbUtils.sendRispostaApplicativaOK(this.ejbUtils.buildOpenSPCoopOK(this.requestInfo.getIntegrationRequestMessageType(), this.localForwardParameter.getIdRequest()),
							this.richiestaDelegata,this.pd,this.sa);
			}
			
			
			if(msgOK!=null)
				msgOK.addMessaggiIntoCache_readFromTable(Imbustamento.ID_MODULO, "messaggio OK");
			
			if(msgOK!=null)
				msgOK.addProprietariIntoCache_readFromTable(Imbustamento.ID_MODULO, "messaggio OK",this.localForwardParameter.getIdRequest(),false);
			
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}
		
	}
	
	
	
	
	
	/* ***** PROCESS RESPONSE ******** */
	private SecurityInfo securityInfoResponse = null;
	public SecurityInfo getSecurityInfoResponse() {
		return this.securityInfoResponse;
	}
	private OpenSPCoop2Message responseMessageAfterProcess = null;
	public OpenSPCoop2Message getResponseMessageAfterProcess() {
		return this.responseMessageAfterProcess;
	}
	public boolean processResponse(OpenSPCoop2Message responseMessage) throws LocalForwardException{
		
		try{
			
			ErroreIntegrazione erroreIntegrazione = null;
			CodiceErroreCooperazione codiceErroreCooperazione = null;
			Exception configException = null;
			MessageSecurityFactory messageSecurityFactory = new MessageSecurityFactory();

			IState state = ((OpenSPCoopState)this.localForwardParameter.getOpenspcoopstate()).getStatoRisposta();
			
			PdDContext pddContext = this.localForwardParameter.getPddContext();
			
			
			
			
			/* ****************** PORTA APPLICATIVA **************************** */
			
			/* *** Init MTOM Processor / SecurityContext *** */
			boolean messageSecurityApply = false;
			this.localForwardParameter.getMsgDiag().mediumDebug("init MTOM Processor / SecurityContext (PA-Response) ...");
			MessageSecurityConfig securityConfig = null;
			MTOMProcessorConfig mtomConfig = null;
			String msgErrore = null;
			String posizione = null;
			MTOMProcessor mtomProcessor = null;
			boolean logDiagnosticError = true;
			IntegrationFunctionError integrationFunctionError = null;
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getMessageSecurityForSender(this.pa, this.localForwardParameter.getLog(), responseMessage, this.busta, this.requestInfo, this.localForwardParameter.getPddContext(),
						this.securityConfigPARequest);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPAResponse";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
				integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getMTOMProcessorForSender(this.pa);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
			}		
			if(erroreIntegrazione==null){
				mtomProcessor = new MTOMProcessor(mtomConfig, securityConfig, TipoPdD.APPLICATIVA, 
						this.localForwardParameter.getMsgDiag(), this.localForwardParameter.getLog(),
						this.localForwardParameter.getPddContext());
			}
			
			/* *** MTOM Processor BeforeSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomBeforeSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED;
				}
			}
			
			/* *** MessageSecurity (InitContext) *** */
			MessageSecurityContext messageSecurityContext = null;
			if(erroreIntegrazione==null && securityConfig!=null && securityConfig.getFlowParameters()!=null){
				if(securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PA) della risposta ...");
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getIdSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(securityConfig.getFlowParameters());
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PA) della risposta completata con successo");
						
					}catch(Exception e){
						posizione = "MessageSecurityPortaApplicativaResponseFlowInitContext";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				IDigestReader digestReader = null;
				if(messageSecurityContext != null) {
					digestReader = messageSecurityContext.getDigestReader(responseMessage!=null ? responseMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				}
				if(digestReader!=null) {
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PA-Response) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica(state);
						this.securityInfoResponse = validazioneSemantica.readSecurityInformation(digestReader,responseMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPAResponse";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED;
					}
				}
			}
			
			/* *** MessageSecurity *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				try{
					if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
						this.localForwardParameter.getMsgDiag().mediumDebug("Normalize to saajImpl");
						//System.out.println("InoltroBusteEgov.request.normalize");
						responseMessage = responseMessage.normalizeToSaajImpl();
						this.responseMessageAfterProcess = responseMessage; // per aggiornare il messaggio fuori dall'engine
					}
					String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getOutgoingProperties());
					this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRispostaInCorso");					
					messageSecurityApply = true;
					DynamicMapBuilderUtils.injectDynamicMap(this.busta, this.requestInfo, pddContext, this.localForwardParameter.getLog());
					if(messageSecurityContext.processOutgoing(responseMessage,this.localForwardParameter.getPddContext(),
							getTransactionNullable()!=null ? getTransactionNullable().getTempiElaborazione() : null) == false){
						msgErrore = messageSecurityContext.getMsgErrore();
						codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
						this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRispostaInErrore");
					}
					else{
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRispostaEffettuato");
					}
				}catch(Exception e){
					this.localForwardParameter.getMsgDiag().addKeywordErroreProcessamento(e);
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRispostaInErrore");
					posizione = "MessageSecurityPortaApplicativaResponseFlow";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED;
				}
			}
			else{
				if(erroreIntegrazione==null){
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_BUSTE,"messageSecurity.processamentoRispostaDisabilitato");
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							erroreIntegrazione,configException,
							(responseMessage!=null ? responseMessage.getParseException() : null));
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
				}
				if(logDiagnosticError==false){
					// comunque effettuo log nel core. Puo' darsi in alcuni casi che non venga registrato (es. NullPointer)
					this.localForwardParameter.getLog().error("("+posizione+") "+Utilities.readFirstErrorValidMessageFromException(configException),configException);
				}
			}
			if(this.responseMessageError!=null){
				return false;
			}

			
			
			
			
			
			
			/* ****************** PORTA DELEGATA **************************** */
			
			/* *** Init MTOM Processor / SecurityContext *** */
			this.localForwardParameter.getMsgDiag().mediumDebug("init MTOM Processor / SecurityContext (PD-Response) ...");
			erroreIntegrazione = null;
			codiceErroreCooperazione = null;
			configException = null;
			securityConfig = null;
			mtomConfig = null;
			mtomProcessor = null;
			msgErrore = null;
			posizione = null;
			messageSecurityContext = null;
			logDiagnosticError = true;
			integrationFunctionError = null;
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getMessageSecurityForReceiver(this.pd, this.localForwardParameter.getLog(), responseMessage, this.busta, this.requestInfo, this.localForwardParameter.getPddContext(),
						this.securityConfigPDRequest);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPDResponse";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
				integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getMTOMProcessorForReceiver(this.pd);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
			}		
			if(erroreIntegrazione==null){
				mtomProcessor = new MTOMProcessor(mtomConfig, securityConfig, TipoPdD.DELEGATA, 
						this.localForwardParameter.getMsgDiag(), this.localForwardParameter.getLog(),
						this.localForwardParameter.getPddContext());
			}
			
			/* *** MTOM Processor BeforeSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomBeforeSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED;
				}
			}
			
			/* *** MessageSecurity *** */
			if(erroreIntegrazione==null){
				if(securityConfig!=null && securityConfig.getFlowParameters()!=null &&
						securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PD) della risposta ...");
						
						if(messageSecurityApply){
							
							OpenSPCoop2SoapMessage soapMessage = responseMessage.castAsSoap();
							
							// Per poter applicare anche questa config devo rigenerare tutto il messaggio
//							requestMessage.writeTo(System.out, false);
							DOMSource s = (DOMSource) soapMessage.getSOAPPart().getContent();
							Node n = s.getNode();
							MessageXMLUtils xmlUtils = MessageXMLUtils.getInstance(responseMessage.getFactory());
							byte[] bytes = xmlUtils.toByteArray(n);
//							System.out.println("A:"+new String(bytes));
							soapMessage.getSOAPPart().setContent(new DOMSource(xmlUtils.newElement(bytes)));
//							System.out.println("B");
//							requestMessage.writeTo(System.out, false);
						}
	
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_SERVER);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getIdSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setIncomingProperties(securityConfig.getFlowParameters());
						this.localForwardParameter.getMsgDiag().mediumDebug("Inizializzazione contesto di Message Security (PD) della risposta completata con successo");
						
						if(org.openspcoop2.security.message.engine.WSSUtilities.isNormalizeToSaajImpl(messageSecurityContext)){
							this.localForwardParameter.getMsgDiag().mediumDebug("Normalize to saajImpl");
							//System.out.println("InoltroBusteEgov.request.normalize");
							responseMessage = responseMessage.normalizeToSaajImpl();
							this.responseMessageAfterProcess = responseMessage; // per aggiornare il messaggio fuori dall'engine
						}
						String tipoSicurezza = SecurityConstants.convertActionToString(messageSecurityContext.getIncomingProperties());
						this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_TIPO_SICUREZZA_MESSAGGIO_RISPOSTA, tipoSicurezza);
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRispostaInCorso");					
						DynamicMapBuilderUtils.injectDynamicMap(this.busta, this.requestInfo, this.localForwardParameter.getPddContext(), this.localForwardParameter.getLog());
						if(messageSecurityContext.processIncoming(responseMessage,this.busta,this.localForwardParameter.getPddContext(),
								getTransactionNullable()!=null ? getTransactionNullable().getTempiElaborazione() : null) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
							this.localForwardParameter.getMsgDiag().addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO , "["+codiceErroreCooperazione+"] "+msgErrore );
							this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRispostaInErrore");
						}
						else{
							this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRispostaEffettuato");
						}
					}catch(Exception e){
						this.localForwardParameter.getMsgDiag().addKeywordErroreProcessamento(e);
						this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRispostaInErrore");
						posizione = "MessageSecurityPortaDelegataResponseFlow";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED;
					}
				}
				else{
					this.localForwardParameter.getMsgDiag().logPersonalizzato(MsgDiagnosticiProperties.MSG_DIAG_INOLTRO_BUSTE,"messageSecurity.processamentoRispostaDisabilitato");					
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && codiceErroreCooperazione==null){
				IDigestReader digestReader = null;
				if(messageSecurityContext != null) {
					digestReader = messageSecurityContext.getDigestReader(responseMessage!=null ? responseMessage.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory());
				}
				if(digestReader!=null){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PD-Response) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica(state);
						this.securityInfoResponse = validazioneSemantica.readSecurityInformation(digestReader,responseMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPDResponse";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
						integrationFunctionError = IntegrationFunctionError.MESSAGE_SECURITY_RESPONSE_FAILED;
					}
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(responseMessage, RuoloMessaggio.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e,CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
					integrationFunctionError = IntegrationFunctionError.ATTACHMENTS_PROCESSING_RESPONSE_FAILED;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(integrationFunctionError==null) {
					integrationFunctionError = IntegrationFunctionError.INTERNAL_RESPONSE_ERROR;
				}
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							erroreIntegrazione,configException,
							(responseMessage!=null ? responseMessage.getParseException() : null));
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,
							ecc,this.richiestaDelegata.getIdSoggettoFruitore(),null);
				}
				if(logDiagnosticError==false){
					// comunque effettuo log nel core. Puo' darsi in alcuni casi che non venga registrato (es. NullPointer)
					this.localForwardParameter.getLog().error("("+posizione+") "+Utilities.readFirstErrorValidMessageFromException(configException),configException);
				}
			}
			if(this.responseMessageError!=null){
				return false;
			}

			
			
			
			
			
			return true;
			
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}
		
	}
	
	
	
	
	
	
	
	
	
	/* ***** SEND RESPONSE ******** */
	
	public void sendErrore(PdDContext pddContext, ErroreIntegrazione errore,Throwable eErrore,ParseException parseException) throws LocalForwardException{
	
		try{
		
			IntegrationFunctionError integrationFunctionError = getIntegrationFunctionError(pddContext);
			
			OpenSPCoop2Message responseMessageError = 
					this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,errore,eErrore,parseException);
			
			// Retry-After
			if(integrationFunctionError!=null && 
					(
							IntegrationFunctionError.SERVICE_UNAVAILABLE.equals(integrationFunctionError)
							||
							IntegrationFunctionError.ENDPOINT_REQUEST_TIMED_OUT.equals(integrationFunctionError)
					)
					) {
				boolean isEnabled = this.propertiesReader.isEnabledServiceUnavailableRetryAfterPAConnectionFailed();
				Integer retryAfterSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSecondsPAConnectionFailed();
				Integer retryAfterBackOffSeconds = this.propertiesReader.getServiceUnavailableRetryAfterSecondsRandomBackoffPAConnectionFailed();
				if(	isEnabled &&
					retryAfterSeconds!=null && retryAfterSeconds>0) {
					int seconds = retryAfterSeconds;
					if(retryAfterBackOffSeconds!=null && retryAfterBackOffSeconds>0) {
						seconds = seconds + ServicesUtils.getRandom().nextInt(retryAfterBackOffSeconds);
					}
					responseMessageError.forceTransportHeader(HttpConstants.RETRY_AFTER, seconds+"");
				}
			}
			
			if(eErrore instanceof HandlerException){
				HandlerException he = (HandlerException) eErrore;
				he.customized(responseMessageError);
			}
			this.sendErrore(responseMessageError);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}	
	}
	
	public void sendErrore(PdDContext pddContext, Eccezione errore,IDSoggetto dominio,ParseException parseException) throws LocalForwardException{
		
		try{
		
			IntegrationFunctionError integrationFunctionError = getIntegrationFunctionError(pddContext);
			
			OpenSPCoop2Message responseMessageError = 
					this.generatoreErrorePortaDelegata.build(pddContext, integrationFunctionError,errore,dominio,parseException);
			this.sendErrore(responseMessageError);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}	
	}
	
	public void sendErrore(OpenSPCoop2Message responseMessageError) throws LocalForwardException{
		
		try{
		
			this.ejbUtils.sendRispostaApplicativaErrore(responseMessageError,this.richiestaDelegata,true,this.pd,this.sa);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}	
	}
	
	public void sendResponse(String idRisposta) throws LocalForwardException{
		
		try{
		
			GestoreMessaggi msgResponse = null;
			
			this.localForwardParameter.getMsgDiag().mediumDebug("Send risposta applicativa...");
			this.ejbUtils.setIdMessage(idRisposta);
			msgResponse = this.ejbUtils.sendRispostaApplicativa(this.richiestaDelegata,this.pd,this.sa);
						
			// Aggiornamento cache messaggio
			if(msgResponse!=null)
				msgResponse.addMessaggiIntoCache_readFromTable(SbustamentoRisposte.ID_MODULO, "risposte");

			// Aggiornamento cache proprietario messaggio
			if(msgResponse!=null)
				msgResponse.addProprietariIntoCache_readFromTable(SbustamentoRisposte.ID_MODULO, "risposte",this.localForwardParameter.getIdRequest(),false);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}	
	}
}
