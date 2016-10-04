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



package org.openspcoop2.pdd.core.integrazione;


import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;

import org.slf4j.Logger;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.ValidatoreXSD;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.utils.xml.XSDResourceResolver;


/**
 * Classe contenenti utilities per le integrazioni.
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class UtilitiesIntegrazione {


	// ***** STATIC *****
	
	private static UtilitiesIntegrazione utilitiesIntegrazione = null;
	public static UtilitiesIntegrazione getInstance(Logger log){
		if(UtilitiesIntegrazione.utilitiesIntegrazione==null){
			UtilitiesIntegrazione.initialize(log);
		}
		return UtilitiesIntegrazione.utilitiesIntegrazione;
	}

	private static synchronized void initialize(Logger log){
		if(UtilitiesIntegrazione.utilitiesIntegrazione==null){
			UtilitiesIntegrazione.utilitiesIntegrazione = new UtilitiesIntegrazione(log);
		}
	}

	
	
	
	// ***** INSTANCE *****

	private java.util.Properties keyValueIntegrazioneTrasporto = null;
	private java.util.Properties keyValueIntegrazioneUrlBased = null;
	private java.util.Properties keyValueIntegrazioneSoap = null;
	private OpenSPCoop2Properties openspcoopProperties = null;
	private ValidatoreXSD validatoreXSD = null;

	private UtilitiesIntegrazione(Logger log){
		this.openspcoopProperties = OpenSPCoop2Properties.getInstance();
		this.keyValueIntegrazioneTrasporto = this.openspcoopProperties.getKeyValue_HeaderIntegrazioneTrasporto();
		this.keyValueIntegrazioneUrlBased = this.openspcoopProperties.getKeyValue_HeaderIntegrazioneUrlBased();
		this.keyValueIntegrazioneSoap = this.openspcoopProperties.getKeyValue_HeaderIntegrazioneSoap();
		
		try{
			XSDResourceResolver xsdResourceResolver = new XSDResourceResolver();
			xsdResourceResolver.addResource("soapEnvelope.xsd", UtilitiesIntegrazione.class.getResourceAsStream("/soapEnvelope.xsd"));
			this.validatoreXSD = new ValidatoreXSD(log,xsdResourceResolver,UtilitiesIntegrazione.class.getResourceAsStream("/integrazione.xsd"));
		}catch(Exception e){
			log.error("Integrazione.xsd, errore durante la costruzione del validatore xsd: "+e.getMessage(),e);
		}
	}
	
	public ValidatoreXSD getValidatoreXSD() {
		return this.validatoreXSD;
	}
	
	public void readTransportProperties(java.util.Properties prop,
			HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		try{
			if(prop!=null && integrazione!=null){
								
				// Ricerca tra l'header del trasporto
				java.util.Enumeration<?> keys =  prop.propertyNames();
				while(keys.hasMoreElements()){
					String key = (String) keys.nextElement();

					if(key!=null){
						// Busta
						if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE)))
							integrazione.getBusta().setTipoMittente(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE)))
							integrazione.getBusta().setMittente(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO)))
							integrazione.getBusta().setTipoDestinatario(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO)))
							integrazione.getBusta().setDestinatario(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO)))
							integrazione.getBusta().setTipoServizio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO)))
							integrazione.getBusta().setServizio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE)))
							integrazione.getBusta().setAzione(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO)))
							integrazione.getBusta().setID(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO)))
							integrazione.getBusta().setRiferimentoMessaggio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE)))
							integrazione.getBusta().setIdCollaborazione(prop.getProperty(key));

						// id e servizio applicativo
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO)))
							integrazione.setIdApplicativo(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA)))
							integrazione.setRiferimentoIdApplicativoRichiesta(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO)))
							integrazione.setServizioApplicativo(prop.getProperty(key));
					}
				}
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header non riuscita: "+e.getMessage(),e);
		}
	}
	
	public void readUrlProperties(java.util.Properties prop,
			HeaderIntegrazione integrazione) throws HeaderIntegrazioneException{
		try{
			if(prop!=null && integrazione!=null){
			
				// Ricerca tra le proprieta' dell'url
				java.util.Enumeration<?> keys =  prop.propertyNames();
				while(keys.hasMoreElements()){
					String key = (String) keys.nextElement();

					if(key!=null){
						// Busta
						if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE)))
							integrazione.getBusta().setTipoMittente(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE)))
							integrazione.getBusta().setMittente(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO)))
							integrazione.getBusta().setTipoDestinatario(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO)))
							integrazione.getBusta().setDestinatario(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO)))
							integrazione.getBusta().setTipoServizio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO)))
							integrazione.getBusta().setServizio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE)))
							integrazione.getBusta().setAzione(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO)))
							integrazione.getBusta().setID(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO)))
							integrazione.getBusta().setRiferimentoMessaggio(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE)))
							integrazione.getBusta().setIdCollaborazione(prop.getProperty(key));

						// id e servizio applicativo
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO)))
							integrazione.setIdApplicativo(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA)))
							integrazione.setRiferimentoIdApplicativoRichiesta(prop.getProperty(key));
						else if(key.equalsIgnoreCase((String)this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO)))
							integrazione.setServizioApplicativo(prop.getProperty(key));
					}
				}
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header non riuscita: "+e.getMessage(),e);
		}
	}
	


	public void setRequestUrlProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{
		setUrlProperties(integrazione, properties, true, false, protocolInfos);
	}
	public void setResponseUrlProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{
		setUrlProperties(integrazione, properties, false, true, protocolInfos);
	}
	private void setUrlProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,boolean request,boolean response,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{

		try{
			if(properties!=null && integrazione!=null){
				if(integrazione.getBusta()!=null){
					if(integrazione.getBusta().getTipoMittente()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE), integrazione.getBusta().getTipoMittente());
					if(integrazione.getBusta().getMittente()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE), integrazione.getBusta().getMittente());
					if(integrazione.getBusta().getTipoDestinatario()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO), integrazione.getBusta().getTipoDestinatario());
					if(integrazione.getBusta().getDestinatario()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO), integrazione.getBusta().getDestinatario());
					if(integrazione.getBusta().getTipoServizio()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO), integrazione.getBusta().getTipoServizio());
					if(integrazione.getBusta().getServizio()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO), integrazione.getBusta().getServizio());
					if(integrazione.getBusta().getAzione()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE), integrazione.getBusta().getAzione());
					if(integrazione.getBusta().getID()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO), integrazione.getBusta().getID());
					if(integrazione.getBusta().getRiferimentoMessaggio()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO), integrazione.getBusta().getRiferimentoMessaggio());
					if(integrazione.getBusta().getIdCollaborazione()!=null)
						properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE), integrazione.getBusta().getIdCollaborazione());
				}
				if(integrazione.getIdApplicativo()!=null)
					properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO), integrazione.getIdApplicativo());
				if(integrazione.getRiferimentoIdApplicativoRichiesta()!=null)
					properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA), integrazione.getRiferimentoIdApplicativoRichiesta());
				if(integrazione.getServizioApplicativo()!=null)
					properties.put(this.keyValueIntegrazioneUrlBased.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO), integrazione.getServizioApplicativo());
			}
			if(properties!=null){
//				properties.put(CostantiPdD.URL_BASED_PDD,URLEncoder.encode(this.openspcoopProperties.getHttpServer(),"UTF-8"));
//				if(this.openspcoopProperties.getHttpXPdDDetails()!=null && !"".equals(this.openspcoopProperties.getHttpXPdDDetails())){
//					properties.put(CostantiPdD.URL_BASED_PDD_DETAILS,URLEncoder.encode(this.openspcoopProperties.getHttpXPdDDetails(),"UTF-8"));
//				}
				// Non deve essere effettuato a questo livello l'URLEncoder altrimenti si ottiene una doppia codifica, essendo poi fatta anche per tutti i valori in ConnettoreUtils.
				properties.put(CostantiPdD.URL_BASED_PDD,this.openspcoopProperties.getHttpServer());
				if(this.openspcoopProperties.getHttpXPdDDetails()!=null && !"".equals(this.openspcoopProperties.getHttpXPdDDetails())){
					properties.put(CostantiPdD.URL_BASED_PDD_DETAILS,this.openspcoopProperties.getHttpXPdDDetails());
				}

				if(protocolInfos!=null && protocolInfos.size()>0){
					Iterator<String> itProtocolInfos = protocolInfos.keySet().iterator();
					while (itProtocolInfos.hasNext()) {
						String name = (String) itProtocolInfos.next();
						String value = protocolInfos.get(name);
						properties.put(name,value);
					}
				}
			}
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, creazione delle proprieta' dell'header non riuscita: "+e.getMessage(),e);
		}
	}
	
	public void setRequestTransportProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{
		setTransportProperties(integrazione, properties, true, false, protocolInfos);
	}
	public void setResponseTransportProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{
		setTransportProperties(integrazione, properties, false, true, protocolInfos);
	}
	private void setTransportProperties(HeaderIntegrazione integrazione,
			java.util.Properties properties,boolean request,boolean response,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{

		try{
			if(properties!=null && integrazione!=null){
				if(integrazione.getBusta()!=null){
					if(integrazione.getBusta().getTipoMittente()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE), integrazione.getBusta().getTipoMittente());
					if(integrazione.getBusta().getMittente()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE), integrazione.getBusta().getMittente());
					if(integrazione.getBusta().getTipoDestinatario()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO), integrazione.getBusta().getTipoDestinatario());
					if(integrazione.getBusta().getDestinatario()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO), integrazione.getBusta().getDestinatario());
					if(integrazione.getBusta().getTipoServizio()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO), integrazione.getBusta().getTipoServizio());
					if(integrazione.getBusta().getServizio()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO), integrazione.getBusta().getServizio());
					if(integrazione.getBusta().getAzione()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE), integrazione.getBusta().getAzione());
					if(integrazione.getBusta().getID()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO), integrazione.getBusta().getID());
					if(integrazione.getBusta().getRiferimentoMessaggio()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO), integrazione.getBusta().getRiferimentoMessaggio());
					if(integrazione.getBusta().getIdCollaborazione()!=null)
						properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE), integrazione.getBusta().getIdCollaborazione());
				}
				if(integrazione.getIdApplicativo()!=null)
					properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO), integrazione.getIdApplicativo());
				if(integrazione.getRiferimentoIdApplicativoRichiesta()!=null)
					properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA), integrazione.getRiferimentoIdApplicativoRichiesta());
				if(integrazione.getServizioApplicativo()!=null)
					properties.put(this.keyValueIntegrazioneTrasporto.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO), integrazione.getServizioApplicativo());
			}
			if(properties!=null){
				if(request)
					properties.put(CostantiPdD.HEADER_HTTP_USER_AGENT,this.openspcoopProperties.getHttpUserAgent());
				properties.put(CostantiPdD.HEADER_HTTP_X_PDD,this.openspcoopProperties.getHttpServer());
				if(this.openspcoopProperties.getHttpXPdDDetails()!=null && !"".equals(this.openspcoopProperties.getHttpXPdDDetails())){
					properties.put(CostantiPdD.HEADER_HTTP_X_PDD_DETAILS,this.openspcoopProperties.getHttpXPdDDetails());
				}
				
				if(protocolInfos!=null && protocolInfos.size()>0){
					Iterator<String> itProtocolInfos = protocolInfos.keySet().iterator();
					while (itProtocolInfos.hasNext()) {
						String name = (String) itProtocolInfos.next();
						String value = protocolInfos.get(name);
						properties.put(name,value);
					}
				}
			}
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, creazione delle proprieta' dell'header non riuscita: "+e.getMessage(),e);
		}
	}
	
	

	public void readHeader(OpenSPCoop2Message message,HeaderIntegrazione integrazione,
			String actorIntegrazione) throws HeaderIntegrazioneException{
		
		
		try{
			if(actorIntegrazione==null)
				throw new Exception("Actor non definito");
			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente");
				return;
			}
			SOAPHeaderElement headerElement = null;
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				headerElement = (SOAPHeaderElement) it.next();
				//Controllo Actor
				if( actorIntegrazione.equals(headerElement.getActor()) ){
					break;
				}else{
					headerElement = null;
				}
			}
			if(headerElement==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("Header di integrazione non presente");
				return;
			}
			
			// validazione XSD
			if(this.validatoreXSD==null)
				throw new Exception("Validatore XSD non istanziato");
			this.validatoreXSD.valida(new java.io.ByteArrayInputStream(message.getAsByte(headerElement, false)));

			
			// Ricerca tra gli attributi dell'header SOAP
			String tipoMittente = null;
			try{
			    tipoMittente = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE));
			}catch(Exception e){}
			if(tipoMittente!=null && tipoMittente.compareTo("")!=0)
				integrazione.getBusta().setTipoMittente(tipoMittente);
			
			String mittente = null;
			try{
			    mittente = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE));
			}catch(Exception e){}
			if(mittente!=null && mittente.compareTo("")!=0)
				integrazione.getBusta().setMittente(mittente);
			
			String tipoDestinatario = null;
			try{
			    tipoDestinatario = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO));
			}catch(Exception e){}
			if(tipoDestinatario!=null && tipoDestinatario.compareTo("")!=0)
				integrazione.getBusta().setTipoDestinatario(tipoDestinatario);
			
			String destinatario = null;
			try{
			    destinatario = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO));
			}catch(Exception e){}
			if(destinatario!=null && destinatario.compareTo("")!=0)
				integrazione.getBusta().setDestinatario(destinatario);

			String tipoServizio = null;
			try{
			    tipoServizio = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO));
			}catch(Exception e){}
			if(tipoServizio!=null && tipoServizio.compareTo("")!=0)
				integrazione.getBusta().setTipoServizio(tipoServizio);
			
			String servizio = null;
			try{
			    servizio = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO));
			}catch(Exception e){}
			if(servizio!=null && servizio.compareTo("")!=0)
				integrazione.getBusta().setServizio(servizio);

			String azione= null;
			try{
			    azione= headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE));
			}catch(Exception e){}
			if(azione!=null && azione.compareTo("")!=0)
				integrazione.getBusta().setAzione(azione);

			String idBusta = null;
			try{
			    idBusta = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO));
			}catch(Exception e){}
			if(idBusta!=null && idBusta.compareTo("")!=0)
				integrazione.getBusta().setID(idBusta);

			String riferimentoMessaggio = null;
			try{
			    riferimentoMessaggio = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO));
			}catch(Exception e){}
			if(riferimentoMessaggio!=null && riferimentoMessaggio.compareTo("")!=0)
				integrazione.getBusta().setRiferimentoMessaggio(riferimentoMessaggio);

			String collaborazione = null;
			try{
			    collaborazione = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE));
			}catch(Exception e){}
			if(collaborazione!=null && collaborazione.compareTo("")!=0)
				integrazione.getBusta().setIdCollaborazione(collaborazione);

			String idApplicativo = null;
			try{
			    idApplicativo = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO));
			}catch(Exception e){}
			if(idApplicativo!=null && idApplicativo.compareTo("")!=0)
				integrazione.setIdApplicativo(idApplicativo);
			
			String riferimentoIdApplicativoRichiesta = null;
			try{
				riferimentoIdApplicativoRichiesta = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA));
			}catch(Exception e){}
			if(riferimentoIdApplicativoRichiesta!=null && riferimentoIdApplicativoRichiesta.compareTo("")!=0)
				integrazione.setRiferimentoIdApplicativoRichiesta(riferimentoIdApplicativoRichiesta);

			String sa = null;
			try{
			    sa = headerElement.getAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO));
			}catch(Exception e){}
			if(sa!=null && sa.compareTo("")!=0)
				integrazione.setServizioApplicativo(sa);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, lettura dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}

	public void updateHeader(OpenSPCoop2Message message,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String idBusta,String servizioApplicativo,
			String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta,
			String actorIntegrazione,String nomeElemento,String prefix,String namespace,
			String proprietaProtocolloNomeElemento,String proprietaProtocolloNomeTipoElemento,
			Map<String, String> protocolInfos) throws Exception{
		updateHeader(message, soggettoFruitore, idServizio, idBusta, null, 
				servizioApplicativo, correlazioneApplicativa, riferimentoCorrelazioneApplicativaRichiesta, 
				actorIntegrazione, nomeElemento, prefix, namespace, 
				proprietaProtocolloNomeElemento, proprietaProtocolloNomeTipoElemento, protocolInfos);
	}
	
	public void updateHeader(OpenSPCoop2Message message,IDSoggetto soggettoFruitore,IDServizio idServizio,
			String idBusta,String idBustaRisposta,String servizioApplicativo,
			String correlazioneApplicativa,String riferimentoCorrelazioneApplicativaRichiesta,
			String actorIntegrazione,String nomeElemento,String prefix,String namespace,
			String proprietaProtocolloNomeElemento,String proprietaProtocolloNomeTipoElemento,
			Map<String, String> protocolInfos) throws Exception{
		
		HeaderIntegrazione integrazione = new HeaderIntegrazione();
		integrazione.setIdApplicativo(correlazioneApplicativa);
		integrazione.setRiferimentoIdApplicativoRichiesta(riferimentoCorrelazioneApplicativaRichiesta);
		integrazione.setServizioApplicativo(servizioApplicativo);
		HeaderIntegrazioneBusta busta = new HeaderIntegrazioneBusta();
		busta.setTipoMittente(soggettoFruitore.getTipo());
		busta.setMittente(soggettoFruitore.getNome());
		busta.setTipoDestinatario(idServizio.getSoggettoErogatore().getTipo());
		busta.setDestinatario(idServizio.getSoggettoErogatore().getNome());
		busta.setTipoServizio(idServizio.getTipoServizio());
		busta.setServizio(idServizio.getServizio());
		busta.setAzione(idServizio.getAzione());
		if(idBustaRisposta==null){
			busta.setID(idBusta);
		}
		else{
			busta.setID(idBustaRisposta);
			busta.setRiferimentoMessaggio(idBusta);
		}
		integrazione.setBusta(busta);
		
		this.updateHeader(message, integrazione, actorIntegrazione, nomeElemento, prefix, namespace, 
				proprietaProtocolloNomeElemento, proprietaProtocolloNomeTipoElemento, protocolInfos);
	}
		
	public void updateHeader(OpenSPCoop2Message message,HeaderIntegrazione integrazione,
			String actorIntegrazione,String nomeElemento,String prefix,String namespace,
			String proprietaProtocolloNomeElemento,String proprietaProtocolloNomeTipoElemento,
			Map<String, String> protocolInfos) throws Exception{
		
		if(actorIntegrazione==null)
			throw new Exception("Actor non definito");
		SOAPHeader header = message.getSOAPHeader();
		SOAPHeaderElement headerIntegrazione = null;
		if(header==null){
			
			// Creo soap header
			OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente: add soapHeader");
			header = message.getSOAPPart().getEnvelope().addHeader();
			
		}else{

			// cerco soap di integrazione
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				headerIntegrazione = (SOAPHeaderElement) it.next();
				//Controllo Actor
				if( actorIntegrazione.equals(headerIntegrazione.getActor()) ){
					break;
				}else{
					headerIntegrazione = null;
				}
			}
			if(headerIntegrazione==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("Header di integrazione non presente, lo creo");
			}
		}

		Vector<SOAPElement> v = new Vector<SOAPElement>(); // mantengo eventuali message element presenti
		if(headerIntegrazione!=null){
			
			java.util.Iterator<?> it = headerIntegrazione.getChildElements();
			if(it.hasNext()){
				SOAPElement tmp = (SOAPElement) it.next();
				//System.out.println("CONSERVO MSG ELEMENT["+tmp.getLocalName()+"]");
				v.add(tmp);
			}
			 
			header.removeChild(headerIntegrazione);
		}
		
		// creo header da nuovo
		SOAPHeaderElement headerIntegrazioneNEW = this.buildHeader(integrazione, nomeElemento, prefix, namespace, 
				actorIntegrazione, message, 
				proprietaProtocolloNomeElemento, proprietaProtocolloNomeTipoElemento, protocolInfos);	
		
		// Riaggiungo eventuali elementi interni
		while(v.size()>0){
			SOAPElement tmp = v.remove(0);
			//System.out.println("RIAGGIUNGO MSG ELEMENT["+tmp.getLocalName()+"]");
			headerIntegrazioneNEW.addChildElement(tmp);
		}
		
		//System.out.println("OTTENGO ["+headerIntegrazioneNEW.getAsString()+"]");
		
		// aggiungo header element al SOAP Header
		//header.addChildElement(headerIntegrazioneNEW);
		message.addHeaderElement(header, headerIntegrazioneNEW);

	}
	
	public SOAPHeaderElement buildHeader(HeaderIntegrazione integrazione,String nomeElemento,
			String prefix,String namespace, String actor, 
			//SOAPVersion soapVersion,
			OpenSPCoop2Message m,
			String proprietaProtocolloNomeElemento,String proprietaProtocolloNomeTipoElemento,
			Map<String, String> protocolInfos) throws HeaderIntegrazioneException{

		try{
//			OpenSPCoop2MessageFactory mf = OpenSPCoop2MessageFactory.getMessageFactory();
//			
//			OpenSPCoop2Message m = mf.createMessage(soapVersion);
			//SOAPHeaderElement header = m.getSOAPHeader().addHeaderElement(new QName(namespace,nomeElemento,prefix));
			SOAPHeader soapHeader = m.getSOAPHeader();
			if(soapHeader==null){
				soapHeader = m.getSOAPPart().getEnvelope().addHeader();
			}
			SOAPHeaderElement header = m.newSOAPHeaderElement(soapHeader, new QName(namespace,nomeElemento,prefix));

			header.setActor(actor);
			header.setMustUnderstand(false);
			header.addNamespaceDeclaration("SOAP_ENV","http://schemas.xmlsoap.org/soap/envelope/");

			setAttributes(integrazione,header);
			
			if(protocolInfos!=null && protocolInfos.size()>0){
				Iterator<String> itProtocolInfos = protocolInfos.keySet().iterator();
				while (itProtocolInfos.hasNext()) {
					String name = (String) itProtocolInfos.next();
					String value = protocolInfos.get(name);
					SOAPElement element = header.addChildElement(new QName(namespace,proprietaProtocolloNomeElemento,prefix));
					element.setTextContent(value);
					@SuppressWarnings("unused")
					SOAPElement attribute = element.addAttribute(new QName(proprietaProtocolloNomeTipoElemento),name);
				}
			}
			
			return header;

		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, creazione dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}
	
	
	public void setAttributes(HeaderIntegrazione integrazione, SOAPHeaderElement header){
		if(integrazione.getBusta()!=null){

			if(integrazione.getBusta().getTipoMittente()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_MITTENTE), integrazione.getBusta().getTipoMittente());
			}
			if(integrazione.getBusta().getMittente()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_MITTENTE), integrazione.getBusta().getMittente());
			}

			if(integrazione.getBusta().getTipoDestinatario()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_DESTINATARIO), integrazione.getBusta().getTipoDestinatario());
			}
			if(integrazione.getBusta().getDestinatario()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_DESTINATARIO), integrazione.getBusta().getDestinatario());
			}

			if(integrazione.getBusta().getTipoServizio()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_TIPO_SERVIZIO), integrazione.getBusta().getTipoServizio());
			}
			if(integrazione.getBusta().getServizio()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO), integrazione.getBusta().getServizio());
			}

			if(integrazione.getBusta().getAzione()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_AZIONE), integrazione.getBusta().getAzione());
			}

			if(integrazione.getBusta().getID()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_MESSAGGIO), integrazione.getBusta().getID());
			}

			if(integrazione.getBusta().getRiferimentoMessaggio()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_RIFERIMENTO_MESSAGGIO), integrazione.getBusta().getRiferimentoMessaggio());
			}

			if(integrazione.getBusta().getIdCollaborazione()!=null){
				header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_COLLABORAZIONE), integrazione.getBusta().getIdCollaborazione());
			}
		}

		if(integrazione.getIdApplicativo()!=null){
			 header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO), integrazione.getIdApplicativo());
		}

		if(integrazione.getRiferimentoIdApplicativoRichiesta()!=null){
			 header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_ID_APPLICATIVO_RICHIESTA), integrazione.getRiferimentoIdApplicativoRichiesta());
		}
		
		if(integrazione.getServizioApplicativo()!=null){
			header.setAttribute((String)this.keyValueIntegrazioneSoap.get(CostantiPdD.HEADER_INTEGRAZIONE_SERVIZIO_APPLICATIVO), integrazione.getServizioApplicativo());
		}
		
		header.setAttribute(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_VERSION, this.openspcoopProperties.getHeaderIntegrazioneSOAPPdDVersione());
		if(this.openspcoopProperties.getHeaderIntegrazioneSOAPPdDDetails()!=null && !"".equals(this.openspcoopProperties.getHeaderIntegrazioneSOAPPdDDetails())){
			header.setAttribute(CostantiPdD.HEADER_INTEGRAZIONE_SOAP_PDD_DETAILS, this.openspcoopProperties.getHeaderIntegrazioneSOAPPdDDetails());
		}

	}
	
	
	public void deleteHeader(OpenSPCoop2Message message,String actorIntegrazione) throws HeaderIntegrazioneException{

		try{

			if(actorIntegrazione==null)
				throw new Exception("Actor non definito");
			SOAPHeader header = message.getSOAPHeader();
			if(header==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("SOAPHeader non presente");
				return;
			}
			SOAPHeaderElement headerElement = null;
			java.util.Iterator<?> it = header.examineAllHeaderElements();
			while( it.hasNext()  ){
				// Test Header Element
				headerElement = (SOAPHeaderElement) it.next();
				//Controllo Actor
				if( actorIntegrazione.equals(headerElement.getActor()) ){
					break;
				}else{
					headerElement = null;
				}
			}
			if(headerElement==null){
				OpenSPCoop2Logger.getLoggerOpenSPCoopCore().debug("Header di integrazione non presente");
				return;
			}
			
			header.removeChild(headerElement);
			
		}catch(Exception e){
			throw new HeaderIntegrazioneException("UtilitiesIntegrazione, eliminazione dell'header soap non riuscita: "+e.getMessage(),e);
		}
	}
}
