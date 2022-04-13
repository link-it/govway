/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2022 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.soapbox;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.dom.DOMSource;

import org.adroitlogic.soapbox.MessageSecurityContext;
import org.adroitlogic.soapbox.SecurityRequest;
import org.adroitlogic.ultraesb.core.MessageImpl;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2MessageParseResult;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContextParameters;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.security.message.engine.MessageSecurityContext_impl;
import org.openspcoop2.security.message.engine.MessageUtilities;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.ClassLoaderUtilities;

/**
 * EngineTest
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class EngineTest {

	public static void main(String[] args) {
		try {
			// ************ MESSAGGIO *****************
			
			QName ciaoName = new QName("http://test.openspcoop.org/esempio", "ciao");
			QName otherName = new QName("other");
			QName body = new QName("http://schemas.xmlsoap.org/soap/envelope/", "Body");
			
			MessageType messageType = MessageType.SOAP_11;
			OpenSPCoop2MessageParseResult pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(messageType,MessageRole.REQUEST,
					org.openspcoop2.message.utils.MessageUtilities.getDefaultContentType(messageType), EngineTest.TEST_MSG_1.getBytes());
			OpenSPCoop2SoapMessage openspcoop2Message = pr.getMessage_throwParseException().castAsSoap();

			/* text/plain */
			AttachmentPart attachmentPart = openspcoop2Message.createAttachmentPart();
			attachmentPart.setContentId("<ID-11111-TEXT-PLAIN-1111>"); // Occhio all'id, deve essere sempre tra < e >		
			attachmentPart.setMimeHeader("Content-Type", "text/plain");
			attachmentPart.setContent("prova", "text/plain");
			openspcoop2Message.addAttachmentPart(attachmentPart);
			
			/* text/tml */
			// VOLUTAMENTE NON LO CIFRO PER TESTARE IL TRANSFORMER!!!
			AttachmentPart attachmentPart2 = openspcoop2Message.createAttachmentPart();
			attachmentPart2.setContentId("<ID-2222-TEXT-XML-22222>"); // Occhio all'id, deve essere sempre tra < e >		
			attachmentPart2.setMimeHeader("Content-Type", "text/xml");
			attachmentPart2.setContent(EngineTest.TEST_ATTACH_XML, "text/xml");
			openspcoop2Message.addAttachmentPart(attachmentPart2);

			System.out.println("-- MESSAGGIO INIZIALE --\n\n");
			System.out.println(DumpSoapMessageUtils.dumpMessage(openspcoop2Message, true));
			
			org.openspcoop2.utils.Map<Object> ctx = new org.openspcoop2.utils.Map<Object>();
			
			
			
			
			// ************* Inizializzazione *********************
			//XMLSupport.initializeInstance(10, 10, 10, 10, true);
			
			String actorWSS = "http://openspcoop.org/wss";
			boolean mustUnderstand = true;
			
			WSSUtils.initWSSecurityHeader(openspcoop2Message,actorWSS,mustUnderstand);
			
			MessageSecurityContextParameters wssContextParameters = new MessageSecurityContextParameters();
			wssContextParameters.setUseActorDefaultIfNotDefined(true);
			wssContextParameters.setActorDefault("prova");
			wssContextParameters.setLog(LoggerWrapperFactory.getLogger(EngineTest.class));
			wssContextParameters.setFunctionAsClient(true);
			wssContextParameters.setPrefixWsuId("prefix");
			org.openspcoop2.security.message.MessageSecurityContext wssContext = new MessageSecurityContext_impl(wssContextParameters);
			try{ 		
				MessageSecurityContext_soapbox s = new MessageSecurityContext_soapbox();
				s.init(wssContext);
	    	}catch(Exception e){
	    		throw new SecurityException(e.getMessage(),e);
	    	}
			
			
			
			
			// ************* Creo Motori per la gestione PD *********************
						
			org.openspcoop2.security.message.soapbox.EncryptPartialMessageProcessor encMsgProc = new org.openspcoop2.security.message.soapbox.EncryptPartialMessageProcessor();
			encMsgProc.setActor(actorWSS);
			encMsgProc.setMustUnderstand(mustUnderstand);
			encMsgProc.setMessage(openspcoop2Message);
			encMsgProc.addElementToEncrypt(ciaoName,true);
			encMsgProc.addElementToEncrypt(otherName,false);
			encMsgProc.addAttachmentToEncrypt(attachmentPart, true);
			encMsgProc.addAttachmentToEncrypt(attachmentPart2, true);
						
			TimestampMessageProcessor timestampProc = new TimestampMessageProcessor();

			
			// ************* Context per la gestione PD *********************
			
			SOAPPart sp = openspcoop2Message.getSOAPPart();
			MessageSecurityContext pdMsgSecCtx = new MessageSecurityContext(sp);

			

			
			// Password Manager			
			Map<String, String> pdPasswordMap = new HashMap<String, String>();
			pdPasswordMap.put("pd", "certpd");
			pdPasswordMap.put("symmetric", "changeit");
			
			// AsymmetricKeystore
			KeyStore pdKsAsymmetric = KeyStore.getInstance(KeyStore.getDefaultType());
			pdKsAsymmetric.load(new FileInputStream("/var/govway/keys/pd.jks"), "keypd".toCharArray());
			org.openspcoop2.security.message.soapbox.SecurityConfig pdSecConfigAsymmetric = new org.openspcoop2.security.message.soapbox.SecurityConfig(pdKsAsymmetric, pdKsAsymmetric, pdPasswordMap, ctx);
			
			// SymmetricKeystore
			KeyStore pdKsSymmetric = KeyStore.getInstance("JCEKS");
			pdKsSymmetric.load(new FileInputStream("/var/govway/keys/symmetricStore.jks"), "changeit".toCharArray());
			org.openspcoop2.security.message.soapbox.SecurityConfig pdSecConfigSymmetric = new org.openspcoop2.security.message.soapbox.SecurityConfig(pdKsSymmetric, pdKsSymmetric, pdPasswordMap, ctx);
			pdSecConfigSymmetric.setSymmetricSharedKey(true);
			
