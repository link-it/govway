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
package org.openspcoop2.protocol.engine.mapping;

import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziException;
import org.openspcoop2.core.registry.driver.DriverRegistroServiziNotFound;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper;
import org.openspcoop2.core.registry.wsdl.AccordoServizioWrapperUtilities;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.protocol.registry.InformationWsdlSource;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
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

	public static String getAzione(RegistroServiziManager registroServiziManager,TransportRequestContext transportContext,
			OpenSPCoop2Message message, 
			IDSoggetto soggettoErogatore, IDServizio idServizio,
			boolean readFirstHeaderIntegrazione, String azioneHeaderIntegrazione, 
			IProtocolFactory<?> protocolFactory,
			ModalitaIdentificazioneAzione modalitaIdentificazione, String pattern, 
			boolean forceRegistryBased, boolean forcePluginBased,
			Logger log) throws DriverConfigurazioneException,DriverConfigurazioneNotFound, IdentificazioneDinamicaException { 
		
		try{

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
							azione = RegularExpressionEngine.getStringMatchPattern(urlInvocazionePD, pattern);
						}						
						else if(ModalitaIdentificazioneAzione.HEADER_BASED.equals(modalitaIdentificazione)){
							// HEADER-BASED
							azione = transportContext.getParametersTrasporto().getProperty(pattern);
						}
						else if(ModalitaIdentificazioneAzione.CONTENT_BASED.equals(modalitaIdentificazione)){
							// CONTENT-BASED
							if(message==null){
								throw new DriverConfigurazioneNotFound("Messaggio non fornito");
							}
							DynamicNamespaceContext dnc = null;
							AbstractXPathExpressionEngine xPathEngine = null;
							Element element = null;
							if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
								element = message.castAsSoap().getSOAPPart().getEnvelope();
							}
							else{
								if(MessageType.XML.equals(message.getMessageType())){
									element = message.castAsRestXml().getContent();
								}
								else{
									throw new DriverConfigurazioneNotFound("Identificazione '"+modalitaIdentificazione.getValue()+"' non supportata per il message-type '"+message.getMessageType()+"'");
								}
							}
							dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
							xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
							azione = xPathEngine.getStringMatchPattern(element,dnc,pattern);
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
							if(message!=null){
								if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
									azione = message.castAsSoap().getSoapAction();
								}
								else{
									throw new DriverConfigurazioneNotFound("Identificazione '"+ModalitaIdentificazioneAzione.SOAP_ACTION_BASED.getValue()+
											"' non supportata per il service binding '"+ServiceBinding.REST+"'");
								}
							}
							else{
								// provo una soluzione veloce di vedere se è presente nell'header di trasporto o nel content-type
								// non so che tipo di message type possiedo
								try{
									azione = SoapUtils.getSoapAction(transportContext, MessageType.SOAP_11, transportContext.getContentType());
								}catch(Exception e){}
								if(azione==null){
									try{
										azione = SoapUtils.getSoapAction(transportContext, MessageType.SOAP_12, transportContext.getContentType());
									}catch(Exception e){}	
								}
							}
							if(azione!=null){
								azione = azione.trim();
								// Nota: la soap action potrebbe essere quotata con "" 
								if(azione.startsWith("\"")){
									azione = azione.substring(1);
								}
								if(azione.endsWith("\"")){
									azione = azione.substring(0,(azione.length()-1));
								}	
								if("".equals(azione)){
									azione = null;
									throw new DriverConfigurazioneNotFound("SoapAction vuota ("+message.castAsSoap().getSoapAction()+") non è utilizzabile con una identificazione '"+
											ModalitaIdentificazioneAzione.SOAP_ACTION_BASED.getValue()+"'");
								}
							}
						}
						else if(ModalitaIdentificazioneAzione.WSDL_BASED.equals(modalitaIdentificazione)){
							// WSDL-BASED
							registryBased = true;
							OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, modalitaIdentificazione);
							azione = OperationFinder.searchOperationByWsdlInRequestMessage(message, registroServiziManager, idServizio, log);
						}
						else if(ModalitaIdentificazioneAzione.PLUGIN_BASED.equals(modalitaIdentificazione)){
							// PLUGIN-BASED
							pluginBased = true;
							if(idServizio.getAzione()!=null){
								// gia localizzata in precedenza
								azione = idServizio.getAzione();
							}
							else{
								Busta busta = protocolFactory.createValidazioneSintattica().getBusta_senzaControlli(message);
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
				try{
					OperationFinder.checkIDServizioPerRiconoscimentoAzione(idServizio, modalitaIdentificazione);
					if(ServiceBinding.SOAP.equals(message.getServiceBinding())){
						azione = OperationFinder.searchOperationByWsdlInRequestMessage(message, registroServiziManager, idServizio, log);
					}
					else{
						// TODO per wadl/swagger
					}
				}catch(Exception eForceRegistry){
					log.debug("Riconoscimento forzato dell'azione non riuscito: "+eForceRegistry.getMessage(),eForceRegistry);
				}
			}
			// Se non ho riconosciuto una azione, provo con la modalita' pluginBased se e' abilitata.
			if(azione==null && forcePluginBased && !pluginBased ){
				try{
					if(idServizio.getAzione()!=null){
						// gia localizzata in precedenza
						azione = idServizio.getAzione();
					}
					else{
						Busta busta = protocolFactory.createValidazioneSintattica().getBusta_senzaControlli(message);
						if(busta!=null){
							azione = busta.getAzione();
						}
					}
				}catch(Exception eForcePlugin){
					log.debug("Riconoscimento forzato dell'azione non riuscito: "+eForcePlugin.getMessage(),eForcePlugin);
				}
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
	
	
	
	public static String searchOperationByWsdlInRequestMessage(OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperationByWsdl(true, msg, registroServiziReader, idServizio, log);
	}
	public static String searchOperationByWsdlInResponseMessage(OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		return searchOperationByWsdl(false, msg, registroServiziReader, idServizio, log);
	}
	private static String searchOperationByWsdl(boolean isRichiesta,OpenSPCoop2Message msg, RegistroServiziManager registroServiziReader,IDServizio idServizio,Logger log) throws DriverRegistroServiziException, DriverRegistroServiziNotFound{
		AccordoServizioWrapper wrapper = registroServiziReader.getWsdlAccordoServizio(idServizio,InformationWsdlSource.SAFE_WSDL_REGISTRY,false);
		AccordoServizioWrapperUtilities wrapperUtilities = new AccordoServizioWrapperUtilities(log,wrapper);
		return wrapperUtilities.searchOperationName(isRichiesta, wrapper.getNomePortType(), msg);
	}
	
	private static void checkIDServizioPerRiconoscimentoAzione(IDServizio idServizio, ModalitaIdentificazioneAzione modalitaIdentificazione) throws Exception{
		if(idServizio==null){
			throw new Exception("Dati del servizio non trovati (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore()==null){
			throw new Exception("Dati del soggetto erogatore del servizio non trovati (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore().getTipo()==null){
			throw new Exception("Tipo soggetto erogatore del servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore().getNome()==null){
			throw new Exception("Nome soggetto erogatore del servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
		if(idServizio.getTipoServizio()==null){
			throw new Exception("Tipo servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
		if(idServizio.getServizio()==null){
			throw new Exception("Nome servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione+" dell'azione)");
		}
	}
	
}
