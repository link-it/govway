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

package org.openspcoop2.protocol.utils;

import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.openspcoop2.message.constants.IntegrationError;
import org.openspcoop2.protocol.sdk.ProtocolException;
import org.openspcoop2.protocol.sdk.constants.CostantiProtocollo;
import org.openspcoop2.protocol.sdk.constants.IntegrationFunctionError;
import org.openspcoop2.utils.LoggerWrapperFactory;
import org.openspcoop2.utils.resources.Loader;
import org.slf4j.Logger;

/**
 * Classe che gestisce il file di properties 'errori.properties'
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ErroriProperties {

	// 400
	private static boolean FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST = false;
	// 502
	private static boolean FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE = false;
	private static boolean FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR = false;
	// 503
	private static boolean FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR = false;

	// 400
	public static boolean isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST() {
		return FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST;
	}
	public static void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST(
			boolean fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST,
			String confDir,Logger log,Loader loader) throws ProtocolException {
		FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST = fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST;
		reInit(confDir, log, loader);
	}
	
	public static boolean isFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE() {
		return FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE;
	}
	public static void setFORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE(
			boolean fORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE,
			String confDir,Logger log,Loader loader) throws ProtocolException {
		FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE = fORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE;
		reInit(confDir, log, loader);
	}

	public static boolean isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR() {
		return FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR;
	}
	public static void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR(
			boolean fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR,
			String confDir,Logger log,Loader loader) throws ProtocolException {
		FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR = fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR;
		reInit(confDir, log, loader);
	}

	public static boolean isFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR() {
		return FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR;
	}
	public static void setFORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR(
			boolean fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR,
			String confDir,Logger log,Loader loader) throws ProtocolException {
		FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR = fORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR;
		reInit(confDir, log, loader);
	}
	
	private static synchronized void reInit(String confDir,Logger log,Loader loader) throws ProtocolException {
		ErroriProperties erroriPropertiesNew = new ErroriProperties(confDir, log);
		erroriPropertiesNew.validaConfigurazione(loader);
		ErroriProperties.erroriProperties = erroriPropertiesNew; // switch
	} 
	
	
	/** Logger utilizzato per errori eventuali. */
	private Logger log = null;


	/** Copia Statica */
	private static ErroriProperties erroriProperties = null;

	/* ********  F I E L D S  P R I V A T I  ******** */

	/** Reader delle proprieta' impostate nel file 'errori.properties' */
	private ErroriInstanceProperties reader;


	


	/* ********  C O S T R U T T O R E  ******** */

	/**
	 * Viene chiamato in causa per istanziare il properties reader
	 *
	 * 
	 */
	private ErroriProperties(String confDir,Logger log) throws ProtocolException{

		if(log != null)
			this.log = log;
		else
			this.log = LoggerWrapperFactory.getLogger(ErroriProperties.class);

		/* ---- Lettura del cammino del file di configurazione ---- */

		Properties propertiesReader = new Properties();
		java.io.InputStream properties = null;
		try{  
			properties = ErroriProperties.class.getResourceAsStream("/org/openspcoop2/protocol/utils/errori.properties");
			if(properties==null){
				throw new Exception("File '/org/openspcoop2/protocol/utils/errori.properties' not found");
			}
			propertiesReader.load(properties);
		}catch(Exception e) {
			this.log.error("Riscontrato errore durante la lettura del file 'org/openspcoop2/protocol/utils/errori.properties': "+e.getMessage());
			throw new ProtocolException("ErroriProperties initialize error: "+e.getMessage(),e);
		}finally{
			try{
				if(properties!=null)
					properties.close();
			}catch(Exception er){
				// close
			}
		}
		try{
			this.reader = new ErroriInstanceProperties(confDir, propertiesReader, this.log);
		}catch(Exception e){
			throw new ProtocolException(e.getMessage(),e);
		}

	}

	/**
	 * Il Metodo si occupa di inizializzare il propertiesReader 
	 *
	 * 
	 */
	public static synchronized void initialize(String confDir,Logger log,Loader loader) throws ProtocolException{

		if(ErroriProperties.erroriProperties==null){
			
			ErroriProperties.erroriProperties = new ErroriProperties(confDir, log);
			erroriProperties.validaConfigurazione(loader);
						
		}

	}

	/**
	 * Ritorna l'istanza di questa classe
	 *
	 * @return Istanza di OpenSPCoopProperties
	 * @throws Exception 
	 * 
	 */
	public static ErroriProperties getInstance(Logger log) throws ProtocolException{

		if(ErroriProperties.erroriProperties==null)
			throw new ProtocolException("ErroriProperties not initialized (use init method in factory)");

		return ErroriProperties.erroriProperties;
	}




	public void validaConfigurazione(Loader loader) throws ProtocolException  {	
		try{  
			
			getPrefixWebSite();
			
			getPrefixWebSiteErrorPage();
			
			initMapWrapped();
			
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				
				getIntegrationError(integrationFunctionError);
				
				getErrorType(integrationFunctionError);

				getGenericDetails(integrationFunctionError);
				
				isForceGenericDetails(integrationFunctionError);
				
				getWebSite(integrationFunctionError);
				
			}
			
			
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la validazione della proprieta' degli errori, "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		}
	}

	
	
	/* **** CONVERTER **** */

	private Boolean typeEnabled = null;
	public boolean isTypeEnabled() throws ProtocolException {
		if(this.typeEnabled==null) {
			this.initTypeEnabled();
		}
		return this.typeEnabled;
	}
	private synchronized void initTypeEnabled() throws ProtocolException {
		if(this.typeEnabled==null) {
			this.typeEnabled = getBooleanProperty("type.enabled");
		}
	}
	
	
	
	
	private String webSite = null;
	public String getPrefixWebSite() throws ProtocolException {
		if(this.webSite==null) {
			this.initPrefixWebSite();
		}
		return this.webSite;
	}
	private synchronized void initPrefixWebSite() throws ProtocolException {
		if(this.webSite==null) {
			this.webSite = getProperty("webSite");
			if(this.webSite.endsWith("/")) {
				this.webSite = this.webSite.substring(0, this.webSite.length()-1); 
			}
		}
	}
	
	

	private String webSiteErrorPage = null;
	public String getPrefixWebSiteErrorPage() throws ProtocolException {
		if(this.webSiteErrorPage==null) {
			this.initPrefixWebSiteErrorPage();
		}
		return this.webSiteErrorPage;
	}
	private synchronized void initPrefixWebSiteErrorPage() throws ProtocolException {
		if(this.webSiteErrorPage==null) {
			this.webSiteErrorPage = getProperty("webSite.errorPage");
		}
	}
	
	
	
	private ConcurrentHashMap<IntegrationFunctionError,Boolean> mapWrapped= null;
	public Boolean isWrapped(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapWrapped==null){
			this.initMapWrapped();
		}
		return this.mapWrapped.get(functionError);
	}
	private synchronized void initMapWrapped() throws ProtocolException {
		if(this.mapWrapped==null){
			this.mapWrapped = new ConcurrentHashMap<IntegrationFunctionError, Boolean>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.wrappedValues();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				
				String pName = integrationFunctionError.name()+".enabled";
				try{
					Boolean p = null;
					if(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.equals(integrationFunctionError)){
						if(FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_BAD_REQUEST) {
							p = false;
						}
						else {
							p = getBooleanProperty(pName);
						}
					}
					else if(IntegrationFunctionError.WRAP_502_BAD_RESPONSE.equals(integrationFunctionError)){
						if(FORCE_SPECIFIC_ERROR_TYPE_FOR_BAD_RESPONSE) {
							p = false;
						}
						else {
							p = getBooleanProperty(pName);
						}
					}
					else if(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.equals(integrationFunctionError)){
						if(FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_RESPONSE_ERROR) {
							p = false;
						}
						else {
							p = getBooleanProperty(pName);
						}
					}
					else if(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.equals(integrationFunctionError)){
						if(FORCE_SPECIFIC_ERROR_TYPE_FOR_INTERNAL_ERROR) {
							p = false;
						}
						else {
							p = getBooleanProperty(pName);
						}
					}
					else {
						// caso non esistente
						p = getBooleanProperty(pName);
					}
					this.mapWrapped.put(integrationFunctionError, p);
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}
	
	
	
	
	
	private ConcurrentHashMap<IntegrationFunctionError,IntegrationError> mapIntegrationError= null;
	private ConcurrentHashMap<IntegrationFunctionError,IntegrationError> mapIntegrationError_noWrap= null;
	public IntegrationError getIntegrationError(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapIntegrationError==null){
			this.initMapIntegrationError();
		}
		return this.mapIntegrationError.get(functionError);
	}
	public IntegrationError getIntegrationError_noWrap(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapIntegrationError_noWrap==null){
			this.initMapIntegrationError();
		}
		return this.mapIntegrationError_noWrap.get(functionError);
	}
	private synchronized void initMapIntegrationError() throws ProtocolException {
		if(this.mapIntegrationError==null){
			this.mapIntegrationError = new ConcurrentHashMap<IntegrationFunctionError, IntegrationError>();
			this.mapIntegrationError_noWrap = new ConcurrentHashMap<IntegrationFunctionError, IntegrationError>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				String pName = integrationFunctionError.name()+".errorCode";
				try{
					String p = getProperty(pName);
					IntegrationError integrationError = IntegrationError.valueOf(p);
					IntegrationError integrationError_noWrap = IntegrationError.valueOf(p);
					
					if(integrationFunctionError.isWrapBadRequest()) {
						if(isWrapped(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST)) {
							String pWrappedName = IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.name()+".errorCode";
							p = getProperty(pWrappedName);
							integrationError = IntegrationError.valueOf(p);
						}
					}
					else if(integrationFunctionError.isWrapBadResponse(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_BAD_RESPONSE)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_BAD_RESPONSE.name()+".errorCode";
							p = getProperty(pWrappedName);
							integrationError = IntegrationError.valueOf(p);
						}
					}
					else if(integrationFunctionError.isWrapInternalResponseError(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.name()+".errorCode";
							p = getProperty(pWrappedName);
							integrationError = IntegrationError.valueOf(p);
						}
					}
					else if(integrationFunctionError.isWrapInternalError()) {
						if(isWrapped(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.name()+".errorCode";
							p = getProperty(pWrappedName);
							integrationError = IntegrationError.valueOf(p);
						}
					}
					
					this.mapIntegrationError.put(integrationFunctionError, integrationError);
					this.mapIntegrationError_noWrap.put(integrationFunctionError, integrationError_noWrap);
					
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}
	
	
	
	private ConcurrentHashMap<IntegrationFunctionError,String> mapErrorType= null;
	private ConcurrentHashMap<IntegrationFunctionError,String> mapErrorType_noWrap= null;
	public String getErrorType(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapErrorType==null){
			this.initMapErrorType();
		}
		return this.mapErrorType.get(functionError);
	}
	public String getErrorType_noWrap(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapErrorType_noWrap==null){
			this.initMapErrorType();
		}
		return this.mapErrorType_noWrap.get(functionError);
	}
	public IntegrationFunctionError convertToIntegrationFunctionError(String errorType) throws ProtocolException {
		return this._convertToIntegrationFunctionError(errorType, this.mapErrorType);
	}
	public IntegrationFunctionError convertToIntegrationFunctionError_noWrap(String errorType) throws ProtocolException {
		return this._convertToIntegrationFunctionError(errorType, this.mapErrorType_noWrap);
	}
	private IntegrationFunctionError _convertToIntegrationFunctionError(String errorType, ConcurrentHashMap<IntegrationFunctionError,String> map) throws ProtocolException {
		if(map==null){
			this.initMapErrorType();
		}
		Enumeration<IntegrationFunctionError> en = map.keys();
		while (en.hasMoreElements()) {
			IntegrationFunctionError integrationFunctionError = (IntegrationFunctionError) en.nextElement();
			String type = map.get(integrationFunctionError);
			if(type.equals(errorType)) {
				return integrationFunctionError;
			}
		}
		throw new ProtocolException("GovWayErrorType '"+errorType+"' unknown");
	}
	private synchronized void initMapErrorType() throws ProtocolException {
		if(this.mapErrorType==null){
			this.mapErrorType = new ConcurrentHashMap<IntegrationFunctionError, String>();
			this.mapErrorType_noWrap = new ConcurrentHashMap<IntegrationFunctionError, String>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				String pName = integrationFunctionError.name()+".errorType";
				try{
					String p = getProperty(pName);
					String p_noWrap = p;
					
					IntegrationError integrationError = this.mapIntegrationError_noWrap.get(integrationFunctionError);
					
					if(integrationFunctionError.isWrapBadRequest()) {
						if(isWrapped(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST)) {
							String pWrappedName = IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.name()+".errorType";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapBadResponse(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_BAD_RESPONSE)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_BAD_RESPONSE.name()+".errorType";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalResponseError(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.name()+".errorType";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalError()) {
						if(isWrapped(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.name()+".errorType";
							p = getProperty(pWrappedName);
						}
					}				
					
					this.mapErrorType.put(integrationFunctionError, p);
					this.mapErrorType_noWrap.put(integrationFunctionError, p_noWrap);
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}
	
	
	
	private ConcurrentHashMap<IntegrationFunctionError,String> mapWebSite= null;
	private ConcurrentHashMap<IntegrationFunctionError,String> mapWebSite_noWrap= null;
	public String getWebSite(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapWebSite==null){
			this.initMapWebSite();
		}
		return this.mapWebSite.get(functionError);
	}
	public String getWebSite_noWrap(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapWebSite_noWrap==null){
			this.initMapWebSite();
		}
		return this.mapWebSite_noWrap.get(functionError);
	}
	private synchronized void initMapWebSite() throws ProtocolException {
		if(this.mapWebSite==null){
			this.mapWebSite = new ConcurrentHashMap<IntegrationFunctionError, String>();
			this.mapWebSite_noWrap = new ConcurrentHashMap<IntegrationFunctionError, String>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				String pName = integrationFunctionError.name()+".webSite";
				try{
					String p = getOptionalProperty(pName);
					String p_noWrap = p;
					
					IntegrationError integrationError = this.mapIntegrationError_noWrap.get(integrationFunctionError);
					IntegrationError integrationError_noWrap = integrationError; 
					
					String integrationType = this.getErrorType(integrationFunctionError);
					String integrationType_noWrap = integrationType;				
					
					if(integrationFunctionError.isWrapBadRequest()) {
						if(isWrapped(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST)) {
							String pWrappedName = IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.name()+".webSite";
							p = getOptionalProperty(pWrappedName);
							integrationError = this.getIntegrationError(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST);
							integrationType = this.getErrorType(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST);
						}
					}
					else if(integrationFunctionError.isWrapBadResponse(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_BAD_RESPONSE)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_BAD_RESPONSE.name()+".webSite";
							p = getOptionalProperty(pWrappedName);
							integrationError = this.getIntegrationError(IntegrationFunctionError.WRAP_502_BAD_RESPONSE);
							integrationType = this.getErrorType(IntegrationFunctionError.WRAP_502_BAD_RESPONSE);
						}
					}
					else if(integrationFunctionError.isWrapInternalResponseError(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.name()+".webSite";
							p = getOptionalProperty(pWrappedName);
							integrationError = this.getIntegrationError(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR);
							integrationType = this.getErrorType(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR);
						}
					}
					else if(integrationFunctionError.isWrapInternalError()) {
						if(isWrapped(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.name()+".webSite";
							p = getOptionalProperty(pWrappedName);
							integrationError = this.getIntegrationError(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR);
							integrationType = this.getErrorType(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR);
						}
					}
					
					String site = this.getSite(p, integrationError, integrationType);
					this.mapWebSite.put(integrationFunctionError, site);
						
					String site_noWrap = this.getSite(p_noWrap, integrationError_noWrap, integrationType_noWrap);
					this.mapWebSite_noWrap.put(integrationFunctionError, site_noWrap);
					
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}
	private String getSite(String p, IntegrationError integrationError, String integrationType) {
				
		String site = null;
		if(p!=null) {
			if(p.startsWith("http://") || p.startsWith("https://")) {
				return p;
			}
			else {
				if(p.startsWith("/")) {
					site = this.webSite + p; 
				}
				else {
					site = this.webSite + "/" + p; 
				}
			}
		}
		else {
			if(this.webSiteErrorPage.startsWith("/")) {
				site = this.webSite + this.webSiteErrorPage; 
			}
			else {
				site = this.webSite + "/" + this.webSiteErrorPage; 
			}
		}
			
		while(site.contains(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE)) {
			switch (integrationError) {
			case BAD_REQUEST:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "400");
				break;
			case AUTHENTICATION:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "401");
				break;
			case AUTHORIZATION:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "403");
				break;
			case NOT_FOUND:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "404");
				break;
			case CONFLICT:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "409");
				break;
			case REQUEST_TOO_LARGE:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "413");
				break;
			case LIMIT_EXCEEDED:
			case TOO_MANY_REQUESTS:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "429");
				break;
			case BAD_RESPONSE:
			case INTERNAL_RESPONSE_ERROR:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "502");
				break;
			case SERVICE_UNAVAILABLE:
			case INTERNAL_REQUEST_ERROR:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "503");
				break;
			case ENDPOINT_REQUEST_TIMED_OUT:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "504");
				break;
			case DEFAULT:
				site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_HTTP_CODE, "503");
				break;
			}	
		}
		
		while(site.contains(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_CODE)) {
			site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_CODE, integrationError.name());
		}
		
		while(site.contains(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_TYPE)) {
			site = site.replace(CostantiProtocollo.OPENSPCOOP2_ERRORI_TEMPLATE_ERROR_TYPE, integrationType);
		}
		
		return site;
	}
	
	
	private ConcurrentHashMap<IntegrationFunctionError,String> mapGenericDetails= null;
	private ConcurrentHashMap<IntegrationFunctionError,String> mapGenericDetails_noWrap= null;
	public String getGenericDetails(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapGenericDetails==null){
			this.initMapGenericDetails();
		}
		return this.mapGenericDetails.get(functionError);
	}
	public String getGenericDetails_noWrap(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapGenericDetails_noWrap==null){
			this.initMapGenericDetails();
		}
		return this.mapGenericDetails_noWrap.get(functionError);
	}
	private synchronized void initMapGenericDetails() throws ProtocolException {
		if(this.mapGenericDetails==null){
			this.mapGenericDetails = new ConcurrentHashMap<IntegrationFunctionError, String>();
			this.mapGenericDetails_noWrap = new ConcurrentHashMap<IntegrationFunctionError, String>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				String pName = integrationFunctionError.name()+".genericDetails";
				try{
					String p = getProperty(pName);
					String p_noWrap = p;
					
					IntegrationError integrationError = this.mapIntegrationError_noWrap.get(integrationFunctionError);
					
					if(integrationFunctionError.isWrapBadRequest()) {
						if(isWrapped(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST)) {
							String pWrappedName = IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.name()+".genericDetails";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapBadResponse(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_BAD_RESPONSE)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_BAD_RESPONSE.name()+".genericDetails";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalResponseError(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.name()+".genericDetails";
							p = getProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalError()) {
						if(isWrapped(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.name()+".genericDetails";
							p = getProperty(pWrappedName);
						}
					}		
					
					this.mapGenericDetails.put(integrationFunctionError, p);
					this.mapGenericDetails_noWrap.put(integrationFunctionError, p_noWrap);
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}
	
	
	private ConcurrentHashMap<IntegrationFunctionError,Boolean> mapIsGenericDetails= null;
	private ConcurrentHashMap<IntegrationFunctionError,Boolean> mapIsGenericDetails_noWrap= null;
	public Boolean isForceGenericDetails(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapIsGenericDetails==null){
			this.initMapIsGenericDetails();
		}
		return this.mapIsGenericDetails.get(functionError);
	}
	public Boolean isForceGenericDetails_noWrap(IntegrationFunctionError functionError) throws ProtocolException {
		if(this.mapIsGenericDetails_noWrap==null){
			this.initMapIsGenericDetails();
		}
		return this.mapIsGenericDetails_noWrap.get(functionError);
	}
	private synchronized void initMapIsGenericDetails() throws ProtocolException {
		if(this.mapIsGenericDetails==null){
			this.mapIsGenericDetails = new ConcurrentHashMap<IntegrationFunctionError, Boolean>();
			this.mapIsGenericDetails_noWrap = new ConcurrentHashMap<IntegrationFunctionError, Boolean>();
			IntegrationFunctionError [] lista = IntegrationFunctionError.values();
			for (IntegrationFunctionError integrationFunctionError : lista) {
				String pName = integrationFunctionError.name()+".forceGenericDetails";
				try{
					Boolean p = getBooleanProperty(pName);
					Boolean p_noWrap = p;
					
					IntegrationError integrationError = this.mapIntegrationError_noWrap.get(integrationFunctionError);
					
					if(integrationFunctionError.isWrapBadRequest()) {
						if(isWrapped(IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST)) {
							String pWrappedName = IntegrationFunctionError.WRAP_400_INTERNAL_BAD_REQUEST.name()+".forceGenericDetails";
							p = getBooleanProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapBadResponse(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_BAD_RESPONSE)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_BAD_RESPONSE.name()+".forceGenericDetails";
							p = getBooleanProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalResponseError(integrationError)) {
						if(isWrapped(IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_502_INTERNAL_RESPONSE_ERROR.name()+".forceGenericDetails";
							p = getBooleanProperty(pWrappedName);
						}
					}
					else if(integrationFunctionError.isWrapInternalError()) {
						if(isWrapped(IntegrationFunctionError.WRAP_503_INTERNAL_ERROR)) {
							String pWrappedName = IntegrationFunctionError.WRAP_503_INTERNAL_ERROR.name()+".forceGenericDetails";
							p = getBooleanProperty(pWrappedName);
						}
					}	
					
					this.mapIsGenericDetails.put(integrationFunctionError, p);
					this.mapIsGenericDetails_noWrap.put(integrationFunctionError, p_noWrap);
				}catch(Exception e){
					throw new ProtocolException("Errore durante la gestione del codice ["+integrationFunctionError.name()+"]: "+e.getMessage(),e);
				}
			}
		}
	}

	


	
	
	/* ******* UTILITIES ********* */
	
	public String getProperty(String property) throws ProtocolException {
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				throw new Exception("proprieta non definita");
			return name.trim();
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
	}
	
	public String getOptionalProperty(String property) throws ProtocolException {
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				return null;
			return name.trim();
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
	}
	
	public Boolean getBooleanProperty(String property) throws ProtocolException {
		try{ 
			String name = null;
			name = this.reader.getValue_convertEnvProperties(property);
			if(name==null)
				throw new Exception("proprieta non definita");
			return Boolean.parseBoolean(name.trim());
		}catch(java.lang.Exception e) {
			String msg = "Riscontrato errore durante la lettura della proprieta' '"+property+"': "+e.getMessage();
			this.log.error(msg,e);
			throw new ProtocolException(msg,e);
		} 	   
	}
	
}
