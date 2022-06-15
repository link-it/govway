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

package org.openspcoop2.core.protocolli.trasparente.testsuite;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.controllo_traffico.driver.PolicyGroupByActiveThreadsType;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
import org.openspcoop2.pdd.core.controllo_traffico.policy.config.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
* DbUtils
*
* @author Francesco Scarlato (scarlato@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    public final JdbcTemplate jdbc;

    public DbUtils(Map<String, String> config) {
        String url = (String) config.get("url");
        String username = (String) config.get("username");
        String password = (String) config.get("password");
        String driver = (String) config.get("driverClassName");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbc = new JdbcTemplate(dataSource);
        logger.info("init jdbc template: {}", url);
    }

    public Object readValue(String query) {
        return this.jdbc.queryForObject(query, Object.class);
    }
    public <T> T readValue(String query, Class<T> classObject) {
        return this.jdbc.queryForObject(query, classObject);
    }
    public Object readValue(String query, Object... args) {
        return this.jdbc.queryForObject(query, Object.class, args);
    }
    public <T> T readValue(String query, Class<T> classObject, Object... args) {
        return this.jdbc.queryForObject(query, classObject, args);
    }
    
    public <T> T readValueArray(String query, Class<T> classObject, Object[] args) {
        return this.jdbc.queryForObject(query, classObject, args);
    }
    
    /*public <T> T readValue(String query, Class<T> classObject, Object[] args) {
        return this.jdbc.queryForObject(query, classObject, args);
    }*/
    
    

    public Map<String, Object> readRow(String query) {
    	Map<String, Object> mapReaded = this.jdbc.queryForMap(query);
    	return this.formatResult(mapReaded);
    }

    public List<Map<String, Object>> readRows(String query, Object... args) {
    	List<Map<String, Object>> listReaded = this.jdbc.queryForList(query, args);
    	if(listReaded!=null && !listReaded.isEmpty()) {
    		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
    		for (Map<String, Object> mapReaded : listReaded) {
				list.add(this.formatResult(mapReaded));
			}
    		return list;
    	}
    	return null;
    }

    private Map<String, Object> formatResult(Map<String, Object> mapReaded){
    	if(mapReaded!=null && !mapReaded.isEmpty()) {
    		Map<String, Object> map = new HashMap<String, Object>();
    		for (String colonna : mapReaded.keySet()) {
				Object value = mapReaded.get(colonna);
				if(value instanceof java.math.BigDecimal) {
					java.math.BigDecimal bd = (java.math.BigDecimal) value;
					map.put(colonna.toLowerCase(), bd.intValue());
				}
				else {
					map.put(colonna.toLowerCase(), value);
				}
			}
    		return map;
    	}
    	return null;
    }
    
    public int update(String query) {
        return this.jdbc.update(query);
    }
    
    public long getIdErogazione(String erogatore, String erogazione) {
    	String query = "select serv.id from "+CostantiDB.SERVIZI+" serv, "+CostantiDB.SOGGETTI+" sog WHERE serv.id_soggetto = sog.id AND sog.tipo_soggetto='gw' AND sog.nome_soggetto='"+erogatore+"' AND serv.tipo_servizio='gw' AND serv.nome_servizio='"+erogazione+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	long idErogazione =  -1;
    	if(oId instanceof Long) {
    		idErogazione = (Long) oId;
    	}
    	else {
    		idErogazione = (Integer) oId;
    	}
    	return idErogazione;
    }
    
    public long getIdFruizione(String fruitore, String erogatore, String erogazione) {
    	String query = "select servfru.id from "+CostantiDB.SERVIZI_FRUITORI+" servfru, "+CostantiDB.SERVIZI+" serv, "+CostantiDB.SOGGETTI+" sogero, "+CostantiDB.SOGGETTI+" sogfru WHERE servfru.id_servizio=serv.id AND servfru.id_soggetto=sogfru.id AND serv.id_soggetto = sogero.id "+
    			" AND sogero.tipo_soggetto='gw' AND sogero.nome_soggetto='"+erogatore+"'"+
    			" AND serv.tipo_servizio='gw' AND serv.nome_servizio='"+erogazione+"'"+
    			" AND sogfru.tipo_soggetto='gw' AND sogfru.nome_soggetto='"+fruitore+"'";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("id");
    	long idFruizione =  -1;
    	if(oId instanceof Long) {
    		idFruizione = (Long) oId;
    	}
    	else {
    		idFruizione = (Integer) oId;
    	}
       	return idFruizione;
    }
    
    private Map<String, String> fillMapPolicyGroupByActiveThreadsType(PolicyGroupByActiveThreadsType type) {
    	Map<String, String> map = new HashMap<String, String>();
    	if(type!=null) {
    		map.put(Constants.GESTORE, type.name());
    		
    		boolean distribuita = false;
    		switch (type){
			case LOCAL:
				map.put(Constants.MODALITA_SINCRONIZZAZIONE, Constants.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE);
				break;
			case LOCAL_DIVIDED_BY_NODES:
				map.put(Constants.MODALITA_SINCRONIZZAZIONE, Constants.VALUE_MODALITA_SINCRONIZZAZIONE_LOCALE_SUDDIVISA_TRA_NODI);
				break;
			default:
				map.put(Constants.MODALITA_SINCRONIZZAZIONE, Constants.VALUE_MODALITA_SINCRONIZZAZIONE_DISTRIBUITA);
				distribuita = true;
				break;
			}
    		
    		if(distribuita) {
    			switch (type){
    			case DATABASE:
    				map.put(Constants.MODALITA_IMPLEMENTAZIONE, Constants.VALUE_MODALITA_IMPLEMENTAZIONE_DATABASE);
    				break;
    			case HAZELCAST:
    			case HAZELCAST_LOCAL_CACHE:
    			case HAZELCAST_NEAR_CACHE:
    			case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
    			case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
    			case HAZELCAST_PNCOUNTER:
    			case HAZELCAST_ATOMIC_LONG:
    			case HAZELCAST_ATOMIC_LONG_ASYNC:
    				map.put(Constants.MODALITA_IMPLEMENTAZIONE, Constants.VALUE_MODALITA_IMPLEMENTAZIONE_HAZELCAST);
    				break;
    			case REDISSON:
    				map.put(Constants.MODALITA_IMPLEMENTAZIONE, Constants.VALUE_MODALITA_IMPLEMENTAZIONE_REDIS);
    				break;
    			default:
    				break;
    			}
    		}
    		
    		if(type.isHazelcast()) {
    			switch (type){
    			case HAZELCAST:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_EXACT);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_FULL_SYNC);
    				break;
    			case HAZELCAST_LOCAL_CACHE:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_EXACT);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_LOCAL_CACHE);
    				break;
    			case HAZELCAST_NEAR_CACHE:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_EXACT);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_NEAR_CACHE);
    				break;
    			case HAZELCAST_NEAR_CACHE_UNSAFE_SYNC_MAP:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_APPROXIMATED);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_SYNC);
    				break;
    			case HAZELCAST_NEAR_CACHE_UNSAFE_ASYNC_MAP:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_APPROXIMATED);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_REMOTE_ASYNC);
    				break;
    			case HAZELCAST_PNCOUNTER:
    				map.put(Constants.MODALITA_CONTATORI, Constants.VALUE_MODALITA_CONTATORI_EXACT);
    				map.put(Constants.MODALITA_TIPOLOGIA, Constants.VALUE_MODALITA_TIPOLOGIA_HAZELCAST_CONTATORI_SINGOLI_PNCOUNTER);
    			default:
    				break;
    			}
    		}
    		
    	}
    	return map;
    }
    
    public void setEngineTypeErogazione(String erogatore, String erogazione, PolicyGroupByActiveThreadsType type) {
    	final String filtroPorta = "%gw_" + erogatore + "/gw_" + erogazione + "/v1%";
    	
    	long idPorta = -1;
    	String select = "select id from "+CostantiDB.PORTE_APPLICATIVE+" pa WHERE pa.nome_porta LIKE '"+filtroPorta+"'";
    	logger.info(select);
    	var result = readRow(select);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		idPorta = (Long) oId;
    	}
    	else {
    		idPorta = (Integer) oId;
    	}
    	
    	String delete = "delete from "+CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP+" rt WHERE rt.id_porta="+idPorta+" AND rt.nome IN ('"+
    			Constants.GESTORE+"','"+Constants.MODALITA_SINCRONIZZAZIONE+"','"+Constants.MODALITA_IMPLEMENTAZIONE+"','"+Constants.MODALITA_CONTATORI+"','"+Constants.MODALITA_TIPOLOGIA+"')";
    	int rows = this.update(delete);
    	logger.info("Delete '"+delete+"': "+rows+" rows");
    	
    	Map<String, String> map = fillMapPolicyGroupByActiveThreadsType(type);
    	if(map!=null && !map.isEmpty()) {
    		for (String key : map.keySet()) {
    			String insert = "insert into "+CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP+" (nome, valore, id_porta) VALUES ('"+key+"','"+map.get(key)+"', "+idPorta+")";
    			rows = this.update(insert);
    	    	logger.info("Insert '"+insert+"': "+rows+" rows");
			}
    	}
    	
