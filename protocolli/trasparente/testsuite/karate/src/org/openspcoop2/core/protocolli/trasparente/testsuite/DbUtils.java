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

package org.openspcoop2.core.protocolli.trasparente.testsuite;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openspcoop2.core.controllo_traffico.AttivazionePolicy;
import org.openspcoop2.core.controllo_traffico.beans.UniqueIdentifierUtilities;
import org.openspcoop2.core.protocolli.trasparente.testsuite.rate_limiting.Utils;
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
    
    public String getIdGlobalPolicy(String policyName) {
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from ct_active_policy WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA is null";
    
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
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from ct_active_policy WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA LIKE'"+filtroPorta+"' AND FILTRO_RUOLO='applicativa' AND filtro_protocollo='trasparente'";
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
    	
    	
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from ct_active_policy WHERE POLICY_ALIAS='"+policyName+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='delegata' AND filtro_protocollo='trasparente'";
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
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from ct_active_policy WHERE POLICY_ALIAS='"+tipoPolicy+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='applicativa' AND filtro_protocollo='trasparente'";
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
    	String query = "select active_policy_id,POLICY_UPDATE_TIME from ct_active_policy WHERE POLICY_ALIAS='"+tipoPolicy+"' AND FILTRO_PORTA LIKE '"+filtroPorta+"' AND FILTRO_RUOLO='delegata' AND filtro_protocollo='trasparente'";
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
