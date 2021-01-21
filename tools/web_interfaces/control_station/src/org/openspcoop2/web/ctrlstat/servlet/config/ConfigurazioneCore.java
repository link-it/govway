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
package org.openspcoop2.web.ctrlstat.servlet.config;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.openspcoop2.core.allarmi.AllarmeParametro;
import org.openspcoop2.core.allarmi.constants.RuoloPorta;
import org.openspcoop2.core.commons.ErrorsHandlerCostant;
import org.openspcoop2.core.commons.Filtri;
import org.openspcoop2.core.commons.ISearch;
import org.openspcoop2.core.commons.Liste;
import org.openspcoop2.core.config.AccessoConfigurazione;
import org.openspcoop2.core.config.AccessoDatiAutorizzazione;
import org.openspcoop2.core.config.AccessoRegistro;
import org.openspcoop2.core.config.AccessoRegistroRegistro;
import org.openspcoop2.core.config.CanaleConfigurazione;
import org.openspcoop2.core.config.CanaleConfigurazioneNodo;
import org.openspcoop2.core.config.Configurazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazione;
import org.openspcoop2.core.config.ConfigurazioneUrlInvocazioneRegola;
import org.openspcoop2.core.config.GenericProperties;
import org.openspcoop2.core.config.GestioneErrore;
import org.openspcoop2.core.config.PortaApplicativa;
import org.openspcoop2.core.config.PortaDelegata;
import org.openspcoop2.core.config.Property;
import org.openspcoop2.core.config.RegistroPlugin;
import org.openspcoop2.core.config.RegistroPluginArchivio;
import org.openspcoop2.core.config.RegistroPlugins;
import org.openspcoop2.core.config.ResponseCachingConfigurazioneRegola;
import org.openspcoop2.core.config.RoutingTable;
import org.openspcoop2.core.config.RoutingTableDestinazione;
import org.openspcoop2.core.config.SystemProperties;
import org.openspcoop2.core.config.constants.PluginSorgenteArchivio;
import org.openspcoop2.core.config.constants.RuoloContesto;
import org.openspcoop2.core.config.driver.DriverConfigurazioneException;
import org.openspcoop2.core.config.driver.DriverConfigurazioneNotFound;
import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyFiltro;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicyRaggruppamento;
import org.openspcoop2.core.controllo_traffico.ConfigurazioneGenerale;
import org.openspcoop2.core.controllo_traffico.ConfigurazionePolicy;
import org.openspcoop2.core.controllo_traffico.IdPolicy;
import org.openspcoop2.core.controllo_traffico.beans.InfoPolicy;
import org.openspcoop2.core.controllo_traffico.constants.RuoloPolicy;
import org.openspcoop2.core.controllo_traffico.constants.TipoRisorsaPolicyAttiva;
import org.openspcoop2.core.id.IDGenericProperties;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDServizioApplicativo;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.core.mvc.properties.utils.DBPropertiesUtils;
import org.openspcoop2.core.registry.beans.AccordoServizioParteComuneSintetico;
import org.openspcoop2.generic_project.exception.NotFoundException;
import org.openspcoop2.message.constants.ServiceBinding;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeBean;
import org.openspcoop2.monitor.engine.alarm.wrapper.ConfigurazioneAllarmeHistoryBean;
import org.openspcoop2.monitor.engine.config.base.IdPlugin;
import org.openspcoop2.monitor.engine.config.base.Plugin;
import org.openspcoop2.monitor.engine.config.base.constants.TipoPlugin;
import org.openspcoop2.monitor.engine.dynamic.DynamicFactory;
import org.openspcoop2.monitor.engine.dynamic.IDynamicLoader;
import org.openspcoop2.monitor.sdk.condition.Context;
import org.openspcoop2.monitor.sdk.parameters.Parameter;
import org.openspcoop2.monitor.sdk.plugins.IAlarmProcessing;
import org.openspcoop2.pdd.config.UrlInvocazioneAPI;
import org.openspcoop2.pdd.core.autorizzazione.canali.CanaliUtils;
import org.openspcoop2.protocol.engine.ProtocolFactoryManager;
import org.openspcoop2.protocol.engine.utils.DBOggettiInUsoUtils;
import org.openspcoop2.protocol.sdk.IProtocolFactory;
import org.openspcoop2.web.ctrlstat.core.ControlStationCore;
import org.openspcoop2.web.ctrlstat.core.Search;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationDB;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationException;
import org.openspcoop2.web.ctrlstat.driver.DriverControlStationNotFound;
import org.openspcoop2.web.lib.audit.AuditException;
import org.openspcoop2.web.lib.audit.DriverAudit;
import org.openspcoop2.web.lib.audit.dao.Filtro;
import org.openspcoop2.web.lib.mvc.dynamic.DynamicComponentUtils;
import org.openspcoop2.web.lib.mvc.dynamic.components.BaseComponent;

