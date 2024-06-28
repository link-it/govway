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

package org.openspcoop2.pdd.config.vault.cli.testsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openspcoop2.core.constants.CostantiDB;
import org.openspcoop2.utils.TipiDatabase;
import org.openspcoop2.utils.Utilities;
import org.openspcoop2.utils.UtilsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
* DbUtils
*
* @author Andrea Poli (andrea.poli@link.it)
* @author $Author$
* @version $Rev$, $Date$
*/
public class DbUtils {

    private static final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private static final String UNCORRECT_TYPE = "Uncorrect return type '";
    private static final String AND_CONDITION=" AND ";
    
    public final JdbcTemplate jdbc;
    
    public final TipiDatabase tipoDatabase;

    public DbUtils(Map<String, String> config) {
        String url = config.get("url");
        String username = config.get("username");
        String password = config.get("password");
        String driver = config.get("driverClassName");
        String type = config.get("dbType");
        if(type!=null) {
        	this.tipoDatabase = TipiDatabase.toEnumConstant(type );
        }
        else {
        	this.tipoDatabase = TipiDatabase.DEFAULT;
        }
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        this.jdbc = new JdbcTemplate(dataSource);
        logger.info("init jdbc template: {}", url);
    }

    public Object readValue(String query) {
        try {
        	return this.jdbc.queryForObject(query, Object.class);
        }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		return this.jdbc.queryForObject(query, Object.class);
    	}
    }
    public <T> T readValue(String query, Class<T> classObject) {
    	 try {
         	return this.jdbc.queryForObject(query, classObject);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject);
     	}
    }
    public Object readValue(String query, Object... args) {
    	 try {
         	 return this.jdbc.queryForObject(query, Object.class, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, Object.class, args);
     	}
    }
    public <T> T readValue(String query, Class<T> classObject, Object... args) {
    	 try {
         	 return this.jdbc.queryForObject(query, classObject, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject, args);
     	}
    }
    
    public <T> T readValueArray(String query, Class<T> classObject, Object[] args) {
    	 try {
         	return this.jdbc.queryForObject(query, classObject, args);
    	 }catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
     		// riprovo dopo 1 secondo
     		Utilities.sleep(1000);
     		return this.jdbc.queryForObject(query, classObject, args);
     	}
    }
    
    

    public Map<String, Object> readRow(String query) {
    	Map<String, Object> mapReaded = null;
    	try {
    		mapReaded = this.jdbc.queryForMap(query);
    	}catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		mapReaded = this.jdbc.queryForMap(query);
    	}
    	return this.formatResult(mapReaded);
    }

    public List<Map<String, Object>> readRows(String query, Object... args) {
    	List<Map<String, Object>> listReaded = null;
    	try {
    		listReaded = this.jdbc.queryForList(query, args);
    	}catch(org.springframework.jdbc.CannotGetJdbcConnectionException getExc) {
    		// riprovo dopo 1 secondo
    		Utilities.sleep(1000);
    		listReaded = this.jdbc.queryForList(query, args);
    	}
    	if(!listReaded.isEmpty()) {
    		List<Map<String, Object>> list = new ArrayList<>();
    		for (Map<String, Object> mapReaded : listReaded) {
				list.add(this.formatResult(mapReaded));
			}
    		return list;
    	}
    	else {
    		listReaded = null;
    	}
    	return listReaded;
    }

    private Map<String, Object> formatResult(Map<String, Object> mapReaded){
    	Map<String, Object> map = null;
    	if(mapReaded!=null && !mapReaded.isEmpty()) {
    		map = new HashMap<>();
    		for (Map.Entry<String,Object> entry : mapReaded.entrySet()) {
    			String colonna= entry.getKey();
				Object value = mapReaded.get(colonna);
				if(value instanceof java.math.BigDecimal) {
					java.math.BigDecimal bd = (java.math.BigDecimal) value;
					map.put(colonna.toLowerCase(), bd.intValue());
				}
				else {
					map.put(colonna.toLowerCase(), value);
				}
			}
    	}
		return map;
    }
    
    public int update(String query) {
        return this.jdbc.update(query);
    }
    
    
    public String getServiziApplicativiNome(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("nome", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getServiziApplicativiPasswordInv(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    public String getServiziApplicativiEncPasswordInv(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getServiziApplicativiNome(String nomePortaDefault, String azione) throws UtilsException {
    	return getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    }
    public String getServiziApplicativiPasswordInv(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	return getColumnValue("passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    }
    public String getServiziApplicativiEncPasswordInv(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	return getColumnValue("enc_passwordinv", CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    }
    
    public String getServiziApplicativiPasswordRisp(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("passwordrisp", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    public String getServiziApplicativiEncPasswordRisp(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_passwordrisp", CostantiDB.SERVIZI_APPLICATIVI, "nome", colonnaIdValue);
    }
    
    public String getConnettorePassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    public String getConnettoreEncPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreProxyPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("proxy_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    public String getConnettoreEncProxyPassword(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("enc_proxy_password", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreApiKey(String colonnaIdValue) throws UtilsException {
    	return getColumnValue("api_key", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, colonnaIdValue);
    }
    
    public String getConnettoreCustomValue(String nomeConnettore, String nomeProprieta) throws UtilsException {
    	long idConnettore = getColumnLongValue("id", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, nomeConnettore);
    	return getColumnValueById("value", CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, idConnettore,
    			"name", nomeProprieta);
    }
    public String getConnettoreCustomEncValue(String nomeConnettore, String nomeProprieta) throws UtilsException {
    	long idConnettore = getColumnLongValue("id", CostantiDB.CONNETTORI, CostantiDB.CONNETTORI_COLUMN_NOME, nomeConnettore);
    	return getColumnValueById("enc_value", CostantiDB.CONNETTORI_CUSTOM, CostantiDB.CONNETTORI_CUSTOM_COLUMN_ID_CONNETTORE, idConnettore,
    			"name", nomeProprieta);
    }
    
    public String getNomeConnettoreByPortaApplicativa(String nomePortaDefault, String azione) throws UtilsException {
    	String nomeSA = getServizioApplicativoAssociatoPorta(nomePortaDefault, azione);
    	long idConnettore = getColumnLongValue(CostantiDB.CONNETTORI_COLUMN_ID_CONNETTORE_INV, CostantiDB.SERVIZI_APPLICATIVI, "nome", nomeSA);
    	return getColumnValueById(CostantiDB.CONNETTORI_COLUMN_NOME, CostantiDB.CONNETTORI, "id", idConnettore);
    }
    
    private String getColumnValue(String colonna, String tabella, String colonnaId, String colonnaIdValue) throws UtilsException {
    	return getColumnValue(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private String getColumnValue(String colonna, String tabella, String colonnaId, String colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    protected String getColumnValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue) throws UtilsException {
    	return getColumnValueById(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private String getColumnValueById(String colonna, String tabella, String colonnaId, long colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private Long getColumnLongValue(String colonna, String tabella, String colonnaId, String colonnaIdValue) throws UtilsException {
    	return getColumnLongValue(colonna, tabella, colonnaId, colonnaIdValue, 
        		null, null);
    }
    private Long getColumnLongValue(String colonna, String tabella, String colonnaId, String colonnaIdValue, 
    		String colonnaId2, String colonnaIdValue2) throws UtilsException {
    	String query = getQueryBase(colonna, tabella, colonnaId, colonnaIdValue);
    	if(colonnaId2!=null && colonnaIdValue2!=null) {
    		query+=AND_CONDITION+colonnaId2+"='"+colonnaIdValue2+"'";
    	}
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get(colonna);
    	if(oId instanceof Long) {
    		return (Long) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private String getServizioApplicativoAssociatoPorta(String nomePortaDefault, String nomeAzione) throws UtilsException {
    	String query = "select nome from servizi_applicativi where id=(select id_servizio_applicativo from porte_applicative_sa where id_porta=(select pa.id from porte_applicative pa,pa_azioni az where az.id_porta=pa.id AND "+
    			"az.azione='"+nomeAzione+"' AND pa.nome_porta LIKE '__"+nomePortaDefault+"__Specific%'));";
    	logger.info(query);
    	var result = readRow(query);
    	Object oId = result.get("nome");
    	if(oId instanceof String) {
    		return (String) oId;
    	}
    	if(oId==null) {
    		return null;
    	}
    	throw new UtilsException(UNCORRECT_TYPE+oId.getClass().getName()+"' ("+oId+")");
    }
    
    private String getQueryBase(String colonna, String tabella, String colonnaId, Object colonnaIdValue) {
    	String q = "select "+colonna+" from "+tabella+" WHERE "+colonnaId;
    	if(colonnaIdValue instanceof String) {
    		q+="='"+colonnaIdValue+"'";
    	}
    	else {
    		q+="="+colonnaIdValue;
    	}
    	return q;
    }
     
}
