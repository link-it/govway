/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it). 
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



package org.openspcoop2.pdd.core.trasformazioni;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.TrasformazioneRegola;
import org.openspcoop2.core.config.TrasformazioneRegolaRichiesta;
import org.openspcoop2.core.config.TrasformazioneRegolaRisposta;
import org.openspcoop2.core.config.Trasformazioni;
import org.openspcoop2.core.config.constants.StatoFunzionalita;
import org.openspcoop2.core.config.constants.VersioneSOAP;
import org.openspcoop2.core.constants.TipoPdD;
import org.openspcoop2.core.id.IDPortaApplicativa;
import org.openspcoop2.core.id.IDPortaDelegata;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.PdDContext;
import org.openspcoop2.pdd.core.behaviour.built_in.multi_deliver.MessaggioDaNotificare;
import org.openspcoop2.pdd.core.dynamic.Costanti;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.dynamic.ErrorHandler;
import org.openspcoop2.pdd.core.dynamic.MessageContent;
import org.openspcoop2.pdd.core.dynamic.Template;
import org.openspcoop2.pdd.core.transazioni.Transaction;
import org.openspcoop2.pdd.logger.MsgDiagnostico;
import org.openspcoop2.pdd.services.connector.FormUrlEncodedHttpServletRequest;
import org.openspcoop2.pdd.services.error.AbstractErrorGenerator;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroreIntegrazione;
import org.openspcoop2.protocol.sdk.constants.ErroriIntegrazione;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.ContentTypeUtilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpServletTransportRequestContext;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * Gestione delle trasformazioni
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public class GestoreTrasformazioni {

	/** Logger utilizzato per debug. */
	private Logger log = null;
	private void logDebug(String msg) {
		if(this.log!=null) {
			this.log.debug(msg);
		}
	}
	private void logDebug(String msg, Throwable t) {
		if(this.log!=null) {
			this.log.debug(msg,t);
		}
	}
	private void logError(String msg) {
		if(this.log!=null) {
			this.log.error(msg);
		}
	}
	private void logError(String msg, Throwable t) {
		if(this.log!=null) {
			this.log.error(msg,t);
		}
	}
	
	private static final String TRANSPORT_RESPONSE_CONTEXT_UNAVAILABLE = "Transport Response Context non disponibile";
	private static String getSuffixMessageApplicabilitaContentBasedPattern(String pattern) {
		return " check applicabilità content-based pattern("+pattern+")";
	}

	private IDServizio idServizio;
	private IDSoggetto soggettoFruitore;
	private String servizioApplicativoFruitore;
	private IDServizioApplicativo idServizioApplicativoToken;
	private Trasformazioni trasformazioni;
	private Transaction transaction;
	private PdDContext pddContext;
	private RequestInfo requestInfo;
	private ErroreIntegrazione errore;
	private MsgDiagnostico msgDiag;
	@SuppressWarnings("unused")
	private TipoPdD tipoPdD;
	private AbstractErrorGenerator errorGenerator;

	private OpenSPCoop2Properties op2Properties;
	
	private Map<String, Object> dynamicMapRequest = null;
	private OpenSPCoop2Message messageRequest = null;
	
	@SuppressWarnings("unused")
	private String nomeConnettore; 
	private String nomeServizioApplicativoErogatore; 
	
	private IDPortaApplicativa idPA;
	private IDPortaDelegata idPD;

	private ConfigurazionePdDManager configurazionePdDManager;



	/* ********  C O S T R U T T O R E  ******** */
	/**
	 * Costruttore. 
	 *
	 */
	public GestoreTrasformazioni(Logger alog,
			MsgDiagnostico msgDiag, 
			IDServizio idServizio,
			IDSoggetto soggettoFruitore,
			String servizioApplicativoFruitore,
			Trasformazioni trasformazioni,
			Transaction transaction,
			PdDContext pddContext,
			RequestInfo requestInfo,
			TipoPdD tipoPdD,
			AbstractErrorGenerator errorGenerator,
			ConfigurazionePdDManager configurazionePdDManager,
			IDPortaApplicativa idPA){
		this(alog,
				msgDiag, 
				idServizio,
				soggettoFruitore,
				servizioApplicativoFruitore,
				trasformazioni,
				transaction,
				pddContext,
				requestInfo,
				tipoPdD,
				errorGenerator,
				configurazionePdDManager);
		this.idPA = idPA;
	}
	public GestoreTrasformazioni(Logger alog,
			MsgDiagnostico msgDiag, 
			IDServizio idServizio,
			IDSoggetto soggettoFruitore,
			String servizioApplicativoFruitore,
			Trasformazioni trasformazioni,
			Transaction transaction,
			PdDContext pddContext,
			RequestInfo requestInfo,
			TipoPdD tipoPdD,
			AbstractErrorGenerator errorGenerator,
			ConfigurazionePdDManager configurazionePdDManager,
			IDPortaDelegata idPD){
		this(alog,
				msgDiag, 
				idServizio,
				soggettoFruitore,
				servizioApplicativoFruitore,
				trasformazioni,
				transaction,
				pddContext,
				requestInfo,
				tipoPdD,
				errorGenerator,
				configurazionePdDManager);
		this.idPD = idPD;
	}
	private GestoreTrasformazioni(Logger alog,
			MsgDiagnostico msgDiag, 
			IDServizio idServizio,
			IDSoggetto soggettoFruitore,
			String servizioApplicativoFruitore,
			Trasformazioni trasformazioni,
			Transaction transaction,
			PdDContext pddContext,
			RequestInfo requestInfo,
			TipoPdD tipoPdD,
			AbstractErrorGenerator errorGenerator,
			ConfigurazionePdDManager configurazionePdDManager){
		if(alog!=null){
			this.log = alog;
		}else{
			this.log = LoggerWrapperFactory.getLogger(GestoreTrasformazioni.class);
		}
		this.msgDiag = msgDiag;
		this.idServizio = idServizio;
		this.soggettoFruitore = soggettoFruitore;
		this.servizioApplicativoFruitore = servizioApplicativoFruitore;
		if(pddContext!=null && pddContext.containsKey(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN)) {
    		this.idServizioApplicativoToken = (IDServizioApplicativo) pddContext.getObject(org.openspcoop2.core.constants.Costanti.ID_APPLICATIVO_TOKEN);
    	}
		this.trasformazioni = trasformazioni;
		this.transaction = transaction;
		this.pddContext = pddContext;
		this.requestInfo = requestInfo;
		this.op2Properties = OpenSPCoop2Properties.getInstance();
		this.tipoPdD = tipoPdD;
		this.errorGenerator = errorGenerator;
		this.configurazionePdDManager = configurazionePdDManager;
	}


	
	
	public OpenSPCoop2Message trasformazioneNotifica(OpenSPCoop2Message message, Busta busta,
			MessaggioDaNotificare messageTypeForNotifier, OpenSPCoop2Message responseMessageForNotifier, Context transactionContextForNotifier) throws GestoreTrasformazioniException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startTrasformazioneRichiesta();
		}
		try {
			return this.trasformazioneRichiestaEngine(message, busta, messageTypeForNotifier, responseMessageForNotifier, transactionContextForNotifier);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endTrasformazioneRichiesta();
			}
		}
	}
	public OpenSPCoop2Message trasformazioneRichiesta(OpenSPCoop2Message message, Busta busta) throws GestoreTrasformazioniException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startTrasformazioneRichiesta();
		}
		try {
			return this.trasformazioneRichiestaEngine(message, busta, null, null, null);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endTrasformazioneRichiesta();
			}
		}
	}
	public OpenSPCoop2Message trasformazioneRisposta(OpenSPCoop2Message message, Busta busta) throws GestoreTrasformazioniException{
		if(this.transaction!=null) {
			this.transaction.getTempiElaborazione().startTrasformazioneRisposta();
		}
		try {
			return this.trasformazioneRispostaEngine(message, busta);
		}
		finally {
			if(this.transaction!=null) {
				this.transaction.getTempiElaborazione().endTrasformazioneRisposta();
			}
		}
	}
	
	public ErroreIntegrazione getErrore() {
		return this.errore;
	}
	
	public void setNomeConnettore(String nomeConnettore) {
		this.nomeConnettore = nomeConnettore;
	}
	public void setNomeServizioApplicativoErogatore(String nomeServizioApplicativoErogatore) {
		this.nomeServizioApplicativoErogatore = nomeServizioApplicativoErogatore;
	}
	
	
	private TrasformazioneRegola regolaTrasformazione; // viene valorizzata durante la gestione della richiesta
	private boolean trasformazioneContenutoRichiestaEffettuata = false;
	public boolean isTrasformazioneContenutoRichiestaEffettuata() {
		return this.trasformazioneContenutoRichiestaEffettuata;
	}
	
	private OpenSPCoop2Message trasformazioneRichiestaEngine(OpenSPCoop2Message messageP, Busta busta,
			MessaggioDaNotificare messageTypeForNotifier, OpenSPCoop2Message responseMessageForNotifier, Context transactionContextForNotifier) throws GestoreTrasformazioniException{

		if(this.trasformazioni==null || this.trasformazioni.sizeRegolaList()<=0) {
			this.msgDiag.logPersonalizzato(messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaDisabilitato" : "trasformazione.processamentoRichiestaDisabilitato");
			this.logDebug("Non esistono regole di trasformazione");
			return messageP;
		}
		boolean existsRuleEnabled = false;
		for (int i = 0; i < this.trasformazioni.sizeRegolaList(); i++) {
			TrasformazioneRegola check = this.trasformazioni.getRegola(i);
			if(check.getStato()!=null // per backward compatibility 
					&& StatoFunzionalita.DISABILITATO.equals(check.getStato())) {
				continue;
			}
			existsRuleEnabled = true;
			break;
		}
		if(!existsRuleEnabled) {
			this.msgDiag.logPersonalizzato(messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaDisabilitato" : "trasformazione.processamentoRichiestaDisabilitato");
			this.logDebug("Non esistono regole di trasformazione abilitate");
			return messageP;
		}
				
		
		
		// *** Lettura Contenuto Richiesta ****
		
		MessageContent messageContent = null;
		boolean contenutoNonNavigabile = false;
		Map<String, List<String>> parametriTrasporto = null;
		Map<String, List<String>> parametriUrl = null;
		Map<String, List<String>> parametriForm = null;
		String urlInvocazione = null;
		String nomePortaInvocata = null;
		String interfaceName = null;
		
		try{
			if(messageP!=null) {
				Object nomePortaInvocataObject = messageP.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
				if(nomePortaInvocataObject instanceof String) {
					nomePortaInvocata = (String) nomePortaInvocataObject;
				}
			}
			
			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			if(ServiceBinding.SOAP.equals(messageP.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = messageP.castAsSoap();
				messageContent = new MessageContent(soapMessage, bufferMessageReadOnly, this.pddContext);
			}
			else{
				if(MessageType.XML.equals(messageP.getMessageType()) && messageP.castAsRest().hasContent()){
					OpenSPCoop2RestXmlMessage xml = messageP.castAsRestXml();
					messageContent = new MessageContent(xml, bufferMessageReadOnly, this.pddContext);
				}
				else if(MessageType.JSON.equals(messageP.getMessageType()) && messageP.castAsRest().hasContent()){
					OpenSPCoop2RestJsonMessage json = messageP.castAsRestJson();
					messageContent = new MessageContent(json, bufferMessageReadOnly, this.pddContext);
				}
				else if(MessageType.MIME_MULTIPART.equals(messageP.getMessageType()) && messageP.castAsRest().hasContent()){
					OpenSPCoop2RestMimeMultipartMessage mime = messageP.castAsRestMimeMultipart();
					messageContent = new MessageContent(mime, bufferMessageReadOnly, this.pddContext);
				}
				else {
					contenutoNonNavigabile = true;
				}
			}
			
			
			if(messageP.getTransportRequestContext()!=null) {
				
				interfaceName = messageP.getTransportRequestContext().getInterfaceName();
				
				if(messageP.getTransportRequestContext().getHeaders()!=null &&
					!messageP.getTransportRequestContext().getHeaders().isEmpty()) {
					parametriTrasporto = messageP.getTransportRequestContext().getHeaders();
				}
				else {
					parametriTrasporto = new HashMap<>();
					messageP.getTransportRequestContext().setHeaders(parametriTrasporto);
				}
				
				if(messageP.getTransportRequestContext().getParameters()!=null &&
						!messageP.getTransportRequestContext().getParameters().isEmpty()) {
					parametriUrl = messageP.getTransportRequestContext().getParameters();
				}
				else {
					parametriUrl = new HashMap<>();
					messageP.getTransportRequestContext().setParameters(parametriUrl);
				}
				
				if(messageP.getTransportRequestContext() instanceof HttpServletTransportRequestContext) {
					HttpServletTransportRequestContext httpServletContext = (HttpServletTransportRequestContext) messageP.getTransportRequestContext();
					HttpServletRequest httpServletRequest = httpServletContext.getHttpServletRequest();
					if(httpServletRequest instanceof FormUrlEncodedHttpServletRequest) {
						FormUrlEncodedHttpServletRequest formServlet = (FormUrlEncodedHttpServletRequest) httpServletRequest;
						if(formServlet.getFormUrlEncodedParametersValues()!=null &&
								!formServlet.getFormUrlEncodedParametersValues().isEmpty()) {
							parametriForm = formServlet.getFormUrlEncodedParametersValues();
						}
					}
				}
				
				urlInvocazione = messageP.getTransportRequestContext().getUrlInvocazione_formBased();
			}
			else {
				throw new GestoreTrasformazioniException("Transport Request Context non disponibile");
			}
			
		}catch(Exception e){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, "N.D.");
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Lettura contenuto della richiesta non riuscita: "+e.getMessage();
			this.logError(msgErrore, e);
			throw new GestoreTrasformazioniException(msgErrore,e);
		}
		
		
		
		
		MessageContent messageResponseContent = null;
		Map<String, List<String>> parametriTrasportoRisposta = null;
		// *** Lettura Contenuto Risposta (per notifiche) ****
		try{
			if(messageTypeForNotifier!=null && MessaggioDaNotificare.ENTRAMBI.equals(messageTypeForNotifier) && responseMessageForNotifier!=null) {
			
				boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
				if(ServiceBinding.SOAP.equals(responseMessageForNotifier.getServiceBinding())){
					OpenSPCoop2SoapMessage soapMessage = responseMessageForNotifier.castAsSoap();
					messageResponseContent = new MessageContent(soapMessage, bufferMessageReadOnly, this.pddContext);
				}
				else{
					if(MessageType.XML.equals(responseMessageForNotifier.getMessageType()) && responseMessageForNotifier.castAsRest().hasContent()){
						OpenSPCoop2RestXmlMessage xml = responseMessageForNotifier.castAsRestXml();
						messageResponseContent = new MessageContent(xml, bufferMessageReadOnly, this.pddContext);
					}
					else if(MessageType.JSON.equals(responseMessageForNotifier.getMessageType()) && responseMessageForNotifier.castAsRest().hasContent()){
						OpenSPCoop2RestJsonMessage json = responseMessageForNotifier.castAsRestJson();
						messageResponseContent = new MessageContent(json, bufferMessageReadOnly, this.pddContext);
					}
					else if(MessageType.MIME_MULTIPART.equals(responseMessageForNotifier.getMessageType()) && responseMessageForNotifier.castAsRest().hasContent()){
						OpenSPCoop2RestMimeMultipartMessage mime = responseMessageForNotifier.castAsRestMimeMultipart();
						messageResponseContent = new MessageContent(mime, bufferMessageReadOnly, this.pddContext);
					}
					else {
						contenutoNonNavigabile = true;
					}
				}
				
				if(responseMessageForNotifier.getTransportResponseContext()!=null) {
					if(responseMessageForNotifier.getTransportResponseContext().getHeaders()!=null &&
						!responseMessageForNotifier.getTransportResponseContext().getHeaders().isEmpty()) {
						parametriTrasportoRisposta = responseMessageForNotifier.getTransportResponseContext().getHeaders();
					}
					else {
						parametriTrasportoRisposta = new HashMap<>();
						responseMessageForNotifier.getTransportResponseContext().setHeaders(parametriTrasportoRisposta);
					}
				}
				else {
					throw new GestoreTrasformazioniException(TRANSPORT_RESPONSE_CONTEXT_UNAVAILABLE);
				}
				
			}
			else if(messageTypeForNotifier!=null && MessaggioDaNotificare.RISPOSTA.equals(messageTypeForNotifier)){
				// Si usa sempre messageP in questo caso
				if(messageP.getTransportResponseContext()!=null) {
					if(messageP.getTransportResponseContext().getHeaders()!=null &&
						!messageP.getTransportResponseContext().getHeaders().isEmpty()) {
						parametriTrasportoRisposta = messageP.getTransportResponseContext().getHeaders();
					}
					else {
						parametriTrasportoRisposta = new HashMap<>();
						messageP.getTransportResponseContext().setHeaders(parametriTrasporto);
					}
				}
				else {
					throw new GestoreTrasformazioniException(TRANSPORT_RESPONSE_CONTEXT_UNAVAILABLE);
				}
			}
		}catch(Exception e){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, "N.D.");
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Lettura contenuto della risposta (notifica) non riuscita: "+e.getMessage();
			this.logError(msgErrore, e);
			throw new GestoreTrasformazioniException(msgErrore,e);
		}
		
		
		
		
		// *** Identificazione regola ****
		
		try {
			
			this.logDebug("Identificazione regola di trasformazione tra le "+this.trasformazioni.sizeRegolaList()+" disponibili ...");
			
			for (int i = 0; i < this.trasformazioni.sizeRegolaList(); i++) {
				
				String suffix = "[Regola-"+i+"] ";
				
				this.logDebug(suffix+"Verifica applicabilità della regola ...");
				
				TrasformazioneRegola check = this.trasformazioni.getRegola(i);
				
				
				// prendo la prima che ha un match nell'ordine e non e' sospesa
				if(check.getStato()!=null // per backward compatibility 
						&& StatoFunzionalita.DISABILITATO.equals(check.getStato())) {
					continue;
				}
				
				if(check.getApplicabilita()!=null) {
				
					// controllo azione
					this.logDebug(suffix+" check applicabilità tra le '"+check.getApplicabilita().sizeAzioneList()+"' azioni ");
					if(check.getApplicabilita().sizeAzioneList()>0) {
						boolean found = false;
						for (String checkAzione : check.getApplicabilita().getAzioneList()) {
							if(checkAzione.equals(this.idServizio.getAzione())) {
								found = true;
								break;
							}
						}
						if(!found) {
							continue;
						}
					}
					
					// controllo contentType
					this.logDebug(suffix+" check applicabilità tra i "+check.getApplicabilita().sizeContentTypeList()+" content types ");
					if(check.getApplicabilita().sizeContentTypeList()>0 &&
						!ContentTypeUtilities.isMatch(messageP.getContentType(), check.getApplicabilita().getContentTypeList())) {
						continue;
					}
		
					// Controllo Pattern
					if(check.getApplicabilita().getPattern()!=null && !"".equals(check.getApplicabilita().getPattern())) {
						
						this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+" ...");
						
						if(contenutoNonNavigabile) {
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", messaggio non conforme al match: "+messageP.getMessageType());
							continue; // no match
						}
						
						String elementJson = null;
						Element element = null;
						if(messageContent.isJson()) {
							elementJson = messageContent.getElementJson();
						}
						else {
							element = messageContent.getElement();
						}
						if(element==null && elementJson==null){
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", messaggio ("+messageP.getMessageType()+") senza contenuto");
							continue; // no match
						}
						String valore = null;
						try {
							if(element!=null) {
								AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(messageP.getFactory());
								valore = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, check.getApplicabilita().getPattern(), this.log);
							}
							else {
								valore = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, check.getApplicabilita().getPattern(), this.log);
							}
						}catch(Exception e){
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+" fallita: "+e.getMessage(),e);
						}
						if(valore==null) {
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", match fallito ("+messageP.getMessageType()+")");
							continue;
						}
					}
					
					// controllo connettore
					this.logDebug(suffix+" check applicabilità tra le '"+check.getApplicabilita().sizeConnettoreList()+"' connettori ");
					if(check.getApplicabilita().sizeConnettoreList()>0) {
						boolean found = false;
						for (String checkConnettore : check.getApplicabilita().getConnettoreList()) {
							if(checkConnettore.equals(this.nomeServizioApplicativoErogatore)) {
								found = true;
								break;
							}
						}
						if(!found) {
							continue;
						}
					}
					
					// controllo applicativi e soggetti
					this.logDebug(suffix+" check applicabilità tra i "+check.getApplicabilita().sizeServizioApplicativoList()+" servizi applicativi e i "+check.getApplicabilita().sizeSoggettoList()+" soggetti");
					if(check.getApplicabilita().sizeServizioApplicativoList()>0 && check.getApplicabilita().sizeSoggettoList()>0) {
						boolean applicativi = false;
						boolean soggetti = false;
						if(check.getApplicabilita().sizeServizioApplicativoList()>0) {
							applicativi = GestoreTrasformazioniUtilities.isMatchServizioApplicativo(this.soggettoFruitore, this.servizioApplicativoFruitore, check.getApplicabilita().getServizioApplicativoList());
							if(!applicativi && this.idServizioApplicativoToken!=null) {
								applicativi = GestoreTrasformazioniUtilities.isMatchServizioApplicativo(this.idServizioApplicativoToken.getIdSoggettoProprietario(), this.idServizioApplicativoToken.getNome(), check.getApplicabilita().getServizioApplicativoList());
							}
						}
						if(check.getApplicabilita().sizeSoggettoList()>0) {
							soggetti = GestoreTrasformazioniUtilities.isMatchSoggetto(this.soggettoFruitore, check.getApplicabilita().getSoggettoList());
							if(!soggetti && this.idServizioApplicativoToken!=null) {
								soggetti = GestoreTrasformazioniUtilities.isMatchSoggetto(this.idServizioApplicativoToken.getIdSoggettoProprietario(), check.getApplicabilita().getSoggettoList());
							}
						}
						if(!applicativi && !soggetti) {
							continue; // basta un match tra un soggetto o un applicativo per avere una applicabilita'
						}
					}
					else if(check.getApplicabilita().sizeServizioApplicativoList()>0) {
						boolean applicativi = false;
						if(check.getApplicabilita().sizeServizioApplicativoList()>0) {
							applicativi = GestoreTrasformazioniUtilities.isMatchServizioApplicativo(this.soggettoFruitore, this.servizioApplicativoFruitore, check.getApplicabilita().getServizioApplicativoList());
							if(!applicativi && this.idServizioApplicativoToken!=null) {
								applicativi = GestoreTrasformazioniUtilities.isMatchServizioApplicativo(this.idServizioApplicativoToken.getIdSoggettoProprietario(), this.idServizioApplicativoToken.getNome(), check.getApplicabilita().getServizioApplicativoList());
							}
						}
						if(!applicativi) {
							continue;
						}
					}
					else if(check.getApplicabilita().sizeSoggettoList()>0) {
						boolean soggetti = false;
						if(check.getApplicabilita().sizeSoggettoList()>0) {
							soggetti = GestoreTrasformazioniUtilities.isMatchSoggetto(this.soggettoFruitore, check.getApplicabilita().getSoggettoList());
							if(!soggetti && this.idServizioApplicativoToken!=null) {
								soggetti = GestoreTrasformazioniUtilities.isMatchSoggetto(this.idServizioApplicativoToken.getIdSoggettoProprietario(), check.getApplicabilita().getSoggettoList());
							}
						}
						if(!soggetti) {
							continue;
						}
					}
				
				}
				
				this.logDebug(suffix+" check applicabilità, regola applicabile alla richiesta in corso");
				this.regolaTrasformazione = check;
				break; // trovata!
				
			}

		} catch(Exception er) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, "N.D.");
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Identificazione regola di trasformazione non riuscita: "+er.getMessage();
			this.logError(msgErrore, er);
			throw new GestoreTrasformazioniException(msgErrore,er);
		}
			
		
		if(this.regolaTrasformazione==null) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, "N.D.");
			this.msgDiag.logPersonalizzato( messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaNessunMatch" : "trasformazione.processamentoRichiestaNessunMatch");
			this.logDebug("Nessuna regola di trasformazione trovata");
			return messageP;
		}
		
		this.messageRequest = messageP;
		
		
		
		
		// *** EmissioneDiagnostico ****
		
		TrasformazioneRegolaRichiesta richiesta = this.regolaTrasformazione.getRichiesta();
		String labelTrasformazione = GestoreTrasformazioniUtilities.getLabelTipoTrasformazioneRichiesta(richiesta, messageP);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RICHIESTA, labelTrasformazione);
		this.msgDiag.logPersonalizzato( messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaInCorso" : "trasformazione.processamentoRichiestaInCorso");
		if(this.pddContext!=null) {
			this.pddContext.addObject(CostantiPdD.TIPO_TRASFORMAZIONE_RICHIESTA, labelTrasformazione);
		}
		
		
		
		
		
		// *** Costruzione Dynamic Map ****
		
		this.logDebug("Costruzione dynamic map ...");
		Map<String, Object> dynamicMap = new HashMap<>();
		ErrorHandler errorHandler = new ErrorHandler(this.errorGenerator, IntegrationFunctionError.TRANSFORMATION_RULE_REQUEST_FAILED, this.pddContext);
		DynamicUtils.fillDynamicMapRequest(this.log, dynamicMap, this.pddContext, urlInvocazione,
				messageP,
				messageContent,
				busta, 
				parametriTrasporto, 
				parametriUrl,
				parametriForm,
				errorHandler);
		this.dynamicMapRequest = dynamicMap;
		if(messageTypeForNotifier!=null && 
				(
						(MessaggioDaNotificare.ENTRAMBI.equals(messageTypeForNotifier) && responseMessageForNotifier!=null)
						||
						(MessaggioDaNotificare.RISPOSTA.equals(messageTypeForNotifier))
				)
			) {
			Map<String, Object> dynamicMapWithResponse = new HashMap<>();
			boolean preserveRequest = true;
			if(MessaggioDaNotificare.RISPOSTA.equals(messageTypeForNotifier)) {
				Object o = dynamicMap.remove(Costanti.MAP_RESPONSE); // essendo di ruolo response, viene scritto cosi
				if(o!=null) {
					dynamicMap.put(Costanti.MAP_REQUEST, o);
				}
				DynamicUtils.fillDynamicMapResponse(this.log, dynamicMapWithResponse, dynamicMap, this.pddContext, 
						messageP,
						messageContent, busta, parametriTrasportoRisposta,
						errorHandler,
						preserveRequest);
			}
			else {
				DynamicUtils.fillDynamicMapResponse(this.log, dynamicMapWithResponse, dynamicMap, this.pddContext, 
						responseMessageForNotifier,
						messageResponseContent, busta, parametriTrasportoRisposta,
						errorHandler,
						preserveRequest);
			}
			dynamicMap = dynamicMapWithResponse;
		}
		this.logDebug("Costruzione dynamic map completata");
		
		
		
		
		// *** Trasformazione Richiesta ****
		
		boolean trasformazioneContenuto = richiesta.getConversione();
		RisultatoTrasformazioneContenuto risultato = null;
		String forceContentTypeRichiesta = null;
		try {
			// contentTypeRichiesta
			if(richiesta.getContentType()!=null && StringUtils.isNotEmpty(richiesta.getContentType())) {
				forceContentTypeRichiesta = richiesta.getContentType();
				forceContentTypeRichiesta = DynamicUtils.convertDynamicPropertyValue("forceContentTypeRichiesta", forceContentTypeRichiesta, dynamicMap, this.pddContext);
			}
			
			if(!trasformazioneContenuto) {
				this.logDebug("Trasformazione contenuto della richiesta disabilitato");
			}
			else {
				Template template = (this.idPA!=null) ? 
						this.configurazionePdDManager.getTemplateTrasformazioneRichiesta(this.idPA, this.regolaTrasformazione.getNome(), richiesta, this.requestInfo) 
						:
						this.configurazionePdDManager.getTemplateTrasformazioneRichiesta(this.idPD, this.regolaTrasformazione.getNome(), richiesta, this.requestInfo);
				risultato = 
						GestoreTrasformazioniUtilities.trasformazioneContenuto(this.log, 
								richiesta.getConversioneTipo(), template, "richiesta", 
								dynamicMap, messageP, messageContent, this.pddContext, forceContentTypeRichiesta,
								this.op2Properties.isTrasformazioni_readCharsetFromContentType());
				if (risultato != null && risultato.getTipoTrasformazione() != null && risultato.getTipoTrasformazione().isContextInjection()) {
					trasformazioneContenuto = false;
					this.logDebug("Trasformazione contenuto della richiesta disabilitato (Context Injection)");
				}
			}
			
		} catch(Exception er) {
			// fix: la condizione serve nel caso vengano utilizate istruzioni come <#stop che lanciano una freemarker.core.StopException
			//      ma comunque prima di lanciarla e' stato impostato correttamente l'error handler
			if(!errorHandler.isError()) {
				this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
				String msgErrore = "Trasformazione richiesta fallita: "+er.getMessage();
				this.logError(msgErrore, er);
				throw new GestoreTrasformazioniException(msgErrore,er);
			}
		}
		
		if(errorHandler.isError()) {
			String msgErrore = "Trasformazione richiesta terminata con errore: "+errorHandler.getDetail();
			this.logError(msgErrore);
			if(errorHandler.getMessage()!=null) {
				throw new GestoreTrasformazioniException(msgErrore,errorHandler.getMessage());
			}
			else {
				throw new GestoreTrasformazioniException(msgErrore,errorHandler.getOp2Message(), errorHandler.getOp2IntegrationFunctionError());
			}
		}
		
		try {				
			// conversione header
			Map<String, List<String>> trasporto = parametriTrasporto;
			Map<String, List<String>> forceAddTrasporto = new HashMap<>();
			GestoreTrasformazioniUtilities.trasformazione(this.log, richiesta.getHeaderList(), trasporto, forceAddTrasporto, GestoreTrasformazioniUtilities.TRASFORMAZIONE_HEADER_HTTP_RICHIESTA, dynamicMap, this.pddContext);
			if(forceContentTypeRichiesta!=null && StringUtils.isNotEmpty(forceContentTypeRichiesta)) {
				TransportUtils.removeRawObject(trasporto, HttpConstants.CONTENT_TYPE);
				TransportUtils.setHeader(trasporto,HttpConstants.CONTENT_TYPE, forceContentTypeRichiesta);
			}
			
			// conversione url
			Map<String, List<String>> url = parametriUrl;
			Map<String, List<String>> forceAddUrl = new HashMap<>();
			GestoreTrasformazioniUtilities.trasformazione(this.log, richiesta.getParametroUrlList(), url, forceAddUrl, GestoreTrasformazioniUtilities.TRASFORMAZIONE_QUERY_PARAMETER, dynamicMap, this.pddContext);
						
			// trasformazione contenuto non richiesta
			if(!trasformazioneContenuto) {
				GestoreTrasformazioniUtilities.addTransportInfo(forceAddTrasporto, forceAddUrl, null, messageP);
				
				if(ServiceBinding.REST.equals(messageP.getServiceBinding()) &&
					richiesta.getTrasformazioneRest()!=null &&
					(StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getMetodo()) || StringUtils.isNotEmpty(richiesta.getTrasformazioneRest().getPath())) 
					){
					GestoreTrasformazioniUtilities.injectNewRestParameter(messageP, 
							richiesta.getTrasformazioneRest().getMetodo(), 
							richiesta.getTrasformazioneRest().getPath(), 
							dynamicMap, this.pddContext);
				}

				this.msgDiag.logPersonalizzato(messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaEffettuato" : "trasformazione.processamentoRichiestaEffettuato");
				
				return messageP;
			}
			
			// trasformazione contenuto
			boolean trasformazioneRest = false;
			String trasformazioneRestMethod = null;
			String trasformazioneRestPath = null;
			if(richiesta.getTrasformazioneRest()!=null) {
				trasformazioneRest = true;
				trasformazioneRestMethod = richiesta.getTrasformazioneRest().getMetodo();
				trasformazioneRestPath = richiesta.getTrasformazioneRest().getPath();
			}
			
			boolean trasformazioneSoap = false;
			VersioneSOAP trasformazioneSoapVersione = null;
			String trasformazioneSoapSOAPAction = null;
			boolean trasformazioneSoapEnvelope = false;
			boolean trasformazioneSoapEnvelopeAsAttachment = false;
			String trasformazioneSoapTipoConversione = null;
			Template trasformazioneSoapTemplateConversione = null;
			if(richiesta.getTrasformazioneSoap()!=null) {
				trasformazioneSoap = true;
				trasformazioneSoapVersione = richiesta.getTrasformazioneSoap().getVersione();
				trasformazioneSoapSOAPAction = richiesta.getTrasformazioneSoap().getSoapAction();
				trasformazioneSoapEnvelope = richiesta.getTrasformazioneSoap().isEnvelope();
				trasformazioneSoapEnvelopeAsAttachment = richiesta.getTrasformazioneSoap().isEnvelopeAsAttachment();
				trasformazioneSoapTipoConversione = richiesta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo();
				
				trasformazioneSoapTemplateConversione = (this.idPA!=null) ? 
						this.configurazionePdDManager.getTemplateTrasformazioneSoapRichiesta(this.idPA, this.regolaTrasformazione.getNome(), richiesta, this.requestInfo) 
						:
						this.configurazionePdDManager.getTemplateTrasformazioneSoapRichiesta(this.idPD, this.regolaTrasformazione.getNome(), richiesta, this.requestInfo);
			}
									
			OpenSPCoop2Message msg = GestoreTrasformazioniUtilities.trasformaMessaggio(this.log, messageP, messageContent, 
					this.requestInfo, dynamicMap, this.pddContext, this.op2Properties, 
					trasporto, forceAddTrasporto,
					url, forceAddUrl,
					-1, 
					forceContentTypeRichiesta, null, 
					risultato, 
					trasformazioneRest, 
					trasformazioneRestMethod, trasformazioneRestPath,
					trasformazioneSoap, 
					trasformazioneSoapVersione, trasformazioneSoapSOAPAction, 
					trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeAsAttachment, 
					trasformazioneSoapTipoConversione, trasformazioneSoapTemplateConversione);

			this.trasformazioneContenutoRichiestaEffettuata = true;
			
			this.msgDiag.logPersonalizzato(messageTypeForNotifier!=null ? "trasformazione.processamentoNotificaEffettuato" : "trasformazione.processamentoRichiestaEffettuato");
			
			if(msg!=null) {
				if(nomePortaInvocata!=null) {
					Object nomePortaInvocataObject = msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
					if(nomePortaInvocataObject == null) {
						msg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
					}
				}
				if(interfaceName!=null && msg.getTransportRequestContext()!=null && msg.getTransportRequestContext().getInterfaceName()==null) {
					msg.getTransportRequestContext().setInterfaceName(interfaceName);
				}
			}
			
			return msg;
						
		} catch(Throwable er) {
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Trasformazione richiesta fallita: "+er.getMessage();
			this.logError(msgErrore, er);
			throw new GestoreTrasformazioniException(msgErrore,er);
		}
		
		

		
	}
	
	
	private OpenSPCoop2Message trasformazioneRispostaEngine(OpenSPCoop2Message message, Busta busta) throws GestoreTrasformazioniException{

		if(this.regolaTrasformazione==null || this.regolaTrasformazione.sizeRispostaList()<=0) {
			this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaDisabilitato");
			this.logDebug("Non esistono regole di trasformazione della risposta");
			return message;
		}
	
		
		// *** Lettura Contenuto Risposta ****
		
		MessageContent messageContent = null;
		boolean contenutoNonNavigabile = false;
		Map<String, List<String>> parametriTrasporto = null;
		int httpStatus = -1;
		try{
			boolean bufferMessageReadOnly =  OpenSPCoop2Properties.getInstance().isReadByPathBufferEnabled();
			if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
				OpenSPCoop2SoapMessage soapMessage = message.castAsSoap();
				messageContent = new MessageContent(soapMessage, bufferMessageReadOnly, this.pddContext);
			}
			else{
				if(MessageType.XML.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestXmlMessage xml = message.castAsRestXml();
					messageContent = new MessageContent(xml, bufferMessageReadOnly, this.pddContext);
				}
				else if(MessageType.JSON.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestJsonMessage json = message.castAsRestJson();
					messageContent = new MessageContent(json, bufferMessageReadOnly, this.pddContext);
				}
				else if(MessageType.MIME_MULTIPART.equals(message.getMessageType()) && message.castAsRest().hasContent()){
					OpenSPCoop2RestMimeMultipartMessage mime = message.castAsRestMimeMultipart();
					messageContent = new MessageContent(mime, bufferMessageReadOnly, this.pddContext);
				}
				else {
					contenutoNonNavigabile = true;
				}
			}
			
			if(message.getTransportResponseContext()!=null) {
				if(message.getTransportResponseContext().getHeaders()!=null &&
					!message.getTransportResponseContext().getHeaders().isEmpty()) {
					parametriTrasporto = message.getTransportResponseContext().getHeaders();
				}
				else {
					parametriTrasporto = new HashMap<>();
					message.getTransportResponseContext().setHeaders(parametriTrasporto);
				}
				try {
					httpStatus = Integer.parseInt(message.getTransportResponseContext().getCodiceTrasporto());
				}catch(Exception e) {
					// ignore
				}
			}
			else {
				throw new GestoreTrasformazioniException(TRANSPORT_RESPONSE_CONTEXT_UNAVAILABLE);
			}
			
		}catch(Throwable e){
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RISPOSTA, "N.D.");
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(e, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Lettura contenuto della risposta non riuscita: "+e.getMessage();
			this.logError(msgErrore, e);
			throw new GestoreTrasformazioniException(msgErrore,e);
		}
				
		
		
		// *** Identificazione regola ****
		
		TrasformazioneRegolaRisposta trasformazioneRisposta = null;
		try {
			
			this.logDebug("Identificazione regola di trasformazione della risposta tra le "+this.regolaTrasformazione.sizeRispostaList()+" disponibili ...");
			
			for (int i = 0; i < this.regolaTrasformazione.sizeRispostaList(); i++) {
				
				String suffix = "[Regola-"+i+"] ";
				
				this.logDebug(suffix+"Verifica applicabilità della regola ...");
				
				TrasformazioneRegolaRisposta check = this.regolaTrasformazione.getRisposta(i);
				
				// prendo la prima che ha un match nell'ordine
				
				if(check.getApplicabilita()!=null) {
				
					// controllo status code
					if(check.getApplicabilita().getReturnCodeMax()!=null || check.getApplicabilita().getReturnCodeMin()!=null) {
						String min = "*";
						if(check.getApplicabilita().getReturnCodeMin()!=null) {
							min = check.getApplicabilita().getReturnCodeMin().intValue()+"";
						} 
						String max = "*";
						if(check.getApplicabilita().getReturnCodeMax()!=null) {
							max = check.getApplicabilita().getReturnCodeMax().intValue()+"";
						} 
						this.logDebug(suffix+" check applicabilità return code ["+min+"-"+max+"]");
						if(check.getApplicabilita().getReturnCodeMin()!=null &&
							httpStatus< check.getApplicabilita().getReturnCodeMin().intValue()) {
							continue;
						}
						if(check.getApplicabilita().getReturnCodeMax()!=null &&
							httpStatus> check.getApplicabilita().getReturnCodeMax().intValue()) {
							continue;
						}
					}
					
					// controllo contentType
					if(!ContentTypeUtilities.isMatch(message.getContentType(), check.getApplicabilita().getContentTypeList())) {
						continue;
					}
		
					// Controllo Pattern
					if(check.getApplicabilita().getPattern()!=null && !"".equals(check.getApplicabilita().getPattern())) {
						
						this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+" ...");
						
						if(contenutoNonNavigabile) {
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", messaggio non conforme al match: "+message.getMessageType());
							continue; // no match
						}
						
						String elementJson = null;
						Element element = null;
						if(messageContent.isJson()) {
							elementJson = messageContent.getElementJson();
						}
						else {
							element = messageContent.getElement();
						}
						if(element==null && elementJson==null){
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", messaggio ("+message.getMessageType()+") senza contenuto");
							continue; // no match
						}
						String valore = null;
						try {
							if(element!=null) {
								AbstractXPathExpressionEngine xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
								valore = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, check.getApplicabilita().getPattern(), this.log);
							}
							else {
								valore = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, check.getApplicabilita().getPattern(), this.log);
							}
						}catch(Exception e){
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+" fallita: "+e.getMessage(),e);
						}
						if(valore==null) {
							this.logDebug(suffix+getSuffixMessageApplicabilitaContentBasedPattern(check.getApplicabilita().getPattern())+", match fallito ("+message.getMessageType()+")");
							continue;
						}
					}
					
				}
				
				this.logDebug(suffix+" check applicabilità, regola applicabile alla risposta in corso");
				trasformazioneRisposta = check;
				break; // trovata!
				
			}
			
		} catch(Throwable er) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RISPOSTA, "N.D.");
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Identificazione regola di trasformazione della risposta non riuscita: "+er.getMessage();
			this.logError(msgErrore, er);
			throw new GestoreTrasformazioniException(msgErrore,er);
		}
			
		
		if(trasformazioneRisposta==null) {
			this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RISPOSTA, "N.D.");
			this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaNessunMatch");
			this.logDebug("Nessuna regola di trasformazione della risposta trovata");
			return message;
		}
	
		
		
		
		
		// *** EmissioneDiagnostico ****
		
		String labelTrasformazione = GestoreTrasformazioniUtilities.getLabelTipoTrasformazioneRisposta(this.regolaTrasformazione.getRichiesta(), trasformazioneRisposta);
		this.msgDiag.addKeyword(CostantiPdD.KEY_TIPO_TRASFORMAZIONE_RISPOSTA, labelTrasformazione);
		this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaInCorso");
		if(this.pddContext!=null) {
			this.pddContext.addObject(CostantiPdD.TIPO_TRASFORMAZIONE_RISPOSTA, labelTrasformazione);
		}
		
		
		
		
		// *** Costruzione Dynamic Map ****
		
		this.logDebug("Costruzione dynamic map ...");
		Map<String, Object> dynamicMap = new HashMap<>();
		ErrorHandler errorHandler = new ErrorHandler(this.errorGenerator, IntegrationFunctionError.TRANSFORMATION_RULE_RESPONSE_FAILED, this.pddContext);
		DynamicUtils.fillDynamicMapResponse(this.log, dynamicMap, this.dynamicMapRequest, this.pddContext, 
				message,
				messageContent, busta, parametriTrasporto,
				errorHandler);
		this.logDebug("Costruzione dynamic map completata");
		
		
		
		
		// *** Trasformazione Risposta ****
		
		boolean trasformazioneContenuto = trasformazioneRisposta.getConversione();
		RisultatoTrasformazioneContenuto risultato = null;
		String forceContentTypeRisposta = null;
		try {
			// contentTypeRisposta
			if(trasformazioneRisposta.getContentType()!=null && StringUtils.isNotEmpty(trasformazioneRisposta.getContentType())) {
				forceContentTypeRisposta = trasformazioneRisposta.getContentType();
				forceContentTypeRisposta = DynamicUtils.convertDynamicPropertyValue("forceContentTypeRisposta", forceContentTypeRisposta, dynamicMap, this.pddContext);
			}
			
			if(!trasformazioneContenuto) {
				this.logDebug("Trasformazione contenuto della richiesta disabilitato");
			}
			else {
				Template template = (this.idPA!=null) ? 
						this.configurazionePdDManager.getTemplateTrasformazioneRisposta(this.idPA, this.regolaTrasformazione.getNome(), trasformazioneRisposta, this.requestInfo) 
						:
						this.configurazionePdDManager.getTemplateTrasformazioneRisposta(this.idPD, this.regolaTrasformazione.getNome(), trasformazioneRisposta, this.requestInfo);
				risultato = 
						GestoreTrasformazioniUtilities.trasformazioneContenuto(this.log, 
								trasformazioneRisposta.getConversioneTipo(), template, "risposta", 
								dynamicMap, message, messageContent, this.pddContext, forceContentTypeRisposta,
								this.op2Properties.isTrasformazioni_readCharsetFromContentType());
				if (risultato != null && risultato.getTipoTrasformazione() != null && risultato.getTipoTrasformazione().isContextInjection()) {
					trasformazioneContenuto = false;
					this.logDebug("Trasformazione contenuto della risposta disabilitato (Context Injection)");
				}
			}
			
		} catch(Throwable er) {
			// fix: la condizione serve nel caso vengano utilizate istruzioni come <#stop che lanciano una freemarker.core.StopException
			//      ma comunque prima di lanciarla e' stato impostato correttamente l'error handler
			if(!errorHandler.isError()) {
				this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
						get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
				String msgErrore = "Trasformazione risposta fallita: "+er.getMessage();
				this.logError(msgErrore, er);
				throw new GestoreTrasformazioniException(msgErrore,er);
			}
		}
		
		if(errorHandler.isError()) {
			String msgErrore = "Trasformazione risposta terminata con errore: "+errorHandler.getDetail();
			this.logError(msgErrore);
			if(errorHandler.getMessage()!=null) {
				throw new GestoreTrasformazioniException(msgErrore,errorHandler.getMessage());
			}
			else {
				throw new GestoreTrasformazioniException(msgErrore,errorHandler.getOp2Message(), errorHandler.getOp2IntegrationFunctionError());
			}
		}
		
		try {
						
			// conversione header
			Map<String, List<String>> trasporto = parametriTrasporto!=null ? parametriTrasporto : new HashMap<>();
			Map<String, List<String>> forceAddTrasporto = new HashMap<>();
			GestoreTrasformazioniUtilities.trasformazione(this.log, trasformazioneRisposta.getHeaderList(), trasporto, forceAddTrasporto, GestoreTrasformazioniUtilities.TRASFORMAZIONE_HEADER_HTTP_RISPOSTA, dynamicMap, this.pddContext);
			if(forceContentTypeRisposta!=null && StringUtils.isNotEmpty(forceContentTypeRisposta)) {
				TransportUtils.removeRawObject(trasporto, HttpConstants.CONTENT_TYPE);
				TransportUtils.setHeader(trasporto,HttpConstants.CONTENT_TYPE, forceContentTypeRisposta);
			}
			
			// returnCode
			String forceResponseStatus = null;
			if(trasformazioneRisposta.getReturnCode()!=null && StringUtils.isNotEmpty(trasformazioneRisposta.getReturnCode())) {
				forceResponseStatus = trasformazioneRisposta.getReturnCode();
				forceResponseStatus = DynamicUtils.convertDynamicPropertyValue("forceResponseStatus", forceResponseStatus, dynamicMap, this.pddContext);
				if(forceResponseStatus!=null && StringUtils.isNotEmpty(forceResponseStatus)) {
					GestoreTrasformazioniUtilities.checkReturnCode(forceResponseStatus);
				}
				else {
					forceResponseStatus = httpStatus+"";
				}
			}
			else {
				forceResponseStatus = httpStatus+"";
			}
						
			// trasformazione contenuto non richiesta
			if(!trasformazioneContenuto) {
				GestoreTrasformazioniUtilities.addTransportInfo(forceAddTrasporto, null, forceResponseStatus, message);
				
				this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaEffettuato");
				
				return message;
			}
			
			// trasformazione contenuto
			boolean trasformazioneRest = false;
			if(this.regolaTrasformazione.getRichiesta().getTrasformazioneSoap()!=null) {
				trasformazioneRest = true; // devo tornare rest
			}
			
			boolean trasformazioneSoap = false;
			VersioneSOAP trasformazioneSoapVersione = null;
			String trasformazioneSoapSOAPAction = null;
			boolean trasformazioneSoapEnvelope = false;
			boolean trasformazioneSoapEnvelopeAsAttachment = false;
			String trasformazioneSoapTipoConversione = null;
			Template trasformazioneSoapTemplateConversione = null;
			if(this.regolaTrasformazione.getRichiesta().getTrasformazioneRest()!=null) {
				// devo tornare soap
				trasformazioneSoap = true;
				if(this.messageRequest!=null) {
					if(MessageType.SOAP_11.equals(this.messageRequest.getMessageType())) {
						trasformazioneSoapVersione = VersioneSOAP._1_1;
					}
					else if(MessageType.SOAP_12.equals(this.messageRequest.getMessageType())) {
						trasformazioneSoapVersione = VersioneSOAP._1_2;
					}
					else {
						throw new GestoreTrasformazioniException("Atteso messaggio di richiesta di tipo SOAP, in presenza di una trasformazione REST attiva");
					}
					trasformazioneSoapSOAPAction = this.messageRequest.castAsSoap().getSoapAction();
				}
				else {
					// non dovrebbe mai succedere
					trasformazioneSoapVersione = VersioneSOAP._1_1;
				}
				if(trasformazioneRisposta.getTrasformazioneSoap()!=null) {
					trasformazioneSoapEnvelope = trasformazioneRisposta.getTrasformazioneSoap().isEnvelope();
					trasformazioneSoapEnvelopeAsAttachment = trasformazioneRisposta.getTrasformazioneSoap().isEnvelopeAsAttachment();
					trasformazioneSoapTipoConversione = trasformazioneRisposta.getTrasformazioneSoap().getEnvelopeBodyConversioneTipo();
					
					trasformazioneSoapTemplateConversione = (this.idPA!=null) ? 
							this.configurazionePdDManager.getTemplateTrasformazioneSoapRisposta(this.idPA, this.regolaTrasformazione.getNome(), trasformazioneRisposta, this.requestInfo) 
							:
							this.configurazionePdDManager.getTemplateTrasformazioneSoapRisposta(this.idPD, this.regolaTrasformazione.getNome(), trasformazioneRisposta, this.requestInfo);
					
				}
			}
			
			OpenSPCoop2Message msg = GestoreTrasformazioniUtilities.trasformaMessaggio(this.log, message, messageContent, 
					this.requestInfo, dynamicMap, this.pddContext, this.op2Properties, 
					trasporto, forceAddTrasporto, 
					null, null,
					httpStatus, 
					forceContentTypeRisposta, forceResponseStatus, 
					risultato, 
					trasformazioneRest, 
					null, null,
					trasformazioneSoap, 
					trasformazioneSoapVersione, trasformazioneSoapSOAPAction, 
					trasformazioneSoapEnvelope, trasformazioneSoapEnvelopeAsAttachment, 
					trasformazioneSoapTipoConversione, trasformazioneSoapTemplateConversione);

			this.msgDiag.logPersonalizzato("trasformazione.processamentoRispostaEffettuato");
			
			return msg;
						
		} catch(Throwable er) {
			this.errore = ErroriIntegrazione.ERRORE_5XX_GENERICO_PROCESSAMENTO_MESSAGGIO.
					get5XX_ErroreProcessamento(er, CodiceErroreIntegrazione.CODICE_562_TRASFORMAZIONE);
			String msgErrore = "Trasformazione risposta fallita: "+er.getMessage();
			this.logError(msgErrore, er);
			throw new GestoreTrasformazioniException(msgErrore,er);
		}
	}
	
}

