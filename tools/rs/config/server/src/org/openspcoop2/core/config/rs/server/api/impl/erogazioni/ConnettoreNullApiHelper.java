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
package org.openspcoop2.core.config.rs.server.api.impl.erogazioni;

import java.util.Map;

import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreNull;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreNullApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConnettoreNullApiHelper extends AbstractConnettoreApiHelper<ConnettoreNull> {

	@Override
	protected ConnettoreNull getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreNull) conn;
	}

	@Override
	protected ConnettoreNull getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreNull) conn;
	}

	@Override
	protected ConnettoreNull getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreNull) conn;
	}

	@Override
	public boolean connettoreCheckData(ConnettoreNull conn, ErogazioniEnv env, boolean erogazione) throws Exception {
		return true;
	}

	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettoreNull conn,
			String oldConnT) throws Exception {
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				TipiConnettore.NULL.getNome(), 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null,
				null,
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null, // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null, // this.httpsurl, 
				null,				// this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,				// this.httpspath
				null,	// this.httpstipo,
				null,			// this.httpspwd,
				null,					// this.httpsalgoritmo
				false,
				null,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,				// pathkey
				null, 		// this.httpstipokey
				null,			// this.httpspwdkey 
				null,				// this.httpspwdprivatekey,  
				null,				// this.httpsalgoritmokey,
				null,					// httpsKeyAlias
        		null,					// httpsTrustStoreCRLs
        		null,					// httpsTrustStoreOCSPPolicy
        		null,					// httpsKeyStoreBYOKPolicy
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,	// this.tempiRisposta_connectionTimeout, 
				null, //null,	// this.tempiRisposta_readTimeout, 
				null,	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transferMode, 
				"", 	// this.transferModeChunkSize, 
				"", 	// this.redirectMode, 
				"", 	// this.redirectMaxHop,
				null,   // this.httpImpl
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
				null,   // tokenPolicy
				null, null, // apiKeyHeader,  apiKeyValue
				null, null, // appIdHeader, appIdValue
				null // listExtendedConnettore
				);		
		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreNull conn,
			String oldConnT) throws Exception {

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				TipiConnettore.NULL.getNome(), 			// endpointtype
				oldConnT,						// oldConnT
				"",						// tipoConn Personalizzato
				null, // this.url,
				null,	// this.nome,
				null, 	// this.tipo,
				null,
				null,
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null, // this.url, 
				null,	// this.connfact,
				null,	// this.sendas,
				null, // this.httpsurl, 
				null,				// this.httpstipologia
				false,	// this.httpshostverify,
				ConnettoriCostanti.DEFAULT_CONNETTORE_HTTPS_TRUST_VERIFY_CERTS, // httpsTrustVerifyCert
				null,				// this.httpspath
				null,	// this.httpstipo,
				null,			// this.httpspwd,
				null,					// this.httpsalgoritmo
				false,
				null,			// this.httpskeystore, 
				"",																	//  this.httpspwdprivatekeytrust
				null,				// pathkey
				null, 		// this.httpstipokey
				null,			// this.httpspwdkey 
				null,				// this.httpspwdprivatekey,  
				null,				// this.httpsalgoritmokey,
				null,					// httpsKeyAlias
        		null,					// httpsTrustStoreCRLs
        		null,					// httpsTrustStoreOCSPPolicy
        		null,					// httpsKeyStoreBYOKPolicy
			
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,
				null,
				null,
				null,
				
				ServletUtils.boolToCheckBoxStatus( false ),	
				null,	// this.tempiRisposta_connectionTimeout, 
				null, //null,	// this.tempiRisposta_readTimeout, 
				null,	// this.tempiRisposta_tempoMedioRisposta,
				"no",	// this.opzioniAvanzate, 
				"", 	// this.transferMode, 
				"", 	// this.transferModeChunkSize, 
				"", 	// this.redirectMode, 
				"", 	// this.redirectMaxHop,
				null,   // this.httpImpl
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
				null,   // tokenPolicy
				null, null, // apiKeyHeader,  apiKeyValue
				null, null, // appIdHeader, appIdValue
				null // listExtendedConnettore
				);			
		return regConnettore;
	}

	@Override
	public ConnettoreNull buildConnettore(Map<String, String> props, String tipo) {
		ConnettoreNull c = new ConnettoreNull();
		
		c.setTipo(ConnettoreEnum.NULL);
		c.setDebug(Boolean.parseBoolean(props.get(CostantiDB.CONNETTORE_DEBUG)));
		
		return c;
	}

	@Override
	public String getUrlConnettore(Map<String, String> props, String tipoConnettore) throws Exception {
		return "[null] govway://dev/null";
	}

}
