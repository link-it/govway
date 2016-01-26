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

import it.gov.fatturapa.sdi.messaggi.v1_0.MetadatiInvioFileType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType;
import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;
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
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRicezioneFatture;
import org.openspcoop2.protocol.sdi.utils.P7MInfo;
import org.openspcoop2.protocol.sdi.utils.SDICompatibilitaNamespaceErrati;
import org.openspcoop2.protocol.sdi.utils.SDIFatturaUtils;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.w3c.dom.Element;

/**
 * SDIValidatoreServizioRicezioneFatture
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidatoreServizioRicezioneFatture {

	private SDIValidazioneSintattica sdiValidazioneSintattica;
	private SDIValidazioneSemantica sdiValidazioneSemantica;
	private OpenSPCoop2Message msg;
	private boolean isRichiesta;
	private SOAPElement sdiMessage;
	private String namespace;
	private Busta busta;
		
	
	public SDIValidatoreServizioRicezioneFatture(SDIValidazioneSintattica sdiValidazioneSintattica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSintattica = sdiValidazioneSintattica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
		this.busta = busta;
	}
	public SDIValidatoreServizioRicezioneFatture(SDIValidazioneSemantica sdiValidazioneSemantica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSemantica = sdiValidazioneSemantica;
		this.msg = msg;
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = SDICostantiServizioRicezioneFatture.RICEZIONE_SERVIZIO_RICEZIONE_FATTURE_NAMESPACE;
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
	
	
	
	/* ***** validaRiceviFatture ***** */
		
	public void validaRiceviFatture() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_RiceviFatture_richiesta();
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_RiceviFatture_richiesta();
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	private void _validazioneSintattica_RiceviFatture_richiesta() throws Exception{
		
		if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ROOT_ELEMENT.equals(this.sdiMessage.getLocalName())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"LocalName ["+this.sdiMessage.getLocalName()+"] differente da quello atteso ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ROOT_ELEMENT+"]"));
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
		String nomeFileMetadati = null;
		Boolean metadatiRead = null;
		for (int i = 0; i < elementChilds.size(); i++) {
			SOAPElement child = elementChilds.get(i);
	
			if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI.equals(child.getLocalName())){
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
			else if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE.equals(child.getLocalName())){
				if(nomeFile!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE+"] non valorizzato"));
					return;	
				}
				nomeFile = child.getTextContent();
			}
			else if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
				if(fileRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] presente piu' volte"));
					return;	
				}
				// Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
				fileRead = true;
			}
			else if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI.equals(child.getLocalName())){
				if(nomeFileMetadati!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI+"] non valorizzato"));
					return;	
				}
				nomeFileMetadati = child.getTextContent();	
			}
			else if(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI.equals(child.getLocalName())){
				if(metadatiRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] presente piu' volte"));
					return;	
				}
				// Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
				metadatiRead = true;	
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
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE+"] non presente"));
			return;	
		}
		if(fileRead==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] non presente"));
			return;	
		}
		if(nomeFileMetadati==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI+"] non presente"));
			return;	
		}
		if(metadatiRead==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] non presente"));
			return;	
		}
		
		
		// nomeFile
		try{
			SDIValidatoreNomeFile.validaNomeFileFattura(nomeFile, true);
			if(nomeFile.toLowerCase().endsWith(SDICostanti.SDI_FATTURA_ESTENSIONE_P7M)){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA, SDICostanti.SDI_TIPO_FATTURA_P7M);
			}
			else{
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA, SDICostanti.SDI_TIPO_FATTURA_XML);
			}
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE+"] non valido: "+e.getMessage(),e,
							!this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()));
			if(this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()){
				return;	
			}
		}
		
		// nomeFileMetadati
		try{
			SDIValidatoreNomeFile.validaNomeFileMessaggi(nomeFile, nomeFileMetadati, TipiMessaggi.MT);
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_NOME_FILE_METADATI+"] non valido: "+e.getMessage(),e,
							!this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()));
			if(this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()){
				return;	
			}
		}
		
		
		// setto i valori sdi nella busta
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE, nomeFile);
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_METADATI, nomeFileMetadati);
		
