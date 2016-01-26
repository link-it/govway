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
package org.openspcoop2.protocol.sdi.validator;

import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ScartoEsitoCommittenteType;
import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.constants.EsitoNotificaType;
import it.gov.fatturapa.sdi.ws.ricezione.v1_0.types.utils.ProjectInfo;

import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;

import org.apache.soap.encoding.soapenc.Base64;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.message.mtom.MTOMUtilities;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviNotifica;
import org.openspcoop2.protocol.sdi.utils.SDICompatibilitaNamespaceErrati;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.w3c.dom.Element;

/**
 * SDIValidatoreServizioRiceviNotifica
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidatoreServizioRiceviNotifica {

	private SDIValidazioneSintattica sdiValidazioneSintattica;
	private SDIValidazioneSemantica sdiValidazioneSemantica;
	private OpenSPCoop2Message msg;
	private boolean isRichiesta;
	private SOAPElement sdiMessage;
	private String namespace;
	private Busta busta;
		
	
	public SDIValidatoreServizioRiceviNotifica(SDIValidazioneSintattica sdiValidazioneSintattica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSintattica = sdiValidazioneSintattica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
		this.busta = busta;
	}
	public SDIValidatoreServizioRiceviNotifica(SDIValidazioneSemantica sdiValidazioneSemantica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSemantica = sdiValidazioneSemantica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = SDICostantiServizioRiceviNotifica.SDI_SERVIZIO_RICEVI_NOTIFICA_NAMESPACE;
		this.busta = busta;
	}
	
	private boolean checkServiceNamespace() throws Exception{
		if(this.namespace.equals(this.sdiMessage.getNamespaceURI())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Namespace ["+this.sdiMessage.getNamespaceURI()+"] differente da quello atteso ["+this.namespace+"]"));
			return false;	
		}
		return true;
	}
	
	
	
	/* ***** validaNotificaEsito ***** */
		
	public void validaNotificaEsito() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_NotificaEsito_richiesta();
			}
			else{
				this._validazioneSintattica_NotificaEsito_risposta();
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_NotificaEsito_richiesta();
			}
			else{
				this._validazioneSemantica_NotificaEsito_risposta();
			}
		}
		
	}
	private void _validazioneSintattica_NotificaEsito_richiesta() throws Exception{
		
		if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT.equals(this.sdiMessage.getLocalName())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"LocalName ["+this.sdiMessage.getLocalName()+"] differente da quello atteso ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT+"]"));
			return;	
		}
		
		Vector<SOAPElement> elementChilds = SoapUtils.getNotEmptyChildSOAPElement(this.sdiMessage);
		if(elementChilds==null || elementChilds.size()<=0){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"RootElement [{"+this.namespace+"}"+this.sdiMessage.getLocalName()+"] non contiene elementi"));
			return;	
		}
		
		String identificativoSdI = null;
		String nomeFile = null;
		Boolean fileRead = null;
		for (int i = 0; i < elementChilds.size(); i++) {
			SOAPElement child = elementChilds.get(i);
	
			if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI.equals(child.getLocalName())){
				if(identificativoSdI!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_PRESENTE_PIU_VOLTE));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALORIZZATO));
					return;	
				}
				identificativoSdI = child.getTextContent();
			}
			else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE.equals(child.getLocalName())){
				if(nomeFile!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE+"] non valorizzato"));
					return;	
				}
				nomeFile = child.getTextContent();
			}
			else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
				if(fileRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] presente piu' volte"));
					return;	
				}
				// Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
				fileRead = true;
			}
			else{
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] sconosciuto"));
				return;	
			}
			
			if(child.getNamespaceURI()!=null){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] appartiene al namespace ("+child.getNamespaceURI()+"). Era atteso un elemento senza namespace"));
				return;	
			}
		}
		if(identificativoSdI==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_PRESENTE));
			return;	
		}
		if(nomeFile==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE+"] non presente"));
			return;	
		}
		if(fileRead==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] non presente"));
			return;	
		}
		
		// nomeFile
		try{
			SDIValidatoreNomeFile.validaNomeFileMessaggi(nomeFile, TipiMessaggi.EC);
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_NOME_FILE+"] non valido: "+e.getMessage(),e,
							!this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()));
			if(this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()){
				return;	
			}
		}
		
		// setto i valori sdi nella busta
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE, nomeFile);
		
		
	}
	
	private void _validazioneSemantica_NotificaEsito_richiesta() throws Exception{
		
		// NOTA: Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
		
		/* **** ESITO COMMITTENTE **** */
		
		byte[] esito = null;
		try{
			// la validazione sintattica garantisce la presenza
			QName qnameEsito = new QName(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE);
			Iterator<?> nodeEsitoIt = this.sdiMessage.getChildElements(qnameEsito);
			SOAPElement nodeEsito = (SOAPElement) nodeEsitoIt.next();
			
			Element xomReference = MTOMUtilities.getIfExistsXomReference(nodeEsito);
			if(xomReference!=null){
				try{
					String cid = MTOMUtilities.getCidXomReference(xomReference);
					if(cid==null){
						throw new Exception("XomReference without cid reference");
					}
					AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
					esito = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
					if(esito==null || "".equals(esito)){
						throw new Exception("Contenuto non presente");
					}
				}catch(Exception e){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
					return;	
				}
			}else{
				if(nodeEsito.getTextContent()==null){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] non valorizzato"));
					return;	
				}
				String base64Metadati = nodeEsito.getTextContent();
				if(base64Metadati==null || "".equals(base64Metadati)){
					throw new Exception("Codifica Base64 non presente");
				}
				try{
					esito = Base64.decode(base64Metadati);
				}catch(Exception e){
					// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] decodifica base64 non riuscita: "+e.getMessage(),e));
					return;	
				}
			}
			
			
		}catch(Exception e){
			// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
			this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] non accessibile: "+e.getMessage(),e));
			return;	
		}
		this._validazioneEsito(esito,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
				this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
		
	}
	
	private void _validazioneEsito(byte[] esitoDoc, SDIProperties sdiProperties, 
			Vector<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory protocolFactory) throws Exception{
	
		byte[] esito = esitoDoc;
		if(sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			esito = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), esito);
		}
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				AbstractValidatoreXSD validatore = 
						it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				validatore.valida(new ByteArrayInputStream(esito));
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] contiene un file Notifica di Esito Committente non valido rispetto allo schema XSD: "+e.getMessage(),e));
				return;	
			}
		}
		
		// Lettura
		NotificaEsitoCommittenteType esitoObject = null;
		try{
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
			esitoObject = deserializer.readNotificaEsitoCommittenteType(esito);
		}catch(Exception e){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RICHIESTA_ELEMENT_FILE+"] contiene un file Notifica di Esito Committente non valido: "+e.getMessage(),e));
			return;	
		}
		
		// Esito.IdentificativoSdI
		if(esitoObject.getIdentificativoSdI()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, esitoObject.getIdentificativoSdI().toString());
		}
		
		// Esito.RiferimentoFattura
		if(esitoObject.getRiferimentoFattura()!=null){
			if(esitoObject.getRiferimentoFattura().getAnnoFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_ANNO, esitoObject.getRiferimentoFattura().getAnnoFattura().toString());
			}
			if(esitoObject.getRiferimentoFattura().getNumeroFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_NUMERO, esitoObject.getRiferimentoFattura().getNumeroFattura());
			}
			if(esitoObject.getRiferimentoFattura().getPosizioneFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_POSIZIONE, esitoObject.getRiferimentoFattura().getPosizioneFattura().toString());
			}
		}
		
		// Esito.Esito
		if(esitoObject.getEsito()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ESITO, esitoObject.getEsito().name());
		}
				
		// Esito.Descrizione
		if(esitoObject.getDescrizione()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESCRIZIONE, esitoObject.getDescrizione());
		}
		
		// Esito.MessageId
		if(esitoObject.getMessageIdCommittente()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID_COMMITTENTE, esitoObject.getMessageIdCommittente());
		}
		
	}
	
	
	
	
	private void _validazioneSintattica_NotificaEsito_risposta() throws Exception{
		
		if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ROOT_ELEMENT.equals(this.sdiMessage.getLocalName())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"LocalName ["+this.sdiMessage.getLocalName()+"] differente da quello atteso ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ROOT_ELEMENT+"]"));
			return;	
		}
		
		Vector<SOAPElement> elementChilds = SoapUtils.getNotEmptyChildSOAPElement(this.sdiMessage);
		if(elementChilds==null || elementChilds.size()<=0){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"RootElement [{"+this.namespace+"}"+this.sdiMessage.getLocalName()+"] non contiene elementi"));
			return;	
		}
		
		String esito = null;
		Boolean scartoElementRead = null;
		String nomeFileScartoEsito = null;
		Boolean scartoEsitoRead = null;
		for (int i = 0; i < elementChilds.size(); i++) {
			SOAPElement child = elementChilds.get(i);
	
			if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO.equals(child.getLocalName())){
				if(esito!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO+"] non valorizzato"));
					return;	
				}
				esito = child.getTextContent();
			}
			else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO.equals(child.getLocalName())){
				if(scartoElementRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] presente piu' volte"));
					return;	
				}
				scartoElementRead = true;
				
				Vector<SOAPElement> elementScartoChilds = SoapUtils.getNotEmptyChildSOAPElement(child);
				if(elementScartoChilds==null || elementScartoChilds.size()<=0){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] non contiene elementi"));
					return;	
				}
				for (int j = 0; j < elementScartoChilds.size(); j++) {
					SOAPElement scartoChild = elementScartoChilds.get(j);
				
					if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE.equals(scartoChild.getLocalName())){
						if(nomeFileScartoEsito!=null){
							this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE+"] presente piu' volte"));
							return;	
						}
						if(scartoChild.getTextContent()==null){
							this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE+"] non valorizzato"));
							return;	
						}
						nomeFileScartoEsito = scartoChild.getTextContent();
					}
					else if(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE.equals(scartoChild.getLocalName())){
						if(scartoEsitoRead!=null){
							this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE+"] presente piu' volte"));
							return;	
						}
						// Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
						scartoEsitoRead = true;
					}
				}
				
			}
			else{
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] sconosciuto"));
				return;	
			}
			
			if(child.getNamespaceURI()!=null){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Element ["+child.getLocalName()+"] appartiene al namespace ("+child.getNamespaceURI()+"). Era atteso un elemento senza namespace"));
				return;	
			}
		}
		
		// esito
		if(esito==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO+"] non presente"));
			return;	
		}
		try{
			if(!EsitoNotificaType.ES00.name().equals(esito) && 
					!EsitoNotificaType.ES01.name().equals(esito) && 
					!EsitoNotificaType.ES02.name().equals(esito) ){
				throw new Exception("Valore ["+esito+"] differente dai valori attesi: ["+EsitoNotificaType.ES00.name()+","+EsitoNotificaType.ES01.name()+","+EsitoNotificaType.ES02.name()+"]");
			}
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_ESITO+"] non valido: "+e.getMessage(),e));
			return;	
		}
		
		// scartoEsito (presente solo se ES00 = NOTIFICA NON ACCETTATA)
		if(EsitoNotificaType.ES00.name().equals(esito)){
			if(scartoEsitoRead==null){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] non presente (richiesto dallo stato "+EsitoNotificaType.ES00.name()+")"));
				return;	
			}
			
			// nomeFile
			try{
				SDIValidatoreNomeFile.validaNomeFileMessaggi(nomeFileScartoEsito, TipiMessaggi.SE);
			}catch(Exception e){
				this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_NOME_FILE+"] non valido: "+e.getMessage(),e,
								!this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()));
				if(this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()){
					return;	
				}
			}
			
		}
		
		// ES02 = SERVIZIO NON DISPONIBILE
		else if(EsitoNotificaType.ES02.name().equals(esito)){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] indica un ES02 - Sistema non Disponibile del SdI"));
			return;	
		}
				
		// setto i valori sdi nella busta
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ESITO_NOTIFICA, esito);
		
		
	}
	
	private void _validazioneSemantica_NotificaEsito_risposta() throws Exception{
		
		// NOTA: Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
		
		/* **** SCARTO ESITO COMMITTENTE **** */
		
		byte[] scartoEsito = null;
		try{
			// la validazione sintattica garantisce la presenza se richiesta
			QName qnameEsito = new QName(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO);
			Iterator<?> nodeEsitoIt = this.sdiMessage.getChildElements(qnameEsito);
			SOAPElement nodeEsito = null;
			if(nodeEsitoIt!=null){
				Object o = null;
				try{
					o = nodeEsitoIt.next();
				}catch(Exception e){}
				if(o!=null){
					nodeEsito = (SOAPElement) o;
				}
			}
			
			if(nodeEsito!=null){

				QName qnameEsitoFile = new QName(SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE);
				Iterator<?> nodeEsitoFileIt = nodeEsito.getChildElements(qnameEsitoFile);
				SOAPElement nodeEsitoFile = null;
				if(nodeEsitoFileIt!=null){
					Object o = null;
					try{
						o = nodeEsitoFileIt.next();
					}catch(Exception e){}
					if(o!=null){
						nodeEsitoFile = (SOAPElement) o;
					}
				}
				
				if(nodeEsitoFile!=null){
					Element xomReference = MTOMUtilities.getIfExistsXomReference(nodeEsitoFile);
					if(xomReference!=null){
						try{
							String cid = MTOMUtilities.getCidXomReference(xomReference);
							if(cid==null){
								throw new Exception("XomReference without cid reference");
							}
							AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
							scartoEsito = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
							if(scartoEsito==null || "".equals(scartoEsito)){
								throw new Exception("Contenuto non presente");
							}
						}catch(Exception e){
							this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
							return;	
						}
					}else{
						if(nodeEsitoFile.getTextContent()==null){
							this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE+"] non valorizzato"));
							return;	
						}
						String base64Metadati = nodeEsitoFile.getTextContent();
						if(base64Metadati==null || "".equals(base64Metadati)){
							throw new Exception("Codifica Base64 non presente");
						}
						try{
							scartoEsito = Base64.decode(base64Metadati);
						}catch(Exception e){
							// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
							this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
									validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE+"] decodifica base64 non riuscita: "+e.getMessage(),e));
							return;	
						}
					}
				}
				
			}
						
		}catch(Exception e){
			// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
			this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO_FILE+"] non accessibile: "+e.getMessage(),e));
			return;	
		}
		if(scartoEsito!=null){
			this._validazioneScartoEsito(scartoEsito,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
		}
		
		
	}
	
	private void _validazioneScartoEsito(byte[] esitoDoc, SDIProperties sdiProperties, 
			Vector<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory protocolFactory) throws Exception{
	
		byte[] esito = esitoDoc;
		if(sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			esito = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), esito);
		}
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				AbstractValidatoreXSD validatore = 
						it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				validatore.valida(new ByteArrayInputStream(esito));
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] contiene un file Notifica di Scarto Esito Committente non valido rispetto allo schema XSD: "+e.getMessage(),e));
				return;	
			}
		}
		
		// Lettura
		ScartoEsitoCommittenteType scartoEsitoObject = null;
		try{
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
			scartoEsitoObject = deserializer.readScartoEsitoCommittenteType(esito);
			if(sdiProperties.isSaveMessaggiInContext()){
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, scartoEsitoObject);
			}
		}catch(Exception e){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRiceviNotifica.NOTIFICA_ESITO_RISPOSTA_ELEMENT_SCARTO_ESITO+"] contiene un file Notifica di Scarto Esito Committente non valido: "+e.getMessage(),e));
			return;	
		}
		
		// Esito.IdentificativoSdI
		if(scartoEsitoObject.getIdentificativoSdI()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, scartoEsitoObject.getIdentificativoSdI().toString());
		}
		
		// Esito.RiferimentoFattura
		if(scartoEsitoObject.getRiferimentoFattura()!=null){
			if(scartoEsitoObject.getRiferimentoFattura().getAnnoFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_ANNO, scartoEsitoObject.getRiferimentoFattura().getAnnoFattura().toString());
			}
			if(scartoEsitoObject.getRiferimentoFattura().getNumeroFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_NUMERO, scartoEsitoObject.getRiferimentoFattura().getNumeroFattura());
			}
			if(scartoEsitoObject.getRiferimentoFattura().getPosizioneFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_POSIZIONE, scartoEsitoObject.getRiferimentoFattura().getPosizioneFattura().toString());
			}
		}
		
		// Esito.Scarto
		if(scartoEsitoObject.getScarto()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_SCARTO, scartoEsitoObject.getScarto().name());
		}
						
		// Esito.MessageId
		if(scartoEsitoObject.getMessageId()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, scartoEsitoObject.getMessageId());
		}
		if(scartoEsitoObject.getMessageIdCommittente()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID_COMMITTENTE, scartoEsitoObject.getMessageIdCommittente());
		}
		
		// Esito.Note
		if(scartoEsitoObject.getNote()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, scartoEsitoObject.getNote());
		}
		
	}
	
}
