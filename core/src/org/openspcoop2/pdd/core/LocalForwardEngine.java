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
import org.openspcoop2.core.id.IDPortaApplicativaByNome;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.XMLUtils;
import org.openspcoop2.pdd.config.MTOMProcessorConfig;
import org.openspcoop2.pdd.config.MessageSecurityConfig;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.RichiestaApplicativa;
import org.openspcoop2.pdd.config.RichiestaDelegata;
import org.openspcoop2.pdd.core.behaviour.Behaviour;
import org.openspcoop2.pdd.core.state.OpenSPCoopState;
import org.openspcoop2.pdd.mdb.Imbustamento;
import org.openspcoop2.pdd.mdb.SbustamentoRisposte;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
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
import org.openspcoop2.protocol.sdk.constants.ProfiloDiCollaborazione;
import org.openspcoop2.protocol.sdk.constants.TipoTraccia;
import org.openspcoop2.protocol.sdk.validator.IValidazioneSemantica;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityFactory;
import org.openspcoop2.utils.date.DateManager;
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
	private ErroreApplicativoBuilder erroreApplicativoBuilder = null;
	private SOAPVersion versioneSoap = null;
	private Busta busta = null;
	private IDPortaApplicativaByNome idPAByNome = null;
	private PortaApplicativa pa = null;
	private RichiestaApplicativa richiestaApplicativa = null;
	private RichiestaDelegata richiestaDelegata = null;
	private PortaDelegata pd =null;
	private ServizioApplicativo sa = null;
	private OpenSPCoop2Properties propertiesReader = null;
	
	
	/* ***** COSTRUTTORE ******** */
	
	public LocalForwardEngine(LocalForwardParameter localForwardParameter) throws LocalForwardException{
		this.localForwardParameter = localForwardParameter;
		try{
			
			this.propertiesReader = OpenSPCoop2Properties.getInstance();
			
			this.richiestaDelegata = this.localForwardParameter.getRichiestaDelegata();
			this.richiestaApplicativa = this.localForwardParameter.getRichiestaApplicativa();
			
			((OpenSPCoopState)this.localForwardParameter.getOpenspcoopstate()).setIDMessaggioSessione(this.localForwardParameter.getIdRequest());
			
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
				
				this.idPAByNome = this.richiestaApplicativa.getIdPAbyNome();
				
				this.busta = this.localForwardParameter.getBusta();
								
			}else{
				profiloGestione = this.richiestaDelegata.getProfiloGestione();
				soggettoFruitore = this.richiestaDelegata.getSoggettoFruitore();
				idServizio = this.richiestaDelegata.getIdServizio();
				fault = this.richiestaDelegata.getFault();
				servizioApplicativo = this.richiestaDelegata.getServizioApplicativo();
				if(ProfiloDiCollaborazione.ONEWAY.equals(this.localForwardParameter.getInfoServizio().getProfiloDiCollaborazione())){
					scenarioCooperazione = Costanti.SCENARIO_ONEWAY_INVOCAZIONE_SERVIZIO;
				}
				else if(ProfiloDiCollaborazione.SINCRONO.equals(this.localForwardParameter.getInfoServizio().getProfiloDiCollaborazione())){
					scenarioCooperazione = Costanti.SCENARIO_SINCRONO_INVOCAZIONE_SERVIZIO;
				}
				
				this.idPAByNome = this.localForwardParameter.getConfigurazionePdDReader().convertTo(idServizio, null);
				this.richiestaApplicativa = 
						new RichiestaApplicativa(soggettoFruitore, idServizio, 
								this.localForwardParameter.getIdModulo(), this.localForwardParameter.getIdentitaPdD(),this.idPAByNome);
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
			
			this.pd = this.localForwardParameter.getConfigurazionePdDReader().getPortaDelegata(this.richiestaDelegata.getIdPortaDelegata());	
			try{
				this.sa = this.localForwardParameter.getConfigurazionePdDReader().getServizioApplicativo(this.richiestaDelegata.getIdPortaDelegata(), 
						this.richiestaDelegata.getServizioApplicativo());
			}catch(DriverConfigurazioneNotFound e){
				if(CostantiPdD.SERVIZIO_APPLICATIVO_ANONIMO.equals(this.richiestaDelegata.getServizioApplicativo())==false)
					throw e;
			}
			if(fault==null){
				fault = this.propertiesReader.getProprietaGestioneErrorePD(this.localForwardParameter.getProtocolFactory().createProtocolManager());
				fault.setDominio(this.localForwardParameter.getIdentitaPdD().getCodicePorta());
				fault.setIdModulo(this.localForwardParameter.getIdModulo());
				this.localForwardParameter.getConfigurazionePdDReader().aggiornaProprietaGestioneErrorePD(fault, this.sa);
			}
			
			this.versioneSoap = (SOAPVersion) this.localForwardParameter.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION);
			
			this.ejbUtils = new EJBUtils(this.localForwardParameter.getIdentitaPdD(), TipoPdD.DELEGATA, 
					this.localForwardParameter.getIdModulo(), this.localForwardParameter.getIdRequest(), this.localForwardParameter.getIdRequest(), Costanti.OUTBOX, 
					this.localForwardParameter.getOpenspcoopstate(), this.localForwardParameter.getMsgDiag(), false, 
					this.localForwardParameter.getImplementazionePdDMittente(), this.localForwardParameter.getImplementazionePdDDestinatario(), 
					profiloGestione, this.localForwardParameter.getPddContext());
			this.ejbUtils.setOneWayVersione11(this.localForwardParameter.isOneWayVersione11());
			this.ejbUtils.setPortaDiTipoStateless_esclusoOneWay11(this.localForwardParameter.isStateless() && !this.localForwardParameter.isOneWayVersione11());
			
			this.erroreApplicativoBuilder = new ErroreApplicativoBuilder(this.localForwardParameter.getLog(), this.localForwardParameter.getProtocolFactory(), 
					this.localForwardParameter.getIdentitaPdD(),soggettoFruitore, idServizio, 
					this.localForwardParameter.getIdModulo(), fault, 
					this.versioneSoap,TipoPdD.DELEGATA,servizioApplicativo);
									
			this.pa = this.localForwardParameter.getConfigurazionePdDReader().getPortaApplicativa(this.idPAByNome);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}
	}
	
	public void updateLocalForwardParameter(LocalForwardParameter localForwardParameter) throws LocalForwardException{
		this.localForwardParameter = localForwardParameter;
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
	public boolean processRequest(OpenSPCoop2Message requestMessage) throws LocalForwardException{
		
		try{

			ErroreIntegrazione erroreIntegrazione = null;
			CodiceErroreCooperazione codiceErroreCooperazione = null;
			Exception configException = null;
			MessageSecurityFactory messageSecurityFactory = new MessageSecurityFactory();
		
			
			
			
			
			/* ****************** PORTA DELEGATA **************************** */
			
			
			/* *** Init MTOM Processor / SecurityContext *** */
			boolean messageSecurityApply = false;
			this.localForwardParameter.getMsgDiag().mediumDebug("init MTOM Processor / SecurityContext (PD) ...");
			MessageSecurityConfig securityConfig = null;
			MTOMProcessorConfig mtomConfig = null;
			String msgErrore = null;
			String posizione = null;
			MTOMProcessor mtomProcessor = null;
			boolean logDiagnosticError = true;
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getPD_MessageSecurityForSender(this.pd);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPDRequest";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getPD_MTOMProcessorForSender(this.pd);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
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
					mtomProcessor.mtomBeforeSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity *** */
			MessageSecurityContext messageSecurityContext = null;
			if(erroreIntegrazione==null && securityConfig!=null && securityConfig.getFlowParameters()!=null){
				if(securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity (PD) ...");
						messageSecurityApply = true;
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(securityConfig.getFlowParameters());
						if(messageSecurityContext.processOutgoing(requestMessage) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
						}
					}catch(Exception e){
						posizione = "MessageSecurityPortaDelegataRequestFlow";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && codiceErroreCooperazione==null){
				if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PD) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica();
						this.securityInfoRequest = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),requestMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPDRequest";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
					}
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PDRequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,configException,null);
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(ecc,this.richiestaDelegata.getSoggettoFruitore(),null);
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
			securityConfig = null;
			mtomConfig = null;
			mtomProcessor = null;
			msgErrore = null;
			posizione = null;
			messageSecurityContext = null;
			logDiagnosticError = true;
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getPA_MessageSecurityForReceiver(this.pa);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPARequest";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getPA_MTOMProcessorForReceiver(this.pa);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
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
					mtomProcessor.mtomBeforeSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity (InitContext) *** */
			if(erroreIntegrazione==null && securityConfig!=null && securityConfig.getFlowParameters()!=null){
				if(securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity init context (PA) ...");
						
						if(messageSecurityApply){
							// Per poter applicare anche questa config devo rigenerare tutto il messaggio
//							System.out.println("AAAAAAAAA");
//							requestMessage.writeTo(System.out, false);
							DOMSource s = (DOMSource) requestMessage.getSOAPPart().getContent();
							Node n = s.getNode();
							byte[] bytes = XMLUtils.getInstance().toByteArray(n);
//							System.out.println("BABA:"+new String(bytes));
							requestMessage.getSOAPPart().setContent(new DOMSource(XMLUtils.getInstance().newElement(bytes)));
//							System.out.println("BBBBB");
//							requestMessage.writeTo(System.out, false);
						}
	
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECUIRYT_SERVER);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setIncomingProperties(securityConfig.getFlowParameters());
					}catch(Exception e){
						posizione = "MessageSecurityPortaApplicativaRequestFlowInitContext";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
				try{
					this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PA) ...");
					IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica();
					this.securityInfoRequest = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),requestMessage);
				}catch(Exception e){
					posizione = "LetturaInformazioniSicurezzaPARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity *** */
			List<Eccezione> eccezioniSicurezza = new ArrayList<Eccezione>();
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				try{
					this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity (PA) ...");
					if(messageSecurityContext.processIncoming(requestMessage,this.busta) == false){  
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
					}										
				}catch(Exception e){
					posizione = "MessageSecurityPortaApplicativaRequestFlow";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(requestMessage, TipoTraccia.RICHIESTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PARequest";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || eccezioniSicurezza.size()>0){
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,configException,null);
				}else{
					Eccezione ecc = eccezioniSicurezza.get(0); // prendo la prima disponibile.
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(ecc,this.richiestaDelegata.getSoggettoFruitore(),null);
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

			Behaviour behaviour = this.ejbUtils.sendToConsegnaContenutiApplicativi(this.richiestaApplicativa, this.busta, gestoreMessaggiRequest, this.pa, 
					this.localForwardParameter.getRepositoryBuste(), this.richiestaDelegata);
			boolean behaviourResponseTo = behaviour!=null && behaviour.isResponseTo();
			
			GestoreMessaggi msgOK = null;
			
			if(behaviourResponseTo){
			
				this.localForwardParameter.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
						org.openspcoop2.core.constants.Costanti.newSimpleDateFormat().format(DateManager.getDate()));
				
				this.localForwardParameter.getMsgDiag().mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi (Behaviour)...");	
				
				OpenSPCoop2Message responseToMessage = null;
				if(behaviour.getResponseTo()!=null){
					responseToMessage = behaviour.getResponseTo().getMessage();
				}
				if(responseToMessage==null){
					if(this.localForwardParameter.getProtocolFactory().createProtocolManager().isHttpEmptyResponseOneWay()){
						responseToMessage = SoapUtils.build_Soap_Empty(this.versioneSoap);
					}
					else{
						responseToMessage = this.ejbUtils.buildOpenSPCoopOK_soapMsg(this.versioneSoap, this.localForwardParameter.getIdRequest());
					}
				}
				msgOK = this.ejbUtils.sendRispostaApplicativaOK(responseToMessage,
						this.richiestaDelegata,this.pd,this.sa);			
				
			}else if(this.localForwardParameter.isStateless()==false || this.localForwardParameter.isOneWayVersione11() || this.ejbUtils.isGestioneSolamenteConIntegrationManager()){
				
				this.localForwardParameter.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.DATA_PRESA_IN_CARICO, 
						org.openspcoop2.core.constants.Costanti.newSimpleDateFormat().format(DateManager.getDate()));
				
				this.localForwardParameter.getMsgDiag().mediumDebug("Invio messaggio 'OK' al modulo di RicezioneContenutiApplicativi...");	
				
				if(this.localForwardParameter.getProtocolFactory().createProtocolManager().isHttpEmptyResponseOneWay())
					msgOK = this.ejbUtils.sendRispostaApplicativaOK(SoapUtils.build_Soap_Empty(this.versioneSoap),this.richiestaDelegata,this.pd,this.sa);
				else
					msgOK = this.ejbUtils.sendRispostaApplicativaOK(this.ejbUtils.buildOpenSPCoopOK_soapMsg(this.versioneSoap, this.localForwardParameter.getIdRequest()),
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
	public boolean processResponse(OpenSPCoop2Message responseMessage) throws LocalForwardException{
		
		try{
			
			ErroreIntegrazione erroreIntegrazione = null;
			CodiceErroreCooperazione codiceErroreCooperazione = null;
			Exception configException = null;
			MessageSecurityFactory messageSecurityFactory = new MessageSecurityFactory();

			
			
			
			
			
			
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
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getPA_MessageSecurityForSender(this.pa);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPAResponse";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getPA_MTOMProcessorForSender(this.pa);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
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
					mtomProcessor.mtomBeforeSecurity(responseMessage, TipoTraccia.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity (InitContext) *** */
			MessageSecurityContext messageSecurityContext = null;
			if(erroreIntegrazione==null && securityConfig!=null && securityConfig.getFlowParameters()!=null){
				if(securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity init context (PA-Response) ...");
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDMittente()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECURITY_CLIENT);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setOutgoingProperties(securityConfig.getFlowParameters());
					}catch(Exception e){
						posizione = "MessageSecurityPortaApplicativaResponseFlowInitContext";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
				try{
					this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PA-Response) ...");
					IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica();
					this.securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),responseMessage);
				}catch(Exception e){
					posizione = "LetturaInformazioniSicurezzaPAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity *** */
			if(erroreIntegrazione==null && messageSecurityContext!=null){
				try{
					this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity (PA-Response) ...");
					messageSecurityApply = true;
					if(messageSecurityContext.processOutgoing(responseMessage) == false){
						msgErrore = messageSecurityContext.getMsgErrore();
						codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
					}
				}catch(Exception e){
					posizione = "MessageSecurityPortaApplicativaResponseFlow";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(responseMessage, TipoTraccia.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PAResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,configException,
							(responseMessage!=null ? responseMessage.getParseException() : null));
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(ecc,this.richiestaDelegata.getSoggettoFruitore(),null);
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
			try{
				securityConfig=this.localForwardParameter.getConfigurazionePdDReader().getPD_MessageSecurityForReceiver(this.pd);
			}catch(Exception e){
				posizione = "LetturaParametriSicurezzaMessaggioPDResponse";
				erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
				configException = e;
			}
			if(erroreIntegrazione==null){
				try{
					mtomConfig=this.localForwardParameter.getConfigurazionePdDReader().getPD_MTOMProcessorForReceiver(this.pd);
				}catch(Exception e){
					posizione = "LetturaParametriMTOMProcessorPDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
					configException = e;
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
					mtomProcessor.mtomBeforeSecurity(responseMessage, TipoTraccia.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomBeforeSecurity
					
					posizione = "MTOMProcessor(BeforeSec-"+mtomProcessor.getMTOMProcessorType()+")PDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** MessageSecurity *** */
			if(erroreIntegrazione==null && securityConfig!=null && securityConfig.getFlowParameters()!=null){
				if(securityConfig.getFlowParameters().size() > 0){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("MessageSecurity (PD-Response) ...");
						
						if(messageSecurityApply){
							// Per poter applicare anche questa config devo rigenerare tutto il messaggio
//							System.out.println("AAAAAAAAA");
//							requestMessage.writeTo(System.out, false);
							DOMSource s = (DOMSource) responseMessage.getSOAPPart().getContent();
							Node n = s.getNode();
							byte[] bytes = XMLUtils.getInstance().toByteArray(n);
//							System.out.println("BABA:"+new String(bytes));
							responseMessage.getSOAPPart().setContent(new DOMSource(XMLUtils.getInstance().newElement(bytes)));
//							System.out.println("BBBBB");
//							requestMessage.writeTo(System.out, false);
						}
	
						MessageSecurityContextParameters contextParameters = new MessageSecurityContextParameters();
						contextParameters.setUseActorDefaultIfNotDefined(this.propertiesReader.isGenerazioneActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setActorDefault(this.propertiesReader.getActorDefault(this.localForwardParameter.getImplementazionePdDDestinatario()));
						contextParameters.setLog(this.localForwardParameter.getLog());
						contextParameters.setFunctionAsClient(SecurityConstants.SECUIRYT_SERVER);
						contextParameters.setPrefixWsuId(this.propertiesReader.getPrefixWsuId());
						contextParameters.setIdFruitore(this.richiestaDelegata.getSoggettoFruitore());
						contextParameters.setIdServizio(this.richiestaDelegata.getIdServizio());
						contextParameters.setPddFruitore(this.localForwardParameter.getIdPdDMittente());
						contextParameters.setPddErogatore(this.localForwardParameter.getIdPdDDestinatario());
						messageSecurityContext = messageSecurityFactory.getMessageSecurityContext(contextParameters);
						messageSecurityContext.setIncomingProperties(securityConfig.getFlowParameters());
						if(messageSecurityContext.processIncoming(responseMessage,this.busta) == false){
							msgErrore = messageSecurityContext.getMsgErrore();
							codiceErroreCooperazione = messageSecurityContext.getCodiceErrore();
						}
					}catch(Exception e){
						posizione = "MessageSecurityPortaDelegataResponseFlow";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
								get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_536_CONFIGURAZIONE_NON_DISPONIBILE);
						configException = e;
					}
				}
			}
			
			/* *** ReadSecurityInformation *** */
			if(erroreIntegrazione==null && codiceErroreCooperazione==null){
				if(messageSecurityContext!=null && messageSecurityContext.getDigestReader()!=null){
					try{
						this.localForwardParameter.getMsgDiag().mediumDebug("ReadSecurityInformation (PD-Response) ...");
						IValidazioneSemantica validazioneSemantica = this.localForwardParameter.getProtocolFactory().createValidazioneSemantica();
						this.securityInfoResponse = validazioneSemantica.readSecurityInformation(messageSecurityContext.getDigestReader(),responseMessage);
					}catch(Exception e){
						posizione = "LetturaInformazioniSicurezzaPDResponse";
						erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(CodiceErroreIntegrazione.CODICE_549_SECURITY_INFO_READER_ERROR);
						configException = e;
					}
				}
			}
			
			/* *** MTOM Processor AfterSecurity  *** */
			if(mtomProcessor!=null && erroreIntegrazione==null){
				try{
					mtomProcessor.mtomAfterSecurity(responseMessage, TipoTraccia.RISPOSTA);
				}catch(Exception e){
					logDiagnosticError = false; // L'errore viene registrato dentro il metodo mtomProcessor.mtomAfterSecurity
					
					posizione = "MTOMProcessor(AfterSec-"+mtomProcessor.getMTOMProcessorType()+")PDResponse";
					erroreIntegrazione = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
							get5XX_ErroreProcessamento(e.getMessage(),CodiceErroreIntegrazione.CODICE_557_MTOM_PROCESSOR_ERROR);
					configException = e;
				}
			}
			
			/* *** Gestione eventuale errore  *** */
			if(erroreIntegrazione!=null || codiceErroreCooperazione!= null){
				if(erroreIntegrazione!=null){
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(erroreIntegrazione.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(erroreIntegrazione,configException,
							(responseMessage!=null ? responseMessage.getParseException() : null));
				}else{
					Eccezione ecc = Eccezione.getEccezioneValidazione(ErroriCooperazione.MESSAGE_SECURITY.getErroreMessageSecurity(msgErrore, codiceErroreCooperazione),
							this.localForwardParameter.getProtocolFactory());
					if(logDiagnosticError){
						this.localForwardParameter.getMsgDiag().logErroreGenerico(ecc.getDescrizione(this.localForwardParameter.getProtocolFactory()), 
								posizione);
					}
					this.responseMessageError = this.erroreApplicativoBuilder.toMessage(ecc,this.richiestaDelegata.getSoggettoFruitore(),null);
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
	
	public void sendErrore(ErroreIntegrazione errore,Throwable eErrore,ParseException parseException) throws LocalForwardException{
	
		try{
		
			OpenSPCoop2Message responseMessageError = 
					this.erroreApplicativoBuilder.toMessage(errore,eErrore,parseException);
			this.sendErrore(responseMessageError);
			
		}catch(Exception e){
			throw new LocalForwardException(e.getMessage(),e);
		}	
	}
	
	public void sendErrore(Eccezione errore,IDSoggetto dominio,ParseException parseException) throws LocalForwardException{
		
		try{
		
			OpenSPCoop2Message responseMessageError = 
					this.erroreApplicativoBuilder.toMessage(errore,dominio,parseException);
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
