/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2023 Link.it srl (https://link.it).
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

package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.openspcoop2.core.config.rs.server.api.impl.Enums;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpBasic;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttps;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsClient;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneHttpsServer;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneProxy;
import org.openspcoop2.core.config.rs.server.model.ConnettoreConfigurazioneTimeout;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreHttp;
import org.openspcoop2.core.config.rs.server.model.KeystoreEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.config.rs.server.model.SslTipologiaEnum;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.utils.certificate.hsm.HSMUtils;
import org.openspcoop2.utils.service.beans.utils.BaseHelper;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreHTTPApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConnettoreHTTPApiHelper extends AbstractConnettoreApiHelper<ConnettoreHttp> {

	@Override
	protected boolean connettoreCheckData(ConnettoreHttp conn, ErogazioniEnv env, boolean erogazione) throws Exception {
		
		
		final boolean http_stato  = conn.getAutenticazioneHttp() != null;
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
      	final boolean autenticazioneToken = tokenPolicy!=null;
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
		return env.saHelper.endPointCheckData(
				env.tipo_protocollo,
				erogazione,
				endpointtype,
				conn.getEndpoint(),
				null,	// nome
				null,	// tipo
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,	// provurl jms,
				null, 	// connfact, 
				null,	// sendas, 
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> getTruststoreType(httpsServer) ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato, 
				httpskeystore,
				"",																		// httpspwdprivatekeytrust, 
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> getKeystoreType(httpsClient) ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey, 
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
        		evalnull( () -> httpsServer.getTruststoreOcspPolicy()),					// httpsTrustStoreOCSPPolicy
				null,																//	tipoconn (personalizzato)
				ServletUtils.boolToCheckBoxStatus( http_stato ),										 	//autenticazioneHttp,
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,   // this.requestOutputFileName_permissions
				null,	// this.requestOutputFileNameHeaders,
				null,   // this.requestOutputFileNameHeaders_permissions
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				autenticazioneToken,
				tokenPolicy,
				listExtendedConnettore,
        		false, // erogazioneServizioApplicativoServerEnabled,
    			null // rogazioneServizioApplicativoServer
			);
	}

	@Override
	protected Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore, ErogazioniEnv env, ConnettoreHttp conn, String oldConnT) throws Exception {
		
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
	     
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				conn.getEndpoint(),		// this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> getTruststoreType(httpsServer) ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> getKeystoreType(httpsClient) ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
        		evalnull( () -> httpsServer.getTruststoreOcspPolicy()),				// httpsTrustStoreOCSPPolicy
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,   // this.requestOutputFileName_permissions
				null,	// this.requestOutputFileNameHeaders,
				null,   // this.requestOutputFileNameHeaders_permissions
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				tokenPolicy,
				listExtendedConnettore);			
		
		return regConnettore;
	}

	@Override
	protected org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreHttp conn,
			String oldConnT) throws Exception {
		
		final boolean proxy_enabled = conn.getProxy() != null;
		final boolean tempiRisposta_enabled = conn.getTempiRisposta() != null; 
		
	    final ConnettoreConfigurazioneHttps httpsConf 	 = conn.getAutenticazioneHttps();
	    final ConnettoreConfigurazioneHttpBasic	httpConf	 = conn.getAutenticazioneHttp();

	    final String endpointtype = httpsConf != null ? TipiConnettore.HTTPS.getNome() : TipiConnettore.HTTP.getNome();
	    
	    final Properties parametersPOST = null;
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
							parametersPOST, false, endpointtype);

	    final ConnettoreConfigurazioneHttpsClient httpsClient = evalnull( () -> httpsConf.getClient() );
	  	final ConnettoreConfigurazioneHttpsServer httpsServer = evalnull( () -> httpsConf.getServer() );
	  	final ConnettoreConfigurazioneProxy 	  proxy   	  = conn.getProxy();
	  	final ConnettoreConfigurazioneTimeout	  timeoutConf = conn.getTempiRisposta();
	  	final String tokenPolicy = conn.getTokenPolicy(); 
	  	
		final boolean httpsstato = httpsClient != null;	// Questo è per l'autenticazione client.
	  	 
		String httpskeystore = null;
		if ( httpsClient != null ) {
			if ( httpsClient.getKeystorePath() != null || httpsClient.getKeystoreTipo() != null ) {
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_RIDEFINISCI;  
			}
			else
				httpskeystore = ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_KEYSTORE_CLIENT_AUTH_MODE_DEFAULT;
		}
        			    
	    	     
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,			// oldConnT
				"",						// tipoConn Personalizzato
				conn.getEndpoint(),		// this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				evalnull( () -> httpConf.getUsername() ),
				evalnull( () -> httpConf.getPassword() ),
				null,	// this.initcont, 
				null,	// this.urlpgk,
				conn.getEndpoint(),	// this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				conn.getEndpoint(), 													// this.httpsurl, 
				evalnull( () -> httpsConf.getTipologia().toString() ),				// this.httpstipologia
				BaseHelper.evalorElse( () -> httpsConf.isHostnameVerifier().booleanValue(), false ),	// this.httpshostverify,
				(httpsConf!=null ? !httpsConf.isTrustAllServerCerts() : ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS), // httpsTrustVerifyCert
				evalnull( () -> httpsServer.getTruststorePath() ),				// this.httpspath
				evalnull( () -> getTruststoreType(httpsServer) ),	// this.httpstipo,
				evalnull( () -> httpsServer.getTruststorePassword() ),			// this.httpspwd,
				evalnull( () -> httpsServer.getAlgoritmo() ),					// this.httpsalgoritmo
				httpsstato,
				httpskeystore,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				evalnull( () -> httpsClient.getKeystorePath() ),				// pathkey
				evalnull( () -> getKeystoreType(httpsClient) ), 		// this.httpstipokey
				evalnull( () -> httpsClient.getKeystorePassword() ),			// this.httpspwdkey 
				evalnull( () -> httpsClient.getKeyPassword() ),				// this.httpspwdprivatekey,  
				evalnull( () -> httpsClient.getAlgoritmo() ),				// this.httpsalgoritmokey,
        		evalnull( () -> httpsClient.getKeyAlias() ),					// httpsKeyAlias
        		evalnull( () -> httpsServer.getTruststoreCrl() ),					// httpsTrustStoreCRLs
        		evalnull( () -> httpsServer.getTruststoreOcspPolicy()),				// httpsTrustStoreOCSPPolicy
			
				ServletUtils.boolToCheckBoxStatus( proxy_enabled ),	
				evalnull( () -> proxy.getHostname() ),
				evalnull( () -> proxy.getPorta().toString() ),
				evalnull( () -> proxy.getUsername() ),
				evalnull( () -> proxy.getPassword() ),
				
				ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,	// this.requestOutputFileName,
				null,   // this.requestOutputFileName_permissions
				null,	// this.requestOutputFileNameHeaders,
				null,   // this.requestOutputFileNameHeaders_permissions
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				tokenPolicy,
				listExtendedConnettore);
		return regConnettore;
	}

	@Override
	public ConnettoreHttp buildConnettore(Map<String, String> props, String tipo) {
		ConnettoreHttp c = new ConnettoreHttp();
		c.setTipo(ConnettoreEnum.HTTP);
		c.setEndpoint(props.get(CostantiDB.CONNETTORE_HTTP_LOCATION));
		c.setDebug(Boolean.parseBoolean(props.get(CostantiDB.CONNETTORE_DEBUG)));
		
		ConnettoreConfigurazioneHttpBasic http = new ConnettoreConfigurazioneHttpBasic();
		http.setPassword(evalnull( () -> props.get(CostantiDB.CONNETTORE_PWD).trim())); 
		http.setUsername(evalnull( () -> props.get(CostantiDB.CONNETTORE_USER).trim()));
		if ( !StringUtils.isAllEmpty(http.getPassword(), http.getUsername()) ) {
			c.setAutenticazioneHttp(http);
		}
	
		ConnettoreConfigurazioneHttps https = new ConnettoreConfigurazioneHttps();
		https.setHostnameVerifier( props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER) != null 
				? Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_HOSTNAME_VERIFIER))
				: null
			);
		https.setTipologia(
				evalnull( () -> Enums.fromValue(SslTipologiaEnum.class, props.get(CostantiDB.CONNETTORE_HTTPS_SSL_TYPE)))
			);
		
		https.setTrustAllServerCerts( props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS) != null 
				? Boolean.valueOf(props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_ALL_CERTS))
				: null
			);
		
		if(https.isTrustAllServerCerts()==null || !https.isTrustAllServerCerts()) {
			ConnettoreConfigurazioneHttpsServer httpsServer = new ConnettoreConfigurazioneHttpsServer();
			
			httpsServer.setAlgoritmo( evalnull( () -> 
				props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_MANAGEMENT_ALGORITM))
				);
			httpsServer.setTruststorePassword(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_PASSWORD))
				);
			httpsServer.setTruststorePath(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_LOCATION))
				);
			
			String trustStoreType = props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_TYPE);
			if(trustStoreType!=null) {
				if(HSMUtils.isKeystoreHSM(trustStoreType)) {
					httpsServer.setTruststoreTipo(KeystoreEnum.PKCS11);
					httpsServer.setPcks11Tipo(trustStoreType);
				}
				else {
					httpsServer.setTruststoreTipo(Enums.fromValue(KeystoreEnum.class,trustStoreType));
				}
			}
			
			httpsServer.setTruststoreCrl(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_CRLs))
				);
			
			httpsServer.setTruststoreOcspPolicy(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_TRUST_STORE_OCSP_POLICY))
				);
			
			if(httpsServer.getAlgoritmo()!=null || httpsServer.getTruststorePassword()!=null ||
					httpsServer.getTruststorePath()!=null || httpsServer.getTruststoreTipo()!=null ||
					httpsServer.getTruststoreCrl()!=null) {
				https.setServer(httpsServer);
			}
		}
		
		ConnettoreConfigurazioneHttpsClient httpsClient = new ConnettoreConfigurazioneHttpsClient();
		
		httpsClient.setAlgoritmo(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_MANAGEMENT_ALGORITM))
			);
		httpsClient.setKeystorePassword(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_PASSWORD))
			);
		httpsClient.setKeystorePath(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_LOCATION))
			);
		String keyStoreType = props.get(CostantiDB.CONNETTORE_HTTPS_KEY_STORE_TYPE);
		if(keyStoreType!=null) {
			if(HSMUtils.isKeystoreHSM(keyStoreType)) {
				httpsClient.setKeystoreTipo(KeystoreEnum.PKCS11);
				httpsClient.setPcks11Tipo(keyStoreType);
			}
			else {
				httpsClient.setKeystoreTipo(Enums.fromValue(KeystoreEnum.class,keyStoreType));
			}
		}
		
		httpsClient.setKeyPassword(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_PASSWORD))
			);
		httpsClient.setKeyAlias(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_HTTPS_KEY_ALIAS))
			);
		
		if(httpsClient.getAlgoritmo()!=null || 
				httpsClient.getKeystorePassword()!=null || httpsClient.getKeystorePath()!=null || httpsClient.getKeystoreTipo()!=null ||
				httpsClient.getKeyPassword()!=null ||  httpsClient.getKeyAlias()!=null) {
			https.setClient(httpsClient);
		}
		
		if ( https.getTipologia() != null ) {
			c.setAutenticazioneHttps(https);
		}
		
		String proxy_type = evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_TYPE).trim() );
		if ( !StringUtils.isEmpty(proxy_type)) {
			ConnettoreConfigurazioneProxy proxy = new ConnettoreConfigurazioneProxy();
			c.setProxy(proxy);
			
			proxy.setHostname(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_HOSTNAME).trim())
				);
			proxy.setPassword(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_PASSWORD).trim())
				);
			proxy.setPorta(
					evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_PROXY_PORT)))
				);
			proxy.setUsername(
					evalnull( () -> props.get(CostantiDB.CONNETTORE_PROXY_USERNAME).trim())
				);
		}
		
		ConnettoreConfigurazioneTimeout tempiRisposta = new ConnettoreConfigurazioneTimeout();		
		tempiRisposta.setConnectionReadTimeout( 
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_READ_CONNECTION_TIMEOUT))) 
			);
		tempiRisposta.setConnectionTimeout(
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_CONNECTION_TIMEOUT)))
			);
		tempiRisposta.setTempoMedioRisposta(
				evalnull( () -> Integer.valueOf(props.get(CostantiDB.CONNETTORE_TEMPO_MEDIO_RISPOSTA)))
			);
		
		if ( tempiRisposta.getConnectionReadTimeout() != null || tempiRisposta.getConnectionTimeout() != null || tempiRisposta.getTempoMedioRisposta() != null) {
			c.setTempiRisposta(tempiRisposta);
		}
		
		c.setTokenPolicy(
				evalnull( () -> props.get(CostantiDB.CONNETTORE_TOKEN_POLICY).trim())
			);
		
		return c;
	}

	@Override
	public String getUrlConnettore(Map<String, String> properties, String tipoConnettore) throws Exception {
		return properties.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
	}

	@Override
	protected ConnettoreHttp getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreHttp) conn;
	}

	@Override
	protected ConnettoreHttp getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreHttp) conn;
	}

	@Override
	protected ConnettoreHttp getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreHttp) conn;
	}

	public static String getKeystoreType(ConnettoreConfigurazioneHttpsClient httpsClient) {
		if(httpsClient.getKeystoreTipo()!=null) {
			if(KeystoreEnum.PKCS11.equals( httpsClient.getKeystoreTipo())) {
				if(httpsClient.getPcks11Tipo()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo keystore pks11 non indicato");
				}
				return httpsClient.getPcks11Tipo();
			}
			else {
				return httpsClient.getKeystoreTipo().toString();
			}
		}
		return null;
	}
	
	public static String getTruststoreType(ConnettoreConfigurazioneHttpsServer httpsServer) {
		if(httpsServer.getTruststoreTipo()!=null) {
			if(KeystoreEnum.PKCS11.equals( httpsServer.getTruststoreTipo())) {
				if(httpsServer.getPcks11Tipo()==null) {
					throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo keystore pks11 non indicato");
				}
				return httpsServer.getPcks11Tipo();
			}
			else {
				return httpsServer.getTruststoreTipo().toString();
			}
		}
		return null;
	}
}
