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

package org.openspcoop2.protocol.sdk.config;

import java.util.Map;

import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.protocol.sdk.Busta;
import org.openspcoop2.protocol.sdk.IComponentFactory;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.FaultIntegrationGenericInfoMode;
import org.openspcoop2.protocol.sdk.constants.TipoIntegrazione;
import org.openspcoop2.protocol.sdk.registry.IRegistryReader;
import org.openspcoop2.utils.io.notifier.NotifierInputStreamParams;
import org.openspcoop2.utils.transport.TransportRequestContext;
import org.openspcoop2.utils.transport.TransportResponseContext;

/**
 * Interfaccia del Manager del Protocollo
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public interface IProtocolManager extends IComponentFactory {
	

	
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
	
	
	
	
	
	
	
    /* *********** Fault della Porta (Protocollo, Porta Applicativa) ******************* */
	
	/**
     * Indicazione se generare i details in caso di Fault *_001 (senza buste Errore) di protocollo
     *   
     * @return Indicazione se generare i details in caso di Fault *_001 (senza buste Errore) di protocollo
     * 
     */
	public boolean isGenerazioneDetailsFaultProtocollo_EccezioneValidazione();
	
	/**
     * Indicazione se generare i details in caso di Fault *_300 di protocollo
     *   
     * @return Indicazione se generare i details in caso di Fault *_300 di protocollo
     * 
     */
    public boolean isGenerazioneDetailsFaultProtocollo_EccezioneProcessamento();
    
    /**
     * Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di Fault *_300 lo stack trace
     *   
     * @return Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di Fault *_300 lo stack trace
     * 
     */
    public boolean isGenerazioneDetailsFaultProtocolloConStackTrace();
    
    /**
     * Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di Fault informazioni generiche
     *   
     * @return Indicazione se generare all'interno dei fault di protocollo, nei details, in caso di Fault informazioni generiche
     * 
     */
	public boolean isGenerazioneDetailsFaultProtocolloConInformazioniGeneriche();
	
	
	
	
	/* *********** Fault della Porta (Integrazione, Porta Delegata) ******************* */
	
	 /**
     * Indicazione se generare i details in Casi di errore 5XX in fase di integrazione
     *   
     * @return Indicazione se generare i details in Casi di errore 5XX in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsFaultIntegratione_erroreServer();

	 /**
     * Indicazione se generare i details in Casi di errore 4XX in fase di integrazione
     *   
     * @return Indicazione se generare i details in Casi di errore 4XX in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsFaultIntegratione_erroreClient();
	
	 /**
     * Indicazione se generare nei details lo stack trace all'interno in fase di integrazione
     *   
     * @return Indicazione se generare nei details lo stack trace all'interno in fase di integrazione
     * 
     */
	public boolean isGenerazioneDetailsFaultIntegrationeConStackTrace();

    /**
     * Indicazione se generare all'interno dei fault di integrazione, nei details, in caso di Fault informazioni generiche
     * Se viene ritornato l'indicazione di usare il default del servizio applicativo
     * viene utilizzato il comportamento associato al servizio applicativo fruitore, riguardante la generazione di un fault code generico
     *   
     * @return Indicazione se generare all'interno dei fault di integrazione, nei details, in caso di Fault informazioni generiche
     * 
     */
	public FaultIntegrationGenericInfoMode getModalitaGenerazioneInformazioniGeneriche_DetailsFaultIntegrazione();
	
	
	
	
	/* *********** Fault della Porta (Generati dagli attori esterni) ******************* */
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel FaultApplicativo originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel FaultApplicativo originale
	 */
	public Boolean isAggiungiDetailErroreApplicativo_FaultApplicativo();
	
	/**
	 * Indicazione se aggiungere un detail contenente descrizione dell'errore nel FaultPdD originale
	 * 
	 * @return Indicazione se aggiungere un detail contenente descrizione dell'errore nel FaultPdD originale
	 */
	public Boolean isAggiungiDetailErroreApplicativo_FaultPdD();
	
	
	
	
	
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
     * @param msg messaggio
     * @param busta Busta
     * @return Messaggio aggiornato
     * @throws ProtocolException
     */
    public OpenSPCoop2Message updateOpenSPCoop2MessageRequest(OpenSPCoop2Message msg, Busta busta,
    		IRegistryReader registryReader) throws ProtocolException;
    
    /**
     * Aggiorna il messaggio in funzione del protocollo
     * 
     * @param msg messaggio
     * @param busta Busta
     * @param notifierInputStreamParams notifierInputStreamParams
     * @return Messaggio aggiornato
     * @throws ProtocolException
     */
    public OpenSPCoop2Message updateOpenSPCoop2MessageResponse(OpenSPCoop2Message msg, Busta busta, 
    		NotifierInputStreamParams notifierInputStreamParams, 
    		TransportRequestContext transportRequestContext, TransportResponseContext transportResponseContext,
    		IRegistryReader registryReader,
    		boolean integration) throws ProtocolException;
	
	
	
	
	
	/* *********** CONNETTORE ******************* */
	
	/**
	 * Indica se la PdD deve inoltrare tutte le richieste verso il dominio esterno ad un connettore statico
	 * 
	 * @return true se la PdD deve inoltrare tutte le richieste verso il dominio esterno ad un connettore statico
	 * @throws ProtocolException
	 */
	public boolean isStaticRoute()  throws ProtocolException;
	
	/**
	 * Ritorna il connettore che la PdD deve utilizzare per la spedizione verso il dominio esterno rappresentato dai dati forniti nei parametri.
	 * Permette di personalizzare a livello di protocollo il connettore utilizzato dalla PdD indipendentemente da quanto descritto nel registro.
	 * 
	 * @param idSoggettoMittente soggettoFruitore
	 * @param idServizio servizio
	 * @return Connettore da utilizzare per la spedizione verso il dominio esterno, indipendentemente da quanto descritto nel registro.
	 */
	public org.openspcoop2.core.registry.Connettore getStaticRoute(IDSoggetto idSoggettoMittente, IDServizio idServizio,
			IRegistryReader registryReader) throws ProtocolException;
	
	/**
	 * Indica se un http return code redirect '3XX' deve essere segnalato con errore o meno.
	 * 
	 * @param serviceBinding ServiceBinding (REST/SOAP)
	 * @return indicazione se un http return code redirect '3XX' deve essere segnalato con errore (false) o come consegna con successo (true).
	 * @throws ProtocolException
	 */
	public boolean isSuccessfulHttpRedirectStatusCode(ServiceBinding serviceBinding) throws ProtocolException;
	
    
    
	
	/* *********** ALTRO ******************* */
	
	 /**
     * Il carico http di risposta per un profilo oneway (e per asincroni in modalita asincrona) non dovrebbe contenere alcun messaggio applicativo,
     * come viene descritto dalla specifica.
 	 * Alcuni framework SOAP, invece, tendono a ritornare come messaggi di risposta a invocazioni di operation che non prevedono un output:
 	 * - SoapEnvelope con SoapBody empty (es. &lt;soapenv:Body /&gt;)
 	 * - SoapEnvelope contenente msg applicativi con root element vuoto (es.  &lt;soapenv:Body &tt; &lt;operationResponse/ &gt; &lt;/soapenv:Body &gt;)
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
