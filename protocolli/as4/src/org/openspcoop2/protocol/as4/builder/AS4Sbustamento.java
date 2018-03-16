/*
 * OpenSPCoop - Customizable API Gateway 
 * http://www.openspcoop2.org
 * 
 * Copyright (c) 2005-2018 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.as4.builder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.xml.soap.AttachmentPart;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.registry.AccordoServizioParteComune;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.Resource;
import org.openspcoop2.core.registry.driver.IDAccordoFactory;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.Costanti;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.XMLUtils;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.as4.pmode.TranslatorPayloadProfilesDefault;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.mime.MimeMultipart;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;

import eu.domibus.configuration.Payload;
import eu.domibus.configuration.PayloadProfile;
import eu.domibus.configuration.PayloadProfiles;

/**
 * AS4Sbustamento
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class AS4Sbustamento {

	public ProtocolMessage buildMessage(IState state, OpenSPCoop2Message msg, Busta busta,
			RuoloMessaggio ruoloMessaggio, ProprietaManifestAttachments proprietaManifestAttachments,
			FaseSbustamento faseSbustamento, 
			ServiceBinding integrationServiceBinding, ServiceBindingConfiguration serviceBindingConfiguration,
			IRegistryReader registryReader, IProtocolFactory<?> protocolFactory) throws ProtocolException {
		try{
			
			Object o = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_USER_MESSAGE);
			if(o==null) {
				throw new ProtocolException("UserMessage not found in context");
			}
			if(!(o instanceof UserMessage)) {
				throw new ProtocolException("UserMessage in context message with wrong type (expected:"+UserMessage.class.getName()+" founded:"+o.getClass().getName()+")");
			}
			UserMessage userMessage = (UserMessage) o;
			
			Object oContent = msg.getContextProperty(AS4Costanti.AS4_CONTEXT_CONTENT);
			if(oContent==null) {
				throw new ProtocolException("Contenuti non trovati in context");
			}
			if(!(oContent instanceof HashMap)) {
				throw new ProtocolException("Contenuti in context message with wrong type (expected:"+HashMap.class.getName()+" founded:"+oContent.getClass().getName()+")");
			}
			@SuppressWarnings("unchecked")
			HashMap<String, byte[]> content = (HashMap<String, byte[]>) oContent;
			
			boolean multipart = userMessage.getPayloadInfo().sizePartInfoList()>1;
			List<PartInfo> listPartInfo = new ArrayList<PartInfo>();
			
			IDSoggetto idSoggettoMittente = new IDSoggetto(busta.getTipoMittente(),busta.getMittente());
			IDSoggetto idSoggettoDestinatario = new IDSoggetto(busta.getTipoDestinatario(),busta.getDestinatario());
			IDSoggetto idSoggettoErogatore = null;
			if(RuoloMessaggio.RICHIESTA.equals(ruoloMessaggio)){
				idSoggettoErogatore = idSoggettoDestinatario;
			}
			else{
				idSoggettoErogatore = idSoggettoMittente;
			}
			
			IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
					idSoggettoErogatore, busta.getVersioneServizio());
			AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio);
			
			IDAccordo idAccordo = IDAccordoFactory.getInstance().getIDAccordoFromUri(asps.getAccordoServizioParteComune());
			AccordoServizioParteComune aspc = registryReader.getAccordoServizioParteComune(idAccordo);
			
			String azione = busta.getAzione();
			String nomePortType = asps.getPortType();
			
			// Raccolgo profile binding per comprensione xml root
			// Serve solamente se gli i contenuti sono piu' di uno
			if(!multipart) {
				listPartInfo = userMessage.getPayloadInfo().getPartInfoList();
			}
			else {
			
				TranslatorPayloadProfilesDefault t = TranslatorPayloadProfilesDefault.getTranslator();
				
				String actionProperty = AS4BuilderUtils.readPropertyInfoAction(aspc, nomePortType, azione);
				String payloadProfile = AS4BuilderUtils.readPropertyPayloadProfile(aspc, nomePortType, azione);
				if(actionProperty==null) {
					throw new ProtocolException("Action '"+azione+"' not found");
				}
				if(payloadProfile==null) {
					payloadProfile = t.getListPayloadProfileDefault().get(0).getName();
				}
				
				PayloadProfiles pps = AS4BuilderUtils.readPayloadProfiles(t, aspc, idAccordo, true);
				
				String payLoadRootMessageName = null;
				for (PayloadProfile p : pps.getPayloadProfileList()) {
					if(p.getName().equals(payloadProfile)) {
						payLoadRootMessageName = p.getAttachment(0).getName();
						break;
					}
				}
				if(payLoadRootMessageName==null) {
					throw new ProtocolException("Action '"+azione+"' with payload profile '"+payloadProfile+"' unknown (search payload name failed)");
				}
				
				String cidRootMessage = null;
				for (Payload p : pps.getPayloadList()) {
					if(p.getName().equals(payLoadRootMessageName)) {
						cidRootMessage = p.getCid();
						break;
					}
				}
				if(cidRootMessage==null) {
					throw new ProtocolException("Action '"+azione+"' with payload profile '"+payloadProfile+"' with payload '"+payLoadRootMessageName+"' unknown");
				}
				
				// inserisco prima root message
				for (PartInfo partInfo : userMessage.getPayloadInfo().getPartInfoList()) {
					if(partInfo.getHref().equals(cidRootMessage)) {
						listPartInfo.add(partInfo);
						break;
					}
				}
				// inserisco poi gli altri
				for (PartInfo partInfo : userMessage.getPayloadInfo().getPartInfoList()) {
					if(!partInfo.getHref().equals(cidRootMessage)) {
						listPartInfo.add(partInfo);
					}
				}
			}
			
			PartInfo partInfoRoot = listPartInfo.get(0);
			String mimeTypeRoot = this.getMimeType(partInfoRoot);
			byte[] contentRoot = content.get(partInfoRoot.getHref());
			String contentIdRoot = partInfoRoot.getHref();
			if(contentIdRoot.startsWith("cid:")) {
				contentIdRoot = contentIdRoot .substring("cid:".length());
			}
			
			OpenSPCoop2Message newMessage = null;
			MessageType messageType = serviceBindingConfiguration.
				getMessageType(integrationServiceBinding, MessageRole.REQUEST, msg.getTransportRequestContext(), mimeTypeRoot, null);
			
			if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
				
				// Non ci si puo' basare sul content-type per capire se siamo in soap11 o soap12.
				// Dobbiamo usare il namespace
				
				// ----- checkEnvelope----
				Element envelope = XMLUtils.getInstance().newElement(contentRoot);
				String namespace = envelope.getNamespaceURI();
				if(Costanti.SOAP12_ENVELOPE_NAMESPACE.equals(namespace)) {
					// correggo message type
					messageType = MessageType.SOAP_12;		
					mimeTypeRoot = HttpConstants.CONTENT_TYPE_SOAP_1_2; 
					// altrimenti se continuo ad utilizzare originale text/xml da errore: 
					// Caused by: com.sun.xml.messaging.saaj.soap.SOAPVersionMismatchException: Cannot create message: incorrect content-type for SOAP version. Got: text/xml Expected: application/soap+xml
			        //  at com.sun.xml.messaging.saaj.soap.MessageImpl.init(MessageImpl.java:403) ~[saaj-impl-1.3.25.jar:?]
			        //  at com.sun.xml.messaging.saaj.soap.MessageImpl.<init>(MessageImpl.java:320) ~[saaj-impl-1.3.25.jar:?]
			        //  at com.sun.xml.messaging.saaj.soap.ver1_2.Message1_2Impl.<init>(Message1_2Impl.java:74) ~[saaj-impl-1.3.25.jar:?]
			        //  at org.openspcoop2.message.soap.Message1_2_FIX_Impl.<init>(Message1_2_FIX_Impl.java:79) ~[openspcoop2_message_BUILD-13647.jar:?]
			        //  at org.openspcoop2.message.soap.OpenSPCoop2Message_saaj_12_impl.<init>(OpenSPCoop2Message_saaj_12_impl.java:59)
				}
				else if(Costanti.SOAP_ENVELOPE_NAMESPACE.equals(namespace)) {
					messageType = MessageType.SOAP_11;		
					mimeTypeRoot = HttpConstants.CONTENT_TYPE_SOAP_1_1; 
				}
				else {
					throw new Exception("Namespace ["+namespace+"] sconosciuto per integrazione SOAP");
				}
				
			}
			
			TransportRequestContext transportRequestContext = msg.getTransportRequestContext();
			transportRequestContext.setFunction(URLProtocolContext.PA_FUNCTION);
			if(ServiceBinding.REST.equals(integrationServiceBinding)) {
				
				if(multipart) {
					messageType = MessageType.MIME_MULTIPART;		
				}
				
				Resource resourceInvoke = null;
				for (Resource r : aspc.getResourceList()) {
					if(r.getNome().equals(azione)) {
						resourceInvoke = r;
						break;
					}
				}
				if(resourceInvoke==null) {
					throw new Exception("Risorsa ["+azione+"] sconosciuta per integrazione REST");
				}
				
				String interfaceName = transportRequestContext.getInterfaceName();
				if(resourceInvoke.getPath()!=null) {
					String p = interfaceName;
					if(interfaceName.endsWith("/")) {
						p = interfaceName.substring(0, (interfaceName.length()-1));
					}
					if(resourceInvoke.getPath().startsWith("/")==false) {
						p = p + "/";
					}
					p = resourceInvoke.getPath();
					transportRequestContext.setFunctionParameters(p);
				}
				else {
					transportRequestContext.setFunctionParameters(interfaceName);
				}
				
				if(resourceInvoke.getMethod()==null) {
					transportRequestContext.setRequestType( org.openspcoop2.core.registry.constants.HttpMethod.POST.getValue()); // DEFAULT
				}
				else {
					transportRequestContext.setRequestType(resourceInvoke.getMethod().getValue());
				}
			}
			if(transportRequestContext.getParametersTrasporto()==null) {
				transportRequestContext.setParametersTrasporto(new Properties());
			}
			transportRequestContext.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE);
			transportRequestContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, mimeTypeRoot);
			
			OpenSPCoop2MessageParseResult result = null;
			if(MessageType.MIME_MULTIPART.equals(messageType)==false) {
				result = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, contentRoot);
	//			result = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, MessageRole.REQUEST, 
	//					mimeTypeRoot, contentRoot);
				newMessage = result.getMessage_throwParseThrowable();
			}
			
			if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
							
				// ----- checkSOAPAction ----
				
				String soapAction = CostantiPdD.OPENSPCOOP2;
				
				if(azione!=null) {
					if(nomePortType!=null) {
						org.openspcoop2.core.registry.wsdl.AccordoServizioWrapper asWrapper = registryReader.getAccordoServizioParteComuneSoap(idServizio, InformationApiSource.SAFE_SPECIFIC_REGISTRY, false);
						if(asWrapper!=null) {
							PortType pt = asWrapper.getPortType(asps.getPortType());
							if(pt!=null) {
								for (Operation op : pt.getAzioneList()) {
									if(op.getNome().equals(busta.getAzione())) {
										soapAction = op.getSoapAction();
										if(soapAction==null) {
											soapAction = "";
										}
										break;
									}
								}
							}
						}
					}
				}
				
				newMessage.castAsSoap().setSoapAction(soapAction);
			}
			
			
			if(multipart) {
				
				MimeMultipart mm = null;
				if(MessageType.MIME_MULTIPART.equals(messageType)) {
					mm = new MimeMultipart(HttpConstants.CONTENT_TYPE_MULTIPART_MIXED_SUBTYPE);
					
					InternetHeaders headers = new InternetHeaders();
					headers.addHeader(HttpConstants.CONTENT_ID, "<"+contentIdRoot+">");
					headers.addHeader(HttpConstants.CONTENT_TYPE, mimeTypeRoot);
					BodyPart bodyPart = mm.createBodyPart(headers, contentRoot);
					mm.addBodyPart(bodyPart);
				}
				
				for (int i = 1; i < listPartInfo.size(); i++) {
					PartInfo partInfoAttach = listPartInfo.get(i);
					byte[] c = content.get(partInfoAttach.getHref()); 
					String mimeType = this.getMimeType(partInfoAttach);
					String contentId = partInfoAttach.getHref();
					if(contentId.startsWith("cid:")) {
						contentId = contentId .substring("cid:".length());
					}
					contentId = "<"+contentId+">";
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
						OpenSPCoop2SoapMessage soapMsg = newMessage.castAsSoap();
						AttachmentPart ap = soapMsg.createAttachmentPart();
						ap.setRawContentBytes(c,0,c.length,mimeType);
						ap.setContentId(contentId);
						soapMsg.addAttachmentPart(ap);
					}
					else {						
						InternetHeaders headers = new InternetHeaders();
						headers.addHeader(HttpConstants.CONTENT_ID, contentId);
						headers.addHeader(HttpConstants.CONTENT_TYPE, mimeType);
						BodyPart bodyPart = mm.createBodyPart(headers, c);
						mm.addBodyPart(bodyPart);
					}
				}
				
				if(MessageType.MIME_MULTIPART.equals(messageType)) {
					transportRequestContext.getParametersTrasporto().remove(HttpConstants.CONTENT_TYPE);
					transportRequestContext.getParametersTrasporto().put(HttpConstants.CONTENT_TYPE, mm.getContentType());
					ByteArrayOutputStream bout = new ByteArrayOutputStream();
					mm.writeTo(bout);
					bout.flush();
					bout.close();
					result = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, transportRequestContext, bout.toByteArray());
					newMessage = result.getMessage_throwParseThrowable();
				}
				
				if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
					OpenSPCoop2SoapMessage soapMsg = newMessage.castAsSoap();
					soapMsg.getSOAPPart().addMimeHeader(HttpConstants.CONTENT_ID, "<"+contentIdRoot+">");
				}
			}
			
			ProtocolMessage protocolMessage = new ProtocolMessage();
			protocolMessage.setMessage(newMessage);
			protocolMessage.setUseBustaRawContentReadByValidation(true);
			return protocolMessage;
			
		}catch(Throwable e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	private String getMimeType(PartInfo partInfo) throws ProtocolException {
		for (Property property : partInfo.getPartProperties().getPropertyList()) {
			if(AS4Costanti.AS4_USER_MESSAGE_PAYLOAD_INFO_PROPERTIES_MIME_TYPE.equals(property.getName())) {
				return property.getBase();
			}
		}
		throw new ProtocolException("MimeType not found in part info ["+partInfo.getHref()+"]");
	}
}