//		NOTA: Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
//		// metadati
//		if(metadati!=null){
//			this._validazioneMetadati(metadati,this.sdiValidazioneSintattica.sdiProperties,this.sdiValidazioneSintattica.erroriValidazione,
//					this.sdiValidazioneSintattica.validazioneUtils,this.sdiValidazioneSintattica.getProtocolFactory());
//		}
//		
//		// fattura
//		if(file!=null){
//			this._validazioneFattura(file,this.sdiValidazioneSintattica.sdiProperties,this.sdiValidazioneSintattica.erroriValidazione,
//					this.sdiValidazioneSintattica.validazioneUtils,this.sdiValidazioneSintattica.getProtocolFactory());
//		}
		
		
	}
	
	private void _validazioneSemantica_RiceviFatture_richiesta() throws Exception{
		
//		NOTA: Il contenuto viene gestito nella validazione semantica (poiche' deve essere applicata la sicurezza per la firma)
		// Questo if sara' sempre vero
		if(this.busta.getProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID)==null){
		
			/* **** METADATI **** */
			
			byte[] metadati = null;
			try{
				// la validazione sintattica garantisce la presenza
				QName qnameMetadati = new QName(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI);
				Iterator<?> nodeMetadatiIt = this.sdiMessage.getChildElements(qnameMetadati);
				SOAPElement nodeMetadati = (SOAPElement) nodeMetadatiIt.next();
				
				Element xomReference = MTOMUtilities.getIfExistsXomReference(nodeMetadati);
				if(xomReference!=null){
					try{
						String cid = MTOMUtilities.getCidXomReference(xomReference);
						if(cid==null){
							throw new Exception("XomReference without cid reference");
						}
						AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
						metadati = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
						if(metadati==null || "".equals(metadati)){
							throw new Exception("Contenuto non presente");
						}
					}catch(Exception e){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
						return;	
					}
				}else{
					if(nodeMetadati.getTextContent()==null){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] non valorizzato"));
						return;	
					}
					String base64Metadati = nodeMetadati.getTextContent();
					if(base64Metadati==null || "".equals(base64Metadati)){
						throw new Exception("Codifica Base64 non presente");
					}
					try{
						metadati = Base64.decode(base64Metadati);
					}catch(Exception e){
						// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] decodifica base64 non riuscita: "+e.getMessage(),e));
						return;	
					}
				}
				
				
			}catch(Exception e){
				// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
				this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] non accessibile: "+e.getMessage(),e));
				return;	
			}
			boolean validazioneMetadatiOk = 
					this._validazioneMetadati(metadati,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			if(!validazioneMetadatiOk){
				return;
			}
			
			
			/* **** FATTURA **** */
			
			byte[] fattura = null;
			try{
				// la validazione sintattica garantisce la presenza
				QName qnameFattura = new QName(SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE);
				Iterator<?> nodeFatturaIt = this.sdiMessage.getChildElements(qnameFattura);
				SOAPElement nodeFattura = (SOAPElement) nodeFatturaIt.next();
				
				Element xomReference = MTOMUtilities.getIfExistsXomReference(nodeFattura);
				if(xomReference!=null){
					try{
						String cid = MTOMUtilities.getCidXomReference(xomReference);
						if(cid==null){
							throw new Exception("XomReference without cid reference");
						}
						AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
						fattura = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
						if(fattura==null || "".equals(fattura)){
							throw new Exception("Contenuto non presente");
						}
					}catch(Exception e){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
						return;	
					}
				}else{
					if(nodeFattura.getTextContent()==null){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] non valorizzato"));
						return;	
					}
					String base64Fattura = nodeFattura.getTextContent();
					if(base64Fattura==null || "".equals(base64Fattura)){
						throw new Exception("Codifica Base64 non presente");
					}
					try{
						fattura = Base64.decode(base64Fattura);
					}catch(Exception e){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] decodifica base64 non riuscita: "+e.getMessage(),e));
						return;	
					}
				}
				
			}catch(Exception e){
				// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
				this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
						"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] non accessibile: "+e.getMessage(),e));
				return;	
			}
			
			// Il formato viene compreso durante la validazione sintattica
			Object formato = this.busta.getProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA);
			byte [] tmpFatturaP7M = null;
			if(formato==null){
				// La validazione sintattica non ha compreso il formato della fattura, per via di errori sulla nomenclatura del file.
				// La validazione del nome file e' disabilitata se si arriva a questo punto.
				// Si prova a comprendere il tipo di formato esaminando direttamente la fattura
				
				// provo a vedere se e' XML (XaDEs)
				try{
					if(it.gov.fatturapa.sdi.fatturapa.v1_0.utils.XMLUtils.isFattura(fattura)){
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA, SDICostanti.SDI_TIPO_FATTURA_XML);
						formato = SDICostanti.SDI_TIPO_FATTURA_XML;
					}
				}catch(Throwable e){}
				if(formato==null){
					try{
						if(it.gov.fatturapa.sdi.fatturapa.v1_1.utils.XMLUtils.isFattura(fattura)){
							this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA, SDICostanti.SDI_TIPO_FATTURA_XML);
							formato = SDICostanti.SDI_TIPO_FATTURA_XML;
						}
					}catch(Throwable e){}
				}
				
				// provo a vedere se e' un P7M (CaDES)
				if(formato==null){
					try{
						P7MInfo infoP7M = new P7MInfo(fattura, this.sdiValidazioneSemantica.getProtocolFactory().getLogger());
						tmpFatturaP7M = infoP7M.getXmlDecoded();
						if(tmpFatturaP7M!=null){
							this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_INVIO_FATTURA, SDICostanti.SDI_TIPO_FATTURA_P7M);
							this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_BASE64, infoP7M.isBase64Encoded()+"");
							formato = SDICostanti.SDI_TIPO_FATTURA_P7M;
						}
					}catch(Throwable e){}
				}
				
				if(formato==null){
					// formato non riconosciuto, non posso continuare ulteriormente
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO, 
									"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] decodifica non riuscita: formato non conosciuto"));
					return;	
				}
			}
			
			// Se la fattura e' un P7M per poterla validare devo estrarne il contenuto.
			if(((String)formato).equals(SDICostanti.SDI_TIPO_FATTURA_P7M) ){
				if(tmpFatturaP7M==null){
					try{
						P7MInfo infoP7M = new P7MInfo(fattura, this.sdiValidazioneSemantica.getProtocolFactory().getLogger());
						fattura = infoP7M.getXmlDecoded();
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_ARCHIVIO_BASE64, infoP7M.isBase64Encoded()+"");
					}catch(Throwable e){
						this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
								validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO, 
										"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_FILE+"] decodifica p7m non riuscita: "+e.getMessage(),e));
						return;	
					}
				}
				else{
					fattura = tmpFatturaP7M;
				}
			}

			SDIFatturaUtils.validazioneFattura(fattura,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory(),
					this.busta,this.msg,true,true,false);
			
			// Posizione (Se abilitato lo split verra' re-impostato correttamente questo valore)
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_POSIZIONE_FATTURA_PA, 1+"");
			
		}
	}
	
	private boolean _validazioneMetadati(byte[] metadati, SDIProperties sdiProperties, 
			Vector<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory protocolFactory) throws Exception{
	
		// validazione XSD file Metadati
		if(sdiProperties.isEnableValidazioneXsdMetadati()){
			try{
				AbstractValidatoreXSD validatore = 
						it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				validatore.valida(new ByteArrayInputStream(metadati));
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] contiene un file Metadati non valido rispetto allo schema XSD: "+e.getMessage(),e));
				return false;	
			}
		}
		
		// Lettura metadati
		MetadatiInvioFileType metadatiObject = null;
		try{
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
			metadatiObject = deserializer.readMetadatiInvioFileType(metadati);
			if(sdiProperties.isSaveFatturaInContext()){
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA_METADATI, metadatiObject);
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_FATTURA_METADATI_BYTES, metadati);
			}
		}catch(Exception e){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.RICEVI_FATTURE_RICHIESTA_ELEMENT_METADATI+"] contiene un file Metadati non valido: "+e.getMessage(),e));
			return false;	
		}
		
		// Metadati.IdentificativoSdI
		String identificativoSdILettoInValidazioneSintattica = this.busta.getProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI);
		if(identificativoSdILettoInValidazioneSintattica.equals(metadatiObject.getIdentificativoSdI().toString())==false){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.IDENTIFICATIVO_MESSAGGIO_NON_VALIDO,
							"Identificativo presente nel messaggio SdI ["+identificativoSdILettoInValidazioneSintattica+
							"] differente da quello presente nel file Metadati ["+metadatiObject.getIdentificativoSdI().toString()+"]",
							!sdiProperties.isEnableValidazioneCampiInterniMetadati()));
			if(sdiProperties.isEnableValidazioneCampiInterniMetadati()){
				return false;	
			}
		}
		
		
		// Metadati.NomeFile
		String nomeFileLettoInValidazioneSintattica = this.busta.getProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE);
		if(nomeFileLettoInValidazioneSintattica.equals(metadatiObject.getNomeFile())==false){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"NomeFile presente nel messaggio SdI ["+nomeFileLettoInValidazioneSintattica+
							"] differente da quello presente nel file Metadati ["+metadatiObject.getNomeFile()+"]",
							!sdiProperties.isEnableValidazioneCampiInterniMetadati()));
			if(sdiProperties.isEnableValidazioneCampiInterniMetadati()){
				return false;	
			}
		}
				
		// Metadati.CodiceDestinatario
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_CODICE_DESTINATARIO, metadatiObject.getCodiceDestinatario());
		
		// Metadati.FormatoFattura (validazione necessaria per riconoscere poi il tipo di fattura da parsare)
		String formatoFattura = metadatiObject.getFormato();
		if(!it.gov.fatturapa.sdi.fatturapa.v1_0.constants.FormatoTrasmissioneType.SDI10.name().equals(formatoFattura) &&
			!it.gov.fatturapa.sdi.fatturapa.v1_1.constants.FormatoTrasmissioneType.SDI11.name().equals(formatoFattura) ){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"FormatoFattura presente nei metadati ["+formatoFattura+
							"] non valido"));
			return false;	
		}
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_FORMATO_FATTURA_PA, formatoFattura);
		
		// Metadati.TentativiInvio
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_TENTATIVI_INVIO, metadatiObject.getTentativiInvio().toString());
		
		// Metadati.MessageId
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, metadatiObject.getMessageId());
		
		// Metadati.Note
		if(metadatiObject.getNote()!=null)
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, metadatiObject.getNote());
		
		return true;
	}
	
	
	
	
	
	
	
	
	/* ***** validaNotificaDecorrenzaTermini ***** */
	
	public void validaNotificaDecorrenzaTermini() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioRicezioneFatture.NOTIFICA_DECORRENZA_TERMINI_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.DT);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.DT);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	
	private void _validazioneSintattica_FileSdiType_richiesta(String rootElement,TipiMessaggi tipoMessaggio) throws Exception{
		
		if(rootElement.equals(this.sdiMessage.getLocalName())==false){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"LocalName ["+this.sdiMessage.getLocalName()+"] differente da quello atteso ["+rootElement+"]"));
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
	
			if(SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI.equals(child.getLocalName())){
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
			else if(SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE.equals(child.getLocalName())){
				if(nomeFile!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non valorizzato"));
					return;	
				}
				nomeFile = child.getTextContent();
			}
			else if(SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
				if(fileRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] presente piu' volte"));
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
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non presente"));
			return;	
		}
		if(fileRead==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non presente"));
			return;	
		}
		
		
		// nomeFile
		try{
			SDIValidatoreNomeFile.validaNomeFileMessaggi(nomeFile, tipoMessaggio);
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non valido: "+e.getMessage(),e,
							!this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()));
			if(this.sdiValidazioneSintattica.sdiProperties.isEnableValidazioneNomeFile()){
				return;	
			}
		}
		
		
		// setto i valori sdi nella busta
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI, identificativoSdI);
		this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE, nomeFile);
		
		
	}
	
	
	
	
	
	private void _validazioneSemantica_FileSdiType_richiesta(TipiMessaggi tipoMessaggio) throws Exception{
		
			
		byte[] xml = null;
		try{
			// la validazione sintattica garantisce la presenza
			QName qnameFile = new QName(SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE);
			Iterator<?> nodeFileIt = this.sdiMessage.getChildElements(qnameFile);
			SOAPElement nodeFile = (SOAPElement) nodeFileIt.next();
			
			Element xomReference = MTOMUtilities.getIfExistsXomReference(nodeFile);
			if(xomReference!=null){
				try{
					String cid = MTOMUtilities.getCidXomReference(xomReference);
					if(cid==null){
						throw new Exception("XomReference without cid reference");
					}
					AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
					xml = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
					if(xml==null || "".equals(xml)){
						throw new Exception("Contenuto non presente");
					}
				}catch(Exception e){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
					return;	
				}
			}else{
				if(nodeFile.getTextContent()==null){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non valorizzato"));
					return;	
				}
				String base64File = nodeFile.getTextContent();
				if(base64File==null || "".equals(base64File)){
					throw new Exception("Codifica Base64 non presente");
				}
				try{
					xml = Base64.decode(base64File);
				}catch(Exception e){
					// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] decodifica base64 non riuscita: "+e.getMessage(),e));
					return;	
				}
			}
			
			
		}catch(Exception e){
			// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
			this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non accessibile: "+e.getMessage(),e));
			return;	
		}
		
		
		switch (tipoMessaggio) {
			
		case DT:
			this._validazioneDT(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;

		default:
			break;
		}
		
			
	}
	
	private void _validazioneDT(byte[] xmlDoc, SDIProperties sdiProperties, 
			Vector<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory protocolFactory) throws Exception{
	
		String tipoXml = "Notifica di Decorrenza Termini";
		byte[] xml = xmlDoc;
		if(sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			xml = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), xml);
		}
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				AbstractValidatoreXSD validatore = 
						it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
				validatore.valida(new ByteArrayInputStream(xml));
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+e.getMessage(),e));
				return;	
			}
		}
		
		// Lettura metadati
		NotificaDecorrenzaTerminiType xmlObject = null;
		try{
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
			xmlObject = deserializer.readNotificaDecorrenzaTerminiType(xml);
			if(sdiProperties.isSaveMessaggiInContext()){
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
			}
		}catch(Exception e){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioRicezioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+e.getMessage(),e));
			return;	
		}
		
		// IdentificativoSdI
		if(xmlObject.getIdentificativoSdI()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, xmlObject.getIdentificativoSdI()+"");
		}
		
		// RiferimentoFattura
		if(xmlObject.getRiferimentoFattura()!=null){
			if(xmlObject.getRiferimentoFattura().getAnnoFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_ANNO, xmlObject.getRiferimentoFattura().getAnnoFattura()+"");
			}
			if(xmlObject.getRiferimentoFattura().getNumeroFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_NUMERO, xmlObject.getRiferimentoFattura().getNumeroFattura()+"");
			}
			if(xmlObject.getRiferimentoFattura().getPosizioneFattura()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_FATTURA_POSIZIONE, xmlObject.getRiferimentoFattura().getPosizioneFattura()+"");
			}
		}
		
		// NomeFile
		if(xmlObject.getNomeFile()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
		}
				
		// Descrizione
		if(xmlObject.getDescrizione()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESCRIZIONE, xmlObject.getDescrizione());
		}
		
		// MessageId
		if(xmlObject.getMessageId()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
		}
		
		// Note
		if(xmlObject.getNote()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
		}
		
	}
}
