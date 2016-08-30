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

import java.sql.Timestamp;
import java.util.Hashtable;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.slf4j.Logger;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.message.Costanti;
import org.openspcoop2.message.MailcapActivationReader;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.ParseException;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.autenticazione.Credenziali;
import org.openspcoop2.pdd.core.connettori.IConnettore;
import org.openspcoop2.pdd.core.connettori.RepositoryConnettori;
import org.openspcoop2.pdd.core.handlers.GestoreHandlers;
import org.openspcoop2.pdd.core.handlers.PostOutResponseContext;
import org.openspcoop2.pdd.core.handlers.PreInRequestContext;
import org.openspcoop2.pdd.logger.MsgDiagnosticiProperties;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.services.connector.ConnectorException;
import org.openspcoop2.pdd.services.connector.ConnectorInMessage;
import org.openspcoop2.pdd.services.connector.ConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorInMessage;
import org.openspcoop2.pdd.services.connector.DirectVMConnectorOutMessage;
import org.openspcoop2.pdd.services.connector.DirectVMProtocolInfo;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.builder.ErroreApplicativoBuilder;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.builder.EsitoTransazione;
import org.openspcoop2.protocol.sdk.builder.InformazioniErroriInfrastrutturali;
import org.openspcoop2.protocol.sdk.builder.ProprietaErroreApplicativo;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.EsitoTransazioneName;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;

