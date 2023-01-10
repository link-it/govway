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



package org.openspcoop2.pdd.core.connettori;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.OpenSPCoop2RestJsonMessage;
import org.openspcoop2.message.OpenSPCoop2RestXmlMessage;
import org.openspcoop2.message.OpenSPCoop2SoapMessage;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.soap.SoapUtils;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;
import org.openspcoop2.utils.properties.PropertiesUtilities;
import org.openspcoop2.utils.regexp.RegExpNotFoundException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.rest.problem.JsonDeserializer;
import org.openspcoop2.utils.rest.problem.ProblemConstants;
import org.openspcoop2.utils.rest.problem.ProblemRFC7807;
import org.openspcoop2.utils.rest.problem.XmlDeserializer;
import org.slf4j.Logger;


/**
 * Classe utilizzata per gestire il comportamento della PdD in caso di errore di consegna tramite connettore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class GestoreErroreConnettore {

	/** Indicazione su riconsegna */
	private boolean riconsegna;
	/** Data di rispedizione */
	private java.sql.Timestamp dataRispedizione;
	/** Motivo errore consegna */
	private String errore;
	/** Eventuale FAULT interpretato */
	private SOAPFault fault;
	/** Eventuale Problem interpretato */
	private ProblemRFC7807 problem;

	/** Logger utilizzato per debug. */
	private static Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();


	private static org.openspcoop2.utils.Semaphore semaphore = new org.openspcoop2.utils.Semaphore("GestoreErroreConnettore");
	/** GestoreErrore di default per il componente di integrazione */
	private static HashMap<String, GestioneErrore> gestioneErroreDefaultComponenteIntegrazioneMap = new HashMap<>();
	/** GestoreErrore di default per il componente di cooperazione */
	private static HashMap<String, GestioneErrore> gestioneErroreDefaultComponenteCooperazioneMap = new HashMap<>();


	public static GestioneErrore getGestioneErroreDefaultComponenteIntegrazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding) {

		String key = protocolFactory.getProtocol()+"_"+serviceBinding;

		if(GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazioneMap.containsKey(key)){
			return GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazioneMap.get(key);
		}else{

			//synchronized (gestioneErroreDefaultComponenteIntegrazioneMap) {
			semaphore.acquireThrowRuntime("getGestioneErroreDefaultComponenteIntegrazione");
			try {

				if(GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazioneMap.containsKey(key)){
					return GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazioneMap.get(key);
				}

				GestioneErrore gestione = new GestioneErrore();
				// default si rispedisce
				gestione.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
				// si accetta il codice di trasporto 200
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
				codiceTrasporto.setValoreMinimo(200);
				codiceTrasporto.setValoreMassimo(299);
				boolean isSuccessfulHttpRedirectStatusCode = false;
				try {
					isSuccessfulHttpRedirectStatusCode = protocolFactory.createProtocolManager().isSuccessfulHttpRedirectStatusCode(serviceBinding);
				}catch(Exception e) {
					log.error(e.getMessage(),e);
				}
				if(isSuccessfulHttpRedirectStatusCode) {
					codiceTrasporto.setValoreMassimo(399);
				}
				gestione.addCodiceTrasporto(codiceTrasporto);


				// Qualsiasi fault si accetta, senza effettuare un re-invio. 
				GestioneErroreSoapFault faultAccetta = new GestioneErroreSoapFault();
				faultAccetta.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
				gestione.addSoapFault(faultAccetta);


				GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazioneMap.put(key, gestione);
				return gestione;

			}finally{
				semaphore.release("getGestioneErroreDefaultComponenteIntegrazione");
			}
		}
	}

	public static GestioneErrore getGestioneErroreDefaultComponenteCooperazione(IProtocolFactory<?> protocolFactory, ServiceBinding serviceBinding) {

		String key = protocolFactory.getProtocol()+"_"+serviceBinding;

		if(GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazioneMap.containsKey(key)){
			return GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazioneMap.get(key);
		}else{

			//synchronized (gestioneErroreDefaultComponenteCooperazioneMap) {
			semaphore.acquireThrowRuntime("getGestioneErroreDefaultComponenteCooperazione");
			try {

				if(GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazioneMap.containsKey(key)){
					return GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazioneMap.get(key);
				}

				GestioneErrore gestione = new GestioneErrore();
				// default si rispedisce
				gestione.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
				// si accetta il codice di trasporto 200-299
				GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
				codiceTrasporto.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
				codiceTrasporto.setValoreMinimo(200);
				boolean isSuccessfulHttpRedirectStatusCode = false;
				try {
					isSuccessfulHttpRedirectStatusCode = protocolFactory.createProtocolManager().isSuccessfulHttpRedirectStatusCode(serviceBinding);
				}catch(Exception e) {
					log.error(e.getMessage(),e);
				}
				if(isSuccessfulHttpRedirectStatusCode) {
					codiceTrasporto.setValoreMassimo(399);
				}
				else {
					codiceTrasporto.setValoreMassimo(299);
				}
				gestione.addCodiceTrasporto(codiceTrasporto);


				// Per qualsiasi fault si effettua una rispedizione, a meno di fault
				// integrati in buste errore:

				// BUSTE ERRORE DI VALIDAZIONE
				// SoapActor == null
				// SoapFaultCode == soap:Client
				// SoapFaultString contain "*_001 - Formato Busta non corretto"

				org.openspcoop2.protocol.sdk.config.ITraduttore trasl = null;
				try {
					trasl = protocolFactory.createTraduttore();
				}catch(Exception e) {
					log.error(e.getMessage(),e);
				}

				if(trasl!=null) {
					// per gestire actor Client senza soap:
					GestioneErroreSoapFault faultClient = new GestioneErroreSoapFault();
					faultClient.setFaultCode("Client");
					faultClient.setFaultString(trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE));
					faultClient.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
					gestione.addSoapFault(faultClient);
					// per gestire actor Client con soap:
					//				GestioneErroreSoapFault faultSoapClient = new GestioneErroreSoapFault();
					//				faultSoapClient.setFaultCode("soap:Client");
					//				faultSoapClient.setFaultString(trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_VALIDAZIONE));
					//				faultSoapClient.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
					//				gestione.addSoapFault(faultSoapClient);

					// BUSTE ERRORE DI PROCESSAMENTO
					// SoapActor == null
					// SoapFaultCode == soap:Server
					// SoapFaultString contain "*_300 - Errore nel processamento del messaggio"

					// per gestire actor Server senza soap:
					GestioneErroreSoapFault faultServer = new GestioneErroreSoapFault();
					faultServer.setFaultCode("Server");
					faultServer.setFaultString(trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO));
					faultServer.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
					gestione.addSoapFault(faultServer);
					// per gestire actor Server con soap:
					//				GestioneErroreSoapFault faultSoapServer = new GestioneErroreSoapFault();
					//				faultSoapServer.setFaultCode("soap:Server");
					//				faultSoapServer.setFaultString(trasl.toString(MessaggiFaultErroreCooperazione.FAULT_STRING_PROCESSAMENTO));
					//				faultSoapServer.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
					//				gestione.addSoapFault(faultSoapServer);
				}

				GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazioneMap.put(key, gestione);
				return gestione;

			}finally {
				semaphore.release("getGestioneErroreDefaultComponenteCooperazione");
			}
		}
	}




	/**
	 * Controlla se la consegna ha generato un errore dovuto al connettore/codiceTrasporto/SoapFault
	 * Se vi e' stato un errore controlla se deve essere rieffettuata la consegna del messaggio.
	 * In tal caso imposta il messaggio sulla riconsegna (RISPEDIZIONE).
	 * Viene anche impostato il msg di errore dovuto ad una consegna errata.
	 * 
	 * @param gestioneErrore Gestione Errore
	 * @param msgErroreConnettore Messaggio di errore fornito con il connettore
	 * @param connectorSender connettore
	 * @return true se la consegna e' stata effettuata con successo, false altrimenti.
	 */
	public boolean verificaConsegna(GestioneErrore gestioneErrore,String msgErroreConnettore,Exception eccezioneErroreConnettore,
			IConnettore connectorSender) throws GestoreMessaggiException,javax.xml.soap.SOAPException,UtilsException{	

		long codiceTrasporto = connectorSender.getCodiceTrasporto();
		OpenSPCoop2Message messageResponse = connectorSender.getResponse();
		String protocolloConnettore = connectorSender.getProtocollo();
		
		// ESITO CONNETTORE:
		// Se ho un errore sul connettore:
		// - la consegna non e' andata a buon fine (return false)
		// - errore = errore del connettore
		// - viene effettuata la riconsegna solo se this.comportamento=rispedisci

		// ESITO SOAP FAULT:
		// Verifico che esista un SoapFault ed in tal caso un match
		// Se trovo un match:
		// a) la consegna e' andata a buon fine se gestore.comportamento=accetta (return true)
		// b) la consegna non e' andata a buon fine se gestore.comportamento=rispedisci (return false)
		//    - errore = SoapFault codice: xxx actor: xxx
		//    - viene effettuata la riconsegna solo se gestore.comportamento=rispedisci
		// Se non trovo un match controllo il codice di trasporto.

		// ESITO CODICE TRASPORTO:
		// Altrimenti verifico che esista un match sul codice trasporto.
		// Se trovo un match:
		// a) la consegna e' andata a buon fine se gestore.comportamento=accetta (return true)
		// b) la consegna non e' andata a buon fine se gestore.comportamento=rispedisci (return false)
		//    - errore = erroreTrasporto codice: xxx
		//    - viene effettuata la riconsegna solo se gestore.comportamento=rispedisci
		// Se non trovo un match adotto la politica di default:
		// a) la consegna e' andata a buon fine se this.comportamento=accetta (return true)
		// b) la consegna non e' andata a buon fine se this.comportamento=rispedisci (return false)
		//    - errore = erroreTrasporto codice: xxx
		//    - viene effettuata la riconsegna solo se this.comportamento=rispedisci


		// La data di rispedizione sara' rimpiazzata con una vera data se serve.
		java.util.Date now = DateManager.getDate();
		this.dataRispedizione = new Timestamp(now.getTime());


		//	ESITO CONNETTORE:
		if(msgErroreConnettore!=null){
			this.errore = msgErroreConnettore;
			if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestioneErrore.getComportamento())){
				this.riconsegna = true;
				if(gestioneErrore.getCadenzaRispedizione()!=null){
					try{
						int cadenzaRispedizione = Integer.parseInt(gestioneErrore.getCadenzaRispedizione());
						this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
					}catch(Exception e)	{
						GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato: "+e.getMessage());
					}
				}
			}else{
				this.riconsegna = false;
			}
			return false;
		}


		// ESITO SOAP FAULT:
		SOAPBody bodyConFault = null;
		MessageType messageType = null;

		if(messageResponse!=null && (messageResponse instanceof OpenSPCoop2SoapMessage) ){
			try{
				messageType = messageResponse.getMessageType();
				if(messageResponse.castAsSoap().hasSOAPFault()) {
					bodyConFault = messageResponse.castAsSoap().getSOAPBody();
				}
			}catch(Exception e){
				throw new GestoreMessaggiException(e.getMessage(),e);
			}
		}

		if(bodyConFault!=null){
			this.fault = bodyConFault.getFault();
			for(int i=0; i<gestioneErrore.sizeSoapFaultList();i++){
				GestioneErroreSoapFault gestore = gestioneErrore.getSoapFault(i);
				boolean match = true;
				if(gestore.getFaultCode()!=null){
					String codice = null;
					@SuppressWarnings("unused")
					String namespaceCodice = null;
					if(this.fault.getFaultCodeAsQName()!=null){
						codice = this.fault.getFaultCodeAsQName().getLocalPart();	
						namespaceCodice = this.fault.getFaultCodeAsQName().getNamespaceURI();
					}
					else{
						codice = this.fault.getFaultCode();
					}
					
					List<String> subCodice_soap12 = new ArrayList<String>();
					List<String> subCodiceNamespace_soap12 = new ArrayList<String>();
					if(MessageType.SOAP_12.equals(messageType)) {
						if(this.fault.getFaultSubcodes()!=null) {
							Iterator<QName> it = this.fault.getFaultSubcodes();
							while (it.hasNext()) {
								QName qName = (QName) it.next();
								if(qName.getLocalPart()!=null) {
									subCodice_soap12.add(qName.getLocalPart());
									subCodiceNamespace_soap12.add(qName.getNamespaceURI()!=null ? qName.getNamespaceURI() : "");
								}
							}
						}
					}
							

					if( gestore.getFaultCode().equalsIgnoreCase(codice) == false) {

						// provo a verificare con regular expr

						boolean matchRegExpr = false;
						try {
							matchRegExpr = RegularExpressionEngine.isMatch(codice, gestore.getFaultCode());
						}catch(RegExpNotFoundException notFound) {}
						catch(Exception e)	{
							GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultCode()+"' per fault code '"+codice+"' fallita: "+e.getMessage());
						}

						if(!matchRegExpr) {
							
							boolean matchSoap12 = false;
							if(!subCodice_soap12.isEmpty()) {
								for (String code12 : subCodice_soap12) {
									if( gestore.getFaultCode().equalsIgnoreCase(code12) ) {
										matchSoap12 = true;
										break;
									}
									else {
										boolean matchRegExprSoap12 = false;
										try {
											matchRegExprSoap12 = RegularExpressionEngine.isMatch(code12, gestore.getFaultCode());
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultCode()+"' per fault subcode 1.2 '"+code12+"' fallita: "+e.getMessage());
										}
										if(matchRegExprSoap12) {
											matchSoap12 = true;
											break;
										}
									}
								}
							}
							
							if(!matchSoap12) {
								match = false; // non ha il codice definito
							}
						}
					}

				}
				if(gestore.getFaultActor()!=null){

					String actor = this.fault.getFaultActor();

					if( gestore.getFaultActor().equalsIgnoreCase(actor) == false) {

						// provo a verificare con regular expr

						boolean matchRegExpr = false;
						try {
							matchRegExpr = RegularExpressionEngine.isMatch(actor, gestore.getFaultActor());
						}catch(RegExpNotFoundException notFound) {}
						catch(Exception e)	{
							GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultActor()+"' per fault actor '"+actor+"' fallita: "+e.getMessage());
						}

						if(!matchRegExpr) {
							match = false; // non ha l'actor definito
						}
					}

				}
				if(gestore.getFaultString()!=null){

					String faultString = this.fault.getFaultString();

					if( (faultString==null) ||
							(
									(faultString.contains(gestore.getFaultString()) == false) &&
									(faultString.contains(gestore.getFaultString().toLowerCase()) == false) &&
									(faultString.contains(gestore.getFaultString().toUpperCase()) == false)
									)
							) {

						// provo a verificare con regular expr

						boolean matchRegExpr = false;
						try {
							matchRegExpr = RegularExpressionEngine.isMatch(faultString, gestore.getFaultString());
						}catch(RegExpNotFoundException notFound) {}
						catch(Exception e)	{
							GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultString()+"' per fault string '"+faultString+"' fallita: "+e.getMessage());
						}

						if(!matchRegExpr) {
							match = false; // non ha il fault string definito
						}
					}			
				}

				if( match ){
					if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestore.getComportamento())){
						try{
							this.errore = "errore applicativo, "+SoapUtils.safe_toString(messageResponse.getFactory(), this.fault, GestoreErroreConnettore.log);
						}catch(Exception e){
							this.errore = "errore applicativo SoapFault";
							GestoreErroreConnettore.log.error("Serializzazione SOAPFault non riuscita: "+e.getMessage(),e);
						}
						this.riconsegna = true;
						if(gestore.getCadenzaRispedizione()!=null){
							try{
								int cadenzaRispedizione = Integer.parseInt(gestore.getCadenzaRispedizione());
								this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
							}catch(Exception e)	{
								GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato (soap fault): "+e.getMessage());
							}
						}else if(gestioneErrore.getCadenzaRispedizione()!=null){
							try{
								int cadenzaRispedizione = Integer.parseInt(gestioneErrore.getCadenzaRispedizione());
								this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
							}catch(Exception e)	{
								GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato: "+e.getMessage());
							}
						}
						return false;
					}else{
						this.riconsegna = false;
						return true;
					}	
				}
			}
		}



		// ESITO PROBLEM DETAIL
		ProblemRFC7807 problem = null;

		if(messageResponse!=null) {
			if(messageResponse instanceof OpenSPCoop2RestJsonMessage ){
				try{
					OpenSPCoop2RestJsonMessage msg = messageResponse.castAsRestJson();
					if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
						JsonDeserializer deserializer = new JsonDeserializer();
						problem = deserializer.fromString(msg.getContent(), false);
					}
				}catch(Exception e){
					throw new GestoreMessaggiException(e.getMessage(),e);
				}
			}
			else if(messageResponse instanceof OpenSPCoop2RestXmlMessage ){
				try{
					OpenSPCoop2RestXmlMessage msg = messageResponse.castAsRestXml();
					if(msg.hasContent() && msg.isProblemDetailsForHttpApis_RFC7807()) {
						XmlDeserializer deserializer = new XmlDeserializer();
						problem = deserializer.fromNode(msg.getContent(), false);
					}
				}catch(Exception e){
					throw new GestoreMessaggiException(e.getMessage(),e);
				}
			}
		}

		if(problem!=null){
			this.problem = problem;
			for(int i=0; i<gestioneErrore.sizeSoapFaultList();i++){
				GestioneErroreSoapFault gestore = gestioneErrore.getSoapFault(i);
				boolean match = true;

				if(gestore.getFaultCode()!=null){ // status in problem
					String codice = problem.getStatus()+"";

					if( gestore.getFaultCode().equalsIgnoreCase(codice) == false) {

						// provo a verificare con regular expr

						boolean matchRegExpr = false;
						try {
							matchRegExpr = RegularExpressionEngine.isMatch(codice, gestore.getFaultCode());
						}catch(RegExpNotFoundException notFound) {}
						catch(Exception e)	{
							GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultCode()+"' per problem status '"+codice+"' fallita: "+e.getMessage());
						}

						if(!matchRegExpr) {
							match = false; // non ha il codice definito
						}
					}

				}

				if(gestore.getFaultActor()!=null){ // type in problem
					String actor = problem.getType();

					if( gestore.getFaultActor().equalsIgnoreCase(actor) == false) {

						// provo a verificare con regular expr

						boolean matchRegExpr = false;
						try {
							matchRegExpr = RegularExpressionEngine.isMatch(actor, gestore.getFaultActor());
						}catch(RegExpNotFoundException notFound) {}
						catch(Exception e)	{
							GestoreErroreConnettore.log.error("Verifica espressione regolare '"+gestore.getFaultActor()+"' per problem type '"+actor+"' fallita: "+e.getMessage());
						}

						if(!matchRegExpr) {
							match = false; // non ha l'actor definito
						}
					}

				}

				if(gestore.getFaultString()!=null) { // claims

					Properties properties = PropertiesUtilities.convertTextToProperties(gestore.getFaultString());
					if(properties!=null && properties.size()>0) {

						Enumeration<?> en = properties.keys();
						while (en.hasMoreElements()) {
							String key = (String) en.nextElement();
							String value = properties.getProperty(key);

							if(ProblemConstants.CLAIM_TYPE.equals(key)) {
								if(problem.getType()==null || "".equals(problem.getType())) {
									match = false; 
									break;
								}
								else {
									if( problem.getType().equalsIgnoreCase(value) == false) {
										// provo a verificare con regular expr
										boolean matchRegExpr = false;
										try {
											matchRegExpr = RegularExpressionEngine.isMatch(problem.getType(), value);
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem type '"+problem.getType()+"' fallita: "+e.getMessage());
										}
										if(!matchRegExpr) {
											match = false; // non ha il valore definito
											break;
										}
									}
								}
							}

							else if(ProblemConstants.CLAIM_TITLE.equals(key)) {
								if(problem.getTitle()==null || "".equals(problem.getTitle())) {
									match = false; 
									break;
								}
								else {
									if( problem.getTitle().equalsIgnoreCase(value) == false) {
										// provo a verificare con regular expr
										boolean matchRegExpr = false;
										try {
											matchRegExpr = RegularExpressionEngine.isMatch(problem.getTitle(), value);
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem title '"+problem.getTitle()+"' fallita: "+e.getMessage());
										}
										if(!matchRegExpr) {
											match = false; // non ha il valore definito
											break;
										}
									}
								}
							}

							else if(ProblemConstants.CLAIM_STATUS.equals(key)) {
								if(problem.getStatus()==null) {
									match = false; 
									break;
								}
								else {
									if( problem.getStatus().toString().equalsIgnoreCase(value) == false) {
										// provo a verificare con regular expr
										boolean matchRegExpr = false;
										try {
											matchRegExpr = RegularExpressionEngine.isMatch(problem.getStatus().toString(), value);
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem status '"+problem.getStatus().toString()+"' fallita: "+e.getMessage());
										}
										if(!matchRegExpr) {
											match = false; // non ha il valore definito
											break;
										}
									}
								}
							}

							else if(ProblemConstants.CLAIM_DETAIL.equals(key)) {
								if(problem.getDetail()==null || "".equals(problem.getDetail())) {
									match = false; 
									break;
								}
								else {
									if( problem.getDetail().equalsIgnoreCase(value) == false) {
										// provo a verificare con regular expr
										boolean matchRegExpr = false;
										try {
											matchRegExpr = RegularExpressionEngine.isMatch(problem.getDetail(), value);
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem detail '"+problem.getDetail()+"' fallita: "+e.getMessage());
										}
										if(!matchRegExpr) {
											match = false; // non ha il valore definito
											break;
										}
									}
								}
							}

							else if(ProblemConstants.CLAIM_INSTANCE.equals(key)) {
								if(problem.getInstance()==null || "".equals(problem.getInstance())) {
									match = false; 
									break;
								}
								else {
									if( problem.getInstance().equalsIgnoreCase(value) == false) {
										// provo a verificare con regular expr
										boolean matchRegExpr = false;
										try {
											matchRegExpr = RegularExpressionEngine.isMatch(problem.getInstance(), value);
										}catch(RegExpNotFoundException notFound) {}
										catch(Exception e)	{
											GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem instance '"+problem.getInstance()+"' fallita: "+e.getMessage());
										}
										if(!matchRegExpr) {
											match = false; // non ha il valore definito
											break;
										}
									}
								}
							}

							else {

								if(problem.getCustom()==null || problem.getCustom().isEmpty() || !problem.getCustom().containsKey(key)) {
									match = false; 
									break;
								}
								else {
									Object valoreClaim = problem.getCustom().get(key);
									if(valoreClaim==null) {
										match = false; 
										break;
									}
									else {
										String v = valoreClaim.toString();
										if( v.equalsIgnoreCase(value) == false) {
											// provo a verificare con regular expr
											boolean matchRegExpr = false;
											try {
												matchRegExpr = RegularExpressionEngine.isMatch(v, value);
											}catch(RegExpNotFoundException notFound) {}
											catch(Exception e)	{
												GestoreErroreConnettore.log.error("Verifica espressione regolare '"+value+"' per problem '"+key+"' '"+v+"' fallita: "+e.getMessage());
											}
											if(!matchRegExpr) {
												match = false; // non ha il valore definito
												break;
											}
										}
									}
								}

							}
						}

					}
				}

				if( match ){
					if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestore.getComportamento())){
						try{
							this.errore = "errore applicativo, "+problem.getRaw();
						}catch(Exception e){
							this.errore = "errore applicativo (Problem Detail RFC 7807)";
							GestoreErroreConnettore.log.error("Serializzazione Problem Detail RFC 7807 non riuscita: "+e.getMessage(),e);
						}
						this.riconsegna = true;
						if(gestore.getCadenzaRispedizione()!=null){
							try{
								int cadenzaRispedizione = Integer.parseInt(gestore.getCadenzaRispedizione());
								this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
							}catch(Exception e)	{
								GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato (problem detail): "+e.getMessage());
							}
						}else if(gestioneErrore.getCadenzaRispedizione()!=null){
							try{
								int cadenzaRispedizione = Integer.parseInt(gestioneErrore.getCadenzaRispedizione());
								this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
							}catch(Exception e)	{
								GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato: "+e.getMessage());
							}
						}
						return false;
					}else{
						this.riconsegna = false;
						return true;
					}	
				}

			}
		}



		// ESITO CODICE TRASPORTO:
		for(int i=0; i<gestioneErrore.sizeCodiceTrasportoList();i++){

			GestioneErroreCodiceTrasporto gestore = gestioneErrore.getCodiceTrasporto(i);
			long valoreMinimo = Long.MIN_VALUE;
			long valoreMassimo = Long.MAX_VALUE;
			if(gestore.getValoreMinimo()!=null)
				valoreMinimo = gestore.getValoreMinimo().longValue();
			if(gestore.getValoreMassimo()!=null)
				valoreMassimo = gestore.getValoreMassimo().longValue();

			if(codiceTrasporto>=valoreMinimo &&
					codiceTrasporto<=valoreMassimo){

				// match
				if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestore.getComportamento())){
					//this.errore = "errore di trasporto, codice "+codiceTrasporto;
					this.errore = "errore "+formatProtocolloConnettore(protocolloConnettore)+codiceTrasporto;
					if(this.fault!=null){
						try{
							this.errore = this.errore + " (" +SoapUtils.safe_toString(messageResponse.getFactory(), this.fault, GestoreErroreConnettore.log)+ ")";
						}catch(Exception e){
							this.errore = "errore applicativo SoapFault";
							GestoreErroreConnettore.log.error("Serializzazione SOAPFault non riuscita: "+e.getMessage(),e);
						}
					}
					this.riconsegna = true;
					if(gestore.getCadenzaRispedizione()!=null){
						try{
							int cadenzaRispedizione = Integer.parseInt(gestore.getCadenzaRispedizione());
							this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
						}catch(Exception e)	{
							GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato (codice di trasporto): "+e.getMessage());
						}
					}else if(gestioneErrore.getCadenzaRispedizione()!=null){
						try{
							int cadenzaRispedizione = Integer.parseInt(gestioneErrore.getCadenzaRispedizione());
							this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
						}catch(Exception e)	{
							GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato: "+e.getMessage());
						}
					}
					return false;
				}else{
					this.riconsegna = false;
					return true;
				}
			}
		}


		// Match non trovato, assumo comportamento di default
		if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestioneErrore.getComportamento())){
			//this.errore = "errore di trasporto, codice "+codiceTrasporto;
			this.errore = "errore "+formatProtocolloConnettore(protocolloConnettore)+codiceTrasporto;
			if(this.fault!=null){
				try{
					this.errore = this.errore + " (" +SoapUtils.safe_toString(messageResponse.getFactory(), this.fault, GestoreErroreConnettore.log)+ ")";
				}catch(Exception e){
					this.errore = "errore applicativo SoapFault";
					GestoreErroreConnettore.log.error("Serializzazione SOAPFault non riuscita: "+e.getMessage(),e);
				}
			}
			this.riconsegna = true;
			if(gestioneErrore.getCadenzaRispedizione()!=null){
				try{
					int cadenzaRispedizione = Integer.parseInt(gestioneErrore.getCadenzaRispedizione());
					this.dataRispedizione = new java.sql.Timestamp(now.getTime()+(cadenzaRispedizione*60*1000));
				}catch(Exception e)	{
					GestoreErroreConnettore.log.error("Intervallo di rispedizione non impostato: "+e.getMessage());
				}
			}
			return false;
		}else{
			this.riconsegna = false;
			return true;
		}


	}

	/**
	 * Motivo errore consegna
	 * 
	 * @return motivo errore consegna
	 */
	public String getErrore() {
		return this.errore;
	}
	/**
	 * Indicazione se il msg deve essere riconsegnato o meno
	 * 
	 * @return true se il msg deve essere riconsegnato
	 */
	public boolean isRiconsegna() {
		return this.riconsegna;
	}

	/**
	 * Data di Rispedizione
	 * 
	 * @return data di spedizione se il msg deve essere riconsegnato
	 */
	public java.sql.Timestamp getDataRispedizione() {
		return this.dataRispedizione;
	}

	/**
	 * @return the fault
	 */
	public SOAPFault getFault() {
		return this.fault;
	}

	/**
	 * @return the problem
	 */
	public ProblemRFC7807 getProblem() {
		return this.problem;
	}


	public static String formatProtocolloConnettore(String protocollo) {
		if(protocollo==null || StringUtils.isEmpty(protocollo)) {
			return "";
		}
		return protocollo+" ";
	}
}
