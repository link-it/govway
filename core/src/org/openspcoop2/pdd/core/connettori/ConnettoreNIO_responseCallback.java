package org.openspcoop2.pdd.core.connettori;

import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.concurrent.FutureCallback;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpBodyParameters;
import org.openspcoop2.utils.transport.http.HttpConstants;

public class ConnettoreNIO_responseCallback implements FutureCallback<HttpResponse> {
	private ConnettoreNIO connettore;
	private ConnettoreMsg request;
	private HttpBodyParameters httpBody;

	public ConnettoreNIO_responseCallback(ConnettoreNIO connettore, ConnettoreMsg request, HttpBodyParameters httpBody ) {
		this.connettore = connettore;
		this.request = request;
		this.httpBody = httpBody;
	}

	@Override
	public void completed(final HttpResponse response) {

		try {
			if(this.connettore.debug) {
				this.connettore.logger.debug("NIO - Callback Response started after 'complete' event ...");
			}
			// Analisi MimeType e ContentLocation della risposta
			if(this.connettore.debug)
				this.connettore.logger.debug("Analisi risposta...");
			Header [] hdrs = response.getAllHeaders();
			if ( hdrs != null && hdrs.length>0 ) {
				for ( Header hdr : response.getAllHeaders() ) {
					if(this.connettore.propertiesTrasportoRisposta==null){
						this.connettore.propertiesTrasportoRisposta = new Properties();
					}
					String name = hdr.getName();
					if(name==null){ // Check per evitare la coppia che ha come chiave null e come valore HTTP OK 200
						name=HttpConstants.RETURN_CODE;
					}
					String value = hdr.getValue();
					if(this.connettore.debug)
						this.connettore.logger.debug("HTTP risposta ["+name+"] ["+value+"]...");
					this.connettore.propertiesTrasportoRisposta.put(name, value);
				}
			}
			
			// TipoRisposta
			this.connettore.tipoRisposta = TransportUtils.get(this.connettore.propertiesTrasportoRisposta, HttpConstants.CONTENT_TYPE);
			
			// ContentLength della risposta
			String contentLenghtString = TransportUtils.get(this.connettore.propertiesTrasportoRisposta, HttpConstants.CONTENT_LENGTH);
			if(contentLenghtString!=null){
				this.connettore.contentLength = Integer.valueOf(contentLenghtString);
			}
			else{
				if(response.getEntity()!=null && response.getEntity().getContentLength()>0) {
					this.connettore.contentLength = response.getEntity().getContentLength();
				}
			}		


			// Parametri di imbustamento
			if(this.connettore.isSoap){
				this.connettore.imbustamentoConAttachment = false;
				if("true".equals(TransportUtils.get(this.connettore.propertiesTrasportoRisposta, this.connettore.openspcoopProperties.getTunnelSOAPKeyWord_headerTrasporto()))){
					this.connettore.imbustamentoConAttachment = true;
				}
				this.connettore.mimeTypeAttachment = TransportUtils.get(this.connettore.propertiesTrasportoRisposta, this.connettore.openspcoopProperties.getTunnelSOAPKeyWordMimeType_headerTrasporto());
				if(this.connettore.mimeTypeAttachment==null)
					this.connettore.mimeTypeAttachment = HttpConstants.CONTENT_TYPE_OPENSPCOOP2_TUNNEL_SOAP;
				//System.out.println("IMB["+imbustamentoConAttachment+"] MIME["+mimeTypeAttachment+"]");
			}

			// Ricezione Risposta
			if(this.connettore.debug)
				this.connettore.logger.debug("Analisi risposta input stream e risultato http...");
			this.connettore.initConfigurationAcceptOnlyReturnCode_202_200();

			// return code
			this.connettore.codice = response.getStatusLine().getStatusCode();
			this.connettore.resultHTTPMessage = response.getStatusLine().getReasonPhrase();

			if(this.connettore.codice>=400){
				this.connettore.isResponse = response.getEntity()!=null ? response.getEntity().getContent() : null;
			}
			else{
				if(this.connettore.codice>299){

					String redirectLocation = TransportUtils.get(this.connettore.propertiesTrasportoRisposta, HttpConstants.REDIRECT_LOCATION);

					// 3XX
					if(this.connettore.followRedirects){

						if(redirectLocation==null){
							throw new Exception("Non è stato rilevato l'header HTTP ["+HttpConstants.REDIRECT_LOCATION+"] necessario alla gestione del Redirect (code:"+this.connettore.codice+")"); 
						}

						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori.CONNETTORE_LOCATION);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
						TransportUtils.removeObject(this.request.getConnectorProperties(), CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
						this.request.getConnectorProperties().put(CostantiConnettori.CONNETTORE_LOCATION, redirectLocation);
						this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER, (this.connettore.numberRedirect+1)+"" );
						if(this.connettore.routeRedirect!=null){
							this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, this.connettore.routeRedirect+" -> "+redirectLocation );
						}else{
							this.request.getConnectorProperties().put(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE, redirectLocation );
						}

						this.connettore.logger.warn("(hope:"+(this.connettore.numberRedirect+1)+") Redirect verso ["+redirectLocation+"] ...");

						if(this.connettore.numberRedirect==this.connettore.maxNumberRedirects){
							throw new Exception("Gestione redirect (code:"+this.connettore.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentita ulteriormente, sono già stati gestiti "+this.connettore.maxNumberRedirects+" redirects: "+this.connettore.routeRedirect);
						}

						boolean acceptOnlyReturnCode_307 = false;
						if(this.connettore.isSoap) {
							if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.connettore.idModulo)){
								acceptOnlyReturnCode_307 = this.connettore.openspcoopProperties.isAcceptOnlyReturnCode_307_consegnaContenutiApplicativi();
							}
							else{
								// InoltroBuste e InoltroRisposte
								acceptOnlyReturnCode_307 = this.connettore.openspcoopProperties.isAcceptOnlyReturnCode_307_inoltroBuste();
							}
						}
						if(acceptOnlyReturnCode_307){
							if(this.connettore.codice!=307){
								throw new Exception("Return code ["+this.connettore.codice+"] (redirect "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Redirect_Status_Codes)");
							}
						}

						this.connettore.asyncInvocationSuccess = this.connettore.send(this.request); // caching ricorsivo non serve
						return;

					}else{
						if(this.connettore.isSoap) {
							throw new Exception("Gestione redirect (code:"+this.connettore.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
						}
						else {
							this.connettore.logger.debug("Gestione redirect (code:"+this.connettore.codice+" "+HttpConstants.REDIRECT_LOCATION+":"+redirectLocation+") non attiva");
							if(this.httpBody.isDoInput()){
								this.connettore.isResponse = response.getEntity()!=null ? response.getEntity().getContent() : null;
							}
						}
					}
				}
				else{
					if(this.connettore.isSoap && this.connettore.acceptOnlyReturnCode_202_200){
						if(this.connettore.codice!=200 && this.connettore.codice!=202){
							throw new Exception("Return code ["+this.connettore.codice+"] non consentito dal WS-I Basic Profile (http://www.ws-i.org/Profiles/BasicProfile-1.1-2004-08-24.html#HTTP_Success_Status_Codes)");
						}
					}
					if(this.httpBody.isDoInput()){
						this.connettore.isResponse = response.getEntity()!=null ? response.getEntity().getContent() : null;
					}
				}
			}