/**
 * ConfigurazioneCore
 *
 * @author Poli Andrea (apoli@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 */
public class ConfigurazioneCore extends ControlStationCore {


	public ConfigurazioneCore() throws Exception {
		super();
	}
	public ConfigurazioneCore(ControlStationCore core) throws Exception {
		super(core);
	}
	
	
	public List<RoutingTableDestinazione> routingList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "routingList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().routingList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	
	public List<AccessoRegistroRegistro> registriList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "registriList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().registriList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<ResponseCachingConfigurazioneRegola> responseCachingConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "responseCachingConfigurazioneRegolaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().responseCachingConfigurazioneRegolaList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<ConfigurazioneUrlInvocazioneRegola> proxyPassConfigurazioneRegolaList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "responseCachingConfigurazioneRegolaList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().proxyPassConfigurazioneRegolaList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsResponseCachingConfigurazioneRegola(Integer statusMin, Integer statusMax, boolean fault) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsResponseCachingConfigurazioneRegola";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsResponseCachingConfigurazioneRegola(statusMin,statusMax,fault);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsProxyPassConfigurazioneRegola(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsProxyPassConfigurazioneRegola";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsProxyPassConfigurazioneRegola(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<RegistroPlugin> pluginsArchiviList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "pluginsArchiviList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().pluginsArchiviList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public int numeroPluginsArchiviList() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "numeroPluginsArchiviList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			Search searchForCount = new Search(true,1);
			driver.getDriverConfigurazioneDB().pluginsArchiviList(searchForCount);
			return searchForCount.getNumEntries(Liste.CONFIGURAZIONE_PLUGINS_ARCHIVI);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPlugin getRegistroPlugin(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRegistroPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getRegistroPlugin(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPlugin getDatiRegistroPlugin(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getDatiRegistroPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getDatiRegistroPlugin(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPluginArchivio getRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRegistroPluginArchivio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getRegistroPluginArchivio(nomePlugin, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsRegistroPluginArchivio(String nomePlugin, String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsRegistroPluginArchivio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsRegistroPluginArchivio(nomePlugin, nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsRegistroPluginArchivio(String nomePlugin, PluginSorgenteArchivio tipoSorgente, String sorgente) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsRegistroPluginArchivio";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsRegistroPluginArchivio(nomePlugin, tipoSorgente, sorgente);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPlugin getRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRegistroPluginFromPosizione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getRegistroPluginFromPosizione(posizione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPlugin getDatiRegistroPluginFromPosizione(int posizione) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getDatiRegistroPluginFromPosizione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getDatiRegistroPluginFromPosizione(posizione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsRegistroPlugin(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "existsRegistroPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsRegistroPlugin(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public int getMaxPosizioneRegistroPlugin() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getMaxPosizioneRegistroPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getMaxPosizioneRegistroPlugin();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public int getNumeroArchiviJarRegistroPlugin(String nome) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getNumeroArchiviJarRegistroPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getNumeroArchiviJarRegistroPlugin(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<RegistroPluginArchivio> pluginsArchiviJarList(String nome, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "pluginsArchiviJarList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().pluginsArchiviJarList(nome, ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Plugin> pluginsClassiList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "pluginsClassiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.pluginsClassiList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Plugin> pluginsAllarmiList(String applicabilita, boolean soloAbilitati) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "pluginsClassiList";
		DriverControlStationDB driver = null;

		try {
			ISearch ricercaPlugin = new Search();
			ricercaPlugin.addFilter( Liste.CONFIGURAZIONE_PLUGINS_CLASSI, Filtri.FILTRO_TIPO_PLUGIN_CLASSI, TipoPlugin.ALLARME.toString());
			ricercaPlugin.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI,  Filtri.FILTRO_APPLICABILITA_NOME, applicabilita);
			if(soloAbilitati) {
				ricercaPlugin.addFilter(Liste.CONFIGURAZIONE_PLUGINS_CLASSI, Filtri.FILTRO_STATO, Filtri.FILTRO_STATO_VALORE_ABILITATO);
			}
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			List<Plugin> pluginsClassiList = new ArrayList<>();
			List<Plugin> p = driver.pluginsClassiList(ricercaPlugin);
			
			Hashtable<String, Plugin> plugins = new Hashtable<String, Plugin>();
			List<String> keys = new ArrayList<String>();
			if(p!=null && p.size()>0){
				for (Plugin plugin : p) {
					String key = plugin.getLabel() + "_" + plugin.getClassName();
					keys.add(key); // possono esistere più plugin con lo stesso label
					plugins.put(key, plugin);
				}	
				
				// ordino
				Collections.sort(keys);
				
				for (String key : keys) {
					pluginsClassiList.add(plugins.get(key));
				}
			}
			
			return pluginsClassiList;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RegistroPlugins getRegistroPlugins() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRegistroPlugins";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getRegistroPlugins();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public int numeroPluginsClassiList() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "numeroPluginsClassiList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.numeroPluginsClassiList();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPlugin(TipoPlugin tipoPlugin, String tipo, String label, String className) throws DriverConfigurazioneException { 
		Connection con = null;
		String nomeMetodo = "existsPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.existsPlugin(tipoPlugin, tipo,label, className); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPluginConTipo(TipoPlugin tipoPlugin, String tipo) throws DriverConfigurazioneException { 
		Connection con = null;
		String nomeMetodo = "existsPluginConTipo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.existsPluginConTipo(tipoPlugin, tipo); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPluginConLabel(TipoPlugin tipoPlugin, String label) throws DriverConfigurazioneException { 
		Connection con = null;
		String nomeMetodo = "existsPluginConLabel";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.existsPluginConLabel(tipoPlugin, label); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsPluginConClassName(TipoPlugin tipoPlugin, String className) throws DriverConfigurazioneException { 
		Connection con = null;
		String nomeMetodo = "existsPluginConClassName";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.existsPluginConClassName(tipoPlugin, className); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Plugin getPlugin(long idPlugin) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getPlugin(idPlugin);  
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Plugin getPlugin(TipoPlugin tipoPlugin, String tipo, boolean throwNotFound) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getPlugin";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getPlugin(tipoPlugin,tipo, throwNotFound);  
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isPluginInUso(String className, String label, String tipoPlugin, String tipo, HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "isPluginInUso";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.isPluginInUso(className, label, tipoPlugin, tipo, whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
		
	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio per il componente di cooperazione
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteCooperazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.getGestioneErrore(true);
	}

	/**
	 * Restituisce la gestione dell'errore di default definita nella Porta di
	 * Dominio per il componente di integrazione
	 * 
	 * @return La gestione dell'errore
	 * 
	 */
	public GestioneErrore getGestioneErroreComponenteIntegrazione() throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		return this.getGestioneErrore(false);
	}

	private GestioneErrore getGestioneErrore(boolean cooperazione) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		Connection con = null;
		String nomeMetodo = "getGestioneErrore";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			if (cooperazione)
				return driver.getDriverConfigurazioneDB().getGestioneErroreComponenteCooperazione();
			else
				return driver.getDriverConfigurazioneDB().getGestioneErroreComponenteIntegrazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoRegistro getAccessoRegistro() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoRegistro";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoRegistro();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoConfigurazione getAccessoConfigurazione() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoConfigurazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoConfigurazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public AccessoDatiAutorizzazione getAccessoDatiAutorizzazione() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getAccessoDatiAutorizzazione";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getAccessoDatiAutorizzazione();

		} catch (DriverConfigurazioneNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public SystemProperties getSystemPropertiesPdD() throws DriverConfigurazioneNotFound, DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getSystemPropertiesPdD";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getSystemPropertiesPdD();

		} catch (DriverConfigurazioneNotFound de) {
			//ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			return new SystemProperties();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public List<Property> systemPropertyList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "systemPropertyList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().systemPropertyList(ricerca);

		} 
		catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public RoutingTable getRoutingTable() throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "getRoutingTable";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);

			return driver.getDriverConfigurazioneDB().getRoutingTable();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	
	public List<Filtro> filtriList(ISearch ricerca) throws AuditException {
		Connection con = null;
		String nomeMetodo = "filtriList";
		DriverAudit driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAudit(con, this.tipoDB);
			return driver.filtriList(ricerca, Liste.FILTRI);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new AuditException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public Filtro getFiltro(long idFiltro) throws AuditException {
		Connection con = null;
		String nomeMetodo = "getFiltro";
		DriverAudit driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverAudit(con, this.tipoDB);
			return driver.getFiltro(idFiltro);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new AuditException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public UrlInvocazioneAPI getConfigurazioneUrlInvocazione(String protocollo,
			RuoloContesto ruolo, ServiceBinding serviceBinding, String interfaceName, IDSoggetto soggettoOperativo,
			AccordoServizioParteComuneSintetico aspc, String canalePorta) throws Exception {
		
		IProtocolFactory<?>protocolFactory = ProtocolFactoryManager.getInstance().getProtocolFactoryByName(protocollo);
		
		Configurazione config = this.getConfigurazioneGenerale();
		
		ConfigurazioneUrlInvocazione configurazioneUrlInvocazione = null;
		if(config!=null && config.getUrlInvocazione()!=null) {
			configurazioneUrlInvocazione = config.getUrlInvocazione();
		}
		
		List<String> tags = new ArrayList<String>();
		if(aspc!=null && aspc.getGruppo()!=null && aspc.getGruppo().size()>0) {
			for (int i = 0; i < aspc.getGruppo().size(); i++) {
				tags.add(aspc.getGruppo().get(i).getNome());
			}
		}
		
		String canaleApi = null;
		if(aspc!=null) {
			canaleApi = aspc.getCanale();
		}
		String canale = CanaliUtils.getCanale(config.getGestioneCanali(), canaleApi, canalePorta);
		
		return UrlInvocazioneAPI.getConfigurazioneUrlInvocazione(configurazioneUrlInvocazione, protocolFactory, ruolo, serviceBinding, interfaceName, soggettoOperativo,
				tags, canale);

	}
	
	
	/**
	 * Accesso alla configurazione di controllo del traffico
	 * 
	 * @return configurazione controllo del traffico della Pdd
	 * @throws DriverControlStationNotFound
	 * @throws DriverControlStationException
	 */
	public ConfigurazioneGenerale getConfigurazioneControlloTraffico() throws DriverControlStationNotFound, DriverControlStationException {
		Connection con = null;
		String nomeMetodo = "getConfigurazioneControlloTraffico";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazioneControlloTraffico();
			
		} catch (DriverControlStationNotFound de) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + de.getMessage(),de);
			throw de;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}

	}
	
	public long countConfigurazionePolicy(Search ricerca)  throws DriverControlStationNotFound, DriverControlStationException {
		String nomeMetodo = "countConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.countConfigurazioneControlloTrafficoConfigurazionePolicy(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long countAttivazionePolicy(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "countAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.countConfigurazioneControlloTrafficoAttivazionePolicy(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<ConfigurazionePolicy> configurazionePolicyList(Search ricerca)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "configurazionePolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.configurazioneControlloTrafficoConfigurazionePolicyList(ricerca);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<TipoRisorsaPolicyAttiva> attivazionePolicyTipoRisorsaList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationException{
		String nomeMetodo = "attivazionePolicyTipoRisorsaList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.configurazioneControlloTrafficoAttivazionePolicyTipoRisorsaList(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AttivazionePolicy> attivazionePolicyList(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationException{
		String nomeMetodo = "attivazionePolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.configurazioneControlloTrafficoAttivazionePolicyList(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}

	public List<AttivazionePolicy> attivazionePolicyListByFilter(Search ricerca, RuoloPolicy ruoloPorta, String nomePorta,
			IDSoggetto filtroSoggettoFruitore, IDServizioApplicativo filtroApplicativoFruitore,String filtroRuoloFruitore,
			IDSoggetto filtroSoggettoErogatore, String filtroRuoloErogatore,
			IDServizio filtroServizioAzione, String filtroRuolo)  throws DriverControlStationException{
		String nomeMetodo = "attivazionePolicyListByFilter";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.configurazioneControlloTrafficoAttivazionePolicyListByFilter(ricerca, ruoloPorta, nomePorta,
					filtroSoggettoFruitore, filtroApplicativoFruitore, filtroRuoloFruitore,
					filtroSoggettoErogatore, filtroRuoloErogatore,
					filtroServizioAzione, filtroRuolo);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(String alias, RuoloPolicy ruoloPorta, String nomePorta)  throws DriverControlStationException, DriverControlStationNotFound{
		String nomeMetodo = "getAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.getAttivazionePolicy(alias, ruoloPorta, nomePorta);
		} catch(NotFoundException notFound) {
			throw new DriverControlStationNotFound(notFound.getMessage(),notFound);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean usedInConfigurazioneControlloTrafficoAttivazionePolicy(RuoloPolicy ruoloPorta, String nomePorta, String azione)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "usedInConfigurazioneControlloTrafficoAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.usedInConfigurazioneControlloTrafficoAttivazionePolicy(ruoloPorta, nomePorta, azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<InfoPolicy> infoPolicyList(Boolean builtIn)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "infoPolicyList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getInfoPolicyList(builtIn, null);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public InfoPolicy getInfoPolicy(String idPolicyParam)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getInfoPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			List<InfoPolicy> lst = driver.getInfoPolicyList(null, idPolicyParam); 
			return (lst != null && lst.size() > 0) ? lst.get(0) : null;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AttivazionePolicy> findInUseAttivazioni(String idPolicy)   throws DriverControlStationNotFound, DriverControlStationException{
		return findInUseAttivazioni(idPolicy, false);
	}
	
	public List<AttivazionePolicy> findInUseAttivazioni(String idPolicy, boolean escludiDisabilitate)   throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "findInUseAttivazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.findInUseAttivazioni(idPolicy, escludiDisabilitate);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public long countInUseAttivazioni(String idPolicy)   throws DriverControlStationNotFound, DriverControlStationException{
		return countInUseAttivazioni(idPolicy, false);
	}
	
	public long countInUseAttivazioni(String idPolicy, boolean escludiDisabilitate)   throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "countInUseAttivazioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);	
			
			return driver.countInUseAttivazioni(idPolicy, escludiDisabilitate);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public ConfigurazionePolicy getConfigurazionePolicy(long id)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazionePolicy(id);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+id+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ConfigurazionePolicy getConfigurazionePolicy(String nomePolicy)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getConfigurazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getConfigurazionePolicy(nomePolicy);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+nomePolicy+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(long id)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAttivazionePolicy(id);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] AttivazionePolicy ["+id+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getAttivazionePolicy(String nomePolicy)  throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getAttivazionePolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAttivazionePolicy(nomePolicy);
		} catch (DriverControlStationNotFound e) {
			ControlStationCore.log.debug("[ControlStationCore::" + nomeMetodo + "] Configurazione ["+nomePolicy+"] non presente");
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Integer getFreeCounterForGlobalPolicy(String policyId) throws DriverControlStationException{
		String nomeMetodo = "getFreeCounterForGlobalPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getFreeCounterForGlobalPolicy(policyId);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getPolicy(String policyId, AttivazionePolicyFiltro filtro, AttivazionePolicyRaggruppamento groupBy,
			RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getGlobalPolicy";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getPolicy(policyId, filtro, groupBy, ruoloPorta, nomePorta); 
		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public AttivazionePolicy getPolicyByAlias(String alias,
			RuoloPolicy ruoloPorta, String nomePorta) throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getGlobalPolicyByAlias";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getPolicyByAlias(alias, ruoloPorta, nomePorta);
		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<AttivazionePolicy> getPolicyByServizioApplicativo(IDServizioApplicativo idServizioApplicativo) throws DriverControlStationNotFound, DriverControlStationException{
		String nomeMetodo = "getPolicyByServizioApplicativo";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getPolicyByServizioApplicativo(idServizioApplicativo);
		} catch (DriverControlStationNotFound e) {
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	
	public List<IDSoggetto> getSoggettiErogatori(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiErogatori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggettiErogatori(protocolloSelezionato,protocolliSupportati);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizio> getServizi(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tag) throws DriverControlStationException{
		String nomeMetodo = "getServizi";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServizi(protocolloSelezionato,protocolliSupportati, tipoErogatore, nomeErogatore, tag);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<String> getAzioni(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getAzioni";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getAzioni(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiErogatori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws DriverControlStationException {
		String nomeMetodo = "getServiziApplicativiErogatori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getServiziApplicativiErogatori(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio, 
					azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDSoggetto> getSoggetti(String protocolloSelezionato,List<String> protocolliSupportati) throws DriverControlStationException{
		String nomeMetodo = "getSoggetti";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggetti(protocolloSelezionato,protocolliSupportati);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDSoggetto> getSoggettiFruitori(String protocolloSelezionato,List<String> protocolliSupportati, 
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio) throws DriverControlStationException{
		String nomeMetodo = "getSoggettiFruitori";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
		
			return driver.getSoggettiFruitori(protocolloSelezionato,protocolliSupportati, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati,
			String tipoFruitore, String nomeFruitore,	
			String tipoErogatore, String nomeErogatore, String tipoServizio, String nomeServizio, Integer versioneServizio,
			String azione) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServiziApplicativiFruitore(protocolloSelezionato,protocolliSupportati, 
					tipoFruitore, nomeFruitore, 
					tipoErogatore, nomeErogatore, tipoServizio, nomeServizio, versioneServizio,
					azione);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativiFruitore(String protocolloSelezionato,List<String> protocolliSupportati,
			String tipoFruitore, String nomeFruitore) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServiziApplicativiFruitore(protocolloSelezionato,protocolliSupportati, 
					tipoFruitore, nomeFruitore);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<IDServizioApplicativo> getServiziApplicativi(String protocolloSelezionato,List<String> protocolliSupportati,
			String tipoProprietario, String nomeProprietario) throws DriverControlStationException{
		String nomeMetodo = "getServiziApplicativiFruitore";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getServiziApplicativi(protocolloSelezionato,protocolliSupportati, 
					tipoProprietario, nomeProprietario);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public Map<String, Properties> readGestorePolicyTokenPropertiesConfiguration(long idGenericProperties) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "readGestorePolicyTokenPropertiesConfiguration";
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			Map<String, String> readProperties = DBPropertiesUtils.readProperties(con, this.tipoDB, CostantiDB.CONFIG_GENERIC_PROPERTY, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_NOME, 
					CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_VALORE, CostantiDB.CONFIG_GENERIC_PROPERTY_COLUMN_ID_PROPS, idGenericProperties);
			return DBPropertiesUtils.toMultiMap(readProperties);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}	
	}
	public List<GenericProperties> gestorePolicyTokenList(Integer idLista, String tipologia, ISearch ricerca) throws DriverConfigurazioneException {
		List<String> tipologiaList = new ArrayList<>();
		tipologiaList.add(tipologia);
		return gestorePolicyTokenList(idLista, tipologiaList, ricerca);
	}
	public List<GenericProperties> gestorePolicyTokenList(Integer idLista, List<String> tipologia, ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "gestorePolicyTokenList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(tipologia, idLista, ricerca,false);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public GenericProperties getGenericProperties(long idGenericProperties) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		String nomeMetodo = "getGenericProperties";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(idGenericProperties);
		}catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		}catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public GenericProperties getGenericProperties(String nome, String tipologia) throws DriverConfigurazioneException,DriverConfigurazioneNotFound{
		Connection con = null;
		String nomeMetodo = "getGenericProperties";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getGenericProperties(tipologia,nome);
		} catch (DriverConfigurazioneNotFound e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<PortaApplicativa> listaPorteApplicativeUtilizzateDaPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "listaPorteApplicativeUtilizzateDaPolicyGestioneToken";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPorteApplicativeByPolicyGestioneToken(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<PortaDelegata> listaPorteDelegateUtilizzateDaPolicyGestioneToken(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "listaPorteDelegateUtilizzateDaPolicyGestioneToken";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().getPorteDelegateByPolicyGestioneToken(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public boolean isPolicyNegoziazioneTokenUsedInConnettore(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "listaPorteDelegateUtilizzateDaPolicyGestioneToken";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().isPolicyNegoziazioneTokenUsedInConnettore(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public List<CanaleConfigurazione> canaleConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException {
		Connection con = null;
		String nomeMetodo = "canaleConfigurazioneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().canaleConfigurazioneList(ricerca); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public boolean existsCanale(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsCanale";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsCanale(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<CanaleConfigurazioneNodo> canaleNodoConfigurazioneList(ISearch ricerca) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "canaleNodoConfigurazioneList";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().canaleNodoConfigurazioneList(ricerca); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean existsCanaleNodo(String nome) throws DriverConfigurazioneException{
		Connection con = null;
		String nomeMetodo = "existsCanaleNodo";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.getDriverConfigurazioneDB().existsCanaleNodo(nome); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isCanaleInUso(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso,	boolean normalizeObjectIds) throws DriverConfigurazioneException {
		Connection con = null; 
		String nomeMetodo = "isCanaleInUso";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
	
			return DBOggettiInUsoUtils.isCanaleInUso(con, this.tipoDB, canale, whereIsInUso, normalizeObjectIds);
	
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isCanaleInUsoRegistro(CanaleConfigurazione canale, Map<ErrorsHandlerCostant, List<String>> whereIsInUso,	boolean normalizeObjectIds) throws DriverConfigurazioneException {
		Connection con = null; 
		String nomeMetodo = "isCanaleInUsoRegistro";
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
	
			return DBOggettiInUsoUtils.isCanaleInUsoRegistro(con, this.tipoDB, canale, whereIsInUso, normalizeObjectIds);
	
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getDettagliCanaleInUso(CanaleConfigurazione canale) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean canaleInUso  = this.isCanaleInUso(canale, whereIsInUso, normalizeObjectIds);
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(canaleInUso) {
			String s = DBOggettiInUsoUtils.toString(canale, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ConfigurazioneCostanti.LABEL_CANALE_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isGenericPropertiesInUso(IDGenericProperties idGP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverConfigurazioneException {
		String nomeMetodo = "isGenericPropertiesInUso";
		Connection con = null; 
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
	
			return DBOggettiInUsoUtils.isGenericPropertiesInUso(con, this.tipoDB, idGP, whereIsInUso, normalizeObjectIds);
	
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getDettagliTokenPolicyInUso(IDGenericProperties idGP) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean tokenPolicyInUso  = this.isGenericPropertiesInUso(idGP, whereIsInUso, normalizeObjectIds);
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(tokenPolicyInUso) {
			String s = DBOggettiInUsoUtils.toString(idGP, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ConfigurazioneCostanti.LABEL_TOKEN_POLICY_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isRateLimitingPolicyInUso(IdPolicy idRP, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverConfigurazioneException {
		String nomeMetodo = "isRateLimitingPolicyInUso";
		Connection con = null; 
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
	
			return DBOggettiInUsoUtils.isRateLimitingPolicyInUso(con, this.tipoDB, idRP, whereIsInUso, normalizeObjectIds);
	
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getDettagliRateLimitingPolicyInUso(IdPolicy idRP) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean rateLimitingPolicyInUso  = this.isRateLimitingPolicyInUso(idRP, whereIsInUso, normalizeObjectIds);
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(rateLimitingPolicyInUso) {
			String s = DBOggettiInUsoUtils.toString(idRP, whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ConfigurazioneCostanti.LABEL_RATE_LIMITING_POLICY_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}
	
	public boolean isPluginClasseInUso(IdPlugin idPlugin, Map<ErrorsHandlerCostant, List<String>> whereIsInUso, boolean normalizeObjectIds) throws DriverConfigurazioneException {
		String nomeMetodo = "isPluginClasseInUso";
		Connection con = null; 
		
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
	
			return DBOggettiInUsoUtils.isPluginInUso(con, this.tipoDB, idPlugin.getClassName(), idPlugin.getLabel(), idPlugin.getTipoPlugin(), idPlugin.getTipo(), 
					whereIsInUso, normalizeObjectIds);

		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverConfigurazioneException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public String getDettagliPluginClasseInUso(IdPlugin idPlugin) throws DriverConfigurazioneException, DriverConfigurazioneNotFound {
		HashMap<ErrorsHandlerCostant, List<String>> whereIsInUso = new HashMap<ErrorsHandlerCostant, List<String>>();
		boolean normalizeObjectIds = true;
		boolean pluginClasseInUso  = this.isPluginClasseInUso(idPlugin, whereIsInUso, normalizeObjectIds);
		
		StringBuilder inUsoMessage = new StringBuilder();
		if(pluginClasseInUso) {
			String s = DBOggettiInUsoUtils.toString(idPlugin.getClassName(), idPlugin.getLabel(), idPlugin.getTipoPlugin(), idPlugin.getTipo(), whereIsInUso, false, "\n");
			if(s!=null && s.startsWith("\n") && s.length()>1) {
				s = s.substring(1);
			}
			inUsoMessage.append(s);
			inUsoMessage.append("\n");
		} else {
			inUsoMessage.append(ConfigurazioneCostanti.LABEL_PLUGIN_CLASSE_IN_USO_BODY_HEADER_NESSUN_RISULTATO);
		}
		
		return inUsoMessage.toString();
	}

	public List<ConfigurazioneAllarmeBean> allarmiList(Search ricerca, RuoloPorta ruoloPorta, String nomePorta) throws DriverControlStationException{ 
		String nomeMetodo = "allarmiList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.allarmiList(ricerca, ruoloPorta, nomePorta);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	public boolean existsAllarme(String nome) throws DriverControlStationException{
		Connection con = null;
		String nomeMetodo = "existsAllarme";
		DriverControlStationDB driver = null;

		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			return driver.existsAllarme(nome); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		} finally {
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ConfigurazioneAllarmeBean getAllarme(Long id) throws DriverControlStationException{ 
		String nomeMetodo = "getAllarme";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAllarme(id);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public ConfigurazioneAllarmeBean getAllarme(String nome) throws DriverControlStationException{ 
		String nomeMetodo = "getAllarmeByNome";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.getAllarme(nome);
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public boolean isUsableFilter(ConfigurazioneAllarmeBean configurazioneAllarme) throws Exception{
		return this._isUsable(configurazioneAllarme, true);
	}
	
	public boolean isUsableGroupBy(ConfigurazioneAllarmeBean configurazioneAllarme) throws Exception{
		return this._isUsable(configurazioneAllarme, false);
	}
	
	public boolean _isUsable(ConfigurazioneAllarmeBean configurazioneAllarme, boolean filter) throws Exception{
		String nomeMetodo = "_isUsable";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			Plugin plugin = driver.getPlugin(TipoPlugin.ALLARME, configurazioneAllarme.getPlugin().getTipo(), true);
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, configurazioneAllarme.getPlugin().getTipo(), plugin.getClassName(), ControlStationCore.log);
			IAlarmProcessing alarmProcessing = (IAlarmProcessing) bl.newInstance();
			return filter ? alarmProcessing.isUsableFilter() : alarmProcessing.isUsableGroupBy();
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Parameter<?>> instanceParameters(ConfigurazioneAllarmeBean configurazioneAllarme, Context context) throws Exception{
		String nomeMetodo = "instanceParameters";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			Plugin plugin = driver.getPlugin(TipoPlugin.ALLARME, configurazioneAllarme.getPlugin().getTipo(), true);
			
			List<Parameter<?>> res = null;
			
			IDynamicLoader bl = DynamicFactory.getInstance().newDynamicLoader(TipoPlugin.ALLARME, plugin.getTipo(), plugin.getClassName(), ControlStationCore.log);
			List<Parameter<?>> sdkParameters = bl.getParameters(context);
			
			if(sdkParameters!=null && sdkParameters.size()>0){
				
				res = new ArrayList<Parameter<?>>();
				
				for (Parameter<?> sdkParameter : sdkParameters) {
					Parameter<?> par = DynamicComponentUtils.createDynamicComponentParameter(sdkParameter, bl);
					((BaseComponent<?>)par).setContext(context);
					res.add(par);
				}
			}

			return res;
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
	
	public List<Parameter<?>> getParameters(ConfigurazioneAllarmeBean configurazioneAllarme, Context context) throws Exception{
		List<Parameter<?>> parameters = this.instanceParameters(configurazioneAllarme, context);
		if(parameters!=null && parameters.size()>0){
			for (AllarmeParametro parDB : configurazioneAllarme.getAllarmeParametroList()) {
				for (Parameter<?> par : parameters) {
					if(parDB.getIdParametro().equals(par.getId())){
						par.setValueAsString(parDB.getValore());
						break;
					}
				}
			}
		}
		
		return parameters;
	}
	public List<ConfigurazioneAllarmeHistoryBean> allarmiHistoryList(Search ricerca, Long idAllarme) throws Exception {
		String nomeMetodo = "allarmiHistoryList";
		Connection con = null;
		DriverControlStationDB driver = null;
		try {
			// prendo una connessione
			con = ControlStationCore.dbM.getConnection();
			
			// istanzio il driver
			driver = new DriverControlStationDB(con, null, this.tipoDB);
			
			return driver.allarmiHistoryList(ricerca, idAllarme); 
		} catch (Exception e) {
			ControlStationCore.log.error("[ControlStationCore::" + nomeMetodo + "] Exception :" + e.getMessage(), e);
			throw new DriverControlStationException("[ControlStationCore::" + nomeMetodo + "] Error :" + e.getMessage(),e);
		}finally{
			ControlStationCore.dbM.releaseConnection(con);
		}
	}
}
