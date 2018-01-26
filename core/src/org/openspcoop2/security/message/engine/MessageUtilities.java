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

package org.openspcoop2.security.message.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;

import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.reference.AttachmentReference;
import org.openspcoop2.message.soap.reference.ElementReference;
import org.openspcoop2.message.soap.reference.Reference;
import org.openspcoop2.security.SecurityException;
import org.openspcoop2.security.message.MessageSecurityContext;
import org.openspcoop2.security.message.SubErrorCodeSecurity;
import org.openspcoop2.security.message.constants.SecurityConstants;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * MessageUtilities
 *
 * @author Andrea Poli <apoli@link.it>
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class MessageUtilities {

	
	public static void checkEncryptionPartElements(Map<QName, QName> notResolved, OpenSPCoop2SoapMessage message, List<SubErrorCodeSecurity> listaErroriInElementi) throws SecurityException {
		
		try{
			
			if(notResolved != null && !notResolved.isEmpty()) {
				for (QName expected : notResolved.keySet()) {
					QName actualQName = notResolved.get(expected);
					boolean found = false;
					NodeList it = message.getSOAPPart().getElementsByTagNameNS(actualQName.getNamespaceURI(), actualQName.getLocalPart());
					for (int j = 0; j < it.getLength() && !found; j++) {
						Node elementFather = (Node) it.item(j);
						//SOAPElement elementFather = (SOAPElement) it.item(j);
						//List<SOAPElement> encryptedElements = SoapUtils.getNotEmptyChildSOAPElement(elementFather);
						List<Node> encryptedElements = SoapUtils.getNotEmptyChildNodes(elementFather, false);
						for (int i = 0; i < encryptedElements.size() && !found; i++) {
							//SOAPElement actual = encryptedElements.get(i);
							Node actual = encryptedElements.get(i);
							String actualNamespaceURI = actual.getNamespaceURI();
							String actualLocalName = actual.getLocalName();
							String expectedNamespaceURI = expected.getNamespaceURI();
							String expectedLocalName = expected.getLocalPart();
							if(((actualNamespaceURI == null && expectedNamespaceURI == null) || actualNamespaceURI.equals(expectedNamespaceURI))
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
		
		try{
			int numAttachmentsInMsg = message.countAttachments();
			List<String> cidAttachments = new ArrayList<String>();
			if(numAttachmentsInMsg>0){
				Iterator<?> itAttach = message.getAttachments();
				while (itAttach.hasNext()) {
					AttachmentPart ap = (AttachmentPart) itAttach.next();
					cidAttachments.add(ap.getContentId());
				}
			}
		
			// *** ENCRYPT VERIFY ***
			Map<QName, QName> notResolvedMap = new HashMap<QName, QName>();
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
				
				Map<String, Boolean> encryptionPartsMap = new HashMap<String, Boolean>();
				Map<Reference, Boolean> encryptionReferenceMap = new HashMap<Reference, Boolean>();
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
							if(SecurityConstants.CID_ATTACH_WSS4j.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
								attach = true;
							}
							else{
								throw new Exception("Part ["+split[i]+"] with wrong format");
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
						//System.out.println("CIFRO ["+tipo+"] ["+namespace+"] ["+nome+"]");	
						
						List<String> cidAttachmentsEncrypt = new ArrayList<String>();
						if(cidAttachments!=null && cidAttachments.size()>0){
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
											// break;
											// Vanno iterate tutte, dovendole contare
										}else{
											int position = -1;
											try{
												position = Integer.parseInt(nome);
											}catch(Exception e){}
											if(position>0){
												int refPosition = -1;
												for (int j = 0; j < cidAttachments.size(); j++) {
													if(cidAttachments.get(j).equals(reference.getReference())){
														refPosition = j+1;
														break;
													}
												}
												//if(refPosition == position){ // Alcune implementazioni durante la spedizione modificano l'ordine degli attachments.
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
							if(SecurityConstants.CID_ATTACH_WSS4j.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.ENCRYPTION_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new Exception("Part ["+split[i]+"] with wrong format");
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
	//							throw new Exception("Expected encryption part("+ split[i] +") not found");
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
					//throw new Exception("All attachments in message (found:"+numAttachmentsInMsg+") must be encrypted, but only "+numAttachmentsEncrypted+" appear to be encrypted");
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
							//throw new Exception("Found encryption part("+ reference+") not expected");
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
				Map<String, Boolean> signaturePartsMap = new HashMap<String, Boolean>();
				Map<Reference, Boolean> referenceMap = new HashMap<Reference, Boolean>();
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
							if(SecurityConstants.CID_ATTACH_WSS4j.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.SIGNATURE_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new Exception("Part ["+split[i]+"] with wrong format");
							}
						}
						if(nome.startsWith("{"))
							nome = nome.substring(1);
						//System.out.println("CIFRO ["+tipo+"] ["+namespace+"] ["+nome+"]");	
						
						List<String> cidAttachmentsSignature = new ArrayList<String>();
						if(cidAttachments!=null && cidAttachments.size()>0){
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
											// break;
											// Vanno iterate tutte, dovendole contare
										}
										else{
											int position = -1;
											try{
												position = Integer.parseInt(nome);
											}catch(Exception e){}
											if(position>0){
												int refPosition = -1;
												for (int j = 0; j < cidAttachments.size(); j++) {
													if(cidAttachments.get(j).equals(reference.getReference())){
														refPosition = j+1;
														break;
													}
												}
												//if(refPosition == position){ // Alcune implementazioni durante la spedizione modificano l'ordine degli attachments.
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
							if(SecurityConstants.CID_ATTACH_WSS4j.equalsIgnoreCase(split2[1].trim())){
								namespace = SecurityConstants.SIGNATURE_NAMESPACE_ATTACH;
								nome = SecurityConstants.ATTACHMENT_INDEX_ALL;
							}
							else{
								throw new Exception("Part ["+part+"] with wrong format");
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
						//throw new Exception("Expected signature part("+ part +") not found");
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
					//throw new Exception("All attachments in message (found:"+numAttachmentsInMsg+") must be signed, but "+numAttachmentsSigned+" appear to be signed");
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
							//throw new Exception("Found signature part("+ reference+") not expected");
						}
					}
				}
			}
			
			return notResolvedMap;
			
		}catch(Exception e){
			throw new SecurityException(e.getMessage(),e);
		}
	}
	
}
