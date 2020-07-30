/*
 * GovWay - A customizable API Gateway 
 * https://govway.org
 * 
 * Copyright (c) 2005-2020 Link.it srl (https://link.it).
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
package org.openspcoop2.web.monitor.core.dao;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.openspcoop2.core.commons.search.AccordoServizioParteComune;
import org.openspcoop2.core.commons.search.AccordoServizioParteSpecifica;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComune;
import org.openspcoop2.core.commons.search.IdAccordoServizioParteComuneGruppo;
import org.openspcoop2.core.commons.search.PortType;
import org.openspcoop2.core.commons.search.Soggetto;
import org.openspcoop2.core.commons.search.constants.TipoPdD;
import org.openspcoop2.core.id.IDAccordo;
import org.openspcoop2.core.id.IDGruppo;
import org.openspcoop2.core.id.IDServizio;
import org.openspcoop2.core.id.IDSoggetto;
import org.openspcoop2.generic_project.utils.ServiceManagerProperties;
import org.openspcoop2.web.monitor.core.core.PermessiUtenteOperatore;
import org.openspcoop2.web.monitor.core.listener.AbstractConsoleStartupListener;
import org.openspcoop2.web.monitor.core.logger.LoggerManager;
import org.slf4j.Logger;


/***
 * 
 * Funzionalita' di supporto per la gestione delle maschere di ricerca.
 * 
 * @author Pintori Giuliano (pintori@link.it)
 * @author $Author$
 * @version $Rev$, $Date$
 *
 */
public class DynamicUtilsService implements IDynamicUtilsService{

	
	// **** COSTRUTTORE PER METODI CHE IMPLEMENTANO L'INTERFACCIA IDynamicUtilsService ****
	
	private DynamicUtilsServiceEngine driver;
	private static Logger log = LoggerManager.getPddMonitorSqlLogger(); 

