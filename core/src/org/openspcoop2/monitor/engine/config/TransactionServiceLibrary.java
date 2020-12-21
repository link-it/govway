/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.monitor.engine.config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.xml.soap.SOAPEnvelope;

import org.slf4j.Logger;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.MessageType;
import org.openspcoop2.message.xml.DynamicNamespaceContextFactory;
import org.openspcoop2.protocol.sdk.diagnostica.MsgDiagnostico;
import org.openspcoop2.protocol.sdk.tracciamento.Traccia;
import org.openspcoop2.protocol.sdk.tracciamento.TracciamentoException;
import org.openspcoop2.utils.regexp.RegularExpressionEngine;
import org.openspcoop2.utils.xml.AbstractXPathExpressionEngine;
import org.openspcoop2.utils.xml.DynamicNamespaceContext;
import org.openspcoop2.utils.xml.XPathException;
import org.openspcoop2.utils.xml.XPathNotFoundException;
import org.openspcoop2.utils.xml.XPathNotValidException;
import org.openspcoop2.utils.xml.XPathReturnType;
import org.w3c.dom.NodeList;

import org.openspcoop2.core.commons.dao.DAOFactory;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazione;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazionePlugin;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneRisorsaContenuto;
import org.openspcoop2.monitor.engine.config.transazioni.ConfigurazioneTransazioneStato;
import org.openspcoop2.monitor.engine.config.transazioni.constants.PosizioneMascheramento;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoControllo;
import org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMascheramento;
import org.openspcoop2.core.transazioni.DumpContenuto;
import org.openspcoop2.core.transazioni.DumpMessaggio;
import org.openspcoop2.core.transazioni.Transazione;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.engine.transaction.TransactionContentUtils;
import org.openspcoop2.monitor.engine.transaction.TransactionManager;
import org.openspcoop2.monitor.sdk.plugins.ITransactionProcessing;
import org.openspcoop2.monitor.sdk.transaction.Transaction;

