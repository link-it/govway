/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2025 Link.it srl (https://link.it). 
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.message.OpenSPCoop2Message;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.message.rest.RestUtilities;
import org.openspcoop2.pdd.config.ConfigurazionePdDManager;
import org.openspcoop2.pdd.config.CostantiProprieta;
import org.openspcoop2.pdd.config.ForwardProxy;
import org.openspcoop2.pdd.config.ForwardProxyConfigurazione;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.CostantiPdD;
import org.openspcoop2.pdd.core.dynamic.DynamicMapBuilderUtils;
import org.openspcoop2.pdd.core.dynamic.DynamicUtils;
import org.openspcoop2.pdd.core.keystore.GestoreKeystoreCaching;
import org.openspcoop2.pdd.logger.OpenSPCoop2Logger;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.CopyStream;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.io.Base64Utilities;
import org.openspcoop2.utils.resources.Charset;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.HttpConstants;
import org.openspcoop2.utils.transport.http.HttpRequestMethod;
import org.openspcoop2.utils.transport.http.HttpUtilities;
import org.openspcoop2.utils.transport.http.RFC2047Encoding;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;
import org.openspcoop2.utils.transport.http.SSLConfig;

/**
 * ConnettoreBaseHTTP
 *
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class ConnettoreBaseHTTP extends ConnettoreBaseWithResponse {
	
    @Override
	public String getProtocollo() {
    	return "HTTP";
    }
	
	/** httpMethod */
    private HttpRequestMethod forceHttpMethod = null;
    public void setForceHttpMethod(HttpRequestMethod forceHttpMethod) {
		this.forceHttpMethod = forceHttpMethod;
	}

	protected HttpRequestMethod httpMethod = null;
	public HttpRequestMethod getHttpMethod() {
		return this.httpMethod;
	}
	public void setHttpMethod(HttpRequestMethod httpMethod) {
		this.httpMethod = httpMethod;
	}
	public void setHttpMethod(OpenSPCoop2Message msg) throws ConnettoreException {
		// HttpMethod
		if(this.forceHttpMethod!=null) {
			this.httpMethod = this.forceHttpMethod;
		}
		else {
			if(ServiceBinding.SOAP.equals(msg.getServiceBinding())){
				this.httpMethod = HttpRequestMethod.POST;
			}
			else{
				if(msg.getTransportRequestContext()==null || msg.getTransportRequestContext().getRequestType()==null){
					throw new ConnettoreException("HttpRequestMethod non definito");
				}
				this.httpMethod = HttpRequestMethod.valueOf(msg.getTransportRequestContext().getRequestType().toUpperCase());
				if(this.httpMethod==null){
					throw new ConnettoreException("HttpRequestMethod sconosciuto ("+this.httpMethod+")");
				}
			}
		}
	}
	
	/** SSL Configuration */
	protected boolean connettoreHttps = false;
	protected SSLConfig sslContextProperties;
	
	/** InputStream Risposta */
	protected String resultHTTPMessage;
	public String getResultHTTPMessage() {
		return this.resultHTTPMessage;
	}
	public void setResultHTTPMessage(String resultHTTPMessage) {
		this.resultHTTPMessage = resultHTTPMessage;
	}

	/** RFC 2047 */
	protected boolean encodingRFC2047 = false;
	protected Charset charsetRFC2047 = null;
	protected RFC2047Encoding encodingAlgorithmRFC2047 = null;
	protected boolean validazioneHeaderRFC2047 = false;
	
	
	/** Proxy Pass Reverse */
	protected boolean proxyPassReverseEnabled = false;
	protected boolean proxyPassReverseLocation = false;
	protected boolean proxyPassReverseSetCookiePath = false;
	protected boolean proxyPassReverseSetCookieDomain = false;
	protected boolean proxyPassReverseUsePrefixProtocol = false;
	protected List<String> proxyPassReverseHeaders = null;
	protected List<String> proxyPassReverseSetCookieHeaders = null;
	private boolean forceDisableProxyPassReverse = false;
	public void setForceDisableProxyPassReverse(boolean forceDisableProxyPassReverseParam) {
		this.forceDisableProxyPassReverse = forceDisableProxyPassReverseParam;
	}

	/** ForwardProxy */
	protected ForwardProxy forwardProxy;
	protected String forwardProxyHeaderName;
	protected String forwardProxyHeaderValue;
	
	
    /* Costruttori */
    protected ConnettoreBaseHTTP(){
            this.connettoreHttps = false;
    }
    protected ConnettoreBaseHTTP(boolean https){
            this.connettoreHttps = https;
    }

	
	
	@Override
	protected boolean initialize(ConnettoreMsg request, boolean connectorPropertiesRequired, ResponseCachingConfigurazione responseCachingConfig){
		boolean init = super.initialize(request, connectorPropertiesRequired, responseCachingConfig);
		
		updateForwardProxy(request.getForwardProxy());
		
		// Location
		if(this.properties.get(CostantiConnettori.CONNETTORE_LOCATION)==null){
			this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_LOCATION+"' non fornita e richiesta da questo tipo di connettore ["+request.getTipoConnettore()+"]";
			return false;
		}
		
		if(this.idModulo!=null){
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				this.encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValueConsegnaContenutiApplicativi();
				this.charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValueConsegnaContenutiApplicativi();
				this.encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValueConsegnaContenutiApplicativi();
				this.validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValueConsegnaContenutiApplicativi();
			}else{
				this.encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValueInoltroBuste();
				this.charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValueInoltroBuste();
				this.encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValueInoltroBuste();
				this.validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValueInoltroBuste();
			}
			
			this.encodingRFC2047 = CostantiProprieta.isConnettoriHeaderValueEncodingRFC2047RequestEnabled(this.proprietaPorta, this.encodingRFC2047);
			this.charsetRFC2047 = CostantiProprieta.getConnettoriHeaderValueEncodingRFC2047RequestCharset(this.proprietaPorta, this.charsetRFC2047);
			this.encodingAlgorithmRFC2047 = CostantiProprieta.getConnettoriHeaderValueEncodingRFC2047RequestType(this.proprietaPorta, this.encodingAlgorithmRFC2047);
			this.validazioneHeaderRFC2047 = CostantiProprieta.isConnettoriHeaderValidationRequestEnabled(this.proprietaPorta, this.validazioneHeaderRFC2047);
		}
		
		if(!this.forceDisableProxyPassReverse) {
			
			if(this.isRest) {
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					this.proxyPassReverseLocation = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse();
					this.proxyPassReverseSetCookiePath = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie() && this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_path();
					this.proxyPassReverseSetCookieDomain = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie() && this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_domain();
					this.proxyPassReverseUsePrefixProtocol = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverseHeaders = this.openspcoopProperties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers();
						this.proxyPassReverseSetCookieHeaders = this.openspcoopProperties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
				else{
					// InoltroBuste e InoltroRisposte
					this.proxyPassReverseLocation = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse();
					this.proxyPassReverseSetCookiePath = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_setCookie() && this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_setCookie_path();
					this.proxyPassReverseSetCookieDomain = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_setCookie() && this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_setCookie_domain();
					this.proxyPassReverseUsePrefixProtocol = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverseHeaders = this.openspcoopProperties.getRESTServices_inoltroBuste_proxyPassReverse_headers();
						this.proxyPassReverseSetCookieHeaders = this.openspcoopProperties.getRESTServices_inoltroBuste_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
			}else {
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					this.proxyPassReverseLocation = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse();
					this.proxyPassReverseSetCookiePath = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie() && this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_path();
					this.proxyPassReverseSetCookieDomain = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie() && this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_domain();
					this.proxyPassReverseUsePrefixProtocol = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverseHeaders = this.openspcoopProperties.getSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_headers();
						this.proxyPassReverseSetCookieHeaders = this.openspcoopProperties.getSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
				else{
					// InoltroBuste e InoltroRisposte
					this.proxyPassReverseLocation = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse();
					this.proxyPassReverseSetCookiePath = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_setCookie() && this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_setCookie_path();
					this.proxyPassReverseSetCookieDomain = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_setCookie() && this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_setCookie_domain();
					this.proxyPassReverseUsePrefixProtocol = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverseHeaders = this.openspcoopProperties.getSOAPServices_inoltroBuste_proxyPassReverse_headers();
						this.proxyPassReverseSetCookieHeaders = this.openspcoopProperties.getSOAPServices_inoltroBuste_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
			}
			
			// check proprieta' specifiche
			if(this.proprietaPorta!=null && !this.proprietaPorta.isEmpty()) {
				try {
					this.proxyPassReverseLocation = CostantiProprieta.isConnettoriProxyPassReverseEnabled(this.proprietaPorta, this.proxyPassReverseLocation);
					if(this.proxyPassReverseLocation) {
						this.proxyPassReverseHeaders = CostantiProprieta.getConnettoriProxyPassReverseHeaders(this.proprietaPorta, this.proxyPassReverseHeaders);
					}
					
					this.proxyPassReverseSetCookiePath = CostantiProprieta.isConnettoriProxyPassReverseSetCookiePathEnabled(this.proprietaPorta, this.proxyPassReverseSetCookiePath);
					this.proxyPassReverseSetCookieDomain = CostantiProprieta.isConnettoriProxyPassReverseSetCookieDomainEnabled(this.proprietaPorta, this.proxyPassReverseSetCookieDomain);
					if(this.proxyPassReverseSetCookiePath || this.proxyPassReverseSetCookieDomain) {
						this.proxyPassReverseSetCookieHeaders = CostantiProprieta.getConnettoriProxyPassReverseHeaders(this.proprietaPorta, this.proxyPassReverseSetCookieHeaders);
					}
					
					if(this.proxyPassReverseLocation || this.proxyPassReverseSetCookiePath || this.proxyPassReverseSetCookieDomain) {
						this.proxyPassReverseUsePrefixProtocol = CostantiProprieta.isConnettoriProxyPassReverseUseProtocolPrefix(this.proprietaPorta, this.proxyPassReverseUsePrefixProtocol);
					}
				}catch(Exception e) {
					this.errore = e.getMessage();
					return false;
				}
			}
			
			if(this.proxyPassReverseLocation) {
				this.proxyPassReverseLocation = (this.proxyPassReverseHeaders!=null && !this.proxyPassReverseHeaders.isEmpty());
			}
			if(this.proxyPassReverseSetCookiePath) {
				this.proxyPassReverseSetCookiePath = (this.proxyPassReverseSetCookieHeaders!=null && !this.proxyPassReverseSetCookieHeaders.isEmpty());
			}
			if(this.proxyPassReverseSetCookieDomain) {
				this.proxyPassReverseSetCookieDomain = (this.proxyPassReverseSetCookieHeaders!=null && !this.proxyPassReverseSetCookieHeaders.isEmpty());
			}

			this.proxyPassReverseEnabled = this.proxyPassReverseLocation || this.proxyPassReverseSetCookiePath || this.proxyPassReverseSetCookieDomain;
		}
		
		return init;
	}
	
	protected void setSSLContext() throws ConnettoreException{
		if(this.connettoreHttps){
			this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(this.properties);
		}
		else {
			String location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			if(!location.trim().startsWith("https")){
				if(this.debug){
					this.logger.debug("Location non richiede gestione https ["+location.trim()+"]");
				}
				return;
			}
			
			boolean urlHttpsOverrideJvmConfiguration = ConnettoreHTTPUrlHttpsKeystoreRepository.isEnabled(this.idModulo, this.proprietaPorta);
			if(!urlHttpsOverrideJvmConfiguration) {
				if(this.debug){
					this.logger.debug("Location https ["+location.trim()+"]; gestione personalizzata dei keystore disabilitata");
				}
				return;
			}
			
			ConnettoreHTTPUrlHttpsKeystoreRepository utils = new ConnettoreHTTPUrlHttpsKeystoreRepository(this.debug, this.logger, this.idModulo);
			utils.init(this.proprietaPorta, this.busta, this.dynamicMap, this.getPddContext());
			this.sslContextProperties = utils.readSSLContext(this.requestInfo);
		}
		
		this.setSecureRandomSSLContext();
	}
	private void setSecureRandomSSLContext() {
		if(this.sslContextProperties!=null &&
				!this.sslContextProperties.isSecureRandomSet() &&
				this.openspcoopProperties.isConnettoreHttpsUseSecureRandom()
				) {
				this.sslContextProperties.setSecureRandom(true);
				if(this.openspcoopProperties.getConnettoreHttpsSecureRandomAlgo()!=null) {
					this.sslContextProperties.setSecureRandomAlgorithm(this.openspcoopProperties.getConnettoreHttpsSecureRandomAlgo());
				}
			}
	}
	
	protected org.openspcoop2.security.keystore.SSLSocketFactory buildSSLContextFactory() throws UtilsException {
		// Gestione https
		if(this.sslContextProperties!=null){
			
			StringBuilder sbError = null;
			StringBuilder sbDebug = null;
			try {
				sbError = new StringBuilder();
				this.sslContextProperties.setSbError(sbError);
				if(this.debug) {
					sbDebug = new StringBuilder();
					this.sslContextProperties.setSbDebug(sbDebug);
				}
				this.sslContextProperties.setLogger(this.logger.getLogger());
				Map<String,Object> dynamicMap = DynamicMapBuilderUtils.buildDynamicMap(this.busta, this.requestInfo, this.getPddContext(), 
						this.logger!=null ? this.logger.getLogger() : null);
				this.sslContextProperties.setDynamicMap(dynamicMap);
				return GestoreKeystoreCaching.getSSLSocketFactory(this.requestInfo, this.sslContextProperties);
			}catch(Exception e) {
				if(this.logger!=null) {
					this.logger.error("Lettura SSLSocketFactory '"+this.sslContextProperties.toString()+"' dalla cache fallita: "+e.getMessage(),e);
				}
				throw new UtilsException(e.getMessage(),e);
			}finally {
				if(sbError!=null && sbError.length()>0 &&
						this.logger!=null) {
					this.logger.error(sbError.toString());
				}
				if(sbDebug!=null && sbDebug.length()>0 &&
						this.logger!=null) {
					this.logger.info(sbDebug.toString(), false);
				}
			}
			
		}
		return null;
	}
	
		
	protected void updateForwardProxy(ForwardProxy forwardProxy) {
		if(this.forwardProxy==null) {
			this.forwardProxy = forwardProxy;
		}
	}
	protected boolean updateLocationForwardProxy(String location) throws ConnettoreException {
		
		if(this.forwardProxy!=null && this.forwardProxy.isEnabled()) {
					
			ForwardProxyConfigurazione config = this.forwardProxy.getConfig();
			
			String base64Location = null;
			if(config.getHeader()!=null) {
				this.forwardProxyHeaderName = config.getHeader();
				
				if(config.isHeaderBase64()) {
					base64Location = Base64Utilities.encodeAsString(location.getBytes());
					this.forwardProxyHeaderValue = base64Location;
				}
				else {
					this.forwardProxyHeaderValue = location;
					if(this.encodingRFC2047){
						try {
							if(!RFC2047Utilities.isAllCharactersInCharset(this.forwardProxyHeaderValue, this.charsetRFC2047)){
								String encoded = RFC2047Utilities.encode(this.forwardProxyHeaderValue+"", this.charsetRFC2047, this.encodingAlgorithmRFC2047);
								/**System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");*/
								if(this.debug)
									this.logger.info("RFC2047 Encoded value ["+this.forwardProxyHeaderValue+"] in ["+encoded+"] (charset:"+this.charsetRFC2047+" encoding-algorithm:"+this.encodingAlgorithmRFC2047+")",false);
								this.forwardProxyHeaderValue = encoded;
							}
						}catch(Exception e) {
							throw new ConnettoreException(e.getMessage(),e);
						}
					}
				}
			}
			
			Map<String, List<String>> queryParameters = new HashMap<>();
			if(config.getQuery()!=null) {
				if(config.isQueryBase64()) {
					if(base64Location==null) {
						base64Location = Base64Utilities.encodeAsString(location.getBytes());
					}
					TransportUtils.addParameter(queryParameters,config.getQuery(), base64Location);
				}
				else {
					TransportUtils.addParameter(queryParameters,config.getQuery(), location);
				}
			}
			
			String newUrl = this.forwardProxy.getUrl();
			if(this.dynamicMap!=null) {
				try {
					newUrl = DynamicUtils.convertDynamicPropertyValue("forwardProxyUrl", newUrl, this.dynamicMap, this.getPddContext(), false);
				}catch(Exception e){
					this.logger.error("Errore durante la costruzione della url per la funzionalità di 'forwardProxy' (dynamic): "+e.getMessage(),e);
				}
			}
			
			boolean encodeBaseLocation = true; // la base location può contenere dei parametri
			this.location = TransportUtils.buildUrlWithParameters(queryParameters, newUrl, encodeBaseLocation, this.logger!=null ? this.logger.getLogger() : OpenSPCoop2Logger.getLoggerOpenSPCoopCore());
			
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public boolean dumpResponse(Map<String, List<String>> trasporto) throws Exception{
		
		if(this.isRest){
			if(this.proxyPassReverseEnabled) {
				checkProxyPassReverse();
			}
		}
		else {
			/*
			 * Se il messaggio e' un html di errore me ne esco 
			 */		
			if(!checkSoapHtmlResponse()) {
				return false;
			}
			
			if(this.proxyPassReverseEnabled) {
				checkProxyPassReverse();
			}
		}
		
		return super.dumpResponse(trasporto);
	}
	
	@Override
	public boolean doSoapResponse() throws ConnettoreException{

		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso (check HTML) ...");
		
		/*
		 * Se il messaggio e' un html di errore me ne esco 
		 */	
		try {
			if(!checkSoapHtmlResponse()) {
				return false;
			}
		}catch(Exception e) {
			throw new ConnettoreException(e.getMessage(),e);
		}
		
		if(this.proxyPassReverseEnabled) {
			checkProxyPassReverse();
		}
		
		return super.doSoapResponse();
	}
	
	private boolean checkSoapHtmlResponse() throws UtilsException, IOException {
		/*
		 * Se il messaggio e' un html di errore me ne esco 
		 */			
		if(this.codice>=400 && 
				(
					(this.tipoRisposta!=null && this.tipoRisposta.contains(HttpConstants.CONTENT_TYPE_HTML))
					||
					(this.tipoRisposta==null || StringUtils.isEmpty(this.tipoRisposta)) // fix per casi in cui viene ritornata una pagina html senza content-type
				)
			){
			String tmpResultHTTPMessage=this.resultHTTPMessage;
			if(tmpResultHTTPMessage==null) {
				// provo a tradurlo
				tmpResultHTTPMessage = HttpUtilities.getHttpReason(this.codice);
			}
			String tipoLetturaRisposta = "("+this.codice+") " +  tmpResultHTTPMessage;
			
			// Registro HTML ricevuto.
			String htmlRicevuto = null;
			if(this.isResponse!=null){
				
				this.emitDiagnosticResponseRead(this.isResponse);
				
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
				/**byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
				int readByte = 0;
				while((readByte = this.isResponse.read(readB))!= -1){
					bout.write(readB,0,readByte);
				}*/
				CopyStream.copy(this.isResponse, bout);
				this.isResponse.close();
				bout.flush();
				bout.close();
				htmlRicevuto = bout.toString();
			}
			
			if(htmlRicevuto!=null && !"".equals(htmlRicevuto))
				this.errore = tipoLetturaRisposta +"\nhttp response: "+htmlRicevuto;
			else
				this.errore = tipoLetturaRisposta;
			return false;
		}
		
		return true;
	}
	
	@Override
	public boolean doRestResponse() throws ConnettoreException{
		
		if(this.proxyPassReverseEnabled) {
			checkProxyPassReverse();
		}
		
		return super.doRestResponse();
	}

	private boolean proxyPassReverseDone = false;
	private void checkProxyPassReverse() throws ConnettoreException {
		if(!this.proxyPassReverseDone) {
			try {
				if(this.debug)
					this.logger.debug("gestione (rest:"+this.isRest+") - proxyPassReverse:"+this.proxyPassReverseEnabled+
							" (location:"+this.proxyPassReverseLocation+" setCookie-path:"+this.proxyPassReverseSetCookiePath+" setCookie-domain:"+this.proxyPassReverseSetCookieDomain+") ...");
			
				if(this.proxyPassReverseEnabled) {
					
					// Raccolgo informazioni
					String baseUrl = null;
					String prefixGatewayUrl = null;
					String contesto = null;
					try {
						baseUrl = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
						if(baseUrl==null) {
							throw new ConnettoreException("BaseURL undefined");
						}
						if(this.debug)
							this.logger.debug("Base URL: ["+baseUrl+"]");
						 
						String interfaceName = null;
						if(this.requestMsg!=null) {
							Object porta = this.requestMsg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
							if(porta instanceof String s) {
								interfaceName = s;
							}
							if(interfaceName==null &&
								this.requestMsg.getTransportRequestContext()!=null) {
								interfaceName = this.requestMsg.getTransportRequestContext().getInterfaceName();
							}
							if(interfaceName==null) {
								if(this.pa!=null) {
									interfaceName = this.pa.getNome();
								}
								else if(this.pd!=null) {
									interfaceName = this.pd.getNome();
								}
							}
						}
						 
						if(this.proxyPassReverseUsePrefixProtocol) {
							UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(this.getProtocolFactory(), 
									ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo) ? RuoloContesto.PORTA_APPLICATIVA : RuoloContesto.PORTA_DELEGATA,
									this.requestMsg!=null ? this.requestMsg.getServiceBinding() : null,
								    interfaceName,
								    this.requestInfo!=null ? this.requestInfo.getIdentitaPdD() : null,
								    this.getIdAccordo(),
								    this.requestInfo);		 
							prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
							contesto = urlInvocazioneApi.getContext();
						}
						 
					}catch(Exception e) {
						throw new ConnettoreException("Errore durante la raccolta delle informazioni necessarie alla funzione di proxy pass reverse: "+e.getMessage(),e);
					}
	
					if(this.proxyPassReverseLocation) {
					 
						for (String header : this.proxyPassReverseHeaders) {
							String redirectLocation = TransportUtils.getFirstValue(this.propertiesTrasportoRisposta, header); 
							if(redirectLocation!=null) {
								if(this.debug)
									this.logger.debug("Trovato Header '"+header+"':["+redirectLocation+"]");
								
								try {
									String newRedirectLocation = RestUtilities.buildPassReverseUrl(this.requestMsg.getTransportRequestContext(), baseUrl, redirectLocation, prefixGatewayUrl, contesto);
									if(this.debug)
										this.logger.debug("Nuovo Header '"+header+"':["+newRedirectLocation+"]");
				               
									if(!redirectLocation.equals(newRedirectLocation)) {
										TransportUtils.removeObject(this.propertiesTrasportoRisposta, header);
										TransportUtils.addHeader(this.propertiesTrasportoRisposta,header, newRedirectLocation);
									}
								}catch(Exception e) {
									throw new ConnettoreException("Errore durante l'aggiornamento dell'header '"+header+
											"' attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
								}
							}
						}
					}
				
					if(this.proxyPassReverseSetCookiePath || this.proxyPassReverseSetCookieDomain) {
					 
						for (String header : this.proxyPassReverseSetCookieHeaders) {
							List<String> cookieValues = TransportUtils.getValues(this.propertiesTrasportoRisposta, header);
							List<String> newCookieValues = null; 
							boolean modify = false;
							if(cookieValues!=null && !cookieValues.isEmpty()) {
								
								newCookieValues = new ArrayList<>();
								
								for (String cookieValue : cookieValues) {
									if(cookieValue!=null) {
										if(this.debug)
											this.logger.debug("Trovato CookieHeader '"+header+"':["+cookieValue+"]");
											
										List<String> cookieNames = new ArrayList<>();
										List<String> cookiePaths = new ArrayList<>();
										List<String> cookieDomains = new ArrayList<>();
										try {
											List<HttpCookie> l = java.net.HttpCookie.parse(cookieValue);
											for (HttpCookie httpCookie : l) {
												/**System.out.println("cookie ["+httpCookie.getName()+"] ["+httpCookie.getPath()+"]");*/
												if(httpCookie.getPath()!=null) {
													cookieNames.add(httpCookie.getName());
													cookiePaths.add(httpCookie.getPath());
													cookieDomains.add(httpCookie.getDomain());
												}
											}
										}catch(Exception e) {
											this.logger.error("Errore durante il parsing del valore dell'header '"+header+"' (gestione cookie): "+e.getMessage(),e);
										}
										
										if(!cookieNames.isEmpty()) {
											for (int i = 0; i < cookieNames.size(); i++) {
												String cName = cookieNames.get(i);
												String cPath = cookiePaths.get(i);
												String cDomain = cookieDomains.get(i);
												
												String newValue = cookieValue;
												
												if(this.proxyPassReverseSetCookiePath) {
													try {
														if(cPath!=null) {
															String newPath = RestUtilities.buildCookiePassReversePath(this.requestMsg.getTransportRequestContext(), baseUrl, cPath, prefixGatewayUrl, contesto);
															if(this.debug)
																this.logger.debug("Nuovo Path '"+cName+"' (header:"+header+"):["+newPath+"] ...");
										               
															if(!cPath.equals(newPath)) {
																newValue = newValue.replace(cPath, newPath);
																/**
																String newValue = newValue.replace("path="+cPath, "path="+newPath);
																newValue = newValue.replace("path ="+cPath, "path ="+newPath);
																newValue = newValue.replace("path= "+cPath, "path= "+newPath);
																newValue = newValue.replace("path = "+cPath, "path = "+newPath);
																
																newValue = newValue.replace("Path="+cPath, "Path="+newPath);
																newValue = newValue.replace("Path ="+cPath, "Path ="+newPath);
																newValue = newValue.replace("Path= "+cPath, "Path= "+newPath);
																newValue = newValue.replace("Path = "+cPath, "Path = "+newPath);
																
																newValue = newValue.replace("PATH="+cPath, "PATH="+newPath);
																newValue = newValue.replace("PATH ="+cPath, "PATH ="+newPath);
																newValue = newValue.replace("PATH= "+cPath, "PATH= "+newPath);
																newValue = newValue.replace("PATH = "+cPath, "PATH = "+newPath);
																
																newValue + newValue.replace("path=\""+cPath, "path=\""+newPath);
																newValue = newValue.replace("path =\""+cPath, "path =\""+newPath);
																newValue = newValue.replace("path= \""+cPath, "path= \""+newPath);
																newValue = newValue.replace("path = \""+cPath, "path = \""+newPath);
																
																newValue = newValue.replace("Path=\""+cPath, "Path=\""+newPath);
																newValue = newValue.replace("Path =\""+cPath, "Path =\""+newPath);
																newValue = newValue.replace("Path= \""+cPath, "Path= \""+newPath);
																newValue = newValue.replace("Path = \""+cPath, "Path = \""+newPath);
																
																newValue = newValue.replace("PATH=\""+cPath, "PATH=\""+newPath);
																newValue = newValue.replace("PATH =\""+cPath, "PATH =\""+newPath);
																newValue = newValue.replace("PATH= \""+cPath, "PATH= \""+newPath);
																newValue = newValue.replace("PATH = \""+cPath, "PATH = \""+newPath);*/
																
																modify=true;
															}
														}
													}catch(Exception e) {
														throw new ConnettoreException("Errore durante l'aggiornamento del cookie '"+cName+
																"' (header:"+header+") attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
													}	
												}
												
												if(this.proxyPassReverseSetCookieDomain) {
													try {
														if(cDomain!=null) {
															String newDomain = RestUtilities.buildCookiePassReverseDomain(this.requestMsg.getTransportRequestContext(), baseUrl, cDomain, prefixGatewayUrl);
															if(this.debug)
																this.logger.debug("Nuovo Domain '"+cDomain+"' (header:"+header+"):["+newDomain+"] ...");
										               
															if(!cDomain.equals(newDomain)) {
																newValue = newValue.replace(cDomain, newDomain);
																/**
																String newValue = newValue.replace("domain="+cDomain, "domain="+newDomain);
																newValue = newValue.replace("domain ="+cDomain, "domain ="+newDomain);
																newValue = newValue.replace("domain= "+cDomain, "domain= "+newDomain);
																newValue = newValue.replace("domain = "+cDomain, "domain = "+newDomain);
																
																newValue = newValue.replace("Domain="+cDomain, "Domain="+newDomain);
																newValue = newValue.replace("Domain ="+cDomain, "Domain ="+newDomain);
																newValue = newValue.replace("Domain= "+cDomain, "Domain= "+newDomain);
																newValue = newValue.replace("Domain = "+cDomain, "Domain = "+newDomain);
																
																newValue = newValue.replace("DOMAIN="+cDomain, "DOMAIN="+newDomain);
																newValue = newValue.replace("DOMAIN ="+cDomain, "DOMAIN ="+newDomain);
																newValue = newValue.replace("DOMAIN= "+cDomain, "DOMAIN= "+newDomain);
																newValue = newValue.replace("DOMAIN = "+cDomain, "DOMAIN = "+newDomain);
																
																newValue + newValue.replace("domain=\""+cDomain, "domain=\""+newDomain);
																newValue = newValue.replace("domain =\""+cDomain, "domain =\""+newDomain);
																newValue = newValue.replace("domain= \""+cDomain, "domain= \""+newDomain);
																newValue = newValue.replace("domain = \""+cDomain, "domain = \""+newDomain);
																
																newValue = newValue.replace("Domain=\""+cDomain, "Domain=\""+newDomain);
																newValue = newValue.replace("Domain =\""+cDomain, "Domain =\""+newDomain);
																newValue = newValue.replace("Domain= \""+cDomain, "Domain= \""+newDomain);
																newValue = newValue.replace("Domain = \""+cDomain, "Domain = \""+newDomain);
																
																newValue = newValue.replace("DOMAIN=\""+cDomain, "DOMAIN=\""+newDomain);
																newValue = newValue.replace("DOMAIN =\""+cDomain, "DOMAIN =\""+newDomain);
																newValue = newValue.replace("DOMAIN= \""+cDomain, "DOMAIN= \""+newDomain);
																newValue = newValue.replace("DOMAIN = \""+cDomain, "DOMAIN = \""+newDomain);*/
																
																modify=true;
															}
														}
													}catch(Exception e) {
														throw new ConnettoreException("Errore durante l'aggiornamento del cookie '"+cName+
																"' (header:"+header+") attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
													}
												}
												
												newCookieValues.add(newValue);
											
											} /** end for (int i = 0; i < cookieNames.size(); i++) { */
										} /** end !cookieNames.isEmpty() */
									} /** end cookieValue!=null */
								} /** end for (String cookieValue : cookieValues) { */
							} /** end if(cookieValues!=null && !cookieValues.isEmpty()) { */
							
							if(modify) {
								TransportUtils.removeObject(this.propertiesTrasportoRisposta, header);
								this.propertiesTrasportoRisposta.put(header, newCookieValues);
							}
						}
					}
				 
				}
				
			}finally {
				this.proxyPassReverseDone = true;
			}
		 }
	}
}