			/* ------------  PostOutRequestHandler ------------- */
			this.connettore.postOutRequest();




			/* ------------  PreInResponseHandler ------------- */
			this.connettore.preInResponse();

			// Lettura risposta parametri NotifierInputStream per la risposta
			this.connettore.notifierInputStreamParams = null;
			if(this.connettore.preInResponseContext!=null){
				this.connettore.notifierInputStreamParams = this.connettore.preInResponseContext.getNotifierInputStreamParams();
			}


			/* ------------  Gestione Risposta ------------- */

			this.connettore.normalizeInputStreamResponse();

			this.connettore.initCheckContentTypeConfiguration();

			if(this.connettore.debug){
				this.connettore.dumpResponse(this.connettore.propertiesTrasportoRisposta);
			}

			if(this.connettore.isRest){

				if(this.connettore.doRestResponse()==false){
					this.connettore.asyncInvocationSuccess = false;
					return;
				}

			}
			else{

				if(this.connettore.doSoapResponse()==false){
					this.connettore.asyncInvocationSuccess = false;
					return;
				}

			}

			if(this.connettore.debug)
				this.connettore.logger.info("Gestione invio/risposta http effettuata con successo",false);

			this.connettore.asyncInvocationSuccess = true;
			return;

		}  catch(Exception e){ 
			
			this.writeExceptionResponse(e);
			return;
			
		} finally {
			
			this.notifyCallbackFinished();
			
		}

		
	}

	@Override
	public void failed(final Exception e) {
		try {
			
			if(this.connettore.debug) {
				this.connettore.logger.debug("NIO - Callback Response started after 'failed' event ...");
			}
			
			this.writeExceptionResponse(e);
			return;
			
		} finally {
			
			this.notifyCallbackFinished();
			
		}
	}

	@Override
	public void cancelled() {
		
		try {
			
			this.writeExceptionResponse(new Exception("Cancelled") );
			return;
			
		} finally {
			
			this.notifyCallbackFinished();
			
		}
		
	}
	
	private void notifyCallbackFinished() {
//		if(this.connettore.debug) {
//			this.connettore.logger.debug("NIO - Sync Notify ...");
//		}
//		this.connettore.callbackResponseFinished = true;
//		synchronized (this.connettore.httpRequest) {
//			if(this.connettore.debug) {
//				this.connettore.logger.debug("NIO - Notify ...");
//			}
//			this.connettore.httpRequest.notify(); // risveglio gestione ferma sul connettore	
//		}
		this.connettore.asyncComplete();
		if(this.connettore.debug) {
			this.connettore.logger.debug("NIO - Callback Response finished");
		}
	}
	
	private void writeExceptionResponse(final Exception e) {
		this.connettore.eccezioneProcessamento = e;
		String msgErrore = this.connettore.readExceptionMessageFromException(e);
		if(this.connettore.generateErrorWithConnectorPrefix) {
			this.connettore.errore = "Errore avvenuto durante la consegna HTTP: "+msgErrore;
		}
		else {
			this.connettore.errore = msgErrore;
		}
		this.connettore.logger.error("Errore avvenuto durante la consegna HTTP: "+msgErrore,e);
		this.connettore.asyncInvocationSuccess = false;
	}
}