/**
 * TransactionServiceLibrary
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class TransactionServiceLibrary implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private BasicServiceLibrary basicServiceLibrary;
	private ConfigurazioneTransazione transactionActionLibrary;
	private ConfigurazioneTransazione transactionActionAllLibrary;

	public BasicServiceLibrary getBasicServiceLibrary() {
		return this.basicServiceLibrary;
	}
	public void setBasicServiceLibrary(BasicServiceLibrary basicServiceLibrary) {
		this.basicServiceLibrary = basicServiceLibrary;
	}
	public ConfigurazioneTransazione getTransactionActionLibrary() {
		return this.transactionActionLibrary;
	}
	public void setTransactionActionLibrary(
			ConfigurazioneTransazione transactionActionLibrary) {
		this.transactionActionLibrary = transactionActionLibrary;
	}
	public ConfigurazioneTransazione getTransactionActionAllLibrary() {
		return this.transactionActionAllLibrary;
	}
	public void setTransactionActionAllLibrary(
			ConfigurazioneTransazione transactionActionAllLibrary) {
		this.transactionActionAllLibrary = transactionActionAllLibrary;
	}





	// ****** STATI E RISORSE *************** 

	public TransactionInfo readStatoAndResources(OpenSPCoop2Message msg, DynamicNamespaceContextFactory dncFactory,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log, boolean isRichiesta,
			String idTransazione) throws Exception{
		TransactionInfo transactionInfo = new TransactionInfo();
		this._readStatoAndResources(null, msg, dncFactory, xpathEngine, debug, log, isRichiesta, idTransazione, true, true,
				transactionInfo);
		return transactionInfo;
	}
	public TransactionInfo readStato(OpenSPCoop2Message msg, DynamicNamespaceContextFactory dncFactory,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log, boolean isRichiesta,
			String idTransazione) throws Exception{
		TransactionInfo transactionInfo = new TransactionInfo();
		this._readStatoAndResources(null,  msg, dncFactory, xpathEngine, debug, log, isRichiesta, idTransazione, true, false,
				transactionInfo);
		return transactionInfo;
	}
	public TransactionInfo readResources(OpenSPCoop2Message msg, DynamicNamespaceContextFactory dncFactory,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log, boolean isRichiesta,
			String idTransazione, String stato) throws Exception{
		TransactionInfo transactionInfo = new TransactionInfo();
		transactionInfo.setStato(stato);
		this._readStatoAndResources(null,  msg, dncFactory, xpathEngine, debug, log, isRichiesta, idTransazione, false, true,
				transactionInfo);
		return transactionInfo;
	}
	private void _readStatoAndResources(SOAPEnvelope soapEnvelope,OpenSPCoop2Message msg, 
			DynamicNamespaceContextFactory dncFactory,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log, boolean isRichiesta,
			String idTransazione, boolean readStato, boolean readResources,
			TransactionInfo transactionInfo) throws Exception{
		// Ottengo SOAPEnvelope
		DynamicNamespaceContext dnc = null;

		if(readStato){
			// ** Esamino gli stati della transazione se la transazione attuale non ha gia assegnato uno stato **
			if(transactionInfo.getStato()==null){
				// Provo prima ad utilizzare lo stato definito in una azione specifica
				if(this.getTransactionActionLibrary()!=null){
					if(this.getTransactionActionLibrary().sizeConfigurazioneTransazioneStatoList()>0){
						if(soapEnvelope==null){
							soapEnvelope = msg.castAsSoap().getSOAPPart().getEnvelope();
						}
						if(dnc==null){
							if(MessageType.SOAP_11.equals(msg.getMessageType())){
								dnc = dncFactory.getNamespaceContextFromSoapEnvelope11(soapEnvelope);
							}
							else{
								dnc = dncFactory.getNamespaceContextFromSoapEnvelope12(soapEnvelope);
							}
						}
					}
					this.updateTransactionState(this.getTransactionActionLibrary().getConfigurazioneTransazioneStatoList(), 
							isRichiesta, soapEnvelope, msg, dnc, 
							idTransazione, xpathEngine, debug, log, transactionInfo);
				}
				// Provo poi ad utilizzare gli stati associati all'azione '*'
				if(transactionInfo.getStato()==null){
					if(this.getTransactionActionAllLibrary()!=null){
						if(this.getTransactionActionAllLibrary().sizeConfigurazioneTransazioneStatoList()>0){
							if(soapEnvelope==null){
								soapEnvelope = msg.castAsSoap().getSOAPPart().getEnvelope();
							}
							if(dnc==null){
								if(MessageType.SOAP_11.equals(msg.getMessageType())){
									dnc = dncFactory.getNamespaceContextFromSoapEnvelope11(soapEnvelope);
								}
								else{
									dnc = dncFactory.getNamespaceContextFromSoapEnvelope12(soapEnvelope);
								}
							}
						}
						updateTransactionState(this.getTransactionActionAllLibrary().getConfigurazioneTransazioneStatoList(), 
								isRichiesta, soapEnvelope, msg, dnc, 
								idTransazione, xpathEngine, debug, log, transactionInfo);
					}
				}
			}
		}
		
		
		if(readResources){

			// elaboro risorse
			List<ConfigurazioneTransazioneRisorsaContenuto> risorse = this.mergeServiceActionTransactionLibrary_resources();
	
			// XPATH
			Hashtable<String, String> notFoundRules = new Hashtable<String, String>();
			Hashtable<String, String> dirtyRules = new Hashtable<String, String>();
			if(risorse.size()>0){
	
				if(soapEnvelope==null){
					soapEnvelope = msg.castAsSoap().getSOAPPart().getEnvelope();
				}
				if(dnc==null){
					if(MessageType.SOAP_11.equals(msg.getMessageType())){
						dnc = dncFactory.getNamespaceContextFromSoapEnvelope11(soapEnvelope);
					}
					else{
						dnc = dncFactory.getNamespaceContextFromSoapEnvelope12(soapEnvelope);
					}
				}
				// NOTA!!!! I Fault devono essere analizzati, puo' darsi che gli stati o le risorse (errore) le debba estrarre da un SoapFault.
				//if(dnc.isSoapFault()){
				//	return; // non devo analizzare i soap fault.
				//}
	
				Vector<TransactionResource> contenuti = this.recuperaContenuti(isRichiesta, transactionInfo.getStato(), soapEnvelope, dnc, risorse, notFoundRules, dirtyRules,
						xpathEngine, debug, log);
				if(contenuti!=null){
					for (int i = 0; i < contenuti.size(); i++) {
						transactionInfo.getRisorse().add(contenuti.get(i));
					}
				}
	
			}
	
			//controllo se ci sono regole non trovate e/o "sporche"
	
			if(debug){
				StringBuilder sbNotFound = new StringBuilder();
				if(notFoundRules.size()>0){
					sbNotFound.append("Regole non trovate:");
					Enumeration<String> nfk = notFoundRules.keys();
					while (nfk.hasMoreElements()) {
						String k = (String) nfk.nextElement();
						sbNotFound.append("\n["+(k)+"]");
					}
				}
				log.debug(sbNotFound.toString());
			}
	
			StringBuilder sb = new StringBuilder();
			if(dirtyRules.size()>0){
				sb.append("\nRegole non valide:");
				Enumeration<String> drk = dirtyRules.keys();
				while (drk.hasMoreElements()) {
					String k = (String) drk.nextElement();
					String val = "\n["+k+"-"+(dirtyRules.get(k))+"]";
					sb.append(val);
	
				}
			}
	
			if(sb.length()>0){
				throw new TracciamentoException(sb.toString());
			}			
		}
	}

	private void updateTransactionState(List<ConfigurazioneTransazioneStato> transactionStates,boolean richiesta,
			SOAPEnvelope soapEnvelope,OpenSPCoop2Message msg,DynamicNamespaceContext dnc,String idTransazione,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log,
			TransactionInfo transactionInfo) throws Exception{
		for (int i = 0; i < transactionStates.size(); i++) {
			ConfigurazioneTransazioneStato stato = transactionStates.get(i);
			
			if(stato.isEnabled()==false){
				continue;
			}
			
			// controllo su richiesta/risposta
			boolean matchTipoMessaggio = false;
			if(richiesta){
				matchTipoMessaggio = org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio.RICHIESTA.equals(stato.getTipoMessaggio());
			}else{
				matchTipoMessaggio = org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio.RISPOSTA.equals(stato.getTipoMessaggio());
			}
			if(!matchTipoMessaggio){
				continue; 
			}
		
			// NOTA!!!! I Fault devono essere analizzati, puo' darsi che gli stati o le risorse (errore) le debba estrarre da un SoapFault.
			//if(dnc.isSoapFault()){
			//	return; // non devo analizzare i soap fault.
			//}
			
			if(TipoControllo.EXIST.equals(stato.getTipoControllo())){
				Object oString = null;
				try{
					oString = xpathEngine.getMatchPattern(soapEnvelope, dnc, stato.getXpath(), XPathReturnType.STRING);
				}catch (XPathNotFoundException nfe) {
					if(debug)
						log.debug("Stato ["+stato.getNome()+"] con tipoControllo["+stato.getTipoControllo()+"] (xpath:"+stato.getXpath()+") non riscontrato per la trasazione ["+idTransazione+"]: "+nfe.getMessage());
				}
				if(oString!=null){
					if(debug)
						log.info("Trovato stato ["+stato.getNome()+"] con tipoControllo["+stato.getTipoControllo()+"] (xpath:"+stato.getXpath()+") per la trasazione ["+idTransazione+"]");
					transactionInfo.setStato(stato.getNome());
					break;
				}
			}
			else{
				String oString = null;
				try{
					oString = (String) xpathEngine.getMatchPattern(soapEnvelope, dnc, stato.getXpath(), XPathReturnType.STRING);
				}catch (XPathNotFoundException nfe) {
					if(debug)
						log.debug("Stato ["+stato.getNome()+"] con tipoControllo["+stato.getTipoControllo()+"] (xpath:"+stato.getXpath()+") non riscontrato per la trasazione ["+idTransazione+"]: "+nfe.getMessage());
				}
				if(oString!=null){
					if(TipoControllo.EQUALS.equals(stato.getTipoControllo())){
						if(oString.equals(stato.getValore())){
							if(debug)
								log.info("Trovato stato ["+stato.getNome()+"] per trasazione ["+idTransazione+"]");
							transactionInfo.setStato(stato.getNome());
							break;
						}
					}else if(TipoControllo.MATCH.equals(stato.getTipoControllo())){
						if(RegularExpressionEngine.isMatch(oString, stato.getValore())){
							if(debug)
								log.info("Trovato stato ["+stato.getNome()+"] con tipoControllo["+stato.getTipoControllo()+"] (xpath:"+stato.getXpath()+") per la trasazione ["+idTransazione+"]");
							transactionInfo.setStato(stato.getNome());
							break;
						}
					}
				}		
			}
		}
	}
	
	private Vector<TransactionResource> recuperaContenuti(boolean richiesta,String stato,SOAPEnvelope soapEnvelope,DynamicNamespaceContext dnc,
			List<ConfigurazioneTransazioneRisorsaContenuto> risorse, Hashtable<String, String> rulesNotFuond, Hashtable<String, String> dirtyRules,
			AbstractXPathExpressionEngine xpathEngine, boolean debug, Logger log) throws Exception {
		
		Vector<TransactionResource> res = new Vector<TransactionResource>();
		
		if(dnc.isSoapBodyEmpty()){
			return res;
		}
		
		for (int i = 0; i < risorse.size(); i++) {
			
			ConfigurazioneTransazioneRisorsaContenuto risorsa = risorse.get(i);
			
			// check tipo messaggio
			boolean matchTipoMessaggio = false;
			if(richiesta){
				matchTipoMessaggio = org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio.RICHIESTA.equals(risorsa.getTipoMessaggio());
			}else{
				matchTipoMessaggio = org.openspcoop2.monitor.engine.config.transazioni.constants.TipoMessaggio.RISPOSTA.equals(risorsa.getTipoMessaggio());
			}
			if(!matchTipoMessaggio){
				continue;  // messaggio non compatibile con lo stato
			}
			
			// check stato
			if(stato!=null){
				if(risorsa.getIdConfigurazioneTransazioneStato()!=null){
					String statoRichiestoDallaRisorsa = risorsa.getIdConfigurazioneTransazioneStato().getStato();
					if(statoRichiestoDallaRisorsa!=null && !"*".equals(statoRichiestoDallaRisorsa)){
						// check che la risorsa in questione sia compatibili con lo stato che sto esaminando
						if(!stato.equals(statoRichiestoDallaRisorsa)){
							continue; // risorsa non compatibile con lo stato
						}
					}	
				}
			}
			else{
				// lo stato associato alla transazione è null.
				// se nella risorsa è richiesto uno stato specifico non devo proseguire
				if(risorsa.getIdConfigurazioneTransazioneStato()!=null){
					String statoRichiestoDallaRisorsa = risorsa.getIdConfigurazioneTransazioneStato().getStato();
					if(statoRichiestoDallaRisorsa!=null && !"*".equals(statoRichiestoDallaRisorsa)){
						continue; // risorsa non compatibile con lo stato
					}	
				}
			}
			
			String nomeProprieta = risorsa.getNome();
			String percorsoProprieta = risorsa.getXpath();
			try{
				// NOTA!!!! I Fault devono essere analizzati, puo' darsi che gli stati o le risorse (errore) le debba estrarre da un SoapFault.
				//if(dnc.isSoapFault()){
				//  non e' possibile raccogliere proprieta' se abbiamo un soap fault
				//	continue;
				//}
				
				// Provo a cercarlo prima come Node
				NodeList nodeList = null;
				Exception exceptionNodeSet = null;
				String risultato = null;
				try{
					nodeList = (NodeList) xpathEngine.getMatchPattern(soapEnvelope, dnc, percorsoProprieta,XPathReturnType.NODESET);
					if(nodeList!=null){
						risultato = xpathEngine.toString(nodeList);
					}
				}catch(Exception e){
					exceptionNodeSet = e;
				}
				
				// Se non l'ho trovato provo a cercarlo come string (es. il metodo sopra serve per avere l'xml, ma fallisce in caso di concat, in caso di errori di concat_openspcoop....)
				// Insomma il caso dell'xml sopra e' quello speciale, che pero' deve essere eseguito prima, perche' altrimenti il caso string sotto funziona sempre, e quindi non si ottiene mai l'xml.
				if(risultato==null || "".equals(risultato)){
					try{
						risultato = xpathEngine.getStringMatchPattern(soapEnvelope, dnc, percorsoProprieta);
					}catch(Exception e){
						if(exceptionNodeSet!=null){
							log.error("Errore avvenuto durante la getStringMatchPattern("+percorsoProprieta
									+") ("+e.getMessage()+") invocata in seguito all'errore dell'invocazione getMatchPattern("+
									percorsoProprieta+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
						}
						// lancio questo errore.
						if(e instanceof XPathNotFoundException){
							throw (XPathNotFoundException) e;
						}else if(e instanceof XPathNotValidException){
							throw (XPathNotValidException) e;
						}else if(e instanceof XPathException){
							throw (XPathException) e;
						}else{
							throw e;
						}
					}
				}
				
				if(risultato == null || "".equals(risultato)){
					if(exceptionNodeSet!=null){
						log.error("Non sono stati trovati risultati tramite l'invocazione del metodo getStringMatchPattern("+percorsoProprieta
								+") invocato in seguito all'errore dell'invocazione getMatchPattern("+
								percorsoProprieta+",NODESET): "+exceptionNodeSet.getMessage(),exceptionNodeSet);
					}
					rulesNotFuond.put(nomeProprieta,percorsoProprieta);
				}else{
					// TROVATO DA AGGIUNGERE AL DATABASE!
					//res.put(nomeProprieta+" - "+ percorsoProprieta, risultato);
					TransactionResource risorsaCalcolata = new TransactionResource();
					if(richiesta)
						risorsaCalcolata.setTipoMessaggio(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RICHIESTA_INGRESSO);
					else
						risorsaCalcolata.setTipoMessaggio(org.openspcoop2.core.transazioni.constants.TipoMessaggio.RISPOSTA_INGRESSO);
					risorsaCalcolata.setNome(nomeProprieta);
					risorsaCalcolata.setValore(risultato);
					res.add(risorsaCalcolata);
					if(debug){
						log.debug(nomeProprieta+" - "+ percorsoProprieta +" : "+risultato);
					}
				}
				
			}catch (XPathNotFoundException nfe) {
				// not found rule
				rulesNotFuond.put(nomeProprieta,nfe.getMessage());
			}catch (XPathNotValidException re) {
				// dirty rule
				log.error("Errore di validazione della query XPath ["+percorsoProprieta+"] durante la valutazione della proprieta ["+nomeProprieta+"]: "+re.getMessage(),re);
				dirtyRules.put(nomeProprieta,re.getMessage());
			}catch (XPathException re) {
				// dirty rule
				log.error("Errore durante la valutazione della proprieta ["+nomeProprieta+"] query XPath ["+percorsoProprieta+"]: "+re.getMessage(),re);
				dirtyRules.put(nomeProprieta,re.getMessage());
			}catch(Exception e){
				throw new Exception("Errore generico durante la valutazione della proprieta ["+nomeProprieta+"] query XPath ["+percorsoProprieta+"]: "+e.getMessage(),e);
			}
		}
		
		return res;
	}
	
	
	


	// ****** SDK BEFORE SAVE ON DATABASE*************** 

	public boolean processResourcesBeforeSaveOnDatabase(Transazione transazione,
			Traccia tracciaRichiesta, Traccia tracciaRisposta,
			List<MsgDiagnostico> msgDiagnostici,
			DumpMessaggio dumpMessaggioRichiestaIngresso, DumpMessaggio dumpMessaggioRichiestaUscita,
			DumpMessaggio dumpMessaggioRispostaIngresso, DumpMessaggio dumpMessaggioRispostaUscita, 
			String stato,
			Logger log, DAOFactory daoFactory) throws Exception{
		return this.processResourcesBeforeSaveOnDatabase(transazione,
				tracciaRichiesta,tracciaRisposta,
				msgDiagnostici,
				dumpMessaggioRichiestaIngresso, dumpMessaggioRichiestaUscita,
				dumpMessaggioRispostaIngresso, dumpMessaggioRispostaUscita,
				stato, 
				this.mergeServiceActionTransactionLibrary_resources(),
				this.mergeServiceActionTransactionLibrary_sdkPlugins(log),
				log,daoFactory);
	}
	public boolean processResourcesBeforeSaveOnDatabase(
			Transazione transazione,Traccia tracciaRichiesta, Traccia tracciaRisposta,
			List<MsgDiagnostico> msgDiagnostici,
			DumpMessaggio dumpMessaggioRichiestaIngresso, DumpMessaggio dumpMessaggioRichiestaUscita,
			DumpMessaggio dumpMessaggioRispostaIngresso, DumpMessaggio dumpMessaggioRispostaUscita, 
			String stato, 
			List<ConfigurazioneTransazioneRisorsaContenuto> listRisorseContenuto,
			List<ITransactionProcessing> listTransactionProcessing,
			Logger log, DAOFactory daoFactory) throws Exception{

		boolean updated = this.updateDumpMessaggio(dumpMessaggioRichiestaIngresso, listRisorseContenuto, stato) &&
				this.updateDumpMessaggio(dumpMessaggioRichiestaUscita, listRisorseContenuto, stato) &&
				this.updateDumpMessaggio(dumpMessaggioRispostaIngresso, listRisorseContenuto, stato) &&
				this.updateDumpMessaggio(dumpMessaggioRispostaUscita, listRisorseContenuto, stato);


		// SDK
		if(listTransactionProcessing!=null && listTransactionProcessing.size()>0){

			Transaction transactionSDK = new Transaction(log,daoFactory,transazione, 
					tracciaRichiesta, tracciaRisposta,
					msgDiagnostici);
			if(dumpMessaggioRichiestaIngresso!=null){
				TransactionManager.setContentResourcesInTransaction(transactionSDK, dumpMessaggioRichiestaIngresso);
			}
			if(dumpMessaggioRichiestaUscita!=null){
				TransactionManager.setContentResourcesInTransaction(transactionSDK, dumpMessaggioRichiestaUscita);
			}
			if(dumpMessaggioRispostaIngresso!=null){
				TransactionManager.setContentResourcesInTransaction(transactionSDK, dumpMessaggioRispostaIngresso);
			}
			if(dumpMessaggioRispostaUscita!=null){
				TransactionManager.setContentResourcesInTransaction(transactionSDK, dumpMessaggioRispostaUscita);
			}

			for (ITransactionProcessing iTransactionProcessing : listTransactionProcessing) {
				iTransactionProcessing.processRealTimeResourcesBeforeSaveOnDatabase(transactionSDK);
			}

			if(dumpMessaggioRichiestaIngresso!=null){
				if (TransactionManager.updateResources(dumpMessaggioRichiestaIngresso, transactionSDK)){
					updated = true;
				}
			}
			if(dumpMessaggioRichiestaUscita!=null){
				if (TransactionManager.updateResources(dumpMessaggioRichiestaUscita, transactionSDK)){
					updated = true;
				}
			}
			if(dumpMessaggioRispostaIngresso!=null){
				if (TransactionManager.updateResources(dumpMessaggioRispostaIngresso, transactionSDK)){
					updated = true;
				}
			}
			if(dumpMessaggioRispostaUscita!=null){
				if (TransactionManager.updateResources(dumpMessaggioRispostaUscita, transactionSDK)){
					updated = true;
				}
			}
		}

		return updated;
	}

	private boolean updateDumpMessaggio(DumpMessaggio dumpMessaggio, List<ConfigurazioneTransazioneRisorsaContenuto> listRisorseContenuto, String stato) throws Exception{

		boolean updated = false;

		if(dumpMessaggio!=null){

			// Risorse contenuto (configurate via govwayMonitor)
			if(dumpMessaggio.sizeContenutoList()>0){
				for (DumpContenuto dumpContenuto : dumpMessaggio.getContenutoList()) {

					for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : listRisorseContenuto) {

						if(configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato()!=null && 
								configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato()!=null &&
								!"".equals(configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato())){
							if(!configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato().equals(stato)){
								continue;
							}
						}

						if(configurazioneTransazioneRisorsaContenuto.getTipoMessaggio().toString().equals(dumpMessaggio.getTipoMessaggio().toString()) &&
								configurazioneTransazioneRisorsaContenuto.getNome().equals(dumpContenuto.getNome())){

							// mascheramento
							if(configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione()!=null &&
									configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione() == 1){

								if(TipoMascheramento.FISICO.equals(configurazioneTransazioneRisorsaContenuto.getTipoMascheramento())){
									TransactionContentUtils.setDumpContenutoValue(dumpContenuto, 
											this.mascheramento(configurazioneTransazioneRisorsaContenuto.getCarattereMaschera(), 
													configurazioneTransazioneRisorsaContenuto.getPosizionamentoMaschera(), 
													configurazioneTransazioneRisorsaContenuto.getNumeroCaratteriMaschera(), 
													TransactionContentUtils.getDumpContenutoValue(dumpContenuto)));
									updated = true;
								}

							}

							// compressione
							if(configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione()!=null &&
									configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione() == 1){
								TransactionContentUtils.compress(dumpContenuto, configurazioneTransazioneRisorsaContenuto.getTipoCompressione());
								updated = true;
							}

							break;
						}
					}

				}
			}
		}

		return updated;
	}


	







	// ****** SDK AFTER READ FROM DATABASE*************** 

	public boolean processResourcesAfterReadFromDatabase(Transazione transazione,DumpMessaggio dumpMessaggio,String stato,
			Logger log, DAOFactory daoFactory) throws Exception{
		return this.processResourcesAfterReadFromDatabase(transazione,dumpMessaggio, stato, 
				this.mergeServiceActionTransactionLibrary_resources(),
				this.mergeServiceActionTransactionLibrary_sdkPlugins(log),
				log,daoFactory);
	}
	public boolean processResourcesAfterReadFromDatabase(Transazione transazione,DumpMessaggio dumpMessaggio,String stato, 
			List<ConfigurazioneTransazioneRisorsaContenuto> list,
			List<ITransactionProcessing> listTransactionProcessing,
			Logger log, DAOFactory daoFactory) throws Exception{


		boolean updated = false;

		if(dumpMessaggio!=null){

			// Risorse contenuto (configurate via govwayMonitor)
			if(dumpMessaggio.sizeContenutoList()>0){
				for (DumpContenuto dumpContenuto : dumpMessaggio.getContenutoList()) {

					for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : list) {

						if(configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato()!=null && 
								configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato()!=null &&
								!"".equals(configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato())){
							if(!configurazioneTransazioneRisorsaContenuto.getIdConfigurazioneTransazioneStato().getStato().equals(stato)){
								continue;
							}
						}

						if(configurazioneTransazioneRisorsaContenuto.getTipoMessaggio().toString().equals(dumpMessaggio.getTipoMessaggio().toString()) &&
								configurazioneTransazioneRisorsaContenuto.getNome().equals(dumpContenuto.getNome())){

							// de-compressione
							if(configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione()!=null &&
									configurazioneTransazioneRisorsaContenuto.getAbilitaCompressione() == 1){
								TransactionContentUtils.decompress(dumpContenuto, configurazioneTransazioneRisorsaContenuto.getTipoCompressione());
								updated = true;
							}

							// de-mascheramento
							if(configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione()!=null &&
									configurazioneTransazioneRisorsaContenuto.getAbilitaAnonimizzazione() == 1){
								if(TipoMascheramento.LOGICO.equals(configurazioneTransazioneRisorsaContenuto.getTipoMascheramento())){
									TransactionContentUtils.setDumpContenutoValue(dumpContenuto, 
											this.mascheramento(configurazioneTransazioneRisorsaContenuto.getCarattereMaschera(), 
											configurazioneTransazioneRisorsaContenuto.getPosizionamentoMaschera(), 
											configurazioneTransazioneRisorsaContenuto.getNumeroCaratteriMaschera(), 
											TransactionContentUtils.getDumpContenutoValue(dumpContenuto)));
									updated = true;
								}
							}

							break;
						}
					}

				}
			}

			// SDK
			if(listTransactionProcessing!=null && listTransactionProcessing.size()>0){

				Transaction transactionSDK = new Transaction(log,daoFactory,transazione);
				TransactionManager.setContentResourcesInTransaction(transactionSDK, dumpMessaggio);

				for (ITransactionProcessing iTransactionProcessing : listTransactionProcessing) {
					iTransactionProcessing.processRealTimeResourcesAfterReadFromDatabase(transactionSDK);
				}

				if (TransactionManager.updateResources(dumpMessaggio, transactionSDK)){
					updated = true;
				}

			}
		}

		return updated;

	}






	// ****** UTILS *************** 

	@Override
	public String toString(){
		StringBuilder bf = new StringBuilder();

		if(this.basicServiceLibrary==null){
			bf.append("BasicServiceLibrary: notDefined");
			bf.append("\n");
		}
		else{
			bf.append(this.basicServiceLibrary.toString());
		}

		bf.append("TransactionActionLibrary: ");
		if(this.getTransactionActionLibrary()!=null){
			bf.append("presente size-plugins[").
			append(this.getTransactionActionLibrary().sizeConfigurazioneTransazionePluginList()).
			append("] plugins[").
			append(this.getTransactionActionLibrary().getConfigurazioneTransazionePluginList()).
			append(" size-stati[").
			append(this.getTransactionActionLibrary().sizeConfigurazioneTransazioneStatoList()).
			append("] stati[").
			append(this.getTransactionActionLibrary().getConfigurazioneTransazioneStatoList()).
			append("] size-risorse[").
			append(this.getTransactionActionLibrary().sizeConfigurazioneTransazioneRisorsaContenutoList()).
			append("] risorse[").
			append(this.getTransactionActionLibrary().getConfigurazioneTransazioneRisorsaContenutoList()).
			append("] enabled[").
			append(this.getTransactionActionLibrary().isEnabled()).
			append("]");
		}else{
			bf.append("-");
		}
		bf.append("\n");

		bf.append("TransactionActionAllLibrary: ");
		if(this.getTransactionActionAllLibrary()!=null){
			bf.append("presente size-plugins[").
			append(this.getTransactionActionAllLibrary().sizeConfigurazioneTransazionePluginList()).
			append("] plugins[").
			append(this.getTransactionActionAllLibrary().getConfigurazioneTransazionePluginList()).
			append("presente size-stati[").
			append(this.getTransactionActionAllLibrary().sizeConfigurazioneTransazioneStatoList()).
			append("] stati[").
			append(this.getTransactionActionAllLibrary().getConfigurazioneTransazioneStatoList()).
			append("] size-risorse[").
			append(this.getTransactionActionAllLibrary().sizeConfigurazioneTransazioneRisorsaContenutoList()).
			append("] risorse[").
			append(this.getTransactionActionAllLibrary().getConfigurazioneTransazioneRisorsaContenutoList()).
			append("] enabled[").
			append(this.getTransactionActionAllLibrary().isEnabled()).
			append("]");
		}else{
			bf.append("-");
		}
		bf.append("\n");


		return bf.toString();
	}

	public List<ConfigurazioneTransazioneStato> mergeServiceActionTransactionLibrary_states() throws Exception{

		List<ConfigurazioneTransazioneStato> list = new ArrayList<ConfigurazioneTransazioneStato>();
		List<String> idStati = new ArrayList<String>();

		// Leggo le risorse associate all'azione specifica
		if(this.transactionActionLibrary!=null && this.transactionActionLibrary.sizeConfigurazioneTransazioneStatoList()>0){
			for (ConfigurazioneTransazioneStato configurazioneTransazioneStato : this.transactionActionLibrary.getConfigurazioneTransazioneStatoList()) {
				if(configurazioneTransazioneStato.isEnabled()){
					list.add(configurazioneTransazioneStato);
					idStati.add(configurazioneTransazioneStato.getNome());
				}
			}
		}

		// Leggo le risorse associate all'azione '*'
		if(this.transactionActionAllLibrary!=null && this.transactionActionAllLibrary.sizeConfigurazioneTransazioneStatoList()>0){
			for (ConfigurazioneTransazioneStato configurazioneTransazioneStato : this.transactionActionAllLibrary.getConfigurazioneTransazioneStatoList()) {
				if(configurazioneTransazioneStato.isEnabled()){
					String idStato = configurazioneTransazioneStato.getNome();
					if(idStati.contains(idStato)==false){
	
						// inserisco solo gli stati il cui nome non siano gia stati utilizzati nell'azione specifica
	
						list.add(configurazioneTransazioneStato);
						idStati.add(idStato);
					}
				}
			}
		}

		return list;
	}

	public List<ConfigurazioneTransazioneRisorsaContenuto> mergeServiceActionTransactionLibrary_resources() throws Exception{

		List<ConfigurazioneTransazioneRisorsaContenuto> list = new ArrayList<ConfigurazioneTransazioneRisorsaContenuto>();
		List<String> idRisorse = new ArrayList<String>();

		// Leggo le risorse associate all'azione specifica
		if(this.transactionActionLibrary!=null && this.transactionActionLibrary.sizeConfigurazioneTransazioneRisorsaContenutoList()>0){
			for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : this.transactionActionLibrary.getConfigurazioneTransazioneRisorsaContenutoList()) {
				if(configurazioneTransazioneRisorsaContenuto.isEnabled()){
					list.add(configurazioneTransazioneRisorsaContenuto);
					//idRisorse.add(configurazioneTransazioneRisorsaContenuto.getNome()+"_"+configurazioneTransazioneRisorsaContenuto.getTipoMessaggio().name());
					idRisorse.add(configurazioneTransazioneRisorsaContenuto.getNome()); // è stato definito lo unique sul nome
				}
			}
		}

		// Leggo le risorse associate all'azione '*'
		if(this.transactionActionAllLibrary!=null && this.transactionActionAllLibrary.sizeConfigurazioneTransazioneRisorsaContenutoList()>0){
			for (ConfigurazioneTransazioneRisorsaContenuto configurazioneTransazioneRisorsaContenuto : this.transactionActionAllLibrary.getConfigurazioneTransazioneRisorsaContenutoList()) {
				if(configurazioneTransazioneRisorsaContenuto.isEnabled()){
					//String idRisorsa = configurazioneTransazioneRisorsaContenuto.getNome()+"_"+configurazioneTransazioneRisorsaContenuto.getTipoMessaggio().name();
					String idRisorsa = configurazioneTransazioneRisorsaContenuto.getNome(); // è stato definito lo unique sul nome
					if(idRisorse.contains(idRisorsa)==false){
	
						// inserisco solo le risorse il cui nome e tipo non siano gia stati utilizzati nell'azione specifica
	
						list.add(configurazioneTransazioneRisorsaContenuto);
						idRisorse.add(idRisorsa);
					}
				}
			}
		}

		return list;
	}

	public List<ITransactionProcessing> mergeServiceActionTransactionLibrary_sdkPlugins(Logger log) throws Exception{

		List<ITransactionProcessing> list = new ArrayList<ITransactionProcessing>();
		List<String> idRisorse = new ArrayList<String>();

		// Leggo le risorse associate all'azione specifica
		if(this.transactionActionLibrary!=null && this.transactionActionLibrary.getEnabled() 
				&& this.transactionActionLibrary.getConfigurazioneTransazionePluginList()!=null &&
						this.transactionActionLibrary.getConfigurazioneTransazionePluginList().size()>0){
			for (ConfigurazioneTransazionePlugin configTransazionePlugin : this.transactionActionLibrary.getConfigurazioneTransazionePluginList()) {
				if(configTransazionePlugin.isEnabled()){
					String className = configTransazionePlugin.getPlugin().getClassName();
					if(idRisorse.contains(className)==false){
						IDynamicLoader loader = DynamicFactory.getInstance().newDynamicLoader(configTransazionePlugin.getPlugin().getTipoPlugin(), configTransazionePlugin.getPlugin().getTipo(),
								className, log);
						ITransactionProcessing transactionProcessor = (ITransactionProcessing) loader.newInstance();
						idRisorse.add(className);
						list.add(transactionProcessor);
					}	
				}
			}
		}

		// Leggo le risorse associate all'azione '*'
		if(this.transactionActionAllLibrary!=null && this.transactionActionAllLibrary.getEnabled() 
				&& this.transactionActionAllLibrary.getConfigurazioneTransazionePluginList()!=null &&
						this.transactionActionAllLibrary.getConfigurazioneTransazionePluginList().size()>0){
			for (ConfigurazioneTransazionePlugin configTransazionePlugin : this.transactionActionAllLibrary.getConfigurazioneTransazionePluginList()) {
				if(configTransazionePlugin.isEnabled()){
					String className = configTransazionePlugin.getPlugin().getClassName();
					if(idRisorse.contains(className)==false){
						IDynamicLoader loader = DynamicFactory.getInstance().newDynamicLoader(configTransazionePlugin.getPlugin().getTipoPlugin(), configTransazionePlugin.getPlugin().getTipo(),
								className, log);
						ITransactionProcessing transactionProcessor = (ITransactionProcessing) loader.newInstance();
						idRisorse.add(className);
						list.add(transactionProcessor);
					}
				}
			}
		}

		return list;
	}

	private String mascheramento(char carattereMascheramento, PosizioneMascheramento posizioneMascheramento, int numero, String originale){
		StringBuilder bf = new StringBuilder();
		if(originale.length()<=numero || numero<=0  ){
			for (int i = 0; i < originale.length(); i++) {
				bf.append(carattereMascheramento);		
			}
		}
		else{
			if(PosizioneMascheramento.PRIMI.equals(posizioneMascheramento)){
				int i=0;
				for (; i < numero; i++) {
					bf.append(carattereMascheramento);		
				}
				for (; i < originale.length(); i++) {
					bf.append(originale.charAt(i));		
				}
			}
			else{
				int i=0;
				for (; i < (originale.length()-numero); i++) {
					bf.append(originale.charAt(i));		
				}
				for (; i < originale.length(); i++) {
					bf.append(carattereMascheramento);		
				}
			}
		}
		return bf.toString();
	}
}
