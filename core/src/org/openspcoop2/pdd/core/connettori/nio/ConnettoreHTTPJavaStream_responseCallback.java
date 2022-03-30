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

package org.openspcoop2.pdd.core.connettori.nio;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.core.connettori.ConnettoreLogger;
import org.openspcoop2.pdd.core.connettori.ConnettoreMsg;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.pdd.services.connector.AsyncResponseCallbackClientEvent;
import org.openspcoop2.pdd.services.connector.IAsyncResponseCallback;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;

/**
 * ConnettoreHTTPJava_responseCallback
 *
 *
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreHTTPJavaStream_responseCallback {
	private ConnettoreHTTPJavaStream connettore;
	private ConnettoreMsg request;
	private HttpBodyParameters httpBody;
	private boolean connettore_debug;
	private ConnettoreLogger connettore_logger;

	public ConnettoreHTTPJavaStream_responseCallback( ConnettoreHTTPJavaStream connettore, ConnettoreMsg request, HttpBodyParameters httpBody ) {
		this.connettore = connettore;
		this.request = request;
		this.httpBody = httpBody;
		
		this.connettore_debug = this.connettore.isDebug();
		this.connettore_logger = this.connettore.getLogger();
	}

	public void completed( final ConnettoreHTTPJavaStream_responseConsumer responseParam ) {
		OpenSPCoop2Properties openspcoopProperties = OpenSPCoop2Properties.getInstance();
		
		Map<String, List<String>> connettore_propertiesTrasportoRisposta = this.connettore.getPropertiesTrasportoRisposta(); 
		
		try {
			if(this.connettore_debug) {
				this.connettore_logger.debug("NIO - Callback Response started after 'complete' event ...");
			}
			// Analisi MimeType e ContentLocation della risposta
			if(this.connettore_debug)
				this.connettore_logger.debug("Analisi risposta...");
			HttpHeaders hdrRisposta = responseParam.getHeaders();
			Map<String, List<String>> mapHeaderHttpResponse = new HashMap<String, List<String>>();
			if ( hdrRisposta != null ) {
				hdrRisposta.map().forEach( (name,lv) -> {
					String key;

					if ( name == null ) {
						// Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						if ( this.connettore_debug )
							this.connettore_logger.debug("HTTP risposta ["+HttpConstants.RETURN_CODE+"] ["+ lv +"]...");
						key = HttpConstants.RETURN_CODE;
					} else {
						if ( this.connettore_debug )
							this.connettore_logger.debug("HTTP risposta [" + name +"] ["+ lv +"]...");
						key = name;
					}
					if ( lv != null ) {
						lv.forEach( (v) -> TransportUtils.addHeader( connettore_propertiesTrasportoRisposta, key, v ) );
						mapHeaderHttpResponse.put( key, lv );
					}
				});
			}

			// TipoRisposta
			this.connettore.setTipoRisposta(TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_TYPE));
			
			// ContentLength
			String contentLengthHdr = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.CONTENT_LENGTH);
			if(contentLengthHdr!=null){
				this.connettore.setContentLength(Long.parseLong(contentLengthHdr));
			}
			else {
				if ( responseParam != null && responseParam.getContentLength() > 0 ) {
					this.connettore.setContentLength( responseParam.getContentLength() );
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
				//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
			}

			
			// Ricezione Risposta
			if(this.connettore_debug)
				this.connettore_logger.debug("Analisi risposta input stream e risultato http...");
			this.connettore.initConfigurationAcceptOnlyReturnCode_202_200();

			
			// return code
			this.connettore.setCodiceTrasporto( responseParam.getResponseCode() );
			// TBK da rivedere
//			this.connettore.setResultHTTPMessage( response.getReasonPhrase() );

			if ( this.connettore.getCodiceTrasporto() >= 400 ) {
				this.connettore.setInputStreamResponse( responseParam != null ? responseParam.getContent() : null );
			}
			else {
				if ( this.connettore.getCodiceTrasporto() > 299 ) {

					String redirectLocation = TransportUtils.getObjectAsString(mapHeaderHttpResponse, HttpConstants.REDIRECT_LOCATION);

					// 3XX
					if(this.connettore.isFollowRedirects()){

						if(redirectLocation==null){
							throw new Exception("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.connettore.getCodiceTrasporto()+")"); 
						}

						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori.CONNETTORE_LOCATION);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
						this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER, (this.connettore.getNumberRedirect()+1)+"" );
						if(this.connettore.getRouteRedirect()!=null){
							this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, this.connettore.getRouteRedirect()+" -> "+redirectLocation );
						}else{
							this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}

						this.connettore_logger.warn("(hope:"+(this.connettore.getNumberRedirect()+1)+") Redirect verso ["+redirectLocation+"] ...");

						if(this.connettore.getNumberRedirect()==this.connettore.getMaxNumberRedirects()){
							throw new Exception("Gestione redirect (code:"+this.connettore.getCodiceTrasporto()+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.connettore.getMaxNumberRedirects()+" redirects: "+this.connettore.getRouteRedirect());
						}

						boolean acceptOnlyReturnCode_307 = false;
						if(this.connettore.isSoap()) {
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.connettore.getIdModulo())){
								acceptOnlyReturnCode_307 = openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
							}
							else{
								// InoltroBuste e InoltroRisposte
								acceptOnlyReturnCode_307 = openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
							}
						}
						if(acceptOnlyReturnCode_307){
							if(this.connettore.getCodiceTrasporto()!=307){
								throw new Exception("Return code ["+this.connettore.getCodiceTrasporto()+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}

						org.openspcoop2.pdd.core.connettori.nio.ConnettoreHTTPJavaStream connettoreySyncRedirect = new org.openspcoop2.pdd.core.connettori.nio.ConnettoreHTTPJavaStream();
//						org.openspcoop2.pdd.core.PdDContext pddContext = new org.openspcoop2.pdd.core.PdDContext();
//						for (String key : this.connettore.getPddContext().keys()) {
//							pddContext.addObject(key, this.connettore.getPddContext().getObject(key));
//						}
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
	//						this.connettore.setAsyncInvocationSuccess(success);
	//						return;
						}finally {
							this.request.setAsyncResponseCallback(callback);
						}

					}else{
						if(this.connettore.isSoap()) {
							throw new Exception("Gestione redirect (code:"+this.connettore.getCodiceTrasporto()+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
						}
						else {
							this.connettore_logger.debug("Gestione redirect (code:"+this.connettore.getCodiceTrasporto()+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
							
							if(this.connettore._getLocationValue()!=null && redirectLocation!=null){
								this.connettore.setLocation(this.connettore._getLocationValue()+" [redirect-location: "+redirectLocation+"]");
					    	}
							
							if(this.httpBody.isDoInput()){
								this.connettore.setInputStreamResponse( responseParam != null ? responseParam.getContent() : null );
							}
						}
					}
				}
				else{
					if(this.connettore.isSoap() && this.connettore.isAcceptOnlyReturnCode_202_200()){
						if(this.connettore.getCodiceTrasporto()!=200 && this.connettore.getCodiceTrasporto()!=202){
							throw new Exception("Return code ["+this.connettore.getCodiceTrasporto()+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
						}
					}
					if(this.httpBody.isDoInput()){
						this.connettore.setInputStreamResponse( responseParam != null ? responseParam.getContent() : null );
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

			this.connettore.normalizeInputStreamResponse(this.connettore.getHttpConnectionConfig().getReadTimeout());

			this.connettore.initCheckContentTypeConfiguration();

			if(this.connettore.isDumpBinarioRisposta()){
				if(!this.connettore.dumpResponse(connettore_propertiesTrasportoRisposta)) {
					this.connettore.setAsyncInvocationSuccess(false);
					return;
				}
			}

			if(this.connettore.isRest()){

				if(this.connettore.doRestResponse()==false){
					this.connettore.setAsyncInvocationSuccess(false);
					return;
				}

			}
			else{

				if(this.connettore.doSoapResponse()==false){
					this.connettore.setAsyncInvocationSuccess(false);
					return;
				}

			}

			if(this.connettore_debug)
				this.connettore_logger.info("Gestione invio/risposta http effettuata con successo",false);

			this.connettore.setAsyncInvocationSuccess(true);
			return;

		}  catch(Exception e){ 
			
			this.writeExceptionResponse(e);
			return;
			
		} finally {
			
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.COMPLETED);
			
		}

		
	}

	public void failed(final Throwable e) {
		try {
			
			if(this.connettore_debug) {
				this.connettore_logger.debug("NIO - Callback Response started after 'failed' event ...");
			}
			
			this.writeExceptionResponse( new Exception(e) );
			return;
			
		} finally {
			
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.FAILED);
			
		}
	}

	public void cancelled() {
		try {
			this.writeExceptionResponse(new Exception("Cancelled") );
			return;
		} finally {
			this.notifyCallbackFinished(AsyncResponseCallbackClientEvent.CANCELLED);
		}
		
	}
	
	private void notifyCallbackFinished(AsyncResponseCallbackClientEvent clientEvent) {
		this.connettore.asyncComplete(clientEvent);
		if(this.connettore_debug) {
			this.connettore_logger.debug("NIO - Callback Response finished");
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
		this.connettore_logger.error("Errore avvenuto durante la consegna HTTP: "+msgErrore,e);
		this.connettore.setAsyncInvocationSuccess(false);
	}
}