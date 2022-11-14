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
package org.openspcoop2.protocol.sdi.validator;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2MessageFactory;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.message.soap.mtom.MTOMUtilities;
import org.openspcoop2.protocol.sdi.config.SDIProperties;
import org.openspcoop2.protocol.sdi.constants.SDICostanti;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioRiceviFile;
import org.openspcoop2.protocol.sdi.constants.SDICostantiServizioTrasmissioneFatture;
import org.openspcoop2.protocol.sdi.utils.SDICompatibilitaNamespaceErrati;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.Eccezione;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.CodiceErroreCooperazione;
import org.openspcoop2.protocol.sdk.validator.ProprietaValidazione;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.io.ZipUtilities;
import org.openspcoop2.utils.resources.FileSystemUtilities;
import org.openspcoop2.utils.xml.AbstractValidatoreXSD;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaImpossibilitaRecapitoType;
import it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaScartoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.AttestazioneTrasmissioneFatturaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.ErroreType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaDecorrenzaTerminiType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaEsitoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaMancataConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.NotificaScartoType;
import it.gov.fatturapa.sdi.messaggi.v1_0.RicevutaConsegnaType;
import it.gov.fatturapa.sdi.messaggi.v1_0.constants.TipiMessaggi;
import it.gov.fatturapa.sdi.ws.trasmissione.v1_0.types.utils.ProjectInfo;

