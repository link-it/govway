/*
 * GovWay - A customizable API Gateway 
 * http://www.govway.org
 *
 * from the Link.it OpenSPCoop project codebase
 * 
 * Copyright (c) 2005-2019 Link.it srl (http://link.it). 
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

package org.openspcoop2.protocol.engine.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.protocol.engine.FunctionContextsCustom;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.UrlMapping;
import org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.json.JsonPathExpressionEngine;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.transport.Credential;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.slf4j.Logger;
import org.w3c.dom.Element;


/**
 * Informazioni Servizi Porta
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class InformazioniServizioURLMapping {

	/* ********* STATIC ********* */
	
	private static final String TIPO_MITTENTE = "identificazione-tipo-mittente";
	private static final String NOME_MITTENTE = "identificazione-mittente";
		
	private static final String INFORMAZIONI_PROTOCOLLO = "identificazione-informazioni-protocollo";
	
	private static final String ID_PROTOCOLLO = "identificazione-id-protocollo";
	
	private static final String GENERAZIONE_LISTA_TRASMISSIONI = "generazione-lista-trasmissioni";
		
	
	private static MappingProperties getMappingProperties(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		
		Openspcoop2 manifest = protocolFactory.getManifest();
		if(InformazioniServizioURLMapping.mappingPropertiesTable.containsKey(manifest.getProtocol().getName())==false){
			InformazioniServizioURLMapping.initMappingProperties(protocolFactory);
		}
		return InformazioniServizioURLMapping.mappingPropertiesTable.get(manifest.getProtocol().getName());
		
	}
	
	private static HashMap<String, MappingProperties> mappingPropertiesTable = new HashMap<String, MappingProperties>();
	public static synchronized void initMappingProperties(IProtocolFactory<?> protocolFactory) throws ProtocolException{
		
		try{
			Openspcoop2 manifest = protocolFactory.getManifest();
			if(InformazioniServizioURLMapping.mappingPropertiesTable.containsKey(manifest.getProtocol().getName())==false){
			
				UrlMapping urlMapping = manifest.getUrlMapping();
				if(UrlMappingSourceType.XML.equals(urlMapping.getType())){
					//TODO IMPLEMENTARE VERSIONE XML
					throw new ProtocolException("Not implemented");
				}
				else if(UrlMappingSourceType.PROPERTIES.equals(urlMapping.getType())){
					File f = new File(urlMapping.getFile());
					InputStream is = null;
					try{
						if(f.exists()){
							is = new FileInputStream(f);
						}
						else{
							is = protocolFactory.getClass().getResourceAsStream(urlMapping.getFile());
						}
						MappingProperties m = new MappingProperties(urlMapping.getFile(), is, protocolFactory.getLogger());
						InformazioniServizioURLMapping.mappingPropertiesTable.put(manifest.getProtocol().getName(), m);
					}finally{
						try{
							if(is!=null){
								is.close();
							}
						}catch(Exception eClose){}
					}
				}
				else{
					throw new ProtocolException("["+urlMapping.getType().getValue()+"] Not supported");
				}
			
			}
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}
	}
	
	
	
	
	
	
	
	/* *********** INSTANCE ********* */
	
	// Dati ricevuti dal costruttore
	private OpenSPCoop2Message msg;
	private URLProtocolContext urlProtocolContext;
	@SuppressWarnings("unused")
	private Logger log;
	
	// Gestore delle properties
	private MappingProperties mp;
	
	// Id Mapping
	private String idMapping; // la keyword che indica il mapping per quanto concerne la url invocata
	
	// Mittente
	private MappingInfo tipoMittente;
	private MappingInfo mittente;
	
	// Informazioni di protocollo
	private MappingInfo infoProtocollo;
	
	// ID di protocollo
	private MappingInfo idProtocollo;
	
	// Indicazione se generare la lista trasmissione
	private boolean generateListaTrasmissione = false;
		

	public InformazioniServizioURLMapping(OpenSPCoop2Message msg,IProtocolFactory<?> protocolFactory,
			URLProtocolContext urlProtocolContext, Logger log, IDService idService, 
			FunctionContextsCustom customContexts) throws ProtocolException{
				
		this.mp = InformazioniServizioURLMapping.getMappingProperties(protocolFactory);
				
		this.msg = msg;
		this.urlProtocolContext = urlProtocolContext;
		this.log = log;
		
		// Id Mapping
		this.idMapping = resolveMappingName(urlProtocolContext, idService, customContexts);

		// Mittente
		this.tipoMittente = this.getMappingInfo(TIPO_MITTENTE);
		this.mittente = this.getMappingInfo(NOME_MITTENTE);
		
		// Informazioni di protocollo
		this.infoProtocollo = this.getMappingInfo(INFORMAZIONI_PROTOCOLLO);
		
		// ID di protocollo
		this.idProtocollo = this.getMappingInfo(ID_PROTOCOLLO);

		// ListaTrasmissione
		String generazioneListaTrasmissioniTmp = this.mp.getValue(this.msg.getProtocolName(), this.idMapping, GENERAZIONE_LISTA_TRASMISSIONI);
		if(generazioneListaTrasmissioniTmp!=null){
			this.generateListaTrasmissione = Boolean.parseBoolean(generazioneListaTrasmissioniTmp);
		}
		
	}

	@Override
	public String toString(){
		StringBuffer bf = new StringBuffer();
		
		bf.append("MappingId(");
		bf.append(this.idMapping);
		bf.append(") ");
				
		bf.append("TipoMittente(");
		if(this.tipoMittente!=null)
			bf.append(this.tipoMittente.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("Mittente(");
		if(this.mittente!=null)
			bf.append(this.mittente.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("InformazioniProtocollo(");
		if(this.infoProtocollo!=null)
			bf.append(this.infoProtocollo.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("IDProtocollo(");
		if(this.idProtocollo!=null)
			bf.append(this.idProtocollo.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("GenerazioneListaTrasmissioni(");
		bf.append(this.generateListaTrasmissione);
		bf.append(") ");
		
		return bf.toString();
	}
	
	
	
	
	
	
	/* ************ UTILITIES ********************** */ 
	
	/**
	 * Recupera il nome del Mapping configurato per la URL invocata.
	 * 
	 * @return Nome del mapping configurato per gestire la URL invocata.
	 * @throws IOException Se ci sono problemi di accesso al file di configurazione.
	 * @throws ProtocolException Se la configurazione non e' corretta.
	 */
	
	private String resolveMappingName(URLProtocolContext urlProtocolContext, IDService idService, FunctionContextsCustom customContexts) throws ProtocolException {
			
		// Recupero il nome del mapping per la url invocata
		String mappingName = this.mp.getMappingName(this.msg.getProtocolName(), urlProtocolContext.getRequestURI(), idService, customContexts);

		return mappingName;
	}
	
	/**
	 * Recupera la modalita' di identificazione di una properieta'
	 */
	private MappingInfo getMappingInfo(String proprieta) throws ProtocolException {
		
		MappingInfo mappingInfo = new MappingInfo();
		
		mappingInfo.setModalitaIdentificazione(this.mp.getModalita(this.msg.getProtocolName(), this.idMapping, proprieta));
		
		mappingInfo.setName(this.mp.getName(this.msg.getProtocolName(), this.idMapping, proprieta));
		
		mappingInfo.setValue(this.mp.getValue(this.msg.getProtocolName(), this.idMapping, proprieta));
		
		mappingInfo.setPattern(this.mp.getPattern(this.msg.getProtocolName(), this.idMapping, proprieta));
		
		mappingInfo.setAnonymous(this.mp.getAnonymous(this.msg.getProtocolName(), this.idMapping, proprieta));
		
		if(ID_PROTOCOLLO.equals(proprieta) || INFORMAZIONI_PROTOCOLLO.equals(proprieta)){
			if( !ModalitaIdentificazione.STATIC.equals(mappingInfo.getModalitaIdentificazione()) 
					&&
				!ModalitaIdentificazione.PLUGIN_BASED.equals(mappingInfo.getModalitaIdentificazione()) 	){
				throw new ProtocolException("La Modalita' '"+mappingInfo.getModalitaIdentificazione().toString()+"', indicata per la proprieta' ["+proprieta+"], non puo' essere utilizzata");
			}
			return mappingInfo;
		}
		
		if(ModalitaIdentificazione.STATIC.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getValue()==null){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.STATIC.toString()+"', indicata per la proprieta' ["+proprieta+"], richiede la definizione di una omonima proprieta' con suffisso '.value'");
			}
		}
		
		if(ModalitaIdentificazione.URL_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getPattern()==null){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.URL_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], richiede la definizione di una omonima proprieta' con suffisso '.pattern'");
			}
		}
		
		if(ModalitaIdentificazione.CONTENT_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getPattern()==null){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.CONTENT_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], richiede la definizione di una omonima proprieta' con suffisso '.pattern'");
			}
		}
		
		if(ModalitaIdentificazione.HEADER_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getName()==null){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.CONTENT_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], richiede la definizione di una omonima proprieta' con suffisso '.name'");
			}
		}

		if(ModalitaIdentificazione.IDENTITY_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(!NOME_MITTENTE.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.IDENTITY_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], puo' essere utilizzata solamente per identificare il nome del mittente");
			}
		}
		
		
		return mappingInfo;
		
	}
	
	public boolean existsUrlBasedIdentificationMode(){
		
		if(this.tipoMittente!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.tipoMittente.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.mittente!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.mittente.getModalitaIdentificazione())){
				return true;
			}
		}
	
		return false;
	}
	
	public boolean existsContentBasedIdentificationMode(){
		
		if(this.tipoMittente!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.tipoMittente.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.mittente!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.mittente.getModalitaIdentificazione())){
				return true;
			}
		}
	
		return false;
	}
	
	public boolean existsIdentityBasedIdentificationMode(){
		
		if(this.mittente!=null){
			if(ModalitaIdentificazione.IDENTITY_BASED.equals(this.mittente.getModalitaIdentificazione())){
				return true;
			}
		}
		
		return false;
	}	
	
	public boolean isStaticBasedIdentificationMode_InfoProtocol(){
		
		if(this.infoProtocollo!=null){
			if(ModalitaIdentificazione.STATIC.equals(this.infoProtocollo.getModalitaIdentificazione())){
				return true;
			}
		}
	
		return false;
	}
	
	public boolean isStaticBasedIdentificationMode_IdProtocol(){
		
		if(this.idProtocollo!=null){
			if(ModalitaIdentificazione.STATIC.equals(this.idProtocollo.getModalitaIdentificazione())){
				return true;
			}
		}
	
		return false;
	}
	
	public boolean isGenerateListaTrasmissione() {
		return this.generateListaTrasmissione;
	}
	
	
	
	/* ***** INIT BUSTA **** */
	
	public void refreshDati(IDSoggetto idMittente, Credential identity, IDSoggetto headerIntegrazioneRichiestaSoggettoMittente) throws ProtocolException{
		
		String urlInvocazione = null;
		
		DynamicNamespaceContext dnc = null;
		AbstractXPathExpressionEngine xPathEngine = null;
		Element element = null;
		String elementJson = null;
		
		if(this.existsUrlBasedIdentificationMode()){
			urlInvocazione = this.urlProtocolContext.getUrlInvocazione_formBased();
		}
		if(this.existsContentBasedIdentificationMode()){
			try{
				if(ServiceBinding.SOAP.equals(this.msg.getServiceBinding())){
					element = this.msg.castAsSoap().getSOAPPart().getEnvelope();
				}
				else{
					if(MessageType.XML.equals(this.msg.getMessageType())){
						element = this.msg.castAsRestXml().getContent();
					}
					else if(MessageType.JSON.equals(this.msg.getMessageType())){
						elementJson = this.msg.castAsRestJson().getContent();
					}
					else{
						throw new DriverConfigurazioneNotFound("Identificazione 'contentBased' non supportata per il message-type '"+this.msg.getMessageType()+"'");
					}
				}
				if(element!=null) {
					dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(element);
					xPathEngine = new org.openspcoop2.message.xml.XPathExpressionEngine();
				}
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		
		// TipoMittente
		String tipoMittente = null;
		try{
			tipoMittente = this.readValue(this.tipoMittente, TIPO_MITTENTE, identity, urlInvocazione,
					element, dnc, xPathEngine, 
					elementJson, 
					headerIntegrazioneRichiestaSoggettoMittente);
		}catch(ProtocolException e){
			if(tipoMittente==null && !ModalitaIdentificazione.PLUGIN_BASED.equals(this.tipoMittente.getModalitaIdentificazione())){
				if(this.tipoMittente.getAnonymous()==null){
					throw e;
				}
				else{
					tipoMittente = this.tipoMittente.getAnonymous();
				}
			}
		}
		if(tipoMittente!=null){
			idMittente.setTipo(tipoMittente);
		}
		// Mittente
		String mittente = null;
		try{
			mittente = this.readValue(this.mittente, NOME_MITTENTE, identity, urlInvocazione, 
					element, dnc, xPathEngine, 
					elementJson, 
					headerIntegrazioneRichiestaSoggettoMittente);
		}catch(ProtocolException e){
			if(mittente==null && !ModalitaIdentificazione.PLUGIN_BASED.equals(this.mittente.getModalitaIdentificazione())){
				if(this.mittente.getAnonymous()==null){
					throw e;
				}
				else{
					mittente = this.mittente.getAnonymous();
				}
			}
		}
		if(mittente!=null){
			idMittente.setNome(mittente);
		}
			
	}
	
	
	private String readValue(MappingInfo mappingInfo,String oggetto,
			Credential identity,String urlInvocazione,
			Element element, DynamicNamespaceContext dnc ,AbstractXPathExpressionEngine xPathEngine,
			String elementJson, 
			IDSoggetto headerIntegrazioneRichiestaSoggettoMittente) throws ProtocolException{
		
		if(ModalitaIdentificazione.STATIC.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getValue()==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.STATIC.toString()+" non riuscita: valore nell'omonima proprieta' con suffisso '.value' non fornito");
			}else{
				return mappingInfo.getValue();
			}
		}
		
		else if(ModalitaIdentificazione.PLUGIN_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			return null; // delegata al plugin
		}
		
		else if(ModalitaIdentificazione.URL_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			try{
				return RegularExpressionEngine.getStringMatchPattern(urlInvocazione, mappingInfo.getPattern());
			}catch(Exception e){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.URL_BASED.toString()+" non riuscita: "+e.getMessage(),e);
			}
		}
		
		else if(ModalitaIdentificazione.CONTENT_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			try{
				if(element!=null) {
					return AbstractXPathExpressionEngine.extractAndConvertResultAsString(element,dnc, xPathEngine, mappingInfo.getPattern(),  this.log);
				}
				else {
					return JsonPathExpressionEngine.extractAndConvertResultAsString(elementJson, mappingInfo.getPattern(), this.log);
				}
			}catch(Exception e){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.CONTENT_BASED.toString()+" non riuscita: "+e.getMessage(),e);
			}
		}
		
		else if(ModalitaIdentificazione.INPUT_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(headerIntegrazioneRichiestaSoggettoMittente==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non riuscita: non esistono informazioni di integrazione");
			}
			else{
				if(TIPO_MITTENTE.equals(oggetto)){
					if(headerIntegrazioneRichiestaSoggettoMittente.getTipo()==null){
						throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non riuscita: non esistono informazioni di integrazione (TipoMittente)");
					}
					else{
						return headerIntegrazioneRichiestaSoggettoMittente.getTipo();
					}
				}
				else if(NOME_MITTENTE.equals(oggetto)){
					if(headerIntegrazioneRichiestaSoggettoMittente.getNome()==null){
						throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non riuscita: non esistono informazioni di integrazione (Mittente)");
					}
					else{
						return headerIntegrazioneRichiestaSoggettoMittente.getNome();
					}
				}
				else{
					throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non supportata per l'oggetto "+oggetto);
				}
			}
		}
		
		else if(ModalitaIdentificazione.HEADER_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(mappingInfo.getName()==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non riuscita: nome header nell'omonima proprieta' con suffisso '.name' non fornito");
			}
			if(this.urlProtocolContext.getParametersTrasporto().containsKey(mappingInfo.getName())==false){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.HEADER_BASED.toString()+" non riuscita: header di trasporto con nome ["+mappingInfo.getName()+"] non trovato");
			}
			return this.urlProtocolContext.getParametersTrasporto().getProperty(mappingInfo.getName());
		}
		
		else if(ModalitaIdentificazione.IDENTITY_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(identity==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.IDENTITY_BASED.toString()+" non riuscita: Identity non fornita");
			}
			String id = null;
			if(identity.getPrincipal()!=null){
				id = identity.getPrincipal();
			}
			else if(identity.getSubject()!=null){
				id = identity.getSubject();
			}
			else if(identity.getUsername()!=null){
				id = identity.getUsername();
			}
			
			if(id!=null){
				if(mappingInfo.getPattern()!=null){
					try{
						String tmp = RegularExpressionEngine.getStringMatchPattern(id, mappingInfo.getPattern());
						if(tmp!=null && !"".equals(tmp.trim())){
							return tmp.trim();
						}
					}catch(Exception e){
					}
				}
				return id;
			}
			
			throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.IDENTITY_BASED.toString()+" non riuscita: Identity fornita non contiene una identita");
		}
		
		return null;
	}
	
	

	

}
