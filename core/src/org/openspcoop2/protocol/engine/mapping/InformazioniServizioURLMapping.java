/*
 * OpenSPCoop - Customizable API Gateway 
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

package org.openspcoop2.protocol.engine.mapping;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.DynamicNamespaceContextFactory;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.protocol.engine.URLProtocolContext;
import org.openspcoop2.protocol.engine.constants.IDService;
import org.openspcoop2.protocol.manifest.Openspcoop2;
import org.openspcoop2.protocol.manifest.UrlMapping;
import org.openspcoop2.protocol.manifest.constants.UrlMappingSourceType;
import org.openspcoop2.protocol.registry.RegistroServiziManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.utils.Identity;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;


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
	
	private static final String TIPO_DESTINATARIO = "identificazione-tipo-destinatario";
	private static final String NOME_DESTINATARIO = "identificazione-destinatario";
	
	private static final String TIPO_SERVIZIO = "identificazione-tipo-servizio";
	private static final String NOME_SERVIZIO = "identificazione-servizio";
	
	private static final String NOME_AZIONE = "identificazione-azione";
	
	private static final String INFORMAZIONI_PROTOCOLLO = "identificazione-informazioni-protocollo";
	
	private static final String ID_PROTOCOLLO = "identificazione-id-protocollo";
	
	private static final String GENERAZIONE_LISTA_TRASMISSIONI = "generazione-lista-trasmissioni";
		
	
	private static MappingProperties getMappingProperties(IProtocolFactory protocolFactory) throws ProtocolException{
		
		Openspcoop2 manifest = protocolFactory.getManifest();
		if(InformazioniServizioURLMapping.mappingPropertiesTable.containsKey(manifest.getProtocolName())==false){
			InformazioniServizioURLMapping.initMappingProperties(protocolFactory);
		}
		return InformazioniServizioURLMapping.mappingPropertiesTable.get(manifest.getProtocolName());
		
	}
	
	private static HashMap<String, MappingProperties> mappingPropertiesTable = new HashMap<String, MappingProperties>();
	public static synchronized void initMappingProperties(IProtocolFactory protocolFactory) throws ProtocolException{
		
		try{
			Openspcoop2 manifest = protocolFactory.getManifest();
			if(InformazioniServizioURLMapping.mappingPropertiesTable.containsKey(manifest.getProtocolName())==false){
			
				UrlMapping urlMapping = manifest.getUrlMapping();
				if(UrlMappingSourceType.XML.equals(urlMapping.getTipo())){
					//TODO IMPLEMENTARE VERSIONE XML
					throw new ProtocolException("Not implemented");
				}
				else if(UrlMappingSourceType.PROPERTIES.equals(urlMapping.getTipo())){
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
						InformazioniServizioURLMapping.mappingPropertiesTable.put(manifest.getProtocolName(), m);
					}finally{
						try{
							if(is!=null){
								is.close();
							}
						}catch(Exception eClose){}
					}
				}
				else{
					throw new ProtocolException("["+urlMapping.getTipo().getValue()+"] Not supported");
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
	private RegistroServiziManager registroServiziManager;
	private Logger log;
	
	// Gestore delle properties
	private MappingProperties mp;
	
	// Id Mapping
	private String idMapping; // la keyword che indica il mapping per quanto concerne la url invocata
	
	// Mittente
	private MappingInfo tipoMittente;
	private MappingInfo mittente;
	
	// Destinatario
	private MappingInfo tipoDestinatario;
	private MappingInfo destinatario;
	
	// Servizio
	private MappingInfo tipoServizio;
	private MappingInfo servizio;
	
	// Azione
	private MappingInfo azione;
	
	// Informazioni di protocollo
	private MappingInfo infoProtocollo;
	
	// ID di protocollo
	private MappingInfo idProtocollo;
	
	// PAInfo
	private MappingPAInfo paInfo;
	
	// Indicazione se generare la lista trasmissione
	private boolean generateListaTrasmissione = false;
		

	public InformazioniServizioURLMapping(OpenSPCoop2Message msg,IProtocolFactory protocolFactory,
			URLProtocolContext urlProtocolContext, RegistroServiziManager registroServiziManager, Logger log, IDService idService) throws ProtocolException{
				
		this.mp = InformazioniServizioURLMapping.getMappingProperties(protocolFactory);
				
		this.msg = msg;
		this.urlProtocolContext = urlProtocolContext;
		this.registroServiziManager = registroServiziManager;
		this.log = log;
		
		// Id Mapping
		this.idMapping = resolveMappingName(urlProtocolContext, idService);

		// Mittente
		this.tipoMittente = this.getMappingInfo(TIPO_MITTENTE);
		this.mittente = this.getMappingInfo(NOME_MITTENTE);
		
		// Destinatario
		this.tipoDestinatario = this.getMappingInfo(TIPO_DESTINATARIO);
		this.destinatario = this.getMappingInfo(NOME_DESTINATARIO);
		
		// Servizio
		this.tipoServizio = this.getMappingInfo(TIPO_SERVIZIO);
		this.servizio = this.getMappingInfo(NOME_SERVIZIO);
		
		// Azione
		this.azione = this.getMappingInfo(NOME_AZIONE);
		
		// Informazioni di protocollo
		this.infoProtocollo = this.getMappingInfo(INFORMAZIONI_PROTOCOLLO);
		
		// ID di protocollo
		this.idProtocollo = this.getMappingInfo(ID_PROTOCOLLO);
		
		// PAInfo
		if(this.existsPABasedIdentificationMode()){
			this.paInfo = this.getMappingPAInfo(this.msg.getProtocolName(), urlProtocolContext, idService);
		}
		
		// ListaTrasmissione
		String generazioneListaTrasmissioniTmp = this.mp.getValue(this.msg.getProtocolName(), this.idMapping, GENERAZIONE_LISTA_TRASMISSIONI);
		if(generazioneListaTrasmissioniTmp!=null){
			this.generateListaTrasmissione = Boolean.parseBoolean(generazioneListaTrasmissioniTmp);
		}
		
	}
	
	public MappingPAInfo getPaInfo() {
		return this.paInfo;
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
		
		bf.append("TipoDestinatario(");
		if(this.tipoDestinatario!=null)
			bf.append(this.tipoDestinatario.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("Destinatario(");
		if(this.destinatario!=null)
			bf.append(this.destinatario.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("TipoServizio(");
		if(this.tipoServizio!=null)
			bf.append(this.tipoServizio.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("Servizio(");
		if(this.servizio!=null)
			bf.append(this.servizio.toString());
		else
			bf.append("?non presente?");
		bf.append(") ");
		
		bf.append("Azione(");
		if(this.azione!=null)
			bf.append(this.azione.toString());
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
		
		if(this.paInfo!=null){
			bf.append("PAMappingInfo(");
			bf.append(this.paInfo.toString());
			bf.append(") ");
		}
		
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
	
	private String resolveMappingName(URLProtocolContext urlProtocolContext, IDService idService) throws ProtocolException {
			
		// Recupero il nome del mapping per la url invocata
		String mappingName = this.mp.getMappingName(this.msg.getProtocolName(), urlProtocolContext.getRequestURI(), idService);

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
		
		mappingInfo.setForceWsdlBased(this.mp.isForceWsdlBased(this.msg.getProtocolName(), this.idMapping, proprieta));
		
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

		if(!NOME_AZIONE.equals(proprieta)){
			if(mappingInfo.getForceWsdlBased()!=null){
				throw new ProtocolException("Il suffisso '.forceWsdlBased', indicato per la proprieta' ["+proprieta+"], puo' essere utilizzata solamente per identificare il nome dell'azione");
			}
		}
		else{
			if(mappingInfo.getForceWsdlBased()!=null && ModalitaIdentificazione.WSDL_BASED.equals(mappingInfo.getModalitaIdentificazione())){
				throw new ProtocolException("Il suffisso '.forceWsdlBased', indicato per la proprieta' ["+proprieta+"], indica un meccanismo alternativo per identificare il nome dell'azione. E' utilizzabile solamente se la modalità d'identificazione principale è differente da "+ModalitaIdentificazione.WSDL_BASED.getValore());
			}
			if(mappingInfo.getForceWsdlBased()!=null && ModalitaIdentificazione.PLUGIN_BASED.equals(mappingInfo.getModalitaIdentificazione())){
				throw new ProtocolException("Il suffisso '.forceWsdlBased', indicato per la proprieta' ["+proprieta+"], non puo' essere utilizzata come meccanismo alternativo per identificare il nome dell'azione se la modalità d'identificazione principale è "+ModalitaIdentificazione.PLUGIN_BASED.getValore());
			}
		}
		
		if(ModalitaIdentificazione.SOAP_ACTION_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(!NOME_AZIONE.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.SOAP_ACTION_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], puo' essere utilizzata solamente per identificare il nome dell'azione");
			}
		}
		
		if(ModalitaIdentificazione.WSDL_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(!NOME_AZIONE.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.WSDL_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], puo' essere utilizzata solamente per identificare il nome dell'azione");
			}
		}
		
		if(ModalitaIdentificazione.PA_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(TIPO_MITTENTE.equals(proprieta) || NOME_MITTENTE.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.PA_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], non puo' essere utilizzata per identificare il tipo od il nome del mittente");
			}
			if(INFORMAZIONI_PROTOCOLLO.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.PA_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], non puo' essere utilizzata per identificare le informazioni di protocollo");
			}
			if(ID_PROTOCOLLO.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.PA_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], non puo' essere utilizzata per identificare l'identificativo unico di protocollo");
			}
		}
		
		if(ModalitaIdentificazione.IDENTITY_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(!NOME_MITTENTE.equals(proprieta)){
				throw new ProtocolException("La Modalita' '"+ModalitaIdentificazione.IDENTITY_BASED.toString()+"', indicata per la proprieta' ["+proprieta+"], puo' essere utilizzata solamente per identificare il nome del mittente");
			}
		}
		
		
		return mappingInfo;
		
	}
	
	
	private MappingPAInfo getMappingPAInfo(String protocol,URLProtocolContext urlProtocolContext, IDService idService) throws ProtocolException {
		
		/*
		# - paBased: il valore viene recuperato accedendo alla porta applicativa. 
		#            La Porta Applicativa deve essere indicata nella url invocata subito dopo 'PA' (es. http://127.0.0.1:8080/openspcoop2/PA/<NOME_PA>)
		#            Siccome potrebbero esistere piu' porte applicative con lo stesso nome, associati a soggetti diversi, 
		#            opzionalmente e' possibile indicare il tipo ed il nome del soggetto prima del nome della porta applicativa utilizzando la seguente sintassi:
		#            	http://<server>/openspcoop2/<protocol>/PA/<NOME_PA>[/<NOME_AZIONE>][[?,&]tipo_soggetto=<TIPO_SOGGETTO>][[?,&]soggetto=<NOME_SOGGETTO>]
		#			 Infine se la PA non possiede una azione all'interno della propria configurazione, e' possibile indicarla nella url.
		*/
			
		String urlWithoutContext = this.mp.getUrlWithoutContext(protocol,urlProtocolContext.getRequestURI(), idService);
		if(urlWithoutContext.startsWith("/")){
			urlWithoutContext = urlWithoutContext.substring(1, urlWithoutContext.length());
		}	
		
		MappingPAInfo mappingInfo = new MappingPAInfo();
		
		if(urlWithoutContext.contains("/")){
			int indexOfLast = urlWithoutContext.lastIndexOf("/");
			mappingInfo.setNomePA(urlWithoutContext.substring(0, indexOfLast));
			if( (indexOfLast+1) < urlWithoutContext.length()){
				mappingInfo.setAzione(urlWithoutContext.substring(indexOfLast+1, urlWithoutContext.length()));
			}
		}
		else{
			mappingInfo.setNomePA(urlWithoutContext);
		}
		
		if(urlProtocolContext.getParametersFormBased()!=null){
			if(urlProtocolContext.getParametersFormBased().containsKey("tipo_soggetto")){
				mappingInfo.setTipoSoggetto(urlProtocolContext.getParametersFormBased().getProperty("tipo_soggetto"));
			}
			if(urlProtocolContext.getParametersFormBased().containsKey("soggetto")){
				mappingInfo.setNomeSoggetto(urlProtocolContext.getParametersFormBased().getProperty("soggetto"));
			}
		}
		
		if(mappingInfo.getNomePA()==null){
			throw new ProtocolException("Nome della Porta Applicativa non presente nella url");
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
		
		if(this.tipoDestinatario!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.tipoDestinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.destinatario!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.destinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.tipoServizio!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.tipoServizio.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.servizio!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.servizio.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.azione!=null){
			if(ModalitaIdentificazione.URL_BASED.equals(this.azione.getModalitaIdentificazione())){
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
		
		if(this.tipoDestinatario!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.tipoDestinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.destinatario!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.destinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.tipoServizio!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.tipoServizio.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.servizio!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.servizio.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.azione!=null){
			if(ModalitaIdentificazione.CONTENT_BASED.equals(this.azione.getModalitaIdentificazione())){
				return true;
			}
		}
	
		return false;
	}
	
	public boolean existsPABasedIdentificationMode(){
				
		if(this.tipoDestinatario!=null){
			if(ModalitaIdentificazione.PA_BASED.equals(this.tipoDestinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.destinatario!=null){
			if(ModalitaIdentificazione.PA_BASED.equals(this.destinatario.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.tipoServizio!=null){
			if(ModalitaIdentificazione.PA_BASED.equals(this.tipoServizio.getModalitaIdentificazione())){
				return true;
			}
		}
		if(this.servizio!=null){
			if(ModalitaIdentificazione.PA_BASED.equals(this.servizio.getModalitaIdentificazione())){
				return true;
			}
		}
		
		if(this.azione!=null){
			if(ModalitaIdentificazione.PA_BASED.equals(this.azione.getModalitaIdentificazione())){
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
	
	public void refreshDati(IDSoggetto idMittente, IDServizio idServizio,String soapAction,Identity identity,PortaApplicativa pa) throws ProtocolException{
		
		String urlInvocazione = null;
		
		DynamicNamespaceContext dnc = null;
		AbstractXPathExpressionEngine xPathEngine = null;
		SOAPEnvelope envelope = null;
		
		if(this.existsUrlBasedIdentificationMode()){
			urlInvocazione = this.urlProtocolContext.getUrlInvocazione_formBased();
		}
		if(this.existsContentBasedIdentificationMode()){
			try{
				envelope = this.msg.getSOAPPart().getEnvelope();
				dnc = DynamicNamespaceContextFactory.getInstance().getNamespaceContext(envelope);
				xPathEngine = new org.openspcoop2.message.XPathExpressionEngine();
			}catch(Exception e){
				throw new ProtocolException(e.getMessage(),e);
			}
		}
		
		// TipoMittente
		String tipoMittente = null;
		try{
			tipoMittente = this.readValue(this.tipoMittente, TIPO_MITTENTE, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
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
			mittente = this.readValue(this.mittente, NOME_MITTENTE, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
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
		
		// Destinatario
		String tipoDestinatario = this.readValue(this.tipoDestinatario, TIPO_DESTINATARIO, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
		if(tipoDestinatario!=null){
			idServizio.getSoggettoErogatore().setTipo(tipoDestinatario);
		}
		String destinatario = this.readValue(this.destinatario, NOME_DESTINATARIO, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
		if(destinatario!=null){
			idServizio.getSoggettoErogatore().setNome(destinatario);
		}
		
		// Servizio
		String tipoServizio = this.readValue(this.tipoServizio, TIPO_SERVIZIO, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
		if(tipoServizio!=null){
			idServizio.setTipoServizio(tipoServizio);
		}
		String servizio = this.readValue(this.servizio, NOME_SERVIZIO, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, null);
		if(servizio!=null){
			idServizio.setServizio(servizio);
		}
		
		// Azione
		String azione = null;
		ProtocolException eAzione = null;
		try{
			azione = this.readValue(this.azione, NOME_AZIONE, soapAction, identity, urlInvocazione, envelope, dnc, xPathEngine, pa, idServizio);
		}catch(ProtocolException e){
			//System.out.println("ERROR ["+this.azione.getModalitaIdentificazione()+"]: "+e.getMessage());
			eAzione = e;
		}
		//System.out.println("DOPO RICONOSCIMENTO ["+this.azione.getModalitaIdentificazione()+"]: "+azione);
		if(azione==null){
			if(this.azione.getForceWsdlBased()!=null && this.azione.getForceWsdlBased()){
				try{
					//System.out.println("Force ...");
					checkIDServizioPerRiconoscimentoAzione(idServizio,ModalitaIdentificazione.WSDL_BASED);
					azione = OperationFinder.searchOperationByRequestMessage(this.msg, this.registroServiziManager, idServizio, this.log);
				}catch(Exception e){
					this.log.debug("Riconoscimento forzato dell'azione non riuscito: "+e.getMessage(),e);
				}
			}
		}
		//System.out.println("FINALE: "+azione);
		if(azione!=null){
			idServizio.setAzione(azione);
		}
		else if(eAzione!=null){
			throw eAzione;
		}
		
	}
	
	
	private String readValue(MappingInfo mappingInfo,String oggetto,
			String soapAction,Identity identity,String urlInvocazione,
			SOAPEnvelope envelope, DynamicNamespaceContext dnc ,AbstractXPathExpressionEngine xPathEngine,
			PortaApplicativa pa,
			IDServizio idServizio) throws ProtocolException{
		
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
				return xPathEngine.getStringMatchPattern(envelope,dnc, mappingInfo.getPattern() );
			}catch(Exception e){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.CONTENT_BASED.toString()+" non riuscita: "+e.getMessage(),e);
			}
		}
		
		else if(ModalitaIdentificazione.SOAP_ACTION_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(soapAction==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.SOAP_ACTION_BASED.toString()+" non riuscita: la soap action non risulta valorizzata nella transazione in corso");
			}else{
				return soapAction;
			}
		}

		else if(ModalitaIdentificazione.WSDL_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			try{
				checkIDServizioPerRiconoscimentoAzione(idServizio,mappingInfo.getModalitaIdentificazione());
				return OperationFinder.searchOperationByRequestMessage(this.msg, this.registroServiziManager, idServizio, this.log);
			}catch(Exception e){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.WSDL_BASED.toString()+" non riuscita: "+e.getMessage(),e);
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
		
		else if(ModalitaIdentificazione.PA_BASED.equals(mappingInfo.getModalitaIdentificazione())){
			if(pa==null){
				throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.PA_BASED.toString()+" non riuscita: porta applicativa non fornita");
			}
			if(TIPO_DESTINATARIO.equals(oggetto)){
				if( pa.getTipoSoggettoProprietario()==null){
					throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.PA_BASED.toString()+" non riuscita: la porta applicativa non contiene l'informazione");
				}
				return pa.getTipoSoggettoProprietario();
			}
			else if(NOME_DESTINATARIO.equals(oggetto)){
				if( pa.getNomeSoggettoProprietario()==null){
					throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.PA_BASED.toString()+" non riuscita: la porta applicativa non contiene l'informazione");
				}
				return pa.getNomeSoggettoProprietario();
			}
			else if(TIPO_SERVIZIO.equals(oggetto)){
				if( pa.getServizio()==null || pa.getServizio().getTipo()==null){
					throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.PA_BASED.toString()+" non riuscita: la porta applicativa non contiene l'informazione");
				}
				return pa.getServizio().getTipo();
			}
			else if(NOME_SERVIZIO.equals(oggetto)){
				if( pa.getServizio()==null || pa.getServizio().getNome()==null){
					throw new ProtocolException("URLMapping["+oggetto+"] identificazione "+ModalitaIdentificazione.PA_BASED.toString()+" non riuscita: la porta applicativa non contiene l'informazione");
				}
				return pa.getServizio().getNome();
			}
			else if(NOME_AZIONE.equals(oggetto)){
				if(pa.getAzione()!=null && pa.getAzione().getNome()!=null){
					return pa.getAzione().getNome();
				}
				else if(this.paInfo!=null && this.paInfo.getAzione()!=null){
					return this.paInfo.getAzione();
				}
			}
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
	
	
	public static void checkIDServizioPerRiconoscimentoAzione(IDServizio idServizio, ModalitaIdentificazione modalitaIdentificazione) throws Exception{
		if(idServizio==null){
			throw new Exception("Dati del servizio non trovati (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore()==null){
			throw new Exception("Dati del soggetto erogatore del servizio non trovati (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore().getTipo()==null){
			throw new Exception("Tipo soggetto erogatore del servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
		if(idServizio.getSoggettoErogatore().getNome()==null){
			throw new Exception("Nome soggetto erogatore del servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
		if(idServizio.getTipoServizio()==null){
			throw new Exception("Tipo servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
		if(idServizio.getServizio()==null){
			throw new Exception("Nome servizio non trovato (necessari per procedere con la modalità di identificazione "+modalitaIdentificazione.name()+" dell'azione)");
		}
	}
	

}
