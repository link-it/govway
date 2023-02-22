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
package org.openspcoop2.protocol.sdi.builder;

import java.util.Iterator;
import java.util.List;

import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.exception.MessageException;
import org.openspcoop2.message.exception.MessageNotSupportedException;
import org.openspcoop2.message.soap.DumpSoapMessageUtils;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.TunnelSoapUtils;
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
import org.openspcoop2.message.utils.MessageUtilities;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdi.utils.SDIUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.dch.MailcapActivationReader;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.constants.EsitoNotificaType;

/**
 * SDISbustamento
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDISbustamento {

	private SDIBustaBuilder bustaBuilder = null;
	@SuppressWarnings("unused")
	public SDISbustamento(SDIBustaBuilder bustaBuilder){
		this.bustaBuilder = bustaBuilder;
	}
	
	public SOAPElement sbustamentoRisposta_ServizioSdIRiceviFile_AzioneRiceviFile(Busta busta,OpenSPCoop2Message msgParam) throws ProtocolException{
		try{
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			
			SOAPElement element = null;
						
			SOAPBody soapBody = msg.getSOAPBody();
			
			// estraggo header
			element = SDIUtils.readHeader(msg);
					
			// detach body
			soapBody.removeContents();
			
			// se sono presenti degli attachments li elimino
			if(msg.countAttachments()>0){
				msg.removeAllAttachments();
			}
			
			// imposto content type
			msg.setContentType(MessageUtilities.getDefaultContentType(msg.getMessageType()));
			
			return element;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	public SOAPElement sbustamentoRichiesta_ServizioRicezioneFatture_AzioneRiceviFatture(Busta busta,OpenSPCoop2Message msgParam) throws ProtocolException{
		
		try{
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			OpenSPCoop2MessageFactory messageFactory = msgParam.getFactory();
			
			SOAPElement element = null;
			Object ctxFatturaPA = msg.removeContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA);
			String versioneFattura = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_VERSIONE_FATTURA_PA);
						
			SOAPBody soapBody = msg.getSOAPBody();
			
			// estraggo header
			element = SDIUtils.readHeader(msg);
			
			// Leggo fattura
			byte [] xmlFattura = null;
			boolean p7m = false;
			if(ctxFatturaPA!=null){
				
				//System.out.println("OTTIMIZZATO");
				if(SDICostanti.SDI_VERSIONE_FATTURA_PA_10.equals(versioneFattura)){
					it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType fattura = (it.gov.fatturapa.sdi.fatturapa.v1_0.FatturaElettronicaType) ctxFatturaPA;
					it.gov.fatturapa.sdi.fatturapa.v1_0.ObjectFactory of = new it.gov.fatturapa.sdi.fatturapa.v1_0.ObjectFactory();
					it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbSerializer serializer = new it.gov.fatturapa.sdi.fatturapa.v1_0.utils.serializer.JaxbSerializer();
					xmlFattura = serializer.toByteArray(of.createFatturaElettronica(fattura));
				}
				else if(SDICostanti.SDI_VERSIONE_FATTURA_PA_11.equals(versioneFattura)){
					it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType fattura = (it.gov.fatturapa.sdi.fatturapa.v1_1.FatturaElettronicaType) ctxFatturaPA;
					it.gov.fatturapa.sdi.fatturapa.v1_1.ObjectFactory of = new it.gov.fatturapa.sdi.fatturapa.v1_1.ObjectFactory();
					it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbSerializer serializer = new it.gov.fatturapa.sdi.fatturapa.v1_1.utils.serializer.JaxbSerializer();
					xmlFattura = serializer.toByteArray(of.createFatturaElettronica(fattura));
				}
				else if(SDICostanti.SDI_VERSIONE_FATTURA_SEMPLIFICATA_10.equals(versioneFattura)){
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType fattura = (it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.FatturaElettronicaType) ctxFatturaPA;
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.ObjectFactory of = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.ObjectFactory();
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.serializer.JaxbSerializer serializer = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_0.utils.serializer.JaxbSerializer();
					xmlFattura = serializer.toByteArray(of.createFatturaElettronicaSemplificata(fattura));
				}
				else{
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType fattura = (it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.FatturaElettronicaType) ctxFatturaPA;
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ObjectFactory of = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.ObjectFactory();
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.utils.serializer.JaxbSerializer serializer = new it.gov.agenziaentrate.ivaservizi.docs.xsd.fatture.v1_2.utils.serializer.JaxbSerializer();
					xmlFattura = serializer.toByteArray(of.createFatturaElettronica(fattura));
				}
				
			}else{
				
				//System.out.println("NON OTTIMIZZATO");
				Element elementBody = SoapUtils.getNotEmptyFirstChildSOAPElement(soapBody);
				List<Node> childs = SoapUtils.getNotEmptyChildNodes(messageFactory, elementBody, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
						Element e = MTOMUtilities.getIfExistsXomReference(messageFactory, (Element)child);
						if(e!=null){
							//System.out.println("NON OTTIMIZZATO MTOM");
							// mtom
							AttachmentPart ap = this.getAttachmentPart(msg, e);
							if(ap!=null) {
								xmlFattura = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
							}
						}else{
							//System.out.println("NON OTTIMIZZATO NO MTOM");
							// no mtom
							xmlFattura = Base64Utilities.decode(child.getTextContent());
						}
					}
				}
				
				// Se la fattura e' un P7M non e' un xml.
				// Il formato viene compreso durante la validazione sintattica
				Object formato = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA);
				if(formato!=null && ((String)formato).equals(SDICostanti.SDI_TIPO_FATTURA_P7M) ){
					p7m = true;
				}
			}
			
					
			// detach body
			soapBody.removeContents();
			
			// se sono presenti degli attachments li elimino
			if(msg.countAttachments()>0){
				msg.removeAllAttachments();
			}
				
			// imposto content type
			msg.setContentType(MessageUtilities.getDefaultContentType(msg.getMessageType()));
			
			// add Fattura as body
			if(p7m){
				org.openspcoop2.utils.mime.MimeTypes mimeTypes = org.openspcoop2.utils.mime.MimeTypes.getInstance();
				TunnelSoapUtils.
					imbustamentoMessaggioConAttachment(msg,xmlFattura,HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP,
							MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP),
							mimeTypes.getMimeType(SDICostanti.SDI_TIPO_FATTURA_P7M), SDICostanti.SDI_PROTOCOL_NAMESPACE);
				
				// Serve per forzare il tunnel SOAP che altrimenti non viene abilitato ('tunnel openspcoop2') 
				DumpSoapMessageUtils.dumpMessage(msg, true);

			}else{
				//soapBody.addChildElement(SoapUtils.getSoapFactory(msg.getMessageType()).createElement(this.xmlUtils.newElement(xmlFattura)));
				// Bug Fix: OP-752
				
				// USO Comunque il tunnel SOAP altrimenti l'xml viene modificato e la firma non e' piu' valida
				// Uso esattamente il codice sopra utilizzando pero' come content type text/xml:
				TunnelSoapUtils.
				imbustamentoMessaggioConAttachment(msg,xmlFattura,HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP,
						MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP),
						HttpConstants.CONTENT_TYPE_TEXT_XML, SDICostanti.SDI_PROTOCOL_NAMESPACE);
			
				// Serve per forzare il tunnel SOAP che altrimenti non viene abilitato ('tunnel openspcoop2') 
				DumpSoapMessageUtils.dumpMessage(msg, true);
			}
			
			
			return element;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	public SOAPElement sbustamentoRisposta_ServizioSdIRiceviNotifica_AzioneNotificaEsito(Busta busta,OpenSPCoop2Message msgParam) throws ProtocolException{
		try{
			
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			OpenSPCoop2MessageFactory messageFactory = msgParam.getFactory();
			
			SOAPElement element = null;
			Object ctxNotificaScartoEsitoCommittente = msg.removeContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI);
						
			SOAPBody soapBody = msg.getSOAPBody();
			
			// estraggo header
			element = SDIUtils.readHeader(msg);
			
			// Leggo se presente una notifica scarto esito committente
			byte [] xmlNotificaScartoEsitoCommittente = null;
			if(ctxNotificaScartoEsitoCommittente!=null){
				//System.out.println("OTTIMIZZATO");
				it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType scarto = (it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType) ctxNotificaScartoEsitoCommittente;
				it.gov.fatturapa.sdi.messaggi.v1_0.ObjectFactory of = new it.gov.fatturapa.sdi.messaggi.v1_0.ObjectFactory();
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer serializer = new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer();
				xmlNotificaScartoEsitoCommittente = serializer.toByteArray(of.createScartoEsitoCommittente(scarto));
				
			}else{
				
				//System.out.println("NON OTTIMIZZATO");
				Element elementBody = SoapUtils.getNotEmptyFirstChildSOAPElement(soapBody);
				List<Node> childs = SoapUtils.getNotEmptyChildNodes(messageFactory, elementBody, false);
				for (int i = 0; i < childs.size(); i++) {
					Node child = childs.get(i);
					if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO.equals(child.getLocalName())){
						
						List<Node> elementScartoChilds = SoapUtils.getNotEmptyChildNodes(messageFactory, child, false);
						if(elementScartoChilds!=null){
							for (int j = 0; j < elementScartoChilds.size(); j++) {
								Node scartoChild = elementScartoChilds.get(j);
						
								if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE.equals(scartoChild.getLocalName())){
									
									Element e = MTOMUtilities.getIfExistsXomReference(messageFactory, (Element)scartoChild);
									if(e!=null){
										//System.out.println("NON OTTIMIZZATO MTOM");
										// mtom
										AttachmentPart ap = this.getAttachmentPart(msg, e);
										if(ap!=null) {
											xmlNotificaScartoEsitoCommittente = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
										}
									}else{
										//System.out.println("NON OTTIMIZZATO NO MTOM");
										// no mtom
										xmlNotificaScartoEsitoCommittente = Base64Utilities.decode(scartoChild.getTextContent());
									}
									
								}
							}
						}
						
					}
				}
			}
					
			// detach body
			soapBody.removeContents();
			
			// se sono presenti degli attachments li elimino
			if(msg.countAttachments()>0){
				msg.removeAllAttachments();
			}
			
			// imposto content type
			msg.setContentType(MessageUtilities.getDefaultContentType(msg.getMessageType()));
			
			// se esiste uno scarto committente lo aggiungo come body
			if(xmlNotificaScartoEsitoCommittente!=null){
				//soapBody.addChildElement(SoapUtils.getSoapFactory(msg.getMessageType()).createElement(this.xmlUtils.newElement(xmlNotificaScartoEsitoCommittente)));
				// Bug Fix: OP-752
				
				// USO Comunque il tunnel SOAP altrimenti l'xml viene modificato e la firma non e' piu' valida
				// Uso esattamente il codice sopra utilizzando pero' come content type text/xml:
				TunnelSoapUtils.
				imbustamentoMessaggioConAttachment(msg,xmlNotificaScartoEsitoCommittente,HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP,
						MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP),
						HttpConstants.CONTENT_TYPE_TEXT_XML, SDICostanti.SDI_PROTOCOL_NAMESPACE);
			
				// Serve per forzare il tunnel SOAP che altrimenti non viene abilitato ('tunnel openspcoop2') 
				DumpSoapMessageUtils.dumpMessage(msg, true);
			}
			
			// se lo sdi ha restituito un esito non ok imposto 500 come codice di trasporto verso il client
			String esitoNotifica = busta.getProperty(SDICostanti.SDI_BUSTA_EXT_ESITO_NOTIFICA);
			// ES01 = NOTIFICA ACCETTATA
			if(EsitoNotificaType.ES01.name().equals(esitoNotifica)){
				msg.setForcedResponseCode("202");
			}
			else{
				// ES00 = NOTIFICA NON ACCETTATA
				if(EsitoNotificaType.ES00.name().equals(esitoNotifica)){
					msg.setForcedResponseCode("200");
				}
			}
			
			return element;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	
	public SOAPElement sbustamentoRichiesta_ServizioTrasmissioneFatture_Notifiche(TipiMessaggi tipoMessaggio, Busta busta,OpenSPCoop2Message msgParam) throws ProtocolException{
		
		try{
		
			OpenSPCoop2SoapMessage msg = msgParam.castAsSoap();
			OpenSPCoop2MessageFactory messageFactory = msgParam.getFactory();
			
			SOAPElement element = null;
			Object ctxMessaggio = msg.removeContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI);
						
			SOAPBody soapBody = msg.getSOAPBody();
			
			// estraggo header
			element = SDIUtils.readHeader(msg);
			
			// Leggo fattura
			byte [] xmlNotifica = null;
			byte [] zip = null;
			if(ctxMessaggio!=null){
				
				//System.out.println("OTTIMIZZATO");
				
				it.gov.fatturapa.sdi.messaggi.v1_0.ObjectFactory of = new it.gov.fatturapa.sdi.messaggi.v1_0.ObjectFactory();
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer serializer = new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer();
							
				switch (tipoMessaggio) {
				case RC:
					it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType rc = (it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createRicevutaConsegna(rc));
					break;
					
				case MC:
					it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType mc = (it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createNotificaMancataConsegna(mc));
					break;
					
				case NS:
					it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType ns = (it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createNotificaScarto(ns));
					break;
					
				case NE:
					it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType ne = (it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createNotificaEsito(ne));
					break;
					
				case DT:
					it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType dt = (it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createNotificaDecorrenzaTermini(dt));
					break;
					
				case AT:
					it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType at = (it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType) ctxMessaggio;
					xmlNotifica = serializer.toByteArray(of.createAttestazioneTrasmissioneFattura(at));
					break;

				default:
					break;
				}
				
			}else{
				
				if(TipiMessaggi.AT.equals(tipoMessaggio) && SDIProperties.getInstance(this.bustaBuilder.getLog()).isEnableAccessoMessaggi()){
					
					Object oZip = msg.removeContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_ZIP);
					if(oZip!=null){
						zip = (byte[]) oZip;
					}
					else{
						Object oXml = msg.removeContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_XML);
						if(oXml!=null){
							xmlNotifica = (byte[]) oXml;
						}
						else{
							throw new Exception("Contenuto della notifica di Attestazione Trasmissione per Impossibilità di Recapito");
						}
					}
					
				}
				else{
					//System.out.println("NON OTTIMIZZATO");
					Element elementBody = SoapUtils.getNotEmptyFirstChildSOAPElement(soapBody);
					List<Node> childs = SoapUtils.getNotEmptyChildNodes(messageFactory, elementBody, false);
					for (int i = 0; i < childs.size(); i++) {
						Node child = childs.get(i);
						if(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
							Element e = MTOMUtilities.getIfExistsXomReference(messageFactory, (Element)child);
							if(e!=null){
								//System.out.println("NON OTTIMIZZATO MTOM");
								// mtom
								AttachmentPart ap = this.getAttachmentPart(msg, e);
								if(ap!=null) {
									xmlNotifica = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
								}
							}else{
								//System.out.println("NON OTTIMIZZATO NO MTOM");
								// no mtom
								xmlNotifica = Base64Utilities.decode(child.getTextContent());
							}
						}
					}
					
					if(TipiMessaggi.AT.equals(tipoMessaggio)) {
						// xmlNotifica contiene uno zip file poiche' non l'ho trattato
						zip = xmlNotifica;
					}
				}
			}
			
					
			// detach body
			soapBody.removeContents();
			
			// se sono presenti degli attachments li elimino
			if(msg.countAttachments()>0){
				msg.removeAllAttachments();
			}
			
			// imposto content type
			msg.setContentType(MessageUtilities.getDefaultContentType(msg.getMessageType()));
			
			// add Fattura as body
			if(zip!=null){
				TunnelSoapUtils.
					imbustamentoMessaggioConAttachment(msg,zip,HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP,
							MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP),
							HttpConstants.CONTENT_TYPE_ZIP, SDICostanti.SDI_PROTOCOL_NAMESPACE);
				
				// Serve per forzare il tunnel SOAP che altrimenti non viene abilitato ('tunnel openspcoop2') 
				DumpSoapMessageUtils.dumpMessage(msg, true);

			}else{
				//soapBody.addChildElement(SoapUtils.getSoapFactory(msg.getMessageType()).createElement(this.xmlUtils.newElement(xmlNotifica)));
				// Bug Fix: OP-752
				
				// USO Comunque il tunnel SOAP altrimenti l'xml viene modificato e la firma non e' piu' valida
				// Uso esattamente il codice sopra utilizzando pero' come content type text/xml:
				TunnelSoapUtils.
				imbustamentoMessaggioConAttachment(msg,xmlNotifica,HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP,
						MailcapActivationReader.existsDataContentHandler(HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP),
						HttpConstants.CONTENT_TYPE_TEXT_XML, SDICostanti.SDI_PROTOCOL_NAMESPACE);
			
				// Serve per forzare il tunnel SOAP che altrimenti non viene abilitato ('tunnel openspcoop2') 
				DumpSoapMessageUtils.dumpMessage(msg, true);
			}
				
			return element;
			
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
		
	}
	
	
	private AttachmentPart getAttachmentPart(OpenSPCoop2SoapMessage msg,Element e) throws MessageException, MessageNotSupportedException{
		String contentId = MTOMUtilities.getCidXomReference(e);
		MimeHeaders mhs = new MimeHeaders();
		mhs.addHeader(HttpConstants.CONTENT_ID, contentId);
		Iterator<?> itAttachments = msg.getAttachments(mhs);
		if(itAttachments == null || itAttachments.hasNext()==false){
			throw new MessageException("Found XOM Reference with attribute ["+
					org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but the message hasn't attachments");
		}
		AttachmentPart ap = null;
		while (itAttachments.hasNext()) {
			if(ap!=null){
				throw new MessageException("Found XOM Reference with attribute ["+
						org.openspcoop2.message.soap.mtom.Costanti.XOP_INCLUDE_ATTRIBUTE_HREF+"]=["+contentId+"] but exists more than one attachment with same id");
			}
			ap = (AttachmentPart) itAttachments.next();
		}
		return ap;
	}
}
