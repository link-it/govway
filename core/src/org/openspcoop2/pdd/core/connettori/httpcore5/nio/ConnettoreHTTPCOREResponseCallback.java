/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2024 Link.it srl (https://link.it). 
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

package org.openspcoop2.pdd.core.connettori.httpcore5.nio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreException;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.core.connettori.ConnettoreUtils;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ConnettoreHTTPCOREResponseCallback
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPCOREResponseCallback implements FutureCallback<ConnettoreHTTPCOREResponse> {
	private ConnettoreHTTPCORE connettore;
	private ConnettoreMsg request;
	private HttpBodyParameters httpBody;
	private boolean connettoreDebug;
	private ConnettoreLogger connettoreLogger;

	public ConnettoreHTTPCOREResponseCallback(ConnettoreHTTPCORE connettore, ConnettoreMsg request, HttpBodyParameters httpBody ) {
		this.connettore = connettore;
		this.request = request;
		this.httpBody = httpBody;
		
		this.connettoreDebug = this.connettore.isDebug();
		this.connettoreLogger = this.connettore.getLogger();
	}

	@Override
	public void completed(final ConnettoreHTTPCOREResponse responseParam) {

		OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
		Map<String, List<String>> connettorePropertiesTrasportoRisposta = this.connettore.getPropertiesTrasportoRisposta(); 
		
		try {
			HttpResponse response = responseParam.getHttpResponse();
			HttpEntity httpEntityResponse = responseParam.getEntity();
			
			if(this.connettoreDebug) {
				this.connettoreLogger.debug("NIO - Callback Response started after 'complete' event ...");
			}
			// Analisi MimeType e ContentLocation della risposta
			if(this.connettoreDebug)
				this.connettoreLogger.debug("Analisi risposta...");
			Header [] hdrRisposta = response.getHeaders();
			Map<String, List<String>> mapHeaderHttpResponse = new HashMap<>();
			if(hdrRisposta!=null){
				for (int i = 0; i < hdrRisposta.length; i++) {
					
					String key = null;
					String value = null;
					
					if(hdrRisposta[i].getName()==null){
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						if(this.connettoreDebug)
							this.connettoreLogger.debug("HTTP risposta ["+HttpConstants.RETURN_CODE+"] ["+hdrRisposta[i].getValue()+"]...");
						key = HttpConstants.RETURN_CODE;
						value = hdrRisposta[i].getValue();
					}
					else{
						if(this.connettoreDebug)
							this.connettoreLogger.debug("HTTP risposta ["+hdrRisposta[i].getName()+"] ["+hdrRisposta[i].getValue()+"]...");
						key = hdrRisposta[i].getName();
						value = hdrRisposta[i].getValue();
					}
					
					TransportUtils.addHeader(connettorePropertiesTrasportoRisposta, key, value);
					
					List<String> list = null;
					if(mapHeaderHttpResponse.containsKey(key)) {
						list = mapHeaderHttpResponse.get(key);
					}
					if(list==null) {
						list = new ArrayList<>();
						mapHeaderHttpResponse.put(key, list);
					}
					list.add(value);
				}
			}
			
			
			// TipoRisposta
			this.connettore.setTipoRisposta(TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_TYPE));
			
			// ContentLength
			String contentLengthHdr = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_LENGTH);
			if(contentLengthHdr!=null){
				this.connettore.setContentLength(Long.parseLong(contentLengthHdr));
			}
			else {
				if(httpEntityResponse!=null && httpEntityResponse.getContentLength()>0) {
					this.connettore.setContentLength(httpEntityResponse.getContentLength());
				}
			}
			
			
			// Parametri di imbustamento
			if(this.connettore.isSoap()){
				this.connettore.setImbustamentoConAttachment(false);
				if("true".equals(TransportUtils.getObjectAsString(mapHeaderHttpResponse, openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
					this.connettore.setImbustamentoConAttachment(true);
				}
				String mimeTypeAttachment = TransportUtils.getObjectAsString(mapHeaderHttpResponse, openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(mimeTypeAttachment==null)
					mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				this.connettore.setMimeTypeAttachment(mimeTypeAttachment);
				/**System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");*/
			}

			
			// Ricezione Risposta
			if(this.connettoreDebug)
				this.connettoreLogger.debug("Analisi risposta input stream e risultato http...");
			this.connettore.initConfigurationAcceptOnlyReturnCode_202_200();

			
			// return code
			this.connettore.setCodiceTrasporto(response.getCode());
			this.connettore.setResultHTTPMessage(response.getReasonPhrase());

			if(this.connettore.getCodiceTrasporto()<300) {
				if(this.connettore.isSoap() && this.connettore.isAcceptOnlyReturnCode_202_200() &&
					this.connettore.getCodiceTrasporto()!=200 && this.connettore.getCodiceTrasporto()!=202){
					throw new ConnettoreException("Return code ["+this.connettore.getCodiceTrasporto()+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
				}
				if(this.httpBody.isDoInput() && httpEntityResponse!=null){
					this.connettore.setInputStreamResponse(httpEntityResponse!=null ? httpEntityResponse.getContent() : null);
				}
			}else{
				if(this.connettore.getCodiceTrasporto()<400){
					String redirectLocation = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.REDIRECT_LOCATION);

					// 3XX
					if(this.connettore.isFollowRedirects()){

						if(redirectLocation==null){
							throw new ConnettoreException("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.connettore.getCodiceTrasporto()+")"); 
						}

						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori.CONNETTORE_LOCATION);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori.CONNETTORE_HTTP_REDIRECT_NUMBER);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE);
						this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_NUMBER, (this.connettore.getNumberRedirect()+1)+"" );
						if(this.connettore.getRouteRedirect()!=null){
							this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE, this.connettore.getRouteRedirect()+" -> "+redirectLocation );
						}else{
							this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}
						if(this.connettore.getOriginalAbsolutePrefixForRelativeRedirectLocation()==null) {
							this.connettore.setOriginalAbsolutePrefixForRelativeRedirectLocation(this.connettore.url.getProtocol()+"://"+this.connettore.url.getHost()+":"+this.connettore.url.getPort());
						}
						this.connettore.setRedirectLocation(redirectLocation); // per la prossima build()
						if(redirectLocation.startsWith("/")) {
							// relative
							this.connettore.setRedirectLocation(this.connettore.getOriginalAbsolutePrefixForRelativeRedirectLocation() + redirectLocation);
						}

						this.connettoreLogger.warn("(hope:"+(this.connettore.getNumberRedirect()+1)+") Redirect verso ["+redirectLocation+"] ...");

						if(this.connettore.getNumberRedirect()==this.connettore.getMaxNumberRedirects()){
							throw new ConnettoreException(ConnettoreUtils.getPrefixRedirect(this.connettore.getCodiceTrasporto(), redirectLocation)+"non consentita ulteriormente, sono già stati gestiti "+this.connettore.getMaxNumberRedirects()+" redirects: "+this.connettore.getRouteRedirect());
						}

						boolean acceptOnlyReturnCode307 = false;
						if(this.connettore.isSoap()) {
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.connettore.getIdModulo())){
								acceptOnlyReturnCode307 = openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
							}
							else{
								// InoltroBuste e InoltroRisposte
								acceptOnlyReturnCode307 = openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
							}
						}
						if(acceptOnlyReturnCode307 &&
							this.connettore.getCodiceTrasporto()!=307){
								throw new ConnettoreException("Return code ["+this.connettore.getCodiceTrasporto()+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							
						}

						org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE connettoreySyncRedirect = new org.openspcoop2.pdd.core.connettori.httpcore5.ConnettoreHTTPCORE();
						/**org.openspcoop2.pdd.core.PdDContext pddContext = new org.openspcoop2.pdd.core.PdDContext();
						for (String key : this.connettore.getPddContext().keys()) {
							pddContext.addObject(key, this.connettore.getPddContext().getObject(key));
						}*/
						connettoreySyncRedirect.init(this.connettore.getPddContext(), this.connettore.getProtocolFactory());
						connettoreySyncRedirect.setHttpMethod(this.connettore.getHttpMethod());
						IAsyncResponseCallback callback = this.request.getAsyncResponseCallback();
						try {
							this.request.setAsyncResponseCallback(null);
							@SuppressWarnings("unused")
							boolean success = connettoreySyncRedirect.send(null, this.request); // caching ricorsivo non serve
							
							this.connettore.setResponseMsg(connettoreySyncRedirect.getResponse());
							this.connettore.setCodiceTrasporto(connettoreySyncRedirect.getCodiceTrasporto());
							this.connettore.setResultHTTPMessage(connettoreySyncRedirect.getResultHTTPMessage());
							this.connettore.setContentLength(connettoreySyncRedirect.getContentLength());
							this.connettore.getPropertiesTrasportoRisposta().clear();
							this.connettore.getPropertiesTrasportoRisposta().putAll(connettoreySyncRedirect.getPropertiesTrasportoRisposta());
							this.connettore.setTipoRisposta(TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_TYPE));
							this.connettore.setInputStreamResponse(connettoreySyncRedirect.getIsResponse());
							/**this.connettore.setAsyncInvocationSuccess(success);
							return;*/
						}finally {
							this.request.setAsyncResponseCallback(callback);
						}

					}else{
						if(this.connettore.isSoap()) {
							throw new ConnettoreException(ConnettoreUtils.getPrefixRedirect(this.connettore.getCodiceTrasporto(), redirectLocation)+"non attiva");
						}
						else {
							this.connettoreLogger.debug(ConnettoreUtils.getPrefixRedirect(this.connettore.getCodiceTrasporto(), redirectLocation)+"non attiva");
							
							if(this.connettore.internalGetLocationValue()!=null && redirectLocation!=null){
								this.connettore.setLocation(this.connettore.internalGetLocationValue()+" [redirect-location: "+redirectLocation+"]");
					    	}
							
							/**if(this.httpBody.isDoInput()){*/
							if(httpEntityResponse!=null) {
								this.connettore.setInputStreamResponse(httpEntityResponse.getContent());
							}
						}
					}
				}
				else {
					if(httpEntityResponse!=null) {
						this.connettore.setInputStreamResponse(httpEntityResponse.getContent());
					}
				}
			}
			
			




			/* ------------  PostOutRequestHandler ------------- */
			this.connettore.postOutRequest();




			/* ------------  PreInResponseHandler ------------- */
			this.connettore.preInResponse();

			// Lettura risposta parametri NotifierInputStream per la risposta
			this.connettore.setNotifierInputStreamParams(null);
			if(this.connettore.getPreInResponseContext()!=null){
				this.connettore.setNotifierInputStreamParams(this.connettore.getPreInResponseContext().getNotifierInputStreamParams());
			}


			/* ------------  Gestione Risposta ------------- */

			this.connettore.normalizeInputStreamResponse(this.connettore.readConnectionTimeout, this.connettore.readConnectionTimeoutConfigurazioneGlobale);

			this.connettore.initCheckContentTypeConfiguration();

			if(this.connettore.isDumpBinarioRisposta() &&
				!this.connettore.dumpResponse(connettorePropertiesTrasportoRisposta)) {
				this.connettore.setAsyncInvocationSuccess(false);
				return;
			}

			if(this.connettore.isRest()){

				if(!this.connettore.doRestResponse()){
					this.connettore.setAsyncInvocationSuccess(false);
					return;
				}

			}
			else{

				if(!this.connettore.doSoapResponse()){
					this.connettore.setAsyncInvocationSuccess(false);
					return;
				}

			}

			if(this.connettoreDebug)
				this.connettoreLogger.info("Gestione invio/risposta http effettuata con successo",false);

			this.connettore.setAsyncInvocationSuccess(true);
			return;

		}  catch(Exception e){ 
			
			this.writeExceptionResponse(e);
			
		} finally {
			
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.COMPLETED);
			
		}

		
	}

	@Override
	public void failed(final Exception e) {
		try {
			
			if(this.connettoreDebug) {
				this.connettoreLogger.debug("NIO - Callback Response started after 'failed' event ...");
			}
			
			this.writeExceptionResponse(e);
			
		} finally {
			
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.FAILED);
			
		}
	}

	@Override
	public void cancelled() {
		
		try {
			
			this.writeExceptionResponse(new Exception("Cancelled") );
			
		} finally {
			
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.CANCELLED);
			
		}
		
	}
	
	private void notifyCallbackFinished(AsyncResponseCallbackClientEvent clientEvent) {
		// se per caso non l'ho ancora chiuso lo faccio
		if(this.connettore.cloasebleDumpBout!=null) { 
			try {
				this.connettore.cloasebleDumpBout.clearResources();
				this.connettore.cloasebleDumpBout = null;
			}catch(Exception t) {
				this.connettoreLogger.error(ConnettoreHTTPCORE.MSG_RELEASE_RESOURCES_FAILED+t.getMessage(),t);
			}
		}
		this.connettore.asyncComplete(clientEvent);
		if(this.connettoreDebug) {
			this.connettoreLogger.debug("NIO - Callback Response finished");
		}
	}
	
	private void writeExceptionResponse(final Exception e) {
		this.connettore.setEccezioneProcessamento(e);
		String msgErrore = this.connettore.readExceptionMessageFromException(e);
		if(this.connettore.isGenerateErrorWithConnectorPrefix()) {
			this.connettore.setErrore("Errore avvenuto durante la consegna HTTP: "+msgErrore);
		}
		else {
			this.connettore.setErrore(msgErrore);
		}
		this.connettoreLogger.error("Errore avvenuto durante la consegna HTTP: "+msgErrore,e);
		
		this.connettore.processConnectionTimeoutException(this.connettore.connectionTimeout, this.connettore.connectionTimeoutConfigurazioneGlobale, e, msgErrore);
		
		this.connettore.processReadTimeoutException(this.connettore.readConnectionTimeout, this.connettore.readConnectionTimeoutConfigurazioneGlobale, e, msgErrore);
		
		this.connettore.setAsyncInvocationSuccess(false);
	}
}