/**
 * Servlet che serve per creare un tunnel da un servizio che non conosce SOAP verso OpenSPCoop (che utilizza SOAP)
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class RicezioneContenutiApplicativiHTTPtoSOAP  {

	
	public static OpenSPCoop2MessageFactory factory = OpenSPCoop2MessageFactory.getMessageFactory();

	public void process(ConnectorInMessage req, ConnectorOutMessage res) throws ConnectorException {
		
		// IDModulo
		String idModulo = req.getIdModulo();
		IDService idModuloAsService = req.getIdModuloAsIDService();
		
		// Timestamp
		Timestamp dataIngressoMessaggio = DateManager.getTimestamp();

		// Log
		Logger logCore = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();
		if(logCore==null)
			logCore = LoggerWrapperFactory.getLogger(idModulo);
				
		//	Proprieta' OpenSPCoop
		OpenSPCoop2Properties openSPCoopProperties = OpenSPCoop2Properties.getInstance();
		if (openSPCoopProperties == null) {
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			try{
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "ErroreInizializzazioneOpenSPCoopProperties"); 
				res.setStatus(500);
				res.sendResponse(SoapUtils.sbustamentoMessaggio(msg));
				res.flush(false);
				res.close(false);
				return;
			}catch(Throwable e){
				logCore.error("Errore generazione SOAPFault",e);
				throw new ConnectorException("Inizializzazione di OpenSPCoop non correttamente effettuata: OpenSPCoopProperties");
			}
		}
		
		// Configurazione Reader
		ConfigurazionePdDManager configPdDManager = null;
		DumpRaw dumpRaw = null;
		try{
			configPdDManager = ConfigurazionePdDManager.getInstance();
			if(configPdDManager==null || configPdDManager.isInitializedConfigurazionePdDReader()==false){
				throw new Exception("ConfigurazionePdDManager not initialized");
			}
			if(configPdDManager.dumpBinarioPD()){
				dumpRaw = new DumpRaw(logCore,true);
				req = new DumpRawConnectorInMessage(logCore, req);
				res = new DumpRawConnectorOutMessage(logCore, res);
			}
		}catch(Throwable e){
			logCore.error("Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager");
			try{
				OpenSPCoop2Message msg = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(SOAPVersion.SOAP11, "ErroreInizializzazioneConfigurazionePdDManager"); 
				res.setStatus(500);
				res.sendResponse(SoapUtils.sbustamentoMessaggio(msg));
				res.flush(false);
				res.close(false);
				return;
			}catch(Throwable eError){
				logCore.error("Errore generazione SOAPFault",e);
				throw new ConnectorException("Inizializzazione di OpenSPCoop non correttamente effettuata: ConfigurazionePdDManager");
			}
		}
		
		// Logger dei messaggi diagnostici
		MsgDiagnostico msgDiag = new MsgDiagnostico(idModulo);
		msgDiag.setPrefixMsgPersonalizzati(MsgDiagnosticiProperties.MSG_DIAG_RICEZIONE_CONTENUTI_APPLICATIVI);
		
		// PddContext from servlet
		Object oPddContextFromServlet = null;
		try{
			oPddContextFromServlet = req.getAttribute(CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP);
		}catch(Exception e){
			logCore.error("req.getAttribute("+CostantiPdD.OPENSPCOOP2_PDD_CONTEXT_HEADER_HTTP+") error: "+e.getMessage(),e);
		}
		PdDContext pddContextFromServlet = null;
		if(oPddContextFromServlet!=null){
			pddContextFromServlet = (PdDContext) oPddContextFromServlet;
		}		

		
		
		
		
		
		
		/* ------------  Lettura parametri della richiesta ------------- */
		
		// ProtocolFactory 
		// XMLBuilder
		
		
		ErroreApplicativoBuilder erroreApplicativoBuilder = null;
		
		// Proprieta errore applicativo
		ProprietaErroreApplicativo proprietaErroreAppl = null;

		// PostOutResponseContext
		PostOutResponseContext postOutResponseContext = null;
		
		RicezioneContenutiApplicativiContext context = null;
		
		PdDContext pddContext = null;
		String errorImbustamentoSoapNonRiuscito = null;
		OpenSPCoop2Message requestMessage = null;
		OpenSPCoop2Message responseMessage = null;
		IProtocolFactory protocolFactory = null;
		SOAPVersion versioneSoap = SOAPVersion.SOAP11;
		String protocol = null;
		byte[] inputBody = null;
		
		try{
			
			/* --------------- Creo il context che genera l'id univoco ----------------------- */
			
			protocolFactory = req.getProtocolFactory();
			protocol = protocolFactory.getProtocol();
			
			if(!ServletUtils.isContentTypeSupported(versioneSoap, protocolFactory))
				versioneSoap = SOAPVersion.SOAP12; // ??
			
			proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
			proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocol));
			proprietaErroreAppl.setIdModulo(idModulo);
			proprietaErroreAppl.setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			
			erroreApplicativoBuilder = new ErroreApplicativoBuilder(logCore, protocolFactory, 
					openSPCoopProperties.getIdentitaPortaDefault(protocol), null, null, proprietaErroreAppl.getIdModulo(), 
					proprietaErroreAppl, versioneSoap,TipoPdD.DELEGATA,null); 
			
			context = new RicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
			context.setTipoPorta(TipoPdD.DELEGATA);
			context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
			context.setIdModulo(idModulo);
			msgDiag.setPddContext(context.getPddContext(),protocolFactory);		
			pddContext = context.getPddContext();
			
			try{
				msgDiag.logPersonalizzato("ricezioneRichiesta.firstLog");
			}catch(Exception e){
				logCore.error("Errore generazione diagnostico di ingresso",e);
			}
			
			if(dumpRaw!=null){
				dumpRaw.serializeContext(context, protocol);
			}
			
			DirectVMConnectorInMessage vm = null;
			if(req instanceof DirectVMConnectorInMessage){
				vm = (DirectVMConnectorInMessage) req;
			}
			else if(req instanceof DumpRawConnectorInMessage){
				if( ((DumpRawConnectorInMessage)req).getWrappedConnectorInMessage() instanceof DirectVMConnectorInMessage ){
					vm = (DirectVMConnectorInMessage) ((DumpRawConnectorInMessage)req).getWrappedConnectorInMessage();
				}
			}
			if(vm!=null && vm.getDirectVMProtocolInfo()!=null){
				vm.getDirectVMProtocolInfo().setInfo(pddContext);
			}
			
			
			
			
			/* ------------  PostOutResponseContext ------------- */
			postOutResponseContext = new PostOutResponseContext(logCore,protocolFactory);
			postOutResponseContext.setTipoPorta(TipoPdD.DELEGATA);
			postOutResponseContext.setPddContext(pddContext);
			postOutResponseContext.setIdModulo(idModulo);
			
			
			
			
			/* ------------  PreInHandler ------------- */
			
			// build context
			PreInRequestContext preInRequestContext = new PreInRequestContext(pddContext);
			if(pddContextFromServlet!=null){
				preInRequestContext.getPddContext().addAll(pddContextFromServlet, true);
			}
			preInRequestContext.setTipoPorta(TipoPdD.DELEGATA);
			preInRequestContext.setIdModulo(idModulo);
			preInRequestContext.setProtocolFactory(protocolFactory);
			Hashtable<String, Object> transportContext = new Hashtable<String, Object>();
			transportContext.put(PreInRequestContext.SERVLET_REQUEST, req);
			transportContext.put(PreInRequestContext.SERVLET_RESPONSE, res);
			preInRequestContext.setTransportContext(transportContext);	
			preInRequestContext.setLogCore(logCore);
			
			// invocazione handler
			GestoreHandlers.preInRequest(preInRequestContext, msgDiag, logCore);
			
			// aggiungo eventuali info inserite nel preInHandler
			pddContext.addAll(preInRequestContext.getPddContext(), false);
			
			// Lettura risposta parametri NotifierInputStream
			NotifierInputStreamParams notifierInputStreamParams = preInRequestContext.getNotifierInputStreamParams();
			context.setNotifierInputStreamParams(notifierInputStreamParams);
			
			if(dumpRaw!=null){
				dumpRaw.serializeRequest(((DumpRawConnectorInMessage)req), false, notifierInputStreamParams);
			}

			
			
			


			
			
			
			
			/* ------------ Controllo ContentType -------------------- */
			
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.tipologiaMessaggio");
			
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
			context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.SOAP_VERSION, SOAPVersion.SOAP11);
			
			
			
			
			
			/* ------------  SoapAction check 1 (controlla che non sia null e ne ritorna il valore) ------------- */			
			String soapAction = null;
			try{
				soapAction = req.getSOAPAction(versioneSoap,"application/xml"); // TODO gestione per SOAP1.2
			}catch(Exception e){// se non esiste la soapaction, non la devo trasmettere 
			}
			
			
			
			
			/* ------------  Imbustamento Messaggio di Richiesta ------------- */
			boolean imbustamentoConAttachment = false;
			String tipoAttachment =  Costanti.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
			// HeaderTrasporto
			String imb = req.getHeader(openSPCoopProperties.getTunnelSOAPKeyWord_headerTrasporto());
			if(imb!=null && "true".equals(imb.trim())){
				imbustamentoConAttachment = true;
				String mime = req.getHeader(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(mime!=null)
					tipoAttachment = mime.trim();
			}
			if(imb==null){
				// Proprieta FORMBased
				if(req.getParameter(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased())!=null){
					if("true".equalsIgnoreCase(req.getParameter(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()))){
						imbustamentoConAttachment = true;
						// lettura eventuale tipo di attachment
						if(req.getParameter(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())!=null){
							tipoAttachment = req.getParameter(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased());
						}
					}
				}
			}
			if(imb==null){
				// Vedo se fosse indicato nel transport Context
				if(transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased())!=null){
					if("true".equalsIgnoreCase((String)transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWord_urlBased()))){
						imbustamentoConAttachment = true;
						// lettura eventuale tipo di attachment
						if(transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased())!=null){
							tipoAttachment = (String) transportContext.get(openSPCoopProperties.getTunnelSOAPKeyWordMimeType_urlBased());
						}
					}
				}
			}

			String tipoLetturaRisposta = null;
			try{
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Pre costruzione richiesta");
				msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.inCorso");
				inputBody = req.getRequest();
				if( inputBody == null || inputBody.length<=0 ){
					throw new Exception("Ricevuto nessun contenuto da imbustare");
				}
				req.close();
				
				if(imbustamentoConAttachment){
					tipoLetturaRisposta = "Costruzione messaggio SOAP per Tunnel con mimeType "+tipoAttachment;
					requestMessage = SoapUtils.
						imbustamentoMessaggioConAttachment(SOAPVersion.SOAP11,inputBody,tipoAttachment,
									MailcapActivationReader.existsDataContentHandler(tipoAttachment),req.getContentType(), openSPCoopProperties.getHeaderSoapActorIntegrazione());
				}else{
					tipoLetturaRisposta = "Imbustamento messaggio in un messaggio SOAP";
					OpenSPCoop2MessageParseResult pr = SoapUtils.imbustamentoMessaggio(notifierInputStreamParams, inputBody, 
							openSPCoopProperties.isDeleteInstructionTargetMachineXml(), openSPCoopProperties.isFileCacheEnable(), 
							openSPCoopProperties.getAttachmentRepoDir(), openSPCoopProperties.getFileThreshold());
					if(pr.getParseException()!=null){
						pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION, pr.getParseException());
					}
					requestMessage = pr.getMessage_throwParseException();
				}
				Utilities.printFreeMemory("RicezioneContenutiApplicativiHTTPtoSOAP - Post costruzione richiesta");
				requestMessage.setProtocolName(protocolFactory.getProtocol());
				
			}catch(Exception e){
				logCore.error(tipoLetturaRisposta +" con errore: "+e.getMessage(),e);
				errorImbustamentoSoapNonRiuscito=tipoLetturaRisposta +" con errore: "+e.getMessage();
				throw e;
			}
			
			
			
			/* ------------  SoapAction check 2 ------------- */	
			if(soapAction!=null){
				if(openSPCoopProperties.checkSoapActionQuotedString_ricezioneContenutiApplicativi()){
					ServletUtils.checkSoapActionQuotedString(soapAction,versioneSoap);
				}
				context.setSoapAction(soapAction);
				requestMessage.setProperty(Costanti.SOAP_ACTION, soapAction);
				requestMessage.getMimeHeaders().addHeader(org.openspcoop2.message.Costanti.SOAP_ACTION, soapAction);
			}
			
			/* ------------  Parametri della porta delegata invocata ------------- */
			URLProtocolContext urlProtocolContext = req.getURLProtocolContext();
			requestMessage.setTransportRequestContext(urlProtocolContext);							
			
			/* ------------  Raccolta Credenziali ------------- */			
			Credenziali credenziali = req.getCredenziali();
					
			/* ------------  Location ------------- */
			context.setFromLocation(req.getLocation(credenziali));
			
		
			/* ------------  Raccolta dati di Richiesta ------------- */
		
			// Contesto di Richiesta
			context.setCredenziali(credenziali);
			context.setGestioneRisposta(true); // siamo in una servlet, la risposta deve essere aspettata
			context.setInvocazionePDPerRiferimento(false); // la PD con questa servlet non effettuera' mai invocazioni per riferimento.
			context.setMessageRequest(requestMessage);
			context.setUrlProtocolContext(urlProtocolContext);
			context.setMsgDiagnostico(msgDiag);
					
			// Log elaborazione dati completata
			msgDiag.logPersonalizzato("ricezioneRichiesta.elaborazioneDati.completata");
			
			// Invocazione...
			RicezioneContenutiApplicativi gestoreRichiesta = new RicezioneContenutiApplicativi(context);
			gestoreRichiesta.process(req);
			responseMessage = context.getMessageResponse();
						
		} catch (ProtocolException e){
			try{
				// Capita nell'instanziazione della ProtocolFactory. Uso la factory basic per costruire l'errore.
				protocolFactory = ProtocolFactoryManager.getInstance().getDefaultProtocolFactory();
				
				proprietaErroreAppl = openSPCoopProperties.getProprietaGestioneErrorePD(protocolFactory.createProtocolManager());
				proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocolFactory.getProtocol()));
				proprietaErroreAppl.setIdModulo(idModulo);
				proprietaErroreAppl.setFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
				
				erroreApplicativoBuilder = this.newErroreApplicativoBuilder(req, logCore, protocolFactory, openSPCoopProperties, idModulo, proprietaErroreAppl);
				
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				
				pddContext = context.getPddContext();
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
				
				msgDiag.setPddContext(pddContext,ProtocolFactoryManager.getInstance().getDefaultProtocolFactory());
				msgDiag.logErroreGenerico(e, "MessaggioRichiestaMalformato");
				
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e),e,null);
			}catch(ProtocolException ep){
				throw new ConnectorException(ep.getMessage(),ep);
			}
			
		} catch (Throwable e) {
			
			if(context==null){
				// Errore durante la generazione dell'id
				context = RicezioneContenutiApplicativiContext.newRicezioneContenutiApplicativiContext(idModuloAsService,dataIngressoMessaggio,openSPCoopProperties.getIdentitaPortaDefault(protocol));
				context.setTipoPorta(TipoPdD.DELEGATA);
				context.setForceFaultAsXML(true); // siamo in una richiesta http senza SOAP, un SoapFault non ha senso
				context.setIdModulo(idModulo);
				context.getPddContext().addObject(org.openspcoop2.core.constants.Costanti.PROTOCOLLO, protocolFactory.getProtocol());
				pddContext = context.getPddContext();
				msgDiag.setPddContext(pddContext,protocolFactory);
				if(postOutResponseContext!=null){
					postOutResponseContext.setPddContext(pddContext);
				}
			}
			
			// Se viene lanciata una eccezione, riguarda la richiesta, altrimenti è gestita dopo nel finally.
			Throwable tParsing = null;
			if(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)){
				tParsing = ((ParseException) pddContext.removeObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION)).getParseException();
			}
			if(tParsing==null && (requestMessage==null || requestMessage.getParseException() == null)){
				tParsing = MessageUtils.getParseException(e);
			}
					
			// Verifico se e' stato instanziato l'erroreApplicativoBuilder (es. eccezione lanciata da PreInRequestHandler non fa inizializzare l'oggetto)
			if(erroreApplicativoBuilder==null){
				erroreApplicativoBuilder = newErroreApplicativoBuilder(req, logCore, protocolFactory, openSPCoopProperties, idModulo, proprietaErroreAppl);
			}
			
			// Genero risposta con errore
			if(errorImbustamentoSoapNonRiuscito!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				logCore.error("ImbustamentoSOAP",e);
				Throwable tMessage = null;
				if(tParsing!=null){
					tMessage = tParsing;
				}
				else{
					tMessage = e;
				}
				String msgErrore = tMessage.getMessage();
				if(msgErrore==null){
					msgErrore = tMessage.toString();
				}
				msgDiag.logErroreGenerico(errorImbustamentoSoapNonRiuscito+"  "+msgErrore, "ImbustamentoSOAP");
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_422_IMBUSTAMENTO_SOAP_NON_RIUSCITO_RICHIESTA_APPLICATIVA.
						getErrore422_MessaggioSOAPNonGenerabileTramiteImbustamentoSOAP(errorImbustamentoSoapNonRiuscito),tMessage,null);
			}
			else if(tParsing!=null){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				String msgErrore = tParsing.getMessage();
				if(msgErrore==null){
					msgErrore = tParsing.toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",e);
				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
						getErrore432_MessaggioRichiestaMalformato(tParsing),tParsing,null);
			}
			else{
				logCore.error("ErroreGenerale",e);
				msgDiag.logErroreGenerico(e, "Generale(richiesta)");
				responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.getErrore426_ServletError(true, e),e,null);
			}
		}
		finally{
			
			if((requestMessage!=null && requestMessage.getParseException() != null) || 
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO, true);
				ParseException parseException = null;
				if( requestMessage!=null && requestMessage.getParseException() != null ){
					parseException = requestMessage.getParseException();
				}
				else{
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RICHIESTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				}
				String msgErrore = parseException.getParseException().getMessage();
				if(msgErrore==null){
					msgErrore = parseException.getParseException().toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRichiesta",parseException.getSourceException());
				msgDiag.logPersonalizzato("parsingExceptionRichiesta");
				try{
					responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_432_PARSING_EXCEPTION_RICHIESTA.
							getErrore432_MessaggioRichiestaMalformato(parseException.getParseException()),
							parseException.getParseException(),null);
				}catch(Exception eBuildError){
					logCore.error("Creazione del messaggio di fault 432 fallita",eBuildError);
					responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, eBuildError);
				}
			}
			else if( (responseMessage!=null && responseMessage.getParseException() != null) ||
					(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
				pddContext.addObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO, true);
				ParseException parseException = null;
				if( responseMessage!=null && responseMessage.getParseException() != null ){
					parseException = responseMessage.getParseException();
				}
				else{
					parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
				}
				String msgErrore = parseException.getParseException().getMessage();
				if(msgErrore==null){
					msgErrore = parseException.getParseException().toString();
				}
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
				logCore.error("parsingExceptionRisposta",parseException.getSourceException());
				msgDiag.logPersonalizzato("parsingExceptionRisposta");
				try{
					responseMessage = erroreApplicativoBuilder.toMessage(ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
							getErrore440_MessaggioRispostaMalformato(parseException.getParseException()),
							parseException.getParseException(),null);
				}catch(Exception eBuildError){
					logCore.error("Creazione del messaggio di fault 440 fallita",eBuildError);
					responseMessage = OpenSPCoop2MessageFactory.getMessageFactory().createFaultMessage(versioneSoap, eBuildError);
				}
			}
			
			try{
				// Se non sono stati recuperati i dati delle url, provo a recuperarli
				URLProtocolContext urlProtocolContext = context.getUrlProtocolContext();
				if(urlProtocolContext==null){
					urlProtocolContext = req.getURLProtocolContext();
				}
				if(urlProtocolContext!=null){
					String urlInvocazione = urlProtocolContext.getUrlInvocazione_formBased();
					if(urlProtocolContext.getFunction()!=null){
						urlInvocazione = "["+urlProtocolContext.getFunction()+"] "+urlInvocazione;
					}
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.URL_INVOCAZIONE, urlInvocazione);
				}
			}catch(Throwable t){}
			try{
				Credenziali credenziali = context.getCredenziali();
				if(credenziali==null){
					credenziali = req.getCredenziali();
				}
				if(credenziali!=null){
					pddContext.addObject(org.openspcoop2.core.constants.Costanti.CREDENZIALI_INVOCAZIONE, credenziali.toString());
				}
			}catch(Throwable t){}
			
			// *** GB ***
			try{
				req.close();
			}catch(Exception e){
				logCore.error("Request.close() error: "+e.getMessage(),e);
			}
			// *** GB ***
		}
			
			
		// Imposto risposta
		if(context.getMsgDiagnostico()!=null){
			msgDiag = context.getMsgDiagnostico();
		}
		if(context.getHeaderIntegrazioneRisposta()!=null){
			java.util.Enumeration<?> en = context.getHeaderIntegrazioneRisposta().keys();
	    	while(en.hasMoreElements()){
	    		String key = (String) en.nextElement();
	    		String value = null;
	    		try{
	    			value = context.getHeaderIntegrazioneRisposta().getProperty(key);
	    			res.setHeader(key,value);
	    		}catch(Exception e){
	    			logCore.error("Request.setHeader("+key+","+value+") error: "+e.getMessage(),e);
	    		}
	    	}	
		}
		if(context!=null && context.getProtocol()!=null){
			erroreApplicativoBuilder.setMittente(context.getProtocol().getFruitore());
			IDServizio idServizio = new IDServizio();
			idServizio.setSoggettoErogatore(context.getProtocol().getErogatore());
			idServizio.setTipoServizio(context.getProtocol().getTipoServizio());
			idServizio.setServizio(context.getProtocol().getServizio());
			idServizio.setAzione(context.getProtocol().getAzione());
			erroreApplicativoBuilder.setServizio(idServizio);
			erroreApplicativoBuilder.setDominio(context.getIdentitaPdD());
			erroreApplicativoBuilder.setProprietaErroreApplicato(context.getProprietaErroreAppl());
		}
		if(context!=null && context.getIntegrazione()!=null){
			erroreApplicativoBuilder.setServizioApplicativo(context.getIntegrazione().getServizioApplicativoFruitore());
		}
		DirectVMConnectorOutMessage vm = null;
		if(res instanceof DirectVMConnectorOutMessage){
			vm = (DirectVMConnectorOutMessage) res;
		}
		else if(req instanceof DumpRawConnectorOutMessage){
			if( ((DumpRawConnectorOutMessage)res).getWrappedConnectorOutMessage() instanceof DirectVMConnectorOutMessage ){
				vm = (DirectVMConnectorOutMessage) ((DumpRawConnectorOutMessage)res).getWrappedConnectorOutMessage();
			}
		}
		if(vm!=null){
			if(context!=null && context.getPddContext()!=null){
				DirectVMProtocolInfo pInfo = new DirectVMProtocolInfo();
				Object oIdTransazione = context.getPddContext().getObject(org.openspcoop2.core.constants.Costanti.CLUSTER_ID);
				if(oIdTransazione!=null){
					pInfo.setIdTransazione((String)oIdTransazione);
				}
				if(context.getProtocol()!=null){
					if(context.getProtocol().getIdRichiesta()!=null){
						pInfo.setIdMessaggioRichiesta(context.getProtocol().getIdRichiesta());
					}
					if(context.getProtocol().getIdRisposta()!=null){
						pInfo.setIdMessaggioRisposta(context.getProtocol().getIdRisposta());
					}
				}
				vm.setDirectVMProtocolInfo(pInfo);
			}
		}
		
		InformazioniErroriInfrastrutturali informazioniErrori = ServletUtils.readInformazioniErroriInfrastrutturali(pddContext);
		
		SOAPBody body = null;
		EsitoTransazione esito = null;
		String descrizioneSoapFault = "";
		int statoServletResponse = 200;
		Throwable erroreConsegnaRisposta = null; 
		boolean httpEmptyResponse = false;
		long lengthOutResponse = -1;
		boolean erroreConnessioneClient = false;
		try{
			if(responseMessage!=null){
				
				// force response code
				boolean forced = false;
				if(responseMessage.getForcedResponseCode()!=null){
					try{
						statoServletResponse = Integer.parseInt(responseMessage.getForcedResponseCode());
						forced = true;
					}catch(Exception e){}
				}
				
				String contentTypeRisposta = null;
				byte[] risposta = null;
				body = responseMessage.getSOAPBody();
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), responseMessage, context.getProprietaErroreAppl(),informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				if(body!=null && body.hasFault()){
					statoServletResponse = 500; // cmq e' un errore come l'errore applicativo
					String msgError = SoapUtils.toString(body.getFault(), false);
					//risposta=msgError.getBytes();
					org.openspcoop2.message.XMLUtils xmlUtils = org.openspcoop2.message.XMLUtils.getInstance();
					risposta=xmlUtils.toByteArray(body.getFault(), true);
					//System.out.println("ELABORATO:"+new String(risposta));
					contentTypeRisposta = responseMessage.getContentType();
					descrizioneSoapFault = " ("+msgError+")";	
				}else{
					risposta=SoapUtils.sbustamentoMessaggio(responseMessage);
					if(risposta==null || risposta.length<=0){
						// Si puo' entrare in questo caso, se nel messaggio Soap vi era presente solo l'header
						risposta = null;
						if(!forced)
							statoServletResponse = 202;
					}else{
						
						SOAPElement child = SoapUtils.getNotEmptyFirstChildSOAPElement(body);
						if(child!=null){
							if(protocolFactory.createErroreApplicativoBuilder().isErroreApplicativo(child)){
								statoServletResponse = 500;
							}
						}
						
						// Non serve la updateContentType. Il messaggio e' gia' stato serializzato ed il cType e' corretto.  
						if(SoapUtils.isTunnelOpenSPCoopSoap(body)){
							contentTypeRisposta = SoapUtils.getContentTypeTunnelOpenSPCoopSoap(responseMessage.getSOAPBody());
						}else{
							contentTypeRisposta = responseMessage.getContentType();
						}
					}
				}
				
				// transfer length
				if(risposta!=null){
					lengthOutResponse = risposta.length;
					ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
							req, res, new Long(risposta.length));
				}
				
				// httpstatus
				res.setStatus(statoServletResponse);
				
				// content type
				if(contentTypeRisposta!=null){
					res.setContentType(contentTypeRisposta);
				}
				
				// contenuto
				if(risposta!=null){
					res.sendResponse(risposta);
				}

			}else{
				// httpstatus
				statoServletResponse = protocolFactory.createProtocolManager().getHttpReturnCodeEmptyResponseOneWay();
				res.setStatus(statoServletResponse);
				httpEmptyResponse = true;
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(), responseMessage, context.getProprietaErroreAppl(),informazioniErrori,
						(pddContext!=null ? pddContext.getContext() : null));
				// carico-vuoto
			}
			
		}catch(Throwable e){
			logCore.error("ErroreGenerale",e);
			erroreConsegnaRisposta = e;
			
			erroreConnessioneClient = ServletUtils.isConnessioneClientNonDisponibile(e);
			
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
			
			// Genero risposta con errore
			try{
				proprietaErroreAppl.setDominio(openSPCoopProperties.getIdentificativoPortaDefault(protocol));
				proprietaErroreAppl.setIdModulo(idModulo);
				byte [] rispostaErrore = null;
				if( (responseMessage!=null && responseMessage.getParseException() != null) ||
						(pddContext.containsKey(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION))){
					ParseException parseException = null;
					if( responseMessage!=null && responseMessage.getParseException() != null ){
						parseException = responseMessage.getParseException();
					}
					else{
						parseException = (ParseException) pddContext.getObject(org.openspcoop2.core.constants.Costanti.CONTENUTO_RISPOSTA_NON_RICONOSCIUTO_PARSE_EXCEPTION);
					}
					String msgErrore = parseException.getParseException().getMessage();
					if(msgErrore==null){
						msgErrore = parseException.getParseException().toString();
					}
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_PROCESSAMENTO, msgErrore);
					logCore.error("parsingExceptionRisposta",parseException.getSourceException());
					msgDiag.logPersonalizzato("parsingExceptionRisposta");
					rispostaErrore = erroreApplicativoBuilder.toByteArray(ErroriIntegrazione.ERRORE_440_PARSING_EXCEPTION_RISPOSTA.
							getErrore440_MessaggioRispostaMalformato(parseException.getParseException()));
					
					InformazioniErroriInfrastrutturali informazioniErrori_error = new InformazioniErroriInfrastrutturali();
					informazioniErrori_error.setContenutoRispostaNonRiconosciuto(true);
					esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),null, context.getProprietaErroreAppl(), informazioniErrori_error,
							(pddContext!=null ? pddContext.getContext() : null));
				} 
				else{
					rispostaErrore = erroreApplicativoBuilder.toByteArray(ErroriIntegrazione.ERRORE_426_SERVLET_ERROR.
							getErrore426_ServletError(false, e));
				}
				
				// transfer length
				lengthOutResponse = rispostaErrore.length;
				ServletUtils.setTransferLength(openSPCoopProperties.getTransferLengthModes_ricezioneContenutiApplicativi(), 
						req, res, new Long(rispostaErrore.length));
				
				// httpstatus
				//statoServletResponse = 500; // Nella servlet con sbustamento non devo ritornare 500
				res.setStatus(200);
				
				// content type
				res.setContentType("text/xml");
				
				// contenuto
				res.sendResponse(rispostaErrore);
						
			}catch(Exception error){
				
				if(!erroreConnessioneClient){
					erroreConnessioneClient = ServletUtils.isConnessioneClientNonDisponibile(error);
				}
				
				logCore.error("Generazione di un risposta errore non riuscita",error);
				statoServletResponse = 500; // ERRORE EFFETTIVO!
				try{
					res.setStatus(500);
				}catch(Exception eStatus){
					logCore.error("Response.setStatus(500) error: "+eStatus.getMessage(),eStatus);
				}
				byte[] ris = error.toString().getBytes();
				try{
					res.sendResponse(ris);
				}catch(Exception erroreStreamChiuso){ 
					erroreConnessioneClient = true;
					//se lo stream non e' piu' disponibile non si potra' consegnare alcuna risposta
				}
				lengthOutResponse = ris.length;
			}
			
		}
		finally{
						
			statoServletResponse = res.getResponseStatus(); // puo' essere "trasformato" da api engine
			msgDiag.addKeyword(CostantiPdD.KEY_CODICE_CONSEGNA, ""+statoServletResponse);
			msgDiag.addKeyword(CostantiPdD.KEY_SOAP_FAULT, descrizioneSoapFault);
			
			try{
				
				// Flush and close response
				// NOTA: per poter ottenere l'errore di BrokenPipe sempre, deve essere disabilitato il socketBufferOutput sul servlet container.
				// Se non lo si disabilta, l'errore viene ritornato solo se il messaggio supera la dimensione del buffer (default: 8192K)
				// Ad esempio in tomcat utilizzare (socketBuffer="-1"): 
				//    <Connector protocol="HTTP/1.1" port="8080" address="${jboss.bind.address}" 
	            //       connectionTimeout="20000" redirectPort="8443" socketBuffer="-1" />
				res.flush(true);
				res.close(true);
				
				// Emetto diagnostico
				if(erroreConsegnaRisposta!=null){
					
					// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
					msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, erroreConsegnaRisposta.toString()); // NOTA: lasciare e.toString()
					msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
					
				}else{
					if(httpEmptyResponse){
						msgDiag.logPersonalizzato("consegnaRispostaApplicativaVuota");
					}else{
						if(statoServletResponse==500)
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaKoEffettuata");
						else
							msgDiag.logPersonalizzato("consegnaRispostaApplicativaOkEffettuata");
					}
				}
				
			}catch(Exception e){
				
				erroreConnessioneClient = true;
				
				logCore.error("Chiusura stream non riuscita",e);
				
				// Risposta non ritornata al servizio applicativo, il socket verso il servizio applicativo era chiuso o cmq inutilizzabile
				msgDiag.addKeyword(CostantiPdD.KEY_ERRORE_CONSEGNA, e.toString()); // NOTA: lasciare e.toString()
				
				msgDiag.logPersonalizzato("consegnaRispostaApplicativaFallita");
				
				erroreConsegnaRisposta = e;
				
				if(esito!=null){
					if(EsitoTransazioneName.OK.equals(esito.getName())){
						// non è ok, essendo andato in errore il flush
						try{
							esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
						}catch(Exception eBuildError){
							esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
						}
					}
				}
				else{
					// non dovrebbe mai essere null
				}
				
			}
			
			if(dumpRaw!=null){
				dumpRaw.serializeResponse(((DumpRawConnectorOutMessage)res));
			}
			
		}
		
		if(erroreConnessioneClient){
			// forzo esito errore connessione client
			try{
				esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_CONNESSIONE_CLIENT_NON_DISPONIBILE);
			}catch(Exception eBuildError){
				esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
			}
		}
		
		
		
		
		
		
		
		
		// *** Chiudo connessione verso PdD Destinazione per casi stateless ***
		String location = "...";
		try{
			IConnettore c = null;
			if(context.getIdMessage()!=null){
				c = RepositoryConnettori.removeConnettorePD(context.getIdMessage());
			}
			if(c!=null){
				location = c.getLocation();
				c.disconnect();
			}
		}catch(Exception e){
			msgDiag.logDisconnectError(e, location);
		}
		
		

		
		
		
		
		
		
		
		/* ------------  PostOutResponseHandler ------------- */
		
		if(postOutResponseContext!=null){
			try{
				postOutResponseContext.getPddContext().addObject(CostantiPdD.DATA_INGRESSO_MESSAGGIO_RICHIESTA, dataIngressoMessaggio);
				postOutResponseContext.setDataElaborazioneMessaggio(DateManager.getDate());
				if(erroreConsegnaRisposta==null){
					postOutResponseContext.setEsito(esito);
				}else{
					try{
						esito = protocolFactory.createEsitoBuilder().getEsito(req.getURLProtocolContext(),EsitoTransazioneName.ERRORE_PROCESSAMENTO_PDD_5XX);
					}catch(Exception eBuildError){
						esito = EsitoTransazione.ESITO_TRANSAZIONE_ERROR;
					}
					postOutResponseContext.setEsito(esito);
				}
				postOutResponseContext.setReturnCode(statoServletResponse);
				postOutResponseContext.setPropertiesRispostaTrasporto(context.getHeaderIntegrazioneRisposta());
				postOutResponseContext.setProtocollo(context.getProtocol());
				postOutResponseContext.setIntegrazione(context.getIntegrazione());
				if(context.getTipoPorta()!=null)
					postOutResponseContext.setTipoPorta(context.getTipoPorta());	
				postOutResponseContext.setIdModulo(idModulo);
				
				if(inputBody!=null){
					postOutResponseContext.setInputRequestMessageSize(new Long(inputBody.length));
				}
				if(requestMessage!=null){
					//postOutResponseContext.setInputRequestMessageSize(requestMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputRequestMessageSize(requestMessage.getOutgoingMessageContentLength());
				}else{
					postOutResponseContext.setInputRequestMessageSize(req.getContentLength()+0l);
				}
				
				if(erroreConsegnaRisposta==null && responseMessage!=null){
					postOutResponseContext.setInputResponseMessageSize(responseMessage.getIncomingMessageContentLength());
					postOutResponseContext.setOutputResponseMessageSize(lengthOutResponse); // sbustata!
					postOutResponseContext.setMessaggio(responseMessage);
				}
								
			}catch(Exception e){
				msgDiag.logErroreGenerico(e,"postOutResponse, preparazione contesto");
			}
			
			GestoreHandlers.postOutResponse(postOutResponseContext, msgDiag, logCore);
		}
		
		
		
		
		
		
		
		
		
		
		// *** Rilascio risorse NotifierInputStream ***
		
		// request
		try{
			if(requestMessage!=null && requestMessage.getNotifierInputStream()!=null){
				requestMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream richiesta");
		}
		
		// response
		try{
			if(responseMessage!=null && responseMessage.getNotifierInputStream()!=null){
				responseMessage.getNotifierInputStream().close();
			}
		}catch(Exception e){
			msgDiag.logErroreGenerico(e,"Rilascio risorse NotifierInputStream risposta");
		}
		
		
		
		
		
		
		
		
		
		// *** GB ***
		requestMessage = null;
		body = null;
		responseMessage = null;
		// *** GB ***
		
		return;

	}

	
	private ErroreApplicativoBuilder newErroreApplicativoBuilder(ConnectorInMessage req,Logger logCore,
			IProtocolFactory protocolFactory, OpenSPCoop2Properties openSPCoopProperties,
			String idModulo, ProprietaErroreApplicativo proprietaErroreAppl) throws ConnectorException{
		try{
			SOAPVersion versioneSoap = SOAPVersion.SOAP11;
			try{
				String contentTypeReq = req.getContentType();
				versioneSoap = ServletUtils.getVersioneSoap(logCore,contentTypeReq);
			}catch(Exception e){
				//ignore}
			}			
			return new ErroreApplicativoBuilder(logCore, protocolFactory, 
					openSPCoopProperties.getIdentitaPortaDefault(protocolFactory.getProtocol()), null, null, idModulo, 
					proprietaErroreAppl, versioneSoap,TipoPdD.DELEGATA,null);
		}catch(Throwable ep){
			throw new ConnectorException(ep.getMessage(),ep);
		}
	}

}
