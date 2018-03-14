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

import java.util.HashMap;

import javax.mail.BodyPart;
import javax.mail.internet.InternetHeaders;
import javax.xml.soap.AttachmentPart;

import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.PartInfo;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.Property;
import org.oasis_open.docs.ebxml_msg.ebms.v3_0.ns.core._200704.UserMessage;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.registry.AccordoServizioParteSpecifica;
import org.openspcoop2.core.registry.Operation;
import org.openspcoop2.core.registry.PortType;
import org.openspcoop2.core.registry.driver.IDServizioFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2RestMimeMultipartMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.config.ServiceBindingConfiguration;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.protocol.as4.constants.AS4Costanti;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.ProtocolMessage;
import org.openspcoop2.protocol.sdk.builder.ProprietaManifestAttachments;
import org.openspcoop2.protocol.sdk.constants.FaseSbustamento;
import org.openspcoop2.protocol.sdk.constants.InformationApiSource;
import org.openspcoop2.protocol.sdk.constants.RuoloMessaggio;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.protocol.sdk.state.IState;
import org.openspcoop2.utils.transport.http.HttpConstants;

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
			IRegistryReader registryReader) throws ProtocolException {
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
			
			PartInfo partInfoRoot = userMessage.getPayloadInfo().getPartInfo(0);
			String mimeTypeRoot = this.getMimeType(partInfoRoot);
			
			OpenSPCoop2Message newMessage = null;
			MessageType messageType = serviceBindingConfiguration.
				getMessageType(integrationServiceBinding, MessageRole.REQUEST, msg.getTransportRequestContext(), mimeTypeRoot, null);
			OpenSPCoop2MessageParseResult result = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(messageType, MessageRole.REQUEST, 
					mimeTypeRoot, content.get(partInfoRoot.getHref()));
			newMessage = result.getMessage_throwParseThrowable();
			if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
				
				String soapAction = CostantiPdD.OPENSPCOOP2;
				
				if(busta.getAzione()!=null) {
					IDServizio idServizio = IDServizioFactory.getInstance().getIDServizioFromValues(busta.getTipoServizio(), busta.getServizio(), 
							busta.getTipoDestinatario(),busta.getDestinatario(), busta.getVersioneServizio());
					AccordoServizioParteSpecifica asps = registryReader.getAccordoServizioParteSpecifica(idServizio, false);
					if(asps.getPortType()!=null) {
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
			
			
			boolean multipart = userMessage.getPayloadInfo().sizePartInfoList()>1;
			if(multipart) {
				for (int i = 1; i < userMessage.getPayloadInfo().sizePartInfoList(); i++) {
					PartInfo partInfoAttach = userMessage.getPayloadInfo().getPartInfo(1);
					byte[] c = content.get(partInfoAttach.getHref()); 
					String mimeType = this.getMimeType(partInfoAttach);
					String contentId = partInfoAttach.getHref();
					if(contentId.startsWith("cid:")) {
						contentId = contentId .substring("cid:".length());
					}
					if(ServiceBinding.SOAP.equals(integrationServiceBinding)) {
						OpenSPCoop2SoapMessage soapMsg = newMessage.castAsSoap();
						AttachmentPart ap = soapMsg.createAttachmentPart();
						ap.setRawContentBytes(c,0,c.length,mimeType);
						ap.setContentId(contentId);
						soapMsg.addAttachmentPart(ap);
					}
					else {
						OpenSPCoop2RestMimeMultipartMessage restMsg = newMessage.castAsRestMimeMultipart();
						InternetHeaders headers = new InternetHeaders();
						headers.addHeader(HttpConstants.CONTENT_ID, contentId);
						headers.addHeader(HttpConstants.CONTENT_TYPE, mimeType);
						BodyPart bodyPart = restMsg.getContent().createBodyPart(headers, c);
						restMsg.getContent().addBodyPart(bodyPart);
					}
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
