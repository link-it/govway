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
package org.openspcoop2.protocol.engine.mapping;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.message.MessageUtils;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reader.OpenSPCoop2MessageSoapStreamReader;
import org.openspcoop2.protocol.engine.constants.ModalitaIdentificazioneAzione;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.state.RequestInfo;
import org.openspcoop2.protocol.utils.PorteNamingUtils;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.rest.api.ApiOperation;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml2json.JsonXmlPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Element;

/**
 * OperationFinder
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class OperationFinder {
	
	private OperationFinder() {}

	private static String getPrefixIdentificazione(ModalitaIdentificazioneAzione modalitaIdentificazione) {
		return "Identificazione '"+modalitaIdentificazione.getValue()+"' ";
	}
	
	public static String getAzione(RegistroServiziManager registroServiziManager,TransportRequestContext transportContext, RequestInfo requestInfo,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader, 
			IDSoggetto soggettoErogatore, IDServizio idServizio,
			boolean readFirstHeaderIntegrazione, String azioneHeaderIntegrazione, 
			IProtocolFactory<?> protocolFactory,
			ModalitaIdentificazioneAzione modalitaIdentificazione, String pattern, 
			boolean forceRegistryBased, boolean forcePluginBased,
			Logger log, boolean portaApplicativa,
			boolean bufferMessageReadOnly , String idTransazione,
			boolean rpcAcceptRootElementUnqualified) throws DriverConfigurazioneException, IdentificazioneDinamicaException { 
		
		try{

			if(soggettoErogatore!=null) {
				 // nop
			}
			
			// Recupero Lista di Azioni
			/**
			List<String> listaAzioni = new ArrayList<>();
			boolean rest = false;
			try {
				AccordoServizioParteSpecifica asps = registroServiziManager.getAccordoServizioParteSpecifica(idServizio, null, false);
				AccordoServizioParteComune aspc = registroServiziManager.getAccordoServizioParteComune(IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune()), null, false);
				if(org.openspcoop2.core.registry.constants.ServiceBinding.REST.equals(aspc.getServiceBinding())) {
					rest = true;
					for (Resource r : aspc.getResourceList()) {
						listaAzioni.add(r.getNome());
					}
				}
				else {
					if(asps.getPortType()==null) {
						for (Azione a : aspc.getAzioneList()) {
							listaAzioni.add(a.getNome());
						}
					}
					else {
						for (PortType pt: aspc.getPortTypeList()) {
							if(pt.getNome().equals(asps.getPortType())) {
								for (Operation a : pt.getAzioneList()) {
									listaAzioni.add(a.getNome());
								}
								break;
							}
						}
					}
				}
			}catch(Throwable t) {
				// ignoro errori, li sollevo in altri posti
			}*/
			
			boolean registryBased = false;
			boolean pluginBased = false;
			
			String azione = null;
			Exception eAzione = null;
			try{
				if(ModalitaIdentificazioneAzione.STATIC.equals(modalitaIdentificazione)){
					// STATIC-BASED
					azione = idServizio.getAzione();
				}
				else{
					if(readFirstHeaderIntegrazione && azioneHeaderIntegrazione!=null){
						// INTEGRATIONMANAGER INPUT
						azione = azioneHeaderIntegrazione;
					}
					else{
						if(ModalitaIdentificazioneAzione.URL_BASED.equals(modalitaIdentificazione)){
							// URL-BASED
							String urlInvocazionePD = transportContext.getUrlInvocazione_formBased();
							azione = RegularExpressionEngine.getStringFindPattern(urlInvocazionePD, pattern); // Sostituito Match con Find per applicare l'espressione regolare che risolve il bug OP-1590
							if(ServiceBinding.REST.equals(message.getServiceBinding())){
								throw new DriverConfigurazioneNotFound(getPrefixIdentificazione(modalitaIdentificazione)+
										"non supportata per il service binding '"+ServiceBinding.REST+"'");
							}
						}						
						else if(ModalitaIdentificazioneAzione.HEADER_BASED.equals(modalitaIdentificazione)){
							// HEADER-BASED
							azione = transportContext.getHeaderFirstValue(pattern);
						}
						else if(ModalitaIdentificazioneAzione.CONTENT_BASED.equals(modalitaIdentificazione)){
							// CONTENT-BASED
							if(message==null){
								throw new DriverConfigurazioneNotFound("Messaggio non fornito");
							}
							AbstractXPathExpressionEngine xPathEngine = null;
							Element element = null;
							String elementJson = null;
							boolean checkSoapBodyEmpty = false; // devo poter fare xpath anche su soapBody empty
							if(message!=null) {
								element = MessageUtils.getContentElement(message, checkSoapBodyEmpty, bufferMessageReadOnly, idTransazione);
								elementJson = MessageUtils.getContentString(message, bufferMessageReadOnly, idTransazione);
							}
							if(element!=null) {
								xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine(message.getFactory());
								azione = AbstractXPathExpressionEngine.extractAndConvertResultAsString(element, xPathEngine, pattern,  log);
							}
							else if(elementJson!=null){
								azione = JsonXmlPathExpressionEngine.extractAndConvertResultAsString(elementJson, pattern, log);
							}
							else{
								throw new DriverConfigurazioneNotFound(getPrefixIdentificazione(modalitaIdentificazione)+"non supportata per il message-type '"+message.getMessageType()+"'");
							}
						}
						else if(ModalitaIdentificazioneAzione.INPUT_BASED.equals(modalitaIdentificazione)){
							// INPUT-BASED
							if(azioneHeaderIntegrazione!=null){
								azione = azioneHeaderIntegrazione;
							}
							else{
								throw new DriverConfigurazioneNotFound("Azione non indicata negli header di integrazione");
							}
						}
						else if(ModalitaIdentificazioneAzione.SOAP_ACTION_BASED.equals(modalitaIdentificazione)){
							// SOAP-ACTION-BASED
							if(message!=null && ServiceBinding.REST.equals(message.getServiceBinding())){
								throw new DriverConfigurazioneNotFound(getPrefixIdentificazione(modalitaIdentificazione)+
										"non supportata per il service binding '"+ServiceBinding.REST+"'");
							}
							if(message!=null){
								azione = message.castAsSoap().getSoapAction();
							}
							else{
								// provo una soluzione veloce di vedere se è presente nell'header di trasporto o nel content-type
								// non so che tipo di message type possiedo
								azione = getSoapAction(transportContext);
							}
							String azioneNonRipulita = azione;
							if(azione!=null){
								azione = azione.trim();
								// Nota: la soap action potrebbe essere quotata con "" 
								if(azione.startsWith("\"")){
									azione = azione.substring(1);
								}
								if(azione.endsWith("\"")){
									azione = azione.substring(0,(azione.length()-1));
								}	
							}
							
							// Provo a fare la traduzione tramite soapAction registrata nell'accordo
							if(azioneNonRipulita!=null || azione!=null) {
								org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper wrapper = registroServiziManager.getWsdlAccordoServizio(idServizio,InformationApiSource.SAFE_SPECIFIC_REGISTRY,false,false, requestInfo);
								if(wrapper!=null){
									for (int i = 0; i < wrapper.sizePortTypeList(); i++) {
										PortType pt = wrapper.getPortType(i);
										if(wrapper.getNomePortType()!=null &&
											!pt.getNome().equals(wrapper.getNomePortType())){
											continue;
										}
										for (int j = 0; j < pt.sizeAzioneList(); j++) {
											Operation op = pt.getAzione(j);
											if(
												(azioneNonRipulita!=null && azioneNonRipulita.equals(op.getSoapAction())) 
												||
												(azione!=null && azione.equals(op.getSoapAction()))
												) {
												/**System.out.println("RESOLVE 1 ["+azioneNonRipulita+"]");*/
												/**System.out.println("RESOLVE 2 ["+azione+"]");*/
												azione = op.getNome();
												break;
											}
										}
									}
								}
							}
							
							if(azione!=null &&
								"".equals(azione)){
								azione = null;
								throw new DriverConfigurazioneNotFound("SoapAction vuota ("+message.castAsSoap().getSoapAction()+") non è utilizzabile con una identificazione '"+
										ModalitaIdentificazioneAzione.SOAP_ACTION_BASED.getValue()+"'");
							}
							
						}
						else if(ModalitaIdentificazioneAzione.INTERFACE_BASED.equals(modalitaIdentificazione)){
							// INTERFACE-BASED
							registryBased = true;
							OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, modalitaIdentificazione);
							org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = registroServiziManager.getServiceBinding(idServizio, requestInfo);
							if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(serviceBinding)){
								azione = OperationFinder.searchOperationByWsdlInRequestMessage(message, soapStreamReader, requestInfo, registroServiziManager, idServizio, log,
										rpcAcceptRootElementUnqualified);
							}
							else {
								azione = OperationFinder.searchOperationByRestInRequestMessage(transportContext, requestInfo, registroServiziManager, idServizio, log, 
										protocolFactory, portaApplicativa);
							}
						}
						else if(ModalitaIdentificazioneAzione.PROTOCOL_BASED.equals(modalitaIdentificazione)){
							// PROTOCOL-BASED
							pluginBased = true;
							if(idServizio.getAzione()!=null){
								// gia localizzata in precedenza
								azione = idServizio.getAzione();
							}
							else{
								OpenSPCoop2Message msgAnalizyProtocol = message;
								if(message==null && soapStreamReader!=null) {
									msgAnalizyProtocol=soapStreamReader.getHeader_OpenSPCoop2Message();
								}
								if(msgAnalizyProtocol==null){
									throw new DriverConfigurazioneNotFound("Messaggio non fornito");
								}
								Busta busta = protocolFactory.createValidazioneSintattica(null).getBusta_senzaControlli(msgAnalizyProtocol);
								if(busta!=null){
									azione = busta.getAzione();
								}
							}
						}
					}
				}
			}catch(Exception e){
				eAzione = e;
			}
			
			// Se non ho riconosciuto una azione, provo con la modalita' wsdlBased se e' abilitata.
			if(azione==null && forceRegistryBased && !registryBased ){
				azione = getActionByInterface(log,
						transportContext, idServizio, modalitaIdentificazione,
						registroServiziManager, requestInfo, 
						message, soapStreamReader,
						protocolFactory,
						portaApplicativa,
						rpcAcceptRootElementUnqualified);
			}
			// Se non ho riconosciuto una azione, provo con la modalita' pluginBased se e' abilitata.
			if(azione==null && forcePluginBased && !pluginBased ){
				azione = getActionByPlugin(log,
						idServizio,
						message, soapStreamReader,
						protocolFactory);
			}
			// Se non ho riconosciuto una azione a questo punto, e durante il processo standard di riconoscimento era stato sollevata una eccezione
			// viene rilanciato
			if(azione==null && eAzione!=null)
				throw new IdentificazioneDinamicaException(eAzione.getMessage(),eAzione);

			
			return azione;

		}catch(IdentificazioneDinamicaException e){
			throw e;
		}
		catch(Exception e){
			throw new DriverConfigurazioneException(e.getMessage(),e);
		}

	}
	
	private static String getSoapAction(TransportRequestContext transportContext) {
		String azione = null;
		try{
			azione = SoapUtils.getSoapAction(transportContext, MessageType.SOAP_11, transportContext.getContentType());
		}catch(Exception e){
			// ignore
		}
		if(azione==null){
			try{
				azione = SoapUtils.getSoapAction(transportContext, MessageType.SOAP_12, transportContext.getContentType());
			}catch(Exception e){
				// ignore
			}	
		}
		return azione;
	}
	
	private static String getActionByInterface(Logger log,
			TransportRequestContext transportContext, IDServizio idServizio, ModalitaIdentificazioneAzione modalitaIdentificazione,
			RegistroServiziManager registroServiziManager, RequestInfo requestInfo, 
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader,
			IProtocolFactory<?> protocolFactory,
			boolean portaApplicativa,
			boolean rpcAcceptRootElementUnqualified) {
		String azione = null;
		try{
			OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, modalitaIdentificazione);
			org.openspcoop2.core.registry.constants.ServiceBinding serviceBinding = registroServiziManager.getServiceBinding(idServizio, requestInfo);
			if(org.openspcoop2.core.registry.constants.ServiceBinding.SOAP.equals(serviceBinding)){
				azione = OperationFinder.searchOperationByWsdlInRequestMessage(message, soapStreamReader, requestInfo, registroServiziManager, idServizio, log,
						rpcAcceptRootElementUnqualified);
			}
			else{
				azione = OperationFinder.searchOperationByRestInRequestMessage(transportContext, requestInfo, registroServiziManager, idServizio, log, 
						protocolFactory, portaApplicativa);
			}
		}catch(Exception eForceRegistry){
			log.debug("Riconoscimento forzato dell'azione non riuscito: "+eForceRegistry.getMessage(),eForceRegistry);
		}
		return azione;
	}
	
	public static String searchOperationByWsdlInRequestMessage(OpenSPCoop2Message msg, OpenSPCoop2MessageSoapStreamReader soapStreamReaderParam, RequestInfo requestInfo, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log,
			boolean rpcAcceptRootElementUnqualified) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperationByWsdl(true, msg, soapStreamReaderParam, requestInfo, registroServiziReader, idServizio, log,
				rpcAcceptRootElementUnqualified);
	}
	/**public static String searchOperationByWsdlInResponseMessage(OpenSPCoop2Message msg, RequestInfo requestInfo, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log,
		boolean rpcAcceptRootElementUnqualified) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperationByWsdl(false, msg, requestInfo, registroServiziReader, idServizio, log,
				rpcAcceptRootElementUnqualified);
	}*/
	private static String searchOperationByWsdl(boolean isRichiesta,OpenSPCoop2Message msg, OpenSPCoop2MessageSoapStreamReader soapStreamReaderParam, RequestInfo requestInfo, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log,
			boolean rpcAcceptRootElementUnqualified) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper wrapper = registroServiziReader.getWsdlAccordoServizio(idServizio,InformationApiSource.SAFE_SPECIFIC_REGISTRY,false,false,requestInfo);
		org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities wrapperUtilities = 
				new org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities(log,wrapper);
		return wrapperUtilities.searchOperationName(isRichiesta, wrapper.getNomePortType(), msg, soapStreamReaderParam, rpcAcceptRootElementUnqualified);
	}
	
	public static String searchOperationByRestInRequestMessage(TransportRequestContext transportContext, RequestInfo requestInfo, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log,
			IProtocolFactory<?> protocolFactory, boolean portaApplicativa) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		
		String normalizedInterfaceName = null;
		try {
			if(transportContext.getInterfaceName()!=null) {
				PorteNamingUtils namingUtils = new PorteNamingUtils(protocolFactory);
				if(portaApplicativa){
					normalizedInterfaceName = namingUtils.normalizePA(transportContext.getInterfaceName());
				}
				else {
					normalizedInterfaceName = namingUtils.normalizePD(transportContext.getInterfaceName());
				}
			}
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		String path = RestUtilities.getUrlWithoutInterface(transportContext, normalizedInterfaceName);
		HttpRequestMethod httpMethod = HttpRequestMethod.valueOf(transportContext.getRequestType());
		
		try {
			org.openspcoop2.core.registry.rest.AccordoServizioWrapper wrapper = registroServiziReader.getRestAccordoServizio(idServizio,InformationApiSource.SAFE_SPECIFIC_REGISTRY,false,false,false, requestInfo);
			ApiOperation op = wrapper.getApi().findOperation(httpMethod, path);
			if(op!=null) {
				// Il path nella 'ApiOperation è normalizzato come sul registro
				for (int i = 0; i < wrapper.getAccordoServizio().sizeResourceList(); i++) {
					Resource r = wrapper.getAccordoServizio().getResource(i);
					
					if(r.getMethod()==null) {
						if(op.getHttpMethod()!=null) {
							continue;
						}
					}
					else {
						if(op.getHttpMethod()==null) {
							continue;
						}
						if(!r.getMethod().name().equals(op.getHttpMethod().name())){
							continue;
						}
					}
					
					if(r.getPath()==null) {
						if(op.getPath()!=null) {
							continue;
						}
					}
					else {
						String rPath = r.getPath();
						String opPath = op.getPath();
						if(rPath!=null && rPath.length()>1 && rPath.endsWith("/")) {
							rPath = rPath.substring(0,rPath.length()-1);
						}
						if(opPath!=null && opPath.length()>1 && opPath.endsWith("/")) {
							opPath = opPath.substring(0,opPath.length()-1);
						}
						if(rPath!=null && !rPath.equals(opPath)){
							continue;
						}
					}
					
					return r.getNome();
					
				}
			}
		}catch(Exception e) {
			throw new DriverRegistroServiziException(e.getMessage(),e);
		}
		
		return null;
	}
	
	private static String getActionByPlugin(Logger log,
			IDServizio idServizio,
			OpenSPCoop2Message message, OpenSPCoop2MessageSoapStreamReader soapStreamReader,
			IProtocolFactory<?> protocolFactory) {
		String azione = null;
		try{
			if(idServizio.getAzione()!=null){
				// gia localizzata in precedenza
				azione = idServizio.getAzione();
			}
			else{
				OpenSPCoop2Message msgAnalizyProtocol = message;
				if(message==null && soapStreamReader!=null) {
					msgAnalizyProtocol=soapStreamReader.getHeader_OpenSPCoop2Message();
				}
				if(msgAnalizyProtocol!=null){
					Busta busta = protocolFactory.createValidazioneSintattica(null).getBusta_senzaControlli(msgAnalizyProtocol);
					if(busta!=null){
						azione = busta.getAzione();
					}
				}
			}
		}catch(Exception eForcePlugin){
			log.debug("Riconoscimento forzato dell'azione non riuscito: "+eForcePlugin.getMessage(),eForcePlugin);
		}
		return azione;
	}
	
	private static void checkIDServizioPerRiconoscimentoAzione(IDServizio idServizio, ModalitaIdentificazioneAzione modalitaIdentificazione) throws DriverConfigurazioneException{
		String suffixError = " (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)";
		if(idServizio==null){
			throw new DriverConfigurazioneException("Dati del servizio non trovati"+suffixError);
		}
		if(idServizio.getSoggettoErogatore()==null){
			throw new DriverConfigurazioneException("Dati del soggetto erogatore del servizio non trovati"+suffixError);
		}
		if(idServizio.getSoggettoErogatore().getTipo()==null){
			throw new DriverConfigurazioneException("Tipo soggetto erogatore del servizio non trovato"+suffixError);
		}
		if(idServizio.getSoggettoErogatore().getNome()==null){
			throw new DriverConfigurazioneException("Nome soggetto erogatore del servizio non trovato"+suffixError);
		}
		if(idServizio.getTipo()==null){
			throw new DriverConfigurazioneException("Tipo servizio non trovato"+suffixError);
		}
		if(idServizio.getNome()==null){
			throw new DriverConfigurazioneException("Nome servizio non trovato"+suffixError);
		}
		if(idServizio.getVersione()==null){
			throw new DriverConfigurazioneException("Versione servizio non trovata"+suffixError);
		}
	}
	
}