//			pdMsgSecCtx.getTimestampRequest().setTimeForExpiryMillis(1000l);
			timestampProc.process(pdSecConfigSymmetric, pdMsgSecCtx);
			
			//Processo il messaggio, applicandoci nell'ordine delle operazioni effettuate tra timestamp, firma e cifratura
			//timestampProc.process(pdSecConfig, pdMsgSecCtx);
			
			// EncryptionAsymmetric
			//pdMsgSecCtx.getEncryptionRequest().setCertAlias("pa");
			//pdMsgSecCtx.getEncryptionRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.ISSUER_SERIAL);
			// EncryptionSymmetric
			pdMsgSecCtx.getEncryptionRequest().setCertAlias("symmetric");
			pdMsgSecCtx.getEncryptionRequest().setEncryptionAlgoURI("http://www.w3.org/2001/04/xmlenc#kw-tripledes");
			pdMsgSecCtx.getEncryptionRequest().setSymmetricKeyAlgoURI("http://www.w3.org/2001/04/xmlenc#tripledes-cbc");
			pdMsgSecCtx.getEncryptionRequest().setKeySize(168);
			encMsgProc.process(pdSecConfigSymmetric, pdMsgSecCtx);
			
//			boolean forzoWriteRewrite = false;
//			if(forzoWriteRewrite){
//				ByteArrayOutputStream bout = new ByteArrayOutputStream();
//				openspcoop2Message.writeTo(bout, true);
//				bout.flush();
//				bout.close();
//				openspcoop2Message = OpenSPCoop2MessageFactory.getMessageFactory().createMessage(new ByteArrayInputStream(bout.toByteArray()), false, openspcoop2Message.getContentType(), null, true, null, null);	
//				pdMsgSecCtx = new MessageSecurityContext(openspcoop2Message.getSOAPPart());
//			}
			
			//Source s = new 
			openspcoop2Message.getSOAPPart().setContent(new DOMSource(openspcoop2Message.getSOAPPart().getEnvelope()));
			//openspcoop2Message.getSOAPPart().normalizeDocument();
			// MANTENERE QUESTA POSSIBILITA CON DOCUMENTAZIONE openspcoop2Message.getSOAPPart().getDomConfig().get
			
			// Signature
			org.openspcoop2.security.message.soapbox.SignPartialMessageProcessor signMsgProc = (SignPartialMessageProcessor) 
					ClassLoaderUtilities.newInstance(openspcoop2Message.getSignPartialMessageProcessorClass());
			signMsgProc.setActor(actorWSS);
			signMsgProc.setMustUnderstand(mustUnderstand);
			signMsgProc.setMessage(openspcoop2Message);
			signMsgProc.addElementToSign(body,true);
			Iterator<?> itAttach = openspcoop2Message.getAttachments();
			signMsgProc.addAttachmentsToSign((AttachmentPart)itAttach.next(), true);
			signMsgProc.addAttachmentsToSign((AttachmentPart)itAttach.next(), true); // xml
			
			pdMsgSecCtx.getSignatureRequest().setCertAlias("pd");
			pdMsgSecCtx.getSignatureRequest().setKeyIdentifierType(SecurityRequest.KeyIdentifierType.ISSUER_SERIAL);
			pdMsgSecCtx.getSignatureRequest().setC14nAlgoURI("http://www.w3.org/2001/10/xml-exc-c14n#");
			pdMsgSecCtx.getSignatureRequest().setDigestAlgoURI("http://www.w3.org/2000/09/xmldsig#sha1");
			pdMsgSecCtx.getSignatureRequest().setWsiBPCompliant(true);
			
			signMsgProc.process(pdSecConfigAsymmetric, pdMsgSecCtx);
