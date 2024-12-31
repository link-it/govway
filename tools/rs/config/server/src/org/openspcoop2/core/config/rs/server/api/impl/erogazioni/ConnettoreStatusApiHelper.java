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
import java.util.Objects;

import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettoreStatus;
import org.openspcoop2.core.config.rs.server.model.ConnettoreStatusVerificaStatistica;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.config.rs.server.model.TipoPeriodoStatisticoEnum;
import org.openspcoop2.core.config.rs.server.model.TipoRispostaStatusEnum;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoreStatusParams;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettoreStatusApiHelper
 * 
 * @author Tommaso Burlon (tommaso.burlon@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConnettoreStatusApiHelper extends AbstractConnettoreApiHelper<ConnettoreStatus> {

	@Override
	protected boolean connettoreCheckData(ConnettoreStatus conn, ErogazioniEnv env, boolean erogazione) throws Exception { 
		return !ConnettoreStatusParams.check(env.apsHelper, null, null).getParsingErrors();
	}

	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettoreStatus conn,
			String oldConnT) throws Exception {
		
		ConnettoreStatusParams params = new ConnettoreStatusParams()
				.statusResponseType(conn.getRisposta().toString())
				.testConnectivity(conn.isVerificaConnettivita())
				.testStatistics(false);
		
		if (conn.getVerificaStatistica() != null) {
			params.testStatistics(true);
			params.periodValue(conn.getVerificaStatistica().getIntervallo());
			params.period(conn.getVerificaStatistica().getFrequenza().toString());
			params.statLifetime(conn.getVerificaStatistica().getCacheLifeTime());
		}
		
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() == Boolean.TRUE ? "true" : "false",				// this.connettoreDebug,
				TipiConnettore.STATUS.getNome(), 			// endpointtype
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
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,   // httpImpl
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
				params, // connettoreStatusParams
				null // listExtendedConnettore
				);
		
		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettoreStatus conn,
			String oldConnType) throws Exception {

		ConnettoreStatusParams params = new ConnettoreStatusParams()
				.statusResponseType(conn.getRisposta().toString())
				.testConnectivity(conn.isVerificaConnettivita())
				.testStatistics(false);
		
		if (conn.getVerificaStatistica() != null) {
			params.testStatistics(true);
			params.periodValue(conn.getVerificaStatistica().getIntervallo());
			params.period(conn.getVerificaStatistica().getFrequenza().toString());
			params.statLifetime(conn.getVerificaStatistica().getCacheLifeTime());
		}
		
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() == Boolean.TRUE ? "true" : "false",				// this.connettoreDebug,
				TipiConnettore.STATUS.getNome(), 			// endpointtype
				oldConnType,						// oldConnT
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
				"", 	// this.transfer_mode, 
				"", 	// this.transfer_mode_chunk_size, 
				"", 	// this.redirect_mode, 
				"", 	// this.redirect_max_hop,
				null,   // httpImpl
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
				params, // connettoreStatusParams
				null // listExtendedConnettore
				);		
		return regConnettore;
	}

	@Override
	public ConnettoreStatus buildConnettore(Map<String, String> props, String tipo) {
		String verificaConnettivita = props.get(CostantiDB.CONNETTORE_STATUS_TEST_CONNECTIVITY);
		String statusResponseType = props.get(CostantiDB.CONNETTORE_STATUS_RESPONSE_TYPE);
		String period = props.get(CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD);
		String periodValue = props.get(CostantiDB.CONNETTORE_STATUS_STATISTICAL_PERIOD_VALUE);
		String cacheLifetime = props.get(CostantiDB.CONNETTORE_STATUS_STAT_LIFETIME);
		String debug = props.get(CostantiDB.CONNETTORE_DEBUG);
		
		ConnettoreStatusVerificaStatistica verificaStatistica = null;
		
		if (period != null) {
			verificaStatistica = new ConnettoreStatusVerificaStatistica()
					.intervallo(Integer.valueOf(periodValue))
					.frequenza(TipoPeriodoStatisticoEnum.fromValue(period))
					.cacheLifeTime(cacheLifetime == null ? null : Integer.valueOf(cacheLifetime));
		}
		
		return new ConnettoreStatus()
				.tipo(ConnettoreEnum.STATUS)
				.risposta(TipoRispostaStatusEnum.fromValue(statusResponseType))
				.debug(Objects.requireNonNullElse(debug, Boolean.FALSE).equals(Boolean.TRUE))
				.verificaConnettivita(Objects.requireNonNullElse(verificaConnettivita, Boolean.FALSE).equals(Boolean.TRUE))
				.verificaStatistica(verificaStatistica);
	}

	@Override
	public String getUrlConnettore(Map<String, String> props, String tipoConnettore) throws Exception {
		return "[status] govway://status";
	}

	@Override
	protected ConnettoreStatus getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettoreStatus) conn;
	}

	@Override
	protected ConnettoreStatus getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettoreStatus) conn;
	}

	@Override
	protected ConnettoreStatus getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettoreStatus) conn;
	}

}