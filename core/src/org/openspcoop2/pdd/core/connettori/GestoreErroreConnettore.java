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



package org.openspcoop2.pdd.core.connettori;

import java.sql.Timestamp;
import java.util.Enumeration;

import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPFault;

import org.slf4j.Logger;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.GestioneErroreCodiceTrasporto;
import org.openspcoop2.core.config.GestioneErroreSoapFault;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SoapUtils;
import org.openspcoop2.pdd.core.GestoreMessaggiException;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.MessaggiFaultErroreCooperazione;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.date.DateManager;


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
	
	/** Logger utilizzato per debug. */
	private static Logger log = OpenSPCoop2Logger.getLoggerOpenSPCoopCore();


	/** GestoreErrore di default per il componente di integrazione */
	private static GestioneErrore gestioneErroreDefaultComponenteIntegrazione = null;
	/** GestoreErrore di default per il componente di cooperazione */
	private static GestioneErrore gestioneErroreDefaultComponenteCooperazione = null;


	public static GestioneErrore getGestioneErroreDefaultComponenteIntegrazione(){
		if(GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazione!=null){
			return GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazione;
		}else{
			GestioneErrore gestione = new GestioneErrore();
			// default si rispedisce
			gestione.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
			// si accetta il codice di trasporto 200
			GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
			codiceTrasporto.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
			codiceTrasporto.setValoreMinimo(new Integer("200"));
			codiceTrasporto.setValoreMassimo(new Integer("299"));
			gestione.addCodiceTrasporto(codiceTrasporto);


			// Qualsiasi fault si accetta, senza effettuare un re-invio. 
			GestioneErroreSoapFault faultAccetta = new GestioneErroreSoapFault();
			faultAccetta.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
			gestione.addSoapFault(faultAccetta);
			
			
			GestoreErroreConnettore.gestioneErroreDefaultComponenteIntegrazione = gestione;
			return gestione;
		}
	}

	public static GestioneErrore getGestioneErroreDefaultComponenteCooperazione(){

		if(GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazione!=null){
			return GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazione;
		}else{
			GestioneErrore gestione = new GestioneErrore();
			// default si rispedisce
			gestione.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG);
			// si accetta il codice di trasporto 200-299
			GestioneErroreCodiceTrasporto codiceTrasporto = new GestioneErroreCodiceTrasporto();
			codiceTrasporto.setComportamento(CostantiConfigurazione.GESTIONE_ERRORE_ACCETTA_MSG);
			codiceTrasporto.setValoreMinimo(new Integer("200"));
			codiceTrasporto.setValoreMassimo(new Integer("299"));
			gestione.addCodiceTrasporto(codiceTrasporto);


			// Per qualsiasi fault si effettua una rispedizione, a meno di fault
			// integrati in buste errore:

			// BUSTE ERRORE DI VALIDAZIONE
			// SoapActor == null
			// SoapFaultCode == soap:Client
			// SoapFaultString contain "*_001 - Formato Busta non corretto"
			ProtocolFactoryManager protocolFactoryManager = null;
			try {
				protocolFactoryManager = ProtocolFactoryManager.getInstance();
			} catch (Exception e) {
				// TODO Succede in caso di incostintenza nella configurazione. Che faccio?
			} 
			
			Enumeration<String> factoriesKeys =  protocolFactoryManager.getProtocolNames();
			while (factoriesKeys.hasMoreElements()) {
				
				String protocolName = factoriesKeys.nextElement();
				IProtocolFactory protocolFactory;
				org.openspcoop2.protocol.sdk.config.ITraduttore trasl = null;
				try {
					protocolFactory = protocolFactoryManager.getProtocolFactoryByName(protocolName);
					trasl = protocolFactory.createTraduttore();
				} catch (ProtocolException e) {
					// TODO Succede in caso di incostintenza nella configurazione. Che faccio?
					e.printStackTrace();
					continue;
				} 

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


			GestoreErroreConnettore.gestioneErroreDefaultComponenteCooperazione = gestione;
			return gestione;
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
	 * @param codiceTrasporto Codice Trasporto fornito dal connettore
	 * @param messageResponse Messaggio di risposta fornito dal connettore
	 * @return true se la consegna e' stata effettuata con successo, false altrimenti.
	 */
	public boolean verificaConsegna(GestioneErrore gestioneErrore,String msgErroreConnettore,Exception eccezioneErroreConnettore,
			long codiceTrasporto,OpenSPCoop2Message messageResponse) throws GestoreMessaggiException,javax.xml.soap.SOAPException,UtilsException{

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
		SOAPBody body = null;
		
		if(messageResponse!=null)
			body = messageResponse.getSOAPBody();

		if(body!=null && body.hasFault()){
			this.fault = body.getFault();
			for(int i=0; i<gestioneErrore.sizeSoapFaultList();i++){
				GestioneErroreSoapFault gestore = gestioneErrore.getSoapFault(i);
				boolean match = true;
				if(gestore.getFaultCode()!=null){
					if(gestore.getFaultCode().equalsIgnoreCase(this.fault.getFaultCodeAsQName().getLocalPart()) == false)
						match = false; // non ha il codice definito
				}
				if(gestore.getFaultActor()!=null){
					if(gestore.getFaultActor().equalsIgnoreCase(this.fault.getFaultActor()) == false)
						match = false; // non ha l'actor definito
				}
				if(gestore.getFaultString()!=null){
					if( (this.fault.getFaultString()==null) ||
						   (
							 (this.fault.getFaultString().contains(gestore.getFaultString()) == false) &&
							 (this.fault.getFaultString().contains(gestore.getFaultString().toLowerCase()) == false) &&
							 (this.fault.getFaultString().contains(gestore.getFaultString().toUpperCase()) == false)
						   )
						)
						match = false; // non ha il fault string definito
				}

				if( match ){
					if(CostantiConfigurazione.GESTIONE_ERRORE_RISPEDISCI_MSG.equals(gestore.getComportamento())){
						this.errore = "errore applicativo, "+SoapUtils.toString(this.fault);
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
					this.errore = "errore di trasporto, codice "+codiceTrasporto;
					if(this.fault!=null){
						this.errore = this.errore + " (" +SoapUtils.toString(this.fault)+ ")";
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
			this.errore = "errore di trasporto, codice "+codiceTrasporto;
			if(this.fault!=null){
				this.errore = this.errore + " (" +SoapUtils.toString(this.fault)+ ")";
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





}
