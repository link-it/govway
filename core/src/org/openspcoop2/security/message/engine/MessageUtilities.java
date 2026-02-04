/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2026 Link.it srl (https://link.it). 
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

package org.openspcoop2.security.message.engine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import jakarta.xml.soap.AttachmentPart;

import org.openspcoop2.core.constants.Costanti;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageRole;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reference.AttachmentReference;
import org.openspcoop2.message.soap.reference.ElementReference;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.protocol.sdk.Context;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.openspcoop2.utils.DynamicStringReplace;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.slf4j.Logger;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.minidev.json.JSONObject;

/**
 * MessageUtilities
 *
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtilities {

	private MessageUtilities() {}


	public static void checkEncryptionPartElements(Map<QName, QName> notResolved, OpenSPCoop2SoapMessage message, List<SubErrorCodeSecurity> listaErroriInElementi) throws SecurityException {

		try{

			if(notResolved != null && !notResolved.isEmpty()) {
				for (QName expected : notResolved.keySet()) {
					QName actualQName = notResolved.get(expected);
					boolean found = false;
					NodeList it = message.getSOAPPart().getElementsByTagNameNS(actualQName.getNamespaceURI(), actualQName.getLocalPart());
					for (int j = 0; j < it.getLength() && !found; j++) {
						Node elementFather = (Node) it.item(j);
						/**SOAPElement elementFather = (SOAPElement) it.item(j);
						List<SOAPElement> encryptedElements = SoapUtils.getNotEmptyChildSOAPElement(elementFather);*/
						List<Node> encryptedElements = SoapUtils.getNotEmptyChildNodes(message.getFactory(), elementFather, false);
						for (int i = 0; i < encryptedElements.size() && !found; i++) {
							/**SOAPElement actual = encryptedElements.get(i);*/
							Node actual = encryptedElements.get(i);
							String actualNamespaceURI = actual.getNamespaceURI();
							String actualLocalName = actual.getLocalName();
							String expectedNamespaceURI = expected.getNamespaceURI();
							String expectedLocalName = expected.getLocalPart();
							if(((actualNamespaceURI == null && expectedNamespaceURI == null) || (actualNamespaceURI!=null && actualNamespaceURI.equals(expectedNamespaceURI)))
									&& actualLocalName.equals(expectedLocalName)) {
								//trovato l'elemento che ci interessa
								found = true;
							}
						}
					}
					if(!found) {
						SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
						subCodice.setEncrypt(true);
						subCodice.setMsgErrore("Expected encryption part("+ expected +") not found");
						subCodice.setTipo(SecurityConstants.ENCRYPTION_PART_CONTENT);
						subCodice.setNamespace(expected.getNamespaceURI());
						subCodice.setName(expected.getLocalPart());
						listaErroriInElementi.add(subCodice);
					}
				}
			}

		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}

	public static Map<QName, QName> checkEncryptSignatureParts(MessageSecurityContext messageSecurityContext,List<Reference> elementsToClean, OpenSPCoop2SoapMessage message,
			List<SubErrorCodeSecurity> codiciErrore, QName qnameSecurity) throws SecurityException {

		Map<String, Object> properties = messageSecurityContext.getIncomingProperties();
		boolean addAttachmentIdBrackets = properties.containsKey(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS) ?
				properties.get(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS).equals(SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS_TRUE) :
					SecurityConstants.ADD_ATTACHMENT_ID_BRACKETS_DEFAULT;
		try{
			int numAttachmentsInMsg = message.countAttachments();
			List<String> cidAttachments = new ArrayList<>();
			if(numAttachmentsInMsg>0){
				Iterator<?> itAttach = message.getAttachments();
				while (itAttach.hasNext()) {
					AttachmentPart ap = (AttachmentPart) itAttach.next();
					String cid = ap.getContentId();
					if (!addAttachmentIdBrackets) {
						cid = cid.replaceAll("(^<)|(>$)", "");
					}
					cidAttachments.add(cid);
				}
			}

			// *** ENCRYPT VERIFY ***
			Map<QName, QName> notResolvedMap = new HashMap<>();
			Object encryptionPartsVerify = messageSecurityContext.getIncomingProperties().get(SecurityConstants.ENCRYPTION_PARTS_VERIFY);
			Object encryptionParts = messageSecurityContext.getIncomingProperties().get(SecurityConstants.ENCRYPTION_PARTS);
			if(encryptionPartsVerify!=null && "true".equalsIgnoreCase(((String)encryptionPartsVerify).trim()) &&
					encryptionParts==null){
				// Se forzo la proprietà verify allora devo indicare le encryption parts
				throw new SecurityException(SecurityConstants.ENCRYPTION_PARTS+" non indicate");
			}
			if( (
					encryptionPartsVerify==null || 
					"true".equalsIgnoreCase(((String)encryptionPartsVerify).trim())
					) &&
					(encryptionParts!=null) ){

				Map<String, Boolean> encryptionPartsMap = new HashMap<>();
				Map<Reference, Boolean> encryptionReferenceMap = new HashMap<>();
				boolean isAllAttachmentEncrypted = false;
				int numAttachmentsEncrypted = 0;
				if(encryptionParts!=null){
					String[]split = ((String)encryptionParts).split(";");
					int numElementsEncrypted = 0;
					for (int i = 0; i < split.length; i++) {
						boolean checked = false;
						String[]split2 = split[i].trim().split("}");
						String tipo = split2[0].trim().substring(1); // Element o Content
						if("".equals(tipo)){
							// caso speciale wss4j {}cid:Attachments
							tipo = SecurityConstants.PART_CONTENT;
						}
						String namespace = null;
						String nome = null;
						boolean attach = false;
						if(split2.length==3){
							namespace = split2[1].trim().substring(1); 
							nome = split2[2].trim();
							attach = SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH.equals(namespace);
						}
						else{
							// caso speciale wss4j {}cid:Attachments ?
							if(SecurityConstants.CID_ATTACH_WSS4J.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
								attach = true;
							}
							else{
								throw new SecurityException("Part ["+split[i]+"] with wrong format");
							}
						}
						if(SecurityConstants.ENCRYPTION_PART_ELEMENT.equals(tipo)) {
							// incremento solamente se non è un attachments
							if(!attach){
								numElementsEncrypted++;
							}
						}
						if(nome.startsWith("{"))
							nome = nome.substring(1);
						/**System.out.println("CIFRO ["+tipo+"] ["+namespace+"] ["+nome+"]");*/	

						List<String> cidAttachmentsEncrypt = new ArrayList<>();
						if(cidAttachments!=null && !cidAttachments.isEmpty()){
							cidAttachmentsEncrypt.addAll(cidAttachments);
						}

						for (Reference reference : elementsToClean) {

							if(reference instanceof AttachmentReference) {
								if(AttachmentReference.TYPE_ENCRYPT_ATTACHMENT==reference.getType()){
									if(SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH.equals(namespace)){
										numAttachmentsEncrypted++;
										if(nome.equals(SecurityConstants.ATTACHMENT_INDEX_ALL)) {
											checked = true;
											isAllAttachmentEncrypted = true;
											encryptionReferenceMap.put(reference, true);
											encryptionPartsMap.put(split[i], checked);
											/** break;*/
											// Vanno iterate tutte, dovendole contare
										}else{
											int position = -1;
											try{
												position = Integer.parseInt(nome);
											}catch(Exception e){
												// position non presente
											}
											if(position>0){
												int refPosition = -1;
												for (int j = 0; j < cidAttachments.size(); j++) {
													if(cidAttachments.get(j).equals(reference.getReference())){
														refPosition = j+1;
														break;
													}
												}
												/**if(refPosition == position){ // Alcune implementazioni durante la spedizione modificano l'ordine degli attachments.*/
												// Non è possibile effettuare tale controllo sulla ricezione. Può essere usato solo per specificare quale attach firmare/cifrare in spedizione
												// verifico solamente che il cid sia presente
												if(refPosition>0){
													cidAttachmentsEncrypt.remove((refPosition-1));
													checked = true;
													encryptionReferenceMap.put(reference, true);
													encryptionPartsMap.put(split[i], checked);
													break;
												}
											}
											else{
												if(nome.equals(reference.getReference())) {
													checked = true;
													encryptionReferenceMap.put(reference, true);
													encryptionPartsMap.put(split[i], checked);
													break;
												}
											}
										}
									}
								}
							} else {
								ElementReference elementReference = ((ElementReference)reference);
								String localName = elementReference.getElement().getLocalName();
								String namespaceURI = elementReference.getElement().getNamespaceURI();
								if(ElementReference.TYPE_ENCRYPT_CONTENT==reference.getType()){
									// Check encrypt content
									if(nome.equals(localName) && namespace.equals(namespaceURI)) {
										checked = SecurityConstants.ENCRYPTION_PART_CONTENT.equals(tipo);
										encryptionReferenceMap.put(reference, checked);
										encryptionPartsMap.put(split[i], checked);
										if(checked){
											break;
										}
									}
								} else if(ElementReference.TYPE_ENCRYPT_ELEMENT==reference.getType() && SecurityConstants.ENCRYPTION_PART_ELEMENT.equals(tipo)) {
									// Segnamo l'elemento come checked, e lo aggiungiamo alla lista di elementi da verificare 
									// dopo la decifratura, visto che allo stato attuale e' imposibile verificarlo
									checked = true;
									encryptionPartsMap.put(split[i], checked);
									QName actual = new QName(namespaceURI, localName);
									QName expected = new QName(namespace, nome);
									boolean localPartSec = localName.equals(qnameSecurity.getLocalPart());
									boolean namespaceURISec = namespaceURI.equals(qnameSecurity.getNamespaceURI()); 
									if(!(localPartSec && namespaceURISec)) {
										if(!notResolvedMap.containsKey(expected)) {
											notResolvedMap.put(expected, actual);
										}
									}
								}
							}
						}
					}

					int numElementsExpectedToBeEncrypted = 0;
					for(Reference reference : elementsToClean) {
						if(reference.getType()==ElementReference.TYPE_ENCRYPT_ELEMENT) {
							numElementsExpectedToBeEncrypted++;
						}
					}

					if(numElementsExpectedToBeEncrypted != numElementsEncrypted) {
						SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
						subCodice.setEncrypt(true);
						subCodice.setMsgErrore("Expected encryption {Element} " + numElementsExpectedToBeEncrypted + ", found " + notResolvedMap.size());
						codiciErrore.add(subCodice);
					}
				}
				if(encryptionParts!=null){
					String[]split = ((String)encryptionParts).split(";");
					for (int i = 0; i < split.length; i++) {
						String[]split2 = split[i].trim().split("}");
						String tipo = split2[0].trim().substring(1); // Element o Content
						if("".equals(tipo)){
							// caso speciale wss4j {}cid:Attachments
							tipo = SecurityConstants.PART_CONTENT;
						}
						String namespace = null;
						String nome = null;
						if(split2.length==3){
							namespace = split2[1].trim().substring(1); 
							nome = split2[2].trim();
						}
						else{
							// caso speciale wss4j {}cid:Attachments ?
							if(SecurityConstants.CID_ATTACH_WSS4J.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new SecurityException("Part ["+split[i]+"] with wrong format");
							}
						}
						if(nome.startsWith("{"))
							nome = nome.substring(1);
						if(SecurityConstants.ENCRYPTION_PART_CONTENT.equals(tipo)){
							if(!encryptionPartsMap.containsKey(split[i]) || !encryptionPartsMap.get(split[i])) {
								SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
								subCodice.setEncrypt(true);
								subCodice.setMsgErrore("Expected encryption part("+ split[i] +") not found");
								subCodice.setTipo(tipo);
								subCodice.setNamespace(namespace);
								subCodice.setName(nome);
								codiciErrore.add(subCodice);
								/**throw new Exception("Expected encryption part("+ split[i] +") not found");*/
							}
						}
					}
				}

				if(isAllAttachmentEncrypted && numAttachmentsEncrypted != numAttachmentsInMsg) {
					SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
					subCodice.setEncrypt(true);
					subCodice.setMsgErrore("All attachments in message (found:"+numAttachmentsInMsg+") must be encrypted, but only "+numAttachmentsEncrypted+" appear to be encrypted");
					subCodice.setTipo(SecurityConstants.ENCRYPTION_PART_CONTENT);
					subCodice.setNamespace(SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH);
					subCodice.setName(SecurityConstants.ATTACHMENT_INDEX_ALL);
					codiciErrore.add(subCodice);
					/**throw new Exception("All attachments in message (found:"+numAttachmentsInMsg+") must be encrypted, but only "+numAttachmentsEncrypted+" appear to be encrypted");*/
				}

				for (Reference reference: elementsToClean) {
					if(reference.getType()==ElementReference.TYPE_ENCRYPT_CONTENT || reference.getType()==AttachmentReference.TYPE_ENCRYPT_ATTACHMENT) {
						if(!encryptionReferenceMap.containsKey(reference) || !encryptionReferenceMap.get(reference)) {
							SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
							subCodice.setEncrypt(true);
							subCodice.setMsgErrore("Found encryption part("+ reference+") not expected");
							if(reference.getType()==ElementReference.TYPE_ENCRYPT_CONTENT){
								ElementReference elReference = (ElementReference) reference;
								subCodice.setTipo(SecurityConstants.ENCRYPTION_PART_CONTENT);
								subCodice.setNamespace(elReference.getElement().getNamespaceURI());
								subCodice.setName(elReference.getElement().getLocalName());
							}
							else{
								subCodice.setTipo(SecurityConstants.ENCRYPTION_PART_CONTENT);
								subCodice.setNamespace(SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH);
								subCodice.setName(reference.getReference());
							}
							codiciErrore.add(subCodice);
							/**throw new Exception("Found encryption part("+ reference+") not expected");*/
						}
					}
				}

			}

			// *** SIGNATURE VERIFY ***
			Object signaturePartsVerify = messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_PARTS_VERIFY);
			Object signatureParts = messageSecurityContext.getIncomingProperties().get(SecurityConstants.SIGNATURE_PARTS);
			if(signaturePartsVerify!=null && "true".equalsIgnoreCase(((String)signaturePartsVerify).trim()) &&
					signatureParts==null){
				// Se forzo la proprietà verify allora devo indicare le signature parts
				throw new SecurityException(SecurityConstants.SIGNATURE_PARTS+" non indicate");
			}
			if( (
					signaturePartsVerify==null || 
					"true".equalsIgnoreCase(((String)signaturePartsVerify).trim())
					) &&
					(signatureParts!=null) ){

				boolean isAllAttachmentSigned = false;
				int numAttachmentsSigned = 0;
				Map<String, Boolean> signaturePartsMap = new HashMap<>();
				Map<Reference, Boolean> referenceMap = new HashMap<>();
				if(signatureParts!=null){
					String[]split = ((String)signatureParts).split(";");
					for (int i = 0; i < split.length; i++) {
						boolean checked = false;
						String[]split2 = split[i].trim().split("}");
						String tipo = split2[0].trim().substring(1); // Element o Content
						if("".equals(tipo)){
							// caso speciale wss4j {}cid:Attachments
							tipo = SecurityConstants.PART_CONTENT;
						}
						String namespace = null;
						String nome = null;
						if(split2.length==3){
							namespace = split2[1].trim().substring(1); 
							nome = split2[2].trim();
						}
						else{
							// caso speciale wss4j {}cid:Attachments ?
							if(SecurityConstants.CID_ATTACH_WSS4J.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.SIGNATURE_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new SecurityException("Part ["+split[i]+"] with wrong format");
							}
						}
						if(nome.startsWith("{"))
							nome = nome.substring(1);
						/**System.out.println("CIFRO ["+tipo+"] ["+namespace+"] ["+nome+"]");*/	

						List<String> cidAttachmentsSignature = new ArrayList<>();
						if(cidAttachments!=null && !cidAttachments.isEmpty()){
							cidAttachmentsSignature.addAll(cidAttachments);
						}

						for (Reference reference : elementsToClean) {

							if(reference instanceof AttachmentReference) {
								if(AttachmentReference.TYPE_SIGN_ATTACHMENT==reference.getType()){
									if(SecurityConstants.SIGNATURE_NAMESPACE_ATTACH.equals(namespace)){
										numAttachmentsSigned++;
										if(nome.equals(SecurityConstants.ATTACHMENT_INDEX_ALL)) {
											checked = true;
											isAllAttachmentSigned = true;
											referenceMap.put(reference, true);
											/** break;*/
											// Vanno iterate tutte, dovendole contare
										}
										else{
											int position = -1;
											try{
												position = Integer.parseInt(nome);
											}catch(Exception e){
												// position non presente
											}
											if(position>0){
												int refPosition = -1;
												for (int j = 0; j < cidAttachments.size(); j++) {
													if(cidAttachments.get(j).equals(reference.getReference())){
														refPosition = j+1;
														break;
													}
												}
												/**if(refPosition == position){ // Alcune implementazioni durante la spedizione modificano l'ordine degli attachments.*/
												// Non è possibile effettuare tale controllo sulla ricezione. Può essere usato solo per specificare quale attach firmare/cifrare in spedizione
												// verifico solamente che il cid sia presente
												if(refPosition>0){
													cidAttachmentsSignature.remove((refPosition-1));
													checked = true;
													referenceMap.put(reference, true);
													break;
												}
											}
											else{
												if(nome.equals(reference.getReference())) {
													checked = true;
													referenceMap.put(reference, true);
													break;
												}
											}
										}
									}
								}
							} else {
								ElementReference elementReference = ((ElementReference)reference);
								String localName = elementReference.getElement().getLocalName();
								String namespaceURI = elementReference.getElement().getNamespaceURI();
								if(ElementReference.TYPE_SIGNATURE==reference.getType() && nome.equals(localName) && namespace.equals(namespaceURI)) {
									checked = SecurityConstants.SIGNATURE_PART_COMPLETE.equals(tipo) ||
											SecurityConstants.SIGNATURE_PART_CONTENT.equals(tipo) || SecurityConstants.SIGNATURE_PART_ELEMENT.equals(tipo);
									referenceMap.put(reference, checked);
									if(checked){
										break;
									}
								}
							}
						}
						signaturePartsMap.put(split[i], checked);
					}
				}

				for (String part : signaturePartsMap.keySet()) {
					if(!signaturePartsMap.get(part)) {
						String[]split2 = part.trim().split("}");
						String tipo = split2[0].trim().substring(1); // Element o Content
						if("".equals(tipo)){
							// caso speciale wss4j {}cid:Attachments
							tipo = SecurityConstants.PART_CONTENT;
						}
						String namespace = null;
						String nome = null;
						if(split2.length==3){
							namespace = split2[1].trim().substring(1); 
							nome = split2[2].trim();
						}
						else{
							// caso speciale wss4j {}cid:Attachments ?
							if(SecurityConstants.CID_ATTACH_WSS4J.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.SIGNATURE_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new SecurityException("Part ["+part+"] with wrong format");
							}
						}
						if(nome.startsWith("{"))
							nome = nome.substring(1);
						SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
						subCodice.setEncrypt(false);
						subCodice.setMsgErrore("Expected signature part("+ part +") not found");
						subCodice.setTipo(tipo);
						subCodice.setNamespace(namespace);
						subCodice.setName(nome);
						codiciErrore.add(subCodice);
						/**throw new Exception("Expected signature part("+ part +") not found");*/
					}
				}


				if(isAllAttachmentSigned && numAttachmentsSigned != numAttachmentsInMsg) {
					SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
					subCodice.setEncrypt(false);
					subCodice.setMsgErrore("All attachments in message (found:"+numAttachmentsInMsg+") must be signed, but only "+numAttachmentsSigned+" appear to be signed");
					subCodice.setTipo(SecurityConstants.SIGNATURE_PART_CONTENT);
					subCodice.setNamespace(SecurityConstants.SIGNATURE_NAMESPACE_ATTACH);
					subCodice.setName(SecurityConstants.ATTACHMENT_INDEX_ALL);
					codiciErrore.add(subCodice);
					/**throw new Exception("All attachments in message (found:"+numAttachmentsInMsg+") must be signed, but "+numAttachmentsSigned+" appear to be signed");*/
				}

				for (Reference reference: elementsToClean) {
					if(reference.getType()==ElementReference.TYPE_SIGNATURE || reference.getType()==AttachmentReference.TYPE_SIGN_ATTACHMENT) {
						if(!referenceMap.containsKey(reference) || !referenceMap.get(reference)) {
							SubErrorCodeSecurity subCodice = new SubErrorCodeSecurity();
							subCodice.setEncrypt(false);
							subCodice.setMsgErrore("Found signature part("+ reference+") not expected");
							if(reference.getType()==ElementReference.TYPE_SIGNATURE){
								ElementReference elReference = (ElementReference) reference;
								subCodice.setTipo("Signature"); // Non esiste Content/Element
								subCodice.setNamespace(elReference.getElement().getNamespaceURI());
								subCodice.setName(elReference.getElement().getLocalName());
							}
							else{
								subCodice.setTipo(SecurityConstants.SIGNATURE_PART_CONTENT);
								subCodice.setNamespace(SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH);
								subCodice.setName(reference.getReference());
							}
							codiciErrore.add(subCodice);						
							/**throw new Exception("Found signature part("+ reference+") not expected");*/
						}
					}
				}
			}

			return notResolvedMap;

		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}


	public static boolean isIntegerInRanges(Integer value, String rawRanges) {
		String[] ranges = rawRanges.replace(" ", "").split(",");
		for (String range : ranges) {
			int minRange = 0;
			int maxRange = 0;
			if (range.contains("-")) {
				String[] limits = range.split("-");
				minRange = limits[0].isBlank() ? 0 : Integer.parseInt(limits[0]);
				maxRange = limits.length == 1 ? Integer.MAX_VALUE : Integer.parseInt(limits[1]);
			} else {
				minRange = Integer.parseInt(range);
				maxRange = minRange;
			}

			if (value >= minRange && value <= maxRange)
				return true;
		}
		return false;
	}

	public static String resolveDynamicValue(String name, String value, Map<String, Object> dynamicMap) throws SecurityException {
		if (value == null || dynamicMap == null) {
			return value;
		}
		try {
			return DynamicStringReplace.replace(value, dynamicMap, true);
		} catch (UtilsException e) {
			throw new SecurityException("Errore nella risoluzione del valore dinamico per '" + name + "': " + e.getMessage(), e);
		}
	}

	
	
	public static void addJSONClaims(MessageSecurityContext messageSecurityContext, OpenSPCoop2Message message, org.openspcoop2.utils.Map<Object> ctx) throws MessageException, MessageNotSupportedException, SecurityException {
		Context context = (ctx instanceof Context) ? (Context) ctx : null;

		// Leggi la policy per i claims esistenti (default: preserve)
		String policy =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_EXISTING_POLICY);
		if (policy == null || policy.isEmpty()) {
			policy = SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_PRESERVE;
		}		

		String contentType =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.MESSAGE_SECURITY_CONTENT_TYPE);
		if (contentType != null) {
			message.setContentType(contentType);
		}

		JSONObject json = null; // la struttura json viene valorizzata solo se necessario

		json = addIssClaim(messageSecurityContext, context, message, json, policy);

		json = addAudClaim(messageSecurityContext, context, message, json, policy);

		json = addExpClaim(messageSecurityContext, context, message, json, policy);

		addJtiClaim(messageSecurityContext, context, message, json, policy);

	}	 
	
	/**
	 * Risolve il valore del claim in base alla policy configurata per i claims esistenti.
	 *
	 * @param existingJson JSON esistente parsato dal messaggio (può essere null)
	 * @param claimName nome del claim da verificare
	 * @param newValue valore da inserire
	 * @param policy policy da applicare (preserve, override, error)
	 * @return il valore da usare (nuovo valore o null se il claim esiste e policy=preserve)
	 * @throws SecurityException se policy=error e il claim esiste già
	 */
	private static Object resolveClaimValue(JSONObject existingJson, String claimName,
			Object newValue, String policy, MessageRole role) throws SecurityException {

		if (existingJson == null) {
			return newValue;
		}

		boolean claimExists = existingJson.containsKey(claimName);

		if (!claimExists) {
			return newValue;
		}

		// Claim esiste, applica policy
		if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_PRESERVE.equals(policy)) {
			// Mantieni originale, non aggiungere nulla
			return null;
		} else if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_OVERRIDE.equals(policy)) {
			// Sovrascrivi con il nuovo valore
			return newValue;
		} else if (SecurityConstants.JWT_CLAIMS_EXISTING_POLICY_ERROR.equals(policy)) {
			String msg = MessageRole.REQUEST.equals(role) ? "richiesta" : "risposta";
			throw new SecurityException("Il claim '" + claimName + "' è già presente nella "+msg);
		}

		// Default: preserve
		return null;
	}

	private static JSONObject getJsonMessage(OpenSPCoop2Message message, Logger log) {
		JSONObject existingJson = null;
		try {
			if (message.castAsRestJson().hasContent()) {
				String jsonContent = message.castAsRestJson().getContent();
				if (jsonContent != null && !jsonContent.trim().isEmpty()) {
					existingJson = JsonPathExpressionEngine.getJSONObject(jsonContent);
				}
			}
		} catch (Exception e) {
			log.debug("Parsing JSON esistente fallito: {}", e.getMessage());
		}

		return existingJson;
	}

	private static JSONObject addIssClaim(MessageSecurityContext messageSecurityContext, Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);

		String iss = MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_ISSUER);
		if (iss != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message, messageSecurityContext.getLog());

			iss = MessageUtilities.resolveDynamicValue("iss", iss, dynamicMap);
			Object resolvedIss = MessageUtilities.resolveClaimValue(existingJson, "iss", iss, policy, message.getMessageRole());
			if (resolvedIss != null) {
				message.castAsRestJson().addSimpleElement("iss", resolvedIss);
			}
		}

		return existingJson;
	}

	private static JSONObject addAudClaim(MessageSecurityContext messageSecurityContext, Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);

		String audMode =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_AUDIENCE_MODE);
		String aud =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_AUDIENCE_VALUE);
		if (SecurityConstants.JWT_CLAIMS_AUDIENCE_MODE_VOUCHER.equals(audMode)) {
			aud = "${tokenInfo:sub}";
		}
		if (aud != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message, messageSecurityContext.getLog());

			aud = MessageUtilities.resolveDynamicValue("aud", aud, dynamicMap);
			Object resolvedAud = MessageUtilities.resolveClaimValue(existingJson, "aud", aud, policy, message.getMessageRole());
			if (resolvedAud != null) {
				message.castAsRestJson().addSimpleElement("aud", resolvedAud);
			}
		}

		return existingJson;
	}

	private static JSONObject addExpClaim(MessageSecurityContext messageSecurityContext, Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);

		String ttl =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_EXPIRED_TTL);
		if (ttl != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message, messageSecurityContext.getLog());

			ttl = MessageUtilities.resolveDynamicValue("ttl", ttl, dynamicMap);
			long instant = Instant.now().getEpochSecond();

			Object resolvedIat = MessageUtilities.resolveClaimValue(existingJson, "iat", instant, policy, message.getMessageRole());
			if (resolvedIat != null) {
				message.castAsRestJson().addSimpleElement("iat", resolvedIat);
			}

			Object resolvedNbf = MessageUtilities.resolveClaimValue(existingJson, "nbf", instant, policy, message.getMessageRole());
			if (resolvedNbf != null) {
				message.castAsRestJson().addSimpleElement("nbf", resolvedNbf);
			}

			long expValue = instant + Integer.parseInt(ttl);
			Object resolvedExp = MessageUtilities.resolveClaimValue(existingJson, "exp", expValue, policy, message.getMessageRole());
			if (resolvedExp != null) {
				message.castAsRestJson().addSimpleElement("exp", resolvedExp);
			}
		}

		return existingJson;
	}

	private static JSONObject addJtiClaim(MessageSecurityContext messageSecurityContext, Context context, OpenSPCoop2Message message, JSONObject existingJson, String policy) throws SecurityException, MessageException, MessageNotSupportedException {
		Map<String, Object> dynamicMap = Costanti.readDynamicMap(context);

		String jtiMode =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_JTI_MODE);
		String jti =  MessageUtilities.getOutgoingProperty(messageSecurityContext, SecurityConstants.JWT_CLAIMS_JTI_VALUE);
		if (SecurityConstants.JWT_CLAIMS_JTI_MODE_TRANSACTION_ID.equals(jtiMode)) {
			jti = message.getTransactionId();
		} else if (jti != null) {
			jti = MessageUtilities.resolveDynamicValue("jti", jti, dynamicMap);
		}
		if (jti != null) {
			if (existingJson == null)
				existingJson = getJsonMessage(message, messageSecurityContext.getLog());

			Object resolvedJti = MessageUtilities.resolveClaimValue(existingJson, "jti", jti, policy, message.getMessageRole());
			if (resolvedJti != null) {
				message.castAsRestJson().addSimpleElement("jti", resolvedJti);
			}
		}

		return existingJson;
	}
	
	public static String getOutgoingProperty(MessageSecurityContext messageSecurityContext, String key) {
		return (String) messageSecurityContext.getOutgoingProperties().get(key);
	}
}