/**
 * SDIValidatoreServizioTrasmissioneFatture
 * 
 * @author Andrea Poli (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class SDIValidatoreServizioTrasmissioneFatture {

	private SDIValidazioneSintattica sdiValidazioneSintattica;
	private SDIValidazioneSemantica sdiValidazioneSemantica;
	private ProprietaValidazione proprietaValidazioneSemantica;
	private OpenSPCoop2Message msg;
	private OpenSPCoop2MessageFactory messageFactory;
	private boolean isRichiesta;
	private SOAPElement sdiMessage;
	private String namespace;
	private Busta busta;
	
	public SDIValidatoreServizioTrasmissioneFatture(SDIValidazioneSintattica sdiValidazioneSintattica,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSintattica = sdiValidazioneSintattica;
		this.msg = msg;
		this.messageFactory = this.msg!=null ? this.msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
		this.busta = busta;
	}
	public SDIValidatoreServizioTrasmissioneFatture(SDIValidazioneSemantica sdiValidazioneSemantica, ProprietaValidazione proprietaValidazione,
			OpenSPCoop2Message msg,boolean isRichiesta,
			SOAPElement sdiMessage,Busta busta){
		this.sdiValidazioneSemantica = sdiValidazioneSemantica;
		this.proprietaValidazioneSemantica = proprietaValidazione;
		this.msg = msg;
		this.messageFactory = this.msg!=null ? this.msg.getFactory() : OpenSPCoop2MessageFactory.getDefaultMessageFactory();
		this.isRichiesta = isRichiesta;
		this.sdiMessage = sdiMessage;
		this.namespace = ProjectInfo.getInstance().getProjectNamespace();
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
	
	
	
	/* ***** validaRicevutaConsegna ***** */
	
	public void validaRicevutaConsegna() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.RICEVUTA_CONSEGNA_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.RC);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.RC);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	
	
	/* ***** validaNotificaMancataConsegna ***** */
	
	public void validaNotificaMancataConsegna() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.NOTIFICA_MANCATA_CONSEGNA_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.MC);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.MC);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	
	/* ***** validaNotificaScarto ***** */
	
	public void validaNotificaScarto()throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.NOTIFICA_SCARTO_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.NS);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.NS);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	
	/* ***** validaNotificaEsito ***** */
	
	public void validaNotificaEsito() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.NOTIFICA_ESITO_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.NE);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.NE);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		
	}
	
	/* ***** validaNotificaDecorrenzaTermini ***** */
	
	public void validaNotificaDecorrenzaTermini() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.NOTIFICA_DECORRENZA_TERMINI_RICHIESTA_ROOT_ELEMENT, 
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
	
	/* ***** validaAttestazioneTrasmissioneFattura ***** */
	
	public void validaAttestazioneTrasmissioneFattura() throws Exception{
		
		if(checkServiceNamespace()==false){
			return;
		}
		
		if(this.sdiValidazioneSintattica!=null){
			if(this.isRichiesta){
				this._validazioneSintattica_FileSdiType_richiesta(SDICostantiServizioTrasmissioneFatture.ATTESTAZIONE_TRASMISSIONE_FATTURA_RICHIESTA_ROOT_ELEMENT, 
						TipiMessaggi.AT);
			}
			else{
				throw new Exception("NotImplemented"); // l'engine non dovrebbe richiedere questo caso.
			}
		}
		else{
			if(this.isRichiesta){
				this._validazioneSemantica_FileSdiType_richiesta(TipiMessaggi.AT);
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
		
		List<SOAPElement> elementChilds = SoapUtils.getNotEmptyChildSOAPElement(this.sdiMessage);
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
	
			if(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_RICHIESTA_ELEMENT_IDENTIFICATIVO_SDI.equals(child.getLocalName())){
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
			else if(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE.equals(child.getLocalName())){
				if(nomeFile!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] presente piu' volte"));
					return;	
				}
				if(child.getTextContent()==null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non valorizzato"));
					return;	
				}
				nomeFile = child.getTextContent();
			}
			else if(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE.equals(child.getLocalName())){
				if(fileRead!=null){
					this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] presente piu' volte"));
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
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non presente"));
			return;	
		}
		if(fileRead==null){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non presente"));
			return;	
		}
		
		
		// nomeFile
		try{
			SDIValidatoreNomeFile.validaNomeFileMessaggi(nomeFile, tipoMessaggio);
		}catch(Exception e){
			this.sdiValidazioneSintattica.erroriValidazione.add(this.sdiValidazioneSintattica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_NOME_FILE+"] non valido: "+e.getMessage(),e,
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
			QName qnameFile = new QName(SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE);
			Iterator<?> nodeFileIt = this.sdiMessage.getChildElements(qnameFile);
			SOAPElement nodeFile = (SOAPElement) nodeFileIt.next();
			
			Element xomReference = MTOMUtilities.getIfExistsXomReference(this.messageFactory, nodeFile);
			if(xomReference!=null){
				try{
					String cid = MTOMUtilities.getCidXomReference(xomReference);
					if(cid==null){
						throw new Exception("XomReference without cid reference");
					}
					AttachmentPart ap = MTOMUtilities.getAttachmentPart(this.msg, cid);
					xml = Utilities.getAsByteArray(ap.getDataHandler().getInputStream());
					if(xml==null || xml.length<=0){
						throw new Exception("Contenuto non presente");
					}
				}catch(Exception e){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non valorizzato correttamente: "+e.getMessage(),e));
					return;	
				}
			}else{
				if(nodeFile.getTextContent()==null){
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non valorizzato"));
					return;	
				}
				String base64File = nodeFile.getTextContent();
				if(base64File==null || "".equals(base64File)){
					throw new Exception("Codifica Base64 non presente");
				}
				try{
					xml = Base64Utilities.decode(base64File);
				}catch(Exception e){
					// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] decodifica base64 non riuscita: "+e.getMessage(),e));
					return;	
				}
			}
			
			
		}catch(Exception e){
			// un errore non dovrebbe mai capitare, la validazione sintattica garantisce la presenza
			this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
					"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] non accessibile: "+e.getMessage(),e));
			return;	
		}
		
		
		switch (tipoMessaggio) {
		case RC:
			this._validazioneRC(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;
			
		case MC:
			this._validazioneMC(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;
			
		case NS:
			this._validazioneNS(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;
			
		case NE:
			this._validazioneNE(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;
			
		case DT:
			this._validazioneDT(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;
			
		case AT:
			this._validazioneAT(xml,this.sdiValidazioneSemantica.sdiProperties,this.sdiValidazioneSemantica.erroriValidazione,
					this.sdiValidazioneSemantica.validazioneUtils,this.sdiValidazioneSemantica.getProtocolFactory());
			break;

		default:
			break;
		}
		
		// Riporto nella notifica le informazioni della fattura precedentemente ricevuta
		String identificativoSdI = this.busta.getProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI);
		if(identificativoSdI!=null) {
			if(this.sdiValidazioneSemantica.sdiProperties.isEnable_fatturazioneAttiva_notifiche_enrichInfoFromFattura()) {
				try{
					this.sdiValidazioneSemantica.validazioneUtils.readInformazioniFatturaRiferita(this.busta, identificativoSdI, 
							SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE, 
							SDICostantiServizioRiceviFile.SDI_SERVIZIO_RICEVI_FILE_AZIONE_RICEVI_FILE,
							true, SDICostanti.SDI_FATTURAZIONE_ATTIVA,
							(this.proprietaValidazioneSemantica!=null) ? this.proprietaValidazioneSemantica.getTracceState() : null);
				}catch(Exception e){
					this.sdiValidazioneSemantica.getLog().error("Traccia di una precedente fattura inviata, con identificativo SDI ["+identificativoSdI+"], non rilevata: "+e.getMessage(),e);
					this.sdiValidazioneSemantica.erroriValidazione.add(this.sdiValidazioneSemantica.
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Traccia di una precedente fattura inviata, con identificativo SDI ["+identificativoSdI+"], non rilevata: "+e.getMessage(),e,
									true)); // info: emetto solamente un errore di livello info
				}
			}
		}
	}
	
	private void _validazioneRC(byte[] xmlDoc, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
		boolean fatturaB2B = false;
		String tipoXml = null;
		if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XMLUtils.isNotificaB2B(xmlDoc)) {
			fatturaB2B = true;
			tipoXml = "Ricevuta di Consegna";
		}
		else if(it.gov.fatturapa.sdi.messaggi.v1_0.utils.XMLUtils.isNotificaPA(xmlDoc, sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov())) {
			fatturaB2B = false;
			tipoXml = "Ricevuta di Consegna";
		}
		else {
			String namespace = null;
			Throwable eMalformato = null;
			try {
				org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(this.messageFactory);
				Document docXML = xmlUtils.newDocument(xmlDoc);
				Element elemXML = docXML.getDocumentElement();
				namespace = elemXML.getNamespaceURI();
			}catch(Throwable t) {
				eMalformato = t;
			}
			if(namespace!=null) {
				eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un namespace ("+namespace+") sconosciuto",
							forceEccezioneLivelloInfo));
			}
			else {
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un xml malformato: "+eMalformato.getMessage(),eMalformato,
								forceEccezioneLivelloInfo));
			}
			return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
		}
		
		byte[] xml = xmlDoc;
		if(!fatturaB2B && sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			xml = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), xml);
		}
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				if(fatturaB2B) {
					AbstractValidatoreXSD validatore = 
							it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
				else {
					AbstractValidatoreXSD validatore = 
							it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
		}
		
		// Lettura metadati
		
		if(fatturaB2B) {
			
			it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.RicevutaConsegnaType xmlObject = null;
			try{
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readRicevutaConsegnaType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
			
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI();
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, xmlObject.getIdentificativoSdI()+"");
			}
			
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
				
			// Hash
			if(xmlObject.getHash()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_HASH_IN_NOTIFICA, xmlObject.getHash());
			}
			
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
			
			// DataOraConsegna
			if(xmlObject.getDataOraConsegna()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_CONSEGNA, xmlObject.getDataOraConsegna().toString());
			}
			
			// Destinatario
			if(xmlObject.getDestinatario()!=null){
				if(xmlObject.getDestinatario().getCodice()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_CODICE, xmlObject.getDestinatario().getCodice());
				}
				if(xmlObject.getDestinatario().getDescrizione()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_DESCRIZIONE, xmlObject.getDestinatario().getDescrizione());
				}
			}
			
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI();
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, xmlObject.getRiferimentoArchivio().getIdentificativoSdI());
				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
			}
			
			// MessageId
			if(xmlObject.getMessageId()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
			}
			
			// Note
			if(xmlObject.getNote()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
			}
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
		}
		else {
			
			RicevutaConsegnaType xmlObject = null;
			try{
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readRicevutaConsegnaType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
		
			String idSdi = null;
			String idSdiRifArchivio = null;
		
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI()+"";
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
			}
		
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
				
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
		
			// DataOraConsegna
			if(xmlObject.getDataOraConsegna()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_CONSEGNA, xmlObject.getDataOraConsegna().toString());
			}
		
			// Destinatario
			if(xmlObject.getDestinatario()!=null){
				if(xmlObject.getDestinatario().getCodice()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_CODICE, xmlObject.getDestinatario().getCodice());
				}
				if(xmlObject.getDestinatario().getDescrizione()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_DESCRIZIONE, xmlObject.getDestinatario().getDescrizione());
				}
			}
		
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI()+"";
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, idSdiRifArchivio);
				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
			}
		
			// MessageId
			if(xmlObject.getMessageId()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
			}
		
			// Note
			if(xmlObject.getNote()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
			}
		
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
		}
	}
	
	
	private void _validazioneMC(byte[] xmlDoc, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
		
		boolean fatturaB2B = false;
		String tipoXml = null;
		if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XMLUtils.isNotificaB2B(xmlDoc)) {
			fatturaB2B = true;
			tipoXml = "Ricevuta di Impossibilità di Recapito";
		}
		else if(it.gov.fatturapa.sdi.messaggi.v1_0.utils.XMLUtils.isNotificaPA(xmlDoc, sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov())) {
			fatturaB2B = false;
			tipoXml = "Notifica di Mancata Consegna";
		}
		else {
			String namespace = null;
			Throwable eMalformato = null;
			try {
				org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(this.messageFactory);
				Document docXML = xmlUtils.newDocument(xmlDoc);
				Element elemXML = docXML.getDocumentElement();
				namespace = elemXML.getNamespaceURI();
			}catch(Throwable t) {
				eMalformato = t;
			}
			if(namespace!=null) {
				eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un namespace ("+namespace+") sconosciuto",
							forceEccezioneLivelloInfo));
			}
			else {
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un xml malformato: "+eMalformato.getMessage(),eMalformato,
								forceEccezioneLivelloInfo));
			}
			return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti	
		}
		
		
		byte[] xml = xmlDoc;
		if(!fatturaB2B && sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			xml = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), xml);
		}
		
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				if(fatturaB2B) {
					AbstractValidatoreXSD validatore = 
							it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
				else {
					AbstractValidatoreXSD validatore = 
							it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
		}
		
		// Lettura metadati
		
		if(fatturaB2B) {
			
			RicevutaImpossibilitaRecapitoType xmlObject = null;
			try{
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readRicevutaImpossibilitaRecapitoType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+e.getMessage(),e,
								forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
					
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI();
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, xmlObject.getIdentificativoSdI());
			}
					
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
			
			// Hash
			if(xmlObject.getHash()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_HASH_IN_NOTIFICA, xmlObject.getHash());
			}
					
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
			
			// DataMessaADisposizione
			if(xmlObject.getDataMessaADisposizione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_MESSA_A_DISPOSIZIONE, xmlObject.getDataMessaADisposizione().toString());
			}
	
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI();
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, xmlObject.getRiferimentoArchivio().getIdentificativoSdI());				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
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
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
			
		}
		else {
			
			NotificaMancataConsegnaType xmlObject = null;
			try{
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readNotificaMancataConsegnaType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
					
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI()+"";
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
			}
					
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
					
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
	
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI()+"";
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, idSdiRifArchivio);
				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
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
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
			
		}
	}

	
	
	private void _validazioneNS(byte[] xmlDoc, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
				
		boolean fatturaB2B = false;
		String tipoXml = null;
		if(it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XMLUtils.isNotificaB2B(xmlDoc)) {
			fatturaB2B = true;
			tipoXml = "Ricevuta di Scarto";
		}
		else if(it.gov.fatturapa.sdi.messaggi.v1_0.utils.XMLUtils.isNotificaPA(xmlDoc, sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov())) {
			fatturaB2B = false;
			tipoXml = "Notifica di Scarto";
		}
		else {
			String namespace = null;
			Throwable eMalformato = null;
			try {
				org.openspcoop2.message.xml.MessageXMLUtils xmlUtils = org.openspcoop2.message.xml.MessageXMLUtils.getInstance(this.messageFactory);
				Document docXML = xmlUtils.newDocument(xmlDoc);
				Element elemXML = docXML.getDocumentElement();
				namespace = elemXML.getNamespaceURI();
			}catch(Throwable t) {
				eMalformato = t;
			}
			if(namespace!=null) {
				eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un namespace ("+namespace+") sconosciuto",
							forceEccezioneLivelloInfo));
			}
			else {
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un xml malformato: "+eMalformato.getMessage(),eMalformato,
								forceEccezioneLivelloInfo));
			}
			return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
		}
		

		byte[] xml = xmlDoc;
		if(!fatturaB2B && sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
			xml = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), xml);
		}
		
		
		// validazione XSD file
		if(sdiProperties.isEnableValidazioneXsdMessaggi()){
			try{
				if(fatturaB2B) {
					AbstractValidatoreXSD validatore = 
							it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
				else {
					AbstractValidatoreXSD validatore = 
							it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xml));
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
		}
		
		// Lettura metadati
		
		if(fatturaB2B) {
			
			RicevutaScartoType xmlObject = null;
			try{
				it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readRicevutaScartoType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+e.getMessage(),e,
								forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
					
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI();
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, xmlObject.getIdentificativoSdI());
			}
					
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
			
			// Hash
			if(xmlObject.getHash()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_HASH_IN_NOTIFICA, xmlObject.getHash());
			}
			
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
	
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI();
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, xmlObject.getRiferimentoArchivio().getIdentificativoSdI());
				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
			}
	
			// ListaErrori
			if(xmlObject.getListaErrori()!=null && xmlObject.getListaErrori().sizeErroreList()>0){
				StringBuilder bf = new StringBuilder();
				bf.append(xmlObject.getListaErrori().sizeErroreList()+" errori rilevati: ");
				for (int i = 0; i < xmlObject.getListaErrori().sizeErroreList(); i++) {
					it.gov.agenziaentrate.ivaservizi.docs.xsd.fattura.messaggi.v1_0.ErroreType errore = xmlObject.getListaErrori().getErrore(i);
					bf.append("\n\t- ["+errore.getCodice()+"] "+errore.getDescrizione());
					if(errore.getSuggerimento()!=null) {
						bf.append(" (").append(errore.getSuggerimento()).append(")");
					}
				}
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ERRORI, bf.toString());
			}
			
			// MessageId
			if(xmlObject.getMessageId()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
			}
			
			// Note
			if(xmlObject.getNote()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
			}
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
			
		}
		else {
			
			NotificaScartoType xmlObject = null;
			try{
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readNotificaScartoType(xml);
				if(sdiProperties.isSaveMessaggiInContext()){
					this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
				}
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
					
			// IdentificativoSdI
			if(xmlObject.getIdentificativoSdI()!=null){
				idSdi = xmlObject.getIdentificativoSdI()+"";
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
			}
					
			// NomeFile
			if(xmlObject.getNomeFile()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
			}
					
			// DataOraRicezione
			if(xmlObject.getDataOraRicezione()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
			}
	
			// RiferimentoArchivio
			if(xmlObject.getRiferimentoArchivio()!=null){
				if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
					idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI()+"";
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, idSdiRifArchivio);
				}
				if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
				}
			}
	
			// ListaErrori
			if(xmlObject.getListaErrori()!=null && xmlObject.getListaErrori().sizeErroreList()>0){
				StringBuilder bf = new StringBuilder();
				bf.append(xmlObject.getListaErrori().sizeErroreList()+" errori rilevati: ");
				for (int i = 0; i < xmlObject.getListaErrori().sizeErroreList(); i++) {
					ErroreType errore = xmlObject.getListaErrori().getErrore(i);
					bf.append("\n\t- ["+errore.getCodice()+"] "+errore.getDescrizione());
				}
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ERRORI, bf.toString());
			}
			
			// MessageId
			if(xmlObject.getMessageId()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
			}
			
			// Note
			if(xmlObject.getNote()!=null){
				this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
			}
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
			
		}
	}
	
	
	private void _validazioneNE(byte[] xmlDoc, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
		
		String tipoXml = "Notifica di Esito (Cedente)";
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
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
			}
		}
		
		// Lettura metadati
		NotificaEsitoType xmlObject = null;
		try{
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
			xmlObject = deserializer.readNotificaEsitoType(xml);
			if(sdiProperties.isSaveMessaggiInContext()){
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
			}
		}catch(Exception e){
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
									e.getMessage(),e, forceEccezioneLivelloInfo));
			return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
		}
		
		String idSdi = null;
		String idSdiRifArchivio = null;
				
		// IdentificativoSdI
		if(xmlObject.getIdentificativoSdI()!=null){
			idSdi = xmlObject.getIdentificativoSdI()+"";
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
		}
				
		// NomeFile
		if(xmlObject.getNomeFile()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
		}
				
		// EsitoCommittente
		if(xmlObject.getEsitoCommittente()!=null){
			it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer serializer =
					new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbSerializer();
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_ESITO_COMMITTENTE, serializer.toString(xmlObject.getEsitoCommittente()));
		}
		
		// MessageId
		if(xmlObject.getMessageId()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
		}
		
		// Note
		if(xmlObject.getNote()!=null){
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
		}
	
		validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
	}
	
	private void _validazioneDT(byte[] xmlDoc, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
		
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
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido rispetto allo schema XSD: "+
										e.getMessage(),e, forceEccezioneLivelloInfo));
				return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
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
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml+" non valido: "+
									e.getMessage(),e, forceEccezioneLivelloInfo));
			return;	// esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
		}
		
		String idSdi = null;
		String idSdiRifArchivio = null;
				
		// IdentificativoSdI
		if(xmlObject.getIdentificativoSdI()!=null){
			idSdi = xmlObject.getIdentificativoSdI()+"";
			this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
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
		
		validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
	}
	
	
	
	private void _validazioneAT(byte[] zip, SDIProperties sdiProperties, 
			List<Eccezione> eccezioniValidazione, SDIValidazioneUtils validazioneUtils, IProtocolFactory<?> protocolFactory) throws Exception{
	
		boolean forceEccezioneLivelloInfo = false;
		if(sdiProperties.isEnableAccessoMessaggi() == false) {
			return;
		}
		else if(sdiProperties.isEnableAccessoMessaggiWarningMode()) {
			forceEccezioneLivelloInfo = true;
		}
		
		
		String tipoXml = "Attestazione di avvenuta trasmissione con impossibilità di recapito";
		boolean consegnaAttestato = sdiProperties.isNotificaATConsegnaSoloAttestato();
		
		// zip
		File tmpZipFile = null;
		try{
			tmpZipFile = File.createTempFile("AttestazioneTrasmissione", ".zip");
			FileSystemUtilities.writeFile(tmpZipFile, zip);
		}catch(Exception e){
			this.sdiValidazioneSemantica.getProtocolFactory().getLogger().error("Operazione di unzip non riuscita: "+e.getMessage(),e);
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml
							+" non valido; è avvenuto un erorre durante il salvataggio dell'archivio zip: "+e.getMessage(),e,forceEccezioneLivelloInfo));
			try{
				if(tmpZipFile!=null){
					tmpZipFile.delete();
				}
			}catch(Exception eClose){
				// ignore
			}
			return; // esco anche in caso di forceEccezioneLivelloInfo poiche' i messaggi non sono ben formati e non ha senso andare avanti
		}
		
		
		// unzip
		File tmpZipFileDir = null;
		byte [] xmlATDoc = null;
		//byte [] xmlFattura = null;
		boolean xmlFatturaRead = false;
		try{
			tmpZipFileDir = new File(tmpZipFile.getAbsolutePath()+".unzip");
			//System.out.println("AAAAA ["+tmpZipFile.getAbsolutePath()+"] ["+tmpZipFileDir.getAbsolutePath()+"]");
			ZipUtilities.unzipFile(tmpZipFile.getAbsolutePath(), tmpZipFileDir.getAbsolutePath());
			File [] xmls = tmpZipFileDir.listFiles();
			if(xmls==null || xmls.length<=0){
				throw new Exception("Archivio decompresso non contiene files");
			}
			if(xmls.length!=2){
				throw new Exception("Archivio decompresso contiene un numero di files ("+xmls.length+") differente dal numero atteso (2)");
			}
			for (int i = 0; i < xmls.length; i++) {
				//System.out.println("i["+xmls[i].getAbsolutePath()+"]");
				if(xmls[i].getName().contains("_AT_")){
					if(xmlATDoc!=null){
						throw new Exception("Archivio decompresso non contiene l'xml della fattura non recapitata");
					}
					xmlATDoc = FileSystemUtilities.readBytesFromFile(xmls[i]);
				}
				else{
					if(xmlFatturaRead){
						throw new Exception("Archivio decompresso non contiene l'xml di attestazione di avvenuta trasmissione con impossibilità di recapito");
					}
					xmlFatturaRead = true;
					//xmlFattura = FileSystemUtilities.readBytesFromFile(xmls[i]);
				}
			}
		}catch(Exception e){
			this.sdiValidazioneSemantica.getProtocolFactory().getLogger().error("Operazione di unzip non riuscita: "+e.getMessage(),e);
			eccezioniValidazione.add(
					validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
							"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+tipoXml
							+" non valido; è avvenuto un erorre durante la lettura dell'archivio zip: "+e.getMessage(),e,
							!consegnaAttestato));
			if(consegnaAttestato){
				return; 
			}
		}finally{
			try{
				if(tmpZipFile!=null){
					tmpZipFile.delete();
				}
			}catch(Exception e){
				// ignore
			}
			try{
				if(tmpZipFileDir!=null){
					FileSystemUtilities.deleteDir(tmpZipFileDir);
				}
			}catch(Exception e){
				// ignore
			}
		}
		

		// validazione XSD file dell'attestato
		byte[] xmlAT = null;
		if(xmlATDoc!=null){
			xmlAT = xmlATDoc;
			if(sdiProperties.isEnableValidazioneMessaggiCompatibilitaNamespaceSenzaGov()){
				xmlAT = SDICompatibilitaNamespaceErrati.convertiXmlNamespaceSenzaGov(protocolFactory.getLogger(), xmlAT);
			}
			if(sdiProperties.isEnableValidazioneXsdMessaggi()){
				try{
					AbstractValidatoreXSD validatore = 
							it.gov.fatturapa.sdi.messaggi.v1_0.utils.XSDValidatorWithSignature.getOpenSPCoop2MessageXSDValidator(protocolFactory.getLogger());
					validatore.valida(new ByteArrayInputStream(xmlAT));
				}catch(Exception e){
					eccezioniValidazione.add(
							validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
									"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+
											tipoXml+" non valido rispetto allo schema XSD: "+e.getMessage(),e,
											!consegnaAttestato));
					if(consegnaAttestato){
						return;
					}
				}
			}
		}
		
		
		// Lettura attestazione
		AttestazioneTrasmissioneFatturaType xmlObject = null;
		if(xmlAT!=null){		
			try{
				it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer deserializer =
						new it.gov.fatturapa.sdi.messaggi.v1_0.utils.serializer.JaxbDeserializer();
				xmlObject = deserializer.readAttestazioneTrasmissioneFatturaType(xmlAT);
			}catch(Exception e){
				eccezioniValidazione.add(
						validazioneUtils.newEccezioneValidazione(CodiceErroreCooperazione.FORMATO_CORPO_NON_CORRETTO,
								"Elemento ["+SDICostantiServizioTrasmissioneFatture.FILE_SDI_TYPE_CONSEGNA_RICHIESTA_ELEMENT_FILE+"] contiene un file "+
										tipoXml+" non valido: "+e.getMessage(),e,
										!consegnaAttestato));
				if(consegnaAttestato){
					return;
				}
			}
			
			String idSdi = null;
			String idSdiRifArchivio = null;
						
			if(xmlObject!=null) {
			
				// IdentificativoSdI
				if(xmlObject.getIdentificativoSdI()!=null){
					idSdi = xmlObject.getIdentificativoSdI()+"";
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_IDENTIFICATIVO_SDI_FATTURA, idSdi);
				}
				
				// NomeFile
				if(xmlObject.getNomeFile()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOME_FILE_IN_NOTIFICA, xmlObject.getNomeFile());
				}
				
				// DataOraRicezione
				if(xmlObject.getDataOraRicezione()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DATA_ORA_RICEZIONE, xmlObject.getDataOraRicezione().toString());
				}
	
				// RiferimentoArchivio
				if(xmlObject.getRiferimentoArchivio()!=null){
					if(xmlObject.getRiferimentoArchivio().getIdentificativoSdI()!=null){
						idSdiRifArchivio = xmlObject.getRiferimentoArchivio().getIdentificativoSdI()+"";
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_IDENTIFICATIVO_SDI, idSdiRifArchivio);
					}
					if(xmlObject.getRiferimentoArchivio().getNomeFile()!=null){
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_RIFERIMENTO_ARCHIVIO_NOME_FILE, xmlObject.getRiferimentoArchivio().getNomeFile());
					}
				}
			
				// Destinatario
				if(xmlObject.getDestinatario()!=null){
					if(xmlObject.getDestinatario().getCodice()!=null){
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_CODICE, xmlObject.getDestinatario().getCodice());
					}
					if(xmlObject.getDestinatario().getDescrizione()!=null){
						this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_DESTINATARIO_DESCRIZIONE, xmlObject.getDestinatario().getDescrizione());
					}
				}
				
				// MessageId
				if(xmlObject.getMessageId()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_MESSAGE_ID, xmlObject.getMessageId());
				}
				
				// Note
				if(xmlObject.getNote()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_NOTE, xmlObject.getNote());
				}
				
				// HashFileOriginale
				if(xmlObject.getHashFileOriginale()!=null){
					this.busta.addProperty(SDICostanti.SDI_BUSTA_EXT_HashFileOriginale, xmlObject.getHashFileOriginale());
				}
			}
			
			validazioneUtils.addHeaderIdentificativoSdiMessaggio(this.msg, idSdi, idSdiRifArchivio);
		}
		
		
		// save context
		if(consegnaAttestato==false){
			this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_ZIP, zip);
		}
		else{
			if(xmlObject!=null && sdiProperties.isSaveMessaggiInContext()){	
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_MESSAGGIO_SERVIZIO_SDI, xmlObject);
			}
			else{
				this.msg.addContextProperty(SDICostanti.SDI_MESSAGE_CONTEXT_AT_ARCHIVIO_XML, xmlATDoc);
			}
		}
		
	}
}