//    	String s = "select nome, valore from "+CostantiDB.PORTE_APPLICATIVE_RATE_LIMITING_PROP+" rt WHERE rt.id_porta="+idPorta+" AND rt.nome='"+Constants.GESTORE+"'";
//    	logger.info(select);
//    	result = readRow(s);
//    	for (String key : result.keySet()) {
//    		logger.info("====");
//    		logger.info(key+"="+result.get(key));
//		}
    }
    
    public void setEngineTypeFruizione(String fruitore, String erogatore, String fruizione, PolicyGroupByActiveThreadsType type) {
    	final String filtroPorta = "%gw_" + fruitore + "/gw_" + erogatore + "/gw_" + fruizione + "/v1%";
    	
    	long idPorta = -1;
    	String select = "select id from "+CostantiDB.PORTE_DELEGATE+" pd WHERE pd.nome_porta LIKE '"+filtroPorta+"'";
    	logger.info(select);
    	var result = readRow(select);
    	Object oId = result.get("id");
    	if(oId instanceof Long) {
    		idPorta = (Long) oId;
    	}
    	else {
    		idPorta = (Integer) oId;
    	}
    	
    	String delete = "delete from "+CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP+" rt WHERE rt.id_porta="+idPorta+" AND rt.nome IN ('"+
    			Constants.GESTORE+"','"+Constants.MODALITA_SINCRONIZZAZIONE+"','"+Constants.MODALITA_IMPLEMENTAZIONE+"','"+Constants.MODALITA_CONTATORI+"','"+Constants.MODALITA_TIPOLOGIA+"')";
    	int rows = this.update(delete);
    	logger.info("Delete '"+delete+"': "+rows+" rows");
    	
    	Map<String, String> map = fillMapPolicyGroupByActiveThreadsType(type);
    	if(map!=null && !map.isEmpty()) {
    		for (String key : map.keySet()) {
    			String insert = "insert into "+CostantiDB.PORTE_DELEGATE_RATE_LIMITING_PROP+" (nome, valore, id_porta) VALUES ('"+key+"','"+map.get(key)+"', "+idPorta+")";
    			rows = this.update(insert);
    	    	logger.info("Insert '"+insert+"': "+rows+" rows");
			}
    	}
    }
    
    public String getIdGlobalPolicy(String policyName) {
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from "+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+" WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA is null";
    
    	logger.info(query);
    	var result = readRow(query);
    	    	
    	String active_policy_id = (String) result.get("active_policy_id");
    	Timestamp policy_update_time = (Timestamp) result.get("policy_update_time");   	
    	
       	AttivazionePolicy policy = new AttivazionePolicy();
    	policy.setIdActivePolicy(active_policy_id);
    	policy.setUpdateTime(policy_update_time);
       	
       	return UniqueIdentifierUtilities.getUniqueId(policy);
    }
    
    public String getIdPolicyErogazione(String erogatore, String erogazione, String policyName) {
    	final String filtroPorta = "%gw_" + erogatore + "/gw_" + erogazione + "/v1%";
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from "+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+" WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA LIKE'"+filtroPorta+"' AND FILTRO_RUOLO='applicativa' AND filtro_protocollo='trasparente'";
    	logger.info(query);
    	var result = readRow(query);
    	
    	String active_policy_id = (String) result.get("active_policy_id");
    	Timestamp policy_update_time = (Timestamp) result.get("policy_update_time");   	
    	
       	AttivazionePolicy policy = new AttivazionePolicy();
    	policy.setIdActivePolicy(active_policy_id);
    	policy.setUpdateTime(policy_update_time);
       	
       	return UniqueIdentifierUtilities.getUniqueId(policy);
    }
     

    public String getIdPolicyErogazione(String erogatore, String erogazione, Utils.PolicyAlias tipoPolicy) {
    	return getIdPolicyErogazione(erogatore, erogazione, tipoPolicy.toString());
    }
    
    
    public String getIdPolicyFruizione(String fruitore, String erogatore, String fruizione, String policyName) {
    	final String filtroPorta = "%gw_" + fruitore + "/gw_" + erogatore + "/gw_" + fruizione + "/v1%";
    	
    	
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from "+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+" WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='delegata' AND filtro_protocollo='trasparente'";
    	logger.info(query);
      	var result = readRow(query);
    	
    	String active_policy_id = (String) result.get("active_policy_id");
    	Timestamp policy_update_time = (Timestamp) result.get("policy_update_time");   	
    	
       	AttivazionePolicy policy = new AttivazionePolicy();
    	policy.setIdActivePolicy(active_policy_id);
    	policy.setUpdateTime(policy_update_time);
       	
       	return UniqueIdentifierUtilities.getUniqueId(policy);
    }
    
    public String getIdPolicyFruizione(String fruitore, String erogatore, String fruizione, Utils.PolicyAlias tipoPolicy) {
    	return getIdPolicyFruizione(fruitore, erogatore, fruizione, tipoPolicy.toString());
    }
    
    public List<String> getAllPoliciesIdErogazione(String erogatore, String erogazione, Utils.PolicyAlias tipoPolicy) {
    	
    	final String filtroPorta = "%gw_" + erogatore + "/gw_" + erogazione + "/v1%";
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from "+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+" WHERE POLICY_ALIAS='"+tipoPolicy+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='applicativa' AND filtro_protocollo='trasparente'";
    	List<Map<String, Object>> results = readRows(query);
    	    	
    	return results.stream()
    		.map( r -> {
    			String active_policy_id = (String) r.get("active_policy_id");
    	    	Timestamp policy_update_time = (Timestamp) r.get("policy_update_time");
    	    	AttivazionePolicy policy = new AttivazionePolicy();
    	    	policy.setIdActivePolicy(active_policy_id);
    	    	policy.setUpdateTime(policy_update_time);
    	    	return UniqueIdentifierUtilities.getUniqueId(policy);
    			
    		})
    		.collect(Collectors.toList());    	   
    }
    
    public List<String> getAllPoliciesIdFruizione(String fruitore, String erogatore, String fruizione, Utils.PolicyAlias tipoPolicy) {
    	
    	final String filtroPorta = "%gw_" + fruitore + "/gw_" + erogatore + "/gw_" + fruizione + "/v1%";
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from "+CostantiDB.CONTROLLO_TRAFFICO_ACTIVE_POLICY+" WHERE POLICY_ALIAS='"+tipoPolicy+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='delegata' AND filtro_protocollo='trasparente'";
    	List<Map<String, Object>> results = readRows(query);
    	    	
    	return results.stream()
    		.map( r -> {
    			String active_policy_id = (String) r.get("active_policy_id");
    	    	Timestamp policy_update_time = (Timestamp) r.get("policy_update_time");
    	    	AttivazionePolicy policy = new AttivazionePolicy();
    	    	policy.setIdActivePolicy(active_policy_id);
    	    	policy.setUpdateTime(policy_update_time);
    	    	return UniqueIdentifierUtilities.getUniqueId(policy);
    			
    		})
    		.collect(Collectors.toList());    	   
    }
    


}