//			
			System.out.println("-- MESSAGGIO DOPO AVER APPLICATO LA SICUREZZA --\n\n");
			System.out.println(DumpSoapMessageUtils.dumpMessage(openspcoop2Message, true));
			
			
			
			
			
			
			// ************* SIMULAZIONE INVIO HTTP *********************
			
			FileOutputStream fout2 = new FileOutputStream("/tmp/SENT");
			openspcoop2Message.writeTo(fout2, true);
			fout2.flush();
			fout2.close();
			
			String contentType = openspcoop2Message.getContentType();
			System.out.println("ContentType ["+contentType+"]");
			
			InputStream is2 = new FileInputStream("/tmp/SENT");
			//String envelope = openspcoop2Message.getAsString(openspcoop2Message.getSOAPPart().getEnvelope(),false);
			//String envelope = XMLUtils.getInstance().toString(openspcoop2Message.getSOAPPart().getEnvelope());
			//InputStream is2 = new ByteArrayInputStream(envelope.getBytes());
			pr = OpenSPCoop2MessageFactory.getDefaultMessageFactory().createMessage(messageType,MessageRole.RESPONSE,
					org.openspcoop2.message.utils.MessageUtilities.getDefaultContentType(messageType), is2, null);
			openspcoop2Message = pr.getMessage_throwParseException().castAsSoap();
			
			
			
			
			
			// ************* Creo Motori per la gestione PA *********************
			
			org.openspcoop2.security.message.soapbox.ProcessPartialEncryptedMessage decryptMsgProc = (ProcessPartialEncryptedMessage) 
					ClassLoaderUtilities.newInstance(openspcoop2Message.getProcessPartialEncryptedMessageClass());
			decryptMsgProc.setActor(actorWSS);
			decryptMsgProc.setMustUnderstand(mustUnderstand);
			decryptMsgProc.setMessage(openspcoop2Message);
			
			org.openspcoop2.security.message.soapbox.ProcessSignedMessage verifySignMsgProc = new org.openspcoop2.security.message.soapbox.ProcessSignedMessage();
			verifySignMsgProc.setActor(actorWSS);
			verifySignMsgProc.setMustUnderstand(mustUnderstand);
			verifySignMsgProc.setMessage(openspcoop2Message);
			
			ProcessTimestampedMessage verifyTimestampedProc = new ProcessTimestampedMessage();
			
			
			
			
			
			// ************* Context per la gestione PA *********************
			
			//inizializzo il securityContext della pa			
			
			MessageSecurityContext paMsgSecCtx = new MessageSecurityContext(openspcoop2Message.getSOAPPart(), new MessageImpl(true, null, "http"));

			//Aggiungo tutti gli attachments del messaggio al securityContext della PA, come properties, cosi' da poterli eventualmente decifrare 
			Iterator<?> it = openspcoop2Message.getAttachments();
			while(it.hasNext()) {
				AttachmentPart part = (AttachmentPart) it.next();
				String contentId = part.getContentId();
				if(contentId.startsWith("<"))
					contentId = contentId.substring(1);
				if(contentId.endsWith(">"))
					contentId = contentId.substring(0,contentId.length()-1);
				paMsgSecCtx.setProperty(contentId, part); //tolgo < e > dal nome eventualmente
			}
			
			// Password Manager			
			Map<String, String> paPasswordMap = new HashMap<String, String>();
			paPasswordMap.put("pa", "certpa");
			paPasswordMap.put("symmetric", "changeit");
			
			// AsymmetricKeystore
			KeyStore paKsAsymmetric = KeyStore.getInstance(KeyStore.getDefaultType());
			paKsAsymmetric.load(new FileInputStream("/var/govway/keys/pa.jks"), "keypa".toCharArray());
			org.openspcoop2.security.message.soapbox.SecurityConfig paSecConfigAsymmetric = new org.openspcoop2.security.message.soapbox.SecurityConfig(paKsAsymmetric, paKsAsymmetric, paPasswordMap, ctx);
			
			// SymmetricKeystore
			KeyStore paKsSymmetric = KeyStore.getInstance("JCEKS");
			paKsSymmetric.load(new FileInputStream("/var/govway/keys/symmetricStore.jks"), "changeit".toCharArray());
			org.openspcoop2.security.message.soapbox.SecurityConfig paSecConfigSymmetric = new org.openspcoop2.security.message.soapbox.SecurityConfig(paKsSymmetric, paKsSymmetric, paPasswordMap, ctx);
			paSecConfigSymmetric.setSymmetricSharedKey(true);
			paMsgSecCtx.getEncryptionRequest().setCertAlias("symmetric");
						
			//Processo il messaggio, applicandoci nell'ordine delle operazioni effettuate tra timestamp, firma e cifratura
			List<Reference> elementsToClean = openspcoop2Message.getWSSDirtyElements(actorWSS, mustUnderstand);

			Map<String, Object> wssProperties = new HashMap<String, Object>();
			
			wssProperties.put(SecurityConstants.ENCRYPTION_PARTS, "{Content}{http://test.openspcoop.org/esempio}ciao;{Element}{}other;{Content}{Attach}{*}");
			wssProperties.put(SecurityConstants.SIGNATURE_PARTS, "{Content}{http://schemas.xmlsoap.org/soap/envelope/}Body;{Content}{Attach}{*}");
			wssContext.setIncomingProperties(wssProperties);
			
			List<SubErrorCodeSecurity> listaErroriRiscontrati = new ArrayList<SubErrorCodeSecurity>();
			
			Map<QName, QName> notResolvedMap = MessageUtilities.checkEncryptSignatureParts(wssContext, elementsToClean, 
					openspcoop2Message, listaErroriRiscontrati, SecurityConstants.QNAME_WSS_ELEMENT_SECURITY);
			
			if(listaErroriRiscontrati.size()>0){
				for (SubErrorCodeSecurity subCodice : listaErroriRiscontrati) {
					System.out.println("SubCodice: " + subCodice.getMsgErrore());
					System.out.println("SubCodice Tipo: " + subCodice.getTipo());
					System.out.println("SubCodice Name:" + subCodice.getName());
					System.out.println("SubCodice Namespace:" + subCodice.getNamespace());
					System.out.println("SubCodice Subcodice:" + subCodice.getSubCodice());
				}
				throw new Exception("Riscontrati " + listaErroriRiscontrati.size() + "nel processamento");
			}
			
			verifySignMsgProc.process(paSecConfigAsymmetric, paMsgSecCtx);
			decryptMsgProc.process(paSecConfigSymmetric, paMsgSecCtx);
			verifyTimestampedProc.process(paSecConfigSymmetric, paMsgSecCtx);
			
			MessageUtilities.checkEncryptionPartElements(notResolvedMap, openspcoop2Message, listaErroriRiscontrati);
			
			if(listaErroriRiscontrati.size()>0){
				for (SubErrorCodeSecurity subCodice : listaErroriRiscontrati) {
					System.out.println("SubCodice: " + subCodice.getMsgErrore());
					System.out.println("SubCodice Tipo: " + subCodice.getTipo());
					System.out.println("SubCodice Name:" + subCodice.getName());
					System.out.println("SubCodice Namespace:" + subCodice.getNamespace());
					System.out.println("SubCodice Subcodice:" + subCodice.getSubCodice());
				}
				throw new Exception("Riscontrati " + listaErroriRiscontrati.size() + "nel processamento");
			}
			
			openspcoop2Message.cleanWSSDirtyElements(actorWSS, mustUnderstand, elementsToClean, true, false);

			
			// Aggiorno attachments
			System.out.println("-- MESSAGGIO DOPO AVER ELIMINATO LA SICUREZZA --\n\n");
			System.out.println(DumpSoapMessageUtils.dumpMessage(openspcoop2Message, true));
			
			/*(non serve se non si imposta il CONTENT_TRANSFER_ENCODING a base64!!)
			java.util.Iterator<?> itAp = openspcoop2Message.getAttachments();
			java.util.List<AttachmentPart> v = new java.util.ArrayList<AttachmentPart>();
		    while(itAp.hasNext()){
		    	AttachmentPart ap = 
		    		(AttachmentPart) itAp.next();
		    	v.add(ap);
		    }
			openspcoop2Message.removeAllAttachments();
			while(v.size()>0){
				System.out.println("ADD");
				
				AttachmentPart ap = v.remove(0);
				byte[]attac = MessageUtils.toByteArrayAttachment(ap);
				System.out.println("ADD ("+new String(attac)+")");
				
				AttachmentPart apNew = openspcoop2Message.createAttachmentPart();
				apNew.setDataHandler(ap.getDataHandler());
				Iterator<?> itMhs = ap.getAllMimeHeaders();
				while (itMhs.hasNext()) {
					javax.xml.soap.MimeHeader mh = (javax.xml.soap.MimeHeader) itMhs.next();
					System.out.println("TIPO["+mh.getName()+"] VALUE["+mh.getValue()+"]");
					if(!"Content-Transfer-Encoding".equals(mh.getName())){
						apNew.addMimeHeader(mh.getName(), mh.getValue());
					}
				}
				
				openspcoop2Message.addAttachmentPart(apNew);
			}
			*/
			
			System.out.println("OUT");
			openspcoop2Message.writeTo(System.out, false);
		
//			part.getDataHandler().writeTo(new FileOutputStream("/home/bussu/Downloads/test.pdf"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	@SuppressWarnings("unused")
//	private static void removeHeader(Document document) {
//		Element wsseSecurityElem = CryptoUtil.getWSSecurityHeader(document);
//		if (wsseSecurityElem != null) {
//			wsseSecurityElem.getParentNode().removeChild(wsseSecurityElem);
//		}
//	}
	
	private static final String TEST_MSG_1 = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
			+ "<soapenv:Body>\n"
			+ "<something xmlns=\"http://test.openspcoop.org/esempio\"><ciao>CIAOCONTENT</ciao></something>\n"
			+ "<other>OTHERCONTENT</other>"
			+ "</soapenv:Body>\n"
			+ "</soapenv:Envelope>";
	
	private static final String TEST_ATTACH_XML = "<testAllegato>GIOVANNI</testAllegato>";
}
