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

package org.openspcoop2.pdd.core.connettori;

import java.io.ByteArrayOutputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLSocketFactory;

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
import org.openspcoop2.pdd.config.OpenSPCoop2Properties;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.CostantiPdD;
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

	/** RFC 2047 */
	boolean encodingRFC2047 = false;
	Charset charsetRFC2047 = null;
	RFC2047Encoding encodingAlgorithmRFC2047 = null;
	boolean validazioneHeaderRFC2047 = false;
	
	
	/** Proxy Pass Reverse */
	protected boolean proxyPassReverse = false;
	protected boolean proxyPassReverse_usePrefixProtocol = false;
	protected List<String> proxyPassReverse_headers = null;
	protected List<String> proxyPassReverse_setCookie_headers = null;
	private boolean forceDisable_proxyPassReverse = false;
	public void setForceDisable_proxyPassReverse(boolean forceDisable_proxyPassReverse) {
		this.forceDisable_proxyPassReverse = forceDisable_proxyPassReverse;
	}

	/** ForwardProxy */
	protected ForwardProxy forwardProxy;
	protected String forwardProxy_headerName;
	protected String forwardProxy_headerValue;
	
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
				this.encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
				this.charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
				this.encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_consegnaContenutiApplicativi();
				this.validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_consegnaContenutiApplicativi();
			}else{
				this.encodingRFC2047 = this.openspcoopProperties.isEnabledEncodingRFC2047HeaderValue_inoltroBuste();
				this.charsetRFC2047 = this.openspcoopProperties.getCharsetEncodingRFC2047HeaderValue_inoltroBuste();
				this.encodingAlgorithmRFC2047 = this.openspcoopProperties.getEncodingRFC2047HeaderValue_inoltroBuste();
				this.validazioneHeaderRFC2047 = this.openspcoopProperties.isEnabledValidazioneRFC2047HeaderNameValue_inoltroBuste();
			}
		}
		
		if(!this.forceDisable_proxyPassReverse) {
			
			if(this.isRest) {
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					this.proxyPassReverse = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse();
					this.proxyPassReverse_usePrefixProtocol = this.openspcoopProperties.isRESTServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverse_headers = this.openspcoopProperties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_headers();
						this.proxyPassReverse_setCookie_headers = this.openspcoopProperties.getRESTServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
				else{
					// InoltroBuste e InoltroRisposte
					this.proxyPassReverse = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse();
					this.proxyPassReverse_usePrefixProtocol = this.openspcoopProperties.isRESTServices_inoltroBuste_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverse_headers = this.openspcoopProperties.getRESTServices_inoltroBuste_proxyPassReverse_headers();
						this.proxyPassReverse_setCookie_headers = this.openspcoopProperties.getRESTServices_inoltroBuste_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
			}else {
				if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
					this.proxyPassReverse = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse();
					this.proxyPassReverse_usePrefixProtocol = this.openspcoopProperties.isSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverse_headers = this.openspcoopProperties.getSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_headers();
						this.proxyPassReverse_setCookie_headers = this.openspcoopProperties.getSOAPServices_consegnaContenutiApplicativi_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
				else{
					// InoltroBuste e InoltroRisposte
					this.proxyPassReverse = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse();
					this.proxyPassReverse_usePrefixProtocol = this.openspcoopProperties.isSOAPServices_inoltroBuste_proxyPassReverse_useProtocolPrefix();
					try {
						this.proxyPassReverse_headers = this.openspcoopProperties.getSOAPServices_inoltroBuste_proxyPassReverse_headers();
						this.proxyPassReverse_setCookie_headers = this.openspcoopProperties.getSOAPServices_inoltroBuste_proxyPassReverse_setCookie_headers();
					}catch(Exception e) {
						this.errore = e.getMessage();
						return false;
					}
				}
			}
			
			// check proprieta' specifiche
			if(this.proprietaPorta!=null && !this.proprietaPorta.isEmpty()) {
				try {
					this.proxyPassReverse = CostantiProprieta.isConnettoriProxyPassReverseEnabled(this.proprietaPorta, this.proxyPassReverse);
					if(this.proxyPassReverse) {
						this.proxyPassReverse_usePrefixProtocol = CostantiProprieta.isConnettoriProxyPassReverseUseProtocolPrefix(this.proprietaPorta, this.proxyPassReverse_usePrefixProtocol);
						this.proxyPassReverse_headers = CostantiProprieta.getConnettoriProxyPassReverseHeaders(this.proprietaPorta, this.proxyPassReverse_headers);
						this.proxyPassReverse_setCookie_headers = CostantiProprieta.getConnettoriProxyPassReverseHeaders(this.proprietaPorta, this.proxyPassReverse_setCookie_headers);
					}
				}catch(Exception e) {
					this.errore = e.getMessage();
					return false;
				}
			}
		}
		
		return init;
	}
	
	protected void setSSLContext() throws Exception{
		if(this.connettoreHttps){
			this.sslContextProperties = ConnettoreHTTPSProperties.readProperties(this.properties);
		}
		else {
			String location = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
			if(location.trim().startsWith("https")==false){
				if(this.debug){
					this.logger.debug("Location non richiede gestione https ["+location.trim()+"]");
				}
				return;
			}
			boolean urlHttps_overrideDefaultConfiguration = false;
			boolean fruizioni = false;
			if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
				urlHttps_overrideDefaultConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttp_urlHttps_overrideDefaultConfiguration_consegnaContenutiApplicativi();
				fruizioni = false;
			}
			else{
				// InoltroBuste e InoltroRisposte
				urlHttps_overrideDefaultConfiguration = OpenSPCoop2Properties.getInstance().isConnettoreHttp_urlHttps_overrideDefaultConfiguration_inoltroBuste();
				fruizioni = true;
			}
			if(urlHttps_overrideDefaultConfiguration==false) {
				if(this.debug){
					this.logger.debug("Location https ["+location.trim()+"]; gestione personalizzata dei keystore disabilitata");
				}
				return;
			}
			this.sslContextProperties = ConnettoreHTTP_urlHttps_keystoreRepository.readSSLContext(this.debug, this.logger, this.busta, fruizioni);
		}
		
		if(this.sslContextProperties!=null){
			if(!this.sslContextProperties.isSecureRandomSet()) {
				if(this.openspcoopProperties.isConnettoreHttps_useSecureRandom()) {
					this.sslContextProperties.setSecureRandom(true);
					if(this.openspcoopProperties.getConnettoreHttps_secureRandomAlgo()!=null) {
						this.sslContextProperties.setSecureRandomAlgorithm(this.openspcoopProperties.getConnettoreHttps_secureRandomAlgo());
					}
				}
			}
		}
	}
	
	protected SSLSocketFactory buildSSLContextFactory() throws UtilsException {
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
				return GestoreKeystoreCaching.getSSLSocketFactory(this.sslContextProperties).getSslSocketFactory();
			}catch(Exception e) {
				this.logger.error("Lettura SSLSocketFactory '"+this.sslContextProperties.toString()+"' dalla cache fallita: "+e.getMessage(),e);
				throw new UtilsException(e.getMessage(),e);
			}finally {
				if(sbError!=null && sbError.length()>0) {
					this.logger.error(sbError.toString());
				}
				if(sbDebug!=null && sbDebug.length()>0) {
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
	protected boolean updateLocation_forwardProxy(String location) throws ConnettoreException {
		
		if(this.forwardProxy!=null && this.forwardProxy.isEnabled()) {
					
			ForwardProxyConfigurazione config = this.forwardProxy.getConfig();
			
			String base64Location = null;
			if(config.getHeader()!=null) {
				this.forwardProxy_headerName = config.getHeader();
				
				if(config.isHeaderBase64()) {
					base64Location = Base64Utilities.encodeAsString(location.getBytes());
					this.forwardProxy_headerValue = base64Location;
				}
				else {
					this.forwardProxy_headerValue = location;
					if(this.encodingRFC2047){
						try {
							if(RFC2047Utilities.isAllCharactersInCharset(this.forwardProxy_headerValue, this.charsetRFC2047)==false){
								String encoded = RFC2047Utilities.encode(new String(this.forwardProxy_headerValue), this.charsetRFC2047, this.encodingAlgorithmRFC2047);
								//System.out.println("@@@@ CODIFICA ["+value+"] in ["+encoded+"]");
								if(this.debug)
									this.logger.info("RFC2047 Encoded value ["+this.forwardProxy_headerValue+"] in ["+encoded+"] (charset:"+this.charsetRFC2047+" encoding-algorithm:"+this.encodingAlgorithmRFC2047+")",false);
								this.forwardProxy_headerValue = encoded;
							}
						}catch(Exception e) {
							throw new ConnettoreException(e.getMessage(),e);
						}
					}
				}
			}
			
			Map<String, List<String>> queryParameters = new HashMap<String, List<String>>();
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
	protected boolean dumpResponse(Map<String, List<String>> trasporto) throws Exception{
		
		if(this.isRest){
			if(this.proxyPassReverse) {
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
			
			if(this.proxyPassReverse) {
				checkProxyPassReverse();
			}
		}
		
		return super.dumpResponse(trasporto);
	}
	
	@Override
	protected boolean doSoapResponse() throws Exception{

		// gestione ordinaria via WS/SOAP
		
		if(this.debug)
			this.logger.debug("gestione WS/SOAP in corso (check HTML) ...");
		
		/*
		 * Se il messaggio e' un html di errore me ne esco 
		 */		
		if(!checkSoapHtmlResponse()) {
			return false;
		}
		
		if(this.proxyPassReverse) {
			checkProxyPassReverse();
		}
		
		return super.doSoapResponse();
	}
	
	private boolean checkSoapHtmlResponse() throws Exception {
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
				ByteArrayOutputStream bout = new ByteArrayOutputStream();
//				byte [] readB = new byte[Utilities.DIMENSIONE_BUFFER];
//				int readByte = 0;
//				while((readByte = this.isResponse.read(readB))!= -1){
//					bout.write(readB,0,readByte);
//				}
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
	protected boolean doRestResponse() throws Exception{
		
		if(this.proxyPassReverse) {
			checkProxyPassReverse();
		}
		
		return super.doRestResponse();
	}

	private boolean proxyPassReverseDone = false;
	private void checkProxyPassReverse() throws Exception {
		if(!this.proxyPassReverseDone) {
			try {
				if(this.debug)
					this.logger.debug("gestione (rest:"+this.isRest+") - proxyPassReverse:"+this.proxyPassReverse+" ...");
			
				if(this.proxyPassReverse &&
						(
								(this.proxyPassReverse_headers!=null && !this.proxyPassReverse_headers.isEmpty())
								||
								(this.proxyPassReverse_setCookie_headers!=null && !this.proxyPassReverse_setCookie_headers.isEmpty())
						)
					) {
					
					// Raccolgo informazioni
					String baseUrl = null;
					String prefixGatewayUrl = null;
					String contesto = null;
					try {
						baseUrl = this.properties.get(CostantiConnettori.CONNETTORE_LOCATION);
						if(baseUrl==null) {
							throw new Exception("BaseURL undefined");
						}
						if(this.debug)
							this.logger.debug("Base URL: ["+baseUrl+"] ...");
						 
						String interfaceName = null;
						if(this.requestMsg!=null) {
							Object porta = this.requestMsg.getContextProperty(CostantiPdD.NOME_PORTA_INVOCATA);
							if(porta!=null && porta instanceof String) {
								interfaceName = (String) porta;
							}
							if(interfaceName==null) {
								if(this.requestMsg.getTransportRequestContext()!=null) {
									interfaceName = this.requestMsg.getTransportRequestContext().getInterfaceName();
								}
							}
						}
						 
						if(this.proxyPassReverse_usePrefixProtocol) {
							UrlInvocazioneAPI urlInvocazioneApi = ConfigurazionePdDManager.getInstance().getConfigurazioneUrlInvocazione(this.getProtocolFactory(), 
									ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo) ? RuoloContesto.PORTA_APPLICATIVA : RuoloContesto.PORTA_DELEGATA,
									this.requestMsg!=null ? this.requestMsg.getServiceBinding() : null,
								    interfaceName,
								    this.requestInfo!=null ? this.requestInfo.getIdentitaPdD() : null,
								    this.getIdAccordo());		 
							prefixGatewayUrl = urlInvocazioneApi.getBaseUrl();
							contesto = urlInvocazioneApi.getContext();
						}
						 
					}catch(Exception e) {
						throw new Exception("Errore durante la raccolta delle informazioni necessarie alla funzione di proxy pass reverse: "+e.getMessage(),e);
					}
	
					if(this.proxyPassReverse_headers!=null && !this.proxyPassReverse_headers.isEmpty()) {
					 
						for (String header : this.proxyPassReverse_headers) {
							String redirectLocation = TransportUtils.getFirstValue(this.propertiesTrasportoRisposta, header); 
							if(redirectLocation!=null) {
								if(this.debug)
									this.logger.debug("Trovato Header '"+header+"':["+redirectLocation+"] ...");
								
								try {
									String newRedirectLocation = RestUtilities.buildPassReverseUrl(this.requestMsg.getTransportRequestContext(), baseUrl, redirectLocation, prefixGatewayUrl, contesto);
									if(this.debug)
										this.logger.debug("Nuovo Header '"+header+"':["+newRedirectLocation+"] ...");
				               
									if(!redirectLocation.equals(newRedirectLocation)) {
										TransportUtils.removeObject(this.propertiesTrasportoRisposta, header);
										TransportUtils.addHeader(this.propertiesTrasportoRisposta,header, newRedirectLocation);
									}
								}catch(Exception e) {
									throw new Exception("Errore durante l'aggiornamento dell'header '"+header+
											"' attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
								}
							}
						}
					}
				
					if(this.proxyPassReverse_setCookie_headers!=null && !this.proxyPassReverse_setCookie_headers.isEmpty()) {
					 
						for (String header : this.proxyPassReverse_setCookie_headers) {
							List<String> cookieValues = TransportUtils.getValues(this.propertiesTrasportoRisposta, header);
							List<String> newCookieValues = null; 
							boolean modify = false;
							if(cookieValues!=null && !cookieValues.isEmpty()) {
								
								newCookieValues = new ArrayList<String>();
								
								for (String cookieValue : cookieValues) {
									if(cookieValue!=null) {
										if(this.debug)
											this.logger.debug("Trovato CookieHeader '"+header+"':["+cookieValue+"] ...");
											
										List<String> cookieNames = new ArrayList<String>();
										List<String> cookiePaths = new ArrayList<String>();
										List<String> cookieDomains = new ArrayList<String>();
										try {
											List<HttpCookie> l = java.net.HttpCookie.parse(cookieValue);
											for (HttpCookie httpCookie : l) {
												//System.out.println("cookie ["+httpCookie.getName()+"] ["+httpCookie.getPath()+"]");
												if(httpCookie.getPath()!=null) {
													cookieNames.add(httpCookie.getName());
													cookiePaths.add(httpCookie.getPath());
													cookieDomains.add(httpCookie.getDomain());
												}
											}
										}catch(Throwable e) {
											this.logger.error("Errore durante il parsing del valore dell'header '"+header+"' (gestione cookie): "+e.getMessage(),e);
										}
										
										if(!cookieNames.isEmpty()) {
											for (int i = 0; i < cookieNames.size(); i++) {
												String cName = cookieNames.get(i);
												String cPath = cookiePaths.get(i);
												String cDomain = cookieDomains.get(i);
												
												String newValue = cookieValue;
												
												try {
													if(cPath!=null) {
														String newPath = RestUtilities.buildCookiePassReversePath(this.requestMsg.getTransportRequestContext(), baseUrl, cPath, prefixGatewayUrl, contesto);
														if(this.debug)
															this.logger.debug("Nuovo Path '"+cName+"' (header:"+header+"):["+newPath+"] ...");
									               
														if(!cPath.equals(newPath)) {
															newValue = newValue.replace(cPath, newPath);
															/*
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
													throw new Exception("Errore durante l'aggiornamento del cookie '"+cName+
															"' (header:"+header+") attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
												}		
												
												try {
													if(cDomain!=null) {
														String newDomain = RestUtilities.buildCookiePassReverseDomain(this.requestMsg.getTransportRequestContext(), baseUrl, cDomain, prefixGatewayUrl);
														if(this.debug)
															this.logger.debug("Nuovo Domain '"+cDomain+"' (header:"+header+"):["+newDomain+"] ...");
									               
														if(!cDomain.equals(newDomain)) {
															newValue = newValue.replace(cDomain, newDomain);
															/*
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
													throw new Exception("Errore durante l'aggiornamento del cookie '"+cName+
															"' (header:"+header+") attraverso la funzione di proxy pass reverse: "+e.getMessage(),e);
												}
												
												newCookieValues.add(newValue);
											
											} // end for (int i = 0; i < cookieNames.size(); i++) {
										} // end !cookieNames.isEmpty()
									} // end cookieValue!=null
								} // end for (String cookieValue : cookieValues) {
							} //end if(cookieValues!=null && !cookieValues.isEmpty()) {
							
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
