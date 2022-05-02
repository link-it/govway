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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import static org.openspcoop2.utils.service.beans.utils.BaseHelper.evalnull;

import java.util.List;
import java.util.Map;

import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreJms;
import org.openspcoop2.core.config.rs.server.model.ConnettoreJmsSendAsEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreJmsTipoEnum;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.CostantiConnettori;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.web.ctrlstat.costanti.ConnettoreServletType;
import org.openspcoop2.web.ctrlstat.plugins.ExtendedConnettore;
import org.openspcoop2.web.ctrlstat.plugins.servlet.ServletExtendedConnettoreUtils;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreJmsApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConnettoreJmsApiHelper extends AbstractConnettoreApiHelper<ConnettoreJms> {

	@Override
	protected ConnettoreJms getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreJms) conn;
	}

	@Override
	protected ConnettoreJms getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreJms) conn;
	}

	@Override
	protected ConnettoreJms getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreJms) conn;
	}

	@Override
	public boolean connettoreCheckData(ConnettoreJms conn, ErogazioniEnv env, boolean erogazione) throws Exception {


	    final String endpointtype = TipiConnettore.JMS.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);

		String sendAs = conn.getSendAs().equals(ConnettoreJmsSendAsEnum.BYTES) ? CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE : CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE;
		return env.saHelper.endPointCheckData(
				env.tipo_protocollo,
				erogazione,
				endpointtype,
				null,
				conn.getNome(),	// this.nome,
				conn.getTipoCoda().toString(), 	// this.tipo,
				conn.getUtente(), 
				conn.getPassword(), 
				conn.getJndiInitialContext(),	// this.initcont, 
				conn.getJndiUrlPgkPrefixes(),	// this.urlpgk,
				conn.getJndiProviderUrl(),   // this.url, 
				conn.getConnectionFactory(),	// this.connfact,
				sendAs, // this.sendas
				null, 													// this.httpsurl,
				null,	// httpsConf.getTipologia().toString(), 
				false,		// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,	// this.httpspath
				null,	// this.httpstipo,
				null,	// this.httpspwd,
				null,	// this.httpsalgoritmo
				false, 
				null,
				"",																		// httpspwdprivatekeytrust, 
				null,	// pathkey
				null,	// this.httpstipokey
				null,	// this.httpspwdkey 
				null,	// this.httpspwdprivatekey,  
				null,	// this.httpsalgoritmokey, 
				null,	// httpsKeyAlias
				null,	// httpsTrustStoreCRLs
				null,																//	tipoconn (personalizzato)
				ServletUtils.boolToCheckBoxStatus(false),										 	//autenticazioneHttp,
				ServletUtils.boolToCheckBoxStatus(false),	
				null,	// evalnull( () -> proxy.getHostname() ),
				null,	// evalnull( () -> proxy.getPorta().toString() ),
				null,	// evalnull( () -> proxy.getUsername() ),
				null,	// evalnull( () -> proxy.getPassword() ),
				ServletUtils.boolToCheckBoxStatus(false), //ServletUtils.boolToCheckBoxStatus( tempiRisposta_enabled ),	
				null,	// evalnull( () -> timeoutConf.getConnectionTimeout().toString()),	// this.tempiRisposta_connectionTimeout, 
				null,	// evalnull( () -> timeoutConf.getConnectionReadTimeout().toString()), //null,	// this.tempiRisposta_readTimeout, 
				null,	// evalnull( () -> timeoutConf.getTempoMedioRisposta().toString()),	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,   // this.clientLibrary
        		null,	// requestOutputFileName,
        		null,   // this.requestOutputFileName_permissions
				null,	// this.requestOutputFileNameHeaders,
				null,   // this.requestOutputFileNameHeaders_permissions
        		null,	// requestOutputParentDirCreateIfNotExists, 
        		null,	// requestOutputOverwriteIfExists,
        		null,	// responseInputMode,
        		null,	// responseInputFileName,
        		null,	// responseInputFileNameHeaders,
        		null,	// responseInputDeleteAfterRead,
        		null,	// responseInputWaitTime,
				false,
				null,
				listExtendedConnettore,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
			);
	}

	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettoreJms conn,
			String oldConnT) throws Exception {
	    final String endpointtype = TipiConnettore.JMS.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);


		String sendAs = conn.getSendAs().equals(ConnettoreJmsSendAsEnum.BYTES) ? CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE : CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE;

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnT,			// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				conn.getNome(),	// this.nome,
				conn.getTipoCoda().toString(), 	// this.tipo,
				conn.getUtente(), 
				conn.getPassword(), 
				conn.getJndiInitialContext(),	// this.initcont, 
				conn.getJndiUrlPgkPrefixes(),	// this.urlpgk,
				conn.getJndiProviderUrl(),   // this.url, 
				conn.getConnectionFactory(),	// this.connfact,
				sendAs, // this.sendas
				null,												// this.httpsurl, 
				null,  //this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,  // this.httpspath
				null,  //this.httpstipo,
				null,  //this.httpspwd,
				null,  // this.httpsalgoritmo
				false,
				null, // this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,  //pathkey
				null,  //this.httpstipokey
				null,  //this.httpspwdkey 
				null,  //this.httpspwdprivatekey,  
				null,  //this.httpsalgoritmokey,
				null,  //httpsKeyAlias
				null,  //httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,   // this.clientLibrary
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
				null,
				listExtendedConnettore);			
		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreJms conn,
			String oldConnType) throws Exception {

	    final String endpointtype = TipiConnettore.JMS.getNome();
	    
		org.openspcoop2.core.registry.Connettore conTmp = null;
		List<ExtendedConnettore> listExtendedConnettore = 
				ServletExtendedConnettoreUtils.getExtendedConnettore(conTmp, ConnettoreServletType.ACCORDO_SERVIZIO_PARTE_SPECIFICA_ADD, env.apsHelper, 
						null, false, endpointtype);


		String sendAs = conn.getSendAs().equals(ConnettoreJmsSendAsEnum.BYTES) ? CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE : CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE;

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				endpointtype, 			// endpointtype
				oldConnType,			// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				conn.getNome(),	// this.nome,
				conn.getTipoCoda().toString(), 	// this.tipo,
				conn.getUtente(), 
				conn.getPassword(), 
				conn.getJndiInitialContext(),	// this.initcont, 
				conn.getJndiUrlPgkPrefixes(),	// this.urlpgk,
				conn.getJndiProviderUrl(),   // this.url, 
				conn.getConnectionFactory(),	// this.connfact,
				sendAs, // this.sendas
				null,												// this.httpsurl, 
				null,  //this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,  // this.httpspath
				null,  //this.httpstipo,
				null,  //this.httpspwd,
				null,  // this.httpsalgoritmo
				false,
				null, // this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,  //pathkey
				null,  //this.httpstipokey
				null,  //this.httpspwdkey 
				null,  //this.httpspwdprivatekey,  
				null,  //this.httpsalgoritmokey,
				null,  //httpsKeyAlias
				null,  //httpsTrustStoreCRLs
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,   // this.clientLibrary
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
				null,
				listExtendedConnettore);			
		return regConnettore;
	}

	@Override
	public ConnettoreJms buildConnettore(Map<String, String> props, String tipo) {
		ConnettoreJms c = new ConnettoreJms();
		
		c.setTipo(ConnettoreEnum.JMS);
		c.setDebug(Boolean.parseBoolean(props.get(CostantiDB.CONNETTORE_DEBUG)));
		
		c.setNome(props.get(CostantiDB.CONNETTORE_HTTP_LOCATION));
		c.setTipoCoda(ConnettoreJmsTipoEnum.fromValue(props.get(CostantiDB.CONNETTORE_JMS_TIPO)));
		String jmsSendAs = props.get(CostantiDB.CONNETTORE_JMS_SEND_AS);
		if(jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_BYTES_MESSAGE)) {
			c.setSendAs(ConnettoreJmsSendAsEnum.BYTES);
		} else if(jmsSendAs.equals(CostantiConnettori.CONNETTORE_JMS_SEND_AS_TEXT_MESSAGE)) {
			c.setSendAs(ConnettoreJmsSendAsEnum.TEXT);
		}
		c.setConnectionFactory(props.get(CostantiDB.CONNETTORE_JMS_CONNECTION_FACTORY));
		
		c.setUtente(evalnull( () -> props.get(CostantiDB.CONNETTORE_USER)));
		c.setPassword(evalnull( () -> props.get(CostantiDB.CONNETTORE_PWD)));

		c.setJndiInitialContext(props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_INITIAL));
		c.setJndiUrlPgkPrefixes(props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_FACTORY_URL_PKG));
		c.setJndiProviderUrl(props.get(CostantiDB.CONNETTORE_JMS_CONTEXT_JAVA_NAMING_PROVIDER_URL));
		
		return c;
	}

	@Override
	public String getUrlConnettore(Map<String, String> properties, String tipoConnettore) throws Exception {
		return "[jms] " + properties.get(CostantiDB.CONNETTORE_HTTP_LOCATION);
	}

}
