/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2021 Link.it srl (https://link.it). 
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
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.ResponseCachingConfigurazione;
import org.openspcoop2.core.config.constants.CostantiConfigurazione;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.constants.TransferLengthModes;
import org.openspcoop2.pdd.mdb.ConsegnaContenutiApplicativi;
import org.openspcoop2.utils.NameValue;
import org.openspcoop2.utils.UtilsException;
import org.openspcoop2.utils.transport.TransportUtils;
import org.openspcoop2.utils.transport.http.RFC2047Utilities;



/**
 * Classe utilizzata per effettuare consegne di messaggi, attraverso
 * l'invocazione di un server http.
 * Gestisce aspetti della connessione quali redirect, trasfern mode, proxy e parametri https 
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */

public abstract class ConnettoreExtBaseHTTP extends ConnettoreBaseHTTP {

	/* ********  F I E L D S  P R I V A T I  ******** */
	
	public ByteArrayOutputStream outByte = new ByteArrayOutputStream();
	
	/** Proxy Configuration */
	protected Proxy.Type proxyType = null;
	protected String proxyHostname = null;
	protected int proxyPort;
	protected String proxyUsername;
	protected String proxyPassword;
	
	/** Redirect */
	protected boolean followRedirects = false;
	public boolean isFollowRedirects() {
		return this.followRedirects;
	}
	protected String routeRedirect = null;
	public String getRouteRedirect() {
		return this.routeRedirect;
	}
	protected int numberRedirect = 0;
	public int getNumberRedirect() {
		return this.numberRedirect;
	}
	protected int maxNumberRedirects = 5;
	public int getMaxNumberRedirects() {
		return this.maxNumberRedirects;
	}
	
	/** TransferMode */
	protected TransferLengthModes tlm = null;
	protected int chunkLength = -1;
			
	/* Costruttori */
	public ConnettoreExtBaseHTTP(){
		super();
	}
	public ConnettoreExtBaseHTTP(boolean https){
		super(https);
	}
	
	
	
	/* ********  METODI  ******** */
	
	@Override
	protected boolean initializePreSend(ResponseCachingConfigurazione responseCachingConfig, ConnettoreMsg request) {
		
		if(this.initialize(request, true, responseCachingConfig)==false){
			return false;
		}
		
		return true;
		
	}
	
	@Override
	public boolean send(ConnettoreMsg request) {

		// HTTPS
		try{
			this.setSSLContext();
		}catch(Exception e){
			this.eccezioneProcessamento = e;
			this.logger.error("[HTTPS error]"+ this.readExceptionMessageFromException(e),e);
			this.errore = "[HTTPS error]"+ this.readExceptionMessageFromException(e);
			return false;
		}
	
		// Proxy
		if(this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE)!=null){
			
			String tipo = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE).trim();
			if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else if(CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS.equals(tipo)){
				this.proxyType = Proxy.Type.HTTP;
			}
			else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE
						+"' non corretta. Impostato un tipo sconosciuto ["+tipo+"] (valori ammessi: "+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTP
						+","+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE_VALUE_HTTPS+")";
				return false;
			}
			
			this.proxyHostname = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME);
			if(this.proxyHostname!=null){
				this.proxyHostname = this.proxyHostname.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_HOSTNAME+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			
			String proxyPortTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT);
			if(proxyPortTmp!=null){
				proxyPortTmp = proxyPortTmp.trim();
			}else{
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+
						"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_TYPE+"'";
				return false;
			}
			try{
				this.proxyPort = Integer.parseInt(proxyPortTmp);
			}catch(Exception e){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PORT+"' non corretta: "+this.readExceptionMessageFromException(e);
				return false;
			}
			
			
			this.proxyUsername = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME);
			if(this.proxyUsername!=null){
				this.proxyUsername = this.proxyUsername.trim();
			}
			
			this.proxyPassword = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD);
			if(this.proxyPassword!=null){
				this.proxyPassword = this.proxyPassword.trim();
			}else{
				if(this.proxyUsername!=null){
					this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_PASSWORD
							+"' non impostata, obbligatoria in presenza della proprietà '"+CostantiConnettori.CONNETTORE_HTTP_PROXY_USERNAME+"'";
					return false;
				}
			}
		}
				
		// TransferMode
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			this.tlm = this.openspcoopProperties.getTransferLengthModes_consegnaContenutiApplicativi();
			this.chunkLength = this.openspcoopProperties.getChunkLength_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			this.tlm = this.openspcoopProperties.getTransferLengthModes_inoltroBuste();
			this.chunkLength = this.openspcoopProperties.getChunkLength_inoltroBuste();
		}
		String tlmTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE);
		if(tlmTmp!=null){
			tlmTmp = tlmTmp.trim();
			try{
				this.tlm = TransferLengthModes.getTransferLengthModes(tlmTmp);
			}catch(Exception e){
				this.errore = "Proprieta' '"+CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE
						+"' non impostata correttamente: "+e.getMessage();
				return false;
			}
		}
		if(TransferLengthModes.TRANSFER_ENCODING_CHUNKED.equals(this.tlm)){
			tlmTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_DATA_TRANSFER_MODE_CHUNK_SIZE);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(tlmTmp!=null){
				tlmTmp = tlmTmp.trim();
				this.chunkLength = Integer.parseInt(tlmTmp);
			}
		}
		
		// Redirect
		if(ConsegnaContenutiApplicativi.ID_MODULO.equals(this.idModulo)){
			if(this.isSoap) {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi_soap();
			}else {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_consegnaContenutiApplicativi_rest();
			}
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_consegnaContenutiApplicativi();
		}
		else{
			// InoltroBuste e InoltroRisposte
			if(this.isSoap) {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste_soap();
			}
			else {
				this.followRedirects = this.openspcoopProperties.isFollowRedirects_inoltroBuste_rest();
			}
			this.maxNumberRedirects = this.openspcoopProperties.getFollowRedirectsMaxHop_inoltroBuste();
		}

		String redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_FOLLOW);
		if(redirectTmp!=null){
			redirectTmp = redirectTmp.trim();
			this.followRedirects = "true".equalsIgnoreCase(redirectTmp) || CostantiConfigurazione.ABILITATO.getValue().equalsIgnoreCase(redirectTmp);
		}
		//this.log.info("FOLLOW! ("+this.followRedirects+")");
		if(this.followRedirects){
			
			redirectTmp = this.properties.get(CostantiConnettori.CONNETTORE_HTTP_REDIRECT_MAX_HOP);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.maxNumberRedirects = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_NUMBER);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.numberRedirect = Integer.parseInt(redirectTmp);
			}
			
			redirectTmp = this.properties.get(CostantiConnettori._CONNETTORE_HTTP_REDIRECT_ROUTE);
			//this.log.info("PROPERTY! ("+redirectTmp+")");
			if(redirectTmp!=null){
				redirectTmp = redirectTmp.trim();
				this.routeRedirect = redirectTmp;
			}
		}
			
		return sendHTTP(request);

	}

	/**
	 * Si occupa di effettuare la consegna HTTP_POST (sbustando il messaggio SOAP).
	 * Si aspetta di ricevere una risposta non sbustata.
	 *
	 * @return true in caso di consegna con successo, false altrimenti
	 * 
	 */
	protected abstract boolean sendHTTP(ConnettoreMsg request);
		
    
	/**
     * Ritorna l'informazione su dove il connettore sta spedendo il messaggio
     * 
     * @return location di inoltro del messaggio
     */
    @Override
	public String getLocation(){
    	if(this.location==null){
    		// può darsi che per un errore non sia ancora stata inizializzata la location
    		try{
    			this.buildLocation();
    		}catch(Throwable t){}
    	}
    	if(this.location!=null){
	    	String l = new String(this.location);
	    	if(this.routeRedirect!=null){
	    		l = l+" [redirects route path: "+this.routeRedirect+"]";
	    	}
	    	if(this.proxyType!=null){
	    		l = l+" [proxy: "+this.proxyHostname+":"+this.proxyPort+"]";
	    	}
//	    	if(this.forwardProxy!=null && this.forwardProxy.isEnabled()) {
//	    		l = l+" [govway-proxy]";
//	    	}
	    	return l;
    	}
    	return null;
    }
    protected String _getTipoConnettore() {
    	return this.connettoreHttps ? TipiConnettore.HTTPS.toString() : TipiConnettore.HTTP.toString();
    }
    protected void buildLocation() throws ConnettoreException {
    	this.location = TransportUtils.getObjectAsString(this.properties,CostantiConnettori.CONNETTORE_LOCATION);	
    	NameValue nv = this.getTokenQueryParameter();
    	if(nv!=null) {
    		if(this.requestMsg!=null && this.requestMsg.getTransportRequestContext()!=null) {
    			this.requestMsg.getTransportRequestContext().removeParameter(nv.getName()); // Fix: senno sovrascriveva il vecchio token
    		}
    		if(this.propertiesUrlBased==null) {
    			this.propertiesUrlBased = new HashMap<String, List<String>>();
    		}
    		TransportUtils.setParameter(this.propertiesUrlBased, nv.getName(), nv.getValue());
    	}
		this.location = ConnettoreUtils.buildLocationWithURLBasedParameter(this.requestMsg, 
				this._getTipoConnettore(), 
				this.propertiesUrlBased, this.location,
				this.getProtocolFactory(), this.idModulo);
		
		this.updateLocation_forwardProxy(this.location);
    }
    

    protected void setRequestHeader(boolean validazioneHeaderRFC2047, String key, List<String> values, ConnettoreLogger logger, Map<String, List<String>> propertiesTrasportoDebug) throws Exception {
    	
    	if(validazioneHeaderRFC2047){
    		try{
        		RFC2047Utilities.validHeader(key, values);
        		setRequestHeader(key, values, propertiesTrasportoDebug);
        	}catch(UtilsException e){
        		logger.error(e.getMessage(),e);
        	}
    	}
    	else{
    		setRequestHeader(key,values, propertiesTrasportoDebug);
    	}
    	
    }
    
   @Override
   protected abstract void setRequestHeader(String key,List<String> values) throws Exception;

}


