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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.rs.server.model.ConnettoreEnum;
import org.openspcoop2.core.config.rs.server.model.ConnettorePlugin;
import org.openspcoop2.core.config.rs.server.model.OneOfApplicativoServerConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreErogazioneConnettore;
import org.openspcoop2.core.config.rs.server.model.OneOfConnettoreFruizioneConnettore;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.constants.TipiConnettore;
import org.openspcoop2.core.plugins.Plugin;
import org.openspcoop2.core.plugins.constants.TipoPlugin;
import org.openspcoop2.core.registry.Connettore;
import org.openspcoop2.utils.service.fault.jaxrs.FaultCode;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.servlet.config.ConfigurazioneCore;
import org.openspcoop2.web.ctrlstat.servlet.connettori.ConnettoriCostanti;
import org.openspcoop2.web.lib.mvc.ServletUtils;

/**
 * ConnettorePluginApiHelper
 * 
 * @author $Author$
 * @version $Rev$, $Date$
 * 
 */
public class ConnettorePluginApiHelper extends AbstractConnettoreApiHelper<ConnettorePlugin> {

	@Override
	protected ConnettorePlugin getConnettore(OneOfConnettoreErogazioneConnettore conn) throws Exception {
		return (ConnettorePlugin) conn;
	}

	@Override
	protected ConnettorePlugin getConnettore(OneOfConnettoreFruizioneConnettore conn) throws Exception {
		return (ConnettorePlugin) conn;
	}

	@Override
	protected ConnettorePlugin getConnettore(OneOfApplicativoServerConnettore conn) throws Exception {
		return (ConnettorePlugin) conn;
	}

	@Override
	public boolean connettoreCheckData(ConnettorePlugin conn, ErogazioniEnv env, boolean erogazione) throws Exception {
		Search ricerca = new Search(true);
		ricerca.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_TIPO_PLUGIN_CLASSI, TipoPlugin.CONNETTORE.toString());

		ConfigurazioneCore confCore = new ConfigurazioneCore(env.stationCore);

		List<Plugin> listaTmp = confCore.pluginsClassiList(ricerca);

		if(!listaTmp.stream().anyMatch(p -> p.getTipo().equals(conn.getPlugin()))) {
			throw FaultCode.RICHIESTA_NON_VALIDA.toException("Tipo plugin ["+conn.getPlugin()+"] non trovato");
		}
		
		
		return env.saHelper.endPointCheckData(
				env.tipo_protocollo,
				erogazione,
				TipiConnettore.CUSTOM.getNome(),
				null,
				null,	// nome
				null,	// tipo
				null,	// httpConf.getUsername()
				null,	// httpConf.getPassword()
				null,	// this.initcont, 
				null,	// this.urlpgk,
				null,	// provurl jms,
				null, 	// connfact, 
				null,	// sendas, 
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
				conn.getPlugin(),																//	tipoconn (personalizzato)
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
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				false,
				null,
				null,
        		false, // erogazioneServizioApplicativoServerEnabled, TODO quando si aggiunge applicativo server
    			null // rogazioneServizioApplicativoServer
			);
	}

	@Override
	public Connettore fillConnettoreRegistro(org.openspcoop2.core.registry.Connettore regConnettore,
			ErogazioniEnv env,
			ConnettorePlugin conn,
			String oldConnT) throws Exception {
		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
				TipiConnettore.CUSTOM.getNome(), 			// endpointtype
				oldConnT,						// oldConnT
				conn.getPlugin(),						// tipoConn Personalizzato
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
				null,	// this.requestOutputFileName,
				null,	// this.requestOutputFileNameHeaders,
				null,	// this.requestOutputParentDirCreateIfNotExists,
				null,	// this.requestOutputOverwriteIfExists,
				null,	// this.responseInputMode, 
				null,	// this.responseInputFileName, 
				null,	// this.responseInputFileNameHeaders, 
				null,	// this.responseInputDeleteAfterRead, 
				null,	// this.responseInputWaitTime,
				null,
				null);
		
		if(conn.getProprieta() != null) {
			for(org.openspcoop2.core.config.rs.server.model.Proprieta prop: conn.getProprieta()) {
				org.openspcoop2.core.registry.Property property = new org.openspcoop2.core.registry.Property();
				property.setNome(prop.getNome());
				property.setValore(prop.getValore());
				regConnettore.addProperty(property);
			}
		}		

		return regConnettore;
	}

	@Override
	public org.openspcoop2.core.config.Connettore buildConnettoreConfigurazione(
			org.openspcoop2.core.config.Connettore regConnettore, ErogazioniEnv env, ConnettorePlugin conn,
			String oldConnT) throws Exception {

		env.apsHelper.fillConnettore(
				regConnettore, 
				conn.isDebug() != null && conn.isDebug() ? "true" : "false",				// this.connettoreDebug,
						TipiConnettore.CUSTOM.getNome(), 			// endpointtype
						oldConnT,						// oldConnT
						conn.getPlugin(),						// tipoConn Personalizzato
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
						null,	// this.requestOutputFileName,
						null,	// this.requestOutputFileNameHeaders,
						null,	// this.requestOutputParentDirCreateIfNotExists,
						null,	// this.requestOutputOverwriteIfExists,
						null,	// this.responseInputMode, 
						null,	// this.responseInputFileName, 
						null,	// this.responseInputFileNameHeaders, 
						null,	// this.responseInputDeleteAfterRead, 
						null,	// this.responseInputWaitTime,
						null,
						null);	

		
		if(conn.getProprieta() != null) {
			for(org.openspcoop2.core.config.rs.server.model.Proprieta prop: conn.getProprieta()) {
				org.openspcoop2.core.config.Property property = new org.openspcoop2.core.config.Property();
				property.setNome(prop.getNome());
				property.setValore(prop.getValore());
				regConnettore.addProperty(property);
			}
		}		

		return regConnettore;
	}

	@Override
	public ConnettorePlugin buildConnettore(Map<String, String> props, String tipo) {
		ConnettorePlugin c = new ConnettorePlugin();
		
		c.setTipo(ConnettoreEnum.PLUGIN);
		c.setDebug(Boolean.parseBoolean(props.get(CostantiDB.CONNETTORE_DEBUG)));
		
		c.setPlugin(tipo);
		
		List<String> propsExclude = Arrays.asList(CostantiDB.CONNETTORE_DEBUG);
		
		List<org.openspcoop2.core.config.rs.server.model.Proprieta> propLst = new ArrayList<>();

		for(Entry<String, String> prop: props.entrySet()) {
			if(!propsExclude.contains(prop.getKey())) {
				org.openspcoop2.core.config.rs.server.model.Proprieta p = new org.openspcoop2.core.config.rs.server.model.Proprieta();
				p.setNome(prop.getKey());
				p.setValore(prop.getValue());
				propLst.add(p);
			}
		}
		
		c.setProprieta(propLst);
		return c;
	}

	@Override
	public String getUrlConnettore(Map<String, String> props, String tipoConnettore) throws Exception {
		return "[plugin] " + tipoConnettore;
	}

}
