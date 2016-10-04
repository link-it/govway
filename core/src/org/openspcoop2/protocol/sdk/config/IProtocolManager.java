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

package org.openspcoop2.protocol.sdk.config;

import java.util.Map;

import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.SOAPVersion;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.SOAPFaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.resources.TransportRequestContext;
import org.openspcoop2.utils.resources.TransportResponseContext;

/**
 * Interfaccia del Manager del Protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProtocolManager {

	public IProtocolFactory getProtocolFactory();
	

	
	/* *********** VALIDAZIONE/GENERAZIONE BUSTE ******************* */

	/**
	 * Indicazione se la busta e' una busta di servizio
	 *   
	 * @return Indicazione se la busta e' una busta di servizio
	 */
	public boolean isBustaServizio(Busta busta);
	
	/**
     * Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
     *   
     * @return Keyword utilizzata per identificare il tipo mittente di una busta dove il tipo mittente non e' indicato
     * 
     */
    public String getKeywordTipoMittenteSconosciuto();
    
    /**
     * Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
     *   
     * @return Keyword utilizzata per identificare il mittente di una busta dove il tipo mittente non e' indicato
     * 
     */
    public String getKeywordMittenteSconosciuto();
	
	/**
	 * Restituisce l'intervallo di scadenza delle buste
	 *
	 * @return Restituisce l'intervallo di scadenza delle buste
	 * 
	 */
	public long getIntervalloScadenzaBuste();
	
	/**
	 * Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 *   
	 * @return Indicazione se devono essere generati in risposte errore, elementi non validabili rispetto xsd.
	 * 
	 */
	public boolean isGenerazioneElementiNonValidabiliRispettoXSD();
	
	/**
	 * Indicazione se ritenere errore eccezioni di livello non gravi
	 *   
	 * @return Indicazione se ritenere errore eccezioni di livello non gravi
	 * 
	 */
	public boolean isIgnoraEccezioniNonGravi();
	
    /**
     * Indicazione se generare MessaggipErrore Processamento senza ListaEccezione
     *   
     * @return Indicazione se generare MessaggiErrore Processamento senza ListaEccezione
     * 
     */
    public boolean isGenerazioneListaEccezioniErroreProcessamento();
	
	
	
	
	
	
	
	/* *********** SOAP Fault della Porta ******************* */
	
	/**
     * Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore) di protocollo
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_001 (senza buste Errore) di protocollo
     * 
     */
	public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneValidazione();
	
	/**
     * Indicazione se generare i details in caso di SOAPFault *_300 di protocollo
     *   
     * @return Indicazione se generare i details in caso di SOAPFault *_300 di protocollo
     * 
     */
    public boolean isGenerazioneDetailsSOAPFaultProtocollo_EccezioneProcessamento();
    
    /**
     * Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di SOAPFault *_300 lo stack trace
     *   
     * @return Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di SOAPFault *_300 lo stack trace
     * 
     */
    public boolean isGenerazioneDetailsSOAPFaultProtocolloConStackTrace();
    
    /**
     * Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di SOAPFault informazioni generiche
     *   
     * @return Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di SOAPFault informazioni generiche
     * 
     */
	public boolean isGenerazioneDetailsSOAPFaultProtocolloConInformazioniGeneriche();
	
	 /**
     * Indicazione se generare i details in Casi di errore 5XX in fase di integrazione
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreServer();

	 /**
     * Indicazione se generare i details in Casi di errore 4XX in fase di integrazione
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsSOAPFaultIntegratione_erroreClient();
	
	 /**
     * Indicazione se generare nei details lo stack trace all'interno in fase di integrazione
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsSOAPFaultIntegrationeConStackTrace();

    /**
     * Indicazione se generare all'interno dei fault di integrazione, nei details, in caso di SOAPFault informazioni generiche
     * Se viene ritornato l'indicazione di usare il default del servizio applicativo
     * viene utilizzato il comportamento associato al servizio applicativo fruitore, riguardante la generazione di un fault code generico
     *   
     * @return Indicazione se generare all'interno dei fault di integrazione, nei details, in caso di SOAPFault informazioni generiche
     * 
     */
	public SOAPFaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsSOAPFaultIntegrazione();
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultApplicativo originale
	 */
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultApplicativo();
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel SoapFaultPdD originale
	 */
	public Boolean isAggiungiDetailErroreApplicativo_SoapFaultPdD();
	
	
	
	
	
	/* *********** INTEGRAZIONE ******************* */
	
    /**
     * Genera un insieme di informazioni di integrazione da inserire nel tipo di integrazione indicato come parametro
     * 
     * @param busta Busta
     * @param isRichiesta indicazione se si tratta del messaggio di richiesta o di risposta
     * @param tipoIntegrazione tipo di integrazione
     * @return Informazioni di integrazione da ritornare sul canale indicato dal tipo di integrazione
     * @throws ProtocolException
     */
    public Map<String, String> buildIntegrationProperties(Busta busta, boolean isRichiesta, TipoIntegrazione tipoIntegrazione) throws ProtocolException;
    
    /**
     * Aggiorna il messaggio in funzione del protocollo
     * 
     * @param soapVersion soapVersion
     * @param msg messaggio
     * @param busta Busta
     * @return Messaggio aggiornato
     * @throws ProtocolException
     */
    public OpenSPCoop2Message updateOpenSPCoop2MessageRequest(SOAPVersion soapVersion,OpenSPCoop2Message msg, Busta busta) throws ProtocolException;
    
    /**
     * Aggiorna il messaggio in funzione del protocollo
     * 
     * @param soapVersion soapVersion
     * @param msg messaggio
     * @param busta Busta
     * @param notifierInputStreamParams notifierInputStreamParams
     * @return Messaggio aggiornato
     * @throws ProtocolException
     */
    public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(SOAPVersion soapVersion,OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext) throws ProtocolException;
    
	
	
    
    
	
	/* *********** ALTRO ******************* */
	
	 /**
     * Il carico http di risposta per un profilo oneway (e per asincroni in modalita asincrona) non dovrebbe contenere alcun messaggio applicativo,
     * come viene descritto dalla specifica.
 	 * Alcuni framework SOAP, invece, tendono a ritornare come messaggi di risposta a invocazioni di operation che non prevedono un output:
 	 * - SoapEnvelope con SoapBody empty (es. <soapenv:Body />)
 	 * - SoapEnvelope contenente msg applicativi con root element vuoto (es. <soapenv:Body><operationResponse/></soapenv:Body>)
 	 * - ....
 	 * La seguente opzione permette di forzare un carico http vuoto, nei casi sopra descritti,
 	 * per la risposta generata dalla PdD in seguito alla gestione dei profili oneway (e asincroni in modalita asincrona)
	 *   
	 * @return Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway
	 * 
	 */
    public boolean isHttpEmptyResponseOneWay();
	
    /**
     * Il return code di una risposta http, come descritto nella specifica http://www.ws-i.org/profiles/basicprofile-1.1.html (3.4.4),
     * puo' assumere entrambi i valori 200 o 202, in caso il carico http di risposta non contiene una soap envelope.
     * La seguente opzione permette di impostare il return code generato dalla PdD in seguito alla gestione dei profili oneway (e asincroni in modalita asincrona)
     *   
     * @return Restituisce l'indicazione su come impostare il return code http di risposta per un profilo oneway
     * 
     */
    public Integer getHttpReturnCodeEmptyResponseOneWay();
    
    /**
     * Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway puo' essere:
 	 * - vuoto con codice http 202 (true)
	 * - msg soap vuoto con codice http 200 (false)
	 *   
	 * @return Restituisce l'indicazione su come impostare il Carico http di risposta per un profilo oneway
	 * 
	 */
    public boolean isHttpOneWay_PD_HTTPEmptyResponse();
    
    /**
     * Una risposta ben formata (envelope SOAP senza Fault) presente in un http-body di una risposta insieme ad un codice di trasporto 500
     * non è compatibile con quanto indicato nel basic profile (R1111, (http://www.ws-i.org/profiles/basicprofile-1.1.html#HTTP_Success_Status_Codes).
     * In particolare non e' chiaro quale sia il senso di questa risposta associata ad un profilo di collaborazione OneWay e quindi quale sia un modo migliore per gestirlo.
     * L'opzione seguente indica se far terminare la transazione con errore o continuarla normalmente la gestione utilizzando la risposta ritornata sul codice di trasporto 500.
     */
    public boolean isBlockedTransaction_responseMessageWithTransportCodeError();
    
    
	
}