	public DynamicUtilsService(){
		this(null);
	}
	public DynamicUtilsService(org.openspcoop2.core.commons.search.dao.IServiceManager serviceManager){
		this.driver = new DynamicUtilsServiceEngine(serviceManager);
	}
	public DynamicUtilsService(Connection con, boolean autoCommit){
		this(con, autoCommit, null, DynamicUtilsService.log);
	}
	public DynamicUtilsService(Connection con, boolean autoCommit, Logger log){
		this(con, autoCommit, null, log);
	}
	public DynamicUtilsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties){
		this(con, autoCommit, serviceManagerProperties, DynamicUtilsService.log);
	}
	public DynamicUtilsService(Connection con, boolean autoCommit, ServiceManagerProperties serviceManagerProperties, Logger log){
		this.driver = new DynamicUtilsServiceEngine(con, autoCommit, serviceManagerProperties, log);
	}

	
	
	
	// **** UTILITY ****
	
	public static String buildKey(Object ... params) {
		StringBuilder sb = new StringBuilder();
		if(params!=null && params.length>0) {
			for (Object object : params) {
				if(sb.length()>0) {
					sb.append("_");
				}
				if(object!=null) {
					sb.append(object.toString());
				}
				else {
					sb.append("-");
				}
			}
		}
		return sb.toString();
	}
	
	

	
	
	// **** METODI CHE IMPLEMENTANO L'INTERFACCIA ****
	
	@Override
	public int countPdD(String protocollo) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(protocollo);
			String methodName = "countPdD";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class},
						protocollo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countPdD(protocollo);
		}
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey("_noSearchTipo",tipoProtocollo, input);
			String methodName = "soggettiAutoComplete";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class},
						tipoProtocollo, input);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.soggettiAutoComplete(tipoProtocollo, input);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> soggettiAutoComplete(String tipoProtocollo,String input,Boolean searchTipo) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey("_searchTipo", tipoProtocollo, input, searchTipo);
			String methodName = "soggettiAutoComplete";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, Boolean.class},
						tipoProtocollo, input, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.soggettiAutoComplete(tipoProtocollo, input, searchTipo);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, idPorta);
			String methodName = "findElencoSoggetti";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class},
						tipoProtocollo, idPorta);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoSoggetti(tipoProtocollo, idPorta);
		}
	}
	@Override
	public int countElencoSoggetti(String tipoProtocollo, String idPorta){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, idPorta);
			String methodName = "countElencoSoggetti";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class},
						tipoProtocollo, idPorta);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoSoggetti(tipoProtocollo, idPorta);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta, String input){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(input==null || StringUtils.isEmpty(input)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey("_noSearchTipo",tipoProtocollo, idPorta, input);
			String methodName = "findElencoSoggetti";
			try {
				return (List<Soggetto>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class},
						tipoProtocollo, idPorta, input);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoSoggetti(tipoProtocollo, idPorta, input);
		}
	}
	@Override
	public int countElencoSoggetti(String tipoProtocollo, String idPorta, String input) {
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(input==null || StringUtils.isEmpty(input)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey("_noSearchTipo",tipoProtocollo, idPorta, input);
			String methodName = "countElencoSoggetti";
			try {
				return (Integer) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class},
						tipoProtocollo, idPorta, input);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoSoggetti(tipoProtocollo, idPorta, input);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> findElencoSoggetti(String tipoProtocollo, String idPorta, String input,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(input==null || StringUtils.isEmpty(input)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey("_searchTipo",tipoProtocollo, idPorta, input, searchTipo);
			String methodName = "findElencoSoggetti";
			try {
				return (List<Soggetto>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, Boolean.class},
						tipoProtocollo, idPorta, input, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoSoggetti(tipoProtocollo, idPorta, input, searchTipo);
		}
	}
	@Override
	public int countElencoSoggetti(String tipoProtocollo, String idPorta, String input,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(input==null || StringUtils.isEmpty(input)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey("_searchTipo",tipoProtocollo, idPorta, input, searchTipo);
			String methodName = "countElencoSoggetti";
			try {
				return (Integer) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, Boolean.class},
						tipoProtocollo, idPorta, input, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoSoggetti(tipoProtocollo, idPorta, input, searchTipo);
		}
	}
	
	

	@Override
	public Soggetto findSoggettoByTipoNome(String tipoSoggetto,	String nomeSoggetto) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoSoggetto, nomeSoggetto);
			String methodName = "findSoggettoByTipoNome";
			try {
				return (Soggetto) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class},
						tipoSoggetto, nomeSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findSoggettoByTipoNome(tipoSoggetto, nomeSoggetto);
		}
	}
	
	@Override
	public Soggetto findSoggettoById(Long idSoggetto) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(idSoggetto);
			String methodName = "findSoggettoById";
			try {
				return (Soggetto) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {Long.class},
						idSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findSoggettoById(idSoggetto);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> findElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoSoggetto);
			String methodName = "findElencoSoggettiFromTipoSoggetto";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class},
						tipoSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoSoggettiFromTipoSoggetto(tipoSoggetto);
		}
	}
	
	@Override
	public int countElencoSoggettiFromTipoSoggetto(String tipoSoggetto) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoSoggetto);
			String methodName = "countElencoSoggettiFromTipoSoggetto";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class},
						tipoSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoSoggettiFromTipoSoggetto(tipoSoggetto);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> findElencoSoggettiFromTipoPdD(String tipoProtocollo, TipoPdD tipoPdD) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoPdD);
			String methodName = "findElencoSoggettiFromTipoPdD";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, TipoPdD.class},
						tipoProtocollo, tipoPdD);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoSoggettiFromTipoPdD(tipoProtocollo, tipoPdD);
		}
	}
	
	@Override
	public int countElencoSoggettiFromTipoTipoPdD(String tipoProtocollo, TipoPdD tipoPdD){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoPdD);
			String methodName = "countElencoSoggettiFromTipoTipoPdD";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, TipoPdD.class},
						tipoProtocollo, tipoPdD);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoSoggettiFromTipoTipoPdD(tipoProtocollo, tipoPdD);
		}
	}
	
	@Override
	public boolean checkTipoPdd(String nome,TipoPdD tipoPdD){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(nome, tipoPdD);
			String methodName = "checkTipoPdd";
			try {
				return (Boolean) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, TipoPdD.class},
						nome, tipoPdD);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return false;
			}
		}
		else {
			return this.driver.checkTipoPdd(nome, tipoPdD);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??");
			String methodName = "findElencoServizi";
			try {
				return (List<Map<String, Object>>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class},
						tipoProtocollo, soggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoServizi(tipoProtocollo, soggetto);
		}
	}
	
	@Override
	public int countElencoServizi(String tipoProtocollo,Soggetto soggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??");
			String methodName = "countElencoServizi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class},
						tipoProtocollo, soggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoServizi(tipoProtocollo, soggetto);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto, String val){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??", "VAL:"+val);
			String methodName = "findElencoServizi";
			try {
				return (List<Map<String, Object>>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class, String.class},
						tipoProtocollo, soggetto, val);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoServizi(tipoProtocollo, soggetto, val);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo,Soggetto soggetto, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??", "VAL:"+val, "SearchTipo:"+searchTipo);
			String methodName = "findElencoServizi";
			try {
				return (List<Map<String, Object>>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class, String.class, Boolean.class},
						tipoProtocollo, soggetto, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoServizi(tipoProtocollo, soggetto, val, searchTipo);
		}
	}
	
	

	@Override
	public AccordoServizioParteComune getAccordoServizio(String tipoProtocollo, IDSoggetto idSoggetto, String tipoServizio, String nomeServizio, Integer versioneServizio){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, idSoggetto, tipoServizio, nomeServizio, versioneServizio);
			String methodName = "getAccordoServizio";
			try {
				return (AccordoServizioParteComune) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, IDSoggetto.class, String.class, String.class, Integer.class},
						tipoProtocollo, idSoggetto, tipoServizio, nomeServizio, versioneServizio);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getAccordoServizio(tipoProtocollo, idSoggetto, tipoServizio, nomeServizio, versioneServizio);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IdAccordoServizioParteComuneGruppo> getAccordoServizioGruppi(IdAccordoServizioParteComune id){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = null;
			if(id!=null) {
				key = buildKey(id.getNome(), id.getIdSoggetto()!=null ? (id.getIdSoggetto().getTipo()+"/"+id.getIdSoggetto().getNome()) : "??" , id.getVersione());
			}
			else {
				key = "??";
			}
			String methodName = "getAccordoServizioGruppi";
			try {
				return (List<IdAccordoServizioParteComuneGruppo>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {IdAccordoServizioParteComune.class},
						id);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getAccordoServizioGruppi(id);
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> findElencoServizi(String tipoProtocollo){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo);
			String methodName = "findElencoServizi";
			try {
				return (List<Map<String, Object>>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class},
						tipoProtocollo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoServizi(tipoProtocollo);
		}
	}
	
	@Override
	public int countElencoServizi(String tipoProtocollo ){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo);
			String methodName = "countElencoServizi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class},
						tipoProtocollo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoServizi(tipoProtocollo);
		}
	}
	

	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> findElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??");
			String methodName = "findElencoServiziApplicativi";
			try {
				return (List<Object>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class},
						tipoProtocollo, soggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findElencoServiziApplicativi(tipoProtocollo, soggetto);
		}
	}

	@Override
	public int countElencoServiziApplicativi(String tipoProtocollo,Soggetto soggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, soggetto!=null ? (soggetto.getTipoSoggetto()+"/"+soggetto.getNomeSoggetto()) : "??");
			String methodName = "countElencoServiziApplicativi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, Soggetto.class},
						tipoProtocollo, soggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countElencoServiziApplicativi(tipoProtocollo, soggetto);
		}
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> findAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio,String tipoErogatore , String nomeErogatore, Integer versioneServizio, String val){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, "VAL:"+ val);
			String methodName = "findAzioniFromServizio";
			try {
				return (Map<String, String>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class,String.class, String.class, Integer.class, String.class },
						tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, val);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.findAzioniFromServizio(tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, val);
		}
	}
	
	@Override
	public int countAzioniFromServizio(String tipoProtocollo,String tipoServizio ,String nomeServizio,String tipoErogatore , String nomeErogatore, Integer versioneServizio, String val){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, "VAL:"+ val);
			String methodName = "countAzioniFromServizio";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class,String.class, String.class, Integer.class, String.class },
						tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, val);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countAzioniFromServizio(tipoProtocollo, tipoServizio , nomeServizio, tipoErogatore ,  nomeErogatore,  versioneServizio, val);
		}
	}
 

	
	@Override
	public PortType getPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, idAccordo , nomeServizio);
			String methodName = "getPortTypeFromAccordoServizio";
			try {
				return (PortType) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, IDAccordo.class, String.class },
						tipoProtocollo, idAccordo , nomeServizio);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getPortTypeFromAccordoServizio(tipoProtocollo, idAccordo , nomeServizio);
		}
	}
	
	@Override
	public int countPortTypeFromAccordoServizio(String tipoProtocollo,IDAccordo idAccordo ,String nomeServizio){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, idAccordo , nomeServizio);
			String methodName = "countPortTypeFromAccordoServizio";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, IDAccordo.class, String.class },
						tipoProtocollo, idAccordo , nomeServizio);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countPortTypeFromAccordoServizio(tipoProtocollo, idAccordo , nomeServizio);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IDGruppo> getGruppi(){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = "";
			String methodName = "getGruppi";
			try {
				return (List<IDGruppo>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getGruppi();
		}
	}
	@Override
	public int countGruppi(){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = "";
			String methodName = "countGruppi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countGruppi();
		}
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public List<AccordoServizioParteComune> getAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto, Boolean isReferente, Boolean isErogatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
			String methodName = "getAccordiServizio";
			try {
				return (List<AccordoServizioParteComune>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, Boolean.class, Boolean.class },
						tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getAccordiServizio(tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
		}
	}
	
	@Override
	public int countAccordiServizio(String tipoProtocollo,String tipoSoggetto, String nomeSoggetto, Boolean isReferente, Boolean isErogatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
			String methodName = "countAccordiServizio";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, Boolean.class, Boolean.class },
						tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countAccordiServizio(tipoProtocollo, tipoSoggetto , nomeSoggetto, isReferente ,  isErogatore);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
			String methodName = "getServizi";
			try {
				return (List<AccordoServizioParteSpecifica>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class },
						tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getServizi(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, String val){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, "VAL:"+val);
			String methodName = "getServizi";
			try {
				return (List<AccordoServizioParteSpecifica>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, String.class },
						tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, val);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getServizi(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, val);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<AccordoServizioParteSpecifica> getServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, "VAL:"+val, "searchTipo:"+searchTipo);
			String methodName = "getServizi";
			try {
				return (List<AccordoServizioParteSpecifica>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, String.class, Boolean.class },
						tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getServizi(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto, val, searchTipo);
		}
	}
	@Override
	public int countServizi(String tipoProtocollo,String uriAccordoServizio, String tipoSoggetto , String nomeSoggetto){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
			String methodName = "countServizi";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class },
						tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countServizi(tipoProtocollo, uriAccordoServizio , tipoSoggetto, nomeSoggetto);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> getSoggettiErogatoreAutoComplete(String tipoProtocollo,String uriAccordoServizio, String input){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , "input:"+input);
			String methodName = "getSoggettiErogatoreAutoComplete";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class},
						tipoProtocollo, uriAccordoServizio , input);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getSoggettiErogatoreAutoComplete(tipoProtocollo, uriAccordoServizio , input);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Soggetto> getSoggettiFruitoreAutoComplete(String tipoProtocollo,String uriAccordoServizio  , String input){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, uriAccordoServizio , "input:"+input);
			String methodName = "getSoggettiFruitoreAutoComplete";
			try {
				return (List<Soggetto>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class},
						tipoProtocollo, uriAccordoServizio , input);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getSoggettiFruitoreAutoComplete(tipoProtocollo, uriAccordoServizio , input);
		}
	}
	
	
	@Override
	public AccordoServizioParteSpecifica getAspsFromValues(String tipoServizio, String nomeServizio, String tipoErogatore, String nomeErogatore, Integer versioneServizio){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(tipoServizio, nomeServizio , tipoErogatore, nomeErogatore, versioneServizio);
			String methodName = "getAspsFromValues";
			try {
				return (AccordoServizioParteSpecifica) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, Integer.class },
						tipoServizio, nomeServizio , tipoErogatore, nomeErogatore, versioneServizio);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getAspsFromValues(tipoServizio, nomeServizio , tipoErogatore, nomeErogatore, versioneServizio);
		}
	}
	@Override
	public AccordoServizioParteSpecifica getAspsFromId(Long idServizio) {
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione!=null) {
			String key = buildKey(idServizio);
			String methodName = "getAspsFromId";
			try {
				return (AccordoServizioParteSpecifica) AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_datiConfigurazione, key, methodName, 
						new Class<?>[] {Long.class },
						idServizio);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getAspsFromId(idServizio);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<IDServizio> getServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, "VAL:"+val, "searchTipo:"+searchTipo);
			String methodName = "getServiziErogazione";
			try {
				return (List<IDServizio>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, Boolean.class },
						tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo);
		}
	}
	@Override
	public int countServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, "VAL:"+val, "searchTipo:"+searchTipo);
			String methodName = "countServiziErogazione";
			try {
				return (Integer) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, Boolean.class },
						tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<IDServizio> getConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto , String nomeSoggetto, String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, "VAL:"+val, "searchTipo:"+searchTipo, "permessi:"+permessiUtenteOperatore);
			String methodName = "getConfigurazioneServiziErogazione";
			try {
				return (List<IDServizio>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class, Boolean.class, PermessiUtenteOperatore.class },
						tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo, permessiUtenteOperatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getConfigurazioneServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, val, searchTipo, permessiUtenteOperatore);
		}
	}
	@Override
	public int countConfigurazioneServiziErogazione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String tipoServizio ,String nomeServizio, 
			String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio, tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, "VAL: "+val, "searchTipo:"+searchTipo, "permessi:"+permessiUtenteOperatore);
			String methodName = "countConfigurazioneServiziErogazione";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class ,String.class, 
										String.class, String.class, Integer.class, String.class, String.class, Boolean.class, PermessiUtenteOperatore.class},
						tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
						tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val, searchTipo, permessiUtenteOperatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countConfigurazioneServiziErogazione(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
					tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val, searchTipo, permessiUtenteOperatore);
		}
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<IDServizio> getServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore , String nomeSoggettoErogatore, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, "VAL:"+val, "searchTipo:"+searchTipo);
			String methodName = "getServiziFruizione";
			try {
				return (List<IDServizio>) cache.getObjectCache(this.driver, debug, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class ,Boolean.class},
						tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getServiziFruizione(tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, val, searchTipo);
		}
	}
	@Override
	public int countServiziFruizione(String tipoProtocollo, String tipoSoggettoErogatore , String nomeSoggettoErogatore, String val,Boolean searchTipo){
		
		DynamicUtilsServiceCache cache = null;
		boolean debug = false;
		if(val==null || StringUtils.isEmpty(val)) {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_datiConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_datiConfigurazione;
		}
		else {
			cache = AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione;
			debug = AbstractConsoleStartupListener.debugCache_ricercheConfigurazione;
		}
		
		if(cache!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, "VAL:"+val, "searchTipo:"+searchTipo);
			String methodName = "countServiziFruizione";
			try {
				return (Integer) cache.getObjectCache(this.driver, debug, key, methodName,
						new Class<?>[] {String.class, String.class, String.class, String.class ,Boolean.class},
						tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, val, searchTipo);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countServiziFruizione(tipoProtocollo, tipoSoggettoErogatore, nomeSoggettoErogatore, val, searchTipo);
		}
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<IDServizio> getConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String tipoServizio ,String nomeServizio, 
			String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio,
					tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, "VAL:"+val, "searchTipo:"+searchTipo, "permessi:"+permessiUtenteOperatore);
			String methodName = "getConfigurazioneServiziFruizione";
			try {
				return (List<IDServizio>) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName,
						new Class<?>[] {String.class, String.class, String.class, String.class ,String.class, 
							String.class, String.class, Integer.class, String.class, String.class, Boolean.class, PermessiUtenteOperatore.class},
						tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
						tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val,searchTipo, permessiUtenteOperatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return null;
			}
		}
		else {
			return this.driver.getConfigurazioneServiziFruizione(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
					tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val,searchTipo, permessiUtenteOperatore);
		}
	}
	@Override
	public int countConfigurazioneServiziFruizione(String tipoProtocollo, String tipoSoggetto, String nomeSoggetto, String tipoServizio ,String nomeServizio, 
			String tipoErogatore, String nomeErogatore, Integer versioneServizio, String nomeAzione, String val,Boolean searchTipo, PermessiUtenteOperatore permessiUtenteOperatore){
		if(AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione!=null) {
			String key = buildKey(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio, nomeServizio,
					tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, "VAL:"+val, "searchTipo:"+searchTipo, "permessi:"+permessiUtenteOperatore);
			String methodName = "countConfigurazioneServiziFruizione";
			try {
				return (Integer) AbstractConsoleStartupListener.dynamicUtilsServiceCache_ricercheConfigurazione.getObjectCache(this.driver, AbstractConsoleStartupListener.debugCache_ricercheConfigurazione, key, methodName, 
						new Class<?>[] {String.class, String.class, String.class, String.class ,String.class, 
							String.class, String.class, Integer.class, String.class, String.class, Boolean.class, PermessiUtenteOperatore.class},
						tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
						tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val,searchTipo, permessiUtenteOperatore);
			}catch(Throwable e) {
				log.error("Cache Access Error (method:"+methodName+" key:"+key+"): "+e.getMessage(),e);
				return 0;
			}
		}
		else {
			return this.driver.countConfigurazioneServiziFruizione(tipoProtocollo, tipoSoggetto, nomeSoggetto, tipoServizio ,nomeServizio, 
					tipoErogatore, nomeErogatore, versioneServizio, nomeAzione, val,searchTipo, permessiUtenteOperatore);
		}
	}

}
